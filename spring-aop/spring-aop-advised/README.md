## Advised

- [Advised](#advised)
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

`Advised` 接口是 Spring AOP 中的核心接口之一，代表了被 Spring AOP 支持的被通知对象，它提供了对被通知对象的通用管理方法，包括检查是否被冻结、是否代理目标类、获取被代理接口、添加、移除和获取通知等，通过这些方法可以实现对被通知对象的动态通知管理。

### 三、主要功能

1. **冻结状态检查（isFrozen）**

   + 可以检查被通知对象是否处于冻结状态，即是否可以修改其 AOP 配置。

2. **代理类型检查（isProxyTargetClass）**

   + 可以检查是否代理了目标类，而不是目标接口。

3. **获取被代理接口（getProxiedInterfaces）**

   + 可以获取被代理对象所实现的接口数组。

4. **接口代理检查（isInterfaceProxied）**

   + 可以检查给定的接口是否被代理。

5. **添加通知（addAdvice）**

   + 可以向被通知对象添加通知，实现对目标方法的增强。

6. **指定位置添加通知（addAdvice）**

   + 可以在指定位置添加通知，控制通知的执行顺序。

7. **获取所有通知（getAdvices）**

   + 可以获取所有添加到被通知对象的通知。

8. **移除通知（removeAdvice）**

   + 可以移除指定的通知，动态调整通知的配置。

### 四、接口源码

`Advised`接口 ，规定了代理工厂配置的结构和行为。这个接口包含了许多方法，用于管理AOP代理的配置，包括拦截器、通知器、被代理的接口等。通过这个接口，可以获取和操作AOP代理的配置信息，如是否冻结、是否代理目标类、获取被代理的接口、添加和移除通知器等。

```java
/**
 * 用于实现持有 AOP 代理工厂配置的类的接口。该配置包括拦截器和其他通知器、通知器以及被代理的接口。
 *
 * 从 Spring 获取的任何 AOP 代理都可以转换为此接口，以允许对其 AOP 通知进行操作。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 13.03.2003
 * @see org.springframework.aop.framework.AdvisedSupport
 */
public interface Advised extends TargetClassAware {

	/**
	 * 返回 Advised 配置是否已冻结，如果已冻结，则无法进行通知更改。
	 */
	boolean isFrozen();

	/**
	 * 是否代理了完整的目标类，而不是指定的接口？
	 */
	boolean isProxyTargetClass();

	/**
	 * 返回 AOP 代理所代理的接口。
	 * 不会包括目标类，目标类可能也会被代理。
	 */
	Class<?>[] getProxiedInterfaces();

	/**
	 * 确定给定的接口是否被代理。
	 * @param intf 要检查的接口
	 */
	boolean isInterfaceProxied(Class<?> intf);

	/**
	 * 更改此 Advised 对象使用的 TargetSource。
	 * 仅在配置未被冻结时有效。
	 * @param targetSource 要使用的新 TargetSource
	 */
	void setTargetSource(TargetSource targetSource);

	/**
	 * 返回此 Advised 对象使用的 TargetSource。
	 */
	TargetSource getTargetSource();

	/**
	 * 设置代理是否应该被 AOP 框架公开为 {@link ThreadLocal}，以便通过 {@link AopContext} 类进行检索。
	 * 如果需要在通知对象上调用自身的方法并应用通知，则可能需要公开代理。否则，如果通知对象调用 {@code this} 的方法，将不会应用任何通知。
	 * 默认为 {@code false}，以获得最佳性能。
	 */
	void setExposeProxy(boolean exposeProxy);

	/**
	 * 返回工厂是否应将代理公开为 {@link ThreadLocal}。
	 * 如果需要在通知对象上调用自身的方法并应用通知，则可能需要公开代理。否则，如果通知对象调用 {@code this} 的方法，将不会应用任何通知。
	 * 获取代理类似于 EJB 调用 {@code getEJBObject()}。
	 * @see AopContext
	 */
	boolean isExposeProxy();

	/**
	 * 设置此代理配置是否经过预过滤，以便仅包含适用的通知器（与此代理的目标类匹配）。
	 * 默认为 "false"。如果通知器已经被预过滤，即可以跳过构建实际的代理调用链时的 ClassFilter 检查，则将其设置为 "true"。
	 * @see org.springframework.aop.ClassFilter
	 */
	void setPreFiltered(boolean preFiltered);

	/**
	 * 返回此代理配置是否经过预过滤，以便仅包含适用的通知器（与此代理的目标类匹配）。
	 */
	boolean isPreFiltered();

	/**
	 * 返回应用于此代理的通知器。
	 * @return 应用于此代理的通知器列表（永远不会为 {@code null}）
	 */
	Advisor[] getAdvisors();

	/**
	 * 返回应用于此代理的通知器数量。
	 * 默认实现委托给 {@code getAdvisors().length}。
	 * @since 5.3.1
	 */
	default int getAdvisorCount() {
		return getAdvisors().length;
	}

	/**
	 * 在通知器链的末尾添加一个通知器。
	 * 通知器可以是 {@link org.springframework.aop.IntroductionAdvisor}，
	 * 在从相关工厂下次获取代理时将提供新的接口。
	 * @param advisor 要添加到链的末尾的通知器
	 * @throws AopConfigException 如果通知器无效
	 */
	void addAdvisor(Advisor advisor) throws AopConfigException;

	/**
	 * 在链中的指定位置添加一个通知器。
	 * @param advisor 要在链中指定位置添加的通知器
	 * @param pos 链中的位置（0 是头）。必须有效。
	 * @throws AopConfigException 如果通知器无效
	 */
	void addAdvisor(int pos, Advisor advisor) throws AopConfigException;

	/**
	 * 删除给定的通知器。
	 * @param advisor 要移除的通知器
	 * @return 如果已移除通知器，则返回 {@code true}；如果未找到该通知器，因此无法移除，则返回 {@code false}
	 */
	boolean removeAdvisor(Advisor advisor);

	/**
	 * 移除给定索引处的通知器。
	 * @param index 要移除的通知器的索引
	 * @throws AopConfigException 如果索引无效
	 */
	void removeAdvisor(int index) throws AopConfigException;

	/**
	 * 返回给定通知器的索引（从 0 开始），
	 * 如果没有这样的通知器适用于此代理，则返回 -1。
	 * 此方法的返回值可用于索引到通知器数组中。
	 * @param advisor 要搜索的通知器
	 * @return 此通知器的从 0 开始的索引，如果没有这样的通知器，则返回 -1
	 */
	int indexOf(Advisor advisor);

	/**
	 * 替换给定的通知器。
	 * 注意如果通知器是 {@link org.springframework.aop.IntroductionAdvisor}，
	 * 并且替换项不是或实现了不同的接口，则需要重新获取代理，否则旧接口将不被支持，新接口也将不被实现。
	 * @param a 要替换的通知器
	 * @param b 要替换的新通知器
	 * @return 是否已替换。如果通知器未在通知器列表中找到，则此方法返回 {@code false}，并且不执行任何操作。
	 * @throws AopConfigException 如果通知器无效
	 */
	boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException;

	/**
	 * 将给定的 AOP Alliance 通知添加到通知（拦截器）链的末尾。
	 * 这将被包装在一个始终适用的 DefaultPointcutAdvisor 中，并从 {@code getAdvisors()} 方法以此包装形式返回。
	 * 请注意，给定的通知将应用于代理的所有调用，甚至是 {@code toString()} 方法！使用适当的通知实现或指定适当的切入点以适用于更窄范围的方法。
	 * @param advice 要添加到链末尾的通知
	 * @throws AopConfigException 如果通知无效
	 * @see #addAdvice(int, Advice)
	 * @see org.springframework.aop.support.DefaultPointcutAdvisor
	 */
	void addAdvice(Advice advice) throws AopConfigException;

	/**
	 * 将给定的 AOP Alliance 通知添加到通知链的指定位置。
	 * 这将被包装在一个 {@link org.springframework.aop.support.DefaultPointcutAdvisor} 中，
	 * 并以此包装形式从 {@link #getAdvisors()} 方法返回。
	 * 注意给定的通知将应用于代理的所有调用，甚至是 {@code toString()} 方法！使用适当的通知实现或指定适当的切入点以适用于更窄范围的方法。
	 * @param pos 从 0 开始的索引（头部）
	 * @param advice 要在通知链的指定位置添加的通知
	 * @throws AopConfigException 如果通知无效
	 */
	void addAdvice(int pos, Advice advice) throws AopConfigException;

	/**
	 * 移除包含给定通知的通知器。
	 * @param advice 要移除的通知
	 * @return 如果找到并移除了通知，则返回 {@code true}；如果没有找到该通知，则返回 {@code false}
	 */
	boolean removeAdvice(Advice advice);

	/**
	 * 返回给定 AOP Alliance 通知的索引（从 0 开始），
	 * 如果没有这样的通知是此代理的通知，则返回 -1。
	 * 此方法的返回值可用于索引到通知器数组中。
	 * @param advice 要搜索的 AOP Alliance 通知
	 * @return 此通知的从 0 开始的索引，如果没有这样的通知，则返回 -1
	 */
	int indexOf(Advice advice);

	/**
	 * 由于通常将 {@code toString()} 委托给目标，因此此方法返回 AOP 代理的等效描述。
	 * @return 代理配置的字符串描述
	 */
	String toProxyConfigString();

}
```

### 五、主要实现

+ **AdvisedSupport**

  + 负责管理 AOP 代理配置信息的核心类，它包含了通知器、目标对象、目标源等关键属性，能够灵活地配置和管理 AOP 代理的创建过程，并提供了各种方法来处理代理配置的冻结状态、代理暴露等功能。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class Advised {
<<Interface>>

}
class AdvisedSupport
class TargetClassAware {
<<Interface>>

}

Advised  -->  TargetClassAware 
AdvisedSupport  ..>  Advised 
~~~

### 七、最佳实践

使用 `AdvisedSupport` 类来配置代理对象的相关属性，包括设置目标对象、接口、通知、通知器、是否暴露代理对象、是否使用 CGLIB 代理以及冻结对象，并通过 `toProxyConfigString()` 方法打印代理配置信息。

```java
public class AdvisedDemo {

    public static void main(String[] args) {
        // 创建 AdvisedSupport 对象
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 设置目标对象实现的接口
        advisedSupport.setInterfaces(MyService.class);
        // 添加通知
        advisedSupport.addAdvice(new Advice() {});
        // 添加通知器
        advisedSupport.addAdvisor(new DefaultPointcutAdvisor());
        // 暴露代理对象
        advisedSupport.setExposeProxy(true);
        // 设置CGLIB 代理
        advisedSupport.setProxyTargetClass(true);
        // 冻结对象
        advisedSupport.setFrozen(true);
        // 打印
        System.out.println("AdvisedSupport = " + advisedSupport.toProxyConfigString());
    }
}
```

定义了一个 `MyService` 接口

```java
public interface MyService {
    void foo();
}
```

实现了 `MyService` 接口的 `MyServiceImpl` 类。

```java
public class MyServiceImpl implements MyService {

    @Override
    public void foo() {
        System.out.println("foo...");
    }
}
```

运行结果，显示了 `AdvisedSupport` 对象的配置信息，包括代理的接口、通知器、目标对象信息、代理类型以及其他相关设置。

```java
AdvisedSupport = org.springframework.aop.framework.AdvisedSupport: 1 interfaces [com.xcs.spring.MyService]; 2 advisors [org.springframework.aop.support.DefaultPointcutAdvisor: pointcut [Pointcut.TRUE]; advice [com.xcs.spring.AdvisedDemo$1@32d992b2], org.springframework.aop.support.DefaultPointcutAdvisor: pointcut [Pointcut.TRUE]; advice [org.springframework.aop.Advisor$1@215be6bb]]; targetSource [SingletonTargetSource for target object [com.xcs.spring.MyServiceImpl@5d5eef3d]]; proxyTargetClass=true; optimize=false; opaque=false; exposeProxy=true; frozen=true
```
