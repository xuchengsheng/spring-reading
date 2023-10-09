package com.xcs.spring.config;

import com.xcs.spring.service.UserServiceImpl;
import org.springframework.beans.factory.BeanClassLoaderAware;

public class MyBeanClassLoaderAware implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void loadAndExecute() {
        try {
            Class<?> clazz = classLoader.loadClass("com.xcs.spring.service.UserServiceImpl");
            UserServiceImpl instance = (UserServiceImpl) clazz.getDeclaredConstructor().newInstance();
            System.out.println("UserInfo = " + instance.getUserInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
