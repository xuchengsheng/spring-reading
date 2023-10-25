package com.xcs.spring.bean;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author xcs
 * @date 2023年10月09日 16时45分
 **/
public class BeanA implements DisposableBean {

    public BeanA() {
        System.out.println("BeanA Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanA Destroyed");
    }
}
