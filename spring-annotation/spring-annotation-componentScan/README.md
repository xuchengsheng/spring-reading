## @ComponentScan

- [@ComponentScan](#componentscan)
  - [一、基本信息](#一基本信息)
  - [二、注解描述](#二注解描述)
  - [三、注解源码](#三注解源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/132346179) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@ComponentScan源码](https://github.com/xuchengsheng/spring-reading/blob/master/spring-annotation/spring-annotation-componentScan/README.md)

### 二、注解描述

`@ComponentScan` 注解，用于自动扫描特定包（和其子包）中的组件，并自动注册为 Spring 容器中的 bean。当我们使用 Spring Boot，它默认会扫描主应用程序所在的包以及子包。但是，如果我们需要更细粒度的控制，或者我们在使用传统的 Spring 而非 Spring Boot，那么我们可能会明确地使用 `@ComponentScan`。

### 三、注解源码

`@ComponentScan`注解是 Spring 框架自 3.1 版本开始引入的一个核心注解，用于指导如何扫描组件。与 `@Configuration` 配合使用，其功能与 Spring XML 的 `<context:component-scan>` 类似。除了允许指定要扫描的包，它还提供了多种属性，如命名生成器、范围解析器、代理设置等，以精细地控制组件的扫描和注册过程。若不指定扫描包，它默认从注解声明的位置开始。与此同时，`@Filter` 注解定义了类型过滤器，特别用于 `@ComponentScan` 中的组件包含和排除设置。它允许基于特定类型、类或模式来筛选组件。

```java
/**
 * 配置 @Configuration 类使用的组件扫描指令。
 * 提供与 Spring XML 的 <context:component-scan> 元素相似的支持。
 *
 * 可以指定 #basePackageClasses 或 #basePackages (或其别名
 * #value }) 来定义要扫描的特定包。如果没有定义特定的包，
 * 则从声明此注解的类的包开始扫描。
 *
 * 注意，<context:component-scan> 元素有一个
 * annotation-config 属性; 但是，此注解没有。这是因为
 * 在几乎所有使用 @ComponentScan 的情况下，默认的注解配置
 * 处理（例如处理 @Autowired 及其朋友们）都是预期的。此外，
 * 使用 AnnotationConfigApplicationContext 时，总是会注册注解配置处理器，
 * 这意味着在 @ComponentScan 级别尝试禁用它们都会被忽略。
 *
 * 有关使用示例，请参见 Configuration @Configuration 的 Javadoc。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 * @see Configuration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {
    
    /**
	 * #basePackages 的别名。
	 * 如果不需要其他属性，则允许更简洁的注解声明，例如，@ComponentScan("org.my.pkg")
	 * 而不是 @ComponentScan(basePackages = "org.my.pkg")。
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * 扫描带注解的组件的基础包。
	 * #value 是此属性的别名（且与此属性互斥）。
	 * 使用 #basePackageClasses 作为基于类型安全的替代方法
	 * 来指定要扫描注解的组件的包。将扫描每个指定类的包。
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * 指定要扫描的包的类型安全替代方法。每个指定类的包都会被扫描。
	 * 考虑在每个包中创建一个特殊的无操作标记类或接口，
	 * 除了被此属性引用之外，没有其他用途。
	 */
	Class<?>[] basePackageClasses() default {};
    
    /**
	 * 在Spring容器内为检测到的组件命名的 BeanNameGenerator 类。
	 * BeanNameGenerator 接口的默认值表明处理此 @ComponentScan 注解的扫描器
	 * 应使用它的继承的bean命名生成器，例如默认的
	 * AnnotationBeanNameGenerator 或在启动时提供给应用上下文的任何自定义实例。
	 */
	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

	/**
	 * 用于解析检测到的组件范围的 ScopeMetadataResolver。
	 */
	Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

	/**
	 * 指示是否应为检测到的组件生成代理，这在以代理风格使用范围时可能是必要的。
	 * 默认值是延迟到执行实际扫描的组件扫描器的默认行为。
	 * 注意，设置此属性会覆盖为 #scopeResolver 设置的任何值。
	 */
	ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

	/**
	 * 控制适用于组件检测的类文件。
	 * 考虑使用 #includeFilters 和 #excludeFilters
	 * 来采用更灵活的方法。
	 */
	String resourcePattern() default ClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;

	/**
	 * 指示是否应启用使用 @Component @Repository, @Service, 或 @Controller 注解的类的自动检测。
	 */
	boolean useDefaultFilters() default true;

	/**
	 * 指定哪些类型有资格进行组件扫描。
	 * 进一步从 #basePackages 中的所有内容缩小到匹配给定过滤器或过滤器的基包中的所有内容。
	 * 注意，这些过滤器将附加到默认过滤器（如果指定）。即使它与默认过滤器不匹配（例如，没有使用 @Component 注解），
	 * 任何匹配给定过滤器的基包下的类型都将被包括。
	 */
	Filter[] includeFilters() default {};

	/**
	 * 指定哪些类型不适合进行组件扫描。
	 */
	Filter[] excludeFilters() default {};

	/**
	 * 指定是否应注册扫描的beans以进行延迟初始化。
	 * 默认值是 false；如果需要，切换为 true。
	 */
	boolean lazyInit() default false;

	/**
	 * 声明用作 ComponentScan#includeFilters include filter 或 
	 * ComponentScan#excludeFilters exclude filter 的类型过滤器。
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({})
	@interface Filter {

		/**
		 * 要使用的过滤器类型。
		 * 默认为 FilterType#ANNOTATION。
		 * @see #classes
		 * @see #pattern
		 */
		FilterType type() default FilterType.ANNOTATION;

		/**
		 * #classes 的别名。
		 */
		@AliasFor("classes")
		Class<?>[] value() default {};

		/**
		 * 用作过滤器的类或类。
		 * 根据 #type 属性的配置值，以下表格解释了如何解释这些类
		 * ...
		 * 这部分包含了一个表格和其它详细说明，由于格式限制，需要额外的处理来适应中文文档
		 * ...
		 */
		@AliasFor("value")
		Class<?>[] classes() default {};

		/**
		 * 用作过滤器的模式（或模式），作为指定类 #value 的替代。
		 * 如果 #type 设置为 FilterType#ASPECTJ ASPECTJ，这是一个 AspectJ 类型模式表达式。
		 * 如果 #type 设置为 FilterType#REGEX REGEX，这是一个正则模式，用于匹配完全限定的类名。
		 */
		String[] pattern() default {};

	}
}

```

`ScopedProxyMode` 是一个枚举，定义了不同的作用域代理选项，用于决定如何为特定的作用域 bean 创建代理。作用域代理是 Spring 中一个高级特性，允许在不同的上下文中共享 bean 实例，如请求或会话。此枚举的主要用途是为这些作用域 bean 提供不同的代理机制。

```java
/**
 * 枚举各种作用域代理选项。
 *
 * 为了更完整地讨论什么是作用域代理，请查看 Spring 参考文档中标题为 '作为依赖的作用域 beans' 的部分。
 *
 * @author Mark Fisher
 * @since 2.5
 * @see ScopeMetadata
 */
public enum ScopedProxyMode {

	/**
	 * 默认通常等于 #NO，除非在组件扫描指令级别配置了不同的默认值。
	 */
	DEFAULT,

	/**
	 * 不创建一个作用域代理。
	 * <p>当与非单例作用域实例一起使用时，这种代理模式通常不太有用，如果要作为依赖项使用，
	 * 它应该优先使用 #INTERFACES 或 #TARGET_CLASS 代理模式。
	 */
	NO,

	/**
	 * 创建一个JDK动态代理，实现目标对象的类所暴露的所有接口。
	 */
	INTERFACES,

	/**
	 * 创建一个基于类的代理（使用CGLIB）。
	 */
	TARGET_CLASS

}
```

`FilterType` 是一个枚举，定义了与 `@ComponentScan` 注解结合使用时的不同类型过滤器选项。这些过滤器用于决定在组件扫描过程中哪些组件应被包括或排除。

```java
/**
 * 与 ComponentScan @ComponentScan 结合使用的类型过滤器的枚举。
 * 该枚举定义了在组件扫描过程中可以用于过滤组件的不同类型。
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.5
 * @see ComponentScan
 * @see ComponentScan#includeFilters()
 * @see ComponentScan#excludeFilters()
 * @see org.springframework.core.type.filter.TypeFilter
 */
public enum FilterType {

	/**
	 * 过滤带有指定注解的候选项。
	 * @see org.springframework.core.type.filter.AnnotationTypeFilter
	 */
	ANNOTATION,

	/**
	 * 过滤可以赋值给指定类型的候选项。
	 * @see org.springframework.core.type.filter.AssignableTypeFilter
	 */
	ASSIGNABLE_TYPE,

	/**
	 * 过滤与指定的AspectJ类型模式表达式匹配的候选项。
	 * @see org.springframework.core.type.filter.AspectJTypeFilter
	 */
	ASPECTJ,

	/**
	 * 过滤与指定的正则表达式模式匹配的候选项。
	 * @see org.springframework.core.type.filter.RegexPatternTypeFilter
	 */
	REGEX,

	/**
	 * 使用给定的自定义 org.springframework.core.type.filter.TypeFilter 实现来过滤候选项。
	 */
	CUSTOM

}
```

### 四、主要功能

1. **指定扫描的包**
   + 通过 `basePackages` 和 `basePackageClasses` 属性，用户可以明确告诉 Spring 在哪些包中查找带有 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类。

2. **自动扫描**
   + 如果用户没有明确指定要扫描的包，则默认从声明 `@ComponentScan` 的类所在的包开始进行扫描。

3. **过滤扫描的组件**
   + 通过 `includeFilters` 和 `excludeFilters` 属性，用户可以更精细地控制哪些组件应被扫描或排除。

4. **其他配置**
   + 此注解还提供了其他属性，如 `nameGenerator`（为检测到的组件命名）、`scopeResolver`（解析组件的范围）、`scopedProxy`（是否为组件生成代理）等，以提供更高级的配置。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。在初始化上下文后，该程序会遍历并打印所有在 Spring 容器中定义的 beans 的名字。

```java
public class ComponentScanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

在`MyConfiguration`类中，Spring 扫描 `com.xcs.spring` 包及其子包，包括所有 `SpecialComponent` 类型的组件，但排除所有 `AdminService` 类型的组件。

```java
@Configuration
@ComponentScan(
        basePackages = "com.xcs.spring",
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SpecialComponent.class),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AdminService.class)
)
public class MyConfiguration {

}
```

 `UserRepository` 的类，位于 `com.xcs.spring.repository` 包中，并用 `@Repository` 注解标记。

```java
package com.xcs.spring.repository;

@Repository
public class UserRepository {

}
```

`AdminService` 和 `UserService`，它们都位于 `com.xcs.spring.service` 包中并分别用 `@Service` 注解标记。

```java
package com.xcs.spring.service;

@Service
public class AdminService {

}

@Service
public class UserService {

}
```

`SpecialComponent` 的类，它位于 `com.xcs.spring.special` 包中，没有使用spring中的任何注解标记。

```java
package com.xcs.spring.special;

public class SpecialComponent {
    
}
```

运行结果发现，`UserRepository` 将被自动检测并注册为一个 Spring bean，因为它位于我们指定的 `com.xcs.spring` 包路径下。`UserService` 将被自动检测并注册为一个 Spring bean，因为它位于我们指定的 `com.xcs.spring` 包路径下。但是，由于 `@ComponentScan` 配置中使用了 `excludeFilters` 明确排除了 `AdminService`，所以即使 `AdminService` 位于 `com.xcs.spring` 包路径下，它也不会被注册为一个 Spring bean。虽然`SpecialComponent` 类是一个没有任何 Spring 注解的普通 Java 类。但通过使用 `@ComponentScan` 的 `includeFilters` 和 `FilterType.ASSIGNABLE_TYPE`，我们可以强制 Spring 上下文扫描并注册它为一个 bean，即使它没有标记为 `@Component` 或其他 Spring 注解。

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myConfiguration
beanName = userRepository
beanName = userService
beanName = specialComponent
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: @ComponentScan注解时序图
ComponentScanApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()
AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)
AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup) 
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor:postProcessBeanDefinitionRegistry(registry)
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor:processConfigBeanDefinitions(registry)
ConfigurationClassPostProcessor->>ConfigurationClassParser:ConfigurationClassParser(...)
ConfigurationClassParser-->>ConfigurationClassPostProcessor:返回解析解析器
ConfigurationClassPostProcessor->>ConfigurationClassParser:parser.parse(candidates)
ConfigurationClassParser->>ConfigurationClassParser:parse(metadata, String beanName)
ConfigurationClassParser->>ConfigurationClassParser:processConfigurationClass(configClass,filter)
ConfigurationClassParser->>ConfigurationClassParser:doProcessConfigurationClass(configClass,sourceClass,filter)
ConfigurationClassParser->>ComponentScanAnnotationParser:parse(componentScan,declaringClass)
ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner:ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment,resourceLoader) 
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerDefaultFilters()
ClassPathBeanDefinitionScanner-->>ComponentScanAnnotationParser:返回扫描器
ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner:doScan(basePackages)
ClassPathBeanDefinitionScanner->>ClassPathScanningCandidateComponentProvider:findCandidateComponents(basePackage)
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider:scanCandidateComponents(basePackage)
ClassPathScanningCandidateComponentProvider-->>ClassPathBeanDefinitionScanner:返回BeanDefinition
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerBeanDefinition(definitionHolder,registry)
ClassPathBeanDefinitionScanner->>BeanDefinitionReaderUtils:registerBeanDefinition(definitionHolder, registry)
BeanDefinitionReaderUtils->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)
ClassPathBeanDefinitionScanner-->>ComponentScanAnnotationParser:返回BeanDefinition
~~~

### 七、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。在初始化上下文后，该程序会遍历并打印所有在 Spring 容器中定义的 beans 的名字。

```java
public class ComponentScanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们重点关注`refresh()`方法。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

在`org.springframework.context.support.AbstractApplicationContext#refresh`方法中我们重点关注一下`finishBeanFactoryInitialization(beanFactory)`这方法会对实例化所有剩余非懒加载的单列Bean对象，其他方法不是本次源码阅读的重点暂时忽略。

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
     // ... [代码部分省略以简化]
     // 调用在上下文中注册为bean的工厂处理器
     invokeBeanFactoryPostProcessors(beanFactory);
     // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors`方法中，又委托了`PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`进行调用。

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`方法中，首先调用了 `BeanDefinitionRegistryPostProcessor`（这是 `BeanFactoryPostProcessor` 的子接口）。它专门用来在所有其他 bean 定义加载之前修改默认的 bean 定义。

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
    // ... [代码部分省略以简化]
    invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
	// ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors`方法中，循环调用了实现`BeanDefinitionRegistryPostProcessor`接口中的`postProcessBeanDefinitionRegistry(registry)`方法

```java
private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry, ApplicationStartup applicationStartup) {

    for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
        StartupStep postProcessBeanDefRegistry = applicationStartup.start("spring.context.beandef-registry.post-process")
            .tag("postProcessor", postProcessor::toString);
        postProcessor.postProcessBeanDefinitionRegistry(registry);
        postProcessBeanDefRegistry.end();
    }
}
```

在`org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`方法中，调用了`processConfigBeanDefinitions`方法，该方法的主要目的是处理和注册配置类中定义的beans。

```java
@Override
public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    // ... [代码部分省略以简化]
    processConfigBeanDefinitions(registry);
}
```

在`org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions`方法中，这个方法主要处理了配置类的解析和验证，并确保了所有在配置类中定义的beans都被正确地注册到Spring的bean定义注册表中。

```java
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    // ... [代码部分省略以简化]

    // 步骤1：创建一个用于解析配置类的解析器
    ConfigurationClassParser parser = new ConfigurationClassParser(
            this.metadataReaderFactory, this.problemReporter, this.environment,
            this.resourceLoader, this.componentScanBeanNameGenerator, registry);

    // 步骤2：初始化候选配置类集合以及已解析配置类集合
    Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
    Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());

    // 步骤3：循环处理所有候选配置类，直至没有候选类为止
    do {
        // 步骤3.1 解析配置类
        parser.parse(candidates);
        // 步骤3.2 验证配置类
        parser.validate();

        // 获取解析后的配置类，并从中移除已经处理过的
        Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
        configClasses.removeAll(alreadyParsed);

        // 步骤4：如果reader为空，则创建一个新的Bean定义读取器
        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(
                    registry, this.sourceExtractor, this.resourceLoader, this.environment,
                    this.importBeanNameGenerator, parser.getImportRegistry());
        }

        // 步骤5：使用读取器为解析的配置类加载Bean定义
        this.reader.loadBeanDefinitions(configClasses);

        // ... [代码部分省略以简化]
        
    } while (!candidates.isEmpty());

    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#parse`方法中，主要是遍历所有的配置类候选者，并对每一个带有注解的Bean定义进行解析。这通常涉及到查找该配置类中的@Bean方法、组件扫描指令等，并将这些信息注册到Spring容器中。

```java
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    // ... [代码部分省略以简化]
    parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#parse(metadata, beanName)`方法中，将注解元数据和Bean名称转化为一个配置类，然后对其进行处理。处理配置类是Spring配置驱动的核心，它涉及到许多关键操作，如处理`@ComponentScan`注解等等。

```java
protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
    processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass`方法中，处理一个给定的配置类。它首先递归地处理配置类及其父类，以确保所有相关的配置都被正确地读取并解析。在递归处理完所有相关配置后，它将配置类添加到已解析的配置类的映射中。

```java
protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
    // ... [代码部分省略以简化]

    // 步骤1：递归地处理配置类及其超类层次结构
    SourceClass sourceClass = asSourceClass(configClass, filter);
    do {
        sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
    } while (sourceClass != null);

    // 步骤2：将处理后的配置类放入映射中
    this.configurationClasses.put(configClass, configClass);
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass`方法中，这个方法的目标是处理和解析标有 `@Configuration` 的类，执行组件扫描，并确保所有相关的配置类都被递归地解析。

```java
@Nullable
protected final SourceClass doProcessConfigurationClass(
    ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
    throws IOException {
    
    // ... [代码部分省略以简化]

    // 处理任何 @ComponentScan 注解
    // 获取当前类（sourceClass）的所有 @ComponentScan 和 @ComponentScans 注解的属性
    Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
        sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);

    // 如果存在 @ComponentScan 或 @ComponentScans 注解，并且该类没有被条件评估排除
    if (!componentScans.isEmpty() &&
        !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
        // 遍历每一个 @ComponentScan 注解
        for (AnnotationAttributes componentScan : componentScans) {
            // 对标有 @ComponentScan 的配置类进行立即扫描
            Set<BeanDefinitionHolder> scannedBeanDefinitions =
                this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());

            // 检查扫描到的定义中是否有任何进一步的配置类，如果需要，则递归解析
            for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
                BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
                if (bdCand == null) {
                    bdCand = holder.getBeanDefinition();
                }
                // 检查 BeanDefinition 是否是一个配置类的候选者
                if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
                    // 如果是，递归解析它
                    parse(bdCand.getBeanClassName(), holder.getBeanName());
                }
            }
        }
    }
    
    // ... [代码部分省略以简化]

    // 没有父类 -> 处理完成
    return null;
}
```

在`org.springframework.context.annotation.ComponentScanAnnotationParser#parse`方法中，主要目的是为 `@ComponentScan` 配置的类提供了详细的处理，并指导了如何根据给定的属性配置和执行组件扫描。

```java
public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {
    // 步骤1. 创建一个新的扫描器
    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this.registry,
             componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);

    // 步骤2. 根据nameGenerator属性设置Bean名称生成器
    Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
    boolean useInheritedGenerator = (BeanNameGenerator.class == generatorClass);
    scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator :
                                 BeanUtils.instantiateClass(generatorClass));

    // 步骤3. 设置作用域代理模式或者作用域元数据解析器
    ScopedProxyMode scopedProxyMode = componentScan.getEnum("scopedProxy");
    if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
        scanner.setScopedProxyMode(scopedProxyMode);
    }
    else {
        Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
        scanner.setScopeMetadataResolver(BeanUtils.instantiateClass(resolverClass));
    }

    // 步骤4. 设置资源模式
    scanner.setResourcePattern(componentScan.getString("resourcePattern"));

    // 步骤5. 根据includeFilters和excludeFilters属性添加类型过滤器
    for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
        for (TypeFilter typeFilter : typeFiltersFor(filter)) {
            scanner.addIncludeFilter(typeFilter);
        }
    }
    for (AnnotationAttributes filter : componentScan.getAnnotationArray("excludeFilters")) {
        for (TypeFilter typeFilter : typeFiltersFor(filter)) {
            scanner.addExcludeFilter(typeFilter);
        }
    }

    // 步骤6. 设置bean是否为懒加载
    boolean lazyInit = componentScan.getBoolean("lazyInit");
    if (lazyInit) {
        scanner.getBeanDefinitionDefaults().setLazyInit(true);
    }

    // 步骤7. 确定扫描器的基础包
    Set<String> basePackages = new LinkedHashSet<>();
    String[] basePackagesArray = componentScan.getStringArray("basePackages");
    for (String pkg : basePackagesArray) {
        String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg),
                                                               ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        Collections.addAll(basePackages, tokenized);
    }
    for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
        basePackages.add(ClassUtils.getPackageName(clazz));
    }
    if (basePackages.isEmpty()) {
        basePackages.add(ClassUtils.getPackageName(declaringClass));
    }

    // 步骤8. 确保声明@ComponentScan的类本身不被注册为bean
    scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
        @Override
        protected boolean matchClassName(String className) {
            return declaringClass.equals(className);
        }
    });
    
    // 步骤9. 使用配置好的扫描器执行实际的组件扫描
    return scanner.doScan(StringUtils.toStringArray(basePackages));
}
```

我们来到`org.springframework.context.annotation.ComponentScanAnnotationParser#parse`方法中的步骤1。在`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner`方法中，首先在这个构造方法初始化了一个新的`ClassPathBeanDefinitionScanner`对象，根据传入的参数决定是否使用默认过滤器，并设置了其环境和资源加载器。

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment, @Nullable ResourceLoader resourceLoader) {

    // 断言确保注册表不为空
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");

    // 将传入的BeanDefinitionRegistry赋值给成员变量registry
    this.registry = registry;

    // 根据useDefaultFilters决定是否注册默认的过滤器
    if (useDefaultFilters) {
        registerDefaultFilters();
    }

    // 设置扫描器的环境
    setEnvironment(environment);

    // 设置资源加载器
    setResourceLoader(resourceLoader);
}
```

在`org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#registerDefaultFilters`方法中，此方法主要用于注册默认的类型过滤器。它首先注册了用于查找带有`@Component`注解的类的过滤器。然后，它尝试注册两个JSR标准的注解过滤器：JSR-250的`@ManagedBean`和JSR-330的`@Named`。如果相关的类不在类路径上，那么这两个过滤器将不会被注册。

```java
protected void registerDefaultFilters() {
    // 添加一个过滤器来包括带有@Component注解的类
    this.includeFilters.add(new AnnotationTypeFilter(Component.class));

    // 获取当前类的类加载器
    ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
    
    try {
        // 尝试添加一个过滤器来包括带有JSR-250 'javax.annotation.ManagedBean'注解的类
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false));
        logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // 如果JSR-250 1.1 API（如Java EE 6中包含的）不可用，仅仅跳过
    }
    
    try {
        // 尝试添加一个过滤器来包括带有JSR-330 'javax.inject.Named'注解的类
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
        logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // 如果JSR-330 API不可用，仅仅跳过
    }
}
```

我们来到`org.springframework.context.annotation.ComponentScanAnnotationParser#parse`方法中的步骤9。在`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`方法中，主要目标是找到指定`basePackages`中所有的组件，并为它们创建 `BeanDefinition`。这些 `BeanDefinition` 之后会被 Spring 容器用来创建实际的 bean 实例。

```java
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    // 断言确保至少有一个基础包被指定
    Assert.notEmpty(basePackages, "At least one base package must be specified");

    // 用于保存找到的bean定义的集合
    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();

    // 遍历每个基础包
    for (String basePackage : basePackages) {
        // 步骤1. 在给定的基础包中找到所有候选的bean定义
        Set<BeanDefinition> candidates = findCandidateComponents(basePackage);

        // 遍历找到的bean定义
        for (BeanDefinition candidate : candidates) {
            // 步骤2. 解析bean的作用域元数据
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            // 设置bean的作用域
            candidate.setScope(scopeMetadata.getScopeName());
            // 步骤3. 生成bean的名字
            String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);

            // 步骤4. 如果是AbstractBeanDefinition，进行后处理
            if (candidate instanceof AbstractBeanDefinition) {
                postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
            }

            // 步骤5. 如果是AnnotatedBeanDefinition，处理常见的注解定义
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }

            // 步骤6. 检查给定的bean名字是否已经存在，如果不存在，进行注册
            if (checkCandidate(beanName, candidate)) {
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                // 步骤7. 应用作用域代理模式，如有必要为bean创建代理
                definitionHolder =
                    AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                // 将bean定义加入集合中
                beanDefinitions.add(definitionHolder);
                // 步骤8. 在bean注册表中注册bean定义
                registerBeanDefinition(definitionHolder, this.registry);
            }
        }
    }
    // 返回所有注册的bean定义
    return beanDefinitions;
}
```

我们来到`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`方法中的步骤1。在`org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents`方法中，主要提供了两种方式查找组件：通过预先生成的索引（如果可用且支持）或通过传统的扫描方式（我们重点关注传统的扫描方式）。

```java
public Set<BeanDefinition> findCandidateComponents(String basePackage) {
    // 如果存在组件索引并且支持include过滤器
    if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
        // 从索引中添加候选组件
        return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
    } 
    else {
        // 扫描给定基础包中的候选组件
        return scanCandidateComponents(basePackage);
    }
}
```

在`org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#scanCandidateComponents`方法中，首先是构建搜索路径，用于在类路径中搜索指定包，然后是扫描类路径，获取匹配的资源（通常是 `.class` 文件），再然后是对于每个资源，检查是否是候选组件，例如是否有 `@Component` 注解，最后对于是候选组件的类，创建一个 `BeanDefinition` 对象并添加到结果集中。

```java
private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
    // 用于保存候选的Bean定义
    Set<BeanDefinition> candidates = new LinkedHashSet<>();
    try {
        // 构建包搜索路径，例如："classpath*:com/example/*"
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
            resolveBasePackage(basePackage) + '/' + this.resourcePattern;

        // 使用模式解析器获取所有匹配的资源（即.class文件）
        Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
        
        // ... [代码部分省略以简化]
        
        for (Resource resource : resources) {
            // ... [代码部分省略以简化]
            // 检查资源是否可读
            if (resource.isReadable()) {
                try {
                    // 使用元数据读取器获取类的元数据
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    // 检查类是否是候选组件（例如，是否带有@Component注释）
                    if (isCandidateComponent(metadataReader)) {
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setSource(resource);
                        // 再次检查Bean定义是否是候选组件
                        if (isCandidateComponent(sbd)) {
                            // ... [代码部分省略以简化]
                            candidates.add(sbd);
                        } 
                        else {
                            // ... [代码部分省略以简化]
                        }
                    } 
                    else {
                        // ... [代码部分省略以简化]
                    }
                }
                catch (Throwable ex) {
                    // ... [代码部分省略以简化]
                }
            } 
            else {
                // ... [代码部分省略以简化]
            }
        }
    }
    catch (IOException ex) {
        // ... [代码部分省略以简化]
    }
    return candidates;
}
```

在`org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#isCandidateComponent`方法中，首先确保类不在排除列表中，然后检查它是否在包含列表中，并确保它满足任何其他指定条件。

```java
protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
    // 遍历所有的排除过滤器
    for (TypeFilter tf : this.excludeFilters) {
        // 如果当前类与任一排除过滤器匹配，则直接返回false，说明不是候选组件
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            return false;
        }
    }

    // 遍历所有的包含过滤器
    for (TypeFilter tf : this.includeFilters) {
        // 如果当前类与任一包含过滤器匹配
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            // 判断该组件是否满足特定的条件
            return isConditionMatch(metadataReader);
        }
    }

    // 默认返回false，说明不是候选组件
    return false;
}
```

我们来到`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`方法中的步骤6。在org.springframework.context.annotation.ClassPathBeanDefinitionScanner#checkCandidate方法中，确保Spring容器中没有重名的、不兼容的bean定义。

```java
protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
    // 检查bean定义注册表中是否已包含给定名称的bean定义
    if (!this.registry.containsBeanDefinition(beanName)) {
        return true;  // 如果不存在相同名称的bean定义，则返回true
    }

    // 获取已存在的bean定义
    BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);

    // 获取原始的bean定义（如果有的话）
    BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
    if (originatingDef != null) {
        existingDef = originatingDef;
    }

    // 检查给定的bean定义与已存在的bean定义是否兼容
    if (isCompatible(beanDefinition, existingDef)) {
        return false;  // 如果它们是兼容的，则返回false
    }

    // 如果给定的bean定义与已存在的bean定义不兼容，则抛出异常
    throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName +
                                                 "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                                                 "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
}
```

我们来到`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`方法中的步骤8。在`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#registerBeanDefinition`方法中，主要调用 `BeanDefinitionReaderUtils` 类的 `registerBeanDefinition` 方法，用于实际的 `BeanDefinition` 注册过程。

```java
protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
	BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
}
```

在`org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition`方法中，主要用于将提供的 `BeanDefinitionHolder` 中的 `BeanDefinition` 及其所有别名注册到 `BeanDefinitionRegistry` 中。对于`@ComponentScan`的扫描和注册阶段而言，当`registerBeanDefinition`方法被调用时，已经完成了。但对于整个Spring容器的生命周期来说，还有其他重要的步骤将在后续发生，如bean的生命周期回调、bean的实例化、bean的初始化等。

```java
public static void registerBeanDefinition(
    BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
    throws BeanDefinitionStoreException {

    // 获取 bean 的主名称，并在 registry 中注册它
    String beanName = definitionHolder.getBeanName();
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

    // 如果提供了 bean 的别名，则注册这些别名
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

### 八、注意事项

**默认扫描**

+ 如果未指定具体的包，`@ComponentScan` 默认会扫描声明此注解的类所在的包及其子包。

**性能考虑**

+ 避免扫描不必要的包，因为这可能导致性能问题。尤其是在大型项目中，指定扫描的精确路径可以加速启动时间。

**默认过滤器**

+ 默认情况下，`@ComponentScan` 使用的过滤器会搜索带有 `@Component`, `@Service`, `@Repository`, 和 `@Controller` 的类。可以通过 `includeFilters` 和 `excludeFilters` 属性进行定制。

**冲突的 Bean 名称**

+ 确保没有重复的 Bean 名称，否则可能会导致 `BeanDefinitionStoreException`。

**使用 `basePackages` 和 `basePackageClasses`**

+ `basePackages` 允许我们指定要扫描的包的名称，而 `basePackageClasses` 允许我们指定一个或多个类，Spring 将扫描这些类所在的包。

**避免使用多个配置**

+ 不建议在同一个配置类中使用多个 `@ComponentScan`。如果确实需要，考虑使用 `@ComponentScans`。

**代理模式**

+ 考虑如何使用 `scopedProxy` 属性，特别是当我们使用非单例作用域的 beans 时。

**注解属性的覆盖**

+ 当多个 `@ComponentScan` 在多个配置类中定义时，后面的定义将覆盖前面的定义。这里需要我们自己去确认。

**对于大型项目，考虑使用模块化**

+ 在大型项目中，为了更好的管理和维护，可以考虑将应用分成多个模块，每个模块有其自己的配置类和 `@ComponentScan`。

### 九、总结

#### 最佳实践总结

1. **应用启动**
   + 在 `ComponentScanApplication` 的主方法中，使用 `AnnotationConfigApplicationContext` 初始化了 Spring 上下文，并将配置类 `MyConfiguration` 传递给它。这告诉 Spring 在 `MyConfiguration` 类中查找配置信息。
2. **配置类**
   + `MyConfiguration` 类被标记为 `@Configuration`，表明它是一个配置类。这个类进一步使用 `@ComponentScan` 注解指定了 Spring 应该在哪里寻找组件。具体来说，Spring 将扫描 `com.xcs.spring` 包及其所有子包。
3. **扫描规则**
   + 在 `@ComponentScan` 中，我们使用 `includeFilters` 明确指定 `SpecialComponent` 类被包含在 Spring 容器中，即使它没有使用任何 Spring 注解。同时，使用 `excludeFilters` 指定 `AdminService` 类不应该被 Spring 容器管理，即使它被标记为一个 `@Service`。
4. **组件类**：
   - `UserRepository` 类在 `com.xcs.spring.repository` 包中，并被标记为 `@Repository`，因此它自动被 Spring 容器管理。
   - `UserService` 类在 `com.xcs.spring.service` 包中，并被标记为 `@Service`，因此它也自动被 Spring 容器管理。
   - `AdminService` 虽然也被标记为 `@Service`，但由于 `@ComponentScan` 的 `excludeFilters` 配置，它没有被 Spring 容器管理。
   - `SpecialComponent` 类没有使用任何 Spring 注解，但由于 `@ComponentScan` 的 `includeFilters` 配置，它被 Spring 容器管理。
5. **运行结果**
   + 当应用启动时，所有被 Spring 容器管理的 beans 的名字都被打印出来，这包括了 `UserRepository`, `UserService`, 和 `SpecialComponent`。不包括 `AdminService`，因为它被排除了。

#### 源码分析总结

1. **应用启动**
   + 通过 `AnnotationConfigApplicationContext` 的构造方法，传入配置类 `MyConfiguration`，来启动Spring应用。
2. **刷新上下文**
   + 在构造方法内部，调用了 `refresh()` 方法开始执行容器的刷新操作。
3. **执行BeanFactory的后处理器**
   + `invokeBeanFactoryPostProcessors(beanFactory)` 方法被调用，它主要执行 `BeanDefinitionRegistryPostProcessor` 和 `BeanFactoryPostProcessor`。其中， `BeanDefinitionRegistryPostProcessor` 是在所有其他bean定义加载之前，用来修改默认的bean定义。
4. **处理配置类**
   + `ConfigurationClassPostProcessor` 是一个核心的后处理器，它会解析配置类（如带有 `@Configuration` 的类），找到 `@ComponentScan` 注解并解析它的属性，然后进行组件扫描。
5. **执行组件扫描**
   + 通过 `ComponentScanAnnotationParser` 类进行详细的扫描操作。它创建一个 `ClassPathBeanDefinitionScanner` 对象，设置其属性（如是否使用默认过滤器、资源加载器、作用域解析器、资源模式、包含和排除的过滤器等），然后扫描指定的基础包。
6. **扫描候选组件**
   + 对于每个基础包，它会查找所有的组件，并为这些组件创建 `BeanDefinition` 对象。
7. **注册Bean定义**
   + 找到的组件都会被注册到Spring容器中。这是通过调用 `registerBeanDefinition` 方法来完成的。如果在容器中已存在同名的bean定义，会进行冲突检查。
8. **完成组件扫描**
   + 当所有的基础包都被扫描完成，`@ComponentScan` 的操作就执行结束了。