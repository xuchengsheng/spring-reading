## AdvisorAdapterRegistry

- [AdvisorAdapterRegistry](#advisoradapterregistry)
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

`AdvisorAdapterRegistry`接口是Spring AOP中的关键接口之一，用于注册和管理AdvisorAdapters，它负责将Advisor与AOP框架所支持的特定拦截器关联起来，实现对目标对象方法的拦截和增强，从而实现面向切面编程的功能。

### 三、主要功能

1. **注册AdvisorAdapters**

   + 允许我们注册自定义的AdvisorAdapters，以适配新的拦截器类型或扩展现有的拦截器逻辑。

3. **支持内置拦截器**

   + 默认实现预先注册了一些标准的AdvisorAdapters，用于支持Spring AOP框架内置的拦截器类型（如BeforeAdvice、AfterReturningAdvice等）。

### 四、接口源码

作为Advisor适配器的注册表。它提供了方法来包装给定的advice为Advisor，并获取Advisor中的拦截器数组。通过注册AdvisorAdapter，实现了Advisor与AOP框架所支持的不同拦截器类型之间的适配。

```java
/**
 * Advisor适配器注册表的接口。
 *
 * <p><i>这是一个SPI接口，不应该由任何Spring用户实现。</i>
 *
 * @author Rod Johnson
 * @author Rob Harrop
 */
public interface AdvisorAdapterRegistry {

	/**
	 * 返回一个包装了给定advice的{@link Advisor}。
	 * <p>默认情况下应该至少支持
	 * {@link org.aopalliance.intercept.MethodInterceptor},
	 * {@link org.springframework.aop.MethodBeforeAdvice},
	 * {@link org.springframework.aop.AfterReturningAdvice},
	 * {@link org.springframework.aop.ThrowsAdvice}。
	 * @param advice 应该是一个advice的对象
	 * @return 包装了给定advice的Advisor（永远不会为{@code null}；
	 * 如果advice参数本身就是一个Advisor，则直接返回）
	 * @throws UnknownAdviceTypeException 如果没有注册的advisor adapter
	 * 能够包装给定的advice
	 */
	Advisor wrap(Object advice) throws UnknownAdviceTypeException;

	/**
	 * 返回一组AOP Alliance MethodInterceptors，以允许在基于拦截的框架中使用给定的Advisor。
	 * <p>如果Advisor是一个{@link org.springframework.aop.PointcutAdvisor}，
	 * 则不必担心与其关联的切入点表达式只需返回一个拦截器。
	 * @param advisor 要查找拦截器的Advisor
	 * @return 一组MethodInterceptor，用于暴露此Advisor的行为
	 * @throws UnknownAdviceTypeException 如果Advisor类型
	 * 不被任何注册的AdvisorAdapter理解
	 */
	MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException;

	/**
	 * 注册给定的{@link AdvisorAdapter}。
	 * 注意，不需要为AOP Alliance Interceptors或Spring Advices注册适配器
	 * 这些必须由{@code AdvisorAdapterRegistry}的实现自动识别。
	 * @param adapter 理解特定Advisor或Advice类型的AdvisorAdapter
	 */
	void registerAdvisorAdapter(AdvisorAdapter adapter);

}
```

### 五、主要实现

1. **DefaultAdvisorAdapterRegistry**

   + 默认Advisor适配器注册表实现，预先注册了标准的Advisor适配器，支持将各种类型的Advice适配到AOP Alliance MethodInterceptor，并允许我们注册自定义的Advisor适配器，从而实现了Advisor与拦截器之间的灵活适配和管理。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AdvisorAdapterRegistry {
<<Interface>>

}
class DefaultAdvisorAdapterRegistry

DefaultAdvisorAdapterRegistry  ..>  AdvisorAdapterRegistry 
~~~

### 七、最佳实践

使用`DefaultAdvisorAdapterRegistry`来包装自定义的`MyMethodBeforeAdvice`，并获取其对应的拦截器数组。通过`wrap()`方法将`MyMethodBeforeAdvice`转换为`Advisor`，然后使用`getInterceptors()`方法获取该`Advisor`中的拦截器数组，最后输出拦截器的信息。

```java
public class AdvisorAdapterRegistryDemo {

    public static void main(String[] args) {
        // 创建默认的Advisor适配器注册表实例
        DefaultAdvisorAdapterRegistry registry = new DefaultAdvisorAdapterRegistry();
        // 包装给定的MyMethodBeforeAdvice为Advisor
        Advisor advisor = registry.wrap(new MyMethodBeforeAdvice());

        // 获取Advisor中的拦截器数组
        MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
        // 输出拦截器信息
        for (MethodInterceptor interceptor : interceptors) {
            System.out.println("interceptor = " + interceptor);
        }
    }
}
```

### 八、源码分析

实现了`AdvisorAdapterRegistry`接口的默认实现`DefaultAdvisorAdapterRegistry`，支持将不同类型的Advice对象适配为Advisor，并提供获取Advisor中拦截器数组的功能。它预先注册了一些常见的Advisor适配器，并允许用户注册自定义的适配器。其核心逻辑包括将Advice对象包装为Advisor、根据Advisor获取拦截器数组以及注册Advisor适配器。

[AdvisorAdapter源码分析](../spring-aop-advisorAdapter/README.md)

```java
/**
 * AdvisorAdapterRegistry接口的默认实现。
 * 支持{@link org.aopalliance.intercept.MethodInterceptor}、
 * {@link org.springframework.aop.MethodBeforeAdvice}、
 * {@link org.springframework.aop.AfterReturningAdvice}、
 * {@link org.springframework.aop.ThrowsAdvice}。
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
@SuppressWarnings("serial")
public class DefaultAdvisorAdapterRegistry implements AdvisorAdapterRegistry, Serializable {

    // 用于存储注册的AdvisorAdapter的列表
    private final List<AdvisorAdapter> adapters = new ArrayList<>(3);

    /**
     * 创建一个新的DefaultAdvisorAdapterRegistry实例，并注册已知的适配器。
     * 这里的“已知的适配器”包括MethodBeforeAdviceAdapter、AfterReturningAdviceAdapter、ThrowsAdviceAdapter。
     */
    public DefaultAdvisorAdapterRegistry() {
        // 注册MethodBeforeAdviceAdapter适配器
        registerAdvisorAdapter(new MethodBeforeAdviceAdapter());
        // 注册AfterReturningAdviceAdapter适配器
        registerAdvisorAdapter(new AfterReturningAdviceAdapter());
        // 注册ThrowsAdviceAdapter适配器
        registerAdvisorAdapter(new ThrowsAdviceAdapter());
    }

    /**
     * 将给定的adviceObject包装为Advisor。
     * 如果adviceObject已经是Advisor，则直接返回；
     * 如果不是Advice类型，则抛出UnknownAdviceTypeException；
     * 如果advice是MethodInterceptor类型，则创建一个DefaultPointcutAdvisor并返回；
     * 否则，遍历已注册的AdvisorAdapter，找到支持advice的适配器，创建一个DefaultPointcutAdvisor并返回。
     *
     * @param adviceObject 要包装为Advisor的Advice对象
     * @return 包装后的Advisor对象
     * @throws UnknownAdviceTypeException 如果adviceObject无法被识别为Advisor或Advice
     */
    @Override
    public Advisor wrap(Object adviceObject) throws UnknownAdviceTypeException {
        if (adviceObject instanceof Advisor) {
            return (Advisor) adviceObject;
        }
        if (!(adviceObject instanceof Advice)) {
            throw new UnknownAdviceTypeException(adviceObject);
        }
        Advice advice = (Advice) adviceObject;
        if (advice instanceof MethodInterceptor) {
            // 对于MethodInterceptor类型的Advice，不需要适配器，直接创建Advisor并返回
            return new DefaultPointcutAdvisor(advice);
        }
        // 遍历已注册的AdvisorAdapter，查找支持当前Advice的适配器
        for (AdvisorAdapter adapter : this.adapters) {
            // 检查是否支持当前Advice
            if (adapter.supportsAdvice(advice)) {
                // 创建Advisor并返回
                return new DefaultPointcutAdvisor(advice);
            }
        }
        // 如果无法找到合适的适配器，抛出异常
        throw new UnknownAdviceTypeException(advice);
    }

    /**
     * 获取Advisor中的拦截器数组。
     * 如果Advisor中的Advice是MethodInterceptor类型，则直接返回；
     * 否则，遍历已注册的AdvisorAdapter，找到支持Advisor中的Advice的适配器，并获取对应的拦截器，返回拦截器数组。
     *
     * @param advisor 要获取拦截器数组的Advisor对象
     * @return 包含Advisor中拦截器的数组
     * @throws UnknownAdviceTypeException 如果Advisor中的Advice无法被识别
     */
    @Override
    public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
        List<MethodInterceptor> interceptors = new ArrayList<>(3);
        Advice advice = advisor.getAdvice();
        // 如果Advisor中的Advice是MethodInterceptor类型，直接将其添加到拦截器数组中
        if (advice instanceof MethodInterceptor) {
            interceptors.add((MethodInterceptor) advice);
        }
        // 遍历已注册的AdvisorAdapter，查找支持Advisor中的Advice的适配器
        for (AdvisorAdapter adapter : this.adapters) {
            // 如果适配器支持当前Advice，获取其拦截器并添加到数组中
            if (adapter.supportsAdvice(advice)) {
                interceptors.add(adapter.getInterceptor(advisor));
            }
        }
        // 如果拦截器数组为空，表示未找到适配器，抛出异常
        if (interceptors.isEmpty()) {
            throw new UnknownAdviceTypeException(advice);
        }
        // 将拦截器数组转换为数组并返回
        return interceptors.toArray(new MethodInterceptor[0]);
    }

    /**
     * 注册给定的AdvisorAdapter。
     *
     * @param adapter 要注册的AdvisorAdapter对象
     */
    @Override
    public void registerAdvisorAdapter(AdvisorAdapter adapter) {
        this.adapters.add(adapter);
    }

}
```
