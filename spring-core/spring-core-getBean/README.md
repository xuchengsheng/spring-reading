## getBean

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN]() 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [getBean源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-core/spring-core-getBean)

### 二、方法描述

在 Spring 框架中，`getBean` 方法是 `ApplicationContext` 接口中的一个核心方法，用于从 Spring 容器中检索 bean。Spring 的核心是控制反转（Inversion of Control, IoC）和依赖注入（Dependency Injection, DI），`getBean` 方法正是实现这两个核心概念的重要方法。

### 三、方法源码

这个方法的定义和说明表明了 Spring IoC 容器的一些核心概念和工作机制。当你请求一个 bean 时，Spring 会查找该 bean、处理任何别名、检查其作用域（例如，单例或原型），并最终返回适当的 bean 实例给调用者。

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

### 四、主要功能

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

### 五、最佳实践

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

### 六、时序图

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

### 七、源码分析

在`org.springframework.beans.factory.support.AbstractBeanFactory#getBean(name)`方法中，这个方法内部调用了`doGetBean`，并且传递了四个参数。

```java
@Override
public Object getBean(String name) throws BeansException {
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
                        throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                                        "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
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
                        return createBean(beanName, mbd, args);
                    }
                    catch (BeansException ex) {
                        // ... [代码部分省略以简化]
                    }
                });
                // 步骤8.2: 获取bean实例本身
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

首先来到在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤1。在`org.springframework.beans.factory.support.AbstractBeanFactory#transformedBeanName`方法中，主要是用来转换bean名称的。Spring允许在引用bean时使用特定的前缀，例如`&`前缀用于表示一个bean是`FactoryBean`，这样我们可以通过`&beanName`来获取`FactoryBean`对象本身，而不是它生产的对象。

```java
protected String transformedBeanName(String name) {
    return canonicalName(BeanFactoryUtils.transformedBeanName(name));
}
```

在`org.springframework.core.SimpleAliasRegistry#canonicalName`方法中，主要是用来获取bean的规范名称的。在Spring中，bean可以有多个别名，这个方法的目的是从别名链中找到最终的规范名称。

```java
public String canonicalName(String name) {
    String canonicalName = name;
    // Handle aliasing...
    String resolvedName;
    do {
        resolvedName = this.aliasMap.get(canonicalName);
        if (resolvedName != null) {
            canonicalName = resolvedName;
        }
    }
    while (resolvedName != null);
    return canonicalName;
}
```

然后来到在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤2。在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName)`方法中，又调用了另外一个重载版本，还接受一个`boolean`参数，表示当bean正在被创建但尚未完成时，是否允许提前暴露这个bean的引用。传递`true`作为这个参数意味着是允许的。

```java
@Override
@Nullable
public Object getSingleton(String beanName) {
    return getSingleton(beanName, true);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,allowEarlyReference)`方法中，首先，它从单例缓存中尝试获取bean。如果bean正在创建中，方法会从提前暴露的缓存中获取它以处理循环引用。为确保线程安全性，在同步块内进行进一步的检查和bean的创建。如果bean仍未被创建，但存在一个工厂，那么它会用这个工厂创建bean。最后，方法返回找到或新创建的bean，或者在没有找到bean时返回`null`。

```java
@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // 1. 从已完成的单例缓存中初步检查bean
    Object singletonObject = this.singletonObjects.get(beanName);

    // 2. 检查bean是否正在创建中，以处理可能的循环引用
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        // 3. 尝试从提前暴露的单例缓存中获取bean
        singletonObject = this.earlySingletonObjects.get(beanName);

        // 4. 如果仍然没有获取到bean，且允许提前引用，则进一步处理
        if (singletonObject == null && allowEarlyReference) {
            // 5. 为了确保线程安全，使用同步块
            synchronized (this.singletonObjects) {
                // 6. 在同步块内再次检查缓存，以确保线程安全
                singletonObject = this.singletonObjects.get(beanName);

                if (singletonObject == null) {
                    singletonObject = this.earlySingletonObjects.get(beanName);

                    // 7. 如果仍然没有找到，尝试使用singletonFactory创建bean
                    if (singletonObject == null) {
                        ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                        if (singletonFactory != null) {
                            // 8. 使用singletonFactory创建bean
                            singletonObject = singletonFactory.getObject();
                            // 9. 将新创建的bean添加到提前暴露的单例缓存中
                            this.earlySingletonObjects.put(beanName, singletonObject);
                            // 10. 从singletonFactories缓存中移除beanName，因为它已经被使用
                            this.singletonFactories.remove(beanName);
                        }
                    }
                }
            }
        }
    }

    // 11. 返回找到或创建的单例bean
    return singletonObject;
}
```

然后来到在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤3。在`org.springframework.beans.factory.support.AbstractBeanFactory#isPrototypeCurrentlyInCreation`方法中，用于确定指定名称的原型bean是否当前正在创建中。

