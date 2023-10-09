package com.xcs.spring.validate;

import org.springframework.stereotype.Component;

@Component("complexUserValidator")
public class ComplexUserValidator implements UserValidator {
    @Override
    public boolean validate(String username, String password) {
        System.out.println("使用ComplexUserValidator");
        return username != null && username.length() > 5 && password != null && password.length() > 8;
    }
}
