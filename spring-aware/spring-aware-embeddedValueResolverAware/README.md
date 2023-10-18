## EmbeddedValueResolverAware

- [EmbeddedValueResolverAware](#embeddedvalueresolveraware)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133914999) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [EmbeddedValueResolverAware源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-embeddedValueResolverAware)

### 二、接口描述

`EmbeddedValueResolverAware` 接口，主要用于提供一个字符串值解析器，这可以在 Bean 属性中解析占位符和表达式。如果我们熟悉 Spring 的 `${...}` 占位符和 `#{...}` 表达式，那么这个接口将帮助我们在自定义组件中解析这些值。

### 三、接口源码

`EmbeddedValueResolverAware` 是 Spring 框架自 3.0.3 开始引入的一个核心接口。实现`EmbeddedValueResolverAware`接口的对象会在Spring容器中被自动注入一个`StringValueResolver`实例。

```java
/**
 * 如果对象希望被通知一个 StringValueResolver，以解析嵌入的定义值，那么它应实现此接口。
 *
 * 这提供了一个通过 ApplicationContextAware/BeanFactoryAware 接口
 * 依赖于完整的 ConfigurableBeanFactory 的替代方法。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0.3
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#resolveEmbeddedValue(String)
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getBeanExpressionResolver()
 * @see org.springframework.beans.factory.config.EmbeddedValueResolver
 */
public interface EmbeddedValueResolverAware extends Aware {

	/**
	 * 设置用于解析嵌入定义值的 StringValueResolver。
	 */
	void setEmbeddedValueResolver(StringValueResolver resolver);

}
```

### 四、主要功能

1. **解析嵌入的字符串值**
   + 当我们在 Bean 的属性或构造函数参数中有一个值，如 `${property.name}` 或 `#{some.expression}`，这需要被解析成实际的值时，`StringValueResolver` 可以帮助做这件事。

2. **避免对 `ConfigurableBeanFactory` 的直接依赖**
   + 通过使用 `EmbeddedValueResolverAware`，我们可以间接地得到这种解析功能，而不必直接依赖于整个 `ConfigurableBeanFactory`。这提供了一种更轻量级、更关注特定功能的方法来解析嵌入的值。

3. **自动注入 `StringValueResolver`**
   + 当我们的 Bean 实现了 `EmbeddedValueResolverAware` 接口，Spring 容器会在 Bean 初始化时自动调用 `setEmbeddedValueResolver` 方法，为其注入一个 `StringValueResolver` 实例。这样，Bean 可以在其生命周期中任何时候使用它来解析字符串值。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyEmbeddedValueResolverAware`类型的bean，最后调用`resolve`方法。

```java
public class EmbeddedValueResolverAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEmbeddedValueResolverAware resolverAware = context.getBean(MyEmbeddedValueResolverAware.class);
        resolverAware.resolve();
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保 `MyEmbeddedValueResolverAware` 被 Spring 容器执行。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyEmbeddedValueResolverAware myEmbeddedValueResolverAware(){
        return new MyEmbeddedValueResolverAware();
    }
}
```

`MyEmbeddedValueResolverAware`类实现了`EmbeddedValueResolverAware`接口，当Spring容器初始化此类的Bean时，此方法将被调用，容器将传入一个`StringValueResolver`实例，然后通过`resolve()`方法，使用注入的`stringValueResolver`来解析包含占位符和SpEL表达式的字符串，并将解析后的字符串打印到控制台。

```java
public class MyEmbeddedValueResolverAware implements EmbeddedValueResolverAware {

    private StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    public void resolve() {
        String resolvedValue = stringValueResolver.resolveStringValue("Hello, ${user.name:xcs}! Today is #{T(java.time.LocalDate).now().toString()}");
        System.out.println(resolvedValue);
    }
}
```

运行结果发现，结合 Spring 的 `Environment` 和 SpEL 功能来解析嵌入的字符串值，并得到了预期的运行结果。

```java
Hello, Lex! Today is 2023-10-03
```

### 六、时序图

~~~mermaid
sequenceDiagram
    Title: EnvironmentAware时序图
    participant EmbeddedValueResolverAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyEmbeddedValueResolverAware
    
    EmbeddedValueResolverAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
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
    ApplicationContextAwareProcessor->>MyEmbeddedValueResolverAware:setEmbeddedValueResolver(resolver)<br>设置StringValueResolver
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>EmbeddedValueResolverAwareApplication:初始化完成
~~~

### 七、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyEmbeddedValueResolverAware`类型的bean，最后调用`resolve`方法。

```java
public class EmbeddedValueResolverAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEmbeddedValueResolverAware resolverAware = context.getBean(MyEmbeddedValueResolverAware.class);
        resolverAware.resolve();
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
	if (bean instanceof EmbeddedValueResolverAware) {
        ((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
    }
    // ... [代码部分省略以简化]
}
```

