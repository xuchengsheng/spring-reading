## DestructionAwareBeanPostProcessor

- [DestructionAwareBeanPostProcessor](#destructionawarebeanpostprocessor)
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

`DestructionAwareBeanPostProcessor` 接口，用于提供在 bean 销毁之前进行额外处理或操作的机会。其主要职责是在 bean 即将被销毁时允许执行自定义的逻辑。

### 二、接口源码

`DestructionAwareBeanPostProcessor` 是 Spring 框架自 1.0.1 版本开始引入的一个核心接口。该接口定义了两个主要的方法：`postProcessBeforeInitialization` 和 `postProcessAfterInitialization`，分别在 Bean 的初始化前后调用。

```java
/**
 * BeanPostProcessor的子接口，增加了销毁前的回调。
 *
 * 典型的用途是在特定的bean类型上调用自定义的销毁回调，
 * 与相应的初始化回调相匹配。
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 在给定的 bean 实例销毁之前应用此 BeanPostProcessor，
     * 例如，调用自定义的销毁回调。
     * 与 DisposableBean 的 {@code destroy} 方法和一个自定义的销毁方法一样，此回调
     * 仅适用于容器完全管理其生命周期的 beans。这通常适用于单例和有作用域的 beans。
     * @param bean 要被销毁的 bean 实例
     * @param beanName bean 的名称
     * @throws org.springframework.beans.BeansException 如果发生错误
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     * @see org.springframework.beans.factory.support.AbstractBeanDefinition#setDestroyMethodName(String)
     */
    void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

    /**
     * 确定给定的 bean 实例是否需要由此后处理器销毁。
     * 默认实现返回true。如果一个基于 pre-5 的 DestructionAwareBeanPostProcessor
     * 实现没有为此方法提供具体实现，Spring 也会默默地假设返回值为 true。
     * @param bean 要检查的 bean 实例
     * @return 如果需要为此 bean 实例最终调用 postProcessBeforeDestruction，返回 true，否则返回 false
     * @since 4.3
     */
    default boolean requiresDestruction(Object bean) {
        return true;
    }
}
```

### 三、主要功能

+ 销毁前逻辑：使用 `postProcessBeforeDestruction(Object bean, String beanName)` 方法，我们可以为 bean 执行自定义的销毁逻辑。当一个 bean 被容器标记为销毁时，此方法将被调用。（例如，容器关闭时进行资源释放，状态记录，依赖清理）

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，然后从Spring上下文中获取一个`ConnectionService`类型的bean，并打印`isConnected`的状态。

```java
public class DestructionAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        ConnectionService connection = context.getBean("connectionService", ConnectionService.class);
        System.out.println("Is connected: " + connection.isConnected());
        context.close();
    }
}
```

这里使用`@Bean`注解，定义了两个Bean，是为了确保`ConnectionService`， `MyDestructionAwareBeanPostProcessor` 被 Spring 容器执行

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyDestructionAwareBeanPostProcessor myDestructionAwareBeanPostProcessor() {
        return new MyDestructionAwareBeanPostProcessor();
    }

    @Bean
    public ConnectionService connectionService() {
        return new ConnectionServiceImpl();
    }
}
```

`MyDestructionAwareBeanPostProcessor` 类的目的是管理 `ConnectionServiceImpl` bean 的生命周期。当这个 bean 初始化完成后，它的连接被打开；当这个 bean 准备被销毁时，它的连接被关闭。这确保了资源在不再需要时被适当地释放。

```java
public class MyDestructionAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).openConnection();
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).closeConnection();
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return (bean instanceof ConnectionServiceImpl);
    }
}
```

定义一个连接的服务接口

```java
public interface ConnectionService {

    void openConnection();

    void closeConnection();

    boolean isConnected();
}
```

`ConnectionServiceImpl` 类提供了一个模拟的连接服务。它可以跟踪其连接状态，并允许调用者打开和关闭连接，以及查询连接的状态。

```java
public class ConnectionServiceImpl implements ConnectionService {

    private boolean isConnected = false;

    @Override
    public void openConnection() {
        isConnected = true;
        System.out.println("connection opened.");
    }

