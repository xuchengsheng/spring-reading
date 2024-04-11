package com.xcs.spring;

import org.springframework.aop.ThrowsAdvice;

public class MyThrowsAdvice implements ThrowsAdvice {
    public void afterThrowing(Exception ex) throws Throwable {
        System.out.println("Exception thrown: " + ex.getMessage());
    }
}
