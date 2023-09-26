## InstantiationAwareBeanPostProcessor

- [InstantiationAwareBeanPostProcessor](#instantiationawarebeanpostprocessor)
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

`InstantiationAwareBeanPostProcessor` 提供了在 bean 实例化之前和之后的回调。这意味着我们有机会在实际的目标 bean 实例之前返回一个代理，或者影响 bean 的构造。

### 二、接口源码

`InstantiationAwareBeanPostProcessor` 是 Spring 框架自 1.2 版本开始引入的一个核心接口。其目的在于提供更细粒度的 bean 生命周期管理，特别是在 bean 的实例化阶段。尽管这个接口是公开的，但它主要针对 Spring 内部的需求进行设计。对于常规的业务开发，我们往往只需要使用 `BeanPostProcessor` 接口，它为 bean 的生命周期提供了足够的回调方法，满足大多数应用场景的需求。为了方便我们使用，Spring 还提供了一个名为 `InstantiationAwareBeanPostProcessorAdapter` 的适配器类。这个类已为我们默认实现了所有方法（基本上是空操作），使得我们可以只关注自己需要的回调方法，无需为其他方法提供冗余的实现。最后，如果我们深入探讨此接口的核心实现，我们会发现 `AbstractAutoProxyCreator` 和 `LazyInitTargetSourceCreator` 两个关键类。前者是 Spring AOP 的基石，它负责自动为匹配的 bean 创建代理，实现面向切面的编程；后者与 bean 的懒初始化策略有关，允许 bean 在首次请求时才被初始化和装配。

```java
/**
 * 这是 BeanPostProcessor 的子接口，它为 bean 的实例化添加了新的回调方法。
 * 主要是在 bean 实例化之前和之后，但在明确地设置属性或进行自动装配之前。
 *
 * 通常，这个接口用于为特定的目标 beans 抑制默认的实例化。
 * 例如，为了创建带有特殊 `TargetSources` 的代理（如池化的目标、延迟初始化的目标等），
 * 或为了实施其他的注入策略，例如字段注入。
 *
 * 注意：这是一个特殊目的的接口，主要供框架内部使用。
 * 建议尽量实现简单的 BeanPostProcessor 接口，
 * 或从 InstantiationAwareBeanPostProcessorAdapter 继承，
 * 以避免受到这个接口的扩展的影响。
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 1.2
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 在目标 bean 被实例化之前应用此 BeanPostProcessor。返回的 bean 对象可能是一个代理，
     * 可用来代替目标 bean，有效地抑制了目标 bean 的默认实例化。
     * 如果此方法返回一个非空对象，bean 的创建过程将被短路。
     * 
     * @param beanClass 要实例化的 bean 的类
     * @param beanName bean 的名称
     * @return 要替代目标 bean 的默认实例公开的 bean 对象，或 {@code null} 继续默认实例化
     * @throws org.springframework.beans.BeansException 如果发生错误
     */
    @Nullable
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    /**
     * 在 bean 通过构造函数或工厂方法被实例化后执行操作，但在 Spring 的属性设置（通过明确的属性或自动装配）发生之前。
     * 这是在 Spring 的自动装配开始之前，对给定的 bean 实例执行自定义字段注入的理想回调。
     * 
     * @param bean 已创建的 bean 实例，其属性尚未设置
     * @param beanName bean 的名称
     * @return 如果应该在 bean 上设置属性，则为 {@code true}；如果应跳过属性填充，则为 {@code false}。
     * @throws org.springframework.beans.BeansException 如果发生错误
     */
    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    /**
     * 在工厂将它们应用于给定的 bean 之前，对给定的属性值进行后处理，而不需要属性描述符。
     * 
     * @param pvs 工厂即将应用的属性值（永远不为 {@code null}）
     * @param bean 已创建但其属性尚未设置的 bean 实例
     * @param beanName bean 的名称
     * @return 要应用于给定 bean 的实际属性值（可以是传入的 PropertyValues 实例），或 {@code null} 
     * @throws org.springframework.beans.BeansException 如果发生错误
     */
    @Nullable
    default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {

        return null;
    }

    /**
     * 在工厂将它们应用于给定的 bean 之前，对给定的属性值进行后处理。允许检查所有的依赖关系是否都已满足，
     * 例如基于 bean 属性 setter 上的 "Required" 注解。
     * 
     * @param pvs 工厂即将应用的属性值（永远不为 {@code null}）
     * @param pds 目标 bean 的相关属性描述符
     * @param bean 已创建但其属性尚未设置的 bean 实例
     * @param beanName bean 的名称
     * @return 要应用于给定 bean 的实际属性值（可以是传入的 PropertyValues 实例），或 {@code null} 
     * @throws org.springframework.beans.BeansException 如果发生错误
     */
    @Deprecated
    @Nullable
    default PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

        return pvs;
    }
}
```

### 三、主要功能

+ 实例化前的处理：在 bean 实际被实例化之前调用，允许你返回一个代理对象来替代真正的目标 bean，这样你可以避免 bean 的默认实例化过程，这是 AOP 和代理创建中非常有用的一个步骤。
+ 实例化后的处理：在 bean 实例化后但在属性注入之前调用，这个回调为你提供了在 Spring 自动装配或属性设置之前对 bean 进行自定义处理的机会。
+ 属性处理：允许你在 Spring 进行属性注入之前对 bean 的属性值进行处理或替换，这是在进行自定义属性注入或验证 bean 属性的理想之处。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`DataBase`类型的bean，最后打印了该bean的几个属性。这样我们就可以确认bean的状态啦。

```java
public class InstantiationAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        DataBase userService = context.getBean(DataBase.class);
        System.out.println("username = " + userService.getUsername());
        System.out.println("password = " + userService.getPassword());
        System.out.println("postInstantiationFlag = " + userService.isPostInstantiationFlag());
    }
}
```

这里使用`@Bean`注解，定义了两个Bean，是为了确保`DataBase`， `MyInstantiationAwareBeanPostProcessor` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyInstantiationAwareBeanPostProcessor myInstantiationAwareBeanPostProcessor() {
        return new MyInstantiationAwareBeanPostProcessor();
    }

    @Bean
    public DataBase dataBase() {
        return new DataBaseImpl();
    }
}
```

`MyInstantiationAwareBeanPostProcessor` 的类，该类实现了 Spring 的 `InstantiationAwareBeanPostProcessor` 接口，在`postProcessBeforeInstantiation`方法中在`DataBase`类型的bean开始实例化前，打印一条通知消息，表明我们正在准备实例化该 bean。在`postProcessAfterInstantiation`方法中`DataBase` bean实例化后，设置标记属性并通知bean已经实例化。在`postProcessProperties`:方法中给`DataBase` bean注入属性前，将密码屏蔽并打印一条消息说明密码已被屏蔽。

```java
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass == DataBase.class) {
            System.out.println("正在准备实例化: " + beanName);
        }
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataBase) {
            ((DataBase) bean).setPostInstantiationFlag(true);
            System.out.println("Bean " + beanName + " 已实例化!");
            return true;
        }
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (bean instanceof DataBase) {
            MutablePropertyValues mpvs = (MutablePropertyValues) pvs;
            mpvs.addPropertyValue("password", "******");
            System.out.println(beanName + "的密码已被屏蔽:");
        }
        return pvs;
    }
}
```

一个简单的接口定义

```java
public interface DataBase {
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
    boolean isPostInstantiationFlag();
    void setPostInstantiationFlag(boolean flag);
}
```

使用 `@Value` 注解在 Spring 的上下文中为相应的属性提供了默认值。

```java
public class DataBaseImpl implements DataBase {

