package com.xcs.spring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

public class ScoresServiceImpl implements ScoresService {

    private JdbcTemplate jdbcTemplate;

    public ScoresServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void insertScore() throws Exception {
        // 主键Id
        long id = System.currentTimeMillis();
        // 分数
        int score = new Random().nextInt(100);
        // 保存数据
        int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
        // 打印影响行数
        System.out.println("scores row = " + row);
        // 模拟异常
        // throw new Exception("测试异常");
    }
}
