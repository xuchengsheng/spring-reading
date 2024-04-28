package com.xcs.spring;

import java.lang.reflect.Proxy;

public class ProxyMethodInvocationDemo {

    public static void main(String[] args) {
        // 创建目标对象
        MyService target = new MyServiceImpl();
        // 获取目标对象的类对象
        Class<? extends MyService> clz = target.getClass();
        // 创建代理对象，并指定目标对象的类加载器、实现的接口以及调用处理器
        MyService proxyObject = (MyService) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new MyInvocationHandler(target));
        // 通过代理对象调用方法，实际上会调用 MyInvocationHandler 中的 invoke 方法
        proxyObject.foo();
    }
}
