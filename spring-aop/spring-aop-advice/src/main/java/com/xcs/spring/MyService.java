package com.xcs.spring;

public class MyService {

    public int doSomething() {
        System.out.println("Doing something...");
        return 1;
    }

    public void doSomethingException() {
        System.out.println("Doing something exception...");
        int i = 1 / 0;
    }
}
