package com.xcs.spring.service;

import com.xcs.spring.bean.MyBeanD;
import org.springframework.context.annotation.Bean;

/**
 * @author xcs
 * @date 2023年08月28日 11时00分
 **/
public class MyService {

    @Bean
    public MyBeanD myBeanD(){
        return new MyBeanD();
    }

    public String getInfo(){
        return "MyService info";
    }
}
