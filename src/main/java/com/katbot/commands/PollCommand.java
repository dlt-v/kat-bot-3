package com.katbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;

public class PollCommand implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getChannel().sendMessage("Usage: kat poll <question> <answer1> <answer2>").queue();
            return;
        }

        ArrayList<Button> buttons = new ArrayList<>();


        for (int i = 1; i < args.length; i++) {
            buttons.add(Button.primary(i + "", args[i]));
            // TODO: There can be max 5 buttons on one embed/message.
        }


        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Test Embed");
        embedBuilder.setDescription("Description");
        embedBuilder.setColor(0x00FF00);

        embedBuilder.addField("Field 1", "Value 1", false);
        embedBuilder.addField("Field 2", "Value 2", true);

        MessageEmbed embed = embedBuilder.build();

        event.getChannel().sendMessageEmbeds(embed).setActionRow(buttons).queue();
    }
}
