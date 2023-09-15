package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);

        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());

        System.out.println("MyConfiguration = " + configuration.getClass().getName());

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
