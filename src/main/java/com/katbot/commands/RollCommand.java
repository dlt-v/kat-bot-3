package com.katbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RollCommand implements Command {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        // Default roll is 1d6
        String input = args.length > 0 ? String.join(" ", args).toLowerCase() : "1d6";

        // Regex to match dice roll commands
        Pattern pattern = Pattern.compile("(\\d*)d(\\d+)(?:\\s*\\+\\s*(\\d+))?");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            int numberOfDice = matcher.group(1) != null && !matcher.group(1).isEmpty() ? Integer.parseInt(matcher.group(1)) : 1;
            int sides = Integer.parseInt(matcher.group(2));
            int modifier = 0;

            if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                modifier = Integer.parseInt(matcher.group(3));
            }

            List<Integer> rolls = rollDice(numberOfDice, sides);
            int rollSum = rolls.stream().mapToInt(Integer::intValue).sum();
            int totalSum = rollSum + modifier;

            String rollResults;
            if (numberOfDice > 1 || modifier > 0) {
                rollResults = rolls.stream().map(String::valueOf).collect(Collectors.joining(" + "));
                if (modifier > 0) {
                    rollResults += " + " + modifier;
                }
                rollResults += " = " + totalSum;
            } else {
                rollResults = String.valueOf(totalSum);
            }

            String response = String.format("<@%s> rolled %s.", event.getAuthor().getId(), rollResults);
            event.getChannel().sendMessage(response).queue();
        } else {
            event.getChannel().sendMessage("""
                    **Invalid roll command.**
                    Use the format 'kat roll xdy +z'
                    where x is the number of dice,
                    y is the number of sides per die,
                    and z is an optional modifier.
                    Example: `kat roll 2d6 +4`
                    """).queue();
        }
    }

    private List<Integer> rollDice(int numberOfDice, int sides) {
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < numberOfDice; i++) {
            rolls.add(ThreadLocalRandom.current().nextInt(1, sides + 1));
        }
        return rolls;
    }
}
