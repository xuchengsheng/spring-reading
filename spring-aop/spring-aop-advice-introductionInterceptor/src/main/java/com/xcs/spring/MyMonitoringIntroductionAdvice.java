package com.xcs.spring;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

public class MyMonitoringIntroductionAdvice extends DelegatingIntroductionInterceptor implements MyMonitoringCapable {

    private boolean active = false;

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void toggleMonitoring() {
        setActive(true);
    }

    // 当被监控的方法被调用时，如果监控处于激活状态，则输出日志
    @Override
    protected Object doProceed(MethodInvocation mi) throws Throwable {
        if (this.active) {
            System.out.println("[开启监控" + mi.getMethod().getName() + "]");
            long startTime = System.currentTimeMillis();
            Object result = super.doProceed(mi);
            long endTime = System.currentTimeMillis();
            System.out.println("[结束监控" + mi.getMethod().getName() + "] 耗费时间：" + (endTime - startTime) + " 毫秒");
            return result;
        }
        return super.doProceed(mi);
    }
}
