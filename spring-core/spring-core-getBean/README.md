## getBean

- [getBean](#getbean)
  - [一、基本信息](#一基本信息)
  - [二、方法源码](#二方法源码)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN]() 📚 **文章目录** - [源码地址](https://github.com/xuchengsheng/spring-reading)

在 Spring 框架中，`getBean` 方法是 `ApplicationContext` 接口中的一个核心方法，用于从 Spring 容器中检索 bean。Spring 的核心是控制反转（Inversion of Control, IoC）和依赖注入（Dependency Injection, DI），`getBean` 方法正是实现这两个核心概念的重要方法。

### 二、方法源码

这个方法的定义和说明表明了 Spring IoC 容器的一些核心概念和工作机制。当我们请求一个 bean 时，Spring 会查找该 bean、处理任何别名、检查其作用域（例如，单例或原型），并最终返回适当的 bean 实例给调用者。

```java
/**
 * 返回指定bean的实例，该实例可能是共享的或独立的。
 * 此方法使Spring BeanFactory可以替代单例或原型设计模式。在单例bean的情况下，调用者可以保留返回对象的引用。
 * 将别名转换回相应的规范bean名称。
 * 如果在这个工厂实例中找不到bean，将询问父工厂。
 * 
 * @param name 要检索的bean的名称
 * @return bean的实例
 * @throws NoSuchBeanDefinitionException 如果没有指定名称的bean
 * @throws BeansException 如果无法获取bean
 */
Object getBean(String name) throws BeansException;
```

### 三、主要功能

1. **检索 Bean**
   + 从 Spring 容器中检索并返回指定名称或类型的 bean 的实例。
2. **作用域处理**
   + 根据 bean 的配置和作用域（例如 "singleton" 或 "prototype"），`getBean` 可以返回单例的 bean 实例或每次都创建一个新的实例。
3. **别名处理**
   + 如果 bean 有别名，`getBean` 可以根据这些别名解析并返回相应的 bean 实例。
4. **考虑父容器**
   + 如果在当前容器中找不到 bean，但容器有父容器，则 `getBean` 会在父容器中查找该 bean。
5. **类型转换**
   + `getBean` 还有一个重载版本，允许用户指定返回 bean 的类型，这样可以避免在后续使用中进行显式的类型转换。
6. **异常处理**
   + 如果容器中不存在指定的 bean，它会抛出 `NoSuchBeanDefinitionException`。如果在尝试创建或检索 bean 时出现其他问题，它会抛出 `BeansException`。
7. **支持依赖查找**
   + 尽管 Spring 的主要目标是通过依赖注入提供依赖关系，但 `getBean` 方法提供了一种手动查找依赖的方式。
8. **初始化 Bean**
   + 如果 bean 尚未初始化（例如，对于单例 bean 在首次请求时），`getBean` 方法会触发其初始化。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，然后从Spring上下文中获取两个Bean对象`myServiceA`，`myServiceB`类型的bean。

```java
public class GetBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("myServiceA = " + context.getBean("myServiceA"));
        System.out.println("myServiceB = " + context.getBean("myServiceB"));
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
public class MyServiceA {

}

@Component
public class MyServiceB {

}
```

运行结果发现，这是我们自己定义的两个服务类。它们都被标记为`@Component`，因此Spring容器会为每个类创建一个bean实例。

```java
myServiceA = com.xcs.spring.service.MyServiceA@23c30a20
myServiceB = com.xcs.spring.service.MyServiceB@1e1a0406
```

### 五、时序图

~~~mermaid
sequenceDiagram
DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)
note over AbstractBeanFactory: 请求一个Bean

AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)
note over AbstractBeanFactory: 执行实际的获取Bean逻辑

AbstractBeanFactory->>AbstractBeanFactory:transformedBeanName(name)
note over AbstractBeanFactory: 获取真正的bean名称

AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName)
note over DefaultSingletonBeanRegistry: 检查Bean是否为单例

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:getSingleton(beanName,allowEarlyReference)
note over DefaultSingletonBeanRegistry: 如果允许，检查早期引用的单例Bean

DefaultSingletonBeanRegistry->>AbstractBeanFactory:返回已注册的singleton对象
note over AbstractBeanFactory: 如果已注册，则返回这个单例Bean

AbstractBeanFactory->>DefaultListableBeanFactory:返回Bean对象
note over DefaultListableBeanFactory: 返回到原始的请求源

note over AbstractBeanFactory: Bean不在缓存中，需要创建

AbstractBeanFactory->>AbstractBeanFactory:getParentBeanFactory()
note over AbstractBeanFactory: 检查是否有父Bean工厂

AbstractBeanFactory->>DefaultListableBeanFactory:parentBeanFactory.getBean(name)
note over DefaultListableBeanFactory: 在父工厂中请求Bean

AbstractBeanFactory->>AbstractBeanFactory:markBeanAsCreated(beanName)
note over AbstractBeanFactory: 标记该Bean为已创建

AbstractBeanFactory->>AbstractBeanFactory:getMergedLocalBeanDefinition(beanName)
note over AbstractBeanFactory: 获取合并后的Bean定义

AbstractBeanFactory->>AbstractBeanFactory:checkMergedBeanDefinition(mbd, beanName, args)
note over AbstractBeanFactory: 检查合并后的Bean定义是否有效

AbstractBeanFactory->>AbstractBeanDefinition:getDependsOn()
note over AbstractBeanFactory: 获取该Bean的依赖

AbstractBeanFactory->>DefaultSingletonBeanRegistry:isDependent(beanName, dep)
note over DefaultSingletonBeanRegistry: 检查是否存在依赖

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:isDependent(beanName, dependentBeanName, null)
note over DefaultSingletonBeanRegistry: 检查依赖

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:canonicalName(beanName)
note over DefaultSingletonBeanRegistry: 获取Bean的规范名称

DefaultSingletonBeanRegistry->>AbstractBeanFactory:返回是否存在循环依赖的情况
note over AbstractBeanFactory: 返回循环依赖的检查结果

note over AbstractBeanFactory: 如果存在循环依赖，则抛出异常 throw new BeanCreationException(""Circular depends-on relationship between")

AbstractBeanFactory->>DefaultSingletonBeanRegistry:registerDependentBean(dep, beanName)
note over DefaultSingletonBeanRegistry: 注册依赖关系

AbstractBeanFactory->>DefaultListableBeanFactory:getBean(name)
note over DefaultListableBeanFactory: 获取被依赖的bean对象

AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)
note over DefaultSingletonBeanRegistry: 获取或创建单例Bean

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:beforeSingletonCreation(beanName)
note over DefaultSingletonBeanRegistry: 在创建单例之前的准备工作

DefaultSingletonBeanRegistry->>AbstractBeanFactory:singletonFactory.getObject()
note over AbstractBeanFactory: 使用单例工厂创建Bean

AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName, mbd, args)
note over AbstractAutowireCapableBeanFactory: 创建新的Bean实例

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:resolveBeanClass(mbd, beanName)
note over AbstractAutowireCapableBeanFactory: 解析Bean的类

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:resolveBeforeInstantiation(beanName, mbdToUse)
note over AbstractAutowireCapableBeanFactory: 在实例化前尝试解析Bean

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)
note over AbstractAutowireCapableBeanFactory: 执行实际的Bean创建

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:createBeanInstance(beanName, mbd, args)
note over AbstractAutowireCapableBeanFactory: 创建Bean实例

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:determineConstructorsFromBeanPostProcessors(beanClass, beanName)
note over AbstractAutowireCapableBeanFactory: 从SmartInstantiationAwareBeanPostProcessor确定构造器

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:instantiateBean(beanName, mbd)
note over AbstractAutowireCapableBeanFactory: 实例化Bean

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName)
note over AbstractAutowireCapableBeanFactory: 应用合并后的Bean定义后处理器

AbstractAutowireCapableBeanFactory->>DefaultSingletonBeanRegistry:addSingletonFactory(beanName,singletonFactory)
note over DefaultSingletonBeanRegistry: 添加单例工厂

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName, mbd, instanceWrapper)
note over AbstractAutowireCapableBeanFactory: 填充Bean的属性

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:postProcessAfterInstantiation(bean,beanName)
note over AbstractAutowireCapableBeanFactory: 实例化后的后处理

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:postProcessProperties(pvs,bean,beanName)
note over AbstractAutowireCapableBeanFactory: 属性后处理

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:postProcessPropertyValues(pvs,pds,bean,beanName)
note over AbstractAutowireCapableBeanFactory: 属性后处理

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyPropertyValues(beanName,mbd,bw,pvs)
note over AbstractAutowireCapableBeanFactory: 应用属性值

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName, exposedObject, mbd)
note over AbstractAutowireCapableBeanFactory: 初始化Bean

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeAwareMethods(beanName, bean)
note over AbstractAutowireCapableBeanFactory: 调用Aware接口方法

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName)
note over AbstractAutowireCapableBeanFactory: 在初始化前应用BeanPostProcessors

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeInitMethods(beanName, wrappedBean, mbd)
note over AbstractAutowireCapableBeanFactory: 调用初始化方法

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName)
note over AbstractAutowireCapableBeanFactory: 在初始化后应用BeanPostProcessors

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:registerDisposableBeanIfNecessary(beanName, bean, mbd)
note over AbstractAutowireCapableBeanFactory: 如果需要，注册可销毁的Bean

AbstractAutowireCapableBeanFactory->>AbstractBeanFactory:返回创建的单例Bean
AbstractBeanFactory->>DefaultSingletonBeanRegistry:返回创建的单例Bean

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:afterSingletonCreation(beanName)
note over DefaultSingletonBeanRegistry: 单例创建后的后续处理

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:addSingleton(beanName, singletonObject)
note over DefaultSingletonBeanRegistry: 向注册表中添加新的单例Bean

DefaultSingletonBeanRegistry->>AbstractBeanFactory:返回创建的单例Bean

AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:getObjectForBeanInstance(sharedInstance, name, beanName, mbd)
note over AbstractAutowireCapableBeanFactory: 获取Bean实例的对象

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:处理FactoryBean
note over AbstractAutowireCapableBeanFactory: 如果是FactoryBean，则处理

AbstractAutowireCapableBeanFactory->>AbstractBeanFactory:返回真正的Bean对象
note over AbstractBeanFactory: 返回真实的Bean对象，而不是FactoryBean

AbstractBeanFactory->>AbstractBeanFactory:adaptBeanInstance(name, beanInstance, requiredType)
note over AbstractBeanFactory: 适配Bean实例的类型

AbstractBeanFactory->>DefaultListableBeanFactory:返回真正的Bean对象
note over DefaultListableBeanFactory: 返回到原始的请求源
~~~

### 六、源码分析

在`org.springframework.beans.factory.support.AbstractBeanFactory#getBean(name)`方法中，提供了一个简单的方式，让调用者能够基于bean的名称从Spring IoC容器中检索bean，而不需要提供任何其他的上下文信息或参数。

```java
@Override
public Object getBean(String name) throws BeansException {
    // 调用doGetBean方法来真正的获取bean。
    // 参数说明：
    // 1. name: 要获取的bean的名称。
    // 2. null: bean的所需类型，这里为null表示没有指定具体类型。
    // 3. null: 构造函数或工厂方法的参数，这里为null表示默认构造方法或工厂方法。
    // 4. false: 指定是否仅进行类型检查，false表示需要实例化bean。
    return doGetBean(name, null, null, false);
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中，是Spring框架中`AbstractBeanFactory`类的核心方法，用于获取bean实例。它考虑了单例、原型、特定作用域bean的创建，还处理了bean定义、循环引用、依赖等各种情况。

```java
protected <T> T doGetBean(
			String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
			throws BeansException {

    // 步骤1: 转换bean名称
    String beanName = transformedBeanName(name);

    // 步骤2: 尝试从缓存中检索单例bean
    Object sharedInstance = getSingleton(beanName);
    Object beanInstance;

    if (sharedInstance != null && args == null) {
        // 获取bean实例本身
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, null);
    }
    else {
        // 步骤3: 处理原型作用域的bean，并检查是否已在创建中
        if (isPrototypeCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }

        // 步骤4: 尝试在父Bean工厂中检索bean定义
        BeanFactory parentBeanFactory = getParentBeanFactory();

        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            String nameToLookup = originalBeanName(name);
            // ... [代码部分省略以简化]
            return (T) parentBeanFactory.getBean(nameToLookup);
        }

        // 步骤5: 标记bean为已创建状态
        if (!typeCheckOnly) {
            markBeanAsCreated(beanName);
        }

        try {
            // 步骤6: 获取合并后的bean定义
            RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            checkMergedBeanDefinition(mbd, beanName, args);

            // 步骤7: 确保bean的依赖已经初始化
            String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                for (String dep : dependsOn) {
                    // 步骤7.1: 是否存在循环依赖
                    if (isDependent(beanName, dep)) {
                       // ... [代码部分省略以简化]
                    }
                    // 步骤7.2: 注册Bean与Bean之间的依赖关系
                    registerDependentBean(dep, beanName);
                    // 步骤7.3: 获取被依赖的Bean对象
                    getBean(dep);
                }
            }

            // 步骤8: 根据bean的作用域，创建或检索bean实例
            if (mbd.isSingleton()) {
                // 步骤8.1: 处理单例作用域
                sharedInstance = getSingleton(beanName, () -> {
                    try {
                        // 步骤8.2: 创建Bean
                        return createBean(beanName, mbd, args);
                    }
                    catch (BeansException ex) {
                        // ... [代码部分省略以简化]
                    }
                });
                // 步骤8.3: 获取bean实例本身
                beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
            }
            else if (mbd.isPrototype()) {
                // 处理原型作用域
                // ... [代码部分省略以简化]
            }
            else {
                // 处理其他作用域
                // ... [代码部分省略以简化]
            }
        }
        catch (BeansException ex) {
            // 处理bean创建失败的情况
            // ... [代码部分省略以简化]
        }
        finally {
            // ... [代码部分省略以简化]
        }
    }

    // 步骤9: 适配bean实例
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤1。

