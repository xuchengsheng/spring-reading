package com.xcs.spring.config;

import com.xcs.spring.bean.MyBeanB;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author xcs
 * @date 2023年08月29日 11时08分
 **/
public class MyDeferredImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanB.class.getName()};
    }
}
