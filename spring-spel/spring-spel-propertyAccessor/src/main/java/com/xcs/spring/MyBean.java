package com.xcs.spring;

public class MyBean {

    public MyBean(String name) {
        this.name = name;
    }

    private String name;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
