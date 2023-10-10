package com.xcs.spring.config;

import com.xcs.spring.bean.MyBean;
import com.xcs.spring.service.MyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class MyConfiguration {

    @Bean
    @Lazy
    public MyBean myBean(){
        System.out.println("MyBean 初始化了!");
        return new MyBean();
    }

    @Bean
    public MyService myService(){
        return new MyService();
    }
}
