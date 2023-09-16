## BeanDefinitionRegistryPostProcessor

[TOC]

### 一、接口描述

`BeanDefinitionRegistryPostProcessor` 是 Spring 框架中的一个接口，它用于在 Spring 容器的标准初始化过程中允许我们修改应用程序上下文的内部 bean 定义。它继承自 `BeanFactoryPostProcessor` 接口。与 `BeanFactoryPostProcessor` 不同的是，它还提供了对 `BeanDefinitionRegistry` 的访问，这使得我们能够在运行时注册新的 beans 或修改现有的 bean 定义。

### 二、接口源码

从接口的源码中可以发现，此接口继承了`BeanFactoryPostProcessor`接口(如果你还对`BeanFactoryPostProcessor`接口不了解，请看我之前写的源码分析[点击跳转](../spring-interface-beanFactoryPostProcessor/README.md))，然后`BeanDefinitionRegistryPostProcessor`接口是从Spring 3.0.1起开始使用的，它主要的功能实现类在`org.springframework.context.annotation.ConfigurationClassPostProcessor`中完成。

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

+ 注册新的 Bean 定义：该接口提供了一个机制，允许在 Spring 容器完成其标准初始化（即加载所有 bean 定义）之后，但在任何 bean 实例化之前，动态注册新的 bean 定义。
+ 修改现有的 Bean 定义：除了能够添加新的 bean 定义，`BeanDefinitionRegistryPostProcessor` 还可以修改已经注册的 bean 定义。例如，它可以修改 bean 的属性值、构造函数参数或其它设置。
+ 控制 `BeanFactoryPostProcessor` 的执行顺序：因为 `BeanDefinitionRegistryPostProcessor` 是 `BeanFactoryPostProcessor` 的子接口，它的实现还可以控制 `BeanFactoryPostProcessor` 的执行顺序。这是因为在 Spring 容器启动时，所有的 `BeanDefinitionRegistryPostProcessor` beans 首先会被实例化和调用，然后才是其他的 `BeanFactoryPostProcessor` beans。
+ 基于条件的 Bean 注册：可以利用 `BeanDefinitionRegistryPostProcessor` 来基于特定的运行时条件（例如类路径上是否存在某个特定的类）来决定是否注册某个 bean。
+ 扩展点以实现高级配置：对于复杂的应用或框架，这个接口提供了一个扩展点，可以在初始化过程中进行更高级的配置，如加载外部的配置或执行特殊的验证逻辑。

### 四、使用示例

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后我们从`AnnotationConfigApplicationContext`中获取`MySimpleBean`并调用`show`方法

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

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class BeanDefinitionRegistryPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MySimpleBean mySimpleBean1 = context.getBean(MySimpleBean.class);
        mySimpleBean1.show();
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

`refresh()`方法中我们重点关注一下`invokeBeanFactoryPostProcessors(beanFactory)`这方法，其他方法不是本次源码阅读的重点暂时忽略，在`invokeBeanFactoryPostProcessors(beanFactory)`方法会对实现了`BeanDefinitionRegistryPostProcessor`这个接口进行接口回调。

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
     // ... [代码部分省略以简化]
     // 调用在上下文中注册为bean的工厂处理器
     invokeBeanFactoryPostProcessors(beanFactory);
     // ... [代码部分省略以简化]
}
```

在`invokeBeanFactoryPostProcessors()`中又委托了`PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`进行调用

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    // ... [代码部分省略以简化]
}
```

在这个`invokeBeanFactoryPostProcessors(beanFactory, beanFactoryPostProcessors)`方法中，主要是对`BeanDefinitionRegistryPostProcessor`，`BeanFactoryPostProcessor`(不是本次关注的重点)这两个接口的实现类进行回调，至于为什么这个方法里面代码很长呢？其实这个方法就做了一个事就是对处理器的执行顺序在做出来。比如说要先对实现了`PriorityOrdered.class`类回调，在对实现了`Ordered.class`类回调，最后才是对没有实现任何优先级的处理器进行回调。

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
    Title:BeanDefinitionRegistryPostProcessor回调排序时序图
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
    Note right of BDRPP: BDRPP = BeanDefinitionRegistryPostProcessor

~~~

`invokeBeanDefinitionRegistryPostProcessors`方法中，循环调用了实现`BeanDefinitionRegistryPostProcessor`接口中的`postProcessBeanDefinitionRegistry(registry)`方法

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

最终调用到了我们自定义实现了`BeanDefinitionRegistryPostProcessor`接口的实现类中，我们在`postProcessBeanDefinitionRegistry`方法中注册了一个新的 bean 定义，在实际应用中，你可能会在 `postProcessBeanDefinitionRegistry` 方法内部执行更复杂的操作，例如修改 bean 的属性、对Bean对象进行代理做功能增强处理、更改它们的作用域或添加新的 bean 定义等。

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

到此我们做个总结吧。`BeanDefinitionRegistryPostProcessor` 在 Spring 中是一个特殊的接口，使我们可以在容器初始化过程中动态地修改或添加 bean 定义。它与 `BeanFactoryPostProcessor` 类似，但提供了更深入的 `BeanDefinitionRegistry` 访问权限。其关键功能包括动态地注册或更改 bean 定义，调整 bean 的属性或参数，以及根据特定条件（如类的存在）来决定是否注册某个 bean。在实际应用中，通过 `AnnotationConfigApplicationContext` 初始化 Spring 容器时，首先加载了 `MyConfiguration` 类中的 bean。在此配置中，我们使用 `@Bean` 注解定义了一个类型为 `MyBeanDefinitionRegistryPostProcessor` 的 bean，其核心方法 `postProcessBeanDefinitionRegistry` 被用于注册新的 `MySimpleBean` 定义。当应用运行时，该方法首先被执行，随后 `MySimpleBean` 的实例被成功创建并输出。而从源码的角度，`AnnotationConfigApplicationContext` 的 `refresh()` 方法是容器初始化的关键，其中 `invokeBeanFactoryPostProcessors(beanFactory)` 保证了所有的 `BeanFactoryPostProcessor` 以正确的顺序被调用。在最佳实践中，为避免配置类的过早实例化，建议将 `BeanDefinitionRegistryPostProcessor` 的 `@Bean` 方法声明为 `static`。虽然 `BeanDefinitionRegistryPostProcessor` 功能强大，但应适度使用，因为常规的 Spring 注解大多已满足常见需求。

好了本次源码分析就到此，希望你能学到有用的知识。