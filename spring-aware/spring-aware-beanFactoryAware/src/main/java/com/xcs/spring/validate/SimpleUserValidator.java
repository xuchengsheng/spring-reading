package com.xcs.spring.validate;

import org.springframework.stereotype.Component;

@Component("simpleUserValidator")
public class SimpleUserValidator implements UserValidator {
    @Override
    public boolean validate(String username, String password) {
        System.out.println("使用SimpleUserValidator");
        return username != null && password != null;
    }
}
