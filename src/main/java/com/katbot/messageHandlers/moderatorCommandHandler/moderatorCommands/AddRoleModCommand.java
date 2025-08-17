package com.katbot.messageHandlers.moderatorCommandHandler.moderatorCommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddRoleModCommand implements ModCommand {
    private static final Logger log = LoggerFactory.getLogger(AddRoleModCommand.class);

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        if (!arePermissionsValid(event)) {
            log.warn("User {} (id:{}) has tried to access an unauthorised command.", event.getAuthor().getName(), event.getAuthor().getId());
            event.getMessage().reply("Sorry, the bot or you don't have necessary permissions to use this command.").queue();
            return;
        }

        processRoleCommand(event, args);
    }

    private void processRoleCommand(MessageReceivedEvent event, String[] args) {
        log.info("Processing arguments {}", String.join(",", args));
        switch (args[0]) {
            case "add":
            case "+":
                addRole(event, args);
                break;
            case "remove":
            case "rm":
            case "delete":
            case "-":
                removeRole(event, args);
                break;
            case "wipe":
                log.info("Wiping role...");
                // TODO: Implement an option when all users has been removed from having that role.
                break;
            default:
                log.error("No option '{}' for role command.", args[0]);
                event.getMessage().reply("No option " + args[0] + " for the role command, check docs!").queue();

        }
    }



    /**
     * Adds a role with given name to the server if it doesn't already exist.
     * @param event event containing server info
     * @param args argument list
     */
    private void addRole(MessageReceivedEvent event, String[] args) {
        List<Role> existingRoles = event.getGuild().getRolesByName(args[1], true);
        if (!existingRoles.isEmpty()) {
            event.getMessage().reply("Role with that name: <@&" + existingRoles.get(0).getId() + "> already exists.").queue();
            return;
        }

        log.info("Creating a guild role with a name of {}...", args[1]);
        event.getGuild().createRole().setName(args[1]).queue(
                role -> {
                    event.getMessage().reply("Role: <@&" + role.getId() + "> has been created.").queue();
                }
        );

    }

    /**
     * Removes a role with the given name
     * @param event event containing server info
     * @param args argument list
     */
    private void removeRole(MessageReceivedEvent event, String[] args) {
        List<Role> existingRoles = event.getGuild().getRolesByName(args[1], true);
        if (existingRoles.isEmpty()) {
            event.getMessage().reply("Role with a name '" + args[1] + "' does not exist.").queue();
            return;
        }

        log.info("Deleting a guild role with the name of {}...", args[1]);
        existingRoles.get(0).delete().queue(
                role -> {
                    event.getMessage().reply("Role with the name '" + args[1] + "' has been removed.").queue();
                }
        );
    }

    @Override
    public List<String> getAliases() {
        return List.of("role");
    }

    private boolean arePermissionsValid(MessageReceivedEvent event) {
        try {
            boolean canManageRoles;
            canManageRoles = event.getGuild().getMemberById(event.getAuthor().getId()).getPermissions().contains(Permission.MANAGE_ROLES);
            return canManageRoles;
        } catch (NullPointerException e) {
            log.error("Can't find message author with id: {} on server {}", event.getAuthor().getId(), event.getGuild().getName());
            event.getMessage().reply("Sorry, something went wrong, check logs!").queue();
        }
        return false;
    }
}


