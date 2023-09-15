package com.xcs.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author xcs
 * @date 2023年08月07日 16时25分
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public MySimpleBean mySimpleBean(){
        return new MySimpleBean();
    }

    @Bean
    public MyBeanFactoryPostProcessor myBeanFactoryPostProcessor(){
        return new MyBeanFactoryPostProcessor();
    }
}