package com.xcs.spring.service;

/**
 * @author 林雷
 * @date 2023年09月18日 17时29分
 **/
public interface ConnectionService {

    void openConnection();

    void closeConnection();

    boolean isConnected();
}
