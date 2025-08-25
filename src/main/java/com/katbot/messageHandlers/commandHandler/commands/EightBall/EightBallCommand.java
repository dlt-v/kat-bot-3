package com.katbot.messageHandlers.commandHandler.commands.EightBall;

import com.katbot.messageHandlers.commandHandler.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EightBallCommand implements Command {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length == 0) {
            event.getChannel().sendMessage("Please ask a question!").queue();
            return;
        }

        String[] responses = {
                "It is certain.",
                "It is decidedly so.",
                "Without a doubt!",
                "Yes - definitely.",
                "As I see it, yes.",
                "Most likely.",
                "Yes.",
                "Signs point to yes.",
                "Ask again later.",
                "Not sure really.",
                "Not really!",
                "My reply is no.",
                "My sources say no.",
                "Very doubtful."
        };

        int randomIndex = (int) (Math.random() * responses.length);
        event.getMessage().reply(responses[randomIndex]).queue();

    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(
                "will", "is", "does", "can", "should", "has", "was", "might", "would", "could", "are",
                "do", "did", "have", "hasn't", "aren't", "wasn't", "wouldn't", "couldn't", "won't",
                "isn't", "doesn't", "hasn't", "haven't", "hadn't");
    }
}
