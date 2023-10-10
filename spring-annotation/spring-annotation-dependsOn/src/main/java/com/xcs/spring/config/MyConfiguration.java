package com.xcs.spring.config;

import com.xcs.spring.bean.BeanA;
import com.xcs.spring.bean.BeanB;
import com.xcs.spring.bean.BeanC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author xcs
 * @date 2023年08月07日 16时25分
 **/
@Configuration
public class MyConfiguration {

    @Bean
    @DependsOn("beanB")
    public BeanA beanA() {
        return new BeanA();
    }

    @Bean
    @DependsOn("beanC")
    public BeanB beanB() {
        return new BeanB();
    }

    @Bean
    public BeanC beanC() {
        return new BeanC();
    }
}