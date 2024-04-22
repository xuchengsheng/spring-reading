## AopContext

- [AopContext](#AopContext)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [五、主要实现](#五主要实现)
    - [六、最佳实践](#六最佳实践)
    - [七、源码分析](#七源码分析)
    - [八、常见问题](#八常见问题)

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

### 四、最佳实践

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

### 五、源码分析

在Spring AOP框架中，无论是在JDK动态代理还是CGLIB动态代理的拦截器中，都对`AopContext.setCurrentProxy(proxy)`进行了赋值操作。这个赋值操作的目的是将当前AOP代理对象设置为当前线程的上下文中，以便在方法内部可以通过`AopContext.currentProxy()`获取代理对象。

#### JDK动态代理拦截器

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

#### CGLIB动态代理拦截器

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

### 六、常见问题

1. **代理对象为空问题** 

   + 如果在没有启用`exposeProxy`选项的情况下尝试使用`AopContext.currentProxy()`来获取代理对象，则可能会导致返回的代理对象为空，因为AOP代理对象并未暴露给方法内部。

2. **多线程环境下的线程安全问题** 

   + `AopContext`是基于线程的，如果在多线程环境下并发调用了`AopContext.setCurrentProxy(proxy)`和`AopContext.currentProxy()`，可能会出现线程安全问题，因此需要谨慎处理多线程情况。

3. **性能问题** 

   + 在某些情况下，频繁地使用`AopContext.currentProxy()`来获取代理对象可能会带来性能开销，因为每次调用都需要对当前线程的上下文进行操作。

4. **可读性问题** 

   + 在方法内部频繁地使用`AopContext.currentProxy()`来获取代理对象可能会降低代码的可读性，因为会使代码变得复杂，难以理解