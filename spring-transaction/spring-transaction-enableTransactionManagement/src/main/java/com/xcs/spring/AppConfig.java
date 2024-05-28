package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@ComponentScan
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SimpleDriverDataSource dataSource() throws SQLException {
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // 数据库用户名
        String username = "root";
        // 数据库密码
        String password = "123456";
        // 初始化数据源
        return new SimpleDriverDataSource(new Driver(), url, username, password);
    }
}
