## `IOC`

- [`IOC`](#ioc)
  - [一、接口描述](#一接口描述)
  - [二、IOC源码](#二ioc源码)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
    - [5.1、Bean定义注册过程时序图](#51bean定义注册过程时序图)
    - [5.2、Bean初始化过程时序图](#52bean初始化过程时序图)
    - [5.3、Bean销毁过程时序图](#53bean销毁过程时序图)
  - [六、源码分析](#六源码分析)
    - [6.1、Bean定义注册过程源码分析](#61bean定义注册过程源码分析)
    - [6.2、Bean初始化过程源码分析](#62bean初始化过程源码分析)
    - [6.3、Bean销毁过程源码分析](#63bean销毁过程源码分析)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [8.1、最佳实践总结](#81最佳实践总结)
    - [8.2、源码分析总结](#82源码分析总结)


### 一、接口描述

Spring IOC（Inversion of Control）容器是Spring框架的核心部分。它负责创建、配置、装配和管理应用中的对象及其生命周期。

### 二、IOC源码

由于IOC是框架的核心部分，其源码是非常庞大和复杂的。在这里，可以为大家提供一个关于IOC部分源码的概述，但要理解每个细节，你可能需要花费相当的时间去阅读和理解。Spring框架的源码是开源的，你可以在其GitHub仓库中找到。

### 三、主要功能

+ **依赖管理**：Spring IOC容器负责自动注入对象的依赖。这意味着我们不需要手动地在对象内部创建或查找其依赖项；相反，Spring容器会为我们注入它们。这有助于实现解耦和更好的模块化。提供多种注入方式，例如构造器注入、Setter方法注入和字段注入。

+ **Bean生命周期管理**：从bean的创建、初始化到销毁，Spring IOC容器都会管理其整个生命周期。可以使用初始化方法、销毁方法、`InitializingBean`和`DisposableBean`接口等方式来控制bean的生命周期。

+ **作用域管理**：Spring IOC容器支持多种bean的作用域，如单例（singleton）、原型（prototype）、会话（session）和请求（request）等。

+ **AOP与IOC的集成**：Spring的AOP（面向切面编程）功能与IOC容器紧密集成，允许你为bean定义切面、通知和切点，实现横切关注点的模块化。

+ **提供扩展点**：通过`BeanPostProcessor`、`BeanFactoryPostProcessor`等扩展点，可以在bean初始化前后插入自定义逻辑

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。通过`getBeanDefinitionNames`方法，可以获得容器中所有bean的名称，使用`getBean`方法获取其实例，并打印该Bean，最后关闭了容器来触发单例bean的销毁方法。

```java
public class IOCApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("Bean = " + context.getBean(beanDefinitionName));
        }
        context.close();
    }
}
```

在配置类中，使用`@ComponentScan`注解让Spring扫描`com.xcs.spring.service`包以及其子包，由此扫描到的任何类，如果它们上面有特定的注解（如`@Component`, `@Service`, `@Repository`, `@Controller`等），都会被Spring自动识别并添加到容器中，成为容器管理的bean。

```java
@Configuration
@ComponentScan("com.xcs.spring.service")
public class MyConfiguration {

}
```

由于我们配置中启用了`@ComponentScan`（如在`MyConfiguration`类中）并指定了正确的包路径，那么这两个类将被自动识别并注册到Spring容器中。

```java
package com.xcs.spring.service;

@Component
public class MyServiceB {

}

@Component
public class MyServiceB {

}
```

运行结果发现，这是我们自己定义的两个服务类。它们都被标记为`@Component`，因此Spring容器会为每个类创建一个bean实例（其他的Bean是Spring内部Bean）。

```java
Bean = org.springframework.context.annotation.ConfigurationClassPostProcessor@23c30a20
Bean = org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor@1e1a0406
Bean = org.springframework.context.event.EventListenerMethodProcessor@3cebbb30
Bean = org.springframework.context.event.DefaultEventListenerFactory@12aba8be
Bean = com.xcs.spring.config.MyConfiguration$$EnhancerBySpringCGLIB$$7a35e03c@290222c1
Bean = com.xcs.spring.service.MyServiceA@67f639d3
Bean = com.xcs.spring.service.MyServiceB@6253c26
```

### 五、时序图

#### 5.1、Bean定义注册过程时序图

~~~mermaid
sequenceDiagram
    Title: Bean注册过程时序图
    participant IOCApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant PostProcessorRegistrationDelegate
    participant ConfigurationClassPostProcessor
    participant ConfigurationClassParser
    participant ComponentScanAnnotationParser
    participant ClassPathBeanDefinitionScanner
    participant BeanDefinitionReaderUtils
    participant DefaultListableBeanFactory
    
    IOCApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建应用上下文
	AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新应用上下文
	AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)<br>调用BFPP方法
	AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>委托BFPP处理
    PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup)<br>调用BDRPP方法
    PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor:postProcessBeanDefinitionRegistry(registry)<br>处理Bean定义
    ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor:processConfigBeanDefinitions(registry)<br>处理配置类Bean
    ConfigurationClassPostProcessor->>ConfigurationClassParser:parse(configCandidates)<br>解析配置类
    ConfigurationClassParser->>ConfigurationClassParser:parse(metadata,beanName)<br>解析指定类
    ConfigurationClassParser->>ConfigurationClassParser:processConfigurationClass(configClass,filter)<br>处理配置注解
	ConfigurationClassParser->>+ConfigurationClassParser: doProcessConfigurationClass(configClass, sourceClass, filter)<br>进一步处理配置
    ConfigurationClassParser-->>-ConfigurationClassParser: 返回SourceClass
    ConfigurationClassParser->>ComponentScanAnnotationParser:parse(componentScan,declaringClass)<br>解析@ComponentScan
    ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner:doScan(basePackages)<br>扫描指定包路径
    ClassPathBeanDefinitionScanner->>+ClassPathBeanDefinitionScanner:findCandidateComponents(basePackage)<br>找到Bean候选者
    ClassPathBeanDefinitionScanner-->>-ClassPathBeanDefinitionScanner:返回Set<BeanDefinition>
    ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerBeanDefinition(definitionHolder,registry)<br>注册Bean定义
    ClassPathBeanDefinitionScanner->>BeanDefinitionReaderUtils:registerBeanDefinition(definitionHolder,registry)<br>工具类注册Bean
    BeanDefinitionReaderUtils->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)<br>最终注册Bean
    ComponentScanAnnotationParser-->>ConfigurationClassParser:返回Set<BeanDefinitionHolder>

~~~

#### 5.2、Bean初始化过程时序图

~~~mermaid
sequenceDiagram
    Title: Bean初始化过程时序图
    participant IOCApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant InstantiationAwareBeanPostProcessor
    participant SmartInstantiationAwareBeanPostProcessor
    participant MergedBeanDefinitionPostProcessor
    participant BeanPostProcessor
    participant InitializingBean
    participant Bean对象
    
    IOCApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建应用上下文
	AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新应用上下文
	AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>完成bean工厂初始化
	AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>预实例化单例beans
	DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>获取bean实例
	AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>具体获取bean逻辑
	AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>从注册表获取单例bean
	DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>从工厂获取bean对象
	AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>创建bean实例
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:resolveBeforeInstantiation(beanName, mbdToUse)<br>解析实例化前的bean
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInstantiation(targetType, beanName)<br>应用BP前实例化
	AbstractAutowireCapableBeanFactory->>InstantiationAwareBeanPostProcessor:postProcessBeforeInstantiation(beanClass, beanName)<br>实例化前处理bean
	InstantiationAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory:返回要使用的bean对象
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>执行创建bean逻辑
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:createBeanInstance(beanName, mbd, args)<br>创建bean实例
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:determineConstructorsFromBeanPostProcessors(beanClass, beanName)<br>确定构造器
	AbstractAutowireCapableBeanFactory->>SmartInstantiationAwareBeanPostProcessor:determineCandidateConstructors(beanClass, beanName)<br>确定候选构造器
	SmartInstantiationAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory:返回构造器
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>应用合并的BDPP
	AbstractAutowireCapableBeanFactory->>MergedBeanDefinitionPostProcessor:postProcessMergedBeanDefinition(mbd,beanType,beanName)<br>处理合并的bean定义
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:addSingletonFactory(beanName,singletonFactory)<br>添加单例工厂
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:getEarlyBeanReference(beanName,mbd,bean)<br>获取早期bean引用
	AbstractAutowireCapableBeanFactory->>SmartInstantiationAwareBeanPostProcessor:getEarlyBeanReference(exposedObject, beanName)<br>获取早期bean引用
	SmartInstantiationAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory:返回bean引用公开的对象
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,instanceWrapper)<br>填充bean属性
	AbstractAutowireCapableBeanFactory->>InstantiationAwareBeanPostProcessor:postProcessAfterInstantiation(bean,beanName)<br>实例化后处理bean
	InstantiationAwareBeanPostProcessor->>AbstractAutowireCapableBeanFactory:返回是否属性填充
	AbstractAutowireCapableBeanFactory->>InstantiationAwareBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>处理bean属性
	InstantiationAwareBeanPostProcessor->>AbstractAutowireCapableBeanFactory:返回属性值
	AbstractAutowireCapableBeanFactory->>InstantiationAwareBeanPostProcessor:postProcessPropertyValues(pds,bean,beanName)<br>处理bean属性
	InstantiationAwareBeanPostProcessor->>AbstractAutowireCapableBeanFactory:返回属性值
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName,bean,mbd)<br>开始初始化bean
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)<br>
	AbstractAutowireCapableBeanFactory->>BeanPostProcessor:postProcessBeforeInitialization(bean,beanName)<br>应用初始化前的BPP
	BeanPostProcessor-->>AbstractAutowireCapableBeanFactory:返回Bean对象
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeInitMethods(beanName,bean,mbd)<br>调用初始化方法
	AbstractAutowireCapableBeanFactory->>InitializingBean:afterPropertiesSet()<br>设置bean属性后
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeCustomInitMethod(beanName,bean,mbd)<br>调用自定义初始化
	AbstractAutowireCapableBeanFactory->>Bean对象:init-method<br>执行bean的初始化方法
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsAfterInitialization(existingBean,beanName)<br>应用初始化后的BPP
	AbstractAutowireCapableBeanFactory->>BeanPostProcessor:postProcessAfterInitialization(bean,beanName)<br>处理初始化后的bean
	BeanPostProcessor-->>AbstractAutowireCapableBeanFactory:返回Bean对象
	AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
	AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
~~~

#### 5.3、Bean销毁过程时序图

~~~mermaid
sequenceDiagram
    Title: Bean销毁过程时序图
    participant IOCApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant DefaultSingletonBeanRegistry
    participant DisposableBeanAdapter
    participant DestructionAwareBeanPostProcessor
    participant DisposableBean
    participant Bean对象
    
    IOCApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses	)<br>应用开始初始化上下文
    AnnotationConfigApplicationContext-->>IOCApplication:初始化完成
    IOCApplication->>AbstractApplicationContext:close()<br>请求关闭上下文
    AbstractApplicationContext->>AbstractApplicationContext:doClose()<br>执行关闭逻辑
    AbstractApplicationContext->>AbstractApplicationContext:destroyBeans()<br>开始销毁beans
    AbstractApplicationContext->>DefaultListableBeanFactory:destroySingletons()<br>销毁单例beans
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingletons()<br>调父类销毁方法
    DefaultSingletonBeanRegistry-->>DefaultListableBeanFactory:destroySingleton(beanName)<br>销毁单个bean
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingleton(beanName)<br>调父类销毁bean方法
    DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:destroyBean(beanName,bean)<br>执行销毁bean操作
    DefaultSingletonBeanRegistry->>DisposableBeanAdapter:destroy()<br>适配器执行销毁
    DisposableBeanAdapter->>DestructionAwareBeanPostProcessor:postProcessBeforeDestruction(bean,beanName)<br>执行自定义销毁逻辑
    DisposableBeanAdapter->>DisposableBean:destroy()<br>执行自定义销毁逻辑
	DisposableBeanAdapter->>DisposableBeanAdapter:invokeCustomDestroyMethod(destroyMethod)<br>调用自定义销毁方法
	DisposableBeanAdapter->>Bean对象:destroy-method<br>执行bean的销毁方法
    AbstractApplicationContext-->>IOCApplication:请求关闭上下文结束
~~~

### 六、源码分析

#### 6.1、Bean定义注册过程源码分析

+ 关于@ComponentScan源码分析

#### 6.2、Bean初始化过程源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。通过`getBeanDefinitionNames`方法，可以获得容器中所有bean的名称，使用`getBean`方法获取其实例，并打印该Bean，最后关闭了容器来触发单例bean的销毁方法。

```java
public class IOCApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("Bean = " + context.getBean(beanDefinitionName));
        }
        context.close();
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

`org.springframework.context.support.AbstractApplicationContext#refresh`方法中我们重点关注一下`finishBeanFactoryInitialization(beanFactory)`这方法会对实例化所有剩余非懒加载的单列Bean对象，其他方法不是本次源码阅读的重点暂时忽略。

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

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons`方法中，对于每个bean，它检查bean是否应该被初始化，即它不是抽象的、是单例的，并且不是延迟加载的，如果bean是一个`FactoryBean`，则检查该`FactoryBean`是否应该提前初始化。如果是，它会执行提前初始化。如果bean不是`FactoryBean`，则直接进行初始化。在所有的beans被初始化之后，方法查找那些实现了`SmartInitializingSingleton`接口的beans。对于每一个这样的bean，它会调用`afterSingletonsInstantiated`方法，允许beans在所有其他的beans被实例化之后执行某些操作。

```java
/**
 * 提前实例化所有非懒加载的单例bean，确保所有的单例bean都被初始化。
 * 1. 该方法首先遍历所有已注册的bean定义的名称。
 * 2. 对于每个bean名称，它获取合并的本地bean定义，并检查bean是否是抽象的、单例的以及非懒加载的。
 * 3. 如果满足上述条件，该方法将根据bean定义是不是一个工厂bean（FactoryBean）来决定如何获取bean实例。
 * 4. 之后，对于所有已创建的单例bean，如果它们实现了SmartInitializingSingleton接口，它们的afterSingletonsInstantiated方法将被调用。
 * 
 * 此方法在应用上下文的刷新过程中被调用，确保所有非懒加载的单例bean在上下文被刷新后都被创建和初始化。
 * 
 * @throws BeansException 如果任何bean不能被实例化或初始化。
 */
@Override
public void preInstantiateSingletons() throws BeansException {
    // 当TRACE日志级别被启用时，记录方法的开始日志。
    if (logger.isTraceEnabled()) {
        logger.trace("Pre-instantiating singletons in " + this);
    }

    // 获取beanDefinitionNames的副本。这样做是为了防止在迭代期间对其进行修改。
    List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

    // 第一步：初始化所有的非延迟加载的单例beans。
    for (String beanName : beanNames) {
        RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
        
        // 检查bean是否满足初始化条件：它不应该是抽象的，应该是一个单例，并且不是延迟加载。
        if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
            
            // 检查bean是否是一个FactoryBean。
            if (isFactoryBean(beanName)) {
                Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
                
                // 如果bean是FactoryBean的实例，进一步检查它是否应该被提前初始化。
                if (bean instanceof FactoryBean) {
                    FactoryBean<?> factory = (FactoryBean<?>) bean;
                    boolean isEagerInit;
                    
                    // ... [代码部分省略以简化]
                    
                    // 如果工厂bean被标记为“eagerInit”，则立即初始化它。
                    if (isEagerInit) {
                        getBean(beanName);
                    }
                }
            } else {
                // 如果bean不是FactoryBean，则直接初始化它。
                getBean(beanName);
            }
        }
    }

    // 第二步：对所有的单例beans执行后初始化回调。
    for (String beanName : beanNames) {
        Object singletonInstance = getSingleton(beanName);
        
        // 如果bean实现了SmartInitializingSingleton接口，则调用afterSingletonsInstantiated方法。
        if (singletonInstance instanceof SmartInitializingSingleton) {
            
            // ... [代码部分省略以简化]
            
            // 调用afterSingletonsInstantiated方法
            smartSingleton.afterSingletonsInstantiated();
        }
    }
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#getBean`方法中，又调用了`doGetBean`方法来实际执行创建Bean的过程，传递给它bean的名称和一些其他默认的参数值。此处，`doGetBean`负责大部分工作，如查找bean定义、创建bean（如果尚未创建）、处理依赖关系等。

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中，首先尝试从单例缓存中获取已存在的bean。如果不存在，考虑原型循环引用并在必要时检查父容器。方法然后根据bean的作用域和定义合并其属性，处理其依赖关系，根据bean的作用域创建bean实例。若在创建过程中遇到错误，它执行清理并抛出异常。最终，返回适当的bean实例或其代理。

```java
/**
 * 根据给定的bean名称，尝试获取或创建bean。这是bean获取过程的核心方法。
 * 1. 如果bean存在于单例缓存中并且没有构造函数参数，它将直接返回该单例。
 * 2. 对于原型bean，此方法将创建一个新实例。
 * 3. 对于其他作用域的bean，它会根据作用域特定的策略创建或检索bean。
 * 4. 如果在父bean工厂中找到bean定义，它将在父工厂中尝试获取bean。
 * 5. 在创建bean之前，此方法还会处理bean的依赖关系，确保所有依赖关系首先被解析和创建。
 *
 * @param name 要检索的bean的名称。
 * @param requiredType 返回对象必须匹配的类型；可以为null以获取任何类型的bean。
 * @param args 创建bean实例时使用的参数；如果为null，则使用默认构造函数。
 * @param typeCheckOnly 是否只进行类型检查，而不是创建bean。
 * @return 返回创建或检索的bean对象。
 * @throws BeansException 如果bean不能被创建或检索。
 */
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {

    // 1. 获取标准化的bean名称。
    String beanName = transformedBeanName(name);
    Object beanInstance;

    // 2. 尝试从单例缓存中获取bean。
    Object sharedInstance = getSingleton(beanName);
    if (sharedInstance != null && args == null) {
        // ... [代码部分省略以简化]
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, null);
    }

    else {
        // 3. 检查原型作用域bean的循环引用。
        if (isPrototypeCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }

        // 4. 检查当前工厂或父工厂中是否存在bean定义。
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            // ... [代码部分省略以简化]
        }

        // 5. 标记bean为已创建。
        if (!typeCheckOnly) {
            markBeanAsCreated(beanName);
        }

        try {
            // 6. 获取合并的bean定义。
            RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            checkMergedBeanDefinition(mbd, beanName, args);

            // 7. 保证当前bean所依赖的beans先被初始化。
            String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                // ... [代码部分省略以简化]
            }

            // 8. 根据bean的作用域创建bean实例。
            if (mbd.isSingleton()) {
                // 为单例作用域创建或检索实例。
                sharedInstance = getSingleton(beanName, () -> createBean(beanName, mbd, args));
                beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
            }
            else if (mbd.isPrototype()) {
                // 为原型作用域创建新实例。
                beanInstance = getObjectForBeanInstance(createBean(beanName, mbd, args), name, beanName, mbd);
            }
            else {
                // 为其他自定义作用域创建实例。
                String scopeName = mbd.getScope();
                Scope scope = this.scopes.get(scopeName);
                beanInstance = getObjectForBeanInstance(scope.get(beanName, () -> createBean(beanName, mbd, args)), name, beanName, mbd);
            }
        }
        catch (BeansException ex) {
            // 9. 如果创建bean失败，进行清理并重新抛出异常。
            cleanupAfterBeanCreationFailure(beanName);
            throw ex;
        }
    }

    // 10. 返回适应的bean实例或其代理。
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton`方法中，方法将请求转发给另一个重载版本的 `getSingleton` 方法。

```java
/**
 * 根据提供的bean名称从单例缓存中检索bean实例。
 *
 * @param beanName 要检索的bean的名称
 * @return bean的实例，如果找不到或不是单例，则返回null
 */
@Override
@Nullable
public Object getSingleton(String beanName) {
    return getSingleton(beanName, true);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName, allowEarlyReference)`方法中，先从单例缓存中检索指定名称的bean实例。当bean处于创建过程中，可能因为循环依赖而没有完成初始化。为了处理这样的场景，方法首先尝试从常规的单例缓存中获取bean，如果没有找到且该bean当前正在创建中，它会尝试从“早期单例缓存”中获取。如果还是没有找到并且允许提前引用，方法会使用单例工厂创建一个新的bean实例。这种机制允许Spring容器在bean完全初始化之前解决循环依赖的问题。

```java
/**
 * 根据给定的 bean 名称从单例缓存中检索 bean 实例。
 * 
 * @param beanName 要检索的 bean 的名称
 * @param allowEarlyReference 如果为 true, 该方法会尝试在完整初始化之前提前解析单例bean，这主要用于处理循环引用
 * @return 返回该名称对应的单例bean对象，如果没有找到则返回 null
 */
@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // 从缓存中快速获取单例对象，这是一个常见的检索操作，避免了不必要的同步
    Object singletonObject = this.singletonObjects.get(beanName);
    
    // 如果单例对象为 null 且当前bean正在创建中，则可能存在循环引用
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        singletonObject = this.earlySingletonObjects.get(beanName);
        if (singletonObject == null && allowEarlyReference) {
            // 使用完整的单例锁来确保单例的一致性创建和缓存
            synchronized (this.singletonObjects) {
                singletonObject = this.singletonObjects.get(beanName);
                if (singletonObject == null) {
                    // 再次尝试从早期单例缓存中获取
                    singletonObject = this.earlySingletonObjects.get(beanName);
                    if (singletonObject == null) {
                        // 使用单例工厂创建对象。这是为了处理循环引用。
                        ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                        if (singletonFactory != null) {
                            singletonObject = singletonFactory.getObject();
                            // 存储到早期单例缓存中并从工厂缓存中移除
                            this.earlySingletonObjects.put(beanName, singletonObject);
                            this.singletonFactories.remove(beanName);
                        }
                    }
                }
            }
        }
    }
    
    return singletonObject;
}
```

我们回到`doGetBean`方法的第二步中，在尝试从单例缓存中成功获取到Bean后，会调用`beanorg.springframework.beans.factory.support.AbstractBeanFactory#getObjectForBeanInstance`方法，此方法主要用于从给定的bean实例中获取相应的对象。这可能涉及处理`FactoryBean`的逻辑，返回由`FactoryBean`创建的对象，或者直接返回传入的普通bean实例。PS：如果第二步中没有获取到Bean，此方法在`doGetBean`方法中的第八步会再次调用，确保能处理`FactoryBean`的逻辑。

```java
/**
 * 获取给定bean实例的相应对象。此方法处理可能的FactoryBean场景，并返回要使用的实际bean实例。
 *
 * @param beanInstance 实际的bean实例，可以是常规bean或FactoryBean
 * @param name 要查询的bean的名称
 * @param beanName bean的实际名称
 * @param mbd 可以为null的合并bean定义
 * @return 从FactoryBean或普通bean实例获取的对象
 * 
 * @throws BeanIsNotAFactoryException 如果给定名称的bean不是工厂，但尝试解引用
 */
protected Object getObjectForBeanInstance(
        Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

    // 如果调用者尝试解引用工厂并且bean实例不是工厂，抛出异常。
    if (BeanFactoryUtils.isFactoryDereference(name)) {
        if (beanInstance instanceof NullBean) {
            return beanInstance;
        }
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
        }
        if (mbd != null) {
            mbd.isFactoryBean = true;
        }
        return beanInstance;
    }

    // 如果bean实例不是FactoryBean，直接返回。
    if (!(beanInstance instanceof FactoryBean)) {
        return beanInstance;
    }

    // 如果FactoryBean的结果在缓存中，直接返回。
    Object object = null;
    if (mbd != null) {
        mbd.isFactoryBean = true;
    }
    else {
        object = getCachedObjectForFactoryBean(beanName);
    }

    // 如果缓存中没有，从FactoryBean获取对象。
    if (object == null) {
        FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
        if (mbd == null && containsBeanDefinition(beanName)) {
            mbd = getMergedLocalBeanDefinition(beanName);
        }
        boolean synthetic = (mbd != null && mbd.isSynthetic());
        object = getObjectFromFactoryBean(factory, beanName, !synthetic);
    }

    return object;
}
```

我们回到`doGetBean`方法中的第八步。在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton`方法中，首先从单例缓存中检索指定的bean，如果该bean不存在并且提供了一个`singletonFactory`，该方法会尝试使用该工厂创建bean。如果在此过程中发生任何异常或在创建期间bean被其他线程提前创建，该方法都有相应的处理策略。特别地，方法还考虑到了循环引用的情况，并在出现循环引用时提供了有关错误信息。简而言之，`getSingleton`确保了单例bean的创建、缓存和提取，同时处理了与并发和循环引用相关的各种潜在问题。

```java
/**
 * 根据给定的bean名称获取单例对象。如果在单例缓存中不存在该对象，使用传入的单例工厂创建它。
 * 该方法确保线程安全地创建和检索单例，并处理与bean创建失败相关的任何异常。
 * 
 * @param beanName 要检索的单例bean的名称。
 * @param singletonFactory 创建新单例时使用的工厂。
 * @return 返回创建或检索的单例对象。
 * @throws BeanCreationNotAllowedException 当单例处于销毁状态时，不允许创建单例。
 * @throws BeanCreationException 如果创建bean时出现问题。
 * @throws IllegalStateException 如果出现状态异常，如并发问题。
 */
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // 1. 断言bean名称不为空
    Assert.notNull(beanName, "Bean name must not be null");

    // 2. 通过同步块确保线程安全
    synchronized (this.singletonObjects) {

        // 3. 尝试从缓存中获取单例对象
        Object singletonObject = this.singletonObjects.get(beanName);

        // 4. 如果缓存中不存在
        if (singletonObject == null) {

            // 5. 如果当前处于销毁其他单例的状态，则抛出异常
            if (this.singletonsCurrentlyInDestruction) {
                // ... [代码部分省略以简化]
            }

            // 6. 记录开始创建单例的日志
            if (logger.isDebugEnabled()) {
                logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
            }

            // 7. 将单例注册为当前正在创建中
            beforeSingletonCreation(beanName);

            // 8. 设置标识，默认为未创建新的单例
            boolean newSingleton = false;

            // 9. 准备记录与bean创建失败相关的任何异常
            boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
            if (recordSuppressedExceptions) {
                this.suppressedExceptions = new LinkedHashSet<>();
            }

            try {
                // 10. 使用传递的singletonFactory创建单例
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
                // 11. 将singleton标记为不再处于创建中
                afterSingletonCreation(beanName);
            }

            // 12. 如果成功创建新的单例，将其加入缓存
            if (newSingleton) {
                addSingleton(beanName, singletonObject);
            }
        }
        // 13. 返回创建或检索到的单例
        return singletonObject;
    }
}
```

我们来到`getSingleton(beanName,singletonFactory)`方法中的第七步中。调用`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#beforeSingletonCreation`方法，将单例注册为当前正在创建中。

```java
protected void beforeSingletonCreation(String beanName) {
    if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
        throw new BeanCurrentlyInCreationException(beanName);
    }
}
```

我们来到`getSingleton(beanName,singletonFactory)`方法中的第十步中，调用`singletonFactory.getObject()`，这是在`doGetBean`方法中传递过来的lambda表达式的一个方法引用。

```java
() -> {
    try {
        return createBean(beanName, mbd, args);
    }
    catch (BeansException ex) {
        // ... [代码部分省略以简化]
    }
}
```

我们来到`getSingleton(beanName,singletonFactory)`方法中的第十一步中。调用`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#afterSingletonCreation`方法，将singleton标记为不再处于创建中。

```java
protected void afterSingletonCreation(String beanName) {
    if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
        throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
    }
}
```

我们回到`getSingleton(beanName,singletonFactory)`方法中的第十步中。当我们调用`singletonFactory.getObject()`方法会调用到`createBean(beanName,mbd,args)`方法上来，在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean`方法中，首先，确保了bean的类已经被正确解析并设置。接着，它会准备并验证方法重写，确保没有配置错误。然后，方法尝试使用`BeanPostProcessors`在实际实例化bean之前返回一个代理对象。如果没有代理返回，方法将继续实际的bean创建和初始化过程。整个过程中，任何发生的异常都会被捕获并转化为`BeanCreationException`，确保调用者可以从异常中获取明确的上下文信息。

```java
/**
 * 创建一个新的bean实例。此过程涉及解析bean的类，处理方法重写，可能的代理创建以及最终bean的实际实例化和初始化。
 *
 * @param beanName 要创建的bean的名称。
 * @param mbd 要创建的bean的合并bean定义。
 * @param args 用于构造函数或工厂方法的参数；可以为null。
 * @return 新创建的bean实例。
 * @throws BeanCreationException 如果创建bean失败。
 */
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {
    // 1.记录日志：开始创建bean
    if (logger.isTraceEnabled()) {
        logger.trace("Creating instance of bean '" + beanName + "'");
    }
    RootBeanDefinition mbdToUse = mbd;

    // 2.解析bean的类，以确保其已经被加载
    Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
    if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
        // 如果bean类已经被解析，并且原始bean定义没有设置bean类，则复制bean定义并设置bean类。
        mbdToUse = new RootBeanDefinition(mbd);
        mbdToUse.setBeanClass(resolvedClass);
    }

    // 3.准备方法重写，并对它们进行验证
    try {
        mbdToUse.prepareMethodOverrides();
    }
    catch (BeanDefinitionValidationException ex) {
        // ... [代码部分省略以简化]
    }
    
    try {
        // 4.允许BeanPostProcessors在bean实例化之前返回代理
        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    // 5.实际bean实例化。如果此过程中出现异常，异常将被捕获并适当处理。
    try {
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
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

我们来到`createBean(beanName,mbd,args)`方法中的第四步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation` 方法中，首先尝试在bean实际实例化之前提前完成bean的实例化。这通常是为了返回一个代理对象。`applyBeanPostProcessorsBeforeInstantiation` 方法，尝试使用 `InstantiationAwareBeanPostProcessor` 的 `postProcessBeforeInstantiation` 方法来预先实例化bean。如果上一步成功创建了bean（例如，返回了一个代理对象），那么这个bean还会经过所有注册的 `BeanPostProcessor` 的 `postProcessAfterInitialization` 方法，这是对bean进行初始化后的最后处理。

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

我们来到`createBean(beanName,mbd,args)`方法中的第五步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，首先负责bean的实例化，然后进行类型的解析和方法覆盖的准备。在实例化bean后，该方法允许`BeanPostProcessors`介入，为bean提供代理或进行其他改变。接下来，为解决可能出现的循环引用问题，bean会被尽早地放入缓存。之后，该方法进行bean的属性注入，并执行其初始化方法。在处理完成bean的循环引用后，如果bean定义了销毁方法或实现了相关接口，它将被注册为可销毁的。最后，方法返回完全初始化的bean实例。

```java
/**
 * 实际创建指定的bean。先实例化，然后暴露为对象引用。
 *
 * @param beanName bean的名称
 * @param mbd bean的合并bean定义
 * @param args 用于构造函数或工厂方法调用的显式参数
 * @return 创建的bean实例(可能是原始的或其创建的代理)
 * @throws BeanCreationException 如果bean不能被创建
 */
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // 1. Bean实例化
    BeanWrapper instanceWrapper = null;
    // 如果是单例并且缓存中有，则从缓存中获取BeanWrapper
    if (mbd.isSingleton()) {
        instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
    }
    // 如果BeanWrapper为空，则创建一个新的实例
    if (instanceWrapper == null) {
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    // 获取bean的实例和类型
    Object bean = instanceWrapper.getWrappedInstance();
    Class<?> beanType = instanceWrapper.getWrappedClass();
    if (beanType != NullBean.class) {
        mbd.resolvedTargetType = beanType;
    }

    // 2. 合并Bean定义后的后处理
    synchronized (mbd.postProcessingLock) {
        if (!mbd.postProcessed) {
            try {
                applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
            } catch (Throwable ex) {
                // ... [代码部分省略以简化]
            }
            mbd.postProcessed = true;
        }
    }

    // 3. 为了处理循环引用，提前将创建的实例放入缓存
    boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
            isSingletonCurrentlyInCreation(beanName));
    if (earlySingletonExposure) {
        addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
    }

    Object exposedObject = bean;
    try {
        // 4. Bean属性的注入
        populateBean(beanName, mbd, instanceWrapper);
        // 5. Bean初始化
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    } catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    // 6. 循环引用处理
    if (earlySingletonExposure) {
        Object earlySingletonReference = getSingleton(beanName, false);
        if (earlySingletonReference != null && exposedObject == bean) {
            exposedObject = earlySingletonReference;
        }
        // ... [代码部分省略以简化]
    }

    // 7. 如果需要，为bean注册销毁方法
    try {
        registerDisposableBeanIfNecessary(beanName, bean, mbd);
    } catch (BeanDefinitionValidationException ex) {
        // ... [代码部分省略以简化]
    }

    return exposedObject;
}
```

我们来到`doCreateBean(beanName,mbd,args)`方法中的第二步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`方法中，遍历每一个 `MergedBeanDefinitionPostProcessor` 的 `postProcessMergedBeanDefinition` 方法，提供了一个自定义或查询合并的 bean 定义的机会。

```java
protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
    for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
        processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
    }
}
```

我们来到`doCreateBean(beanName,mbd,args)`方法中的第四步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean`方法中，首先确定bean实例是否存在。如果bean存在，该方法会根据bean的自动装配模式（例如`byName`或`byType`）来进行属性的自动装配。此外，对于任何显式定义的属性值，该方法也会确保它们被正确地设置到bean中。在整个属性填充过程中，方法为`InstantiationAwareBeanPostProcessors`提供了在不同阶段介入、修改bean属性或中断属性填充的机会。简而言之，这个方法确保bean的所有属性都被正确地配置和填充。

```java
/**
 * 用于填充给定bean名称的bean实例的属性。此方法负责属性的自动装配以及应用显式属性值。
 * 
 * @param beanName bean的名称
 * @param mbd bean的合并BeanDefinition
 * @param bw bean的BeanWrapper
 */
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {

    // 如果BeanWrapper为空并且存在属性值，则抛出BeanCreationException异常。
    if (bw == null) {
        if (mbd.hasPropertyValues()) {
            throw new BeanCreationException(
                mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
        } else {
            // 若实例为null且没有属性需要设置，则直接返回。
            return;
        }
    }

    // 在设置bean属性之前，给任何InstantiationAwareBeanPostProcessors一个机会来修改bean的状态。
    // 这可以用来支持字段注入的风格。
    if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                return;
            }
        }
    }

    // 获取bean定义中的属性值。
    PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

    // 根据bean定义的自动装配模式进行处理。
    int resolvedAutowireMode = mbd.getResolvedAutowireMode();
    if (resolvedAutowireMode == AUTOWIRE_BY_NAME || resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
        MutablePropertyValues newPvs = new MutablePropertyValues(pvs);

        // 如果是按名称自动装配，将对应的属性值加入属性集。
        if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
            autowireByName(beanName, mbd, bw, newPvs);
        }
        // 如果是按类型自动装配，将对应的属性值加入属性集。
        if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
            autowireByType(beanName, mbd, bw, newPvs);
        }

        pvs = newPvs;
    }

    // 检查是否有InstantiationAwareBeanPostProcessors，并对属性值进行后处理。
    boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
    boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

    PropertyDescriptor[] filteredPds = null;
    if (hasInstAwareBpps) {
        if (pvs == null) {
            pvs = mbd.getPropertyValues();
        }
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
            if (pvsToUse == null) {
                if (filteredPds == null) {
                    filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                }
                pvsToUse = bp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
                if (pvsToUse == null) {
                    return;
                }
            }
            pvs = pvsToUse;
        }
    }

    // 如果需要进行依赖性检查，则进行检查。
    if (needsDepCheck) {
        if (filteredPds == null) {
            filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
        }
        checkDependencies(beanName, mbd, filteredPds, pvs);
    }

    // 最后，将属性值应用到bean。
    if (pvs != null) {
        applyPropertyValues(beanName, mbd, bw, pvs);
    }
}
```

我们来到`doCreateBean(beanName,mbd,args)`方法中的第五步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`方法中，首先如果Bean实现了特定的Aware接口（如`BeanNameAware`, `BeanFactoryAware`等），Spring容器会先调用相关的方法。在实际执行初始化方法前，Spring会先通过注册的`BeanPostProcessors`的`postProcessBeforeInitialization`方法进行预处理，允许修改Bean或执行其他自定义操作。接着，Spring会调用Bean的初始化方法。这可以是Bean定义中通过`init-method`属性指定的方法，也可以是Bean实现的`InitializingBean`接口的`afterPropertiesSet`方法。Bean的初始化方法调用后，再次通过注册的`BeanPostProcessors`的`postProcessAfterInitialization`方法进行后处理。这为开发者提供了一个在Bean初始化完成后再次修改Bean或执行其他自定义操作的机会。

