package com.katbot.reactionHandlers;

import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;

public interface ReactionHandler {

    boolean isValid(GenericMessageReactionEvent event);

    void process(GenericMessageReactionEvent event, boolean reactionAdded);
}