```java
protected boolean isPrototypeCurrentlyInCreation(String beanName) {
    Object curVal = this.prototypesCurrentlyInCreation.get();
    return (curVal != null &&
            (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName))));
}
```

然后来到在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤5。在`org.springframework.beans.factory.support.AbstractBeanFactory#markBeanAsCreated`方法中，主要目的是标记指定的bean已经被创建或正在被创建。它在Spring的bean生命周期中起到关键作用，特别是当需要确保bean只被创建一次或者对其进行某些状态检查时。

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

然后来到在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤6。在`org.springframework.beans.factory.support.AbstractBeanFactory#getMergedLocalBeanDefinition`方法中，主要是用于获取给定bean名称的合并bean定义。合并的bean定义是从父bean和子bean（如果有的话）定义中合并的结果。

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

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#getBeanDefinition`方法中，主要用于查找bean定义。

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

在`org.springframework.beans.factory.support.AbstractBeanFactory#getMergedBeanDefinition(beanName, bd)`方法中，我们传递了`null`作为父bean定义，表示不需要合并父bean定义。

```java
protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd)
			throws BeanDefinitionStoreException {

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

然后来到在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤7.1。Spring处理了`@DependsOn`注解。这个注解允许开发者定义bean初始化之间的依赖关系，确保一个bean在另一个bean之前被初始化和销毁。为了深入了解`@DependsOn`注解的工作机制及其在Spring中的实现，您可以查阅已经准备好的专门分析该注解的博客文章。

[@DependsOn注解详解](https://github.com/xuchengsheng/spring-reading/blob/master/spring-annotation/spring-annotation-dependsOn)

```java
String[] dependsOn = mbd.getDependsOn();
if (dependsOn != null) {
    for (String dep : dependsOn) {
        // 步骤7.1: 是否存在循环依赖
        if (isDependent(beanName, dep)) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                            "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
        }
        // 步骤7.2: 注册Bean与Bean之间的依赖关系
        registerDependentBean(dep, beanName);
        // 步骤7.3: 获取被依赖的Bean对象
        getBean(dep);
    }
}
```

然后来到在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中的步骤8.1。在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName, singletonFactory)`方法中，首先尝试从缓存中检索该bean。如果没有找到，它会使用提供的`singletonFactory`来创建这个bean，并在创建过程中进行前置和后置处理，以确保处理诸如循环引用等问题。创建的bean会被添加到缓存中。此外，该方法还处理了在创建过程中可能出现的各种异常，并确保在多线程环境中的线程安全。最后，返回所需的单例bean。

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    synchronized (this.singletonObjects) {
        // 首先尝试从缓存中获取单例
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            // ... [代码部分省略以简化]
            
            // 前置处理，例如标记这个bean正在创建，以处理循环引用等问题。
            beforeSingletonCreation(beanName);
            
            // ... [代码部分省略以简化]
            try {
                // 使用singletonFactory创建单例对象
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
                afterSingletonCreation(beanName);
            }
            // 如果成功创建了新的单例bean，将其添加到缓存中
            if (newSingleton) {
                addSingleton(beanName, singletonObject);
            }
        }
        // 返回现有或新创建的单例bean
        return singletonObject;
    }
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(beanName,mbd,args)`方法中，首先尝试进行前置处理，可能返回一个由BeanPostProcessors提供的代理bean。如果没有代理bean，它会进行真正的bean创建。

`resolveBeforeInstantiation` 方法主要在实际的 bean 实例化之前尝试解析 bean。这允许`InstantiationAwareBeanPostProcessor`在 bean 的实际实例化过程开始之前对其进行操作。

在Spring中，`InstantiationAwareBeanPostProcessor` 是一个特殊类型的`BeanPostProcessor`，它提供了更多的回调方法，特别是允许在实例化bean之前以及属性填充之前对bean进行处理。例如，它可以在 bean 实例化之前返回一个代理对象，从而避免默认的 bean 实例化过程。

要深入了解这两个接口以及它们在 Spring 框架中的工作方式，您可以参考以下两篇博客文章：

1. [InstantiationAwareBeanPostProcessor 接口详解](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)
2. [BeanPostProcessor 接口详解](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-beanPostProcessor)

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {

    // ... [代码部分省略以简化]
    RootBeanDefinition mbdToUse = mbd;

    // ... [代码部分省略以简化]

    try {
        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    try {
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        // ... [代码部分省略以简化]
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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，主要处理了bean生命周期中的多个关键阶段，从bean的实例化、属性注入、初始化，到bean的清理注册。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // Step 1: 尝试实例化bean
    BeanWrapper instanceWrapper = null;
    if (mbd.isSingleton()) {
        instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
    }
    if (instanceWrapper == null) {
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    
    // ... [代码部分省略以简化]

    // Step 2: 合并bean定义的后置处理
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

    // Step 3: 处理可能的循环引用，通过提前暴露bean的引用
    boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
            isSingletonCurrentlyInCreation(beanName));
    if (earlySingletonExposure) {
        // ... [代码部分省略以简化]
        addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
    }

    // Step 4: 初始化bean实例，填充bean属性并应用后处理器
    Object exposedObject = bean;
    try {
        // 属性填充
        populateBean(beanName, mbd, instanceWrapper);
        // 初始化bean
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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance`方法中，首先尝试从后处理器获取构造函数，然后检查是否有首选构造函数，最后如果没有其他选项，它会使用无参数构造函数。

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    // ... [代码部分省略以简化]

    // Step 1: 首先尝试从BeanPostProcessors确定构造函数，这主要是为了处理例如@Autowired注解的情况
    Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
    // 如果确定了构造函数或者bean定义中有相关的自动装配模式和构造函数参数，则使用自动装配构造函数创建bean实例
    if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
        mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
        return autowireConstructor(beanName, mbd, ctors, args);
    }

    // Step 2: 如果BeanDefinition中存在首选构造函数，使用这些构造函数
    ctors = mbd.getPreferredConstructors();
    if (ctors != null) {
        return autowireConstructor(beanName, mbd, ctors, null);
    }

    // Step 3: 如果前面的步骤都没有返回bean实例，那么使用无参数构造函数实例化bean
    return instantiateBean(beanName, mbd);
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#determineConstructorsFromBeanPostProcessors`方法中，在`determineConstructorsFromBeanPostProcessors`方法中，Spring查询所有注册的`SmartInstantiationAwareBeanPostProcessor`来确定bean的构造函数。这允许开发者或Spring内部组件对bean的实例化过程有更大的控制权。特别地，`SmartInstantiationAwareBeanPostProcessor`接口扩展了Spring的标准`BeanPostProcessor`，为bean的实例化提供更为智能的预测和干预能力。例如，它可以在bean实例化之前尝试猜测最合适的构造函数，或者提供代理类来代替实际的bean实例。对于想要深入了解`SmartInstantiationAwareBeanPostProcessor`的工作原理和如何使用它的开发者来说，我推荐阅读这篇详细的博客[SmartInstantiationAwareBeanPostProcessor源码分析](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-smartInstantiationAwareBeanPostProcessor)。在这篇博客中，您将获得关于该接口及其与Spring框架交互的详尽解释。

```java
@Nullable
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

### 八、注意事项

### 九、总结

#### 最佳实践总结

#### 源码分析总结
