package com.xcs.spring;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.adapter.AdvisorAdapter;

/**
 * 空返回通知适配器，用于将空返回通知（NullReturningAdvice）适配到拦截器链中。
 */
public class NullReturningAdviceAdapter implements AdvisorAdapter {

    /**
     * 判断该适配器是否支持给定的通知。
     * @param advice 一个通知，如空返回通知（NullReturningAdvice）
     * @return 如果该适配器支持给定的通知，则返回 true；否则返回 false
     */
    @Override
    public boolean supportsAdvice(Advice advice) {
        return (advice instanceof NullReturningAdvice);
    }

    /**
     * 获取一个方法拦截器，将给定的通知行为暴露给基于拦截的 AOP 框架。
     * @param advisor Advisor。supportsAdvice() 方法必须在此对象上返回 true
     * @return 给定 Advisor 的方法拦截器
     */
    @Override
    public MethodInterceptor getInterceptor(Advisor advisor) {
        NullReturningAdvice advice = (NullReturningAdvice) advisor.getAdvice();
        return new NullReturningAdviceInterceptor(advice);
    }
}

