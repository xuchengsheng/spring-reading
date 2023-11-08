package com.xcs.spring;

import com.xcs.spring.controller.MyController;
import com.xcs.spring.repository.MyRepository;
import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * @author xcs
 * @date 2023年11月07日 17时45分
 **/
public class ClassPathBeanDefinitionScannerDemo {

    public static void main(String[] args) {
        // 创建一个 AnnotationConfigApplicationContext
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // 创建 ClassPathBeanDefinitionScanner 并将其关联到容器
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(factory);

        // 使用 ClassPathBeanDefinitionScanner的scan方法扫描Bean对象
        scanner.scan("com.xcs.spring");

        System.out.println("MyController = " + factory.getBean(MyController.class));
        System.out.println("MyService = " + factory.getBean(MyService.class));
        System.out.println("MyRepository = " + factory.getBean(MyRepository.class));
    }
}