在`org.springframework.beans.factory.support.AbstractBeanFactory#transformedBeanName`方法中，主要作用是对给定的bean名称进行转换，确保返回的名称是规范的、没有任何前缀，并处理可能的别名。

```java
protected String transformedBeanName(String name) {
    // 首先，使用BeanFactoryUtils的transformedBeanName方法来处理传入的name。
    // 这通常用于移除bean名称前缀，例如"&"，这在工厂beans中使用。
    // 使用canonicalName方法来获取别名映射后的真实bean名称。
    return canonicalName(BeanFactoryUtils.transformedBeanName(name));
}
```

在`org.springframework.core.SimpleAliasRegistry#canonicalName`方法中，持续地从别名映射中查找真实的bean名称，直到找不到更多的别名为止，从而确保返回的是真实的bean名称，而不是任何别名。

```java
public String canonicalName(String name) {
    // 初始化canonicalName为传入的name
    String canonicalName = name; 

    // 循环处理别名映射
    String resolvedName;
    do {
        // 从别名映射中获取真实的bean名称
        resolvedName = this.aliasMap.get(canonicalName);
        
        // 如果找到了一个真实的bean名称（即resolvedName不为null），则更新canonicalName为这个新找到的名称
        if (resolvedName != null) {
            canonicalName = resolvedName;
        }
    } 
    // 如果还可以在aliasMap中找到resolvedName的别名，继续循环
    while (resolvedName != null);

    // 返回最终确定的bean名称
    return canonicalName;
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤2。

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName)`方法中，目的是简化单例bean的获取过程。它默认允许在bean正在创建过程中返回早期的bean引用，这在解决循环依赖的场景中是有用的。

```java
@Override
@Nullable
public Object getSingleton(String beanName) {
    // 调用重载的getSingleton方法来获取单例bean。
    // 参数说明：
    // 1. beanName: 要获取的单例bean的名称。
    // 2. true: 表示如果当前bean正在创建中（例如处理循环引用的情况），则允许返回早期的单例bean引用。
    return getSingleton(beanName, true);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,allowEarlyReference)`方法中，主要目的是检索beanName指定的单例对象，考虑了多种可能的缓存位置，包括完全初始化的缓存、早期的单例对象缓存和单例工厂缓存。如果bean目前正在创建中（这可能是由于循环引用），该方法还会处理这种情况。

```java
@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // 尝试从缓存中快速检索已存在的bean实例，避免完全锁定单例
    Object singletonObject = this.singletonObjects.get(beanName);
    
    // 如果找不到实例，并且该bean当前正在创建中（例如，处理循环引用）
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        singletonObject = this.earlySingletonObjects.get(beanName);
        
        // 如果允许提前引用并且在早期单例对象中仍未找到
        if (singletonObject == null && allowEarlyReference) {
            synchronized (this.singletonObjects) {
                // 在完整的单例锁定范围内，确保早期引用的一致性创建
                singletonObject = this.singletonObjects.get(beanName);
                if (singletonObject == null) {
                    singletonObject = this.earlySingletonObjects.get(beanName);
                    
                    // 如果在早期的单例对象中仍然找不到，并且存在一个单例工厂来创建这个bean
                    if (singletonObject == null) {
                        ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                        if (singletonFactory != null) {
                            // 使用单例工厂创建bean
                            singletonObject = singletonFactory.getObject();
                            // 将新创建的bean存放到早期单例对象缓存中
                            this.earlySingletonObjects.put(beanName, singletonObject);
                            // 从单例工厂缓存中删除对应的工厂
                            this.singletonFactories.remove(beanName);
                        }
                    }
                }
            }
        }
    }
    // 返回找到的单例bean实例，如果没有找到则返回null
    return singletonObject;
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤3。

在`org.springframework.beans.factory.support.AbstractBeanFactory#isPrototypeCurrentlyInCreation`方法中，检查一个特定的bean名称是否正在创建中的原型beans列表中。这是为了处理可能出现的原型bean的循环引用。

```java
protected boolean isPrototypeCurrentlyInCreation(String beanName) {
    // 获取当前正在创建的原型bean的值
    Object curVal = this.prototypesCurrentlyInCreation.get();

    // 检查当前值是否不为空，并且
    // 1) 当前值是否等于给定的bean名称，或者
    // 2) 当前值是否是一个Set并且该Set包含给定的bean名称
    return (curVal != null &&
            (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName))));
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤5。

在`org.springframework.beans.factory.support.AbstractBeanFactory#markBeanAsCreated`方法中，主要目的是标记指定的bean已经被创建或正在被创建。它在Spring的bean生命周期中起到关键作用，特别是当需要确保bean只被创建一次或者对其进行某些状态检查时。

```java
protected void markBeanAsCreated(String beanName) {
    // 1. 初步检查bean是否已被标记为已创建
    if (!this.alreadyCreated.contains(beanName)) {
        
        // 2. 为了确保在多线程环境下的线程安全，进行同步操作
        synchronized (this.mergedBeanDefinitions) {
            
            // 3. 双重检查锁定模式：再次确认bean是否已被标记为已创建
            if (!this.alreadyCreated.contains(beanName)) {
                
                // 4. 清除bean的合并定义，以便在后续访问时重新合并
                clearMergedBeanDefinition(beanName);
                
                // 5. 在集合中标记bean已被创建
                this.alreadyCreated.add(beanName);
            }
        }
    }
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤6。

在`org.springframework.beans.factory.support.AbstractBeanFactory#getMergedLocalBeanDefinition`方法中，主要是用于获取给定bean名称的合并bean定义。合并的bean定义是从父bean和子bean（如果有的话）定义中合并的结果。

```java
protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
    // 1. 快速从并发映射中检查bean定义，这样做可以最小化锁定。
    RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
    
    // 2. 如果合并的bean定义存在并且没有过期，直接返回它。
    if (mbd != null && !mbd.stale) {
        return mbd;
    }
    
    // 3. 如果上述检查失败，进一步获取并返回合并的bean定义。
    return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#getBeanDefinition`方法中，主要用于从当前Bean工厂的bean定义映射中检索指定名称的bean定义。如果没有找到指定的bean定义，它会抛出一个`NoSuchBeanDefinitionException`异常。

