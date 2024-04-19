package com.xcs.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry;

public class AdvisorAdapterRegistryDemo {

    public static void main(String[] args) {
        // 创建默认的Advisor适配器注册表实例
        DefaultAdvisorAdapterRegistry registry = new DefaultAdvisorAdapterRegistry();
        // 包装给定的MyMethodBeforeAdvice为Advisor
        Advisor advisor = registry.wrap(new MyMethodBeforeAdvice());

        // 获取Advisor中的拦截器数组
        MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
        // 输出拦截器信息
        for (MethodInterceptor interceptor : interceptors) {
            System.out.println("interceptor = " + interceptor);
        }
    }
}
