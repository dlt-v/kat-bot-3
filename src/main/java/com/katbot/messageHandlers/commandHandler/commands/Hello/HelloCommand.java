package com.katbot.messageHandlers.commandHandler.commands.Hello;

import com.katbot.messageHandlers.commandHandler.commands.Command;
import com.katbot.messageHandlers.commandHandler.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelloCommand implements Command, SlashCommand {

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getMessage().reply("Hello! :)").queue();
    }

    @Override
    public List<String> getAliases() {
        return List.of("hello", "hi", "hey", "yo", "sup", "greetings");
    }


    @Override
    public CommandData getCommandData() {
        return Commands.slash("hello", "Says hello to the user. Basic healthcheck.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Hello! :)").queue();
    }
}
