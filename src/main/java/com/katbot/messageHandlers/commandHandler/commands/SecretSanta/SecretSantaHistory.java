package com.katbot.messageHandlers.commandHandler.commands.SecretSanta;

import java.util.List;
import java.util.Map;

public class SecretSantaHistory {
    private Map<String, String> users;
    private List<EditionHistory> history;

    public Map<String, String> getUsers() { return users; }
    public void setUsers(Map<String, String> users) { this.users = users; }
    public String getUserNameById(String userId) {
        if (users == null || userId == null) {
            return null;
        }

        for (Map.Entry<String, String> entry : users.entrySet()) {
            if (entry.getValue().equals(userId)) {
                return entry.getKey();
            }
        }

        throw new SecretSantaException("User with id " + userId + " is not defined in the json file");
    }
    public String getIdByUserName(String userName) {
        if (users == null || userName == null) {
            return null;
        }

        for (Map.Entry<String, String> entry : users.entrySet()) {
            if (entry.getKey().equals(userName)) {
                return entry.getValue();
            }
        }

        throw new SecretSantaException("User with username " + userName + " is not defined in the json file");
    }

    public List<EditionHistory> getHistory() { return history; }
    public void setHistory(List<EditionHistory> history) { this.history = history; }
}
