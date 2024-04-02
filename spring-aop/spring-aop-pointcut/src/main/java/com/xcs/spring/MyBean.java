package com.xcs.spring;

@MyAnnotation
public class MyBean {
    public void getName() {
        System.out.println("getName() method");
    }

    public void setName() {
        System.out.println("setName() method");
    }

    public void getAge() {
        System.out.println("getAge() method");
    }
}
