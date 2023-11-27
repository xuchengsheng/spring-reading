package com.xcs.spring;

import com.xcs.spring.config.MyBeanPostProcessor;
import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.repository.MyRepository;
import com.xcs.spring.service.MyService;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年11月24日 15时02分
 **/
public class AutowireCapableBeanFactoryDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

        // 配置一个后置处理器，用于验证Bean的初始化前后拦截信息打印
        applicationContext.getBeanFactory().addBeanPostProcessor(new MyBeanPostProcessor());
        // 注册一个MyRepository的Bean对象
        applicationContext.getBeanFactory().registerSingleton("myRepository", new MyRepository());

        // 获取 AutowireCapableBeanFactory
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        // 创建指定Bean名称的实例
        // createBean(beanFactory);

        // 对给定的Bean实例进行进一步的配置
        // configureBean(beanFactory);

        // 对给定的Bean实例进行自动装配
        // autowireBean(beanFactory);

        // 使用指定的自动装配模式创建Bean实例
        // autowire(beanFactory);

        // 对给定的Bean实例的属性进行自动装配
        // autowireBeanProperties(beanFactory);

        // 将属性值应用到给定的Bean实例
        // applyBeanPropertyValues(beanFactory);

        // 初始化给定的Bean实例
        // initializeBean(beanFactory);

        // 销毁给定的Bean实例
        // destroyBean(beanFactory);

        // 解析Bean之间的依赖关系
        // resolveDependency(beanFactory);
    }

    private static void createBean(AutowireCapableBeanFactory beanFactory) {
        MyService myService = beanFactory.createBean(MyService.class);
        System.out.println("调用createBean方法,创建Bean对象 = " + myService);
    }

    private static void configureBean(AutowireCapableBeanFactory beanFactory) {
        // 配置一个RootBeanDefinition
        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("myService", new RootBeanDefinition(MyService.class));

        MyService myService = new MyService();
        System.out.println("调用configureBean前,MyService = " + myService);
        beanFactory.configureBean(myService, "myService");
        System.out.println("调用configureBean后,MyService = " + myService);
    }

    private static void autowireBean(AutowireCapableBeanFactory beanFactory) {
        MyService myService = new MyService();
        System.out.println("调用autowireBean前,MyService = " + myService);
        beanFactory.autowireBean(myService);
        System.out.println("调用autowireBean后,MyService = " + myService);
    }

    private static void autowire(AutowireCapableBeanFactory beanFactory) {
        Object myService = beanFactory.autowire(MyService.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        System.out.println("调用autowire方法,创建Bean对象 =" + myService);
    }

    private static void autowireBeanProperties(AutowireCapableBeanFactory beanFactory) {
        MyService myService = new MyService();
        System.out.println("调用autowireBeanProperties前,MyService = " + myService);
        beanFactory.autowireBeanProperties(myService, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        System.out.println("调用autowireBeanProperties后,MyService = " + myService);
    }

    private static void applyBeanPropertyValues(AutowireCapableBeanFactory beanFactory) {
        PropertyValue propertyValue = new PropertyValue("javaHome", "这里是我自定义的javaHome路径配置");
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.addPropertyValue(propertyValue);

        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(MyService.class);
        rootBeanDefinition.setPropertyValues(propertyValues);
        // 配置一个RootBeanDefinition
        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("myService", rootBeanDefinition);

        MyService myService = new MyService();
        System.out.println("调用applyBeanPropertyValues前,MyService = " + myService);
        beanFactory.applyBeanPropertyValues(myService, "myService");
        System.out.println("调用applyBeanPropertyValues后,MyService = " + myService);
    }

    private static void initializeBean(AutowireCapableBeanFactory beanFactory) {
        MyService myService = new MyService();
        System.out.println("调用initializeBean前,MyService = " + myService);
        beanFactory.initializeBean(myService, "myService");
        System.out.println("调用initializeBean前,MyService = " + myService);
    }

    private static void destroyBean(AutowireCapableBeanFactory beanFactory) {
        beanFactory.destroyBean(new MyService());
    }

    private static void resolveDependency(AutowireCapableBeanFactory beanFactory) {
        try {
            DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(MyService.class.getDeclaredField("myRepository"), false);
            Object resolveDependency = beanFactory.resolveDependency(dependencyDescriptor, "myRepository");
            System.out.println("resolveDependency = " + resolveDependency);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
