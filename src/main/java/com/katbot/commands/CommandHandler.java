package com.katbot.commands;

import com.katbot.commands.About.AboutCommand;
import com.katbot.commands.EightBall.EightBallCommand;
import com.katbot.commands.MinecraftStatus.MinecraftStatusCommand;
import com.katbot.commands.Poll.PollCommand;
import com.katbot.commands.Roll.RollCommand;
import com.katbot.commands.Zabawa.ZabawaCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commandMap;

    public CommandHandler() {
        this.commandMap = new HashMap<>();

        registerCommand(new RollCommand(), Arrays.asList("roll", "dice"));
        registerCommand(new ZabawaCommand(), Arrays.asList("zabawa", "fun"));
        registerCommand(new PollCommand(), Arrays.asList("poll", "vote"));
        registerCommand(new MinecraftStatusCommand(), Arrays.asList("mc-status", "minecraft"));
        registerCommand(new AboutCommand(), Arrays.asList("about", "info"));
        registerCommand(new EightBallCommand(), Arrays.asList(
                "will", "is", "does", "can", "should", "has", "was", "might", "would", "could", "are",
                "do", "did", "have", "hasn't", "aren't", "wasn't", "wouldn't", "couldn't", "won't",
                "isn't", "doesn't", "hasn't", "haven't", "hadn't"));
    }

    private void registerCommand(Command command, List<String> aliases) {
        for (String alias : aliases) {
            commandMap.put(alias, command);
        }
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
