## AopProxyFactory
- [AopProxyFactory](#aopproxyfactory)
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

`AopProxyFactory` 接口是 Spring AOP 框架的关键组件之一，负责根据配置信息创建 AOP 代理对象，支持根据目标对象的类型和配置选择合适的代理方式（JDK 动态代理或 CGLIB 代理）。

### 三、主要功能

1. **创建 AOP 代理对象**

   + 根据传入的 `AdvisedSupport` 对象，利用指定的代理方式（JDK 动态代理或 CGLIB 代理）生成 AOP 代理对象。
   
2. **决定代理方式**

   + 根据目标类的类型和配置信息，确定是否使用 JDK 动态代理或 CGLIB 代理。这个决定通常是基于配置中的一些条件，例如是否需要代理接口或者是否允许使用 CGLIB 代理。
   
3. **支持灵活配置**

   + 通过实现该接口，可以灵活地定制 AOP 代理的生成方式，以满足不同场景下的需求。

### 四、接口源码

`AopProxyFactory`接口 ，其功能是创建基于 `AdvisedSupport` 配置对象的 AOP 代理。代理对象需要满足一系列约定，包括实现被配置指示的所有接口、实现 `Advised` 接口、实现 `equals` 方法用于比较被代理的接口、通知和目标、并且在通知者和目标都是可序列化的情况下应该是可序列化的，以及在通知者和目标都是线程安全的情况下应该是线程安全的。该接口的实现应该能够根据给定的 AOP 配置创建相应的 AOP 代理对象，并在配置无效时抛出 `AopConfigException` 异常。

```java
/**
 * 接口，由能够基于 {@link AdvisedSupport} 配置对象创建 AOP 代理的工厂实现。
 *
 * <p>代理对象应遵守以下约定：
 * <ul>
 * <li>它们应该实现配置中指示应该被代理的所有接口。
 * <li>它们应该实现 {@link Advised} 接口。
 * <li>它们应该实现 equals 方法以比较被代理的接口、通知和目标。
 * <li>如果所有通知者和目标都是可序列化的，它们应该是可序列化的。
 * <li>如果通知者和目标都是线程安全的，它们应该是线程安全的。
 * </ul>
 *
 * <p>代理可能允许或不允许更改通知。如果它们不允许更改通知（例如，因为配置已被冻结），则代理应在尝试更改通知时抛出 {@link AopConfigException}。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AopProxyFactory {

	/**
	 * 根据给定的 AOP 配置创建一个 {@link AopProxy}。
	 * @param config 以 AdvisedSupport 对象形式表示的 AOP 配置
	 * @return 相应的 AOP 代理
	 * @throws AopConfigException 如果配置无效
	 */
	AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;

}

```

### 五、主要实现

1. **DefaultAopProxyFactory**

   + `DefaultAopProxyFactory` 是 `AopProxyFactory` 接口的默认实现类，它负责根据给定的 `AdvisedSupport` 配置对象创建 AOP 代理，根据配置信息选择合适的代理方式（JDK 动态代理或 CGLIB 代理），并处理可能出现的异常情况，使得 Spring AOP 能够灵活地生成并使用 AOP 代理对象，实现切面编程的功能。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AopProxyFactory {
<<Interface>>

}
class DefaultAopProxyFactory

DefaultAopProxyFactory  ..>  AopProxyFactory 
~~~

### 七、最佳实践

何使用 Spring AOP 创建 JDK 动态代理和 CGLIB 代理。在 `jdkProxy()` 方法中，首先创建了一个 `AdvisedSupport` 对象用于配置 AOP 代理，然后通过 `DefaultAopProxyFactory` 实例创建 JDK 动态代理对象，并打印生成的代理类。而在 `cglibProxy()` 方法中，同样创建了一个 `AdvisedSupport` 对象用于配置 AOP 代理，然后通过 `DefaultAopProxyFactory` 实例创建 CGLIB 代理对象，并打印生成的代理类。

```java
public class AopProxyFactoryDemo {

    public static void main(String[] args) {
        // 分别演示 JDK 动态代理和 CGLIB 代理
        jdkProxy();
        cglibProxy();
    }

    /**
     * JDK 动态代理示例
     */
    private static void jdkProxy() {
        // 创建 AdvisedSupport 对象，用于配置 AOP 代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 设置目标对象的类
        advisedSupport.setTargetClass(MyService.class);

        // 创建 DefaultAopProxyFactory 实例
        AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
        // 创建 JDK 动态代理对象
        MyService myService = (MyService) aopProxyFactory.createAopProxy(advisedSupport).getProxy();
        // 打印生成的代理类
        System.out.println("jdkProxy = " + myService.getClass());
    }

    /**
     * CGLIB 代理示例
     */
    private static void cglibProxy() {
        // 创建 AdvisedSupport 对象，用于配置 AOP 代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 创建 DefaultAopProxyFactory 实例
        AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();
        // 创建 CGLIB 代理对象
        MyService myService = (MyService) aopProxyFactory.createAopProxy(advisedSupport).getProxy();
        // 打印生成的代理类
        System.out.println("cglibProxy = " + myService.getClass());
    }
}
```

运行结果，显示了两种不同的代理类。`jdkProxy` 是使用 JDK 动态代理生成的代理类，它由 `com.sun.proxy.$Proxy0` 表示。而 `cglibProxy` 是使用 CGLIB 生成的代理类，它由 `com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$3c109cf5` 表示。这两种代理类分别对应了不同的代理方式，分别由 Spring AOP 根据配置信息生成。

```java
jdkProxy = class com.sun.proxy.$Proxy0
cglibProxy = class com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$3c109cf5
```

### 八、源码分析

`DefaultAopProxyFactory` 是 `AopProxyFactory` 接口的默认实现，根据给定的 `AdvisedSupport` 配置对象，可以创建 CGLIB 代理或 JDK 动态代理。如果对于给定的 `AdvisedSupport` 实例满足以下条件之一，则会创建 CGLIB 代理：优化标志被设置、代理目标类标志被设置，或者未指定代理接口。通常情况下，可以通过指定 `proxyTargetClass` 来强制使用 CGLIB 代理，或者通过指定一个或多个接口来使用 JDK 动态代理。

[AopProxy源码分析](../spring-aop-aopProxy/README.md)

```java
/**
 * 默认的 {@link AopProxyFactory} 实现，根据条件创建 CGLIB 代理或 JDK 动态代理。
 *
 * <p>如果对于给定的 {@link AdvisedSupport} 实例满足以下条件之一，则创建 CGLIB 代理：
 * <ul>
 * <li>设置了 {@code optimize} 标志
 * <li>设置了 {@code proxyTargetClass} 标志
 * <li>未指定代理接口
 * </ul>
 *
 * <p>通常情况下，可以通过指定 {@code proxyTargetClass} 来强制使用 CGLIB 代理，或者通过指定一个或多个接口来使用 JDK 动态代理。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 2004-03-12
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 */
@SuppressWarnings("serial")
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {

	@Override
    public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
        // 检查是否支持CGLIB代理，如果是，则创建CGLIB代理
        if (!NativeDetector.inNativeImage() &&
                (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config))) {
            // 获取目标类
            Class<?> targetClass = config.getTargetClass();
            if (targetClass == null) {
                // 如果目标类为空，则抛出AopConfigException异常
                throw new AopConfigException("TargetSource cannot determine target class: " +
                        "Either an interface or a target is required for proxy creation.");
            }
            // 如果目标类是接口或者是代理类，则创建JDK动态代理
            if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
                return new JdkDynamicAopProxy(config);
            }
            // 否则，创建CGLIB代理
            return new ObjenesisCglibAopProxy(config);
        } else {
            // 否则，创建JDK动态代理
            return new JdkDynamicAopProxy(config);
        }
    }

	/**
	 * 确定提供的 {@link AdvisedSupport} 是否仅指定了 {@link org.springframework.aop.SpringProxy} 接口
	 * （或者根本没有指定代理接口）。
	 */
	private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
		Class<?>[] ifcs = config.getProxiedInterfaces();
		return (ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0])));
	}

}
```
