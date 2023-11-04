package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.Arrays;

/**
 * @author xcs
 * @date 2023年11月03日 17时20分
 **/
public class BeanDefinitionHolderDemo {

    public static void main(String[] args) {
        // 创建一个 DefaultListableBeanFactory，它是 BeanDefinitionRegistry 的一个实现
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建一个新的 BeanDefinition 对象
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MyBean.class);

        // Bean名称
        String beanName = "myBean";
        // 设置别名（aliases）
        String[] aliases = {"myBeanX", "myBeanY"};
        // 创建一个 BeanDefinitionHolder，将 BeanDefinition 与名称关联起来
        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName, aliases);

        // 使用 BeanDefinitionReaderUtils 注册 BeanDefinitionHolder
        BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, beanFactory);

        System.out.println("myBean = " + beanFactory.getBean("myBean"));
        System.out.println("myBeanX = " + beanFactory.getBean("myBeanX"));
        System.out.println("myBeanY = " + beanFactory.getBean("myBeanY"));
    }
}
