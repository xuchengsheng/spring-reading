package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Date;

public class PropertyEditorDemo {

    public static void main(String[] args) {
        // 创建一个 BeanWrapperImpl 实例，用于操作 MyBean 类。
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(MyBean.class);

        // 为 Date 类型的属性注册自定义的属性编辑器 MyCustomDateEditor。
        beanWrapper.overrideDefaultEditor(Date.class, new MyCustomDateEditor());

        // 设置 MyBean 类中名为 "date" 的属性值，使用字符串 "2023-12-5"。
        // 这里会使用注册的 MyCustomDateEditor 来将字符串转换为 Date 对象。
        beanWrapper.setPropertyValue("date", "2023-12-5");

        // 设置 MyBean 类中名为 "path" 的属性值，使用字符串 "/opt/spring-reading"。
        // 因为没有为 Path 类型注册特定的编辑器，所以使用默认转换逻辑。
        beanWrapper.setPropertyValue("path", "/opt/spring-reading");

        // 输出最终包装的 MyBean 实例。
        System.out.println("MyBean = " + beanWrapper.getWrappedInstance());
    }
}
