package com.xcs.spring;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.IOException;
import java.util.Properties;

/**
 * @author xcs
 * @date 2023年11月09日 15时38分
 **/
public class PropertiesPersisterDemo {

    public static void main(String[] args) throws IOException {
        Properties loadProperties = new Properties();

        PropertiesPersister propertiesPersister = ResourcePropertiesPersister.INSTANCE;
        propertiesPersister.load(loadProperties, new ClassPathResource("bean-definitions.properties").getInputStream());

        System.out.println("loadProperties = " + loadProperties);
    }
}
