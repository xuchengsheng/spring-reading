package com.xcs.spring;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MyLTWAspect {

    @Around("ltwPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 在方法调用之前执行的逻辑
        System.out.println("Before Method " + pjp.getSignature().getName());
        // 调用原始方法
        Object result = pjp.proceed();
        // 在方法调用之后执行的逻辑
        System.out.println("After Method " + pjp.getSignature().getName());
        return result;
    }

    @Pointcut("execution(public * com.xcs.spring.MyService.*(..))")
    public void ltwPointcut() {
    }
}
