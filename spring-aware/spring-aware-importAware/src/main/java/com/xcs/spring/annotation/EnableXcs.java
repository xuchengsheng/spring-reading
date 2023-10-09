package com.xcs.spring.annotation;

import com.xcs.spring.config.MyImportAware;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MyImportAware.class)
public @interface EnableXcs {
}
