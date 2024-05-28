package com.xcs.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class ScoresServiceImpl implements ScoresService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void insertScore() {
        long id = System.currentTimeMillis();
        int score = new Random().nextInt(100);
        // 向数据库中插入随机生成的分数
        int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
        // 模拟异常，用于测试事务回滚
        // int i = 1 / 0;
        // 打印影响行数
        System.out.println("scores row = " + row);
    }
}
