package com.xcs.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class MyAspect {

    @Before("execution(* FooService+.*(..))")
    public void advice() {
        System.out.println("Before method execution");
    }
}