    @Override
    public void closeConnection() {
        if (isConnected) {
            isConnected = false;
            System.out.println("connection closed.");
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
}
```

运行结果发现，由于在 `MyDestructionAwareBeanPostProcessor` 的 `postProcessAfterInitialization` 方法中，我们检测到 bean 是 `ConnectionServiceImpl` 的实例并调用了其 `openConnection` 方法，因此该连接被打开。然后我们在`DestructionAwareBeanPostProcessorApplication`类的main方法中调用 `isConnected` 方法并打印结果的直接效果。这证明了在应用上下文启动和运行期间，连接确实是打开的。由于在 `MyDestructionAwareBeanPostProcessor` 的 `postProcessBeforeDestruction` 方法中，我们检测到 bean 是 `ConnectionServiceImpl` 的实例并调用了其 `closeConnection` 方法，因此该连接被关闭。

```java
connection opened.
Is connected: true
connection closed.
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: DestructionAwareBeanPostProcessor时序图
    participant DestructionAwareBeanPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant DefaultSingletonBeanRegistry
    participant DisposableBeanAdapter
    participant MyDestructionAwareBeanPostProcessor
    
    DestructionAwareBeanPostProcessorApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses	)<br>应用开始初始化上下文
    AnnotationConfigApplicationContext-->>DestructionAwareBeanPostProcessorApplication:初始化完成
    DestructionAwareBeanPostProcessorApplication->>AbstractApplicationContext:close()<br>请求关闭上下文
    AbstractApplicationContext->>AbstractApplicationContext:doClose()<br>执行关闭逻辑
    AbstractApplicationContext->>AbstractApplicationContext:destroyBeans()<br>开始销毁beans
    AbstractApplicationContext->>DefaultListableBeanFactory:destroySingletons()<br>销毁单例beans
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingletons()<br>调父类销毁方法
    DefaultSingletonBeanRegistry-->>DefaultListableBeanFactory:destroySingleton(beanName)<br>销毁单个bean
    DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingleton(beanName)<br>调父类销毁bean方法
    DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:destroyBean(beanName,bean)<br>执行销毁bean操作
    DefaultSingletonBeanRegistry->>DisposableBeanAdapter:destroy()<br>适配器执行销毁
    DisposableBeanAdapter->>MyDestructionAwareBeanPostProcessor:postProcessBeforeDestruction(bean,beanName)<br>执行自定义销毁逻辑
    AbstractApplicationContext-->>DestructionAwareBeanPostProcessorApplication:请求关闭上下文结束
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`ConnectionService`类型的bean，并打印`isConnected`的状态。

```java
public class DestructionAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        ConnectionService connection = context.getBean("connectionService", ConnectionService.class);
        System.out.println("Is connected: " + connection.isConnected());
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

在`org.springframework.beans.factory.support.DisposableBeanAdapter#destroy`方法中，遍历 `beanPostProcessors` 集合的循环。每一个元素都是 `DestructionAwareBeanPostProcessor` 类型，它是 `BeanPostProcessor` 的一个子接口，专门为销毁阶段提供了一个回调。

```java
@Override
public void destroy() {
    if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
        for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
            processor.postProcessBeforeDestruction(this.bean, this.beanName);
        }
    }
    // ... [代码部分省略以简化]
}
```

最后来到我们自定义的销毁`com.xcs.spring.config.MyDestructionAwareBeanPostProcessor#postProcessBeforeDestruction`方法中，在这个方法中，如果 bean 是 `ConnectionServiceImpl` 的一个实例，则该 bean 的 `closeConnection` 方法将被调用。这确保了每当 `ConnectionServiceImpl` 类型的 bean 被销毁之前，它的连接都会被关闭。

```java
public class MyDestructionAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).openConnection();
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConnectionServiceImpl) {
            ((ConnectionServiceImpl) bean).closeConnection();
        }
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return (bean instanceof ConnectionServiceImpl);
    }
}
```

### 七、注意事项

**性能影响**：每一个 `DestructionAwareBeanPostProcessor` 在 bean 的生命周期结束时都会被调用，所以应该确保其中的代码高效执行，避免产生不必要的性能瓶颈。

**检查 `requiresDestruction`**：实现 `requiresDestruction` 方法来指定哪些 beans 需要在销毁时进行处理，可以避免不必要的 `postProcessBeforeDestruction` 调用，从而提高性能。

**注意线程安全**：如果在多线程环境中使用 Spring，确保 `DestructionAwareBeanPostProcessor` 的实现是线程安全的。

**异常处理**：在 `postProcessBeforeDestruction` 方法中可能会遇到任何类型的异常，应确保适当地处理这些异常，以避免影响其他 beans 的销毁。

**确保与其他 `BeanPostProcessors` 协调**：如果我们的应用程序中还有其他 `BeanPostProcessors`，确保它们之间的相互作用不会导致问题。

**与 `@PreDestroy` 注解协同工作**：如果 bean 已经使用了 `@PreDestroy` 注解来定义自己的销毁方法，这些方法会在 `postProcessBeforeDestruction` 被调用之前执行。确保这两者的逻辑不会互相干扰。

### 八、总结

#### 8.1、最佳实践总结

**应用启动**:

- 在 `DestructionAwareBeanPostProcessorApplication` 的 `main` 方法中，首先创建了一个 `AnnotationConfigApplicationContext` 上下文，并通过 `MyConfiguration` 类进行配置。
- 通过该上下文获取了一个名为 `connectionService` 的 `ConnectionService` 类型的 bean。

**Spring容器的初始化**:

- 在初始化过程中，Spring容器将根据 `MyConfiguration` 类创建两个 beans: `MyDestructionAwareBeanPostProcessor` 和 `ConnectionServiceImpl`。
- 当 `ConnectionServiceImpl` bean 初始化完成后，`MyDestructionAwareBeanPostProcessor` 的 `postProcessAfterInitialization` 方法被调用，此时连接被打开。
- `MyDestructionAwareBeanPostProcessor` 的作用确保了 `ConnectionServiceImpl` bean 初始化完成后会立即打开连接。

**应用运行期**:

- 在 `main` 方法中，应用查询了 `ConnectionService` bean 的连接状态，并打印出结果，显示连接为 "打开" 状态。
- 随后，上下文被关闭，意味着所有的单例 bean 将被销毁。

**销毁阶段**:

- 在上下文关闭时，`MyDestructionAwareBeanPostProcessor` 的 `postProcessBeforeDestruction` 方法会被调用。
- 由于我们在这个方法中检查了 bean 是否是 `ConnectionServiceImpl` 的实例，所以 `closeConnection` 方法被调用，从而关闭连接。
- 这确保了在 bean 的生命周期结束时，资源被适当地释放。

**运行结果**:

- 最终，控制台上的输出证明了在 bean 的生命周期开始时资源被打开，在生命周期结束时资源被关闭。

#### 8.2、源码分析总结

**应用启动**:

- 应用通过 `AnnotationConfigApplicationContext` 启动，并用 `MyConfiguration` 类进行配置。
- 然后从Spring容器中获取了一个类型为 `ConnectionService` 的 bean，并检查其连接状态。

**Spring容器销毁**:

- 通过调用 `context.close()`，应用启动了Spring容器的关闭流程。
- 关闭流程首先通过同步机制确保在任何时刻都只有一个线程能够执行关闭操作。
- 内部调用 `doClose` 方法来执行实际的关闭逻辑。
- JVM 的关闭钩子被移除，表示上下文已经明确关闭。

**销毁Beans**:

- 在 `doClose` 方法中，`destroyBeans` 方法被调用，它的主要作用是销毁所有的单例 beans。
- `destroyBeans` 方法进而调用 `BeanFactory` 的 `destroySingletons` 方法。

**销毁单例Beans**:

- `destroySingletons` 方法销毁所有在 `BeanFactory` 中缓存的单例 beans。
- 它首先获取所有需要被销毁的 beans 的名称，然后反向遍历这些 beans，确保依赖的 beans 先被销毁。
- 对每一个需要被销毁的 bean，`destroySingleton` 方法被调用。

**执行销毁逻辑**:

- 在 `destroySingleton` 方法中，从 `disposableBeans` 列表中获取到实现了 `DisposableBean` 接口的 bean，然后调用它的 `destroy` 方法。
- 在 `DisposableBeanAdapter` 的 `destroy` 方法中，所有注册的 `DestructionAwareBeanPostProcessor` 将被遍历，对每一个处理器，都会调用其 `postProcessBeforeDestruction` 方法。

**自定义销毁逻辑**:

- 最终，我们的自定义 `DestructionAwareBeanPostProcessor` 的 `postProcessBeforeDestruction` 方法被调用。
- 在这个方法中，检查 bean 是否是 `ConnectionServiceImpl` 的实例。如果是，关闭它的连接。