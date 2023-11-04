package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * @author xcs
 * @date 2023年11月04日 11时28分
 **/
public class BeanDefinitionRegistryDemo {

    public static void main(String[] args) {
        // 创建一个BeanFactory，它是BeanDefinitionRegistry
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建一个Bean定义
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MyBean.class);

        // 注册Bean定义到BeanFactory
        beanFactory.registerBeanDefinition("myBean", beanDefinition);

        // 获取BeanDefinition
        BeanDefinition retrievedBeanDefinition = beanFactory.getBeanDefinition("myBean");
        System.out.println("Bean定义的类名 = " + retrievedBeanDefinition.getBeanClassName());

        // 检查Bean定义是否存在
        boolean containsBeanDefinition = beanFactory.containsBeanDefinition("myBean");
        System.out.println("Bean定义是否包含(myBean) = " + containsBeanDefinition);

        // 获取所有Bean定义的名称
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        System.out.println("Bean定义的名称 = " + String.join(", ", beanDefinitionNames));

        // 获取Bean定义的数量
        int beanDefinitionCount = beanFactory.getBeanDefinitionCount();
        System.out.println("Bean定义的数量 = " + beanDefinitionCount);

        // 检查Bean名称是否已被使用
        boolean isBeanNameInUse = beanFactory.isBeanNameInUse("myBean");
        System.out.println("Bean名称(myBean)是否被使用 = " + isBeanNameInUse);

        // 移除Bean定义
        beanFactory.removeBeanDefinition("myBean");
        System.out.println("Bean定义已被移除(myBean)");
    }
}
