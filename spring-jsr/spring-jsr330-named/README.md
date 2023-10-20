## @Named

- [@Named](#named)
  - [一、基本信息](#一基本信息)
  - [二、注解描述](#二注解描述)
  - [三、注解源码](#三注解源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
    - [@Named注册默认过滤器](#named注册默认过滤器)
    - [@Named扫描组件](#named扫描组件)
  - [七、源码分析](#七源码分析)
    - [@Named注册默认过滤器](#named注册默认过滤器-1)
    - [@Named扫描组件](#named扫描组件-1)
      - [前置条件](#前置条件)
      - [扫描入口](#扫描入口)
    - [@Named依赖注入](#named依赖注入)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133945784) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Named源码](https://github.com/xuchengsheng/spring-reading/blob/master/spring-jsr/spring-jsr330-named)

### 二、注解描述

`@Named` 注解是 Java 的标准注解，来源于 JSR 330: Dependency Injection for Java。它的主要目的是提供一种标准化的方式在 Java 平台上实现依赖注入。

### 三、注解源码

`@Named` 是 JSR-330 中定义的一个注解，用于指定依赖注入的资格标识。其主要用途是在存在多个同类型的 bean 实例时，提供一个明确的名称或标识，以消除歧义。该注解具有一个 `value` 属性，允许用户指定 bean 的名称，默认值为空字符串。`@Named` 携带了一个重要的元注解：`@Qualifier` 表明其可以作为资格提供者此注解在 Java 的依赖注入场景中，尤其是解决注入歧义性时，起到了关键作用。

```java
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Named {
    String value() default "";
}

```

### 四、主要功能

1. **Bean标识**
   + `@Named` 注解可以为一个类提供一个名称，使得这个类可以被识别并管理为一个bean。这样，这个bean就可以在其他地方通过这个名称被引用和注入。
2. **替代默认命名**
   + 默认情况下，没有具体指定名称的bean会使用其类名的首字母小写形式作为其名称。使用 `@Named`，我们可以`value`属性来覆盖这个默认名称。
3. **解决歧义性**
   + 在依赖注入中，有时候可能有多个bean都符合某个注入点的要求。在这种情况下，`@Named` 可以与 `@Inject` 注解结合使用，明确指定哪个bean应该被注入。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showService`方法。

```java
public class NamedApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

在`MyConfiguration`类中，使用了`@ComponentScan("com.xcs.spring")`注解告诉 Spring 在指定的包（在这里是 "`com.xcs.spring`"）及其子包中搜索带有 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类，并将它们自动注册为 beans。这样，spring就不必为每个组件明确写一个 bean 定义。Spring 会自动识别并注册它们。

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

}
```

`MyController` 由 `@Controller` 注解标记。该类依赖于一个类型为 `MyService` 的服务。通过使用 `@Inject` 注解，我们表示希望这个服务被注入，而 `@Named("myServiceB")` 注解则指定了具体要注入的服务实例的名称，即 "`myServiceB`"。`showService()` 方法简单地打印出当前注入的服务实例。

```java
@Controller
public class MyController {

    @Inject
    @Named("myServiceB")
    private MyService myService;

    public void showService(){
        System.out.println("myService = " + myService);
    }
}
```

`MyService` 是一个基础类。`MyServiceA` 是 `MyService` 的一个子类，它被 `@Named("myServiceA")` 注解标记，意味着在依赖注入容器中，这个类的实例将被识别为 "myServiceA"。类似地，`MyServiceB` 也是 `MyService` 的一个子类，并被 `@Named("myServiceB")` 注解标记，在依赖注入容器中，这个类的实例将被识别为 "myServiceB"。

```java
public class MyService {

}

@Named("myServiceA")
public class MyServiceA extends MyService{

}

@Named("myServiceB")
public class MyServiceB extends MyService{

}
```

运行结果发现，通过`@Named("myServiceB")`注解，成功地注入了一个 `MyServiceB` 类型的实例。

```
myService = com.xcs.spring.service.MyServiceB@2e8c1c9b
```

### 六、时序图

#### @Named注册默认过滤器

~~~mermaid
sequenceDiagram
Title: @Named注解注册默认过滤器时序图

Note right of NamedApplication: 初始化应用
NamedApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)
Note right of AnnotationConfigApplicationContext: 初始化Spring上下文
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext()
Note right of AnnotationConfigApplicationContext: 创建Bean定义扫描器
AnnotationConfigApplicationContext->>ClassPathBeanDefinitionScanner: ClassPathBeanDefinitionScanner(registry)
Note right of ClassPathBeanDefinitionScanner: 设置是否使用默认过滤器
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: ClassPathBeanDefinitionScanner(registry,useDefaultFilters)
Note right of ClassPathBeanDefinitionScanner: 获取或创建环境配置
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: getOrCreateEnvironment(registry)
Note right of ClassPathBeanDefinitionScanner: 使用指定环境初始化扫描器
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment)
Note right of ClassPathBeanDefinitionScanner: 最终初始化扫描器
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment,resourceLoader)
Note right of ClassPathBeanDefinitionScanner: 注册默认的注解过滤器
ClassPathBeanDefinitionScanner->>ClassPathScanningCandidateComponentProvider: registerDefaultFilters()
Note right of ClassPathScanningCandidateComponentProvider: 加入 @Named 过滤器
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider: this.includeFilters.add("javax.inject.Named");
~~~

#### @Named扫描组件

~~~mermaid
sequenceDiagram
Title: @Named注解扫描组件时序图
ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner: doScan(basePackages)
Note right of ClassPathBeanDefinitionScanner: 查找候选组件
ClassPathBeanDefinitionScanner->>ClassPathScanningCandidateComponentProvider: findCandidateComponents(basePackage)
Note right of ClassPathScanningCandidateComponentProvider: 扫描候选组件
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider: scanCandidateComponents(basePackage)
Note right of ClassPathScanningCandidateComponentProvider: 判断是否是候选组件
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider: isCandidateComponent(metadataReader)
Note right of ClassPathScanningCandidateComponentProvider: 过滤器匹配检查
ClassPathScanningCandidateComponentProvider->>AbstractTypeHierarchyTraversingFilter: match(metadataReader,metadataReaderFactory)
Note right of AbstractTypeHierarchyTraversingFilter: 检查注解匹配
AbstractTypeHierarchyTraversingFilter->>AnnotationTypeFilter: matchSelf(metadataReader)
Note right of ClassPathScanningCandidateComponentProvider: 返回Bean定义结果
ClassPathScanningCandidateComponentProvider-->>ClassPathBeanDefinitionScanner: 返回BeanDefinition
Note right of ClassPathBeanDefinitionScanner: 注册Bean定义
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner: registerBeanDefinition(definitionHolder,registry)
Note right of ClassPathBeanDefinitionScanner: 辅助工具注册Bean
ClassPathBeanDefinitionScanner->>BeanDefinitionReaderUtils: registerBeanDefinition(definitionHolder, registry)
Note right of BeanDefinitionReaderUtils: 在Bean工厂中注册Bean
BeanDefinitionReaderUtils->>DefaultListableBeanFactory: registerBeanDefinition(beanName,beanDefinition)
~~~

### 七、源码分析

#### @Named注册默认过滤器

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showService`方法。

```java
public class NamedApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们本次重点关注`this()`。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()`方法中，初始化了的两个核心组件，一个用于读取注解定义的bean (`AnnotatedBeanDefinitionReader`)，另一个用于扫描类路径并自动检测bean组件 (`ClassPathBeanDefinitionScanner`)，也是本次重点分析的内容。

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

在`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry)`方法中，又调用了另一个构造函数。

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
    this(registry, true);
}
```

在`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry, useDefaultFilters)`方法中，又调用了另一个构造函数。

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
    this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
}
```

在`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry, useDefaultFilters,environment)`方法中，最后，又调用另一个构造函数，使用所有这些参数进行实际的初始化工作。

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment) {

    this(registry, useDefaultFilters, environment,
         (registry instanceof ResourceLoader ? (ResourceLoader) registry : null));
}
```

在`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(registry, useDefaultFilters,environment,resourceLoader)`方法中， `useDefaultFilters` 为 `true` 时，除了常见的如 `@Component`, `@Service`, `@Repository` 等注解外，Spring也会自动注册JSR-330规范中的 `@Named` 注解作为一个默认的过滤器。这是Spring为了支持JSR-330注入规范而做的集成。

```java
public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment, @Nullable ResourceLoader resourceLoader) {

    // ... [代码部分省略以简化]
    if (useDefaultFilters) {
        registerDefaultFilters();
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#registerDefaultFilters`方法中，识别和处理带有 `@Named` 注解的组件，前提是该注解存在于类路径上。如果JSR-330库不在类路径上，Spring会优雅地跳过这一步，而不是导致失败。到此`@Named`注解的注册默认过滤器已经完成，待后续扫描组件过程中，会使用到此过滤器。

```java
protected void registerDefaultFilters() {
    // ... [代码部分省略以简化]
    try {
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
        logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // JSR-330 API not available - simply skip.
    }
}
```

#### @Named扫描组件

##### 前置条件

在Spring中，为了充分理解`@Named`注解的工作机制，需要深入研究组件扫描和bean定义注册过程。特别地，下述注解和接口为`@Named`的识别、解析和注册提供了基础支持。简而言之，为了完全理解`@Named`如何在Spring中工作，深入了解以下组件是关键的。

1. **`@ComponentScan` 注解**
   - `@ComponentScan` 注解用于配置类，指定Spring框架在哪些包中查找带有`@Component`, `@Service`, `@Repository`, `@Controller`和`@Named`等注解的类。这些被发现的组件将被自动注册为Spring应用上下文中的beans。特别地，`@Named`注解的组件会因此被扫描并注册。
   - 🔗 [@ComponentScan注解传送门](https://github.com/xuchengsheng/spring-reading/blob/master/spring-annotation/spring-annotation-componentScan)
2. **`BeanDefinitionRegistryPostProcessor` 接口**
   - 其中的`postProcessBeanDefinitionRegistry`方法允许在所有其他bean定义被加载之后、但在任何bean实例化之前，修改或添加bean定义。特定于组件扫描，当解析到`@ComponentScan`注解时，相应的`BeanDefinitionRegistryPostProcessor`实现（如`ConfigurationClassPostProcessor`）会被触发，负责处理组件扫描并注册相应的bean定义。
   - 🔗 [BeanDefinitionRegistryPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-beanDefinitionRegistryPostProcessor)

##### 扫描入口

在`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan`方法中，主要目标是找到指定`basePackages`中所有的组件，并为它们创建 `BeanDefinition`。这些 `BeanDefinition` 之后会被 Spring 容器用来创建实际的 bean 实例。

```java
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    // 断言确保至少有一个基础包被指定
    Assert.notEmpty(basePackages, "At least one base package must be specified");

    // 用于保存找到的bean定义的集合
    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();

    // 遍历每个基础包
    for (String basePackage : basePackages) {
        // 在给定的基础包中找到所有候选的bean定义
        Set<BeanDefinition> candidates = findCandidateComponents(basePackage);

        // 遍历找到的bean定义
        for (BeanDefinition candidate : candidates) {
            // ... [代码部分省略以简化]

            // 检查给定的bean名字是否已经存在，如果不存在，进行注册
            if (checkCandidate(beanName, candidate)) {
                // ... [代码部分省略以简化]
                
                // 在bean注册表中注册bean定义
                registerBeanDefinition(definitionHolder, this.registry);
            }
        }
    }
    // 返回所有注册的bean定义
    return beanDefinitions;
}
```

在`org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents`方法中，主要提供了两种方式查找组件：通过预先生成的索引（如果可用且支持）或通过传统的扫描方式（我们重点关注传统的扫描方式）。

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

在`org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#scanCandidateComponents`方法中，首先是构建搜索路径，用于在类路径中搜索指定包，然后是扫描类路径，获取匹配的资源（通常是 `.class` 文件），再然后是对于每个资源，检查是否是候选组件，例如是否有 `@Named` 注解，最后对于是候选组件的类，创建一个 `BeanDefinition` 对象并添加到结果集中。

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
                    // 检查类是否是候选组件（例如，是否带有@Named注释）
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

在`org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#isCandidateComponent`方法中，是扫描过程中的一个关键步骤，用于确定一个类是否应该被视为一个Spring组件的候选者。如果一个类带有 `@Named` 注解，那么对应的 `AnnotationTypeFilter` 将会识别它，并导致 `isCandidateComponent` 返回 `true`，表示这个类是一个合格的组件候选者。

```java
protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
    // ... [代码部分省略以简化]

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

在`org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter#match(metadataReader, metadataReaderFactory)`方法中，作用是确定给定的`metadataReader`是否与某些特定条件匹配。在上下文中，这个方法通常用于检查类或其元数据是否符合某些特定的条件，例如检查一个类是否有`@Named`注解。

```java
@Override
public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
    throws IOException {

    // This method optimizes avoiding unnecessary creation of ClassReaders
    // as well as visiting over those readers.
    if (matchSelf(metadataReader)) {
        return true;
    }
    // ... [代码部分省略以简化]
    return false;
}
```

在`org.springframework.core.type.filter.AnnotationTypeFilter#matchSelf`方法中，当我们在Spring框架中使用组件扫描时，我们的目标之一是自动发现并注册带有特定注解的类。其中，`@Named`注解是我们关心的一个，因为它标记了一个类，指示Spring应将其视为一个bean，并将其添加到Spring的应用上下文中。

**场景带入：**

+ 首先从`metadataReader`获取类的注解元数据。

+ 接着，它检查`MyServiceA`类是否直接带有`@Named`注解。这是通过`metadata.hasAnnotation(this.annotationType.getName())`完成的，其中`this.annotationType.getName()`会返回`javax.inject.Named`（代表`@Named`的完全限定类名）。

+ 如果`considerMetaAnnotations`是`true`，它还会检查`MyServiceA`类是否带有任何元注解，这些元注解自己可能被`@Named`标记。
+ 在这种情况下，因为`MyServiceA`直接带有`@Named`注解，所以`matchSelf`会返回`true`。这意味着`MyServiceA`类满足过滤条件，并且应该被注册为Spring的一个bean。

```java
@Override
protected boolean matchSelf(MetadataReader metadataReader) {
    AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
    return metadata.hasAnnotation(this.annotationType.getName()) ||
        (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
}
```

#### @Named依赖注入

在Spring中，`@Named`注解既是用于标记一个类作为Spring的bean，同时也在依赖注入中作为一个限定符来解决歧义性。这里，`@Named`的功能与`@Qualifier`非常相似，实际上，`@Named`注解在Spring中也是作为一个特殊类型的`@Qualifier`来实现的。

1. **`@Qualifier`注解**:
   - 在Spring中，`@Qualifier`注解用于指定在多个相同类型的bean之间进行选择时所需的bean的名称。当与`@Named`或`@Inject`结合使用时，它帮助Spring容器解析正确的依赖关系。
   - 🔗 [Qualifier注解传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-qualifier)

### 八、注意事项

1. **与@Component的区别**
   + 尽管`@Named`和`@Component`在功能上相似（都可以用于标识和注册beans），但它们来自不同的规范。`@Component`是Spring特有的，而`@Named`来自JSR-330。
2. **与@Qualifier的结合**
   + `@Named`可以与`@Inject`注解一起使用，作为`@Qualifier`的替代，以指定应该注入哪个具体的bean。
3. **不要混淆注解来源**
   + 由于Spring同时支持其自己的依赖注入注解和JSR-330，可能会在同一个项目中混合使用`@Autowired`和`@Inject`，这可能导致混淆。为了代码的一致性，最好在一个项目中坚持使用其中一种。
4. **类路径依赖**
   + 要使用`@Named`和其他JSR-330注解，需要确保`javax.inject`库在类路径上。如果没有添加此依赖，使用这些注解会导致类找不到错误。
5. **与JSR-330的其他注解的集成**
   + 当使用`@Named`时，我们可能还希望考虑使用JSR-330的其他注解，如`@Inject`，以确保一致性。
6. **避免名称冲突**
   + 当使用`@Named`为bean指定一个名称时，应确保在Spring上下文中没有其他bean使用相同的名称。否则，可能会出现不可预测的行为或错误。
7. **作用域**
   + `@Named`默认的作用域是singleton。但如果我们需要不同的作用域，例如prototype，我们需要结合使用`@Scope`注解。

### 九、总结

#### 最佳实践总结

1. **启动程序**
   + 在 `NamedApplication` 中，我们初始化了 `AnnotationConfigApplicationContext` 上下文，指定使用 `MyConfiguration` 作为配置类。然后，从该上下文中获取 `MyController` 的 bean 实例，并调用其 `showService` 方法。

2. **配置类**
   + `MyConfiguration` 类使用 `@ComponentScan` 注解，指示Spring在 "`com.xcs.spring`" 包及其子包中搜索被Stereotype注解标记的组件。因此，所有带有 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类都会被自动识别并注册为beans。

3. **控制器**
   + `MyController` 是一个通过 `@Controller` 注解标记的控制器类，它有一个 `MyService` 类型的依赖。我们使用 `@Inject` 注解要求这个依赖被注入，并通过 `@Named("myServiceB")` 注解明确指定我们想要注入哪个具体的bean实例。

4. **服务类**
   + 有三个与服务相关的类：基类 `MyService` 以及其两个子类 `MyServiceA` 和 `MyServiceB`。这两个子类都通过 `@Named` 注解被标记，这为它们在依赖注入容器中提供了明确的标识名称。

5. **运行结果**
   + 当程序执行时，通过 `@Named("myServiceB")` 的指示，成功地注入了一个 `MyServiceB` 类型的实例到 `MyController`。因此，当调用 `showService` 方法时，输出证明了 `MyServiceB` 的实例已经被正确注入。

#### 源码分析总结

1. **初始化上下文**
   + 启动类 `NamedApplication` 利用 `AnnotationConfigApplicationContext` 为上下文环境，注册了配置类 `MyConfiguration`，并从该上下文获取 `MyController` 类型的bean。

2. **注解配置类扫描**
   + Spring的 `AnnotationConfigApplicationContext` 在构造时初始化了两个关键组件：一个用于读取注解定义的bean (`AnnotatedBeanDefinitionReader`) 和另一个用于扫描类路径并自动发现bean组件 (`ClassPathBeanDefinitionScanner`)。

3. **`@Named` 注解的注册**
   + 在构造 `ClassPathBeanDefinitionScanner` 时，如果 `useDefaultFilters` 为 `true`，Spring会为 `@Named` 注解添加默认过滤器，从而使其在类路径扫描时被识别。这是Spring对JSR-330规范的集成，使得我们可以使用 `@Named` 作为组件标识。

4. **`@Named`扫描组件**

   + 当运行应用时，Spring会根据 `@ComponentScan` 的指示扫描指定包及其子包中的组件。在这个扫描过程中，它会检查每个类，看它们是否带有特定的注解，如 `@Named`。

   + 在组件扫描过程中，`isCandidateComponent` 方法用于检查一个类是否应被视为一个Spring组件的候选者。它会通过应用已注册的过滤器（其中之一就是对 `@Named` 注解的过滤器）来决定。

5. **`@Named`依赖注入**
   + 在Spring中，`@Named` 注解的另一个关键用途是作为限定符用于依赖注入。当存在多个同类型的bean，而我们需要指定注入哪一个时，`@Named` 可以帮助解决歧义。在这方面，它的功能与 `@Qualifier` 注解类似。