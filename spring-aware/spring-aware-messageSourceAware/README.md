## MessageSourceAware

- [MessageSourceAware](#messagesourceaware)
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

`MessageSourceAware` 接口，主要用于对象希望被注入`MessageSource`。`MessageSource`是Spring中用于国际化（i18n）的接口，它提供了从不同的消息资源（例如：属性文件）获取消息的方法。使用`MessageSource`，你可以为应用程序提供国际化的消息支持。

### 二、接口源码

`MessageSourceAware` 是 Spring 框架自 1.1.1 开始引入的一个核心接口。实现`MessageSourceAware`接口的对象会在Spring容器中被自动注入一个`MessageSource`实例。

```java
/**
 * 任何希望被通知运行其中的MessageSource（通常是ApplicationContext）的对象需要实现的接口。
 *
 * 注意，MessageSource通常也可以作为bean引用传递
 * （到任意的bean属性或构造函数参数），因为它在应用上下文中通常是以"name"为messageSource的bean定义的。
 * 
 * 作者: Juergen Hoeller, Chris Beams
 * 版本: 1.1.1
 * 参见: ApplicationContextAware
 */
public interface MessageSourceAware extends Aware {

    /**
     * 设置此对象运行的MessageSource。
     * 此方法在常规bean属性被填充之后调用，但在初始化回调（如InitializingBean的afterPropertiesSet或自定义的init-method）之前调用。
     * 此方法在ApplicationContextAware的setApplicationContext方法之前被调用。
     * 
     * @param messageSource 此对象要使用的消息源
     */
    void setMessageSource(MessageSource messageSource);

}
```

### 三、主要功能

**自动注入**：当一个bean实现了`MessageSourceAware`接口，并且被Spring容器管理时，Spring将会自动调用该bean的`setMessageSource`方法，传入当前应用上下文的`MessageSource`实例。

**国际化支持**：通过`MessageSourceAware`，beans可以获得对`MessageSource`的访问权，从而可以根据不同的地区和语言获取相应的消息。这对于需要显示不同语言的错误消息、UI标签或其他用户面向的文本的beans特别有用。

**简化配置**：虽然我们可以通过常规的依赖注入方法将`MessageSource`注入到beans中，但`MessageSourceAware`提供了一种更加自动化和明确的方法，特别是当我们的bean需要在初始化过程的特定阶段获得`MessageSource`时。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyMessageSourceAware`类型的bean，最后调用`getMessage`方法。

```java
public class MessageSourceAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyMessageSourceAware messageSourceAware = context.getBean(MyMessageSourceAware.class);
        messageSourceAware.getMessage();
    }
}
```

这里使用`@Bean`注解，定义了两个Bean，是为了确保 `MyMessageSourceAware` ，`MessageSource`被 Spring 容器执行。其中`ResourceBundleMessageSource` 是 Spring 框架中用于国际化（i18n）的一个具体实现。它为应用程序提供了从属性文件中读取国际化消息的能力。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyMessageSourceAware myMessageSourceAware(){
        return new MyMessageSourceAware();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        return messageSource;
    }
}
```

`MyMessageSourceAware`类使用`MessageSourceAware`接口来自动获得对`MessageSource`的引用。这个引用可以用来根据不同的语言或地区检索国际化的消息。然后利用注入的`MessageSource`，从属性文件中检索并打印两个国际化的消息，一个是英文的，另一个是简体中文的。

```java
public class MyMessageSourceAware implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void getMessage() {
        System.out.println("English："+messageSource.getMessage("greeting", null, Locale.ENGLISH));
        System.out.println("中文："+messageSource.getMessage("greeting", null, Locale.SIMPLIFIED_CHINESE));
    }
}
```

运行结果发现，`MyMessageSourceAware`类已成功从属性文件中获取了国际化消息。

