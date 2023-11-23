package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年11月23日 18时06分
 **/
public class AnnotationConfigApplicationContextDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册Bean
        context.register(MyBean.class);
        // 扫描包
        context.scan("com.xcs.spring");
        // 打印Bean定义
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