```java
/**
 * 初始化指定的bean。
 *
 * @param beanName bean的名称
 * @param bean     实际的bean实例
 * @param mbd      bean的合并bean定义；可以为{@code null}，例如对手动注册的单例。
 * @return 处理过后的bean实例 (可能是原始的或新的代理)
 * @throws BeansException 如果初始化失败
 */
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // 1.调用bean的Aware回调方法
    invokeAwareMethods(beanName, bean);

    // 初始时bean的引用
    Object wrappedBean = bean;

    // 2.在真正的初始化之前，应用BeanPostProcessors的前置处理
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }

    // 3.调用bean的初始化方法（例如afterPropertiesSet或自定义的init方法）
    try {
        invokeInitMethods(beanName, wrappedBean, mbd);
    } catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    // 4.在初始化之后，应用BeanPostProcessors的后置处理
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    // 返回处理后的bean实例
    return wrappedBean;  
}
```

我们来到`initializeBean(beanName,bean,mbd)`方法中的第一步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeAwareMethods`方法中，专门用于处理那些实现了Spring的Aware接口的bean，实现了`BeanNameAware`的bean，Spring容器会设置bean的名称，实现了`BeanClassLoaderAware`的bean，Spring容器会提供当前使用的类加载器，实现了`BeanFactoryAware`的bean，Spring容器会将自身的引用传递给bean。

```java
/**
 * 调用bean的Aware接口方法。
 * 如果bean实现了Spring的Aware接口之一，那么Spring容器将会在bean初始化时通知bean关于某些容器的状态或信息。
 *
 * @param beanName 该bean在Spring容器中的名称
 * @param bean 实际的bean实例
 */
