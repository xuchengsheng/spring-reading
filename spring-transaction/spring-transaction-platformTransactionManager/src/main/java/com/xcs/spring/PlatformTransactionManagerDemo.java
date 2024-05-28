package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;

public class PlatformTransactionManagerDemo {

    private static PlatformTransactionManager transactionManager;
    private static JdbcTemplate jdbcTemplate;

    public static void main(String[] args) throws SQLException {
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // 数据库用户名
        String username = "root";
        // 数据库密码
        String password = "123456";

        // 创建 SimpleDriverDataSource 对象，用于管理数据源
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(new Driver(), url, username, password);
        // 创建 DataSourceTransactionManager 对象，用于管理事务
        transactionManager = new DataSourceTransactionManager(dataSource);
        // 创建 JdbcTemplate 对象，用于执行 SQL 语句
        jdbcTemplate = new JdbcTemplate(dataSource);

        insertScore();
    }

    private static void insertScore() {
        // 事务定义
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        // 开启一个新的事务，返回事务状态对象
        TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
        try {
            long id = System.currentTimeMillis();
            int score = new Random().nextInt(100);
            // 向数据库中插入随机生成的分数
            int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
            insertScoreLog(id);
            // 模拟异常，用于测试事务回滚
            // int i = 1 / 0;
            // 提交事务
            transactionManager.commit(transactionStatus);
            // 打印影响行数
            System.out.println("scores row = " + row);
        } catch (Exception e) {
            // 出现异常时回滚事务
            transactionManager.rollback(transactionStatus);
            e.printStackTrace();
        }
    }

    private static void insertScoreLog(long scoreId) {
        // 事务定义
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        // 开启一个新的事务，返回事务状态对象
        TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
        try {
            long id = System.currentTimeMillis();
            LocalDateTime createTime = LocalDateTime.now();
            // 向数据库中插入随机生成的分数
            int row = jdbcTemplate.update("insert into scores_log(id,score_id,create_time) values(?,?,?)", id, scoreId, createTime);
            // 模拟异常，用于测试事务回滚
            // int i = 1 / 0;
            // 提交事务
            transactionManager.commit(transactionStatus);
            // 打印影响行数
            System.out.println("scores_log row = " + row);
        } catch (Exception e) {
            // 出现异常时回滚事务
            transactionManager.rollback(transactionStatus);
            e.printStackTrace();
        }
    }
}
