package com.xcs.spring;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023年11月30日 16时40分
 **/
public class ConfigurablePropertyResolverDemo {

    public static void main(String[] args) {
        // 创建属性源
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");

        MapPropertySource propertySource = new MapPropertySource("myPropertySource", properties);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(propertySource);

        // 创建 ConfigurablePropertyResolver
        PropertySourcesPropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        // 设置和获取转换服务
        ConfigurableConversionService conversionService = new DefaultConversionService();
        propertyResolver.setConversionService(conversionService);

        // 设置占位符前后缀
        propertyResolver.setPlaceholderPrefix("${");
        propertyResolver.setPlaceholderSuffix("}");

        // 设置默认值分隔符
        propertyResolver.setValueSeparator(":");

        // 设置未解析占位符的处理方式
        propertyResolver.setIgnoreUnresolvableNestedPlaceholders(true);

        // 设置并验证必需的属性
        propertyResolver.setRequiredProperties("app.name", "app.version");
        propertyResolver.validateRequiredProperties();

        // 读取属性
        String appName = propertyResolver.getProperty("app.name");
        String appVersion = propertyResolver.getProperty("app.version", String.class, "Unknown Version");
        System.out.println("获取属性 app.name: " + appName);
        System.out.println("获取属性 app.version: " + appVersion);
    }
}
