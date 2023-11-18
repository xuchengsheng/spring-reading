package com.xcs.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xcs
 * @date 2023年11月17日 14时52分
 **/
@Configuration
@Import(MyImportBeanDefinitionRegistrar.class)
public class MyConfiguration {
}
