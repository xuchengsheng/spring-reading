package com.xcs.spring.bean;

import com.xcs.spring.bean.config.MyBeanConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class ConditionBeanApplication {

    public static void main(String[] args) {
        System.setProperty("enable.bean","false");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyBeanConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
