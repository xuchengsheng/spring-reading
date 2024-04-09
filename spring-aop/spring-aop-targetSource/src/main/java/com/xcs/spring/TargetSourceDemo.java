package com.xcs.spring;

import org.springframework.aop.framework.ProxyFactory;

public class TargetSourceDemo {

    public static void main(String[] args) {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置目标源为连接池目标源，连接池大小为3
        proxyFactory.setTargetSource(new ConnectionPoolTargetSource(3));
        // 获取代理对象
        MyConnection proxy = (MyConnection) proxyFactory.getProxy();

        // 调用10次方法
        for (int i = 0; i < 10; i++) {
            System.out.println("MyConnection Name = " + proxy.getName());
        }
    }
}
