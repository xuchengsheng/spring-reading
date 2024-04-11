package com.xcs.spring;

public class MyService {

    public void doSomethingException() {
        System.out.println("Doing something exception...");
        int i = 1 / 0;
    }
}
