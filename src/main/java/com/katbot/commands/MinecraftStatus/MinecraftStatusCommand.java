package com.katbot.commands.MinecraftStatus;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.katbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftStatusCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(MinecraftStatusCommand.class);
    private static final String host = System.getenv("mc-server-address");

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        MinecraftProtocol protocol = new MinecraftProtocol();
        TcpClientSession session = new TcpClientSession(host, 25565, protocol);

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

                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

                session1.disconnect("Finished");

            });
            session.connect(true);
        } catch (Exception e) {
            event.getChannel().sendMessage("An error occurred while trying to connect to the server!").queue();
            LOGGER.error("An error occurred while trying to connect to the server!", e);
        }
    }

}
