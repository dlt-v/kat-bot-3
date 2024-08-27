package com.katbot.commands.TestDatabase;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestDatabaseCommand implements Command {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage("TestDatabaseCommand executed!").queue();
    }

    @Override
    public List<String> getAliases() {
        return List.of("testdb", "testdatabase");
    }
}
