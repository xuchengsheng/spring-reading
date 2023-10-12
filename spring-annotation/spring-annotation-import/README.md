## @Import

- [@Import](#import)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/132806548) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Import源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-import)

### 二、注解描述

`@Import` 是 Spring 框架的核心注解，用于导入配置类或组件到当前的 Spring 上下文中。它可以用于导入常规的 `@Configuration` 类、常规组件类，或实现了 `ImportSelector` 和 `ImportBeanDefinitionRegistrar` 接口的类。`ImportSelector` 允许根据条件动态地选择要导入的组件，而 `ImportBeanDefinitionRegistrar` 提供了一种以编程方式注册bean的方法。使用 `@Import` 注解，我们可以更灵活、模块化地组织 Spring 的配置，确保上下文中有所需的所有组件和配置。

### 三、注解源码

`@Import` 是 Spring 框架自 3.0 版本开始引入的一个核心注解。允许我们导入一个或多个组件类，这些类通常是 `@Configuration` 类。它在功能上相当于 Spring XML 中的 `<import/>` 元素，导入类型`@Configuration`类、`ImportSelector`、`ImportBeanDefinitionRegistrar`的实现以及其他常规组件类，在导入的 `@Configuration` 类中声明的 bean 定义应使用 `@Autowired` 进行注入。

```java
/**
 * 表示要导入的一个或多个组件类 —— 通常是
 * Configuration @Configuration 类。
 *
 * 提供与Spring XML中的 <import/> 元素相同的功能。
 * 允许导入 @Configuration 类、ImportSelector 和
 * ImportBeanDefinitionRegistrar 的实现，以及常规组件
 * 类 (从 4.2 开始；与 AnnotationConfigApplicationContext#register 相似)。
 *
 * 在导入的 @Configuration 类中声明的 @Bean 定义应通过
 * org.springframework.beans.factory.annotation.Autowired @Autowired 注入。
 * 可以自动注入bean本身，也可以自动注入声明bean的配置类实例。
 * 后者允许在 @Configuration 类方法之间进行明确的、IDE友好的导航。
 *
 * 可以在类级别声明或作为元注解。
 *
 * 如果需要导入XML或其他非-@Configuration 的bean定义资源，
 * 请改用 ImportResource @ImportResource 注解。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Configuration
 * @see ImportSelector
 * @see ImportBeanDefinitionRegistrar
 * @see ImportResource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

	/**
	 * 要导入的 Configuration @Configuration、ImportSelector、
	 * ImportBeanDefinitionRegistrar 或常规组件类。
	 */
	Class<?>[] value();

}
```

### 四、主要功能

1. **导入配置类**
   + 允许一个 `@Configuration` 类引入另一个 `@Configuration` 类。

2. **导入选择器**
   + 通过实现 `ImportSelector` 接口，可以动态地选择和导入配置类。

3. **手动注册Bean**
   + 通过实现 `ImportBeanDefinitionRegistrar` 接口，可以在运行时手动注册 bean。

4. **导入常规组件类**
   + 从 Spring 4.2 开始，还可以导入常规的组件类。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，然后遍历并打印所有的bean定义名。

```java
public class ImportApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

使用`@Import` 注解允许导入其他组件或配置到当前的配置类。在 `MyConfiguration` 类中，它导入了四个不同的组件或选择器，第一个是`MyBean.class`一个常规Bean组件。第二个是`MyImportSelector.class`一个实现了 `ImportSelector` 的类，用于动态选择并导入配置。第三个是`MyDeferredImportSelector.class`一个实现了 `DeferredImportSelector` 的类，用于延迟地选择并导入配置。第四个是`MyImportBeanDefinitionRegistrar.class`一个实现了 `ImportBeanDefinitionRegistrar` 的类，用于手动注册bean。

```java
@Configuration
@Import({MyBean.class, MyImportSelector.class, MyDeferredImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class MyConfiguration {

}
```

 `MyImportSelector` 类提供了一种动态导入 `MyBeanA` 组件的机制。确保 `MyBeanA` 被加入到Spring的上下文中。

```java
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanA.class.getName()};
    }
}
```

`MyDeferredImportSelector` 类提供了一种延迟导入 `MyBeanB` 组件的机制，确保 `MyBeanB` 被添加到Spring的上下文中。与普通的 `ImportSelector` 不同，`DeferredImportSelector` 允许在Spring处理完所有其他配置类之后再进行导入，从而确保某些特定的处理顺序。

```java
public class MyDeferredImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanB.class.getName()};
    }
}
```

`MyImportBeanDefinitionRegistrar` 类提供手动注册 `MyBeanC` 组件到Spring容器的方法，而不依赖于组件扫描或其他自动配置机制。确保 `MyBeanC` 被添加到Spring的上下文中。

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(MyBeanC.class);
        registry.registerBeanDefinition(MyBeanC.class.getName(), beanDefinition);
    }
}
```

