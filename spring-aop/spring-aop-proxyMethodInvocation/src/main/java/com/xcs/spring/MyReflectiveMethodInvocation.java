package com.xcs.spring;

import org.springframework.aop.framework.ReflectiveMethodInvocation;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 自定义的方法调用对象，继承自 Spring AOP 的 ReflectiveMethodInvocation 类。
 * 用于在方法调用中加入自定义逻辑或增强功能。
 */
public class MyReflectiveMethodInvocation extends ReflectiveMethodInvocation {

    /**
     * 构造方法，初始化方法调用对象。
     * @param proxy 代理对象
     * @param target 目标对象
     * @param method 被调用的方法对象
     * @param arguments 方法参数
     * @param targetClass 目标对象的类
     * @param interceptorsAndDynamicMethodMatchers 拦截器链和动态方法匹配器列表
     */
    public MyReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
    }
}
