package com.xcs.spring;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.BeanFactoryAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.List;

public class AspectJAdvisorFactoryDemo {

    public static void main(String[] args) {
        // 创建一个默认的 Bean 工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 在 Bean 工厂中注册一个名为 "myAspect" 的单例 Bean，类型为 MyAspect
        beanFactory.registerSingleton("myAspect", new MyAspect());

        // 创建一个 Aspect 实例工厂，用于实例化切面
        MetadataAwareAspectInstanceFactory factory = new BeanFactoryAspectInstanceFactory(beanFactory, "myAspect");
        // 创建 ReflectiveAspectJAdvisorFactory 实例，用于创建 Advisor
        ReflectiveAspectJAdvisorFactory aspectJAdvisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        // 获取所有注解式 AspectJ 方法的 Advisors
        List<Advisor> advisors = aspectJAdvisorFactory.getAdvisors(factory);
        // 打印 Advisors
        advisors.forEach(System.out::println);
    }
}
