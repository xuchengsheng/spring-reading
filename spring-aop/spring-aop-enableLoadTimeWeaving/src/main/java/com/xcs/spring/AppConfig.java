package com.xcs.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

@Configuration
@EnableLoadTimeWeaving
public class AppConfig {

    @Bean
    public FooService fooService(){
        return new FooService();
    }
}
