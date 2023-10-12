package com.xcs.spring.configuration;

import com.xcs.spring.configuration.config.MyConfigConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class ConditionConfigurationApplication {

    public static void main(String[] args) {
        System.setProperty("enable.config","false");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfigConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
