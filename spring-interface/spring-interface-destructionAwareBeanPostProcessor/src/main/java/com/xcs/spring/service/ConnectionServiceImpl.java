package com.xcs.spring.service;

/**
 * @author xcs
 * @date 2023年09月18日 17时30分
 **/
public class ConnectionServiceImpl implements ConnectionService {

    private boolean isConnected = false;

    @Override
    public void openConnection() {
        isConnected = true;
        System.out.println("connection opened.");
    }

    @Override
    public void closeConnection() {
        if (isConnected) {
            isConnected = false;
            System.out.println("connection closed.");
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
}
