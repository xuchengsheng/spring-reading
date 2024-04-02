package com.xcs.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

import java.lang.reflect.Method;

class MyCustomPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        // 匹配所有类
        return clazz -> true;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // 匹配所有以 "get" 开头的方法
                return method.getName().startsWith("get");
            }

            @Override
            public boolean isRuntime() {
                // 是否需要在运行时动态匹配
                return false;
            }

            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) {
                // 运行时匹配，这里不需要，所以简单返回 false
                return false;
            }
        };
    }
}
