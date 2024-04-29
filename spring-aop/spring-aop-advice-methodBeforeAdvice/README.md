## MethodBeforeAdvice

- [MethodBeforeAdvice](#methodbeforeadvice)
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

`MethodBeforeAdvice`接口是Spring AOP中的一个核心接口，允许我们在目标方法执行之前插入自定义的逻辑，例如参数验证、日志记录等，从而实现面向切面编程的前置通知功能。

### 三、主要功能

1. **前置通知**

   + 允许我们在目标方法执行之前执行额外的逻辑操作。

2. **横切关注点的分离**

   + 将与业务逻辑无关的横切关注点（如日志记录、性能监控、安全验证等）与核心业务逻辑分离开来，提高代码的模块化和可维护性。

3. **参数验证**

   + 在目标方法执行前对方法参数进行验证，确保参数的合法性。

4. **权限控制**

   + 在方法执行前进行权限检查，确保只有具有足够权限的用户能够执行该方法。

### 四、接口源码

`MethodBeforeAdvice`接口，用于在方法调用之前执行通知。通知方法`before`接收被调用的方法、方法参数以及方法调用的目标对象作为参数，并可以抛出Throwable以中止方法调用。这样的通知无法阻止方法调用的继续进行，除非它们抛出了Throwable。

```java
/**
 * 在方法被调用之前调用的通知。这样的通知不能阻止方法调用的继续进行，除非它们抛出了一个Throwable。
 *
 * @author Rod Johnson
 * @see AfterReturningAdvice
 * @see ThrowsAdvice
 */
public interface MethodBeforeAdvice extends BeforeAdvice {

	/**
	 * 在给定方法被调用之前的回调。
	 * @param method 被调用的方法
	 * @param args 方法的参数
	 * @param target 方法调用的目标对象。可能为 {@code null}。
	 * @throws Throwable 如果此对象希望中止调用。任何抛出的异常如果方法签名允许，将返回给调用者。否则异常将作为运行时异常进行包装。
	 */
	void before(Method method, Object[] args, @Nullable Object target) throws Throwable;

}
```

### 五、主要实现

1. **AspectJMethodBeforeAdvice**

   - 实现了前置通知，使用 AspectJ 风格定义的通知，用于在目标方法执行前执行额外的逻辑。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class Advice {
<<Interface>>

}
class AspectJMethodBeforeAdvice
class BeforeAdvice {
<<Interface>>

}
class MethodBeforeAdvice {
<<Interface>>

}

AspectJMethodBeforeAdvice  ..>  Advice 
AspectJMethodBeforeAdvice  ..>  MethodBeforeAdvice 
BeforeAdvice  -->  Advice 
MethodBeforeAdvice  -->  BeforeAdvice 
~~~

### 七、最佳实践

使用`MethodBeforeAdvice`接口。首先，通过创建代理工厂和目标对象，然后创建自定义的前置通知`MyMethodBeforeAdvice`，将其添加到代理工厂中。接着，通过代理工厂获取代理对象，并调用代理对象的方法。在方法调用之前，前置通知会被触发执行，执行自定义的逻辑。

```java
public class MethodBeforeAdviceDemo {

    public static void main(String[] args) {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyMethodBeforeAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomething();
    }
}
```

`MyMethodBeforeAdvice`类实现了`MethodBeforeAdvice`接口，在其`before`方法中，打印出目标方法被调用之前的信息，包括方法名。这个类可以用作Spring AOP中的前置通知，在目标方法执行之前执行一些额外的逻辑。

```java
public class MyMethodBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before Method " + method.getName());
    }
}
```

`MyService` 类是一个简单的服务类，其中包含了一个名为 `doSomething()` 的方法。在上下文中，`MyService` 类被用作目标对象，即需要被拦截和增强的对象。

```java
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

运行结果，调用目标方法`foo`之前，`MyMethodBeforeAdvice`中的前置通知被成功触发，并打印了相应的信息。

```java
Before Method foo
foo...
```
