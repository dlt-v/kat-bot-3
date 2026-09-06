package com.katbot.messageHandlers.commandHandler.commands.About;

import com.katbot.messageHandlers.commandHandler.commands.Command;
import com.katbot.messageHandlers.commandHandler.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Component
public class AboutCommand implements Command, SlashCommand {

    @Value("${project.version}")
    private String version;

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        buildAboutEmbed(event.getJDA(), embed ->
                event.getChannel().sendMessageEmbeds(embed).queue()
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        buildAboutEmbed(event.getJDA(), embed ->
                event.replyEmbeds(embed).queue()
        );
    }

    private void buildAboutEmbed(JDA jda, Consumer<MessageEmbed> callback) {
        jda.retrieveUserById(System.getenv("testing-user-id")).queue(user -> {
            String description = """
                KatBot is a personalised Discord bot created for shits and giggles *but* also has a few useful things!

                I'm currently in development, so expect some bugs and missing features.

                If you have any suggestions or feedback, feel free to contact the author.

                Use `kat help` to see available commands!

                Version: `%s`

                [GitHub Repository Link](https://github.com/dlt-v/kat-bot-3)
                """.formatted(version);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("About KatBot")
                    .setDescription(description)
                    .setColor(0xFFFF00)
                    .setImage("https://repository-images.githubusercontent.com/710022281/b1e83128-385e-4fe0-8286-e47d6bb2174c")
                    .setFooter("Created by delta.v", user.getAvatarUrl())
                    .build();

            callback.accept(embed);
        });
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("about", "info");
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("about", "Provides information about KatBot, its features, and the author.");
    }
}