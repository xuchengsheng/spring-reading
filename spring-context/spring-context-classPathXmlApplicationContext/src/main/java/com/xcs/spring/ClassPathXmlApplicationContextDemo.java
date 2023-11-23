package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author xcs
 * @date 2023年11月23日 16时27分
 **/
public class ClassPathXmlApplicationContextDemo {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans.xml");
        System.out.println("MyBean = " + context.getBean(MyBean.class));
    }
}
