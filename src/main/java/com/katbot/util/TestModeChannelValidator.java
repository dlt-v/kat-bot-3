package com.katbot.util;

import java.util.Objects;

public class TestModeChannelValidator {

    private static final String testingChannelId = System.getenv("testing-channel-id");
    private static final boolean isBotInTestMode = "testing".equals(System.getenv("environment"));

    /**
     * Bot will respond only in the designated test channel if it's in "testing" mode.
     * Bot in "production" mode will answer in all channels other than test channel.
     * @param channelId channel id of the event
     * @return will be true if channel is valid and bot can process message further
     */
    public static boolean isInValidChannel(String channelId) {
        Objects.requireNonNull(channelId, "Channel ID cannot be null");

        if (isBotInTestMode && testingChannelId.equals(channelId)) return true; // correct testing mode
        else if (!isBotInTestMode && !testingChannelId.equals(channelId)) return true; // correct production mode
        else return false; // invalid combination
    }
}
