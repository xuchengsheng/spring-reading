## ImportAware

- [ImportAware](#importaware)
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

`ImportAware` 接口，提供被导入类的访问功能。当一个类实现了 `ImportAware` 接口，并且被通过 @Import 注解导入到其他配置类中，该类可以获得对导入它的 `AnnotationMetadata` 的访问权。

### 二、接口源码

`ApplicationStartupAware` 是 Spring 框架自 3.1 开始引入的一个核心接口。实现`ImportAware`接口的对象会在Spring容器中被自动注入一个`AnnotationMetadata`实例。

```java
/**
 * 任何希望被注入其导入它的Configuration类的AnnotationMetadata的Configuration类都应实现此接口。
 * 与使用Import作为元注解的注解结合使用时特别有用。
 * 
 * @author Chris Beams
 * @since 3.1
 */
public interface ImportAware extends Aware {

	/**
	 * 设置导入的@Configuration类的注解元数据。
	 */
	void setImportMetadata(AnnotationMetadata importMetadata);

}
```

### 三、主要功能

**访问导入类的注解元数据**：当一个类实现了 `ImportAware` 接口，并且它是通过 `@Import` 或其他特定方式被导入的，Spring 容器会自动调用它的 `setImportMetadata` 方法，并传入与导入该类的注解相关的 `AnnotationMetadata`。

**条件性的行为**：通过访问导入类的注解元数据，可以实现基于特定条件的行为。例如，根据导入类上的注解属性，决定是否注册某个 bean，或者为 bean 设置特定的属性值。

**框架和库的开发**：`ImportAware` 在 Spring 框架内部和某些第三方库中被用于执行特定的初始化和配置任务。例如，某些特性的自动配置可能会根据导入它们的配置类上的注解属性进行调整。

**增强诊断和调试信息**：可以基于导入类的元数据为开发者提供更多的上下文信息，这在诊断复杂的配置问题时可能会很有用。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`String`类型的bean并打印。

```java
public class ImportAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        String customBean  = context.getBean(String.class);
        System.out.println(customBean);
    }
}
```

 `MyConfiguration` 是一个 Spring 配置类，然后通过这个类启用了通过 `@EnableXcs` 注解提供`ImportAware`功能。

```java
@Configuration
@EnableXcs
public class MyConfiguration {

}
```

`EnableXcs`是一个注解类，`@Import(MyImportAware.class)`会对 Spring 上下文进行某种配置或修改

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MyImportAware.class)
public @interface EnableXcs {
    
}
```

由于 `MyImportAware` 实现了 `ImportAware`，它会检查导入它的配置类上是否存在 `@EnableXcs` 注解。如果存在，则继续处理并注册 `customBean` 这个 String 类型的 bean 到 Spring 上下文中。如果不存在 `@EnableXcs` 注解，则抛出异常。

```java
public class MyImportAware implements ImportAware {

    private AnnotationAttributes enableXcs;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableXcs = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableXcs.class.getName(), false));
        if (this.enableXcs == null) {
            throw new IllegalArgumentException(
                    "@EnableXcs is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    public String customBean() {
        return "This is a custom bean!";
    }
}
```

运行结果发现，当我们在 Spring 上下文中使用 `@EnableXcs` 注解并运行程序时，`MyImportAware` 类会被导入并处理，最后在 Spring 容器中注册一个 String 类型的 bean，其值为 "This is a custom bean!"。

```java
This is a custom bean!
```

当我们不通过`@EnableXcs` 注解方式去导入`MyImportAware`类，而是直接在MyConfiguration类中导入`@Import(MyImportAware.class)`来看看另外一种情况。

```java
@Configuration
@Import(MyImportAware.class)
public class MyConfiguration {

}
```

运行结果发现，当我们直接使用`@Import(MyImportAware.class)`导入`MyImportAware`类而不使用`@EnableXcs`注解时，由于`MyConfiguration`上没有`@EnableXcs`注解，所以`enableXcs`的值为null，由于此时`enableXcs`是null，`MyImportAware`抛出了一个`IllegalArgumentException`异常。

```java
Caused by: java.lang.IllegalArgumentException: @EnableXcs is not present on importing class com.xcs.spring.config.MyConfiguration
	at com.xcs.spring.config.MyImportAware.setImportMetadata(MyImportAware.java:19)
	at org.springframework.context.annotation.ConfigurationClassPostProcessor$ImportAwareBeanPostProcessor.postProcessBeforeInitialization(ConfigurationClassPostProcessor.java:484)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(AbstractAutowireCapableBeanFactory.java:440)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1796)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:620)
	... 10 more
