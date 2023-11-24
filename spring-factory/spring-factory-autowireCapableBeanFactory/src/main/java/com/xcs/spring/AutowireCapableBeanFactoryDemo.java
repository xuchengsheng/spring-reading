package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.controller.MyController;
import com.xcs.spring.service.MyService;
import com.xcs.spring.service.impl.MyServiceImpl;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年11月24日 15时02分
 **/
public class AutowireCapableBeanFactoryDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

        // 获取 AutowireCapableBeanFactory
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        // 示例：全面创建新的 bean 实例
        MyService myService = beanFactory.createBean(MyServiceImpl.class);
        System.out.println("全面创建新的 bean 实例 = " + myService);

        System.out.println();

        MyController autowireBean = new MyController();
        System.out.println("调用autowireBean方法前,MyController: " + autowireBean);
        beanFactory.autowireBean(autowireBean);
        System.out.println("调用autowireBean方法后,MyController: " + autowireBean);

        System.out.println();

        MyController configureBean = new MyController();
        System.out.println("调用configureBean方法前,MyController: " + configureBean);
        beanFactory.configureBean(configureBean, "myController");
        System.out.println("调用configureBean方法后,MyController: " + configureBean);

    }
}
