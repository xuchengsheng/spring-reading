package com.xcs.spring;

import com.xcs.spring.config.MyApplicationEventPublisherAware;
import com.xcs.spring.config.MyConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年09月16日 16时09分
 **/
public class ApplicationEventPublisherAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyApplicationEventPublisherAware publisherAware = context.getBean(MyApplicationEventPublisherAware.class);
        publisherAware.publish("hello world");
    }
}
