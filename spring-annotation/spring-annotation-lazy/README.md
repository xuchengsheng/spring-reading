## @Lazy

- [@Lazy](#lazy)
  - [一、注解描述](#一注解描述)
  - [二、注解源码](#二注解源码)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
    - [5.1、Bean注册时序图](#51bean注册时序图)
    - [5.2、Bean延迟创建时序图](#52bean延迟创建时序图)
    - [5.3、Bean延迟注入时序图](#53bean延迟注入时序图)
  - [六、源码分析](#六源码分析)
    - [6.1、延迟初始化](#61延迟初始化)
    - [6.2、延迟注入](#62延迟注入)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [8.1、最佳实践总结](#81最佳实践总结)
    - [8.2、源码分析总结](#82源码分析总结)


### 一、注解描述

`@Lazy`注解，它的主要用途是延迟依赖注入的初始化。通常情况下，当 ApplicationContext 被启动和刷新时，所有的单例 bean 会被立即初始化。但有时，可能希望某些 bean 在首次使用时才被初始化。

### 二、注解源码

`@Lazy`注解是 Spring 框架自 3.0 版本开始引入的一个核心注解，用于控制 bean 的懒加载行为。默认情况下，当 `@Lazy` 被应用，bean 不会在 Spring 容器启动时立即初始化，而是在首次被引用或请求时。这适用于通过 `@Component` 或 `@Bean` 定义的 bean。此外，`@Lazy` 还可以用于注入点，如 `@Autowired`，创建一个懒解析代理，从而实现延迟注入。当用在 `@Configuration` 类上时，它影响该配置中所有的 `@Bean` 定义。

```java
/**
 * 指示一个bean是否应懒加载初始化。
 *
 * 可直接或间接地用于带 org.springframework.stereotype.Component @Component 注解的类，
 * 或者用于带有 Bean @Bean 注解的方法。
 *
 * 如果此注解不在 @Component 或 @Bean 定义上，将会立即初始化。
 * 如果存在并且设置为 true，除非被另一个bean引用或从包围的 org.springframework.beans.factory.BeanFactory BeanFactory 中显式检索，
 * 否则 @Bean 或 @Component 不会初始化。如果存在并设置为 false，那么执行积极初始化单例的bean工厂将在启动时实例化bean。
 *
 * 如果Lazy存在于 Configuration @Configuration 类上，表示该 @Configuration 中的所有 @Bean 方法都应懒加载。
 * 如果在一个带有 @Lazy 注解的 @Configuration 类的 @Bean 方法上 @Lazy 设置为false，则表示覆盖了“默认懒惰”行为，该bean应立即初始化。
 *
 * 除了其在组件初始化中的作用外，此注解也可用于带有 org.springframework.beans.factory.annotation.Autowired 或 javax.inject.Inject 的注入点：
 * 在这种情况下，它会导致为所有受影响的依赖关系创建一个懒解析代理，作为使用 org.springframework.beans.factory.ObjectFactory 或 javax.inject.Provider 的替代方法。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Primary
 * @see Bean
 * @see Configuration
 * @see org.springframework.stereotype.Component
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {

	/**
	 * 是否应进行懒加载初始化。
	 */
	boolean value() default true;

}
```

### 三、主要功能

1. **延迟初始化**
   + 当 `@Lazy` 注解应用于一个 `@Bean` 或 `@Component` 上时，该 bean 不会在 Spring 容器启动时立即初始化。而是直到首次被引用或请求时才进行初始化。
2. **延迟注入**
   + 当 `@Lazy` 注解应用于 `@Autowired` 或其他注入点上时，它导致为所注入的依赖关系创建一个懒解析代理，实现首次访问时的延迟注入。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，然后从中获取一个 `MyService` bean 并调用其 `show` 方法。

```java
public class LazyApplication {

    public static void main(String[] args) {
        System.out.println("启动 Spring ApplicationContext...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("启动完成 Spring ApplicationContext...");

        System.out.println("获取MyService...");
        MyService myService = context.getBean(MyService.class);

        System.out.println("调用show方法...");
        myService.show();
    }
}
```

这里使用`@Bean`注解，`MyBean` 被标注了 `@Lazy`，这意味着它只会在首次被请求时才会初始化。`MyService` 没有 `@Lazy` 注解，所以它会被立即初始化。

```java
@Configuration
public class MyConfiguration {

    @Bean
    @Lazy
    public MyBean myBean(){
        System.out.println("MyBean 初始化了!");
        return new MyBean();
    }

    @Bean
    public MyService myService(){
        return new MyService();
    }
}
```

 `MyBean` 类定义很简单。它有一个构造函数，在构造函数中打印了 "MyBean 的构造函数被调用了!"，并有一个 `show` 方法，该方法打印 "hello world!"。

```java
public class MyBean {

    public MyBean() {
        System.out.println("MyBean 的构造函数被调用了!");
    }

    public void show() {
        System.out.println("hello world!");
    }
}
```

`MyService` 类有一个对 `MyBean` 的引用，而该引用通过 `@Autowired` 进行依赖注入。由于我们还在这个注入点上添加了 `@Lazy` 注解，这意味着 `myBean` 的实际注入将被延迟，直到我们首次尝试访问它时。

```java
public class MyService  {

    @Autowired
    @Lazy
    private MyBean myBean;

    public void show() {
        System.out.println("MyBean Class = " + myBean.getClass());
        myBean.show();
    }
}
```

运行结果发现

1. **延迟初始化**:
   - 尽管 `MyService` 在应用上下文启动后可用，但由于 `MyBean` 上的 `@Lazy` 注解，`MyBean` 在启动时并未被初始化。
   - 只有在首次访问或使用 `MyBean` 时，它才真正被初始化。这在 "MyBean 初始化了!" 和 "MyBean 的构造函数被调用了!" 的输出中得到了体现。
2. **延迟注入**:
   - 在 `MyService` 的 `show` 方法首次被调用时，由于 `@Autowired` 和 `@Lazy` 注解的组合，Spring 不是直接注入原始的 `MyBean` 实例，而是注入一个代理对象。
   - 这个代理对象的类名为 `com.xcs.spring.bean.MyBean$$EnhancerBySpringCGLIB$$2a517c55`，表明它是由 Spring 的 CGLIB 动态生成的。
   - 当尝试访问该代理对象上的方法（如 `show` 方法）时，代理会确保真正的 `MyBean` 被初始化并代理该方法调用。

```java
启动 Spring ApplicationContext...
启动完成 Spring ApplicationContext...
获取MyService...
调用show方法...
MyBean Class = class com.xcs.spring.bean.MyBean$$EnhancerBySpringCGLIB$$2a517c55
MyBean 初始化了!
MyBean 的构造函数被调用了!
hello world!
```

### 五、时序图

#### 5.1、Bean注册时序图

~~~mermaid
sequenceDiagram 
LazyApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建应用上下文
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新应用上下文
AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)<br>调用BFPP方法
AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>委托BFPP处理
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup)<br>调用BDRPP方法
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor:postProcessBeanDefinitionRegistry(registry)<br>处理Bean定义
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor:processConfigBeanDefinitions(registry)<br>处理配置类Bean
ConfigurationClassPostProcessor->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitions(configurationModel)<br>加载Bean定义
ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitionsForConfigurationClass(configClass,trackedConditionEvaluator)<br>为配置类加载
ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitionsForBeanMethod(beanMethod)<br>为@Bean方法加载
ConfigurationClassBeanDefinitionReader->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd,metadata)<br>处理注解定义
AnnotationConfigUtils->>AnnotationConfigUtils:attributesFor(metadata, Lazy.class)<br>获取注解属性
AnnotationConfigUtils->>AbstractBeanDefinition:setLazyInit(lazyInit)<br>设置懒加载属性
~~~

#### 5.2、Bean延迟创建时序图

~~~mermaid
sequenceDiagram 
DependsOnApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建应用上下文
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新应用上下文
AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>完成bean工厂初始化
AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>预实例化单例beans
DefaultListableBeanFactory->>AbstractBeanDefinition:isLazyInit()
AbstractBeanDefinition->>DefaultListableBeanFactory:返回Bean是否懒加载
DefaultListableBeanFactory->>AbstractBeanFactory:getBean(beanName)
alt Bean是懒加载
	AbstractBeanFactory->>DefaultListableBeanFactory: 执行初始化Bean对象
else Bean不是懒加载
	AbstractBeanFactory->>DefaultListableBeanFactory: 跳过Bean初始化，等待真正使用时在初始化
end
~~~

#### 5.3、Bean延迟注入时序图

~~~mermaid
sequenceDiagram
Title: InstantiationAwareBeanPostProcessor时序图
InstantiationAwareBeanPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext<br>开始初始化
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh<br>刷新上下文
AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization<br>实例化非懒加载的单列Bean
AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons<br>预实例化Singleton
DefaultListableBeanFactory->>AbstractBeanFactory:getBean<br>根据beanName获取对象
AbstractBeanFactory->>AbstractBeanFactory:doGetBean<br>返回指定bean的实例
AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton<br>获取单例对象
DefaultSingletonBeanRegistry->>AbstractBeanFactory:getObject<br>ObjectFactory接口的工厂方法
AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean<br>创建一个bean实例，填充bean实例，应用后处理器
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName) 
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)
AutowiredAnnotationBeanPostProcessor->>InjectionMetadata:inject(target,beanName,pvs)
InjectionMetadata->>AutowiredFieldElement:inject(bean,beanName,pvs)
AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field,bean,beanName)
AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(descriptor,requestingBeanName,autowiredBeanNames,typeConverter)
DefaultListableBeanFactory->>DefaultListableBeanFactory:getAutowireCandidateResolver()
DefaultListableBeanFactory->>ContextAnnotationAutowireCandidateResolver:getLazyResolutionProxyIfNecessary(descriptor,beanName)
ContextAnnotationAutowireCandidateResolver->>ContextAnnotationAutowireCandidateResolver:isLazy(descriptor)
ContextAnnotationAutowireCandidateResolver->>ContextAnnotationAutowireCandidateResolver:buildLazyResolutionProxy(descriptor, beanName)
ContextAnnotationAutowireCandidateResolver->>ProxyFactory:getProxy(classLoader)
ProxyFactory->>ContextAnnotationAutowireCandidateResolver:返回被代理的对象
ContextAnnotationAutowireCandidateResolver->>DefaultListableBeanFactory:返回被代理的对象
DefaultListableBeanFactory->>AutowiredFieldElement:返回被代理的对象
AutowiredFieldElement->>Field:field.set(bean, value)注入被代理的对象
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，然后从中获取一个 `MyService` bean 并调用其 `show` 方法。

```java
public class LazyApplication {

    public static void main(String[] args) {
        System.out.println("启动 Spring ApplicationContext...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("启动完成 Spring ApplicationContext...");

        System.out.println("获取MyService...");
        MyService myService = context.getBean(MyService.class);

        System.out.println("调用show方法...");
        myService.show();
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤。

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
    // 实例化所有剩余非懒加载的单列Bean对象
    finishBeanFactoryInitialization(beanFactory);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization`方法中，会继续调用`DefaultListableBeanFactory`类中的`preInstantiateSingletons`方法来完成所有剩余非懒加载的单列Bean对象。

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [代码部分省略以简化]
    // 完成所有剩余非懒加载的单列Bean对象。
    beanFactory.preInstantiateSingletons();
}
```

#### 6.1、延迟初始化

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons`方法中，确保所有非懒加载的单例 beans 在容器启动时都被初始化，除非它们显式地标记为懒加载。这也是为什么 `@Lazy` 注解对于那些想要推迟 bean 初始化的场景非常有用。

```java
public void preInstantiateSingletons() throws BeansException {
    // ... [代码部分省略以简化]
    // 复制Bean名称列表
    List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

    // 初始化非懒加载单例
    for (String beanName : beanNames) {
        RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
        // Bean属性检查
        if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
            // 是否为工厂Bean
            if (isFactoryBean(beanName)) {
                Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
                // 是否为FactoryBean实例
                if (bean instanceof FactoryBean) {
                    FactoryBean<?> factory = (FactoryBean<?>) bean;
                    boolean isEagerInit;
                    // 安全权限检查
                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                        isEagerInit = AccessController.doPrivileged(
                            (PrivilegedAction<Boolean>) ((SmartFactoryBean<?>) factory)::isEagerInit,
                            getAccessControlContext());
                    }
                    else {
                        // 是否立即初始化
                        isEagerInit = (factory instanceof SmartFactoryBean &&
                                       ((SmartFactoryBean<?>) factory).isEagerInit());
                    }
                    // 立即初始化Bean
                    if (isEagerInit) {
                        getBean(beanName);
                    }
                }
            }
            else {
                // 获取/创建Bean
                getBean(beanName);
            }
        }
    }
    // ... [代码部分省略以简化]
}
```

#### 6.2、延迟注入

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
    // ... [代码部分省略以简化]

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，对 bean 的属性进行注入。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // ... [代码部分省略以简化]
    
    try {
        // 属性注入：这一步将配置中的属性值注入到bean实例中。例如，XML中定义的属性或使用@Autowired和@Value注解的属性都会在这里被注入
        populateBean(beanName, mbd, instanceWrapper);

       	// ... [代码部分省略以简化]
    } 
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }
    
	// ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean`方法中，如果一个属性被标记为 `@Autowired`，并且与 `@Lazy` 结合使用，那么实际的懒加载逻辑会在这个步骤中的其他部分被处理（特别是通过 `AutowiredAnnotationBeanPostProcessor`）。

```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
    // ... [代码部分省略以简化]
    // 对每一个InstantiationAwareBeanPostProcessor进行处理，这些处理器可能会修改Bean的属性值。
    for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
        // 尝试使用新版本的方法 postProcessProperties
        PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
         // ... [代码部分省略以简化]
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties`方法中，用于处理 `@Autowired` 注解的依赖注入。

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    // 根据bean的名称和类查找@Autowired注解元数据
    InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
    try {
        // 执行实际的依赖注入
        metadata.inject(bean, beanName, pvs);
    }
    catch (BeanCreationException ex) {
        // 抛出bean创建异常
        throw ex;
    }
    catch (Throwable ex) {
        // 处理其他类型的异常，转换为Bean创建异常
        throw new BeanCreationException(beanName, "依赖自动注入失败", ex);
    }
    // 返回属性值
    return pvs;
}
```

在`org.springframework.beans.factory.annotation.InjectionMetadata#inject`方法中，遍历所有这些元素并调用其 `inject` 方法，这样实现了对带有注解的字段或方法的实际注入。

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // 获取已校验的注入元素
    Collection<InjectedElement> checkedElements = this.checkedElements;
    // 如果没有已校验的元素，则使用所有注入元素
    Collection<InjectedElement> elementsToIterate =
        (checkedElements != null ? checkedElements : this.injectedElements);
    
    if (!elementsToIterate.isEmpty()) {
        // 遍历所有待注入的元素（字段或方法）
        for (InjectedElement element : elementsToIterate) {
            // 执行实际的注入操作
            element.inject(target, beanName, pvs);
        }
    }
}
```

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject`方法中，这个 `inject` 方法的核心逻辑是解析 `@Autowired` 字段的值（即找到匹配的 bean 依赖）并注入到目标 bean 中。在这个过程中，使用缓存以提高性能。

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // 获取带有@Autowired注解的字段
    Field field = (Field) this.member;
    Object value;
    // 如果字段的值已经被缓存，则直接从缓存中获取
    if (this.cached) {
        try {
            value = resolvedCachedArgument(beanName, this.cachedFieldValue);
        }
        catch (NoSuchBeanDefinitionException ex) {
            // 如果缓存中的bean意外被移除 -> 重新解析
            value = resolveFieldValue(field, bean, beanName);
        }
    }
    else {
        // 解析字段的值（即找到要注入的bean）
        value = resolveFieldValue(field, bean, beanName);
    }
    if (value != null) {
        // 使字段可访问（例如，如果它是私有的）
        ReflectionUtils.makeAccessible(field);
        // 将解析出的值（bean）注入到目标bean的字段中
        field.set(bean, value);
    }
}
```

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#resolveFieldValue`方法中，主要用于解析 `@Autowired` 注解的字段值。它确定了应该为目标字段注入哪个 bean。

