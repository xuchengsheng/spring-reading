package com.xcs.spring.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MyService {

    public MyService(){
        System.out.println("执行MyService构造函数");
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("执行@PostConstruct方法");
    }
}
