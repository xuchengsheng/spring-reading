## BeanPostProcessor

- [BeanPostProcessor](#beanpostprocessor)
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

`BeanPostProcessor`接口，允许对新创建的 Bean 进行操作和修改。

### 二、接口源码

`BeanPostProcessor` 是 Spring 框架自 10.10.2003 开始引入的一个核心接口。该接口定义了两个主要的方法：`postProcessBeforeInitialization` 和 `postProcessAfterInitialization`，分别在 Bean 的初始化前后调用。从源码中可以发现`BeanPostProcessor`接口具备注册与排序功能，那什么是注册呢？首先是自动注册，当启动一个`AnnotationConfigApplicationContext`时，Spring 会自动扫描定义的所有 bean。如果某个 bean 实现了 `BeanPostProcessor` 接口，Spring 将自动识别并注册它。另外一个是手动注册通过调用 `ConfigurableBeanFactory` 的 `addBeanPostProcessor` 方法来完成。那为什么是排序呢？如果 `BeanPostProcessor` 实现了 `org.springframework.core.PriorityOrdered` 或 `org.springframework.core.Ordered` 接口，那么 Spring 将会根据它们的优先级或顺序进行排序。在 `AnnotationConfigApplicationContext` 中，Spring 会考虑上述两个接口的语义来确定 `BeanPostProcessor` 的执行顺序。但是需要注意的是，如果我们手动在 `ConfigurableBeanFactory` 中注册了 `BeanPostProcessor`，那么 Spring 会按照我们注册它们的顺序来应用这些后处理器，在这种情况下，即使 `BeanPostProcessor` 实现了 `PriorityOrdered` 或 `Ordered` 接口，这些排序语义也会被忽略。

```java
/**
 * 工厂钩子，允许对新创建的bean实例进行自定义修改。
 * 例如，检查标记接口或使用代理包装beans。
 *
 * 通常，通过标记接口等填充beans的后处理器会实现 #postProcessBeforeInitialization
 * 而使用代理包装beans的后处理器通常会实现 #postProcessAfterInitialization
 *
 * 注册
 * ApplicationContext 可以在其bean定义中自动检测到 BeanPostProcessor beans，
 * 并将这些后处理器应用于随后创建的任何beans。一个普通的 BeanFactory 允许以编程方式注册
 * 后处理器，将它们应用于通过bean工厂创建的所有beans。
 *
 * 排序
 * 在 ApplicationContext 中自动检测到的 BeanPostProcessor beans 将根据
 * org.springframework.core.PriorityOrdered 和
 * org.springframework.core.Ordered 语义进行排序。相反，以编程方式在 
 * BeanFactory 中注册的 BeanPostProcessor beans 将按注册顺序应用；
 * 通过实现 PriorityOrdered 或 Ordered 接口表达的任何排序语义
 * 对于编程注册的后处理器都将被忽略。此外，org.springframework.core.annotation.Order @Order
 * 注释不适用于 BeanPostProcessor beans。
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * 在任何bean初始化回调（如InitializingBean的 afterPropertiesSet
	 * 或自定义初始化方法）之前，将此 BeanPostProcessor 应用于给定的新bean实例。
	 * 该bean已使用属性值填充。返回的bean实例可能是原始实例的包装。
	 * 默认实现返回给定的 bean。
	 * @param bean 新的bean实例
	 * @param beanName bean的名称
	 * @return 要使用的bean实例，可以是原始实例或其包装；如果为 null，则不会调用后续的BeanPostProcessors
	 * @throws org.springframework.beans.BeansException 出错时
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * 在任何bean初始化回调（如InitializingBean的 afterPropertiesSet
	 * 或自定义初始化方法）之后，将此 BeanPostProcessor 应用于给定的新bean实例。
	 * 该bean已使用属性值填充。返回的bean实例可能是原始实例的包装。
	 * 对于FactoryBean，此回调将被调用，既适用于FactoryBean实例，也适用于FactoryBean创建的对象（自Spring 2.0起）。
	 * 后处理器可以决定通过相应的 bean instanceof FactoryBean 检查，是否应用于FactoryBean、创建的对象或两者。
	 * 此回调还将在由 InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation
	 * 方法触发的短路之后调用，与所有其他 BeanPostProcessor 回调相反。
	 * 默认实现返回给定的 bean。
	 * @param bean 新的bean实例
	 * @param beanName bean的名称
	 * @return 要使用的bean实例，可以是原始实例或其包装；如果为 null，则不会调用后续的BeanPostProcessors
	 * @throws org.springframework.beans.BeansException 出错时
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
```

### 三、主要功能

**修改 Bean 属性**：在 Bean 初始化之前或之后，可以修改 Bean 的属性。例如，可以根据某些条件为 Bean 的某些属性设置默认值。

**验证 Bean 的状态**：在 Bean 初始化完成后，可以检查 Bean 的状态，确保它满足某些条件或约束。

**返回代理 Bean**：最常见的用例是返回一个代理（proxy）来包装原始的 Bean。例如，Spring AOP 使用它来为目标 Bean 创建代理，从而实现切面的功能。

**改变返回的 Bean 类型**：可以完全替换一个 Bean 的实例，返回一个不同类型的对象。这是一个高级用例，但在某些场景中可能是必要的

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyService`类型的bean，最后打印了该`show`方法返回的值。

```java
public class BeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyService myService = context.getBean(MyService.class);
        System.out.println(myService.show());
        context.close();
    }
}
```

这里使用`@Bean`注解，定义了两个Bean，是为了确保`MyService`， `MyBeanPostProcessor` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

    @Bean
    public BeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
```

