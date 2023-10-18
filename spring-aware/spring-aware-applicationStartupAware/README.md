## ApplicationStartupAware

- [ApplicationStartupAware](#applicationstartupaware)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133914474) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [ApplicationStartupAware源码]()

### 二、接口描述

`ApplicationStartupAware`接口，是为了提供对这一过程的细粒度跟踪。通过`StartupStep`，我们可以定义应用启动过程中的各个步骤，并收集关于它们的性能和上下文信息。

### 三、接口源码

`ApplicationStartupAware` 是 Spring 框架自 5.3 开始引入的一个核心接口。实现`ApplicationStartupAware`接口的对象会在Spring容器中被自动注入一个`ApplicationStartup`实例。

```java
/**
 * 任何希望在运行时被通知其关联的ApplicationStartup实例的对象都应实现此接口。
 * 在更简单的术语中，这是一个使bean意识到应用启动跟踪机制的接口。
 * 
 * @author Brian Clozel
 * @since 5.3
 * @see ApplicationContextAware
 */
public interface ApplicationStartupAware extends Aware {

    /**
     * 设置此对象运行时的ApplicationStartup。
     * 此方法的调用时机为正常bean属性填充之后，但在任何初始化回调（例如InitializingBean的afterPropertiesSet或自定义的初始化方法）之前。
     * 并且在ApplicationContextAware的setApplicationContext之前调用。
     * 
     * @param applicationStartup 由此对象使用的application startup实例
     */
    void setApplicationStartup(ApplicationStartup applicationStartup);
}
```

### 四、主要功能

1. **启动性能跟踪**
   + 通过提供对`ApplicationStartup`的访问，实现此接口的beans可以使用`StartupStep`API来跟踪它们在启动过程中的各个步骤。这对于检测和优化启动性能非常有用。

2. **为beans提供跟踪能力**
   + 而不仅仅是Spring框架内部使用。这意味着我们可以为他们的自定义beans或组件提供与Spring框架同样的启动跟踪能力。

3. **细粒度控制**
   + 与`StartupStep`一起使用，`ApplicationStartupAware`允许beans对其启动过程中的特定部分进行跟踪，例如数据库初始化、外部服务连接或任何其他可能需要时间的操作。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），首先设置了`BufferingApplicationStartup`，这是Spring Boot提供的一个`ApplicationStartup`实现，缓存了最后的100个启动步骤。这使得我们可以在应用启动后查看并分析这些步骤，以便了解哪些操作可能会影响启动性能，然后使用`register`方法，我们告诉Spring上下文加载`MyConfiguration`类，最后调用`refresh`方法会触发应用上下文的初始化，包括bean的创建和依赖注入。

```java
public class ApplicationStartupAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setApplicationStartup(new BufferingApplicationStartup(100));
        context.register(MyConfiguration.class);
        context.refresh();
        context.close();
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保 `MyApplicationStartupAware` 被 Spring 容器执行。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyApplicationStartupAware myApplicationStartupAware(){
        return new MyApplicationStartupAware();
    }
}
```

`MyApplicationStartupAware`类的主要目的是展示如何使用`ApplicationStartup`来跟踪Spring应用程序启动过程中的特定逻辑。这对于我们程序来说是有用的，因为我们可以看到哪些启动步骤是最消耗时间的，然后据此进行优化。在这个具体的实现中，仅仅模拟了两个步骤，但在实际应用中，可以跟踪任意数量和类型的步骤。

```java
public class MyApplicationStartupAware implements ApplicationStartupAware, InitializingBean {

    private ApplicationStartup applicationStartup;

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StartupStep step1 = applicationStartup.start("MyApplicationStartupAware Logic Step 1");
        // 自定义逻辑
        Thread.sleep(200);
        step1.tag("status", "done").end();

        StartupStep step2 = applicationStartup.start("MyApplicationStartupAware Logic Step 2");
        // 自定义逻辑
        Thread.sleep(300);
        step2.tag("status", "done").end();
    }
}
```

### 六、时序图

~~~mermaid
sequenceDiagram
    Title: ApplicationStartupAware时序图
    participant ApplicationStartupAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyApplicationStartupAware
    
    ApplicationStartupAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
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
    ApplicationContextAwareProcessor->>MyApplicationStartupAware:setApplicationStartup(applicationStartup)<br>设置运行环境
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>ApplicationStartupAwareApplication:初始化完成
~~~

### 七、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），首先设置了`BufferingApplicationStartup`，这是Spring Boot提供的一个`ApplicationStartup`实现，缓存了最后的100个启动步骤。这使得我们可以在应用启动后查看并分析这些步骤，以便了解哪些操作可能会影响启动性能，然后使用`register`方法，我们告诉Spring上下文加载`MyConfiguration`类，最后调用`refresh`方法会触发应用上下文的初始化，包括bean的创建和依赖注入。

```java
public class ApplicationStartupAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setApplicationStartup(new BufferingApplicationStartup(100));
        context.register(MyConfiguration.class);
        context.refresh();
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
	if (bean instanceof ApplicationStartupAware) {
        ((ApplicationStartupAware) bean).setApplicationStartup(this.applicationContext.getApplicationStartup());
    }
    // ... [代码部分省略以简化]
}
```

最后执行到我们自定义的逻辑中，`MyApplicationStartupAware`类的主要目的是展示如何使用`ApplicationStartup`来跟踪Spring应用程序启动过程中的特定逻辑。这对于我们程序来说是有用的，因为我们可以看到哪些启动步骤是最消耗时间的，然后据此进行优化。在这个具体的实现中，仅仅模拟了两个步骤，但在实际应用中，可以跟踪任意数量和类型的步骤。

```java
public class MyApplicationStartupAware implements ApplicationStartupAware, InitializingBean {

    private ApplicationStartup applicationStartup;

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        this.applicationStartup = applicationStartup;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StartupStep step1 = applicationStartup.start("MyApplicationStartupAware Logic Step 1");
        // 自定义逻辑
        Thread.sleep(200);
        step1.tag("status", "done").end();

        StartupStep step2 = applicationStartup.start("MyApplicationStartupAware Logic Step 2");
        // 自定义逻辑
        Thread.sleep(300);
        step2.tag("status", "done").end();
    }
}
```

### 八、注意事项

1. **生命周期时机**
   + `setApplicationStartup`方法在其他bean属性设置之后、`InitializingBean`的`afterPropertiesSet`方法之前调用。确保我们的bean在这一阶段不依赖于其他尚未初始化或注入的属性。

2. **性能考虑**
   + 虽然启动跟踪对于分析应用程序启动时间很有用，但添加太多的启动步骤跟踪可能会对性能产生微小的影响。在生产环境中，我们可能需要权衡跟踪的详细程度和性能的关系。

3. **清晰的步骤名称**
   + 当定义`StartupStep`时，为其提供清晰、描述性的名称，这样其他我们可以更容易地理解它代表的步骤。

4. **不要滥用**
   + 尽量只为那些真正重要和可能影响启动性能的步骤使用启动跟踪。不需要为每个小操作都添加跟踪。

5. **不要忘记结束步骤**
   + 每当我们开始一个`StartupStep`，记得在适当的时机调用`end`方法结束它。否则，该步骤可能会在报告中显示为仍在运行，这可能会导致混淆。

### 九、总结

#### 最佳实践总结

1. **启动类概述**
   + 使用`AnnotationConfigApplicationContext`，一个基于Java注解的Spring上下文初始化方法。设置`BufferingApplicationStartup`来缓存应用启动过程的最后100个步骤。这样可以分析哪些步骤可能影响启动性能。注册`MyConfiguration`类以加载相应的配置。刷新并初始化应用上下文，从而触发bean的创建和依赖注入。关闭上下文。

2. **配置类概述**
   + 使用`@Configuration`注解标记，告诉Spring这是一个配置类。通过`@Bean`注解定义了`MyApplicationStartupAware` bean。这样可以确保它被Spring容器处理，并在容器启动时执行其生命周期方法。

3. **`MyApplicationStartupAware`类概述**
   + 实现了`ApplicationStartupAware`接口，允许它在启动时获知`ApplicationStartup`实例。定义了两个启动步骤来模拟潜在的长时间运行任务，并使用`StartupStep`进行跟踪。在每个步骤的末尾，都有一个标记状态为"done"，然后结束该步骤。

#### 源码分析总结

1. **实例化Beans**
   + 在`AbstractApplicationContext`的`refresh()`方法中，`finishBeanFactoryInitialization`方法被调用，确保所有单例Bean被预先实例化。

2. **Bean预实例化**
   + `DefaultListableBeanFactory`的`preInstantiateSingletons`方法确保所有非懒加载的单例Beans被实例化。核心操作是调用`getBean(beanName)`。

3. **获取Bean实例**
   + `AbstractBeanFactory`的`getBean`方法进一步调用`doGetBean`来真正实例化Bean，处理异常和依赖，并返回Bean实例。

4. **Bean单例获取**
   + `DefaultSingletonBeanRegistry`的`getSingleton`方法确保Bean以单例形式存在，从缓存获取或使用提供的`ObjectFactory`创建新实例。

5. **创建Bean实例**
   + `AbstractAutowireCapableBeanFactory`的`createBean`方法调用`doCreateBean`进行Bean的实际实例化，并进行初始化，确保Bean完全配置并准备就绪。

6. **Bean初始化**
   + `AbstractAutowireCapableBeanFactory`的`initializeBean`方法确保Bean被正确初始化，其中调用`applyBeanPostProcessorsBeforeInitialization`方法是Spring生命周期中的关键点，允许BeanPostProcessors在Bean初始化之前进行操作。

7. **处理Aware接口**
   + 在Bean初始化过程中，`ApplicationContextAwareProcessor`确保实现了`Aware`接口的Beans被正确处理，这些Beans会自动"感知"并获得其运行环境或特定依赖的引用。

8. **自定义逻辑执行**
   + `MyApplicationStartupAware`类实现了`ApplicationStartupAware`接口，它将接收一个`ApplicationStartup`实例。