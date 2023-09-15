package com.xcs.spring.config;

import com.xcs.spring.service.MyService1;
import com.xcs.spring.service.MyService2;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

/**
 * @author 林雷
 * @date 2023年08月14日 17时07分
 **/
public class MyCustomScopeMetadataResolver implements ScopeMetadataResolver {

    private AnnotationScopeMetadataResolver resolver = new AnnotationScopeMetadataResolver();

    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        ScopeMetadata scopeMetadata = new ScopeMetadata();

        // 检查Bean的名称是否为'MyService1'或'MyService2'，并据此设置相应的作用域。
        if (MyService1.class.getName().equals(definition.getBeanClassName()) ||
                MyService2.class.getName().equals(definition.getBeanClassName())) {
            scopeMetadata.setScopeName(BeanDefinition.SCOPE_PROTOTYPE);
            return scopeMetadata;
        }
        // 再次交由AnnotationScopeMetadataResolver解析否则会导致@Scope失效
        return resolver.resolveScopeMetadata(definition);
    }
}
