## BeanDefinitionRegistryPostProcessor

- [BeanDefinitionRegistryPostProcessor](#beandefinitionregistrypostprocessor)
  - [一、接口描述](#一接口描述)
  - [二、接口源码](#二接口源码)
  - [三、主要功能](#三主要功能)
  - [四、使用示例](#四使用示例)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)


### 一、接口描述

`BeanDefinitionRegistryPostProcessor` 是 Spring 框架中的一个接口，它用于在 Spring 容器的标准初始化过程中允许我们修改应用程序上下文的内部 bean 定义。它继承自 `BeanFactoryPostProcessor` 接口。与 `BeanFactoryPostProcessor` 不同的是，它还提供了对 `BeanDefinitionRegistry` 的访问，这使得我们能够在运行时注册新的 beans 或修改现有的 bean 定义。

### 二、接口源码

`InstantiationAwareBeanPostProcessor` 是 Spring 框架自 3.0.1 版本开始引入的一个核心接口。最核心的方法是 `postProcessBeanDefinitionRegistry`，它允许我们在运行时注册新的 beans 或修改现有的 bean 定义。

```java
/**
 * 相对于标准的 {@link BeanFactoryPostProcessor} SPI 的扩展，
 * 允许在常规 BeanFactoryPostProcessor 检测启动之前 注册更多的 bean 定义。
 * 特别地，BeanDefinitionRegistryPostProcessor 可以注册进一步的 bean 定义，
 * 这些定义可能会进一步定义 BeanFactoryPostProcessor 实例。
 *
 * 作者：Juergen Hoeller
 * 自版本：3.0.1 起
 * 参见：org.springframework.context.annotation.ConfigurationClassPostProcessor
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

    /**
     * 在其标准初始化之后，修改应用上下文的内部 bean 定义注册表。
     * 此时，所有常规的 bean 定义都已经被加载，但还没有 bean 被实例化。
     * 这允许在下一后处理阶段开始之前，添加更多的 bean 定义。
     * 
     * @param registry 应用上下文使用的 bean 定义注册表
     * @throws org.springframework.beans.BeansException 如果发生错误
     */
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
```

### 三、主要功能

+ **注册新的 Bean 定义**：该接口提供了一个机制，允许在 Spring 容器完成其标准初始化（即加载所有 bean 定义）之后，但在任何 bean 实例化之前，动态注册新的 bean 定义。
+ **修改现有的 Bean 定义**：除了能够添加新的 bean 定义，`BeanDefinitionRegistryPostProcessor` 还可以修改已经注册的 bean 定义。例如，它可以修改 bean 的属性值、构造函数参数或其它设置。
+ **控制 `BeanFactoryPostProcessor` 的执行顺序**：因为 `BeanDefinitionRegistryPostProcessor` 是 `BeanFactoryPostProcessor` 的子接口，它的实现还可以控制 `BeanFactoryPostProcessor` 的执行顺序。这是因为在 Spring 容器启动时，所有的 `BeanDefinitionRegistryPostProcessor` beans 首先会被实例化和调用，然后才是其他的 `BeanFactoryPostProcessor` beans。
+ **基于条件的 Bean 注册**：可以利用 `BeanDefinitionRegistryPostProcessor` 来基于特定的运行时条件（例如类路径上是否存在某个特定的类）来决定是否注册某个 bean。
+ **扩展点以实现高级配置**：对于复杂的应用或框架，这个接口提供了一个扩展点，可以在初始化过程中进行更高级的配置，如加载外部的配置或执行特殊的验证逻辑。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后我们从`AnnotationConfigApplicationContext`中获取`MySimpleBean`并调用`show`方法。

```java
public class BeanDefinitionRegistryPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MySimpleBean mySimpleBean1 = context.getBean(MySimpleBean.class);
        mySimpleBean1.show();
    }
}
```

这里使用`@Bean`注解，为了确保 `MyBeanDefinitionRegistryPostProcessor` 被 Spring 容器执行，你需要将它注册为一个 bean，该后处理器可以新增一个`BeanDefinition`。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyBeanDefinitionRegistryPostProcessor myBeanDefinitionRegistryPostProcessor(){
        return new MyBeanDefinitionRegistryPostProcessor();
    }
}
```

在我自定义的`postProcessBeanDefinitionRegistry` 方法中，创建了一个新的 `RootBeanDefinition` 对象，该对象代表 `MySimpleBean` 类。然后，使用了 `registry` 的 `registerBeanDefinition` 方法来注册这个新的 bean 定义，并为它指定了名称 `"mySimpleBean"`。

```java
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("开始新增Bean定义");
        // 创建一个新的 BeanDefinition 对象
        BeanDefinition beanDefinition = new RootBeanDefinition(MySimpleBean.class);
        // 使用 registry 来注册这个新的 bean 定义
        registry.registerBeanDefinition("mySimpleBean", beanDefinition);
        System.out.println("完成新增Bean定义");
    }
}
```

要被动态注册的 Bean

```java
public class MySimpleBean {

    public void show() {
        System.out.println("MySimpleBean instance: " + this);
    }
}
```

运行结果发现，`MySimpleBean` 实例已成功创建，并打印了它的实例信息，这证明了 `BeanDefinitionRegistryPostProcessor` 成功地在运行时动态注册了这个 bean

```java
开始新增Bean定义
完成新增Bean定义
MySimpleBean instance: com.xcs.spring.config.MySimpleBean@7e5afaa6
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: BeanFactoryPostProcessor时序图
    participant BeanDefinitionRegistryPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant PostProcessorRegistrationDelegate
    participant MyBeanDefinitionRegistryPostProcessor
    participant BeanDefinitionRegistry
    
    BeanDefinitionRegistryPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(...)<br>启动上下文
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()
    AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(...)<br>触发整个BeanFactoryPostProcessor调用的流程
    AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(...)<br>确保正确的顺序触发BeanDefinitionRegistryPostProcessor调用的流程
    PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(...)<br>最终对BeanDefinitionRegistryPostProcessor接口回调
    PostProcessorRegistrationDelegate->>MyBeanDefinitionRegistryPostProcessor:postProcessBeanDefinitionRegistry(...)<br>执行自定义的逻辑
    MyBeanDefinitionRegistryPostProcessor-->>BeanDefinitionRegistry:通过新增bean定义
    BeanDefinitionRegistry-->>MyBeanDefinitionRegistryPostProcessor:新增已完成
    PostProcessorRegistrationDelegate-->>AbstractApplicationContext: 调用Bean工厂后置处理器完成
    AnnotationConfigApplicationContext->>BeanDefinitionRegistryPostProcessorApplication:初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后我们从`AnnotationConfigApplicationContext`中获取`MySimpleBean`并调用`show`方法。

```java
public class BeanDefinitionRegistryPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MySimpleBean mySimpleBean1 = context.getBean(MySimpleBean.class);
        mySimpleBean1.show();
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

在`org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`方法中，主要是对`BeanDefinitionRegistryPostProcessor`，`BeanFactoryPostProcessor`这两个接口的实现类进行回调，至于为什么这个方法里面代码很长呢？其实这个方法就做了一个事就是对处理器的执行顺序在做处理。比如说要先对实现了`PriorityOrdered.class`类回调，在对实现了`Ordered.class`类回调，最后才是对没有实现任何优先级的处理器进行回调。

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

    // 先调用 BeanDefinitionRegistryPostProcessors (如果有的话)
    Set<String> processedBeans = new HashSet<>();

    // 判断 beanFactory 是否为 BeanDefinitionRegistry 的实例
    if (beanFactory instanceof BeanDefinitionRegistry) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
        List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

        // 遍历所有的后处理器，按类型分类
        for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
            if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                BeanDefinitionRegistryPostProcessor registryProcessor =
                    (BeanDefinitionRegistryPostProcessor) postProcessor;
                registryProcessor.postProcessBeanDefinitionRegistry(registry);
                registryProcessors.add(registryProcessor);
            }
            else {
                regularPostProcessors.add(postProcessor);
            }
        }

        // 这里不初始化 FactoryBeans，为了让 bean 工厂的后处理器可以应用到它们
        List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

        // 首先，调用实现了 PriorityOrdered 的 BeanDefinitionRegistryPostProcessors
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

        // 接下来，调用实现了 Ordered 的 BeanDefinitionRegistryPostProcessors
        postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
        for (String ppName : postProcessorNames) {
            if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                processedBeans.add(ppName);
            }
        }
        sortPostProcessors(currentRegistryProcessors, beanFactory);
        registryProcessors.addAll(currentRegistryProcessors);
        invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
        currentRegistryProcessors.clear();

        // 最后，调用所有其他的 BeanDefinitionRegistryPostProcessors，直到没有更多的后处理器出现
        boolean reiterate = true;
        while (reiterate) {
            reiterate = false;
            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName : postProcessorNames) {
                if (!processedBeans.contains(ppName)) {
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                    reiterate = true;
                }
            }
            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
            currentRegistryProcessors.clear();
        }

        // 现在，调用到目前为止处理过的所有处理器的 postProcessBeanFactory 回调
        invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
    }

    else {
        // 调用在上下文实例中注册的工厂处理器
        invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
    }
    
    // ... [代码部分省略以简化]
}
```

下面是我画的一个关于`BeanDefinitionRegistryPostProcessor`排序回调过程时序图大家可以参考一下。

~~~mermaid
sequenceDiagram
    Title: BeanDefinitionRegistryPostProcessor回调排序时序图
    participant Init as invokeBeanFactoryPostProcessors
    participant BDRPP_PO as BDRPP(PriorityOrdered)
    participant BDRPP_O as BDRPP(Ordered)
    participant BDRPP as 其余的BDRPP

    Init->>BDRPP_PO: 回调
    BDRPP_PO-->>Init: 完成
    Init->>BDRPP_O: 回调
    BDRPP_O-->>Init: 完成
    Init->>BDRPP: 回调
    BDRPP-->>Init: 完成
    
    Note right of BDRPP: 提示: 
    Note right of BDRPP: BDRPP = BeanFactoryPostProcessor
~~~

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

最后执行到我们自定义的逻辑中，我们在`postProcessBeanDefinitionRegistry`方法中注册了一个新的 bean 定义，在实际应用中，我们可以在 `postProcessBeanDefinitionRegistry` 方法内部执行更复杂的操作，例如修改 bean 的属性、对Bean对象进行代理做功能增强处理、更改它们的作用域或添加新的 bean 定义等。

```java
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("开始新增Bean定义");
        // 创建一个新的 BeanDefinition 对象
        BeanDefinition beanDefinition = new RootBeanDefinition(MySimpleBean.class);
        // 使用 registry 来注册这个新的 bean 定义
        registry.registerBeanDefinition("mySimpleBean", beanDefinition);
        System.out.println("完成新增Bean定义");
    }
}
```

### 七、注意事项

**调用顺序**：`BeanDefinitionRegistryPostProcessor` 比 `BeanFactoryPostProcessor` 有更高的优先级。这意味着 `BeanDefinitionRegistryPostProcessor` 的 `postProcessBeanDefinitionRegistry` 方法会在任何 `BeanFactoryPostProcessor` 的 `postProcessBeanFactory` 方法之前被调用。如果我们在Spring中有多个 `BeanDefinitionRegistryPostProcessor`，它们之间的执行顺序可能会受到 `Ordered` 或 `PriorityOrdered` 接口的影响，所以要确保正确地实现这些接口以确保预期的执行顺序。

**过早实例化**：一个常见的陷阱是，定义在 `@Configuration` 类中返回 `BeanDefinitionRegistryPostProcessor` 的非静态 `@Bean` 方法可能导致配置类过早实例化。为避免这一问题，这种方法应该被声明为 `static`。在本次最近实践中我们也是用到了static来修饰我们的`MyBeanDefinitionRegistryPostProcessor`

**不要过度使用**：虽然 `BeanDefinitionRegistryPostProcessor` 提供了强大的功能，但不应该在不需要修改或动态添加 Bean 定义的情况下滥用它，除非你是框架开发者。在我们绝大部分业务系统中，我觉得我们其实只要使用`@Component`、`@Service`、`@Repository`、`@Controller` 和 `@Configuration` 注解应该足够使用。

### 八、总结

#### 8.1、最佳实践总结

**应用启动**：启动类 `BeanDefinitionRegistryPostProcessorApplication` 通过 `AnnotationConfigApplicationContext` 初始化 Spring 容器，并加载了 `MyConfiguration` 类作为配置。

**配置类定义**：在 `MyConfiguration` 类中，我们通过 `@Bean` 注解定义了一个静态方法，该方法返回一个 `MyBeanDefinitionRegistryPostProcessor` 对象，确保它在 Spring 容器初始化时被执行。

**动态注册**：在我们自定义的 `MyBeanDefinitionRegistryPostProcessor` 实现中，我们重写了 `postProcessBeanDefinitionRegistry` 方法。在这个方法里，我们创建了一个新的 `RootBeanDefinition` 对象来代表 `MySimpleBean` 类，并通过 `registry` 的 `registerBeanDefinition` 方法注册了这个新的 bean 定义，为它指定了名为 `"mySimpleBean"` 的名称。

**目标 Bean**：`MySimpleBean` 是一个简单的 bean 类，它的 `show` 方法用于输出它自身的实例信息。

**验证动态注册**：当运行 `BeanDefinitionRegistryPostProcessorApplication` 时，可以观察到控制台首先打印了 "开始新增Bean定义" 和 "完成新增Bean定义"，说明 `postProcessBeanDefinitionRegistry` 方法被正确执行。紧接着，`MySimpleBean` 的实例被创建并打印了它的实例信息，证明 `BeanDefinitionRegistryPostProcessor` 成功地在运行时动态注册了这个 bean。

#### 8.2、源码分析总结

**应用启动**：通过 `AnnotationConfigApplicationContext` 来初始化 Spring 容器并加载 `MyConfiguration` 配置类。随后从该上下文中获取 `MySimpleBean` 并调用其 `show` 方法。

**Spring容器的刷新**：`AnnotationConfigApplicationContext` 的构造函数调用了 `refresh()` 方法，负责启动 Spring 容器的初始化流程。

**执行Bean工厂的后处理器**：在 `refresh()` 方法中，调用了 `invokeBeanFactoryPostProcessors(beanFactory)` 方法，负责执行所有注册的 `BeanFactoryPostProcessor` 和 `BeanDefinitionRegistryPostProcessor` 接口实现。

**按优先级执行回调**：Spring 按照不同的优先级（如 `PriorityOrdered` 和 `Ordered`）对 `BeanDefinitionRegistryPostProcessor` 进行排序并回调。这确保了回调的执行顺序。

**动态注册Bean定义**：在我们自定义的 `MyBeanDefinitionRegistryPostProcessor` 中，我们重写了 `postProcessBeanDefinitionRegistry` 方法。在该方法内部，我们动态地创建了 `MySimpleBean` 的定义，并将其注册到了容器中。

**执行结果**：`MySimpleBean` 成功被动态注册到 Spring 容器中，从而能够在应用启动时被检索并使用。