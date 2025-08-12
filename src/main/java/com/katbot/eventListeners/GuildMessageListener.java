package com.katbot.eventListeners;

import com.katbot.messageHandlers.*;
import com.katbot.messageHandlers.commandHandler.CommandHandler;
import com.katbot.messageHandlers.moderatorCommandHandler.ModeratorCommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.katbot.util.TestModeChannelValidator.isInValidChannel;

@Service
public class GuildMessageListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GuildMessageListener.class);

    private final CommandHandler commandHandler;
    private final TwitterLinkHandler twitterLinkHandler;
    private final ChatGptHandler chatGptHandler;
    private final ModeratorCommandHandler moderatorCommandHandler;

    public GuildMessageListener (
            CommandHandler commandHandler,
            TwitterLinkHandler twitterLinkHandler,
            ChatGptHandler chatGptHandler,
            ModeratorCommandHandler moderatorCommandHandler
    ) {
        this.commandHandler = commandHandler;
        this.twitterLinkHandler = twitterLinkHandler;
        this.chatGptHandler = chatGptHandler;
        this.moderatorCommandHandler = moderatorCommandHandler;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!isInValidChannel(event.getChannel().getId())) return;

        if (twitterLinkHandler.isViable(event)) {
            logEvent(event);
            twitterLinkHandler.handle(event);
            return;
        }

        if (chatGptHandler.isViable(event)) {
            logEvent(event);
            chatGptHandler.handle(event);
            return;
        }

        if (moderatorCommandHandler.isViable(event)) {
            logEvent(event);
            moderatorCommandHandler.handle(event);
            return;
        }

        if (commandHandler.isViable(event)) {
            logEvent(event);
            commandHandler.handle(event);
            return;
        }
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