使用`@Import`注解和其相关的选择器或注册器来将这些bean类导入到Spring上下文中

```java
public class MyBean {

}

public class MyBeanA {

}

public class MyBeanB {

}

public class MyBeanC {

}
```

### 六、时序图

~~~mermaid
sequenceDiagram
    participant ImportApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant PostProcessorRegistrationDelegate
    participant ConfigurationClassPostProcessor
    participant ConfigurationClassParser
    participant DeferredImportSelectorHandler
    participant DeferredImportSelectorGroupingHandler
    participant DeferredImportSelectorGrouping
    participant DefaultDeferredImportSelectorGroup
    participant ConfigurationClassBeanDefinitionReader
    participant MyImportSelector
    participant MyDeferredImportSelector
    participant MyImportBeanDefinitionRegistrar
    participant DefaultListableBeanFactory

    ImportApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>初始化上下文
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新上下文
    AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)<br>调用BeanFactory的后处理器
    AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>委托调用BeanFactory的后处理器
    PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup)<br>执行BeanDefinition的注册后处理器
    PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor:postProcessBeanDefinitionRegistry(registry)<br>处理配置类
    ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor:processConfigBeanDefinitions(registry)<br>处理配置类bean的定义
    ConfigurationClassPostProcessor->>ConfigurationClassParser:new ConfigurationClassParser()<br>创建配置类解析器
    ConfigurationClassParser-->>ConfigurationClassPostProcessor:返回parser
    ConfigurationClassPostProcessor->>ConfigurationClassParser:parser.parse(candidates)<br>解析候选类
    ConfigurationClassParser->>ConfigurationClassParser:parse(metadata,beanName)<br>进一步解析类元数据
    ConfigurationClassParser->>ConfigurationClassParser:processConfigurationClass(configClass,filter)<br>处理@Configuration类
    ConfigurationClassParser->>+ConfigurationClassParser:doProcessConfigurationClass(configClass, sourceClass, filter)<br>实际处理配置类
    ConfigurationClassParser-->>-ConfigurationClassParser:返回SourceClass
    ConfigurationClassParser->>+ConfigurationClassParser:processImports(configClass, sourceClass, importCandidates, filter, true)<br>处理导入
    ConfigurationClassParser->>MyImportSelector:selectImports(importingClassMetadata)<br>调用自定义的导入选择器
    MyImportSelector-->>ConfigurationClassParser:返回Bean数组
    ConfigurationClassParser->>DeferredImportSelectorHandler:process()<br>处理延迟导入选择器
    DeferredImportSelectorHandler->>DeferredImportSelectorGroupingHandler:processGroupImports()<br>处理组导入
    DeferredImportSelectorGroupingHandler->>DeferredImportSelectorGrouping:getImports()<br>获取导入
    DeferredImportSelectorGrouping->>DefaultDeferredImportSelectorGroup:process(metadata,selector)<br>处理默认延迟导入选择器的组
    DefaultDeferredImportSelectorGroup->>MyDeferredImportSelector:selectImports(importingClassMetadata)<br> 调用自定义的导入选择器
    MyDeferredImportSelector-->>DefaultDeferredImportSelectorGroup:返回Bean数组,存储在imports字段中
    DeferredImportSelectorGrouping->>DefaultDeferredImportSelectorGroup:selectImports()<br>选择导入
    DeferredImportSelectorGrouping-->>DeferredImportSelectorGroupingHandler:返回Iterable<Group.Entry>
    DeferredImportSelectorGroupingHandler->>ConfigurationClassParser:processImports(configClass, sourceClass, importCandidates, filter, true)<br> 再次处理导入
    ConfigurationClassParser-->>-ConfigurationClassParser:递归处理@Configuration
    ConfigurationClassPostProcessor->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitions(configClasses)<br>加载bean定义
    ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator)<br>加载配置类的bean定义
    ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:registerBeanDefinitionForImportedConfigurationClass(configClass)<br>注册导入的配置类的bean定义
    ConfigurationClassBeanDefinitionReader->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)<br>在bean工厂中注册bean定义
    ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitionsFromRegistrars(registrars)<br>从注册器中加载bean定义
    ConfigurationClassBeanDefinitionReader->>MyImportBeanDefinitionRegistrar:registerBeanDefinitions(importingClassMetadata,registry)<br>调用自定义的bean定义注册器
    MyImportBeanDefinitionRegistrar->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)<br>在bean工厂中注册bean定义
