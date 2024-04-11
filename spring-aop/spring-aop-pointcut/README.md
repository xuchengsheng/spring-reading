## Pointcut

- [Pointcut](#pointcut)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、接口源码](#四接口源码)
  - [五、主要实现](#五主要实现)
  - [六、最佳实践](#六最佳实践)
    - [自定义Pointcut](#自定义pointcut)
    - [AspectJExpressionPointcut](#aspectjexpressionpointcut)
    - [AnnotationMatchingPointcut](#annotationmatchingpointcut)
    - [NameMatchMethodPointcut](#namematchmethodpointcut)
  - [七、源码分析](#七源码分析)
  - [八、常见问题](#八常见问题)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`Pointcut` 接口主要用于定义切入点，即确定哪些方法应该被切面所影响。Pointcut 接口提供了匹配规则，以确定在哪些类的哪些方法上应用切面，以及在何种情况下应该应用切面。

### 三、主要功能

1. **定义切入点**

   + Pointcut 接口用于定义切入点，即确定哪些方法应该被切面所影响。它允许开发人员指定在哪些类的哪些方法上应用切面。

2. **匹配规则** 

   + 提供了匹配规则，以确定在哪些方法上应用切面。这些规则可以基于方法的名称、参数、返回类型、类名称等多种条件来定义，从而实现对切入点的精确定位。

3. **获取类过滤器**

   + `getClassFilter()` 方法用于获取一个 `ClassFilter` 对象，该对象用于确定哪些类应该被匹配。我们可以根据自己的需求自定义类过滤逻辑。

4. **获取方法匹配器**

   + `getMethodMatcher()` 方法用于获取一个 `MethodMatcher` 对象，该对象用于确定哪些方法应该被匹配。我们可以根据自己的需求自定义方法匹配逻辑。

### 四、接口源码

`Pointcut`接口定义了 Spring AOP 中的切入点的核心抽象，由 `ClassFilter` 和 `MethodMatcher` 组成，分别用于确定哪些类和方法应该被匹配。通过这个接口，可以创建不同的切入点，并灵活地组合它们来定义复杂的切面。接口中还定义了一个常量 `TRUE`，代表始终匹配的切入点。

```java
/**
 * 核心的 Spring 切入点抽象。
 *
 * <p>一个切入点由一个 {@link ClassFilter} 和一个 {@link MethodMatcher} 组成。
 * 这两个基本术语以及一个切入点本身可以组合起来构建组合（例如通过 {@link org.springframework.aop.support.ComposablePointcut}）。
 *
 * @author Rod Johnson
 * @see ClassFilter
 * @see MethodMatcher
 * @see org.springframework.aop.support.Pointcuts
 * @see org.springframework.aop.support.ClassFilters
 * @see org.springframework.aop.support.MethodMatchers
 */
public interface Pointcut {

	/**
	 * 返回此切入点的 ClassFilter。
	 * @return ClassFilter（永不为 {@code null}）
	 */
	ClassFilter getClassFilter();

	/**
	 * 返回此切入点的 MethodMatcher。
	 * @return MethodMatcher（永不为 {@code null}）
	 */
	MethodMatcher getMethodMatcher();


	/**
	 * 始终匹配的规范切入点实例。
	 */
	Pointcut TRUE = TruePointcut.INSTANCE;

}
```

### 五、主要实现

1. **NameMatchMethodPointcut** 

   + 根据方法名称匹配的切入点。可以配置指定的方法名称或通配符，以匹配目标类中的方法。

2. **JdkRegexpMethodPointcut** 

   + 使用正则表达式匹配方法的切入点。可以使用正则表达式指定方法的匹配规则。

3. **AspectJExpressionPointcut** 

   + 使用 AspectJ 切入点表达式匹配方法的切入点。可以使用 AspectJ 的语法来定义更灵活的切入点匹配规则。

4. **ComposablePointcut** 

   + 可组合的切入点，允许将多个切入点组合起来使用，支持与、或、非等逻辑操作。

5. **StaticMethodMatcherPointcut** 

   + 静态方法匹配器切入点，用于直接指定方法匹配规则，不支持动态匹配。

6. **TruePointcut** 

   + 始终匹配的切入点，代表不进行任何匹配，即匹配所有的类和方法。

7. **AnnotationMatchingPointcut**

   + 用于基于注解匹配的切入点定义。它可以根据指定的注解类型匹配类或方法，并用于将通知应用于带有特定注解的目标对象的方法。

### 六、最佳实践

#### 自定义Pointcut

使用 Spring AOP 创建代理对象，并应用自定义的切入点和通知来拦截目标方法的调用。首先，通过 `ProxyFactory` 创建了一个代理工厂，然后使用 `addAdvisor` 方法添加了一个切面，其中包含了自定义的切入点和通知。接着，通过代理工厂的 `getProxy` 方法获取代理对象。最后，使用代理对象调用方法。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        customPointcut();
    }

    /**
     * 自定义 Pointcut 最佳实践
     */
    private static void customPointcut() {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyBean());
        // 添加切面：使用自定义的切入点和通知构建默认切面
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new MyCustomPointcut(), new MyCustomAdvice()));
        // 获取代理对象
        MyBean myBean = (MyBean) proxyFactory.getProxy();

        // 使用代理对象调用方法
        myBean.getName(); // 将被通知拦截
        myBean.getAge(); // 将被通知拦截
        myBean.setName(); // 不会被通知拦截
    }
}
```

自定义了一个自定义的切入点 `MyCustomPointcut`，该切入点匹配所有类，并且匹配所有方法名以 "get" 开头的方法。这意味着通过该切入点定义的切面将会拦截所有类的所有以 "get" 开头的方法调用。

```java
class MyCustomPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        // 匹配所有类
        return clazz -> true;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MethodMatcher() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // 匹配所有以 "get" 开头的方法
                return method.getName().startsWith("get");
            }

            @Override
            public boolean isRuntime() {
                // 是否需要在运行时动态匹配
                return false;
            }

            @Override
            public boolean matches(Method method, Class<?> targetClass, Object... args) {
                // 运行时匹配，这里不需要，所以简单返回 false
                return false;
            }
        };
    }
}
```

自定义的通知 `MyCustomAdvice`，它实现了 `MethodBeforeAdvice` 接口，因此是一个前置通知，用于在目标方法执行之前执行额外的逻辑。在 `before` 方法中，它输出一条日志信息 "Before advice is executed"，表示在目标方法执行前执行了该通知逻辑。

```java
class MyCustomAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before advice is executed");
    }
}
```

定义了一个简单的 Java 类 `MyBean`，其中包含了三个方法`getName()`、`setName()` 和 `getAge()`。

```java
public class MyBean {
    public void getName() {
        System.out.println("getName() method");
    }

