package com.katbot.messageHandlers.moderatorCommandHandler;

import com.katbot.messageHandlers.Handler;
import com.katbot.messageHandlers.moderatorCommandHandler.moderatorCommands.ModCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModeratorCommandHandler implements Handler {

    private final Map<String, ModCommand> commandMap = new HashMap<>();

    public ModeratorCommandHandler(List<ModCommand> commands) {
        registerCommands(commands);
    }

    private void registerCommands(List<ModCommand> commands) {
        commands.forEach(command -> {
            for (String alias : command.getAliases()) {
                commandMap.put(alias, command);
            }
        });
    }

    public void handle(MessageReceivedEvent event) {
        String[] splitMessage = event.getMessage()
                .getContentDisplay()
                .split("\\s+");

        if (splitMessage.length > 1 && splitMessage[0].equalsIgnoreCase("katmod")) {

            ModCommand command = commandMap.get(splitMessage[1].toLowerCase());

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
                .split("\\s+");

        return splitMessage.length > 0 && splitMessage[0].equalsIgnoreCase("katmod");
    }
}
