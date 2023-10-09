package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class DependsOnApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
