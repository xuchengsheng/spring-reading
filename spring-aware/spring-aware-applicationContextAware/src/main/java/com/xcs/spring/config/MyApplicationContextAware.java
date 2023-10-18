package com.xcs.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MyApplicationContextAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        System.out.println("实现ApplicationContextAware接口,自动调用setApplicationContext方法");
        System.out.println("ApplicationContext = " + context);
    }
}
