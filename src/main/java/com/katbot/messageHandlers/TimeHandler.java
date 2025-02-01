package com.katbot.messageHandlers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class TimeHandler implements Handler {

    private static final Pattern TIME_PATTERN = Pattern.compile(
            "\\b(this\\s+evening|tonight|in\\s+\\d+\\s+(minute|minutes|hour|hours)|tomorrow|later)\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public void handle(MessageReceivedEvent event) {
        // Reply to the user with an ephemeral message only he can see with value "test".
        event.getMessage().reply("test").queue(response -> response.suppressEmbeds(true).queue());
    }

    @Override
    public boolean isViable(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();
        return TIME_PATTERN.matcher(messageContent).find();
    }
}