~~~

### 七、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，然后遍历并打印所有的bean定义名。

```java
public class ImportApplication {

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

我们来到`org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions`方法中的步骤3.1。在`org.springframework.context.annotation.ConfigurationClassParser#parse`方法中，主要是遍历所有的配置类候选者，并对每一个带有注解的Bean定义进行解析。这通常涉及到查找该配置类中的@Bean方法、组件扫描指令等，并将这些信息注册到Spring容器中。

```java
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    // 遍历每个配置类的持有者
    for (BeanDefinitionHolder holder : configCandidates) {
        BeanDefinition bd = holder.getBeanDefinition();
        try {
            // 步骤1：如果Bean定义是AnnotatedBeanDefinition的实例，则获取其注解元数据并解析
            if (bd instanceof AnnotatedBeanDefinition) {
                parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
            }
            // 步骤2：如果Bean定义是AbstractBeanDefinition并且已经有关联的Bean类，则直接解析该Bean类
            else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
                parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
            }
            // 步骤3：如果上述情况都不符合，则直接使用Bean的类名进行解析
            else {
                parse(bd.getBeanClassName(), holder.getBeanName());
            }
        }
        // 如果在解析过程中捕获到BeanDefinitionStoreException，则直接抛出
        catch (BeanDefinitionStoreException ex) {
            throw ex;
        }
        // 对于其他类型的异常，封装并抛出一个新的BeanDefinitionStoreException
        catch (Throwable ex) {
            // ... [代码部分省略以简化]
        }
    }
    // 步骤4：在所有配置类都被解析后，处理所有延迟的导入选择器
    this.deferredImportSelectorHandler.process();
}
```

我们来到`org.springframework.context.annotation.ConfigurationClassParser#parse`方法的第一步，在`org.springframework.context.annotation.ConfigurationClassParser#parse(metadata, beanName)`方法中，将注解元数据和Bean名称转化为一个配置类，然后对其进行处理。处理配置类是Spring配置驱动的核心，它涉及到许多关键操作，如解析`@Bean`方法、处理`@ComponentScan`注解、处理`@Import`注解等。

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

在`org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass`方法中，其中主要目的是处理给定`ConfigurationClass`中的`@Import`注解。在处理完所有的导入之后，它完成了任务并返回null。

```java
@Nullable
protected final SourceClass doProcessConfigurationClass(
    ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
    throws IOException {

    // ... [代码部分省略以简化]
    
    // 处理 sourceClass 上的所有 @Import 注解
    processImports(configClass, sourceClass, getImports(sourceClass), filter, true);

    // ... [代码部分省略以简化]
    
    // 当前实现中，直接返回null，意味着没有超类需要进一步处理
    return null;
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#processImports`方法中，主要分为三个步骤。第一步是对于`ImportSelector`，`processImports`方法主要完成了动态选择和导入类的功能，使得Spring配置更加灵活和模块化。第二步是`ImportBeanDefinitionRegistrar`提供了一个在bean定义注册过程中的插入点，允许动态、条件性配置。`processImports`方法确保这些逻辑在处理`@Import`时正确执行。第三步是当遇到一个不是`ImportSelector`或`ImportBeanDefinitionRegistrar`的类时，直接视其为一个普通的Spring组件或配置类，并按照常规的处理流程进行。这确保了所有通过`@Import`导入的类，不论它们的类型如何，都会被正确地注册和处理。

