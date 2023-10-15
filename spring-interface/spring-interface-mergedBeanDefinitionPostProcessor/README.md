## MergedBeanDefinitionPostProcessor

- [MergedBeanDefinitionPostProcessor](#mergedbeandefinitionpostprocessor)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845274) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [MergedBeanDefinitionPostProcessor源码](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)

### 二、接口描述

`MergedBeanDefinitionPostProcessor` 是 Spring 框架中的一个接口，主要用于在 bean 定义被合并后（但在 bean 实例化之前）进行后处理。它扩展了 `BeanPostProcessor`，增加了处理合并 bean 定义的能力。

### 三、接口源码

`MergedBeanDefinitionPostProcessor` 是 Spring 框架自 2.5 版本开始引入的一个核心接口。其中的核心方法是`postProcessMergedBeanDefinition` 主要用途为提供了一个自定义或查询合并的 bean 定义的机会，例如应用自定义注释、修改 bean 元数据或基于合并的 bean 定义实现自定义行为。

```java
/**
 * 用于在运行时后处理合并的 bean 定义的回调接口。
 * BeanPostProcessor 实现可以实现此子接口，以便在 Spring 的 BeanFactory 
 * 用于创建 bean 实例的时候对已合并的 bean 定义（原始 bean 定义的处理副本）进行后处理。
 *
 * #postProcessMergedBeanDefinition 方法可以用于内省 bean 定义，
 * 例如在后缀处理器 bean 的实例之前准备一些缓存的元数据。它也允许修改 bean 定义，
 * 但仅限于那些实际上用于并发修改的定义属性。本质上，这只应用于 RootBeanDefinition 
 * 本身上定义的操作，但不包括其基类的属性。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getMergedBeanDefinition
 */
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

	/**
	 * 后处理指定 bean 的给定合并 bean 定义。
	 * @param beanDefinition bean 的合并定义
	 * @param beanType 管理的 bean 实例的实际类型
	 * @param beanName bean 的名称
	 * @see AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors
	 */
	void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

	/**
	 * 通知指定名称的 bean 定义已被重置，
	 * 这个 post-processor 应该清除受影响的 bean 的任何元数据。
	 * 默认实现是空的。
	 * @param beanName bean 的名称
	 * @since 5.1
	 * @see DefaultListableBeanFactory#resetBeanDefinition
	 */
	default void resetBeanDefinition(String beanName) {
	}
}
```

### 四、主要功能

1. **处理合并后的 Bean 定义**
   + 在 Spring 中，一个 bean 可以继承另一个 bean 的配置，产生所谓的 "合并后的" bean 定义。这个合并的定义包括原始 bean 定义和任何父 bean 定义中的属性。`MergedBeanDefinitionPostProcessor` 允许我们在 bean 的实例化和初始化之前，基于这个合并的定义执行定制逻辑。

2. **缓存元数据**
   + 这个接口常常被用于检查 bean 定义并缓存相关的元数据，从而加速后续的 bean 实例化和初始化。例如，Spring 的 `AutowiredAnnotationBeanPostProcessor` 使用它来缓存 `@Autowired` 和 `@Value` 注解的信息。

3. **修改合并后的 Bean 定义**
   + 虽然不是主要的使用场景，但 `MergedBeanDefinitionPostProcessor` 也允许修改合并后的 bean 定义。但这种修改应该小心进行，并且通常只限于那些真正用于并发修改的定义属性。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyBean`类型的bean，最后打印了该`getMessage`方法返回的值。

```java
public class MergedBeanDefinitionPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBean bean = context.getBean(MyBean.class);
        System.out.println("message = " + bean.getMessage());
    }
}
```

这里使用`@Bean`注解，定义了两个Bean，是为了确保`MyBean`， `MergedBeanDefinitionPostProcessor` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MergedBeanDefinitionPostProcessor myBeanPostProcessor() {
        return new MyMergedBeanDefinitionPostProcessor();
    }

    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
}
```

实现了 `MergedBeanDefinitionPostProcessor` 的类 `MyMergedBeanDefinitionPostProcessor`。这个类针对带有自定义注解 `MyValue` 的属性进行处理。在`postProcessMergedBeanDefinition`方法中，对于每个字段，检查是否有 `MyValue` 注解。如果有，则获取注解的值，并将字段和该值存储在 `defaultValues` 映射中。在`postProcessAfterInitialization`方法中，检查 `metadata` 是否包含这个 bean 的名称。如果包含，表示这个 bean 有需要处理的字段，还需检查该字段的当前值。如果字段的值为 null，则使用注解提供的值来设置该字段的值。

