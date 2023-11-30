package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.metrics.ApplicationStartup;

import java.security.AccessControlContext;

/**
 * @author xcs
 * @date 2023年11月24日 13时56分
 **/
public class ConfigurableBeanFactoryDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext
        ConfigurableBeanFactory configurableBeanFactory = new AnnotationConfigApplicationContext(MyConfiguration.class).getBeanFactory();

        // 设置父级 BeanFactory
        configurableBeanFactory.setParentBeanFactory(new DefaultListableBeanFactory());

        // 获取BeanPostProcessor数量
        int beanPostProcessorCount = configurableBeanFactory.getBeanPostProcessorCount();
        System.out.println("获取BeanPostProcessor数量: " + beanPostProcessorCount);

        // 获取所有已注册的 Scope 名称
        String[] scopeNames = configurableBeanFactory.getRegisteredScopeNames();
        System.out.println("获取所有已注册的Scope名称: " + String.join(", ", scopeNames));

        // 获取注册的 Scope
        Scope customScope = configurableBeanFactory.getRegisteredScope("customScope");
        System.out.println("获取注册的Scope :" + customScope);

        // 获取ApplicationStartup
        ApplicationStartup applicationStartup = configurableBeanFactory.getApplicationStartup();
        System.out.println("获取ApplicationStartup: " + applicationStartup);

        // 获取AccessControlContext
        AccessControlContext accessControlContext = configurableBeanFactory.getAccessControlContext();
        System.out.println("获取AccessControlContext: " + accessControlContext);

        // 拷贝配置
        ConfigurableListableBeanFactory otherFactory = new DefaultListableBeanFactory();
        configurableBeanFactory.copyConfigurationFrom(otherFactory);
        System.out.println("拷贝配置copyConfigurationFrom: " + otherFactory);

        // 注册别名
        String beanName = "myService";
        String alias = "helloService";
        configurableBeanFactory.registerAlias(beanName, alias);
        System.out.println("注册别名registerAlias, BeanName: " + beanName + "alias: " + alias);

        // 获取合并后的 BeanDefinition
        BeanDefinition mergedBeanDefinition = configurableBeanFactory.getMergedBeanDefinition("myService");
        System.out.println("获取合并后的 BeanDefinition: " + mergedBeanDefinition);

        // 判断是否为 FactoryBean
        String factoryBeanName = "myService";
        boolean isFactoryBean = configurableBeanFactory.isFactoryBean(factoryBeanName);
        System.out.println("判断是否为FactoryBean" + isFactoryBean);

        // 设置当前 Bean 是否正在创建
        String currentBeanName = "myService";
        boolean inCreation = true;
        configurableBeanFactory.setCurrentlyInCreation(currentBeanName, inCreation);
        System.out.println("设置当前Bean是否正在创建: " + currentBeanName);

        // 判断指定的 Bean 是否正在创建
        boolean isCurrentlyInCreation = configurableBeanFactory.isCurrentlyInCreation(currentBeanName);
        System.out.println("判断指定的Bean是否正在创建" + isCurrentlyInCreation);

        // 注册依赖关系
        String dependentBeanName = "dependentBean";
        configurableBeanFactory.registerDependentBean(beanName, dependentBeanName);
        System.out.println("注册依赖关系" + "beanName: " + beanName + "dependentBeanName: " + dependentBeanName);

        // 获取所有依赖于指定 Bean 的 Bean 名称
        String[] dependentBeans = configurableBeanFactory.getDependentBeans(beanName);
        System.out.println("获取所有依赖于指定Bean的Bean名称: " + String.join(", ", dependentBeans));

        // 获取指定 Bean 依赖的所有 Bean 名称
        String[] dependencies = configurableBeanFactory.getDependenciesForBean(beanName);
        System.out.println("获取指定Bean依赖的所有Bean名称: " + String.join(", ", dependencies));

        // 销毁指定 Bean 实例
        Object beanInstance = configurableBeanFactory.getBean(beanName);
        configurableBeanFactory.destroyBean(beanName, beanInstance);
        System.out.println("销毁指定 Bean 实例: " + beanName);

        // 销毁所有单例 Bean
        configurableBeanFactory.destroySingletons();
        System.out.println("销毁所有单例Bean destroySingletons" );
    }

}
