## AdvisorChainFactory

- [AdvisorChainFactory](#advisorchainfactory)
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

`AdvisorChainFactory`接口是Spring AOP中负责创建顾问链的工厂接口，通过`getInterceptorsAndDynamicInterceptionAdvice()`方法，它能够将一组顾问对象转换为拦截器数组，用于管理和执行切面逻辑，提供了灵活性和可扩展性来定制切面的执行方式。

### 三、主要功能

1. **创建顾问链（Advisor Chain）** 

   + 通过`getInterceptorsAndDynamicInterceptionAdvice()`方法，将一组顾问对象转换为拦截器数组，形成顾问链，用于在目标方法执行前后执行特定的操作。

2. **动态顾问链的创建** 

   + 可以根据运行时的情况动态地创建顾问链，例如根据目标对象的类型或方法签名动态地决定哪些通知要被执行。

### 四、接口源码

`AdvisorChainFactory`接口 ，用于创建Advisor链的工厂接口。其中的方法 `getInterceptorsAndDynamicInterceptionAdvice()` 接受AOP配置（`Advised`对象）、被代理的方法以及目标类，并返回一个包含MethodInterceptors的列表，用于配置Advisor链。这个接口的目的是根据给定的配置，确定在代理方法执行时应该应用哪些拦截器，以及是否需要动态匹配方法。

```java
/**
 * Advisor链工厂的工厂接口。
 * Factory interface for advisor chains.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AdvisorChainFactory {

	/**
	 * 根据给定的Advisor链配置，确定一组MethodInterceptor对象。
	 * Determine a list of {@link org.aopalliance.intercept.MethodInterceptor} objects
	 * for the given advisor chain configuration.
	 * @param config 表示AOP配置的Advised对象
	 * @param method 被代理的方法
	 * @param targetClass 目标类（可能为null，表示没有目标对象的代理，在这种情况下，方法的声明类是下一个最佳选择）
	 * @return 一个MethodInterceptors的列表（也可能包括InterceptorAndDynamicMethodMatchers）
	 */
	List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, @Nullable Class<?> targetClass);

}
```

### 五、主要实现

1. **DefaultAdvisorChainFactory**

   + 负责根据给定的AOP配置、被代理的方法和目标类，确定应该应用哪些拦截器，并支持动态方法匹配和缓存机制，以提供高效的顾问链创建功能

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AdvisorChainFactory {
<<Interface>>

}
class DefaultAdvisorChainFactory

DefaultAdvisorChainFactory  ..>  AdvisorChainFactory 
~~~

### 七、最佳实践

使用`DefaultAdvisorChainFactory`类来创建Advisor链。首先，创建了一个`AdvisedSupport`对象，配置了前置通知和后置返回通知。然后，指定了目标类和目标方法。接着，实例化了`DefaultAdvisorChainFactory`类，并调用其`getInterceptorsAndDynamicInterceptionAdvice()`方法获取Advisor链。最后，打印了Advisor链中的拦截器。

```java
public class AdvisorChainFactoryDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // 创建AOP配置对象
        AdvisedSupport config = new AdvisedSupport();
        // 添加前置通知
        config.addAdvice(new MyMethodBeforeAdvice());
        // 添加后置返回通知
        config.addAdvice(new MyAfterReturningAdvice());
        // 设置目标类
        Class<MyService> targetClass = MyService.class;
        // 获取目标方法
        Method method = targetClass.getDeclaredMethod("foo");

        // 创建默认的Advisor链工厂实例
        DefaultAdvisorChainFactory chainFactory = new DefaultAdvisorChainFactory();
        // 获取Advisor链
        List<Object> chain = chainFactory.getInterceptorsAndDynamicInterceptionAdvice(config, method, targetClass);
        // 打印Advisor链中的拦截器
        chain.forEach(System.out::println);
    }
}
```

运行结果，显示了Advisor链中的两个拦截器，分别是`MethodBeforeAdviceInterceptor`和`AfterReturningAdviceInterceptor`。这些拦截器是根据配置的前置通知和后置返回通知生成的，用于在目标方法执行前后进行相应的操作。

```java
org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor@215be6bb
org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor@4439f31e
```

### 八、源码分析

`DefaultAdvisorChainFactory`类。它提供了一种简单但确定的方法，根据给定的`Advised`对象，在方法级别确定通知链的构建顺序。通过遍历配置的Advisor数组，并根据Advisor的类型和Pointcut来确定应该应用哪些拦截器，最终返回一个拦截器列表。在此过程中，它支持动态方法匹配和引入拦截器的处理，并提供了一个缓存机制来提高性能。

[AdvisorAdapterRegistry源码分析](../spring-aop-advisorAdapterRegistry/README.md)

```java
/**
 * 给定一个 {@link Advised} 对象，为一个方法确定一个通知链的简单但确定的方法。总是重新构建每个通知链；
 * 子类可以提供缓存功能。
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @author Adrian Colyer
 * @since 2.0.3
 */
@SuppressWarnings("serial")
public class DefaultAdvisorChainFactory implements AdvisorChainFactory, Serializable {

	@Override
	public List<Object> getInterceptorsAndDynamicInterceptionAdvice(
			Advised config, Method method, @Nullable Class<?> targetClass) {

		// 获取Advisor适配器注册表
		AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
		// 获取AOP配置中的所有Advisor
		Advisor[] advisors = config.getAdvisors();
		// 创建一个拦截器列表
		List<Object> interceptorList = new ArrayList<>(advisors.length);
		// 获取实际类
		Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
		Boolean hasIntroductions = null;

		// 遍历所有Advisor
		for (Advisor advisor : advisors) {
			if (advisor instanceof PointcutAdvisor) {
				// 添加条件性地。
				PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
				if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
					// 获取Advisor的Pointcut和MethodMatcher
					MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
					boolean match;
					if (mm instanceof IntroductionAwareMethodMatcher) {
						if (hasIntroductions == null) {
							// 检查是否存在匹配的IntroductionAdvisor
							hasIntroductions = hasMatchingIntroductions(advisors, actualClass);
						}
						match = ((IntroductionAwareMethodMatcher) mm).matches(method, actualClass, hasIntroductions);
					}
					else {
						match = mm.matches(method, actualClass);
					}
					// 如果匹配，则将Interceptor添加到拦截器列表中
					if (match) {
						MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
						if (mm.isRuntime()) {
							// 如果是动态匹配，则创建一个新的InterceptorAndDynamicMethodMatcher对象
							for (MethodInterceptor interceptor : interceptors) {
								interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
							}
						}
						else {
							// 否则直接添加Interceptor
							interceptorList.addAll(Arrays.asList(interceptors));
						}
					}
				}
			}
			else if (advisor instanceof IntroductionAdvisor) {
				IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
				if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
					// 如果是IntroductionAdvisor，则直接获取Interceptor并添加到拦截器列表中
					Interceptor[] interceptors = registry.getInterceptors(advisor);
					interceptorList.addAll(Arrays.asList(interceptors));
				}
			}
			else {
				// 对于其他类型的Advisor，直接获取Interceptor并添加到拦截器列表中
				Interceptor[] interceptors = registry.getInterceptors(advisor);
				interceptorList.addAll(Arrays.asList(interceptors));
			}
		}

		// 返回拦截器列表
		return interceptorList;
	}

	/**
	 * 判断Advisor中是否存在匹配的引入拦截器。
	 */
	private static boolean hasMatchingIntroductions(Advisor[] advisors, Class<?> actualClass) {
		for (Advisor advisor : advisors) {
			if (advisor instanceof IntroductionAdvisor) {
				IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
				if (ia.getClassFilter().matches(actualClass)) {
					return true;
				}
			}
		}
		return false;
	}

}
```
