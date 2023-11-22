package com.xcs.spring.bean;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author xcs
 * @date 2023年11月22日 14时28分
 **/
public class MyBean implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        System.out.println("MyBean被销毁了");
    }
}
