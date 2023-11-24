package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.metrics.ApplicationStartup;

import java.security.AccessControlContext;

/**
 * @author xcs
 * @date 2023年11月24日 13时56分
 **/
public class ConfigurableBeanFactoryDemo {

    public static void main(String[] args) {
        // 创建 ApplicationContext
        ConfigurableBeanFactory configurableBeanFactory = new AnnotationConfigApplicationContext(MyConfiguration.class).getBeanFactory();

        // 示例：设置父级 BeanFactory
        configurableBeanFactory.setParentBeanFactory(new DefaultListableBeanFactory());

        // 示例：设置 ClassLoader
        configurableBeanFactory.setBeanClassLoader(ConfigurableBeanFactoryDemo.class.getClassLoader());

        // 示例：设置临时 ClassLoader
        configurableBeanFactory.setTempClassLoader(Thread.currentThread().getContextClassLoader());

        // 示例：设置是否缓存 bean metadata
        configurableBeanFactory.setCacheBeanMetadata(true);

        // 示例：获取BeanPostProcessor数量
        int beanPostProcessorCount = configurableBeanFactory.getBeanPostProcessorCount();
        System.out.println("1.BeanPostProcessor 数量: " + beanPostProcessorCount);

        // 示例：获取所有已注册的 Scope 名称
        String[] scopeNames = configurableBeanFactory.getRegisteredScopeNames();
        System.out.println("2.已注册的 Scope 名称: " + String.join(", ", scopeNames));

        // 示例：获取注册的 Scope
        String customScopeName = "customScope";
        Scope customScope = configurableBeanFactory.getRegisteredScope(customScopeName);
        System.out.println("3." + customScopeName + " 对应的 Scope: " + customScope);

        // 示例：获取 ApplicationStartup
        ApplicationStartup applicationStartup = configurableBeanFactory.getApplicationStartup();
        System.out.println("4.ApplicationStartup: " + applicationStartup);

        // 示例：获取 AccessControlContext
        AccessControlContext accessControlContext = configurableBeanFactory.getAccessControlContext();
        System.out.println("5.AccessControlContext: " + accessControlContext);

        // 示例：拷贝配置
        ConfigurableListableBeanFactory otherFactory = new DefaultListableBeanFactory();
        configurableBeanFactory.copyConfigurationFrom(otherFactory);

        // 示例：注册别名
        String beanName = "myService";
        String alias = "helloService";
        configurableBeanFactory.registerAlias(beanName, alias);

        // 示例：解析别名
        configurableBeanFactory.resolveAliases(value -> value + "_resolved");

        // 示例：获取合并后的 BeanDefinition
        String mergedBeanName = "myService";
        BeanDefinition mergedBeanDefinition = configurableBeanFactory.getMergedBeanDefinition(mergedBeanName);
        System.out.println("6.合并后的 BeanDefinition: " + mergedBeanDefinition);

        // 示例：判断是否为 FactoryBean
        String factoryBeanName = "myService";
        boolean isFactoryBean = configurableBeanFactory.isFactoryBean(factoryBeanName);
        System.out.println("7." + factoryBeanName + " 是否为 FactoryBean: " + isFactoryBean);

        // 示例：设置当前 Bean 是否正在创建
        String currentBeanName = "myService";
        boolean inCreation = true;
        configurableBeanFactory.setCurrentlyInCreation(currentBeanName, inCreation);

        // 示例：判断指定的 Bean 是否正在创建
        boolean isCurrentlyInCreation = configurableBeanFactory.isCurrentlyInCreation(currentBeanName);
        System.out.println("8." + currentBeanName + " 是否正在创建: " + isCurrentlyInCreation);

        // 示例：注册依赖关系
        String dependentBeanName = "dependentBean";
        configurableBeanFactory.registerDependentBean(beanName, dependentBeanName);

        // 示例：获取所有依赖于指定 Bean 的 Bean 名称
        String[] dependentBeans = configurableBeanFactory.getDependentBeans(beanName);
        System.out.println("9." + beanName + " 的所有依赖 Bean 名称: " + String.join(", ", dependentBeans));

        // 示例：获取指定 Bean 依赖的所有 Bean 名称
        String[] dependencies = configurableBeanFactory.getDependenciesForBean(beanName);
        System.out.println("10." + beanName + " 依赖的所有 Bean 名称: " + String.join(", ", dependencies));

        // 销毁指定 Bean 实例
        Object beanInstance = configurableBeanFactory.getBean(beanName);
        configurableBeanFactory.destroyBean(beanName, beanInstance);

        // 销毁所有单例 Bean
        configurableBeanFactory.destroySingletons();

        // 示例：添加 PropertyEditorRegistrar
        // configurableBeanFactory.addPropertyEditorRegistrar(null);

        // 示例：注册自定义 PropertyEditor
        // configurableBeanFactory.registerCustomEditor(String.class, null);

        // 示例：添加 StringValueResolver
        // configurableBeanFactory.addEmbeddedValueResolver(null);

        // 示例：添加 BeanPostProcessor
        // configurableBeanFactory.addBeanPostProcessor(null);

        // 示例：注册 Scope
        // configurableBeanFactory.registerScope("customScope", null);

        // 示例：设置 ApplicationStartup
        // configurableBeanFactory.setApplicationStartup(null);

        // 示例：设置 Bean 表达式解析器
        // configurableBeanFactory.setBeanExpressionResolver(null);

        // 示例：设置 ConversionService
        // configurableBeanFactory.setConversionService(null);

        // 示例：设置 TypeConverter
        // configurableBeanFactory.setTypeConverter(null);
    }

}
