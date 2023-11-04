package com.xcs.spring.bean;

/**
 * @author xcs
 * @date 2023年11月04日 15时51分
 **/
public class MyBean {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "message='" + message + '\'' +
                '}';
    }
}
