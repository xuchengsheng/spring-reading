package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;

public class AfterReturningAdviceDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyAfterReturningAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.foo();
    }
}
