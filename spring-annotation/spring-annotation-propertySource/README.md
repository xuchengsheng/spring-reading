## @PropertySource

- [@PropertySource](#propertysource)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133800438) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@PropertySource源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-propertySource)

### 二、注解描述

`@PropertySource` 注解，用于指定外部的属性文件，从而将该文件中的键值对加载到 Spring 的 `Environment` 中。这样，我们就可以在应用程序中轻松地访问和使用这些属性值。

### 三、注解源码

`@Configuration`注解是 Spring 框架自 3.1 版本开始引入的一个核心注解，`@PropertySource` 是用于在 Spring 框架中声明属性源的注解。它允许我们指定外部属性文件（例如 `.properties` 或 `.xml` 文件），并将这些文件中的键值对加载到 Spring 的 `Environment` 中，从而使得应用程序可以轻松访问和使用这些属性值。

```java
/**
 * @PropertySource 是一个用于在 Spring 框架中声明属性源的注解。
 * 使用此注解，我们可以指定外部的属性文件（如 .properties 或 .xml 文件），
 * 从而将该文件中的键值对加载到 Spring 的 Environment 中。
 * 这样，应用程序就可以轻松地访问和使用这些属性值。
 * 
 * 通常，此注解与 @Configuration 注解类一起使用，为整个 Spring 环境提供配置属性。
 * 
 * 示例：
 * Configuration
 * PropertySource("classpath:/com/myco/app.properties")
 * public class AppConfig {
 *     // ...
 * }
 * 
 * 通过上面的配置，应用程序可以确保 `app.properties` 文件中的属性都可以在 Spring Environment 中使用。
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.1
 * @see PropertySources
 * @see Configuration
 * @see org.springframework.core.env.PropertySource
 * @see org.springframework.core.env.ConfigurableEnvironment#getPropertySources()
 * @see org.springframework.core.env.MutablePropertySources
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PropertySources.class)
public @interface PropertySource {

    /**
     * 指定此属性源的名称。如果省略，工厂将根据底层资源生成一个名称。
     */
    String name() default "";

    /**
     * 指定要加载的属性文件的资源位置。
     * 支持传统的属性文件和XML格式，例如："classpath:/com/myco/app.properties" 或 "file:/path/to/file.xml"。
     * 不支持资源位置的通配符。
     * ${...} 占位符将根据已在 Environment 中注册的所有属性源进行解析。
     * 每个位置将按声明的顺序添加到封闭的 Environment 作为它自己的属性源。
     */
    String[] value();

    /**
     * 指示是否应忽略找不到的属性资源。
     * 如果属性文件是完全可选的，则 true 是合适的。
     * 默认值为 false。
     */
    boolean ignoreResourceNotFound() default false;

    /**
     * 为给定的资源指定一个特定的字符编码，例如 "UTF-8"。
     */
    String encoding() default "";

    /**
     * 指定一个自定义的 PropertySourceFactory。
     * 默认情况下，将使用标准资源文件的默认工厂。
     */
    Class<? extends PropertySourceFactory> factory() default PropertySourceFactory.class;
}
```

### 四、主要功能

1. **加载属性文件**
   + 使我们能够指定一个或多个外部属性文件（如 `.properties` 或 `.xml` 文件），并将其加载到 Spring 的上下文中。
2. **声明资源位置**
   + `@PropertySource` 提供一个 `value` 属性，用于指定外部属性文件的位置，如 `classpath:/com/myco/app.properties` 或 `file:/path/to/file.xml`。
3. **属性覆盖**
   + 当使用多个 `@PropertySource` 注解或 `PropertySources` 注解（该注解允许我们指定多个 `@PropertySource`）时，后声明的属性源会覆盖先声明的属性源中的同名属性。
4. **处理资源找不到的情况**
   + 通过 `ignoreResourceNotFound` 属性，我们可以指定当资源找不到时是否抛出异常。这在某些属性文件是可选的情况下特别有用。
5. **指定文件编码**
   + 通过 `encoding` 属性，我们可以为资源文件指定特定的字符编码，如 "UTF-8"。
6. **自定义属性源工厂**
   + 使用 `factory` 属性，我们可以为属性源指定一个自定义的 `PropertySourceFactory`，用于解析和加载属性。
7. **支持占位符解析**
   + 在 `@PropertySource` 指定的文件中的属性值可以使用 `${...}` 占位符，这些占位符将被解析为在 `Environment` 中已经加载的其他属性的值。
8. **与 `Environment` 交互**
   + 一旦属性文件被加载到 `Environment` 中，可以通过 `@Autowired` 注入 `Environment` 来查询和使用这些属性。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从中获取两个属性：`apiVersion` 和 `kind`。

```java
public class PropertySourceApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("apiVersion = " + context.getEnvironment().getProperty("apiVersion"));
        System.out.println("kind = " + context.getEnvironment().getProperty("kind"));
    }
}
```

`MyConfiguration` 类标注了 `@Configuration`，这意味着它是一个基于Java的Spring配置类。同时，它还使用 `@PropertySource` 注解来加载名为 `my-application.yml` 的外部属性文件。

```java
@Configuration
@PropertySource("classpath:my-application.yml")
public class MyConfiguration {

}
```

`my-application.yml` 文件包含以下内容

```yaml
apiVersion: v1
kind: ConfigMap
```

运行结果发现，我们从Spring应用成功地从一个YAML文件中加载了属性，并能够在应用中使用这些属性值。

```java
apiVersion = v1
kind = ConfigMap
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: @PropertySource注解时序图

ComponentScanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
AnnotationConfigApplicationContext->>AbstractApplicationContext: refresh()<br>刷新上下文
AbstractApplicationContext->>AbstractApplicationContext: invokeBeanFactoryPostProcessors(beanFactory)<br>调用Bean工厂后置处理器
AbstractApplicationContext->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>委托处理
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate: invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup) <br>注册后置处理器
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor: postProcessBeanDefinitionRegistry(registry)<br>配置类后处理
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor: processConfigBeanDefinitions(registry)<br>处理Bean定义
ConfigurationClassPostProcessor->>ConfigurationClassParser: ConfigurationClassParser(...)<br>创建解析器
ConfigurationClassParser-->>ConfigurationClassPostProcessor: 返回解析器<br>
ConfigurationClassPostProcessor->>ConfigurationClassParser: parser.parse(candidates)<br>解析候选项
ConfigurationClassParser->>ConfigurationClassParser: parse(metadata, String beanName)<br>解析元数据
ConfigurationClassParser->>ConfigurationClassParser: processConfigurationClass(configClass,filter)<br>处理配置类
ConfigurationClassParser->>ConfigurationClassParser: doProcessConfigurationClass(configClass,sourceClass,filter)<br>执行处理操作
ConfigurationClassParser->>ConfigurationClassParser: processPropertySource(propertySource)<br>处理属性源
ConfigurationClassParser->>DefaultPropertySourceFactory:createPropertySource(name,resource)<br>创建属性源
DefaultPropertySourceFactory->>ConfigurationClassParser:返回PropertySource
ConfigurationClassParser->>StandardEnvironment:getPropertySources()<br>获取属性源
StandardEnvironment->>ConfigurationClassParser:返回MutablePropertySources
ConfigurationClassParser->>ConfigurationClassParser: addPropertySource(propertySource)<br>添加属性源
ConfigurationClassParser->>MutablePropertySources:addLast(propertySource)<br>添加最低优先级的属性源
~~~

### 七、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从中获取两个属性：`apiVersion` 和 `kind`。

```java
public class PropertySourceApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("apiVersion = " + context.getEnvironment().getProperty("apiVersion"));
        System.out.println("kind = " + context.getEnvironment().getProperty("kind"));
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

在`org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass`方法中，主要任务是处理 `ConfigurationClass` 中定义的 `@PropertySource` 注解，并确保它们被正确地添加到 `ConfigurableEnvironment`。如果环境不可配置（即不是 `ConfigurableEnvironment` 的实例），则会忽略该注解并记录相关信息。

```java
protected final SourceClass doProcessConfigurationClass(
			ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
			throws IOException {

    // 处理任何 @PropertySource 注解
    for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(
        sourceClass.getMetadata(), PropertySources.class,
        org.springframework.context.annotation.PropertySource.class)) {
        
        // 检查当前环境是否为ConfigurableEnvironment的实例
        if (this.environment instanceof ConfigurableEnvironment) {
            // 如果是，则处理该属性源
            processPropertySource(propertySource);
        } 
        else {
            // 否则，记录一条信息，说明正在忽略@PropertySource注解
            logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() +
                        "]. Reason: Environment must implement ConfigurableEnvironment");
        }
    }

    // 没有超类 -> 处理完成
    return null;
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#processPropertySource`方法中，首先解析 `@PropertySource` 注解的各个属性，如 `name`、`encoding`、`value` 和 `factory`。然后，它尝试加载每个指定的属性文件，并根据文件的内容和其他相关属性创建一个属性源，最后将这个属性源添加到Spring环境中。

```java
/**
 * 处理给定的@PropertySource注解属性。
 *
 * @param propertySource @PropertySource注解的属性
 * @throws IOException 如果处理时发生IO异常
 */
private void processPropertySource(AnnotationAttributes propertySource) throws IOException {
    
    // 获取"name"属性，如果没有指定，设置为null
    String name = propertySource.getString("name");
    if (!StringUtils.hasLength(name)) {
        name = null;
    }

    // 获取"encoding"属性，如果没有指定，设置为null
    String encoding = propertySource.getString("encoding");
    if (!StringUtils.hasLength(encoding)) {
        encoding = null;
    }

    // 获取"value"属性，它表示属性文件的位置
    String[] locations = propertySource.getStringArray("value");
    // 确保至少指定了一个位置
    Assert.isTrue(locations.length > 0, "At least one @PropertySource(value) location is required");

    // 获取"ignoreResourceNotFound"属性，表示如果资源找不到是否应该忽略
    boolean ignoreResourceNotFound = propertySource.getBoolean("ignoreResourceNotFound");

    // 获取并实例化PropertySourceFactory，用于创建属性源
    Class<? extends PropertySourceFactory> factoryClass = propertySource.getClass("factory");
    PropertySourceFactory factory = (factoryClass == PropertySourceFactory.class ?
                                     DEFAULT_PROPERTY_SOURCE_FACTORY : BeanUtils.instantiateClass(factoryClass));

    // 对于每一个指定的位置：
    for (String location : locations) {
        try {
            // 解析位置中的占位符
            String resolvedLocation = this.environment.resolveRequiredPlaceholders(location);
            // 获取资源
            Resource resource = this.resourceLoader.getResource(resolvedLocation);
            // 添加属性源到环境中
            addPropertySource(factory.createPropertySource(name, new EncodedResource(resource, encoding)));
        }
        catch (IllegalArgumentException | FileNotFoundException | UnknownHostException | SocketException ex) {
            // 当尝试打开资源时，占位符不可解析或资源找不到
            if (ignoreResourceNotFound) {
                // 如果忽略资源找不到，则记录一条信息
                if (logger.isInfoEnabled()) {
                    logger.info("Properties location [" + location + "] not resolvable: " + ex.getMessage());
                }
            }
            else {
                // 否则，抛出异常
                throw ex;
            }
        }
    }
}
```

在`org.springframework.core.io.support.DefaultPropertySourceFactory#createPropertySource`方法中，如果提供了名称，它将为该属性源使用该名称。如果没有提供名称，它将只基于资源创建一个属性源，属性源的名称可能会基于资源自己的描述或其他默认方式来确定。

```java
@Override
public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
    return (name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource));
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#addPropertySource`方法中，首先检查是否已经添加了具有相同名称的属性源。如果是，则它会将新的属性源与现有的属性源合并。如果没有，它将按照适当的顺序添加新的属性源。

```java
private void addPropertySource(PropertySource<?> propertySource) {
    // 获取属性源的名称
    String name = propertySource.getName();
    // 获取当前环境的所有属性源
    MutablePropertySources propertySources = ((ConfigurableEnvironment) this.environment).getPropertySources();

    // 如果此名称的属性源已经被添加过
    if (this.propertySourceNames.contains(name)) {
        // 我们已经添加了一个版本，需要扩展它
        PropertySource<?> existing = propertySources.get(name);
        if (existing != null) {
            PropertySource<?> newSource = (propertySource instanceof ResourcePropertySource ?
                                           ((ResourcePropertySource) propertySource).withResourceName() : propertySource);
            // 如果现有的属性源是CompositePropertySource
            if (existing instanceof CompositePropertySource) {
                ((CompositePropertySource) existing).addFirstPropertySource(newSource);
            }
            else {
                // 创建一个新的CompositePropertySource并将新的和现有的属性源添加进去
                if (existing instanceof ResourcePropertySource) {
                    existing = ((ResourcePropertySource) existing).withResourceName();
                }
                CompositePropertySource composite = new CompositePropertySource(name);
                composite.addPropertySource(newSource);
                composite.addPropertySource(existing);
                propertySources.replace(name, composite);
            }
            return;
        }
    }

    // 如果还没有添加任何属性源
    if (this.propertySourceNames.isEmpty()) {
        propertySources.addLast(propertySource);
    }
    else {
        // 添加属性源到上次处理的属性源之前
        String firstProcessed = this.propertySourceNames.get(this.propertySourceNames.size() - 1);
        propertySources.addBefore(firstProcessed, propertySource);
    }
    // 将属性源名称添加到跟踪的名称列表中
    this.propertySourceNames.add(name);
}
```

### 八、注意事项

1. **文件位置**
   + 确保你提供的文件路径是正确的。例如，`classpath:` 前缀表示文件应该在类路径中，而 `file:` 前缀则表示文件应该在文件系统的特定位置。
2. **占位符**
   + 在 `@PropertySource` 的 `value` 属性中，你可以使用 `${...}` 占位符，它们将会被已注册的任何属性源解析。
3. **处理重复的属性源名称**
   + 如果你有多个 `@PropertySource` 注解（或使用 `@PropertySources` 注解）且它们具有相同的名称，那么它们会合并。后声明的 `@PropertySource` 将覆盖先前声明的同名 `@PropertySource`。
4. **属性源的顺序**
   + 属性源的顺序很重要，因为在多个属性源中定义的同名属性将使用先找到的值。你可以使用 `PropertySource` 的 `name` 属性来明确指定属性源的名称，以控制其在环境中的顺序。
5. **忽略找不到的资源**
   + 你可以使用 `ignoreResourceNotFound` 属性来指定当属性文件找不到时是否应该抛出异常。默认情况下，这是 `false`，意味着如果属性文件找不到，会抛出异常。设置为 `true` 可以让Spring在找不到文件时安静地继续运行。
6. **字符编码**
   + 从Spring 4.3开始，`@PropertySource` 注解有一个 `encoding` 属性，允许你为给定的资源指定特定的字符编码。
7. **自定义属性源工厂**
   + 如果你需要特殊的逻辑来创建属性源，可以使用 `factory` 属性来指定一个自定义的 `PropertySourceFactory`。
8. **激活属性占位符解析**
   + 仅仅使用 `@PropertySource` 并不会激活属性占位符解析。为了替换你的bean定义中的 `${...}` 占位符，你还需要添加 `@Bean` 定义为 `PropertySourcesPlaceholderConfigurer`。
9. **与Profiles结合**
   + 你可以与Spring的Profile功能结合使用 `@PropertySource`，以根据不同的环境加载不同的属性文件。

### 九、总结

#### 最佳实践总结

1. **初始化上下文**
   + 使用 `AnnotationConfigApplicationContext` 作为Spring容器的初始化方式，它允许从Java注解中配置Spring容器。
2. **定义配置类**
   + 创建一个名为 `MyConfiguration` 的类，并使用 `@Configuration` 注解来标记它，表示这是一个基于Java的Spring配置类。
3. **指定属性源**
   + 在 `MyConfiguration` 类中，使用 `@PropertySource` 注解指定了一个外部属性文件的位置，文件名为 `my-application.yml`。
4. **定义属性内容**
   + 在 `my-application.yml` 文件中定义了两个属性：`apiVersion` 和 `kind`。
5. **加载并访问属性**
   + 运行应用程序后，使用Spring的 `Environment` API来访问这些属性，并将其输出到控制台。
6. **运行结果**
   + 从YAML文件成功加载的属性在控制台上显示为 `apiVersion = v1` 和 `kind = ConfigMap`。

#### 源码分析总结

1. **上下文初始化**
   + 通过使用 `AnnotationConfigApplicationContext`, Spring 上下文被初始化。该类允许Spring容器从Java注解中进行配置。传递的配置类是 `MyConfiguration`，该类定义了要从中加载的属性源。
2. **处理@PropertySource注解**
   + 在配置类处理期间，Spring 检查每个配置类以查找 `@PropertySource` 注解。如果找到，则属性源的相关数据（例如其位置和其他属性）被提取出来。
3. **资源位置解析**
   + 对于 `@PropertySource` 注解中定义的每个属性文件位置，Spring 尝试加载和解析该文件。它首先解析任何占位符，然后尝试加载资源。
4. **创建属性源**
   + 使用 `DefaultPropertySourceFactory`, Spring 创建一个属性源。这个工厂可以从给定的资源（例如 .properties 或 .yml 文件）创建一个属性源。
5. **向环境中添加属性源**
   + 一旦属性源被创建，它就被添加到Spring的运行时环境中。如果已经存在具有相同名称的属性源，新的属性源可能会与旧的合并，或者会以适当的方式进行处理。
6. **处理完成**
   + 在完成所有配置类和属性源的处理之后，Spring上下文继续其正常的启动过程，最终在应用程序运行时提供这些属性。