```java
@Nullable
private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
	// ... [代码部分省略以简化]

    Object value;
    try {
        // 解析依赖：这里的核心逻辑是使用bean工厂去解析字段的依赖。它会查找合适的bean来注入到此字段。
        value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
    }
    catch (BeansException ex) {
        // 当无法解析依赖时，抛出异常
        throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
    }
    
    // ... [代码部分省略以简化]

    // 返回解析到的值（bean）
    return value; 
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency`方法中，这里的关键逻辑是 `getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary`，该方法尝试为描述的依赖关系获取一个懒加载代理。如果该依赖关系标记为懒加载 (`@Lazy`)，并且被 `@Autowired` 引用，那么它将返回一个懒加载代理，而不是实际的 bean。这样，只有当应用程序真正访问该依赖关系时，实际的 bean 才会被初始化。

```java
@Override
@Nullable
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                                @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
	// ... [代码部分省略以简化]
    
    // 尝试为描述的依赖关系获取一个懒加载代理。如果依赖是懒加载的，这将返回一个代理对象。
    Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
        descriptor, requestingBeanName);

    // 如果没有为懒加载的依赖关系获取到代理，则尝试直接解析依赖关系
    if (result == null) {
        result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
    }

    return result; // 返回解析得到的bean或者懒加载代理
}
```

