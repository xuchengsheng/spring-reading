package com.xcs.spring.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xcs
 * @date 2023年09月27日 10时36分
 **/
@Service
public class MyService {

    /**
     * 这里使用了Java的Timer来模拟定时任务。在实际应用中，可能会使用更复杂的调度机制。
     */
    public void startScheduledTask() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println(getDate() + " hello world ");
                    }
                },
                0,
                2000
        );
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public String getDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
