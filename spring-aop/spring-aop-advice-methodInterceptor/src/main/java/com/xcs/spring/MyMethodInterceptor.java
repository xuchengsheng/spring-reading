package com.xcs.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 在方法调用之前执行的逻辑
        System.out.println("Before Method " + invocation.getMethod().getName());
        // 调用原始方法
        Object result = invocation.proceed();
        // 在方法调用之后执行的逻辑
        System.out.println("After Method " + invocation.getMethod().getName());
        return result;
    }
}
