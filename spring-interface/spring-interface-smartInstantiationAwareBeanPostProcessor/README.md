## SmartInstantiationAwareBeanPostProcessor

- [SmartInstantiationAwareBeanPostProcessor](#smartinstantiationawarebeanpostprocessor)
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

`InstantiationAwareBeanPostProcessor`。接口，能够对 Spring 容器创建的 beans 进行更精细的控制和更多的干预，尤其是在涉及代理和其他高级场景时。

### 二、接口源码

`SmartInstantiationAwareBeanPostProcessor` 是 Spring 框架自 2.0.3 版本开始引入的一个核心接口，主要用于框架内部。正常情况下我们实现`BeanPostProcessor`接口或者`InstantiationAwareBeanPostProcessorAdapter`接口就能满足自定义需求。

```java
/**
 * InstantiationAwareBeanPostProcessor 接口的扩展，
 * 增加了预测处理的bean的最终类型的回调方法。
 *
 * 注意: 这是一个特定目的的接口，主要用于
 * 框架内部。一般来说，应用程序提供的后处理器应该
 * 直接实现简单的 BeanPostProcessor
 * 接口或继承 InstantiationAwareBeanPostProcessorAdapter 类。
 * 即使在点版本中，也可能向此接口添加新方法。
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see InstantiationAwareBeanPostProcessorAdapter
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

	/**
	 * 预测从此处理器的 #postProcessBeforeInstantiation 回调返回的bean的类型。
	 * 默认实现返回 null。
	 * @param beanClass bean的原始类
	 * @param beanName bean的名称
	 * @return bean的类型，如果不可预测则为 null
	 * @throws org.springframework.beans.BeansException 出错时抛出
	 */
	@Nullable
	default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	/**
	 * 确定给定bean的候选构造函数。
	 * 默认实现返回 null。
	 * @param beanClass bean的原始类（永远不是 null）
	 * @param beanName bean的名称
	 * @return 候选构造函数，如果没有指定则为 null
	 * @throws org.springframework.beans.BeansException 出错时抛出
	 */
	@Nullable
	default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
			throws BeansException {

		return null;
	}

	/**
	 * 为了解决循环引用，提前获取指定bean的引用。
	 * 此回调为后处理器提供了一个机会，可以在目标bean实例完全初始化之前暴露一个包装器。
	 * 暴露的对象应当等同于 #postProcessBeforeInitialization / 
     * #postProcessAfterInitialization 否则会暴露。需要注意的是，
     * 由此方法返回的对象将被用作bean引用，除非后处理器从上述后处理回调中返回一个不同的包装器。
	 * 默认实现返回给定的 bean 原样。
	 * @param bean 原始bean实例
	 * @param beanName bean的名称
	 * @return 作为bean引用暴露的对象（通常使用传入的bean实例作为默认值）
	 * @throws org.springframework.beans.BeansException 出错时抛出
	 */
	default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
```

### 三、主要功能

**预测 Bean 类型 (predictBeanType)**：这个方法允许在实例化 bean 之前预测 bean 的最终类型。这在涉及代理或其他类型转换的场景中特别有用，例如，一个 bean 可能会被一个 AOP 代理包裹，此方法可以返回预期的代理类型而不是实际的目标类型，这有助于 Spring 在创建和连接 bean 时做出更加明智的决策，特别是在涉及类型匹配（如自动装配）时。

**确定候选构造函数 (determineCandidateConstructors)**：在 bean 实例化之前，这个方法允许确定用于给定 bean 的构造函数，这为我们提供了一种方式来定制或干预 Spring 默认的构造函数选择逻辑，例如，当存在多个构造函数并且我们想基于特定逻辑选择其中一个时。

**获取早期 Bean 引用 (getEarlyBeanReference)**：这个方法提供了一个机会，允许在 bean 完全初始化之前暴露一个包装器或代理，它在处理循环依赖时特别有用，当一个 bean 还未完全初始化但另一个 bean 需要引用它时，这个方法就会被调用，这样，我们可以暴露一个早期的 bean 引用，可能是一个代理，这个代理在完成所有初始化步骤后仍然有效。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class SmartInstantiationAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保 `MySmartInstantiationAwareBeanPostProcessor` 被 Spring 容器执行

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

    @Bean
    public static MySmartInstantiationAwareBeanPostProcessor mySmartInstantiationAwareBeanPostProcessor(){
        return new MySmartInstantiationAwareBeanPostProcessor();
    }
}
```

自定义的 `SmartInstantiationAwareBeanPostProcessor` 实现，然后我们重写了 `determineCandidateConstructors` 方法。如果类中有一个或多个带有 `@MyAutowired` 注解的构造函数，这些构造函数将被作为候选返回，如果没有找到任何带有 `@MyAutowired` 注解的构造函数，那么后处理器会尝试查找默认（无参数）的构造函数，如果没有找到带有 `@MyAutowired` 注解的构造函数，并且没有默认构造函数，那么所有可用的构造函数将被作为候选返回，从而使 Spring 能够选择最具体的构造函数。

```java
public class MySmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        // 首先，查找@MyAutowired带注释的构造函数
        List<Constructor<?>> myAutowiredConstructors = Arrays.stream(beanClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(MyAutowired.class))
                .collect(Collectors.toList());

        if (!myAutowiredConstructors.isEmpty()) {
            return myAutowiredConstructors.toArray(new Constructor<?>[0]);
        }

        // 其次，检查默认构造函数
        try {
            Constructor<?> defaultConstructor = beanClass.getDeclaredConstructor();
            return new Constructor<?>[]{defaultConstructor};
        } catch (NoSuchMethodException e) {
            // 找不到默认构造函数，请继续选择合适的构造函数
        }

        // 返回所有构造函数，让Spring将选择最具体的构造函数
        return beanClass.getConstructors();
    }
}
```

自定义注解

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface MyAutowired {
    
}
```