    @Value("root")
    private String username;

    @Value("123456")
    private String password;

    private boolean postInstantiationFlag;
	
    // get and set
}
```

运行结果发现，`dataBase`的准备实例化，到已实例化的过程日志打印了，另外最重要的是我们也吧`password`字段已经被屏蔽了。

```java
正在准备实例化: dataBase
Bean dataBase 已实例化!
dataBase的密码已被屏蔽:
username = root
password = ******
postInstantiationFlag = true
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: InstantiationAwareBeanPostProcessor时序图
    participant InstantiationAwareBeanPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyInstantiationAwareBeanPostProcessor
    
    InstantiationAwareBeanPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext<br>开始初始化
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh<br>刷新上下文
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization<br>实例化非懒加载的单列Bean
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons<br>预实例化Singleton
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean<br>根据beanName获取对象
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean<br>返回指定bean的实例
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton<br>获取单例对象
    DefaultSingletonBeanRegistry->>AbstractBeanFactory:getObject<br>ObjectFactory接口的工厂方法
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean<br>创建一个bean实例，填充bean实例，应用后处理器
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:resolveBeforeInstantiation
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInstantiation
    AbstractAutowireCapableBeanFactory->>MyInstantiationAwareBeanPostProcessor:postProcessBeforeInstantiation<br>实例化前处理
    MyInstantiationAwareBeanPostProcessor->>AbstractAutowireCapableBeanFactory:返回Bean对象
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory: 如果bean对象不为空直接返回，后续操作跳过
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean
    AbstractAutowireCapableBeanFactory->>MyInstantiationAwareBeanPostProcessor:postProcessAfterInstantiation<br>实例化后处理
    MyInstantiationAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory:返回true or false <br>表示属性注入和后续的生命周期处理
    AbstractAutowireCapableBeanFactory->>MyInstantiationAwareBeanPostProcessor:postProcessProperties<br>处理属性
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory: 返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext->>InstantiationAwareBeanPostProcessorApplication:初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后通过调用`context.getBean(DataBase.class)`，应用程序从Spring容器中获取了一个名为`DataBase`的bean实例，并打印了用户名，密码，标志位。

