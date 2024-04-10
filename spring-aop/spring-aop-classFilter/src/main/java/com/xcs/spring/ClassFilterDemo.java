package com.xcs.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.TypePatternClassFilter;
import org.springframework.aop.support.RootClassFilter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;

public class ClassFilterDemo {
    public static void main(String[] args) {
        // 创建 AnnotationClassFilter 实例，匹配带有 MyAnnotation 注解的类
        ClassFilter filter1 = new AnnotationClassFilter(MyAnnotation.class);
        System.out.println("AnnotationClassFilter 是否匹配 MyService 类：" + filter1.matches(MyService.class));

        // 创建 TypePatternClassFilter 实例，匹配指定类名的类
        ClassFilter filter2 = new TypePatternClassFilter("com.xcs.spring.MyService");
        System.out.println("TypePatternClassFilter 是否匹配 MyService 类：" + filter2.matches(MyService.class));

        // 创建 RootClassFilter 实例，匹配指定类的根类
        ClassFilter filter3 = new RootClassFilter(MyService.class);
        System.out.println("RootClassFilter 是否匹配 MySubService 的根类：" + filter3.matches(MySubService.class));

        // 创建 AspectJExpressionPointcut 实例，根据 AspectJ 表达式匹配类和方法
        AspectJExpressionPointcut filter4 = new AspectJExpressionPointcut();
        filter4.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("AspectJExpressionPointcut 是否匹配 MyService 类：" + filter4.matches(MyService.class));
    }
}
