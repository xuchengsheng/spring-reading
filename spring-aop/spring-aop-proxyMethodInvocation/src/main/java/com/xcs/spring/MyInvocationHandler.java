package com.xcs.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 自定义的 InvocationHandler 实现类，用于处理 Java 动态代理的方法调用。
 */
class MyInvocationHandler implements InvocationHandler {

    // 目标对象
    private final Object target;

    /**
     * 构造方法，初始化目标对象。
     * @param target 目标对象
     */
    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * 处理方法调用的核心方法。
     * @param proxy 代理对象
     * @param method 被调用的方法对象
     * @param args 方法参数
     * @return 方法调用结果
     * @throws Throwable 可能抛出的异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 创建 MyReflectiveMethodInvocation 对象，用于执行方法调用链
        MyReflectiveMethodInvocation invocation = new MyReflectiveMethodInvocation(proxy, target, method, args, target.getClass(), List.of(new MyMethodInterceptor()));
        // 执行方法调用链，并返回结果
        return invocation.proceed();
    }
}
