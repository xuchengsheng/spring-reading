## Bean的销毁过程

- [Bean的销毁过程](#bean的销毁过程)
  - [一、基本信息](#一基本信息)
  - [二、知识储备](#二知识储备)
  - [三、基本描述](#三基本描述)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)
    - [注册适配器](#注册适配器)
    - [Bean销毁](#bean销毁)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. [**Bean的定义注册过程**](https://github.com/xuchengsheng/spring-reading/tree/master/spring-core/spring-core-registerBeanDefinition)

   + Bean的注册包括加载和解析配置文件，从中提取Bean定义。解析后的Bean信息，如类名、作用域、属性等，被注册到Spring容器。通过解析配置文件，容器获得Bean的元数据，进而创建Bean定义，包括类名、作用域（如singleton或prototype）、属性值等。这些定义通过唯一标识符与容器关联，以便后续通过ApplicationContext获取和管理。

2. [**Bean的初始化过程**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-core/spring-core-getBean/README.md)

   + 通过构造函数实例化Bean，随后进行属性注入满足依赖关系。若Bean实现了Aware接口，容器通过相应回调方法注入上下文信息。注册的后置处理器在初始化前后对Bean进行额外处理。Bean实现InitializingBean接口时，容器调用afterPropertiesSet执行初始化逻辑。通过配置中的init-method属性也可指定自定义初始化方法。最终，Bean标记为已初始化状态，可被应用程序使用。

3. [**Bean的依赖解析过程**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-core/spring-core-resolveDependency/README.md)

   + 通过声明依赖，Bean表达对其他Bean的需求。容器通过查找依赖，即在ApplicationContext中寻找所需的Bean定义。然后，容器进行依赖注入，将找到的Bean实例注入到相应的属性或构造函数参数中。在处理循环依赖时，Spring通过使用提前暴露的代理对象来解决循环引用问题。最后，容器支持延迟依赖解析，即在需要使用Bean时再进行实际的依赖解析和注入，以提高性能和减少启动时间。

### 三、基本描述

容器调用实现了`DisposableBean`接口的Bean的`destroy`方法执行自定义销毁逻辑。其次，如果Bean配置中通过`destroy-method`属性指定了自定义的销毁方法，容器也会调用。接着，对于实现了`DestructionAwareBeanPostProcessor`接口的后置处理器，容器分别调用其`postProcessBeforeDestruction`和`postProcessAfterDestruction`方法，允许进行额外的清理工作。通过`@PreDestroy`注解，我们可以在Bean方法上标记销毁前的清理操作。

### 四、主要功能

1. **执行自定义销毁逻辑**

   - 调用实现了`DisposableBean`接口的Bean的`destroy`方法，执行我们定义的自定义销毁逻辑，用于释放资源或执行必要的清理工作。

2. **调用自定义销毁方法**

   - 如果Bean配置中通过`destroy-method`属性指定了自定义的销毁方法，容器会调用这个方法，允许我们在销毁时执行特定的清理操作。

3. **后置处理器清理工作**

   - 对于实现了`DestructionAwareBeanPostProcessor`接口的后置处理器，容器在销毁前后分别调用其`postProcessBeforeDestruction`和`postProcessAfterDestruction`方法，允许进行额外的清理工作。

4. **执行`@PreDestroy`注解方法**

   - 通过`@PreDestroy`注解，我们可以在Bean的方法上标记销毁前的清理操作，确保在销毁时执行特定的业务逻辑。

5. **触发销毁通知**

   - Spring容器会触发销毁通知，通知相关的监听器或观察者，允许应用程序在Bean销毁时执行特定的处理。

### 五、最佳实践

使用 Spring 的基于注解的配置方式，创建一个应用程序上下文，注册 bean，然后关闭应用程序上下文。

```java
public class DestroyBeanApplication {

    public static void main(String[] args) {
        // 创建一个基于注解的应用程序上下文对象
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册配置类 MyBean，告诉 Spring 在容器中管理这个配置类所定义的 bean
        context.register(MyBean.class);
        // 刷新应用程序上下文，初始化并启动 Spring 容器
        context.refresh();
        // 关闭应用程序上下文，销毁 Spring 容器并释放资源
        context.close();
    }
}
```

实现了 Spring 框架中 `DisposableBean` 接口的类，通过实现这个接口，我们可以在 bean 销毁时执行一些自定义的清理逻辑。

```java
public class MyBean implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        System.out.println("MyBean被销毁了");
    }
}
```

运行结果发现，当 Spring 容器关闭时，会触发 bean 的销毁阶段，而实现了 `DisposableBean` 接口的 bean 就会调用其 `destroy` 方法。在我们的 `MyBean` 类中，`destroy` 方法中只有一行代码，即打印一条消息，因此我们在运行时看到的输出就是 `"MyBean被销毁了"`。

```java
MyBean被销毁了
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: Bean的销毁过程时序图

par 注册适配器阶段
DestroyBeanApplication->>AbstractApplicationContext: refresh()
Note over AbstractApplicationContext: 初始化应用程序上下文

AbstractApplicationContext->>AbstractApplicationContext: finishBeanFactoryInitialization(beanFactory)
Note over AbstractApplicationContext: 完成bean工厂的初始化

AbstractApplicationContext->>DefaultListableBeanFactory: preInstantiateSingletons()
Note over DefaultListableBeanFactory: 预实例化所有单例bean

DefaultListableBeanFactory->>AbstractBeanFactory: getBean(name)
Note over AbstractBeanFactory: 从bean工厂获取bean实例

AbstractBeanFactory->>AbstractBeanFactory: doGetBean(name, requiredType, args, typeCheckOnly)
Note over AbstractBeanFactory: 实际获取bean的方法

AbstractBeanFactory->>DefaultSingletonBeanRegistry: getSingleton(beanName, singletonFactory)
Note over DefaultSingletonBeanRegistry: 从单例注册表获取单例bean

DefaultSingletonBeanRegistry->>AbstractBeanFactory: getObject()
Note over AbstractBeanFactory: 获取bean的实例对象

AbstractBeanFactory->>AbstractAutowireCapableBeanFactory: createBean(beanName, mbd, args)
Note over AbstractAutowireCapableBeanFactory: 创建bean实例

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory: doCreateBean(beanName, mbdToUse, args)
Note over AbstractAutowireCapableBeanFactory: 实际创建bean实例的方法

AbstractAutowireCapableBeanFactory->>AbstractBeanFactory: registerDisposableBeanIfNecessary(beanName, bean, mbd)
Note over AbstractBeanFactory: 注册bean的销毁适配器

AbstractBeanFactory->>DisposableBeanAdapter: new DisposableBeanAdapter(bean, beanName, beanDefinition, postProcessors, acc)
Note over DisposableBeanAdapter: 创建bean的销毁适配器

DisposableBeanAdapter->>AbstractBeanFactory: 返回销毁Bean适配器
Note over AbstractBeanFactory: 返回销毁Bean适配器

AbstractBeanFactory->>DefaultSingletonBeanRegistry: registerDisposableBean(beanName, bean)
Note over DefaultSingletonBeanRegistry: 注册单例bean的销毁适配器
end

par Bean销毁阶段
DestroyBeanApplication->>AbstractApplicationContext: close()
Note over AbstractApplicationContext: 关闭应用程序上下文

AbstractApplicationContext->>AbstractApplicationContext: doClose()
Note over AbstractApplicationContext: 执行关闭操作

AbstractApplicationContext->>AbstractApplicationContext: destroyBeans()
Note over AbstractApplicationContext: 销毁所有bean

AbstractApplicationContext->>DefaultListableBeanFactory: destroySingletons()
Note over DefaultListableBeanFactory: 销毁所有单例bean

DefaultListableBeanFactory->>DefaultSingletonBeanRegistry: destroySingletons()
Note over DefaultSingletonBeanRegistry: 销毁所有单例bean

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry: destroySingleton(beanName)
Note over DefaultSingletonBeanRegistry: 销毁单例bean

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry: destroyBean(beanName, disposableBean)
Note over DefaultSingletonBeanRegistry: 销毁bean的销毁适配器

DefaultSingletonBeanRegistry->>DisposableBeanAdapter: destroy()
Note over DisposableBeanAdapter: 调用bean的destroy方法

DisposableBeanAdapter->>MyBean: destroy()
Note over MyBean: MyBean的销毁逻辑执行
end
~~~

### 七、源码分析

#### 注册适配器

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
    // 完成所有剩余非懒加载的单列Bean对象。
    beanFactory.preInstantiateSingletons();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons`方法中，主要的核心目的是预先实例化所有非懒加载的单例bean。在Spring的上下文初始化完成后，该方法会被触发，以确保所有单例bean都被正确地创建并初始化。其中`getBean(beanName)`是此方法的核心操作。对于容器中定义的每一个单例bean，都会调用`getBean`方法，这将触发bean的实例化、初始化及其依赖的注入。如果bean之前没有被创建过，那么这个调用会导致其被实例化和初始化。

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

在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中，首先检查所请求的bean是否是一个单例并且已经创建。如果尚未创建，将创建一个新的实例。在这个过程中，处理可能的异常情况，如循环引用，并确保返回的bean是正确的类型。这是Spring容器bean生命周期管理的核心部分。

```java
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {
    // ... [代码部分省略以简化]

    // 开始创建bean实例
    if (mbd.isSingleton()) {
        // 如果bean是单例的，我们会尝试从单例缓存中获取
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

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，主要是在创建 bean 实例的过程中注册了销毁适配器，以便在容器关闭时能够执行相应的销毁逻辑。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // 在此注册bean以便在销毁时进行处理。
    try {
        registerDisposableBeanIfNecessary(beanName, bean, mbd);
    }
    catch (BeanDefinitionValidationException ex) {
        // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#registerDisposableBeanIfNecessary`方法中，首先检查Bean的作用域是否为单例且是否需要执行销毁逻辑。如果是单例作用域，注册一个`DisposableBean`适配器，负责执行各种销毁工作，包括`DestructionAwareBeanPostProcessors`、`DisposableBean`接口以及自定义的销毁方法。对于自定义作用域，查找相应的Scope并注册销毁回调。

```java
protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
    // ... [代码部分省略以简化]

    // 检查是否为原型作用域（prototype），以及是否需要销毁
    if (!mbd.isPrototype() && requiresDestruction(bean, mbd)) {
        if (mbd.isSingleton()) {
            // 如果是单例作用域，注册一个 DisposableBean 适配器，该适配器执行所有销毁工作
            // 包括 DestructionAwareBeanPostProcessors、DisposableBean 接口、自定义销毁方法等
            registerDisposableBean(beanName, new DisposableBeanAdapter(
                bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, acc));
        } else {
            // 如果是自定义作用域，找到相应的 Scope 并注册销毁回调
            Scope scope = this.scopes.get(mbd.getScope());
            if (scope == null) {
                throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
            }
            // 注册一个 DisposableBean 适配器，该适配器执行所有销毁工作
            scope.registerDestructionCallback(beanName, new DisposableBeanAdapter(
                bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, acc));
        }
    }
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#requiresDestruction`方法中，如果 bean 类型不是 `NullBean` 且存在销毁方法或与销毁相关的 `DestructionAwareBeanPostProcessor`，则返回 `true`，表示该 bean 需要执行销毁逻辑。否则，返回 `false`，表示不需要执行销毁逻辑。

```java
protected boolean requiresDestruction(Object bean, RootBeanDefinition mbd) {
   return (bean.getClass() != NullBean.class && (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) ||
         (hasDestructionAwareBeanPostProcessors() && DisposableBeanAdapter.hasApplicableProcessors(
               bean, getBeanPostProcessorCache().destructionAware))));
}
```

在`org.springframework.beans.factory.support.DisposableBeanAdapter#DisposableBeanAdapter`方法中，主要用于处理 Bean 销毁逻辑的适配器。根据 Bean 的定义和类型信息，确定是否存在可调用的销毁方法，以及如何执行销毁逻辑。在初始化过程中，检查是否实现了 `DisposableBean` 接口、存在自定义的销毁方法，以及相关的 BeanPostProcessor。如果存在可调用的销毁方法，将进行相关的配置，以便在容器关闭时执行销毁逻辑。

```java
public DisposableBeanAdapter(Object bean, String beanName, RootBeanDefinition beanDefinition,
        List<DestructionAwareBeanPostProcessor> postProcessors, @Nullable AccessControlContext acc) {

    // 确保待销毁的 Bean 实例不为 null
    Assert.notNull(bean, "Disposable bean must not be null");

    // 初始化实例变量
    this.bean = bean;
    this.beanName = beanName;

    // 确定是否需要调用 DisposableBean 接口的 destroy 方法
    this.invokeDisposableBean = (this.bean instanceof DisposableBean && !beanDefinition.isExternallyManagedDestroyMethod("destroy"));

    // 是否允许访问非公共方法
    this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
    this.acc = acc;

    // 推断销毁方法的名称
    String destroyMethodName = inferDestroyMethodIfNecessary(bean, beanDefinition);

    // 如果存在销毁方法，并且不是外部管理的 destroy 方法，则进行相关处理
    if (destroyMethodName != null && !(this.invokeDisposableBean && "destroy".equals(destroyMethodName)) &&
        !beanDefinition.isExternallyManagedDestroyMethod(destroyMethodName)) {
        
        // 记录销毁方法的名称
        this.destroyMethodName = destroyMethodName;

        // 确定销毁方法
        Method destroyMethod = determineDestroyMethod(destroyMethodName);

        // 如果找不到销毁方法，并且 Bean 定义要求强制存在，则抛出异常
        if (destroyMethod == null) {
            // ... [代码部分省略以简化]
        }
        // 否则，检查销毁方法的合法性
        else {
            if (destroyMethod.getParameterCount() > 0) {
                Class<?>[] paramTypes = destroyMethod.getParameterTypes();
                // ... [代码部分省略以简化]
            }
            // 获取销毁方法的接口方法（如果存在）
            destroyMethod = ClassUtils.getInterfaceMethodIfPossible(destroyMethod);
        }
        // 记录销毁方法
        this.destroyMethod = destroyMethod;
    }

    // 过滤与销毁相关的 BeanPostProcessor
    this.beanPostProcessors = filterPostProcessors(postProcessors, bean);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerDisposableBean`方法中，

```java
public void registerDisposableBean(String beanName, DisposableBean bean) {
    // 使用同步块确保线程安全
    synchronized (this.disposableBeans) {
        // 将待销毁的 Bean 放入 disposableBeans 集合中
        this.disposableBeans.put(beanName, bean);
    }
}
```

#### Bean销毁

在`org.springframework.context.support.AbstractApplicationContext#close`方法中，首先是启动了一个同步块，它同步在 `startupShutdownMonitor` 对象上。这确保了在给定时刻只有一个线程可以执行这个块内的代码，防止多线程导致的资源竞争或数据不一致，然后是调用了 `doClose` 方法。

```java
@Override
public void close() {
    synchronized (this.startupShutdownMonitor) {
        doClose();
        // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.context.support.AbstractApplicationContext#doClose`方法中，又调用了 `destroyBeans` 方法。

```java
protected void doClose() {
    // ... [代码部分省略以简化]
    // Destroy all cached singletons in the context's BeanFactory.
    destroyBeans();
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#destroyBeans`方法中，首先是调用了`getBeanFactory()`返回 Spring 的 `BeanFactory` ，然后在获得的 `BeanFactory` 上，调用了 `destroySingletons` 方法，这个方法的目的是销毁所有在 `BeanFactory` 中缓存的单例 beans。

```java
protected void destroyBeans() {
    getBeanFactory().destroySingletons();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingletons`方法中，首先是调用了父类的 `destroySingletons` 方法，为了确保继承自父类的销毁逻辑得到了执行。

```java
@Override
public void destroySingletons() {
    super.destroySingletons();
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingletons`方法中，首先是在`disposableBeans` 字段上，从其键集合中获取所有的 bean 名称，并将它们转换为一个字符串数组。`disposableBeans` 可能包含了实现了 `DisposableBean` 接口的 beans，这些 beans 需要在容器销毁时特殊处理，最后倒序循环，从最后一个开始，销毁所有在 `disposableBeans` 列表中的 beans。这样做是为了确保依赖关系正确地处理，beans先被创建的应该后被销毁。

```java
public void destroySingletons() {
    // ... [代码部分省略以简化]
    String[] disposableBeanNames;
    synchronized (this.disposableBeans) {
        disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
    }
    for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
        destroySingleton(disposableBeanNames[i]);
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingleton`方法中，首先是调用了父类的 `destroySingleton` 方法，为了确保继承自父类的销毁逻辑得到了执行。

```java
@Override
public void destroySingleton(String beanName) {
    super.destroySingleton(beanName);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton`方法中，首先是使用 `synchronized` 关键字在 `disposableBeans` 对象上进行同步，以确保在多线程环境中安全地访问和修改它，从 `disposableBeans` 集合中移除指定名称的 bean，并将其转换为 `DisposableBean` 类型，最后调用`destroyBean`方法执行实际的销毁操作。

```java
public void destroySingleton(String beanName) {
    // 从已注册的单例中移除指定名称的单例 Bean
    removeSingleton(beanName);

    // 获取对应的 DisposableBean 实例
    DisposableBean disposableBean;
    synchronized (this.disposableBeans) {
        disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
    }

    // 执行销毁逻辑
    destroyBean(beanName, disposableBean);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean`方法中，主要用于销毁给定名称的 Bean，包括触发其依赖关系的销毁、执行 Bean 的 `destroy` 方法以及销毁该 Bean 包含的其他 Bean，最后，清理已销毁 Bean 的依赖关系和准备好的依赖信息。

```java
protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
    // 触发销毁依赖于该 Bean 的其他 Bean...
    Set<String> dependencies;
    
    // 在完全同步的情况下，以确保获取的 Set 是线程安全的
    synchronized (this.dependentBeanMap) {
        dependencies = this.dependentBeanMap.remove(beanName);
    }
    
    if (dependencies != null) {
        // ... [代码部分省略以简化]
        for (String dependentBeanName : dependencies) {
            destroySingleton(dependentBeanName);
        }
    }

    // 实际执行 Bean 的销毁逻辑...
    if (bean != null) {
        try {
            bean.destroy();
        }
        catch (Throwable ex) {
            // ... [代码部分省略以简化]
        }
    }

    // 触发销毁该 Bean 包含的其他 Bean...
    Set<String> containedBeans;
    synchronized (this.containedBeanMap) {
        // 在完全同步的情况下，以确保获取的 Set 是断开连接的
        containedBeans = this.containedBeanMap.remove(beanName);
    }
    if (containedBeans != null) {
        for (String containedBeanName : containedBeans) {
            destroySingleton(containedBeanName);
        }
    }

    // 从其他 Bean 的依赖关系中移除已销毁的 Bean...
    synchronized (this.dependentBeanMap) {
        for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Set<String>> entry = it.next();
            Set<String> dependenciesToClean = entry.getValue();
            dependenciesToClean.remove(beanName);
            if (dependenciesToClean.isEmpty()) {
                it.remove();
            }
        }
    }

    // 移除已销毁 Bean 的准备好的依赖信息
    this.dependenciesForBeanMap.remove(beanName);
}
```

在`org.springframework.beans.factory.support.DisposableBeanAdapter#destroy`方法中，实现了 `DisposableBean` 接口的销毁方法，负责在 Bean 销毁阶段执行各个销毁相关的操作。这包括前置销毁处理、调用 `DisposableBean` 接口的 `destroy` 方法、执行自定义的销毁方法。

```java
@Override
public void destroy() {
    // 执行 DestructionAwareBeanPostProcessor 的前置销毁处理
    if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
        for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
            processor.postProcessBeforeDestruction(this.bean, this.beanName);
        }
    }

    // 如果实现了 DisposableBean 接口，则调用其 destroy 方法
    if (this.invokeDisposableBean) {
        // ... [代码部分省略以简化]
        try {
            // ... [代码部分省略以简化]
            ((DisposableBean) this.bean).destroy();
        }
        catch (Throwable ex) {
            // ... [代码部分省略以简化]
        }
    }

    // 如果存在自定义的销毁方法，则执行
    if (this.destroyMethod != null) {
        invokeCustomDestroyMethod(this.destroyMethod);
    }
    // 如果存在指定名称的自定义销毁方法，则查找并执行
    else if (this.destroyMethodName != null) {
        Method methodToInvoke = determineDestroyMethod(this.destroyMethodName);
        if (methodToInvoke != null) {
            // 获取接口方法（如果存在）并执行自定义销毁方法
            invokeCustomDestroyMethod(ClassUtils.getInterfaceMethodIfPossible(methodToInvoke));
        }
    }
}
```

### 八、注意事项

1. **销毁方法避免抛出异常**

   + 在销毁方法中应尽量避免抛出异常，因为抛出的异常可能会影响到其他Bean的销毁过程。如果有异常，最好进行适当的记录。

2. **注意Bean的依赖关系**

   + 确保销毁Bean的顺序符合依赖关系，首先销毁依赖关系较少的Bean，然后再销毁依赖关系较多的Bean。Spring容器会尽量按照依赖关系的顺序销毁Bean。

3. **注意Bean的生命周期和作用域**

   + 对于单例（Singleton）Bean，其销毁过程会在容器关闭时触发。对于原型（Prototype）Bean，Spring容器不会负责销毁，需要手动管理。


### 九、总结

#### 最佳实践总结

1. **创建应用程序上下文**

   + 使用`AnnotationConfigApplicationContext`创建一个基于注解的应用程序上下文对象。

2. **注册Bean**

   + 使用`context.register(MyBean.class)`注册了`MyBean`类，告诉Spring容器要管理这个配置类所定义的bean。

3. **刷新应用程序上下文**

   + 使用`context.refresh()`刷新应用程序上下文，初始化并启动Spring容器。

4. **容器关闭**

   + 使用`context.close()`关闭应用程序上下文。在关闭过程中，Spring容器会触发bean的销毁阶段。

5. **Bean销毁**

   + 由于`MyBean`实现了`DisposableBean`接口，因此在容器关闭时，会调用`MyBean`的`destroy`方法。在`destroy`方法中，我们简单地打印一条消息，表示`MyBean`被销毁了。
   + 最后，控制台输出了`MyBean被销毁了`的消息，证明了bean的销毁过程已经执行。

#### 源码分析总结

1. **注册Bean销毁适配器** 

   + 在`AbstractApplicationContext`的`finishBeanFactoryInitialization`方法中，调用了`DefaultListableBeanFactory`的`preInstantiateSingletons`方法，该方法会预先实例化所有非懒加载的单例bean。在实例化的过程中，对于每个bean，会调用`registerDisposableBeanIfNecessary`方法注册销毁适配器，以确保在容器关闭时能够执行相应的销毁逻辑。

2. **Bean销毁** 

   + 在容器关闭时，通过`AbstractApplicationContext`的`close`方法触发销毁过程。在`destroyBeans`方法中，调用了`DefaultListableBeanFactory`的`destroySingletons`方法，该方法销毁所有在`BeanFactory`中缓存的单例beans。

   + 在`destroySingletons`方法中，遍历`disposableBeans`集合，获取所有待销毁的bean名称，并倒序循环销毁这些bean。在销毁过程中，首先调用`destroyBean`方法执行实际的销毁操作。

   + 在`destroyBean`方法中，触发销毁依赖于该bean的其他bean，执行bean的`destroy`方法，销毁bean包含的其他bean，从其他bean的依赖关系中移除已销毁的bean，清理已销毁bean的准备好的依赖信息。

   + 最后通过`DisposableBeanAdapter`类处理Bean的销毁逻辑。该类实现了`DisposableBean`接口的`destroy`方法，执行前置销毁处理、调用`DisposableBean`接口的`destroy`方法、执行自定义的销毁方法等操作。