两个普通Bean对象

```java
@Component
public class MyServiceA {

    public void execute() {
        System.out.println("MyServiceA executed");
    }
}

@Component
public class MyServiceB {

    public void execute() {
        System.out.println("MyServiceB executed");
    }
}
```

我们定义了四个构造函数（默认构造函数，只接受 `MyServiceA` 的构造函数，只接受 `MyServiceB` 的构造函数，同时接受 `MyServiceA` 和 `MyServiceB` 的构造函数）。根据`MySmartInstantiationAwareBeanPostProcessor`定义的分析下面选择构造函数过程，当 Spring 容器尝试实例化 `MyService` 类的一个实例时，由于存在一个被 `@MyAutowired` 标记的构造函数，所以它将首先尝试使用它。这意味着如果在 Spring 容器中已经有了 `MyServiceA` 和 `MyServiceB` 的 bean，那么这两者都会被注入，并且会输出 `"Constructor with ServiceA and ServiceB used"`。如果没有提供 `@MyAutowired` 或者没有适当的 bean 来满足带有 `@MyAutowired` 注解的构造函数的依赖关系，则会尝试使用默认构造函数。如果没有默认构造函数，Spring 将尝试其他构造函数并查找可以匹配的 bean。在这种情况下，如果 `MyServiceA` 和 `MyServiceB` 都可用，Spring 将选择接受最多参数的构造函数，因为这被视为最具体的构造函数。

```java
@Component
public class MyService {

    private final MyServiceA myServiceA;
    private final MyServiceB myServiceB;

    public MyService() {
        System.out.println("Default constructor used");
        this.myServiceA = null;
        this.myServiceB = null;
    }

    public MyService(MyServiceA myServiceA) {
        System.out.println("Constructor with ServiceA used");
        this.myServiceA = myServiceA;
        this.myServiceB = null;
    }

    public MyService(MyServiceB serviceB) {
        System.out.println("Constructor with ServiceB used");
        this.myServiceA = null;
        this.myServiceB = serviceB;
    }

    @MyAutowired
    public MyService(MyServiceA serviceA, MyServiceB serviceB) {
        System.out.println("Constructor with ServiceA and ServiceB used");
        this.myServiceA = serviceA;
        this.myServiceB = serviceB;
    }
}
```

运行结果发现，Spring 容器成功地使用带有 `@MyAutowired` 注解的构造函数实例化了 `MyService` 类，并正确地注入了它的两个依赖：`MyServiceA` 和 `MyServiceB`。

```java
Constructor with ServiceA and ServiceB used
```

<span style="color:red">注意：由于predictBeanType，getEarlyBeanReference方法是Spring框架内部使用无法演示出效果，因此不演示这两个方法。</span>

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: SmartInstantiationAwareBeanPostProcessor时序图
    participant SmartInstantiationAwareBeanPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MySmartInstantiationAwareBeanPostProcessor
    
    SmartInstantiationAwareBeanPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新上下文
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>初始化Bean工厂
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>实例化单例
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>获取Bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>执行获取Bean
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>获取单例Bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>获取Bean实例
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>创建Bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>执行Bean创建
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:createBeanInstance(beanName,mbd,args)<br>创建bean实例
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:determineConstructorsFromBeanPostProcessors(beanClass, beanName)<br>确定构造方法
	AbstractAutowireCapableBeanFactory->>MySmartInstantiationAwareBeanPostProcessor:determineCandidateConstructors(beanClass, beanName)<br>回调候选构造方法
    MySmartInstantiationAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory:返回构造方法
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>SmartInstantiationAwareBeanPostProcessorApplication:初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