```java
@Override
public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    // 1. 从beanDefinitionMap中获取bean的定义
    BeanDefinition bd = this.beanDefinitionMap.get(beanName);
    
    // 2. 如果没有找到BeanDefinition，进行日志跟踪并抛出异常
    if (bd == null) {
        // 如果启用了trace级别的日志，记录一条日志
        if (logger.isTraceEnabled()) {
            logger.trace("No bean named '" + beanName + "' found in " + this);
        }
        // 抛出没有找到BeanDefinition的异常
        throw new NoSuchBeanDefinitionException(beanName);
    }
    
    // 3. 返回找到的BeanDefinition
    return bd;
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#getMergedBeanDefinition(beanName, bd)`方法中，又调用了另一个`getMergedBeanDefinition`方法版本，为给定的Bean名称和Bean定义获取一个合并的`RootBeanDefinition`。

```java
protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd)
			throws BeanDefinitionStoreException {

    // 为给定的Bean名称和Bean定义获取一个合并的RootBeanDefinition，
    // 由于这个版本的方法没有提供一个父Bean定义，所以我们传递null作为第三个参数。
    return getMergedBeanDefinition(beanName, bd, null);
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#getMergedBeanDefinition(beanName,bd,containingBd)`方法中，主要目的是获取指定bean名称的合并bean定义。它的主要工作是处理bean定义的父子关系和其他相关设置，然后返回一个合并后的bean定义。

```java
protected RootBeanDefinition getMergedBeanDefinition(
        String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd)
        throws BeanDefinitionStoreException {

    // 1. 对mergedBeanDefinitions进行同步以确保线程安全。
    synchronized (this.mergedBeanDefinitions) {
        RootBeanDefinition mbd = null;
        RootBeanDefinition previous = null;

        // 2. 在完整的锁定中检查，以确保使用相同的合并实例。
        if (containingBd == null) {
            mbd = this.mergedBeanDefinitions.get(beanName);
        }

        // 3. 如果bean定义未被合并或已过期，进行合并操作。
        if (mbd == null || mbd.stale) {
            previous = mbd;

            // 4. 处理没有父定义的情况。
            if (bd.getParentName() == null) {
                if (bd instanceof RootBeanDefinition) {
                    mbd = ((RootBeanDefinition) bd).cloneBeanDefinition();
                }
                else {
                    mbd = new RootBeanDefinition(bd);
                }
            }
            // 5. 处理有父定义的情况：需要与父定义合并。
            else {
                BeanDefinition pbd;
                try {
                    String parentBeanName = transformedBeanName(bd.getParentName());
                    if (!beanName.equals(parentBeanName)) {
                        pbd = getMergedBeanDefinition(parentBeanName);
                    }
                    else {
                        BeanFactory parent = getParentBeanFactory();
                        if (parent instanceof ConfigurableBeanFactory) {
                            pbd = ((ConfigurableBeanFactory) parent).getMergedBeanDefinition(parentBeanName);
                        }
                        else {
                            // ... [代码部分省略以简化]
                        }
                    }
                }
                catch (NoSuchBeanDefinitionException ex) {
                    // ... [代码部分省略以简化]
                }
                mbd = new RootBeanDefinition(pbd);
                mbd.overrideFrom(bd);
            }

            // 6. 如果bean定义的范围没有明确设置，将其默认为单例。
            if (!StringUtils.hasLength(mbd.getScope())) {
                mbd.setScope(SCOPE_SINGLETON);
            }

            // 7. 非单例bean中的bean不能是单例。在这里修复这种情况。
            if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
                mbd.setScope(containingBd.getScope());
            }

            // 8. 如果需要，缓存合并后的bean定义。
            if (containingBd == null && isCacheBeanMetadata()) {
                this.mergedBeanDefinitions.put(beanName, mbd);
            }
        }
        // 9. 如果之前存在一个bean定义，复制相关的缓存。
        if (previous != null) {
            copyRelevantMergedBeanDefinitionCaches(previous, mbd);
        }

        // 10. 返回合并后的bean定义。
        return mbd;
    }
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤7.1。

在Spring的bean初始化过程中，`@DependsOn`注解扮演了一个关键的角色，用于确保某个bean在其他指定的beans之前初始化。下面的代码片段详细展示了如何处理这个注解。为了深入了解这些细节，特别是`@DependsOn`注解背后的工作原理，我建议参考这篇文章: [**初始化顺序@DependsOn**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-annotation/spring-annotation-dependsOn) - 精确控制 Spring Beans 的加载顺序。这篇文章详细解析了注解的源码，并深入探讨了其在Spring框架中的作用。

```java
String[] dependsOn = mbd.getDependsOn();
if (dependsOn != null) {
    for (String dep : dependsOn) {
        // 步骤7.1: 是否存在循环依赖
        // 它首先检查是否存在循环依赖，这意味着Bean A依赖Bean B，而Bean B又依赖Bean A。
        // 如果存在这样的情况它会抛出一个BeanCreationException异常。
        if (isDependent(beanName, dep)) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                            "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
        }
        // 步骤7.2: 注册Bean与Bean之间的依赖关系
        // 当前的Bean工厂中注册bean之间的依赖关系。这样，当获取或销毁bean时，Spring可以保持正确的顺序。
        registerDependentBean(dep, beanName);
        // 步骤7.3: 获取被依赖的Bean对象
        // 确保每个被依赖的bean都已经被创建。这是通过直接调用getBean方法完成的，该方法负责初始化并返回指定的bean。
        getBean(dep);
    }
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤8.1。

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`方法中，首先尝试从缓存中检索该bean。如果没有找到，它会使用提供的`singletonFactory`来创建这个bean，并在创建过程中进行前置和后置处理，以确保处理诸如循环引用等问题。创建的bean会被添加到缓存中。此外，该方法还处理了在创建过程中可能出现的各种异常，并确保在多线程环境中的线程安全。最后，返回所需的单例bean。

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    synchronized (this.singletonObjects) {
        // 首先尝试从缓存中获取单例
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            // ... [代码部分省略以简化]
            
            // 步骤1: 前置处理，例如标记这个bean正在创建，以处理循环引用等问题。
            beforeSingletonCreation(beanName);
            
            // ... [代码部分省略以简化]
            try {
                // 步骤2: 使用singletonFactory创建单例对象
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
                // 步骤3: 创建单例之后的回调
                afterSingletonCreation(beanName);
            }
            // 步骤4: 如果成功创建了新的单例bean，将其添加到缓存中
            if (newSingleton) {
                addSingleton(beanName, singletonObject);
            }
        }
        // 返回现有或新创建的单例bean
        return singletonObject;
    }
}
```

> `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`方法中的步骤1。

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#beforeSingletonCreation`方法中，Spring框架尝试创建单例bean之前调用的，用于确保当前bean没有同时被多次创建，这样可以避免因循环引用导致的问题。如果bean已经在创建过程中，此方法会抛出一个异常。

