package com.katbot.repositories.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("userRepository")
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean doesUserHaveBroadcastEnabled(Long userId) {
        String sql = "SELECT broadcast FROM katbot_database.users WHERE id = ?";
        List<Boolean> results = jdbcTemplate.query(
                sql,
                new Object[]{userId},
                (rs, rowNum) -> rs.getBoolean("broadcast")
        );

        // If the user is found and broadcast is true, return true; otherwise, return false.
        return !results.isEmpty() && Boolean.TRUE.equals(results.get(0));
    }

}
