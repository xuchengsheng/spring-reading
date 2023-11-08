## AnnotatedBeanDefinitionReader

- [AnnotatedBeanDefinitionReader](#annotatedbeandefinitionreader)
  - [一、知识储备](#一知识储备)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)
    - [注册注解后置处理器阶段](#注册注解后置处理器阶段)
    - [注册Bean定义阶段](#注册bean定义阶段)
  - [七、与其他组件的关系](#七与其他组件的关系)
  - [八、常见问题](#八常见问题)


### 一、知识储备

1. **`AnnotationMetadata`**
   +  `AnnotationMetadata` 是 Spring 框架中用于处理类上的注解信息的接口，它提供了对类上注解信息的访问和操作方法。 `AnnotatedBeanDefinitionReader` 利用 `AnnotationMetadata` 解析类上的注解信息，并将其转化为 Spring 的 BeanDefinition。
   +  [点击查看AnnotationMetadata接口](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-annotationMetadata)
2. **`BeanDefinition`**
   +  `BeanDefinition` 是 Spring 中描述和管理 Bean 配置的核心概念，它包括了有关 Bean 的信息，如类名、作用域、依赖关系、初始化方法等，而 `AnnotatedBeanDefinitionReader` 的主要任务之一是将使用注解配置的类转化为 `BeanDefinition` 并注册到 Spring 容器中。
   +  [点击查看BeanDefinition接口](https://github.com/xuchengsheng/spring-reading/tree/master/spring-beans/spring-bean-beanDefinition)

### 二、基本描述

`AnnotatedBeanDefinitionReader`是一个用于读取和解析带有注解的Bean定义的类，它主要用于基于注解的配置方式，允许开发者将Java类标记为Spring组件，从而让Spring容器自动扫描和注册这些组件，而不需要显式配置这些组件的Bean定义。

### 三、主要功能

1. **注册Bean定义**
   + 适用于那些没有使用特定注解的类，但需要交给 Spring 容器管理的情况。
2. **集成 Spring 容器**
   + `AnnotatedBeanDefinitionReader` 通常与 Spring 容器的注册机（如 `GenericApplicationContext`）一起使用，从而将解析的 Bean 定义注册到容器。

### 四、最佳实践

创建一个 `DefaultListableBeanFactory` 容器和关联的 `AnnotatedBeanDefinitionReader`，手动注册一个 `MyBean` 类为 Spring Bean，然后获取和打印该 Bean 的实例。

```java
public class AnnotatedBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // 创建一个 AnnotationConfigApplicationContext
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // 创建 AnnotatedBeanDefinitionReader 并将其关联到容器
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(factory);

        // 使用 AnnotatedBeanDefinitionReader 注册Bean对象
        reader.registerBean(MyBean.class);

        // 获取并打印 MyBean
        System.out.println("MyBean = " + factory.getBean(MyBean.class));
    }
}
```

`MyBean` 的Java类，代表了一个简单的Java Bean。

```java
public class MyBean {
}
```

运行结果发现，手动注册和获取 Bean 的过程成功，并且 `MyBean` 现在已经被 Spring 容器管理。

```java
MyBean = com.xcs.spring.bean.MyBean@6166e06f
```

### 五、时序图

~~~mermaid
sequenceDiagram
Title: AnnotatedBeanDefinitionReader时序图

par 注册注解后置处理器阶段
AnnotatedBeanDefinitionReaderDemo->>AnnotatedBeanDefinitionReader:new AnnotatedBeanDefinitionReader(registry)
Note over AnnotatedBeanDefinitionReaderDemo, AnnotatedBeanDefinitionReader: 创建 AnnotatedBeanDefinitionReader 对象
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:AnnotatedBeanDefinitionReader(registry, environment)
Note over AnnotatedBeanDefinitionReader: 使用容器注册和环境信息
AnnotatedBeanDefinitionReader->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry)
Note over AnnotatedBeanDefinitionReader, AnnotationConfigUtils: 调用工具类方法注册注解后置处理器
AnnotationConfigUtils->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry, source)
Note over AnnotationConfigUtils: 注册注解后置处理器到容器
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReaderDemo:返回reader
Note over AnnotatedBeanDefinitionReaderDemo,AnnotatedBeanDefinitionReader: 返回 AnnotatedBeanDefinitionReader 实例
end

par 注册Bean定义阶段
AnnotatedBeanDefinitionReaderDemo->>AnnotatedBeanDefinitionReader:registerBean(beanClass)
Note over AnnotatedBeanDefinitionReaderDemo, AnnotatedBeanDefinitionReader: 注册 BeanClass
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:doRegisterBean(beanClass, name, qualifiers, supplier, customizers)
Note over AnnotatedBeanDefinitionReader: 执行 Bean 注册
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:new AnnotatedGenericBeanDefinition(beanClass)
Note over AnnotatedBeanDefinitionReader: 创建 AnnotatedGenericBeanDefinition 对象
AnnotatedBeanDefinitionReader->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd)
Note over AnnotatedBeanDefinitionReader, AnnotationConfigUtils: 处理常见的Bean定义注解
AnnotationConfigUtils->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd,metadata)
Note over AnnotationConfigUtils: 处理 Bean 的元数据信息
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:new BeanDefinitionHolder(abd, beanName)
Note over AnnotatedBeanDefinitionReader: 创建 BeanDefinitionHolder
AnnotatedBeanDefinitionReader->>BeanDefinitionReaderUtils:registerBeanDefinition(definitionHolder, registry)
Note over AnnotatedBeanDefinitionReader, BeanDefinitionReaderUtils: 注册 Bean 定义
end
~~~

### 六、源码分析

#### 注册注解后置处理器阶段

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry)`方法中，实际上是通过委派给另一个构造函数来创建，并且获取了上下文的环境变量进行传递。

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
    this(registry, getOrCreateEnvironment(registry));
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry,environment)`方法中，主要目的是为了初始化 `AnnotatedBeanDefinitionReader`，并确保相关的条件评估器和注解处理器已经注册到 Spring 容器中，以便进行基于注解的组件扫描和 Bean 注册。

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    Assert.notNull(environment, "Environment must not be null");

    // 初始化 BeanDefinitionRegistry 和 Environment
    this.registry = registry;
    this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);

    // 注册注解相关的后置处理器，以支持基于注解的组件扫描和 Bean 注册
    AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry)`方法中，注解配置处理器注册到 Spring 容器中，从而启用注解驱动的配置。

```java
public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
    registerAnnotationConfigProcessors(registry, null);
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry, source)`方法中，主要目的是注册一些关键的注解配置处理器，以便支持注解驱动的配置和处理不同类型的注解。

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
        BeanDefinitionRegistry registry, @Nullable Object source) {

    // 1. 如果 BeanDefinitionRegistry 是 DefaultListableBeanFactory 的实例，执行以下操作
    DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
    if (beanFactory != null) {
        // 1.1 检查当前的依赖比较器是否是 AnnotationAwareOrderComparator 的实例，如果不是，设置依赖比较器为 AnnotationAwareOrderComparator.INSTANCE，用于处理注解驱动排序。
        if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
            beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
        }
        // 1.2 检查当前的自动装配候选解析器是否是 ContextAnnotationAutowireCandidateResolver 的实例，如果不是，设置自动装配候选解析器为 ContextAnnotationAutowireCandidateResolver，用于处理注解驱动的自动装配。
        if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
            beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        }
    }

    // 2. 创建一个空的 LinkedHashSet 用于存储将要注册的 Bean 定义。
    Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);

    // 3. 检查是否已经注册了名为 CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME 的 Bean 定义。如果没有，创建一个 ConfigurationClassPostProcessor 类型的 Bean 定义，并将其添加到 BeanDefinitionRegistry 中。
    if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // 4. 检查是否已经注册了名为 AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME 的 Bean 定义。如果没有，创建一个 AutowiredAnnotationBeanPostProcessor 类型的 Bean 定义，并将其添加到 BeanDefinitionRegistry 中。
    if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // 5. 检查是否已经注册了名为 COMMON_ANNOTATION_PROCESSOR_BEAN_NAME 的 Bean 定义，这是用于支持 JSR-250 注解的处理器。如果没有，并且检测到 JSR-250 的支持，创建一个 CommonAnnotationBeanPostProcessor 类型的 Bean 定义，并将其添加到 BeanDefinitionRegistry 中。
    if (jsr250Present && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // 6. 检查是否已经注册了名为 PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME 的 Bean 定义，这是用于支持 JPA 注解的处理器。如果没有，并且检测到 JPA 的支持，创建一个 PersistenceAnnotationBeanPostProcessor 类型的 Bean 定义，并将其添加到 BeanDefinitionRegistry 中。
    if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition();
        try {
            def.setBeanClass(ClassUtils.forName(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME,
                                                AnnotationConfigUtils.class.getClassLoader()));
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException(
						"Cannot load optional framework class: " + PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, ex);
        }
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // 7. 检查是否已经注册了名为 EVENT_LISTENER_PROCESSOR_BEAN_NAME 的 Bean 定义。如果没有，创建一个 EventListenerMethodProcessor 类型的 Bean 定义，并将其添加到 BeanDefinitionRegistry 中。
    if (!registry.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(EventListenerMethodProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_PROCESSOR_BEAN_NAME));
    }

    // 8. 检查是否已经注册了名为 EVENT_LISTENER_FACTORY_BEAN_NAME 的 Bean 定义。如果没有，创建一个 DefaultEventListenerFactory 类型的 Bean 定义，并将其添加到 BeanDefinitionRegistry 中。
    if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(DefaultEventListenerFactory.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    // 9. 返回包含注册的 Bean 定义的 LinkedHashSet。
    return beanDefs;
}
```