```java
private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
            Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
            boolean checkForCircularImports) {
    // 如果没有要导入的候选类，直接返回
    if (importCandidates.isEmpty()) {
        return;
    }
    // 如果设置了检查循环导入，并且当前的配置类在导入堆栈中形成了一个链，报告循环导入问题
    if (checkForCircularImports && isChainedImportOnStack(configClass)) {
        this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
    }
    else {
        // 将当前的配置类推入导入堆栈中
        this.importStack.push(configClass);
        try {
            for (SourceClass candidate : importCandidates) {
                // 步骤1：判断候选类是否实现了 ImportSelector 接口
                if (candidate.isAssignable(ImportSelector.class)) {
                    // 加载当前的候选类
                    Class<?> candidateClass = candidate.loadClass();
                    // 通过工具方法实例化 ImportSelector 接口的实现类
                    ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
                                        this.environment, this.resourceLoader, this.registry);
                    // 获取从 ImportSelector 指定的排除过滤器
                    Predicate<String> selectorFilter = selector.getExclusionFilter();
                    // 如果选择器提供了额外的排除过滤器，则合并当前的排除过滤器
                    if (selectorFilter != null) {
                        exclusionFilter = exclusionFilter.or(selectorFilter);
                    }
                    // 步骤1.1：检查该选择器是否是 DeferredImportSelector 的实例
                    if (selector instanceof DeferredImportSelector) {
                        // 如果是 DeferredImportSelector，使用特定的处理器来处理它
                        this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
                    }
                    else {
                        // 步骤1.2： 如果不是 DeferredImportSelector，则使用选择器来选择要导入的类
                        String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                        // 将这些类名转化为 SourceClass 集合
                        Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
                        // 递归地处理这些要导入的类
                        processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
                    }
                }
                // 步骤2：检查当前候选类是否实现了 ImportBeanDefinitionRegistrar 接口
                else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                    // 加载当前的候选类
                    Class<?> candidateClass = candidate.loadClass();
                    // 通过工具方法实例化 ImportBeanDefinitionRegistrar 接口的实现类
                    ImportBeanDefinitionRegistrar registrar =
                        ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
                            this.environment, this.resourceLoader, this.registry);
                    // 将实例化的 registrar 添加到当前的配置类中
                    configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
                }
                // 步骤3：如果候选类既不是 ImportSelector 也不是 ImportBeanDefinitionRegistrar
                else {
                    // 在导入堆栈中为当前的源类注册导入的类
                    this.importStack.registerImport(
                        currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
                    // 将候选类处理为一个配置类，并递归地处理它
                    processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter);
                }
            }
        }
        catch (BeanDefinitionStoreException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            // ... [代码部分省略以简化]
        }
        finally {
            // 在处理完成后，从导入堆栈中弹出当前配置类
            this.importStack.pop();
        }
    }
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#processImports`方法中的步骤1.2中，最后调用到我们自定义的实现逻辑中来，`selectImports`方法始终返回`MyBeanA`类的完全限定名，表示当这个`ImportSelector`被处理时，`MyBeanA`类将被导入到Spring应用上下文中。

```java
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanA.class.getName()};
    }
}
```

我们回到`org.springframework.context.annotation.ConfigurationClassParser#parse`方法的第四步，在`org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorHandler#process`方法中，首先获取当前待处理的延迟导入选择器，然后使用`DeferredImportSelectorGroupingHandler`来按组处理它们，最后确保再次初始化延迟导入选择器列表，为下一次处理做准备。

```java
/**
 * 处理所有注册的延迟导入选择器。
 */
public void process() {
    // 获取当前的延迟导入选择器列表
    List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
    // 将当前的延迟导入选择器列表设为null，表示它们即将被处理
    this.deferredImportSelectors = null;
    
    try {
        // 如果存在待处理的延迟导入选择器
        if (deferredImports != null) {
            // 创建一个新的分组处理器，用于处理延迟导入选择器的分组逻辑
            DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
            
            // 对延迟导入选择器进行排序，确保按预期的顺序进行处理
            deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
            
            // 遍历每个延迟导入选择器，使用分组处理器进行注册
            deferredImports.forEach(handler::register);
            
            // 使用分组处理器处理分组的导入
            handler.processGroupImports();
        }
    }
    // 确保在方法结束时，无论是否发生异常，都将延迟导入选择器列表重新初始化为一个新的空列表
    finally {
        this.deferredImportSelectors = new ArrayList<>();
    }
}
```

