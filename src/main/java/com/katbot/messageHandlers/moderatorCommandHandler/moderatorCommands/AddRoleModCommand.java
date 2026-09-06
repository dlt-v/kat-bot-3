package com.katbot.messageHandlers.moderatorCommandHandler.moderatorCommands;

import com.katbot.messageHandlers.commandHandler.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Random;

import static com.katbot.util.ColorUtils.parseColorFromHexCode;

@Component
public class AddRoleModCommand implements ModCommand, SlashCommand {

    private static final Logger log = LoggerFactory.getLogger(AddRoleModCommand.class);

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("role", "Manage server roles")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES))
                .setGuildOnly(true)
                .addSubcommands(
                        new SubcommandData("add", "Create a new server role")
                                .addOption(OptionType.STRING, "name", "Role name", true)
                                .addOption(OptionType.STRING, "color", "HEX color code, for example \"#FF0000\" for color red", false),
                        new SubcommandData("remove", "Delete a server role")
                                .addOption(OptionType.ROLE, "target-role", "Target role to delete", true),
                        new SubcommandData("clean", "Remove all members from a role")
                                .addOption(OptionType.ROLE, "target-role", "Target role to clean", true),
                        new SubcommandData("banner", "Generate a banner so users can sign up")
                                .addOption(OptionType.ROLE, "target-role", "Target role for the banner", true)
                                .addOption(OptionType.STRING, "emoji", "Reaction emoji", false)
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) {
            event.reply("Unknown subcommand.").setEphemeral(true).queue();
            return;
        }

        switch (subcommand) {
            case "add" -> handleSlashAdd(event);
            case "remove" -> handleSlashRemove(event);
            case "clean" -> handleSlashClean(event);
            case "banner" -> handleSlashBanner(event);
            default -> event.reply("Unknown action specified.").setEphemeral(true).queue();
        }
    }

    private void handleSlashAdd(SlashCommandInteractionEvent event) {
        String roleName = event.getOption("name").getAsString();
        OptionMapping colorOption = event.getOption("color");
        String colorCode = colorOption != null ? colorOption.getAsString() : generateRandomColorHexCode();

        if(roleName.isBlank()) {
            event.reply("Role name cannot be empty.").setEphemeral(true).queue();
            return;
        }

        Color color;
        try {
            color = parseColorFromHexCode(colorCode);
        } catch (IllegalArgumentException e) {
            event.reply("Value `" + colorCode + "` is not a valid HEX color format.").setEphemeral(true).queue();
            return;
        }

        List<Role> existingRoles = event.getGuild().getRolesByName(roleName, true);
        if (!existingRoles.isEmpty()) {
            event.reply("Role <@&" + existingRoles.get(0).getId() + "> already exists.").setEphemeral(true).queue();
            return;
        }

        try {
            event.getGuild().createRole().setName(roleName).setColor(color).queue(
                    role -> {
                        role.getManager().setMentionable(true).queue();
                        event.reply("Role <@&" + role.getId() + "> has been created.").queue();
                    },
                    error -> event.reply("Failed to create role: " + error.getMessage()).setEphemeral(true).queue()
            );
        } catch (Exception e) {
            log.error("Error while creating role: {}", e.getMessage(), e);
            event.reply("Error: `" + e.getMessage() + "`").setEphemeral(true).queue();
        }

    }

    private void handleSlashRemove(SlashCommandInteractionEvent event) {
        Role targetRole = event.getOption("target-role").getAsRole();

        // Role cannot be removed if there are still members assigned to it
        int memberCount = event.getGuild().getMembersWithRoles(targetRole).size();
        if (memberCount > 0) {
            log.error("Attempted to delete role {} which still has {} members assigned.", targetRole.getName(), memberCount);
            event.reply("Cannot delete role `" + targetRole.getName() + "` because it still has " + memberCount + " members assigned. Please remove all members from the role before deleting it.").setEphemeral(true).queue();
            return;
        }

        targetRole.delete().queue(
                success -> event.reply("Role @`" + targetRole.getName() + "` has been removed.").queue(),
                error -> event.reply("Failed to delete role: " + error.getMessage()).setEphemeral(true).queue()
        );
    }

    private void handleSlashClean(SlashCommandInteractionEvent event) {
        Role foundRole = event.getOption("target-role").getAsRole();

        try {
            List<Member> membersWithRole = event.getGuild().getMembersWithRoles(foundRole);
            if (membersWithRole.isEmpty()) {
                event.reply("No members found with role `" + foundRole.getName() + "`.").setEphemeral(true).queue();
                return;
            }
            membersWithRole.forEach(member -> {
                event.getGuild().removeRoleFromMember(member, foundRole).queue(
                        success -> log.info("Removed role {} from member {}", foundRole.getName(), member.getUser().getAsTag()),
                        error -> log.error("Failed to remove role {} from member {}: {}", foundRole.getName(), member.getUser().getAsTag(), error.getMessage())
                );
            });
            event.reply("All members have been removed from role `" + foundRole.getName() + "`. Total members removed: " + membersWithRole.size()).queue();
        } catch (Exception e) {
            log.error("Error while cleaning role {}: {}", foundRole.getName(), e.getMessage(), e);
            event.reply("Error while cleaning role `" + foundRole.getName() + "`: " + e.getMessage()).setEphemeral(true).queue();
        }

    }

    private void handleSlashBanner(SlashCommandInteractionEvent event) {
        Role foundRole = event.getOption("target-role").getAsRole();
        OptionMapping emojiOption = event.getOption("emoji");

        Emoji emoji = Emoji.fromUnicode("✅"); // default emoji
        if (emojiOption != null) {
            try {
                emoji = Emoji.fromFormatted(emojiOption.getAsString());
            } catch (IllegalArgumentException e) {
                event.reply("Invalid emoji format provided.").setEphemeral(true).queue();
                return;
            }
        }

        String title = foundRole.getName() + " Role";
        String description = "If you'd like to have the **" + foundRole.getName() + "** role assigned to you, click on the reaction below.";

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(foundRole.getColorRaw());

        Emoji finalEmoji = emoji;
        event.getChannel().sendMessageEmbeds(eb.build()).queue(
                message -> {
                    message.addReaction(finalEmoji).queue();
                    event.reply("Role reaction banner generated successfully.").setEphemeral(true).queue();
                },
                error -> event.reply("Failed to post banner: " + error.getMessage()).setEphemeral(true).queue()
        );
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (!arePermissionsValid(event)) {
            log.warn("User {} (id:{}) tried to access unauthorized command.", event.getAuthor().getName(), event.getAuthor().getId());
            event.getMessage().reply("Sorry, you don't have permission to use this command.").queue();
            return;
        }

        if (args.length == 0) {
            event.getMessage().reply("Please specify a subcommand.").queue();
            return;
        }

        processRoleCommand(event, args);
    }

    private void processRoleCommand(MessageReceivedEvent event, String[] args) {
        log.info("Processing arguments {}", String.join(",", args));
        switch (args[0].toLowerCase()) {
            case "add", "+" -> addRole(event, args);
            case "remove", "rm", "delete", "-" -> removeRole(event, args);
            case "clean" -> cleanRole(event, args);
            case "banner" -> createRoleReactionBanner(event, args);
            default -> {
                log.error("No option '{}' for role command.", args[0]);
                event.getMessage().reply("No option `" + args[0] + "` for the role command.").queue();
            }
        }
    }

    private void createRoleReactionBanner(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getMessage().reply("Usage: `role banner <role_name> [emoji]`").queue();
            return;
        }
        String roleName = args[1];
        String optionalEmoji = args.length > 2 ? args[2] : null;

        List<Role> foundRoleList = event.getGuild().getRolesByName(roleName, true);
        if (foundRoleList.isEmpty()) {
            event.getMessage().reply("Role with name '" + roleName + "' does not exist.").queue();
            return;
        }
        Role foundRole = foundRoleList.get(0);

        String title = foundRole.getName() + " Role";
        String description = "If you'd want to have **" + foundRole.getName() + "** role assigned to you, click on the reaction below this message.";
        if (optionalEmoji != null) description += " [ " + optionalEmoji + " ]";
        Emoji emoji = optionalEmoji != null ? Emoji.fromFormatted(optionalEmoji) : Emoji.fromUnicode("✅");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(description);
        eb.setColor(foundRole.getColorRaw());

        event.getChannel().sendMessageEmbeds(eb.build()).queue(
                something -> {
                    log.info("Role assign banner created for role {}", roleName);
                    something.addReaction(emoji).queue();
                }
        );
    }

    private void cleanRole(MessageReceivedEvent event, String[] args) {
        if (args.length < 2 || !doesRoleExist(event, args[1])) {
            event.getMessage().reply("Role with name '" + (args.length > 1 ? args[1] : "") + "' does not exist.").queue();
            return;
        }

        log.warn("User attempted 'role clean' command which is not implemented yet...");
        event.getMessage().reply("Sorry, `role clean` functionality is not implemented yet!").queue();
    }

    private boolean doesRoleExist(MessageReceivedEvent event, String roleName) {
        List<Role> existingRoles = event.getGuild().getRolesByName(roleName, true);
        return !existingRoles.isEmpty();
    }

    private void addRole(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getMessage().reply("Usage: `role add <role_name> [color]`").queue();
            return;
        }
        String roleName = args[1];
        String colorCode = args.length >= 3 ? args[2] : generateRandomColorHexCode();
        Color color;
        try {
            color = parseColorFromHexCode(colorCode);
        } catch (IllegalArgumentException e) {
            log.error("Value '{}' cannot be converted into a Color object.", colorCode);
            event.getMessage().reply("Value `" + colorCode + "` cannot be converted into color. Please provide a valid HEX format.").queue();
            return;
        }

        List<Role> existingRoles = event.getGuild().getRolesByName(roleName, true);
        if (!existingRoles.isEmpty()) {
            log.error("Attempted to create role {} on server {}, but it already exists.", roleName, event.getGuild().getName());
            event.getMessage().reply("Role <@&" + existingRoles.get(0).getId() + "> already exists.").queue();
            return;
        }

        log.info("Creating a guild role with name {}...", roleName);
        event.getGuild().createRole().setName(roleName).setColor(color).queue(
                role -> {
                    role.getManager().setMentionable(true).queue();
                    event.getMessage().reply("Role <@&" + role.getId() + "> has been created.").queue();
                }
        );
    }

    private void removeRole(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getMessage().reply("Usage: `role remove <role_name>`").queue();
            return;
        }
        List<Role> existingRoles = event.getGuild().getRolesByName(args[1], true);
        if (existingRoles.isEmpty()) {
            event.getMessage().reply("Role `" + args[1] + "` does not exist on this server.").queue();
            return;
        }

        String foundRoleName = existingRoles.get(0).getName();
        log.info("Deleting role '{}' from server {}.", foundRoleName, event.getGuild().getName());
        existingRoles.get(0).delete().queue(
                role -> event.getMessage().reply("Role `" + foundRoleName + "` has been removed.").queue()
        );
    }

    @Override
    public List<String> getAliases() {
        return List.of("role", "-r");
    }

    private boolean arePermissionsValid(MessageReceivedEvent event) {
        try {
            return event.getGuild().getMemberById(event.getAuthor().getId()).getPermissions().contains(Permission.MANAGE_ROLES);
        } catch (NullPointerException e) {
            log.error("Can't find message author with id: {} on server {}", event.getAuthor().getId(), event.getGuild().getName());
            event.getMessage().reply("Sorry, something went wrong, check logs!").queue();
        }
        return false;
    }

    private String generateRandomColorHexCode() {
        Random random = new Random();
        int nextInt = random.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }



    @Override
    public String getShortDocs() {
        return "Server role management commands";
    }

    @Override
    public String getDocs() {
        return """
                `role/-r` command allows a moderator to manage roles in a given server.
                Both Kat-Bot and the user need to have `MANAGE_ROLES` permissions assigned.
                
                **Subcommands:**
                * `/role add <name> [color]` - Creates a role.
                * `/role remove <role>` - Deletes a role.
                * `/role clean <role>` - Removes all users from a role.
                * `/role banner <role> [emoji]` - Sends a reaction role banner.
                """;
    }
}