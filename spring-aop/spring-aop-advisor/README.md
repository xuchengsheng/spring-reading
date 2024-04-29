## Advisor

- [Advisor](#advisor)
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

`Advisor`接口是Spring框架中的一个关键接口，用于将切点（Pointcut）和通知（Advice）组合起来，以便在AOP（面向切面编程）中定义何时、何地以及如何应用横切关注点。

### 三、主要功能

1. **组合切点和通知**

   + Advisor接口允许将切点（Pointcut）和通知（Advice）组合在一起。切点确定何时应该应用通知，而通知定义了在连接点处执行的代码。


### 四、接口源码

`Advisor`接口是Spring框架中的一个基础接口，用于持有AOP通知（在连接点执行的操作）和确定通知适用性的过滤器（例如切点）。该接口定义了获取通知部分的方法`getAdvice()`，以及确定通知是否与特定实例相关联的方法`isPerInstance()`。同时，该接口还提供了一个常量`EMPTY_ADVICE`，用作当未配置适当通知时的占位符。在Spring AOP中，Advisor接口允许支持不同类型的通知，例如拦截器、前置通知、异常通知等，并且并非所有通知都需要使用拦截来实现。

```java
/**
 * 基础接口，持有AOP <b>通知</b>（在连接点执行的操作）和确定通知适用性的过滤器（例如切点）。
 * <i>此接口不供Spring用户使用，而是为了在支持不同类型的通知时提供共性。</i>
 *
 * <p>Spring AOP基于通过方法拦截（interception）提供的<b>环绕通知</b>，符合AOP Alliance拦截API。
 * Advisor接口允许支持不同类型的通知，例如<b>前置</b>和<b>后置</b>通知，它们不一定要使用拦截来实现。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface Advisor {

	/**
	 * 如果尚未配置适当的通知，则从{@link #getAdvice()}返回一个空的{@code Advice}的常用占位符。
	 * @since 5.0
	 */
	Advice EMPTY_ADVICE = new Advice() {};


	/**
	 * 返回此方面的通知部分。通知可以是拦截器、前置通知、异常通知等。
	 * @return 如果切点匹配，则应应用的通知
	 * @see org.aopalliance.intercept.MethodInterceptor
	 * @see BeforeAdvice
	 * @see ThrowsAdvice
	 * @see AfterReturningAdvice
	 */
	Advice getAdvice();

	/**
	 * 返回此通知是否与特定实例相关联（例如，创建混入），或与从同一Spring bean工厂获取的被通知类的所有实例共享。
	 * <p><b>请注意，框架当前不使用此方法。</b>
	 * 典型的Advisor实现总是返回{@code true}。
	 * 使用singleton/prototype bean定义或适当的编程代理创建来确保Advisor具有正确的生命周期模型。
	 * @return 此通知是否与特定目标实例关联
	 */
	boolean isPerInstance();

}
```

`PointcutAdvisor`接口是所有由切点驱动的Advisor的超级接口。它覆盖了几乎所有的Advisor，但不包括引介Advisor，因为引介Advisor不适用于方法级别的匹配。该接口表示由切点驱动的Advisor，通过`getPointcut()`方法获取驱动该Advisor的切点。 PointcutAdvisor通常用于基于切点的切面，通过指定切点来确定通知逻辑应该应用于哪些连接点。

```java
/**
 * 所有由切点驱动的Advisor的超级接口。
 * 这几乎涵盖了所有的Advisor，除了引介Advisor，
 * 因为方法级别的匹配不适用于引介Advisor。
 *
 * 该接口是Advisor的子接口，用于表示由切点驱动的Advisor。
 * 切点驱动的Advisor通常用于基于切点的切面，通过指定切点来确定通知逻辑应该应用于哪些连接点。
 * 
 * 作者：Rod Johnson
 */
public interface PointcutAdvisor extends Advisor {

	/**
	 * 获取驱动该Advisor的切点。
	 */
	Pointcut getPointcut();

}
```

### 五、主要实现

1. **RegexpMethodPointcutAdvisor**

   - 基于正则表达式来匹配方法名的切点。通过使用正则表达式，可以根据方法名模式匹配连接点，并将通知应用于匹配的连接点，从而实现基于方法名模式的切面逻辑。
2. **AspectJExpressionPointcutAdvisor**

   - 基于AspectJ表达式来定义切点。通过使用AspectJ的语法，可以更灵活地定义切面，从而匹配连接点，并将通知应用于匹配的连接点，实现更复杂的切面逻辑。
3. **NameMatchMethodPointcutAdvisor**

   - 基于方法名模式匹配来定义切点。通过使用方法名模式，可以轻松地匹配连接点，并将通知应用于匹配的连接点，从而实现基于方法名模式的切面逻辑。
4. **DefaultPointcutAdvisor**

   - 一个通用的切点Advisor，用于将切点和通知组合在一起。它允许将任何类型的通知与任何类型的切点结合使用，并将通知应用于匹配的连接点，从而实现横切关注点的管理。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class Advisor {
<<Interface>>

}
class AspectJPointcutAdvisor
class DefaultPointcutAdvisor
class NameMatchMethodPointcutAdvisor
class PointcutAdvisor {
<<Interface>>

}
class RegexpMethodPointcutAdvisor

AspectJPointcutAdvisor  ..>  PointcutAdvisor 
DefaultPointcutAdvisor  ..>  PointcutAdvisor 
NameMatchMethodPointcutAdvisor  ..>  PointcutAdvisor 
PointcutAdvisor  -->  Advisor 
RegexpMethodPointcutAdvisor  ..>  PointcutAdvisor 

~~~

### 七、最佳实践

使用Advisor来创建代理对象并应用切面逻辑。首先，通过创建代理工厂`ProxyFactory`，并将目标对象`MyService`传递给它。然后，通过`proxyFactory.addAdvisor(new MyCustomAdvisor())`添加了一个自定义的Advisor，其中包含了切点和通知的定义。接着，通过`proxyFactory.getProxy()`获取了代理对象`MyService`。最后，调用了代理对象的`foo()`方法，该方法触发了切面逻辑的执行。

```java
public class AdvisorDemo {

    public static void main(String[] args) {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyService());
        // 添加Advisor
        proxyFactory.addAdvisor(new MyCustomAdvisor());
        // 获取代理对象
        MyService proxy = (MyService) proxyFactory.getProxy();
        // 调用方法
        proxy.foo();
    }
}
```

`MyCustomAdvisor`类是一个自定义的Advisor，它实现了`PointcutAdvisor`接口，并用于将通知应用于带有特定注解的方法。在该类中，我们定义了一个通知对象`advice`和一个切点对象`pointcut`，切点用于匹配带有自定义注解`MyCustomAnnotation`的方法。通过实现`getPointcut()`方法和`getAdvice()`方法，我们指定了切点和通知的逻辑。

```java

/**
 * 自定义Advisor，用于将通知应用于带有特定注解的方法。
 */
public class MyCustomAdvisor implements PointcutAdvisor {

    /**
     * 通知对象
     */
    private final Advice advice = new MyAdvice();

    /**
     * 切点对象，用于匹配带有自定义注解的方法
     */
    private final Pointcut pointcut = new AnnotationMatchingPointcut(null, MyCustomAnnotation.class);

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }
}
```

`MyAdvice`类是一个通知类，它实现了`MethodInterceptor`接口，用于在方法执行前后添加额外的逻辑。在`invoke`方法中，我们首先输出了正在调用的方法名，然后调用了`invocation.proceed()`方法来执行原始方法，并获取了方法执行的结果。最后，我们在方法执行之后再次输出了方法名。

```java
public class MyAdvice implements MethodInterceptor {

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

`MyCustomAnnotation`是一个自定义的注解，通常用于标记需要特殊处理的方法。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyCustomAnnotation {
}
```

`MyCustomAnnotation`类其中包含一个名为`foo`的方法。该方法被`@MyCustomAnnotation`注解标记，表明需要特殊处理。

```java
public class MyService {

    @MyCustomAnnotation
    public void foo() {
        System.out.println("foo...");
    }
}
```

运行结果，切面逻辑成功应用于带有特定注解的方法。

```java
Before Method foo
foo...
After Method foo
```
