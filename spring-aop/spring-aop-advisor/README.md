## Advisor

- [Advisor](#advisor)
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

`Advisor`接口是Spring框架中的一个关键接口，用于将切点（Pointcut）和通知（Advice）组合起来，以便在AOP（面向切面编程）中定义何时、何地以及如何应用横切关注点。

### 三、主要功能

1. **组合切点和通知**

   + Advisor接口允许将切点（Pointcut）和通知（Advice）组合在一起。切点确定何时应该应用通知，而通知定义了在连接点处执行的代码。

2. **定义横切逻辑**

   + 通过Advisor接口及其实现类，可以定义在应用程序中何时、何地以及如何应用横切关注点（cross-cutting concerns），如日志记录、事务管理、安全性等。

3. **提供AOP配置的抽象**

   + Advisor接口是AOP配置的一种抽象，它使得可以通过编程方式或声明式的方式定义切面，并将它们应用到目标对象上。

4. **实现面向切面编程**

   + Advisor接口是面向切面编程（AOP）的核心组件之一，它允许我们在不改变业务逻辑的情况下，通过切面来管理横切关注点，从而提高代码的模块化和可维护性。

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

### 五、主要实现

1. **RegexpMethodPointcutAdvisor**

   - 基于正则表达式来匹配方法名的切点。通过使用正则表达式，可以根据方法名模式匹配连接点，并将通知应用于匹配的连接点，从而实现基于方法名模式的切面逻辑。

2. **AspectJExpressionPointcutAdvisor**

   - 基于AspectJ表达式来定义切点。通过使用AspectJ的语法，可以更灵活地定义切面，从而匹配连接点，并将通知应用于匹配的连接点，实现更复杂的切面逻辑。

3. **NameMatchMethodPointcutAdvisor**

   - 基于方法名模式匹配来定义切点。通过使用方法名模式，可以轻松地匹配连接点，并将通知应用于匹配的连接点，从而实现基于方法名模式的切面逻辑。

4. **DefaultPointcutAdvisor**

   - 一个通用的切点Advisor，用于将切点和通知组合在一起。它允许将任何类型的通知与任何类型的切点结合使用，并将通知应用于匹配的连接点，从而实现横切关注点的管理。

5. **DefaultIntroductionAdvisor**

   + 是Spring AOP中的一个特殊类型的Advisor实现，用于引入新的接口（或Mixin）到目标类中。它允许将新的接口实现引入到现有的目标类中，以扩展目标类的功能。通过`DefaultIntroductionAdvisor`，可以在不修改现有类的情况下，向其添加新的行为或功能，从而实现更好的代码复用和扩展性。

### 六、最佳实践

使用Advisor来创建代理对象并应用切面逻辑。首先，我们创建了一个代理工厂`ProxyFactory`，并将目标对象`MyService`传递给它。然后，我们通过`proxyFactory.addAdvisor(new MyCustomAdvisor())`添加了一个自定义的Advisor，该Advisor定义了切点和通知。接着，我们通过`proxyFactory.getProxy()`获取了代理对象`MyService`。最后，我们调用了代理对象的方法`proxy.foo()`和`proxy.bar()`。

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
        proxy.foo(); // 会触发通知
        proxy.bar(); // 不会触发通知
    }
}
```

`MyCustomAdvisor`类是一个自定义的Advisor，它继承自`AbstractPointcutAdvisor`，用于将通知应用于带有特定注解的方法。在该类中，我们定义了一个通知对象`advice`和一个切点对象`pointcut`，切点用于匹配带有自定义注解`MyCustomAnnotation`的方法。通过实现`getPointcut()`方法和`getAdvice()`方法，我们指定了切点和通知的逻辑。这样，在应用该Advisor时，它将会根据定义的切点匹配带有指定注解的方法，并在匹配的方法执行前后应用通知。

```java
/**
 * 自定义Advisor，用于将通知应用于带有特定注解的方法。
 */
public class MyCustomAdvisor extends AbstractPointcutAdvisor {

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
}
```

`MyAdvice`类是一个通知类，实现了`MethodBeforeAdvice`接口，用于在目标方法执行前执行一些操作。

```java
public class MyAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before method: " + method.getName());
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

`MyService`类包含了两个方法`foo()`和`bar()`。其中，`foo()`方法被标记了`@MyCustomAnnotation`注解，而`bar()`方法没有。当调用`foo()`方法时，将会执行特定的逻辑，而调用`bar()`方法则不会执行特定逻辑。这种注解标记方式为方法提供了特殊的标记，使得在应用切面时可以针对带有特定注解的方法执行特定的通知。

```java
public class MyService {

    @MyCustomAnnotation
    public void foo() {
        System.out.println("Executing foo method");
    }

    public void bar() {
        System.out.println("Executing bar method");
    }
}
```

运行结果，切面逻辑成功应用于带有特定注解的方法，并且未被标记的方法不受影响。

```java
Before method: foo
Executing foo method
Executing bar method
```

### 七、源码分析

暂无

### 八、常见问题

1. **切点定义错误** 

   + 定义切点时可能会出现错误，导致切面未能正确地匹配到预期的连接点。这可能是因为切点表达式错误，或者切点逻辑与实际场景不匹配。

2. **通知应用顺序错误**

   + 当存在多个Advisor时，通知的应用顺序可能会影响最终的切面逻辑。如果通知的应用顺序不正确，可能会导致意外的行为或不一致的结果。

3. **切面匹配范围不正确**

   +  切面的匹配范围可能过于宽泛或过于狭窄，导致通知被应用于不希望被影响的连接点，或者未能影响到应该被影响的连接点。

4. **性能问题** 

   + 使用过多或过于复杂的切面可能会影响应用程序的性能。因此，在设计切面时需要考虑性能问题，并尽量减少切面的数量和复杂度。

5. **代理问题**

   +  如果代理配置不正确，可能会导致切面无法正确应用。例如，如果代理对象的类型与切面匹配范围不一致，或者代理对象不符合切面的预期行为，都可能导致切面无法正确工作。

6. **异常处理问题** 

   + 切面中的异常处理可能会影响应用程序的正常运行。如果异常处理不正确，可能会导致应用程序无法正确处理异常情况，从而影响用户体验。

7. **与其他切面冲突** 

   + 如果存在多个切面，并且它们的匹配范围有重叠，可能会导致切面之间的冲突。例如，两个切面都匹配同一个连接点，并且它们的通知行为相互矛盾，可能会导致意料之外的结果。