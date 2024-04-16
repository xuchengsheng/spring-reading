package com.xcs.spring;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.List;

public class BeanFactoryAdvisorRetrievalHelperDemo {

    public static void main(String[] args) {
        // 创建一个默认的 Bean 工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 向 Bean 工厂注册一个名为 "myAspect" 的 Advisor
        beanFactory.registerSingleton("myAspect", new MyAdvisor());

        // 创建 BeanFactoryAdvisorRetrievalHelper 实例，并传入 Bean 工厂
        BeanFactoryAdvisorRetrievalHelper helper = new BeanFactoryAdvisorRetrievalHelper(beanFactory);
        // 获取 Bean 工厂中的 Advisor 列表
        List<Advisor> advisors = helper.findAdvisorBeans();
        // 打印 Advisors
        advisors.forEach(System.out::println);
    }
}
