package com.xcs.spring;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("Before invoking method: " + method.getName());
        Object result = methodProxy.invokeSuper(obj, args);
        System.out.println("After invoking method: " + method.getName());
        return result;
    }
}