```java
English：Hello!
中文：你好
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: EnvironmentAware时序图
    participant MessageSourceAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyMessageSourceAware
    
    MessageSourceAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
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
    ApplicationContextAwareProcessor->>MyMessageSourceAware:setMessageSource(messageSource)<br>设置messageSource
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>MessageSourceAwareApplication:初始化完成
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyMessageSourceAware`类型的bean，最后调用`getMessage`方法。

```java
public class MessageSourceAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyMessageSourceAware messageSourceAware = context.getBean(MyMessageSourceAware.class);
        messageSourceAware.getMessage();
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

在`org.springframework.context.support.AbstractApplicationContext#refresh`方法中，我们重点关注一下`finishBeanFactoryInitialization(beanFactory)`这方法会对实例化所有剩余非懒加载的单列Bean对象，其他方法不是本次源码阅读的重点暂时忽略。

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [代码部分省略以简化]
    
    // 步骤1. Initialize message source for this context.
	initMessageSource();
    
    // 步骤2. Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [代码部分省略以简化]
}
```

我们来到`org.springframework.context.support.AbstractApplicationContext#refresh`方法中的步骤1，在`org.springframework.context.support.AbstractApplicationContext#initMessageSource`方法中，这个方法确保Spring应用上下文总是有一个`MessageSource` bean可用，无论是否明确定义了它。如果用户没有定义，它会提供一个默认实现。这意味着在Spring上下文中，你总是可以安全地调用`getMessage()`，因为总会有一个`MessageSource`可用。

```java
protected void initMessageSource() {
    // 获取Bean工厂
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    // 检查是否已经存在名为MESSAGE_SOURCE_BEAN_NAME的bean
    if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
        // 如果存在，则获取该bean
        this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
        // 如果当前MessageSource具有层次结构并且没有设置父MessageSource
        if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
            if (hms.getParentMessageSource() == null) {
                // 设置父上下文作为父MessageSource（如果之前没有注册过父MessageSource）
                hms.setParentMessageSource(getInternalParentMessageSource());
            }
        }
        // ... [代码部分省略以简化]
    }
    else {
        // 如果不存在MESSAGE_SOURCE_BEAN_NAME的bean，则创建一个DelegatingMessageSource并注册到上下文
        // 使用DelegatingMessageSource以便能够处理getMessage调用
        DelegatingMessageSource dms = new DelegatingMessageSource();
        dms.setParentMessageSource(getInternalParentMessageSource());
        this.messageSource = dms;
        beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
        // ... [代码部分省略以简化]
    }
}
```

