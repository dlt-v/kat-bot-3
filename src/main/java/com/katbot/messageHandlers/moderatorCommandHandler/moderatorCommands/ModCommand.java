package com.katbot.messageHandlers.moderatorCommandHandler.moderatorCommands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface ModCommand {

    void execute(MessageReceivedEvent event, String[] args);

    List<String> getAliases();
}
