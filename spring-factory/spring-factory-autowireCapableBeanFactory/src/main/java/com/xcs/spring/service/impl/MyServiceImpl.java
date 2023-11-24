package com.xcs.spring.service.impl;

import com.xcs.spring.service.MyService;

/**
 * @author xcs
 * @date 2023年11月24日 14时17分
 **/
public class MyServiceImpl implements MyService {
    @Override
    public void greet() {
        System.out.println("Hello from MyService!");
    }
}