```java
protected void beforeSingletonCreation(String beanName) {
    // 检查beanName是否在排除列表中或已经在创建中的集合中。
    // 如果bean不在排除列表中并且也不能添加到创建中的集合中，意味着bean已经在创建中。
    if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
        throw new BeanCurrentlyInCreationException(beanName);
    }
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤8.2。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`方法中，主要责根据指定的bean定义创建bean实例。此方法考虑了各种细节，例如是否有工厂方法、构造函数注入等，以及如何处理前置和后置处理器。

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {

    // 对mbd进行一些预处理，这可能包括克隆bean定义，如果mbd是非共享的原型。
    RootBeanDefinition mbdToUse = mbd;

    // 步骤1: 尝试使用InstantiationAwareBeanPostProcessors来实例化bean。
    // 如果后处理器产生bean实例（例如通过AOP代理），则直接返回该实例。
    try {
        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }
    }
    catch (Throwable ex) {
        // 如果解析失败，记录异常并继续常规的bean创建。
        // ... [代码部分省略以简化]
    }

    // 如果前置处理没有返回bean实例，进入常规的bean创建过程。
    try {
        // 步骤2: 创建bean实例。这可能是通过工厂方法、构造函数注入等完成的。
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        return beanInstance;
    }
    // 捕获创建过程中可能出现的异常，并处理它们。
    catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
        // ... [代码部分省略以简化]
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`方法中的步骤1。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`方法中，在Spring框架中，`InstantiationAwareBeanPostProcessor`允许在标准实例化前拦截bean的创建。这一功能主要通过`resolveBeforeInstantiation`方法体现。为深入理解其工作机制，推荐我们阅读：[**Bean实例拦截InstantiationAwareBeanPostProcessor**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)。这篇文章详细探讨了该接口在Spring中的核心作用。

```java
@Nullable
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
    // 初始化一个bean变量，它可能会被后续的处理过程赋值
    Object bean = null;
    
    // 检查'beforeInstantiationResolved'属性是否为FALSE。如果是FALSE，则跳过后续的处理
    if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
        
        // 首先，确保此时的bean类已经被解析。
        // 然后，对于非合成的bean，并且如果有任何InstantiationAwareBeanPostProcessors，尝试进行前置处理。
        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            
            // 确定目标类型。这可能涉及到类的解析和其他初始化操作。
            Class<?> targetType = determineTargetType(beanName, mbd);
            
            if (targetType != null) {
                // 如果确定了目标类型，首先应用BeanPostProcessors的前置处理。这可能会返回一个bean实例，
                // 这样我们就可以避免标准的实例化过程。
                bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                
                // 如果bean实例在上述步骤中被创建，则还需要进行初始化后的BeanPostProcessors处理。
                if (bean != null) {
                    bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            }
        }
        
        // 更新'mbd.beforeInstantiationResolved'的值，如果bean在上述步骤中被创建，则为true，否则为false。
        mbd.beforeInstantiationResolved = (bean != null);
    }
    
    // 返回可能已经在上述过程中创建的bean实例，或者如果没有创建bean，则返回null。
    return bean;
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`方法中的步骤2。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，主要处理了bean生命周期中的多个关键阶段，从bean的实例化、属性注入、初始化，到bean的清理注册。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    BeanWrapper instanceWrapper = null;
    
    // ... [代码部分省略以简化]
    
    // 步骤1: 尝试实例化bean
    if (instanceWrapper == null) {
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    
    // ... [代码部分省略以简化]

    // 步骤2: 合并bean定义的后置处理
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

    // 步骤3: 处理可能的循环引用，通过提前暴露bean的引用
    boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
            isSingletonCurrentlyInCreation(beanName));
    if (earlySingletonExposure) {
        // ... [代码部分省略以简化]
        // 步骤3.1: 注册一个`ObjectFactory`
        addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
    }

    // 步骤4: 初始化bean实例，填充bean属性并应用后处理器
    Object exposedObject = bean;
    try {
        // 步骤4.1: 属性填充
        populateBean(beanName, mbd, instanceWrapper);
        // 步骤4.2: 初始化bean
        exposedObject = initializeBean(beanName, exposedObject, mbd);  
    } catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    // Step 5: 如果需要，注册bean以便在容器关闭时进行清理
    try {
        registerDisposableBeanIfNecessary(beanName, bean, mbd);
    } catch (BeanDefinitionValidationException ex) {
        // ... [代码部分省略以简化]
    }

    return exposedObject;
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中的步骤1。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance`方法中，首先尝试从后处理器获取构造函数，然后检查是否有首选构造函数，最后如果没有其他选项，它会使用无参数构造函数。

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    // ... [代码部分省略以简化]

    // 步骤1: 首先尝试从BeanPostProcessors确定构造函数，这主要是为了处理例如@Autowired注解的情况
    Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
    // 如果确定了构造函数或者bean定义中有相关的自动装配模式和构造函数参数，则使用自动装配构造函数创建bean实例
    if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
        mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
        return autowireConstructor(beanName, mbd, ctors, args);
    }

    // 步骤2: 如果BeanDefinition中存在首选构造函数，使用这些构造函数
    ctors = mbd.getPreferredConstructors();
    if (ctors != null) {
        return autowireConstructor(beanName, mbd, ctors, null);
    }

    // 步骤3: 如果前面的步骤都没有返回bean实例，那么使用无参数构造函数实例化bean
    return instantiateBean(beanName, mbd);
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance`方法中的步骤1。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#determineConstructorsFromBeanPostProcessors`方法中，`SmartInstantiationAwareBeanPostProcessor`提供了智能的bean实例化策略，尤其是通过`determineConstructorsFromBeanPostProcessors`方法调整构造函数选择。为了深入理解其作用，建议阅读：[**调整Bean实例化策略SmartInstantiationAwareBeanPostProcessor**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-smartInstantiationAwareBeanPostProcessor)。这篇文章深入分析了其在Spring的核心作用。

```java
@Nullable
protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName)
    throws BeansException {

    // 检查提供的beanClass是否不为null，以及是否存在任何InstantiationAwareBeanPostProcessor
    if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {

        // 遍历所有的SmartInstantiationAwareBeanPostProcessor
        for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
            
            // 使用当前的BeanPostProcessor获取候选的构造函数
            Constructor<?>[] ctors = bp.determineCandidateConstructors(beanClass, beanName);
            
            // 如果找到了合适的构造函数，直接返回它们
            if (ctors != null) {
                return ctors;
            }
        }
    }
    
    // 如果没有找到合适的构造函数，或beanClass为null，或没有相应的BeanPostProcessor，返回null
    return null;
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance`方法中的步骤3。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#instantiateBean`方法中，主要用于根据提供的bean定义来实例化一个新的bean，并返回一个包装了该bean实例的`BeanWrapper`。这允许对bean实例进行进一步的操作，例如属性注入。

