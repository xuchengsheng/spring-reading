package com.xcs.spring;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.DefaultAdvisorChainFactory;

import java.lang.reflect.Method;
import java.util.List;

public class AdvisorChainFactoryDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // 创建AOP配置对象
        AdvisedSupport config = new AdvisedSupport();
        // 添加前置通知
        config.addAdvice(new MyMethodBeforeAdvice());
        // 添加后置返回通知
        config.addAdvice(new MyAfterReturningAdvice());
        // 设置目标类
        Class<MyService> targetClass = MyService.class;
        // 获取目标方法
        Method method = targetClass.getDeclaredMethod("foo");

        // 创建默认的Advisor链工厂实例
        DefaultAdvisorChainFactory chainFactory = new DefaultAdvisorChainFactory();
        // 获取Advisor链
        List<Object> chain = chainFactory.getInterceptorsAndDynamicInterceptionAdvice(config, method, targetClass);
        // 打印Advisor链中的拦截器
        chain.forEach(System.out::println);
    }
}
