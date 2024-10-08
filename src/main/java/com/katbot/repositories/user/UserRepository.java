package com.katbot.repositories.user;

import org.springframework.stereotype.Component;

@Component
public interface UserRepository {

    public boolean doesUserHaveBroadcastEnabled(Long userId);

}
