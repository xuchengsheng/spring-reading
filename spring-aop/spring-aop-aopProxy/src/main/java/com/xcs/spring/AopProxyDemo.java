package com.xcs.spring;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;

import java.lang.reflect.Constructor;

public class AopProxyDemo {

    public static void main(String[] args) throws Exception {
//        cglibProxy();
        jdkProxy();
    }

    /**
     * cglib代理
     *
     * @throws Exception
     */
    private static void cglibProxy() throws Exception {
        // 创建AdvisedSupport对象，用于配置AOP代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 添加拦截器
        advisedSupport.addAdvice(new MyMethodInterceptor());

        // 获取CglibAopProxy的Class对象
        Class<?> cglibClass = Class.forName("org.springframework.aop.framework.CglibAopProxy");

        // 获取CglibAopProxy的构造方法
        Constructor<?> constructor = cglibClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // 使用构造方法创建CglibAopProxy实例
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // 调用getProxy方法创建代理对象
        MyService myService = (MyService) aopProxy.getProxy();

        // 输出代理对象的信息
        System.out.println("Cglib Class = " + myService.getClass());
        // 调用代理对象的方法
        myService.foo();
    }

    /**
     * Jdk代理
     *
     * @throws Exception
     */
    private static void jdkProxy() throws Exception {
        // 创建AdvisedSupport对象，用于配置AOP代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 设置目标对象实现的接口
        advisedSupport.setInterfaces(MyService.class);
        // 添加拦截器
        advisedSupport.addAdvice(new MyMethodInterceptor());

        // 获取JdkDynamicAopProxy的Class对象
        Class<?> jdkClass = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");

        // 获取JdkDynamicAopProxy的构造方法
        Constructor<?> constructor = jdkClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // 使用构造方法创建JdkDynamicAopProxy实例
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // 调用getProxy方法创建代理对象
        MyService myService = (MyService) aopProxy.getProxy();

        // 输出代理对象的信息
        System.out.println("JDK Class = " + myService.getClass());
        // 调用代理对象的方法
        myService.foo();
    }
}
