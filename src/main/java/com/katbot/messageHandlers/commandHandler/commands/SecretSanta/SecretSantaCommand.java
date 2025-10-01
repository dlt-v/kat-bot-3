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
        String roleName = "krampus-25"; // TODO: Get this programmatically.
        List<Role> roles = guild.getRolesByName(roleName, true);

        if (roles.isEmpty()) {
            logger.error("Can't find a role with a name: {}", roleName);
            throw new SecretSantaException("Can't find a role with a name: " + roleName);
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
                        processCandidates(event, secretSantaCandidates, args);
                    })
                    .onError(error -> {
                        logger.error("Error fetching candidates: {}", error.getMessage());
                        throw new SecretSantaException("Error fetching candidates.");
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

        SecretSantaHistory history = loadHistory();
        Map<String, String> allUsers = history.getUsers();

        Map<String, List<String>> allPreviousPairs = fetchAllPreviousPairs(history);

        String formattedPairs = allPreviousPairs.entrySet().stream()
                .map(entry -> "- " + capitalizeFirstLetter(entry.getKey()) + " gifted " + entry.getValue())
                .collect(Collectors.joining("\n"));

        String message = "**All Past Secret Santa Pairs:**\n" + formattedPairs;

        event.getMessage().reply(message).queue();
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
    private Map<String, List<String>> fetchAllPreviousPairs(SecretSantaHistory history) {
        // Change the return type to map a Sender to a List of their past Receivers
        Map<String, List<String>> allPairs = new HashMap<>();

        if (history.getHistory() != null) {
            for (EditionHistory edition : history.getHistory()) {
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

//        List<Member> secretSantaCandidates = santaGuild.getMembersWithRoles(selectedRole);
//        event.getMessage().reply("Candidate amount: " + secretSantaCandidates.size()).queue();

//        HashMap<String, String> userMap = fetchAllSecretSantaUsers();
//        Map<String, String> lastYearCombinationsMap = fetchCombinationsFromLastYear();
//
//        List<String> unassignedReceiver = new ArrayList<>(userMap.keySet());
//        Map<String, String> senderReceiverMap = new HashMap<>();
//
//        // Assign each user a Secret Santa recipient
//        for (String sender : userMap.keySet()) {
//            boolean wasSenderNotAlreadyAReceiver = unassignedReceiver.remove(sender);
//            String lastYearReceiver = null;
//            boolean lastYearRemoved = false;
//            if (lastYearCombinationsMap.containsKey(sender)) {
//                lastYearReceiver = lastYearCombinationsMap.get(sender);
//                lastYearRemoved = unassignedReceiver.remove(lastYearReceiver);
//            }
//
//            String receiver = unassignedReceiver.get((int) (Math.random() * unassignedReceiver.size()));
//            unassignedReceiver.remove(receiver);
//
//            senderReceiverMap.put(sender, receiver);
//
//            if (lastYearReceiver != null && lastYearRemoved) unassignedReceiver.add(lastYearReceiver);
//            if (wasSenderNotAlreadyAReceiver) unassignedReceiver.add(sender);
//        }
//        sendAssignments(event, senderReceiverMap, userMap, isReal);



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

    // Top-level class for the entire JSON file
    public static class SecretSantaHistory {
        private Map<String, String> users;
        private List<EditionHistory> history;

        public Map<String, String> getUsers() { return users; }
        public void setUsers(Map<String, String> users) { this.users = users; }

        public List<EditionHistory> getHistory() { return history; }
        public void setHistory(List<EditionHistory> history) { this.history = history; }
    }

    // Class for each item in the "history" array
    public static class EditionHistory {
        private String edition;
        private Map<String, String> pairs;

        public String getEdition() { return edition; }
        public void setEdition(String edition) { this.edition = edition; }

        public Map<String, String> getPairs() { return pairs; }
        public void setPairs(Map<String, String> pairs) { this.pairs = pairs; }
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
