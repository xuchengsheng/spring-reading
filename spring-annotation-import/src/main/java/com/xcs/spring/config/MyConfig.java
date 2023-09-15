package com.xcs.spring.config;

import com.xcs.spring.service.MyService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xcs
 * @date 2023年08月07日 16时25分
 **/
@Configuration
// 导入常规的 @Configuration 类
// @Import(UserConfig.class)

// 导入普通的组件类
// @Import(MyService.class)

// 使用ImportSelector
// @Import(CustomImportSelector.class)

// 使用 ImportBeanDefinitionRegistrar
// @Import(CustomRegistrar.class)

@Import({UserConfig.class, MyService.class, CustomImportSelector.class, CustomDeferredImportSelector.class, CustomRegistrar.class})
public class MyConfig {

}