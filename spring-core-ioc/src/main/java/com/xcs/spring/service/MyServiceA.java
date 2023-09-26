package com.xcs.spring.service;

import org.springframework.stereotype.Component;

/**
 * @author xcs
 * @date 2023年09月21日 10时30分
 **/
@Component
public class MyServiceA {

    public void destroy(){
        System.out.println("MyServiceA.destroy");
    }
}
