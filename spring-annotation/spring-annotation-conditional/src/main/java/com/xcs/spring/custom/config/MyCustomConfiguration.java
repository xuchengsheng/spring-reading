package com.xcs.spring.custom.config;

import com.xcs.spring.custom.annotation.ConditionalOnCustomActive;
import com.xcs.spring.custom.entity.User5;
import com.xcs.spring.custom.entity.User6;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnCustomActive
public class MyCustomConfiguration {

    @Bean
    public User5 user5() {
        return new User5();
    }

    @Bean
    public User6 user6() {
        return new User6();
    }
}
