package com.xcs.spring.config;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

public class MyEmbeddedValueResolverAware implements EmbeddedValueResolverAware {

    private StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    public void resolve() {
        String resolvedValue = stringValueResolver.resolveStringValue("Hello, ${user.name:xcs}! Today is #{T(java.time.LocalDate).now().toString()}");
        System.out.println(resolvedValue);
    }
}
