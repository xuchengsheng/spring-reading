package com.xcs.spring.config;

import com.xcs.spring.service.MyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author xcs
 * @date 2023年10月13日 14时54分
 **/
@Configuration
@PropertySource("classpath:application.properties")
public class MyConfiguration {

    @Bean
    public MyService myService(){
        return new MyService();
    }
}