最后执行到我们自定义的逻辑中，`MyEmbeddedValueResolverAware`类实现了`EmbeddedValueResolverAware`接口，当Spring容器初始化此类的Bean时，此方法将被调用，容器将传入一个`StringValueResolver`实例，然后通过`resolve()`方法，使用注入的`stringValueResolver`来解析包含占位符和SpEL表达式的字符串，并将解析后的字符串打印到控制台。

```java
public class MyEmbeddedValueResolverAware implements EmbeddedValueResolverAware {

    private StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }

    public void resolve() {
        String resolvedValue = stringValueResolver.resolveStringValue("Hello, ${user.name:xcs}! Today is #{T(java.time.LocalDate).now().toString()}");
        System.out.println(resolvedValue);
    }
}
```

### 八、注意事项

1. **正确的环境**
   + 确保我们在 Spring 的环境中使用它，因为 `StringValueResolver` 需要 Spring 上下文来正确解析嵌入的值。

2. **非延迟依赖注入**
   + `setEmbeddedValueResolver` 方法在 Bean 初始化时调用。如果我们太早地尝试使用 `StringValueResolver`（例如，在构造函数中），它可能还没有被注入。

3. **默认值**
   + 当使用 `${user.name:xcs}` 语法时，如果 `user.name` 没有在环境中定义，它将使用 `xcs`。这可以避免因缺少配置而导致的错误。

4. **明确解析的范围**
   +  `EmbeddedValueResolverAware` 通常用于解析占位符和 SpEL 表达式。确保不将它与更复杂的 Bean 解析逻辑混淆。

5. **错误处理**
   + 当解析一个字符串值失败时，Spring 通常会抛出一个异常。确保在代码中适当地处理这些异常。

6. **与其他 Aware 接口的交互**
   + 如果我们的 Bean 实现了多个 `Aware` 接口，需要确保我们理解了每个接口的初始化时机和顺序，以及如何与其他 Aware 方法（如 `setBeanFactory` 或 `setApplicationContext`）交互。

### 九、总结

#### 最佳实践总结

1. **启动类**
   + 在 `EmbeddedValueResolverAwareApplication` 中，我们初始化了 Spring 的 `AnnotationConfigApplicationContext` 并加载了 `MyConfiguration` 作为配置类。接着，我们从上下文中取得 `MyEmbeddedValueResolverAware` 的 Bean，并调用了其 `resolve` 方法。

2. **配置与Bean声明**
   + 在 `MyConfiguration` 配置类中，我们声明了 `MyEmbeddedValueResolverAware` 为一个 Bean，这确保了它会被 Spring 容器管理，并且会接收到 `StringValueResolver` 的实例注入。

3. **嵌入值解析**
   + `MyEmbeddedValueResolverAware` 类实现了 `EmbeddedValueResolverAware` 接口，这意味着在该 Bean 被初始化时，Spring 会自动提供一个 `StringValueResolver` 实例。这个解析器之后被用于解析字符串 "Hello, ${user.name:xcs}! Today is #{T(java.time.LocalDate).now().toString()}"。

#### 源码分析总结

1. **应用启动**
   + 在`EmbeddedValueResolverAwareApplication`类中，使用`AnnotationConfigApplicationContext`来启动Spring应用并加载`MyConfiguration`配置类。

2. **容器初始化**
   + 在构造函数`AnnotationConfigApplicationContext`中，`refresh()`方法被调用来初始化Spring容器。

3. **实例化Beans**
   + 在`AbstractApplicationContext`的`refresh()`方法中，`finishBeanFactoryInitialization`方法被调用，确保所有单例Bean被预先实例化。

4. **Bean预实例化**
   + `DefaultListableBeanFactory`的`preInstantiateSingletons`方法确保所有非懒加载的单例Beans被实例化。核心操作是调用`getBean(beanName)`。

5. **获取Bean实例**
   + `AbstractBeanFactory`的`getBean`方法进一步调用`doGetBean`来真正实例化Bean，处理异常和依赖，并返回Bean实例。

6. **Bean单例获取**
   + `DefaultSingletonBeanRegistry`的`getSingleton`方法确保Bean以单例形式存在，从缓存获取或使用提供的`ObjectFactory`创建新实例。

7. **创建Bean实例**
   + `AbstractAutowireCapableBeanFactory`的`createBean`方法调用`doCreateBean`进行Bean的实际实例化，并进行初始化，确保Bean完全配置并准备就绪。

8. **Bean初始化**
   + `AbstractAutowireCapableBeanFactory`的`initializeBean`方法确保Bean被正确初始化，其中调用`applyBeanPostProcessorsBeforeInitialization`方法是Spring生命周期中的关键点，允许BeanPostProcessors在Bean初始化之前进行操作。

9. **处理Aware接口**
   + 在Bean初始化过程中，`ApplicationContextAwareProcessor`确保实现了`Aware`接口的Beans被正确处理，这些Beans会自动"感知"并获得其运行环境或特定依赖的引用。

10. **值解析**
    + 最后，我们的`MyEmbeddedValueResolverAware` Bean接收到了一个`StringValueResolver`实例。此时，当`resolve`方法被调用，它会使用这个解析器来解析嵌入的字符串值，并打印到控制台。