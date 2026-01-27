package com.katbot.messageHandlers.commandHandler.commands.SecretSanta;

public class SecretSantaException extends RuntimeException {

    public SecretSantaException(String message) {
        super(message);
    }

    public SecretSantaException(String message, Throwable cause) {
        super(message, cause);
    }
}
