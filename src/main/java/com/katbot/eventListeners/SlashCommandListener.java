package com.katbot.eventListeners;

import com.katbot.messageHandlers.commandHandler.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SlashCommandListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SlashCommandListener.class);
    private final Map<String, SlashCommand> commandMap = new HashMap<>();

    public SlashCommandListener(List<SlashCommand> commands) {
        for (SlashCommand command : commands) {
            commandMap.put(command.getCommandData().getName().toLowerCase(), command);
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        List<CommandData> commandDataList = commandMap.values().stream()
                .map(SlashCommand::getCommandData)
                .toList();

        event.getJDA().updateCommands().addCommands(commandDataList).queue(
                v -> logger.info("Successfully registered {} slash commands.", commandDataList.size()),
                error -> logger.error("Failed to register slash commands", error)
        );
    }

    // Routes incoming slash commands to the right class
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand command = commandMap.get(event.getName().toLowerCase());
        if (command != null) {
            command.execute(event);
        } else {
            event.reply("Command not recognized.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        SlashCommand command = commandMap.get(event.getName().toLowerCase());
        if (command != null) {
            command.onAutoComplete(event);
        }
    }
}
