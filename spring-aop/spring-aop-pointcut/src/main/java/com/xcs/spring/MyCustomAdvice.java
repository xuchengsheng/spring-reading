package com.xcs.spring;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

class MyCustomAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before advice is executed");
    }
}