```java
protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
    try {
        Object beanInstance;

        if (System.getSecurityManager() != null) {
            // ... [代码部分省略以简化]
        }
        else {
            // 如果不存在，使用实例化策略来创建bean实例
            beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this);
        }

        // 使用创建的bean实例初始化BeanWrapper
        BeanWrapper bw = new BeanWrapperImpl(beanInstance);
        // 初始化BeanWrapper，可以设置一些自定义的属性编辑器等
        initBeanWrapper(bw);

        // 返回包装了bean实例的BeanWrapper
        return bw;
    }
    catch (Throwable ex) {
        // 处理创建bean实例过程中可能发生的异常
        // ... [代码部分省略以简化]
    }
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中的步骤2。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`方法中，在Spring框架中，`MergedBeanDefinitionPostProcessor`是一个关键接口，负责在bean实例化前对其定义进行调整和合并。为了深入了解这一机制和其在Spring中的重要性，建议查看：[**Bean定义的动态处理MergedBeanDefinitionPostProcessor**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)。这篇文章详细地探讨了该接口的源码和核心功能。

```java
protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
    // 遍历已缓存的所有MergedBeanDefinitionPostProcessor类型的后处理器
    for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
        // 调用每个后处理器的postProcessMergedBeanDefinition方法，对合并后的bean定义进行处理
        processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
    }
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中的步骤3.1。

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingletonFactory`方法中，主要目的是为一个bean名称注册一个`ObjectFactory`，这可以用于在bean真正被创建之前解决循环引用问题。当其他bean尝试早期引用这个bean时，它可以使用这个`ObjectFactory`来获取一个bean的早期引用。

```java
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
    // 确保传入的singletonFactory不为null
    Assert.notNull(singletonFactory, "Singleton factory must not be null");
    synchronized (this.singletonObjects) {
        // 如果指定名称的bean尚未在singletonObjects缓存中
        if (!this.singletonObjects.containsKey(beanName)) {
            // 将传入的singletonFactory添加到singletonFactories缓存中
            this.singletonFactories.put(beanName, singletonFactory);
            // 从earlySingletonObjects缓存中移除指定bean名称，因为它现在已有一个完整的ObjectFactory
            this.earlySingletonObjects.remove(beanName);
            // 将bean名称添加到registeredSingletons集合中，标记它已被注册
            this.registeredSingletons.add(beanName);
        }
    }
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中的步骤4.1。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean`方法中，主要用于填充bean的属性。它会遍历所有的`InstantiationAwareBeanPostProcessors`，并调用它们的`postProcessAfterInstantiation`和`postProcessProperties`方法来后处理bean的属性。如果`InstantiationAwareBeanPostProcessor`返回`false`或`null`属性值，则提前结束bean属性的设置。

```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
    // ... [代码部分省略以简化]

    // 如果当前的bean不是合成的，并且存在InstantiationAwareBeanPostProcessors
    if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
        // 遍历所有的InstantiationAwareBeanPostProcessors
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            // 调用postProcessAfterInstantiation方法
            if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                // 如果返回false，则提前结束bean属性的设置
                return;
            }
        }
    }

    // 获取bean定义中的属性值
    PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

    // ... [代码部分省略以简化]

    // 检查是否有InstantiationAwareBeanPostProcessors
    boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
    // 确定是否需要进行依赖性检查
    boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

    PropertyDescriptor[] filteredPds = null;
    if (hasInstAwareBpps) {
        // 如果没有属性值，则从bean定义中获取
        if (pvs == null) {
            pvs = mbd.getPropertyValues();
        }
        // 遍历所有的InstantiationAwareBeanPostProcessors
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            // 调用postProcessProperties方法处理属性值
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
    // 如果需要进行依赖性检查
    if (needsDepCheck) {
        if (filteredPds == null) {
            filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
        }
        checkDependencies(beanName, mbd, filteredPds, pvs);
    }

    // 最后，将处理后的属性值应用到bean实例上
    if (pvs != null) {
        applyPropertyValues(beanName, mbd, bw, pvs);
    }
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中的步骤4.2。

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean(beanName, bean,mbd)`方法中，主要负责bean的初始化过程，包括调用Aware接口方法、执行`BeanPostProcessors`的初始化前后方法以及bean的自定义初始化方法。

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
    // 如果存在SecurityManager，执行相应的安全代码（省略部分）
    if (System.getSecurityManager() != null) {
        // ... [代码部分省略以简化]
    }
    else {
        // 如果bean实现了特定的Aware接口（如BeanNameAware, BeanFactoryAware等），则调用相应的方法
        invokeAwareMethods(beanName, bean);
    }

    // 初始化前的预处理
    // 如果bean不是合成的，调用所有BeanPostProcessors的postProcessBeforeInitialization方法
    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }

    try {
        // 调用bean的初始化方法，例如afterPropertiesSet和custom init-method
        invokeInitMethods(beanName, wrappedBean, mbd);
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    // 初始化后的后处理
    // 如果bean不是合成的，调用所有BeanPostProcessors的postProcessAfterInitialization方法
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    // 返回最终的bean实例，可能被AOP代理等包装
    return wrappedBean;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeAwareMethods`方法中，在Spring框架中，`Aware`接口赋予beans与容器交互的能力，如获取其名字、类加载器或与bean工厂的交互。为更深入地探究这些接口，我推荐我们查看以下文章，它们详细分析了这些`Aware`接口在Spring中的实现：

- [**获取Bean名称BeanNameAware**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-aware/spring-aware-beanNameAware) - 这个接口使bean能够获取其在Spring容器中的名字。
- [**获取类加载器BeanClassLoaderAware**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-aware/spring-aware-beanClassLoaderAware) - 通过这个接口，bean可以获得与其相关的类加载器的引用。
- [**与Bean工厂互动BeanFactoryAware**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-aware/spring-aware-beanFactoryAware) - 这个接口让bean可以与其所在的bean工厂或应用上下文互动。

```java
private void invokeAwareMethods(String beanName, Object bean) {
    // 检查bean是否实现了Aware接口
    if (bean instanceof Aware) {
        // 如果bean实现了BeanNameAware接口，设置bean的名字
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).setBeanName(beanName);
        }
        // 如果bean实现了BeanClassLoaderAware接口，设置bean的类加载器
        if (bean instanceof BeanClassLoaderAware) {
            ClassLoader bcl = getBeanClassLoader();
            if (bcl != null) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
            }
        }
        // 如果bean实现了BeanFactoryAware接口，设置bean的工厂
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
        }
    }
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`方法中，在Spring中，`BeanPostProcessor`接口提供了在bean初始化过程中进行拦截的能力。要深入了解其工作原理，建议阅读：[**调整Bean属性BeanPostProcessor**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-beanPostProcessor)。这篇文章详细解析了其在Spring中的关键作用。

```java
@Override
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
    throws BeansException {
	// 设置当前bean为传入的bean
    Object result = existingBean;  

    // 遍历容器中所有的BeanPostProcessors
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
        // 调用每个BeanPostProcessor的postProcessBeforeInitialization方法
        Object current = processor.postProcessBeforeInitialization(result, beanName);

        // 如果postProcessBeforeInitialization返回null，则直接返回原bean
        if (current == null) {
            return result;
        }
        
        result = current;  // 更新result为postProcessBeforeInitialization处理后的bean
    }

    // 返回所有BeanPostProcessors处理后的bean
    return result;
}
```

> `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中的步骤5。

