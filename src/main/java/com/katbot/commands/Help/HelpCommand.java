package com.katbot.commands.Help;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        });
    }

    @Override
    public List<String> getAliases() {
        return List.of("help", "commands");
    }

    private EmbedBuilder buildSpecificCommand(String argument) {
        EmbedBuilder resultEmbed = new EmbedBuilder();
        String title;
        StringBuilder description = new StringBuilder();
        int color = 0x00FF00;

        switch (argument) {
            case "roll":
                title = "Roll Command";
                description.append("The `kat roll` command allows you to simulate rolling dice. ")
                        .append("You can roll a single die or multiple dice with customizable sides and optional modifiers.\n\n")
                        .append("**Usage Examples:**\n")
                        .append("• `kat roll` - Rolls a single 6-sided die (default behavior, equivalent to `kat roll 1d6`).\n")
                        .append("• `kat roll 2d6` - Rolls two 6-sided dice and sums the result.\n")
                        .append("• `kat roll 3d8 +2` - Rolls three 8-sided dice, sums the result, and adds a modifier of +2.\n\n")
                        .append("**Syntax:**\n")
                        .append("• `kat roll xdy +z`\n")
                        .append("Where:\n")
                        .append("  - `x` is the number of dice to roll (optional, defaults to 1).\n")
                        .append("  - `y` is the number of sides per die.\n")
                        .append("  - `+z` is an optional modifier added to the final result.");
                break;
            case "zabawa":
                title = "Zabawa Command";
                description.append("The `kat zabawa` command gives zabawa. ");
                break;
            case "poll":
                title = "Poll Command";
                description.append("The `kat poll` command allows you to create a custom poll with up to 5 possible answers. ")
                        .append("Users can vote on the options via buttons.\n\n")
                        .append("**Usage:**\n")
                        .append("`kat poll <question> <answer1> <answer2> ... <answer5>`\n\n")
                        .append("**Examples:**\n")
                        .append("• `kat poll \"What's your favorite color?\" Red Blue Green`\n")
                        .append("• `kat poll \"Which day works best?\" Monday Tuesday Wednesday \"I don't know\"`\n\n")
                        .append("You can provide between 2 and 5 answers. The question and answers can be in quotes if they contain spaces.\n\n")
                        .append("Once the poll is created, users can vote by clicking on the buttons provided with the answers.\n\n")
                        .append("**Note**: Poll command is no longer maintained since Discord's own poll feature is available.");
                break;
            case "mc-status":
            case "minecraft":
                title = "Minecraft Status Command";
                description.append("The `kat minecraft` or `kat mc-status` command allows you to check the status of a Minecraft server. ")
                        .append("It connects to specific server and retrieves information such as the version, current player count, and the names of online players.\n\n")
                        .append("**Usage:**\n")
                        .append("• `kat mc-status` or `kat minecraft` - Fetches and displays the current status of the configured Minecraft server.\n\n")
                        .append("**Examples:**\n")
                        .append("• `kat mc-status` - Shows the Minecraft server status, including online players and version info.\n")
                        .append("• `kat minecraft` - Same as above; an alias for the `mc-status` command.\n\n")
                        .append("The bot will attempt to connect to the server and provide you with details about its current state.\n")
                        .append("If the server is unreachable or there is a connection issue, the command will time-out.");
                break;
            case "about":
                title = "About Command";
                description.append("The `kat about` command gives information about KatBot.");
                break;
            case "help":
                title = "Help Command";
                description.append("The `kat help` command provides information about the available commands for KatBot. ")
                        .append("You can use this command to get an overview of all commands or to learn more about a specific command.\n\n")
                        .append("**Usage:**\n")
                        .append("• `kat help` - Displays a list of all available commands.\n")
                        .append("• `kat help <command>` - Displays detailed information about a specific command.\n\n")
                        .append("**Examples:**\n")
                        .append("• `kat help` - Shows all commands.\n")
                        .append("• `kat help roll` - Shows details and usage examples for the `kat roll` command.");
                break;
            case "8ball":
                title = "8ball Command";
                description.append("The `kat 8ball` command allows you to ask a yes/no question to the magic 8-ball. ")
                        .append("The bot will provide a random answer to your question, similar to the classic magic 8-ball toy.\n\n")
                        .append("**Usage:**\n")
                        .append("• `kat <question>` - Ask the magic 8-ball a yes/no question. The question should typically start with a word like 'will', 'is', 'does', etc.\n\n")
                        .append("**Examples:**\n")
                        .append("• `kat will I pass my exam?`\n")
                        .append("• `kat is it going to rain tomorrow?`\n\n")
                        .append("The 8-ball will provide one of many possible responses, ranging from certain to doubtful.");
                break;
            default:
                title = "Unknown command";
                description.append("The command you are looking for does not exist.\n\n")
                        .append("Please use `kat help` to see the available commands.");
                color = 0xFF0000;
        }
        resultEmbed.setTitle(title);
        resultEmbed.setDescription(description);
        resultEmbed.setColor(color);
        return resultEmbed;
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
