package com.xcs.spring;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataSourceDemo {

    public static void main(String[] args) throws Exception {
        // 使用 DataSource 体现了高内聚，有利于后面更改数据库
        MysqlDataSource dataSource = new MysqlDataSource();
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        dataSource.setUrl("jdbc:mysql://localhost:3306/spring-reading");
        // 数据库用户名
        dataSource.setUser("root");
        // 数据库密码
        dataSource.setPassword("123456");

        // 建立数据库连接
        Connection connection = dataSource.getConnection();
        // SQL 查询语句
        String sql = "SELECT * FROM scores";
        // 创建 PreparedStatement 对象，用于执行 SQL 查询
        PreparedStatement statement = connection.prepareStatement(sql);
        // 执行查询，获取结果集
        ResultSet resultSet = statement.executeQuery();

        // 遍历结果集
        while (resultSet.next()) {
            // 获取 id 列的值
            int id = resultSet.getInt("id");
            // 获取 score 列的值
            String score = resultSet.getString("score");
            // 输出结果
            System.out.println("id: " + id + ", score: " + score);
        }
        // 关闭结果集、PreparedStatement 和数据库连接
        resultSet.close();
        statement.close();
        connection.close();
    }
}
