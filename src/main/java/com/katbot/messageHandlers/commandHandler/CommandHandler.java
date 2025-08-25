package com.katbot.messageHandlers.commandHandler;

import com.katbot.messageHandlers.commandHandler.commands.Command;
import com.katbot.messageHandlers.Handler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandHandler implements Handler {

    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandHandler(List<Command> commands) {
        registerCommands(commands);
    }

    private void registerCommands(List<Command> commands) {
        commands.forEach(command -> {
            for (String alias : command.getAliases()) {
                commandMap.put(alias, command);
            }
        });
    }

    public void handle(MessageReceivedEvent event) {
        String[] splitMessage = event.getMessage()
                .getContentDisplay()
                .toLowerCase()
                .split("\\s+");

        if (splitMessage.length > 0 && splitMessage[0].equals("kat")) {
            Command command = commandMap.get(splitMessage[1]);
            if (command != null) {
                String[] args = Arrays.copyOfRange(splitMessage, 2, splitMessage.length);
                command.execute(event, args);
            } else {
                event.getChannel().sendMessage("Unknown command!").queue();
            }
        }
    }

    public boolean isViable(MessageReceivedEvent event) {
        String[] splitMessage = event.getMessage()
                .getContentDisplay()
                .toLowerCase()
                .split("\\s+");

        return splitMessage.length > 0 && splitMessage[0].equals("kat");
    }
}
