package com.xcs.spring.controller;

import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

@Controller
public class MyController {

    @Autowired
    private Provider<MyService> myServiceProvider;

    public void showService(){
        System.out.println("myServiceProvider1 = " + myServiceProvider.get());
        System.out.println("myServiceProvider2 = " + myServiceProvider.get());
    }
}
