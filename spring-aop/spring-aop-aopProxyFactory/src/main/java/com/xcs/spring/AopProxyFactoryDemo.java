package com.xcs.spring;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.DefaultAopProxyFactory;

public class AopProxyFactoryDemo {

    public static void main(String[] args) {
        // 分别演示 JDK 动态代理和 CGLIB 代理
        jdkProxy();
        cglibProxy();
    }

    /**
     * JDK 动态代理示例
     */
    private static void jdkProxy() {
        // 创建 AdvisedSupport 对象，用于配置 AOP 代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 设置目标对象的类
        advisedSupport.setTargetClass(MyService.class);

        // 创建 DefaultAopProxyFactory 实例
        AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
        // 创建 JDK 动态代理对象
        MyService myService = (MyService) aopProxyFactory.createAopProxy(advisedSupport).getProxy();
        // 打印生成的代理类
        System.out.println("jdkProxy = " + myService.getClass());
    }

    /**
     * CGLIB 代理示例
     */
    private static void cglibProxy() {
        // 创建 AdvisedSupport 对象，用于配置 AOP 代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 创建 DefaultAopProxyFactory 实例
        AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
        // 创建 CGLIB 代理对象
        MyService myService = (MyService) aopProxyFactory.createAopProxy(advisedSupport).getProxy();
        // 打印生成的代理类
        System.out.println("cglibProxy = " + myService.getClass());
    }
}

