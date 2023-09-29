## BeanNameAware

- [BeanNameAware](#beannameaware)
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

`BeanNameAware` 接口。当一个 Bean 实现了此接口，可以感知其在 Spring 容器中的名称。

### 二、接口源码

`BeanNameAware` 是 Spring 框架自 01.11.2003 开始引入的一个核心接口。这个接口是为那些想要了解其在 bean 工厂中的名称的 beans 设计的。

```java
/**
 * 由希望知道其在 bean 工厂中名称的 beans 实现的接口。
 * 注意通常不推荐一个对象依赖于其 bean 名称，因为这可能导致对外部配置的脆弱依赖，
 * 以及可能的不必要的对 Spring API 的依赖。
 *
 * 有关所有 bean 生命周期方法的列表，请参见
 * BeanFactory BeanFactory javadocs。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 01.11.2003
 * @see BeanClassLoaderAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanNameAware extends Aware {

	/**
	 * 设置在创建此 bean 的 bean 工厂中的 bean 的名称。
	 * 此方法在填充常规 bean 属性之后被调用，但在如 InitializingBean#afterPropertiesSet() 这样的
	 * 初始化回调或自定义初始化方法之前被调用。
	 * @param name 工厂中的 bean 的名称。注意，这个名称是工厂中使用的实际 bean 名称，
     * 这可能与最初指定的名称不同：尤其对于内部 bean 名称，实际的 bean 名称可能已通过添加 "#..." 后缀变得唯一。
     * 如果需要，可以使用 BeanFactoryUtils#originalBeanName(String) 方法来提取没有后缀的原始 bean 名称。
	 */
	void setBeanName(String name);

}
```

### 三、主要功能

**提供 `setBeanName` 方法**：当一个 Bean 实现了 `BeanNameAware` 接口，它需要提供 `setBeanName` 方法的实现。这个方法有一个参数，即该 Bean 在 Spring 容器中的名称。

**自动回调**：当 Spring 容器创建并配置一个实现了 `BeanNameAware` 接口的 Bean 时，容器会自动回调 `setBeanName` 方法，并传入该 Bean 在容器中的名称。这意味着开发者不需要显式地调用这个方法；Spring 容器会自动处理。

**获取 Bean 的名称**：有时，Bean 可能需要知道其在容器中的名称以执行特定的逻辑或功能，或者为了日志记录或其他目的。通过实现 `BeanNameAware`，Bean 可以轻松获得此信息。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class BeanNameAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

使用`@ComponentScan`注解，告诉 Spring 容器去 "`com.xcs.spring.service`" 扫描包及其子包

```java
@Configuration
@ComponentScan("com.xcs.spring.service")
public class MyConfiguration {

}
```

`MyBasePayService` 是一个抽象类，结合了 Spring 的三个特殊接口：`BeanNameAware`（让 Bean 知道其名字）、`InitializingBean`（Bean 属性设置后的初始化操作）和 `DisposableBean`（Bean 销毁前的操作）。

```java
public abstract class MyBasePayService implements BeanNameAware, InitializingBean, DisposableBean {

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Module " + beanName + " has been registered.");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Module " + beanName + " has been unregistered.");
    }
}
```

`MyAliPayService` 和 `MyWeChatPayService` 是两个支付服务类，都继承自 `MyBasePayService`，因此它们会自动获得与 Spring 容器生命周期相关的基本功能。这种设计方式为多个支付服务提供了一个共同的生命周期管理模式，同时允许每个服务添加其特定的支付逻辑。

```java
@Service
public class MyAliPayService extends MyBasePayService{
    
}

@Service
public class MyWeChatPayService extends MyBasePayService{
    
}
```

运行结果发现，当 Spring 容器启动并初始化 Beans 时，它正确地识别并实例化了 `MyAliPayService` 和 `MyWeChatPayService` 这两个服务。由于这两个服务都继承自 `MyBasePayService`，在 Bean 的属性被设置之后（即在 `afterPropertiesSet()` 方法中），它们分别打印出了 "Module myAliPayService has been registered." 和 "Module myWeChatPayService has been registered." 这两条信息，提供了一个共同的生命周期管理模式。

```java
Module myAliPayService has been registered.
Module myWeChatPayService has been registered.
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: BeanNameAware时序图
    participant InitializingBeanApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyBasePayService
    
    InitializingBeanApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
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
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeAwareMethods(beanName, bean)<br>调用Aware方法
    AbstractAutowireCapableBeanFactory->>MyBasePayService:setBeanName(beanName)<br>设置Bean名称
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>InitializingBeanApplication:初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class BeanNameAwareApplication {

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`方法中，`invokeAwareMethods(beanName, bean)`是一个非常重要的步骤。这个方法是为了处理实现了Spring的`Aware`接口族的Beans（例如`BeanNameAware`, `BeanFactoryAware`等）。如果提供的bean实现了任何这些接口，该方法会回调相应的`Aware`方法。

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [代码部分省略以简化]
    
    invokeAwareMethods(beanName, bean);
    
    // ... [代码部分省略以简化]

    return wrappedBean;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeAwareMethods`方法中，用于处理实现了 Spring `Aware` 接口族的 beans。当一个 bean 实现了如 `BeanNameAware`、`BeanClassLoaderAware` 或 `BeanFactoryAware` 等接口时，此方法确保正确的回调方法被调用，从而为 bean 提供关于其运行环境或其他相关信息。

```java
private void invokeAwareMethods(String beanName, Object bean) {
    if (bean instanceof Aware) {
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).setBeanName(beanName);
        }
        if (bean instanceof BeanClassLoaderAware) {
            // ... [代码部分省略以简化]
        }
        if (bean instanceof BeanFactoryAware) {
            // ... [代码部分省略以简化]
        }
    }
}
```

最后执行到我们自定义的逻辑中，我们将这个名称存储在 `beanName` 变量中，以便后续使用。

```java
public abstract class MyBasePayService implements BeanNameAware, InitializingBean, DisposableBean {

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Module " + beanName + " has been registered.");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Module " + beanName + " has been unregistered.");
    }
}
```

### 七、注意事项

**与其他生命周期方法的顺序**：`setBeanName` 方法的调用是在其他许多生命周期方法之前的，例如 `InitializingBean#afterPropertiesSet` 和任何定义的初始化方法。因此，我们不应该在 `setBeanName` 方法内部预期其他配置或初始化逻辑已经完成。

