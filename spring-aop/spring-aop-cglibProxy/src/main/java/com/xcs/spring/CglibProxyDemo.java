package com.xcs.spring;

import org.springframework.cglib.proxy.Enhancer;

public class CglibProxyDemo {

    public static void main(String[] args) {
        // 创建 Enhancer 对象，用于生成代理类
        Enhancer enhancer = new Enhancer();
        // 设置目标对象的父类
        enhancer.setSuperclass(MyServiceImpl.class);
        // 设置回调拦截器
        enhancer.setCallback(new MyMethodInterceptor());
        // 创建代理对象
        MyService proxyObject = (MyService) enhancer.create();
        // 输出代理对象的类名
        System.out.println("ProxyObject = " + proxyObject.getClass());
        // 调用代理对象的方法
        proxyObject.doSomething();
    }
}
