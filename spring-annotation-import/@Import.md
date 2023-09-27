
## @Import

[TOC]

### 1、注解说明

`@Import` 是 Spring 框架中的注解，用于导入配置类或组件到当前的 Spring 上下文中。它可以用于导入常规的 `@Configuration` 类、常规组件类，或实现了 `ImportSelector` 和 `ImportBeanDefinitionRegistrar` 接口的类。`ImportSelector` 允许根据条件动态地选择要导入的组件，而 `ImportBeanDefinitionRegistrar` 提供了一种以编程方式注册bean的方法。使用 `@Import` 注解，开发者可以更灵活、模块化地组织 Spring 的配置，确保上下文中有所需的所有组件和配置。

### 2、注解源码

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

	Class<?>[] value();
}
```

### 3、字段描述

#### 3.1、value

指定了一个类数组。这些类可以是配置类、组件选择器、bean定义注册器或普通的组件类

### 4、如何使用

下面是一个`main`方法启动入口，使用`AnnotationConfigApplicationContext`作为上下文，并传入`MyConfig`作为组件类

```java
public class ImportApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

#### 4.1、导入常规的 `@Configuration` 类

通过 `@Import` 注解，它导入了 `UserConfig` 类。当Spring处理 `MyConfig` 类时，它也会处理 `UserConfig` 类，并注册在其中定义的所有beans。

```java
@Configuration
@Import(UserConfig.class)
public class MyConfig {

}
```

用 `@Configuration` 注解标记的Java配置类。这告诉Spring该类是一个配置类，并且它包含bean的定义。内部有一个名为 `user` 的方法，该方法使用 `@Bean` 注解标记。当Spring处理这个配置类时，该方法将被调用，并且其返回值（我们的一个 `User` 对象）将被注册为一个bean在Spring应用上下文中。

```java
@Configuration
public class UserConfig {

    @Bean
    public User user(){
        return new User("xcs");
    }
}
```

这是一个简单的Java类，没有特殊的注解或配置。在我们这个案例使用中，我们假设它代表一个Spring组件，但它并不需要直接使用任何Spring注解（如`@Component`或`@Service`）进行标记。

```java
public class User {

    private String name;

    public User(String name) {
        this.name = name;
    }
}
```

运行结果发现，即使我们只直接引用或使用 `MyConfig`，由于导入关系，我们也会在Spring应用上下文中得到 `UserConfig` 中定义的 `User` bean实例。

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myConfig
beanName = com.xcs.spring.config.UserConfig
beanName = user
```

#### 4.2、导入普通的组件类

`@Import` 注解在这里被用来导入 `MyService` 类。当Spring处理这个配置类时，由于 `@Import` 的存在，它会将 `MyService` 类注册为一个bean。即使 `MyService` 本身没有任何Spring相关的注解，也会如此。

```java
@Configuration
@Import(MyService.class)
public class MyConfig {

}
```

`MyService`类不需要直接使用任何Spring注解（如 `@Component`, `@Service` 等）进行标记，所以默认情况下，Spring容器不会自动将其作为一个bean来处理或识别。

```java
public class MyService {

    public String getInfo(){
        return "MyService info";
    }
}
```

运行结果发现，当Spring应用上下文中的 `MyConfig` 类使用了`@Import`注解类时，它也将自动包含 `MyService` 的一个实例。这意味着，你可以在其他组件或配置中通过依赖注入（如使用 `@Autowired`等）来注入 `MyService` 的实例。

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myConfig
beanName = com.xcs.spring.service.MyService
```

#### 4.3、使用 `ImportSelector`

`ImportSelector` 接口允许我们动态地选择要导入的组件

```java
@Configuration
@Import(CustomImportSelector.class)
public class MyConfig {

}
```

`CustomImportSelector`实现了`ImportSelector`接口的类，在`selectImports`方法需要返回一个字符串数组，在我们这个案例使用中，`CustomImportSelector`选择导入`MyBeanA`类。

```java
public class CustomImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanA.class.getName()};
    }
}
```

这是一个简单的Java类，没有特殊的注解或配置。在我们这个案例使用中，我们假设它代表一个Spring组件，但它并不需要直接使用任何Spring注解（如`@Component`或`@Service`）进行标记。

```java
public class MyBeanA {
}
```

运行结果发现，当Spring处理`MyConfig`时，它也会处理`CustomImportSelector`并最终导入`MyBeanA`，但是`CustomImportSelector`类并不作为bean类，也不会注入到IOC容器中。

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myConfig
beanName = com.xcs.spring.bean.MyBeanA
```

#### 4.4、使用 `ImportBeanDefinitionRegistrar`

编程方式使用 `ImportBeanDefinitionRegistrar` 注册bean。

```java
@Configuration
@Import(CustomRegistrar.class)
public class MyConfig {

}
```

`CustomRegistrar`类实现了`ImportBeanDefinitionRegistrar`接口，在`registerBeanDefinitions`方法中，创建了一个`RootBeanDefinition`，代表`MyBeanB`类。然后，我们将这个定义手动注册到registry中，并为它指定一个名为"`myBeanB`"的名字。

```java
public class CustomRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(MyBeanB.class);
        registry.registerBeanDefinition("myBeanB", beanDefinition);
    }
}
```

这是一个简单的Java类。但它并不需要直接使用任何Spring注解（如`@Component`、`@Service`等），因此默认情况下，Spring不会知道它或将其作为一个bean处理。

```java
public class MyBeanB {

}
```

运行结果发现，当Spring处理`MyConfig`时，它也会处理`CustomRegistrar`并最终导入`MyBeanB`，但是`CustomRegistrar`类并不作为bean类，也不会注入到IOC容器中。

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myConfig
beanName = myBeanB
```

### 5、源码分析

