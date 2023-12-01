package com.xcs.spring;

import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author xcs
 * @date 2023年11月30日 18时15分
 **/
public class EnvironmentDemo {

    public static void main(String[] args) {
        // 设置系统属性以模拟 Spring 的配置文件功能
        System.setProperty("spring.profiles.default", "dev");
        System.setProperty("spring.profiles.active", "test");

        // 创建 StandardEnvironment 实例，用于访问属性和配置文件信息
        Environment environment = new StandardEnvironment();

        // 使用 getProperty 方法获取系统属性。这里获取了 Java 版本
        String javaVersion = environment.getProperty("java.version");
        System.out.println("java.version: " + javaVersion);

        // 获取当前激活的配置文件（profiles）
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("activeProfiles = " + String.join(",", activeProfiles));

        // 获取默认配置文件（当没有激活的配置文件时使用）
        String[] defaultProfiles = environment.getDefaultProfiles();
        System.out.println("defaultProfiles = " + String.join(",", defaultProfiles));

        // 检查是否激活了指定的配置文件。这里检查的是 'test' 配置文件
        boolean isDevProfileActive = environment.acceptsProfiles("test");
        System.out.println("acceptsProfiles('test'): " + isDevProfileActive);
    }
}
