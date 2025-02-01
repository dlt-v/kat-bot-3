package com.katbot.messageHandlers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class TwitterLinkHandler implements Handler {

    private static final Pattern TWITTER_LINK_PATTERN = Pattern.compile("^https://x\\.com/[A-Za-z0-9_]+/status/\\d+$");
    private static final Logger logger = LoggerFactory.getLogger(TwitterLinkHandler.class.getName());

    public boolean isViable(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay().toLowerCase();
        return TWITTER_LINK_PATTERN.matcher(message).matches();
    }

    public void handle(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay().toLowerCase();
        String newMessage = message.replace("x.com", "vxtwitter.com");
        try {
            event.getMessage().suppressEmbeds(true).queue();
        } catch (InsufficientPermissionException e) {
            String guildName = event.getGuild().getName().substring(0, Math.min(35, event.getGuild().getName().length()));
            logger.error("Lacking permission to suppress embeds in server: {}", guildName);
        }
        event.getMessage().reply(newMessage).queue();
    }
}
