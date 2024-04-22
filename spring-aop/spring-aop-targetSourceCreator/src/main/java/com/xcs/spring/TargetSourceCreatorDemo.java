package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TargetSourceCreatorDemo {
    public static void main(String[] args) {
        // 创建一个基于注解的应用程序上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从上下文中获取 MyConnection bean
        MyConnection myConnection = context.getBean(MyConnection.class);
        // 打印 MyConnection 实例的类名
        System.out.println("MyConnection Class = " + myConnection.getClass());
        // 循环调用 MyConnection 实例的 getName() 方法
        for (int i = 0; i < 10; i++) {
            // 打印 MyConnection 实例的名称
            System.out.println("MyConnection Name = " + myConnection.getName());
        }
    }
}