```



### 五、时序图

~~~mermaid
sequenceDiagram
    Title: ImportAware时序图
    participant ImportAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ImportAwareBeanPostProcessor
    participant MyImportAware
    
    ImportAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName,bean,mbd)
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(existingBean,beanName)
    AbstractAutowireCapableBeanFactory->>ImportAwareBeanPostProcessor: postProcessBeforeInitialization(bean,beanName)
    ImportAwareBeanPostProcessor->>MyImportAware:setImportMetadata(importMetadata)设置importMetadata
    ImportAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory: 返回Bean对象
	AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
	AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
	AnnotationConfigApplicationContext->>ImportAwareApplication: 初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`String`类型的bean并打印。

```java
public class ImportAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        String customBean  = context.getBean(String.class);
        System.out.println(customBean);
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们重点关注`refresh()`方法。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

在`org.springframework.context.support.AbstractApplicationContext#refresh`方法中，我们重点关注一下`finishBeanFactoryInitialization(beanFactory)`这方法会对实例化所有剩余非懒加载的单列Bean对象，其他方法不是本次源码阅读的重点暂时忽略。

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，主要负责bean初始化。

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean()`方法中，核心逻辑是对`BeanPostProcessors`接口中的`postProcessBeforeInitialization`进行回调。

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`方法中，遍历每一个 `BeanPostProcessor` 的 `postProcessBeforeInitialization` 方法都有机会对bean进行修改或增强。

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

在`org.springframework.context.annotation.ConfigurationClassPostProcessor.ImportAwareBeanPostProcessor#postProcessBeforeInitialization`方法中，主要作用是为实现了 `ImportAware` 接口的 beans 设置导入它们的类的 `AnnotationMetadata`。这样，任何实现了 `ImportAware` 接口的 bean 都可以知道它是由哪个类导入的，以及这个导入类上的所有注解信息。

```java
@Override
public Object postProcessBeforeInitialization(Object bean, String beanName) {
    if (bean instanceof ImportAware) {
        ImportRegistry ir = this.beanFactory.getBean(IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class);
        AnnotationMetadata importingClass = ir.getImportingClassFor(ClassUtils.getUserClass(bean).getName());
        if (importingClass != null) {
            ((ImportAware) bean).setImportMetadata(importingClass);
        }
    }
    return bean;
}
```

最后执行到我们自定义的逻辑中，由于 `MyImportAware` 实现了 `ImportAware`，它会检查导入它的配置类上是否存在 `@EnableXcs` 注解。如果存在，则继续处理并注册 `customBean` 这个 String 类型的 bean 到 Spring 上下文中。如果不存在 `@EnableXcs` 注解，则抛出异常。

```java
public class MyImportAware implements ImportAware {

    private AnnotationAttributes enableXcs;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableXcs = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableXcs.class.getName(), false));
        if (this.enableXcs == null) {
            throw new IllegalArgumentException(
                    "@EnableXcs is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    public String customBean() {
        return "This is a custom bean!";
    }
}
```

### 七、注意事项

**明确需求**：在决定实现 `ImportAware` 之前，请确保您确实需要知道是哪个类导入了您的组件，并且需要访问其注解元数据。避免不必要的复杂性。

