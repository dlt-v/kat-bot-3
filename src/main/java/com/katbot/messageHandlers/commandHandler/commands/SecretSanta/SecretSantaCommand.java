package com.katbot.messageHandlers.commandHandler.commands.SecretSanta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbot.messageHandlers.commandHandler.commands.Command;
import com.katbot.parameters.ParameterService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.katbot.util.StringUtils.capitalizeFirstLetter;

@Component
public class SecretSantaCommand implements Command {

    private final ParameterService parameterService;
    private static final Logger logger = LoggerFactory.getLogger(SecretSantaCommand.class);
    private SecretSantaHistory secretSantaHistory;

    private final String ROLE_NAME = "Krampus-25";

    public SecretSantaCommand(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Check ownerID is the same as the user who sent the message
     */
    private void authorize(MessageReceivedEvent event) {
        String senderId = event.getAuthor().getId();
        String deltaId = parameterService.getTestingUserID();
        if (!senderId.equals(deltaId)) {
            logger.error("Unauthorized user tried to use the Secret Santa command: {}", senderId);
            throw new SecretSantaException("This user is not authorised to use this command");
        }
    }

    private Guild fetchSecretSantaGuild(MessageReceivedEvent event) {
        String secretSantaServerId = System.getenv("secret-santa-server-id");
        Guild santaGuild = event.getJDA().getGuildById(secretSantaServerId);
        if (santaGuild == null) {
            logger.error("Can't find a guild with this id: {}", secretSantaServerId);
            throw new SecretSantaException("Can't find secret santa server");
        }
        return santaGuild;
    }

    private Role fetchCurrentRole(Guild guild) {
        List<Role> roles = guild.getRolesByName(ROLE_NAME, true);

        if (roles.isEmpty()) {
            logger.error("Can't find a role with a name: {}", ROLE_NAME);
            throw new SecretSantaException("Can't find a role with a name: " + ROLE_NAME);
        }

        return roles.get(0);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        // incoming message: "kat santa"
        try {
            authorize(event);
            Guild guild = fetchSecretSantaGuild(event);
            Role selectedRole = fetchCurrentRole(guild);

            guild.findMembers(member -> member.getRoles().contains(selectedRole))
                    .onSuccess(secretSantaCandidates -> {
                        try {
                            processCandidates(event, secretSantaCandidates, args);
                        } catch (SecretSantaException e) {
                            logger.error("Secret santa functionality has thrown an exception in onSuccess: {}", e.getMessage());
                            event.getMessage().reply("Secret santa functionality has thrown an exception: " + e.getMessage()).queue();
                        } catch (Exception e) {
                            logger.error("Unknown error has occurred in onSuccess: {}", e.getMessage());
                            event.getMessage().reply("Unknown error has occurred: " + e.getMessage()).queue();
                        }
                    })
                    .onError(error -> {
                        logger.error("Error fetching candidates: {}", error.getMessage());
                        event.getMessage().reply("An error occurred while fetching participants: " + error.getMessage()).queue();
                    });

        } catch (SecretSantaException e) {
            logger.error("Secret santa functionality has thrown an exception: {}", e.getMessage());
            event.getMessage().reply("Secret santa functionality has thrown an exception: " + e.getMessage()).queue();
        } catch (Exception e) {
            logger.error("Unknown error has occurred: {}", e.getMessage());
            event.getMessage().reply("Unknown error has occurred: " + e.getMessage()).queue();
        }
    }

    private void processCandidates(MessageReceivedEvent event, List<Member> candidateList, String[] args) {
        boolean isReal = Arrays.asList(args).contains("-real"); // Don't add it when testing the app
        if (isReal) logger.info("-real flag detected. Formatting messages accordingly.");

        this.secretSantaHistory = loadHistory();

        Map<String, List<String>> allPreviousPairs = fetchAllPreviousPairs();

        List<String> currentSenders = candidateList.stream()
                .map(member -> this.secretSantaHistory.getUserNameById(member.getId()))
                .filter(Objects::nonNull) // Filter out candidates not in the JSON file
                .toList();

        if (currentSenders.isEmpty()) {
            event.getMessage().reply("No valid participants found in the Secret Santa history data.").queue();
            return;
        }

        Map<String, String> assignments = generateAssignments(currentSenders, allPreviousPairs);

        if (Arrays.asList(args).contains("-info")) {
            logger.info("-info flag detected. Displaying diagnostic info");
            displayDiagnosticInfo(event, allPreviousPairs, candidateList, assignments, isReal);
        }

        sendAssignments(event, assignments, isReal);
    }

    private void displayDiagnosticInfo(MessageReceivedEvent event, Map<String, List<String>> allPreviousPairs, List<Member> candidateList, Map<String, String> assignments, boolean isReal) {
        String formattedPairs = allPreviousPairs.entrySet().stream()
                .map(entry -> "- " + capitalizeFirstLetter(entry.getKey()) + " gifted " + entry.getValue())
                .collect(Collectors.joining("\n"));

        String message = isReal ? "**This is a real run.**\n\n" : "**This is a testing run.**\n\n";
        message += "All past Secret Santa pairs:\n" + formattedPairs;
        String currentParticipants = formatCurrentParticipants(this.secretSantaHistory, candidateList);
        message += "\n\nThis edition's participants (" + candidateList.size() + "):\n" + currentParticipants;

//        if (isReal) {
//            String currentAssignments = formatCurrentAssignments(assignments);
//            message += "\nThis edition's assignments (for tests):\n" + currentAssignments;
//        } else {
//
//        }

        message += "\nThis year's assignments are going to be sent to Secret Santa Host in spoilered message";

        event.getMessage().reply(message).queue();
    }

    private String formatCurrentAssignments(Map<String, String> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return "No assignments were generated.";
        }
        return assignments.entrySet().stream()
                .map(entry -> "- " + capitalizeFirstLetter(entry.getKey()) + " gifts " + capitalizeFirstLetter(entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private String formatCurrentParticipants(SecretSantaHistory history, List<Member> candidates) {
        StringBuilder formattedList = new StringBuilder();

        for (Member candidate : candidates) {
            formattedList.append("- ").append(capitalizeFirstLetter(history.getUserNameById(candidate.getId()))).append("\n");
        }

        return formattedList.toString();
    }

    private SecretSantaHistory loadHistory() throws SecretSantaException {
        ObjectMapper mapper = new ObjectMapper();
        String filename = "secret-santa-pair-history.json";

        try {
            // Read the file from the resources folder
            ClassPathResource resource = new ClassPathResource(filename);

            // Map the JSON stream to the SecretSantaHistory class
            return mapper.readValue(resource.getInputStream(), SecretSantaHistory.class);

        } catch (IOException e) {
            logger.error("Failed to load or parse JSON file: {}", filename, e);
            throw new SecretSantaException("Failed to load Secret Santa history data.", e);
        }
    }

    /**
     * Aggregates all Secret Santa pairs from all editions, allowing multiple
     * receivers for the same sender across different years.
     */
    private Map<String, List<String>> fetchAllPreviousPairs() {
        // Change the return type to map a Sender to a List of their past Receivers
        Map<String, List<String>> allPairs = new HashMap<>();

        if (this.secretSantaHistory.getHistory() != null) {
            for (EditionHistory edition : this.secretSantaHistory.getHistory()) {
                if (edition.getPairs() != null) {

                    // Iterate through the pairs of the current edition
                    edition.getPairs().forEach((sender, receiver) -> {

                        // Use computeIfAbsent to ensure a list exists for the sender key
                        allPairs.computeIfAbsent(sender, k -> new ArrayList<>()).add(receiver);
                    });
                }
            }
        }
        return allPairs;
    }

    private Map<String, String> generateAssignments(List<String> senders, Map<String, List<String>> previousPairs) {
        List<String> receivers = new ArrayList<>(senders);
        Collections.shuffle(receivers);

        Map<String, String> assignments = new HashMap<>();

        final int MAX_RETRIES = 5 * senders.size();
        int retries = 0;

        for (String sender: senders) {
            String receiver = null;
            int attempt = 0;

            // Loop until a valid receiver is found or max attempts reached
            while (receiver == null && attempt < MAX_RETRIES) {
                if (receivers.isEmpty()) throw new SecretSantaException("Not enough unique receivers to complete pairings.");

                String potentialReceiver = receivers.remove(0);

                // 1. Check for self-assignment (Sender != Receiver)
                boolean isSelfAssignment = potentialReceiver.equals(sender);

                // 2. Check if sender has gifted this receiver before
                List<String> giftedHistory = previousPairs.getOrDefault(sender, Collections.emptyList());
                boolean hasGiftedBefore = giftedHistory.contains(potentialReceiver);

                if (!isSelfAssignment && !hasGiftedBefore) {
                    logger.info("Found a valid match. {} prepares a gift for {}", sender, potentialReceiver);
                    receiver = potentialReceiver;
                } else {
                    receivers.add(potentialReceiver);

                    attempt++;
                    retries++;
                }

                if (retries > MAX_RETRIES) {
                    throw new SecretSantaException("Could not generate valid pairings after multiple retries. History constraints might be too tight.");
                }
            }

            if (receiver == null) {
                // This should be caught by the MAX_RETRIES check above, but for final safety:
                throw new SecretSantaException("Failed to find a valid recipient for sender: " + sender);
            }

            assignments.put(sender, receiver);
        }

        return assignments;
    }

    private void sendAssignments(MessageReceivedEvent event, Map<String, String> assignmentMap, boolean isReal) {

        // Send the host message for debug.
        String answer = assignmentMap.entrySet().stream()
                .map(entry -> entry.getKey().charAt(0) + "||" + entry.getKey().substring(1) + "||" + " -> " + entry.getValue().charAt(0) + "||" + entry.getValue().substring(1) + "||")
                .reduce("", (acc, entry) -> acc + entry + "\n");

        User user = event.getJDA().retrieveUserById(parameterService.getTestingUserID()).complete();
        if (user == null) {
            logger.error("Secret Santa host was not found.");
            throw new SecretSantaException("Secret Santa host could not be found.");
        }
        answer = "Secret Krampus assignments:\n" + answer;
        answer = isReal ? "**This is not a test.**\n" : "**This is just a test.**\n" + answer;
        final String finalAnswer = answer;
        user.openPrivateChannel().queue(channel -> channel.sendMessage(finalAnswer).queue());
        logger.info("A debug message has been sent to Secret Santa host.");


        // Sending a message to individual secret santa participant
        assignmentMap.forEach((senderName, receiverName) -> {
            String senderId = this.secretSantaHistory.getIdByUserName(senderName);
            User senderUser = event.getJDA().retrieveUserById(senderId).complete();

            if (senderUser == null) {
                logger.error("Could not fetch a sender JDA user object with this name: {}", senderName);
                throw new SecretSantaException("Could not fetch a sender JDA user object with this name: " + senderName);
            }

            final String messageForSender = "**Secret Krampus 2025**\nHello! " + (isReal ? "This is your **real** Secret Santa assignment: " : "This is just for **testing purposes**, here's your test assignment: `") + capitalizeFirstLetter(receiverName) + "`. Good luck!";
            senderUser.openPrivateChannel().queue(channel -> channel.sendMessage(messageForSender).queue());
            logger.info("A user {} has been sent a message that their receiver is {}", capitalizeFirstLetter(senderName), capitalizeFirstLetter(receiverName));
        });

        logger.info("Secret Santa assignments have been sent out!");
        event.getMessage().reply("Secret Santa assignments have been successfully sent out! Good luck!").queue();

    }

    @Override
    public List<String> getAliases() {
        return List.of("santa");
    }
}