private void invokeAwareMethods(String beanName, Object bean) {
    // 检查bean是否实现了Aware接口
    if (bean instanceof Aware) {

        // 如果bean实现了BeanNameAware接口，容器会通知bean关于其bean名称
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).setBeanName(beanName);
        }

        // 如果bean实现了BeanClassLoaderAware接口，容器会通知bean关于它正在使用的类加载器
        if (bean instanceof BeanClassLoaderAware) {
            ClassLoader bcl = getBeanClassLoader();
            // 检查类加载器是否为null，然后将其传递给bean
            if (bcl != null) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
            }
        }

        // 如果bean实现了BeanFactoryAware接口，容器会通知bean关于其自身的引用
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
        }
    }
}
```

我们来到`initializeBean(beanName,bean,mbd)`方法中的第二步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`方法中，遍历每一个 `BeanPostProcessor` 的 `postProcessBeforeInitialization` 方法都有机会对bean进行修改或增强

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

我们来到`initializeBean(beanName,bean,mbd)`方法中的第三步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeInitMethods`方法中，首先检查bean是否实现了`InitializingBean`接口，并调用其`afterPropertiesSet`方法，为bean执行任何必要的初始化逻辑。此外，如果bean定义中指定了一个自定义的初始化方法，该方法也会被执行，除非这个自定义的方法就是`afterPropertiesSet`，因为它已经在前面调用过了。总之，该方法确保了在bean的所有属性都被设置之后，可以按预期的顺序执行任何初始化逻辑。

```java
/**
 * 执行指定bean的初始化方法。这可能包括执行`InitializingBean`接口的`afterPropertiesSet`方法，
 * 以及在bean定义中指定的任何自定义初始化方法。
 *
 * @param beanName bean的名称
 * @param bean 实际的bean实例
 * @param mbd bean的定义，可能为null
 * @throws Throwable 如果初始化方法抛出异常
 */
protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
        throws Throwable {

    // 检查bean是否实现了InitializingBean接口
    boolean isInitializingBean = (bean instanceof InitializingBean);
    // 如果是，并且afterPropertiesSet不是外部管理的（例如，通过AspectJ）
    if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
        if (logger.isTraceEnabled()) {
            logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
        }
        if (System.getSecurityManager() != null) {
            // ... [代码部分省略以简化]
        } else {
            // 调用afterPropertiesSet方法
            ((InitializingBean) bean).afterPropertiesSet();
        }
    }

    // 检查是否存在自定义的初始化方法，并且这个方法不是afterPropertiesSet（因为它已经在上面调用过了）
    if (mbd != null && bean.getClass() != NullBean.class) {
        String initMethodName = mbd.getInitMethodName();
        if (StringUtils.hasLength(initMethodName) &&
            !(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
            !mbd.isExternallyManagedInitMethod(initMethodName)) {
            // 调用自定义的初始化方法
            invokeCustomInitMethod(beanName, bean, mbd);
        }
    }
}
```

我们来到`initializeBean(beanName,bean,mbd)`方法中的第四步中。在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`方法中，遍历每一个 `BeanPostProcessor` 的 `postProcessAfterInitialization` 方法都有机会对bean进行修改或增强。

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

