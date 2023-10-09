package com.xcs.spring.service;

/**
 * @author xcs
 * @date 2023年09月16日 16时10分
 **/
public interface DataBase {
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
    boolean isPostInstantiationFlag();
    void setPostInstantiationFlag(boolean flag);
}
