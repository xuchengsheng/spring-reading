package com.xcs.spring;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * @author xcs
 * @date 2023年11月06日 10时09分
 **/
public class PropertiesBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(beanFactory);

        // 从properties文件加载bean定义
        reader.loadBeanDefinitions(new ClassPathResource("bean-definitions.properties"));

        // 获取bean
        System.out.println("myBean = " + beanFactory.getBean("myBean"));
        System.out.println("myBean = " + beanFactory.getBean("myBean"));
    }
}