在`postProcessBeforeInitialization`方法中，将 `MyServiceImpl` 中的 `message` 属性，为其添加 `Prefix:`  前缀。在`postProcessAfterInitialization`方法中，将 `MyServiceImpl` 中的 `message` 属性，为其添加 `:Suffix`  后缀。

```java
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage("Prefix: " + myService.getMessage());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage(myService.getMessage() + " :Suffix");
        }
        return bean;
    }
}
```

一个简单的接口

```java
public interface MyService {
    String show();
}
```

一个简单的实现类

```java
public class MyServiceImpl implements MyService{

    private String message = "Hello from MyService";

    @Override
    public String show() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

运行结果发现， `MyBeanPostProcessor` 实现修改了 `MyServiceImpl` bean的`message`属性，首先在初始化前加了前缀，然后在初始化后加了后缀。

```java
Prefix: Hello from MyService :Suffix
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: BeanPostProcessor时序图
    participant BeanPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyBeanPostProcessor
    
    BeanPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
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
    AbstractAutowireCapableBeanFactory->>MyBeanPostProcessor: postProcessBeforeInitialization(bean,beanName)
    MyBeanPostProcessor-->>AbstractAutowireCapableBeanFactory: 返回Bean对象
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsAfterInitialization(existingBean,beanName)
    AbstractAutowireCapableBeanFactory->>MyBeanPostProcessor: postProcessAfterInitialization(bean,beanName)
    MyBeanPostProcessor-->>AbstractAutowireCapableBeanFactory: 返回Bean对象
	AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
	AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
	AnnotationConfigApplicationContext->>BeanPostProcessorApplication: 初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyService`类型的bean，最后调用了`MyService`对象的`show`方法，并将其返回值打印到控制台。

```java
public class BeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyService myService = context.getBean(MyService.class);
        System.out.println(myService.show());
        context.close();
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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，主要负责两大步骤，第一步是属性注入，第二步是bean初始化，确保bean是完全配置和准备好的。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // 声明一个对象，后续可能用于存放初始化后的bean或它的代理对象
    Object exposedObject = bean;

    // ... [代码部分省略以简化]
    
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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean()`方法中，核心逻辑是对`BeanPostProcessors`接口中的`postProcessBeforeInitialization`，`postProcessAfterInitialization`进行回调。

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
    // ... [代码部分省略以简化]

    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }

    // ... [代码部分省略以简化]
    
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`方法中，遍历每一个 `BeanPostProcessor` 的 `postProcessAfterInitialization` 方法都有机会对bean进行修改或增强

```java
@Override
public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
    throws BeansException {

    Object result = existingBean;
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
        Object current = processor.postProcessAfterInitialization(result, beanName);
        if (current == null) {
            return result;
        }
        result = current;
    }
    return result;
}
```

最后执行到我们自定义的逻辑中，将 `MyServiceImpl` 中的 `message` 属性，为其添加 `Prefix:`  前缀。在`postProcessAfterInitialization`方法中，将 `MyServiceImpl` 中的 `message` 属性，为其添加 `:Suffix`  后缀。

```java
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage("Prefix: " + myService.getMessage());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage(myService.getMessage() + " :Suffix");
        }
        return bean;
    }
}
```

