package com.xcs.spring.config;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023年09月19日 16时35分
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public static MergedBeanDefinitionPostProcessor myBeanPostProcessor() {
        return new MyMergedBeanDefinitionPostProcessor();
    }

    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
}
