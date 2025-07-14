package com.katbot.eventListeners;

import com.katbot.messageHandlers.*;
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

    private static final Logger logger = LoggerFactory.getLogger(GuildMessageListener.class);
    private final String testingChannelID;
    private final String testingUserID;

    private final CommandHandler commandHandler;
    private final TwitterLinkHandler twitterLinkHandler;
    private final ParameterService parameterService;
    private final BroadcastChannelHandler broadcastChannelHandler;
    private final TimeHandler timeHandler;
    private final ChatGptHandler chatGptHandler;

    public GuildMessageListener(
            CommandHandler commandHandler,
            ParameterService parameterService,
            TwitterLinkHandler twitterLinkHandler,
            BroadcastChannelHandler broadcastChannelHandler,
            TimeHandler timeHandler,
            ChatGptHandler chatGptHandler) {
        this.commandHandler = commandHandler;
        this.parameterService = parameterService;
        this.twitterLinkHandler = twitterLinkHandler;
        this.broadcastChannelHandler = broadcastChannelHandler;
        this.timeHandler = timeHandler;
        this.testingChannelID = parameterService.getTestingChannelID();
        this.testingUserID = parameterService.getTestingUserID();
        this.chatGptHandler = chatGptHandler;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!isMessageValid(event)) return;

        if (twitterLinkHandler.isViable(event)) {
            logEvent(event);
            twitterLinkHandler.handle(event);
            return;
        }

        if (broadcastChannelHandler.isViable(event)) {
            logEvent(event);
            broadcastChannelHandler.handle(event);
            return;
        }

        if (chatGptHandler.isViable(event)) {
            logEvent(event);
            chatGptHandler.handle(event);
            return;
        }

        if (commandHandler.isViable(event)) {
            logEvent(event);
            commandHandler.handle(event);
            return;
        }

        if (timeHandler.isViable(event)) {
            logEvent(event);
            timeHandler.handle(event);
            return;
        }
    }


    private boolean isMessageValid(MessageReceivedEvent event) {
        if (event == null) return false;
        if (event.getChannel().getType() != ChannelType.TEXT) return false;
        if (event.getAuthor().isBot()) return false;

        // If in test mode, only allow messages from the testing channel and a testing user.
        if (parameterService.isInTest() && !(event.getChannel().getId().equals(testingChannelID) && event.getAuthor().getId().equals(testingUserID))) {
            String serverName = event.getGuild().getName().substring(0, Math.min(20, event.getGuild().getName().length()));
            logger.warn("Received message from unauthorized user ({}) or channel ({}.{}) during testing.", event.getAuthor().getName(), serverName, event.getChannel().getName());
            return false;
        }
        // If not in test mode, allow messages from any channel BUT the testing channel.
        if (!parameterService.isInTest() && event.getChannel().getId().equals(testingChannelID)) {
            logger.warn("Received message from channel ({}) designated for testing.", event.getChannel().getName());
            return false;
        }

        return true;
    }

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
