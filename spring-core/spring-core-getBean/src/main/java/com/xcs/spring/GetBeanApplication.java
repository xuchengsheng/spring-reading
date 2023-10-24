package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.MyServiceA;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年09月16日 16时09分
 **/
public class GetBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("myServiceA = " + context.getBean("myServiceA"));
        System.out.println("myServiceB = " + context.getBean("myServiceB"));
    }
}
