package com.xcs.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xcs
 * @date 2023年08月07日 16时25分
 **/
@Configuration
@ComponentScan(basePackages = "com.xcs.spring")
// includeFilters 基于ANNOTATION过滤
// @ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class), useDefaultFilters = false)

// includeFilters 基于ASSIGNABLE_TYPE过滤
// @ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MyInterface.class), useDefaultFilters = false)

// includeFilters 基于CUSTOM过滤
// @ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = MyCustomTypeFilter.class), useDefaultFilters = false)

// includeFilters 基于REGEX过滤
// @ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*ComponentRegex"), useDefaultFilters = false)

// includeFilters 基于AspectJ过滤
// @ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "*..*AspectJ"), useDefaultFilters = false)

// excludeFilters 基于ANNOTATION过滤
// @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class))

// excludeFilters 基于ASSIGNABLE_TYPE过滤
// @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MyInterface.class))

// excludeFilters 基于CUSTOM过滤
// @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = MyCustomTypeFilter.class))

// excludeFilters 基于REGEX过滤
// @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*ComponentRegex"))

// excludeFilters 基于AspectJ过滤
// @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "*..*AspectJ"))
public class MyComponentScanConfig {

}
