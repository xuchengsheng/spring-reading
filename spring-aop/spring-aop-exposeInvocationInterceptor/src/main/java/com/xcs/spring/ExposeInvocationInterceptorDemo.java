package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ExposeInvocationInterceptorDemo {

    public static void main(String[] args) {
        // 创建一个基于注解的应用程序上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从上下文中获取 MyService
        MyService myService = context.getBean(MyService.class);
        // 调用方法
        myService.foo();
    }
}