#### 注册Bean定义阶段

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#registerBean(beanClass)`方法中，实际上又调用了`doRegisterBean`方法。

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`方法中，创建一个 Bean 的定义，设置其属性（如作用域、名称、限定符、自定义配置等），最后将这个定义注册到 Spring 容器中，使其成为容器管理的 Bean。

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
        @Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
        @Nullable BeanDefinitionCustomizer[] customizers) {
    // 步骤1: 创建一个 AnnotatedGenericBeanDefinition，用于表示要注册的 Bean 的定义
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
    // 如果存在条件判断，根据条件结果决定是否注册 Bean
    if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
        return;
    }
    // 设置 Bean 的实例提供者（supplier），用于创建 Bean 实例
    abd.setInstanceSupplier(supplier);
    // 解析 Bean 的作用域并设置，确定 Bean 的范围（Scope）
    ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
    abd.setScope(scopeMetadata.getScopeName());
    // 生成或使用指定的 Bean 名称，如果没有指定名称，生成默认的 Bean 名称
    String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
    
    // 步骤2: 处理常见的 Bean 定义注解，如 @Lazy、@Primary 等
    AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    // 处理 Bean 的限定符（qualifiers），如 @Qualifier 注解
    if (qualifiers != null) {
        for (Class<? extends Annotation> qualifier : qualifiers) {
            if (Primary.class == qualifier) {
                abd.setPrimary(true);
            } else if (Lazy.class == qualifier) {
                abd.setLazyInit(true);
            } else {
                abd.addQualifier(new AutowireCandidateQualifier(qualifier));
            }
        }
    }
    // 自定义 Bean 定义，如果有自定义操作的话
    if (customizers != null) {
        for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(abd);
        }
    }
    // 步骤3: 将 Bean 注册到 Spring 容器中，使其成为容器管理的 Bean
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
}
```

