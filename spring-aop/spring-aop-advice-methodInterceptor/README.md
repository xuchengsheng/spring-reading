## MethodInterceptor

- [MethodInterceptor](#methodinterceptor)
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

`MethodInterceptor`接口是Spring框架中的一个核心接口，用于实现面向切面编程（AOP）。通过实现该接口，可以在目标方法执行前后、异常抛出时等关键点对方法进行拦截和增强，从而实现横切关注点的集中管理，提高代码的可维护性和灵活性。

### 三、主要功能

1. **方法拦截和增强** 

   + 可以在目标方法执行前后、异常抛出时等关键点对方法进行拦截和增强，从而实现横切关注点的代码集中管理。

2. **横切关注点的集中管理**

   + 可以对横切关注点（如日志记录、权限控制、事务管理等）进行集中管理，避免代码重复，提高代码的可维护性。

### 四、接口源码

`MethodInterceptor`接口是用于拦截接口方法调用并在目标方法之前和之后执行额外处理的核心接口。我们需要实现其中的`invoke`方法来定义拦截器的具体行为，例如，可以实现一个跟踪拦截器来追踪被拦截方法的调用情况。在`invoke`方法中，通常会调用`proceed()`方法来继续执行目标方法，并在必要时对返回值或异常进行处理。

```java
/**
 * 拦截器，用于拦截接口方法调用并在目标方法之前和之后执行额外处理。
 * 这些拦截器被嵌套在目标方法之上。
 *
 * <p>用户应该实现 {@link #invoke(MethodInvocation)} 方法来修改原始行为。例如，以下类实现了一个跟踪拦截器（跟踪所有被拦截方法的调用）：
 *
 * @author Rod Johnson
 */
@FunctionalInterface
public interface MethodInterceptor extends Interceptor {

    /**
     * 实现此方法以在调用之前和之后执行额外处理。礼貌的实现应该肯定地调用 {@link Joinpoint#proceed()}。
     * @param invocation 方法调用连接点
     * @return 调用 {@link Joinpoint#proceed()} 的结果；可能会被拦截器拦截
     * @throws Throwable 如果拦截器或目标对象抛出异常
     */
    @Nullable
    Object invoke(@Nonnull MethodInvocation invocation) throws Throwable;

}
```

### 五、主要实现

1. **MethodBeforeAdviceInterceptor** 

   + 实现了前置通知的拦截器。前置通知在目标方法执行之前执行，允许我们在方法执行前插入额外的逻辑。通常用于日志记录、参数验证等场景。

2. **AfterReturningAdviceInterceptor** 

   + 实现了返回后通知的拦截器。返回后通知在目标方法成功执行并返回结果后执行，允许我们在方法返回后插入额外的逻辑。通常用于日志记录、结果处理等场景。

3. **ThrowsAdviceInterceptor** 

   + 实现了异常抛出后通知的拦截器。异常抛出后通知在目标方法抛出异常后执行，允许我们在方法抛出异常后插入额外的逻辑。通常用于异常处理、日志记录等场景。

### 六、最佳实践

创建了一个代理工厂 `ProxyFactory`，并传入了目标对象 `MyService`。然后通过 `proxyFactory.addAdvice()` 方法添加了一个自定义的方法拦截器 `MyMethodInterceptor` 作为通知。接着，通过 `proxyFactory.getProxy()` 方法获取代理对象 `MyService` 的实例。最后，调用代理对象的方法 `doSomething()`。

```java
public class MethodInterceptorDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyMethodInterceptor());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomething();
    }
}
```

`MyMethodInterceptor` 类用于实现方法拦截和增强的功能。在 `invoke()` 方法中，首先通过 `MethodInvocation` 对象获取被调用方法的信息，例如方法名等，并在方法调用之前输出方法被调用的信息。然后调用 `invocation.proceed()` 方法来执行原始方法，获取方法执行结果。最后，在方法调用之后输出方法返回值，并将其返回。

```java
public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 在方法调用之前执行的逻辑
        System.out.println("Method " + invocation.getMethod().getName() + " is called.");
        // 调用原始方法
        Object result = invocation.proceed();
        // 在方法调用之后执行的逻辑
        System.out.println("Method " + invocation.getMethod().getName() + " returns " + result);
        return result;
    }
}
```

`MyService` 类是一个简单的服务类，其中包含了一个名为 `doSomething()` 的方法。在上下文中，`MyService` 类被用作目标对象，即需要被拦截和增强的对象。

```java
public class MyService {

    public void doSomething() {
        System.out.println("Doing something...");
    }
}
```

运行结果，在调用 `MyService` 实例的 `doSomething()` 方法时，`MyMethodInterceptor` 拦截器成功地拦截了方法的执行，并在方法执行前后添加了额外的逻辑处理。

```java
Method doSomething is called.
Doing something...
Method doSomething returns hello world
```

### 七、源码分析

暂无

### 八、常见问题

1. **使用问题** 

   + 我们可能会遇到如何正确实现和使用 `MethodInterceptor` 接口的问题，包括如何编写拦截器逻辑、如何将拦截器应用到目标方法上等。

2. **执行顺序问题** 

   + 在多个拦截器同时作用于同一个目标方法时，我们可能会关注拦截器的执行顺序问题，即哪个拦截器会先执行、哪个会后执行等。

3. **异常处理问题** 
   + 当目标方法执行过程中发生异常时，我们可能需要考虑如何在拦截器中处理异常，以及如何保证异常处理不会影响其他拦截器的执行。