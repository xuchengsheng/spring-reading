package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EnableAspectJAutoProxyDemo {

    public static void main(String[] args) {
        // 创建基于注解的应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从应用上下文中获取FooService bean
        FooService fooService = context.getBean(FooService.class);
        // 调用FooService的方法
        fooService.foo();
    }
}