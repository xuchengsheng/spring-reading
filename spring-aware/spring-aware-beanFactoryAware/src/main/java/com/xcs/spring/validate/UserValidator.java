package com.xcs.spring.validate;

public interface UserValidator {
    boolean validate(String username, String password);
}
