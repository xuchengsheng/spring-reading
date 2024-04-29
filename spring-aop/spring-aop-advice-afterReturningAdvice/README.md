## AfterReturningAdvice

- [AfterReturningAdvice](#afterreturningadvice)
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

`AfterReturningAdvice`接口是Spring AOP框架中的一个核心接口，用于在目标方法执行后拦截并执行自定义逻辑。通过实现该接口的`afterReturning`方法，可以在目标方法成功返回结果后进行一些操作。

### 三、主要功能

1. **日志记录**

   + 记录目标方法的执行情况，如方法名称、参数值、返回结果等，以便跟踪应用程序的运行状态。

2. **性能监控**
   + 统计目标方法的执行时间，分析应用程序的性能瓶颈，优化程序性能。
   
3. **缓存处理**

   + 在方法返回结果后，将结果缓存起来，以提高后续相同请求的响应速度。

4. **统一处理**

   + 执行一些与业务逻辑无关的统一处理，如资源释放、清理等。

### 四、接口源码

`AfterReturningAdvice`接口主要用于在方法正常返回时执行后置通知。后置通知可以查看方法的返回值，但不能修改它，并且仅在方法正常返回时被调用，不会在抛出异常时被调用。该接口提供了一个`afterReturning`方法，在方法成功返回后进行回调，其中包含了返回值、被调用的方法、方法的参数以及方法调用的目标对象等信息。

```java
/**
 * 后置返回通知仅在方法正常返回时被调用，如果抛出异常则不会被调用。这样的通知可以查看方法的返回值，但不能修改它。
 * 
 * 作者：Rod Johnson
 * @see MethodBeforeAdvice
 * @see ThrowsAdvice
 */
public interface AfterReturningAdvice extends AfterAdvice {

    /**
     * 在给定方法成功返回后的回调。
     * @param returnValue 方法返回的值，如果有的话
     * @param method 被调用的方法
     * @param args 方法的参数
     * @param target 方法调用的目标对象。可能为{@code null}。
     * @throws Throwable 如果此对象希望中止调用。如果方法签名允许，将返回任何抛出的异常给调用者。否则，异常将被包装为运行时异常。
     */
    void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable;

}
```

### 五、主要实现

1. **AspectJAfterReturningAdvice** 

   + 实现了返回后通知，使用 AspectJ 风格定义的通知，用于在目标方法成功执行并返回结果后执行额外的逻辑。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class Advice {
<<Interface>>

}
class AfterAdvice {
<<Interface>>

}
class AfterReturningAdvice {
<<Interface>>

}
class AspectJAfterReturningAdvice

AfterAdvice  -->  Advice 
AfterReturningAdvice  -->  AfterAdvice 
AspectJAfterReturningAdvice  ..>  Advice 
AspectJAfterReturningAdvice  ..>  AfterAdvice 
AspectJAfterReturningAdvice  ..>  AfterReturningAdvice 
~~~

### 七、最佳实践

使用Spring AOP中的后置返回通知（AfterReturningAdvice）。首先，创建了一个代理工厂（ProxyFactory）并指定目标对象（MyService）。然后，创建了一个后置返回通知（MyAfterReturningAdvice）并添加到代理工厂中。接着，通过代理工厂获取代理对象，并调用代理对象的方法。

```java
public class AfterReturningAdviceDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyAfterReturningAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.foo();
    }
}
```

`MyAfterReturningAdvice`的类，它实现了Spring AOP框架中的`AfterReturningAdvice`接口。在`afterReturning`方法中，当目标方法成功返回结果时，它将打印出目标方法的名称以及返回的值。

```java
public class MyAfterReturningAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("After Method " + method.getName());
    }
}
```

`MyService` 类是一个简单的服务类，其中包含了一个名为 `foo()` 的方法。在上下文中，`MyService` 类被用作目标对象，即需要被拦截和增强的对象。

```java
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

运行结果，成功地执行了代理对象的`foo()`方法，并在方法执行完成后，后置返回通知`MyAfterReturningAdvice`被触发。

```java
foo...
After Method foo
```
