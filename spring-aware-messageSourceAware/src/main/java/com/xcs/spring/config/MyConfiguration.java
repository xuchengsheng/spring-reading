package com.xcs.spring.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author xcs
 * @date 2023年09月19日 16时35分
 **/
@Configuration
public class MyConfiguration {

    @Bean
    public MyMessageSourceAware myMessageSourceAware(){
        return new MyMessageSourceAware();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        return messageSource;
    }
}