### 七、注意事项

**执行顺序**：Spring容器中可能注册了多个 `BeanPostProcessor`。它们的执行顺序是由它们的`Ordered`值决定的（如果实现了`Ordered`接口）。如果有多个`BeanPostProcessor`对同一个bean进行处理，它们的处理顺序可能会对结果产生影响。

**返回值**：`postProcessBeforeInitialization` 和 `postProcessAfterInitialization` 方法都可以返回一个新的bean来替代原始bean。如果我们返回`null`，那么后续的`BeanPostProcessor`不会被执行。

**注意性能**：由于`BeanPostProcessor`方法对容器中的所有bean都会被调用，所以它们可能对启动时间和性能产生影响。我们应该确保这些方法的实现尽可能高效。

**不要修改无状态bean**：无状态的bean（如工具类或帮助类）应该避免在 `BeanPostProcessor` 中被修改，因为这通常没有意义，并可能导致不必要的性能开销。

**避免在`BeanPostProcessor`中抛出异常**：应该避免在`BeanPostProcessor`方法中抛出异常。这可能会导致Spring容器启动失败或其他bean初始化失败。

### 八、总结

#### 8.1、最佳实践总结

**启动类 `BeanPostProcessorApplication`：**

此类首先初始化了一个基于Java配置的Spring上下文。在初始化完成后，从上下文中获取了一个`MyService`类型的bean，并调用了它的`show`方法，打印了返回的值。

**配置类 `MyConfiguration`：**

这是一个基于Java的配置类，其中定义了两个beans：`MyService`和`MyBeanPostProcessor`。这确保了当Spring容器启动时，这两个beans都会被创建和管理。

**实现类 `MyBeanPostProcessor`：**

该类实现了`BeanPostProcessor`接口，并覆盖了`postProcessBeforeInitialization`和`postProcessAfterInitialization`方法。在这两个方法中，它检查当前正在处理的bean是否是`MyServiceImpl`的实例。如果是，它会修改该bean的`message`属性：

- 在`postProcessBeforeInitialization`中，给`message`添加了`Prefix:`前缀。
- 在`postProcessAfterInitialization`中，给`message`添加了`:Suffix`后缀。

**接口与实现：**

`MyService`接口定义了一个`show`方法，而`MyServiceImpl`是它的一个简单实现，其中包含一个`message`属性。

**运行结果：**

当我们运行`BeanPostProcessorApplication`主程序，输出结果显示`MyServiceImpl` bean的`message`属性被成功地修改了。这是通过`MyBeanPostProcessor`实现的，该实现首先在bean初始化前加了前缀，然后在初始化后加了后缀。

#### 8.2、源码分析总结

**Spring上下文初始化**: 通过`AnnotationConfigApplicationContext`的构造函数，Spring上下文会被初始化。这个过程中，`refresh()`方法是核心。

**实例化单例Beans**: 在`refresh()`方法中，`finishBeanFactoryInitialization(beanFactory)`方法负责实例化所有剩余的非懒加载单例beans。

**预实例化**: `preInstantiateSingletons()`方法负责预实例化所有的非懒加载的单例beans。对于每一个bean, 它都会调用`getBean`方法。

**获取Bean**: `getBean()`方法负责获取bean实例，其核心是`doGetBean`方法，它会处理bean的实例化、初始化以及依赖注入。

**单例保证**: 在获取单例bean时, 如果bean已存在于单例缓存中，直接返回，否则会创建一个新的实例。

**Bean创建**: `createBean`方法调用`doCreateBean`来真正进行bean的实例化、属性填充和初始化。

**属性填充与初始化**: `doCreateBean`方法有两个主要步骤:

- `populateBean`: 属性注入
- `initializeBean`: 执行bean的初始化方法，同时调用`BeanPostProcessor`的方法。

**BeanPostProcessors回调**:

- `initializeBean`会分别调用`applyBeanPostProcessorsBeforeInitialization`和`applyBeanPostProcessorsAfterInitialization`方法，分别在bean初始化前后进行处理。
- 每个`BeanPostProcessor`都会参与上述的回调，允许对bean进行修改或增强。

**自定义处理**:

- 最后，我们的自定义`BeanPostProcessor` (`MyBeanPostProcessor`) 进行了特定的处理。在bean初始化前，为`message`添加了`Prefix:`，在bean初始化后，为`message`添加了`:Suffix`。