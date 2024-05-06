package com.xcs.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class MyAspect {

    @Before("execution(* com.xcs.spring.MyService+.*(..))")
    public void before() {
        System.out.println("Before method execution");
    }
}