在`org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary`方法中，如果是懒加载，会调用 `buildLazyResolutionProxy` 来创建一个代理，当首次访问该代理时，代理会触发实际的 bean 初始化。

```java
@Override
@Nullable
public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
    // 判断依赖描述是否被标记为懒加载
    // 如果是懒加载，为其构建一个懒加载代理
    // 如果不是懒加载，则返回null
    return (isLazy(descriptor) ? buildLazyResolutionProxy(descriptor, beanName) : null);
}
```

在`org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver#buildLazyResolutionProxy`方法中，核心部分是 `TargetSource` 和 `ProxyFactory`。当我们访问这个懒加载代理时，`TargetSource` 的 `getTarget` 方法会被调用，它会解析和返回真正的 bean（或其他依赖项）。使用 `ProxyFactory`，可以为给定的 `TargetSource` 创建一个新的代理。这是 `@Lazy` 注解在字段注入时的实际实现，确保在首次访问字段时才触发依赖的解析和bean的初始化。

```java
protected Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, final @Nullable String beanName) {
    // 获取当前的BeanFactory
    BeanFactory beanFactory = getBeanFactory();
    // 确认BeanFactory是DefaultListableBeanFactory的实例
    Assert.state(beanFactory instanceof DefaultListableBeanFactory,
                 "BeanFactory needs to be a DefaultListableBeanFactory");
    final DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;

    // 创建一个目标源(TargetSource)用于懒加载代理
    TargetSource ts = new TargetSource() {
        // 获取依赖的类型
        @Override
        public Class<?> getTargetClass() {
            return descriptor.getDependencyType();
        }
        @Override
        public boolean isStatic() {
            return false;
        }
        // 当访问代理时，该方法被调用来解析实际的依赖关系
        @Override
        public Object getTarget() {
            // ... [代码部分省略以简化]
            return target; // 返回解析得到的bean
        }
        @Override
        public void releaseTarget(Object target) {
        }
    };

    // 使用Spring的ProxyFactory创建一个新的代理
    ProxyFactory pf = new ProxyFactory();
    pf.setTargetSource(ts);
    Class<?> dependencyType = descriptor.getDependencyType();
    if (dependencyType.isInterface()) {
        pf.addInterface(dependencyType);
    }
    // 返回懒加载代理
    return pf.getProxy(dlbf.getBeanClassLoader());
}
```

