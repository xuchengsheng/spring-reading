package com.xcs.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author xcs
 * @date 2023年09月14日 14时17分
 **/
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("开始新增Bean定义");
        // 创建一个新的 BeanDefinition 对象
        BeanDefinition beanDefinition = new RootBeanDefinition(MySimpleBean.class);
        // 使用 registry 来注册这个新的 bean 定义
        registry.registerBeanDefinition("mySimpleBean", beanDefinition);
        System.out.println("完成新增Bean定义");
    }
}
