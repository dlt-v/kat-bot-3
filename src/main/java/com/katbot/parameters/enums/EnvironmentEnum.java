package com.katbot.parameters.enums;

public enum EnvironmentEnum {
    TESTING("testing"),
    PRODUCTION("production");

    private final String environment;

    EnvironmentEnum(String environment) {
        this.environment = environment;
    }

    public String getEnvironment() {
        return environment;
    }
}