    public void setName() {
        System.out.println("setName() method");
    }

    public void getAge() {
        System.out.println("getAge() method");
    }
}
```

#### AspectJExpressionPointcut

使用 `AspectJExpressionPointcut` 实现一个简单的切入点定义。在 `aspectJExpressionPointcut` 方法中，我们创建了一个 `AspectJExpressionPointcut` 对象，并设置了 AspectJ 表达式，该表达式匹配了所有类中的 `getName()` 方法。然后，我们将切入点与通知关联，并将其作为切面添加到代理工厂中。最后，我们使用代理对象调用了几个方法，根据切入点的定义，只有匹配到的方法会被通知拦截。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        aspectJExpressionPointcut();
    }

    /**
     * AspectJExpressionPointcut最佳实践
     */
    private static void aspectJExpressionPointcut() {
        // 创建 AspectJ 表达式切入点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* *.getName())");

        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyBean());
        // 添加切面：使用自定义的切入点和通知构建默认切面
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(pointcut, new MyCustomAdvice()));
        // 获取代理对象
        MyBean myBean = (MyBean) proxyFactory.getProxy();

        // 使用代理对象调用方法
        myBean.getName(); // 将被通知拦截
        myBean.getAge(); // 不会被通知拦截
        myBean.setName(); // 不会被通知拦截
    }
}
```

