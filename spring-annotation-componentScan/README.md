## @ComponentScan

- [@ComponentScan](#componentscan)
  - [一、接口描述](#一接口描述)
  - [二、注解源码](#二注解源码)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [8.1、最佳实践总结](#81最佳实践总结)
    - [8.2、源码分析总结](#82源码分析总结)


### 一、接口描述

`@ComponentScan` 注解，用于自动扫描特定包（和其子包）中的组件，并自动注册为 Spring 容器中的 bean。当我们使用 Spring Boot，它默认会扫描主应用程序所在的包以及子包。但是，如果你需要更细粒度的控制，或者我们在使用传统的 Spring 而非 Spring Boot，那么我们可能会明确地使用 `@ComponentScan`。

### 二、注解源码

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

### 三、主要功能

**指定扫描的包**：通过 `basePackages` 和 `basePackageClasses` 属性，用户可以明确告诉 Spring 在哪些包中查找带有 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类。

**自动扫描**：如果用户没有明确指定要扫描的包，则默认从声明 `@ComponentScan` 的类所在的包开始进行扫描。

**过滤扫描的组件**：通过 `includeFilters` 和 `excludeFilters` 属性，用户可以更精细地控制哪些组件应被扫描或排除。

**其他配置**：此注解还提供了其他属性，如 `nameGenerator`（为检测到的组件命名）、`scopeResolver`（解析组件的范围）、`scopedProxy`（是否为组件生成代理）等，以提供更高级的配置。

### 四、最佳实践



### 五、时序图

### 六、源码分析

### 七、注意事项

### 八、总结

#### 8.1、最佳实践总结

#### 8.2、源码分析总结