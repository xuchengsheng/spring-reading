package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;

public class AdvisorAdapterDemo {

    public static void main(String[] args) {
        // 注册自定义适配器
        GlobalAdvisorAdapterRegistry.getInstance().registerAdvisorAdapter(new NullReturningAdviceAdapter());
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 添加Advisor
        proxyFactory.addAdvice(new MyNullReturningAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 不会触发通知
        System.out.println("foo return value : " + proxy.foo());
        // 换行
        System.out.println("==================================");
        // 会触发通知
        System.out.println("bar return value : " + proxy.bar());
    }
}
