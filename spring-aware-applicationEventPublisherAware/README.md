## ApplicationEventPublisherAware

- [ApplicationEventPublisherAware](#applicationeventpublisheraware)
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

`ApplicationEventPublisherAware` 接口，用于给需要发布应用事件的bean提供一个便捷的方式。实现此接口的bean可以接收到一个 `ApplicationEventPublisher` 的引用，这样它们就可以发布事件到 Spring 应用上下文中。

### 二、接口源码

`ApplicationEventPublisherAware` 是 Spring 框架自 1.1.1 开始引入的一个核心接口。需要发布一些事件到Spring上下文，我们可以实现此接口，然后使用注入的 `ApplicationEventPublisher` 来发布事件（通常是 `ApplicationContext`）。

```java
/**
 * 任何希望被通知 ApplicationEventPublisher（通常是 ApplicationContext）的对象都应该实现的接口。
 * 当对象运行在某个 ApplicationEventPublisher 中时，它将被通知。
 * 
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.1.1
 * @see ApplicationContextAware
 */
public interface ApplicationEventPublisherAware extends Aware {

    /**
     * 设置此对象运行的 ApplicationEventPublisher。
     * 此方法在正常bean属性填充之后被调用，但在init回调（如 InitializingBean 的 afterPropertiesSet 或自定义的 init-method）之前被调用。
     * 并且在 ApplicationContextAware 的 setApplicationContext 之前被调用。
     * 
     * @param applicationEventPublisher 由这个对象使用的事件发布器
     */
    void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher);

}
```

### 三、主要功能

**事件发布能力**：它允许 Spring beans 获得事件发布的能力，使它们能够发布事件到 Spring 应用上下文中。

**回调机制**：当一个 bean 实现了 `ApplicationEventPublisherAware` 接口时，Spring 容器会自动注入 `ApplicationEventPublisher` 实例到该 bean 中。

**与 ApplicationContext 的关联**：通常，所注入的 `ApplicationEventPublisher` 实例实际上就是 `ApplicationContext` 本身，这意味着 beans 可以使用它来发布事件。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyApplicationEventPublisherAware`类型的bean，最后调用`publish`方法用于发布一个事件。

```java
public class ApplicationEventPublisherAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyApplicationEventPublisherAware publisherAware = context.getBean(MyApplicationEventPublisherAware.class);
        publisherAware.publish("hello world");
    }
}
```

这里使用`@Bean`注解，定义了两个Bean，是为了确保`MyEventListener`， `MyApplicationEventPublisherAware` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyApplicationEventPublisherAware myApplicationEventPublisherAware(){
        return new MyApplicationEventPublisherAware();
    }

    @Bean
    public MyEventListener MyEventListener(){
        return new MyEventListener();
    }
}
```

`MyApplicationContextAware` 类使用 `ApplicationEventPublisherAware` 来获取 `ApplicationEventPublisher` 的引用，并使用这个引用来发布自定义事件。

```java
public class MyApplicationEventPublisherAware implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(String message) {
        publisher.publishEvent(new MyEvent(this, message));
    }
}
```

`MyEvent` 是我们自定义的 Spring 应用事件，用于传递一个字符串消息。

```java
public class MyEvent extends ApplicationEvent {

    private final String message;

    public MyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

`MyEventListener` 是一个监听器。当 `MyEvent` 事件被发布时，此监听器会自动被触发，执行 `onApplicationEvent` 方法的逻辑。

```java
public class MyEventListener implements ApplicationListener<MyEvent> {

    @Override
    public void onApplicationEvent(MyEvent event) {
        System.out.println("Received my event - " + event.getMessage());
    }
}
```

运行结果发现，这表示监听器成功地捕获了该事件并处理了它。

```java
Received my event - hello world
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: ApplicationEventPublisherAware时序图
    participant ApplicationEventPublisherAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyApplicationEventPublisherAware
    
    ApplicationEventPublisherAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新上下文
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>初始化Bean工厂
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>实例化单例
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>获取Bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>执行获取Bean
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>获取单例Bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>获取Bean实例
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>创建Bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>执行Bean创建
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName,bean,mbd)<br>负责bean的初始化
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(existingBean, beanName)<br>调用前置处理
AbstractAutowireCapableBeanFactory->>ApplicationContextAwareProcessor:postProcessBeforeInitialization(bean,beanName)<br>前置处理器方法
ApplicationContextAwareProcessor->>ApplicationContextAwareProcessor:invokeAwareInterfaces(bean)<br>调用Aware接口
ApplicationContextAwareProcessor->>MyApplicationEventPublisherAware:setApplicationEventPublisher(publisher)<br>注入事件发布器
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>ApplicationEventPublisherAwareApplication:初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyApplicationEventPublisherAware`类型的bean，最后调用`publish`方法用于发布一个事件。

```java
public class ApplicationEventPublisherAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyApplicationEventPublisherAware publisherAware = context.getBean(MyApplicationEventPublisherAware.class);
        publisherAware.publish("hello world");
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们重点关注`refresh()`方法

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`方法中，如果条件满足（即 bean 不是合成的），那么它会调用 `applyBeanPostProcessorsBeforeInitialization` 方法。这个方法是 Spring 生命周期中的一个关键点，它会遍历所有已注册的 `BeanPostProcessor` 实现，并调用它们的 `postProcessBeforeInitialization` 方法。这允许我们和内部处理器在 bean 初始化之前对其进行修改或执行其他操作。

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [代码部分省略以简化]
    
    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }
    
    // ... [代码部分省略以简化]

    return wrappedBean;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`方法中，遍历每一个 `BeanPostProcessor` 的 `postProcessBeforeInitialization` 方法都有机会对bean进行修改或增强

