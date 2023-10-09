package com.xcs.spring.bean;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * @author 林雷
 * @date 2023年10月09日 16时45分
 **/
@DependsOn("classBeanSecond")
@Component
public class ClassBeanFirst {
    public ClassBeanFirst() {
        System.out.println("ClassBeanFirst Initialized");
    }
}
