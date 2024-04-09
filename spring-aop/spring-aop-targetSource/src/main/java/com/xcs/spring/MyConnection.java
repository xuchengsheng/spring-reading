package com.xcs.spring;

public class MyConnection {

    private String name;

    public MyConnection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyConnection{" +
                "name='" + name + '\'' +
                '}';
    }
}
