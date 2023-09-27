## SmartInitializingSingleton

- [SmartInitializingSingleton](#smartinitializingsingleton)
  - [一、接口描述](#一接口描述)
  - [二、接口源码](#二接口源码)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [8.1、最佳实践总结](#81最佳实践总结)
    - [8.2、源码分析总结](#82源码分析总结)

### 一、接口描述

`SmartInitializingSingleton`接口，用于bean初始化，当所有单例bean都已完全初始化后，用此接口进行回调。

### 二、接口源码

`SmartInitializingSingleton` 是 Spring 框架自 4.1 版本开始引入的一个核心接口。其中`afterSingletonsInstantiated()`方法会在单例预实例化阶段结束时被调用。它保证所有常规的单例beans在此时已经被创建和初始化。

```java
/**
 * 在BeanFactory启动时，单例预实例化阶段结束后触发的回调接口。
 * 单例beans可以实现此接口，以在常规单例实例化算法后执行某些初始化，
 * 避免因意外的早期初始化（例如，从ListableBeanFactory#getBeansOfType调用）引起的副作用。
 * 在这方面，它是InitializingBean的替代品，后者在bean的本地构建阶段结束时被触发。
 *
 * 这个回调变种与org.springframework.context.event.ContextRefreshedEvent有些类似，
 * 但不需要实现org.springframework.context.ApplicationListener，
 * 也不需要在上下文层次结构中过滤上下文引用等。它还意味着仅依赖于beans包，
 * 并由单独的ListableBeanFactory实现尊重，不仅仅在org.springframework.context.ApplicationContext环境中。
 *
 * 注意: 如果我们打算开始/管理异步任务，最好实现org.springframework.context.Lifecycle，
 * 它提供了一个更丰富的运行时管理模型，并允许分阶段启动/关闭。
 *
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
 */
public interface SmartInitializingSingleton {

	/**
	 * 在单例预实例化阶段的末尾调用，
	 * 保证所有常规单例beans已经创建。在此方法中的
	 * ListableBeanFactory#getBeansOfType调用不会在引导期间引起意外的副作用。
	 * 注意: 此回调不会为在BeanFactory启动后按需延迟初始化的单例beans触发，
	 * 也不会触发任何其他bean范围。仅为具有预期引导语义的beans小心使用它。
	 */
	void afterSingletonsInstantiated();

}
```

### 三、主要功能

**bean已完全初始化后回调**：提供了一个回调机制，允许单例bean在Spring容器中所有其他常规单例bean都已完全初始化之后，执行某些特定的初始化操作。具体来说，当所有的单例bean都被实例化和初始化后，`SmartInitializingSingleton`接口中的`afterSingletonsInstantiated()`方法会被调用。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class SmartInitializingSingletonApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保 `MySmartInitializingSingleton` 被 Spring 容器执行

```java
@Configuration
@ComponentScan("com.xcs.spring.service")
public class MyConfiguration {

    @Bean
    public static MySmartInitializingSingleton mySmartInitializingSingleton(){
        return new MySmartInitializingSingleton();
    }
}
```

`MySmartInitializingSingleton`类中，在所有其他的单例bean被完全初始化后，然后在`afterSingletonsInstantiated()`方法会启动`MyService`类中定义的定时任务。

```java
public class MySmartInitializingSingleton implements SmartInitializingSingleton {

    @Autowired
    private MyService myService;

    @Override
    public void afterSingletonsInstantiated() {
        myService.startScheduledTask();
    }
}
```

`MyService`定义了一个定时任务，该任务会每隔2秒打印出当前的日期时间和"hello world"消息。其中`MySmartInitializingSingleton`会在所有的单例bean完全初始化后，调用`startScheduledTask()`方法，从而启动定时任务。

```java
@Service
public class MyService {

    /**
     * 这里使用了Java的Timer来模拟定时任务。在实际应用中，可能会使用更复杂的调度机制。
     */
    public void startScheduledTask() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println(getDate() + " hello world ");
                    }
                },
                0,
                2000
        );
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public String getDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
```

运行结果发现，`MySmartInitializingSingleton`成功地在所有其他的单例bean初始化后启动了`MyService`中的定时任务。我们的实现是正确的，每隔2秒都会产生下述输出。

```java
2023-09-27 10:41:36 hello world 
2023-09-27 10:41:38 hello world 
2023-09-27 10:41:40 hello world 
2023-09-27 10:41:42 hello world 
2023-09-27 10:41:44 hello world 
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: SmartInitializingSingleton时序图
    participant SmartInitializingSingletonApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant MySmartInitializingSingleton
    
    SmartInitializingSingletonApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新上下文
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>初始化Bean工厂
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>实例化单例
    DefaultListableBeanFactory->>MySmartInitializingSingleton:afterSingletonsInstantiated()<br>所有单例初始化
~~~

### 六、源码分析

```java
public class SmartInitializingSingletonApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
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
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [代码部分省略以简化]
    // Instantiate all remaining (non-lazy-init) singletons.
    beanFactory.preInstantiateSingletons();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons`方法中，实现`SmartInitializingSingleton`接口的beans在所有其他的单例beans完全实例化后才会触发其`afterSingletonsInstantiated`方法，从而确保了初始化的正确时序。

```java
@Override
public void preInstantiateSingletons() throws BeansException {
    // ... [代码部分省略以简化]

    // 触发所有SmartInitializingSingleton bean的初始化后回调。。。
    for (String beanName : beanNames) {
        Object singletonInstance = getSingleton(beanName);
        if (singletonInstance instanceof SmartInitializingSingleton) {
            // ... [代码部分省略以简化]
            smartSingleton.afterSingletonsInstantiated();
            // ... [代码部分省略以简化]
        }
    }
}
```

### 七、注意事项

**避免复杂逻辑**： `SmartInitializingSingleton`的设计是为了执行初始化后的逻辑。避免在`afterSingletonsInstantiated()`方法中加入过于复杂的逻辑。

**注意依赖关系**： 当`afterSingletonsInstantiated()`被调用时，所有的常规单例bean都已经被初始化。但请确保在这个方法中调用的任何bean已经完全初始化并且所有的依赖都被满足。

**避免早期初始化**： 请确保不会意外地触发其他bean的早期初始化，尤其是在`afterSingletonsInstantiated()`方法中。早期初始化可能会导致不可预见的副作用。

**限制范围**： `SmartInitializingSingleton`仅对常规单例bean起作用。对于在`BeanFactory`启动后按需延迟初始化或其他作用域的beans（如原型作用域），此回调不会被触发。

**异步任务**： 如果我们的目的是启动或管理异步任务，最好使用`Lifecycle`接口或考虑其他Spring的启动监听器，如`ApplicationListener<ContextRefreshedEvent>`。`Lifecycle`为运行时管理提供了一个更完善的模型。

**确保幂等性**： 如果有可能多次刷新应用程序上下文（虽然在我看来这种情况基本上很少），请确保`afterSingletonsInstantiated()`方法的实现是幂等的，即多次执行与一次执行产生的效果相同。

**与`InitializingBean`和`@PostConstruct`的区别**： `SmartInitializingSingleton`和`InitializingBean`或`@PostConstruct`注解有区别。后两者是bean级别的初始化回调，而`SmartInitializingSingleton`是容器级别的，确保在所有bean初始化之后才执行。

**不要滥用**： 只有在确实需要确保所有其他bean都初始化后才执行某些操作时，才应使用`SmartInitializingSingleton`。如果不需要这种保证，考虑使用更标准的初始化回调。

### 八、总结

#### 8.1、最佳实践总结

**启动入口**：

在示例的启动类`SmartInitializingSingletonApplication`中，我们使用了`AnnotationConfigApplicationContext`来加载和初始化Spring容器。我们为上下文提供了一个Java配置类`MyConfiguration`，该类定义了应该由Spring扫描和管理的bean。

**配置**：

在`MyConfiguration`类中，我们使用`@Bean`注解显式地定义了`MySmartInitializingSingleton`这个bean。这确保了`MySmartInitializingSingleton`被Spring容器管理并在适当的时机执行。

**实现SmartInitializingSingleton接口**：

`MySmartInitializingSingleton`实现了`SmartInitializingSingleton`接口。当所有其他的单例bean都被完全初始化后，`afterSingletonsInstantiated()`方法被调用。在这个方法中，我们启动了`MyService`类中定义的定时任务。

**定时任务**：

`MyService`中定义了一个使用Java的`Timer`模拟的定时任务。这个任务会每隔2秒打印当前时间和"hello world"这个消息。在实际应用中，可能会使用更复杂的调度机制，如Spring的`TaskScheduler`或Quartz等。

**结果**：

启动示例应用后，可以观察到每隔2秒在控制台上都会输出格式化的当前时间后跟着"hello world"这样的消息，证明定时任务已经成功启动并在运行。

#### 8.2、源码分析总结

**应用启动**:

一切从`SmartInitializingSingletonApplication`的主函数开始，其中初始化了`AnnotationConfigApplicationContext`，这是Spring用于Java注解配置的上下文。

**AnnotationConfigApplicationContext构造函数**：

在`AnnotationConfigApplicationContext`的构造函数中，执行了三个主要步骤，其中最关键的是`refresh()`方法。

**执行refresh方法**:

`refresh()`方法是Spring上下文刷新的核心。在这里，重点是`finishBeanFactoryInitialization(beanFactory)`，它负责实例化所有剩余的非懒加载单例Bean。

**完成BeanFactory初始化**:

在`finishBeanFactoryInitialization`方法中，为了完成上述任务，它进一步调用了`beanFactory.preInstantiateSingletons()`。

**预实例化单例**:

这步是最关键的。在`DefaultListableBeanFactory`的`preInstantiateSingletons`方法中，所有单例beans都被实例化。紧接着，为那些实现了`SmartInitializingSingleton`接口的beans触发了`afterSingletonsInstantiated`回调，确保这些回调在所有其他单例beans完全实例化后才被执行。