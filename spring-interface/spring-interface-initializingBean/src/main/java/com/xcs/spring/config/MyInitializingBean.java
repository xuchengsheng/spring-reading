package com.xcs.spring.config;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xcs
 * @date 2023年09月19日 16时42分
 **/
public class MyInitializingBean implements InitializingBean {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    @Override
    public void afterPropertiesSet() {
        // 在此方法中，我们模拟数据加载
        data = new ArrayList<>();
        data.add("数据1");
        data.add("数据2");
        data.add("数据3");
        System.out.println("MyInitializingBean 初始化完毕，数据已加载!");
    }
}
