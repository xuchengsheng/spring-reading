## AopProxy

- [AopProxy](#aopproxy)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、接口源码](#四接口源码)
  - [五、主要实现](#五主要实现)
  - [六、最佳实践](#六最佳实践)
    - [JDK动态代理](#jdk动态代理)
    - [CGLIB代理](#cglib代理)
  - [七、时序图](#七时序图)
    - [JdkDynamicAopProxy](#jdkdynamicaopproxy)
    - [CglibAopProxy](#cglibaopproxy)
  - [七、源码分析](#七源码分析)
    - [JdkDynamicAopProxy](#jdkdynamicaopproxy-1)
    - [CglibAopProxy](#cglibaopproxy-1)
  - [八、常见问题](#八常见问题)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`AopProxy` 接口是Spring框架中用于支持面向切面编程（AOP）的关键组件之一，它定义了生成代理对象的标准接口，允许在运行时动态地创建代理对象，以实现对目标对象的方法调用进行拦截和增强。

### 三、主要功能

1. **代理对象的创建与管理**

   + `AopProxy` 接口定义了创建和管理代理对象的标准方法，可以通过这些方法在运行时动态地生成代理对象。

2. **对目标对象方法的拦截与增强**

   + AOP代理对象通过 `AopProxy` 接口实现对目标对象方法的拦截，允许在方法执行前、后或异常时执行额外的逻辑，从而实现对方法行为的增强。

3. **支持不同的代理方式**

   + `AopProxy` 接口支持多种代理方式，包括JDK动态代理和CGLIB代理。这样可以根据目标对象是否实现接口来选择合适的代理方式。

4. **实现AOP的横切关注点**

   + 通过 `AopProxy` 接口，可以将AOP的横切关注点与业务逻辑进行分离，提高了代码的模块化和可维护性，同时也使得横切关注点可以被重用在多个不同的业务逻辑中。

### 四、接口源码

`AopProxy` 接口是一个委托接口，用于配置AOP代理，并允许创建实际的代理对象。它提供了两个方法用于创建代理对象，第一个方法使用默认的类加载器创建代理对象，通常是线程上下文类加载器；第二个方法允许指定类加载器创建代理对象。可以使用JDK动态代理或者CGLIB代理技术来生成代理对象。

```java
/**
 * 配置AOP代理的委托接口，允许创建实际的代理对象。
 *
 * <p>默认情况下，可用于创建代理对象的实现包括JDK动态代理和CGLIB代理，
 * 这些代理实现由 {@link DefaultAopProxyFactory} 应用。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DefaultAopProxyFactory
 */
public interface AopProxy {

	/**
	 * 创建一个新的代理对象。
	 * <p>使用AopProxy的默认类加载器（必要时用于代理创建）：
	 * 通常为线程上下文类加载器。
	 * @return 新的代理对象（永远不会是 {@code null}）
	 * @see Thread#getContextClassLoader()
	 */
	Object getProxy();

	/**
	 * 创建一个新的代理对象。
	 * <p>使用给定的类加载器（必要时用于代理创建）。
	 * 如果给定的类加载器为 {@code null}，则简单地传递并因此导致低级代理工具的默认值，
	 * 这通常不同于AopProxy实现的 {@link #getProxy()} 方法选择的默认值。
	 * @param classLoader 用于创建代理的类加载器
	 * （或 {@code null} 表示使用低级代理工具的默认值）
	 * @return 新的代理对象（永远不会是 {@code null}）
	 */
	Object getProxy(@Nullable ClassLoader classLoader);

}
```

### 五、主要实现

1. **JdkDynamicAopProxy**

   + 使用 JDK 动态代理实现的 `AopProxy` 实现类。当目标对象实现了至少一个接口时，Spring 将使用该类创建代理对象。该类通过 Java 标准库中的 `java.lang.reflect.Proxy` 类来创建代理对象。
   
2. **CglibAopProxy**

     + 使用 CGLIB（Code Generation Library）动态代理实现的 `AopProxy` 实现类。当目标对象没有实现任何接口时，Spring 将使用该类创建代理对象。该类通过生成目标类的子类来创建代理对象，实现了对目标对象方法的拦截和增强。

### 六、最佳实践

#### JDK动态代理

使用 JDK 动态代理来创建 AOP 代理对象。在 `jdkProxy` 方法中，通过配置 `AdvisedSupport` 对象，设置目标对象和接口，然后利用反射创建 `JdkDynamicAopProxy` 实例，并调用 `AopProxy` 接口的 `getProxy` 方法生成代理对象。最后，输出代理对象的信息和调用代理对象方法的结果。

```java
public class AopProxyDemo {

    public static void main(String[] args) throws Exception {
        jdkProxy();
    }

    /**
     * Jdk代理
     *
     * @throws Exception
     */
    private static void jdkProxy() throws Exception {
        // 创建AdvisedSupport对象，用于配置AOP代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 设置目标对象实现的接口
        advisedSupport.setInterfaces(MyService.class);

        // 获取JdkDynamicAopProxy的Class对象
        Class jdkClass = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");

        // 获取JdkDynamicAopProxy的构造方法
        Constructor constructor = jdkClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // 使用构造方法创建JdkDynamicAopProxy实例
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // 调用getProxy方法创建代理对象
        MyService myService = (MyService) aopProxy.getProxy();

        // 输出代理对象的信息
        System.out.println("JDK Class = " + myService.getClass());
        // 调用代理对象的方法
        System.out.println("doSomething method result = " + myService.doSomething());
    } 
}
```

运行结果，代理对象的类为 `com.sun.proxy.$Proxy0`，调用代理对象的 `doSomething` 方法结果为 `"hello world"`。

```java
JDK Class = class com.sun.proxy.$Proxy0
doSomething method result = hello world
```

#### CGLIB代理

使用 CGLIB 动态代理来创建 AOP 代理对象。在 `cglibProxy` 方法中，通过配置 `AdvisedSupport` 对象，设置目标对象，然后利用反射创建 `CglibAopProxy` 实例，并调用 `AopProxy` 接口的 `getProxy` 方法生成代理对象。最后，输出代理对象的信息和调用代理对象方法的结果。

```java
public class AopProxyDemo {

    public static void main(String[] args) throws Exception {
        cglibProxy();
    }

    /**
     * cglib代理
     *
     * @throws Exception
     */
    private static void cglibProxy() throws Exception {
        // 创建AdvisedSupport对象，用于配置AOP代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());

        // 获取CglibAopProxy的Class对象
        Class cglibClass = Class.forName("org.springframework.aop.framework.CglibAopProxy");

        // 获取CglibAopProxy的构造方法
        Constructor constructor = cglibClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // 使用构造方法创建CglibAopProxy实例
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // 调用getProxy方法创建代理对象
        MyService myService = (MyService) aopProxy.getProxy();

        // 输出代理对象的信息
        System.out.println("Cglib Class = " + myService.getClass());
        // 调用代理对象的方法
        System.out.println("doSomething method result = " + myService.doSomething());
    }   
}
```

运行结果，代理对象的类为 `com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$3c231008`，调用代理对象的 `doSomething` 方法结果为 `"hello world"`。

```java
Cglib Class = class com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$3c231008
doSomething method result = hello world
```

### 七、时序图

#### JdkDynamicAopProxy

~~~mermaid
sequenceDiagram
autonumber
AopProxyDemo->>JdkDynamicAopProxy:new JdkDynamicAopProxy()
JdkDynamicAopProxy->>JdkDynamicAopProxy:this.advised
JdkDynamicAopProxy->>JdkDynamicAopProxy:this.proxiedInterfaces
JdkDynamicAopProxy->>AopProxyDemo:返回aopProxy
AopProxyDemo->>JdkDynamicAopProxy:aopProxy.getProxy()
JdkDynamicAopProxy->>JdkDynamicAopProxy:getProxy(classLoader)
JdkDynamicAopProxy->>Proxy:Proxy.newProxyInstance()
JdkDynamicAopProxy->>AopProxyDemo:返回代理对象
AopProxyDemo->>$Proxy0:aopProxy.doSomething()
$Proxy0->>JdkDynamicAopProxy:invoke()
alt 不存在拦截链
	rect rgb(122,197,205)
		JdkDynamicAopProxy->>AopUtils: invokeJoinpointUsingReflection()
		AopUtils->>Method:method.invoke(target, args)
        Method->>$Proxy0:doSomething()
	end
else 存在拦截链
	rect rgb(155,205,155)
		JdkDynamicAopProxy->>ReflectiveMethodInvocation:new ReflectiveMethodInvocation()
		ReflectiveMethodInvocation->>JdkDynamicAopProxy:返回invocation
		JdkDynamicAopProxy->>ReflectiveMethodInvocation:invocation.proceed()
        ReflectiveMethodInvocation->>ReflectiveMethodInvocation:invokeJoinpoint()
        ReflectiveMethodInvocation->>AopUtils:invokeJoinpointUsingReflection()
        AopUtils->>Method:method.invoke(target, args)
        Method->>$Proxy0:doSomething()
    end
end

~~~

#### CglibAopProxy

~~~mermaid
sequenceDiagram
autonumber
AopProxyDemo->>CglibAopProxy:new CglibAopProxy()
CglibAopProxy->>CglibAopProxy:this.advised
CglibAopProxy->>CglibAopProxy:this.advisedDispatcher
CglibAopProxy->>AopProxyDemo:返回aopProxy
AopProxyDemo->>CglibAopProxy:aopProxy.getProxy()
CglibAopProxy->>CglibAopProxy:getProxy(classLoader)
CglibAopProxy->>Enhancer:new Enhancer()
Enhancer->>CglibAopProxy:返回enhancer
CglibAopProxy->>CglibAopProxy:getCallbacks()
CglibAopProxy->>CglibAopProxy:createProxyClassAndInstance()
CglibAopProxy->>Enhancer:enhancer.create()
CglibAopProxy->>AopProxyDemo:返回代理对象
AopProxyDemo->>MyServiceImpl$$EnhancerBySpringCGLIB$$:aopProxy.doSomething()
MyServiceImpl$$EnhancerBySpringCGLIB$$->>DynamicAdvisedInterceptor:intercept()
alt 不存在拦截链
	rect rgb(122,197,205)
		DynamicAdvisedInterceptor->>MethodProxy:this.methodProxy.invoke()
		MethodProxy->>MyServiceImpl$$EnhancerBySpringCGLIB$$:doSomething()
	end
else 存在拦截链
	rect rgb(155,205,155)
		DynamicAdvisedInterceptor->>CglibMethodInvocation:new CglibMethodInvocation()
        MethodProxy->>CglibMethodInvocation:传递methodProxy
        CglibMethodInvocation->>CglibMethodInvocation:接收methodProxy
        CglibMethodInvocation->>DynamicAdvisedInterceptor:返回invocation
        DynamicAdvisedInterceptor->>CglibMethodInvocation:invocation.proceed()
        CglibMethodInvocation->>ReflectiveMethodInvocation:super.proceed()
        ReflectiveMethodInvocation->>CglibMethodInvocation:invokeJoinpoint()
        CglibMethodInvocation->>MethodProxy:this.methodProxy.invoke()
        MethodProxy->>MyServiceImpl$$EnhancerBySpringCGLIB$$:doSomething()
    end
end
~~~

### 七、源码分析

#### JdkDynamicAopProxy

在`org.springframework.aop.framework.JdkDynamicAopProxy#getProxy()`方法中，主要作用是返回一个代理对象，使用默认的类加载器来生成代理。

```java
@Override
public Object getProxy() {
    return getProxy(ClassUtils.getDefaultClassLoader());
}
```

在`org.springframework.aop.framework.JdkDynamicAopProxy#getProxy(java.lang.ClassLoader)`方法中，接收一个类加载器作为参数，并根据传入的类加载器和被代理的接口数组来创建一个 JDK 动态代理对象。

```java
@Override
public Object getProxy(@Nullable ClassLoader classLoader) {
    if (logger.isTraceEnabled()) {
       logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
    }
    return Proxy.newProxyInstance(classLoader, this.proxiedInterfaces, this);
}
```

在`org.springframework.aop.framework.JdkDynamicAopProxy#invoke`方法中，`JdkDynamicAopProxy`实现了`InvocationHandler`接口，因此可以执行`invoke`方法。在方法中，首先根据方法是否为`equals`或`hashCode`方法进行特殊处理，然后获取目标对象并获取拦截器链。接着，根据拦截器链是否为空，选择直接调用目标对象方法或者通过方法拦截器链依次执行。最后，根据方法的返回值类型进行处理，如果返回值为目标对象并且返回类型与代理类型相同，则将返回值修改为代理对象。在方法执行完毕后，确保释放目标对象并恢复旧的代理对象。

```java
/**
 * 实现了 {@code InvocationHandler.invoke} 方法。
 * <p>调用者将看到目标对象抛出的异常，除非一个钩子方法抛出异常。
 */
@Override
@Nullable
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 旧的代理对象
    Object oldProxy = null; 
    // 是否设置了代理上下文标志
    boolean setProxyContext = false; 

    // 目标源
    TargetSource targetSource = this.advised.targetSource; 
    // 目标对象
    Object target = null; 

    try {
        if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
            // 目标对象未实现 equals(Object) 方法
            return equals(args[0]);
        } else if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
            // 目标对象未实现 hashCode() 方法
            return hashCode();
        } else if (method.getDeclaringClass() == DecoratingProxy.class) {
            // 只有 getDecoratedClass() 声明 -> 转发到代理配置
            return AopProxyUtils.ultimateTargetClass(this.advised);
        } else if (!this.advised.opaque && method.getDeclaringClass().isInterface() &&
                method.getDeclaringClass().isAssignableFrom(Advised.class)) {
            // 在代理配置上执行服务调用...
            return AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
        }

        Object retVal;

        if (this.advised.exposeProxy) {
            // 必要时使调用可用
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
        }

        // 尽可能晚地获取目标对象，以最小化我们“拥有”目标对象的时间，以防它来自池。
        target = targetSource.getTarget();
        Class<?> targetClass = (target != null ? target.getClass() : null);

        // 获取此方法的拦截器链。
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

        // 检查是否有任何通知。如果没有，则可以回退到直接反射调用目标，避免创建 MethodInvocation。
        if (chain.isEmpty()) {
            // 我们可以跳过创建一个 MethodInvocation：直接调用目标
            // 注意，最终的调用者必须是一个 InvokerInterceptor，这样我们就知道它只是在目标上执行反射操作，而没有热交换或花哨的代理。
            Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
            retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
        } else {
            // 我们需要创建一个方法调用...
            MethodInvocation invocation =
                    new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
            // 通过拦截器链继续进行连接点。
            retVal = invocation.proceed();
        }

        // 如果需要，修改返回值。
        Class<?> returnType = method.getReturnType();
        if (retVal != null && retVal == target &&
                returnType != Object.class && returnType.isInstance(proxy) &&
                !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            // 特殊情况：它返回了“this”，并且方法的返回类型与之相容。
            // 请注意，如果目标在另一个返回对象中设置了对自身的引用，我们无法帮助。
            retVal = proxy;
        } else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException(
						"Null return value from advice does not match primitive return type for: " + method);
        }
        return retVal;
    } finally {
        if (target != null && !targetSource.isStatic()) {
            // 必须来自 TargetSource。
            targetSource.releaseTarget(target);
        }
        if (setProxyContext) {
            // 恢复旧代理。
            AopContext.setCurrentProxy(oldProxy);
        }
    }
}
```

在`org.springframework.aop.framework.ReflectiveMethodInvocation#ReflectiveMethodInvocation`方法中，`ReflectiveMethodInvocation`类的构造函数，用于创建一个反射方法调用对象。它接收代理对象、目标对象、要调用的方法、方法参数、目标类以及拦截器和动态方法匹配器列表作为参数，并在构造过程中对这些参数进行初始化。

```java
/**
 * 使用给定参数构造一个新的 ReflectiveMethodInvocation。
 * @param proxy 调用所在的代理对象
 * @param target 要调用的目标对象
 * @param method 要调用的方法
 * @param arguments 调用方法时传入的参数
 * @param targetClass 目标类，用于方法匹配器的调用
 * @param interceptorsAndDynamicMethodMatchers 应该应用的拦截器，以及需要在运行时进行评估的任何 InterceptorAndDynamicMethodMatchers。
 * 此结构中包含的 MethodMatchers 必须已经被找到并匹配，尽可能地是静态的。传递一个数组可能会快约10%，但会使代码复杂化。并且它只能用于静态切入点。
 */
protected ReflectiveMethodInvocation(
        Object proxy, @Nullable Object target, Method method, @Nullable Object[] arguments,
        @Nullable Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
	// 代理对象
    this.proxy = proxy;
    // 目标对象
    this.target = target; 
    // 目标类
    this.targetClass = targetClass; 
    // 找到桥接方法
    this.method = BridgeMethodResolver.findBridgedMethod(method); 
    // 调整参数
    this.arguments = AopProxyUtils.adaptArgumentsIfNecessary(method, arguments); 
    // 拦截器和动态方法匹配器列表
    this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers; 
}
```

在`org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`方法中，首先，检查当前拦截器索引是否到达了链的末尾，如果是，则直接调用`invokeJoinpoint()`方法执行方法调用。如果不是末尾，获取当前拦截器或拦截器通知，并根据其类型进行不同的处理。如果是`InterceptorAndDynamicMethodMatcher`类型，则评估动态方法匹配器，如果匹配成功，则调用对应的拦截器的`invoke()`方法。如果匹配失败，则跳过此拦截器，继续调用链中的下一个拦截器。如果是普通的拦截器，则直接调用其`invoke()`方法。

```java
/**
 * 执行方法拦截器链中的下一个拦截器或方法调用。
 * @return 方法调用的结果
 * @throws Throwable 如果执行方法调用时发生异常
 */
@Override
@Nullable
public Object proceed() throws Throwable {
    // 我们从 -1 开始索引，并尽早递增。
    if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
        return invokeJoinpoint();
    }

    // 获取当前拦截器或拦截器通知
    Object interceptorOrInterceptionAdvice =
            this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
    if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
        // 在此处评估动态方法匹配器：静态部分已经被评估并确定匹配。
        InterceptorAndDynamicMethodMatcher dm =
                (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
        Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
        if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
            return dm.interceptor.invoke(this);
        } else {
            // 动态匹配失败。
            // 跳过此拦截器，并调用链中的下一个。
            return proceed();
        }
    } else {
        // 它是一个拦截器，所以我们只是调用它：在此对象构造之前，切入点将已经被静态评估。
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
    }
}
```

在`org.springframework.aop.framework.ReflectiveMethodInvocation#invokeJoinpoint`方法中，调用`AopUtils.invokeJoinpointUsingReflection`方法来实现连接点的调用，该方法接收目标对象、方法和参数，并返回连接点的结果。

```java
/**
 * 使用反射调用连接点。
 * 子类可以重写此方法以使用自定义调用。
 * @return 连接点的返回值
 * @throws Throwable 如果调用连接点导致异常
 */
@Nullable
protected Object invokeJoinpoint() throws Throwable {
    return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
}
```

在`org.springframework.aop.support.AopUtils#invokeJoinpointUsingReflection`方法中，通过`ReflectionUtils.makeAccessible(method)`方法确保要调用的方法是可访问的。然后，使用`method.invoke(target, args)`方法调用目标对象的方法，并传入参数。

```java
/**
 * 使用反射调用给定目标对象的方法，作为AOP方法调用的一部分。
 * @param target 目标对象
 * @param method 要调用的方法
 * @param args 方法的参数
 * @return 调用结果，如果有的话
 * @throws Throwable 如果目标方法抛出异常
 * @throws org.springframework.aop.AopInvocationException 如果发生反射错误
 */
@Nullable
public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args)
        throws Throwable {

    // 使用反射调用方法。
    try {
        // 使方法可访问
        ReflectionUtils.makeAccessible(method); 
        // 调用方法
        return method.invoke(target, args); 
    }
    catch (InvocationTargetException ex) {
        // 调用的方法抛出了一个已检查的异常。
        // 我们必须重新抛出它。客户端不会看到拦截器。
        throw ex.getTargetException(); // 抛出目标异常
    }
    catch (IllegalArgumentException ex) {
        // 参数异常
        throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" +
					method + "] on target [" + target + "]", ex);
    }
    catch (IllegalAccessException ex) {
        // 非法访问异常
        throw new AopInvocationException("Could not access method [" + method + "]", ex);
    }
}
```

#### CglibAopProxy

在`org.springframework.aop.framework.CglibAopProxy#getProxy()`方法中，它返回代理对象。在没有指定目标类加载器的情况下，它调用了另一个重载方法 `getProxy(null)` 来生成代理对象并返回。

```java
@Override
public Object getProxy() {
    return getProxy(null);
}
```

在`org.springframework.aop.framework.CglibAopProxy#getProxy(java.lang.ClassLoader)`方法中，使用 CGLIB 动态生成代理类，并创建代理对象。首先，它检查是否启用了跟踪日志，然后获取目标类的根类，并确保目标类可用于创建代理。接着，它设置代理类的父类为目标类的根类，并根据需要添加额外的接口。在配置 CGLIB Enhancer 之后，它为代理类设置回调函数，并最终生成代理类并创建代理实例。

```java
@Override
public Object getProxy(@Nullable ClassLoader classLoader) {
    // 如果启用了跟踪日志，则记录正在创建 CGLIB 代理的信息
    if (logger.isTraceEnabled()) {
        logger.trace("Creating CGLIB proxy: " + this.advised.getTargetSource());
    }

    try {
        // 获取目标类的根类
        Class<?> rootClass = this.advised.getTargetClass();
        // 断言目标类必须可用于创建 CGLIB 代理
        Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");

        // 设置代理类的父类为目标类的根类
        Class<?> proxySuperClass = rootClass;
        // 如果目标类的名称包含了 CGLIB 分隔符，则将父类修改为目标类的父类，并将额外的接口添加到代理类中
        if (rootClass.getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
            proxySuperClass = rootClass.getSuperclass();
            // 获取额外的接口并添加到代理类中
            Class<?>[] additionalInterfaces = rootClass.getInterfaces();
            for (Class<?> additionalInterface : additionalInterfaces) {
                this.advised.addInterface(additionalInterface);
            }
        }

        // 在需要时验证类，并写入日志消息
        validateClassIfNecessary(proxySuperClass, classLoader);

        // 配置 CGLIB Enhancer...
        Enhancer enhancer = createEnhancer();
        // 如果指定了类加载器，则设置 Enhancer 的类加载器，并在类加载器为可重新加载时禁用缓存
        if (classLoader != null) {
            enhancer.setClassLoader(classLoader);
            if (classLoader instanceof SmartClassLoader &&
                    ((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
                enhancer.setUseCache(false);
            }
        }
        // 设置代理类的父类
        enhancer.setSuperclass(proxySuperClass);
        // 设置代理类实现的接口
        enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
        // 设置命名策略
        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        // 设置策略以考虑类加载器
        enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(classLoader));

        // 获取回调对象和对应的类型
        Callback[] callbacks = getCallbacks(rootClass);
        Class<?>[] types = new Class<?>[callbacks.length];
        for (int x = 0; x < types.length; x++) {
            types[x] = callbacks[x].getClass();
        }
        // fixedInterceptorMap 只在上面的 getCallbacks 调用后才填充
        // 设置回调过滤器，用于过滤固定拦截器
        enhancer.setCallbackFilter(new ProxyCallbackFilter(
                this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
        // 设置回调类型
        enhancer.setCallbackTypes(types);

        // 生成代理类并创建代理实例
        return createProxyClassAndInstance(enhancer, callbacks);
    } catch (CodeGenerationException | IllegalArgumentException ex) {
        // 如果生成代理类出现异常，则抛出 AopConfigException
        throw new AopConfigException("Could not generate CGLIB subclass of " + this.advised.getTargetClass() +
                ": Common causes of this problem include using a final class or a non-visible class", ex);
    } catch (Throwable ex) {
        // 如果获取目标类实例失败，则抛出 AopConfigException
        throw new AopConfigException("Unexpected AOP exception", ex);
    }
}
```

在`org.springframework.aop.framework.CglibAopProxy#getCallbacks`方法中，根据代理配置的不同情况，选择不同的拦截器和分发器，并根据目标类的静态性和建议链的冻结状态进行优化选择。如果目标是静态的并且建议链是冻结的，它会通过使用固定链将AOP调用直接发送到目标来进行一些优化。最终返回一个包含所有选定回调的数组。

```java
private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
    // 用于优化选择的参数...
    boolean exposeProxy = this.advised.isExposeProxy();
    boolean isFrozen = this.advised.isFrozen();
    boolean isStatic = this.advised.getTargetSource().isStatic();

    // 选择一个“AOP”拦截器（用于AOP调用）。
    Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);

    // 选择一个“直接到目标”的拦截器（用于无通知的调用，但可以返回this）。
    Callback targetInterceptor;
    if (exposeProxy) {
        targetInterceptor = (isStatic ?
                new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) :
                new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource()));
    } else {
        targetInterceptor = (isStatic ?
                new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) :
                new DynamicUnadvisedInterceptor(this.advised.getTargetSource()));
    }

    // 选择一个“直接到目标”的分发器（用于对静态目标的未通知调用，无法返回this）。
    Callback targetDispatcher = (isStatic ?
            new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp());

    Callback[] mainCallbacks = new Callback[] {
            aopInterceptor,  // 用于普通建议
            targetInterceptor,  // 在优化的情况下调用目标，不考虑建议
            new SerializableNoOp(),  // 对于映射到此的方法，没有覆盖
            targetDispatcher, this.advisedDispatcher,
            new EqualsInterceptor(this.advised),
            new HashCodeInterceptor(this.advised)
    };

    Callback[] callbacks;

    // 如果目标是静态的并且建议链被冻结，
    // 则我们可以通过使用固定链将AOP调用直接发送到目标来进行一些优化。
    if (isStatic && isFrozen) {
        Method[] methods = rootClass.getMethods();
        Callback[] fixedCallbacks = new Callback[methods.length];
        this.fixedInterceptorMap = CollectionUtils.newHashMap(methods.length);

        // TODO: 这里进行了一些内存优化（可以跳过没有建议的方法的创建）
        for (int x = 0; x < methods.length; x++) {
            Method method = methods[x];
            List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, rootClass);
            fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(
                    chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
            this.fixedInterceptorMap.put(method, x);
        }

        // 现在将mainCallbacks和fixedCallbacks中的回调复制到callbacks数组中。
        callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
        System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
        System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
        this.fixedInterceptorOffset = mainCallbacks.length;
    } else {
        callbacks = mainCallbacks;
    }
    return callbacks;
}
```

在`org.springframework.aop.framework.CglibAopProxy.DynamicAdvisedInterceptor#intercept`方法中，首先，它获取目标对象和目标类，并获取与指定方法相关的拦截器链。然后，根据拦截器链和方法的特性进行适当的处理。如果拦截器链为空且方法是公共的，则直接调用目标方法，否则创建一个方法调用。最后，处理方法调用的返回值并返回结果。在方法执行过程中，还会根据配置决定是否暴露代理对象，并在必要时设置AOP上下文。最后，在finally块中释放目标对象，并在必要时恢复旧的代理对象。

> 在 `CglibAopProxy` 中，`DynamicAdvisedInterceptor` 是一个特殊的拦截器，它被添加到了回调数组中。当执行方法时，回调数组中的 `DynamicAdvisedInterceptor` 将拦截目标方法的调用。这个拦截器负责处理 AOP 的核心功能，例如应用通知（Advice）、织入横切逻辑等。在 `DynamicAdvisedInterceptor` 的 `intercept` 方法中，它会根据代理配置获取目标方法的拦截器链，并依次调用这些拦截器来实现 AOP 功能。

```java
@Override
@Nullable
public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    // 保存旧代理对象
    Object oldProxy = null; 
    // 是否设置了代理上下文
    boolean setProxyContext = false; 
    // 目标对象
    Object target = null; 
    // 获取目标源
    TargetSource targetSource = this.advised.getTargetSource(); 
    try {
       if (this.advised.exposeProxy) {
          // 如果配置中允许暴露代理对象，则将当前代理对象设置为Aop上下文的当前代理对象
          oldProxy = AopContext.setCurrentProxy(proxy);
          setProxyContext = true;
       }
       // 获取目标对象，尽可能晚地获取以最小化拥有目标的时间，以防它来自池...
       target = targetSource.getTarget();
       // 目标对象的类
       Class<?> targetClass = (target != null ? target.getClass() : null); 
       // 获取拦截器链
       List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass); 
       // 方法调用返回值
       Object retVal; 
       // 检查是否只有一个 InvokerInterceptor：即，没有真正的建议，而只是目标的反射调用。
       if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
          // 我们可以跳过创建一个 MethodInvocation：直接调用目标。
          // 请注意，最终调用者必须是一个 InvokerInterceptor，因此我们知道它只是对目标进行了反射操作，并且没有热交换或花哨的代理。
          Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args); 
          // 直接调用目标方法
          retVal = methodProxy.invoke(target, argsToUse); 
       }
       else {
          // 我们需要创建一个方法调用...
          // 创建方法调用并执行
          retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed(); 
       }
       // 处理返回类型
       retVal = processReturnType(proxy, target, method, retVal); 
       // 返回方法调用结果
       return retVal; 
    }
    finally {
       if (target != null && !targetSource.isStatic()) {
          // 如果目标对象不是静态的，则释放目标对象
          targetSource.releaseTarget(target); 
       }
       if (setProxyContext) {
          // 恢复旧代理对象。
          AopContext.setCurrentProxy(oldProxy); // 恢复Aop上下文的当前代理对象为旧代理对象
       }
    }
}
```

在`org.springframework.aop.framework.CglibAopProxy.CglibMethodInvocation#CglibMethodInvocation`方法中，`CglibMethodInvocation` 类的构造方法。它接收代理对象、目标对象、方法、方法参数、目标类、拦截器链和方法代理作为参数，并调用父类的构造方法进行初始化。在这个构造方法中，它会检查方法的修饰符是否为公共的且不是继承自 `java.lang.Object` 的方法，如果是，则将方法代理赋值给成员变量 `methodProxy`；否则将 `methodProxy` 设置为 `null`。

```java
public CglibMethodInvocation(Object proxy, @Nullable Object target, Method method,
				Object[] arguments, @Nullable Class<?> targetClass,
				List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {
    // 调用父类的构造方法，初始化方法调用
    super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);

    // 只对公共方法且不是继承自 java.lang.Object 的方法使用方法代理
    this.methodProxy = (Modifier.isPublic(method.getModifiers()) &&
            method.getDeclaringClass() != Object.class && !AopUtils.isEqualsMethod(method) &&
            !AopUtils.isHashCodeMethod(method) && !AopUtils.isToStringMethod(method) ?
            methodProxy : null); // 设置方法代理
}
```

在`org.springframework.aop.framework.CglibAopProxy.CglibMethodInvocation#proceed`方法中，用于执行方法调用链。它首先尝试调用父类的 `proceed()` 方法来执行方法调用链。如果在执行过程中捕获到运行时异常，则直接抛出该异常。

> 调用父类的 proceed 方法执行方法调用链，请参考org.springframework.aop.framework.ReflectiveMethodInvocation#proceed

```java
@Override
@Nullable
public Object proceed() throws Throwable {
    try {
        // 调用父类的 proceed 方法执行方法调用链
        return super.proceed(); 
    }
    catch (RuntimeException ex) { 
        throw ex;
    }
    catch (Exception ex) {
        if (ReflectionUtils.declaresException(getMethod(), ex.getClass()) ||
                KotlinDetector.isKotlinType(getMethod().getDeclaringClass())) {
            // Propagate original exception if declared on the target method
            // (with callers expecting it). Always propagate it for Kotlin code
            // since checked exceptions do not have to be explicitly declared there.
            throw ex;
        }
        else {
            // Checked exception thrown in the interceptor but not declared on the
            // target method signature -> apply an UndeclaredThrowableException,
            // aligned with standard JDK dynamic proxy behavior.
            throw new UndeclaredThrowableException(ex);
        }
    }
}
```

在`org.springframework.aop.framework.CglibAopProxy.CglibMethodInvocation#invokeJoinpoint`方法中，重写了 `ReflectiveMethodInvocation` 类的 `invokeJoinpoint()` 方法。`ReflectiveMethodInvocation` 是 Spring AOP 框架中的一个关键类，用于执行方法调用链。而在这个方法中，它通过检查是否存在方法代理来提升性能，避免了在调用公共方法时使用反射直接调用目标方法。而方法代理的具体实现是由 `MethodProxy` 类提供的。`MethodProxy` 是 CGLIB 库中的一个重要组件，它允许在运行时动态生成的代理类中，以高效的方式调用目标方法。

```java
/**
 * 在调用公共方法时，相比使用反射调用目标，使用此方法可以略微提升性能。
 */
@Override
protected Object invokeJoinpoint() throws Throwable {
     // 如果方法代理不为null
    if (this.methodProxy != null) {
         // 使用方法代理调用目标方法
        return this.methodProxy.invoke(this.target, this.arguments);
    }
    else {
        return super.invokeJoinpoint(); // 否则调用父类的invokeJoinpoint方法
    }
}
```

### 八、常见问题

1. **选择合适的代理方式**

   + 在使用 Spring AOP 时，需要根据目标对象是否实现接口来选择合适的代理方式（JDK 动态代理还是 CGLIB 动态代理）。如果目标对象实现了接口，则会使用 JDK 动态代理，否则会使用 CGLIB 动态代理。选择合适的代理方式可以影响到代理对象的性能和行为。

2. **代理对象的类型**

   + 生成的代理对象的类型可能与原始目标对象的类型不同。这可能会导致一些类型转换或 instanceof 判断出现问题。因此，在使用代理对象时，需要注意其类型和行为与原始对象可能存在的差异。