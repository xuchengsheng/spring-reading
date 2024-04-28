## ProxyMethodInvocation

- [ProxyMethodInvocation](#ProxyMethodInvocation)
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

`ProxyMethodInvocation`是Spring AOP中的核心接口之一，用于表示代理方法调用，提供了获取方法、参数、目标对象等信息的方法，并允许拦截器链中的拦截器对方法调用进行处理，是实现方法拦截和增强逻辑的关键组件。

### 三、主要功能

1. **获取方法信息**

   + 通过`getMethod()`方法可以获取当前正在调用的方法对象，包括方法名、参数类型等信息。

2. **获取方法参数**

   + 使用`getArguments()`方法可以获取方法调用时传递的参数数组，允许拦截器对参数进行处理或检查。

3. **执行下一个拦截器或目标方法**

   + 通过`proceed()`方法可以继续执行拦截器链中的下一个拦截器或者调用目标方法。如果`proceed()`方法不被调用，拦截器链可能会被终止，不会执行目标方法。

4. **获取目标对象**

   + `getThis()`方法允许获取被代理的目标对象，即被拦截的对象实例。

5. **获取代理对象**

   + 通过`getProxy()`方法获取执行当前方法调用的代理对象，这对于需要在拦截器中替换返回值为代理对象的情况非常有用。

### 四、接口源码

`ProxyMethodInvocation`接口，它是AOP联盟 `MethodInvocation` 接口的扩展，允许访问通过方法调用所使用的代理对象。该接口提供了获取代理对象、创建方法调用的克隆、设置方法调用的参数、添加和获取用户属性等功能，用于在AOP环境中处理方法调用并进行增强。

```java
/**
 * 扩展了AOP联盟 {@link org.aopalliance.intercept.MethodInvocation} 接口，
 * 允许访问通过方法调用所使用的代理对象。
 *
 * <p>如果需要的话，通过此接口可以方便地使用代理对象替换返回值，
 * 例如如果调用目标返回了自身对象。
 *
 * @author Juergen Hoeller
 * @author Adrian Colyer
 * @since 1.1.3
 * @see org.springframework.aop.framework.ReflectiveMethodInvocation
 * @see org.springframework.aop.support.DelegatingIntroductionInterceptor
 */
public interface ProxyMethodInvocation extends MethodInvocation {

    /**
     * 返回执行此方法调用的代理对象。
     * @return 原始代理对象
     */
    Object getProxy();

    /**
     * 创建此对象的克隆。如果在此对象上调用 {@code proceed()} 之前进行克隆，
     * 则每个克隆可以调用 {@code proceed()} 一次，以多次调用连接点（以及其余的通知链）。
     * @return 此调用的可调用克隆。
     * {@code proceed()} 可以每个克隆调用一次。
     */
    MethodInvocation invocableClone();

    /**
     * 创建此对象的克隆，并指定克隆对象所使用的参数。如果在此对象上调用 {@code proceed()} 之前进行克隆，
     * 则每个克隆可以调用 {@code proceed()} 一次，以多次调用连接点（以及其余的通知链）。
     * @param arguments 克隆调用所使用的参数，覆盖原始参数
     * @return 此调用的可调用克隆。
     * {@code proceed()} 可以每个克隆调用一次。
     */
    MethodInvocation invocableClone(Object... arguments);

    /**
     * 设置将在此链中的后续调用中使用的参数。
     * @param arguments 参数数组
     */
    void setArguments(Object... arguments);

    /**
     * 向此方法调用添加指定的用户属性和给定的值。
     * <p>这些属性在AOP框架内部不使用。它们只是作为调用对象的一部分保留，
     * 供特殊拦截器使用。
     * @param key 属性的名称
     * @param value 属性的值，如果要重置则传入 {@code null}
     */
    void setUserAttribute(String key, @Nullable Object value);

    /**
     * 返回指定用户属性的值。
     * @param key 属性的名称
     * @return 属性的值，如果未设置则返回 {@code null}
     * @see #setUserAttribute
     */
    @Nullable
    Object getUserAttribute(String key);

}
```

### 五、主要实现

1. **ReflectiveMethodInvocation**

   + 通过Java反射机制执行方法调用。当目标对象是基于接口的JDK动态代理时，Spring会使用`ReflectiveMethodInvocation`来处理方法调用。它具有通用性但性能较低，适用于代理接口类型的目标对象。

2. **CglibMethodInvocation** 

   + 基于CGLIB动态代理生成子类来执行方法调用。当目标对象是基于类的CGLIB代理时，Spring会使用`CglibMethodInvocation`来处理方法调用。它通常比`ReflectiveMethodInvocation`性能更高，主要用于代理非接口类型的目标对象。

### 六、最佳实践

使用Java动态代理创建代理对象并调用方法。首先，创建了一个目标对象 `MyService target = new MyServiceImpl()`，然后通过 `Proxy.newProxyInstance()` 方法创建了代理对象，指定了目标对象的类加载器、实现的接口以及调用处理器。最后，通过代理对象调用方法，实际上会触发调用处理器中的 `invoke()` 方法来执行额外的逻辑。

```java
public class ProxyMethodInvocationDemo {

    public static void main(String[] args) {
        // 创建目标对象
        MyService target = new MyServiceImpl();
        // 获取目标对象的类对象
        Class<? extends MyService> clz = target.getClass();
        // 创建代理对象，并指定目标对象的类加载器、实现的接口以及调用处理器
        MyService proxyObject = (MyService) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new MyInvocationHandler(target));
        // 通过代理对象调用方法，实际上会调用 MyInvocationHandler 中的 invoke 方法
        proxyObject.foo();
    }
}
```

 `MyInvocationHandler` 类实现了 `InvocationHandler` 接口，作为 Java 动态代理的调用处理器。在 `invoke()` 方法中，它接收代理对象、方法对象和方法参数，并使用这些信息创建一个 `MyReflectiveMethodInvocation` 对象，然后调用 `proceed()` 方法来执行方法调用链。这个类的目的是将方法调用转发给拦截器链处理，以实现额外的逻辑或增强功能。

```java
/**
 * 自定义的 InvocationHandler 实现类，用于处理 Java 动态代理的方法调用。
 */
class MyInvocationHandler implements InvocationHandler {

    // 目标对象
    private final Object target;

    /**
     * 构造方法，初始化目标对象。
     * @param target 目标对象
     */
    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * 处理方法调用的核心方法。
     * @param proxy 代理对象
     * @param method 被调用的方法对象
     * @param args 方法参数
     * @return 方法调用结果
     * @throws Throwable 可能抛出的异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 创建 MyReflectiveMethodInvocation 对象，用于执行方法调用链
        MyReflectiveMethodInvocation invocation = new MyReflectiveMethodInvocation(proxy, target, method, args, target.getClass(), List.of(new MyMethodInterceptor()));
        // 执行方法调用链，并返回结果
        return invocation.proceed();
    }
}
```

我们自定义 `MyReflectiveMethodInvocation` 类是为了继承 Spring AOP 中的 `ReflectiveMethodInvocation` 并提供一个公开的构造方法。这样做允许你在自定义的方法调用对象中添加额外的逻辑或功能，并且可以在其它地方使用这个自定义的方法调用对象。

```java
/**
 * 自定义的方法调用对象，继承自 Spring AOP 的 ReflectiveMethodInvocation 类。
 * 用于在方法调用中加入自定义逻辑或增强功能。
 */
public class MyReflectiveMethodInvocation extends ReflectiveMethodInvocation {

    /**
     * 构造方法，初始化方法调用对象。
     * @param proxy 代理对象
     * @param target 目标对象
     * @param method 被调用的方法对象
     * @param arguments 方法参数
     * @param targetClass 目标对象的类
     * @param interceptorsAndDynamicMethodMatchers 拦截器链和动态方法匹配器列表
     */
    public MyReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
    }
}
```

`MyMethodInterceptor` 类用于实现方法拦截和增强的功能。在 `invoke()` 方法中，首先通过 `MethodInvocation` 对象获取被调用方法的信息，例如方法名等，并在方法调用之前输出方法被调用的信息。然后调用 `invocation.proceed()` 方法来执行原始方法，获取方法执行结果。最后并将其返回。

```java
public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 在方法调用之前执行的逻辑
        System.out.println("Before Method " + invocation.getMethod().getName());
        // 调用原始方法
        Object result = invocation.proceed();
        // 在方法调用之后执行的逻辑
        System.out.println("After Method " + invocation.getMethod().getName());
        return result;
    }
}
```

运行结果，在调用 `MyService` 实例的 `foo()` 方法时，`MyMethodInterceptor` 拦截器成功地拦截了方法的执行，并在方法执行前后添加了额外的逻辑处理。

```java
Before Method foo
foo...
After Method foo
```

### 七、源码分析

在`org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`方法中，首先判断当前拦截器索引是否到达了拦截器链的末尾，如果是，则调用连接点方法；否则，获取下一个拦截器或拦截器与动态方法匹配器对象，并进行动态方法匹配。如果方法匹配成功，则调用拦截器的 `invoke()` 方法；如果方法匹配失败，则跳过当前拦截器并调用链中的下一个拦截器。如果获取的是拦截器对象，则直接调用拦截器的 `invoke()` 方法。这个方法负责在方法调用链中依次执行拦截器或目标方法，实现了方法调用链的顺序执行。

```java
/**
 * 执行拦截器链中的下一个拦截器或目标方法。
 * @return 方法调用结果
 * @throws Throwable 可能抛出的异常
 */
@Override
@Nullable
public Object proceed() throws Throwable {
    // 我们从索引 -1 开始并提前递增。
    if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
       // 如果当前拦截器索引达到了拦截器链的末尾，则调用连接点方法。
       return invokeJoinpoint();
    }

    // 获取下一个拦截器或拦截器与动态方法匹配器对象
    Object interceptorOrInterceptionAdvice =
          this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
    if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
       // 如果是拦截器与动态方法匹配器，则进行动态方法匹配
       InterceptorAndDynamicMethodMatcher dm =
             (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
       // 获取目标类对象，如果目标类对象不为空则使用目标类对象，否则使用方法的声明类对象
       Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
       if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
          // 如果方法匹配成功，则调用拦截器的invoke方法
          return dm.interceptor.invoke(this);
       }
       else {
          // 动态匹配失败，跳过当前拦截器并调用链中的下一个拦截器
          return proceed();
       }
    }
    else {
       // 如果是拦截器，则直接调用拦截器的invoke方法
       return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
    }
}
```

在`org.springframework.aop.framework.ReflectiveMethodInvocation#invokeJoinpoint`方法中，使用反射调用连接点。

```java
/**
 * 使用反射调用连接点。
 * 子类可以重写此方法以使用自定义调用。
 * @return 连接点的返回值
 * @throws Throwable 如果调用连接点导致异常
 */
@Nullable
protected Object invokeJoinpoint() throws Throwable {
    // 使用反射调用连接点
    return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
}
```

在`org.springframework.aop.support.AopUtils#invokeJoinpointUsingReflection`方法中，通过反射调用目标方法，作为AOP方法调用的一部分。它接收目标对象、要调用的方法以及方法的参数作为输入，并尝试使用反射机制来调用方法。

```java
/**
 * 使用反射调用给定的目标方法，作为AOP方法调用的一部分。
 * @param target 目标对象
 * @param method 要调用的方法
 * @param args 方法的参数
 * @return 调用结果，如果有的话
 * @throws Throwable 如果目标方法抛出异常
 * @throws org.springframework.aop.AopInvocationException 如果发生反射错误
 */
@Nullable
public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args)
       throws Throwable {

    // 使用反射调用方法。
    try {
       // 设置方法可访问性
       ReflectionUtils.makeAccessible(method);
       // 调用方法
       return method.invoke(target, args);
    }
    catch (InvocationTargetException ex) {
       // 调用的方法抛出了已检查的异常。
       // 我们必须重新抛出它。客户端不会看到拦截器。
       throw ex.getTargetException();
    }
    catch (IllegalArgumentException ex) {
       // 如果发生参数错误，则抛出AOP调用异常
       throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" +
					method + "] on target [" + target + "]", ex);
    }
    catch (IllegalAccessException ex) {
       // 如果无法访问方法，则抛出AOP调用异常
       throw new AopInvocationException("Could not access method [" + method + "]", ex);
    }
}

```

### 八、常见问题

1. **方法调用链中的拦截器执行顺序问题**

   + 拦截器的执行顺序对于实现预期的方法拦截和增强至关重要。我们可能会遇到拦截器执行顺序不正确的问题，导致期望的功能无法正常工作。

2. **拦截器链中的异常处理**

   + 拦截器链中的一个拦截器可能会抛出异常，而这会影响到后续拦截器的执行或者最终方法调用的结果。如何正确地处理拦截器链中的异常是一个常见的问题。

3. **动态方法匹配器的使用**

   + 在某些情况下，拦截器可能需要根据方法的参数或其他条件来动态地决定是否执行。因此，我们需要了解如何正确地使用动态方法匹配器，并确保其逻辑正确性。