### 七、注意事项

1. **默认行为**
   + 如果没有使用 `@Lazy` 注解，Spring 容器会在启动时立即实例化单例 bean。通过使用 `@Lazy`，我们可以改变这个行为，使得 bean 只在首次请求时被初始化。
2. **构造函数注入**
   + 如果一个懒加载的 bean 依赖于另一个非懒加载的 bean，那么该懒加载的 bean 会在容器启动时被初始化，因为它的依赖需要它。这种情况常常在构造函数注入时出现。
3. **上下文的生命周期**
   + `@Lazy` 对于应用上下文的启动速度可能有益，因为少了一些即时初始化的工作。但请注意，延迟初始化可能会导致我们在首次访问 bean 时遇到延迟。
4. **与 `@Autowired` 结合使用**
   + 如果我们在一个字段或 setter 方法上同时使用 `@Autowired` 和 `@Lazy`，Spring 会注入一个代理对象。这个代理对象会在我们首次访问它时触发真正的 bean 初始化。
5. **懒加载的代理**
   + 当与 `@Autowired` 结合使用时，要确保 bean 的类型与代理类型兼容。如果注入的 bean 类型是一个接口，Spring 会创建一个基于接口的代理。如果是一个类，Spring 会创建一个基于类的代理。
6. **错误处理**
   + 延迟初始化意味着某些错误可能在应用启动时不会被立即发现，例如 bean 配置错误。这样的错误只有在实际尝试初始化 bean 时才会被抛出。
