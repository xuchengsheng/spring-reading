package com.xcs.spring;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;

public class MethodMatcherDemo {

    public static void main(String[] args) throws Exception {
        // 使用 AnnotationMethodMatcher 检查是否具有特定注解
        AnnotationMethodMatcher methodMatcher = new AnnotationMethodMatcher(MyAnnotation.class);
        System.out.println("方法是否具有特定注解： " + methodMatcher.matches(MyService.class.getDeclaredMethod("myMethod"), MyService.class));

        // 使用 AspectJExpressionPointcut 基于 AspectJ 表达式匹配方法
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("方法是否匹配 AspectJ 表达式： " + pointcut.matches(MyService.class.getDeclaredMethod("myMethod"), MyService.class));

        // 使用 NameMatchMethodPointcut 基于方法名称匹配方法
        NameMatchMethodPointcut pointcut2 = new NameMatchMethodPointcut();
        pointcut2.setMappedName("myMethod");
        System.out.println("方法是否匹配指定名称： " + pointcut2.matches(MyService.class.getDeclaredMethod("myMethod"), MyService.class));

        // 使用 JdkRegexpMethodPointcut 基于正则表达式匹配方法
        JdkRegexpMethodPointcut pointcut3 = new JdkRegexpMethodPointcut();
        pointcut3.setPattern(".*my.*");
        System.out.println("方法是否匹配正则表达式： " + pointcut3.matches(MyService.class.getDeclaredMethod("myMethod"), MyService.class));
    }
}
