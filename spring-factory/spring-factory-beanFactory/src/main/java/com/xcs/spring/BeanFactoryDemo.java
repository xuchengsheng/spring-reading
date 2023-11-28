package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ResolvableType;

/**
 * @author xcs
 * @date 2023年11月23日 19时17分
 **/
public class BeanFactoryDemo {

    public static void main(String[] args) {
        // 创建 BeanFactory
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(MyBean.class).getBeanFactory();

        // 根据名称获取 bean
        Object bean = beanFactory.getBean("myBean");
        System.out.println("通过名称获取Bean: " + bean);

        // 获取 bean 的 ObjectProvider
        ObjectProvider<MyBean> objectProvider = beanFactory.getBeanProvider(MyBean.class);
        System.out.println("获取Bean的ObjectProvider: " + objectProvider);

        // 获取 bean 的类型
        Class<?> beanType = beanFactory.getType("myBean");
        System.out.println("获取Bean的类型: " + beanType);

        // 判断是否包含某个 bean
        boolean containsBean = beanFactory.containsBean("myBean");
        System.out.println("判断是否包含Bean: " + containsBean);

        // 判断 bean 是否为单例
        boolean isSingleton = beanFactory.isSingleton("myBean");
        System.out.println("判断是否为单例: " + isSingleton);

        // 判断 bean 是否为原型
        boolean isPrototype = beanFactory.isPrototype("myBean");
        System.out.println("判断是否为原型: " + isPrototype);

        // 判断 bean 是否匹配指定类型
        boolean isTypeMatch = beanFactory.isTypeMatch("myBean", ResolvableType.forClass(MyBean.class));
        System.out.println("判断是否匹配指定类型: " + isTypeMatch);

        // 获取 bean 的所有别名
        String[] aliases = beanFactory.getAliases("myBean");
        System.out.println("获取Bean的所有别名: " + String.join(", ", aliases));
    }
}
