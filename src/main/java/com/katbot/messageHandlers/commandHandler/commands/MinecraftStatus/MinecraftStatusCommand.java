package com.katbot.messageHandlers.commandHandler.commands.MinecraftStatus;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.katbot.messageHandlers.commandHandler.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MinecraftStatusCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(MinecraftStatusCommand.class);
    private static final String host = System.getenv("mc-server-address");
    public static final int PORT = 25565;

    public void execute(ButtonInteractionEvent event) {
        execute(event.getChannel());
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        execute(event.getChannel());
    }

    public void execute(MessageChannelUnion channel) {
        MinecraftProtocol protocol = new MinecraftProtocol();
        TcpClientSession session = new TcpClientSession(host, PORT, protocol);

        try {
            session.setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, (ServerInfoHandler) (session1, serverStatusInfo) -> {

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setThumbnail("https://static.wikia.nocookie.net/minecraft_gamepedia/images/9/99/Chicken_%28inventory%29_MCE.png/revision/latest?cb=20210918065652");
                embedBuilder.setTitle("Minecraft Server Status");
                StringBuilder description = new StringBuilder("Version: **%s**\n\nPlayer count: **%d**/**%d**".formatted(
                        serverStatusInfo.getVersionInfo().getVersionName(),
                        serverStatusInfo.getPlayerInfo().getOnlinePlayers(),
                        serverStatusInfo.getPlayerInfo().getMaxPlayers()
                ));

                if (serverStatusInfo.getPlayerInfo().getOnlinePlayers() > 0) {
                    description.append("\n\nPlayers online:\n");
                    for (GameProfile player : serverStatusInfo.getPlayerInfo().getPlayers()) {
                        description.append("- ").append(player.getName()).append("\n");
                    }
                }
                embedBuilder.setDescription(description.toString());
                embedBuilder.setColor(0x00FF00);

                Button button = Button.primary("mc-status:check_again", "Check again");

                channel.sendMessageEmbeds(embedBuilder.build()).setActionRow(button).queue();

                session1.disconnect("Finished");

            });
            session.setConnectTimeout(5000);
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

            Future<?> future = executor.submit(() -> {
                try {
                    session.connect(false);
                } catch (Exception e) {
                    channel.sendMessage("An error occurred while trying to connect to the server!").queue();
                    logger.error("An error occurred while trying to connect to the server!", e);
                }
            });
            executor.schedule(() -> {
                future.cancel(true);
            }, 5, TimeUnit.SECONDS); // 5 seconds timeout for the connection attempt

            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            channel.sendMessage("An error occurred while trying to connect to the server!").queue();
            logger.error("An error occurred while trying to connect to the server!", e);
        }
    }

    @Override
    public List<String> getAliases() {
        return List.of("mc-status", "minecraft");
    }

}
