## EnvironmentAware

- [EnvironmentAware](#environmentaware)
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

`EnvironmentAware` 接口，允许Beans访问`Environment`对象。这是一个回调接口，当实现该接口的Bean被Spring容器管理时，Spring容器会为该Bean设置`Environment`对象。

### 二、接口源码

`EnvironmentAware` 是 Spring 框架自 3.1 开始引入的一个核心接口。主要目的是允许bean在运行时获取与其运行环境有关的信息。这包括环境属性、配置文件、激活的profile等。

```java
/**
 * 任何希望被通知其运行的Environment的bean应该实现的接口。
 * 
 * @author Chris Beams
 * @since 3.1
 * @see org.springframework.core.env.EnvironmentCapable
 */
public interface EnvironmentAware extends Aware {

    /**
     * 设置此组件运行所在的Environment。
     */
    void setEnvironment(Environment environment);
}
```

### 三、主要功能

**访问环境属性**：通过实现 `EnvironmentAware`，beans 可以直接访问应用上下文的`Environment`对象。这意味着它们可以读取环境属性，这些属性可能来自多个来源，例如系统属性、JVM参数、操作系统环境变量、属性文件等。

**识别运行时环境**：beans可以通过`Environment`对象来检查和确定当前激活的Spring profiles。这使得bean可以根据不同的运行环境（例如开发、测试、生产等）进行特定的操作或配置。

**自动回调**：当Spring容器识别到一个bean实现了`EnvironmentAware`接口时，容器会自动调用 `setEnvironment` 方法并传递当前的 `Environment` 对象。这意味着开发者不需要特意去手动设置或获取它。

**框架级别的集成**：此接口提供了一个标准机制，允许框架级别的代码（如其他Spring组件和第三方库）访问和集成`Environment`对象，而不必依赖特定的注入策略或其他机制。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyEnvironmentAware`类型的bean，最后调用`getAppProperty`方法并打印。

```java
public class EnvironmentAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEnvironmentAware environmentAware = context.getBean(MyEnvironmentAware.class);
        System.out.println("AppProperty = " + environmentAware.getAppProperty());
    }
}
```

这里使用`@Bean`注解，定义了以个Bean，是为了确保 `MyEnvironmentAware` 被 Spring 容器执行，另外使用`@PropertySource`注解从类路径下的`application.properties`文件中加载属性。这意味着我们可以在这个文件中定义属性，然后在应用中使用`Environment`对象来访问它们。

```java
@Configuration
@PropertySource("classpath:application.properties")
public class MyConfiguration {

    @Bean
    public MyEnvironmentAware myEnvironmentAware(){
        return new MyEnvironmentAware();
    }

}
```

`MyEnvironmentAware`类实现了`EnvironmentAware`接口，并重写了`setEnvironment`方法，以便在Spring容器初始化它时获取`Environment`对象。之后，我们可以使用`getPropertyValue`方法来查询`application.properties`中的任何属性。

```java
public class MyEnvironmentAware implements EnvironmentAware {

    private String appProperty;

    @Override
    public void setEnvironment(Environment environment) {
        this.appProperty = environment.getProperty("app.xcs.property");
    }

