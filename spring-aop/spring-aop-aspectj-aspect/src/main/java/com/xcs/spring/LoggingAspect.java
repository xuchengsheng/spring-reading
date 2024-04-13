package com.xcs.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
class LoggingAspect {

    @Before("execution(* com.xcs.spring.MyService.doSomething(..))")
    public void beforeAdvice() {
        System.out.println("Before executing the method..." );
    }
}
