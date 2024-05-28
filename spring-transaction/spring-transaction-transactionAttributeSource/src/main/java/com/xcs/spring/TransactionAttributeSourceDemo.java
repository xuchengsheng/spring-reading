package com.xcs.spring;

import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.Method;

public class TransactionAttributeSourceDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // 获取 ScoresServiceImpl 类中的 insertScore 方法
        Method insertScoreMethod = ScoresServiceImpl.class.getMethod("insertScore");
        // 创建一个基于注解的事务属性源对象
        TransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();
        // 解析 insertScore 方法的事务属性
        TransactionAttribute transactionAttribute = transactionAttributeSource.getTransactionAttribute(insertScoreMethod, ScoresServiceImpl.class);
        // 输出事务属性
        System.out.println(transactionAttribute);
    }
}
