package com.xcs.spring.controller;

import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xcs
 * @date 2023年11月24日 14时50分
 **/
public class MyController {

    @Autowired
    private MyService myService;
}
