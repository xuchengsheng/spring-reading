## ProxyFactory

- [ProxyFactory](#proxyfactory)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、类关系图](#四类关系图)
  - [五、最佳实践](#五最佳实践)
  - [六、源码分析](#六源码分析)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`ProxyFactory`类是Spring AOP中的关键组件之一，用于动态创建代理对象并将切面逻辑织入到目标对象的方法调用中。通过设置目标对象、接口、通知等属性，`ProxyFactory`提供了一种便捷的方式来创建代理对象，实现面向切面编程的功能。

### 三、主要功能

1. **创建代理对象**

   + 允许动态地创建代理对象，这些代理对象可以代表目标对象，并在方法调用前后执行额外的逻辑。

2. **切面逻辑织入**

   + 通过添加通知（Advice），`ProxyFactory`可以将切面逻辑织入到代理对象的方法调用中，实现横切关注点的处理。

3. **支持接口代理和类代理**

   + 支持基于接口的JDK动态代理和基于类的CGLIB代理，可以根据需要选择合适的代理方式。

4. **灵活的配置选项**

   + 提供了丰富的配置选项，如设置目标对象、接口、通知、代理方式等，可以根据具体需求进行灵活配置。

5. **简化AOP配置**

   + 简化了AOP配置的过程，通过简单的API调用，即可实现代理对象的创建和切面逻辑的织入，降低了AOP配置的复杂度。

### 四、类关系图

~~~mermaid
classDiagram
direction BT
class Advised {
<<Interface>>

}
class AdvisedSupport
class ProxyConfig
class ProxyCreatorSupport
class ProxyFactory
class TargetClassAware {
<<Interface>>

}

Advised  -->  TargetClassAware 
AdvisedSupport  ..>  Advised 
AdvisedSupport  -->  ProxyConfig 
ProxyCreatorSupport  -->  AdvisedSupport 
ProxyFactory  -->  ProxyCreatorSupport 

~~~

### 五、最佳实践

使用`ProxyFactory`类来创建代理对象，并将前置通知（MethodBeforeAdvice）应用于目标对象的方法调用中。首先，通过`ProxyFactory`创建代理工厂，并指定目标对象为`MyService`类的实例。然后，添加前置通知`MyMethodBeforeAdvice`到代理工厂中。接着，调用`getProxy()`方法获取代理对象，并将其存储在`Object`类型的变量中。最后，打印出代理对象的类名。

```java
public class ProxyFactoryDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyMethodBeforeAdvice());
        // 获取代理对象
        Object proxy = proxyFactory.getProxy();
        // 调用代理对象的方法
        System.out.println("proxy = " + proxy.getClass());
    }
}
```

这个类可以作为一个目标对象，通过代理工厂创建代理对象，并在其方法调用前后应用额外的逻辑，实现面向切面编程的功能。

```java
public class MyService {

}
```

运行结果，表明代理对象是通过Spring的CGLIB动态代理生成的，它是`MyService`类的一个增强版本。

```java
proxy = class com.xcs.spring.MyService$$EnhancerBySpringCGLIB$$d9bdf44b
```

### 六、源码分析

**初始化阶段**

在`org.springframework.aop.framework.ProxyCreatorSupport#ProxyCreatorSupport()`方法中，`ProxyCreatorSupport`类是`ProxyFactory`类的父类，因此当初始化`ProxyFactory`时，`ProxyCreatorSupport`也会跟着初始化，确保在创建代理对象时能够利用`ProxyCreatorSupport`的功能。在构造函数中，它初始化了`aopProxyFactory`成员变量，将其设置为一个`DefaultAopProxyFactory`对象，用于后续创建AOP代理对象。

```java
/**
 * 创建一个新的 ProxyCreatorSupport 实例。
 */
public ProxyCreatorSupport() {
    this.aopProxyFactory = new DefaultAopProxyFactory();
}
```

在`org.springframework.aop.framework.ProxyFactory#ProxyFactory(java.lang.Object)`方法中，构造函数接受一个目标对象作为参数，并将其设置为要被代理的目标对象。然后，通过调用`ClassUtils.getAllInterfaces(target)`方法获取目标对象实现的所有接口，并将它们设置为代理工厂要代理的接口。

```java
/**
 * 创建一个新的ProxyFactory。
 * <p>将代理目标对象实现的所有接口。
 * @param target 要被代理的目标对象
 */
public ProxyFactory(Object target) {
    setTarget(target);
    setInterfaces(ClassUtils.getAllInterfaces(target));
}

```

在`org.springframework.aop.framework.AdvisedSupport#setTarget`方法中，它接受一个对象作为参数，并将该对象设置为代理工厂的目标对象。在内部，它会使用`SingletonTargetSource`来包装目标对象，以确保每次获取代理时都返回相同的目标对象实例。

```java
/**
 * 将给定的对象设置为目标对象。
 * 将为该对象创建一个 SingletonTargetSource。
 * @see #setTargetSource
 * @see org.springframework.aop.target.SingletonTargetSource
 */
public void setTarget(Object target) {
    setTargetSource(new SingletonTargetSource(target));
}
```

在`org.springframework.aop.framework.AdvisedSupport#setInterfaces`方法中，用于设置要被代理的接口。它接受一个可变参数`interfaces`，表示需要被代理的接口列表。在方法内部，首先通过`Assert.notNull(interfaces, "Interfaces must not be null")`断言确保传入的接口数组不为空。然后，清空当前`ProxyFactory`对象中已经设置的接口列表，并遍历传入的接口数组，逐个调用`addInterface(ifc)`方法将接口添加到接口列表中。这样做是为了确保在创建代理对象时，只代理指定的接口。

```java
/**
 * 设置要被代理的接口。
 */
public void setInterfaces(Class<?>... interfaces) {
    Assert.notNull(interfaces, "Interfaces must not be null");
    this.interfaces.clear();
    for (Class<?> ifc : interfaces) {
        addInterface(ifc);
    }
}
```

**创建代理阶段**

在`org.springframework.aop.framework.ProxyFactory#getProxy()`方法中，根据工厂中的配置创建一个新的代理对象。可以重复调用此方法，根据已添加或删除的接口以及添加或移除的拦截器的不同，其效果会有所变化。该方法会使用默认的类加载器，通常是线程上下文类加载器（如果需要代理创建时）。最终返回创建的代理对象。

[AopProxy源码分析](../spring-aop-aopProxy/README.md)

```java
/**
 * 根据该工厂中的设置创建一个新的代理对象。
 * <p>可以重复调用。如果已添加或删除接口，则效果会有所不同。可以添加和删除拦截器。
 * <p>使用默认的类加载器：通常是线程上下文类加载器（如果需要创建代理）。
 * @return 代理对象
 */
public Object getProxy() {
    return createAopProxy().getProxy();
}
```

在`org.springframework.aop.framework.ProxyCreatorSupport#createAopProxy`方法中，首先检查`active`标志，如果当前`ProxyFactory`对象未激活，则调用`activate()`方法进行激活。然后，通过调用`getAopProxyFactory().createAopProxy(this)`来获取AOP代理对象的工厂，并使用当前`ProxyFactory`对象作为参数来创建AOP代理。最终返回创建的AOP代理对象。

[AopProxyFactory源码分析](../spring-aop-aopProxyFactory/README.md)

```java
/**
 * 子类应调用此方法以获取一个新的AOP代理。它们不应该使用 {@code this} 作为参数创建AOP代理。
 */
protected final synchronized AopProxy createAopProxy() {
    if (!this.active) {
        activate();
    }
    return getAopProxyFactory().createAopProxy(this);
}

```

在`org.springframework.aop.framework.ProxyCreatorSupport#getAopProxyFactory`
方法中，用于返回该代理配置所使用的AOP代理工厂（AopProxyFactory）。在`ProxyCreatorSupport()`构造方法中，`aopProxyFactory`
对象被初始化为`DefaultAopProxyFactory`的实例。因此，当调用`getAopProxyFactory()`方法时，将返回一个`DefaultAopProxyFactory`
对象，该对象用于创建AOP代理对象。

```java
/**
 * 返回该代理配置使用的AopProxyFactory。
 */
public AopProxyFactory getAopProxyFactory() {
    return this.aopProxyFactory;
}
```
