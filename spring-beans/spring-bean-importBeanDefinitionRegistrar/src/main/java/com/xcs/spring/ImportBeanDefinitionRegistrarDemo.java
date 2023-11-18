package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import com.xcs.spring.config.MyConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年11月17日 14时48分
 **/
public class ImportBeanDefinitionRegistrarDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBean bean = context.getBean(MyBean.class);
        System.out.println("bean = " + bean);
    }
}
