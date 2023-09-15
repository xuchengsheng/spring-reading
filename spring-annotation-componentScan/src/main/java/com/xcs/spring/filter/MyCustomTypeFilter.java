package com.xcs.spring.filter;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * @author 林雷
 * @date 2023年08月15日 14时15分
 **/
public class MyCustomTypeFilter implements TypeFilter {
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return metadataReader.getClassMetadata().getClassName().endsWith("Custom");
    }
}
