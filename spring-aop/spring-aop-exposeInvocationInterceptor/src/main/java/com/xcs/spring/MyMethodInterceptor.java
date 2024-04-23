package com.xcs.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyMethodInterceptor {

    @Before("execution(public * com.xcs.spring.MyService.*(..))")
    public void before() {
        LogUtil.print();
    }
}
