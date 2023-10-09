package com.xcs.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MyImportAware.class)
public class MyConfiguration {

}
