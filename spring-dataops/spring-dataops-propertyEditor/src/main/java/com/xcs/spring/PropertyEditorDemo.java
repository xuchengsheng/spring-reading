package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Date;

public class PropertyEditorDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.refresh();
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(MyBean.class);
        rootBeanDefinition.getPropertyValues().add("path", "/opt/myfile");
        rootBeanDefinition.getPropertyValues().add("date", "2023-12-5");

        context.registerBeanDefinition("myBean", rootBeanDefinition);

        context.getBeanFactory().registerCustomEditor(Date.class, MyCustomDateEditor.class);

        System.out.println("myBean= " + context.getBean("myBean"));
    }
}
