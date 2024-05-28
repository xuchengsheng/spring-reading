package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.util.List;

public class JdbcTemplateDemo {

    public static void main(String[] args) throws Exception {
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // 数据库用户名
        String username = "root";
        // 数据库密码
        String password = "123456";

        // 创建 SimpleDriverDataSource 对象，用于管理数据源
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(new Driver(), url, username, password);
        // 创建 JdbcTemplate 对象，用于执行 SQL 语句
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        List<Scores> scoresList = jdbcTemplate.query("select * from scores ", (rs, rowNum) -> {
            Scores scores = new Scores();
            scores.setId(rs.getLong("id"));
            scores.setScore(rs.getLong("score"));
            return scores;
        });

        scoresList.forEach(System.out::println);
    }
}
