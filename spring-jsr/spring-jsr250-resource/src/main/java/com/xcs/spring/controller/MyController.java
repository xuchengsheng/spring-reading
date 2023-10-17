package com.xcs.spring.controller;

import com.xcs.spring.service.MyService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.inject.Inject;

@Controller
public class MyController {

    @Resource(name="myService")
    private MyService myService;

    public void showService(){
        System.out.println("myService = " + myService);
    }
}
