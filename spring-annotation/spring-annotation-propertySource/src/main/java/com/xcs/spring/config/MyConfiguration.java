package com.xcs.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author xcs
 * @date 2023年08月07日 16时25分
 **/
@Configuration
@PropertySource("classpath:my-application.yml")
public class MyConfiguration {

}