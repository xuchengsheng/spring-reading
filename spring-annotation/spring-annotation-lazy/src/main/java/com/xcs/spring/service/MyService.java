package com.xcs.spring.service;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class MyService  {

    @Autowired
    @Lazy
    private MyBean myBean;

    public void show() {
        System.out.println("@Lazy MyBean = " + myBean.getClass());
        myBean.show();
    }
}
