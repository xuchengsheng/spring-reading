package com.xcs.spring.config;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xcs
 * @date 2023年11月17日 14时52分
 **/
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 注册一个名为 "myBean" 的简单Bean
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MyBean.class);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getBeanDefinition();
        registry.registerBeanDefinition("myBean", definition);
    }
}