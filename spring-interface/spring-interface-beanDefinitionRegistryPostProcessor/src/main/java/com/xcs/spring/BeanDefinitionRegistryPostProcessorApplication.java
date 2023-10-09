package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.config.MySimpleBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023年08月07日 16时21分
 **/
public class BeanDefinitionRegistryPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MySimpleBean mySimpleBean1 = context.getBean(MySimpleBean.class);
        mySimpleBean1.show();
    }
}
