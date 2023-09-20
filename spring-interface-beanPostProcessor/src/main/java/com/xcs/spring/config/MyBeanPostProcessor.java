package com.xcs.spring.config;

import com.xcs.spring.service.MyServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author xcs
 * @date 2023年09月19日 16时42分
 **/
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage("Prefix: " + myService.getMessage());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage(myService.getMessage() + " :Suffix");
        }
        return bean;
    }
}
