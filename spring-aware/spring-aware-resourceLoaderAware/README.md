## ResourceLoaderAware

- [ResourceLoaderAware](#resourceloaderaware)
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

`ResourceLoaderAware` 接口，它用于为需要访问 `ResourceLoader` 的 bean 提供一个回调。`ResourceLoader` 是一个简单的策略接口，定义了如何加载底层资源（如类路径或文件系统资源）的方法。

### 二、接口源码

`ResourceLoaderAware` 是 Spring 框架自 10.03.2004 开始引入的一个核心接口。主要是希望被通知并访问 `ResourceLoader` 的对象提供了一个机制。实现此接口的对象可以获取到 `ResourceLoader`，从而加载资源。

```java
/**
 * 任何希望被通知 ResourceLoader（通常是 ApplicationContext）的对象都应实现此接口。
 * 这是通过 org.springframework.context.ApplicationContextAware 接口完全依赖 ApplicationContext 的另一种方式。
 *
 * 请注意，org.springframework.core.io.Resource 依赖也可以暴露为类型为 Resource 或 Resource[] 的bean属性，
 * 通过字符串在bean工厂中进行自动类型转换进行填充。这样就消除了为了访问特定文件资源而实现任何回调接口的需求。
 *
 * 当您的应用对象需要访问其名称经过计算的各种文件资源时，通常需要一个 ResourceLoader。
 * 一个好策略是使对象使用 org.springframework.core.io.DefaultResourceLoader，但仍然实现 ResourceLoaderAware，
 * 以允许在 ApplicationContext 中运行时覆盖。参考 org.springframework.context.support.ReloadableResourceBundleMessageSource 为例。
 *
 * 传入的 ResourceLoader 也可以检查是否为 org.springframework.core.io.support.ResourcePatternResolver 接口
 * 并相应地进行类型转换，以将资源模式解析为 Resource 对象的数组。在 ApplicationContext 中运行时，这总是可行的
 * （因为 context 接口扩展了 ResourcePatternResolver 接口）。默认情况下，使用 org.springframework.core.io.support.PathMatchingResourcePatternResolver；
 * 也可以查看 ResourcePatternUtils.getResourcePatternResolver 方法。
 *
 * 作为 ResourcePatternResolver 依赖的替代，考虑暴露类型为 Resource[] 的 bean 属性，通过模式字符串
 * 在bean工厂的绑定时间进行自动类型转换进行填充。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 10.03.2004
 * @see ApplicationContextAware
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.ResourceLoader
 * @see org.springframework.core.io.support.ResourcePatternResolver
 */
public interface ResourceLoaderAware extends Aware {

    /**
     * 设置此对象运行的 ResourceLoader。
     * 可能是一个 ResourcePatternResolver，可以通过 instanceof ResourcePatternResolver 检查。
     * 也可以查看 ResourcePatternUtils.getResourcePatternResolver 方法。
     * 在填充正常的bean属性之后但在像 InitializingBean 的 afterPropertiesSet 这样的初始化回调或自定义初始化方法之前被调用。
     * 在 ApplicationContextAware 的 setApplicationContext 之前调用。
     * @param resourceLoader 此对象要使用的 ResourceLoader
     * @see org.springframework.core.io.support.ResourcePatternResolver
     * @see org.springframework.core.io.support.ResourcePatternUtils#getResourcePatternResolver
     */
    void setResourceLoader(ResourceLoader resourceLoader);

}
```

### 三、主要功能

**资源加载回调**：当 bean 实现了 `ResourceLoaderAware` 接口，Spring 容器会在该 bean 初始化时，自动将一个 `ResourceLoader` 注入到该 bean 中，从而使得 bean 可以加载资源。

**提供资源加载策略**：通过 `ResourceLoader`, bean 可以加载各种类型的资源，如类路径资源、文件系统资源、URL 资源等。它为资源访问提供了一个统一的策略。

**减少对 ApplicationContext 的直接依赖**：虽然 `ApplicationContext` 也扩展了 `ResourceLoader` 的功能，但有时候 bean 只需要资源加载功能，而不需要其他的 ApplicationContext 功能。通过实现 `ResourceLoaderAware`，bean 可以只获得资源加载功能，从而降低与完整的 `ApplicationContext` 的耦合。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyResourceLoaderAware`类型的bean，最后调用`getResource`方法并传递了一个路径。

```java
public class ResourceLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyResourceLoaderAware resourceLoaderAware = context.getBean(MyResourceLoaderAware.class);
        resourceLoaderAware.getResource("classpath:xcs.txt");
    }
}
```

这里使用`@Bean`注解，定义了以个Bean，是为了确保 `MyResourceLoaderAware` 被 Spring 容器执行。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyResourceLoaderAware myResourceLoaderAware(){
        return new MyResourceLoaderAware();
    }
}
```

