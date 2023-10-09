package com.xcs.spring.config;

import com.xcs.spring.event.MyEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MyApplicationContextAware implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public void publish(String message) {
        context.publishEvent(new MyEvent(this, message));
    }
}
