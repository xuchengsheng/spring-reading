package com.xcs.spring.custom;

import com.xcs.spring.custom.config.MyCustomConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class ConditionCustomApplication {

    public static void main(String[] args) {
        System.setProperty("enable.custom","false");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyCustomConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
