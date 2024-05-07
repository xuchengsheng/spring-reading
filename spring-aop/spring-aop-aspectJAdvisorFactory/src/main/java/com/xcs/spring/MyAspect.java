package com.xcs.spring;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
class MyAspect {

    @Before("execution(* com.xcs.spring.MyService.foo(..))")
    public void before() {
        System.out.println("Before executing the method..." );
    }

    @After("execution(* com.xcs.spring.MyService.foo(..))")
    public void after() {
        System.out.println("After executing the method..." );
    }
}
