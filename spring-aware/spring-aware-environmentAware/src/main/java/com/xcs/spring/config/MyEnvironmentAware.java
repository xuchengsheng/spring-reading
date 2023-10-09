package com.xcs.spring.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class MyEnvironmentAware implements EnvironmentAware {

    private String appProperty;

    @Override
    public void setEnvironment(Environment environment) {
        this.appProperty = environment.getProperty("app.xcs.property");
    }

    public String getAppProperty() {
        return appProperty;
    }
}
