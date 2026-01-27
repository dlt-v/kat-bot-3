package com.katbot.messageHandlers.commandHandler.commands.SecretSanta;

import java.util.Map;

public class EditionHistory {
    private String edition;
    private Map<String, String> pairs;

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public Map<String, String> getPairs() { return pairs; }
    public void setPairs(Map<String, String> pairs) { this.pairs = pairs; }
}
