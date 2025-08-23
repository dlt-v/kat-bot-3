package com.katbot.messageHandlers.moderatorCommandHandler.moderatorCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Random;

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
            case "clean":
                cleanRole(event, args);
                break;
            case "banner":
                createRoleReactionBanner(event, args);
                break;
            default:
                log.error("No option '{}' for role command.", args[0]);
                event.getMessage().reply("No option " + args[0] + " for the role command, check docs!").queue();

        }
    }

    private void createRoleReactionBanner(MessageReceivedEvent event, String[] args) {
        String roleName = args[1];
        String optionalEmoji = args.length > 2 ? args[2] : null;

        if (!doesRoleExist(event, roleName)) {
            event.getMessage().reply("Role with a name '" + args[1] + "' does not exist.").queue();
            return;
        }

        String title = roleName.substring(0, 1).toUpperCase() + roleName.substring(1).toLowerCase() + " Role";
        String description = "If you'd want to have **" + roleName + "** role assigned to you, click on the reaction below this message.";
        Random random = new Random();
        int color = random.nextInt(0xFFFFFF + 1);
        Emoji emoji = optionalEmoji != null ? Emoji.fromUnicode(optionalEmoji) : Emoji.fromUnicode("U+2705");

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(title);
        eb.setDescription(description);
        eb.setColor(color);

        event.getChannel().sendMessageEmbeds(eb.build()).queue(
            something -> {
                log.info("Role assign banner has been created for role {}", roleName);
                something.addReaction(emoji).queue();
            }
        );
    }

    private void cleanRole(MessageReceivedEvent event, String[] args) {
        if (!doesRoleExist(event, args[1])) {
            event.getMessage().reply("Role with a name '" + args[1] + "' does not exist.").queue();
            return;
        }

        // TODO: Implement an option when all users has been removed from having that role.
        log.warn("User attempted 'katmod role wipe' command which is not implemented yet...");
        event.getMessage().reply("Sorry, `role clean` functionality is not implemented yet!").queue();
    }

    private boolean doesRoleExist(MessageReceivedEvent event, String roleName) {
        List<Role> existingRoles = event.getGuild().getRolesByName(roleName, true);
        return !existingRoles.isEmpty();
    }


    /**
     * Adds a role with given name to the server if it doesn't already exist.
     * @param event event containing server info
     * @param args argument list
     */
    private void addRole(MessageReceivedEvent event, String[] args) {
        String roleName = args[1];
        String colorCode = args.length >= 3 ? args[2] : generateRandomColorHexCode();
        Color color;
        try {
            color = parseColorFromHexCode(colorCode);
        } catch (NumberFormatException e) {
            log.error("Value '{}' cannot be converted into a Color object.", colorCode);
            event.getMessage().reply("Value `" + colorCode + "` cannot be converted into color. Please provide a valid HEX format.").queue();
            return;
        }

        List<Role> existingRoles = event.getGuild().getRolesByName(roleName, true);
        if (!existingRoles.isEmpty()) {
            event.getMessage().reply("Role with that name: <@&" + existingRoles.get(0).getId() + "> already exists.").queue();
            return;
        }

        log.info("Creating a guild role with a name of {}...", roleName);
        event.getGuild().createRole().setName(args[1]).setColor(color).queue(
                role -> {
                    role.getManager().setMentionable(true).queue();
                    event.getMessage().reply("Role <@&" + role.getId() + "> has been created.").queue();
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
            event.getMessage().reply("Role `@" + args[1] + "` does not exist on this server.").queue();
            return;
        }

        String foundRoleName = existingRoles.get(0).getName();
        log.info("Deleting role '@{}' from server {}.", foundRoleName, event.getGuild().getName());
        existingRoles.get(0).delete().queue(
                role -> {
                    event.getMessage().reply("Role `@" + foundRoleName + "` has been removed from this server.").queue();
                }
        );
    }

    @Override
    public List<String> getAliases() {
        return List.of("role", "-r");
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

    @Override
    public String getShortDocs() {
        return "server role related commands";
    }

    private String generateRandomColorHexCode() {
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }

    private Color parseColorFromHexCode(String hexCode) {
        return Color.decode(hexCode);
    }
}