在`org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorGroupingHandler#processGroupImports`方法中，遍历每个已注册的延迟导入选择器分组，并对每个分组中的每个导入条目进行处理。处理的内容包括递归地处理所有相关的`@Import`和`@Bean`定义。

```java
/**
 * 处理每个分组中的延迟导入选择器。
 */
public void processGroupImports() {
    // 遍历每个已注册的延迟导入选择器分组
    for (DeferredImportSelectorGrouping grouping : this.groupings.values()) {
        // 获取分组的候选类排除过滤器
        Predicate<String> exclusionFilter = grouping.getCandidateFilter();
        
        // 遍历分组中的每个导入选择器
        grouping.getImports().forEach(entry -> {
            // 获取与当前导入条目关联的配置类
            ConfigurationClass configurationClass = this.configurationClasses.get(entry.getMetadata());
            
            try {
                // 对每个导入条目进行处理，包括递归地处理所有相关的@Import和@Bean定义
                processImports(configurationClass, asSourceClass(configurationClass, exclusionFilter),
                               Collections.singleton(asSourceClass(entry.getImportClassName(), exclusionFilter)),
                               exclusionFilter, false);
            }
            // 如果在处理过程中捕获到BeanDefinitionStoreException，则直接抛出
            catch (BeanDefinitionStoreException ex) {
                throw ex;
            }
            // 对于其他类型的异常，处理相关逻辑（此处简化，不展开详细的异常处理逻辑）
            catch (Throwable ex) {
                // ... [代码部分省略以简化]
            }
        });
    }
}
```

在`org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorGrouping#getImports`方法中，遍历每个已注册的延迟导入选择器，将它们的相关信息传递给分组进行处理，然后最终使用分组实例来选择并返回要导入的类。

```java
public Iterable<Group.Entry> getImports() {
    // 遍历每个已注册的延迟导入选择器
    for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
        // 对于每个延迟导入选择器，将其关联的配置类的元数据和其选择器传递给分组进行处理
        this.group.process(deferredImport.getConfigurationClass().getMetadata(),
                           deferredImport.getImportSelector());
    }
    // 使用分组实例来选择并返回要导入的类
    return this.group.selectImports();
}
```

在`org.springframework.context.annotation.ConfigurationClassParser.DefaultDeferredImportSelectorGroup#process`方法中，主要给传入的`DeferredImportSelector`，找出它选择导入的所有类，并将这些类与提供的注解元数据一起存储在一个列表中，以供以后使用。

```java
@Override
public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
    // 使用选择器和提供的元数据选择要导入的类
    for (String importClassName : selector.selectImports(metadata)) {
        // 对于每个选定的导入类，创建一个新的Entry对象并将其添加到当前分组的导入列表中
        this.imports.add(new Entry(metadata, importClassName));
    }
}
```

最后调用到我们自定义的实现逻辑中来，`selectImports`方法始终返回`MyBeanB`类的完全限定名，表示当这个`DeferredImportSelector`被处理时，`MyBeanB`类将被导入到Spring应用上下文中。

```java
public class MyDeferredImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanB.class.getName()};
    }
}
```

我们来到`org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions`方法中的步骤5。在`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitions`方法中，遍历给定的一组`ConfigurationClass`对象（这些对象代表的是`@Configuration`注解的类），并为每个类加载其相关的Bean定义。

```java
public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
    TrackedConditionEvaluator trackedConditionEvaluator = new TrackedConditionEvaluator();
    for (ConfigurationClass configClass : configurationModel) {
        loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator);
    }
}
```

在`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`方法中，如果配置类是通过`@Import`注解或其他机制导入的，则该方法会为其注册一个Bean定义，另外该方法会处理与`ConfigurationClass`关联的所有`ImportBeanDefinitionRegistrar`。这些注册器允许在解析配置类时以编程方式动态地添加额外的Bean定义。

