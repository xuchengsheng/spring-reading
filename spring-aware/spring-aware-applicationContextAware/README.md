## ApplicationContextAware

- [ApplicationContextAware](#applicationcontextaware)
  - [一、基本信息](#一基本信息)
  - [二、接口描述](#二接口描述)
  - [三、接口源码](#三接口源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133914136) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [ApplicationContextAware源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-applicationContextAware)

### 二、接口描述

`ApplicationContextAware` 接口，允许我们访问当前的应用上下文 (`ApplicationContext`)。这通常在某些Spring bean需要访问应用上下文本身或其内部其他bean时会有用。

### 三、接口源码

实现`ApplicationContextAware`接口的对象会在Spring容器中被自动注入一个`ApplicationContext`实例。

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

### 四、主要功能

1. **动态查找其他Beans**
   + 尽管我们通常使用依赖注入来获取其他beans的引用，但在某些动态或复杂情况下，bean可能需要在运行时查找其他beans。

2. **发布事件**
   + 通过 `ApplicationContext`，bean可以发布应用级事件，这些事件可以被其他beans捕获和处理，这是实现松耦合交互的一种方法。

3. **资源加载**
   + `ApplicationContext` 扩展了 `ResourceLoader`，因此bean可以使用它来加载外部资源，如文件或URL。

4. **访问消息源**
   + 对于支持国际化的应用程序，bean可以通过 `ApplicationContext` 访问消息源，从而解析特定的消息。

5. **访问其他应用上下文服务**
   + 除了上述功能，`ApplicationContext` 还提供了其他一些服务，例如与JNDI交互、访问应用的环境属性等。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class ApplicationContextAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保 `MyApplicationContextAware` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyApplicationContextAware myApplicationContextAware(){
        return new MyApplicationContextAware();
    }
}
```

 `MyApplicationContextAware` 的实现，它实现了 `ApplicationContextAware` 接口。

```java
public class MyApplicationContextAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        System.out.println("实现ApplicationContextAware接口,自动调用setApplicationContext方法");
        System.out.println("ApplicationContext = " + context);
    }
}
```

运行结果发现，Spring 容器确实自动调用了 `setApplicationContext` 方法并传递了 `ApplicationContext` 对象。

```java
实现ApplicationContextAware接口,自动调用setApplicationContext方法
ApplicationContext = org.springframework.context.annotation.AnnotationConfigApplicationContext@64bf3bbf
```

### 六、时序图

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

### 七、源码分析

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

最后执行到我们自定义的逻辑中，使用 `ApplicationContextAware` 来获取 `ApplicationContext` 的引用。

```java
public class MyApplicationContextAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        System.out.println("实现ApplicationContextAware接口,自动调用setApplicationContext方法");
        System.out.println("ApplicationContext = " + context);
    }
}
```

### 八、注意事项

1. **记住生命周期**
   + 当我们实现 `ApplicationContextAware` 时，记住上下文是在 bean 的生命周期的一个特定点被注入的。这通常是在属性注入后、初始化方法前。

2. **记住上下文层次结构**
   + 在更复杂的应用中，可能会有多个 `ApplicationContext` 层次结构（例如，一个根上下文和一个或多个子上下文）。确保我们了解从哪个上下文检索 beans，以及这些 beans 的生命周期和可见性。

3. **小心与懒加载 beans 的交互**
   + 如果我们使用 `ApplicationContextAware` 来动态检索一个定义为懒加载的 bean，那么这将导致该 bean 被立即初始化。

4. **避免创建循环依赖**
   + 如果使用 `ApplicationContext` 来动态查找 beans，要确保不会创建意外的循环依赖。

5. **避免在构造函数中使用 ApplicationContext**
   + 当 bean 实现 `ApplicationContextAware` 时，`setApplicationContext` 方法是在 bean 的属性注入之后、初始化方法（如 `afterPropertiesSet` 或自定义的 init 方法）之前调用的。因此，不应该试图在构造函数中访问 `ApplicationContext`，因为它在那时可能还没有被设置。

### 九、总结

#### 最佳实践总结

1. **启动与配置**:
   - 在 `ApplicationContextAwareApplication` 的 `main` 方法中，我们使用 `AnnotationConfigApplicationContext` 来启动 Spring 容器，这是一个基于 Java 注解的配置方式。
   - `MyConfiguration` 类被指定为配置类，这意味着 Spring 将会查找这个类中定义的 beans 和配置。
2. **Bean 定义**:
   - 在 `MyConfiguration` 配置类中，我们使用 `@Bean` 注解定义了一个 `MyApplicationContextAware` 类型的 bean。这确保 `MyApplicationContextAware` 将被 Spring 容器管理。
3. **实现 Aware 接口**:
   - `MyApplicationContextAware` 类实现了 `ApplicationContextAware` 接口。这是一个特殊的接口，当一个 bean 实现它，Spring 容器会在 bean 初始化时自动调用 `setApplicationContext` 方法并传入当前的 `ApplicationContext` 对象。
4. **运行结果**:
   - 当应用启动时，Spring 容器确实检测到 `MyApplicationContextAware` 实现了 `ApplicationContextAware` 接口，并自动调用了 `setApplicationContext` 方法。
   - 控制台上的输出明确显示了这个过程，并显示了传递给该方法的 `ApplicationContext` 实例。
5. **结论**:
   - 通过 `ApplicationContextAware` 接口，我们可以轻松地在 Spring 容器中管理的 bean 中获取 `ApplicationContext`。这为我们提供了一个强大的机制，使得 bean 可以感知其所在的环境，并据此执行相应的操作。

#### 源码分析总结

1. **启动与上下文初始化**
   + 使用 `AnnotationConfigApplicationContext` 启动 Spring 应用，并传递 `MyConfiguration` 作为参数进行上下文初始化。

2. **Spring 上下文刷新**
   + 在 `refresh()` 方法中，主要关注点是调用 `finishBeanFactoryInitialization(beanFactory)`，负责实例化所有非延迟加载的单例 bean。

3. **Bean 实例化**
   + `preInstantiateSingletons()` 方法在 Spring 上下文初始化完成后被调用，确保所有非延迟加载的单例 beans 都被实例化，对于每个 bean，都会调用 `getBean(beanName)`，这会触发 bean 的实例化、初始化以及依赖注入。

4. **Bean 创建与初始化**
   + 在 `doCreateBean` 方法中，核心操作是调用 `initializeBean` 进行 bean 初始化，确保 bean 完全配置并准备好，`initializeBean` 中会调用 `applyBeanPostProcessorsBeforeInitialization`，在 bean 初始化之前遍历所有已注册的 `BeanPostProcessor`。

5. **处理 Aware 接口**
   + `ApplicationContextAwareProcessor` 的作用是对实现了 "Aware" 接口的 beans 进行特殊处理。在 `invokeAwareInterfaces` 方法中，针对不同的 "Aware" 接口进行了处理，使得 beans 可以自动感知其运行环境或特定依赖。