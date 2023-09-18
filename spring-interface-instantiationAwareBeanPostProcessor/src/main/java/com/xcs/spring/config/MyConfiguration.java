package com.xcs.spring.config;

import com.xcs.spring.service.DataBaseImpl;
import com.xcs.spring.service.DataBase;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023年08月07日 16时25分
 **/
@Configuration
public class MyConfiguration implements InitializingBean {

    @Bean
    public static MyInstantiationAwareBeanPostProcessor myInstantiationAwareBeanPostProcessor() {
        return new MyInstantiationAwareBeanPostProcessor();
    }

    @Bean
    public DataBase dataBase() {
        return new DataBaseImpl();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("MyConfiguration.afterPropertiesSet");
    }
}