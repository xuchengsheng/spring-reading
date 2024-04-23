package com.xcs.spring;

import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;

public class LogUtil {

    public static void print() {
        ProxyMethodInvocation methodInvocation = (ProxyMethodInvocation) ExposeInvocationInterceptor.currentInvocation();
        System.out.println("Method = " + methodInvocation.getMethod());
        System.out.println("Arguments Length = " + methodInvocation.getArguments().length);
        System.out.println("Target = " + methodInvocation.getThis());
        System.out.println("Proxy Class = " + methodInvocation.getProxy().getClass());
    }
}
