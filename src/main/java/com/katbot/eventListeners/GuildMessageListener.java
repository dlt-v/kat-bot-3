package com.katbot.eventListeners;

import com.katbot.messageHandlers.CommandHandler;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GuildMessageListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildMessageListener.class);
    private static final String testingChannelID = System.getenv("testing-channel-id");
    private static final String testingUserID = System.getenv("testing-user-id");
    private static final String environmentType = System.getenv("environment");

    private final CommandHandler commandHandler;

    public GuildMessageListener(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if (!isMessageValid(event)) return;
        event.getMessage().reply("I'm here!").queue();
        String message = event.getMessage().getContentDisplay().toLowerCase();


        logEvent(event);

        commandHandler.handle(event);
    }


    private boolean isMessageValid(MessageReceivedEvent event) {
        if (event == null) return false;
        if (event.getChannel().getType() != ChannelType.TEXT) return false;
        if (event.getAuthor().isBot()) return false;

        if ("testing".equals(environmentType) &&
            !(event.getChannel().getId().equals(testingChannelID) &&
            event.getAuthor().getId().equals(testingUserID))) {
            return false;
        }
        return true;
//
//        String message = event.getMessage().getContentDisplay().toLowerCase();
//        return message.startsWith("kat ");
    }

    private void logEvent(MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            // Server channel
            LOGGER.info("[{}] [#{}] {}: {}",
                    event.getGuild().getName(),
                    event.getChannel().getName(),
                    event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        } else {
            // Private channel
            LOGGER.debug("[direct] {}: {}",
                    event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        }
    }
}