```java
public class InstantiationAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        DataBase userService = context.getBean(DataBase.class);
        System.out.println("username = " + userService.getUsername());
        System.out.println("password = " + userService.getPassword());
        System.out.println("postInstantiationFlag = " + userService.isPostInstantiationFlag());
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
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [代码部分省略以简化]
    // Instantiate all remaining (non-lazy-init) singletons.
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
                // 如果在创建bean过程中出现异常，从单例缓存中移除它
                // 这样做是为了防止循环引用的情况
                destroySingleton(beanName);
                throw ex;
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

            // 在创建单例之前执行某些操作，如记录创建状态
            beforeSingletonCreation(beanName);

            boolean newSingleton = false;
            boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
            if (recordSuppressedExceptions) {
                this.suppressedExceptions = new LinkedHashSet<>();
            }

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

            // 如果成功创建了新的单例，将其加入缓存
            if (newSingleton) {
                addSingleton(beanName, singletonObject);
            }
        }

        // 返回单例对象
        return singletonObject;
    }
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()`方法中，首先尝试调用`resolveBeforeInstantiation`，这个方法给`InstantiationAwareBeanPostProcessor`一个机会，允许它们返回一个代理对象，而不是目标bean的实例。如果这一步返回了一个非空的对象（也就是说，一个`InstantiationAwareBeanPostProcessor`创建了一个代理对象），那么这个代理对象将作为该bean的实例返回，跳过正常的bean创建过程。如果上面的步骤没有返回任何对象，那么代码将执行`doCreateBean`方法，这个方法负责实际的bean实例化、属性注入和初始化。

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [代码部分省略以简化]
    
    try {
        // 1. 给BeanPostProcessors一个机会返回一个代理对象，而不是目标bean实例。
        // 如果这步返回了一个非null的bean，那么这个bean将被返回，跳过正常的bean实例化过程。
        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    try {
        // 正常的bean实例化、属性注入和初始化。
        // 2. 这里是真正进行bean创建的部分。
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

我们来到`createBean(beanName,mbd,args)`方法中的第一步，在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation` 方法中，首先尝试在bean实际实例化之前提前完成bean的实例化。这通常是为了返回一个代理对象。`applyBeanPostProcessorsBeforeInstantiation` 方法，尝试使用 `InstantiationAwareBeanPostProcessor` 的 `postProcessBeforeInstantiation` 方法来预先实例化bean。如果上一步成功创建了bean（例如，返回了一个代理对象），那么这个bean还会经过所有注册的 `BeanPostProcessor` 的 `postProcessAfterInitialization` 方法，这是对bean进行初始化后的最后处理。

```java
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
    Object bean = null;
    // 检查是否已尝试在实例化之前解析此bean
    if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
        // 如果bean不是合成的，并且存在InstantiationAwareBeanPostProcessors
        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            // 确定目标bean的类型
            Class<?> targetType = determineTargetType(beanName, mbd);
            // 如果目标类型不为空
            if (targetType != null) {
                // 尝试在实际实例化之前，通过BeanPostProcessors提前创建bean实例
                bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                // 如果bean实例创建成功
                if (bean != null) {
                    // 对bean实例应用postProcessAfterInitialization方法
                    bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            }
        }
        // 标记bean在实例化之前是否已被解析
        mbd.beforeInstantiationResolved = (bean != null);
    }
    // 返回创建的bean实例或null
    return bean;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation` 方法中，回调每一个`InstantiationAwareBeanPostProcessor`接口的`postProcessBeforeInstantiation`方法。

```java
protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
    for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
        Object result = bp.postProcessBeforeInstantiation(beanClass, beanName);
        if (result != null) {
            return result;
        }
    }
    return null;
}
```

简单的实现`InstantiationAwareBeanPostProcessor`接口的类`MyInstantiationAwareBeanPostProcessor`，然后重写了`postProcessBeforeInstantiation`方法，此方法在bean实例化之前被调用。在bean实例化之前，我们可以选择返回一个不同的bean实例来替换原来要实例化的bean。如果我们从此方法返回非null的对象，Spring将使用我们返回的这个对象作为bean，并不会进入标准的实例化过程。如果返回null，则Spring将继续其正常的bean实例化过程，在`postProcessBeforeInstantiation`中我们仅是打印一个消息表示正在准备实例化该bean，并返回null。返回null意味着不中断正常的实例化过程。以上就是关于`InstantiationAwareBeanPostProcessor`类中的`postProcessBeforeInstantiation`方法的源码分析全过程，剩下两个方法请看后续分析。

```java
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass == DataBase.class) {
            System.out.println("正在准备实例化: " + beanName);
        }
        return null;
    }
}
```

我们来到`createBean(beanName,mbd,args)`方法中的第二步，在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，主要负责两大步骤，第一步是属性注入，第二步是bean初始化，确保bean是完全配置和准备好的。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // 声明一个对象，后续可能用于存放初始化后的bean或它的代理对象
    Object exposedObject = bean;

    try {
        // 属性注入：这一步将配置中的属性值注入到bean实例中。例如，XML中定义的属性或使用@Autowired和@Value注解的属性都会在这里被注入
        populateBean(beanName, mbd, instanceWrapper);

        // bean初始化：这一步会执行bean的初始化方法，同时也会调用BeanPostProcessor的postProcessBeforeInitialization和postProcessAfterInitialization方法，它们可以对bean进行进一步的处理
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    } 
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    // 返回创建和初始化后的bean
    return exposedObject;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean`方法中，首先会调用所有的`InstantiationAwareBeanPostProcessors`的`postProcessAfterInstantiation`方法，以给它们一个机会在属性设置之前修改bean的状态。如果`postProcessAfterInstantiation`方法返回的是true，它首先会尝试使用`postProcessProperties`方法来处理属性值。如果这个方法返回`null`，则会继续使用老版本的`postProcessPropertyValues`方法。

