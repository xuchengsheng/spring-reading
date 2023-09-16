## BeanFactoryPostProcessor

[TOC]

### 一、接口描述

`BeanFactoryPostProcessor` 是一个接口，任何实现此接口的类都必须提供`postProcessBeanFactory`方法的实现。此方法提供了一个机会，在bean实例化之前修改bean的定义。

### 二、接口源码

```java
/**
 * 函数式接口注解，确保这个接口只有一个抽象方法，
 * 这也意味着它可以与Java 8的lambda表达式配合使用。
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    /**
     * 在标准初始化之后修改应用上下文的内部bean工厂。
     * 所有bean定义都会被加载，但没有bean会被实例化。
     * 这允许我们覆盖或添加属性，甚至是对于那些急切初始化的beans。
     *
     * @param beanFactory Spring应用的bean工厂
     * @throws BeansException 如果在处理过程中出现错误
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
```

### 三、主要功能

+ 修改Bean定义：在Spring加载所有bean定义后，但在它开始实例化这些bean之前，`BeanFactoryPostProcessor`会被调用。这意味着我们可以使用它来修改这些bean定义。
+ 更改属性值：你可以更改bean的属性或依赖，这在某些场景下，如需要根据环境或其他外部因素动态地配置bean时，会非常有用。
+ 添加或删除Bean定义：不仅可以修改现有的bean定义，还可以添加新的bean定义或删除现有的bean定义。
+ 应用多个BeanFactoryPostProcessors：如果有多个`BeanFactoryPostProcessor`，你可以通过实现`Ordered`接口来控制它们的执行顺序。

### 四、使用示例

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后我们会调用`mySimpleBean1`和`mySimpleBean2`中的`show()`方法，我们可以判断`MySimpleBean`的作用域是单例还是原型。如果它们指向同一个实例，那么它是单例的；否则，它是原型的。

```java
public class BeanFactoryPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);

        MySimpleBean mySimpleBean1 = context.getBean(MySimpleBean.class);
        MySimpleBean mySimpleBean2 = context.getBean(MySimpleBean.class);

        mySimpleBean1.show();
        mySimpleBean2.show();
    }
}
```

这里使用`@Bean`注解，定义了一个简单的bean (`MySimpleBean`)，并使用了一个`BeanFactoryPostProcessor` (`MyBeanFactoryPostProcessor`)，该后处理器可以修改bean定义

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MySimpleBean mySimpleBean(){
        return new MySimpleBean();
    }

    @Bean
    public static MyBeanFactoryPostProcessor myBeanFactoryPostProcessor(){
        return new MyBeanFactoryPostProcessor();
    }
}
```

`MySimpleBean`类中的`show`方法在被调用时，会在控制台输出“MySimpleBean instance”和当前对象的实例地址（通过`this`关键字）。这有助于我们了解每次获取bean时是否返回相同的实例（单例）还是新的实例（原型）。

```java
public class MySimpleBean {

    public void show() {
        System.out.println("MySimpleBean instance: " + this);
    }
}
```

这个 `MyBeanFactoryPostProcessor` 类是一个简单的 `BeanFactoryPostProcessor` 的实现，它在被调用时，会从`beanFactory`工厂中获取名为`mySimpleBean`的bean定义，默认情况下，所有的bean都是单例的，然后将`mySimpleBean`的作用域从单例改为原型。在实际应用中，你可能会在 `postProcessBeanFactory` 方法内部执行更复杂的操作，例如修改 bean 的属性、对Bean对象进行代理做功能增强处理、更改它们的作用域或添加新的 bean 定义等。

```java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("修改bean的定义");
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("mySimpleBean");
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        System.out.println("将mySimpleBean从默认的单例修改成多例");
        System.out.println("修改bean的定义已完成");
    }
}
```

通过运行结果，由于`mySimpleBean`现在是原型作用域，`[com.xcs.spring.config.MySimpleBean@11392934]`和`[com.xcs.spring.config.MySimpleBean@6892b3b6]`将是两个不同的地址，说明`MySimpleBean`的两个实例是不同的。

```java
修改bean的定义
将mySimpleBean从默认的单例修改成多例
修改bean的定义已完成
MySimpleBean instance: com.xcs.spring.config.MySimpleBean@11392934
MySimpleBean instance: com.xcs.spring.config.MySimpleBean@6892b3b6
```

### 五、时序图

```mermaid
sequenceDiagram
    Title: BeanFactoryPostProcessor时序图
    participant BeanFactoryPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant PostProcessorRegistrationDelegate
    participant MyBeanFactoryPostProcessor
    participant ConfigurableListableBeanFactory
    
    BeanFactoryPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>启动上下文
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()
    AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)<br>触发整个BeanFactoryPostProcessor调用的流程
    AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(...)<br>确保正确的顺序触发BeanFactoryPostProcessor调用的流程
    PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(postProcessors,beanFactory)<br>最终对BeanFactoryPostProcessor接口回调
    PostProcessorRegistrationDelegate->>MyBeanFactoryPostProcessor:postProcessBeanFactory(beanFactory)<br>执行自定义的逻辑
    MyBeanFactoryPostProcessor-->>ConfigurableListableBeanFactory:访问和修改bean定义
    ConfigurableListableBeanFactory-->>MyBeanFactoryPostProcessor:修改已完成
    PostProcessorRegistrationDelegate-->>AbstractApplicationContext: 调用Bean工厂后置处理器完成
    AnnotationConfigApplicationContext->>BeanFactoryPostProcessorApplication:初始化完成
