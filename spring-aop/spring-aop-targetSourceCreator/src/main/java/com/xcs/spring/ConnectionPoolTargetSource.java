package com.xcs.spring;

import org.springframework.aop.TargetSource;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 连接池目标源，用于管理自定义连接对象的连接池。
 *
 * @author xcs
 * @date 2024年4月9日15:26:28
 */
public class ConnectionPoolTargetSource implements TargetSource {

    /**
     * 连接池
     */
    private final BlockingQueue<MyConnection> connectionPool;

    /**
     * 构造函数，初始化连接池。
     *
     * @param poolSize 连接池大小
     */
    public ConnectionPoolTargetSource(int poolSize) {
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);
        initializeConnectionPool(poolSize);
    }

    /**
     * 初始化连接池，填充连接对象。
     *
     * @param poolSize 连接池大小
     */
    private void initializeConnectionPool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            MyConnection connection = new MyConnection("Connection" + i);
            connectionPool.offer(connection);
        }
    }

    /**
     * 获取目标类的类型。
     *
     * @return 目标类的类型
     */
    @Override
    public Class<?> getTargetClass() {
        return MyConnection.class;
    }

    /**
     * 判断目标对象是否是静态的。
     *
     * @return 如果目标对象是静态的，则返回true，否则返回false
     */
    @Override
    public boolean isStatic() {
        return false;
    }

    /**
     * 获取连接对象。
     *
     * @return 连接对象
     * @throws Exception 如果获取连接对象时发生异常
     */
    @Override
    public Object getTarget() throws Exception {
        return connectionPool.take();
    }

    /**
     * 释放连接对象。
     *
     * @param target 待释放的连接对象
     * @throws Exception 如果释放连接对象时发生异常
     */
    @Override
    public void releaseTarget(Object target) throws Exception {
        if (target instanceof MyConnection) {
            connectionPool.offer((MyConnection) target);
        }
    }
}