#### 6.3、Bean销毁过程源码分析

+ [关于DestructionAwareBeanPostProcessor源码分析](spring-interface-destructionAwareBeanPostProcessor/README.md)
+ [关于DisposableBean源码分析](spring-interface-disposableBean/README.md)

### 七、注意事项

**选择合适的生命周期**：默认情况下，Spring中的beans是单例的。如果bean需要在每次请求时都有一个新的实例，应该将其作用域设置为`prototype`。

**慎用原型作用域与单例作用域的组合**：注入原型作用域的bean到单例作用域的bean可能不会像预期的那样工作。每次对单例bean的请求都会返回相同的原型实例。

**避免循环依赖**：Spring可以解决构造器和setter方法的循环依赖，但推荐避免这种设计，因为它可能使代码更难理解和维护。

**避免多重bean定义**：确保应用程序上下文中没有多个相同名字或类型的bean，否则可能导致出现一些奇奇怪怪的问题。

**懒加载**：如果我们不希望容器启动时立即初始化某个bean，可以使用`@Lazy`注解或在XML中设置`lazy-init`属性。

**避免使用`depends-on`**：虽然`depends-on`可以指定bean的启动顺序，但在可能的情况下，避免使用它，因为它可能会导致配置更加复杂。

**使用`@PostConstruct`和`@PreDestroy`注解**：为bean的初始化和销毁提供方法，而不是使用Spring特定的`init-method`和`destroy-method`属性。