```java
public class ImportApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

首先我们来看看源码中的，构造函数中，执行了三个步骤，我们重点关注`refresh()`方法

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

`refresh()`方法中我们重点关注一下`invokeBeanFactoryPostProcessors(beanFactory)`这方法，其他方法不是本次源码阅读的重点暂时忽略，在`invokeBeanFactoryPostProcessors(beanFactory)`方法会对实现了`BeanDefinitionRegistryPostProcessor`，`BeanFactoryPostProcessor`这两个接口进行接口回调。

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // 同步块，确保应用上下文的刷新操作是线程安全的
    synchronized (this.startupShutdownMonitor) {
        // 开始记录应用启动的步骤
        StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");

        // 准备上下文刷新
        prepareRefresh();

        // 刷新内部的Bean工厂
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // 准备bean工厂以在此上下文中使用
        prepareBeanFactory(beanFactory);

        try {
            // 允许子类对bean工厂进行后处理
            postProcessBeanFactory(beanFactory);

            // 开始记录bean的后处理步骤
            StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");

            // 调用在上下文中注册为bean的工厂处理器
            invokeBeanFactoryPostProcessors(beanFactory);

            // 注册拦截bean创建的bean处理器
            registerBeanPostProcessors(beanFactory);

            beanPostProcess.end();

            // 初始化此上下文的消息源
            initMessageSource();

            // 初始化此上下文的事件多播器
            initApplicationEventMulticaster();

            // 初始化特定上下文子类中的其他特殊bean
            onRefresh();

            // 检查监听器beans并注册它们
            registerListeners();

            // 实例化所有剩余的(非延迟初始化)单例
            finishBeanFactoryInitialization(beanFactory);

            // 最后一步：发布相应的事件
            finishRefresh();
        }

        catch (BeansException ex) {
            // 记录异常警告信息
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
            }

            // 销毁已创建的单例以避免资源悬挂
            destroyBeans();

            // 重置'active'标志
            cancelRefresh(ex);

            // 将异常传播给调用者
            throw ex;
        }

        finally {
            // 重置Spring核心中的常见内省缓存，因为我们可能不再需要单例bean的元数据...
            resetCommonCaches();

            contextRefresh.end();
        }
    }
}
```

在`invokeBeanFactoryPostProcessors()`中又委托了`PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`进行调用

```java
/**
 * 调用在此应用上下文中注册的所有 BeanFactoryPostProcessor bean。
 *
 * @param beanFactory 此应用上下文使用的BeanFactory
 */
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    // 调用在此应用上下文中注册的所有 BeanFactoryPostProcessors。
    // 这可以在配置元数据中修改bean定义或bean的其他属性。
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

    // 在以下情况下检测 LoadTimeWeaver：
    // 1. 当前不是在Native Image环境中运行。
    // 2. 临时ClassLoader还没有设置。
    // 3. BeanFactory中存在LOAD_TIME_WEAVER_BEAN_NAME的bean定义。
    if (!NativeDetector.inNativeImage() && beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
        // 如果找到LoadTimeWeaver，添加LoadTimeWeaverAwareProcessor BeanPostProcessor。
        // 这确保了所有的LoadTimeWeaverAware beans都会接收到LoadTimeWeaver引用。
        beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));

        // 设置一个临时的ClassLoader，它可以在运行时匹配特定的类类型，这对于某些形式的织入（weaving）很有用。
        beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
    }
}
```

在这个`invokeBeanFactoryPostProcessors(beanFactory, beanFactoryPostProcessors)`方法中，主要是对`BeanDefinitionRegistryPostProcessor`，`BeanFactoryPostProcessor`这两个接口的实现类进行回调，至于为什么这个方法里面代码很长呢？其实这个方法就做了一个事就是对处理器的执行顺序在做出来。比如说要先对实现了`PriorityOrdered.class`类回调，在对实现了`Ordered.class`类回调，最后才是对没有实现任何优先级的处理器进行回调。

```java
/**
 * 调用容器中所有注册的 BeanFactoryPostProcessor。
 *
 * @param beanFactory                容器的 bean 工厂
 * @param beanFactoryPostProcessors  已经手动注册的 BeanFactoryPostProcessors
 */
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

    // 用于存储已处理的bean名称
    Set<String> processedBeans = new HashSet<>();

    // 判断bean工厂是否是BeanDefinitionRegistry
    if (beanFactory instanceof BeanDefinitionRegistry) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
        List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

        // 遍历已经存在的 BeanFactoryPostProcessors
        for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
            if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                BeanDefinitionRegistryPostProcessor registryProcessor =
                        (BeanDefinitionRegistryPostProcessor) postProcessor;
                // 调用 BeanDefinitionRegistryPostProcessor 的方法
                registryProcessor.postProcessBeanDefinitionRegistry(registry);
                registryProcessors.add(registryProcessor);
            } else {
                regularPostProcessors.add(postProcessor);
            }
        }

        // 这个列表用于暂时存储当前正在处理的 BeanDefinitionRegistryPostProcessors
        List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

        // 首先，调用实现 PriorityOrdered 接口的 BeanDefinitionRegistryPostProcessors。
        String[] postProcessorNames =
                beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
        for (String ppName : postProcessorNames) {
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                processedBeans.add(ppName);
            }
        }
        sortPostProcessors(currentRegistryProcessors, beanFactory);
        registryProcessors.addAll(currentRegistryProcessors);
        invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
        currentRegistryProcessors.clear();

        // 接着，调用实现 Ordered 接口的 BeanDefinitionRegistryPostProcessors。
        // 重复以上的步骤...

        // 最后，调用其余的 BeanDefinitionRegistryPostProcessors。
        // 重复以上的步骤...

        // 调用到目前为止处理的所有处理器的 postProcessBeanFactory 回调
        invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
    } else {
        invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
    }

    // 调用在容器中实现 PriorityOrdered、Ordered 和其他的 BeanFactoryPostProcessors
    // 实现步骤类似于上面...

    // 清除合并的bean定义的缓存
    beanFactory.clearMetadataCache();
}
```

下面是我画的一个时序图大家可以参考一下。

~~~mermaid
sequenceDiagram
    participant Init as invokeBeanFactoryPostProcessors
    participant BDRPP_PO as BDRPP(PriorityOrdered)
    participant BDRPP_O as BDRPP(Ordered)
    participant BDRPP as 其余的BDRPP
    participant BFPP_PO as BFPP(PriorityOrdered)
    participant BFPP_O as BFPP(Ordered)
    participant BFPP as 其余的BFPP

    Init->>BDRPP_PO: 回调
    BDRPP_PO-->>Init: 完成
    Init->>BDRPP_O: 回调
    BDRPP_O-->>Init: 完成
    Init->>BDRPP: 回调
    BDRPP-->>Init: 完成
    Init->>BFPP_PO: 回调
    BFPP_PO-->>Init: 完成
    Init->>BFPP_O: 回调
    BFPP_O-->>Init: 完成
    Init->>BFPP: 回调
    BFPP-->>Init: 完成
    
    Note right of BFPP: 提示: 
    Note right of BFPP: BDRPP = BeanDefinitionRegistryPostProcessor
    Note right of BFPP: BFPP = BeanFactoryPostProcessor

~~~

从截图就可以看出`ConfigurationClassPostProcessor`也实现了`BeanDefinitionRegistryPostProcessor`接口中的`postProcessBeanDefinitionRegistry`方法，其中一个重要的功能就是解析`@Import`注解。

