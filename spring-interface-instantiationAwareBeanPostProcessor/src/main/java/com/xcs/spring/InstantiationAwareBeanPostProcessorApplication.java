package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.DataBase;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年09月16日 16时09分
 **/
public class InstantiationAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        DataBase userService = context.getBean(DataBase.class);
        System.out.println("username = " + userService.getUsername());
        System.out.println("password = " + userService.getPassword());
        System.out.println("postInstantiationFlag = " + userService.isPostInstantiationFlag());
    }
}
