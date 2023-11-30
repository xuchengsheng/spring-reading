package com.xcs.spring;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023年11月30日 15时27分
 **/
public class SimplePropertyResolverDemo {

    public static void main(String[] args) {
        // 创建属性源
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");
        properties.put("app.description", "This is a ${app.name} with version ${app.version}");

        MapPropertySource propertySource = new MapPropertySource("myPropertySource", properties);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(propertySource);

        // 使用 PropertySourcesPropertyResolver
        PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        // 获取属性
        String appName = propertyResolver.getProperty("app.name");
        String appVersion = propertyResolver.getProperty("app.version");
        System.out.println("获取属性 app.name: " + appName);
        System.out.println("获取属性 app.version: " + appVersion);

        // 检查属性是否存在
        boolean containsDescription = propertyResolver.containsProperty("app.description");
        boolean containsReleaseDate = propertyResolver.containsProperty("app.releaseDate");
        System.out.println("是否包含 'app.description' 属性: " + containsDescription);
        System.out.println("是否包含 'app.releaseDate' 属性: " + containsReleaseDate);

        // 带默认值的属性获取
        String appReleaseDate = propertyResolver.getProperty("app.releaseDate", "2023-11-30");
        System.out.println("带默认值的属性获取 app.releaseDate : " + appReleaseDate);

        // 获取必需属性
        String requiredAppName = propertyResolver.getRequiredProperty("app.name");
        System.out.println("获取必需属性 app.name: " + requiredAppName);

        // 解析占位符
        String appDescription = propertyResolver.resolvePlaceholders(properties.get("app.description").toString());
        System.out.println("解析占位符 app.description: " + appDescription);

        // 解析必需的占位符
        String requiredAppDescription = propertyResolver.resolveRequiredPlaceholders("App Description: ${app.description}");
        System.out.println("解析必需的占位符 : " + requiredAppDescription);
    }
}
