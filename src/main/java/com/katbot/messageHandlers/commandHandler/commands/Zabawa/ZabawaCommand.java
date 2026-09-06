package com.katbot.messageHandlers.commandHandler.commands.Zabawa;

import com.katbot.messageHandlers.commandHandler.commands.Command;
import com.katbot.messageHandlers.commandHandler.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZabawaCommand implements Command, SlashCommand {

    private final ZabawaUrlLoader zabawaUrlLoader;

    public ZabawaCommand() {
        zabawaUrlLoader = new ZabawaUrlLoader();
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage(zabawaUrlLoader.getRandomMemeUrl()).queue();
    }

    @Override
    public List<String> getAliases() {
        return List.of("zabawa", "fun");
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("zabawa", "Get a random zabawa video");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(zabawaUrlLoader.getRandomMemeUrl()).queue();
    }
}
