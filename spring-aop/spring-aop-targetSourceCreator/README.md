## TargetSourceCreator

- [TargetSourceCreator](#targetsourcecreator)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [五、主要实现](#五主要实现)
    - [六、类关系图](#六类关系图)
    - [七、最佳实践](#七最佳实践)
    - [八、源码分析](#八源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`TargetSourceCreator`接口，主要用于创建目标对象的代理。通过实现该接口，你可以自定义代理对象的创建逻辑，例如根据不同的条件返回不同的代理对象。这在AOP（面向切面编程）和代理模式中非常有用，可以灵活地控制代理对象的生成过程。

### 三、主要功能

1. **创建代理对象的目标源（TargetSource）** 

   + 该接口允许我们定义创建代理对象的逻辑，包括决定何时创建代理对象以及如何创建代理对象的目标源。

2. **定制代理对象的创建过程** 

   + 通过实现该接口，你可以根据需要定制代理对象的创建过程。这包括根据不同的条件返回不同的目标源，或者在创建代理对象之前或之后执行特定的逻辑。

3. **支持AOP的灵活配置** 

   + 在Spring框架中，AOP（面向切面编程）经常使用代理对象来实现横切关注点。`TargetSourceCreator`接口允许我们灵活地控制代理对象的生成过程，从而为AOP提供了更高的定制性和灵活性。

### 四、接口源码

`TargetSourceCreator`接口，它允许实现类创建特殊的目标源（TargetSource），例如池化目标源，用于特定的bean。实现类可以基于目标类的属性，如池化属性，来决定选择哪种类型的目标源。`AbstractAutoProxyCreator`可以支持多个`TargetSourceCreators`，它们将按顺序应用，为Spring框架中的代理对象创建提供了灵活性和定制性。

```java
/**
 * 实现类可以为特定的bean创建特殊的目标源，例如池化目标源。例如，它们可以根据目标类上的属性（例如池化属性）来选择目标源。
 *
 * <p>AbstractAutoProxyCreator 可以支持多个 TargetSourceCreators，它们将按顺序应用。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@FunctionalInterface
public interface TargetSourceCreator {

    /**
     * 为给定的bean创建一个特殊的目标源（如果有的话）。
     * @param beanClass bean的类
     * @param beanName bean的名称
     * @return 特殊的目标源，如果此 TargetSourceCreator 不感兴趣于特定的bean，则返回 {@code null}
     */
    @Nullable
    TargetSource getTargetSource(Class<?> beanClass, String beanName);

}
```

### 五、主要实现

1. **QuickTargetSourceCreator**

   + 用于快速创建目标源。它适用于那些不需要延迟加载的情况，通过特定的条件或策略，可以快速地生成目标源，以提高性能或满足其他需求。

2. **LazyInitTargetSourceCreator**

   + 用于延迟创建目标源。它适用于需要延迟加载的场景，以减少启动时间或资源占用。根据特定的条件或策略，它会延迟地创建目标源，直到被请求时才进行加载。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AbstractBeanFactoryBasedTargetSourceCreator
class LazyInitTargetSourceCreator
class QuickTargetSourceCreator
class TargetSourceCreator {
<<Interface>>

}

AbstractBeanFactoryBasedTargetSourceCreator  ..>  TargetSourceCreator 
LazyInitTargetSourceCreator  -->  AbstractBeanFactoryBasedTargetSourceCreator 
QuickTargetSourceCreator  -->  AbstractBeanFactoryBasedTargetSourceCreator 
~~~

### 七、最佳实践

使用Spring框架中的注解配置来创建应用程序上下文，并从上下文中获取`MyConnection` bean。然后，它打印了`MyConnection`实例的类名，并循环调用了`MyConnection`实例的`getName()`方法来获取实例的名称并打印输出。

```java
public class TargetSourceCreatorDemo {
    public static void main(String[] args) {
        // 创建一个基于注解的应用程序上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从上下文中获取 MyConnection bean
        MyConnection myConnection = context.getBean(MyConnection.class);
        // 打印 MyConnection 实例的类名
        System.out.println("MyConnection Class = " + myConnection.getClass());
        // 循环调用 MyConnection 实例的 getName() 方法
        for (int i = 0; i < 10; i++) {
            // 打印 MyConnection 实例的名称
            System.out.println("MyConnection Name = " + myConnection.getName());
        }
    }
}
```

通过`@EnableAspectJAutoProxy`注解启用了AspectJ自动代理功能，允许Spring框架创建和管理切面（Aspects）。同时，通过`@Configuration`注解标识这是一个配置类，并使用`@ComponentScan("com.xcs.spring")`注解来指定需要扫描的包，以便Spring能够自动装配Bean和发现组件。

```java
@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.xcs.spring")
public class AppConfig {

}
```

`SetMyTargetSourceCreator`类实现了Spring框架的`BeanPostProcessor`接口和`PriorityOrdered`接口。在`postProcessAfterInitialization`方法中，通过判断bean是否为`AbstractAutoProxyCreator`的实例，然后为其设置了自定义的目标源创建器`MyTargetSourceCreator`。通过实现`PriorityOrdered`接口并重写`getOrder`方法，确保了该后置处理器具有最高的优先级，以确保在其他后置处理器之前执行。

```java
@Component
public class SetMyTargetSourceCreator implements BeanPostProcessor , PriorityOrdered {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractAutoProxyCreator) {
            ((AbstractAutoProxyCreator) bean).setCustomTargetSourceCreators(new MyTargetSourceCreator());
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
```

`MyTargetSourceCreator`类实现了`TargetSourceCreator`接口。在`getTargetSource`方法中，它根据传入的bean类和bean名称来判断是否需要为特定的bean创建目标源。如果传入的bean类是`MyConnection`类或其子类，它将返回一个具有连接池功能的目标源`ConnectionPoolTargetSource`，并设置连接池的大小为3。

```java
public class MyTargetSourceCreator implements TargetSourceCreator {
    @Override
    public TargetSource getTargetSource(Class<?> beanClass, String beanName) {
        if (MyConnection.class.isAssignableFrom(beanClass)) {
            return new ConnectionPoolTargetSource(3);
        }
        return null;
    }
}
```

`ConnectionPoolTargetSource` 类实现了 Spring 的 `TargetSource` 接口，用于管理自定义连接对象的连接池。在构造函数中，它会初始化一个固定大小的阻塞队列作为连接池，并填充连接对象。通过实现 `getTarget()` 方法，它能够从连接池中获取连接对象，并在 `releaseTarget()` 方法中释放连接对象。

```java
/**
 * 连接池目标源，用于管理自定义连接对象的连接池。
 *
 * @author xcs
 * @date 2024年4月9日15:26:28
 */
public class ConnectionPoolTargetSource implements TargetSource {

    /**
     * 连接池
     */
    private final BlockingQueue<MyConnection> connectionPool;

    /**
     * 构造函数，初始化连接池。
     *
     * @param poolSize 连接池大小
     */
    public ConnectionPoolTargetSource(int poolSize) {
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);
        initializeConnectionPool(poolSize);
    }

    /**
     * 初始化连接池，填充连接对象。
     *
     * @param poolSize 连接池大小
     */
    private void initializeConnectionPool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            MyConnection connection = new MyConnection("Connection" + i);
            connectionPool.offer(connection);
        }
    }

    /**
     * 获取目标类的类型。
     *
     * @return 目标类的类型
     */
    @Override
    public Class<?> getTargetClass() {
        return MyConnection.class;
    }

    /**
     * 判断目标对象是否是静态的。
     *
     * @return 如果目标对象是静态的，则返回true，否则返回false
     */
    @Override
    public boolean isStatic() {
        return false;
    }

    /**
     * 获取连接对象。
     *
     * @return 连接对象
     * @throws Exception 如果获取连接对象时发生异常
     */
    @Override
    public Object getTarget() throws Exception {
        return connectionPool.take();
    }

    /**
     * 释放连接对象。
     *
     * @param target 待释放的连接对象
     * @throws Exception 如果释放连接对象时发生异常
     */
    @Override
    public void releaseTarget(Object target) throws Exception {
        if (target instanceof MyConnection) {
            connectionPool.offer((MyConnection) target);
        }
    }
}
```

`MyConnection` 类代表了一个自定义的连接对象。

```java
public class MyConnection {

    private String name;

    public MyConnection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyConnection{" +
                "name='" + name + '\'' +
                '}';
    }
}
```

运行结果，`MyTargetSourceCreator`类成功地为`MyConnection`类创建了一个连接池目标源。Spring框架通过CGLIB动态代理增强了`MyConnection`类，使其能够使用连接池功能。每次调用`getName()`方法时，都从连接池中获取连接，并返回连续的"Connection0"、"Connection1"和"Connection2"字符串，表明连接池的大小为3且连接名称在连接池中循环使用。

```java
MyConnection Class = class com.xcs.spring.MyConnection$$EnhancerBySpringCGLIB$$fb2fa879
MyConnection Name = Connection0
MyConnection Name = Connection1
MyConnection Name = Connection2
MyConnection Name = Connection0
MyConnection Name = Connection1
MyConnection Name = Connection2
MyConnection Name = Connection0
MyConnection Name = Connection1
MyConnection Name = Connection2
MyConnection Name = Connection0
```

### 八、源码分析

在`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessBeforeInstantiation`方法中，在Bean实例化之前进行处理。首先，它检查缓存中是否存在目标Bean的信息，如果存在则直接返回null，否则继续执行。然后，它检查Bean是否是基础设施类或是否应该被跳过，如果是，则将其标记为不需要增强，并返回null。最后，如果存在自定义的目标源（TargetSource），则创建代理对象，并使用自定义的目标源处理目标实例，从而避免不必要的默认实例化过程。

```java
@Override
public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
    // 获取缓存键
    Object cacheKey = getCacheKey(beanClass, beanName);

    // 如果bean名称为空或不在目标源bean列表中，且缓存中存在该键，则返回null
    if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
        if (this.advisedBeans.containsKey(cacheKey)) {
            return null;
        }
        // 如果bean类是基础设施类或应跳过，则将其标记为不需要增强，并返回null
        if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
            this.advisedBeans.put(cacheKey, Boolean.FALSE);
            return null;
        }
    }

    // 如果存在自定义的目标源，则在此处创建代理：
    // 避免不必要的目标bean默认实例化：
    // 目标源将以自定义方式处理目标实例。
    TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
    if (targetSource != null) {
        // 如果bean名称不为空，则将其添加到目标源bean列表中
        if (StringUtils.hasLength(beanName)) {
            this.targetSourcedBeans.add(beanName);
        }
        // 获取适用于bean的特定拦截器和增强器
        Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
        // 创建代理对象
        Object proxy = createProxy(beanClass, beanName, specificInterceptors, targetSource);
        // 将代理对象的类与缓存键关联
        this.proxyTypes.put(cacheKey, proxy.getClass());
        return proxy;
    }

    return null;
}
```

在`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#getCustomTargetSource`方法中，根据bean的类和名称创建目标源（TargetSource）。如果设置了自定义的TargetSourceCreators，并且bean工厂中包含了指定名称的bean，则会尝试使用这些TargetSourceCreators来创建目标源。方法会遍历所有的TargetSourceCreators，调用它们的getTargetSource方法来获取目标源。如果找到了匹配的目标源，则返回该目标源；否则返回null。

```java
/**
 * 为bean实例创建目标源。如果设置了任何TargetSourceCreators，则使用它们。
 * 如果不应使用自定义的TargetSource，则返回{@code null}。
 * <p>此实现使用"customTargetSourceCreators"属性。
 * 子类可以重写此方法以使用不同的机制。
 * @param beanClass bean的类
 * @param beanName bean的名称
 * @return 用于此bean的目标源
 * @see #setCustomTargetSourceCreators
 */
@Nullable
protected TargetSource getCustomTargetSource(Class<?> beanClass, String beanName) {
    // 对于直接注册的单例bean，我们无法创建复杂的目标源。
    if (this.customTargetSourceCreators != null &&
        this.beanFactory != null && this.beanFactory.containsBean(beanName)) {
        // 遍历所有的TargetSourceCreators
        for (TargetSourceCreator tsc : this.customTargetSourceCreators) {
            // 通过TargetSourceCreator获取目标源
            TargetSource ts = tsc.getTargetSource(beanClass, beanName);
            // 如果找到匹配的目标源，则返回
            if (ts != null) {
                // 找到了匹配的目标源。
                if (logger.isTraceEnabled()) {
                    logger.trace("TargetSourceCreator [" + tsc +
                            "] found custom TargetSource for bean with name '" + beanName + "'");
                }
                return ts;
            }
        }
    }

    // 没有找到自定义的目标源。
    return null;
}
```
