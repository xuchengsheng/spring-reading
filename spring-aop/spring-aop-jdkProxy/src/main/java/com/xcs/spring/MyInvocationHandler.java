package com.xcs.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class MyInvocationHandler implements InvocationHandler {

    private Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before method execution");
        Object result = method.invoke(target, args);
        System.out.println("After method execution");
        return result;
    }
}