```

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class BeanFactoryPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);

        MySimpleBean mySimpleBean1 = context.getBean(MySimpleBean.class);
        MySimpleBean mySimpleBean2 = context.getBean(MySimpleBean.class);

        mySimpleBean1.show();
        mySimpleBean2.show();
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

`refresh()`方法中我们重点关注一下`invokeBeanFactoryPostProcessors(beanFactory)`这方法，其他方法不是本次源码阅读的重点暂时忽略，在`invokeBeanFactoryPostProcessors(beanFactory)`方法会对实现了`BeanFactoryPostProcessor`这个接口进行接口回调。

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

在这个`invokeBeanFactoryPostProcessors(beanFactory, beanFactoryPostProcessors)`方法中，主要是对`BeanDefinitionRegistryPostProcessor`（不是本次关注的重点），`BeanFactoryPostProcessor`这两个接口的实现类进行回调，至于为什么这个方法里面代码很长呢？其实这个方法就做了一个事就是对处理器的执行顺序在做出来。比如说要先对实现了`PriorityOrdered.class`类回调，在对实现了`Ordered.class`类回调，最后才是对没有实现任何优先级的处理器进行回调。

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

    // ... [代码部分省略以简化]

    // 获取所有实现了BeanFactoryPostProcessor接口的bean的名称。
    String[] postProcessorNames =
        beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

    // 创建集合以区分不同类型的BeanFactoryPostProcessors
    List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
    List<String> orderedPostProcessorNames = new ArrayList<>();
    List<String> nonOrderedPostProcessorNames = new ArrayList<>();

    // 对BeanFactoryPostProcessors进行分类
    for (String ppName : postProcessorNames) {
        if (processedBeans.contains(ppName)) {
            // 如果这个bean已经被处理，直接跳过
        }
        else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
            // 优先排序的BeanFactoryPostProcessors
            priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
        }
        else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
            // 有排序的BeanFactoryPostProcessors
            orderedPostProcessorNames.add(ppName);
        }
        else {
            // 没有排序的BeanFactoryPostProcessors
            nonOrderedPostProcessorNames.add(ppName);
        }
    }

    // 调用实现了PriorityOrdered接口的BeanFactoryPostProcessors
    sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
    invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

    // 调用实现了Ordered接口的BeanFactoryPostProcessors
    List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
    for (String postProcessorName : orderedPostProcessorNames) {
        orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
    }
    sortPostProcessors(orderedPostProcessors, beanFactory);
    invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

    // 调用其他所有的BeanFactoryPostProcessors
    List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
    for (String postProcessorName : nonOrderedPostProcessorNames) {
        nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
    }
    invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

    // 清除元数据缓存，因为BeanFactoryPostProcessors可能已修改bean定义
    beanFactory.clearMetadataCache();
}
```

下面是我画的一个关于`BeanFactoryPostProcessor`排序回调过程时序图大家可以参考一下。

