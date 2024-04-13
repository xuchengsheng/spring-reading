package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AspectDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MyService service = context.getBean(MyService.class);
        System.out.println("service.getClass() = " + service.getClass());
        service.doSomething();
    }
}
