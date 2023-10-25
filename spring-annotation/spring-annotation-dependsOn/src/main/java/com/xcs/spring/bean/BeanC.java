package com.xcs.spring.bean;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author xcs
 * @date 2023年10月09日 16时46分
 **/
public class BeanC implements DisposableBean {

    public BeanC() {
        System.out.println("BeanC Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanC Destroyed");
    }
}
