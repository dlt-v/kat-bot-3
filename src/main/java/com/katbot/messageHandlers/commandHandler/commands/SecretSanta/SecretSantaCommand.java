package com.katbot.messageHandlers.commandHandler.commands.SecretSanta;

import com.katbot.messageHandlers.commandHandler.commands.Command;
import com.katbot.parameters.ParameterService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SecretSantaCommand implements Command {

    private final ParameterService parameterService;
    private static final Logger logger = LoggerFactory.getLogger(SecretSantaCommand.class);

    public SecretSantaCommand(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        boolean isReal = Arrays.asList(args).contains("real");

        // Check ownerID is the same as the user who sent the message
        String senderId = event.getAuthor().getId();
        String deltaId = parameterService.getTestingUserID();
        if (!senderId.equals(deltaId)) {
            event.getMessage().reply("You are not authorized to use this command!").queue();
            logger.error("Unauthorized user tried to use the Secret Santa command: {}", senderId);
            return;
        }

        HashMap<String, String> userMap = fetchAllSecretSantaUsers();
        Map<String, String> lastYearCombinationsMap = fetchCombinationsFromLastYear();

        List<String> unassignedReceiver = new ArrayList<>(userMap.keySet());
        Map<String, String> senderReceiverMap = new HashMap<>();

        // Assign each user a Secret Santa recipient
        for (String sender : userMap.keySet()) {
            boolean wasSenderNotAlreadyAReceiver = unassignedReceiver.remove(sender);
            String lastYearReceiver = null;
            boolean lastYearRemoved = false;
            if (lastYearCombinationsMap.containsKey(sender)) {
                lastYearReceiver = lastYearCombinationsMap.get(sender);
                lastYearRemoved = unassignedReceiver.remove(lastYearReceiver);
            }

            String receiver = unassignedReceiver.get((int) (Math.random() * unassignedReceiver.size()));
            unassignedReceiver.remove(receiver);

            senderReceiverMap.put(sender, receiver);

            if (lastYearReceiver != null && lastYearRemoved) unassignedReceiver.add(lastYearReceiver);
            if (wasSenderNotAlreadyAReceiver) unassignedReceiver.add(sender);
        }
        sendAssignments(event, senderReceiverMap, userMap, isReal);

    }

    private void sendAssignments(MessageReceivedEvent event, Map<String, String> senderReceiverMap, HashMap<String, String> userMap, boolean isReal) {

        String answer = senderReceiverMap.entrySet().stream()
                .map(entry -> entry.getKey().charAt(0) + "||" + entry.getKey().substring(1) + "||" + " -> " + entry.getValue().charAt(0) + "||" + entry.getValue().substring(1) + "||")
                .reduce("", (acc, entry) -> acc + entry + "\n");

        if (parameterService.isInTest()) {

            answer = "**TEST** Secret Santa assignments (generated on " + (new Date()) + "):\n" + answer;
            logger.info(answer);
            event.getMessage().reply(answer).queue();

        } else {

            event.getMessage().reply("Sending " + senderReceiverMap.size() + " Secret Santa assignments. " + (isReal ? "" : "**This is just a test.**")).queue();
            String testingChannelID = parameterService.getTestingChannelID();
            event.getJDA().getTextChannelById(testingChannelID).sendMessage("Secret Santa assignments:\n" + answer).queue();

            try {

                senderReceiverMap.forEach((sender, receiver) -> {
                    logger.info("Sending Secret Santa assignment to: {}, {}", sender, userMap.get(sender));
                    String userId = userMap.get(sender);
                    User user = event.getJDA().retrieveUserById(userId).complete();
                    user.openPrivateChannel().queue(channel -> channel.sendMessage((isReal ? "**REAL**" : "**THIS IS JUST A TEST, SORRY FOR SPAM**") + " You are the Secret Santa for `" + receiver + "`.").queue());
                    logger.info("Secret Santa assignment successfully sent to: {}", sender);
                });

                event.getMessage().reply("Secret Santa assignments have been sent out!").queue();

            } catch (Exception exception) {
                logger.error("An error occurred while sending out the Secret Santa assignments!", exception);
            }

        }

    }

    private HashMap<String, String> fetchAllSecretSantaUsers() {
        // Fetch all users from the database
        HashMap<String, String> userMap = new HashMap<>();

        // TODO: Fetch them from database for next year.

        return userMap;
    }

    private Map<String, String> fetchCombinationsFromLastYear() {
        // TODO: Fetch all combinations from the database next year
        return Map.of();
    }

    @Override
    public List<String> getAliases() {
        return List.of("santa");
    }
}
