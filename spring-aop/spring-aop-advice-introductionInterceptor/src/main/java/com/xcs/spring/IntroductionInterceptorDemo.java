package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

public class IntroductionInterceptorDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 强制私用CGLIB
        proxyFactory.setProxyTargetClass(true);
        // 创建通知
        proxyFactory.addAdvisor(new DefaultIntroductionAdvisor(new MyMonitoringIntroductionAdvice(), MyMonitoringCapable.class));
        // 创建代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.foo();
        // 开始监控
        ((MyMonitoringCapable) proxy).toggleMonitoring();
        // 再次调用代理对象的方法
        proxy.foo();
    }
}
