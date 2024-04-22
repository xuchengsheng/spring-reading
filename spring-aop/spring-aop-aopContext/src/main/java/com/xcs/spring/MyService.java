package com.xcs.spring;

import org.springframework.aop.framework.AopContext;

@MyAnnotation
public class MyService {

    public void foo() {
        System.out.println("foo...");
        // 直接调用bar会导致切入无效
        // this.bar();
        // 获取代理对象并调用bar
        ((MyService) AopContext.currentProxy()).bar();
    }

    public void bar() {
        System.out.println("bar...");
    }
}
