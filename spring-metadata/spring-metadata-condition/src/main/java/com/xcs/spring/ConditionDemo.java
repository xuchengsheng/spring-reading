package com.xcs.spring;

import com.xcs.spring.condition.MyOnClassCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;

public class ConditionDemo {

    public static void main(String[] args) throws IOException {
        // 创建资源解析器，用于获取匹配指定模式的资源
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 创建MetadataReader工厂，用于读取类的元数据信息
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

        // 获取指定模式下的所有资源
        Resource[] resources = resolver.getResources("classpath*:com/xcs/spring/bean/**/*.class");

        // 创建自定义条件类的实例，用于条件匹配
        Condition condition = new MyOnClassCondition("com.xcs.spring.ConditionDemo1");

        // 遍历每个资源，判断是否满足自定义条件
        for (Resource resource : resources) {
            // 获取资源对应的元数据读取器
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

            // 判断资源是否满足自定义条件
            if (condition.matches(null, metadataReader.getAnnotationMetadata())) {
                System.out.println(resource.getFile().getName() + "满足条件");
            } else {
                System.out.println(resource.getFile().getName() + "不满足条件");
            }
        }
    }
}

