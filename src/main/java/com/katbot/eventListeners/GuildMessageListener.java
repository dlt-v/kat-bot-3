package com.katbot.eventListeners;

import com.katbot.messageHandlers.BroadcastChannelHandler;
import com.katbot.messageHandlers.CommandHandler;
import com.katbot.messageHandlers.TwitterLinkHandler;
import com.katbot.parameters.ParameterService;
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
    private final String testingChannelID;
    private final String testingUserID;

    private final CommandHandler commandHandler;
    private final TwitterLinkHandler twitterLinkHandler;
    private final ParameterService parameterService;
    private final BroadcastChannelHandler broadcastChannelHandler;

    public GuildMessageListener(
            CommandHandler commandHandler,
            ParameterService parameterService,
            TwitterLinkHandler twitterLinkHandler,
            BroadcastChannelHandler broadcastChannelHandler
    ) {
        this.commandHandler = commandHandler;
        this.parameterService = parameterService;
        this.twitterLinkHandler = twitterLinkHandler;
        this.broadcastChannelHandler = broadcastChannelHandler;
        this.testingChannelID = parameterService.getTestingChannelID();
        this.testingUserID = parameterService.getTestingUserID();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!isMessageValid(event)) return;

        if (twitterLinkHandler.isTwitterLink(event.getMessage().getContentDisplay())) {
            logEvent(event);
            twitterLinkHandler.handle(event);
            return;
        }

        if (broadcastChannelHandler.isBroadcastChannel(event)) {
            logEvent(event);
            broadcastChannelHandler.handle(event);
            return;
        }

        if (commandHandler.isKatCommand(event)) {
            logEvent(event);
            commandHandler.handle(event);
            return;
        }
    }


    private boolean isMessageValid(MessageReceivedEvent event) {
        if (event == null) return false;
        if (event.getChannel().getType() != ChannelType.TEXT) return false;
        if (event.getAuthor().isBot()) return false;

        // If in test mode, only allow messages from the testing channel and a testing user.
        if (parameterService.isInTest() && !(event.getChannel().getId().equals(testingChannelID) &&
                event.getAuthor().getId().equals(testingUserID))) {
            String serverName = event.getGuild().getName().substring(0, Math.min(20, event.getGuild().getName().length()));
            LOGGER.warn("Received message from unauthorized user ({}) or channel ({}.{}) during testing.", event.getAuthor().getName(), serverName, event.getChannel().getName());
            return false;
        }
        // If not in test mode, allow messages from any channel BUT the testing channel.
        if (!parameterService.isInTest() && event.getChannel().getId().equals(testingChannelID)) {
            LOGGER.warn("Received message from channel ({}) designated for testing.", event.getChannel().getName());
            return false;
        }

        return true;
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