我们来到`org.springframework.context.support.AbstractApplicationContext#refresh`方法中的步骤2，在`org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization`方法中，会继续调用`DefaultListableBeanFactory`类中的`preInstantiateSingletons`方法来完成所有剩余非懒加载的单列Bean对象。

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
	if (bean instanceof MessageSourceAware) {
        ((MessageSourceAware) bean).setMessageSource(this.applicationContext);
    }
    // ... [代码部分省略以简化]
}
```

最后执行到我们自定义的逻辑中，`MyMessageSourceAware`类使用`MessageSourceAware`接口来自动获得对`MessageSource`的引用。这个引用可以用来根据不同的语言或地区检索国际化的消息。然后利用注入的`MessageSource`，从属性文件中检索并打印两个国际化的消息，一个是英文的，另一个是简体中文的。

```java
public class MyMessageSourceAware implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void getMessage() {
        System.out.println("English："+messageSource.getMessage("greeting", null, Locale.ENGLISH));
        System.out.println("中文："+messageSource.getMessage("greeting", null, Locale.SIMPLIFIED_CHINESE));
    }
}
```

### 七、注意事项

**明确的配置**： 确保你的Spring上下文中有一个`MessageSource` bean，通常命名为“messageSource”。虽然Spring提供了一个默认的，但为了满足自定义需求，你可能需要明确地配置它。

**生命周期时机**： `MessageSourceAware`的`setMessageSource`方法在常规属性设置之后和初始化方法（如`InitializingBean`的`afterPropertiesSet`或任何自定义的init方法）之前被调用。确保你的bean不在其生命周期的早期阶段（例如，在构造函数中）期望使用`MessageSource`。

**文件位置和命名**： 如果你使用`ResourceBundleMessageSource`或类似的机制，确保你的属性文件位于类路径上，并且与你在`MessageSource`配置中指定的basename匹配。

**编码问题**： 属性文件默认使用ISO-8859-1编码。如果你的消息包含非此编码的字符（例如中文、俄文等），确保使用Unicode转义或正确设置文件的编码。

**父子上下文**： 在使用Spring的父子上下文（例如，在Web应用中）时，子上下文可以访问父上下文中的`MessageSource`，但反之则不行。确保你在正确的上下文中配置了`MessageSource`。

**避免硬编码**： 尽量不要在代码中硬编码消息键或默认消息。最好在属性文件中管理它们，这样在未来需要更改或添加新的语言支持时，你不需要修改代码。

**默认消息**： 当使用`MessageSource`检索消息时，考虑提供一个默认消息。这可以在未找到特定消息时提供一个后备，避免抛出异常。

### 八、总结

#### 8.1、最佳实践总结

**启动类**： 在`MessageSourceAwareApplication`类中，使用了`AnnotationConfigApplicationContext`来启动Spring应用。这个上下文是专为基于Java注解的配置而设计的。启动时，它加载了`MyConfiguration`配置类，并从上下文中获取了`MyMessageSourceAware`bean，随后调用了`getMessage`方法显示消息。

**配置类**： `MyConfiguration`是一个基于Java的Spring配置类，其中定义了两个bean：`MyMessageSourceAware`和`messageSource`。`messageSource` bean是一个`ResourceBundleMessageSource`实例，用于从`i18n/messages`基本名称的属性文件中读取国际化消息。

**实现MessageSourceAware接口**： `MyMessageSourceAware`类实现了`MessageSourceAware`接口，这意味着Spring容器会自动注入一个`MessageSource`实例到这个bean中。这是通过`setMessageSource`方法完成的。

**消息检索**： 在`MyMessageSourceAware`的`getMessage`方法中，使用了注入的`MessageSource`来检索和打印两种语言的国际化消息：英文和简体中文。

**运行结果**： 当应用程序执行时，它成功地从对应的属性文件中获取并显示了英文和简体中文的国际化消息。

#### 8.2、源码分析总结

**应用启动**：你从`MessageSourceAwareApplication`启动应用，使用`AnnotationConfigApplicationContext`初始化Spring容器，并加载`MyConfiguration`配置。

**容器初始化**：在`AnnotationConfigApplicationContext`的构造函数中，执行了`register`和`refresh`方法，其中`refresh`是最重要的，它触发了容器的初始化和bean的创建过程。

**消息源初始化**：在容器刷新的`refresh`方法中，首先确保了一个`MessageSource` bean存在，这是通过`initMessageSource`方法完成的。如果没有明确定义`MessageSource` bean，Spring会提供一个默认实现，确保应用上下文总是有一个可用。

**bean实例化**：随后，在`refresh`方法中，通过调用`finishBeanFactoryInitialization`方法，容器开始实例化所有非延迟加载的单例bean。

**Bean的生命周期**：在bean的创建过程中，Spring容器会确保所有的生命周期回调都被正确地执行，其中最重要的是`BeanPostProcessors`。这些处理器提供了一个插件机制，允许我们在bean的初始化前后执行自定义的逻辑。

**处理Aware接口**：`ApplicationContextAwareProcessor`是一个特殊的`BeanPostProcessor`，它关心那些实现了"Aware"接口的beans。对于实现了`MessageSourceAware`的beans，该处理器会自动注入应用上下文的`MessageSource`。

**消息检索**：在你的`MyMessageSourceAware`类中，已经成功地获取了`MessageSource`的引用。然后，你调用其`getMessage`方法，从属性文件中检索并打印两个国际化的消息。