package com.katbot.commands.Help;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        JDA jda = event.getJDA();

        jda.retrieveUserById(System.getenv("testing-user-id")).queue(user -> {

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("KatBot Commands");
            embedBuilder.setDescription(
                    """
                            Here are the available commands for KatBot:
    
                            `kat roll` - Roll a dice
                            `kat zabawa` - Fun commands
                            `kat poll` - Create a poll
                            `kat mc-status` - Check Minecraft server status
                            `kat about` - About KatBot
                            `kat help` - Show available commands
                            `kat <question>` - Ask the magic 8-ball a question
                            
                            If you'd like to know more about a specific command, use `kat help <command>`.
    
                            Version: `1.0.0`""");
            embedBuilder.setColor(0xFFFF00);
            embedBuilder.setFooter("Created by delta.v", user.getAvatarUrl());
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        });
    }
}
