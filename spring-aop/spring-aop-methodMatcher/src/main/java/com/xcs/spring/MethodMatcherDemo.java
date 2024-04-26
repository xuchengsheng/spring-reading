package com.xcs.spring;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;

import java.lang.reflect.Method;

public class MethodMatcherDemo {

    public static void main(String[] args) throws Exception {
        Class<MyService> target = MyService.class;
        Method setNameMethod = target.getDeclaredMethod("setName");

        // 使用 AnnotationMethodMatcher 检查是否具有特定注解
        AnnotationMethodMatcher annotationMethodMatcher = new AnnotationMethodMatcher(MyMethodAnnotation.class);
        System.out.println("annotationMethodMatcher matches = " + annotationMethodMatcher.matches(setNameMethod, target));

        // 使用 AspectJExpressionPointcut 基于 AspectJ 表达式匹配方法
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("aspectJExpressionPointcut matches = " + aspectJExpressionPointcut.matches(setNameMethod, target));

        // 使用 NameMatchMethodPointcut 基于方法名称匹配方法
        NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
        nameMatchMethodPointcut.setMappedName("setName");
        System.out.println("nameMatchMethodPointcut matches = " + nameMatchMethodPointcut.matches(setNameMethod, target));

        // 使用 JdkRegexpMethodPointcut 基于正则表达式匹配方法
        JdkRegexpMethodPointcut jdkRegexpMethodPointcut = new JdkRegexpMethodPointcut();
        jdkRegexpMethodPointcut.setPattern(".*set.*");
        System.out.println("jdkRegexpMethodPointcut matches = " + jdkRegexpMethodPointcut.matches(setNameMethod, target));
    }
}
