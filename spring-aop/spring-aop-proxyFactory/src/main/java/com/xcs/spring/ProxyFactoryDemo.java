package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;

public class ProxyFactoryDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyMethodBeforeAdvice());
        // 获取代理对象
        Object proxy = proxyFactory.getProxy();
        // 调用代理对象的方法
        System.out.println("proxy = " + proxy.getClass());
    }
}
