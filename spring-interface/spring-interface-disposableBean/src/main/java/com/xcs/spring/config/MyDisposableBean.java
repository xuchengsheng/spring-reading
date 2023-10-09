package com.xcs.spring.config;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author xcs
 * @date 2023年09月19日 16时42分
 **/
public class MyDisposableBean implements DisposableBean {

    // 模拟的数据库连接对象
    private String databaseConnection;

    public MyDisposableBean() {
        // 在构造函数中模拟建立数据库连接
        this.databaseConnection = "Database connection established";
        System.out.println(databaseConnection);
    }

    @Override
    public void destroy() throws Exception {
        // 在 destroy 方法中模拟关闭数据库连接
        databaseConnection = null;
        System.out.println("Database connection closed");
    }
}
