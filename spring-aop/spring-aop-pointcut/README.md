## Pointcut

- [Pointcut](#pointcut)
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

`Pointcut` 接口主要用于定义切入点，即确定哪些方法应该被切面所影响。Pointcut 接口提供了匹配规则，以确定在哪些类的哪些方法上应用切面，以及在何种情况下应该应用切面。

### 三、主要功能

1. **定义切入点**

   + Pointcut 接口用于定义切入点，即确定哪些方法应该被切面所影响。它允许我们指定在哪些类的哪些方法上应用切面。

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

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AbstractExpressionPointcut
class AbstractRegexpMethodPointcut
class AnnotationMatchingPointcut
class AspectJExpressionPointcut
class DynamicMethodMatcherPointcut
class ExpressionPointcut {
<<Interface>>

}
class JdkRegexpMethodPointcut
class NameMatchMethodPointcut
class Pointcut {
<<Interface>>

}
class StaticMethodMatcherPointcut
class TruePointcut

AbstractExpressionPointcut  ..>  ExpressionPointcut 
AbstractRegexpMethodPointcut  -->  StaticMethodMatcherPointcut 
AnnotationMatchingPointcut  ..>  Pointcut 
AspectJExpressionPointcut  -->  AbstractExpressionPointcut 
DynamicMethodMatcherPointcut  ..>  Pointcut 
ExpressionPointcut  -->  Pointcut 
JdkRegexpMethodPointcut  -->  AbstractRegexpMethodPointcut 
NameMatchMethodPointcut  -->  StaticMethodMatcherPointcut 
StaticMethodMatcherPointcut  ..>  Pointcut 
TruePointcut  ..>  Pointcut 
~~~

### 七、最佳实践

**MyCustomPointcut**

使用自定义的 `Pointcut` 对象 `MyCustomPointcut`。在 `customPointcut` 方法中，我们创建了 `MyCustomPointcut` 的实例，并通过 `showMatchesLog` 方法展示了其对类和方法的匹配情况。最后，我们通过调用 `showMatchesLog` 方法来检查 `MyCustomPointcut` 对象对目标类 `MyService` 中的方法的匹配情况，并输出匹配结果。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        customPointcut();
    }

    /**
     * 自定义 Pointcut 
     */
    private static void customPointcut() {
        MyCustomPointcut pointcut = new MyCustomPointcut();
        showMatchesLog(pointcut);
    }
    
    public static void showMatchesLog(Pointcut pointcut) {
        try {
            Class<MyService> target = MyService.class;
            Method getNameMethod = target.getDeclaredMethod("getName");
            Method getAgeMethod = target.getDeclaredMethod("getAge");
            Method setNameMethod = target.getDeclaredMethod("setName");

            ClassFilter classFilter = pointcut.getClassFilter();
            MethodMatcher methodMatcher = pointcut.getMethodMatcher();

            System.out.println("ClassFilter MyService = " + classFilter.matches(target));
            System.out.println("MethodMatcher MyService getName = " + methodMatcher.matches(getNameMethod, target));
            System.out.println("MethodMatcher MyService getAge = " + methodMatcher.matches(getAgeMethod, target));
            System.out.println("MethodMatcher MyService setName = " + methodMatcher.matches(setNameMethod, target));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

`MyService` 类是一个示例服务类，标注了类级别的 `@MyClassAnnotation` 注解，其中包含了三个方法：`getName()`、`setName()` 和 `getAge()`。其中，`setName()` 方法标注了方法级别的 `@MyMethodAnnotation` 注解。

```java
@MyClassAnnotation
public class MyService {

    public void getName() {
        System.out.println("getName...");
    }

    @MyMethodAnnotation
    public void setName() {
        System.out.println("setName...");
    }

    public void getAge() {
        System.out.println("getAge...");
    }
}
```

运行结果，`MyService` 类级别的过滤器匹配成功，而在方法级别，`getName` 和 `getAge` 方法成功匹配，但 `setName` 方法未匹配成功。

```java
ClassFilter MyService = true
MethodMatcher MyService getName = true
MethodMatcher MyService getAge = true
MethodMatcher MyService setName = false
```

**AspectJExpressionPointcut**

使用 `AspectJExpressionPointcut` 创建一个基于 AspectJ 表达式的切入点。在 `aspectJExpressionPointcut` 方法中，我们创建了 `AspectJExpressionPointcut` 的实例，并设置了 AspectJ 表达式 `"execution(* com.xcs.spring.MyService.get*())"`，该表达式匹配了 `com.xcs.spring.MyService` 类中以 `get` 开头的所有方法。最后，我们通过调用 `showMatchesLog` 方法来检查 `AspectJExpressionPointcut` 对象对指定类中的方法的匹配情况，并输出匹配结果。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        aspectJExpressionPointcut();
    }

     /**
     * AspectJExpressionPointcut
     */
    private static void aspectJExpressionPointcut() {
        // 创建 AspectJ 表达式切入点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.xcs.spring.MyService.get*())");
        showMatchesLog(pointcut);
    }
}
```

运行结果，`MyService` 类级别的过滤器匹配成功，而在方法级别，`getName` 和 `getAge` 方法成功匹配，但 `setName` 方法未匹配成功。

```java
ClassFilter MyService = true
MethodMatcher MyService getName = true
MethodMatcher MyService getAge = true
MethodMatcher MyService setName = false
```

**AnnotationMatchingPointcut**

使用 `AnnotationMatchingPointcut` 创建一个基于注解匹配的切入点。在 `annotationMatchingPointcut` 方法中，我们创建了 `AnnotationMatchingPointcut` 的实例，并指定了类级别注解 `MyClassAnnotation` 和方法级别注解 `MyMethodAnnotation`，同时设置了不检查继承的方法。最后，我们通过调用 `showMatchesLog` 方法来检查 `AnnotationMatchingPointcut` 对象对指定类中的方法的匹配情况，并输出匹配结果。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        annotationMatchingPointcut();
    }

    /**
     * AnnotationMatchingPointcut
     */
    private static void annotationMatchingPointcut() {
        // 使用AnnotationMatchingPointcut切入点
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(MyClassAnnotation.class, MyMethodAnnotation.class, false);
        showMatchesLog(pointcut);
    }
}
```

