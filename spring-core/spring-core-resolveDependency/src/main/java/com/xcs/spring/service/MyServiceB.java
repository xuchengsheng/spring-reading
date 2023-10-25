package com.xcs.spring.service;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author xcs
 * @date 2023年10月25日 10时37分
 **/
public class MyServiceB {

    /**
     * 方法注入
     */
    private MyServiceA methodMyServiceA;

    /**
     * 字段注入
     */
    private MyServiceA fieldMyServiceA;

    /**
     * 字段注入 (环境变量)
     */
    @Value("${my.property.value}")
    private String myPropertyValue;

    public void setMethodMyServiceA(MyServiceA methodMyServiceA){
        this.methodMyServiceA = methodMyServiceA;
    }

    @Override
    public String toString() {
        return "MyServiceB{" +
                "myPropertyValue='" + myPropertyValue + '\'' +
                ", methodMyServiceA=" + methodMyServiceA +
                ", fieldMyServiceA=" + fieldMyServiceA +
                '}';
    }
}
