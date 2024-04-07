package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

public class AdviceDemo {

    public static void main(String[] args) {
//        methodInterceptor();
//        beforeAdvice();
//        afterAdvice();
//        throwsAdvice();
        introductionAdvice();
    }

    private static void methodInterceptor() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyMethodInterceptor());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomething();
    }

    private static void beforeAdvice() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyBeforeAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomething();
    }

    private static void afterAdvice() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyAfterReturningAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomething();
    }

    private static void throwsAdvice() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyThrowsAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomethingException();
    }

    private static void introductionAdvice() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 强制私用CGLIB
        proxyFactory.setProxyTargetClass(true);
        // 创建通知
        proxyFactory.addAdvisor(new DefaultIntroductionAdvisor(new MyMonitoringIntroductionAdvice(), MyMonitoringCapable.class));
        // 创建代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomething();
        // 开始监控
        ((MyMonitoringCapable) proxy).toggleMonitoring();
        // 再次调用代理对象的方法
        proxy.doSomething();
    }
}
