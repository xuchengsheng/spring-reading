## ApplicationContextAware

- [ApplicationContextAware](#applicationcontextaware)
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

`ApplicationContextAware` 接口，允许我们访问当前的应用上下文 (`ApplicationContext`)。这通常在某些Spring bean需要访问应用上下文本身或其内部其他bean时会有用。

### 二、接口源码

`ApplicationContextAware` 是 Spring 框架中的一个接口，主要用于那些需要感知或交互其所在的 ApplicationContext 的 Spring beans。例如，可能需要发布事件或查询 ApplicationContext 中的其他 beans。

```java
/**
 * 该接口应由希望接收其运行的 ApplicationContext 通知的任何对象实现。
 *
 * 例如，当对象需要访问一组合作的bean时，实现此接口是有意义的。
 * 注意，通过bean引用进行配置优于仅为查找bean的目的而实现此接口。
 *
 * 如果对象需要访问文件资源（即希望调用getResource），
 * 想要发布一个应用事件，或需要访问MessageSource，也可以实现此接口。
 * 但在这种特定情况下，最好实现更为特定的 ResourceLoaderAware，
 * ApplicationEventPublisherAware 或 MessageSourceAware 接口。
 *
 * 注意，文件资源依赖也可以作为类型为 org.springframework.core.io.Resource 的bean属性暴露，
 * 通过字符串填充，由bean工厂自动进行类型转换。这样就不需要为访问特定文件资源而实现任何回调接口了。
 *
 * org.springframework.context.support.ApplicationObjectSupport 是应用对象的便利基类，
 * 它实现了此接口。
 *
 * 关于所有bean生命周期方法的列表，请参阅
 * org.springframework.beans.factory.BeanFactory BeanFactory的javadocs。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @see ResourceLoaderAware
 * @see ApplicationEventPublisherAware
 * @see MessageSourceAware
 * @see org.springframework.context.support.ApplicationObjectSupport
 * @see org.springframework.beans.factory.BeanFactoryAware
 */
public interface ApplicationContextAware extends Aware {

    /**
     * 设置此对象运行的 ApplicationContext。
     * 此调用通常用于初始化对象。
     * 此方法在普通bean属性被填充之后调用，但在诸如 org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     * 这样的初始化回调或自定义初始化方法之前调用。
     * 在 ResourceLoaderAware#setResourceLoader，
     * ApplicationEventPublisherAware#setApplicationEventPublisher 和
     * MessageSourceAware 之后调用（如果适用）。
     *
     * @param applicationContext 要由此对象使用的 ApplicationContext 对象
     * @throws ApplicationContextException 如果上下文初始化出错
     * @throws BeansException 如果应用上下文方法抛出异常
     * @see org.springframework.beans.factory.BeanInitializationException
     */
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}
```

### 三、主要功能

**动态查找其他Beans**：尽管我们通常使用依赖注入来获取其他beans的引用，但在某些动态或复杂情况下，bean可能需要在运行时查找其他beans。

**发布事件**：通过 `ApplicationContext`，bean可以发布应用级事件，这些事件可以被其他beans捕获和处理，这是实现松耦合交互的一种方法。

**资源加载**：`ApplicationContext` 扩展了 `ResourceLoader`，因此bean可以使用它来加载外部资源，如文件或URL。

**访问消息源**：对于支持国际化的应用程序，bean可以通过 `ApplicationContext` 访问消息源，从而解析特定的消息。

**访问其他应用上下文服务**：除了上述功能，`ApplicationContext` 还提供了其他一些服务，例如与JNDI交互、访问应用的环境属性等。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyApplicationContextAware`类型的bean，最后调用`publish`方法用于发布一个事件。

```java
public class ApplicationContextAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyApplicationContextAware contextAware = context.getBean(MyApplicationContextAware.class);
        contextAware.publish("hello world");
    }
}
```

这里使用`@Bean`注解，定义了两个Bean，是为了确保`MyEventListener`， `MyApplicationContextAware` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyApplicationContextAware myApplicationContextAware(){
        return new MyApplicationContextAware();
    }

    @Bean
    public MyEventListener MyEventListener(){
        return new MyEventListener();
    }
}
```

 `MyApplicationContextAware` 类使用 `ApplicationContextAware` 来获取 `ApplicationContext` 的引用，并使用这个引用来发布自定义事件。

```java
public class MyApplicationContextAware implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public void publish(String message) {
        context.publishEvent(new MyEvent(this, message));
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
    Title: BeanClassLoaderAware时序图
    participant ApplicationContextAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyApplicationContextAware
    
    ApplicationContextAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
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
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(existingBean, beanName)<br>调用前置处理器
    AbstractAutowireCapableBeanFactory->>ApplicationContextAwareProcessor:postProcessBeforeInitialization(bean,beanName)<br>触发Aware处理
    ApplicationContextAwareProcessor->>ApplicationContextAwareProcessor:invokeAwareInterfaces(bean)<br>执行Aware回调
    ApplicationContextAwareProcessor->>MyApplicationContextAware:setApplicationContext(context)<br>设置应用上下文
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>ApplicationContextAwareApplication:初始化完成

~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyApplicationContextAware`类型的bean，最后调用`publish`方法用于发布一个事件。