**谨慎使用Bean后处理器**：Bean后处理器（`BeanPostProcessor`）是一个强大的机制，但如果不当使用，可能会导致性能问题，因为它们会在每个bean的初始化阶段都被调用。

**避免在bean的初始化方法中使用其他bean**：在bean的生命周期中，避免在构造函数或初始化方法中调用其他bean，因为这些bean可能尚未完全初始化。

### 八、总结

#### 8.1、最佳实践总结

**应用程序入口**：在`IOCApplication`类中，首先创建了一个`AnnotationConfigApplicationContext`的实例，该实例是基于Java的Spring容器配置。作为构造参数，传递了`MyConfiguration`类，这意味着Spring将根据`MyConfiguration`类来配置IoC容器。

**配置类**：`MyConfiguration`类被注解为`@Configuration`，这意味着它是一个Spring配置类。它使用了`@ComponentScan`注解来告诉Spring去哪里查找beans。在这个示例中，它会扫描`com.xcs.spring.service`包及其子包。

**服务类**：`MyServiceA`和`MyServiceB`类都在指定的包路径下，并且都被注解为`@Component`。这意味着Spring将为它们创建bean实例并管理它们的生命周期。

**输出结果**：当`IOCApplication`运行时，它会打印出容器中所有bean的名称和实例。除了自定义的两个服务类之外，还有其他的Spring内部bean，例如`ConfigurationClassPostProcessor`、`AutowiredAnnotationBeanPostProcessor`等。