7. **在组件类上使用**
   + 对于直接或间接使用 `@Component`、`@Service`、`@Repository` 或 `@Controller` 注解的类，可以使用 `@Lazy` 注解来使这些组件在首次被注入或查找时才被初始化。

### 八、总结

#### 8.1、最佳实践总结

1. **上下文初始化**:
   - 使用 `AnnotationConfigApplicationContext` 初始化应用上下文是针对基于Java的配置的推荐做法。
   - 提供一个配置类（如 `MyConfiguration`）作为参数可以帮助上下文知道如何加载和配置beans。
2. **懒加载配置**:
   - 通过在配置类的 `@Bean` 方法上添加 `@Lazy` 注解，可以确保特定的bean只在首次请求时被初始化，而不是在应用上下文启动时。
   - 这可以提高应用启动速度，特别是对于资源密集型的beans或需要长时间初始化的beans。
3. **依赖注入**:
   - 使用 `@Autowired` 注解是Spring的核心特性，它可以自动注入bean的依赖关系。
   - 当与 `@Lazy` 注解组合使用时，Spring会注入一个代理对象而不是实际的bean实例。这个代理对象在首次访问时会触发真正的bean初始化。
4. **理解代理**:
   - 由于延迟注入，通过 `@Autowired` 和 `@Lazy` 注解注入的对象是一个由CGLIB生成的代理对象。
   - 这个代理对象负责在首次访问时初始化真正的bean。
