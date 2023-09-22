## DisposableBean

- [DisposableBean](#disposablebean)
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

`DisposableBean` 接口允许执行某些资源清理操作，比如我们可以使用这个接口来确保某些资源，比如文件句柄、网络连接、数据库连接等，被正确地释放或清理。

### 二、接口源码

`DisposableBean` 是 Spring 框架自 12.08.2003 开始引入的一个核心接口。如果bean在销毁时希望释放资源，那么可以实现此接口，另外实现 Java 的 `AutoCloseable` 接口以达到同样的目的

```java
/**
 * 要实现此接口的 bean 在销毁时希望释放资源。
 * 当单独销毁作用域内的 bean 时，BeanFactory 会调用 destroy 方法。
 * 一个 org.springframework.context.ApplicationContext 应当在关闭时，
 * 根据应用的生命周期来销毁其所有的单例对象。
 *
 * Spring 管理的 bean 也可以实现 Java 的 AutoCloseable 接口以达到同样的目的。
 * 实现接口的另一种选择是指定一个自定义的销毁方法，例如在 XML bean 定义中。
 * 关于所有 bean 生命周期方法的列表，参见 BeanFactory BeanFactory。
 *
 * @author Juergen Hoeller
 * @since 12.08.2003
 * @see InitializingBean
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName()
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#destroySingletons()
 * @see org.springframework.context.ConfigurableApplicationContext#close()
 */
public interface DisposableBean {

    /**
     * 当 bean 被其包含的 BeanFactory 销毁时调用。
     * @throws Exception 如果在关闭时出现错误。错误会被记录，
     *                   但不会被重新抛出，以允许其他 beans 正确释放他们的资源。
     */
    void destroy() throws Exception;

}
```

### 三、主要功能

**销毁回调**：当 bean 被 Spring 容器销毁时，如果它实现了 `DisposableBean` 接口，容器会自动调用其 `destroy()` 方法。这为 beans 提供了一个机会在销毁之前执行任何必要的清理操作。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，最后调用`context.close()`方法关闭容器。

```java
public class DisposableBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保`MyDisposableBean`被 Spring 容器执行。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyDisposableBean myDisposableBean(){
        return new MyDisposableBean();
    }
}
```

 `MyDisposableBean`类在实例化时会建立一个模拟的数据库连接，并且在销毁时会关闭这个连接。

```java
public class MyDisposableBean implements DisposableBean {

    // 模拟的数据库连接对象
    private String databaseConnection;

    public MyDisposableBean() {
        // 在构造函数中模拟建立数据库连接
        this.databaseConnection = "Database connection established";
        System.out.println(databaseConnection);
    }

    @Override
    public void destroy() throws Exception {
        // 在 destroy 方法中模拟关闭数据库连接
        databaseConnection = null;
        System.out.println("Database connection closed");
    }
}
```

运行结果发现，当 `MyDisposableBean` bean 被销毁时，`destroy()` 方法确实被调用了，并模拟关闭了数据库连接。

```java
Database connection established
Database connection closed
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: DisposableBean时序图
    participant DisposableBeanApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant DefaultSingletonBeanRegistry
    participant DisposableBeanAdapter
    participant MyDisposableBean
    
    DisposableBeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses	)<br>应用开始初始化上下文
    AnnotationConfigApplicationContext-->>DisposableBeanApplication:初始化完成
    DisposableBeanApplication->>AbstractApplicationContext:close()<br>请求关闭上下文
    AbstractApplicationContext->>AbstractApplicationContext:doClose()<br>执行关闭逻辑
    AbstractApplicationContext->>AbstractApplicationContext:destroyBeans()<br>开始销毁beans
    AbstractApplicationContext->>DefaultListableBeanFactory:destroySingletons()<br>销毁单例beans
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingletons()<br>调父类销毁方法
    DefaultSingletonBeanRegistry-->>DefaultListableBeanFactory:destroySingleton(beanName)<br>销毁单个bean
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingleton(beanName)<br>调父类销毁bean方法
    DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:destroyBean(beanName,bean)<br>执行销毁bean操作
    DefaultSingletonBeanRegistry->>DisposableBeanAdapter:destroy()<br>适配器执行销毁
    DisposableBeanAdapter->>MyDisposableBean:destroy()<br>执行自定义销毁逻辑
    AbstractApplicationContext-->>DisposableBeanApplication:请求关闭上下文结束
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，然后通过调用 `context.close()` 方法来关闭应用上下文。

```java
public class DisposableBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
```

在`org.springframework.context.support.AbstractApplicationContext#close`方法中，首先是启动了一个同步块，它同步在 `startupShutdownMonitor` 对象上。这确保了在给定时刻只有一个线程可以执行这个块内的代码，防止多线程导致的资源竞争或数据不一致，然后是调用了 `doClose` 方法，最后是为 JVM 注册了一个关闭钩子。

```java
@Override
public void close() {
    synchronized (this.startupShutdownMonitor) {
        doClose();
        // If we registered a JVM shutdown hook, we don't need it anymore now:
        // We've already explicitly closed the context.
        if (this.shutdownHook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
            }
            catch (IllegalStateException ex) {
                // ignore - VM is already shutting down
            }
        }
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
    updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
    clearByTypeCache();
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
    removeManualSingletonName(beanName);
    clearByTypeCache();
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton`方法中，首先是使用 `synchronized` 关键字在 `disposableBeans` 对象上进行同步，以确保在多线程环境中安全地访问和修改它，从 `disposableBeans` 集合中移除指定名称的 bean，并将其转换为 `DisposableBean` 类型，最后调用`destroyBean`方法执行实际的销毁操作。

```java
public void destroySingleton(String beanName) {
    // Remove a registered singleton of the given name, if any.
    removeSingleton(beanName);

    // Destroy the corresponding DisposableBean instance.
    DisposableBean disposableBean;
    synchronized (this.disposableBeans) {
        disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
    }
    destroyBean(beanName, disposableBean);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean`方法中，直接调用 `bean` 的 `destroy` 方法。因为 `bean` 是一个 `DisposableBean` 类型的实例，所以它一定有一个 `destroy` 方法，该方法提供了 bean 的自定义销毁逻辑。

```java
protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
    // ... [代码部分省略以简化]
    // Actually destroy the bean now...
    if (bean != null) {
        try {
            bean.destroy();
        }
        catch (Throwable ex) {
            // ... [代码部分省略以简化]
        }
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DisposableBeanAdapter#destroy`方法中，首先检查  `invokeDisposableBean` 变量，如果为true，就直接调用实现 `DisposableBean` 接口的 bean 的 `destroy` 方法。

```java
@Override
public void destroy() {
    // ... [代码部分省略以简化]
    
    if (this.invokeDisposableBean) {
        if (logger.isTraceEnabled()) {
            logger.trace("Invoking destroy() on bean with name '" + this.beanName + "'");
        }
        try {
            if (System.getSecurityManager() != null) {
                // ... [代码部分省略以简化]
            }
            else {
                ((DisposableBean) this.bean).destroy();
            }
        }
        catch (Throwable ex) {
            // ... [代码部分省略以简化]
        }
    }
}
```

最后执行到我们自定义的逻辑中， `MyDisposableBean`类在实例化时会建立一个模拟的数据库连接，并且在销毁时会关闭这个连接。

```java
public class MyDisposableBean implements DisposableBean {

    // 模拟的数据库连接对象
    private String databaseConnection;

    public MyDisposableBean() {
        // 在构造函数中模拟建立数据库连接
        this.databaseConnection = "Database connection established";
        System.out.println(databaseConnection);
    }

    @Override
    public void destroy() throws Exception {
        // 在 destroy 方法中模拟关闭数据库连接
        databaseConnection = null;
        System.out.println("Database connection closed");
    }
}
```

### 七、注意事项

**不要过度依赖**: 尽管 `DisposableBean` 提供了一种定义销毁逻辑的标准方法，但更推荐使用 `@PreDestroy` 注解或在 bean 定义中指定 `destroy-method` 属性。这些方法通常更简单，更具有声明性，并且避免了不必要的代码耦合。

**原型 bean**: 对于原型作用域的 beans，Spring 不会管理它们的完整生命周期。这意味着对于原型 beans，`DisposableBean` 的 `destroy()` 方法不会被自动调用。应确保通过其他方式处理这些 bean 的资源释放。

**与初始化方法配合**: 如果我们正在使用 `DisposableBean` 来处理销毁逻辑，可能也会考虑使用 `InitializingBean` 来处理初始化逻辑。但同样，推荐使用 `@PostConstruct` 注解或 `init-method` 属性来定义初始化方法。

**避免重复代码**: 如果多个 beans 具有相似的销毁逻辑，考虑将这些逻辑提取到一个共享方法或帮助类中，以减少重复代码和增强可维护性。

**避免长时间运行的操作**: `destroy()` 方法应该快速执行并释放资源。避免在其中执行可能耗时的操作，因为这可能会延迟应用的关闭过程。

### 八、总结

#### 8.1、最佳实践总结

**应用启动**: 我们创建了一个启动类 `DisposableBeanApplication`，其中初始化了一个基于注解的应用上下文 `AnnotationConfigApplicationContext`。这个上下文根据 `MyConfiguration` 类配置了 Spring 容器。

**Java配置**: 在 `MyConfiguration` 类中，我们使用 `@Bean` 注解定义了一个名为 `myDisposableBean` 的 bean。这确保了 `MyDisposableBean` 类的实例被 Spring 容器管理。

**资源管理**: `MyDisposableBean` 类模拟了数据库的连接和断开过程。当这个类被实例化时，它模拟建立了一个数据库连接。然后，当这个 bean 被销毁时（由于上下文的关闭），它的 `destroy()` 方法被调用，模拟了数据库连接的关闭。

**应用关闭**: 在启动类的 `main` 方法的最后，通过调用 `context.close()` 方法来关闭应用上下文。这导致容器销毁所有单例 beans。在这个过程中，`MyDisposableBean` bean 的 `destroy()` 方法被调用，从而模拟关闭了数据库连接。

**输出结果**: 运行程序时，我们首先看到 `"Database connection established"`，这是 `MyDisposableBean` 构造函数中的输出，表示模拟的数据库连接已经建立。随后，当应用上下文关闭时，我们看到 `"Database connection closed"`，这是 `MyDisposableBean` 的 `destroy()` 方法中的输出，表示模拟的数据库连接已经关闭。

#### 8.2、源码分析总结

**启动和关闭**:

- 我们创建了 `DisposableBeanApplication` 启动类，其中初始化了一个基于注解的应用上下文 `AnnotationConfigApplicationContext`。
- 通过传递 `MyConfiguration` 类作为构造参数来配置 Spring 容器。
- 使用 `context.close()` 方法来关闭应用上下文，触发资源的清理和释放。

**关闭的同步操作**:

- 在 `AbstractApplicationContext#close` 方法中，启动了一个同步块，确保在给定时刻只有一个线程可以关闭应用上下文，防止资源竞争或数据不一致。
- 关闭上下文后，任何先前注册的 JVM 关闭钩子都会被移除，因为上下文已经明确地被关闭了。

**实际关闭操作**:

- 在 `AbstractApplicationContext#doClose` 方法中，调用 `destroyBeans` 方法来销毁容器中的 beans。

**销毁beans**:

- 通过 `AbstractApplicationContext#destroyBeans` 方法，`BeanFactory` 调用其 `destroySingletons` 方法来销毁所有缓存的单例 beans。
- 在 `DefaultListableBeanFactory` 中，首先确保调用了其父类的销毁逻辑。
- `DefaultSingletonBeanRegistry#destroySingletons` 会获取所有需要销毁的 beans 的名称，并以创建的相反顺序进行销毁，以正确处理依赖关系。

**具体的bean销毁**:

- 对于每个要销毁的 bean，都会调用 `DefaultSingletonBeanRegistry#destroySingleton` 方法。
- 如果 bean 实现了 `DisposableBean` 接口，它的 `destroy` 方法会被调用。
- 为了确保线程安全，许多操作都在同步块中执行，特别是当操作 `disposableBeans` 时。

**自定义销毁逻辑**:

- 最终，到达我们定义的 `MyDisposableBean` 类。当这个类的实例被销毁时，它会模拟关闭一个数据库连接。