```java
private void loadBeanDefinitionsForConfigurationClass(
        ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {

    // ... [可能的初始化或其他预处理]

    // 步骤1：如果这个配置类是由于@Import或其他机制而被导入的，那么为它注册一个Bean定义。
    // 这确保了导入的@Configuration类也被视为一个Bean，并可以在ApplicationContext中被检索。
    if (configClass.isImported()) {
        registerBeanDefinitionForImportedConfigurationClass(configClass);
    }
    
    // ... [可能的其他处理或Bean定义加载]

    // 步骤2：从当前配置类关联的ImportBeanDefinitionRegistrars加载Bean定义。
    // ImportBeanDefinitionRegistrar允许我们在解析@Configuration类时进行编程式地注册额外的Bean定义。
    loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
}
```

我们来到`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`方法中步骤1，在`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#registerBeanDefinitionForImportedConfigurationClass`方法中，首先，它根据配置类的注解元数据创建一个Bean定义。然后，它确定并设置Bean的作用域，例如单例或原型。为Bean定义生成一个唯一的名称后，该方法处理了其上的常见注解，例如`@Lazy`和`@Primary`。如果Bean的作用域如请求或会话需要代理，会应用相应的代理。最后，它将Bean定义注册到Spring容器，并将生成的名称与原始配置类关联。这确保了导入的配置类在Spring中也可以作为一个常规Bean进行访问或注入。

```java
/**
 * 为导入的ConfigurationClass注册Bean定义。
 *
 * @param configClass 要处理的ConfigurationClass
 */
private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
    // 获取configClass的元数据
    AnnotationMetadata metadata = configClass.getMetadata();

    // 创建基于元数据的Bean定义
    AnnotatedGenericBeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(metadata);

    // 解析Bean的作用域（例如：singleton, prototype）
    ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(configBeanDef);
    configBeanDef.setScope(scopeMetadata.getScopeName());

    // 为configBeanDef生成Bean名称
    String configBeanName = this.importBeanNameGenerator.generateBeanName(configBeanDef, this.registry);

    // 处理常见的注解定义（例如：@Lazy, @Primary）
    AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef, metadata);

    // 将Bean定义封装在BeanDefinitionHolder中，以携带其他配置元数据
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(configBeanDef, configBeanName);

    // 根据需要应用作用域代理模式（例如，对于"request"和"session"作用域的Beans）
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);

    // 在Bean定义注册表中注册Bean定义
    this.registry.registerBeanDefinition(definitionHolder.getBeanName(), definitionHolder.getBeanDefinition());

    // 将生成的Bean名称设置到configClass中
    configClass.setBeanName(configBeanName);

    // 如果日志级别为TRACE，记录一条关于注册的消息
    if (logger.isTraceEnabled()) {
        logger.trace("Registered bean definition for imported class '" + configBeanName + "'");
    }
}
```

我们来到`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`方法中步骤2，每个`ImportBeanDefinitionRegistrar`都有机会基于其自定义逻辑在Spring容器中注册额外的Bean定义。

```java
private void loadBeanDefinitionsFromRegistrars(Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> registrars) {
    registrars.forEach((registrar, metadata) ->
                       registrar.registerBeanDefinitions(metadata, this.registry, this.importBeanNameGenerator));
}
```

