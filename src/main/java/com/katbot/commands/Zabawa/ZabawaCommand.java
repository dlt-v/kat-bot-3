package com.katbot.commands.Zabawa;

import com.katbot.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZabawaCommand implements Command {
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
}
