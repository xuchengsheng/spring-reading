package com.xcs.spring;

@MyClassAnnotation
public class MyService {

    public void getName() {
        System.out.println("getName...");
    }

    @MyMethodAnnotation
    public void setName() {
        System.out.println("setName...");
    }

    public void getAge() {
        System.out.println("getAge...");
    }
}
