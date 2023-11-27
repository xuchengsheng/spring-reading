package com.xcs.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author xcs
 * @date 2023年11月27日 11时31分
 **/
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor#postProcessBeforeInitialization方法被调用了,Bean名称 = " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor#postProcessBeforeInitialization方法被调用了,Bean名称 = " + beanName);
        return bean;
    }
}
