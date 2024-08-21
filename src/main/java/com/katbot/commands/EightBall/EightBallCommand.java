package com.katbot.commands.EightBall;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
}
