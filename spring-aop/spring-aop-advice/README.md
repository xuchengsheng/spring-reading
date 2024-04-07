## Advice

- [Advice](#advice)
  - [一、基本信息](#一基本信息)
  - [二、知识储备](#二知识储备)
  - [三、基本描述](#三基本描述)
  - [四、主要功能](#四主要功能)
  - [五、接口源码](#五接口源码)
    - [Interceptor](#interceptor)
    - [BeforeAdvice](#beforeadvice)
    - [AfterAdvice](#afteradvice)
    - [ThrowsAdvice](#throwsadvice)
    - [IntroductionAdvice](#introductionadvice)
  - [六、主要实现](#六主要实现)
  - [七、最佳实践](#七最佳实践)
    - [MethodInterceptor](#methodinterceptor)
    - [BeforeAdvice](#beforeadvice-1)
    - [AfterReturningAdvice](#afterreturningadvice)
    - [ThrowsAdvice](#throwsadvice-1)
    - [IntroductionInterceptor](#introductioninterceptor)
  - [八、与其他组件的关系](#八与其他组件的关系)
  - [九、常见问题](#九常见问题)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. **AOP概念**

   + 了解什么是 AOP 以及它的基本原理和概念是理解 `Advice` 接口的前提。AOP 是一种编程范式，允许我们在程序的核心逻辑之外定义交叉关注点的功能。

2. **通知类型**

   + 了解 AOP 中的各种通知类型，如前置通知、后置通知、异常通知、最终通知和环绕通知。这些通知类型描述了在目标方法执行的不同阶段要执行的操作。

3. **AOP 框架**

   + 熟悉至少一种 AOP 框架（如 Spring AOP、AspectJ 等）是非常有帮助的。了解如何在特定框架中配置和使用 `Advice` 接口以及如何将其织入到应用程序中。

4. **代理模式**

   + 了解代理模式的基本原理和实现方式有助于理解 AOP 中的概念。在 AOP 中，通常使用动态代理来实现横切关注点。

### 三、基本描述

`Advice` 接口是 AOP（面向切面编程）的核心接口之一，用于定义在程序执行过程中在特定切点处要执行的操作。通过实现该接口的不同实现类，可以实现各种类型的通知，包括前置通知、后置通知、异常通知、最终通知和环绕通知，以实现横切关注点的功能。

### 四、主要功能

1. **定义通知类型**

   + Advice 接口定义了不同类型的通知，包括前置通知、后置通知、异常通知、最终通知和环绕通知。

2. **定义通知的执行逻辑**

   + 每个实现了 Advice 接口的类都可以定义通知的具体执行逻辑，例如在方法执行前后执行特定的操作，处理方法返回值或异常等。

3. **实现横切关注点**

   + 通过 Advice 接口的实现类，可以将通知织入到应用程序的特定切点处，实现横切关注点的功能，如日志记录、性能监视、事务管理等。

4. **与AOP框架集成**

   + Advice 接口是 AOP 框架中通知的抽象表示，可以与不同的 AOP 框架集成，例如 Spring AOP、AspectJ 等。

5. **增强代码可维护性和灵活性**

   + 通过将通知的逻辑抽象为 Advice 接口的实现类，可以使代码更具灵活性和可维护性，将横切关注点与核心业务逻辑分离，提高了代码的模块化程度和可复用性。

### 五、接口源码

`Advice` 接口是一个标记接口，用于表示 AOP 中的通知，可以是任何类型的通知，例如拦截器。

```java
/**
 * Advice 的标记接口。实现可以是任何类型的通知，例如拦截器。
 * 
 * @author Rod Johnson
 * @version $Id: Advice.java,v 1.1 2004/03/19 17:02:16 johnsonr Exp $
 */
public interface Advice {

}
```

#### Interceptor

`Interceptor` 接口表示一个通用的拦截器，用于拦截基本程序中发生的运行时事件。这些事件可以是调用、字段访问、异常等，在程序执行过程中由连接点实现。通常，`Interceptor` 接口的实现类会拦截特定类型的事件，并在必要时执行相关的逻辑。

```java
/**
 * 这个接口表示一个通用的拦截器。
 *
 * <p>通用的拦截器可以拦截基本程序中发生的运行时事件。这些事件由连接点（joinpoint）实现（reified）。运行时连接点可以是调用、字段访问、异常等。
 *
 * <p>这个接口不会直接使用。使用子接口来拦截特定的事件。例如，下面的类实现了一些特定的拦截器，以实现一个调试器：
 *
 * @author Rod Johnson
 * @see Joinpoint
 */
public interface Interceptor extends Advice {
}
```

`MethodInterceptor` 接口表示一个方法拦截器，用于拦截目标对象的接口方法调用，并在目标对象之上执行。实现该接口的类可以在方法调用前后执行额外的处理，例如跟踪方法调用、日志记录、权限检查等。通过实现 `invoke` 方法，可以在方法调用前后执行自定义的逻辑，并决定是否调用 `Joinpoint.proceed()` 继续执行原始方法。

```java
/**
 * 拦截目标对象的接口方法调用，并在目标对象之上执行。这些拦截器嵌套在目标对象的上方。
 *
 * <p>用户应该实现 {@link #invoke(MethodInvocation)} 方法来修改原始行为。例如，以下类实现了一个跟踪拦截器（跟踪所有被拦截方法的调用）：
 *
 * @author Rod Johnson
 */
@FunctionalInterface
public interface MethodInterceptor extends Interceptor {

    /**
     * 实现这个方法以在调用前后执行额外的处理。有礼貌的实现肯定会调用 {@link Joinpoint#proceed()}。
     * @param invocation 方法调用连接点
     * @return {@link Joinpoint#proceed()} 方法调用的结果；可能会被拦截器拦截
     * @throws Throwable 如果拦截器或目标对象抛出异常
     */
    @Nullable
    Object invoke(@Nonnull MethodInvocation invocation) throws Throwable;

}

```

`ConstructorInterceptor` 接口表示一个构造器拦截器，用于拦截新对象的构造。通过实现该接口的类，可以在对象构造之前和之后执行额外的处理，例如实现单例模式、初始化对象属性等。通过实现 `construct` 方法，可以在构造新对象时执行自定义的逻辑，并决定是否调用 `Joinpoint.proceed()` 继续执行原始构造过程。

```java
/**
 * 拦截新对象的构造。
 *
 * <p>用户应该实现 {@link #construct(ConstructorInvocation)} 方法来修改原始行为。例如，以下类实现了一个单例拦截器（只允许拦截的类有一个唯一实例）：
 *
 * @author Rod Johnson
 */
public interface ConstructorInterceptor extends Interceptor  {

    /**
     * 实现这个方法以在构造新对象之前和之后执行额外的处理。有礼貌的实现肯定会调用 {@link Joinpoint#proceed()}。
     * @param invocation 构造连接点
     * @return 新创建的对象，也是对 {@link Joinpoint#proceed()} 方法调用的结果；可能会被拦截器替换
     * @throws Throwable 如果拦截器或目标对象抛出异常
     */
    @Nonnull
    Object construct(ConstructorInvocation invocation) throws Throwable;

}
```

#### BeforeAdvice

`BeforeAdvice` 接口是一个通用的标记接口，用于表示前置通知，在方法执行之前执行特定的操作。

```java
/**
 * BeforeAdvice 是一个通用的标记接口，用于表示前置通知，例如 {@link MethodBeforeAdvice}。
 * 
 * <p>Spring 仅支持方法前置通知。尽管这种情况不太可能改变，但此 API 的设计允许在将来如果需要的话支持字段通知。
 * 
 * @author Rod Johnson
 * @see AfterAdvice
 */
public interface BeforeAdvice extends Advice {
}
```

`MethodBeforeAdvice` 接口是一个通知接口，用于在方法被调用之前执行特定的操作。它的实现类可以在目标方法执行前拦截方法调用，并在拦截到方法调用时执行定义的逻辑。通常用于执行诸如日志记录、权限验证等与方法调用前相关的操作。

```java
/**
 * 在方法被调用之前调用的通知。这种通知不能阻止方法调用的进行，除非它们抛出一个 Throwable。
 * 
 * @author Rod Johnson
 * @see AfterReturningAdvice
 * @see ThrowsAdvice
 */
public interface MethodBeforeAdvice extends BeforeAdvice {

    /**
     * 在给定方法调用之前的回调。
     * @param method 被调用的方法
     * @param args 方法的参数
     * @param target 方法调用的目标对象。可能为 {@code null}。
     * @throws Throwable 如果此对象希望中止调用。如果允许的话，任何抛出的异常都将返回给调用者，否则异常将被包装为运行时异常。
     */
    void before(Method method, Object[] args, @Nullable Object target) throws Throwable;

}
```

#### AfterAdvice

`AfterAdvice` 接口是一个通用的标记接口，用于表示后置通知，在方法执行之后执行特定的操作。

```java
/**
 * AfterAdvice 是一个通用的标记接口，用于表示后置通知，例如 {@link AfterReturningAdvice} 和 {@link ThrowsAdvice}。
 * 
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see BeforeAdvice
 */
public interface AfterAdvice extends Advice {
}
```

`AfterReturningAdvice` 接口表示一个后置通知，在目标方法成功返回时被调用。这种通知可以查看方法的返回值，但无法修改它。通常用于在方法返回后执行一些清理工作或记录日志等操作。

```java
/**
 * AfterReturningAdvice 仅在方法正常返回时调用，如果抛出异常则不会调用。这种通知可以看到返回值，但不能改变它。
 * 
 * @author Rod Johnson
 * @see MethodBeforeAdvice
 * @see ThrowsAdvice
 */
public interface AfterReturningAdvice extends AfterAdvice {

    /**
     * 在给定方法成功返回后的回调。
     * @param returnValue 方法返回的值，如果有的话
     * @param method 被调用的方法
     * @param args 方法的参数
     * @param target 方法调用的目标对象。可能为 {@code null}。
     * @throws Throwable 如果此对象希望中止调用。如果允许的话，任何抛出的异常都将返回给调用者，否则异常将被包装为运行时异常。
     */
    void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable;

}

```

#### ThrowsAdvice

`ThrowsAdvice` 是一个标记接口，用于表示异常通知。它没有定义任何方法，因为异常通知的方法是通过反射调用的。实现 `ThrowsAdvice` 接口的类需要实现特定形式的方法签名，以捕获目标方法抛出的异常，并在必要时处理它们。异常通知通常用于在方法抛出异常时执行一些清理操作或记录异常信息等。

```java
/**
 * ThrowsAdvice 是一个标记接口，用于表示异常通知。
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
 * 前三个参数是可选的，只有在我们想要更多关于连接点的信息时才有用，如 AspectJ 中的 <b>after-throwing</b> 通知。
 *
 * <p><b>注意：</b> 如果一个 throws-advice 方法本身抛出异常，它将覆盖原始异常（即更改向用户抛出的异常）。覆盖的异常通常是 RuntimeException；这与任何方法签名兼容。但是，如果 throws-advice 方法抛出一个已检查的异常，它必须与目标方法的声明异常匹配，因此在某种程度上与特定目标方法签名耦合。<b>不要抛出与目标方法签名不兼容的未声明的已检查异常！</b>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see AfterReturningAdvice
 * @see MethodBeforeAdvice
 */
public interface ThrowsAdvice extends AfterAdvice {
}
```

####  IntroductionAdvice

`IntroductionInterceptor` 类是 AOP Alliance 中的 `MethodInterceptor` 接口的子接口，它允许拦截器实现额外的接口，并通过拦截器的代理来使用这些接口。这个接口引入了 AOP 中的一个基本概念，称为引入，通常用于实现混入（mixins），使得可以构建具有多继承目标的复合对象。通过实现 `IntroductionInterceptor` 接口，可以实现在方法拦截的同时向目标对象引入新的行为或接口。

```java
/**
 * AOP Alliance MethodInterceptor 的子接口，允许拦截器实现额外的接口，并通过该拦截器的代理使用这些接口。这是一个基本的 AOP 概念，称为 <b>引入</b>。
 *
 * <p>引入通常是 <b>混入</b>，允许构建可以实现多继承目标的复合对象。
 *
 * @author Rod Johnson
 * @see DynamicIntroductionAdvice
 */
public interface IntroductionInterceptor extends MethodInterceptor, DynamicIntroductionAdvice {

}
```

### 六、主要实现

1. **MethodInterceptor**
   + 实现了 `Advice` 接口的子接口，用于定义环绕通知，即围绕目标方法执行的通知，可以在方法调用之前和之后执行自定义的逻辑。
2. **BeforeAdvice**
   + 实现了 `Advice` 接口的子接口，用于定义前置通知，即在目标方法执行之前执行的通知。
3. **AfterReturningAdvice**
   + 实现了 `Advice` 接口的子接口，用于定义返回后通知，即在目标方法正常返回后执行的通知。
4. **ThrowsAdvice**
   + 实现了 `Advice` 接口的子接口，用于定义异常通知，即在目标方法抛出异常后执行的通知。
5. **IntroductionInterceptor**
   + 实现了 `Advice` 接口的子接口，用于实现引入通知，即向目标对象引入新的行为或接口。

### 七、最佳实践

#### MethodInterceptor

使用 Spring AOP 中的 `MethodInterceptor` 来实现方法拦截器。在 `methodInterceptor` 方法中，首先创建了一个 `ProxyFactory` 对象，并传入了一个目标对象 `MyService` 的实例。然后，通过 `addAdvice` 方法为代理工厂添加了一个方法拦截器 `MyMethodInterceptor`。接着，通过代理工厂的 `getProxy` 方法获取代理对象，并调用其方法 `doCustomMethodInterceptor()`。在方法执行时，方法拦截器会拦截目标方法的调用，并执行自定义的逻辑。

```java
public class AdviceDemo {

    public static void main(String[] args) {
        methodInterceptor();
    }

    private static void methodInterceptor() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyMethodInterceptor());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doCustomMethodInterceptor();
    }
}
```

`MyMethodInterceptor` 是一个实现了 `MethodInterceptor` 接口的拦截器类。在 `invoke` 方法中，首先会执行在目标方法调用之前的逻辑，输出被调用的方法名称。然后调用 `invocation.proceed()` 方法执行原始方法，并获取其返回值。最后，在方法调用完成后执行的逻辑，输出方法返回值。

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

`MyService` 是一个简单的类，其中包含了一个名为 `doSomething` 的方法。该方法会输出一条消息 "Doing something..."，并返回整数值 1。这个类用于演示 Spring AOP 中的方法拦截器功能。

```java
public class MyService {

    public int doSomething() {
        System.out.println("Doing something...");
        return 1;
    }
}
```

运行结果，成功触发了方法拦截器，并在目标方法调用前后执行了拦截器中定义的逻辑。

```java
Method doSomething is called.
Doing something...
Method doSomething returns 1
```

#### BeforeAdvice

使用 Spring AOP 中的前置通知（Before Advice）。在 `beforeAdvice` 方法中，首先创建了一个 `ProxyFactory` 对象，并传入了一个目标对象 `MyService` 的实例。然后，通过 `addAdvice` 方法为代理工厂添加了一个前置通知 `MyBeforeAdvice`。接着，通过代理工厂的 `getProxy` 方法获取代理对象，并调用其方法 `doCustomBeforeAdvice()`。在方法执行时，前置通知会在目标方法调用之前执行自定义的逻辑。

```java
public class AdviceDemo {

    public static void main(String[] args) {
        beforeAdvice();
    }

    private static void beforeAdvice() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyBeforeAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doCustomBeforeAdvice();
    }
}
```

`MyBeforeAdvice` 是一个实现了`MethodBeforeAdvice` 接口的前置通知类。在 `before` 方法中，会在目标方法调用之前执行自定义的逻辑。

```java
public class MyBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before method " + method.getName() + " is called.");
    }
}
```

`MyService` 是一个简单的类，其中包含了一个名为 `doSomething` 的方法。该方法会输出一条消息 "Doing something..."，并返回整数值 1。这个类用于演示 Spring AOP 中的方法拦截器功能。

```java
public class MyService {

    public int doSomething() {
        System.out.println("Doing something...");
        return 1;
    }
}
```

运行结果，成功触发了前置通知，并在目标方法调用之前执行了前置通知中定义的逻辑。

```java
Before method doSomething is called.
Doing something...
```

#### AfterReturningAdvice

使用 Spring AOP 中的返回后通知（After Returning Advice）。在 `afterAdvice` 方法中，首先创建了一个 `ProxyFactory` 对象，并传入了一个目标对象 `MyService` 的实例。然后，通过 `addAdvice` 方法为代理工厂添加了一个返回后通知 `MyAfterReturningAdvice`。接着，通过代理工厂的 `getProxy` 方法获取代理对象，并调用其方法 `doSomething()`。在方法执行后，返回后通知会在目标方法正常返回时执行自定义的逻辑。

```java
public class AdviceDemo {

    public static void main(String[] args) {
        afterAdvice();
    }

    private static void afterAdvice() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 创建通知
        proxyFactory.addAdvice(new MyAfterReturningAdvice());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomething();
    }
}
```

`MyAfterReturningAdvice` 是一个实现了`AfterReturningAdvice`接口的后置通知类。在 `afterReturning` 方法中，会在目标方法正常返回后执行自定义的逻辑。

```java
public class MyAfterReturningAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("After method " + method.getName() + " is called, returned value: " + returnValue);
    }
}
```

`MyService` 是一个简单的类，其中包含了一个名为 `doSomething` 的方法。该方法会输出一条消息 "Doing something..."，并返回整数值 1。这个类用于演示 Spring AOP 中的方法拦截器功能。

```java
public class MyService {

    public int doSomething() {
        System.out.println("Doing something...");
        return 1;
    }
}
```

运行结果，成功触发了返回后通知，并在目标方法正常返回后执行了返回后通知中定义的逻辑。

```java
Doing something...
After method doSomething is called, returned value: 1
```

#### ThrowsAdvice

使用 Spring AOP 中的异常通知（Throws Advice）。在 `throwsAdvice` 方法中，首先创建了一个 `ProxyFactory` 对象，并传入了一个目标对象 `MyService` 的实例。然后，通过 `addAdvice` 方法为代理工厂添加了一个异常通知 `MyThrowsAdvice`。接着，通过代理工厂的 `getProxy` 方法获取代理对象，并调用其方法 `doSomethingException()`。在方法执行过程中，如果目标方法抛出了异常，则异常通知会被触发，执行自定义的异常处理逻辑。

```java
public class AdviceDemo {

    public static void main(String[] args) {
        throwsAdvice();
    }

    private static void throwsAdvice() {
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

`MyThrowsAdvice` 是一个实现了 `ThrowsAdvice`接口的异常通知类。在 `afterThrowing` 方法中，会在目标方法抛出异常后执行自定义的逻辑。

```java
public class MyThrowsAdvice implements ThrowsAdvice {
    public void afterThrowing(Exception ex) throws Throwable {
        System.out.println("Exception thrown: " + ex.getMessage());
    }
}
```

`MyService` 是一个简单的类，其中包含了一个名为 `doSomethingException` 的方法。该方法会输出一条消息 "Doing something exception..."，然后故意抛出一个异常，用于演示异常通知的使用。这个类用于演示 Spring AOP 中的异常通知功能。

```java
public class MyService {

    public void doSomethingException() {
        System.out.println("Doing something exception...");
        int i = 1 / 0;
    }
}
```

运行结果，在执行 `doSomethingException` 方法时，首先输出了消息 "Doing something exception..."，然后抛出了一个算术异常 "/ by zero"。异常通知 `MyThrowsAdvice` 被触发，并输出了异常信息 "Exception thrown: / by zero"。

```java
Doing something exception...
Exception thrown: / by zero
Exception in thread "main" java.lang.ArithmeticException: / by zero
	at com.xcs.spring.MyService.doSomethingException(MyService.java:12)
	at com.xcs.spring.MyService$$FastClassBySpringCGLIB$$c768e93b.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.adapter.ThrowsAdviceInterceptor.invoke(ThrowsAdviceInterceptor.java:113)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.xcs.spring.MyService$$EnhancerBySpringCGLIB$$c20ef0aa.doSomethingException(<generated>)
	at com.xcs.spring.AdviceDemo.throwsAdvice(AdviceDemo.java:57)
	at com.xcs.spring.AdviceDemo.main(AdviceDemo.java:12)
```

#### IntroductionInterceptor

使用 Spring AOP 中的引介通知（Introduction Advice）。在 `introductionAdvice` 方法中，首先创建了一个 `ProxyFactory` 对象，并传入了一个目标对象 `MyService` 的实例。然后，通过调用 `setProxyTargetClass(true)` 方法强制使用 CGLIB 代理。接着，通过 `addAdvisor` 方法为代理工厂添加了一个引介通知 `MyMonitoringIntroductionAdvice`，并指定引介接口为 `MyMonitoringCapable`。获取代理对象后，调用了 `doSomething` 方法，然后通过 `toggleMonitoring` 方法启用了监控。最后再次调用了 `doSomething` 方法。

```java
public class AdviceDemo {

    public static void main(String[] args) {
        introductionAdvice();
    }

    private static void introductionAdvice() {
        // 创建代理工厂&创建目标对象
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 强制私用CGLIB
        proxyFactory.setProxyTargetClass(true);
        // 创建通知
        proxyFactory.addAdvisor(new DefaultIntroductionAdvisor(new MyMonitoringIntroductionAdvice(), MyMonitoringCapable.class));
        // 创建代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用代理对象的方法
        proxy.doSomething();
        // 开始监控
        ((MyMonitoringCapable) proxy).toggleMonitoring();
        // 再次调用代理对象的方法
        proxy.doSomething();
    }
}
```

`MyMonitoringIntroductionAdvice` 类是一个实现了`IntroductionAdvice`引介通知接口。它继承了 Spring AOP 中的 `DelegatingIntroductionInterceptor` 类，并实现了自定义的接口 `MyMonitoringCapable`。这个类的作用是为目标对象引入监控功能，可以动态地开启和关闭监控。

```java
public class MyMonitoringIntroductionAdvice extends DelegatingIntroductionInterceptor implements MyMonitoringCapable {

    private boolean active = false;

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void toggleMonitoring() {
        setActive(true);
    }

    // 当被监控的方法被调用时，如果监控处于激活状态，则输出日志
    @Override
    protected Object doProceed(MethodInvocation mi) throws Throwable {
        if (this.active) {
            System.out.println("开启监控...");
            long startTime = System.currentTimeMillis();
            Object result = super.doProceed(mi);
            long endTime = System.currentTimeMillis();
            System.out.println(mi.getClass().getName() + "." + mi.getMethod().getName() + " 耗费时间：" + (endTime - startTime) + " 毫秒");
            System.out.println("结束监控...");
            return result;
        }
        return super.doProceed(mi);
    }
}
```

`MyMonitoringCapable` 接口定义了一个 `toggleMonitoring` 方法，用于控制监控功能的开启和关闭。这个接口可以被引入到目标对象中，使得目标对象具备了监控能力，可以根据需要动态地开启和关闭监控。

```java
public interface MyMonitoringCapable {
    void toggleMonitoring();
}
```

`MyService` 是一个简单的类，其中包含了一个名为 `doSomething` 的方法。该方法会输出一条消息 "Doing something..."，并返回整数值 1。这个类用于演示 Spring AOP 中的方法拦截器功能。

```java
public class MyService {

    public int doSomething() {
        System.out.println("Doing something...");
        return 1;
    }
}
```

运行结果，首先调用了 `doSomething` 方法，然后开启了监控功能。接着再次调用了 `doSomething` 方法，监控记录了方法执行的耗时，并输出了监控结束的日志。

```java
Doing something...
开启监控...
Doing something...
org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.doSomething 耗费时间：0 毫秒
结束监控...
```

### 八、与其他组件的关系

1. **与具体通知类型的关系**

   +  `Advice` 接口是所有具体通知类型的父接口，比如前置通知、后置通知、环绕通知等都是 `Advice` 接口的子类或实现类。

2. **与切面的关系** 

   + 在 Spring AOP 中，通知（Advice）是切面的组成部分之一。切面由切点（Pointcut）和通知（Advice）组成，其中切点定义了在目标方法的哪些位置执行通知，而通知则定义了在这些位置执行的具体逻辑。

3. **与代理对象的关系** 

   + 在 AOP 中，通知被织入到目标对象的方法执行过程中，生成一个代理对象来实现横切关注点的功能。通知决定了在方法执行前、执行后或抛出异常时要执行的逻辑，而代理对象则负责管理这些通知的执行顺序和条件。

4. **与 AOP 框架的关系** 

   + `Advice` 接口是 AOP 联盟定义的标准接口，被 Spring AOP 等 AOP 框架采用和实现。在 Spring AOP 中，各种具体的通知类型都是基于 `Advice` 接口来实现的，因此 `Advice` 接口在 Spring AOP 中具有重要的地位和作用。

### 九、常见问题

1. **选择合适的通知类型** 

   + Advice 接口是一个标记接口，它定义了各种通知类型的基础。在实现具体的通知时，需要选择适合场景的通知类型，如前置通知、后置通知、环绕通知等。

2. **确保通知逻辑简洁清晰** 

   + 实现通知时，通知的逻辑应该保持简洁清晰，只包含与切面关注点相关的逻辑。避免在通知中引入复杂的业务逻辑或者与切面无关的代码。

3. **异常处理** 

   + 在通知中可能会涉及异常处理逻辑，确保通知能够正确处理可能出现的异常情况，并根据实际需求进行适当的处理或者传播。

4. **性能考虑** 

   + 通知会对应用程序的性能产生一定的影响，特别是在大规模应用中。在编写通知时，需要注意避免影响系统性能的操作，尽量保持通知的执行效率。

5. **通知与目标方法的参数和返回值** 

   + 在实现通知时，可能需要访问目标方法的参数和返回值。了解如何在通知中获取和处理这些参数和返回值是很重要的。

6. **与其他切面的协作** 

   + 在实际应用中，可能会存在多个切面，需要确保各个切面之间的协作和执行顺序是符合预期的。