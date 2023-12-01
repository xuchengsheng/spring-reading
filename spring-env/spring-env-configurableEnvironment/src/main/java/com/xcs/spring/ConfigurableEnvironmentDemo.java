package com.xcs.spring;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023年12月01日 14时05分
 **/
public class ConfigurableEnvironmentDemo {

    public static void main(String[] args) {
        // 创建 StandardEnvironment 实例，用于访问属性和配置文件信息
        ConfigurableEnvironment environment = new StandardEnvironment();

        // 设置配置文件
        environment.setActiveProfiles("dev");
        System.out.println("Active Profiles: " + String.join(", ", environment.getActiveProfiles()));

        // 添加配置文件
        environment.addActiveProfile("test");
        System.out.println("Updated Active Profiles: " + String.join(", ", environment.getActiveProfiles()));

        // 设置默认配置文件
        environment.setDefaultProfiles("default");
        System.out.println("Default Profiles: " + String.join(", ", environment.getDefaultProfiles()));

        // 获取系统属性
        Map<String, Object> systemProperties = environment.getSystemProperties();
        System.out.println("System Properties: " + systemProperties);

        // 获取系统环境变量
        Map<String, Object> systemEnvironment = environment.getSystemEnvironment();
        System.out.println("System Environment: " + systemEnvironment);

        // 合并环境变量
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");
        StandardEnvironment standardEnvironment = new StandardEnvironment();
        standardEnvironment.getPropertySources().addFirst(new MapPropertySource("myEnvironment", properties));
        environment.merge(standardEnvironment);

        // 获取可变属性源
        MutablePropertySources propertySources = environment.getPropertySources();
        System.out.println("MutablePropertySources: " + propertySources);
    }
}
