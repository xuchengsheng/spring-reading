## TargetSource

- [TargetSource](#targetsource)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [五、主要实现](#五主要实现)
    - [六、类关系图](#六类关系图)
    - [七、最佳实践](#七最佳实践)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`TargetSource` 接口是 Spring AOP 框架中的一个关键组件，用于定义获取目标对象的策略，允许我们灵活地管理目标对象的创建和管理。通过实现该接口，可以实现各种目标对象的获取方式，如单例、原型、池化等，从而为 AOP 切面提供了更高度的可定制性和灵活性。

### 三、主要功能

1. **目标对象获取方法**

   + `getTarget()`，该方法根据具体的策略获取目标对象，如单例、原型、池化等。

2. **目标对象释放方法**

   + `releaseTarget(Object target)`，一些实现可能需要释放目标对象，如将对象返回到对象池中。

3. **灵活的目标对象管理**

   + 可以根据应用程序的需求实现自定义的目标对象获取策略，从而实现对目标对象的灵活管理。

4. **内置的目标源实现**

   + Spring AOP 提供了几种内置的 `TargetSource` 实现，如 `SingletonTargetSource`、`PrototypeTargetSource`、`ThreadLocalTargetSource` 等，可以根据具体情况选择合适的目标源实现。

5. **扩展性和定制性**

   + 我们可以通过实现 `TargetSource` 接口来实现自定义的目标源，从而满足特定场景下的需求，如基于线程的对象管理、对象池管理等。

### 四、接口源码

`TargetSource` 接口用于获取当前 AOP 调用的目标对象，通过 `getTarget()` 方法获取目标对象，并通过 `releaseTarget(Object target)` 方法释放目标对象。接口定义了 `getTargetClass()` 方法用于返回目标对象的类型，`isStatic()` 方法用于检查是否所有调用 `getTarget()` 方法的返回值都是相同的对象。此接口支持静态和动态目标源，静态目标源始终返回相同的目标对象，而动态目标源支持池化、热交换等功能。

```java
/**
 * TargetSource 接口用于获取 AOP 调用的当前目标对象，如果没有环绕通知选择结束拦截器链，则将通过反射调用目标对象。
 *
 * <p>如果 TargetSource 是 "static"，它将始终返回相同的目标对象，从而允许 AOP 框架进行优化。动态目标源可以支持池化、热交换等。
 *
 * <p>应用程序开发人员通常不需要直接使用 TargetSources：这是一个 AOP 框架接口。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface TargetSource extends TargetClassAware {

    /**
     * 返回此 TargetSource 返回的目标类型。
     * <p>可以返回 {@code null}，尽管某些 TargetSource 的用法可能只使用预定的目标类。
     *
     * @return 此 TargetSource 返回的目标类型
     */
    @Override
    @Nullable
    Class<?> getTargetClass();

    /**
     * 所有对 getTarget() 的调用是否将返回相同的对象？
     * <p>在这种情况下，不需要调用 releaseTarget(Object)，并且 AOP 框架可以缓存 getTarget() 的返回值。
     *
     * @return 如果目标是不可变的，则为 true
     * @see #getTarget
     */
    boolean isStatic();

    /**
     * 返回一个目标实例。在 AOP 框架调用 AOP 方法调用的目标之前立即调用此方法。
     *
     * @return 包含连接点的目标对象，如果没有实际目标实例，则为 {@code null}
     * @throws Exception 如果无法解析目标对象
     */
    @Nullable
    Object getTarget() throws Exception;

    /**
     * 释放从 getTarget() 方法获取的给定目标对象（如果有）。
     *
     * @param target 从调用 getTarget() 获取的对象
     * @throws Exception 如果无法释放对象
     */
    void releaseTarget(Object target) throws Exception;

}	
```

### 五、主要实现

1. **SingletonTargetSource**

   + 用于管理单例对象的目标源。该实现每次调用 `getTarget()` 方法都返回同一个单例对象，适用于目标对象是单例的情况。

2. **PrototypeTargetSource**

   + 用于每次调用时创建新对象的目标源。该实现每次调用 `getTarget()` 方法都返回一个新的目标对象实例，适用于目标对象需要频繁更新或重置的情况。

3. **ThreadLocalTargetSource**

   + 用于在每个线程中保持一个目标对象的引用。该实现在每个线程中都维护一个目标对象的副本，适用于需要在多线程环境中使用不同的目标对象实例的情况。

4. **CommonsPool2TargetSource**

   + 用于使用 Apache Commons Pool 来管理目标对象的池化目标源。该实现通过对象池管理目标对象的创建和销毁，以提高对象的重用性和性能。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class CommonsPool2TargetSource
class PrototypeTargetSource
class SingletonTargetSource
class TargetClassAware {
<<Interface>>

}
class TargetSource {
<<Interface>>

}
class ThreadLocalTargetSource

CommonsPool2TargetSource  ..>  TargetSource 
PrototypeTargetSource  ..>  TargetSource 
SingletonTargetSource  ..>  TargetSource 
TargetSource  -->  TargetClassAware 
ThreadLocalTargetSource  ..>  TargetSource 
~~~

### 七、最佳实践

使用 Spring 的代理工厂（`ProxyFactory`）和目标源（`TargetSource`）来创建代理对象。在这个示例中，我们创建了一个连接池目标源（`ConnectionPoolTargetSource`），设置连接池的大小为 3。然后，我们将这个连接池目标源设置为代理工厂的目标源，并通过代理工厂获取代理对象。最后，我们通过代理对象调用了10次方法。

```java
public class TargetSourceDemo {

    public static void main(String[] args) {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置目标源为连接池目标源，连接池大小为3
        proxyFactory.setTargetSource(new ConnectionPoolTargetSource(3));
        // 获取代理对象
        MyConnection proxy = (MyConnection) proxyFactory.getProxy();

        // 调用10次方法
        for (int i = 0; i < 10; i++) {
            System.out.println("MyConnection Name = " + proxy.getName());
        }
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

运行结果，连接池会循环地提供连接对象，直到连接池中的所有连接对象都被使用过一次后，再重新开始循环。这与预期的连接池行为一致，确保了连接对象的复用和管理。

```java
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