在`org.springframework.beans.factory.support.AbstractBeanFactory#registerDisposableBeanIfNecessary`方法中，主要目的是为在Spring容器中管理的bean注册一个销毁回调。当容器关闭并且bean需要清理资源或执行其他销毁逻辑时，这个销毁回调会被调用。

```java
protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
    // ... [代码部分省略以简化]
    registerDisposableBean(beanName, new DisposableBeanAdapter(
                bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, acc));
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerDisposableBean`方法中，将给定的bean名字和对应的`DisposableBean`实例放入`disposableBeans`映射中。这个映射会在容器关闭时被遍历，所有的`DisposableBean`实例的`destroy`方法会被调用，以确保资源得到适当的释放和bean得到适当的销毁。

```java
public void registerDisposableBean(String beanName, DisposableBean bean) {
    synchronized (this.disposableBeans) {
        this.disposableBeans.put(beanName, bean);
    }
}
```

> `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`方法中的步骤3。

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#afterSingletonCreation`方法中，此方法确保bean的创建过程是线程安全的，并保护系统免受不正确的并发访问，特别是当多个线程试图同时访问或修改同一个bean的状态时。

```java
protected void afterSingletonCreation(String beanName) {
    // 检查给定的bean名称是否在排除列表中，如果不是，继续检查该bean是否正在创建
    if (!this.inCreationCheckExclusions.contains(beanName) 
        // 尝试从表示“当前正在创建的单例bean”集合中移除给定的bean名称
        && !this.singletonsCurrentlyInCreation.remove(beanName)) {
        // 如果给定的bean名称无法从集合中移除，说明在此时该bean不应该在创建中。
        // 这可能表示bean的创建有问题或被错误地标记为“当前正在创建”，因此抛出异常。
        throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
    }
}
```

