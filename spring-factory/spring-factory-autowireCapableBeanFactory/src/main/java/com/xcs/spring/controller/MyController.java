package com.xcs.spring.controller;

import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xcs
 * @date 2023年11月24日 14时50分
 **/
public class MyController implements BeanNameAware {

    @Autowired
    private MyService myService;

    @Override
    public String toString() {
        return "MyController{" +
                "myService=" + myService +
                '}';
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("MyController.setBeanName: " + name);
    }
}
