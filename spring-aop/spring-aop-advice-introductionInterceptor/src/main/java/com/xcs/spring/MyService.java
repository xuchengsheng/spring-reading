package com.xcs.spring;

public class MyService {

    public void foo() {
        System.out.println("foo...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
