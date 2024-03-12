package com.xcs.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class BeanResolverDemo {
    public static void main(String[] args) {
        // 创建 BeanFactory
        // 这里使用注解配置的方式创建 BeanFactory
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(MyBean.class).getBeanFactory();

        // 创建一个SpEL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        // 创建一个标准的评估上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 将 BeanFactoryResolver 设置为上下文的 BeanResolver
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));

        // 解析 SpEL 表达式，获取 Bean 实例
        Object myBean = parser.parseExpression("@myBean").getValue(context);

        // 打印 Bean 实例
        System.out.println("myBean = " + myBean);
    }
}
