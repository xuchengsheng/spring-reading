package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EnableTransactionManagementDemo {

    public static void main(String[] args) {
        // 创建基于注解的应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从应用上下文中获取ScoresService bean
        ScoresService scoresService = context.getBean(ScoresService.class);
        // 调用ScoresService的方法
        scoresService.insertScore();
    }
}
