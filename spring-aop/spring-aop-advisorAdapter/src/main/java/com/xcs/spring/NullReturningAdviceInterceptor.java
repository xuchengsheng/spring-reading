package com.xcs.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterAdvice;
import org.springframework.util.Assert;

/**
 * 空返回通知拦截器，用于在方法执行后检查返回值是否为空，并根据情况执行空返回通知的逻辑。
 */
public class NullReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice {

    /** 空返回通知 */
    private final NullReturningAdvice advice;

    /**
     * 构造一个空返回通知拦截器。
     * @param advice 空返回通知
     */
    public NullReturningAdviceInterceptor(NullReturningAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
    }

    /**
     * 在方法执行后拦截，检查返回值是否为空，并根据情况执行空返回通知的逻辑。
     * @param mi 方法调用信息
     * @return 方法执行结果，如果返回值为空，则根据空返回通知执行后的返回值
     * @throws Throwable 如果方法调用过程中发生异常，则抛出异常
     */
    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        // 执行方法调用，获取返回值
        Object retVal = mi.proceed();
        // 如果返回值为空，则根据空返回通知执行后的返回值
        if (retVal == null) {
            retVal = this.advice.nullReturning(mi.getMethod(), mi.getArguments(), mi.getThis());
        }
        return retVal;
    }
}
