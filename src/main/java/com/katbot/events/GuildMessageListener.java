package com.katbot.events;

import com.katbot.commands.CommandHandler;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuildMessageListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GuildMessageListener.class);
    private static final String testingChannelID = System.getenv("testing-channel-id");
    private static final String testingUserID = System.getenv("testing-user-id");

    private final CommandHandler commandHandler = new CommandHandler();
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if (!isMessageValid(event)) return;
        logEvent(event);

        commandHandler.handle(event);

//        event.getChannel().sendMessage(
//                String.format("Hello! You wrote : \"%s\"", event.getMessage().getContentDisplay())
//        ).queue();

    }


    /**
     * Checks if the received message is valid based on specific criteria.
     * A valid message is one that comes from a text channel (not a DM or other type),
     * is not sent by a bot, is in the specified testing channel and user,
     * and starts with "Kat " (case-insensitive).
     *
     * @param event The MessageReceivedEvent to evaluate.
     * @return true if the message is valid, false otherwise.
     */
    private boolean isMessageValid(MessageReceivedEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT) {
            return false;
        }
        if (event.getAuthor().isBot()) {
            return false;
        }
        if (!event.getChannel().getId().equals(testingChannelID)) {
            return false;
        }
        if (!event.getAuthor().getId().equals(testingUserID)) {
            return false;
        }
        String message = event.getMessage().getContentDisplay().toLowerCase();
        return message.startsWith("kat ");
    }

    /**
     * Logs a message received event. Differentiates between messages from a guild
     * and direct messages.
     *
     * @param event The MessageReceivedEvent to log.
     */
    private void logEvent(MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            // Server channel
            logger.info("[{}] [#{}] {}: {}",
                    event.getGuild().getName(),
                    event.getChannel().getName(),
                    event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        } else {
            // Private channel
            logger.debug("[direct] {}: {}",
                    event.getAuthor().getName(),
                    event.getMessage().getContentDisplay());
        }
    }
}