```java
public class MyMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

    /**
     * 记录元数据
     */
    private Map<String, Map<Field, String>> metadata = new HashMap<>();

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        Field[] fields = beanType.getDeclaredFields();
        Map<Field, String> defaultValues = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyValue.class)) {
                MyValue annotation = field.getAnnotation(MyValue.class);
                defaultValues.put(field, annotation.value());
            }
        }
        if (!defaultValues.isEmpty()) {
            metadata.put(beanName, defaultValues);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (metadata.containsKey(beanName)) {
            Map<Field, String> defaultValues = metadata.get(beanName);
            for (Map.Entry<Field, String> entry : defaultValues.entrySet()) {
                Field field = entry.getKey();
                String value = entry.getValue();
                try {
                    field.setAccessible(true);
                    if (field.get(bean) == null) {
                        field.set(bean, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
```

一个简单的注解类

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MyValue {
    String value();
}
```

一个简单的Bean类

```java
public class MyBean {

    @MyValue("hello world")
    private String message;

    public String getMessage() {
        return message;
    }
}
```

运行结果发现，当 `MyBean` 实例化并初始化时，由于 `message` 字段的值未被明确设置，`MyMergedBeanDefinitionPostProcessor` 会使用 `MyValue` 注解中的默认值，即 "hello world"，来为其赋值。

```
message = hello world
```

### 六、时序图

~~~mermaid
sequenceDiagram
        Title: MergedBeanDefinitionPostProcessor时序图
    participant MergedBeanDefinitionPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyMergedBeanDefinitionPostProcessor
    
    MergedBeanDefinitionPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新上下文
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>初始化Bean工厂
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>实例化单例
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>获取Bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>执行获取Bean
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>获取单例Bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>获取Bean实例
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>创建Bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>执行Bean创建
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>应用Post处理器
    AbstractAutowireCapableBeanFactory->>MyMergedBeanDefinitionPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>处理合并定义
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>MergedBeanDefinitionPostProcessorApplication:初始化完成
~~~

### 七、源码分析

```java
public class MergedBeanDefinitionPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBean bean = context.getBean(MyBean.class);
        System.out.println("message = " + bean.getMessage());
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

