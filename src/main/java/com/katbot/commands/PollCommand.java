package com.katbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PollCommand implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        List<String> arguments = parseArguments(String.join(" ", args));

        if (arguments.size() < 2) {
            event.getChannel().sendMessage("Usage: kat poll <question> <answer1> <answer2> ... <answer 5>").queue();
            return;
        }

        if (arguments.size() > 5) {
            event.getChannel().sendMessage("Too many possible answers. Maximum is 5. You provided: " + (arguments.size()- 1)).queue();
            return;
        }

        ArrayList<Button> buttons = new ArrayList<>();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Poll: " + capitalizeString(arguments.get(0)));
        embedBuilder.setDescription("Created by <@%s>".formatted(event.getAuthor().getId()));
        embedBuilder.setColor(0x00FF00);

        for (int i = 1; i < arguments.size(); i++) {
            buttons.add(Button.primary(capitalizeString(arguments.get(i)), capitalizeString(arguments.get(i))));
            embedBuilder.addField(capitalizeString(arguments.get(i)), String.valueOf(0), false);
        }

        MessageEmbed embed = embedBuilder.build();

        event.getChannel().sendMessageEmbeds(embed).setActionRow(buttons).queue();
    }

    private List<String> parseArguments(String input) {
        ArrayList<String> arguments = new ArrayList<>();
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(input);
        while (matcher.find()) {
            arguments.add(matcher.group(1).replace("\"", ""));
        }
        return arguments;
    }

    private String capitalizeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char firstChar = input.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            input = Character.toUpperCase(firstChar) + input.substring(1);
        }

        char lastChar = input.charAt(input.length() - 1);
        if (lastChar != '.' && lastChar != '?' && lastChar != '!') {
            input += '.';
        }

        return input;
    }
}
