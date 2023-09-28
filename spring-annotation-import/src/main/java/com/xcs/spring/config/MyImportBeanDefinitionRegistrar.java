package com.xcs.spring.config;

import com.xcs.spring.bean.MyBeanC;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xcs
 * @date 2023年08月28日 11时17分
 **/
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(MyBeanC.class);
        registry.registerBeanDefinition(MyBeanC.class.getName(), beanDefinition);
    }
}