**仅在容器管理的 Beans 中有效**：只有当 bean 是由 Spring 容器管理时，`BeanNameAware` 才会生效。简单地创建一个类的实例（例如通过 `new` 关键字）并不会触发 `BeanNameAware` 功能。

**与其他 Aware 接口的组合使用**：当一个 bean 同时实现多个 `Aware` 接口时，需要注意它们的调用顺序。例如，`BeanNameAware`、`BeanFactoryAware` 和 `ApplicationContextAware` 的回调方法调用顺序是固定的。

**Bean 名称的唯一性**：Spring 容器内的 bean 名称是唯一的，但如果使用别名，同一个 bean 可能会有多个名称。当实现 `BeanNameAware` 时，我们获得的是 bean 的主要名称。

### 八、总结

#### 8.1、最佳实践总结

**启动及配置**：我们使用了 `AnnotationConfigApplicationContext` 作为 Spring 容器的入口，专门为基于 Java 的配置设计。该容器被初始化并加载了 `MyConfiguration` 类，它定义了应用的主要配置。

**组件扫描**：通过在 `MyConfiguration` 类中使用 `@ComponentScan` 注解，我们告诉 Spring 容器去扫描 "`com.xcs.spring.service`" 包及其子包，以找到和管理 Beans。

**生命周期管理**：**MyBasePayService** 类展示了如何利用 Spring 的特殊接口，例如 `BeanNameAware`、`InitializingBean` 和 `DisposableBean`，来插入到 Bean 的生命周期的特定阶段。当一个 Bean 实例被创建并管理 by Spring, 它会被赋予一个名称（通过 `BeanNameAware`）、在所有属性设置后初始化（通过 `InitializingBean`）以及在应用结束或 Bean 被销毁时执行特定操作（通过 `DisposableBean`）。

**具体的服务实现**：有两个具体的支付服务，`MyAliPayService` 和 `MyWeChatPayService`，它们都继承了 `MyBasePayService`。这意味着它们都自动继承了上述的生命周期管理功能。当 Spring 容器启动时，这两个服务的相关生命周期方法会被调用，如我们从打印的消息中所看到的。

**实际效果**：当应用运行时，每个服务类都会打印出其已经被注册和注销的消息，这是由于它们都继承了 `MyBasePayService` 中定义的生命周期方法。

#### 8.2、源码分析总结

**启动和上下文初始化**：使用`AnnotationConfigApplicationContext`初始化Spring容器，其中传递了配置类`MyConfiguration`。

**注册和刷新上下文**：在`AnnotationConfigApplicationContext`构造函数中，`register()`方法注册配置类，而`refresh()`方法开始加载和初始化beans。

**开始bean的实例化**：`refresh()`方法进一步调用了`finishBeanFactoryInitialization(beanFactory)`，该方法负责预先实例化所有非懒加载的单例bean。

**实例化单例bean**：`preInstantiateSingletons()`方法遍历所有bean名称，并通过调用`getBean(beanName)`来实例化和初始化bean。

**创建bean实例**：`doGetBean()`是实际进行bean创建的核心方法，它处理了bean的实例化、依赖注入和初始化等逻辑。

**处理Aware接口族**：在bean的初始化过程中，`invokeAwareMethods(beanName, bean)`被调用，负责处理实现了`Aware`接口族的beans。这是我们的`BeanNameAware`接口发挥作用的地方，当bean实现此接口时，其`setBeanName`方法会被调用。

**用户定义的逻辑**：在`MyBasePayService`类中，我们实现了`BeanNameAware`接口，并重写了`setBeanName`方法来保存bean的名称。此外，还使用了`InitializingBean`和`DisposableBean`接口来在bean的生命周期的特定时刻执行自定义的逻辑。