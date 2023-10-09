package com.xcs.spring.service;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class MyBasePayService implements BeanNameAware, InitializingBean, DisposableBean {

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Module " + beanName + " has been registered.");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Module " + beanName + " has been unregistered.");
    }
}
