package com.xcs.spring;

public class MyService {

    @MyCustomAnnotation
    public void foo() {
        System.out.println("foo...");
    }
}
