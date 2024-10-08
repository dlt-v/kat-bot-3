package com.katbot.repositories.broadcastChannel;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("broadcastChannelRepository")
public class BroadcastChannelRepositoryImpl implements BroadcastChannelRepository {

    private final JdbcTemplate jdbcTemplate;

    public BroadcastChannelRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isBroadcastChannel(long channelId) {
        return getBroadcastChannelIDs().contains(channelId);
    }

    @Override
    public void addBroadcastChannel(long channelId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<BroadcastChannel> getBroadcastChannelIDs() {
        return jdbcTemplate.query(
                "SELECT id, server_name FROM katbot_database.broadcast_channels",
                (rs, rowNum) -> new BroadcastChannel(
                        rs.getLong("id"),
                        rs.getString("server_name")
                )
        );
    }

}
