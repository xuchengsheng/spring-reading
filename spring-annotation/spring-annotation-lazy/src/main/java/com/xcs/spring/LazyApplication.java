package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.MyService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class LazyApplication {

    public static void main(String[] args) {
        System.out.println("启动 Spring ApplicationContext...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("启动完成 Spring ApplicationContext...");

        System.out.println("获取MyService...");
        MyService myService = context.getBean(MyService.class);

        System.out.println("调用show方法...");
        myService.show();
    }
}
