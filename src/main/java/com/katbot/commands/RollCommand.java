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
        int numberOfDice, sides, modifier = 0;

        // Regex to match various dice roll commands
        Pattern pattern = Pattern.compile("(?:(\\d*)d(\\d+))(?:\\s*\\+\\s*(\\d+))?");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            // Default to 1 die if no number is specified before 'd'
            numberOfDice = matcher.group(1) != null && !matcher.group(1).isEmpty() ? Integer.parseInt(matcher.group(1)) : 1;
            sides = Integer.parseInt(matcher.group(2));
            // Check for modifier, if present
            if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                modifier = Integer.parseInt(matcher.group(3));
            }

            // Roll the dice
            List<Integer> rolls = rollDice(numberOfDice, sides);
            int rollSum = rolls.stream().mapToInt(Integer::intValue).sum();
            int totalSum = rollSum + modifier;

            // Build the roll result string
            String rollResults = rolls.stream().map(String::valueOf).collect(Collectors.joining(" + "));

            // Format the response string based on whether we rolled more than one die
            String response = numberOfDice > 1 ?
                    String.format("<@%s> rolled %s = %d", event.getAuthor().getId(), rollResults, totalSum) :
                    modifier > 0 ?
                            String.format("<@%s> rolled %s + %d = %d", event.getAuthor().getId(), rollResults, modifier, totalSum) :
                            String.format("<@%s> rolled %d", event.getAuthor().getId(), totalSum);

            event.getChannel().sendMessage(response).queue();
        } else {
            event.getChannel().sendMessage("Invalid roll command. Use the format '/kat roll [NdM] [+X]' where N is the number of dice, M is the number of sides per die, and X is an optional modifier.").queue();
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
