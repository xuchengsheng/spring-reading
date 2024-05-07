## AopContext

- [AopContext](#aopcontext)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、类源码](#四类源码)
  - [五、最佳实践](#五最佳实践)
  - [六、源码分析](#六源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`AopContext`类是Spring AOP框架提供的一个工具类，用于在方法内部访问当前AOP代理对象。通过`currentProxy()`方法，可以获取当前方法被AOP代理后的代理对象，从而在方法内部执行代理对象的其他方法或获取相关信息。

### 三、主要功能

1. **获取当前AOP代理对象**

   + 通过调用`currentProxy()`方法，可以在方法内部获取到当前的AOP代理对象，即被AOP增强后的对象。

2. **允许在方法内部调用代理对象的方法**

   + 获得代理对象后，可以在方法内部直接调用代理对象的其他方法，包括被增强的切面方法或目标对象的方法。

3. **解决代理对象的传递问题**

   + 在一些特定场景下，可能需要在不同的方法间传递AOP代理对象，而不是直接调用`this`。`AopContext`类提供了一种解决方案，可以在方法调用间传递AOP代理对象。

### 四、类源码

`AopContext`类提供了用于获取当前AOP调用信息的静态方法集合。通过`currentProxy()`方法可以获取当前AOP代理对象，前提是AOP框架已配置为暴露代理对象。这对目标对象或通知进行增强调用，以及查找通知配置非常有用。然而，由于性能成本较高，Spring的AOP框架默认不会暴露代理对象。

```java
/**
 * 用于获取当前AOP调用信息的静态方法集合。
 * 
 * <p>如果AOP框架配置为暴露当前代理对象（非默认情况），则可使用 {@code currentProxy()} 方法获取正在使用的AOP代理对象。
 * 目标对象或通知可以使用此方法进行增强调用，类似于EJB中的 {@code getEJBObject()}。也可用于查找通知配置。
 * 
 * <p>Spring的AOP框架默认不暴露代理对象，因为这样做会带来性能开销。
 * 
 * <p>此类中的功能可被目标对象使用，以获取调用中的资源。然而，当存在合理替代方案时，不应使用此方法，因为这会使应用程序代码依赖于AOP下的使用和Spring AOP框架。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 */
public final class AopContext {

	/**
	 * 线程本地变量，用于保存与该线程关联的AOP代理对象。
	 * 除非控制代理配置的“exposeProxy”属性被设置为“true”，否则将包含{@code null}。
	 * @see ProxyConfig#setExposeProxy
	 */
	private static final ThreadLocal<Object> currentProxy = new NamedThreadLocal<>("Current AOP proxy");


	private AopContext() {
	}


	/**
	 * 尝试返回当前AOP代理对象。此方法仅在调用方法通过AOP调用，并且AOP框架已设置为暴露代理对象时可用。
	 * 否则，此方法将抛出IllegalStateException异常。
	 * @return 当前AOP代理对象（永远不会返回{@code null}）
	 * @throws IllegalStateException 如果无法找到代理对象，因为该方法是在AOP调用上下文之外调用的，或者因为AOP框架尚未配置为暴露代理对象
	 */
	public static Object currentProxy() throws IllegalStateException {
		Object proxy = currentProxy.get();
		if (proxy == null) {
			throw new IllegalStateException(
					"Cannot find current proxy: Set 'exposeProxy' property on Advised to 'true' to make it available, and " +
							"ensure that AopContext.currentProxy() is invoked in the same thread as the AOP invocation context.");
		}
		return proxy;
	}

	/**
	 * 使给定的代理对象可通过{@code currentProxy()}方法访问。
	 * <p>注意，调用者应谨慎地保留旧值。
	 * @param proxy 要暴露的代理对象（或{@code null}以重置）
	 * @return 旧的代理对象，如果没有绑定，则可能为{@code null}
	 * @see #currentProxy()
	 */
	@Nullable
	static Object setCurrentProxy(@Nullable Object proxy) {
		Object old = currentProxy.get();
		if (proxy != null) {
			currentProxy.set(proxy);
		}
		else {
			currentProxy.remove();
		}
		return old;
	}

}
```

### 五、最佳实践

使用Spring AOP创建一个代理对象，并在代理对象的方法调用前应用自定义的前置通知。首先，通过`ProxyFactory`创建了一个代理工厂，并设置了要被代理的目标对象`MyService`。然后通过`proxyFactory.setExposeProxy(true)`来暴露代理对象，以便在方法内部可以使用`AopContext`类访问到代理对象。接着，使用`proxyFactory.addAdvisor()`方法添加了一个切面通知器，将自定义的前置通知`MyMethodBeforeAdvice`应用到被`MyAnnotation`注解标记的方法上。最后，通过`proxyFactory.getProxy()`获取代理对象，并调用其方法`foo()`。

```java
public class AopContextDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 暴露代理对象
        proxyFactory.setExposeProxy(true);
        // 创建通知器
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(MyAnnotation.class), new MyMethodBeforeAdvice()));
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.foo();
    }
}
```

`MyMethodBeforeAdvice`自定义前置通知类``。

```java
public class MyMethodBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before method " + method.getName() + " is called.");
    }
}
```

自定义注解`MyAnnotation`。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyAnnotation {
}
```

定义了一个名为`MyService`的Java类，它被`@MyAnnotation`注解标记。该类包含两个方法，`foo()`和`bar()`。在`foo()`方法中，通过调用`AopContext.currentProxy()`来获取当前的AOP代理对象，并使用该代理对象来调用`bar()`方法，以确保AOP切面可以正确地被应用。这种方式避免了直接调用`bar()`方法而导致AOP切面失效的问题。

```java
@MyAnnotation
public class MyService {

    public void foo() {
        System.out.println("foo...");
        // 直接调用bar会导致切入无效
        // this.bar();
        // 获取代理对象并调用bar
        ((MyService) AopContext.currentProxy()).bar();
    }

    public void bar() {
        System.out.println("bar...");
    }
}
```

运行结果1，`foo()`方法被调用时，会执行前置通知打印日志，然后调用`this.bar()`方法，由于直接调用`this.bar()`方法绕过了AOP代理对象，因此不会触发AOP切面的逻辑。

```java
Before method foo is called.
foo...
bar...
```

运行结果2，`foo()`方法被调用时，会执行前置通知打印日志，然后调用`((MyService) AopContext.currentProxy()).bar();`方法。由于使用了`AopContext.currentProxy()`获取了当前的AOP代理对象，并调用了该代理对象的`bar()`方法，因此会触发AOP切面的逻辑。

```java
Before method foo is called.
foo...
Before method bar is called.
bar...
```

### 六、源码分析

在Spring AOP框架中，无论是在JDK动态代理还是CGLIB动态代理的拦截器中，都对`AopContext.setCurrentProxy(proxy)`进行了赋值操作。这个赋值操作的目的是将当前AOP代理对象设置为当前线程的上下文中，以便在方法内部可以通过`AopContext.currentProxy()`获取代理对象。

**JDK动态代理拦截器**

在`org.springframework.aop.framework.JdkDynamicAopProxy#invoke`方法中，是JDK动态代理拦截器的一部分。主要处理了AOP代理的上下文。具体来说，在方法执行前，如果AOP代理对象已经暴露了（即`this.advised.exposeProxy`为`true`），则通过`AopContext.setCurrentProxy(proxy)`方法将当前的AOP代理对象设置为当前线程的上下文中，以便在方法内部可以通过`AopContext.currentProxy()`来获取代理对象。在方法执行完成后，将之前设置的代理对象恢复，以保证AOP代理对象的上下文不会影响其他线程。

```java
@Override
@Nullable
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object oldProxy = null;
    boolean setProxyContext = false;
    // ... [代码部分省略以简化]
    try {
        // ... [代码部分省略以简化]
        if (this.advised.exposeProxy) {
            // Make invocation available if necessary.
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
        }
        // ... [代码部分省略以简化]
    }
    finally {
        // ... [代码部分省略以简化]
        if (setProxyContext) {
            // Restore old proxy.
            AopContext.setCurrentProxy(oldProxy);
        }
    }
}
```

**CGLIB动态代理拦截器**

在`org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept`方法中，是CGLIB动态代理拦截器的一部分。在方法拦截过程中，它主要处理了AOP代理的上下文。具体来说，如果AOP代理对象已经暴露了（即`this.advised.exposeProxy`为`true`），则通过`AopContext.setCurrentProxy(proxy)`方法将当前的AOP代理对象设置为当前线程的上下文中，以便在方法内部可以通过`AopContext.currentProxy()`来获取代理对象。在方法执行完成后，将之前设置的代理对象恢复，以保证AOP代理对象的上下文不会影响其他线程。

```java
@Override
@Nullable
public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    Object oldProxy = null;
    boolean setProxyContext = false;
    // ... [代码部分省略以简化]
    try {
        if (this.advised.exposeProxy) {
            // Make invocation available if necessary.
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
        }
        // ... [代码部分省略以简化]
    }
    finally {
        // ... [代码部分省略以简化]
        if (setProxyContext) {
            // Restore old proxy.
            AopContext.setCurrentProxy(oldProxy);
        }
    }
}
```
