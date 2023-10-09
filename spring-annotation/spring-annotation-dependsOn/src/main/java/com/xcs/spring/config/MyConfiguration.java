package com.xcs.spring.config;

import com.xcs.spring.bean.MethodBeanFirst;
import com.xcs.spring.bean.MethodBeanSecond;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author xcs
 * @date 2023年08月07日 16时25分
 **/
@Configuration
@ComponentScan("com.xcs.spring.bean")
public class MyConfiguration {

    @Bean
    @DependsOn("methodBeanSecond")
    public MethodBeanFirst methodBeanFirst() {
        return new MethodBeanFirst();
    }

    @Bean
    public MethodBeanSecond methodBeanSecond() {
        return new MethodBeanSecond();
    }
}