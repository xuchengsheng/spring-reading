package com.xcs.spring.service;

import com.xcs.spring.annotation.MyAutowired;
import org.springframework.stereotype.Component;

/**
 * @author xcs
 * @date 2023年09月21日 10时31分
 **/
@Component
public class MyService {

    private final MyServiceA myServiceA;
    private final MyServiceB myServiceB;

    public MyService() {
        System.out.println("Default constructor used");
        this.myServiceA = null;
        this.myServiceB = null;
    }

    public MyService(MyServiceA myServiceA) {
        System.out.println("Constructor with ServiceA used");
        this.myServiceA = myServiceA;
        this.myServiceB = null;
    }

    public MyService(MyServiceB serviceB) {
        System.out.println("Constructor with ServiceB used");
        this.myServiceA = null;
        this.myServiceB = serviceB;
    }

    @MyAutowired
    public MyService(MyServiceA serviceA, MyServiceB serviceB) {
        System.out.println("Constructor with ServiceA and ServiceB used");
        this.myServiceA = serviceA;
        this.myServiceB = serviceB;
    }
}
