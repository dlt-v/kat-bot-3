package com.katbot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonInteractionListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GuildMessageListener.class);
    private static final String testingChannelID = System.getenv("testing-channel-id");
    private static final String testingUserID = System.getenv("testing-user-id");

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {
        logger.info("Button was reacted with.");
        event.getChannel().sendMessage("Button was interacted with.").queue();
    }
}

