package com.xcs.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

import java.lang.reflect.Method;

/**
 * @author xcs
 * @date 2024年4月7日15:42:49
 */
public class PointcutDemo {
    public static void main(String[] args) {
        customPointcut();
//        aspectJExpressionPointcut();
//        annotationMatchingPointcut();
//        nameMatchMethodPointcut();
//        jdkRegexpMethodPointcut();
    }

    /**
     * 自定义 Pointcut
     */
    private static void customPointcut() {
        MyCustomPointcut pointcut = new MyCustomPointcut();
        showMatchesLog(pointcut);
    }

    /**
     * AspectJExpressionPointcut
     */
    private static void aspectJExpressionPointcut() {
        // 创建 AspectJ 表达式切入点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.xcs.spring.MyService.get*())");
        showMatchesLog(pointcut);
    }

    /**
     * AnnotationMatchingPointcut
     */
    private static void annotationMatchingPointcut() {
        // 使用AnnotationMatchingPointcut切入点
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(MyClassAnnotation.class, MyMethodAnnotation.class, false);
        showMatchesLog(pointcut);
    }

    /**
     * AspectJExpressionPointcut
     */
    private static void nameMatchMethodPointcut() {
        // 使用AnnotationMatchingPointcut切入点
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.addMethodName("getAge");
        showMatchesLog(pointcut);
    }

    /**
     * JdkRegexpMethodPointcut
     */
    private static void jdkRegexpMethodPointcut() {
        JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        pointcut.setPattern(".*set.*");
        showMatchesLog(pointcut);
    }

    public static void showMatchesLog(Pointcut pointcut) {
        try {
            Class<MyService> target = MyService.class;
            Method getNameMethod = target.getDeclaredMethod("getName");
            Method getAgeMethod = target.getDeclaredMethod("getAge");
            Method setNameMethod = target.getDeclaredMethod("setName");

            ClassFilter classFilter = pointcut.getClassFilter();
            MethodMatcher methodMatcher = pointcut.getMethodMatcher();

            System.out.println("ClassFilter MyService = " + classFilter.matches(target));
            System.out.println("MethodMatcher MyService getName = " + methodMatcher.matches(getNameMethod, target));
            System.out.println("MethodMatcher MyService getAge = " + methodMatcher.matches(getAgeMethod, target));
            System.out.println("MethodMatcher MyService setName = " + methodMatcher.matches(setNameMethod, target));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
