package com.xcs.spring;

import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionDefinitionDemo {

    public static void main(String[] args) {
        // 创建一个 DefaultTransactionDefinition 实例
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();

        // 设置传播行为为PROPAGATION_REQUIRES_NEW
        transactionDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        System.out.println("Propagation Behavior: " + transactionDefinition.getPropagationBehavior());

        // 设置隔离级别为ISOLATION_REPEATABLE_READ
        transactionDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_REPEATABLE_READ);
        System.out.println("Isolation Level: " + transactionDefinition.getIsolationLevel());

        // 设置事务超时时间为30秒
        transactionDefinition.setTimeout(30);
        System.out.println("Timeout: " + transactionDefinition.getTimeout());

        // 设置事务为只读
        transactionDefinition.setReadOnly(true);
        System.out.println("Is Read Only: " + transactionDefinition.isReadOnly());

        // 设置事务名称为"DemoTransaction"
        transactionDefinition.setName("DemoTransaction");
        System.out.println("Transaction Name: " + transactionDefinition.getName());
    }
}