![image-20230816101148882](https://img-blog.csdnimg.cn/851ff307c3644840b5cee1062c513b4d.png#pic_center)

`invokeBeanDefinitionRegistryPostProcessors(postProcessors)`方法中，循环调用了实现`BeanDefinitionRegistryPostProcessor`接口中的`postProcessBeanDefinitionRegistry(registry)`方法，

```java
/**
 * 调用给定的 BeanDefinitionRegistryPostProcessor bean，按它们的自然顺序。
 *
 * @param postProcessors     要调用的 BeanDefinitionRegistryPostProcessor beans
 * @param registry           BeanDefinitionRegistry 用于注册或修改bean定义
 * @param applicationStartup 应用启动跟踪器，用于Spring Boot的启动分析
 */
private static void invokeBeanDefinitionRegistryPostProcessors(
        Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors,
        BeanDefinitionRegistry registry,
        ApplicationStartup applicationStartup) {

    // 遍历所有的 BeanDefinitionRegistryPostProcessor
    for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
        // 启动Spring Boot的启动跟踪
        StartupStep postProcessBeanDefRegistry = applicationStartup.start("spring.context.beandef-registry.post-process")
            .tag("postProcessor", postProcessor::toString);
        
        // 调用每个处理器的 postProcessBeanDefinitionRegistry 方法
        postProcessor.postProcessBeanDefinitionRegistry(registry);
        
        // 结束启动跟踪
        postProcessBeanDefRegistry.end();
    }
}
```

在`ConfigurationClassPostProcessor`类中的`postProcessBeanDefinitionRegistry`回调方法中又调用了`processConfigBeanDefinitions(registry)`方法

```java
/**
 * 处理给定的 BeanDefinitionRegistry。此实现检查此后处理器是否已经被调用过，
 * 以确保每个registry只处理一次，避免重复处理。
 *
 * @param registry 需要处理的 BeanDefinitionRegistry
 */
@Override
public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    // 获取registry的唯一标识符，这里使用系统的identityHashCode作为标识符
    int registryId = System.identityHashCode(registry);

    // 检查该registry是否已经被postProcessBeanDefinitionRegistry方法处理过
    if (this.registriesPostProcessed.contains(registryId)) {
        throw new IllegalStateException(
            "postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
    }

    // 检查该registry是否已经被postProcessBeanFactory方法处理过
    if (this.factoriesPostProcessed.contains(registryId)) {
        throw new IllegalStateException(
            "postProcessBeanFactory already called on this post-processor against " + registry);
    }

    // 将当前registry的ID添加到处理过的registries集合中，表示该registry已经被处理过
    this.registriesPostProcessed.add(registryId);

    // 实际处理registry的方法
    processConfigBeanDefinitions(registry);
}
```

在`processConfigBeanDefinitions(registry)`方法中，创建一个用于解析`@Configuration`类的解析器实例(在我们的Demo中只有一个`MyBeanConfig`的配置类)，最后调用`parser.parse(candidates)`方法去解析。

```java
/**
 * 解析@Configuration注解的类，并为Spring容器注册相应的bean定义。
 * 
 * @param registry 用于注册bean定义的注册表
 */
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    // 1. 初始化配置类的候选列表
    List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
    String[] candidateNames = registry.getBeanDefinitionNames();

    // 2. 筛选出所有的配置类
    for (String beanName : candidateNames) {
        BeanDefinition beanDef = registry.getBeanDefinition(beanName);
        // 检查bean是否已被处理为配置类
        if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
            }
        }
        // 添加新的配置类候选
        else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
            configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
        }
    }

    // 3. 如果没有找到任何@Configuration类，方法会立即返回
    if (configCandidates.isEmpty()) {
        return;
    }

    // 4. 根据@Order注解排序配置类
    configCandidates.sort((bd1, bd2) -> {
        int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
        int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
        return Integer.compare(i1, i2);
    });

    // 5. 查看是否有自定义的bean命名策略
    SingletonBeanRegistry sbr = null;
    if (registry instanceof SingletonBeanRegistry) {
        sbr = (SingletonBeanRegistry) registry;
        if (!this.localBeanNameGeneratorSet) {
            BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(
                    AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR);
            if (generator != null) {
                this.componentScanBeanNameGenerator = generator;
                this.importBeanNameGenerator = generator;
            }
        }
    }

    if (this.environment == null) {
        this.environment = new StandardEnvironment();
    }

    // 6. 使用ConfigurationClassParser解析所有的配置类
    ConfigurationClassParser parser = new ConfigurationClassParser(
            this.metadataReaderFactory, this.problemReporter, this.environment,
            this.resourceLoader, this.componentScanBeanNameGenerator, registry);

    Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
    Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
    do {
        parser.parse(candidates);
        parser.validate();

        Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
        configClasses.removeAll(alreadyParsed);

        // 7. 为解析出的配置类加载bean定义
        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(
                    registry, this.sourceExtractor, this.resourceLoader, this.environment,
                    this.importBeanNameGenerator, parser.getImportRegistry());
        }
        this.reader.loadBeanDefinitions(configClasses);
        alreadyParsed.addAll(configClasses);

        candidates.clear();
        if (registry.getBeanDefinitionCount() > candidateNames.length) {
            String[] newCandidateNames = registry.getBeanDefinitionNames();
            Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
            Set<String> alreadyParsedClasses = new HashSet<>();
            for (ConfigurationClass configurationClass : alreadyParsed) {
                alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
            }
            for (String candidateName : newCandidateNames) {
                if (!oldCandidateNames.contains(candidateName)) {
                    BeanDefinition bd = registry.getBeanDefinition(candidateName);
                    if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) &&
                            !alreadyParsedClasses.contains(bd.getBeanClassName())) {
                        candidates.add(new BeanDefinitionHolder(bd, candidateName));
                    }
                }
            }
            candidateNames = newCandidateNames;
        }
    } while (!candidates.isEmpty());

    // 8. 注册ImportRegistry，支持ImportAware@Configuration类
    if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
        sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
    }

    // 9. 如果使用了缓存的元数据读取器，清除其缓存
    if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
        ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
    }
}
```

接着我们来看看`ConfigurationClassParser`类中的`parse(candidates)`方法

```java
/**
 * 解析配置候选者集合中的每个 BeanDefinition。
 *
 * @param configCandidates 配置 BeanDefinition 的集合
 */
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    // 遍历配置候选者集合
    for (BeanDefinitionHolder holder : configCandidates) {
        BeanDefinition bd = holder.getBeanDefinition();
        try {
            // 判断 BeanDefinition 的类型并进行相应的处理
            if (bd instanceof AnnotatedBeanDefinition) {
                // AnnotatedBeanDefinition 表示一个注解的bean定义
                // 解析该bean定义的元数据和bean名称
                parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
            }
            else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
                // AbstractBeanDefinition 有bean的class信息
                // 直接解析该bean定义的bean类和bean名称
                parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
            }
            else {
                // 其他情况，直接解析该bean定义的bean类名和bean名称
                parse(bd.getBeanClassName(), holder.getBeanName());
            }
        }
        // 捕获异常并抛出
        catch (BeanDefinitionStoreException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new BeanDefinitionStoreException(
                "Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
        }
    }

    // 处理延迟的导入选择器
    this.deferredImportSelectorHandler.process();
}
```

在parse使用给定的注解元数据和bean名称创建一个新的`ConfigurationClass`实例。

```java
/**
 * 基于给定的注解元数据和bean名称，解析并处理配置类。
 *
 * @param metadata   配置类上的注解元数据
 * @param beanName   bean的名称
 * @throws IOException 如果处理过程中出现IO异常
 */
protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
    // 创建一个新的 ConfigurationClass 实例，并传递注解元数据和bean名称
    // 使用默认的排除过滤器来处理该配置类
    processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
}
```

这里的核心逻辑是通过do-while循环递归地调用`doProcessConfigurationClass(configClass, sourceClass, filter)`方法，目的是处理配置类及其超类层次结构。这确保了不仅仅是直接注解了`@Configuration`的类被处理，而且它的所有超类也都被解析。

```java
/**
 * 处理给定的配置类，这涉及到解析类上的相关注解，并可能会递归处理其父类。
 *
 * @param configClass 要处理的配置类
 * @param filter      用于过滤的断言
 * @throws IOException 如果处理过程中出现IO异常
 */
protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
    // 判断配置类是否应该被跳过，例如根据其上的 @Conditional 注解
    if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationPhase.PARSE_CONFIGURATION)) {
        return;
    }

    // 检查配置类是否已经被处理过
    ConfigurationClass existingClass = this.configurationClasses.get(configClass);
    if (existingClass != null) {
        if (configClass.isImported()) {
            if (existingClass.isImported()) {
                // 如果两个类都是通过 @Import 导入的，合并它们的导入元数据
                existingClass.mergeImportedBy(configClass);
            }
            // 如果当前类是导入的，但已存在的类不是，则忽略当前类
            return;
        } else {
            // 显式的bean定义被找到，可能会替换一个导入。
            // 删除旧的配置类，并使用新的配置类。
            this.configurationClasses.remove(configClass);
            this.knownSuperclasses.values().removeIf(configClass::equals);
        }
    }

    // 递归地处理配置类及其超类层次结构。
    SourceClass sourceClass = asSourceClass(configClass, filter);
    do {
        sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
    }
    while (sourceClass != null);

    // 最后，将配置类添加到已处理的配置类映射中
    this.configurationClasses.put(configClass, configClass);
}
```

我们来到`doProcessConfigurationClass(configClass, sourceClass, filter)`方法中，我们重点关注一个核心的方法`processImports(configClass, sourceClass, getImports(sourceClass), filter, true)`

```java
/**
 * 处理给定的配置类，解析和处理相关的注解。
 *
 * @param configClass   待处理的配置类
 * @param sourceClass   配置类对应的SourceClass对象
 * @param filter        过滤器用于决定哪些类应被处理
 * @return  如果配置类有未处理的超类，返回其SourceClass；否则返回null
 * @throws IOException  处理过程中可能抛出的I/O异常
 */
protected final SourceClass doProcessConfigurationClass(
        ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
        throws IOException {

    // 1. 处理配置类的内部类
    if (configClass.getMetadata().isAnnotated(Component.class.getName())) {
        processMemberClasses(configClass, sourceClass, filter);
    }

    // 2. 处理 @PropertySource 注解
    // 加载配置属性，如从.properties或.yml文件中
    for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(
            sourceClass.getMetadata(), PropertySources.class,
            org.springframework.context.annotation.PropertySource.class)) {
        if (this.environment instanceof ConfigurableEnvironment) {
            processPropertySource(propertySource);
        } else {
            logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() +
                        "]. Reason: Environment must implement ConfigurableEnvironment");
        }
    }

    // 3. 处理 @ComponentScan 注解
    // 扫描指定包以查找Spring组件
    Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
        sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
    if (!componentScans.isEmpty() &&
        !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
        // ... [代码部分省略以简化]

    // 4. 处理 @Import 注解
    processImports(configClass, sourceClass, getImports(sourceClass), filter, true);

    // 5. 处理 @ImportResource 注解
    // 用于从XML文件中导入配置
    AnnotationAttributes importResource =
        AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ImportResource.class);
    if (importResource != null) {
        // ... [代码部分省略以简化]

    // 6. 处理 @Bean 方法
    Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
    for (MethodMetadata methodMetadata : beanMethods) {
        configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
    }

    // 7. 处理配置类实现的接口上的方法
    processInterfaces(configClass, sourceClass);

    // 8. 如果配置类有超类，递归处理它
    if (sourceClass.getMetadata().hasSuperClass()) {
        String superclass = sourceClass.getMetadata().getSuperClassName();
        if (superclass != null && !superclass.startsWith("java") &&
            !this.knownSuperclasses.containsKey(superclass)) {
            this.knownSuperclasses.put(superclass, configClass);
            return sourceClass.getSuperClass();
        }
    }

    // 处理完成，返回 null
    return null;
}
```

我们先来看看`getImports`方法从指定的`sourceClass`中收集所有的导入类。它首先初始化两个`LinkedHashSet`集合：`imports`用于存储收集到的导入，而`visited`记录已经处理过的类以避免循环。然后，它递归地调用`collectImports`方法来填充`imports`集合。最后，返回收集到的导入。这个逻辑确保了所有通过`@Import`注解指定的类被正确地识别和返回。

```java
private Set<SourceClass> getImports(SourceClass sourceClass) throws IOException {
   Set<SourceClass> imports = new LinkedHashSet<>();
   Set<SourceClass> visited = new LinkedHashSet<>();
   collectImports(sourceClass, imports, visited);
   return imports;
}
```

`collectImports`是一个递归方法，目的是从指定的`sourceClass`及其注解中收集通过`@Import`指定的所有导入类。首先，检查`sourceClass`是否已被访问过，以避免无限递归。然后，遍历`sourceClass`上的所有注解。对于每一个注解，如果它不是`@Import`，则对其递归调用`collectImports`，这是因为注解上也可能存在其他元注解，它们也可能包含`@Import`信息。在处理所有注解后，该方法会从`sourceClass`中提取`@Import`注解的`value`属性（即要导入的类列表），并将其添加到`imports`集合中。这种递归收集机制确保了从`sourceClass`及其所有相关注解中正确、完整地收集所有`@Import`信息。

```java
/**
 * 从给定的sourceClass及其注解中递归收集使用@Import注解导入的所有类。
 * 
 * @param sourceClass     起始的收集处理类。
 * @param imports         存储找到的导入的集合。
 * @param visited         记录已经处理过的类，以避免无限递归。
 * @throws IOException    读取元数据时可能出现的异常。
 */
private void collectImports(SourceClass sourceClass, Set<SourceClass> imports, Set<SourceClass> visited)
      throws IOException {

   // 如果sourceClass尚未被访问过，那么处理它。
   if (visited.add(sourceClass)) {

      // 遍历sourceClass上的所有注解。
      for (SourceClass annotation : sourceClass.getAnnotations()) {

         // 获取当前注解的完整类名。
         String annName = annotation.getMetadata().getClassName();

         // 如果当前注解不是@Import，则继续递归处理此注解。
         // 因为注解上也可能存在其它元注解，并且它们也可能包含@Import信息。
         if (!annName.equals(Import.class.getName())) {
            collectImports(annotation, imports, visited);
         }
      }

      // 从sourceClass中获取@Import注解的value属性（即导入的类）并将其添加到imports集合。
      imports.addAll(sourceClass.getAnnotationAttributes(Import.class.getName(), "value"));
   }
}
```

我们在回到`doProcessConfigurationClass`方法上来，在`doProcessConfigurationClass`方法中调用了`getImports(sourceClass)`方法获取到使用`@Import`注解导入的类，然后调用`processImports`方法进行处理

```java
// 4. 处理 @Import 注解
processImports(configClass, sourceClass, getImports(sourceClass), filter, true);
```

该`processImports`方法专门处理通过`@Import`注解引入的配置类。首先，它检查是否存在要导入的类，如果没有，则直接返回。方法还对循环导入进行检查，如果检查到，此方法会立即报告错误。后续主要有三个部分，下面会为大家一一解析这三部分。

```java
/**
 * 处理通过@Import注解导入的类。
 * 
 * @param configClass            当前正在处理的配置类。
 * @param currentSourceClass     当前来源类。
 * @param importCandidates       通过@Import注解指定需要导入的类的集合。
 * @param exclusionFilter        用于排除某些类的过滤器。
 * @param checkForCircularImports 是否需要检查循环导入。
 */
private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
                            Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
                            boolean checkForCircularImports) {

    // 如果没有导入的候选类，直接返回。
    if (importCandidates.isEmpty()) {
        return;
    }

    // 检查循环导入，如果存在则报告错误。
    if (checkForCircularImports && isChainedImportOnStack(configClass)) {
        this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
    }
    else {
        // 将当前配置类压入导入栈。
        this.importStack.push(configClass);
        try {
            // 遍历所有导入的候选类。
            for (SourceClass candidate : importCandidates) {
                // 如果候选类是ImportSelector，则委托给它确定进一步的导入。
                if (candidate.isAssignable(ImportSelector.class)) {
                    Class<?> candidateClass = candidate.loadClass();
                    ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
                                                                                   this.environment, this.resourceLoader, this.registry);
                    Predicate<String> selectorFilter = selector.getExclusionFilter();
                    if (selectorFilter != null) {
                        exclusionFilter = exclusionFilter.or(selectorFilter);
                    }
                    if (selector instanceof DeferredImportSelector) {
                        // 如果是DeferredImportSelector，进行特定处理。
                        this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
                    }
                    else {
                        // 否则，获取选择器指定的导入并递归处理。
                        String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
                        Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
                        processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
                    }
                }
                // 如果候选类是ImportBeanDefinitionRegistrar，则委托给它注册额外的bean定义。
                else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
                    Class<?> candidateClass = candidate.loadClass();
                    ImportBeanDefinitionRegistrar registrar =
                        ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
                                                             this.environment, this.resourceLoader, this.registry);
                    configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
                }
                else {
                    // 如果既不是ImportSelector也不是ImportBeanDefinitionRegistrar，处理它作为一个@Configuration类。
                    this.importStack.registerImport(
                        currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
                    processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter);
                }
            }
        }
        // 捕获并处理可能的异常。
        catch (BeanDefinitionStoreException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new BeanDefinitionStoreException(
                "处理配置类[" + configClass.getMetadata().getClassName() + "]的导入候选时出错。", ex);
        }
        finally {
            // 清理操作：从导入栈中弹出当前配置类。
            this.importStack.pop();
        }
    }
}
```

**第一部分**

如果是`ImportSelector`，则实例化它，获取其提供的导入，并进一步处理这些导入。如果它是`DeferredImportSelector`，则进行特殊处理，首先调用`selector.selectImports(currentSourceClass.getMetadata())`获取方法的导入类名，然后通过递归调用`processImports`方法来处理选择器提供的导入。

```java
if (candidate.isAssignable(ImportSelector.class)) {
    Class<?> candidateClass = candidate.loadClass();
    ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
                                                                   this.environment, this.resourceLoader, this.registry);
    Predicate<String> selectorFilter = selector.getExclusionFilter();
    if (selectorFilter != null) {
        exclusionFilter = exclusionFilter.or(selectorFilter);
    }
    if (selector instanceof DeferredImportSelector) {
        // 如果是DeferredImportSelector，进行特定处理。
        this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
    }
    else {
        // 否则，获取选择器指定的导入并递归处理。
        String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
        Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
        processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
    }
}
```

通过调用`selector.selectImports(importingClassMetadata)`方法，最终会调用到我们自定义的`CustomImportSelector`类

```java
public class CustomImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MyBeanA.class.getName()};
    }
}
```

![image-20230828173216747](https://img-blog.csdnimg.cn/40e1e0ec2a7f46fabf66b6c637e0fada.png#pic_center)

**第二部分**

如果是`ImportBeanDefinitionRegistrar`，则实例化它，并通过`configClass.addImportBeanDefinitionRegistrar`方法将其添加到`configClass`，这样它可以在稍后的处理中注册额外的bean定义。

```java
if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
    Class<?> candidateClass = candidate.loadClass();
    ImportBeanDefinitionRegistrar registrar =
        ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
                                             this.environment, this.resourceLoader, this.registry);
    configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
}
```

一旦`ImportBeanDefinitionRegistrar`实例化，它就被添加到当前处理的配置类`configClass`中。稍后，当Spring解析完成所有的配置类后，这些注册器将被用于添加额外的bean定义到Spring容器。

```java
void addImportBeanDefinitionRegistrar(ImportBeanDefinitionRegistrar registrar, AnnotationMetadata importingClassMetadata) {
    this.importBeanDefinitionRegistrars.put(registrar, importingClassMetadata);
}
```

最终初始化后的`ImportBeanDefinitionRegistrar`类，存储在`ConfigurationClass`中的`importBeanDefinitionRegistrars`字段，这样spring就可以在稍后的处理中注册额外的bean定义。

```java
final class ConfigurationClass {
    // ... [代码部分省略以简化]
    private final Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> importBeanDefinitionRegistrars =
                new LinkedHashMap<>();
    // ... [代码部分省略以简化]
}
```

**第三部分**

在第三部分中，但它特别关注那些既不是`ImportSelector`也不是`ImportBeanDefinitionRegistrar`的导入类。如果一个类被`@Import`导入但它并不实现上述两个特殊的接口，则它通常会被视为一个普通的`@Configuration`类，并进一步调用`processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter)`方法进行处理。

```java
else {
    // Candidate class not an ImportSelector or ImportBeanDefinitionRegistrar ->
    // process it as an @Configuration class
    this.importStack.registerImport(
        currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
    processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter);
}
```

好了，我们中途做一个小小的总结

+ 当我们通过`@Import`导入一个实现了`ImportSelector`接口的类，首先会实例化这个类。接着会判断是否为`DeferredImportSelector`接口，如果是则存入`ConfigurationClassParser`类中的`deferredImportSelectorHandler`字段，以便后续做延迟导出处理。然后再处理实现了`ImportSelector`的接口，根据该实现类返回的类名路径（例如`CustomImportSelector`），系统会再次调用`processImports()`方法进行递归处理。在第二次递归过程中，返回的类名路径会被视为一个既不是`ImportSelector`也不是`ImportBeanDefinitionRegistrar`的导入类，相当于一个普通的`@Configuration`类。它会进一步调用`processConfigurationClass()`方法进行处理，并最终被解析成一个`ConfigurationClass`对象，存储在`ConfigurationClassParser`类的`configurationClasses`字段中，以便后续处理。
+ 当我们通过`@Import`导入一个实现了`ImportBeanDefinitionRegistrar`接口的类，它首先会被实例化。实例化后的对象会暂时存储在`ConfigurationClass`的`importBeanDefinitionRegistrars`字段中，等待后续处理。
+ 如果我们通过`@Import`直接导入一个普通类（既不是`ImportSelector`也不是`ImportBeanDefinitionRegistrar`），这个类会直接被解析成一个`ConfigurationClass`对象，并存储在`ConfigurationClassParser`类的`configurationClasses`字段中，以便后续处理。

下面是我总结后的流程图，大家可以参考一下。

~~~mermaid
graph TD;

processConfigClass[processConfigurationClass] --> processImports

processImports --> typeDecision[判断Imported注解导入的类型]

typeDecision --> config[Configuration类]
typeDecision --> normal[普通类]
typeDecision --> selector[ImportSelector类]
typeDecision --> deferred[DeferredImportSelector类]
typeDecision --> registrar[ImportBeanDefinitionRegistrar类]

config --> parseConfig[解析成ConfigurationClass类] 
parseConfig --> registerBeans[存储在configurationClasses字段中]
normal --> registerBean[存储在configurationClasses字段中]
normal --> processConfigClass

selector --> instantiateSelector[实例化ImportSelector]
instantiateSelector --> callSelectImports[调用selectImports,在递归回去处理]
callSelectImports --> processImports

deferred --> delayProcess[调用延迟处理deferredImportSelectorHandler.process方法] 
delayProcess --> callSelectImportsDeferred[调用selectImports,在递归回去处理]
callSelectImportsDeferred --> processImports

registrar --> instantiateRegistrar[实例化ImportBeanDefinitionRegistrar] 
instantiateRegistrar --> manualRegister[存储在importBeanDefinitionRegistrars字段中]

~~~



我们再次回到源码分析中来，上面刚刚分析的都是在`ConfigurationClassPostProcessor`类中的`processConfigBeanDefinitions`方法中的`parser.parse(candidates)`中关于`@Import`注解的解析过程，我们接下来看`this.reader.loadBeanDefinitions(configClasses)`方法的注册过程。

```java
/**
 * 解析@Configuration注解的类，并为Spring容器注册相应的bean定义。
 * 
 * @param registry 用于注册bean定义的注册表
 */
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    // 1. 初始化配置类的候选列表
    List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
    String[] candidateNames = registry.getBeanDefinitionNames();

    // 2. 筛选出所有的配置类
    for (String beanName : candidateNames) {
        BeanDefinition beanDef = registry.getBeanDefinition(beanName);
        // 检查bean是否已被处理为配置类
        if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
            }
        }
        // 添加新的配置类候选
        else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
            configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
        }
    }

    // 3. 如果没有找到任何@Configuration类，方法会立即返回
    if (configCandidates.isEmpty()) {
        return;
    }

    // 4. 根据@Order注解排序配置类
    configCandidates.sort((bd1, bd2) -> {
        int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
        int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
        return Integer.compare(i1, i2);
    });

    // 5. 查看是否有自定义的bean命名策略
    SingletonBeanRegistry sbr = null;
    if (registry instanceof SingletonBeanRegistry) {
        sbr = (SingletonBeanRegistry) registry;
        if (!this.localBeanNameGeneratorSet) {
            BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(
                    AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR);
            if (generator != null) {
                this.componentScanBeanNameGenerator = generator;
                this.importBeanNameGenerator = generator;
            }
        }
    }

    if (this.environment == null) {
        this.environment = new StandardEnvironment();
    }

    // 6. 使用ConfigurationClassParser解析所有的配置类
    ConfigurationClassParser parser = new ConfigurationClassParser(
            this.metadataReaderFactory, this.problemReporter, this.environment,
            this.resourceLoader, this.componentScanBeanNameGenerator, registry);

    Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
    Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
    do {
        parser.parse(candidates);
        parser.validate();

        Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
        configClasses.removeAll(alreadyParsed);

        // 7. 为解析出的配置类加载bean定义
        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(
                    registry, this.sourceExtractor, this.resourceLoader, this.environment,
                    this.importBeanNameGenerator, parser.getImportRegistry());
        }
        this.reader.loadBeanDefinitions(configClasses);
        alreadyParsed.addAll(configClasses);

        candidates.clear();
        if (registry.getBeanDefinitionCount() > candidateNames.length) {
            String[] newCandidateNames = registry.getBeanDefinitionNames();
            Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
            Set<String> alreadyParsedClasses = new HashSet<>();
            for (ConfigurationClass configurationClass : alreadyParsed) {
                alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
            }
            for (String candidateName : newCandidateNames) {
                if (!oldCandidateNames.contains(candidateName)) {
                    BeanDefinition bd = registry.getBeanDefinition(candidateName);
                    if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) &&
                            !alreadyParsedClasses.contains(bd.getBeanClassName())) {
                        candidates.add(new BeanDefinitionHolder(bd, candidateName));
                    }
                }
            }
            candidateNames = newCandidateNames;
        }
    } while (!candidates.isEmpty());

    // 8. 注册ImportRegistry，支持ImportAware@Configuration类
    if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
        sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
    }

    // 9. 如果使用了缓存的元数据读取器，清除其缓存
    if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
        ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
    }
}
```

 在`ConfigurationClassBeanDefinitionReader`类中的`loadBeanDefinitions`方法，在此循环中，方法会遍历传入的每一个`ConfigurationClass`实例。对于每个实例，它都调用`loadBeanDefinitionsForConfigurationClass`方法，该方法的任务是从当前的`ConfigurationClass`加载bean定义。`trackedConditionEvaluator`传入此方法以确保正确处理任何`@Conditional`注解。

```java
public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
    TrackedConditionEvaluator trackedConditionEvaluator = new TrackedConditionEvaluator();
    for (ConfigurationClass configClass : configurationModel) {
        loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator);
    }
}
```

我们来看一下`loadBeanDefinitionsForConfigurationClass`方法，在第二个步骤中如果判断出`ConfigurationClass`是否为一个配置类，如果是则表示由其他类导入的，然后继续调用`registerBeanDefinitionForImportedConfigurationClass(configClass)`方法来处理其bean定义。

```java
/**
 * 为指定的@Configuration类加载bean定义。
 *
 * @param configClass 要加载bean定义的配置类
 * @param trackedConditionEvaluator 用于评估@Conditional注解的追踪条件评估器
 */
private void loadBeanDefinitionsForConfigurationClass(
        ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {

    // 1. 使用追踪条件评估器检查是否应该跳过此配置类
    if (trackedConditionEvaluator.shouldSkip(configClass)) {
        String beanName = configClass.getBeanName();

        // 如果配置类已经被注册并现在应该跳过，则从注册表中移除该bean定义
        if (StringUtils.hasLength(beanName) && this.registry.containsBeanDefinition(beanName)) {
            this.registry.removeBeanDefinition(beanName);
        }

        // 从导入注册表中移除当前配置类
        this.importRegistry.removeImportingClass(configClass.getMetadata().getClassName());

        // 既然我们决定跳过此配置类，没有进一步的处理是必要的
        return;
    }

    // 2. 如果配置类是由其他类导入的，处理其bean定义
    if (configClass.isImported()) {
        registerBeanDefinitionForImportedConfigurationClass(configClass);
    }

    // 3. 处理配置类中的所有@Bean方法，并为每个方法加载bean定义
    for (BeanMethod beanMethod : configClass.getBeanMethods()) {
        loadBeanDefinitionsForBeanMethod(beanMethod);
    }

    // 4. 加载由配置类导入的任何其他资源（例如，通过@ImportResource导入的XML文件）
    loadBeanDefinitionsFromImportedResources(configClass.getImportedResources());

    // 5. 为配置类处理所有的Bean定义注册器（由@Import导入的类）
    loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
}
```

`registerBeanDefinitionForImportedConfigurationClass`方法主要目的就是处理`@Import`注解导入的配置类都会被正确注册到Spring容器中。

```java
private void registerBeanDefinitionForImportedConfigurationClass(ConfigurationClass configClass) {
    // 1. 获取配置类的注解元数据
    AnnotationMetadata metadata = configClass.getMetadata();

    // 2. 根据元数据创建Bean定义
    AnnotatedGenericBeanDefinition configBeanDef = new AnnotatedGenericBeanDefinition(metadata);

    // 3. 解析并设置Bean的作用域（如：单例、原型）
    ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(configBeanDef);
    configBeanDef.setScope(scopeMetadata.getScopeName());

    // 4. 生成Bean的名称
    String configBeanName = this.importBeanNameGenerator.generateBeanName(configBeanDef, this.registry);

    // 5. 处理常见的注解定义，例如@Lazy, @Primary等
    AnnotationConfigUtils.processCommonDefinitionAnnotations(configBeanDef, metadata);

    // 6. 创建Bean定义的持有者并应用适当的作用域代理模式（如有必要）
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(configBeanDef, configBeanName);
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);

    // 7. 在Bean注册表中注册Bean定义
    this.registry.registerBeanDefinition(definitionHolder.getBeanName(), definitionHolder.getBeanDefinition());

    // 8. 将生成的Bean名称设置为ConfigurationClass
    configClass.setBeanName(configBeanName);

    // 9. 日志跟踪：记录已注册的Bean定义信息
    if (logger.isTraceEnabled()) {
        logger.trace("Registered bean definition for imported class '" + configBeanName + "'");
    }
}
```

在`loadBeanDefinitionsForConfigurationClass`方法的关键第五步，我们执行了`loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars())`方法。首先，通过`configClass.getImportBeanDefinitionRegistrars()`，我们从`ConfigurationClass`的`importBeanDefinitionRegistrars`字段中提取了缓存的`ImportBeanDefinitionRegistrar`实例。这些实例在先前的解析过程中，因为遇到`@Import`注解而被缓存（在前面部分我们分析了如何存储`ImportBeanDefinitionRegistrar`类到`importBeanDefinitionRegistrars`字段的过程，如果不清楚，请在返回去看看）。接下来，`loadBeanDefinitionsFromRegistrars`方法遍历所有这些注册器。对于每一个注册器，Spring调用其`registerBeanDefinitions`方法，允许我们根据需要定制地注册`BeanDefinitions`。这个流程强调了Spring在配置处理过程中的灵活性，给我们程序员提供了深度定制的可能性，比如`OpenFeign`框架，`MyBatis`框架都用到了`ImportBeanDefinitionRegistrar`机制

```java
// 5. 为配置类处理所有的Bean定义注册器（由@Import导入的类）
loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());

Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> getImportBeanDefinitionRegistrars() {
    return this.importBeanDefinitionRegistrars;
}

private void loadBeanDefinitionsFromRegistrars(Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> registrars) {
    registrars.forEach((registrar, metadata) ->
                       registrar.registerBeanDefinitions(metadata, this.registry, this.importBeanNameGenerator));
}
```

在我们自定义的`CustomRegistrar`类中，我们可以自己注册跟自己业务相关的`BeanDefinition`，比如我在此处注册了一个`MyBeanB`类的`RootBeanDefinition`。

```java
public class CustomRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(MyBeanB.class);
        registry.registerBeanDefinition("myBeanB", beanDefinition);
    }
}
```

### 6、常见问题

#### 6.1、@Import注解引起循环导入

在下面的这个错误例子中，`ConfigA` 导入了 `ConfigB`，而 `ConfigB` 又导入了 `ConfigA`，这会导致循环导入。

```java
@Configuration
@Import(ConfigB.class)
public class ConfigA {
    
}
```

```java
@Configuration
@Import(ConfigA.class)
public class ConfigB {

}
```

**如何解决@Import注解循环导入问题呢？**

要解决循环导入的问题，我们需要重新组织项目里面的配置，确保bean之间没有循环依赖。我们可能考虑从以下三点入手。

+ 将共同的 beans 移动到一个单独的配置类
+ 重新评估你的配置模块化策略
+ 考虑使用 `@ComponentScan`

下面这个例子中`ConfigA` 和 `ConfigB` 都导入了 `CommonConfig`，而不是相互导入，从而避免了循环导入的问题。

```java
@Configuration
public class CommonConfig {
    // 定义 ConfigA 和 ConfigB 都需要的 beans
}
```

```java
@Configuration
@Import(CommonConfig.class)
public class ConfigA {
    // 定义只有 ConfigA 需要的 beans
}
```

```java
@Configuration
@Import(CommonConfig.class)
public class ConfigB {
    // 定义只有 ConfigB 需要的 beans
}
```

#### 6.2、@Import注解顺序引起Bean定义的覆盖

在下面的这个错误例子中， `ConfigB` 会覆盖 `ConfigA` 中的同名 `myBean` 定义，这会导致Bean定义的覆盖。

```java
public class MyBean {

    private String describe;

    public MyBean(String describe) {
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "describe='" + describe + '\'' +
                '}';
    }
}
```

```java
@Configuration
public class ConfigA {
    
    @Bean
    public MyBean myBean() {
        return new MyBean("Bean from ConfigA");
    }
}
```

```java
@Configuration
public class ConfigB {
    
    @Bean
    public MyBean myBean() {
        return new MyBean("Bean from ConfigB");
    }
}
```

```java
@Configuration
// ConfigB 会覆盖 ConfigA 中的同名 myBean 定义
@Import({ConfigA.class, ConfigB.class}) 
public class MainConfig {
    
}
```

如果你现在访问 `myBean`，并打印`MyBean`中的`ToString`方法会得到值 "Bean from ConfigB"，因为 `ConfigB` 的定义覆盖了 `ConfigA` 的定义。

要避免不必要的冲突和混淆，最好确保每个配置类中的 bean 名称是唯一的，或者至少你清楚哪个定义应该被保留，并能够预测结果。如果确实需要覆盖某个 bean 定义，确保这是一个明确的决策，并在代码中进行适当的注释，以帮助其他开发者理解你的意图。

#### 6.3、@Import注解顺序引起生命周期和初始化逻辑

在下面的这个错误例子的 `MainConfig` 中，我们使用 `@Import` 导入了 `BeanBConfig` 和 `BeanAConfig`。但是，由于 `BeanB` 依赖于 `BeanA`，而导入的顺序是反的，这会导致初始化 `BeanB` 时抛出异常，因为此时 `BeanA` 还没有完全初始化。

```java
@Configuration
public class BeanAConfig {

    @Bean
    public BeanA beanA() {
        System.out.println("BeanA is being created...");
        return new BeanA();
    }

    static class BeanA {
        public boolean isReady = false;

        public BeanA() {
            // 模拟BeanA初始化过程中的一些延迟。
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isReady = true;
            System.out.println("BeanA is now ready.");
        }
    }
}
```

```java
@Configuration
public class BeanBConfig {

    @Bean(initMethod = "initialize")
    public BeanB beanB() {
        System.out.println("BeanB is being created...");
        return new BeanB();
    }

    static class BeanB {
        public BeanB() {
            BeanA beanAInstance = ApplicationContextHolder.getBean(BeanA.class);
            if (!beanAInstance.isReady) {
                throw new IllegalStateException("BeanA is not ready when BeanB is being initialized!");
            }
        }

        public void initialize() {
            System.out.println("BeanB initialized using the state from BeanA.");
        }
    }
}
```

```java
@Configuration
// 注意这里的导入顺序，导致BeanB先于BeanA初始化
@Import({BeanBConfig.class, BeanAConfig.class}) 
public class MainConfig {

}
```

```java
@Configuration
class ApplicationContextHolder {
    private static ApplicationContext context;

    @Autowired
    public void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
```

当你尝试加载 `MainConfig` 的 Spring 上下文时，我们在控制台会输出：

```
BeanB is being created...
Exception in thread "main" java.lang.IllegalStateException: BeanA is not ready when BeanB is being initialized!
```

### 7、总结

在我们阅读完`@Import`注解的源码分析后，我们来做一个总结。首先，在第一部分中，我们对`@Import`注解进行了说明。在说明中，我们分别介绍了`@Import`注解可以导入的三种类型。第一种是常规的`@Configuration`类和常规组件类；第二种是实现了`ImportSelector`接口的类；第三种是实现了`ImportBeanDefinitionRegistrar`接口的类。在第二部分中，我们展示了`@Import`注解的源码。在这个源码中，注解定义了一个value字段。在第三部分中，我们对这个value字段进行了介绍。这个字段指定了一个类数组，这些类可以是配置类、组件选择器、bean定义注册器或普通的组件类。在第四部分中，我们分了四个小点来分别介绍如何使用这个注解。第一点是通过`@Import`注解来导入`UserConfig`类。当Spring处理`MyConfig`类时，它会同时处理`UserConfig`类，并注册在其中定义的所有beans。第二点是`@Import`注解在这里被用来导入`MyService`类。当Spring处理这个配置类时，它会将`MyService`类注册为一个bean，即使`MyService`本身没有任何Spring相关的注解。第三点是`ImportSelector`接口。这个接口允许我们动态地选择要导入的组件。在我们这个例子中，`CustomImportSelector`选择导入了`MyBeanA`类。第四点是编程方式使用`ImportBeanDefinitionRegistrar`来注册bean。`CustomRegistrar`类就是实现了这个接口，在这个类的`registerBeanDefinitions`方法中，创建了一个`RootBeanDefinition`，代表`MyBeanB`类。然后，我们将这个定义手动注册到registry中，并为它指定一个名为"myBeanB"的名字。在第五部分中，我们对`@Import`注解进行了源码分析。最开始是由Spring中的`refresh()`方法起到的关键作用，其中，`invokeBeanFactoryPostProcessors(beanFactory)`是核心方法。它处理了`BeanDefinitionRegistryPostProcessor`和`BeanFactoryPostProcessor`的回调。`@Import`的功能远超过直接导入配置类。它的设计允许动态导入配置和手动注册bean，给予开发者极大的灵活性。最后，我们也讨论了与`@Import`注解相关的常见问题，包括由于`@Import`引起的循环导入、导入顺序导致的Bean定义的覆盖，以及导入顺序影响的生命周期和初始化逻辑等问题。这里就不过多介绍了，有兴趣的可以回头去看常见问题分析。

