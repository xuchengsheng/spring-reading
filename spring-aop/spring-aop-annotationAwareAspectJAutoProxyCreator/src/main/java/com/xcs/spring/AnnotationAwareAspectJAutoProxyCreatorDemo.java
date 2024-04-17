package com.xcs.spring;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationAwareAspectJAutoProxyCreatorDemo {

    public static void main(String[] args) {
        // 创建一个默认的 Bean 工厂
        AnnotationConfigApplicationContext beanFactory = new AnnotationConfigApplicationContext();
        // 注册AnnotationAwareAspectJAutoProxyCreator作为Bean，用于自动创建切面代理
        beanFactory.registerBeanDefinition("internalAutoProxyCreator", new RootBeanDefinition(AnnotationAwareAspectJAutoProxyCreator.class));
        // 注册应用程序配置类
        beanFactory.register(AppConfig.class);
        // 刷新应用程序上下文
        beanFactory.refresh();

        // 从容器中获取MyService bean
        MyService myService = beanFactory.getBean(MyService.class);
        // 调用MyService的方法
        myService.doSomething();
    }
}
