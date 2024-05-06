package com.xcs.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyService();
    }

    @Bean
    public MyAspect myAspect() {
        return new MyAspect();
    }
}
