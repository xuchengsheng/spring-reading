package com.xcs.spring.config;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

public class MyResourceLoaderAware implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void getResource(String location){
        try {
            Resource resource = resourceLoader.getResource(location);
            System.out.println("Resource content: " + new String(FileCopyUtils.copyToByteArray(resource.getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
