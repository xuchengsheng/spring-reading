package com.xcs.spring;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;

public class MyTargetSourceCreator implements TargetSourceCreator {
    @Override
    public TargetSource getTargetSource(Class<?> beanClass, String beanName) {
        if (MyConnection.class.isAssignableFrom(beanClass)) {
            return new ConnectionPoolTargetSource(3);
        }
        return null;
    }
}
