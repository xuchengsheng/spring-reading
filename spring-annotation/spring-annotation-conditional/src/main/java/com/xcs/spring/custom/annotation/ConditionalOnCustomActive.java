package com.xcs.spring.custom.annotation;

import com.xcs.spring.custom.condition.CustomActiveCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(CustomActiveCondition.class)
public @interface ConditionalOnCustomActive {

}
