package com.xcs.spring;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * 自定义Advisor，用于将通知应用于带有特定注解的方法。
 */
public class MyCustomAdvisor implements PointcutAdvisor {

    /**
     * 通知对象
     */
    private final Advice advice = new MyAdvice();

    /**
     * 切点对象，用于匹配带有自定义注解的方法
     */
    private final Pointcut pointcut = new AnnotationMatchingPointcut(null, MyCustomAnnotation.class);

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }
}
