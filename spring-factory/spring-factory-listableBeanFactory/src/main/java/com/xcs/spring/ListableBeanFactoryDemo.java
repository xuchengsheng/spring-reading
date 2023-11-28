package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xcs
 * @date 2023年11月23日 20时01分
 **/
public class ListableBeanFactoryDemo {

    public static void main(String[] args) {
        // 创建 ListableBeanFactory
        ListableBeanFactory beanFactory = new AnnotationConfigApplicationContext(MyConfiguration.class).getBeanFactory();

        // 判断是否包含指定名称的 bean 定义
        boolean containsBeanDefinition = beanFactory.containsBeanDefinition("myService");
        System.out.println("判断是否包含指定名称的Bean定义: " + containsBeanDefinition);

        // 获取工厂中所有 bean 定义的数量
        int beanDefinitionCount = beanFactory.getBeanDefinitionCount();
        System.out.println("获取工厂中所有Bean定义数量: " + beanDefinitionCount);

        // 获取工厂中所有 bean 定义的名称数组
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        System.out.println("获取工厂中所有Bean定义名称: " + String.join(", ", beanDefinitionNames));

        // 获取 ObjectProvider，并懒加载获取 bean 实例
        ObjectProvider<MyService> objectProvider = beanFactory.getBeanProvider(MyService.class, true);
        System.out.println("获取Bean的ObjectProvider: " + objectProvider.getObject());

        // 根据类型获取所有 bean 的名称
        String[] beanNamesForType = beanFactory.getBeanNamesForType(ResolvableType.forClass(MyService.class));
        System.out.println("根据类型获取Bean名称: " + String.join(", ", beanNamesForType));

        // 根据注解类型获取所有 bean 的名称
        String[] beanNamesForAnnotation = beanFactory.getBeanNamesForAnnotation(Service.class);
        System.out.println("根据注解获取Bean名称: " + String.join(", ", beanNamesForAnnotation));

        // 根据注解类型获取所有 bean 实例
        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(Service.class);
        System.out.println("根据注解类型获取所有Bean实例: " + beansWithAnnotation);

        // 在指定 bean 上查找指定类型的注解
        Service annotation = beanFactory.findAnnotationOnBean("myService", Service.class);
        System.out.println("指定Bean上查找指定类型的注解: " + annotation);
    }
}