<span style="color:red">PS：由于`predictBeanType`, `determineCandidateConstructors`, 和 `getEarlyBeanReference` 这三个方法虽然都属于 `SmartInstantiationAwareBeanPostProcessor` 接口，但它们处理不同的关注点，具有不同的目的。在进行源码分析时，此处只演示determineCandidateConstructors方法。</span>

```java
public class SmartInstantiationAwareBeanPostProcessorApplication {

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，如果是单例，尝试从工厂实例缓存中获取。如果缓存中没有实例，创建一个新的实例。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {
    // 实例化 bean。
    BeanWrapper instanceWrapper = null;
    if (mbd.isSingleton()) {
        // 如果是单例，尝试从工厂实例缓存中获取。
        instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
    }
    if (instanceWrapper == null) {
        // 如果缓存中没有实例，创建一个新的实例。
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    
    // ... [省略部分代码以简化]

    // 返回创建和初始化后的 bean。
    return exposedObject;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance`方法中，首先尝试从 `SmartInstantiationAwareBeanPostProcessor` 中确定用于 bean 的构造函数，接下来，它检查是否已经确定了某些构造函数、是否 bean 定义指明了使用构造函数自动装配、是否为 bean 提供了构造函数参数值或是否有明确的构造函数参数。如果满足这些条件之一，它将调用 `autowireConstructor` 方法，这个方法会使用确定的构造函数（或者选择一个）来实例化 bean。接着，如果之前没有选择构造函数，它会检查是否存在首选的默认构造函数。这些构造函数可以是由用户明确指定的或是由其他部分的框架预先确定的。如果有这样的构造函数，框架又会尝试使用 `autowireConstructor` 方法，最后，如果所有先前的步骤都没有返回构造函数，spring会默认为 bean 使用无参数构造函数。

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    // ... [省略部分代码以简化]

    // 是否有用于自动装配的候选构造函数？
    Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
    // 如果从后处理器中确定了构造函数、或者 bean 定义信息指明了使用构造函数自动装配、或者存在构造函数参数值、或者提供了特定的构造函数参数
    if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
        mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
        // 使用确定的构造函数进行自动装配
        return autowireConstructor(beanName, mbd, ctors, args);
    }

    // 是否有优先使用的默认构造函数？
    ctors = mbd.getPreferredConstructors();
    if (ctors != null) {
        // 使用优选的构造函数进行自动装配
        return autowireConstructor(beanName, mbd, ctors, null);
    }

    // 没有特殊处理：直接使用无参数构造函数进行实例化
    return instantiateBean(beanName, mbd);
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#determineConstructorsFromBeanPostProcessors`方法中，回调每一个 `SmartInstantiationAwareBeanPostProcessor`，调用它的 `determineCandidateConstructors` 方法以确定 bean 的构造函数。如果找到了候选的构造函数，就返回这些构造函数。如果多个 `SmartInstantiationAwareBeanPostProcessor` 都返回了构造函数，则只会使用第一个返回的构造函数。

```java
protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName)
			throws BeansException {

    if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {
        for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
            Constructor<?>[] ctors = bp.determineCandidateConstructors(beanClass, beanName);
            if (ctors != null) {
                return ctors;
            }
        }
    }
    return null;
}
```

最后执行到我们自定义的逻辑中，在我们自定义的逻辑中，如果类中有一个或多个带有 `@MyAutowired` 注解的构造函数，这些构造函数将被作为候选返回，如果没有找到任何带有 `@MyAutowired` 注解的构造函数，那么后处理器会尝试查找默认（无参数）的构造函数，如果没有找到带有 `@MyAutowired` 注解的构造函数，并且没有默认构造函数，那么所有可用的构造函数将被作为候选返回，从而使 Spring 能够选择最具体的构造函数。

```java
public class MySmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        // 首先，查找@MyAutowired带注释的构造函数
        List<Constructor<?>> myAutowiredConstructors = Arrays.stream(beanClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(MyAutowired.class))
                .collect(Collectors.toList());

        if (!myAutowiredConstructors.isEmpty()) {
            return myAutowiredConstructors.toArray(new Constructor<?>[0]);
        }

        // 其次，检查默认构造函数
        try {
            Constructor<?> defaultConstructor = beanClass.getDeclaredConstructor();
            return new Constructor<?>[]{defaultConstructor};
        } catch (NoSuchMethodException e) {
            // 找不到默认构造函数，请继续选择合适的构造函数
        }

        // 返回所有构造函数，让Spring将选择最具体的构造函数
        return beanClass.getConstructors();
    }
}
```

### 七、注意事项

