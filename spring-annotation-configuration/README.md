## @Configuration

- [@Configuration](#configuration)
  - [一、注解描述](#一注解描述)
  - [二、注解源码](#二注解源码)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
    - [4.1、proxyBeanMethods设置为true](#41proxybeanmethods设置为true)
    - [4.2、proxyBeanMethods设置为false](#42proxybeanmethods设置为false)
  - [五、时序图](#五时序图)
    - [5.1、初始化流程](#51初始化流程)
    - [5.2、注册流程](#52注册流程)
    - [5.3、增强流程](#53增强流程)
  - [六、源码分析](#六源码分析)
    - [6.1、初始化流程](#61初始化流程)
    - [6.2、注册流程](#62注册流程)
    - [6.3、增强流程](#63增强流程)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [8.1、最佳实践总结](#81最佳实践总结)
    - [8.2、源码分析总结](#82源码分析总结)
  - [九、常见问题](#九常见问题)
    - [9.2 @Configuration中full模式与lite模式如何选择？](#92-configuration中full模式与lite模式如何选择)
      - [9.2.1 Full 模式](#921-full-模式)
      - [9.2.2 Lite 模式](#922-lite-模式)
      - [9.2.3 如何选择](#923-如何选择)


### 一、注解描述

`@Configuration` 是 Spring 框架中提供的一个核心注解，它指示一个类声明了一个或多个 `@Bean` 定义方法，这些方法由 Spring 容器管理并执行，以便在运行时为 bean 实例化、配置和初始化对象。

### 二、注解源码

`@Configuration`注解是 Spring 框架自 3.0 版本开始引入的一个核心注解，标记一个类为 Spring 的配置类，该类可能包含一个或多个 `@Bean` 方法来定义和配置 Spring beans，其中一个`value` 属性允许用户明确指定与 `@Configuration` 类关联的 Spring bean 定义的名称。如果未指定，名称会自动生成，另外一个`proxyBeanMethods` 属性决定是否应代理 `@Bean` 方法来强制实施 bean 生命周期行为，如确保返回的是单例 bean 实例。

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

**Bean定义方法**：`@Configuration` 类中可以包含一个或多个使用 `@Bean` 注解的方法，这些方法用于创建和配置应用程序上下文中的 beans。

**代理支持**：当 `@Configuration` 中的 `proxyBeanMethods` 属性设置为 `true`（这是默认值）时，`@Bean` 方法会被代理以确保正确的 bean 生命周期。这允许在一个配置类中，一个 `@Bean` 方法调用另一个 `@Bean` 方法并返回单例 bean 实例。

**组件扫描**：由于 `@Configuration` 注解本身带有 `@Component` 注解，因此它可以被 Spring 的组件扫描机制自动检测。这意味着在启用组件扫描的应用程序上下文中，只需声明 `@Configuration` 类而无需明确注册。

**模块化和组合**：通过使用 `@Import` 注解，你可以将多个 `@Configuration` 类组合在一起，从而实现配置的模块化。此外，`@Profile` 注解可以与 `@Configuration` 一起使用，以提供基于环境或其他条件的配置。

**属性源和属性占位符**：`@Configuration` 类可以与 `@PropertySource` 注解结合使用，从而将属性文件的值导入 Spring 环境。这些值可以使用 `@Value` 注解或直接通过 `Environment` API 注入到 beans 中。

**与其他注解结合**：`@Configuration` 类通常与其他 Spring 注解（如 `@ComponentScan`、`@PropertySource` 等）结合使用，以提供全面的配置机制。

### 四、最佳实践

#### 4.1、proxyBeanMethods设置为true

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyConfiguration`类型的bean，最后调用了 `myBean` 方法两次，并将其结果打印到控制台。

```java
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);

        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());
    }
}
```

`MyConfiguration` 类定义了一个名为 `myBean` 的 bean，这个 bean 的类型是 `MyBean`。每次从 Spring 容器请求这个 bean 时，都会得到同一个 `MyBean` 的实例。

```java
// proxyBeanMethods默认就是true，此处不设置
@Configuration
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

`MyBean` 的简单类，它没有任何属性或方法。

```java
public class MyBean {

}
```

运行结果发现，两次对 `MyBean` 对象的引用，这两个引用具有相同的 hashcode (`@f736069`)，表示它们引用的是相同的对象，因为在 `MyConfiguration` 类中，`myBean()` 方法被 `@Bean` 注解标记，它默认返回单例对象。当我们在 `ConfigurationApplication` 的 `main` 方法中两次调用 `myBean()` 方法时，Spring 容器都返回相同的 `MyBean` 实例。

```java
com.xcs.spring.bean.MyBean@f736069
com.xcs.spring.bean.MyBean@f736069
```

#### 4.2、proxyBeanMethods设置为false

将 `proxyBeanMethods` 设置为 `false` 时，此代理行为被禁用。这意味着，如果你在配置类内部多次调用同一个 `@Bean` 方法，每次都会创建一个新的实例。

```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

运行结果发现，两次对 `MyBean` 对象的引用，这两个引用具有不相同的 hashcode (`@3b69e7d1`，`@815b41f`)，表示它们引用的是不相同的对象。因为我们在 `@Configuration` 注解设置了 `proxyBeanMethods = false`，并在 `ConfigurationApplication` 的 `main` 方法中两次调用 `myBean()` 方法，每次调用都会创建一个新的 `MyBean` 实例，这就是为什么你看到两个不同的 hashcodes。

```
com.xcs.spring.bean.MyBean@3b69e7d1
com.xcs.spring.bean.MyBean@815b41f
```

### 五、时序图

时序图主要分为三个关键步骤

#### 5.1、初始化流程

- 当 `AnnotationConfigApplicationContext` 被实例化时，它开始初始化过程。
- 在这个过程中，`AnnotatedBeanDefinitionReader` 会被创建。这个读取器负责解析带注解的类，并将其转化为 Spring 可理解的 `BeanDefinition`。
- 然后，Spring 的核心工具类 `AnnotationConfigUtils` 会注册一些默认的处理器，特别是 `ConfigurationClassPostProcessor`，这是处理 `@Configuration` 注解的核心类。

~~~mermaid
sequenceDiagram 
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>启动上下文
AnnotationConfigApplicationContext-->>ConfigurationApplication: 返回context<br>返回实例
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext()<br>构造函数
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: AnnotatedBeanDefinitionReader(registry)<br>创建读取器
AnnotatedBeanDefinitionReader-->>AnnotationConfigApplicationContext: 返回reader
AnnotatedBeanDefinitionReader-->>AnnotationConfigUtils: registerAnnotationConfigProcessors(registry)<br>注册处理器
AnnotationConfigUtils-->>AnnotationConfigUtils: registerAnnotationConfigProcessors(registry,source)<br>处理器注册
AnnotationConfigUtils-->>AnnotationConfigUtils: registerPostProcessor(registry,definition,beanName)<br>后置处理器
AnnotationConfigUtils-->>DefaultListableBeanFactory: registerBeanDefinition(beanName, beanDefinition)<br>注册Bean定义
~~~

#### 5.2、注册流程

- 使用 `AnnotationConfigApplicationContext` 的 `register` 方法，配置类（带有 `@Bean` 方法的类）会被注册到Spring上下文中。
- `AnnotatedBeanDefinitionReader` 负责解析这些配置类，并创建相应的 `BeanDefinition`。
- 这些 `BeanDefinition` 最后会在 `DefaultListableBeanFactory` 中被注册，该工厂是 Spring IOC 容器的核心部分，它管理所有的 beans 和其定义。

~~~mermaid
sequenceDiagram 
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>启动上下文
AnnotationConfigApplicationContext-->>ConfigurationApplication: 返回context<br>返回上下文实例
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: register(componentClasses)<br>注册组件类
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: register(componentClasses)<br>读取器注册类
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: registerBean(beanClass)<br>注册Bean类
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: doRegisterBean(beanClass,name,qualifiers, supplier,customizers)<br>执行Bean注册
AnnotatedBeanDefinitionReader-->>BeanDefinitionReaderUtils: registerBeanDefinition(definitionHolder,registry)<br>注册Bean定义
BeanDefinitionReaderUtils-->>DefaultListableBeanFactory: registerBeanDefinition(beanName,beanDefinition)<br>工厂存Bean定义
~~~

#### 5.3、增强流程

- 当容器开始刷新（通过 `refresh` 方法），它会启动 beans 的创建和初始化过程。
- 在这个过程中，所有的 `BeanFactoryPostProcessor` 会被触发，特别是 `ConfigurationClassPostProcessor`。
- `ConfigurationClassPostProcessor` 的主要职责是增强 `@Configuration` 类，确保 `@Bean` 方法的正确代理行为。它使用 `ConfigurationClassEnhancer` 来增强类，使其能够正确地管理 bean 的生命周期，并确保，例如，单例 beans 只被创建一次。

~~~mermaid
sequenceDiagram 
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>启动上下文
AnnotationConfigApplicationContext-->>ConfigurationApplication: 返回context<br>返回上下文实例
AnnotationConfigApplicationContext-->>AnnotationConfigApplicationContext: refresh<br>刷新容器
AnnotationConfigApplicationContext-->>AnnotationConfigApplicationContext: invokeBeanFactoryPostProcessors<br>触发后处理器
AnnotationConfigApplicationContext-->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>委派后处理
PostProcessorRegistrationDelegate-->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(postProcessors,beanFactory)<br>执行后处理
PostProcessorRegistrationDelegate-->>ConfigurationClassPostProcessor: postProcessBeanFactory(beanFactory)<br>处理配置类
ConfigurationClassPostProcessor-->>ConfigurationClassPostProcessor: enhanceConfigurationClasses(beanFactory)<br>增强配置类
ConfigurationClassPostProcessor-->>ConfigurationClassEnhancer: enhance(configClass,classLoader)<br>执行增强操作
ConfigurationClassEnhancer-->>ConfigurationClassEnhancer: createClass(enhancer)<br>创建增强类
ConfigurationClassEnhancer-->>ConfigurationClassPostProcessor: 增强后的Class类
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyConfiguration`类型的bean，最后调用了 `myBean` 方法两次，并将其结果打印到控制台。

```java
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);

        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    // 步骤1. 这个构造方法初始化了基本的配置读取器和类路径扫描器
    this();
    // 步骤2. 这个方法将这些类注册到 Spring 上下文中，这样它们可以被识别并进一步处理。
    register(componentClasses);
    // 步骤3. 这个方法触发整个Spring容器的启动过程
    refresh();
}
```

#### 6.1、初始化流程

我们首先来到`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中步骤1。在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`方法中，`AnnotationConfigApplicationContext` 的无参数构造函数中，初始化了 `AnnotatedBeanDefinitionReader` 和 `ClassPathBeanDefinitionScanner` 这两个核心组件。

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader`方法中，首先从注册表获取 `Environment`（配置环境的抽象）。如果没有获取到，它会创建一个新的环境，最后调用另一个构造函数来完成 `AnnotatedBeanDefinitionReader` 的实例化。

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
    this(registry, getOrCreateEnvironment(registry));
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry,environment)`方法中，首先是设置了内部的条件评估器 (`conditionEvaluator`)，条件评估器用于处理如 `@Conditional` 这样的注解。然后调用工具类 `AnnotationConfigUtils` 的 `registerAnnotationConfigProcessors` 方法来为注册表注册注解配置处理器，例如 `ConfigurationClassPostProcessor`，这是处理 `@Configuration` 注解的核心类。

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    Assert.notNull(environment, "Environment must not be null");
    this.registry = registry;
    this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
    AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry)`方法中，该方法直接调用另一个重载版本的 `registerAnnotationConfigProcessors`，传入的 `registry` 和一个 `null` 值作为第二个参数。

```java
public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
    registerAnnotationConfigProcessors(registry, null);
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry,source)`方法中，这个方法主要是确保了 `ConfigurationClassPostProcessor`被注册到指定的注册表中，从而保证了 `@Configuration` 注解及相关功能能够被正确处理。

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
			BeanDefinitionRegistry registry, @Nullable Object source) {

    // ... [代码部分省略以简化]

    Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);
    
    if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // ... [代码部分省略以简化]

    return beanDefs;
}
```

#### 6.2、注册流程

然后我们来到`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中步骤2。在org.springframework.context.annotation.AnnotationConfigApplicationContext#register方法中，主要是允许我们注册一个或多个组件类（例如，那些使用 `@Component`, `@Service`, `@Repository`, `@Controller`, `@Configuration` 等注解的类）到Spring容器。

```java
@Override
public void register(Class<?>... componentClasses) {
    Assert.notEmpty(componentClasses, "At least one component class must be specified");
    StartupStep registerComponentClass = this.getApplicationStartup().start("spring.context.component-classes.register")
        .tag("classes", () -> Arrays.toString(componentClasses));
    this.reader.register(componentClasses);
    registerComponentClass.end();
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#register`方法中，遍历每一个传入的组件类，并逐一调用另一个方法来完成实际的注册工作。

```java
public void register(Class<?>... componentClasses) {
    for (Class<?> componentClass : componentClasses) {
        registerBean(componentClass);
    }
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#registerBean(beanClass)`方法中，主要目的是快速注册一个 bean 类型，而不需要指定其他详细的配置或参数。

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`方法中，主要负责将给定的 bean 类型及其相关配置注册到Spring容器中。处理 bean 名称的生成、bean 定义的创建和注册，以及应用任何必要的代理模式。

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {

    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
    
    // ... [代码部分省略以简化]
    
    String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

    // ... [代码部分省略以简化]

    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
}
```

在`org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition`方法中，主要负责bean定义及其所有相关别名都被注册到指定的 `BeanDefinitionRegistry`。这是 Spring 容器内部使用的一个实用方法，用于确保 bean 定义和其别名都正确存储，从而可以在后续的容器生命周期中被正确访问和使用。

```java
public static void registerBeanDefinition(
    	BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
    	throws BeanDefinitionStoreException {

    // Register bean definition under primary name.
    String beanName = definitionHolder.getBeanName();
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

    // Register aliases for bean name, if any.
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

#### 6.3、增强流程

然后我们来到`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中步骤3。在`org.springframework.context.support.AbstractApplicationContext#refresh`方法中我们重点关注一下`finishBeanFactoryInitialization(beanFactory)`这方法会对实例化所有剩余非懒加载的单列Bean对象，其他方法不是本次源码阅读的重点暂时忽略。

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

在`org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`方法中，主要是处理Spring容器在启动时如何处理 `BeanFactoryPostProcessor` 的核心逻辑。

```java
public static void invokeBeanFactoryPostProcessors(
      ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
	// ... [代码部分省略以简化]
   	// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
	invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`方法中，遍历提供的 `BeanFactoryPostProcessor` 集合，其中主要方法`postProcessBeanFactory` 是一个允许我们介入并修改 `BeanFactory` 的扩展点。在此实现中增强通过 `@Configuration` 注解定义的配置类。

```java
private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

    for (BeanFactoryPostProcessor postProcessor : postProcessors) {
        StartupStep postProcessBeanFactory = beanFactory.getApplicationStartup().start("spring.context.bean-factory.post-process")
            .tag("postProcessor", postProcessor::toString);
        postProcessor.postProcessBeanFactory(beanFactory);
        postProcessBeanFactory.end();
    }
}
```

在`org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanFactory`方法中，主要目的是为 `@Configuration` 标注的类进行增强。

```java
@Override
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // ... [代码部分省略以简化]
    enhanceConfigurationClasses(beanFactory);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.ConfigurationClassPostProcessor#enhanceConfigurationClasses`方法中，主要是对标记为 `@Configuration` 的类进行增强，确保它们的 `@Bean` 方法在每次调用时都返回相同的实例（除非它们是原型作用域的）。这是通过使用 `ConfigurationClassEnhancer` 来创建代理类实现的。这种代理确保了 Spring IoC 容器的正确行为，尤其是对于配置类。

```java
public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
    // ... [代码部分省略以简化]

    ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
    for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
        AbstractBeanDefinition beanDef = entry.getValue();
        // If a @Configuration class gets proxied, always proxy the target class
        beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
        // Set enhanced subclass of the user-specified bean class
        Class<?> configClass = beanDef.getBeanClass();
        Class<?> enhancedClass = enhancer.enhance(configClass, this.beanClassLoader);
        if (configClass != enhancedClass) {
            // ... [代码部分省略以简化]
            beanDef.setBeanClass(enhancedClass);
        }
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.ConfigurationClassEnhancer#enhance`方法中，首先是查看该类还没有被增强，则创建一个新的增强器并生成一个代理类；如果它已经被增强，那么直接返回原始类。这种增强确保了 `@Configuration` 类中的 `@Bean` 方法在每次调用时都返回相同的实例。

```java
public Class<?> enhance(Class<?> configClass, @Nullable ClassLoader classLoader) {
    if (EnhancedConfiguration.class.isAssignableFrom(configClass)) {
        // ... [代码部分省略以简化]
        return configClass;
    }
    Class<?> enhancedClass = createClass(newEnhancer(configClass, classLoader));
    // ... [代码部分省略以简化]
    return enhancedClass;
}
```

在`org.springframework.context.annotation.ConfigurationClassEnhancer#newEnhancer`方法中，主要负责为给定的配置类创建一个用于生成代理类的 `Enhancer` 对象。

```java
private Enhancer newEnhancer(Class<?> configSuperClass, @Nullable ClassLoader classLoader) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(configSuperClass);
    enhancer.setInterfaces(new Class<?>[] {EnhancedConfiguration.class});
    enhancer.setUseFactory(false);
    enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
    enhancer.setStrategy(new BeanFactoryAwareGeneratorStrategy(classLoader));
    enhancer.setCallbackFilter(CALLBACK_FILTER);
    enhancer.setCallbackTypes(CALLBACK_FILTER.getCallbackTypes());
    return enhancer;
}
```

在`org.springframework.context.annotation.ConfigurationClassEnhancer#createClass`方法中，使用提供的 `Enhancer` 对象来创建增强后的类，并为这个类注册静态回调。

```java
private Class<?> createClass(Enhancer enhancer) {
    Class<?> subclass = enhancer.createClass();
    // Registering callbacks statically (as opposed to thread-local)
    // is critical for usage in an OSGi environment (SPR-5932)...
    Enhancer.registerStaticCallbacks(subclass, CALLBACKS);
    return subclass;
}
```

在`org.springframework.context.annotation.ConfigurationClassEnhancer#createClass`方法中，使用了一个名为 `CALLBACKS` 的静态常量数组，它包含了三个回调对象。这些回调对象在CGLIB库中用于拦截和处理增强（代理）类的方法调用。

```java
private static final Callback[] CALLBACKS = new Callback[] {
    new BeanMethodInterceptor(),
    new BeanFactoryAwareMethodInterceptor(),
    NoOp.INSTANCE
};
```

在`org.springframework.context.annotation.ConfigurationClassEnhancer.BeanMethodInterceptor#intercept`方法中，主要是对 `@Bean` 方法的拦截逻辑，确保它们在被调用时总是返回正确的bean实例。这是通过结合检查当前方法、解析bean名称、处理特殊情况（如 `FactoryBeans` 或作用域代理）以及从bean工厂解析实际的bean引用来实现的。

```java
public Object intercept(Object enhancedConfigInstance, Method beanMethod, Object[] beanMethodArgs,
				MethodProxy cglibMethodProxy) throws Throwable {

	// 获取关联的 BeanFactory 通过反射读取了代理类中的$$beanFactory字段
	ConfigurableBeanFactory beanFactory = getBeanFactory(enhancedConfigInstance);
	// 确定当前 @Bean 方法对应的 bean 名称
	String beanName = BeanAnnotationHelper.determineBeanNameFor(beanMethod);

	// 检查当前的 @Bean 方法是否定义了一个作用域代理
	if (BeanAnnotationHelper.isScopedProxy(beanMethod)) {
		String scopedBeanName = ScopedProxyCreator.getTargetBeanName(beanName);
		if (beanFactory.isCurrentlyInCreation(scopedBeanName)) {
			beanName = scopedBeanName;
		}
	}
    // FactoryBeans 在 Spring 中是特殊的 beans，它们不产生 bean 实例本身，而是产生其他 beans。
    // 此代码块处理了当 FactoryBean 被请求时的情况，
    // 确保返回的是 FactoryBean 创建的实际 bean，而不是 FactoryBean 本身。
	if (factoryContainsBean(beanFactory, BeanFactory.FACTORY_BEAN_PREFIX + beanName) &&
		factoryContainsBean(beanFactory, beanName)) {
		// 此部分代码省略，但它处理 FactoryBean 创建的 bean 的返回和增强
	}

	// 检查当前的方法是否是正在被工厂调用的工厂方法
	if (isCurrentlyInvokedFactoryMethod(beanMethod)) {
		// 如果是，直接调用方法的原始实现
		return cglibMethodProxy.invokeSuper(enhancedConfigInstance, beanMethodArgs);
	}

	// 尝试从 bean 工厂中解析并返回 bean 的引用
	return resolveBeanReference(beanMethod, beanMethodArgs, beanFactory, beanName);
}
```

在`org.springframework.context.annotation.ConfigurationClassEnhancer.BeanMethodInterceptor#resolveBeanReference`方法中，主要责任是确保能够从 `BeanFactory` 中安全、正确地获取到bean实例，并处理所有相关的边缘情况和潜在异常。

```java
private Object resolveBeanReference(Method beanMethod, Object[] beanMethodArgs,
				ConfigurableBeanFactory beanFactory, String beanName) {
    
    // 判断bean是否正在创建中
    boolean alreadyInCreation = beanFactory.isCurrentlyInCreation(beanName);
    try {
        // 如果bean正在创建中，暂时将其设置为不在创建中，以避免异常
        if (alreadyInCreation) {
            beanFactory.setCurrentlyInCreation(beanName, false);
        }
        boolean useArgs = !ObjectUtils.isEmpty(beanMethodArgs);
        // 对于单例的bean，如果bean方法的参数包含null，则可能预期它们会被自动装配
        if (useArgs && beanFactory.isSingleton(beanName)) {
            for (Object arg : beanMethodArgs) {
                if (arg == null) {
                    useArgs = false;
                    break;
                }
            }
        }
        // 根据上面的判断，从BeanFactory中获取bean实例
        Object beanInstance = (useArgs ? beanFactory.getBean(beanName, beanMethodArgs) :
                               beanFactory.getBean(beanName));
        // 检查获取的bean实例是否与@Bean方法的返回类型兼容
        if (!ClassUtils.isAssignableValue(beanMethod.getReturnType(), beanInstance)) {
            if (beanInstance.equals(null)) {
                // 如果返回了特定的NullBean实例，进行相应的处理
                // ... [日志输出代码]
                beanInstance = null;
            }
            else {
                // 抛出异常，说明有一个同名但类型不兼容的bean覆盖了当前的bean
                String msg = String.format("@Bean method %s.%s ...", 
                                           // ... [代码省略以简化]
                                           );
                throw new IllegalStateException(msg);
            }
        }
        // 如果当前正在调用另一个@Bean方法，处理其依赖关系
        Method currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod();
        if (currentlyInvoked != null) {
            String outerBeanName = BeanAnnotationHelper.determineBeanNameFor(currentlyInvoked);
            beanFactory.registerDependentBean(beanName, outerBeanName);
        }
        return beanInstance;
    }
    finally {
        // 清理阶段，恢复bean的创建状态
        if (alreadyInCreation) {
            beanFactory.setCurrentlyInCreation(beanName, true);
        }
    }
}
```

### 七、注意事项

1. **单例保证**：
   - 在 `@Configuration` 类中，如果一个方法被标记为 `@Bean` 并被多次调用，它不会多次实例化一个 bean，而是返回同一个实例。这是因为 CGLIB 增强了 `@Configuration` 类，以确保 bean 的单例特性。
2. **proxyBeanMethods属性**：
   - `@Configuration(proxyBeanMethods = false)` 会关闭 CGLIB 代理的生成。这样做可以提高性能，但可能会导致单例 bean 的引用不一致，特别是在同一个配置类中直接调用其他 `@Bean` 方法时。
3. **防止循环引用**：
   - 在 `@Configuration` 类中，避免创建循环依赖。这可能会导致创建 bean 时出现问题。
4. **使用`@Profile`**：
   - 可以与 `@Configuration` 一起使用 `@Profile` 注解，以根据当前环境条件决定是否加载某个配置。
5. **避免使用 `final`**：
   - 由于 `@Configuration` 类是通过 CGLIB 增强的，因此它们不能是 `final` 类型，同样，它们的方法也不能声明为 `final`。

### 八、总结

#### 8.1、最佳实践总结

1. **`proxyBeanMethods = true`（默认）**：
   - 当在 `@Configuration` 类中调用一个由 `@Bean` 注解的方法时，Spring 容器确保每次都返回同一个 bean 实例。这是通过 CGLIB 代理实现的，该代理拦截对该方法的所有调用并返回 bean 的单例实例。这就是为什么在 `ConfigurationApplication` 的 `main` 方法中，两次调用 `myBean()` 方法都返回具有相同 hashcode 的 `MyBean` 实例。
2. **`proxyBeanMethods = false`**：
   - 在这种配置下，`@Configuration` 类中的方法不再被代理。因此，如果在配置类内部多次调用同一个 `@Bean` 方法，每次调用都会创建一个新的 bean 实例。在 `ConfigurationApplication` 的 `main` 方法中，两次调用 `myBean()` 方法会返回具有不同 hashcode 的 `MyBean` 实例，这证明了两次调用返回了两个不同的对象。

#### 8.2、源码分析总结

1. **初始化流程**:
   - 当使用 `AnnotationConfigApplicationContext` 启动 Spring 应用时，会调用其构造函数，该函数执行三个主要步骤：初始化、注册和刷新。
   - 初始化过程中，Spring 上下文创建 `AnnotatedBeanDefinitionReader` 和 `ClassPathBeanDefinitionScanner`。`AnnotatedBeanDefinitionReader` 负责注册通过注解定义的 beans。
   - `AnnotationConfigUtils.registerAnnotationConfigProcessors` 方法确保必要的后处理器（如 `ConfigurationClassPostProcessor`）注册到 Spring 容器中，从而能够识别和处理 `@Configuration` 类。
2. **注册流程**:
   - 使用 `AnnotatedBeanDefinitionReader` 将配置类（如 `MyConfiguration`）注册到 Spring 容器中。
   - 针对每个 `@Bean` 方法，`BeanDefinition`（bean 定义）被创建和注册到 `BeanDefinitionRegistry` 中。
3. **增强流程**:
   - 在容器的刷新过程中，`invokeBeanFactoryPostProcessors` 方法被调用，以执行所有的 `BeanFactoryPostProcessor` 实现。
   - `ConfigurationClassPostProcessor` 是一个关键的后处理器，它识别配置类并进行增强。
   - 对于每个标记为 `@Configuration` 的类，通过 `ConfigurationClassEnhancer` 创建一个 CGLIB 代理类。
   - 这个代理确保对配置类中的 `@Bean` 方法的每次调用都返回同一个 bean 实例，除非它是原型作用域的。
   - 当应用上下文启动完成后，对于任何请求的 bean，代理的 `@Bean` 方法会从 Spring 容器中返回已存在的 bean 实例，而不是重新创建一个新的实例。

### 九、常见问题

#### 9.2 @Configuration中full模式与lite模式如何选择？

`@Configuration` 注解有两种模式：`full` 和 `lite`。它们在功能和性能上有所不同。了解它们的优缺点有助于为特定的场景做出合适的选择。

##### 9.2.1 Full 模式

- 启用方式：在 `@Configuration` 注解中不设置 `proxyBeanMethods` 或将其设置为 `true`。
- 功能：当在配置类中的 `@Bean` 方法内部调用另一个 `@Bean` 方法时，Spring 会确保返回的是容器中的单例bean，而不是一个新的实例。这是通过CGLIB代理实现的。
- 优势：保持单例语义，确保容器中的单例Bean在配置类中的调用中始终是单例的。
- 劣势：需要通过CGLIB创建配置类的子类，可能带来一些性能开销，增加了启动时间，可能与某些库不兼容，这些库期望操作实际类而不是其CGLIB代理。

##### 9.2.2 Lite 模式

- 启用方式：在 `@Configuration` 注解中设置 `proxyBeanMethods` 为 `false`。
- 功能：禁用CGLIB代理。`@Bean` 方法之间的调用就像普通的Java方法调用，每次都会创建一个新的实例。
- 优势：更快的启动时间，因为不需要通过CGLIB增强配置类，对于简单的注入，这种模式可能更为简洁和直接。
- 劣势：不保持单例语义。如果在一个 `@Bean` 方法内部调用另一个 `@Bean` 方法，会创建一个新的bean实例。

##### 9.2.3 如何选择

- 如果你的配置中需要确保在配置类中调用的bean始终是Spring容器中的单例bean，选择full模式。
- 如果你的配置类只是简单地定义beans并注入依赖，且不需要在配置类方法之间共享单例实例，选择lite模式。
- 如果你关心应用的启动性能，特别是在云环境或微服务中，使用lite模式可能更合适，因为它避免了额外的CGLIB处理。

最终，根据项目的具体需求和场景选择合适的模式。如果没有特殊的单例需求，推荐使用lite模式，因为它更简单且启动性能更好。