package com.xcs.spring;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class ConnectionDemo {

    public static void main(String[] args) {
        // 使用 DataSource 体现了高内聚，有利于后面更改数据库
        MysqlDataSource dataSource = new MysqlDataSource();
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        dataSource.setUrl("jdbc:mysql://localhost:3306/spring-reading");
        // 数据库用户名
        dataSource.setUser("root");
        // 数据库密码
        dataSource.setPassword("123456");

        try (Connection connection = dataSource.getConnection()) {
            // 设置不自动提交，开启事务
            connection.setAutoCommit(false);
            // SQL 插入语句
            String sql = "insert into scores(score) values(?)";
            // 创建 PreparedStatement 对象，用于执行 SQL 插入操作
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // 设置参数
                statement.setInt(1, new Random().nextInt(100));
                // 执行插入操作
                int row = statement.executeUpdate();
                // 模拟异常，用于测试事务回滚
                // int i = 1 / 0;
                // 提交事务
                connection.commit();
                // 打印影响行数
                System.out.println("row = " + row);
            } catch (Exception e) {
                // 发生异常时回滚事务
                connection.rollback();
                // 打印异常信息
                e.printStackTrace();
            }
        } catch (SQLException e) {
            // 处理关闭连接异常
            e.printStackTrace();
        }
    }
}