```java
@Override
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
    throws BeansException {

    Object result = existingBean;
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
        Object current = processor.postProcessBeforeInitialization(result, beanName);
        if (current == null) {
            return result;
        }
        result = current;
    }
    return result;
}
```

在`org.springframework.context.support.ApplicationContextAwareProcessor#postProcessBeforeInitialization`方法中，在这个方法的实现特别关注那些实现了 "aware" 接口的 beans，并为它们提供所需的运行环境信息。

```java
@Override
@Nullable
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (!(bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
          bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
          bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware ||
          bean instanceof ApplicationStartupAware)) {
        return bean;
    }

    // ... [代码部分省略以简化]
    
    invokeAwareInterfaces(bean);

    return bean;
}
```

在`org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces`方法中，用于处理实现了"Aware"接口的beans。这些接口使得beans能够被自动"感知"并获得对其运行环境或特定依赖的引用，而不需要显式地进行查找或注入。

```java
private void invokeAwareInterfaces(Object bean) {
	// ... [代码部分省略以简化]
    // 对ApplicationEventPublisherAware接口进行回调
    if (bean instanceof ApplicationEventPublisherAware) {
        ((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
    }
    // ... [代码部分省略以简化]
}
```

最后执行到我们自定义的逻辑中，使用 `ApplicationEventPublisherAware` 来获取 `ApplicationEventPublisher` 的引用，并使用这个引用来发布自定义事件。

```java
public class MyApplicationEventPublisherAware implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(String message) {
        publisher.publishEvent(new MyEvent(this, message));
    }
}
```

### 七、注意事项

**确保容器支持**：不是所有的Spring容器都支持 `Aware` 接口。例如，基本的 `BeanFactory` 不支持，而 `ApplicationContext` 支持。确保您的bean是由支持 `ApplicationEventPublisherAware` 的容器管理的。

**避免复杂的业务逻辑**：在实现的 `setApplicationEventPublisher` 方法中，尽量避免放入复杂的业务逻辑，该方法主要是用于注入 `ApplicationEventPublisher` 的。

**注意事件的目标**：当使用 `ApplicationEventPublisher` 发布事件时，这些事件会被所有相应的监听器捕获。确保你了解这些监听器的存在和它们的行为，以避免出现一些奇奇怪怪的问题。

**不要手动调用**：`setApplicationEventPublisher` 方法是为了由Spring容器调用的，而不是为了应用程序代码调用的。你不应该在业务逻辑中手动调用这个方法。

### 八、总结

#### 8.1、最佳实践总结

**启动应用**：在 `ApplicationEventPublisherAwareApplication` 的主方法中，使用 `AnnotationConfigApplicationContext` 初始化了 Spring 上下文，并指定了配置类 `MyConfiguration`。

**配置类**：`MyConfiguration` 使用了 `@Configuration` 注解，表示它是一个 Spring 配置类。此类中使用 `@Bean` 注解定义了两个 Bean：`MyApplicationEventPublisherAware` 和 `MyEventListener`，确保它们被 Spring 容器管理。

**事件发布者**：`MyApplicationEventPublisherAware` 类实现了 `ApplicationEventPublisherAware` 接口，从而可以获取 `ApplicationEventPublisher` 的引用。它还定义了一个 `publish` 方法，用于发布自定义的 `MyEvent` 事件。

**自定义事件**：`MyEvent` 是一个自定义事件类，继承自 `ApplicationEvent`。它携带一个字符串消息。

**事件监听器**：`MyEventListener` 是一个事件监听器，它实现了 `ApplicationListener` 并专门用于监听 `MyEvent` 事件。当相应事件被发布时，它的 `onApplicationEvent` 方法会被自动触发。

**执行结果**：当运行 `ApplicationEventPublisherAwareApplication` 主方法时，应用发布了一个 `MyEvent` 事件，携带了 "hello world" 消息。`MyEventListener` 成功捕获此事件，并输出了相应的消息。

#### 8.2、源码分析总结

**启动应用**：通过 `ApplicationEventPublisherAwareApplication` 的主方法，使用 `AnnotationConfigApplicationContext` 初始化了 Spring 上下文，并指定了配置类 `MyConfiguration`。

**注册和刷新**：在 `AnnotationConfigApplicationContext` 构造函数中，先注册组件类，然后调用 `refresh()` 来启动Spring容器的初始化过程。

**初始化Bean工厂**：在 `AbstractApplicationContext#refresh` 方法中，调用 `finishBeanFactoryInitialization` 以实例化所有非懒加载的单例Bean。

**预实例化单例**：在 `DefaultListableBeanFactory` 中，通过 `preInstantiateSingletons` 方法预先实例化所有非懒加载的单例Bean。

**Bean创建**：在 `AbstractBeanFactory#getBean` 中，调用 `doGetBean` 来真正执行Bean的创建过程。此方法中涉及到真正的Bean实例化、属性注入和初始化。

**初始化Bean**：在 `AbstractAutowireCapableBeanFactory` 类中，`initializeBean` 方法用于确保bean完全配置并准备就绪。这个过程中会应用所有的 `BeanPostProcessor`，它们能在bean初始化前后做额外的处理。

**处理Aware接口**：在 `ApplicationContextAwareProcessor` 中，`invokeAwareInterfaces` 方法负责处理实现了 `Aware` 接口的beans，为它们自动注入对应的依赖或运行环境信息。

**发布事件**：在我们的自定义逻辑中，使用 `ApplicationEventPublisherAware` 接口来获取Spring的事件发布器。然后，使用这个事件发布器，我们可以发布自定义事件。