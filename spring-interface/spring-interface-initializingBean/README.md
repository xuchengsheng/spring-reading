## InitializingBean

- [InitializingBean](#initializingbean)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845609) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [InitializingBean源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-initializingBean)

### 二、接口描述

`InitializingBean` 接口，主要用于在 bean 的所有属性被初始化后，但在 bean 被实际使用之前，执行某些初始化逻辑或设置。

### 三、接口源码

`InitializingBean` 接口，实现此接口的 beans 会在所有属性都设置完毕后，由 `BeanFactory` 调用其 `afterPropertiesSet()` 方法。

```java
/**
 * 接口定义，用于需要在其所有属性被 BeanFactory 设置后执行操作的 beans。
 * 例如，可以执行自定义初始化或检查所有必需属性是否已设置。
 * 
 * 实现此接口的 beans 会在所有属性都设置完毕后，由 BeanFactory 调用其 `afterPropertiesSet()` 方法。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DisposableBean  // 当 bean 不再需要时，用于回调的接口
 * @see org.springframework.beans.factory.config.BeanDefinition#getPropertyValues()
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getInitMethodName()
 */
public interface InitializingBean {

	/**
	 * 当 BeanFactory 设置了 bean 的所有属性后调用此方法。
     * 也即满足了 BeanFactoryAware, ApplicationContextAware 等条件后。
     * 
	 * 此方法让 bean 实例可以在所有属性都设置后进行最终的配置验证和初始化。
	 * 如果出现配置错误（如未设置必需的属性）或因其他原因初始化失败，此方法可能会抛出异常。
	 * 
	 * @throws Exception 配置错误或其他任何初始化失败原因导致的异常
	 */
	void afterPropertiesSet() throws Exception;
}
```

### 四、主要功能

1. **初始化回调**
   + `InitializingBean` 接口为 Spring 容器提供了一个机制，以确保在 bean 的所有属性都被设置后，但在 bean 被其他组件使用之前，可以执行某些初始化逻辑或操作。

2. **属性验证**
   + 在 `afterPropertiesSet` 方法中，我们可以验证 bean 的属性是否都已正确设置，特别是一些必要的属性。

3. **自定义初始化逻辑**
   + 如果 bean 需要进行特定的初始化操作，如开启资源、连接数据库、启动某些线程或其他任何初始化活动，那么这些操作可以在 `afterPropertiesSet` 方法中进行。

4. **生命周期管理**
   + `InitializingBean` 是 Spring 生命周期中的一个关键点，它在属性注入 (`Property Injection`) 之后和使用 bean 之前被调用。这提供了一个干净的生命周期钩子，可以用来确保 bean 在被使用之前是完全准备好的。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class InitializingBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保 `MyInitializingBean` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyInitializingBean myInitializingBean(){
        return new MyInitializingBean();
    }
}
```

在 `afterPropertiesSet()` 方法中，模拟了数据加载的过程。

```java
public class MyInitializingBean implements InitializingBean {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    @Override
    public void afterPropertiesSet() {
        // 在此方法中，我们模拟数据加载
        data = new ArrayList<>();
        data.add("数据1");
        data.add("数据2");
        data.add("数据3");
        System.out.println("MyInitializingBean 初始化完毕，数据已加载!");
    }
}
```

运行结果发现，我们会在控制台上看到 "`MyInitializingBean 初始化完毕，数据已加载!`" 这样的输出，表示数据已经被加载到 `data` 列表中。

```java
MyInitializingBean 初始化完毕，数据已加载!
```

### 六、时序图

~~~mermaid
sequenceDiagram
    Title: InitializingBean时序图
    participant InitializingBeanApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyInitializingBean
    
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
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeInitMethods(beanName,bean,mbd)<br>调用bean的初始化方法
    AbstractAutowireCapableBeanFactory->>MyInitializingBean:afterPropertiesSet()<br>执行InitializingBean接口的方法
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>InitializingBeanApplication:初始化完成
~~~

### 七、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class InitializingBeanApplication {

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`方法中，如果bean实现了`InitializingBean`接口，则它的`afterPropertiesSet`方法会在此处被调用。此外，如果bean配置中定义了自定义的初始化方法，spring会在这里被调用。

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [代码部分省略以简化]
    
    try {
        invokeInitMethods(beanName, wrappedBean, mbd);
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }
    
    // ... [代码部分省略以简化]

    return wrappedBean;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods`方法中，首先检查 bean 是否实现了 `InitializingBean` 接口。如果是，则进一步检查 `afterPropertiesSet` 方法是否被外部管理。如果不是，则调用该方法。这是 Spring bean 生命周期中的一个关键步骤，确保在 bean 被应用程序其他部分使用之前，它已经正确初始化。

```java
protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
			throws Throwable {

    boolean isInitializingBean = (bean instanceof InitializingBean);
    if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
        if (logger.isTraceEnabled()) {
            logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
        }
        if (System.getSecurityManager() != null) {
            // ... [代码部分省略以简化]
        }
        else {
            ((InitializingBean) bean).afterPropertiesSet();
        }
    }
    // ... [代码部分省略以简化]
}
```

最后执行到我们自定义的逻辑中，模拟了数据加载的过程。

```java
public class MyInitializingBean implements InitializingBean {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    @Override
    public void afterPropertiesSet() {
        // 在此方法中，我们模拟数据加载
        data = new ArrayList<>();
        data.add("数据1");
        data.add("数据2");
        data.add("数据3");
        System.out.println("MyInitializingBean 初始化完毕，数据已加载!");
    }
}
```

### 八、注意事项

1. **使用 @PostConstruct**
   + 尽管 `InitializingBean` 提供了一个初始化 bean 的方式，但现代的 Spring 我们更倾向于使用 `@PostConstruct` 注解，因为它是 JSR-250 的一部分，不依赖于 Spring 特定的接口。

2. **避免业务逻辑**
   + 在 `afterPropertiesSet` 方法中，应该只包含与初始化相关的逻辑。避免将核心的业务逻辑放在这里。

3. **处理异常**
   + `afterPropertiesSet` 方法允许抛出异常。确保我们处理了可能出现的所有异常，特别是可能阻止 bean 正确初始化的那些。

4. **明确的初始化顺序**:
   + 请记住，`afterPropertiesSet` 是在所有属性都设置之后调用的，但在任何自定义的 init 方法和 `@PostConstruct` 方法之前。

5. **不要过于依赖**
   + 尽量避免让太多的 beans 实现 `InitializingBean`，因为这可能会使代码难以阅读和管理。如果可能，考虑使用其他的初始化方法。如 `@PostConstruct` 注解。

### 九、总结

#### 最佳实践总结

1. **启动类**
   + 在 `InitializingBeanApplication` 类中，我们使用 `AnnotationConfigApplicationContext` 为上下文环境。这种上下文环境使用 Java 注解来配置 Spring 容器，而不是传统的 XML。通过传递 `MyConfiguration` 类作为构造参数，我们告诉 Spring 在哪里找到 bean 的定义。

2. **配置类**
   + `MyConfiguration` 类使用 `@Configuration` 注解，标识它为一个 Spring 配置类。在该类中，我们定义了一个名为 `myInitializingBean` 的 bean，它返回一个新的 `MyInitializingBean` 实例。这样，我们确保 `MyInitializingBean` 类将由 Spring 容器管理，并且其生命周期方法（如 `afterPropertiesSet()`）会被调用。

3. **初始化逻辑**
   + `MyInitializingBean` 类实现了 `InitializingBean` 接口，并重写了其 `afterPropertiesSet()` 方法。在这个方法中，我们模拟了数据加载的过程，简单地向 `data` 列表中添加了三条字符串数据。当 Spring 容器初始化这个 bean 时，它会自动调用 `afterPropertiesSet()` 方法，从而执行这个初始化逻辑。

4. **运行结果**
   + 当我们运行应用程序时，由于 `MyInitializingBean` 已经被 Spring 容器管理并初始化，`afterPropertiesSet()` 方法被调用，因此我们会在控制台上看到 "`MyInitializingBean 初始化完毕，数据已加载!`" 的输出。

#### 源码分析总结

1. **启动上下文**
   + 使用 `AnnotationConfigApplicationContext` 以 Java 注解方式启动 Spring 上下文，传入 `MyConfiguration` 配置类为参数，此时 Spring 容器启动并初始化。

2. **构造函数中的重点**
   + `AnnotationConfigApplicationContext` 的构造函数执行了 `register` 和 `refresh` 方法，其中 `refresh` 是我们关注的核心。

3. **刷新上下文**
   + 在 `refresh` 方法中，Spring 上下文开始其核心的刷新过程，重点是 `finishBeanFactoryInitialization`，它确保实例化所有剩余的非懒加载的单例 Bean。

4. **预实例化单例 Beans**
   + 方法 `preInstantiateSingletons` 负责预先实例化所有非懒加载的单例 bean。这意味着在 Spring 上下文初始化完成后，所有的单例 beans 都会被实例化，初始化，并注入所需的依赖。

5. **获取 Bean**
   + 核心方法 `getBean` 和 `doGetBean` 负责从容器中检索 bean。如果 bean 尚未创建，这些方法还会负责 bean 的创建、属性注入和初始化。

6. **单例注册**
   + `getSingleton` 方法在 `DefaultSingletonBeanRegistry` 中确保 bean 作为单例存在。如果 bean 未在缓存中找到，它会使用提供的 `ObjectFactory` 创建一个新的实例。

7. **创建 Bean**
   + `createBean` 和 `doCreateBean` 方法负责实际的 bean 创建过程，其中包括实例化、属性注入和初始化。

8. **初始化 Bean**
   + 方法 `initializeBean` 负责 bean 的初始化，调用其初始化方法。这包括 `InitializingBean` 接口的 `afterPropertiesSet` 方法。

9. **初始化方法调用**
   + `invokeInitMethods` 方法会检查 bean 是否实现了 `InitializingBean` 接口。如果实现了，并且 `afterPropertiesSet` 方法不是外部管理的，那么它会被调用。

10. **自定义初始化逻辑**
    + 我们自定义的 `MyInitializingBean` 类实现了 `InitializingBean` 接口，并重写了 `afterPropertiesSet` 方法来模拟数据加载的过程。