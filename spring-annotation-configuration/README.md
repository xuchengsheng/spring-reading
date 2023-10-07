## @Configuration

- [@Configuration](#configuration)
  - [一、注解描述](#一注解描述)
  - [二、注解源码](#二注解源码)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [8.1、最佳实践总结](#81最佳实践总结)
    - [8.2、源码分析总结](#82源码分析总结)

### 一、注解描述

`@Configuration` 是 Spring 框架中提供的一个核心注解，它指示一个类声明了一个或多个 `@Bean` 定义方法，这些方法由 Spring 容器管理并执行，以便在运行时为 bean 实例化、配置和初始化对象。

### 二、注解源码

```java
/**
 * @Configuration 是一个核心注解，用于指示该类是一个 Spring 配置类，
 * 可能包含一个或多个 @Bean 定义方法。此注解与 XML 配置是等效的，但以编程方式
 * 提供了更多的类型安全和灵活性。它通常与 @Bean、@ComponentScan 和其他注解结合使用，
 * 为 Spring 应用程序上下文定义 beans 和配置。
 * 
 * 通过 @Component 注解，@Configuration 被视为一个组件，
 * 这意味着 Spring 的组件扫描可以自动检测和处理它。
 * 
 * 例如，当在应用程序上下文中注册 @Configuration 类时，
 * 该类中定义的 @Bean 方法将被解析，并在上下文中注册相应的 beans。
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Bean
 * @see Profile
 * @see Import
 * @see ImportResource
 * @see ComponentScan
 * @see Lazy
 * @see PropertySource
 * @see AnnotationConfigApplicationContext
 * @see ConfigurationClassPostProcessor
 * @see org.springframework.core.env.Environment
 * @see org.springframework.test.context.ContextConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

	/**
	 * 明确指定与 @Configuration 类相关的 Spring bean 定义的名称。
	 * 如果未指定（这是常见情况），则会自动生成 bean 的名称。
	 * 这个自定义名称仅在 @Configuration 类通过组件扫描被检测到，
	 * 或直接提供给 AnnotationConfigApplicationContext 时才有效。
	 * 如果 @Configuration 类以传统的 XML bean 定义方式注册，
	 * 则 bean 元素的名称/ID 会优先。
	 */
	@AliasFor(annotation = Component.class)
	String value() default "";

	/**
	 * 指定是否应代理 @Bean 方法，以强制执行 bean 生命周期行为。
	 * 例如，即使在用户代码中直接调用了 @Bean 方法，也要返回共享的单例 bean 实例。
	 * 这个特性需要方法拦截，通过在运行时生成的 CGLIB 子类来实现，
	 * 这带有一些限制，如配置类和其方法不能声明为 final。
	 * 
	 * 默认为 true，这允许在配置类内部通过直接方法调用进行"bean之间的引用"，
	 * 以及从另一个配置类对此配置的 @Bean 方法的外部调用。
	 * 如果这不是必需的，因为此特定配置的每个 @Bean 方法都是独立的，
	 * 并设计为容器使用的简单工厂方法，可以将此标志设置为 false 以避免 CGLIB 子类处理。
	 * 
	 * 关闭 bean 方法拦截实际上是独立处理 @Bean 方法，
	 * 就像在非 @Configuration 类上声明的那样，即 "@Bean 简易模式"。
	 * 在行为上，它等同于删除 @Configuration 注解。
	 */
	boolean proxyBeanMethods() default true;
}
```

### 三、主要功能

### 四、最佳实践

### 五、时序图

### 六、源码分析

### 七、注意事项

### 八、总结

#### 8.1、最佳实践总结

#### 8.2、源码分析总结