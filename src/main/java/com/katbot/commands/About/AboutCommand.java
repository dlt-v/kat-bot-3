package com.katbot.commands.About;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AboutCommand implements com.katbot.commands.Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        JDA jda = event.getJDA(); // TODO: Integrate Spring

        jda.retrieveUserById(System.getenv("testing-user-id")).queue(user -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("About KatBot");
            embedBuilder.setDescription("I'm KatBot, a personalised Discord bot for shits and giggles and a few useful things. :)");
            embedBuilder.setColor(0xFFFF00);
            embedBuilder.setFooter("Created by delta.v", user.getAvatarUrl());
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        });
    }
}