#### AnnotationMatchingPointcut

使用 `AnnotationMatchingPointcut` 实现一个简单的切入点定义。在 `annotationMatchingPointcut` 方法中，我们创建了一个 `AnnotationMatchingPointcut` 对象，并指定了要匹配的注解类型 `MyAnnotation`，以及是否检查继承的方法。然后，我们将切入点与通知关联，并将其作为切面添加到代理工厂中。最后，我们使用代理对象调用了几个方法，根据切入点的定义，所有使用了 `MyAnnotation` 注解的方法都会被通知拦截。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        annotationMatchingPointcut();
    }

    /**
     * AnnotationMatchingPointcut 最佳实践
     */
    private static void annotationMatchingPointcut() {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyBean());
        // 添加切面：使用AnnotationMatchingPointcut切入点和通知构建默认切面
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(new AnnotationMatchingPointcut(MyAnnotation.class, false), new MyCustomAdvice()));
        // 获取代理对象
        MyBean myBean = (MyBean) proxyFactory.getProxy();

        // 使用代理对象调用方法
        myBean.getName(); // 将被通知拦截
        myBean.getAge(); // 将被通知拦截
        myBean.setName(); // 将被通知拦截
    }
}
```

#### NameMatchMethodPointcut

使用 `NameMatchMethodPointcut` 实现一个简单的切入点定义。在 `nameMatchMethodPointcut` 方法中，我们创建了一个 `NameMatchMethodPointcut` 对象，并添加了要匹配的方法名 `getAge`。然后，我们将切入点与通知关联，并将其作为切面添加到代理工厂中。最后，我们使用代理对象调用了几个方法，根据切入点的定义，只有匹配到的方法会被通知拦截。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        nameMatchMethodPointcut();
    }

    /**
     * AspectJExpressionPointcut最佳实践
     */
    private static void nameMatchMethodPointcut() {
        // 创建方法名匹配切入点
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.addMethodName("getAge");

        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory(new MyBean());
        // 添加切面：使用自定义的切入点和通知构建默认切面
        proxyFactory.addAdvisor(new DefaultPointcutAdvisor(pointcut, new MyCustomAdvice()));
        // 获取代理对象
        MyBean myBean = (MyBean) proxyFactory.getProxy();

        // 使用代理对象调用方法
        myBean.getName(); // 不会被通知拦截
        myBean.getAge(); // 将被通知拦截
        myBean.setName(); // 不会被通知拦截
    }
}
```

### 七、源码分析

暂无

### 八、常见问题

1. **切入点表达式定义错误** 

   + 使用 AspectJ 表达式时，可能会由于表达式定义错误导致切入点匹配失败。例如，表达式写错了、漏掉了必要的切入点信息等。

2. **匹配不到目标方法** 

   + 定义的切入点可能无法匹配到目标方法，导致通知无法正确地应用。这可能是由于切入点的匹配规则不正确，或者目标方法的特征与切入点不匹配等原因造成的。

3. **切入点过于宽泛** 

   + 切入点定义过于宽泛，导致匹配到了不必要的方法，使得通知影响范围过大。这可能会导致性能问题或意外的行为。

4. **切入点过于狭窄** 

   + 切入点定义过于狭窄，导致无法匹配到预期的目标方法，使得通知无法正确应用。这可能会导致切面无法达到预期的效果。

5. **运行时动态匹配问题**

   +  如果使用了运行时动态匹配的切入点，可能会由于动态条件的设置不正确或者动态条件的结果不符合预期等原因导致匹配失败。

6. **与其他切面冲突** 

   + 如果多个切面定义了相互冲突的切入点，可能会导致切面的顺序问题或者切面之间的冲突，使得通知的执行顺序出现问题或者切面功能失效。

7. **性能问题** 

   + 如果切入点定义过于宽泛或者运行时动态匹配过于频繁，可能会导致性能问题，影响应用程序的性能表现。