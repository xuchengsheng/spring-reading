package com.xcs.spring.config;

import com.xcs.spring.bean.MyBeanA;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xcs
 * @date 2023年08月28日 11时12分
 **/
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanA.class.getName()};
    }
}
