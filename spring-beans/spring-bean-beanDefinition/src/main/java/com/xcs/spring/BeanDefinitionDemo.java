package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;

/**
 * @author xcs
 * @date 2023年11月01日 11时21分
 **/
public class BeanDefinitionDemo {
    public static void main(String[] args) throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("myBean", createBeanDefinition());

        // 获取MyBean
        MyBean myChildBean = beanFactory.getBean("myBean", MyBean.class);
        // 打印Bean对象
        System.out.println("MyBean = " + myChildBean);
        // 销毁myBean
        beanFactory.destroySingleton("myBean");
    }

    private static BeanDefinition createBeanDefinition() throws IOException {
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(MyBean.class.getName());

        ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
        beanDefinition.setScope("singleton");
        beanDefinition.setLazyInit(true);
        beanDefinition.setPrimary(true);
        beanDefinition.setAbstract(false);
        beanDefinition.setInitMethodName("init");
        beanDefinition.setDestroyMethodName("destroy");
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
        beanDefinition.setDescription("This is a custom bean definition");
        beanDefinition.setResourceDescription("com.xcs.spring.BeanDefinitionDemo");
        beanDefinition.getPropertyValues().add("name", "lex");
        beanDefinition.getPropertyValues().add("age", "18");
        return beanDefinition;
    }
}
