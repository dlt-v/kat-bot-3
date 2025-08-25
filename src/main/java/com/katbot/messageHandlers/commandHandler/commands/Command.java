package com.katbot.messageHandlers.commandHandler.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface Command {

    void execute(MessageReceivedEvent event, String[] args);

    List<String> getAliases();

}
