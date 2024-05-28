package com.xcs.spring;

import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Method;

public class SpringTransactionAnnotationParserDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // 获取 ScoresServiceImpl 类中的 insertScore 方法
        Method insertScoreMethod = ScoresServiceImpl.class.getMethod("insertScore");
        // 创建 SpringTransactionAnnotationParser 实例
        SpringTransactionAnnotationParser parser = new SpringTransactionAnnotationParser();
        // 解析 insertScore 方法上的事务注解，并转换为事务属性对象
        TransactionAttribute transactionAttribute = parser.parseTransactionAnnotation(insertScoreMethod);
        // 输出事务属性对象
        System.out.println(transactionAttribute);
    }
}
