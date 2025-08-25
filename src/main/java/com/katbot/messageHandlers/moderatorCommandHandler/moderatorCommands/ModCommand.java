package com.katbot.messageHandlers.moderatorCommandHandler.moderatorCommands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface ModCommand {

    void execute(MessageReceivedEvent event, String[] args);

    List<String> getAliases();

    default String getShortDocs() {
        return "Short docs about this command do not exist";
    }

    default String getDocs() {
        return "Documentation about this command has not been implemented yet.";
    }
}