#### 8.2、源码分析总结

**启动类与上下文环境**：应用启动时，创建了一个`AnnotationConfigApplicationContext`，并注册了配置类`MyConfiguration`。随后，通过遍历并获取所有容器中的Bean实例，并打印其信息。在上下文关闭时，触发了单例bean的销毁方法。

**上下文刷新**：在`AnnotationConfigApplicationContext`构造函数中，主要关注了`refresh()`方法，这个方法里再继续调用了`finishBeanFactoryInitialization(beanFactory)`，它主要负责实例化所有剩余的非懒加载单例Bean对象。

**实例化所有非懒加载的单例Bean**：在`finishBeanFactoryInitialization`方法里，调用了`DefaultListableBeanFactory`类中的`preInstantiateSingletons`方法，这个方法会对每个非懒加载的单例bean进行实例化。当这些beans被初始化后，方法还会查找那些实现了`SmartInitializingSingleton`接口的beans，并执行它们的`afterSingletonsInstantiated`方法。

**Bean获取与创建**：`AbstractBeanFactory`类中的`getBean`方法负责获取bean。如果bean尚未创建，则会进一步调用`doGetBean`方法进行创建。这个方法处理了从缓存获取单例bean的逻辑，循环引用的问题，以及根据bean的作用域创建bean实例的逻辑。

