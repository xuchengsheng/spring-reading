## AdvisorAdapter

- [AdvisorAdapter](#advisoradapter)
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

`AdvisorAdapter` 接口是 Spring AOP 中的一个重要接口，用于将不同类型的通知（Advice）适配到拦截器链中，以便将其应用于目标方法的执行。它允许我们自定义适配器来将自定义的通知与 Spring AOP 框架结合，从而实现对目标方法的前置、后置、环绕等类型的增强操作，为 AOP 的灵活性和可扩展性提供了支持。

### 三、主要功能

1. **通知适配** 

   + 将不同类型的通知（Advice）适配到 Spring AOP 拦截器链中，以便将其应用于目标方法的执行。

2. **支持不同通知类型** 

   + 支持适配各种类型的通知，包括前置通知（MethodBeforeAdvice）、后置通知（AfterReturningAdvice）、环绕通知（MethodInterceptor）、抛出异常通知（ThrowsAdvice）等。

3. **适配器注册和管理** 

   + 允许我们注册和管理不同类型通知的适配器，以便在应用中使用不同类型的通知。


### 四、接口源码

这个接口定义了一种机制，允许向 Spring AOP 框架中引入新的 Advisor 和 Advice 类型。实现该接口的对象可以将自定义的 Advice 类型转换为 AOP Alliance 拦截器，使得这些自定义的 Advice 类型能够在 Spring AOP 框架中被使用。通常情况下，大多数 Spring 用户不需要直接实现这个接口；只有在需要引入新的 Advisor 或 Advice 类型时才需要这样做。

```java
/**
 * 允许扩展 Spring AOP 框架的接口，以处理新的 Advisor 和 Advice 类型。
 *
 * <p>实现该接口的对象可以从自定义的 Advice 类型创建 AOP Alliance 拦截器，
 * 从而使得这些 Advice 类型可以在 Spring AOP 框架中使用，该框架在底层使用拦截。
 *
 * <p>大多数 Spring 用户无需实现此接口；只有在需要向 Spring 引入更多的 Advisor 或 Advice 类型时才需要这样做。
 *
 * @author Rod Johnson
 */
public interface AdvisorAdapter {

    /**
     * 此适配器是否了解该通知对象？是否可以使用 Advisor 包含此通知作为参数调用 getInterceptors 方法？
     * @param advice 一个 Advice，如 BeforeAdvice
     * @return 此适配器是否了解给定的 Advice 对象
     * @see #getInterceptor(org.springframework.aop.Advisor)
     * @see org.springframework.aop.BeforeAdvice
     */
    boolean supportsAdvice(Advice advice);

    /**
     * 返回一个 AOP Alliance MethodInterceptor，将给定 Advice 的行为暴露给基于拦截的 AOP 框架。
     * <p>不必担心 Advisor 中包含的 Pointcut；AOP 框架将负责检查切点。
     * @param advisor Advisor。supportsAdvice() 方法必须在此对象上返回 true
     * @return 此 Advisor 的 AOP Alliance 拦截器。无需为效率缓存实例，因为 AOP 框架会缓存 Advice 链。
     */
    MethodInterceptor getInterceptor(Advisor advisor);
}
```

### 五、主要实现

1. **MethodBeforeAdviceAdapter** 

   + 用于将 `MethodBeforeAdvice` 类型的通知适配到 Spring AOP 拦截器链中。`MethodBeforeAdvice` 是一个在目标方法执行前执行的通知接口。

2. **ThrowsAdviceAdapter** 

   + 用于将 `ThrowsAdvice` 类型的通知适配到 Spring AOP 拦截器链中。`ThrowsAdvice` 通知用于捕获目标方法抛出的异常。

3. **AfterReturningAdviceAdapter** 

   + 用于将 `AfterReturningAdvice` 类型的通知适配到 Spring AOP 拦截器链中。`AfterReturningAdvice` 通知在目标方法正常返回后执行。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AdvisorAdapter {
<<Interface>>

}
class AfterReturningAdviceAdapter
class MethodBeforeAdviceAdapter
class ThrowsAdviceAdapter

AfterReturningAdviceAdapter  ..>  AdvisorAdapter 
MethodBeforeAdviceAdapter  ..>  AdvisorAdapter 
ThrowsAdviceAdapter  ..>  AdvisorAdapter 
~~~

### 七、最佳实践

用自定义的 AdvisorAdapter 和 Advice 来实现对目标方法的增强。在示例中，首先注册了一个自定义的 AdvisorAdapter（NullReturningAdviceAdapter），然后创建了一个代理工厂（ProxyFactory）并向其添加了一个自定义的通知（MyNullReturningAdvice）。最后，通过代理工厂获取了代理对象，并调用了两个方法，其中一个方法会触发通知，另一个方法不会触发通知。

```java
public class AdvisorAdapterDemo {

    public static void main(String[] args) {
        // 注册自定义适配器
        GlobalAdvisorAdapterRegistry.getInstance().registerAdvisorAdapter(new NullReturningAdviceAdapter());
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 添加Advisor
        proxyFactory.addAdvice(new MyNullReturningAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 不会触发通知
        System.out.println("foo return value : " + proxy.foo());
        // 换行
        System.out.println("==================================");
        // 会触发通知
        System.out.println("bar return value : " + proxy.bar());
    }
}
```

一个空返回通知的适配器，用于将空返回通知（NullReturningAdvice）适配到拦截器链中。它实现了 AdvisorAdapter 接口，包含了支持给定通知和获取方法拦截器的功能，以便将特定类型的通知行为暴露给基于拦截的 AOP 框架。

```java
/**
 * 空返回通知适配器，用于将空返回通知（NullReturningAdvice）适配到拦截器链中。
 */
public class NullReturningAdviceAdapter implements AdvisorAdapter {
    
    /**
     * 判断该适配器是否支持给定的通知。
     * @param advice 一个通知，如空返回通知（NullReturningAdvice）
     * @return 如果该适配器支持给定的通知，则返回 true；否则返回 false
     */
    @Override
    public boolean supportsAdvice(Advice advice) {
        return (advice instanceof NullReturningAdvice);
    }

    /**
     * 获取一个方法拦截器，将给定的通知行为暴露给基于拦截的 AOP 框架。
     * @param advisor Advisor。supportsAdvice() 方法必须在此对象上返回 true
     * @return 给定 Advisor 的方法拦截器
     */
    @Override
    public MethodInterceptor getInterceptor(Advisor advisor) {
        NullReturningAdvice advice = (NullReturningAdvice) advisor.getAdvice();
        return new NullReturningAdviceInterceptor(advice);
    }
}
```

一个空返回通知拦截器，用于在方法执行后检查返回值是否为空，并根据情况执行空返回通知的逻辑。它实现了 MethodInterceptor 和 AfterAdvice 接口，通过拦截方法调用后的返回值来判断是否需要执行空返回通知，并在必要时调用空返回通知的逻辑。

```java
/**
 * 空返回通知拦截器，用于在方法执行后检查返回值是否为空，并根据情况执行空返回通知的逻辑。
 */
public class NullReturningAdviceInterceptor implements MethodInterceptor, AfterAdvice {

    /** 空返回通知 */
    private final NullReturningAdvice advice;

    /**
     * 构造一个空返回通知拦截器。
     * @param advice 空返回通知
     */
    public NullReturningAdviceInterceptor(NullReturningAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
    }

    /**
     * 在方法执行后拦截，检查返回值是否为空，并根据情况执行空返回通知的逻辑。
     * @param mi 方法调用信息
     * @return 方法执行结果，如果返回值为空，则根据空返回通知执行后的返回值
     * @throws Throwable 如果方法调用过程中发生异常，则抛出异常
     */
    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        // 执行方法调用，获取返回值
        Object retVal = mi.proceed();
        // 如果返回值为空，则根据空返回通知执行后的返回值
        if (retVal == null) {
            retVal = this.advice.nullReturning(mi.getMethod(), mi.getArguments(), mi.getThis());
        }
        return retVal;
    }
}
```

一个空返回通知的定义，继承了 AfterAdvice 接口。它包含了一个方法 nullReturning，用于在目标方法返回值为空时执行相应的逻辑，并返回一个新的返回值。

```java
/**
 * 空返回通知接口，继承自 AfterAdvice。
 */
public interface NullReturningAdvice extends AfterAdvice {

    /**
     * 当目标方法返回值为空时调用的方法。
     * @param method 目标方法
     * @param args 方法参数
     * @param target 目标对象
     * @return 空返回通知执行后的返回值
     * @throws Throwable 如果在执行空返回通知的过程中发生异常，则抛出异常
     */
    Object nullReturning(Method method, Object[] args, @Nullable Object target) throws Throwable;
}

```

实现了`NullReturningAdvice`空返回通知接口，用于在目标方法返回值为空时执行特定逻辑。在 nullReturning 方法中返回一个默认的字符串值。

```java
public class MyNullReturningAdvice implements NullReturningAdvice {

    @Override
    public Object nullReturning(Method method, Object[] args, Object target) throws Throwable {
        return "this is a defaultValue";
    }
}
```

简单的服务类，包含了两个方法 foo 和 bar。foo 方法执行后返回字符串 "this is a foo"，而 bar 方法执行后返回 null。

```java
public class MyService {

    public String foo() {
        System.out.println("foo...");
        return "this is a foo";
    }

    public String bar() {
        System.out.println("bar...");
        return null;
    }
}
```

运行结果，调用了 foo 方法，它返回 "this is a foo"；然后调用了 bar 方法，由于其返回值为 null，因此触发了空返回通知，打印了相应的消息，并返回了默认值 "this is a defaultValue"。

```java
foo...
foo return value : this is a foo
==================================
bar...
bar return value : this is a defaultValue
```

### 八、源码分析

**注册适配器**

在`org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry#registerAdvisorAdapter`方法中，向适配器列表中注册一个新的 AdvisorAdapter 实例。

```java
/**
 * 注册一个Advisor适配器。
 * @param adapter 要注册的Advisor适配器
 */
@Override
public void registerAdvisorAdapter(AdvisorAdapter adapter) {
    this.adapters.add(adapter);
}
```

在`org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry#adapters`字段中，用于存储 AdvisorAdapter 实例

```java
private final List<AdvisorAdapter> adapters = new ArrayList<>(3);
```

**适配器转换拦截器**

在`org.springframework.aop.framework.JdkDynamicAopProxy#invoke`方法中，JDK动态代理入口中，获取指定方法的拦截链。

```java
@Override
@Nullable
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // ... [代码部分省略以简化]

    try {
        // ... [代码部分省略以简化]

        // Get the interception chain for this method.
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

        // ... [代码部分省略以简化]
    }
    finally {
        // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept`方法中，CGLIB动态代理入口中，获取指定方法的拦截链。

```java
@Override
@Nullable
public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    // ... [代码部分省略以简化]
    try {
        // ... [代码部分省略以简化]
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
        // ... [代码部分省略以简化]
    }
    finally {
        // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.aop.framework.AdvisedSupport#getInterceptorsAndDynamicInterceptionAdvice`方法中，配置确定给定方法的拦截器链，首先尝试从缓存中获取，如果缓存中不存在，则通过 AdvisorChainFactory 获取，并将结果存入缓存后返回。

```java
/**
 * 根据配置确定给定方法的拦截器链。
 * @param method 被代理的方法
 * @param targetClass 目标类
 * @return 方法拦截器列表（可能还包括 InterceptorAndDynamicMethodMatchers）
 */
public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, @Nullable Class<?> targetClass) {
    // 创建方法缓存键
    MethodCacheKey cacheKey = new MethodCacheKey(method);
    // 从缓存中获取拦截器链
    List<Object> cached = this.methodCache.get(cacheKey);
    // 如果缓存为空
    if (cached == null) {
        // 通过AdvisorChainFactory获取拦截器链
        cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(
                this, method, targetClass);
        // 将拦截器链放入缓存中
        this.methodCache.put(cacheKey, cached);
    }
    // 返回拦截器链
    return cached;
}
```

在`org.springframework.aop.framework.DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`方法中，根据给定的AOP配置和方法，从Advisor列表中获取适用于该方法的拦截器链和动态拦截通知。然后根据配置和目标类的匹配情况选择性地添加适当的拦截器到列表中，并返回该列表。

```java
/**
 * 根据给定的AOP配置和方法，获取拦截器链和动态拦截通知。
 * @param config AOP配置对象
 * @param method 被代理的方法
 * @param targetClass 目标类
 * @return 返回一个拦截器链和动态拦截通知的列表
 */
@Override
public List<Object> getInterceptorsAndDynamicInterceptionAdvice(
       Advised config, Method method, @Nullable Class<?> targetClass) {

    // 获取全局的AdvisorAdapterRegistry实例
    AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
    // 获取AOP配置中的所有Advisor数组
    Advisor[] advisors = config.getAdvisors();
    // 创建一个拦截器列表，初始化大小为advisors数组的长度
    List<Object> interceptorList = new ArrayList<>(advisors.length);
    
    // ... [代码部分省略以简化]

    // 遍历所有的Advisor
    for (Advisor advisor : advisors) {
        
       // ... [代码部分省略以简化]
        
       // 获取Advisor对应的拦截器数组
       Interceptor[] interceptors = registry.getInterceptors(advisor);
       // 将拦截器数组添加到拦截器列表中
       interceptorList.addAll(Arrays.asList(interceptors));
        
       // ... [代码部分省略以简化]
    }

    // 返回拦截器列表
    return interceptorList;
}
```

在`org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry#getInterceptors`方法中，根据给定的Advisor对象，获取其对应的拦截器数组。它首先检查Advisor中的Advice类型，如果是MethodInterceptor类型，则直接添加到拦截器列表中。然后遍历注册的AdvisorAdapter，查找适配器支持的Advice类型，并将适配器返回的拦截器添加到列表中。最后将拦截器列表转换为数组并返回，如果未找到适配的拦截器则抛出UnknownAdviceTypeException异常。

```java
/**
 * 根据Advisor获取拦截器数组。
 * @param advisor Advisor对象
 * @return 返回拦截器数组
 * @throws UnknownAdviceTypeException 如果Advisor中的Advice类型无法识别
 */
@Override
public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
    // 创建一个拦截器列表
    List<MethodInterceptor> interceptors = new ArrayList<>(3);
    // 获取Advisor中的Advice对象
    Advice advice = advisor.getAdvice();
    // 如果Advice对象是MethodInterceptor类型
    if (advice instanceof MethodInterceptor) {
       // 将MethodInterceptor添加到拦截器列表中
       interceptors.add((MethodInterceptor) advice);
    }
    // 遍历所有的适配器
    for (AdvisorAdapter adapter : this.adapters) {
       // 如果适配器支持Advice对象
       if (adapter.supportsAdvice(advice)) {
          // 获取适配器的拦截器并添加到拦截器列表中
          interceptors.add(adapter.getInterceptor(advisor));
       }
    }
    // 如果拦截器列表为空
    if (interceptors.isEmpty()) {
       // 抛出未知通知类型异常
       throw new UnknownAdviceTypeException(advisor.getAdvice());
    }
    // 将拦截器列表转换为数组并返回
    return interceptors.toArray(new MethodInterceptor[0]);
}
```

在`com.xcs.spring.NullReturningAdviceAdapter#supportsAdvice`方法中，检查该适配器是否支持给定的Advice类型。

```java
/**
 * 检查该适配器是否支持给定的Advice类型。
 * @param advice Advice对象
 * @return 如果适配器支持给定的Advice类型，则返回true；否则返回false
 */
@Override
public boolean supportsAdvice(Advice advice) {
    // 检查Advice对象是否是NullReturningAdvice类型
    return (advice instanceof NullReturningAdvice);
}
```

在`com.xcs.spring.NullReturningAdviceAdapter#getInterceptor`方法中，首先从Advisor中获取Advice对象，并将其强制转换为NullReturningAdvice类型。然后，使用该Advice对象创建一个NullReturningAdviceInterceptor拦截器，并返回。

```java
/**
 * 根据Advisor获取拦截器。
 * @param advisor Advisor对象
 * @return 返回一个拦截器
 */
@Override
public MethodInterceptor getInterceptor(Advisor advisor) {
    // 强制转换Advisor中的Advice对象为NullReturningAdvice类型
    NullReturningAdvice advice = (NullReturningAdvice) advisor.getAdvice();
    // 创建一个NullReturningAdviceInterceptor拦截器，将Advisor中的Advice作为参数传入
    return new NullReturningAdviceInterceptor(advice);
}
```
