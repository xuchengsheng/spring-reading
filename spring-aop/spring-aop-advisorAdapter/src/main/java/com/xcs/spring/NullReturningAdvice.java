package com.xcs.spring;

import org.springframework.aop.AfterAdvice;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * 空返回通知接口，继承自 AfterAdvice。
 */
public interface NullReturningAdvice extends AfterAdvice {

    /**
     * 当目标方法返回值为空时调用的方法。
     * @param method 目标方法
     * @param args 方法参数
     * @param target 目标对象
     * @return 空返回通知执行后的返回值
     * @throws Throwable 如果在执行空返回通知的过程中发生异常，则抛出异常
     */
    Object nullReturning(Method method, Object[] args, @Nullable Object target) throws Throwable;
}