最后调用到我们自定义的实现逻辑中来，`MyImportBeanDefinitionRegistrar` 类提供手动注册 `MyBeanC` 组件到Spring容器的方法，而不依赖于组件扫描或其他自动配置机制。确保 `MyBeanC` 被添加到Spring的上下文中。

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(MyBeanC.class);
        registry.registerBeanDefinition(MyBeanC.class.getName(), beanDefinition);
    }
}
```

### 八、注意事项

1. **避免循环引用**
   + 确保我们没有创建循环引用，即一个配置类导入另一个配置类，而后者又反过来导入前者。

2. **与`@ComponentScan`的关系**
   + `@Import`和`@ComponentScan`都可以用于注册bean，但是它们的用途稍有不同。`@ComponentScan`用于自动扫描和注册bean，而`@Import`用于明确地导入其他配置类。

3. **属性覆盖**
   + 如果从多个配置类中导入相同的bean定义，并设置了不同的属性或值，那么后导入的bean定义将覆盖先导入的bean定义。

4. **与`@Profile`的关系**
   + 可以结合使用`@Import`和`@Profile`，这样只有在特定的激活配置文件中才会导入某个配置类。

### 九、总结

#### 最佳实践总结

1. **启动类**
   + 通过`ImportApplication`类，我们初始化了Spring的上下文环境，选择了`AnnotationConfigApplicationContext`，这是Spring提供的使用Java注解配置方式。在该上下文中，我们指定了`MyConfiguration`作为主要的配置类，并遍历了上下文中所有已注册的bean名称。

2. **主配置类**

   + `MyConfiguration`类通过使用`@Import`注解导入了四种不同类型的组件或选择器：

     - `MyBean`：一个普通的Java类。

     - `MyImportSelector`：一个实现了`ImportSelector`接口的类。

     - `MyDeferredImportSelector`：一个实现了`DeferredImportSelector`接口的类。

     - `MyImportBeanDefinitionRegistrar`：一个实现了`ImportBeanDefinitionRegistrar`接口的类。


3. **选择器与注册器**：

   - `MyImportSelector`通过实现`ImportSelector`接口，动态地选择并导入了`MyBeanA`类。

   - `MyDeferredImportSelector`作为`DeferredImportSelector`的实现，延迟地选择并导入了`MyBeanB`类。与`ImportSelector`不同，它允许在所有其他配置类被处理后再进行导入。

   - `MyImportBeanDefinitionRegistrar`提供了手动注册bean的功能。在这个示例中，它将`MyBeanC`手动注册到了Spring上下文中。


4. **组件定义**
   + 在我们最佳实践中包含四个Java类，分别为`MyBean`、`MyBeanA`、`MyBeanB`和`MyBeanC`，它们都被上述的选择器或注册器选择并导入到Spring上下文中。

#### 源码分析总结

1. **启动阶段**
   + 在Spring启动时，会使用`AnnotationConfigApplicationContext`来加载和解析配置类，这个配置类可能包含有`@Import`注解。

2. **解析配置类**
   + 在`AnnotationConfigApplicationContext`的构造函数中，通过`refresh()`方法开始刷新容器并解析Bean定义。进一步，在`AbstractApplicationContext#refresh`方法中，会调用`invokeBeanFactoryPostProcessors`方法来处理在上下文中注册为bean的工厂处理器。

3. **处理`@Import`注解**
   + 处理过程中会特别关注`BeanDefinitionRegistryPostProcessor`，这是`BeanFactoryPostProcessor`的子接口，专门用于在所有其他bean定义加载之前修改默认的bean定义。然后，系统解析配置类并验证其内容，这主要涉及查找该类中的@Bean方法、组件扫描指令等，并将这些信息注册到Spring容器中。

4. **处理`ImportSelector`和`DeferredImportSelector`**
   + 在上述解析过程中，如果配置类包含一个实现了`ImportSelector`或`DeferredImportSelector`接口的类，那么它们将被用来选择并导入其他的类。在示例中，`MyImportSelector`和`MyDeferredImportSelector`是这样的选择器，分别导入`MyBeanA`和`MyBeanB`。

5. **处理`ImportBeanDefinitionRegistrar`**
   + 在加载Bean定义的过程中，也会处理与`ConfigurationClass`关联的所有`ImportBeanDefinitionRegistrar`。这些注册器允许在解析时动态地、以编程方式地添加额外的Bean定义，如`MyImportBeanDefinitionRegistrar`示例中为`MyBeanC`进行的手动注册。

6. **注册导入的配置类**
   + 如果一个`@Configuration`类是由于`@Import`或其他机制导入的，Spring不仅会处理它的`@Bean`方法和其他注解，还会为这个类本身注册一个Bean定义，以便它也可以被注入到其他Bean或由应用程序检索。

7. **额外Bean的注册**
   + 通过`ImportBeanDefinitionRegistrar`和其自定义逻辑，能够根据需要将任意数量的额外Bean定义注册到容器中。