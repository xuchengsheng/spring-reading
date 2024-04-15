package com.xcs.spring;

import org.springframework.aop.aspectj.SimpleAspectInstanceFactory;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.config.SimpleBeanFactoryAwareAspectInstanceFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class AspectInstanceFactoryDemo {

    public static void main(String[] args) {
        // 使用 SimpleAspectInstanceFactory 创建切面实例
        SimpleAspectInstanceFactory sAif = new SimpleAspectInstanceFactory(MyAspect.class);
        System.out.println("SimpleAspectInstanceFactory (1): " + sAif.getAspectInstance());
        System.out.println("SimpleAspectInstanceFactory (2): " + sAif.getAspectInstance());

        // 使用 SingletonAspectInstanceFactory 创建单例切面实例
        SingletonAspectInstanceFactory singletonAif = new SingletonAspectInstanceFactory(new MyAspect());
        System.out.println("SingletonAspectInstanceFactory (1): " + singletonAif.getAspectInstance());
        System.out.println("SingletonAspectInstanceFactory (2): " + singletonAif.getAspectInstance());

        // 创建一个 DefaultListableBeanFactory 实例，用于注册和管理 bean
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 注册一个名为 "myAspect" 的单例 bean，类型为 MyAspect
        beanFactory.registerSingleton("myAspect", new MyAspect());
        // 创建一个切面工厂的 BeanDefinition
        RootBeanDefinition aspectFactoryDef = new RootBeanDefinition(SimpleBeanFactoryAwareAspectInstanceFactory.class);
        // 设置切面工厂的属性 aspectBeanName 为 "myAspect"
        aspectFactoryDef.getPropertyValues().add("aspectBeanName", "myAspect");
        // 设置切面工厂为合成的，即不对外暴露
        aspectFactoryDef.setSynthetic(true);
        // 注册名为 "simpleBeanFactoryAwareAspectInstanceFactory" 的 bean，并使用切面工厂的 BeanDefinition
        beanFactory.registerBeanDefinition("simpleBeanFactoryAwareAspectInstanceFactory", aspectFactoryDef);
        // 从 BeanFactory 中获取 SimpleBeanFactoryAwareAspectInstanceFactory 的实例
        SimpleBeanFactoryAwareAspectInstanceFactory simpleBeanFactoryAwareAif = beanFactory.getBean(SimpleBeanFactoryAwareAspectInstanceFactory.class);
        System.out.println("SimpleBeanFactoryAwareAspectInstanceFactory (1): " + simpleBeanFactoryAwareAif.getAspectInstance());
        System.out.println("SimpleBeanFactoryAwareAspectInstanceFactory (2): " + simpleBeanFactoryAwareAif.getAspectInstance());
    }
}
