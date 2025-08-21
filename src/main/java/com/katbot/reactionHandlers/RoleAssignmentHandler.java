package com.katbot.reactionHandlers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RoleAssignmentHandler implements ReactionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RoleAssignmentHandler.class);
    private static final Pattern ROLE_REGEX = Pattern.compile("^If you'd want to have \\*\\*(.*?)\\*\\* role assigned to you, click on the reaction below this message\\.$");

    @Override
    public boolean isValid(GenericMessageReactionEvent event) {
        if (event.retrieveMessage().complete().getEmbeds().isEmpty()) return false;

        String description = event.retrieveMessage().complete().getEmbeds().get(0).getDescription();
        if (description == null) return false;

        Matcher matcher = ROLE_REGEX.matcher(description);
        return matcher.matches();
    }

    @Override
    public void process(GenericMessageReactionEvent event, boolean reactionAdded) {
        User user = event.getUser();
        if (user == null) {
            logger.error("Retrieved user is null");
            return;
        }

        String roleName = retrieveRoleName(event.retrieveMessage().complete());
        List<Role> roleList = event.getGuild().getRolesByName(roleName, true);
        if (roleList.isEmpty()) {
            logger.error("No roles in this guild that have this name");
            return;
        }
        Role role = roleList.get(0);

        if (reactionAdded) {
            event.getGuild().addRoleToMember(user, role).complete();
            logger.info("Assigning a role '{}' to {} in server '{}'", role.getName(), user.getName(), event.getGuild().getName());
        } else {
            event.getGuild().removeRoleFromMember(user, role).complete();
            logger.info("Removing a role '{}' from {} in server '{}'", role.getName(), user.getName(), event.getGuild().getName());
        }
    }

    private String retrieveRoleName(Message complete) {
        String description = complete.getEmbeds().get(0).getDescription();

        if (description == null) {
            logger.error("Embed has empty description, can't find role.");
            throw new RuntimeException("Embed has empty description, can't find role.");
        }

        Matcher matcher = ROLE_REGEX.matcher(description);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            logger.error("Cannot parse the description from Kat-Bot's embed.");
            return null;
        }

    }
}
