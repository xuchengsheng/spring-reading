package com.xcs.spring.config;

import com.xcs.spring.bean.MyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xcs
 * @date 2023年08月07日 16时25分
 **/
@Configuration
@Import({MyBean.class, MyImportSelector.class, MyDeferredImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class MyConfiguration {

}