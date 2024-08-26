package com.katbot.parameters;

import com.katbot.parameters.enums.EnvironmentEnum;
import org.springframework.stereotype.Service;

@Service
public class ParameterService {
    private EnvironmentEnum environment;

    public ParameterService() {
        this.environment = EnvironmentEnum.PRODUCTION;
    }

    public EnvironmentEnum getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environmentString) {
        try {
            this.environment = EnvironmentEnum.valueOf(environmentString.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.environment = EnvironmentEnum.PRODUCTION;
        }
    }
}
