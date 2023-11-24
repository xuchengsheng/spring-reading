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

        // 1. 根据名称获取 bean
        Object bean1 = beanFactory.getBean("myBean");
        System.out.println("1.通过名称获取Bean: " + bean1);

        // 2. 根据名称和类型获取 bean
        MyBean bean2 = beanFactory.getBean("myBean", MyBean.class);
        System.out.println("2.通过名称和类型获取Bean: " + bean2);

        // 3. 根据名称和参数获取 bean
        Object bean3 = beanFactory.getBean("myBean", "自定义消息");
        System.out.println("3.通过名称和参数获取Bean: " + bean3);

        // 4. 根据类型获取 bean
        MyBean bean4 = beanFactory.getBean(MyBean.class);
        System.out.println("4.通过类型获取Bean: " + bean4);

        // 5. 根据类型和参数获取 bean
        MyBean bean5 = beanFactory.getBean(MyBean.class, "自定义消息");
        System.out.println("5.通过类型和参数获取Bean: " + bean5);

        // 6. 获取 bean 的 ObjectProvider
        ObjectProvider<MyBean> objectProvider = beanFactory.getBeanProvider(MyBean.class);
        System.out.println("6.Bean的ObjectProvider: " + objectProvider.getObject());

        // 7. 获取 bean 的类型
        Class<?> beanType = beanFactory.getType("myBean");
        System.out.println("7.Bean的类型: " + beanType);

        // 8. 判断是否包含某个 bean
        boolean containsBean = beanFactory.containsBean("myBean");
        System.out.println("8.是否包含Bean: " + containsBean);

        // 9. 判断 bean 是否为单例
        boolean isSingleton = beanFactory.isSingleton("myBean");
        System.out.println("9.是否为单例: " + isSingleton);

        // 10. 判断 bean 是否为原型
        boolean isPrototype = beanFactory.isPrototype("myBean");
        System.out.println("10.是否为原型: " + isPrototype);

        // 11. 判断 bean 是否匹配指定类型
        boolean isTypeMatch = beanFactory.isTypeMatch("myBean", ResolvableType.forClass(MyBean.class));
        System.out.println("11.是否匹配指定类型: " + isTypeMatch);

        // 12. 获取 bean 的所有别名
        String[] aliases = beanFactory.getAliases("myBean");
        System.out.println("12.Bean的所有别名: " + String.join(", ", aliases));
    }
}
