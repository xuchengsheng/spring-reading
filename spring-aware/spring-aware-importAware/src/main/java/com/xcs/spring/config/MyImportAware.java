package com.xcs.spring.config;

import com.xcs.spring.annotation.EnableXcs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportAware implements ImportAware {

    private AnnotationAttributes enableXcs;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableXcs = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableXcs.class.getName(), false));
        if (this.enableXcs == null) {
            throw new IllegalArgumentException(
                    "@EnableXcs is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    public String customBean() {
        return "This is a custom bean!";
    }
}
