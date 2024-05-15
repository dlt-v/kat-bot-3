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
            embedBuilder.setDescription(
                    "KatBot is a personalised Discord bot created for shits and giggles *but* also has a few useful things!" +
                    "\n\nI'm currently in development, so expect some bugs and missing features." +
                    "If you have any suggestions or feedback, feel free to contact the author.\n\n" +
                    "Use `kat help` to see available commands!\n\n" +
                    "Version: `"+ System.getenv("version") + "`");
            embedBuilder.setColor(0xFFFF00);
            embedBuilder.setFooter("Created by delta.v", user.getAvatarUrl());
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        });
    }
}
