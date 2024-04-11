## ThrowsAdvice

- [ThrowsAdvice](#ThrowsAdvice)
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

`ThrowsAdvice`接口是Spring AOP中的一种通知类型，用于在方法抛出异常时执行额外的逻辑。实现该接口的类可以捕获方法抛出的异常并执行自定义的异常处理逻辑，比如记录日志、发送通知等。

### 三、主要功能

1. **捕获异常** 

   + 允许在目标方法抛出异常时捕获这些异常。

2. **执行额外逻辑** 

   + 提供了`afterThrowing()`方法，允许实现类在方法抛出异常时执行额外的逻辑，比如记录日志、发送通知等。

3. **参数传递** 

   + `afterThrowing()`方法提供了抛出异常的方法对象、参数数组、目标对象和异常对象作为参数，方便实现类在处理异常时获取相关信息。

4. **定制化处理** 

   + 可以根据业务需求定制化异常处理逻辑，使应用程序更加灵活和健壮。

### 四、接口源码

`ThrowsAdvice`接口，用作异常通知的标签接口。它没有任何方法，方法是通过反射调用的。实现类必须实现`afterThrowing()`方法，以处理方法抛出的异常。该方法的参数形式为`void afterThrowing([Method, args, target], ThrowableSubclass)`，前三个参数可选，用于提供关于连接点的更多信息，

```java
/**
 * 用于异常通知的标签接口。
 *
 * <p>该接口没有任何方法，因为方法是通过反射调用的。实现类必须实现以下形式的方法：
 *
 * <pre class="code">void afterThrowing([Method, args, target], ThrowableSubclass);</pre>
 *
 * <p>以下是一些有效的方法示例：
 *
 * <pre class="code">public void afterThrowing(Exception ex)</pre>
 * <pre class="code">public void afterThrowing(RemoteException)</pre>
 * <pre class="code">public void afterThrowing(Method method, Object[] args, Object target, Exception ex)</pre>
 * <pre class="code">public void afterThrowing(Method method, Object[] args, Object target, ServletException ex)</pre>
 *
 * 前三个参数是可选的，只有在我们需要有关连接点更多信息时才有用，如AspectJ中的<b>after-throwing</b>通知。
 *
 * <p><b>注意:</b> 如果throws-advice方法本身抛出异常，它将覆盖原始异常（即将异常更改为用户）。
 * 覆盖的异常通常是RuntimeException; 这与任何方法签名兼容。但是，如果throws-advice方法抛出一个已检查的异常，
 * 它将必须匹配目标方法的声明异常，并且在某种程度上与特定目标方法签名耦合。
 * <b>不要抛出与目标方法签名不兼容的未声明的检查异常！</b>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see AfterReturningAdvice
 * @see MethodBeforeAdvice
 */
public interface ThrowsAdvice extends AfterAdvice {

}
```

### 五、主要实现

暂无

### 六、最佳实践

使用`ThrowsAdvice`接口来处理方法抛出的异常。它创建了一个代理工厂，并将目标对象（`MyService`）和异常通知（`MyThrowsAdvice`）传递给代理工厂。然后，它通过代理工厂获取代理对象，并调用代理对象的方法`doSomethingException()`。

```java
public class ThrowsAdviceDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyThrowsAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomethingException();
    }
}
```

`MyThrowsAdvice`类实现了`ThrowsAdvice`接口，并定义了`afterThrowing()`方法，用于处理方法抛出的异常。当目标方法抛出异常时，该方法将被调用，并打印出异常信息。

```java
public class MyThrowsAdvice implements ThrowsAdvice {
    public void afterThrowing(Exception ex) throws Throwable {
        System.out.println("Exception thrown: " + ex.getMessage());
    }
}
```

`MyService`类包含了一个名为`doSomethingException()`的方法，该方法执行某些操作，并故意引发了一个异常（通过除以零）。

```java
public class MyService {

    public void doSomethingException() {
        System.out.println("Doing something exception...");
        int i = 1 / 0;
    }
}
```

运行结果，当调用了`MyService`类的`doSomethingException()`方法，但在该方法中发生了除以零的错误，导致了`java.lang.ArithmeticException: / by zero`异常的抛出。

```java
Doing something exception...
Exception thrown: / by zero
Exception in thread "main" java.lang.ArithmeticException: / by zero
	at com.xcs.spring.MyService.doSomethingException(MyService.java:7)
	at com.xcs.spring.MyService$$FastClassBySpringCGLIB$$c768e93b.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.adapter.ThrowsAdviceInterceptor.invoke(ThrowsAdviceInterceptor.java:113)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.xcs.spring.MyService$$EnhancerBySpringCGLIB$$abe9fbc2.doSomethingException(<generated>)
	at com.xcs.spring.ThrowsAdviceDemo.main(ThrowsAdviceDemo.java:15)
```

### 七、源码分析

暂无

### 八、常见问题

1. **异常处理的影响范围** 

   + 异常通知对整个目标方法的异常都起作用，这可能不是所期望的行为。有时候可能只想针对特定类型的异常执行特定的处理逻辑。

2. **异常信息的捕获和处理** 

   + 在异常通知中捕获到的异常信息可能不够详细，特别是对于大型应用程序中的复杂异常场景，可能需要更多的异常信息来进行适当的处理。

3. **异常类型的匹配**

   + 在异常通知中定义的异常类型需要与目标方法抛出的异常类型匹配，否则可能无法正确执行异常处理逻辑。

4. **对目标方法参数的访问** 

   + 在异常通知中可以访问目标方法的参数，但需要小心处理参数可能为空的情况，以避免出现空指针异常。