`MyResourceLoaderAware` 类是一个简单的实用工具，我们利用 Spring 的 `ResourceLoader` 机制，可以用于加载和打印资源内容。

```java
public class MyResourceLoaderAware implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void getResource(String location){
        try {
            Resource resource = resourceLoader.getResource(location);
            System.out.println("Resource content: " + new String(FileCopyUtils.copyToByteArray(resource.getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

运行结果发现， `MyResourceLoaderAware` 类成功地读取了资源文件的内容并将其打印到了控制台。

```java
Resource content: hello world
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: EnvironmentAware时序图
    participant ResourceLoaderAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyResourceLoaderAware
    
    ResourceLoaderAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
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
    ApplicationContextAwareProcessor->>MyResourceLoaderAware:setResourceLoader(resourceLoader)<br>设置resourceLoader
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>ResourceLoaderAwareApplication:初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyResourceLoaderAware`类型的bean，最后调用`getResource`方法并传递了一个路径。

```java
public class ResourceLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyResourceLoaderAware resourceLoaderAware = context.getBean(MyResourceLoaderAware.class);
        resourceLoaderAware.getResource("classpath:xcs.txt");
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
	if (bean instanceof ResourceLoaderAware) {
        ((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
    }
    // ... [代码部分省略以简化]
}
```

最后执行到我们自定义的逻辑中，`MyResourceLoaderAware` 类是一个简单的实用工具，我们利用 Spring 的 `ResourceLoader` 机制，可以用于加载和打印资源内容。

```java
public class MyResourceLoaderAware implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void getResource(String location){
        try {
            Resource resource = resourceLoader.getResource(location);
            System.out.println("Resource content: " + new String(FileCopyUtils.copyToByteArray(resource.getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 七、注意事项

**资源路径**：当使用 `ResourceLoader` 获取资源时，需要提供完整的路径。例如，使用 "classpath:" 前缀来加载类路径上的资源。你应确保路径是正确的，否则 `ResourceLoader` 可能找不到资源。

**资源缓存**：`ResourceLoader` 本身不提供资源内容的缓存功能。每次调用 `getResource` 方法都可能返回一个新的 `Resource` 实例。如果需要缓存资源内容，你应该自己实现。

**资源存在性检查**：使用 `ResourceLoader` 获取的 `Resource` 不保证资源确实存在。在尝试访问资源内容之前，你应使用 `Resource.exists()` 方法检查资源是否存在。

**资源类型的多样性**：根据运行环境和 `ResourceLoader` 的具体实现，它可以加载多种类型的资源，如类路径资源、文件系统资源、URL资源等。你应当了解当前环境支持的资源类型，并正确使用。

**避免过度使用**：虽然 `ResourceLoaderAware` 提供了一种方便的方式来访问资源，但不是所有的 beans 都需要它。只有当 bean 真正需要动态地加载资源时，才应实现这个接口。否则，更简洁的方式是直接注入 `Resource` 类型的属性。

**生命周期时机**：当一个 bean 实现了 `ResourceLoaderAware` 接口，`setResourceLoader` 方法会在 bean 初始化的早期被调用，这确保了后续的 bean 初始化和业务逻辑可以使用到 `ResourceLoader`。但你应确保不在构造函数中访问 `ResourceLoader`，因为它此时尚未被设置。

**避免过度使用**：虽然 `ResourceLoaderAware` 提供了一种方便的方式来访问资源，但不是所有的 beans 都需要它。只有当 bean 真正需要动态地加载资源时，才应实现这个接口。否则，更简洁的方式是直接注入 `Resource` 类型的属性。

**与 `ApplicationContextAware` 的区别**：`ApplicationContext` 本身也是一个 `ResourceLoader`，因此实现 `ApplicationContextAware` 也可以获得类似的资源加载功能。但如果你的 bean 只需要资源加载功能，而不需要其他的 `ApplicationContext` 功能，那么最好只实现 `ResourceLoaderAware` 以减少耦合。

### 八、总结

#### 8.1、最佳实践总结

**启动类入口**：使用了 `AnnotationConfigApplicationContext` 类来启动Spring应用。这是一个使用基于Java的注解来配置Spring容器的方式。上下文初始化时使用了 `MyConfiguration` 类作为配置类。接着，从Spring上下文中获取了一个 `MyResourceLoaderAware` 类型的bean。最后，调用了 `getResource` 方法并传入了一个指定的路径。

**配置类**：`MyConfiguration` 是一个标注有 `@Configuration` 的配置类，表示它是一个Spring配置类。在这个配置类中，通过 `@Bean` 注解定义了一个 `MyResourceLoaderAware` 类型的bean。这确保 `MyResourceLoaderAware` 被Spring容器管理，并且 `ResourceLoader` 被正确注入。

**资源加载实现**：`MyResourceLoaderAware` 类实现了 `ResourceLoaderAware` 接口，从而允许Spring容器在bean初始化时自动注入 `ResourceLoader`。`getResource` 方法使用注入的 `ResourceLoader` 来加载给定路径的资源，然后读取并打印资源的内容。

**运行结果**：当运行应用程序时，`MyResourceLoaderAware` 成功地从指定的资源路径加载内容，并将 "hello world" 打印到控制台。

#### 8.2、源码分析总结

**启动与上下文初始化**：使用 `AnnotationConfigApplicationContext` 创建了一个基于Java注解的Spring容器，传入了 `MyConfiguration` 作为配置。从上下文中获取 `MyResourceLoaderAware` 类型的bean，并调用了其 `getResource` 方法。

**配置类与Bean注册**：在 `MyConfiguration` 配置类中，通过 `@Bean` 注解注册了 `MyResourceLoaderAware` 类型的bean。

**上下文刷新与Bean实例化**：在上下文的 `refresh` 方法中，调用了 `finishBeanFactoryInitialization` 方法以实例化所有剩余的非懒加载单例Bean。在此方法中，调用了 `preInstantiateSingletons` 方法预先实例化所有非懒加载的单例bean。

**Bean获取与创建流程**：使用 `getBean` 方法来实际获取Bean，这可能会触发Bean的创建。在 `doGetBean` 方法中，如果bean还未创建，会尝试创建新实例，处理依赖关系，并返回正确的bean实例。

**单例Bean的创建与缓存**：在 `getSingleton` 方法中，首先尝试从单例缓存中获取bean实例。如果尚未创建，则使用提供的 `ObjectFactory` 创建新实例，并存入缓存。

**Bean初始化**：在Bean创建完成后，进行初始化。在 `initializeBean` 方法中，会对特定的bean应用 `BeanPostProcessor` 逻辑。

**Aware接口的处理**：使用 `ApplicationContextAwareProcessor` 处理实现了 `Aware` 接口的beans。对于实现了 `ResourceLoaderAware` 的beans，会注入一个 `ResourceLoader` 实例。

**自定义逻辑**：在 `MyResourceLoaderAware` 类中，利用注入的 `ResourceLoader`，加载并打印资源内容。