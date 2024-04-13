package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EnableLoadTimeWeavingDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        FooService fooService = context.getBean(FooService.class);
        fooService.foo();
        // 换行
        System.out.println();
        FooService fooService1 = new FooService();
        fooService1.foo();
    }
}
