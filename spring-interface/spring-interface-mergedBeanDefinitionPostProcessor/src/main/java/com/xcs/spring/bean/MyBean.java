package com.xcs.spring.bean;

import com.xcs.spring.annotation.MyValue;

/**
 * @author 林雷
 * @date 2023年09月20日 10时52分
 **/
public class MyBean {

    @MyValue("hello world")
    private String message;

    public String getMessage() {
        return message;
    }
}
