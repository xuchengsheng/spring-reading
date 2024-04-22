package com.xcs.spring;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class AdvisedDemo {

    public static void main(String[] args) {
        // 创建 AdvisedSupport 对象
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 设置目标对象实现的接口
        advisedSupport.setInterfaces(MyService.class);
        // 添加通知
        advisedSupport.addAdvice(new Advice() {});
        // 添加通知器
        advisedSupport.addAdvisor(new DefaultPointcutAdvisor());
        // 暴露代理对象
        advisedSupport.setExposeProxy(true);
        // 设置CGLIB 代理
        advisedSupport.setProxyTargetClass(true);
        // 冻结对象
        advisedSupport.setFrozen(true);
        // 打印
        System.out.println("AdvisedSupport = " + advisedSupport.toProxyConfigString());
    }
}