**单例Bean的获取与创建**：`DefaultSingletonBeanRegistry`类中的`getSingleton`方法负责从单例缓存中获取bean，或者通过提供的singletonFactory进行创建。这个方法确保了线程安全地创建单例，并处理了与创建失败、并发和循环引用相关的问题。

**Bean创建前后的处理**：在创建单例bean之前和之后，系统通过`beforeSingletonCreation`和`afterSingletonCreation`方法进行相应的处理，如标记bean正在创建，以及后续的清理工作。

**Bean的创建**：`createBean(beanName,mbd,args)`是一个核心方法，它负责创建Bean。此过程包括：确保Bean的类已正确解析并设置。验证方法重写，确保没有配置错误。在实际实例化Bean之前，尝试使用`BeanPostProcessors`返回一个代理对象。实际创建和初始化Bean。

**早期解析与代理**：在`resolveBeforeInstantiation`方法中，尝试在Bean实际实例化之前完成Bean的实例化，这通常是为了返回一个代理对象。

**Bean实例化与属性注入**：`doCreateBean`方法首先负责Bean的实例化，然后进行类型的解析和方法覆盖的准备。在实例化Bean后，该方法允许`BeanPostProcessors`为Bean提供代理或进行其他更改。之后，该方法为解决可能的循环引用问题，提前将Bean放入缓存。接着，进行属性注入，并执行初始化方法。最后，如果Bean定义了销毁方法或实现了相关接口，它将被注册为可销毁的。

**Bean属性的填充**：`populateBean`方法确保Bean的所有属性都被正确地配置和填充。它负责按名字或类型进行自动装配，并处理显式定义的属性值。

**Bean的初始化**：`initializeBean`方法负责执行Bean的初始化逻辑。这包括调用Aware接口的方法，通过`BeanPostProcessors`进行预处理，执行初始化方法（例如`afterPropertiesSet`或自定义的init方法），最后通过`BeanPostProcessors`进行后处理。

**Aware接口的处理**：`invokeAwareMethods`方法是专门为那些实现了Spring的Aware接口的bean设计的，它负责通知Bean关于容器的某些状态或信息。