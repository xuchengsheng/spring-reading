package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年09月16日 16时09分
 **/
public class ApplicationStartupAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setApplicationStartup(new BufferingApplicationStartup(100));
        context.register(MyConfiguration.class);
        context.refresh();
        context.close();
    }
}