~~~mermaid
sequenceDiagram
    Title: BeanFactoryPostProcessor回调排序时序图
    participant Init as invokeBeanFactoryPostProcessors
    participant BFPP_PO as BFPP(PriorityOrdered)
    participant BFPP_O as BFPP(Ordered)
    participant BFPP as 其余的BFPP

    Init->>BFPP_PO: 回调
    BFPP_PO-->>Init: 完成
    Init->>BFPP_O: 回调
    BFPP_O-->>Init: 完成
    Init->>BFPP: 回调
    BFPP-->>Init: 完成
    
    Note right of BFPP: 提示: 
    Note right of BFPP: BFPP = BeanFactoryPostProcessor

~~~

`invokeBeanFactoryPostProcessors`方法中，循环调用了实现`BeanFactoryPostProcessor`接口中的`postProcessBeanFactory(registry)`方法

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

最终调用到了我们自定义实现了`BeanFactoryPostProcessor`接口的实现类中。在实际应用中，你可能会在 `postProcessBeanFactory` 方法内部执行更复杂的操作，例如修改 bean 的属性、对Bean对象进行代理做功能增强处理、更改它们的作用域或添加新的 bean 定义等。

```java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("修改bean的定义");
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("mySimpleBean");
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        System.out.println("将mySimpleBean从默认的单例修改成多例");
        System.out.println("修改bean的定义已完成");
    }
}
```

### 七、注意事项

**考虑其他的`BeanFactoryPostProcessor`**：在大型应用程序中，可能存在多个`BeanFactoryPostProcessor`。你需要确保它们不会互相冲突或导致不一致的bean定义。

**注意执行顺序**：如果有多个`BeanFactoryPostProcessor`，它们的执行顺序可能会影响结果。使用`Ordered`接口或`PriorityOrdered`接口来明确设置执行顺序。

**避免循环依赖**：修改bean定义可能会引入循环依赖。确保你充分理解bean之间的依赖关系，并尝试避免修改这些关系。比如当你修改了`BeanDefinition`构造函数，等等情况都有可能引入循环依赖。

**不要过度使用**：虽然`BeanFactoryPostProcessor`是一个非常强大接口，但这并不意味着我们就应该频繁使用它。只在真正需要的时候使用它，并考虑是否有其他更简单、更直观的方法可以达到同样的目的。

**谨慎使用**：虽然`BeanFactoryPostProcessor`是一个非常强大接口，允许你修改bean的定义。这意味着你可以更改bean的类、作用域、属性等。我们要在做这些更改时要非常小心，想想为什么要修改？影响的范围有多少？，以免引入不一致或不可预测的行为。

### 八、总结

到此我们做个总结吧。`BeanFactoryPostProcessor`是Spring中一个非常有用的接口，允许我们在bean实例化之前，但所有bean定义加载完之后，介入并修改bean的定义。此功能使得我们可以在bean开始其生命周期之前做一些微调，为后续的bean实例化、初始化提供更为灵活的控制。然后我们在也做了一个小小最佳实践，首先通过`AnnotationConfigApplicationContext`和`MyConfiguration`配置类，我们定义了`MySimpleBean`和`MyBeanFactoryPostProcessor`两个bean。虽然`MySimpleBean`默认是单例的，但由于`MyBeanFactoryPostProcessor`的介入，它的作用域被改为原型。`MySimpleBean`的`show`方法显示了bean的实例引用，从而证明了这一改变。最终，每次请求`MySimpleBean`都得到了一个新的实例，这从两个不同的输出地址得以验证，表明作用域的修改是成功的。在源码分析部分中，在Spring启动过程中，我们基于`AnnotationConfigApplicationContext`作为Spring容器，传入`MyConfiguration`作为配置类。其中，refresh()方法是容器初始化的核心，它在执行时调用了`invokeBeanFactoryPostProcessors(beanFactory)`，用于执行实现了`BeanFactoryPostProcessor`接口的方法。核心逻辑在`PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`中，该方法根据处理器的类型（如`PriorityOrdered`, `Ordered`）对其进行排序，确保不同类型的处理器按预期顺序执行。自定义的`BeanFactoryPostProcessor`实现最终也会在这个流程中被调用，允许用户对bean定义进行自定义操作，如修改属性、增强功能、改变作用域等。这整个机制为Spring提供了极大的灵活性，允许我们在Spring初始化bean前介入并进行自定义处理。

好了本次源码分析就到此，希望你能学到有用的知识。