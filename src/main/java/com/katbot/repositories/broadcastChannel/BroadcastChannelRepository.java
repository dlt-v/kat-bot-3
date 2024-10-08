package com.katbot.repositories.broadcastChannel;

import java.util.List;

public interface BroadcastChannelRepository {

    public List<BroadcastChannel> getBroadcastChannelIDs();

    public boolean isBroadcastChannel(long channelId);

    public void addBroadcastChannel(long channelId);

}