    public String getAppProperty() {
        return appProperty;
    }
}
```

运行结果发现，这个输出证明了`EnvironmentAware`接口及其与`application.properties`文件的整合成功工作，我们已经成功地使用Spring环境获取了配置属性。

```java
AppProperty = Hello from EnvironmentAware!
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: EnvironmentAware时序图
    participant EnvironmentAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyEnvironmentAware
    
    EnvironmentAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
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
    ApplicationContextAwareProcessor->>MyEnvironmentAware:setEnvironment(environment)<br>设置运行环境
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>EnvironmentAwareApplication:初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyEnvironmentAware`类型的bean，最后调用`getAppProperty`方法并打印。

```java
public class EnvironmentAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEnvironmentAware environmentAware = context.getBean(MyEnvironmentAware.class);
        System.out.println("AppProperty = " + environmentAware.getAppProperty());
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
	if (bean instanceof EnvironmentAware) {
        ((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
    }
    // ... [代码部分省略以简化]
}
```

最后执行到我们自定义的逻辑中，`MyEnvironmentAware`类实现了`EnvironmentAware`接口，并重写了`setEnvironment`方法，以便在Spring容器初始化它时获取`Environment`对象。之后，我们可以使用`getPropertyValue`方法来查询`application.properties`中的任何属性。

```java
public class MyEnvironmentAware implements EnvironmentAware {

    private String appProperty;

    @Override
    public void setEnvironment(Environment environment) {
        this.appProperty = environment.getProperty("app.xcs.property");
    }

    public String getAppProperty() {
        return appProperty;
    }
}
```

### 七、注意事项

**不要过度使用**：虽然`EnvironmentAware`为Bean提供了一个直接访问`Environment`的方法，但这并不意味着所有的Bean都应该使用它。在可能的情况下，首先考虑使用Spring的属性注入功能，例如`@Value`。

**避免使用硬编码的属性键**：当从`Environment`对象中获取属性时，尽量避免在代码中硬编码属性键。最好是将这些键作为常量或在外部配置中定义。

**处理不存在的属性**：当使用`Environment`获取属性时，如果该属性不存在，`Environment`可能会返回`null`。确保在代码中正确处理这种情况，或使用`Environment`提供的默认值方法。

**记住激活的配置文件**：`Environment`允许我们查询当前激活的配置文件(profiles)。确保我们知道哪些profiles是激活的，尤其是在使用特定于profile的属性时。

**了解Environment的层次结构**：`Environment`对象可能会从多个来源获取属性（例如系统属性、环境变量、配置文件等）。了解这些来源的优先级和加载顺序，以便正确地理解在存在冲突时哪个属性值会被使用。

### 八、总结

#### 8.1、最佳实践总结

**启动过程**：通过`EnvironmentAwareApplication`作为主入口，我们使用了`AnnotationConfigApplicationContext`来启动Spring上下文，并加载了`MyConfiguration`作为配置类。

**加载属性**：在`MyConfiguration`类中，我们使用了`@PropertySource`注解指定了从类路径下的`application.properties`文件加载属性到Spring的环境中。

**注册Bean**：在配置类`MyConfiguration`中，我们定义了一个bean `MyEnvironmentAware`。这保证了当Spring容器启动时，`MyEnvironmentAware`对象会被创建并由Spring管理。

**访问环境属性**：`MyEnvironmentAware`类实现了`EnvironmentAware`接口，这使得当Spring容器初始化该bean时，它会自动调用`setEnvironment`方法，注入当前的`Environment`对象。我们使用这个方法来读取`app.xcs.property`属性，并将其值存储在`appProperty`私有变量中。

**显示属性**：最后，在`EnvironmentAwareApplication`主程序中，我们从Spring上下文中获取了`MyEnvironmentAware` bean，并调用了`getAppProperty`方法来获取属性值，然后将其打印到控制台。

**输出**：结果显示为“AppProperty = Hello from EnvironmentAware!”，这证明了`EnvironmentAware`接口和`application.properties`文件成功地结合起来，并且我们已经成功地使用Spring环境获取了配置属性。

#### 8.2、源码分析总结

**应用启动**： 通过`EnvironmentAwareApplication`作为入口，使用`AnnotationConfigApplicationContext`来初始化Spring的上下文，并加载`MyConfiguration`作为配置类。

**属性加载**： 在`MyConfiguration`类中，利用`@PropertySource`注解，指定从`application.properties`文件加载属性到Spring环境中。

**Bean注册与初始化**： 在上下文的`refresh()`方法中，调用`finishBeanFactoryInitialization()`确保所有非懒加载的单例bean都被实例化。这个过程在`preInstantiateSingletons()`中通过循环调用`getBean()`完成，该方法将触发bean的创建、初始化及其依赖的注入。

**Bean后处理与"感知"**： 在bean的初始化过程中，`ApplicationContextAwareProcessor`负责检查并调用那些实现了Aware接口的bean的特定方法。对于实现了`EnvironmentAware`接口的beans，它会调用`setEnvironment()`方法并传入当前的`Environment`对象。

**自定义Bean的处理**： `MyEnvironmentAware`在其`setEnvironment()`方法中，从传入的`Environment`对象中获取了`app.xcs.property`属性，并存储到了它的私有变量`appProperty`中。

**应用结果输出**： 在`EnvironmentAwareApplication`的主方法中，从Spring上下文获取了`MyEnvironmentAware` bean并调用其`getAppProperty()`方法，然后将获得的属性值输出到控制台。