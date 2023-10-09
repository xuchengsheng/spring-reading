package com.xcs.spring;

import com.xcs.spring.config.MyEnvironmentAware;
import com.xcs.spring.config.MyConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年09月16日 16时09分
 **/
public class EnvironmentAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEnvironmentAware environmentAware = context.getBean(MyEnvironmentAware.class);
        System.out.println("AppProperty = " + environmentAware.getAppProperty());
    }
}
