package com.xcs.spring.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author xcs
 * @date 2023年09月16日 16时10分
 **/
public class DataBaseImpl implements DataBase , InitializingBean {

    @Value("root")
    private String username;

    @Value("123456")
    private String password;

    private boolean postInstantiationFlag;

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isPostInstantiationFlag() {
        return this.postInstantiationFlag;
    }

    @Override
    public void setPostInstantiationFlag(boolean flag) {
        this.postInstantiationFlag = flag;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("DataBaseImpl.afterPropertiesSet");
    }
}
