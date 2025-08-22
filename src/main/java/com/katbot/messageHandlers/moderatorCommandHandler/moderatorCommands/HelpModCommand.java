package com.katbot.messageHandlers.moderatorCommandHandler.moderatorCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;

import static com.katbot.util.StringUtils.capitalizeFirstLetter;

@Component
public class HelpModCommand implements ModCommand {

    private static final Logger logger = LoggerFactory.getLogger(HelpModCommand.class);
    private final List<ModCommand> modCommandsList;

    HelpModCommand (List<ModCommand> modCommandsList) {
        this.modCommandsList = modCommandsList;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.YELLOW);

        if (args.length == 0) {
            // if argument list is zero -> "kat help", build the default command
            generateDefaultManual(eb);
            User user = event.getJDA().getUserById(System.getenv("testing-user-id"));
            if (user != null) eb.setFooter("Created by delta.v", user.getAvatarUrl());
        } else {
            try {
                generateManualForSpecificCommand(eb, args[0]);
            } catch (Exception e) {
                event.getMessage().reply("Something went wrong during processing your command:\n> " + e.getMessage()).queue();
                return;
            }
        }

        event.getMessage().replyEmbeds(eb.build()).queue();
    }

    private void generateDefaultManual(EmbedBuilder eb) {
        eb.setTitle("KatBot Moderator Commands");

        StringBuilder description = new StringBuilder("Here are the available moderator commands for KatBot:\n\n");

        for (ModCommand modCommand : this.modCommandsList) {
            description.append("`katmod ").append(modCommand.getAliases().get(0)).append("` - ").append(modCommand.getShortDocs());
        }

        description.append("\n\nIf you'd like to know more about a command, use `katmod help <command>`.");
        description.append("\nTo check user commands, use `kat help`.");

        description.append("\n\nVersion: `").append(System.getenv("version")).append("`");

        eb.setDescription(description);
    }

    private void generateManualForSpecificCommand(EmbedBuilder eb, String command) {

        for (ModCommand modCommand : this.modCommandsList) {
            for (String alias : modCommand.getAliases()) {
                if (command.equals(alias)) {
                    eb.setTitle(capitalizeFirstLetter(modCommand.getAliases().get(0)) + " Command");
                    eb.setDescription(modCommand.getDocs());
                    return;
                }
            }
        }

        logger.error("Command 'katmod {}' does not exist.", command);
        throw new IllegalArgumentException("Command `katmod " + command + "` does not exist.");

    }

    @Override
    public List<String> getAliases() {
        return List.of("help");
    }

}
