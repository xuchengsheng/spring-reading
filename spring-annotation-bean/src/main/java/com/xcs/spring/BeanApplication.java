package com.xcs.spring;

import com.xcs.spring.config.MyBeanConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class BeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyBeanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
        context.close();
    }
}