```java
public class ApplicationContextAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyApplicationContextAware contextAware = context.getBean(MyApplicationContextAware.class);
        contextAware.publish("hello world");
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
    
    // 对ApplicationContextAware接口进行回调
    if (bean instanceof ApplicationContextAware) {
        ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
    }
}
```

最后执行到我们自定义的逻辑中，使用 `ApplicationContextAware` 来获取 `ApplicationContext` 的引用，并使用这个引用来发布自定义事件。

```java
public class MyApplicationContextAware implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public void publish(String message) {
        context.publishEvent(new MyEvent(this, message));
    }
}
```

### 七、注意事项

**记住生命周期**：当我们实现 `ApplicationContextAware` 时，记住上下文是在 bean 的生命周期的一个特定点被注入的。这通常是在属性注入后、初始化方法前。

**记住上下文层次结构**：在更复杂的应用中，可能会有多个 `ApplicationContext` 层次结构（例如，一个根上下文和一个或多个子上下文）。确保我们了解从哪个上下文检索 beans，以及这些 beans 的生命周期和可见性。

**小心与懒加载 beans 的交互**：如果我们使用 `ApplicationContextAware` 来动态检索一个定义为懒加载的 bean，那么这将导致该 bean 被立即初始化。

**避免创建循环依赖**：如果使用 `ApplicationContext` 来动态查找 beans，要确保不会创建意外的循环依赖。

**避免在构造函数中使用 ApplicationContext**：当 bean 实现 `ApplicationContextAware` 时，`setApplicationContext` 方法是在 bean 的属性注入之后、初始化方法（如 `afterPropertiesSet` 或自定义的 init 方法）之前调用的。因此，不应该试图在构造函数中访问 `ApplicationContext`，因为它在那时可能还没有被设置。

### 八、总结

#### 8.1、最佳实践总结

**应用启动与上下文初始化**： 在 `ApplicationContextAwareApplication` 类中，我们启动了一个基于 Java 注解配置的 Spring 应用。使用 `AnnotationConfigApplicationContext` 与 `MyConfiguration` 作为参数，我们成功地启动了 Spring 容器。

**配置类的定义**： `MyConfiguration` 是一个配置类，标注了 `@Configuration` 注解。这里，我们定义了两个 beans，分别是 `MyApplicationContextAware` 和 `MyEventListener`。由于它们都被定义为 `@Bean`，Spring 容器在启动时会实例化并管理它们。

**获取并使用 ApplicationContextAware bean**： 从启动的 Spring 容器中，我们获取了 `MyApplicationContextAware` bean 的实例，并调用其 `publish` 方法来发布一个自定义事件（携带消息 "hello world"）。

**ApplicationContextAware 的实现**： `MyApplicationContextAware` 实现了 `ApplicationContextAware` 接口，这允许它获取和存储其运行环境的 `ApplicationContext` 引用。它提供了一个 `publish` 方法，用于使用保存的 ApplicationContext 来发布事件。

**自定义事件的定义**： `MyEvent` 是一个自定义事件，继承自 `ApplicationEvent`。它携带了一个字符串消息，这个消息在事件发布后可以由监听器获取和处理。

**事件监听器的定义**： `MyEventListener` 实现了 `ApplicationListener<MyEvent>`，这使得它成为了一个专门用于监听和处理 `MyEvent` 事件的监听器。当 `MyEvent` 事件被发布时，监听器的 `onApplicationEvent` 方法被自动触发，并打印出消息内容。

**运行结果**： 当应用运行时，`MyEventListener` 成功地捕获了由 `MyApplicationContextAware` 发布的事件，并输出了 "Received my event - hello world"，证明整个事件发布和处理的机制都工作正常。

#### 8.2、源码分析总结

**启动与上下文初始化**：使用 `AnnotationConfigApplicationContext` 启动 Spring 应用，并传递 `MyConfiguration` 作为参数进行上下文初始化，从上下文中获取 `MyApplicationContextAware` bean，并发布自定义事件。

**Spring 上下文刷新**：在 `refresh()` 方法中，主要关注点是调用 `finishBeanFactoryInitialization(beanFactory)`，负责实例化所有非延迟加载的单例 bean。

**Bean 实例化**：`preInstantiateSingletons()` 方法在 Spring 上下文初始化完成后被调用，确保所有非延迟加载的单例 beans 都被实例化，对于每个 bean，都会调用 `getBean(beanName)`，这会触发 bean 的实例化、初始化以及依赖注入。

**Bean 创建与初始化**：在 `doCreateBean` 方法中，核心操作是调用 `initializeBean` 进行 bean 初始化，确保 bean 完全配置并准备好，`initializeBean` 中会调用 `applyBeanPostProcessorsBeforeInitialization`，在 bean 初始化之前遍历所有已注册的 `BeanPostProcessor`。

**处理 Aware 接口**：`ApplicationContextAwareProcessor` 的作用是对实现了 "Aware" 接口的 beans 进行特殊处理。在 `invokeAwareInterfaces` 方法中，针对不同的 "Aware" 接口进行了处理，使得 beans 可以自动感知其运行环境或特定依赖。

**自定义事件发布**：通过实现 `ApplicationContextAware`，`MyApplicationContextAware` 能够获得 `ApplicationContext` 的引用，随后使用此引用发布自定义事件。