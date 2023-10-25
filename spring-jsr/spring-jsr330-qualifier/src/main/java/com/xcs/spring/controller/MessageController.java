package com.xcs.spring.controller;

import com.xcs.spring.annotation.Email;
import com.xcs.spring.annotation.SMS;
import com.xcs.spring.service.MessageService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author xcs
 * @date 2023年10月20日 14时59分
 **/
@Controller
public class MessageController {

    @Inject
    @Email
    private MessageService emailService;

    @Inject
    @SMS
    private MessageService smsService;

    public void showMessage() {
        System.out.println("EmailService: " + emailService.getMessage());
        System.out.println("SMSService: " + smsService.getMessage());
    }
}
