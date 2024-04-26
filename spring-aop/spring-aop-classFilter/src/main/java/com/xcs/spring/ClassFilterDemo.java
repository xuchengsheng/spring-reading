package com.xcs.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.TypePatternClassFilter;
import org.springframework.aop.support.RootClassFilter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;

public class ClassFilterDemo {
    public static void main(String[] args) {
        // 创建 AnnotationClassFilter 实例，匹配带有 MyAnnotation 注解的类
        ClassFilter annotationClassFilter = new AnnotationClassFilter(MyClassAnnotation.class);
        System.out.println("annotationClassFilter matches =" + annotationClassFilter.matches(MyService.class));

        // 创建 TypePatternClassFilter 实例，匹配指定类名的类
        ClassFilter typePatternClassFilter = new TypePatternClassFilter("com.xcs.spring.MyService");
        System.out.println("typePatternClassFilter matches =" + typePatternClassFilter.matches(MyService.class));

        // 创建 RootClassFilter 实例，匹配指定类的根类
        ClassFilter rootClassFilter = new RootClassFilter(MyService.class);
        System.out.println("rootClassFilter matches = " + rootClassFilter.matches(MySubService.class));

        // 创建 AspectJExpressionPointcut 实例，根据 AspectJ 表达式匹配类和方法
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("aspectJExpressionPointcut matches = " + aspectJExpressionPointcut.matches(MyService.class));
    }
}
