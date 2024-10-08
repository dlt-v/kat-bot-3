package com.katbot.commands.About;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AboutCommand implements com.katbot.commands.Command {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        JDA jda = event.getJDA();

        jda.retrieveUserById(System.getenv("testing-user-id")).queue(user -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("About KatBot");
            StringBuilder description = new StringBuilder();
            description.append("KatBot is a personalised Discord bot created for shits and giggles *but* also has a few useful things!\n\n")
                    .append("I'm currently in development, so expect some bugs and missing features.\n\n")
                    .append("If you have any suggestions or feedback, feel free to contact the author.\n\n")
                    .append("Use `kat help` to see available commands!\n\n")
                    .append("Version: `").append(System.getenv("version")).append("`\n\n")
                    .append("[GitHub Repository Link](https://github.com/dlt-v/kat-bot-3)");
            embedBuilder.setDescription(description);
            embedBuilder.setColor(0xFFFF00);
            embedBuilder.setImage("https://repository-images.githubusercontent.com/710022281/b1e83128-385e-4fe0-8286-e47d6bb2174c");
            embedBuilder.setFooter("Created by delta.v", user.getAvatarUrl());
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        });
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("about", "info");
    }
}
