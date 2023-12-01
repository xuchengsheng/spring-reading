package com.xcs.spring;

import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author xcs
 * @date 2023年12月01日 16时12分
 **/
public class PropertySourceDemo {

    public static void main(String[] args) throws Exception {
        // 从 .properties 文件加载属性
        Properties source = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
        PropertySource propertySource = new PropertiesPropertySource("properties", source);

        // 直接从Resource加载属性
        ClassPathResource classPathResource = new ClassPathResource("application.properties");
        PropertySource resourcePropertySource = new ResourcePropertySource("resource", classPathResource);

        // 从Map加载属性
        Map<String, Object> map = new HashMap<>();
        map.put("app.name", "Spring-Reading");
        map.put("app.version", "1.0.0");
        PropertySource mapPropertySource = new MapPropertySource("mapSource", map);

        // 访问系统环境变量
        Map mapEnv = System.getenv();
        PropertySource envPropertySource = new SystemEnvironmentPropertySource("systemEnv", mapEnv);

        // 解析命令行参数
        String[] myArgs = {"--appName=MyApplication", "--port=8080"};
        PropertySource commandLinePropertySource = new SimpleCommandLinePropertySource(myArgs);

        // 组合多个 PropertySource 实例
        CompositePropertySource composite = new CompositePropertySource("composite");
        composite.addPropertySource(propertySource);
        composite.addPropertySource(resourcePropertySource);
        composite.addPropertySource(mapPropertySource);
        composite.addPropertySource(envPropertySource);
        composite.addPropertySource(commandLinePropertySource);

        // 打印结果
        for (PropertySource<?> ps : composite.getPropertySources()) {
            System.out.printf("Name: %-15s || Source: %s%n", ps.getName(), ps.getSource());
        }
    }
}
