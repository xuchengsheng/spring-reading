package com.xcs.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author xcs
 * @date 2023年09月19日 16时35分
 **/
@Configuration
@PropertySource("classpath:application.properties")
public class MyConfiguration {

    @Bean
    public MyEnvironmentAware myEnvironmentAware(){
        return new MyEnvironmentAware();
    }

}
