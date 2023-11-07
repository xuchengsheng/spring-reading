package com.xcs.spring.bean;

/**
 * @author xcs
 * @date 2023年11月06日 10时11分
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
                ", hashCode='0x" + Integer.toHexString(System.identityHashCode(this)).toUpperCase() + '\'' +
                '}';
    }
}