> [`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`步骤2]

在`org.springframework.context.annotation.AnnotationConfigUtils#processCommonDefinitionAnnotations(abd)`方法中，处理 Bean 定义上的常见注解，以确保 Bean 在容器中的行为和属性符合这些注解的规定。

```java
public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
    processCommonDefinitionAnnotations(abd, abd.getMetadata());
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#processCommonDefinitionAnnotations(abd,metadata)`方法中，处理常见的 Bean 定义注解，如 `@Lazy`、`@Primary`、`@DependsOn`、`@Role` 和 `@Description`。

```java
static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
    // 处理 @Lazy 注解
    AnnotationAttributes lazy = attributesFor(metadata, Lazy.class);
    if (lazy != null) {
        abd.setLazyInit(lazy.getBoolean("value"));
    } else if (abd.getMetadata() != metadata) {
        lazy = attributesFor(abd.getMetadata(), Lazy.class);
        if (lazy != null) {
            abd.setLazyInit(lazy.getBoolean("value"));
        }
    }

    // 处理 @Primary 注解
    if (metadata.isAnnotated(Primary.class.getName())) {
        abd.setPrimary(true);
    }

    // 处理 @DependsOn 注解
    AnnotationAttributes dependsOn = attributesFor(metadata, DependsOn.class);
    if (dependsOn != null) {
        abd.setDependsOn(dependsOn.getStringArray("value"));
    }

    // 处理 @Role 注解
    AnnotationAttributes role = attributesFor(metadata, Role.class);
    if (role != null) {
        abd.setRole(role.getNumber("value").intValue());
    }

    // 处理 @Description 注解
    AnnotationAttributes description = attributesFor(metadata, Description.class);
    if (description != null) {
        abd.setDescription(description.getString("value"));
    }
}
```