**性能影响**：每个  `SmartInstantiationAwareBeanPostProcessor`，都会对每个 bean 的创建过程产生额外的开销。因此，应避免创建不必要的 `SmartInstantiationAwareBeanPostProcessor`，并确保其实现尽可能高效。

**与其他后处理器的交互**：如果有多个 `SmartInstantiationAwareBeanPostProcessor` ，它们会按照注册的顺序被调用。应确保这些后处理器的执行顺序是正确的，避免意外的覆盖或冲突，因为只会使用第一个返回的构造函数。

**返回非空值的考虑**：`determineCandidateConstructors`：当这个方法返回非空值时，Spring 容器将不会再尝试使用其他方式自动选择构造函数，`predictBeanType`：返回的类型应该尽可能准确地反映后处理器预期的最终 bean 类型，以确保类型匹配和自动装配的正确性。

**与 `InstantiationAwareBeanPostProcessor` 的区别**：虽然 `SmartInstantiationAwareBeanPostProcessor` 扩展了 `InstantiationAwareBeanPostProcessor`，但它添加了更多的回调和复杂性。除非我们确实需要这些额外的功能，否则最好仅使用 `InstantiationAwareBeanPostProcessor`。

### 八、总结

#### 8.1、最佳实践总结

**初始化 Spring 容器**：通过 `AnnotationConfigApplicationContext` 初始化 Spring 容器，并使用 `MyConfiguration` 作为配置类。

**注册后处理器**：在 `MyConfiguration` 中，我们注册了自定义的 `SmartInstantiationAwareBeanPostProcessor` 实现 `MySmartInstantiationAwareBeanPostProcessor`。这确保了它会在 Spring 容器中被考虑并执行。

**自定义后处理器的工作**：我们的 `MySmartInstantiationAwareBeanPostProcessor` 重写了 `determineCandidateConstructors` 方法，该方法的目标是返回一组构造函数，供 Spring 选择用于 bean 的实例化。

**查找 `@MyAutowired`**：首先，后处理器会查找带有 `@MyAutowired` 注解的构造函数。

**使用默认构造函数**：如果没有带有 `@MyAutowired` 的构造函数，后处理器会查找默认构造函数。

**返回所有构造函数**：如果没有找到上述两种情况的构造函数，所有的构造函数将被作为候选返回。

**Bean 的实例化**：当 Spring 尝试实例化 `MyService` bean 时，它会使用 `MySmartInstantiationAwareBeanPostProcessor` 中指定的构造函数。在这个示例中，由于我们有一个带有 `@MyAutowired` 注解的构造函数，且两个依赖 `MyServiceA` 和 `MyServiceB` 都可用，这个构造函数被选择并使用，从而输出了 `"Constructor with ServiceA and ServiceB used"`。

**关于其他方法**：虽然 `SmartInstantiationAwareBeanPostProcessor` 提供了其他方法，如 `predictBeanType` 和 `getEarlyBeanReference`，但这些主要是为 Spring 内部使用。在大多数常规用例中，你可能不需要重写或使用它们。

#### 8.2、源码分析总结

**启动和初始化：**

- 使用`AnnotationConfigApplicationContext`初始化Spring上下文。
- 构造参数中给定一个配置类，该配置类中定义了自定义的`SmartInstantiationAwareBeanPostProcessor`。

**Bean预实例化过程：**

- 在上下文刷新过程中，`finishBeanFactoryInitialization`方法会预实例化所有非懒加载的单例Bean。
- `preInstantiateSingletons`方法循环遍历所有bean的名称并通过`getBean`方法实例化bean。
- 如果Bean已经存在并且是单例，则会从单例缓存中返回。否则，会创建一个新的bean实例。

**Bean创建过程：**

- 创建Bean的核心逻辑在`doCreateBean`方法中。如果bean是单例并且在缓存中不存在，则会创建一个新的bean实例。
- 在创建bean实例时，首先从`SmartInstantiationAwareBeanPostProcessor`中确定用于bean的构造函数。
- 这个过程首先尝试使用带有特定注解（如我们的示例中的`@MyAutowired`）的构造函数。
- 如果没有这样的构造函数，则会选择默认构造函数。
- 如果没有带有注解的构造函数且没有默认构造函数，则会返回所有可用的构造函数，从而使Spring选择最具体的构造函数。

**自定义的逻辑：**

- 自定义的`SmartInstantiationAwareBeanPostProcessor`实现首先检查是否有带有`@MyAutowired`注解的构造函数。
- 如果有，则这些构造函数会作为候选返回。
- 如果没有，则后处理器会检查是否存在默认的无参数构造函数。
- 如果既没有带有`@MyAutowired`注解的构造函数，也没有默认构造函数，则所有构造函数将被作为候选返回。