> `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`方法中的步骤4。

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingleton`方法中，处理了与单例bean生命周期相关的各种缓存和集合。

```java
protected void addSingleton(String beanName, Object singletonObject) {
    // 使用`synchronized`块确保多线程环境中对单例对象的线程安全操作
    synchronized (this.singletonObjects) {
        // 将新创建的单例对象添加到`singletonObjects`缓存中
        this.singletonObjects.put(beanName, singletonObject);
        
        // 从`singletonFactories`中移除bean名称，因为现在我们已经完成了该bean的完整实例化
        this.singletonFactories.remove(beanName);
        
        // 从`earlySingletonObjects`中移除bean名称，因为该bean现在已经完全初始化并存储在`singletonObjects`中
        this.earlySingletonObjects.remove(beanName);
        
        // 将bean名称添加到`registeredSingletons`集合中，以表示该bean已经被注册为一个单例
        this.registeredSingletons.add(beanName);
    }
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤8.3。

在`org.springframework.beans.factory.support.AbstractBeanFactory#getObjectForBeanInstance`方法中，根据提供的bean实例和名称，要么返回bean实例本身，要么从`FactoryBean`中获取对象。同时，它还处理了与工厂bean缓存相关的各种细节。

```java
protected Object getObjectForBeanInstance(
        Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

    // 检查名称是否有工厂的解引用前缀（例如'&'）并且bean实例不是工厂
    if (BeanFactoryUtils.isFactoryDereference(name)) {
        // 当bean实例是NullBean时，直接返回bean实例
        if (beanInstance instanceof NullBean) {
            return beanInstance;
        }
        // 如果bean实例不是一个FactoryBean，抛出异常
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
        }
        // 如果bean定义不为null，标记它为一个FactoryBean
        if (mbd != null) {
            mbd.isFactoryBean = true;
        }
        return beanInstance;
    }

    // 如果bean实例不是一个FactoryBean，则直接返回bean实例
    if (!(beanInstance instanceof FactoryBean)) {
        return beanInstance;
    }

    Object object = null;
    // 如果bean定义不为null，标记它为一个FactoryBean
    if (mbd != null) {
        mbd.isFactoryBean = true;
    }
    // 如果没有提供bean定义，则尝试从缓存中获取工厂bean生成的对象
    else {
        object = getCachedObjectForFactoryBean(beanName);
    }
    // 如果缓存中没有对象，则需要从FactoryBean中获取
    if (object == null) {
        FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
        // 如果存在bean定义并且没有为给定的beanName缓存对象，则获取合并的bean定义
        if (mbd == null && containsBeanDefinition(beanName)) {
            mbd = getMergedLocalBeanDefinition(beanName);
        }
        // 检查bean定义是否为合成的（例如，由基础设施代码创建的）
        boolean synthetic = (mbd != null && mbd.isSynthetic());
        // 从FactoryBean获取对象
        object = getObjectFromFactoryBean(factory, beanName, !synthetic);
    }
    return object;
}
```

> `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤9。

在`org.springframework.beans.factory.support.AbstractBeanFactory#adaptBeanInstance`方法中，目的是确保给定的bean实例与指定的目标类型匹配。如果它们不匹配，此方法将尝试使用类型转换器将bean实例转换为所需的类型。如果转换失败，它将抛出一个异常。

```java
<T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> requiredType) {
    // 检查所需的类型是否与实际bean实例的类型匹配
    if (requiredType != null && !requiredType.isInstance(bean)) {
        try {
            // 如果不匹配，尝试转换bean实例为所需的类型
            Object convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
            // 如果转换后的bean为null，抛出异常
            if (convertedBean == null) {
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
            return (T) convertedBean;
        }
        catch (TypeMismatchException ex) {
            // 如果类型转换失败，记录trace日志并抛出异常
            if (logger.isTraceEnabled()) {
                logger.trace("Failed to convert bean '" + name + "' to required type '" +
                             ClassUtils.getQualifiedName(requiredType) + "'", ex);
            }
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        }
    }
    // 如果bean实例的类型与所需的类型匹配，直接返回bean实例
    return (T) bean;
}
```

### 七、注意事项

1. **Bean的存在性**
   + 确保bean确实已经在Spring上下文中定义了。如果没有，`NoSuchBeanDefinitionException`将被抛出。
2. **正确的Bean名称**
   + 确保我们使用的名称是bean的正确ID或名称。Spring的bean名称默认是非限定类名的首字母小写，但如果在bean定义中指定了不同的名称，我们应该使用那个。
3. **Bean的生命周期**
   + `getBean()`方法每次都可能返回不同的实例或相同的实例，具体取决于bean的范围（singleton、prototype等）。
4. **类型安全**
   + 使用`getBean(name, class)`可以确保返回的bean是期望的类型，从而避免在运行时出现类转换异常。
5. **循环依赖**
   + 如果在bean的依赖关系中存在循环依赖，`getBean`可能会失败，并抛出`BeanCurrentlyInCreationException`。
6. **Lazy初始化**
   + 对于懒惰初始化的bean，第一次调用`getBean()`会触发bean的创建和初始化。
7. **可能的副作用**
   + 因为`getBean()`可以触发bean的创建和初始化，所以可能会有副作用，例如数据库连接、文件IO或其他资源的初始化。
8. **不要过度使用**
   + 在一个Spring管理的bean中频繁调用`getBean()`并不是一个好的实践。这违背了控制反转的原则，可能导致代码难以测试和维护。我们应该尽可能地依赖注入，而不是显式地从容器中获取bean。
9. **线程安全性**
   + 虽然`getBean()`方法是线程安全的，但返回的bean可能不是，除非我们确保它是线程安全的。
10. **生命周期回调**
    + 请记住，当我们通过`getBean`方法创建一个新的bean实例时（例如，范围为prototype的bean），Spring将不会管理该bean的完整生命周期。特别是，Spring不会调用prototype bean的销毁方法。

### 八、总结

#### 最佳实践总结

1. **使用正确的上下文环境**
   + 选择`AnnotationConfigApplicationContext`作为Spring上下文环境，这是专为Java注解配置的Spring容器。
2. **定义配置类**
   + 建立一个配置类如`MyConfiguration`，使用`@Configuration`注解标记它。这将告诉Spring，该类包含bean的配置信息。
3. **启用组件扫描**
   + 在配置类上使用`@ComponentScan`，并为其提供需要扫描的包名。这允许Spring自动检测带有特定注解的类，并将其注册为bean。
4. **定义Bean**
   + 在目标类（如服务类）上使用`@Component`或其他相关注解（如`@Service`, `@Repository`, `@Controller`等）。这确保Spring可以识别它们并自动将它们添加到容器中。
5. **获取和使用Bean**
   + 在应用程序入口中，初始化上下文并使用`context.getBean()`方法从Spring容器中获取bean。
6. **查看结果**
   + 运行应用程序并确认输出，确保Spring正确地识别并初始化了期望的bean。

#### 源码分析总结

1. **获取Bean定义**
   + 通过`getBean`方法，Spring提供了一个方式让调用者基于bean的名称从Spring IoC容器中检索bean。
2. **获取Bean名称**
   + `transformedBeanName`方法处理了bean名称的转换，确保返回的名称是规范的并处理了可能的别名。别名处理由`canonicalName`方法完成，这个方法不断地从别名映射中查找真实的bean名称。
3. **检索单例Bean**
   + `getSingleton`方法尝试从缓存中检索单例bean。它会考虑完全初始化的bean、早期引用以及单例工厂缓存的bean。此方法在解决循环依赖问题时特别有用。
4. **处理原型Bean**
   + `isPrototypeCurrentlyInCreation`方法检查特定的bean是否正在创建中的原型beans列表中，以处理原型bean的循环引用。
5. **标记Bean已创建**
   + `markBeanAsCreated`方法标记了指定的bean已经被创建或正在创建，这在Spring的bean生命周期中起到了关键作用。
6. **获取合并的Bean定义**
   + `getMergedLocalBeanDefinition`方法负责获取给定bean名称的合并bean定义。合并的bean定义是从父bean和子bean定义中合并的结果。而`getMergedBeanDefinition`进一步处理了bean定义的父子关系并返回了合并后的bean定义。
7. **处理@DependsOn注解**
   + 如果bean定义中指定了@DependsOn注解，Spring会确保在当前bean之前创建它所依赖的其他beans。该处理包括：检查是否存在循环依赖，在Bean工厂中注册bean之间的依赖关系，确保每个被依赖的bean都已经被创建。
8. **Singleton Bean的缓存获取**
   - 在`DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`方法中，Spring首先尝试从缓存中检索该bean。若无法在缓存中找到，它会使用提供的`singletonFactory`来创建bean。创建的bean会被加入到缓存中，这保证了其单例性。
9. **处理循环引用**
   - 在`beforeSingletonCreation`方法中，Spring确保当前bean不会被多次创建，这样可以避免因循环引用导致的问题。
10. **创建Bean实例**
    - `AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`是bean创建的核心方法。在这里，Spring会考虑工厂方法、构造函数注入等多种方式来实例化bean。同时，此处还会处理前置和后置处理器。
11. **处理Bean实例化之前的逻辑**
    - 在`resolveBeforeInstantiation`方法中，`InstantiationAwareBeanPostProcessor`后处理器可能会拦截bean的标准实例化流程。这主要用于如AOP的场景。
12. **Bean的实例化、属性注入、初始化**
    - `doCreateBean`方法处理了bean生命周期中的多个关键阶段，从bean的实例化、属性注入、初始化，到bean的清理注册。
13. **选择构造函数并实例化bean**
    - 在`createBeanInstance`方法中，Spring首先尝试从后处理器获取构造函数。如果没有找到合适的构造函数，它可能会使用无参数构造函数，或者考虑其他逻辑，如首选构造函数。
14. **智能实例化策略**
    - `determineConstructorsFromBeanPostProcessors`方法中，通过`SmartInstantiationAwareBeanPostProcessor`，Spring可以调整构造函数选择，提供更加智能的bean实例化策略。
15. **直接实例化bean**
    - `instantiateBean`方法是一个简单的bean实例化过程，通常用于没有特定构造函数或工厂方法的bean。

16. **`MergedBeanDefinitionPostProcessor`处理**：
    - 在`applyMergedBeanDefinitionPostProcessors`方法中，`MergedBeanDefinitionPostProcessor`接口用于在bean实例化前对其定义进行处理和调整。

17. **处理循环引用**：
    - `addSingletonFactory`方法注册一个`ObjectFactory`，旨在解决bean创建前的循环引用问题。

18. **填充bean属性**：
    - `populateBean`方法负责填充bean的属性。它遍历所有的`InstantiationAwareBeanPostProcessors`，调用它们的方法进行bean属性的后处理。

19. **bean初始化**：

    - `initializeBean`方法处理bean的初始化，包括调用Aware接口方法、执行`BeanPostProcessors`的初始化前后方法，以及bean的自定义初始化方法。

    - `invokeAwareMethods`方法处理bean的Aware接口调用，让bean可以获得Spring容器提供的一些能力。

20. **注册bean的销毁方法**：
    - `registerDisposableBeanIfNecessary`方法负责为bean注册一个销毁回调。当容器关闭并需要清理资源或执行其他销毁逻辑时，这个回调会被触发。

21. **保护并发bean创建**：
    - `afterSingletonCreation`方法确保bean创建过程是线程安全的，并保护系统免受不正确的并发访问。

22. **处理单例bean的生命周期**：
    - `addSingleton`方法处理与单例bean生命周期相关的各种缓存和集合。

23. **获取或转换bean实例**：

    - `getObjectForBeanInstance`方法根据提供的bean实例和名称，要么返回bean实例本身，要么从`FactoryBean`中获取对象。

    - `adaptBeanInstance`方法确保bean实例与指定的目标类型匹配，如果不匹配，它将尝试转换bean实例。
