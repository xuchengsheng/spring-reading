## @Bean

- [@Bean](#bean)
  - [一、基本信息](#一基本信息)
  - [二、注解描述](#二注解描述)
  - [三、注解源码](#三注解源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
    - [@Bean注册时序图](#bean注册时序图)
    - [@Bean初始化方法时序图](#bean初始化方法时序图)
    - [@Bean销毁方法时序图](#bean销毁方法时序图)
  - [七、源码分析](#七源码分析)
    - [@Bean注册源码分析](#bean注册源码分析)
    - [@Bean初始化源码分析](#bean初始化源码分析)
    - [@Bean销毁源码分析](#bean销毁源码分析)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/132498762) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Bean源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-bean)

### 二、注解描述

`@Bean` 是 Spring 框架的核心注解，用于标记一个方法，表明这个方法返回值应被注册为 Spring 容器中的一个对象（Bean）。与传统的 XML 配置方式相比，它提供了一种更加简洁和直观的方式来定义 Bean。通常，`@Bean` 与 `@Configuration` 注解一起使用，后者标记一个类为 Spring 的配置类。方法名默认作为 Bean 的 ID，但也可以通过 `@Bean` 的 `name` 属性自定义。这种声明式的 Bean 定义方式在 Java 代码中提供了强大的灵活性，允许我们利用 Java 的完整特性来配置和初始化对象。此外，结合其他 Spring 特性，如 `@Autowired`，可以轻松实现依赖注入，进一步简化了应用的配置和组件管理。总之，通过 `@Bean` 注解，Spring 为现代化的应用开发提供了强大的支持，使得代码更为整洁和易于维护。

### 三、注解源码

`@Bean`注解是 Spring 框架自 3.0 版本开始引入的一个核心注解，这个注解表明一个方法会返回一个对象，该对象应该被注册为 Spring 应用上下文中的一个 bean。

```java
/**
 * 指示一个方法会产生一个由Spring容器管理的Bean。
 * @author Rod Johnson
 * @author Costin Leau
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see Configuration
 * @see Scope
 * @see DependsOn
 * @see Lazy
 * @see Primary
 * @see org.springframework.stereotype.Component
 * @see org.springframework.beans.factory.annotation.Autowired
 * @see org.springframework.beans.factory.annotation.Value
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    /**
     * {@link #name}的别名。
     * 当不需要其他属性时可以使用，例如：{@code @Bean("customBeanName")}。
     * @see #name
     */
    @AliasFor("name")
    String[] value() default {};

    /**
     * 这个bean的名称，或者如果有多个名称，则为主要的bean名称加上别名。
     * 如果未指定，bean的名称是注解方法的名称。
     * @see #value
     */
    @AliasFor("value")
    String[] name() default {};

    /**
     * 是否通过基于习惯的按名称或类型进行自动装配？
     * 注意，这种自动装配模式是基于按照习惯通过bean属性的setter方法进行的外部驱动的自动装配。
     * @deprecated ...
     */
    @Deprecated
    Autowire autowire() default Autowire.NO;

    /**
     * 这个bean是否可以作为其他bean的自动注入候选者？
     */
    boolean autowireCandidate() default true;

    /**
     * 在初始化时调用bean实例上的方法的可选名称。
     * 通常，可以在带有@Bean注解的方法的主体中直接以编程方式调用该方法。
     */
    String initMethod() default "";

    /**
     * 在关闭应用程序上下文时调用bean实例上的方法的可选名称。
     * 例如，一个JDBC DataSource的close()方法。
     */
    String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;

}
```

### 四、主要功能

1. **Bean 的创建与注册**:
   + `@Bean` 注解用于标记一个方法，该方法返回的对象会被 Spring 容器管理。这意味着当应用上下文启动时，该方法会被调用，并且它的返回值会被添加到容器中作为一个 bean。

2. **自定义 Bean 名称**
   + 虽然默认的 bean 名称是标注 `@Bean` 的方法的名称，但可以通过 `@Bean` 的 `name` 属性为 bean 指定一个或多个名称。

3. **生命周期管理**
   + 通过 `initMethod` 和 `destroyMethod` 属性，可以为 bean 指定初始化和销毁的回调方法。当 bean 被创建或销毁时，这些方法会被调用。

4. **替代 XML 配置**
   + 在早期的 Spring 版本中，bean 通常是在 XML 文件中定义的。使用 `@Bean` 注解可以完全用 Java 配置来替代 XML，从而使配置更为集中和类型安全。

5. **灵活的依赖注入**
   + 在 `@Bean` 方法内部，可以直接调用其他 `@Bean` 方法，实现依赖的注入。这种方式保证了类型安全，并使得代码与配置紧密结合。

6. **与其他注解结合**
   + `@Bean` 注解经常与其他 Spring 注解一起使用，如 `@Scope`（定义 bean 的范围，如单例或原型），`@Lazy`（延迟 bean 的初始化），`@Primary`（当存在多个相同类型的 bean 时，标记一个为首选）等，为 bean 提供更详细的配置。

7. **控制自动装配行为**
   + 通过 `autowireCandidate` 属性，可以控制该 bean 是否应被视为自动装配的候选对象，当其他 bean 需要进行类型匹配的自动装配时。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyBean`类型的bean，最后调用`context.close()`方法关闭容器。

```java
public class BeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(context.getBean(MyBean.class));
        context.close();
    }
}
```

在 `MyConfiguration` 配置类中，通过 `@Bean` 注解定义了一个名为 `myBean` 的 bean。这个 bean 是 `MyBean` 类的实例。当 Spring 容器初始化这个 bean 时，它会首先调用 `MyBean` 的 `init` 方法（由 `initMethod` 属性指定）。当这个 bean 在容器关闭时被销毁，它会调用 `MyBean` 的 `destroy` 方法（由 `destroyMethod` 属性指定）。

```java
@Configuration
public class MyConfiguration {

    @Bean(initMethod = "init",destroyMethod = "destroy")
    public MyBean myBean(){
        return new MyBean();
    }
}
```

当 Spring 容器创建 `MyBean` 的实例时，`init` 方法会被自动调用，因此 "MyBean.init" 会被输出到控制台。同样地，当 Spring 容器准备销毁这个 bean 或关闭时，`destroy` 方法会被调用，于是 "MyBean.destroy" 会被输出到控制台。

```java
public class MyBean {

    public void init(){
        System.out.println("MyBean.init");
    }

    public void destroy(){
        System.out.println("MyBean.destroy");
    }
}
```

运行结果发现，证明了在创建 bean 时 `init` 方法的调用以及在销毁 bean 时 `destroy` 方法的调用，从而展示了通过 `@Bean` 注解指定的生命周期方法在实际应用中的执行顺序。

```java
MyBean.init
com.xcs.spring.bean.MyBean@2fb3536e
MyBean.destroy
```

### 六、时序图

#### @Bean注册时序图

~~~mermaid
sequenceDiagram
Title: @Bean注册时序图
BeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>创建应用上下文
AnnotationConfigApplicationContext->>AbstractApplicationContext: refresh()<br>刷新应用上下文
AbstractApplicationContext->>AbstractApplicationContext: invokeBeanFactoryPostProcessors(beanFactory)<br>调用BeanFactory后置处理器
AbstractApplicationContext->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>执行BeanFactory的后置处理器
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate: invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup)<br>执行BeanDefinition的注册后处理器
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor: postProcessBeanDefinitionRegistry(registry)<br>处理@Configuration注解的类的Bean定义
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor: processConfigBeanDefinitions(registry)<br>处理配置类的Bean定义
ConfigurationClassPostProcessor->>ConfigurationClassParser: ConfigurationClassParser(...)<br>创建配置类解析器
ConfigurationClassParser-->>ConfigurationClassPostProcessor: 返回parser解析器<br>
ConfigurationClassPostProcessor->>ConfigurationClassParser: parser.parse(candidates)<br>解析候选的@Configuration类
ConfigurationClassParser->>ConfigurationClassParser: parse(metadata, String beanName)<br>解析具体的@Configuration类
ConfigurationClassParser->>ConfigurationClassParser: processConfigurationClass(configClass,filter)<br>处理@Configuration类
ConfigurationClassParser->>+ConfigurationClassParser: doProcessConfigurationClass(configClass,sourceClass,filter)<br>实际处理@Configuration类
ConfigurationClassParser->>+ConfigurationClassParser: retrieveBeanMethodMetadata(sourceClass)<br>获取@Bean方法的元数据
ConfigurationClassParser-->>-ConfigurationClassParser: 返回Set<MethodMetadata><br>
ConfigurationClassParser-->>-ConfigurationClassParser: 返回SourceClass<br>
ConfigurationClassPostProcessor->>ConfigurationClassBeanDefinitionReader: loadBeanDefinitions(configurationModel)<br>加载Bean定义
ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader: loadBeanDefinitionsForConfigurationClass(configClass,trackedConditionEvaluator)<br>为@Configuration类加载Bean定义
ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader: loadBeanDefinitionsForBeanMethod(beanMethod)<br>为@Bean方法加载Bean定义
ConfigurationClassBeanDefinitionReader->>DefaultListableBeanFactory: registerBeanDefinition(beanName, beanDefToRegister)<br>在BeanFactory中注册Bean定义
~~~

#### @Bean初始化方法时序图

~~~mermaid
sequenceDiagram
Title: @Bean初始化方法时序图
BeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>创建应用上下文
AnnotationConfigApplicationContext->>AbstractApplicationContext: refresh()<br>刷新应用上下文
AbstractApplicationContext->>AbstractApplicationContext: finishBeanFactoryInitialization(beanFactory)<br>完成BeanFactory的初始化
AbstractApplicationContext->>DefaultListableBeanFactory: preInstantiateSingletons()<br>预先实例化单例Bean
DefaultListableBeanFactory->>AbstractBeanFactory: getBean(name)<br>获取Bean
AbstractBeanFactory->>AbstractBeanFactory: doGetBean(name,requiredType,args,typeCheckOnly)<br>执行真实的获取Bean的操作
AbstractBeanFactory->>DefaultSingletonBeanRegistry: getSingleton(beanName,singletonFactory)<br>从单例注册表中获取Bean
DefaultSingletonBeanRegistry->>AbstractBeanFactory: singletonFactory.getObject()<br>从singleton工厂中获取Bean对象
AbstractBeanFactory->>AbstractAutowireCapableBeanFactory: createBean(beanName,mbd,args)<br>创建Bean
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: doCreateBean(beanName,mbd,args)<br>执行Bean的创建过程
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: initializeBean(beanName,bean,mbd)<br>初始化Bean
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: invokeInitMethods(beanName,bean,mbd)<br>调用Bean的初始化方法
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: invokeCustomInitMethod(beanName,bean,mbd)<br>执行自定义的初始化方法
AbstractAutowireCapableBeanFactory->>Method: invoke(obj,args)<br>反射调用Bean的方法
Method->>MyBean: init()<br>执行Bean的init方法
AbstractBeanFactory-->>DefaultListableBeanFactory: 返回最终创建的Bean

~~~

#### @Bean销毁方法时序图

~~~mermaid
sequenceDiagram
Title: @Bean销毁方法时序图
BeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>创建应用上下文
AnnotationConfigApplicationContext-->>BeanApplication: 应用上下文初始化完成并返回应用
BeanApplication->>AbstractApplicationContext: close()<br>请求关闭应用上下文
AbstractApplicationContext->>AbstractApplicationContext: doClose()<br>执行关闭操作
AbstractApplicationContext->>AbstractApplicationContext: destroyBeans()<br>销毁容器中的所有Beans
AbstractApplicationContext->>DefaultListableBeanFactory: destroySingletons()<br>销毁所有单例Beans
DefaultListableBeanFactory->>DefaultSingletonBeanRegistry: super.destroySingletons()<br>调用父类方法，销毁所有单例Beans
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry: destroySingleton(beanName)<br>销毁指定名称的单例Bean
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry: destroyBean(beanName,bean)<br>执行Bean的销毁逻辑
DefaultSingletonBeanRegistry->>DisposableBeanAdapter: destroy()<br>调用Bean的销毁方法
DisposableBeanAdapter->>DisposableBeanAdapter: invokeCustomDestroyMethod(destroyMethod)<br>调用自定义的销毁方法
DisposableBeanAdapter->>Method: invoke(this.bean, args)<br>反射调用Bean的销毁方法
Method->>MyBean: destroy()<br>执行Bean的destroy方法
~~~

### 七、源码分析

#### @Bean注册源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyBean`类型的bean，最后调用`context.close()`方法关闭容器。

```java
public class BeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(context.getBean(MyBean.class));
        context.close();
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
    // ... [代码部分省略以简化]
    parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#parse(metadata, beanName)`方法中，将注解元数据和Bean名称转化为一个配置类，然后对其进行处理。处理配置类是Spring配置驱动的核心，它涉及到许多关键操作，如解析@Bean方法、处理@ComponentScan注解、处理@Import注解等。

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

在`org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass`方法中，进一步处理配置类。特别是，它处理配置类中所有带有@Bean注解的方法。这些方法定义了如何在Spring应用程序上下文中初始化bean。这里简化的注释重点放在@Bean方法上，但原方法可能还包含其他重要的处理逻辑。

```java
@Nullable
protected final SourceClass doProcessConfigurationClass(
    ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
    throws IOException {

    // ... [代码部分省略以简化]
    
    // 处理配置类中的每一个@Bean注解的方法
    // 这里的目标是识别和注册配置类中定义的所有@Bean方法。这些方法定义了如何初始化应用程序中的bean。
    Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
    for (MethodMetadata methodMetadata : beanMethods) {
        configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
    }

    // ... [代码部分省略以简化]
    return null;
}
```

在`org.springframework.context.annotation.ConfigurationClassParser#retrieveBeanMethodMetadata`方法中，终于看到对`@Bean`注解的解析过程了，首先是尝试使用ASM获取确定的方法顺序，这一步是因为Java反射API返回的方法顺序是不确定的，但在某些场景下，方法的声明顺序可能是重要的。为了获得一个确定的顺序，我们尝试使用ASM（一个Java字节码处理框架）来读取类文件。其次是比较反射和ASM的结果，如果通过ASM获取到的方法数大于或等于通过反射获取的方法数，那么我们会进一步比较这两个集合。如果这两个集合的方法名相匹配，我们会更倾向于使用ASM的结果，因为它提供了确定的方法顺序。最后，方法返回一个`Set<MethodMetadata>`，表示找到的所有标注了@Bean的方法。

**为什么要关心方法的顺序?** 在某些情况下，我们可能期望@Bean方法按照它们在类中的声明顺序执行。虽然这种依赖通常应该避免，但Spring还是尽可能地根据我们声明的这种顺序，特别是在涉及生命周期回调和依赖注入时。

```java
private Set<MethodMetadata> retrieveBeanMethodMetadata(SourceClass sourceClass) {
    // 从源类中获取其注解元数据
    AnnotationMetadata original = sourceClass.getMetadata();
    
    // 从注解元数据中获取所有带有@Bean注解的方法
    Set<MethodMetadata> beanMethods = original.getAnnotatedMethods(Bean.class.getName());

    // 如果发现多个@Bean方法，并且注解元数据是StandardAnnotationMetadata的实例，
    // 则尝试使用ASM库读取类文件，以获取确定的方法声明顺序。
    if (beanMethods.size() > 1 && original instanceof StandardAnnotationMetadata) {
        try {
            // 使用ASM读取类文件的元数据
            AnnotationMetadata asm =
                this.metadataReaderFactory.getMetadataReader(original.getClassName()).getAnnotationMetadata();
            Set<MethodMetadata> asmMethods = asm.getAnnotatedMethods(Bean.class.getName());

            // 检查ASM读取的方法集是否包含所有通过标准反射检测到的方法
            if (asmMethods.size() >= beanMethods.size()) {
                Set<MethodMetadata> selectedMethods = new LinkedHashSet<>(asmMethods.size());
                for (MethodMetadata asmMethod : asmMethods) {
                    for (MethodMetadata beanMethod : beanMethods) {
                        if (beanMethod.getMethodName().equals(asmMethod.getMethodName())) {
                            selectedMethods.add(beanMethod);
                            break;
                        }
                    }
                }
                if (selectedMethods.size() == beanMethods.size()) {
                    // 所有通过反射检测到的方法都在ASM方法集中，因此使用ASM方法集
                    beanMethods = selectedMethods;
                }
            }
        }
        catch (IOException ex) {
            // 如果使用ASM读取类文件时发生错误，记录错误信息，并继续使用反射元数据
            logger.debug("Failed to read class file via ASM for determining @Bean method order", ex);
        }
    }

    // 返回检索到的@Bean方法集
    return beanMethods;
}
```

上面是步骤3.1对`@Bean`方法进行扫描，并将扫描结果存储在`ConfigurationClass`类中的`beanMethods`字段中。

我们来到`org.springframework.context.annotation.ConfigurationClassPostProcessor#processConfigBeanDefinitions`方法中的步骤4。在`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitions`方法中，首先设置了一个跟踪条件评估的工具，然后遍历每一个配置类，为每一个配置类加载bean定义。这是Spring Java配置方式的核心过程，确保`@Bean`方法被正确地解析并在容器中注册为bean。

```java
public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
    TrackedConditionEvaluator trackedConditionEvaluator = new TrackedConditionEvaluator();
    for (ConfigurationClass configClass : configurationModel) {
        loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator);
    }
}
```

在`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass`方法中，对于每一个`BeanMethod`，调用了`loadBeanDefinitionsForBeanMethod`方法。这个方法会针对单个@Bean方法进行解析，并将其转换为一个Spring IoC容器可以管理的Bean定义。

```java
private void loadBeanDefinitionsForConfigurationClass(
			ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {

    // ... [代码部分省略以简化]
    for (BeanMethod beanMethod : configClass.getBeanMethods()) {
        loadBeanDefinitionsForBeanMethod(beanMethod);
    }
	// ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForBeanMethod`方法中，如果@Bean方法是静态的，那么它直接使用方法所在类作为bean的类。否则，它使用配置类的bean名称作为工厂bean名，并设置工厂方法名为当前@Bean方法的名字，然后尝试获取更详细的方法元数据，特别是如果元数据是`StandardMethodMetadata`类型的，它可以直接获取原始方法对象，然后还要处理`autowire`, `autowireCandidate`, `initMethod`, 和`destroyMethod`等属性，这些属性提供了有关如何处理bean生命周期和依赖注入的信息。然后如果@Bean方法有`@Scope`注解，它会处理这个注解的属性，如作用域名和代理模式，然后需要为这个bean创建一个代理，最后，使用`registerBeanDefinition`方法将Bean定义注册到IoC容器中。

```java
private void loadBeanDefinitionsForBeanMethod(BeanMethod beanMethod) {
    // ... [代码部分省略以简化]

    // 创建一个新的Bean定义基于@Bean方法的元数据
    ConfigurationClassBeanDefinition beanDef = new ConfigurationClassBeanDefinition(configClass, metadata, beanName);

    // 设置Bean定义的来源信息
    beanDef.setSource(this.sourceExtractor.extractSource(metadata, configClass.getResource()));

    // 检查@Bean方法是否为静态方法
    if (metadata.isStatic()) {
        // 处理静态@Bean方法
        if (configClass.getMetadata() instanceof StandardAnnotationMetadata) {
            beanDef.setBeanClass(((StandardAnnotationMetadata) configClass.getMetadata()).getIntrospectedClass());
        }
        else {
            beanDef.setBeanClassName(configClass.getMetadata().getClassName());
        }
        beanDef.setUniqueFactoryMethodName(methodName);
    }
    else {
        // 处理实例@Bean方法
        beanDef.setFactoryBeanName(configClass.getBeanName());
        beanDef.setUniqueFactoryMethodName(methodName);
    }

    // 如果元数据是StandardMethodMetadata类型的，设置已解析的工厂方法
    if (metadata instanceof StandardMethodMetadata) {
        beanDef.setResolvedFactoryMethod(((StandardMethodMetadata) metadata).getIntrospectedMethod());
    }

    // 设置自动装配模式为构造器自动装配
    beanDef.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
    beanDef.setAttribute(org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor.
                         SKIP_REQUIRED_CHECK_ATTRIBUTE, Boolean.TRUE);

    // 处理Bean定义的通用注解属性
    AnnotationConfigUtils.processCommonDefinitionAnnotations(beanDef, metadata);

    // 获取并设置autowire属性
    Autowire autowire = bean.getEnum("autowire");
    if (autowire.isAutowire()) {
        beanDef.setAutowireMode(autowire.value());
    }

    // 获取并设置autowireCandidate属性
    boolean autowireCandidate = bean.getBoolean("autowireCandidate");
    if (!autowireCandidate) {
        beanDef.setAutowireCandidate(false);
    }

    // 获取并设置初始化方法名
    String initMethodName = bean.getString("initMethod");
    if (StringUtils.hasText(initMethodName)) {
        beanDef.setInitMethodName(initMethodName);
    }

    // 获取并设置销毁方法名
    String destroyMethodName = bean.getString("destroyMethod");
    beanDef.setDestroyMethodName(destroyMethodName);

    // 根据@Scope注解处理Bean的作用域及其代理模式
    ScopedProxyMode proxyMode = ScopedProxyMode.NO;
    AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(metadata, Scope.class);
    if (attributes != null) {
        beanDef.setScope(attributes.getString("value"));
        proxyMode = attributes.getEnum("proxyMode");
        if (proxyMode == ScopedProxyMode.DEFAULT) {
            proxyMode = ScopedProxyMode.NO;
        }
    }

    // 如果需要，创建一个代理的Bean定义
    BeanDefinition beanDefToRegister = beanDef;
    if (proxyMode != ScopedProxyMode.NO) {
        BeanDefinitionHolder proxyDef = ScopedProxyCreator.createScopedProxy(
            new BeanDefinitionHolder(beanDef, beanName), this.registry,
            proxyMode == ScopedProxyMode.TARGET_CLASS);
        beanDefToRegister = new ConfigurationClassBeanDefinition(
            (RootBeanDefinition) proxyDef.getBeanDefinition(), configClass, metadata, beanName);
    }

    // 最终将@Bean方法对应的Bean定义注册到容器
    this.registry.registerBeanDefinition(beanName, beanDefToRegister);
}
```

#### @Bean初始化源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyBean`类型的bean，最后调用`context.close()`方法关闭容器。

```java
public class BeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(context.getBean(MyBean.class));
        context.close();
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们重点关注`refresh()`方法

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
    // Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization`方法中，会继续调用`DefaultListableBeanFactory`类中的`preInstantiateSingletons`方法来完成所有剩余非懒加载的单列Bean对象。

```java
/**
 * 完成此工厂的bean初始化，实例化所有剩余的非延迟初始化单例bean。
 * 
 * @param beanFactory 要初始化的bean工厂
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [代码部分省略以简化]
    // 完成所有剩余非懒加载的单列Bean对象。
    beanFactory.preInstantiateSingletons();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons`方法中，主要的核心目的是预先实例化所有非懒加载的单例bean。在Spring的上下文初始化完成后，该方法会被触发，以确保所有单例bean都被正确地创建并初始化。其中`getBean(beanName)`是此方法的核心操作。对于容器中定义的每一个单例bean，它都会调用`getBean`方法，这将触发bean的实例化、初始化及其依赖的注入。如果bean之前没有被创建过，那么这个调用会导致其被实例化和初始化。

```java
public void preInstantiateSingletons() throws BeansException {
    // ... [代码部分省略以简化]
    // 循环遍历所有bean的名称
    for (String beanName : beanNames) {
        getBean(beanName);
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#getBean()`方法中，又调用了`doGetBean`方法来实际执行创建Bean的过程，传递给它bean的名称和一些其他默认的参数值。此处，`doGetBean`负责大部分工作，如查找bean定义、创建bean（如果尚未创建）、处理依赖关系等。

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中，首先检查所请求的bean是否是一个单例并且已经创建。如果尚未创建，它将创建一个新的实例。在这个过程中，它处理可能的异常情况，如循环引用，并确保返回的bean是正确的类型。这是Spring容器bean生命周期管理的核心部分。

```java
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {
    // ... [代码部分省略以简化]

    // 开始创建bean实例
    if (mbd.isSingleton()) {
        // 如果bean是单例的，我们会尝试从单例缓存中获取它
        // 如果不存在，则使用lambda创建一个新的实例
        sharedInstance = getSingleton(beanName, () -> {
            try {
                // 尝试创建bean实例
                return createBean(beanName, mbd, args);
            }
            catch (BeansException ex) {
                // ... [代码部分省略以简化]
            }
        });
        // 对于某些bean（例如FactoryBeans），可能需要进一步处理以获取真正的bean实例
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [代码部分省略以简化]

    // 确保返回的bean实例与请求的类型匹配
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()`方法中，主要负责从单例缓存中获取一个已存在的bean实例，或者使用提供的`ObjectFactory`创建一个新的实例。这是确保bean在Spring容器中作为单例存在的关键部分。

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // 断言bean名称不能为空
    Assert.notNull(beanName, "Bean name must not be null");

    // 同步访问单例对象缓存，确保线程安全
    synchronized (this.singletonObjects) {
        // 从缓存中获取单例对象
        Object singletonObject = this.singletonObjects.get(beanName);

        // 如果缓存中没有找到
        if (singletonObject == null) {
            // ... [代码部分省略以简化]

            try {
                // 使用工厂创建新的单例实例
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            catch (IllegalStateException ex) {
                // ... [代码部分省略以简化]
            }
            catch (BeanCreationException ex) {
                // ... [代码部分省略以简化]
            }
            finally {
                // ... [代码部分省略以简化]
            }

            // ... [代码部分省略以简化]
        }

        // 返回单例对象
        return singletonObject;
    }
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()`方法中，主要的逻辑是调用 `doCreateBean`，这是真正进行 bean 实例化、属性填充和初始化的地方。这个方法会返回新创建的 bean 实例。

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [代码部分省略以简化]
    
    try {
        // 正常的bean实例化、属性注入和初始化。
        // 这里是真正进行bean创建的部分。
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        // 记录bean成功创建的日志
        if (logger.isTraceEnabled()) {
            logger.trace("Finished creating instance of bean '" + beanName + "'");
        }
        return beanInstance;
    }
    catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
        // ... [代码部分省略以简化]
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，`initializeBean`方法是bean初始化，确保bean是完全配置和准备好的。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // 声明一个对象，后续可能用于存放初始化后的bean或它的代理对象
    Object exposedObject = bean;

    // ... [代码部分省略以简化]
    
    try {
        // ... [代码部分省略以简化]
        
        // bean初始化
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    } 
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    // 返回创建和初始化后的bean
    return exposedObject;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`方法中，如果bean实现了`InitializingBean`接口，则它的`afterPropertiesSet`方法会在此处被调用。此外，如果bean配置中定义了自定义的初始化方法，spring会在这里被调用。

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [代码部分省略以简化]
    
    try {
        invokeInitMethods(beanName, wrappedBean, mbd);
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }
    
    // ... [代码部分省略以简化]

    return wrappedBean;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods`方法中，`afterPropertiesSet`是`InitializingBean`接口中定义的一个特殊的初始化方法。如果bean实现了这个接口，这个方法会在bean的属性被注入之后自动被调用。在这里，为了避免重复调用，Spring会检查bean是否是`InitializingBean`的实例，以及其初始化方法名是否为`afterPropertiesSet`。

```java
protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
			throws Throwable {
    // ... [代码部分省略以简化]
    
    // 检查Bean定义是否存在，且bean实例不是一个NullBean（Spring中用于标记占位bean的特殊类型）
    if (mbd != null && bean.getClass() != NullBean.class) {
        // 获取初始化方法名
        String initMethodName = mbd.getInitMethodName();
        // 检查是否有初始化方法需要被调用
        if (StringUtils.hasLength(initMethodName) &&
            // bean不是一个InitializingBean，或者初始化方法名不是"afterPropertiesSet"
            !(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
            // 初始化方法没有被外部管理（例如，通过AspectJ的切面）
            !mbd.isExternallyManagedInitMethod(initMethodName)) {
            // 调用自定义的初始化方法
            invokeCustomInitMethod(beanName, bean, mbd);
        }
    }
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeCustomInitMethod`方法中，使用Java反射API来动态地调用Bean的初始化方法。

```java
protected void invokeCustomInitMethod(String beanName, Object bean, RootBeanDefinition mbd)
			throws Throwable {
	// ... [代码部分省略以简化]

	// 使用反射来调用Bean的初始化方法
    try {
        // 确保要调用的方法是可访问的
        ReflectionUtils.makeAccessible(methodToInvoke);
        // 调用Bean的自定义初始化方法
        methodToInvoke.invoke(bean);
    }
    catch (InvocationTargetException ex) {
        // 如果初始化方法抛出了异常，这里会捕获到，并继续抛出该异常的目标异常
        throw ex.getTargetException();
    }
    // ... [代码部分省略以简化]
}
```

#### @Bean销毁源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyBean`类型的bean，最后调用`context.close()`方法关闭容器。

```java
public class BeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println(context.getBean(MyBean.class));
        context.close();
    }
}
```

在`org.springframework.context.support.AbstractApplicationContext#close`方法中，首先是启动了一个同步块，它同步在 `startupShutdownMonitor` 对象上。这确保了在给定时刻只有一个线程可以执行这个块内的代码，防止多线程导致的资源竞争或数据不一致，然后是调用了 `doClose` 方法，最后是为 JVM 注册了一个关闭钩子。

```java
@Override
public void close() {
    synchronized (this.startupShutdownMonitor) {
        doClose();
        // If we registered a JVM shutdown hook, we don't need it anymore now:
        // We've already explicitly closed the context.
        if (this.shutdownHook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
            }
            catch (IllegalStateException ex) {
                // ignore - VM is already shutting down
            }
        }
    }
}
```

在`org.springframework.context.support.AbstractApplicationContext#doClose`方法中，又调用了 `destroyBeans` 方法。

```java
protected void doClose() {
    // ... [代码部分省略以简化]
    // Destroy all cached singletons in the context's BeanFactory.
    destroyBeans();
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#destroyBeans`方法中，首先是调用了`getBeanFactory()`返回 Spring 的 `BeanFactory` ，然后在获得的 `BeanFactory` 上，调用了 `destroySingletons` 方法，这个方法的目的是销毁所有在 `BeanFactory` 中缓存的单例 beans。

```java
protected void destroyBeans() {
    getBeanFactory().destroySingletons();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingletons`方法中，首先是调用了父类的 `destroySingletons` 方法，为了确保继承自父类的销毁逻辑得到了执行。

```java
@Override
public void destroySingletons() {
    super.destroySingletons();
    updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
    clearByTypeCache();
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingletons`方法中，首先是在`disposableBeans` 字段上，从其键集合中获取所有的 bean 名称，并将它们转换为一个字符串数组。`disposableBeans` 可能包含了实现了 `DisposableBean` 接口的 beans，这些 beans 需要在容器销毁时特殊处理，最后倒序循环，从最后一个开始，销毁所有在 `disposableBeans` 列表中的 beans。这样做是为了确保依赖关系正确地处理，beans先被创建的应该后被销毁。

```java
public void destroySingletons() {
    // ... [代码部分省略以简化]
    String[] disposableBeanNames;
    synchronized (this.disposableBeans) {
        disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
    }
    for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
        destroySingleton(disposableBeanNames[i]);
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingleton`方法中，首先是调用了父类的 `destroySingleton` 方法，为了确保继承自父类的销毁逻辑得到了执行。

```java
@Override
public void destroySingleton(String beanName) {
    super.destroySingleton(beanName);
    removeManualSingletonName(beanName);
    clearByTypeCache();
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton`方法中，首先是使用 `synchronized` 关键字在 `disposableBeans` 对象上进行同步，以确保在多线程环境中安全地访问和修改它，从 `disposableBeans` 集合中移除指定名称的 bean，并将其转换为 `DisposableBean` 类型，最后调用`destroyBean`方法执行实际的销毁操作。

```java
public void destroySingleton(String beanName) {
    // Remove a registered singleton of the given name, if any.
    removeSingleton(beanName);

    // Destroy the corresponding DisposableBean instance.
    DisposableBean disposableBean;
    synchronized (this.disposableBeans) {
        disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
    }
    destroyBean(beanName, disposableBean);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean`方法中，直接调用 `bean` 的 `destroy` 方法。因为 `bean` 是一个 `DisposableBean` 类型的实例，所以它一定有一个 `destroy` 方法，该方法提供了 bean 的自定义销毁逻辑。

```java
protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
    // ... [代码部分省略以简化]
    // Actually destroy the bean now...
    if (bean != null) {
        try {
            bean.destroy();
        }
        catch (Throwable ex) {
            // ... [代码部分省略以简化]
        }
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DisposableBeanAdapter#destroy`方法中，此方法首先检查是否已经为bean指定了一个销毁方法(`destroyMethod`)。如果有，则直接调用`invokeCustomDestroyMethod`方法。如果没有提供具体的方法，但提供了方法的名称(`destroyMethodName`)，那么它会尝试根据这个名称找到对应的方法。一旦找到了方法，就会调用`invokeCustomDestroyMethod`方法。

```java
@Override
public void destroy() {
    // ... [代码部分省略以简化]

    // 如果提供了销毁方法（destroyMethod），则直接调用它
    if (this.destroyMethod != null) {
        invokeCustomDestroyMethod(this.destroyMethod);
    }
    // 否则，如果提供了销毁方法的名称（destroyMethodName），则尝试找到对应的方法并调用它
    else if (this.destroyMethodName != null) {
        // 根据方法名确定要调用的销毁方法
        Method methodToInvoke = determineDestroyMethod(this.destroyMethodName);
        if (methodToInvoke != null) {
            // 如果找到了方法，则调用它，但确保使用的是方法的接口版本（如果可能的话）
            invokeCustomDestroyMethod(ClassUtils.getInterfaceMethodIfPossible(methodToInvoke));
        }
    }
}
```

在`org.springframework.beans.factory.support.DisposableBeanAdapter#invokeCustomDestroyMethod`方法中，用于执行自定义的销毁方法。自定义销毁方法是由我们在Spring配置中为bean定义的，用于在bean的生命周期结束时释放资源、清理等。

```java
private void invokeCustomDestroyMethod(final Method destroyMethod) {
    // ... [代码部分省略以简化]
    try {
        if (System.getSecurityManager() != null) {
            // ... [代码部分省略以简化]
        }
        else {
            ReflectionUtils.makeAccessible(destroyMethod);
            destroyMethod.invoke(this.bean, args);
        }
    }
    catch (InvocationTargetException ex) {
        // ... [代码部分省略以简化]
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }
}
```

### 八、注意事项

1. **方法名称即Bean名称**
   + 默认情况下，使用`@Bean`注解的方法名称将用作Bean的名称。如果需要自定义bean的名称，可以在`@Bean`注解中指定名称，如`@Bean("customBeanName")`。

2. **返回类型**
   + 确保`@Bean`方法的返回类型与实际创建的bean对象匹配。

3. **调用其他`@Bean`方法**
   + 在一个配置类中，可以调用一个`@Bean`方法来注入另一个`@Bean`方法的结果。这是因为`@Bean`方法在调用时是通过Spring代理进行的，这确保了单例beans的单一实例性。

4. **作用域**
   + 使用`@Scope`注解与`@Bean`一起可以定义Bean的作用域，例如单例（默认）、原型、请求、会话等。

5. **生命周期回调**
   + `@Bean`注解允许指定`initMethod`和`destroyMethod`，这些方法分别在bean初始化和销毁时被调用。

6. **避免重复定义**
   + 确保不在XML和Java配置中同时定义同一个bean。如果必须这样做，确保bean的名称和定义相同，否则会出现不可预测的行为。

7. **慎用`@Lazy`**
   + `@Lazy`注解使得bean在首次请求时才被初始化。如果一个bean需要在应用启动时就初始化，那么不应该标记为延迟初始化。

8. **参数化`@Bean`方法**
   + `@Bean`方法可以接受参数，这些参数会从Spring容器中自动解析。这在创建一个bean依赖于另一个bean时特别有用。

9. **考虑线程安全性**
   + 特别是对于原型作用域的bean，确保bean是线程安全的，或者不会在多个线程之间共享。

10. **使用条件注解**
    + 配合`@Conditional`或其他相关注解，可以在满足某些条件时才创建bean。

11. **`@Bean`与`@Component`的区别**
    + 虽然两者都用于定义bean，但`@Bean`通常用于方法，特别是在Java配置类中，而`@Component`（及其特化：`@Service`、`@Repository`、`@Controller`等）用于类。

12. **注意潜在的循环依赖**
    + 如果两个`@Bean`方法相互调用，可能会出现循环依赖。Spring可以解决单例作用域的bean之间的循环依赖，但不推荐这样做。

13. **配置类应被Spring管理**
    + 确保包含`@Bean`方法的类由Spring管理，并标记为`@Configuration`。这确保`@Bean`方法由Spring的代理调用，从而支持上述提到的特性，如单例保证和循环引用。

### 九、总结

#### 最佳实践总结

1. **应用启动**
   + 使用`AnnotationConfigApplicationContext`可以基于Java注解来启动和配置Spring的上下文。在上述示例中，我们传递了一个Java配置类 `MyConfiguration` 作为参数来初始化这个上下文。

2. **配置类的使用**
   + 使用 `@Configuration` 注解标记配置类，表明这个类包含Spring的bean定义。在配置类中，可以使用 `@Bean` 注解来定义bean。这个注解的方法的名称默认会作为bean的名称，返回的实例则为Spring容器管理的bean实例。

3. **Bean的生命周期**
   + 通过 `@Bean` 注解的 `initMethod` 和 `destroyMethod` 属性，可以为bean定义初始化和销毁时要调用的方法。这为bean提供了一种自定义的初始化和清理机制。当bean被Spring容器实例化时，指定的初始化方法会被调用；当bean被销毁或容器关闭时，指定的销毁方法会被调用。

#### 源码分析总结

1. **@Bean 注册源码分析总结**

   + **启动及配置加载**
     + 使用 `AnnotationConfigApplicationContext` 初始化Spring上下文，并传入 `MyConfiguration` 配置类。该上下文构造函数将执行 `register()` 和 `refresh()` 方法。

   + **Bean定义的解析**
     + `refresh()` 方法触发上下文的刷新，其中涉及到对bean定义的处理。调用 `invokeBeanFactoryPostProcessors()` 方法来处理bean工厂后置处理器。

   + **处理配置类**
     + `BeanDefinitionRegistryPostProcessor` 是特殊的 `BeanFactoryPostProcessor`，它会在所有其他bean定义被加载之前运行。这个流程主要通过 `ConfigurationClassPostProcessor` 实现，该类负责处理标有 `@Configuration` 的类。

   + **解析 `@Bean` 方法**
     + 对每个 `@Configuration` 类执行解析，将其中的 `@Bean` 方法解析为bean定义。这涉及读取方法元数据，并将其转化为一个Spring可以理解和管理的 `BeanDefinition` 对象。

   + **处理bean作用域与代理**
     + 如果在 `@Bean` 方法上有 `@Scope` 注解，会根据其属性处理bean的作用域。根据需要，可能会为bean创建一个代理，这是通过 `ScopedProxyCreator` 来完成的。

   + **bean定义的注册**
     + 最终，解析出的bean定义会被注册到Spring的bean定义注册表中，这样在上下文启动后，这些定义的bean就可以被实例化和使用了。

2. **@Bean初始化源码分析总结**

   + **启动**
     + 当使用`AnnotationConfigApplicationContext`并提供`MyConfiguration`类作为参数时，Spring容器开始初始化。

   + **刷新上下文**
     +  通过调用`refresh()`方法，Spring上下文被刷新。这是在上下文中创建和初始化所有bean的关键阶段。

   + **初始化Bean工厂**
     +  方法`finishBeanFactoryInitialization`会确保所有非懒加载的单例bean都被初始化。它调用`DefaultListableBeanFactory`中的`preInstantiateSingletons`方法，该方法循环遍历容器中定义的每一个单例bean并使用`getBean`方法进行初始化。

   + **获取Bean**：
     + 在获取bean时，首先会检查是否已存在此单例bean。如果不存在，则会创建它。核心方法是`doGetBean`，它管理整个bean的生命周期，从查找bean定义到初始化bean。

   + **单例处理**
     + `getSingleton`方法管理单例bean的缓存。它首先尝试从缓存中获取bean，如果未找到，则使用提供的`ObjectFactory`创建一个新的实例。

   + **创建Bean**
     +  如果需要，`createBean`方法将负责实例化bean。最终，真正的bean创建工作是在`doCreateBean`方法中完成的。

   + **初始化Bean**
     +  一旦bean被实例化，就需要进行初始化。如果bean实现了`InitializingBean`接口，那么它的`afterPropertiesSet`方法会被调用。此外，如果bean配置中定义了自定义的初始化方法，该方法也会在此时被调用。

   + **反射调用**
     + 最终，如果定义了自定义的初始化方法，Spring会使用Java的反射API来调用它。

3. **@Bean销毁源码分析总结**

   + **启动和关闭**
     + 通过`AnnotationConfigApplicationContext`，Spring容器初始化后，`context.close()`方法会被调用以关闭容器。

   + **关闭上下文**
     + `close()`方法首先同步`startupShutdownMonitor`，确保在特定时刻只有一个线程能关闭上下文，接着调用`doClose`方法执行实际关闭操作。

   + **销毁Beans**
     + 在`doClose`方法中，`destroyBeans`方法被调用以销毁所有缓存中的单例bean。

   + **销毁单例Beans**
     + `destroyBeans`方法会调用BeanFactory的`destroySingletons`方法来销毁所有缓存的单例beans。

   + **遍历并销毁**
     + 在`destroySingletons`中，所有缓存的单例bean都会被遍历。对于每一个bean，`destroySingleton`方法会被调用来执行销毁操作。

   + **实际的销毁操作**
     + 在`destroySingleton`中，会从`disposableBeans`列表中移除对应的bean，并执行实际的销毁操作。

   + **自定义销毁逻辑**
     + 如果bean实现了`DisposableBean`接口或者定义了自定义的销毁方法，Spring容器会确保在销毁bean时调用这些方法。销毁方法可以是定义在bean配置中的任何方法，不需要特定的方法签名。

   + **反射调用销毁方法**
     + 最后，Spring使用Java反射API来动态地调用bean的自定义销毁方法。