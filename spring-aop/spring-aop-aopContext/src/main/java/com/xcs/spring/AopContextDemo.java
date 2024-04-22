package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

public class AopContextDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 暴露代理对象
        proxyFactory.setExposeProxy(true);
        // 创建通知器
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(MyAnnotation.class), new MyMethodBeforeAdvice()));
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.foo();
    }
}
