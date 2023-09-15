package com.xcs.spring;

import com.xcs.spring.config.MyConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class ImportApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