```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
    // ... [代码部分省略以简化]

    // 如果当前的Bean不是合成的，并且存在InstantiationAwareBeanPostProcessors，那么给这些后处理器一个机会
    // 在Spring填充属性之前进行处理，例如支持不同风格的字段注入。
    if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            // 如果返回false，代表此bean的属性不应继续被填充。
            if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                return;
            }
        }
    }

    // ... [代码部分省略以简化]
    // 对每一个InstantiationAwareBeanPostProcessor进行处理，这些处理器可能会修改Bean的属性值。
    for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
        // 尝试使用新版本的方法 postProcessProperties
        PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
        if (pvsToUse == null) {
            // 如果postProcessProperties返回null，尝试使用旧版本的方法 postProcessPropertyValues
            if (filteredPds == null) {
                filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
            }
            pvsToUse = bp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
            // 如果旧版本的方法也返回null，那么此bean的属性不应继续被填充。
            if (pvsToUse == null) {
                return;
            }
        }
        pvs = pvsToUse;
    }
    // ... [代码部分省略以简化]
}
```

最后调用到我们自定义的实现逻辑中来，在`postProcessAfterInstantiation`方法中设置了bean的`postInstantiationFlag`属性为`true`。这可以视为我们留下的标记，说明该bean已经被实例化了，然后返回`true`，表示我们允许Spring继续bean的初始化。然后再`postProcessProperties`方法中，修改bean的属性值将`password`属性的值修改为`"******"`

