package com.katbot.eventListeners;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.katbot.util.TestModeChannelValidator.isInValidChannel;

@Component
public class ReactionInteractionListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ReactionInteractionListener.class);

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        logger.info("event reaction by {}", event.getGuild());
        if (!isValid(event)) {
            logger.debug("Message reaction detected in invalid channel.");
            return;
        };
        logger.info("Message reaction detected in valid format.");
    }

    private boolean isValid(MessageReactionAddEvent event) {
        if (!isInValidChannel(event.getChannel().getId())) return false;
        // check if message belongs to kat-bot
        if (event.getMessageAuthorIdLong() != event.getJDA().getSelfUser().getIdLong()) return false;
        // check if message is in proper channel
        if (!event.getChannel().getId().equals("1097579511475875931")) return false;

        return true;
    }


}