> [`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`步骤3]

在`org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition`方法中，将Bean定义注册到Spring容器的Bean定义注册表中，并处理别名的注册。

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

### 七、与其他组件的关系

1. **`BeanDefinitionLoader`**
   + 通常与 Spring Boot 应用程序的加载和初始化过程相关。在 Spring Boot 中，`BeanDefinitionLoader` 用于加载应用程序的配置，并可能涉及到 `AnnotationConfigApplicationContext` 或 `AnnotationConfigWebApplicationContext`，这些上下文类会使用 `AnnotatedBeanDefinitionReader` 来注册基于注解的 Bean。
2. **`AnnotationConfigServletWebApplicationContext`**
   + 这是 Spring Boot Web 应用程序的上下文类，用于基于注解的配置。它内部使用 `AnnotatedBeanDefinitionReader` 来处理组件扫描和 Bean 的注册，以支持 Spring Boot Web 应用程序的初始化。
3. **`AnnotationConfigApplicationContext`**
   + 这是 Spring 的标准应用程序上下文，用于基于注解的配置。它内部使用 `AnnotatedBeanDefinitionReader` 来注册基于注解的 Bean 定义，并支持组件扫描以及自动配置。
4. **`AnnotationConfigWebApplicationContext`**
   + 这是 Spring Web 应用程序的上下文类，用于基于注解的配置。它类似于 `AnnotationConfigApplicationContext`，但专门用于 Web 应用程序。它也使用 `AnnotatedBeanDefinitionReader` 来处理组件扫描和 Bean 的注册，以支持 Spring Web 应用程序的初始化。

### 八、常见问题

1. **基于注解的 Spring 配置**
   + `AnnotatedBeanDefinitionReader` 可以用于创建基于注解的 Spring 配置，从而避免使用传统的 XML 配置。这在现代的 Spring 应用程序开发中非常常见，可以提高配置的可读性和维护性。
2. **Spring Boot 自动配置**
   + 在 Spring Boot 中，`AnnotatedBeanDefinitionReader` 用于自动扫描并注册 Spring Boot 自动配置类。这使得 Spring Boot 应用程序可以自动配置各种功能，从而降低了我们的工作负担。
3. **自定义组件扫描**
   + 我们可以自定义组件扫描器，使用 `AnnotatedBeanDefinitionReader` 的功能来实现特定的组件注册逻辑。这在某些复杂场景下非常有用，例如需要根据特定条件动态注册组件。