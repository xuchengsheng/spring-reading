package com.xcs.spring;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023年12月02日 10时29分
 **/
public class PropertySourcesDemo {

    public static void main(String[] args) {
        // 创建 MutablePropertySources 对象
        MutablePropertySources propertySources = new MutablePropertySources();

        // 创建两个 MapPropertySource 对象
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "value1");
        PropertySource<?> mapPropertySource1 = new MapPropertySource("config1", config1);

        Map<String, Object> config2 = new HashMap<>();
        config2.put("key2", "value2");
        PropertySource<?> mapPropertySource2 = new MapPropertySource("config2", config2);

        // 添加属性源到开头
        propertySources.addFirst(mapPropertySource1);
        // 添加属性源到末尾
        propertySources.addLast(mapPropertySource2);

        // 打印
        System.out.println("打印属性源");
        for (PropertySource<?> ps : propertySources) {
            System.out.printf("Name: %-10s || Source: %s%n", ps.getName(), ps.getSource());
        }
        System.out.println();

        // 替换属性源
        Map<String, Object> newConfig = new HashMap<>();
        newConfig.put("app.name", "Spring-Reading");
        newConfig.put("app.version", "1.0.0");
        PropertySource<?> newMapPropertySource = new MapPropertySource("config1", newConfig);
        propertySources.replace("config1", newMapPropertySource);

        // 打印替换后
        System.out.println("打印替换后的属性源");
        for (PropertySource<?> ps : propertySources) {
            System.out.printf("Name: %-10s || Source: %s%n", ps.getName(), ps.getSource());
        }
        System.out.println();

        // 检查是否包含属性源
        System.out.println("检查是否包含属性源 config2: " + propertySources.contains("config2"));

        // 移除属性源
        System.out.println("移除属性源 config2: " + propertySources.remove("config2"));

        // 再次检查是否包含属性源
        System.out.println("删除后是否包含属性源 config2: " + propertySources.contains("config2"));
    }
}
