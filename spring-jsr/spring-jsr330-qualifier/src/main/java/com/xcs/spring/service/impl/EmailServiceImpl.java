package com.xcs.spring.service.impl;

import com.xcs.spring.annotation.Email;
import com.xcs.spring.service.MessageService;

import javax.inject.Named;

/**
 * @author xcs
 * @date 2023年10月20日 14时57分
 **/
@Email
@Named
public class EmailServiceImpl implements MessageService {
    @Override
    public String getMessage() {
        return "Email message";
    }
}