```java
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataBase) {
            ((DataBase) bean).setPostInstantiationFlag(true);
            System.out.println("Bean " + beanName + " 已实例化!");
            return true;
        }
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (bean instanceof DataBase) {
            MutablePropertyValues mpvs = (MutablePropertyValues) pvs;
            mpvs.addPropertyValue("password", "******");
            System.out.println(beanName + "的密码已被屏蔽:");
        }
        return pvs;
    }
}
```

### 七、注意事项

**影响性能**：这种后置处理器会在每个bean的创建过程中调用多次。尽量确保后处理器的逻辑简单且执行速度快，以减少对应用性能的影响。如果你在这三个方法`postProcessBeforeInstantiation`，`postProcessAfterInstantiation`，`postProcessProperties`中执行了复杂的逻辑，如数据库查询、远程调用或其他IO操作，由于每个bean的创建都会触发后处理器，这意味着上述方法将被频繁调用，这会严重影响应用启动时间和bean的创建性能。

**返回非空的bean**：如果在`postProcessBeforeInstantiation`方法中返回了非空的bean，那么正常的bean实例化和属性设置流程将被短路。这意味着`postProcessAfterInstantiation`和`postProcessProperties`等方法将不会被调用。另外也会导致`BeanPostProcessor`类中的`postProcessBeforeInitialization`也不会被调用

**避免修改非目标bean**：如果我们的`InstantiationAwareBeanPostProcessor`只对特定类型或名称的bean进行操作，确保在执行任何操作之前进行适当的检查。

### 八、总结

#### 8.1、最佳实践总结

**启动与上下文初始化**:

- 使用`AnnotationConfigApplicationContext`来启动应用，并注册了配置类`MyConfiguration`。
- 从Spring上下文中获取了一个`DataBase`类型的bean并打印了它的属性，这是为了验证bean状态的更改是否成功。

**配置类与Bean定义**:

- 通过`MyConfiguration`配置类，两个Bean（`DataBase`和`MyInstantiationAwareBeanPostProcessor`）被定义。其中`MyInstantiationAwareBeanPostProcessor`是一个后处理器，它会在Spring容器中的其他Bean实例化时触发。

**拦截实例化过程**:

- `MyInstantiationAwareBeanPostProcessor`类实现了Spring的`InstantiationAwareBeanPostProcessor`接口，这允许它介入bean的实例化、初始化和属性设置过程。
- 在`postProcessBeforeInstantiation`方法中，当`DataBase` bean开始实例化之前，一个通知消息被打印。
- 在`postProcessAfterInstantiation`方法中，bean已经实例化，此时会设置一个标记属性并打印一条通知消息。
- 在`postProcessProperties`方法中，修改了`DataBase` bean的密码属性，并打印了通知消息。

**DataBase接口与实现**:

- 定义了一个`DataBase`接口，该接口定义了数据库连接的基本属性及其getters和setters。
- 在`DataBaseImpl`类中，实现了这个接口，并使用`@Value`注解为属性设置了默认值。

**运行结果**:

- 从输出中可以看到，`dataBase` bean从准备实例化到实例化的过程都被成功拦截，并且密码已经被屏蔽。

#### 8.2、源码分析总结

**启动及Bean获取**

- 应用程序启动时，`AnnotationConfigApplicationContext`类被用于初始化Spring上下文，并注册了配置类`MyConfiguration`。
- 然后，应用程序从Spring上下文中获取名为`DataBase`的bean实例并打印它的一些属性。

**注册Bean及后处理器**

- 通过`MyConfiguration`配置类，注册了两个Bean，其中一个是`MyInstantiationAwareBeanPostProcessor`，这个后处理器用于在Bean实例化过程中介入。

**实例化前的拦截**

- 在Bean实例化之前，Spring首先调用`postProcessBeforeInstantiation`方法。这里，我们只是简单地打印了一条消息并返回了null，表示让Spring继续执行标准的Bean实例化。

**Bean属性注入**

- 在Bean实例化之后但属性注入之前，Spring调用`postProcessProperties`方法。
- 在这个示例中，我们修改了`password`属性的值为`"******"`并打印了一条消息。

**Bean实例化后的处理**

- 紧接着，`postProcessAfterInstantiation`方法被调用。这里，我们简单地设置了`postInstantiationFlag`属性并打印了一条消息。

**Bean的完成**

- 在所有这些拦截器运行后，Spring会继续进行属性注入、Bean初始化等后续工作。
- 之后，Bean将完全初始化并准备好供应用程序使用。