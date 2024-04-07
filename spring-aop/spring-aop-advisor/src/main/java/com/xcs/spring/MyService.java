package com.xcs.spring;

public class MyService {

    @MyCustomAnnotation
    public void foo() {
        System.out.println("Executing foo method");
    }

    public void bar() {
        System.out.println("Executing bar method");
    }
}
