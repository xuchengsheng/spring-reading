package com.xcs.spring.config;

import com.xcs.spring.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023年08月28日 10时48分
 **/
@Configuration
public class UserConfig {

    @Bean
    public User user(){
        return new User("xcs");
    }
}
