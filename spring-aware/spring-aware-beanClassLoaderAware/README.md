## BeanClassLoaderAware

- [BeanClassLoaderAware](#beanclassloaderaware)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133916700) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [BeanClassLoaderAware源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-beanClassLoaderAware)

### 二、接口描述

`BeanClassLoaderAware` 接口，允许 bean 得知其加载的类加载器。当一个 bean 实现了这个接口时，Spring 容器在 bean 初始化的时候会设置它的类加载器。

### 三、接口源码

`BeanClassLoaderAware` 是 Spring 框架自 2.0 版本开始引入的一个核心接口。当一个 bean 实现了这个接口，并在 Spring 容器中被初始化时，Spring 容器会自动调用 `setClassLoader` 方法，并将加载该 bean 的类加载器传入。

```java
/**
 * 回调接口，允许 bean 了解其所使用的 ClassLoader 类加载器；
 * 也就是当前 bean 工厂用于加载 bean 类的类加载器。
 * 
 * 主要目的是供那些需要按名称选择应用类的框架类实现，尽管这些框架类可能是由共享的类加载器加载的。
 *
 * 对于所有 bean 生命周期方法的列表，请参阅 BeanFactory BeanFactory 的 javadocs。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.0
 * @see BeanNameAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanClassLoaderAware extends Aware {

    /**
     * 提供 bean 的 ClassLoader 类加载器 的回调方法。
     * 在填充普通的 bean 属性之后但在初始化回调（如 InitializingBean InitializingBean 的
     * InitializingBean#afterPropertiesSet() 方法或自定义初始化方法）之前调用此方法。
     * 
     * @param classLoader 拥有的类加载器
     */
    void setBeanClassLoader(ClassLoader classLoader);

}
```

### 四、主要功能

1. **提供类加载器信息**
   + Bean 可以得知其加载的类加载器，从而可以利用该类加载器进行动态的类加载或资源查找。

2. **框架与应用类加载器隔离**
   + 在某些复杂的环境中，框架类和应用程序类可能是由不同的类加载器加载的。例如，在某些应用服务器环境中，框架可能是由共享的类加载器加载的，而应用程序类是由专门的类加载器加载的。通过 `BeanClassLoaderAware`，框架类可以确保使用正确的类加载器来加载或访问应用程序类。

3. **生命周期管理**
   + `setBeanClassLoader` 方法会在填充 bean 的普通属性之后但在调用任何初始化回调（如 `InitializingBean#afterPropertiesSet()`）之前被调用。这确保了在 bean 的生命周期的合适阶段提供类加载器信息。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyBeanClassLoaderAware`类型的bean，最后调用`loadAndExecute`方法。

```java
public class BeanClassLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBeanClassLoaderAware myBeanClassLoaderAware = context.getBean(MyBeanClassLoaderAware.class);
        myBeanClassLoaderAware.loadAndExecute();
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保`MyBeanClassLoaderAware` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyBeanClassLoaderAware myBeanClassLoaderAware(){
        return new MyBeanClassLoaderAware();
    }
}
```

在`MyBeanClassLoaderAware` 类中，我们实现了 `BeanClassLoaderAware` 接口，允许这个 bean 在初始化时获取其 `ClassLoader`。接着，在 `loadAndExecute` 方法中，我们使用这个 `ClassLoader` 来动态加载一个名为 `com.xcs.spring.service.UserServiceImpl` 的类并执行它的 `getUserInfo` 方法。

```java
public class MyBeanClassLoaderAware implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void loadAndExecute() {
        try {
            Class<?> clazz = classLoader.loadClass("com.xcs.spring.service.UserServiceImpl");
            UserServiceImpl instance = (UserServiceImpl) clazz.getDeclaredConstructor().newInstance();
            System.out.println("UserInfo = " + instance.getUserInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

定义一个接口与此接口的实现类。

```java
package com.xcs.spring.service;

public interface UserService {
    String getUserInfo();
}

public class UserServiceImpl implements UserService {
    @Override
    public String getUserInfo() {
        return "this is user info";
    }
}
```

运行结果发现，通过这种方式，我们保证了`MyBeanClassLoaderAware`的代码与`UserServiceImpl`的具体实现解耦。这意味着，如果`UserServiceImpl`的具体实现发生了变化，或者有了新的实现，只要我们遵循`UserService`接口，我们的`MyBeanClassLoaderAware`代码就不需要任何更改。

```java
UserInfo = this is user info
```

### 六、时序图

~~~mermaid
sequenceDiagram
    Title: BeanClassLoaderAware时序图
    participant BeanFactoryAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant BeanClassLoaderAware
    
    BeanFactoryAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
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
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeAwareMethods(beanName, bean)<br>调用Aware方法
    AbstractAutowireCapableBeanFactory->>BeanClassLoaderAware:setBeanClassLoader(classLoader)<br>设置classLoader
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>BeanFactoryAwareApplication:初始化完成

~~~

### 七、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyBeanClassLoaderAware`类型的bean，最后调用`loadAndExecute`方法。

```java
public class BeanClassLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBeanClassLoaderAware myBeanClassLoaderAware = context.getBean(MyBeanClassLoaderAware.class);
        myBeanClassLoaderAware.loadAndExecute();
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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`方法中，`invokeAwareMethods(beanName, bean)`是一个非常重要的步骤。这个方法是为了处理实现了Spring的`Aware`接口族的Beans（例如`BeanNameAware`, `BeanFactoryAware`等）。如果提供的bean实现了任何这些接口，该方法会回调相应的`Aware`方法。

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [代码部分省略以简化]
    
    invokeAwareMethods(beanName, bean);
    
    // ... [代码部分省略以简化]

    return wrappedBean;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeAwareMethods`方法中，用于处理实现了 Spring `Aware` 接口族的 beans。当一个 bean 实现了如 `BeanNameAware`、`BeanClassLoaderAware` 或 `BeanFactoryAware` 等接口时，此方法确保正确的回调方法被调用，从而为 bean 提供关于其运行环境或其他相关信息。

```java
private void invokeAwareMethods(String beanName, Object bean) {
    if (bean instanceof Aware) {
        if (bean instanceof BeanNameAware) {
            // ... [代码部分省略以简化]
        }
        if (bean instanceof BeanClassLoaderAware) {
            ClassLoader bcl = getBeanClassLoader();
            if (bcl != null) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
            }
        }
        if (bean instanceof BeanFactoryAware) {
            // ... [代码部分省略以简化]
        }
    }
}
```

最后执行到我们自定义的逻辑中，我们实现了 `BeanClassLoaderAware` 接口，允许这个 bean 在初始化时获取其 `ClassLoader`。

```java
public class MyBeanClassLoaderAware implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void loadAndExecute() {
        try {
            Class<?> clazz = classLoader.loadClass("com.xcs.spring.service.UserServiceImpl");
            UserServiceImpl instance = (UserServiceImpl) clazz.getDeclaredConstructor().newInstance();
            System.out.println("UserInfo = " + instance.getUserInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 八、注意事项

1. **避免过度使用**
   + 只有当我们真的需要访问类加载器时才使用 `BeanClassLoaderAware`。不要滥用它，因为这可能会导致代码与Spring框架过度耦合。

2. **考虑类加载器层次结构**
   + 在Java中，类加载器通常有一个父子关系。如果我们不能使用当前的类加载器找到一个类，可能需要检查其父类加载器。

3. **不要在setter中执行复杂的逻辑**
   + `setBeanClassLoader` 是一个setter方法，应该避免在其中执行复杂的逻辑。它应该只用于设置类加载器。

4. **避免存储状态**
   + 尽量不要在实现了`BeanClassLoaderAware`的bean中存储状态或依赖于其他bean的状态。这会使bean的生命周期和初始化更加复杂。

5. **考虑使用其他技术**
   + 在某些情况下，可能有其他技术或方法可以达到同样的目的，而无需使用 `BeanClassLoaderAware`。例如，使用Spring的`@Value`注解或`ResourceLoader`来加载资源，而不是直接使用类加载器。

6. **考虑类加载器层次结构**
   + 在Java中，类加载器通常有一个父子关系。如果我们不能使用当前的类加载器找到一个类，可能需要检查其父类加载器。

7. **资源管理**
   + 类加载器不仅可以加载类，还可以用来加载其他资源（例如，属性文件）。但是，要小心确保资源路径正确，并记住类加载器的搜索行为可能与文件系统或其他加载机制不同。

### 九、总结

#### 最佳实践总结

1. **启动及上下文配置**
   + 利用 `AnnotationConfigApplicationContext` 初始化Spring容器，使用 `MyConfiguration` 作为配置类来为Spring上下文提供设置。

2. **配置类定义**
   + 标记配置类为 `@Configuration`，使用 `@Bean` 注解来确保 `MyBeanClassLoaderAware` 被Spring容器管理。

3. **实现 `BeanClassLoaderAware`**
   + 通过实现 `BeanClassLoaderAware` 接口，bean 可以在初始化时获得其加载的 `ClassLoader`，在 `loadAndExecute` 方法中，动态加载并执行特定的服务方法。

4. **接口与实现**
   + 定义清晰的 `UserService` 接口和相应的 `UserServiceImpl` 实现。

5. **结果及结论**
   + 运行程序后，我们能够看到预期输出，这表明成功地将 `MyBeanClassLoaderAware` 与特定实现解耦。

#### 源码分析总结

1. **应用启动入口**
   + 通过`AnnotationConfigApplicationContext`，以`MyConfiguration`为配置类，初始化Spring容器。随后获取`MyBeanClassLoaderAware` bean并调用其`loadAndExecute`方法。

2. **初始化Spring上下文**
   + 在`AnnotationConfigApplicationContext`构造函数中，`refresh()`方法被调用来刷新或初始化Spring容器。

3. **Bean的预实例化**
   + 在Spring的上下文初始化的`refresh()`方法中，`finishBeanFactoryInitialization(beanFactory)`方法确保所有非延迟加载的单例bean被实例化。

4. **单例Bean的创建**
   + `DefaultListableBeanFactory`中的`preInstantiateSingletons`方法负责预先实例化所有非懒加载的单例bean。它会对容器中的每个单例bean调用`getBean`方法。

5. **Bean的实例化及初始化**
   + 在获取bean的过程中，如果bean还未创建，`doCreateBean`方法会被调用，完成bean的实例化、属性填充和初始化。

6. **处理Aware接口族**
   + 在bean的初始化阶段，`invokeAwareMethods`方法确保任何实现了`Aware`接口族（如`BeanNameAware`、`BeanClassLoaderAware`等）的bean都会得到适当的回调。

7. **BeanClassLoaderAware的实现**
   + 对于实现了`BeanClassLoaderAware`接口的bean，Spring容器在初始化阶段会通过`setBeanClassLoader`方法设置bean的`ClassLoader`。

8. **自定义逻辑的执行**
   + 在`MyBeanClassLoaderAware`中，已经保存了bean的类加载器，然后在`loadAndExecute`方法中，利用这个类加载器动态加载并执行特定的类和方法。