package com.katbot.commands.Zabawa;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ZabawaCommand implements Command {
    private final ZabawaUrlLoader zabawaUrlLoader;
    public ZabawaCommand() {
        zabawaUrlLoader = new ZabawaUrlLoader();
    }
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage(zabawaUrlLoader.getRandomMemeUrl()).queue();
    }
}
