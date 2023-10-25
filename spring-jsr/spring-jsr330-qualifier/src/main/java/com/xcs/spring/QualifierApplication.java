package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.controller.MessageController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年10月20日 14时56分
 **/
public class QualifierApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MessageController messageController = context.getBean(MessageController.class);
        messageController.showMessage();
    }
}
