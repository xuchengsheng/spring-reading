package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EnableLoadTimeWeavingDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MyService myService = new MyService();
        myService.foo();
        context.close();
    }
}