**正确的上下文**：`ImportAware` 只对通过 `@Import` 导入的类有意义。对于其他方式注册的 beans（例如，通过 component scanning 或 XML 配置），`setImportMetadata` 方法可能不会被调用。

**小心处理元数据**：当访问 `AnnotationMetadata` 时，确保处理不存在的注解或属性的情况，以避免空指针异常。

**注意与其他 `BeanPostProcessor` 的交互**：`ImportAware` 的功能部分是通过 `BeanPostProcessor` 机制实现的。如果您在应用中使用其他 `BeanPostProcessor`，请确保您了解它们之间的交互和执行顺序。

**不要过度使用**：虽然 `ImportAware` 可以带来一些灵活性，但不应在不需要的地方使用它。过度使用可能会导致配置变得复杂且难以追踪。

### 八、总结

#### 8.1、最佳实践总结

**初始化与运行**：使用 `AnnotationConfigApplicationContext` 初始化了一个 Spring 上下文，加载了 `MyConfiguration` 配置类，并从上下文中获取了一个类型为 `String` 的 bean。

**`@EnableXcs` 注解的作用**：`@EnableXcs` 是一个自定义注解，其主要作用是通过 `@Import` 注解导入 `MyImportAware` 类，从而启动 `ImportAware` 功能。

**`MyImportAware` 类与 `ImportAware`**：`MyImportAware` 实现了 `ImportAware` 接口，允许它获取关于导入它的类的注解信息。在 `setImportMetadata` 方法中，`MyImportAware` 会检查导入它的类是否有 `@EnableXcs` 注解。如果存在 `@EnableXcs` 注解，它会继续并注册一个 `String` 类型的 bean，值为 "This is a custom bean!"。如果不存在，它会抛出异常，提示 `@EnableXcs` 注解不存在于导入它的类上。

**正常使用**：当 `MyConfiguration` 使用 `@EnableXcs` 注解时，程序可以正常运行，从上下文中获取到的 String 类型的 bean 值为 "This is a custom bean!"。

**异常情况**：但如果 `MyConfiguration` 直接使用 `@Import(MyImportAware.class)` 导入 `MyImportAware` 类，而不使用 `@EnableXcs` 注解，会导致 `MyImportAware` 在查找 `@EnableXcs` 注解时发现它不存在，从而抛出异常。

#### 8.2、源码分析总结

**应用程序启动**： 使用 `AnnotationConfigApplicationContext` 初始化 Spring 上下文，加载 `MyConfiguration` 配置类。程序试图从 Spring 上下文中获取一个类型为 `String` 的 bean。

**上下文刷新**： 在构造 `AnnotationConfigApplicationContext` 时，会调用 `refresh()` 方法，这是 Spring 上下文的初始化和刷新过程的入口点。

**实例化Beans**： 执行 `finishBeanFactoryInitialization`，该方法负责预实例化上下文中的所有非懒加载单例bean。对于每个bean，它都会调用 `getBean` 方法。

**处理 `ImportAware` Beans**： 如果bean实现了 `ImportAware` 接口，`postProcessBeforeInitialization` 方法会为该 bean 设置导入它的类的注解元数据。在我们的例子中，`MyImportAware` 就是这样一个bean。

**检查 `@EnableXcs`**： 在 `MyImportAware` 的 `setImportMetadata` 方法中，它会检查导入它的类是否有 `@EnableXcs` 注解。如果存在该注解，则继续处理；如果不存在，则抛出异常。

**Bean创建**： 如果导入类上存在 `@EnableXcs` 注解，`MyImportAware` 继续并定义了一个 `String` 类型的 bean。这就是我们从上下文中检索并在控制台上打印的bean。

**异常处理**： 如果直接使用 `@Import` 导入 `MyImportAware` 而不使用 `@EnableXcs` 注解，会发生异常，因为 `MyImportAware` 期望导入它的类上有 `@EnableXcs` 注解。