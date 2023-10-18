package com.xcs.spring.service;

import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class MyService {

    public MyService(){
        System.out.println("执行MyService构造函数");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println("执行@PreDestroy方法");
    }
}
