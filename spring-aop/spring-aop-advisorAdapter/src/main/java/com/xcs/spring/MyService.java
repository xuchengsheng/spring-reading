package com.xcs.spring;

public class MyService {

    public String foo() {
        System.out.println("Executing foo method");
        return "hello foo";
    }

    public String bar() {
        System.out.println("Executing bar method");
        return null;
    }
}
