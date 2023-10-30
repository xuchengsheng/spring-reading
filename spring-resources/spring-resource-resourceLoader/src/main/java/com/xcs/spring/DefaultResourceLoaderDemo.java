package com.xcs.spring;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * @author xcs
 * @date 2023年10月30日 17时22分
 **/
public class DefaultResourceLoaderDemo {
    public static void main(String[] args) {
        DefaultResourceLoader loader = new DefaultResourceLoader();

        // 从类路径加载资源
        Resource classpathResource = loader.getResource("classpath:application.properties");
        System.out.println("Classpath Exists= " + classpathResource.exists());

        // 加载文件系统中的资源
        Resource fileResource = loader.getResource("file:/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/myfile1.txt");
        System.out.println("File Exists = " + fileResource.exists());
    }
}
