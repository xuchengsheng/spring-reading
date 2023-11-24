package com.xcs.spring.config;

import com.xcs.spring.service.MyService;
import com.xcs.spring.service.impl.MyServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023年11月24日 14时17分
 **/
@Configuration
public class MyConfiguration {
    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
