package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.util.Random;

public class TransactionTemplateDemo {

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
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        // 创建 JdbcTemplate 对象，用于执行 SQL 语句
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // 创建事务模板
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        Boolean insertSuccess = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                // 主键Id
                long id = System.currentTimeMillis();
                // 分数
                int score = new Random().nextInt(100);
                // 保存数据
                int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
                // 模拟异常，用于测试事务回滚
                // int i = 1 / 0;
                // 我们也可以使用setRollbackOnly来回滚
                // status.setRollbackOnly();
                // 返回是否新增成功
                return row >= 1;
            }
        });
        System.out.println("新增scores表数据：" + insertSuccess);
    }
}
