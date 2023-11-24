package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.controller.MyController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Iterator;

/**
 * @author xcs
 * @date 2023年11月24日 14时44分
 **/
public class ConfigurableListableBeanFactoryDemo {

    public static void main(String[] args) throws NoSuchFieldException {
        // 创建 ApplicationContext
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

        // 获取 ConfigurableListableBeanFactory
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        // 忽略指定类型的依赖
        beanFactory.ignoreDependencyType(String.class);

        // 忽略指定接口的依赖
        beanFactory.ignoreDependencyInterface(BeanFactory.class);

        // 注册可解析的依赖
        beanFactory.registerResolvableDependency(ApplicationContext.class, applicationContext);

        // 判断指定的 Bean 是否可以作为自动注入的候选者
        String beanName = "myService";

        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(MyController.class.getDeclaredField("myService"), false);
        boolean isAutowireCandidate = beanFactory.isAutowireCandidate(beanName, dependencyDescriptor);
        System.out.println(beanName + " 是否为自动注入的候选者: " + isAutowireCandidate);

        // 获取指定 Bean 的 BeanDefinition
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        System.out.println(beanName + " 的 BeanDefinition: " + beanDefinition);

        // 获取所有 Bean 的名称的迭代器
        Iterator<String> beanNamesIterator = beanFactory.getBeanNamesIterator();
        System.out.print("所有 Bean 的名称: ");
        beanNamesIterator.forEachRemaining(System.out::print);

        // 清除元数据缓存
        beanFactory.clearMetadataCache();

        // 冻结配置
        beanFactory.freezeConfiguration();

        // 判断配置是否已冻结
        boolean isConfigurationFrozen = beanFactory.isConfigurationFrozen();
        System.out.println("配置是否已冻结: " + isConfigurationFrozen);

        // 预实例化所有非懒加载的单例 Bean
        beanFactory.preInstantiateSingletons();
    }
}