运行结果，`MyService` 类级别的过滤器匹配成功，而方法级别的匹配器成功匹配了 `setName` 方法，但未匹配 `getName` 和 `getAge` 方法。

```java
ClassFilter MyService = true
MethodMatcher MyService getName = false
MethodMatcher MyService getAge = false
MethodMatcher MyService setName = true
```

**NameMatchMethodPointcut**

使用 `NameMatchMethodPointcut` 创建一个基于方法名匹配的切入点。在 `nameMatchMethodPointcut` 方法中，我们创建了 `NameMatchMethodPointcut` 的实例，并添加了要匹配的方法名 `getAge`。然后，我们通过调用 `showMatchesLog` 方法来检查 `NameMatchMethodPointcut` 对象对指定类中的方法的匹配情况，并输出匹配结果。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        nameMatchMethodPointcut();
    }

    /**
     * AspectJExpressionPointcut
     */
    private static void nameMatchMethodPointcut() {
        // 使用AnnotationMatchingPointcut切入点
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.addMethodName("getAge");
        showMatchesLog(pointcut);
    }
}
```

运行结果， `MyService` 类级别的过滤器匹配成功，而方法级别的匹配器成功匹配了 `getAge` 方法，但未匹配 `getName` 和 `setName` 方法。

```java
ClassFilter MyService = true
MethodMatcher MyService getName = false
MethodMatcher MyService getAge = true
MethodMatcher MyService setName = false
```

**JdkRegexpMethodPointcut**

使用 `JdkRegexpMethodPointcut` 创建一个基于 JDK 正则表达式匹配的切入点。在 `jdkRegexpMethodPointcut` 方法中，我们创建了 `JdkRegexpMethodPointcut` 的实例，并设置了正则表达式模式 `".*set.*"`，该模式匹配了所有包含 "set" 字符串的方法名。然后，我们通过调用 `showMatchesLog` 方法来检查 `JdkRegexpMethodPointcut` 对象对指定类中的方法的匹配情况，并输出匹配结果。

```java
public class PointcutDemo {
    public static void main(String[] args) {
        jdkRegexpMethodPointcut();
    }

    /**
     * JdkRegexpMethodPointcut
     */
    private static void jdkRegexpMethodPointcut() {
        JdkRegexpMethodPointcut pointcut = new JdkRegexpMethodPointcut();
        pointcut.setPattern(".*set.*");
        showMatchesLog(pointcut);
    }
}
```

运行结果，`MyService` 类级别的过滤器匹配成功，而方法级别的匹配器成功匹配了 `setName` 方法，但未匹配 `getName` 和 `getAge` 方法。

```java
ClassFilter MyService = true
MethodMatcher MyService getName = false
MethodMatcher MyService getAge = false
MethodMatcher MyService setName = true
```
