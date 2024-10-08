package com.katbot.commands.Hello;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelloCommand implements Command {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getMessage().reply("Hello! :)").queue();
    }

    @Override
    public List<String> getAliases() {
        return List.of("hello", "hi", "hey", "yo", "sup", "greetings");
    }
}
