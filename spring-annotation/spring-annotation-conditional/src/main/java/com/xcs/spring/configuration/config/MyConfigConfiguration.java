package com.xcs.spring.configuration.config;

import com.xcs.spring.configuration.condition.ConfigPropertyCondition;
import com.xcs.spring.configuration.entity.User3;
import com.xcs.spring.configuration.entity.User4;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(ConfigPropertyCondition.class)
public class MyConfigConfiguration {

    @Bean
    public User3 user3() {
        return new User3();
    }

    @Bean
    public User4 user4() {
        return new User4();
    }
}
