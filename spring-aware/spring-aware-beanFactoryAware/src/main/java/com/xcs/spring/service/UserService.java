package com.xcs.spring.service;

import com.xcs.spring.validate.UserValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class UserService implements BeanFactoryAware, InitializingBean {

    private BeanFactory beanFactory;
    private UserValidator userValidator;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (someConfigurationMethod()) {
            userValidator = beanFactory.getBean("simpleUserValidator", UserValidator.class);
        } else {
            userValidator = beanFactory.getBean("complexUserValidator", UserValidator.class);
        }
    }

    public void validateUser(String username, String password) {
        boolean success = userValidator.validate(username, password);
        if (success){
            System.out.println("验证账号密码成功");
        }else{
            System.out.println("验证账号密码失败");
        }
    }

    private boolean someConfigurationMethod() {
        return true;
    }
}
