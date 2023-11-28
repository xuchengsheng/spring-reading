package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年11月23日 20时22分
 **/
public class HierarchicalBeanFactoryDemo {

    public static void main(String[] args) {
        // 创建父级容器
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(MyBean.class);
        // 创建子级容器
        AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setParent(parentContext);

        // 在子级 BeanFactory 中获取 bean
        HierarchicalBeanFactory childHierarchicalBeanFactory = childContext.getBeanFactory();
        System.out.println("在子级BeanFactory中获取Bean: " + childHierarchicalBeanFactory.getBean(MyBean.class));

        // 在父级 BeanFactory 中获取 bean
        HierarchicalBeanFactory parentHierarchicalBeanFactory = parentContext.getBeanFactory();
        System.out.println("在父级BeanFactory中获取Bean: " + parentHierarchicalBeanFactory.getBean(MyBean.class));

        // 获取父级 BeanFactory
        BeanFactory parentBeanFactory = childHierarchicalBeanFactory.getParentBeanFactory();
        System.out.println("获取父级BeanFactory: " + parentBeanFactory);

        // 判断本地 BeanFactory 是否包含指定名称的 bean
        boolean containsLocalBean = childHierarchicalBeanFactory.containsLocalBean("myBean");
        System.out.println("判断本地BeanFactory是否包含指定名称的Bean: " + containsLocalBean);

        // 判断整个 BeanFactory 是否包含指定名称的 bean
        boolean containsBean = childHierarchicalBeanFactory.containsBean("myBean");
        System.out.println("判断整个BeanFactory是否包含指定名称的Bean: " + containsBean);
    }
}
