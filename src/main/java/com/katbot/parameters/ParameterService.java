package com.katbot.parameters;

import com.katbot.parameters.enums.EnvironmentEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParameterService {

    private EnvironmentEnum environment;
    private final String testingChannelID;
    private final String testingUserID;

    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterService.class);


    public ParameterService() {
        setEnvironment(System.getenv("environment"));
        LOGGER.info("Environment is set to: {}", environment);

        testingChannelID = System.getenv("testing-channel-id");
        testingUserID = System.getenv("testing-user-id");
    }

    public boolean isInTest() {
        return environment == EnvironmentEnum.TESTING;
    }

    public void setEnvironment(String environmentString) {
        try {
            this.environment = EnvironmentEnum.valueOf(environmentString.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.environment = EnvironmentEnum.PRODUCTION;
        }
    }

    public String getTestingUserID() {
        return testingUserID;
    }

    public String getTestingChannelID() {
        return testingChannelID;
    }
}
