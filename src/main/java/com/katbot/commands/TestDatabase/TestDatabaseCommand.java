package com.katbot.commands.TestDatabase;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestDatabaseCommand implements Command {

    private final JdbcTemplate jdbcTemplate;

    public TestDatabaseCommand(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String username = jdbcTemplate.queryForObject(
                "SELECT username FROM users WHERE username = 'alice'",
                String.class
        );
        // Send the retrieved username as a message in the Discord channel
        if (username != null) {
            event.getChannel().sendMessage("Username found: " + username).queue();
        } else {
            event.getChannel().sendMessage("Username not found.").queue();
        }
    }

    @Override
    public List<String> getAliases() {
        return List.of("testdb", "testdatabase");
    }
}
