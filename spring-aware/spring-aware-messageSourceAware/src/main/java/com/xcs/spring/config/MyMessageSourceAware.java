package com.xcs.spring.config;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import java.util.Locale;

public class MyMessageSourceAware implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void getMessage() {
        System.out.println("English："+messageSource.getMessage("greeting", null, Locale.ENGLISH));
        System.out.println("中文："+messageSource.getMessage("greeting", null, Locale.SIMPLIFIED_CHINESE));
    }
}
