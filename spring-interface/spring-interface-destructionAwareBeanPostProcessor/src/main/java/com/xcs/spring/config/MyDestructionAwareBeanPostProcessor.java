package com.xcs.spring.config;

import com.xcs.spring.service.ConnectionServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

/**
 * @author xcs
 * @date 2023年09月18日 16时13分
 **/
public class MyDestructionAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).openConnection();
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).closeConnection();
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return (bean instanceof ConnectionServiceImpl);
    }
}
