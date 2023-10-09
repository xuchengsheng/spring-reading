package com.xcs.spring.config;

import com.xcs.spring.service.MyService;
import com.xcs.spring.service.MyServiceImpl;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023年09月19日 16时35分
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

    @Bean
    public BeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