5. **输出与验证**:
   - 通过在bean的初始化和方法调用中添加日志或打印语句，可以验证和观察懒加载和代理的行为。
   - 这对于确保应用的预期行为和性能调优非常有用。

#### 8.2、源码分析总结

1. **启动及初始化**:
   - 使用`AnnotationConfigApplicationContext`初始化应用上下文。
   - 在`AnnotationConfigApplicationContext`的构造函数中，执行了注册(`register`)和刷新(`refresh`)方法。
2. **Bean的实例化**:
   - 在上下文刷新过程中，`finishBeanFactoryInitialization(beanFactory)`方法确保所有非懒加载的单例Beans被实例化。
   - `DefaultListableBeanFactory#preInstantiateSingletons`方法确保所有非懒加载的单例Beans在容器启动时被初始化。
3. **延迟初始化**:
   - 如果Bean被标记为`@Lazy`，它将不会在容器启动时被初始化，但只在首次请求时。
   - `DefaultListableBeanFactory#preInstantiateSingletons`方法中，对于`isLazyInit`返回`true`的Beans，不会进行预初始化。
4. **Bean的获取与依赖注入**:
   - 使用`AbstractBeanFactory#getBean`方法获取Bean实例。
   - 如果Bean尚未创建，`doGetBean`方法将执行Bean的实际创建，包括解析依赖关系、处理循环引用等。
   - 对于单例Beans，它们将被缓存，确保每次都返回相同的实例。
   - 通过`AbstractAutowireCapableBeanFactory#createBean`来进行实际的Bean创建，并且将其属性通过`populateBean`方法注入。
5. **延迟注入**:
   - 如果一个字段或属性被`@Autowired`注解，并与`@Lazy`结合使用，实际的懒加载逻辑会在属性填充阶段被处理。
   - 使用`AutowiredAnnotationBeanPostProcessor`来处理带有`@Autowired`注解的属性的注入。
   - 在`AutowiredFieldElement#inject`方法中，如果字段被标记为`@Lazy`，Spring不会直接注入真实的Bean，而是注入一个懒加载代理。
   - 这个懒加载代理的实际行为是在首次访问时触发真正的Bean初始化。
6. **懒加载代理的创建**:
   - 使用`ContextAnnotationAutowireCandidateResolver`来检查依赖关系是否需要懒加载。
   - 如果需要懒加载，它将使用`buildLazyResolutionProxy`方法来为该依赖关系创建一个代理。
   - 这个代理的行为是：在首次访问时，它会解析和返回真正的Bean或其他依赖项。
   - 使用Spring的`ProxyFactory`来为给定的目标源创建新的代理。