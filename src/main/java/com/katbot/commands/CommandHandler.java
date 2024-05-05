package com.katbot.commands;

import com.katbot.commands.MinecraftStatus.MinecraftStatusCommand;
import com.katbot.commands.Poll.PollCommand;
import com.katbot.commands.Roll.RollCommand;
import com.katbot.commands.Zabawa.ZabawaCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commandMap;

    public CommandHandler() {
        this.commandMap = new HashMap<>();
        commandMap.put("roll", new RollCommand());
        commandMap.put("zabawa", new ZabawaCommand());
        commandMap.put("poll", new PollCommand());
        commandMap.put("mc-status", new MinecraftStatusCommand());
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
}
