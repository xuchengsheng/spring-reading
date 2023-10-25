package com.xcs.spring.service.impl;

import com.xcs.spring.annotation.SMS;
import com.xcs.spring.service.MessageService;

import javax.inject.Named;

/**
 * @author xcs
 * @date 2023年10月20日 14时57分
 **/
@SMS
@Named
public class SMSServiceImpl implements MessageService {
    @Override
    public String getMessage() {
        return "SMS message";
    }
}
