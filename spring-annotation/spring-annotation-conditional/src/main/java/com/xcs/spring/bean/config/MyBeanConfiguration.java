package com.xcs.spring.bean.config;

import com.xcs.spring.bean.condition.BeanPropertyCondition;
import com.xcs.spring.bean.entity.User1;
import com.xcs.spring.bean.entity.User2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBeanConfiguration {

    @Bean
    @Conditional(BeanPropertyCondition.class)
    public User1 user1() {
        return new User1();
    }

    @Bean
    public User2 user2() {
        return new User2();
    }
}
