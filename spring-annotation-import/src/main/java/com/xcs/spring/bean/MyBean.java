package com.xcs.spring.bean;

/**
 * @author xcs
 * @date 2023年09月11日 11时13分
 **/
public class MyBean {

    private String describe;

    public MyBean(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "describe='" + describe + '\'' +
                '}';
    }
}
