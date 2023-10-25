package com.xcs.spring.bean;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author xcs
 * @date 2023年10月09日 16时46分
 **/
public class BeanB implements DisposableBean {

    public BeanB() {
        System.out.println("BeanB Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanB Destroyed");
    }
}