`org.springframework.context.support.AbstractApplicationContext#refresh`方法中，我们重点关注一下`finishBeanFactoryInitialization(beanFactory)`这方法会对实例化所有剩余非懒加载的单列Bean对象，其他方法不是本次源码阅读的重点暂时忽略。

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，在 bean 的实例化前，会对合并的 bean 定义进行处理。这是 `MergedBeanDefinitionPostProcessors` 可以介入和修改合并后的 bean 定义的地方，为了确保对每个 bean 定义只执行一次后处理，有一个 `postProcessed` 标志，如果此标志为 `false`，则会调用 `applyMergedBeanDefinitionPostProcessors` 方法应用所有的 `MergedBeanDefinitionPostProcessors`，为了线程安全，此操作在 `mbd.postProcessingLock` 的同步块中执行。这确保了并发的 bean 创建请求不会导致对同一 bean 定义的重复后处理。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {
    // ... [代码部分省略以简化]
    // 允许MergedBeanDefinitionPostProcessor修改合并的bean定义
    synchronized (mbd.postProcessingLock) {
        if (!mbd.postProcessed) {
            try {
                applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
            }
            catch (Throwable ex) {
                // ... [代码部分省略以简化]
            }
            mbd.postProcessed = true;
        }
    }
    
    // ... [代码部分省略以简化]
    
    // 返回创建和初始化后的bean
    return exposedObject;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`方法中，遍历每一个 `MergedBeanDefinitionPostProcessor` 的 `postProcessMergedBeanDefinition` 方法，提供了一个自定义或查询合并的 bean 定义的机会。

```java
protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
    for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
        processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
    }
}
```

最后执行到我们自定义的逻辑中，对于每个字段，检查是否带有 `MyValue` 注解。如果带有，从该字段获取 `MyValue` 注解，并将字段与注解的值存储在 `defaultValues` `Map` 中，如果 `defaultValues` 不为空（即存在至少一个带有 `MyValue` 注解的字段），则将该 `Map` 存储在 `metadata` 中，键为 bean 的名称。

```java
public class MyMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

    /**
     * 记录元数据
     */
    private Map<String, Map<Field, String>> metadata = new HashMap<>();

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        Field[] fields = beanType.getDeclaredFields();
        Map<Field, String> defaultValues = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyValue.class)) {
                MyValue annotation = field.getAnnotation(MyValue.class);
                defaultValues.put(field, annotation.value());
            }
        }
        if (!defaultValues.isEmpty()) {
            metadata.put(beanName, defaultValues);
        }
    }
}
```

### 八、注意事项

1. **调用时机**
   + `postProcessMergedBeanDefinition` 是在 bean 处于一个 "半实例化" 的状态。更确切地说，在此时，bean 的实例已经被创建，但属性注入、初始化方法等还没有执行，这意味着我们不应该在此方法中尝试访问 bean 实例。

2. **避免修改不可变属性**
   + 虽然我们可以在 `postProcessMergedBeanDefinition` 方法中修改 `RootBeanDefinition`，但应该小心只修改那些预期为可变的属性。例如（Bean的类名，构造函数参数值，懒加载标记，依赖信息，作用域，等等）

3. **影响性能**
   + 如果 `postProcessMergedBeanDefinition` 执行的操作很重，这可能会影响应用的启动性能，因为它会被每个 bean 的创建过程调用。

4. **防止无限递归**
   + 如果我们在 `postProcessMergedBeanDefinition` 方法中尝试获取其他 beans，这可能会触发那些 beans 的创建，从而再次调用 `postProcessMergedBeanDefinition`。我们应该注意避免这种无限递归的情况。

### 九、总结

#### 最佳实践总结

1. **启动类入口**
   + `MergedBeanDefinitionPostProcessorApplication` 是应用的主入口。在这里，我们使用了 `AnnotationConfigApplicationContext` 来初始化和配置 Spring 容器，并为其提供了一个配置类 `MyConfiguration`。

2. **配置类**
   + 在 `MyConfiguration` 中，我们定义了两个bean：一个是自定义的 `MyMergedBeanDefinitionPostProcessor`，另一个是一个简单的 `MyBean` 类实例。

3. **自定义后处理器**
   + `MyMergedBeanDefinitionPostProcessor` 实现了 `MergedBeanDefinitionPostProcessor` 接口，允许我们在bean的实例化之前处理和修改其定义。在这个示例中，我们检查bean的字段是否有 `MyValue` 注解。如果有，我们将字段的名称和注解的值存储在一个映射中。然后，在bean的实例化和初始化后，我们检查是否需要为字段设置值。如果字段的当前值是 `null`，我们使用 `MyValue` 注解提供的值来设置它。

4. **自定义注解**
   + `MyValue` 是一个简单的自定义注解，用于指定一个字段的默认值。

5. **目标Bean**
   + `MyBean` 是一个简单的Java类，其中一个字段 `message` 被标记为 `MyValue("hello world")`。这意味着，如果在Spring容器初始化此bean时，`message` 字段没有被明确设置一个值，那么它将使用 `MyValue` 注解中的默认值 "hello world"。

6. **执行结果**
   + 当应用程序运行时，Spring容器会实例化并初始化 `MyBean`。由于 `message` 字段的值未被明确设置，因此 `MyMergedBeanDefinitionPostProcessor` 将使用 `MyValue` 注解中的默认值 "hello world" 为其赋值。最后，应用程序输出 "message = hello world"。

#### 源码分析总结

1. **启动类**
   + 应用的主入口是`MergedBeanDefinitionPostProcessorApplication`。它使用`AnnotationConfigApplicationContext`来初始化Spring容器，并传入配置类`MyConfiguration`。

2. **初始化Spring容器**
   + 在`AnnotationConfigApplicationContext`的构造函数中，除了一些基本的配置外，它主要调用了`refresh()`方法来完成容器的初始化。

3. **容器刷新**
   + `refresh()`方法是在`AbstractApplicationContext`中定义的，用于完成容器的初始化。其中，`finishBeanFactoryInitialization(beanFactory)`方法被用来实例化所有非懒加载的单例Bean对象。

4. **实例化单例Beans**
   + `preInstantiateSingletons()`方法在`DefaultListableBeanFactory`中被调用，用于预先实例化所有非懒加载的单例bean。该方法通过调用`getBean(beanName)`来实例化和初始化bean。

5. **Bean获取**
   + `getBean()`方法在`AbstractBeanFactory`中定义，它最终会调用`doGetBean()`方法来完成实际的Bean创建工作。

6. **Bean的创建**
   + `doGetBean()`方法处理bean的查找、创建和依赖处理。如果请求的bean是一个单例并且尚未创建，则它将使用`getSingleton()`方法从单例缓存中获取或创建新的实例。

7. **处理单例Beans**
   + 在`DefaultSingletonBeanRegistry`中，`getSingleton()`方法用于从单例缓存中获取已存在的bean或使用`ObjectFactory`创建新的实例。

8. **实际Bean的创建**
   + 在`AbstractAutowireCapableBeanFactory`中，`createBean()`方法是Bean创建的入口，它主要调用`doCreateBean()`方法。在`doCreateBean()`中，`applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName)`方法允许`MergedBeanDefinitionPostProcessors`修改合并的bean定义。

9. **应用合并的Bean定义后处理器**
   + `applyMergedBeanDefinitionPostProcessors()`方法遍历并调用每个`MergedBeanDefinitionPostProcessor`的`postProcessMergedBeanDefinition()`方法。这为每个合并的Bean定义提供了自定义或查询的机会。

10. **自定义后处理器逻辑**
    + 在我们的例子中，`MyMergedBeanDefinitionPostProcessor`对带有`MyValue`注解的属性进行了处理。它在`postProcessMergedBeanDefinition()`中检查每个字段是否有`MyValue`注解，并为这些字段收集默认值。