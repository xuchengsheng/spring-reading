package com.xcs.spring;

import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author xcs
 * @date 2023年11月07日 10时01分
 **/
public class GroovyBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // 创建一个 Spring IOC 容器
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // 创建一个 GroovyBeanDefinitionReader
        GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader(factory);

        // 加载 Groovy 文件并注册 Bean 定义
        reader.loadBeanDefinitions(new ClassPathResource("my-beans.groovy"));

        // 获取MyService
        MyService myService = factory.getBean(MyService.class);
        // 打印消息
        myService.showMessage();
    }
}
