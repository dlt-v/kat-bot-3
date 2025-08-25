package com.katbot.eventListeners;

import com.katbot.reactionHandlers.RoleAssignmentHandler;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.katbot.util.TestModeChannelValidator.isInValidChannel;

@Component
public class ReactionInteractionListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ReactionInteractionListener.class);
    private final RoleAssignmentHandler roleAssignmentHandler;

    ReactionInteractionListener(RoleAssignmentHandler roleAssignmentHandler) {
        this.roleAssignmentHandler = roleAssignmentHandler;
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        onMessageReactionInternal(event, true);
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        onMessageReactionInternal(event, false);
    }

    private void onMessageReactionInternal(GenericMessageReactionEvent event, boolean reactionAdded) {
        if (!isValid(event)) return;

        if (this.roleAssignmentHandler.isValid(event)) {
            this.roleAssignmentHandler.process(event, reactionAdded);
            return;
        }

        logger.warn("Message reaction detected in valid format but not handled.");
    }

    private boolean isValid(GenericMessageReactionEvent event) {
        if (!isInValidChannel(event.getChannel().getId())) return false;
        // check if message belongs to kat-bot
        if (event.retrieveMessage().complete().getAuthor().getIdLong() != event.getJDA().getSelfUser().getIdLong()) return false;
        // check if reaction is from kat-bot
        if (event.getMember().getIdLong() == event.getJDA().getSelfUser().getIdLong()) return false;
        return true;
    }


}

