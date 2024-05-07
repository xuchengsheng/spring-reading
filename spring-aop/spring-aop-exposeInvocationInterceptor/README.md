## ExposeInvocationInterceptor

- [ExposeInvocationInterceptor](#exposeinvocationinterceptor)
	- [一、基本信息](#一基本信息)
	- [二、基本描述](#二基本描述)
	- [三、主要功能](#三主要功能)
	- [四、类源码](#四类源码)
	- [五、最佳实践](#五最佳实践)
    - [六、时序图](#六时序图)
    - [七、源码分析](#七源码分析)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`ExposeInvocationInterceptor`是Spring AOP中的一个拦截器类，主要功能是在AOP调用链中暴露当前方法调用的上下文信息，通过暴露`MethodInvocation`对象，使其他拦截器或切面能够访问并处理方法调用的相关信息。

### 三、主要功能

1. **暴露当前方法调用的上下文信息** 

   + 通过暴露`MethodInvocation`对象，允许其他拦截器或切面访问当前方法调用的相关信息，如目标对象、方法、参数等。

2. **提供`currentInvocation()`方法** 

   + 允许在拦截器或切面中调用`currentInvocation()`方法来获取当前方法调用的`MethodInvocation`对象，从而获取方法调用的上下文信息。

3. **支持AOP调用链的处理** 

   + 作为Spring AOP的一个拦截器，`ExposeInvocationInterceptor`能够被添加到AOP代理链中，确保在调用链的初始阶段就将`MethodInvocation`对象暴露出来，以便后续的拦截器或切面可以使用。

### 四、类源码

 `ExposeInvocationInterceptor`拦截器，其主要目的是将当前的方法调用上下文暴露为线程本地对象。它允许在Spring AOP中获取方法调用的详细信息，例如目标对象、方法、参数等。这个拦截器在AOP链中通常是第一个，用于确保其他拦截器或切面能够访问方法调用的完整上下文。

```java
/**
 * 拦截器，将当前{@link org.aopalliance.intercept.MethodInvocation}暴露为线程本地对象。
 * 仅在必要时使用此拦截器；例如，当切点（例如，AspectJ表达式切点）需要知道完整的调用上下文时。
 *
 * <p>除非绝对必要，否则不要使用此拦截器。目标对象通常不应知道Spring AOP，
 * 因为这会创建对Spring API的依赖。目标对象应尽可能是纯POJO。
 *
 * <p>如果使用，此拦截器通常将是拦截器链中的第一个。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public final class ExposeInvocationInterceptor implements MethodInterceptor, PriorityOrdered, Serializable {

	/** 此类的单例实例。 */
	public static final ExposeInvocationInterceptor INSTANCE = new ExposeInvocationInterceptor();

	/**
	 * 此类的单例顾问。在使用Spring AOP时，请使用它，因为它可以避免创建新的Advisor来包装该实例。
	 */
	public static final Advisor ADVISOR = new DefaultPointcutAdvisor(INSTANCE) {
		@Override
		public String toString() {
			return ExposeInvocationInterceptor.class.getName() +".ADVISOR";
		}
	};

	private static final ThreadLocal<MethodInvocation> invocation =
			new NamedThreadLocal<>("Current AOP method invocation");


	/**
	 * 返回与当前调用关联的AOP Alliance MethodInvocation对象。
	 * @return 与当前调用关联的调用对象
	 * @throws IllegalStateException 如果当前没有AOP调用，
	 * 或者ExposeInvocationInterceptor未添加到此拦截器链中
	 */
	public static MethodInvocation currentInvocation() throws IllegalStateException {
		MethodInvocation mi = invocation.get();
		if (mi == null) {
			throw new IllegalStateException(
					"No MethodInvocation found: Check that an AOP invocation is in progress and that the " +
					"ExposeInvocationInterceptor is upfront in the interceptor chain. Specifically, note that " +
					"advices with order HIGHEST_PRECEDENCE will execute before ExposeInvocationInterceptor! " +
					"In addition, ExposeInvocationInterceptor and ExposeInvocationInterceptor.currentInvocation() " +
					"must be invoked from the same thread.");
		}
		return mi;
	}


	/**
	 * 确保只能创建规范实例。
	 */
	private ExposeInvocationInterceptor() {
	}

	@Override
	@Nullable
	public Object invoke(MethodInvocation mi) throws Throwable {
		MethodInvocation oldInvocation = invocation.get();
		invocation.set(mi);
		try {
			return mi.proceed();
		}
		finally {
			invocation.set(oldInvocation);
		}
	}

	@Override
	public int getOrder() {
		return PriorityOrdered.HIGHEST_PRECEDENCE + 1;
	}

	/**
	 * Required to support serialization. Replaces with canonical instance
	 * on deserialization, protecting Singleton pattern.
	 * <p>Alternative to overriding the {@code equals} method.
	 */
	private Object readResolve() {
		return INSTANCE;
	}

}
```

### 五、最佳实践

创建了一个基于注解的应用程序上下文，从中获取了一个名为 `MyService` 的 bean，并调用了其 `foo()` 方法。

```java
public class ExposeInvocationInterceptorDemo {

    public static void main(String[] args) {
        // 创建一个基于注解的应用程序上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从上下文中获取 MyService
        MyService myService = context.getBean(MyService.class);
        // 调用方法
        myService.foo();
    }
}
```

使用了 `@EnableAspectJAutoProxy` 注解启用了 AspectJ 自动代理功能，并且通过 `@ComponentScan` 注解扫描了包 `com.xcs.spring` 下的组件。

```java
@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.xcs.spring")
public class AppConfig {

}
```

一个服务类，用于业务逻辑的实现。

```java
@Service
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

`MyMethodInterceptor`标记为 `@Aspect` 和 `@Component`，表明它是一个切面，并且由 Spring 容器进行管理。其中包含一个名为 `before()` 的方法，使用 `@Before` 注解标记，表示在目标方法执行之前执行。方法内部调用了 `LogUtil.print()` 方法，用于记录日志或执行其他操作。这个切面主要是针对 `com.xcs.spring.MyService` 类中所有公共方法的执行，在方法执行之前添加了特定的逻辑。

```java
@Aspect
@Component
public class MyMethodInterceptor {

    @Before("execution(public * com.xcs.spring.MyService.*(..))")
    public void before() {
        LogUtil.print();
    }
}
```

通过 `ExposeInvocationInterceptor.currentInvocation()` 获取当前方法调用的 `ProxyMethodInvocation` 对象，然后打印了方法名称、参数长度、目标对象以及代理对象的类名。

```java
public class LogUtil {

    public static void print() {
        ProxyMethodInvocation methodInvocation = (ProxyMethodInvocation) ExposeInvocationInterceptor.currentInvocation();
        System.out.println("Method = " + methodInvocation.getMethod());
        System.out.println("Arguments Length = " + methodInvocation.getArguments().length);
        System.out.println("Target = " + methodInvocation.getThis());
        System.out.println("Proxy Class = " + methodInvocation.getProxy().getClass());
    }
}
```

运行结果，通过`ExposeInvocationInterceptor.currentInvocation()`获取方法调用上下文实现日志打印。

```java
Method = public void com.xcs.spring.MyService.doSomething()
Arguments Length = 0
Target = com.xcs.spring.MyService@49964d75
Proxy Class = class com.xcs.spring.MyService$$EnhancerBySpringCGLIB$$f30643a6
Doing something...
```

### 六、时序图

~~~mermaid
sequenceDiagram
    AbstractAutowireCapableBeanFactory->>AbstractAutoProxyCreator: postProcessAfterInitialization()
    Note over AbstractAutowireCapableBeanFactory,AbstractAutoProxyCreator: 调用后处理方法
    AbstractAutoProxyCreator->>AbstractAutoProxyCreator: wrapIfNecessary()
    Note over AbstractAutoProxyCreator: 调用包装方法
    AbstractAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: getAdvicesAndAdvisorsForBean()
    Note over AbstractAutoProxyCreator,AbstractAdvisorAutoProxyCreator: 获取通知和 Advisors
    AbstractAdvisorAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: findEligibleAdvisors()
    Note over AbstractAdvisorAutoProxyCreator: 查找合适的 Advisors
    AbstractAdvisorAutoProxyCreator->>AspectJAwareAdvisorAutoProxyCreator: extendAdvisors()
    Note over AbstractAdvisorAutoProxyCreator,AspectJAwareAdvisorAutoProxyCreator: Advisor 的扩展钩子
    AspectJAwareAdvisorAutoProxyCreator->>AspectJProxyUtils:makeAdvisorChainAspectJCapableIfNecessary()
    Note over AspectJAwareAdvisorAutoProxyCreator,AspectJProxyUtils: 添加特殊的拦截器
~~~

### 七、源码分析

在`org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator#extendAdvisors`方法中，在开头添加了一个 `ExposeInvocationInterceptor`。

```java
/**
 * 将{@link ExposeInvocationInterceptor}添加到advice链的开头。
 * <p>在使用AspectJ切点表达式和AspectJ风格的advice时，需要此额外的Advisors。
 */
@Override
protected void extendAdvisors(List<Advisor> candidateAdvisors) {
    AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(candidateAdvisors);
}
```

在`org.springframework.aop.aspectj.AspectJProxyUtils#makeAdvisorChainAspectJCapableIfNecessary`
方法中，用于在代理链中添加特殊的拦截器，以确保与包含AspectJ建议的代理链一起正常工作。具体来说，它将 `ExposeInvocationInterceptor`
添加到advisors列表的开头。这样做的目的是为了暴露当前Spring AOP调用（对于某些AspectJ切点匹配是必要的），并使当前AspectJ
JoinPoint可用。如果advisors链中不存在AspectJ advisor，则此调用不会产生任何效果。方法返回 `true`
表示成功向建议列表中添加了 `ExposeInvocationInterceptor`，否则返回 `false`。

```java
/**
 * 如果需要，向包含AspectJ建议的代理链中添加特殊的建议：
 * 具体来说，在列表的开头添加{@link ExposeInvocationInterceptor}。
 * <p>这将暴露当前Spring AOP调用（对于某些AspectJ切点匹配是必要的），
 * 并使当前AspectJ JoinPoint可用。如果建议链中没有AspectJ建议，则调用不会产生任何效果。
 * @param advisors 可用的建议列表
 * @return 如果向列表中添加了{@link ExposeInvocationInterceptor}，则返回{@code true}，否则返回{@code false}
 */
public static boolean makeAdvisorChainAspectJCapableIfNecessary(List<Advisor> advisors) {
    // 不要向空列表添加建议；这可能表示不需要代理
    if (!advisors.isEmpty()) {
        boolean foundAspectJAdvice = false;
        for (Advisor advisor : advisors) {
            // 谨慎使用不带保护的Advice，因为这可能会急切地实例化非单例的AspectJ切面...
            if (isAspectJAdvice(advisor)) {
                foundAspectJAdvice = true;
                break;
            }
        }
        // 如果在建议链中找到AspectJ建议，并且没有ExposeInvocationInterceptor.ADVISOR，则添加
        if (foundAspectJAdvice && !advisors.contains(ExposeInvocationInterceptor.ADVISOR)) {
            advisors.add(0, ExposeInvocationInterceptor.ADVISOR);
            return true;
        }
    }
    return false;
}
```

在`org.springframework.aop.aspectj.AspectJProxyUtils#isAspectJAdvice`方法中，判断给定的 Advisor 是否包含 AspectJ Advice。它检查 Advisor 实例是否属于特定类型或者其 Advice 是否是 AbstractAspectJAdvice 的子类，或者其 Pointcut 是否是 AspectJExpressionPointcut 的实例。

```java
/**
 * 判断给定的 Advisor 是否包含 AspectJ Advice。
 * @param advisor 要检查的 Advisor
 */
private static boolean isAspectJAdvice(Advisor advisor) {
    return (advisor instanceof InstantiationModelAwarePointcutAdvisor ||
          advisor.getAdvice() instanceof AbstractAspectJAdvice ||
          (advisor instanceof PointcutAdvisor &&
                ((PointcutAdvisor) advisor).getPointcut() instanceof AspectJExpressionPointcut));
}
```
