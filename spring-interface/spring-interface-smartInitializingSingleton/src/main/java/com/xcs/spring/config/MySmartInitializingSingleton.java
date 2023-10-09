package com.xcs.spring.config;

import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xcs
 * @date 2023年09月19日 16时42分
 **/
public class MySmartInitializingSingleton implements SmartInitializingSingleton {

    @Autowired
    private MyService myService;

    @Override
    public void afterSingletonsInstantiated() {
        myService.startScheduledTask();
    }
}
