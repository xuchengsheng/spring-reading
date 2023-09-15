package com.xcs.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

/**
 * @author 林雷
 * @date 2023年08月14日 15时38分
 **/
public class MyCustomBeanNameGenerator implements BeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        // 此处只是一个示例，我们可以根据自己的实际情况生成Bean名称
        String originalName = definition.getBeanClassName();
        if (originalName != null) {
            return "_这是我自定义Bean名称的前缀_" + originalName;
        } else {
            // 你可以选择其他的逻辑处理或者抛出异常
            throw new IllegalArgumentException("Bean class name is null");
        }
    }
}
