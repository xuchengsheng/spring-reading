package com.xcs.spring;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * @author xcs
 * @date 2024年4月7日15:42:49
 */
public class PointcutDemo {
    public static void main(String[] args) {
        customPointcut();
    }

    /**
     * 自定义 Pointcut 最佳实践
     */
    private static void customPointcut() {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyBean());
        // 添加切面：使用自定义的切入点和通知构建默认切面
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new MyCustomPointcut(), new MyCustomAdvice()));
        // 获取代理对象
        MyBean myBean = (MyBean) proxyFactory.getProxy();

        // 使用代理对象调用方法
        myBean.getName(); // 将被通知拦截
        myBean.getAge(); // 将被通知拦截
        myBean.setName(); // 不会被通知拦截
    }

    /**
     * AspectJExpressionPointcut最佳实践
     */
    private static void aspectJExpressionPointcut() {
        // 创建 AspectJ 表达式切入点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* *.getName())");

        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyBean());
        // 添加切面：使用自定义的切入点和通知构建默认切面
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(pointcut, new MyCustomAdvice()));
        // 获取代理对象
        MyBean myBean = (MyBean) proxyFactory.getProxy();

        // 使用代理对象调用方法
        myBean.getName(); // 将被通知拦截
        myBean.getAge(); // 不会被通知拦截
        myBean.setName(); // 不会被通知拦截
    }

    /**
     * AnnotationMatchingPointcut 最佳实践
     */
    private static void annotationMatchingPointcut() {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyBean());
        // 添加切面：使用AnnotationMatchingPointcut切入点和通知构建默认切面
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(MyAnnotation.class, false), new MyCustomAdvice()));
        // 获取代理对象
        MyBean myBean = (MyBean) proxyFactory.getProxy();

        // 使用代理对象调用方法
        myBean.getName(); // 将被通知拦截
        myBean.getAge(); // 将被通知拦截
        myBean.setName(); // 将被通知拦截
    }

    /**
     * AspectJExpressionPointcut最佳实践
     */
    private static void nameMatchMethodPointcut() {
        // 创建方法名匹配切入点
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.addMethodName("getAge");

        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyBean());
        // 添加切面：使用自定义的切入点和通知构建默认切面
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(pointcut, new MyCustomAdvice()));
        // 获取代理对象
        MyBean myBean = (MyBean) proxyFactory.getProxy();

        // 使用代理对象调用方法
        myBean.getName(); // 不会被通知拦截
        myBean.getAge(); // 将被通知拦截
        myBean.setName(); // 不会被通知拦截
    }
}
