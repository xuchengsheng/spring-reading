package com.xcs.spring.controller;

import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MyController {

    @Autowired
    private MyService myService;

    public void showService(){
        System.out.println("myService = " + myService);
    }
}
