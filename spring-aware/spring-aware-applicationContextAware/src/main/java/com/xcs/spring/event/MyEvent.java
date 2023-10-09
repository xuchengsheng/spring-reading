package com.xcs.spring.event;

import org.springframework.context.ApplicationEvent;

public class MyEvent extends ApplicationEvent {

    private final String message;

    public MyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}