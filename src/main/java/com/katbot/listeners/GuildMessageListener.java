package com.katbot.listeners;

import com.katbot.handlers.CommandHandler;
import com.katbot.handlers.TwitterLinkHandler;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuildMessageListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GuildMessageListener.class);
    private static final String testingChannelID = System.getenv("testing-channel-id");
    private static final String testingUserID = System.getenv("testing-user-id");
    private static final String environmentType = System.getenv("environment");

    private final CommandHandler commandHandler = new CommandHandler();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if (isMessageTwitterLink(event)) {
            String twitterLink = event.getMessage().getContentDisplay().toLowerCase();
            StringBuilder sb = new StringBuilder();
            sb.append("Raw x.com links don't embed properly! Use vxtwitter.com instead!");
            sb.append("\n\n");
            sb.append(TwitterLinkHandler.convertTwitterLink(twitterLink));
            try {
                event.getMessage().suppressEmbeds(true).queue();

            } catch (InsufficientPermissionException e) {
                sb.append("\n");
                sb.append("I don't have permission to suppress embeds of the original message. **Please contact the server owner.**");
                logger.error("Error suppressing embeds", e);
            }
            event.getMessage().reply(sb.toString()).queue();
            return;
        }
        if (!isMessageValid(event)) return;
        logEvent(event);

        commandHandler.handle(event);
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
        if ("testing".equals(environmentType) &&
            !(event.getChannel().getId().equals(testingChannelID) &&
            event.getAuthor().getId().equals(testingUserID))) {
            return false;
        }

        String message = event.getMessage().getContentDisplay().toLowerCase();
        return message.startsWith("kat ");
    }

    private boolean isMessageTwitterLink(MessageReceivedEvent event) {
        return event.getMessage().getContentDisplay().toLowerCase().startsWith("https://x.com/");
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
