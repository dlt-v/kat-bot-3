package com.katbot.commands.Help;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        JDA jda = event.getJDA();
        String argument = args.length > 0 ? args[0] : "";

        jda.retrieveUserById(System.getenv("testing-user-id")).queue(user -> {

            EmbedBuilder embedBuilder;
            if (args.length == 0) {
                embedBuilder = buildDefaultCommand(user);
            } else {
                embedBuilder = buildSpecificCommand(argument);
            }
//
            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        });
    }

    private EmbedBuilder buildSpecificCommand(String argument) {
        EmbedBuilder resultEmbed = new EmbedBuilder();

        switch(argument) {
            case "roll":
                resultEmbed.setTitle("Roll Command");
                resultEmbed.setDescription("The `kat roll` command allows you to roll a dice. " +
                        "\nThe command will generate a random number between 1 and 6.");
                resultEmbed.setColor(0x00FF00);
                return resultEmbed;
            case "zabawa":
                resultEmbed.setTitle("Zabawa Command");
                resultEmbed.setDescription("The `kat zabawa` command gives zabawa. ");
                resultEmbed.setColor(0x00FF00);
                return resultEmbed;
            case "poll":
                resultEmbed.setTitle("Poll Command");
                StringBuilder pollDescription = new StringBuilder("The `kat poll` command allows you to create a poll.");
                pollDescription.append("\nUsage: `kat poll <question> <answer1> <answer2> ... <answer5>`");
                pollDescription.append("\nExample: `kat poll \"What day?\" Monday Tuesday Wednesday \"I don't know\"`");
                pollDescription.append("\n(I guess kind of useless know since Discord added their own polls but oh well!");
                resultEmbed.setDescription(pollDescription);
                resultEmbed.setColor(0x00FF00);
                return resultEmbed;
            case "mc-status":
                resultEmbed.setTitle("Minecraft Status Command");
                resultEmbed.setDescription("The `kat mc-status` or `kat minecraft` command allows you to check the status of a Minecraft server.");
                resultEmbed.setColor(0x00FF00);
                return resultEmbed;
            case "about":
                resultEmbed.setTitle("About Command");
                resultEmbed.setDescription("The `kat about` command gives information about KatBot.");
                resultEmbed.setColor(0x00FF00);
                return resultEmbed;
            case "help":
                resultEmbed.setTitle("Help Command");
                resultEmbed.setDescription("The `kat help` command shows the available commands for KatBot.");
                resultEmbed.setColor(0x00FF00);
                return resultEmbed;
            case "8ball":
                resultEmbed.setTitle("8ball Command");
                resultEmbed.setDescription("The `kat 8ball` command allows you to ask the magic 8-ball a question. Answers yes or no questions.");
                resultEmbed.setColor(0x00FF00);
                return resultEmbed;
            default:
                resultEmbed.setTitle("Unknown command");
                resultEmbed.setDescription("The command you are looking for does not exist. " +
                        "\nPlease use `kat help` to see the available commands.");
                resultEmbed.setColor(0xFF0000);
                return resultEmbed;
        }
    }

    private static @NotNull EmbedBuilder buildDefaultCommand(User user) {
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
                        `kat <question>` - Ask the magic 8-ball a yes or no question (`kat 8ball`)
                        
                        If you'd like to know more about a specific command, use `kat help <command>`.

                        Version: `1.0.0`""");
        embedBuilder.setColor(0xFFFF00);
        embedBuilder.setFooter("Created by delta.v", user.getAvatarUrl());
        return embedBuilder;
    }
}
