package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EnableAspectJAutoProxyDemo {

    public static void main(String[] args) {
        // 创建基于注解的应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从应用上下文中获取MyService bean
        MyService myService = context.getBean(MyService.class);
        // 调用MyService的方法
        myService.foo();
    }
}