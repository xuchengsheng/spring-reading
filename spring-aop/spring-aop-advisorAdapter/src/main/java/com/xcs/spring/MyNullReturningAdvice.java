package com.xcs.spring;

import java.lang.reflect.Method;

public class MyNullReturningAdvice implements NullReturningAdvice {

    @Override
    public Object nullReturning(Method method, Object[] args, Object target) throws Throwable {
        return "this is a defaultValue";
    }
}
