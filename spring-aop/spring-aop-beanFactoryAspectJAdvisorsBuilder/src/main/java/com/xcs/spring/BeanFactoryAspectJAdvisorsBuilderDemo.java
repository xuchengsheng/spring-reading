package com.xcs.spring;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder;
import org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.List;

public class BeanFactoryAspectJAdvisorsBuilderDemo {

    public static void main(String[] args) {
        // 创建一个默认的 Bean 工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 在 Bean 工厂中注册一个名为 "myAspect" 的单例 Bean，类型为 MyAspect
        beanFactory.registerSingleton("myAspect", new MyAspect());

        // 创建 BeanFactoryAspectJAdvisorsBuilder 实例，并传入 Bean 工厂和 ReflectiveAspectJAdvisorFactory 实例
        BeanFactoryAspectJAdvisorsBuilder builder = new BeanFactoryAspectJAdvisorsBuilder(beanFactory, new ReflectiveAspectJAdvisorFactory(beanFactory));
        // 构建 AspectJ Advisors
        List<Advisor> advisors = builder.buildAspectJAdvisors();
        // 打印 Advisors
        advisors.forEach(System.out::println);
    }
}
