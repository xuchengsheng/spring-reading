package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

public class TransactionInterceptorDemo {

    public static void main(String[] args) throws Exception {
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // 数据库用户名
        String username = "root";
        // 数据库密码
        String password = "123456";

        // 创建 SimpleDriverDataSource 对象，用于管理数据源
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(new Driver(), url, username, password);
        // 创建 DataSourceTransactionManager 对象，用于管理事务
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        // 创建 JdbcTemplate 对象，用于执行 SQL 语句
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // 创建 AnnotationTransactionAttributeSource 对象，用于获取事务属性
        AnnotationTransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();

        // 创建 TransactionInterceptor 对象，用于拦截方法调用并管理事务
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionAttributeSource(transactionAttributeSource);
        transactionInterceptor.setTransactionManager(transactionManager);

        // 创建 BeanFactoryTransactionAttributeSourceAdvisor 对象，用于配置事务拦截器
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(transactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);

        // 创建 ProxyFactory 对象，用于创建代理对象
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addAdvisor(advisor);
        proxyFactory.setTarget(new ScoresServiceImpl(jdbcTemplate));

        // 获取代理对象，并调用其方法
        ScoresService scoresService = (ScoresService) proxyFactory.getProxy();
        scoresService.insertScore();
    }
}
