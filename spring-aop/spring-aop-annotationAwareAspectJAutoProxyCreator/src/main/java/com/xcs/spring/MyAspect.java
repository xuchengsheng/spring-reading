package com.xcs.spring;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
class MyAspect {

    @Before("execution(* com.xcs.spring.MyService.doSomething(..))")
    public void before() {
        System.out.println("Before executing the method..." );
    }

    @After("execution(* com.xcs.spring.MyService.doSomething(..))")
    public void after() {
        System.out.println("After executing the method..." );
    }
}
