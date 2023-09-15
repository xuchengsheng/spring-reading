package com.xcs.spring.bean;

/**
 * @author 林雷
 * @date 2023年08月25日 10时22分
 **/
public class MyBean {

    private String name;

    public MyBean(String name) {
        this.name = name;
    }

    public void init(){
        System.out.println("MyBean.init");
    }

    public void destroy(){
        System.out.println("MyBean.destroy");
    }
}
