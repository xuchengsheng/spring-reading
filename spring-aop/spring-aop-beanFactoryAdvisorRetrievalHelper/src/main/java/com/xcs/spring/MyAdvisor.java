package com.xcs.spring;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

public class MyAdvisor extends AbstractPointcutAdvisor {

    @Override
    public Pointcut getPointcut() {
        return Pointcut.TRUE;
    }

    @Override
    public Advice getAdvice() {
        return Advisor.EMPTY_ADVICE;
    }
}
