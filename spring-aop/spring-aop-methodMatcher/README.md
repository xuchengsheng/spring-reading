## MethodMatcher

- [MethodMatcher](#methodmatcher)
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

`MethodMatcher` 接口是Spring AOP中的一个关键接口，用于判断一个给定的方法是否匹配指定的切点表达式。它定义了方法匹配的规则和逻辑，我们可以通过实现这个接口来自定义方法匹配的行为，从而实现针对特定方法的切面逻辑的拦截和执行。

### 三、主要功能

1. **方法匹配** 

   + 判断一个给定的方法是否符合指定的切点表达式，即确定是否应该对该方法进行拦截和应用额外的逻辑。

2. **静态匹配** 

   + 可以静态地匹配方法，这意味着方法的匹配逻辑可以在编译时确定，并且在整个应用程序的生命周期内保持不变。

3. **动态匹配** 

   + 有些切点需要在运行时根据方法的参数或其他条件来动态确定匹配与否，`MethodMatcher` 接口也支持这种动态匹配的能力。

4. **运行时效率**

   +  `MethodMatcher` 的实现应该具有高效率，尤其是在动态匹配的情况下，以避免对应用程序性能造成过大的负担。

5. **可扩展性** 

   + `MethodMatcher` 接口的设计应该具有良好的扩展性，我们可以根据实际需求自定义方法匹配的规则和逻辑，以满足不同的业务场景和需求。

### 四、接口源码

`MethodMatcher` 接口用于检查目标方法是否符合通知的条件。它支持静态匹配和动态匹配两种方式，静态匹配在编译时确定，而动态匹配在运行时根据方法参数和先前通知的执行情况进行判断。

```java
/**
 * {@link Pointcut}的一部分：检查目标方法是否符合通知的条件。
 *
 * <p>MethodMatcher 可以<b>静态</b>或<b>动态</b>地评估。
 * 静态匹配涉及方法和（可能的）方法属性。
 * 动态匹配还可以使调用的参数可用，并且可以考虑到之前应用于连接点的先前通知的任何效果。
 *
 * <p>如果实现从其{@link #isRuntime()}方法返回{@code false}，则可以静态地执行评估，
 * 并且对于此方法的所有调用，无论其参数如何，结果都将相同。
 * 这意味着如果{@link #isRuntime()}方法返回{@code false}，则永远不会调用 3-arg
 * {@link #matches(java.lang.reflect.Method, Class, Object[])} 方法。
 *
 * <p>如果实现从其 2-arg {@link #matches(java.lang.reflect.Method, Class)} 方法返回{@code true}，
 * 并且其{@link #isRuntime()}方法返回{@code true}，则将在<i>每次相关通知的潜在执行之前</i>
 * 调用 3-arg {@link #matches(java.lang.reflect.Method, Class, Object[])} 方法，
 * 以决定是否应该运行通知。
 * 所有先前的通知，例如拦截器链中的较早拦截器，都将已运行，因此在评估时将可用参数或ThreadLocal状态的任何状态更改。
 *
 * <p>此接口的具体实现通常应提供{@link Object#equals(Object)}和{@link Object#hashCode()}的正确实现，
 * 以便允许在缓存方案中使用匹配器 - 例如，由CGLIB生成的代理。
 *
 * @author Rod Johnson
 * @since 11.11.2003
 * @see Pointcut
 * @see ClassFilter
 */
public interface MethodMatcher {

	/**
	 * 执行静态检查，确定给定的方法是否匹配。
	 * <p>如果此方法返回{@code false}，或者{@link #isRuntime()}方法返回{@code false}，
	 * 则不会进行运行时检查（即不会调用 {@link #matches(java.lang.reflect.Method, Class, Object[])} 方法）。
	 * @param method 候选方法
	 * @param targetClass 目标类
	 * @return 此方法是否静态匹配
	 */
	boolean matches(Method method, Class<?> targetClass);

	/**
	 * 此 MethodMatcher 是否是动态的，也就是说，即使 2-arg matches 方法返回 {@code true}，
	 * 在运行时是否必须对 {@link #matches(java.lang.reflect.Method, Class, Object[])} 方法进行最终调用？
	 * <p>可以在创建AOP代理时调用，不需要在每次方法调用之前再次调用。
	 * @return 是否需要运行时匹配
	 */
	boolean isRuntime();

	/**
	 * 检查此方法是否存在运行时（动态）匹配，此匹配必须已经通过静态匹配。
	 * <p>仅在给定方法和目标类的 2-arg matches 方法返回{@code true}，
	 * 并且 {@link #isRuntime()} 方法返回{@code true} 时才会调用此方法。
	 * 在潜在运行通知之前立即调用，之前的通知链中的所有通知已运行。
	 * @param method 候选方法
	 * @param targetClass 目标类
	 * @param args 方法的参数
	 * @return 是否存在运行时匹配
	 * @see MethodMatcher#matches(Method, Class)
	 */
	boolean matches(Method method, Class<?> targetClass, Object... args);

	/**
	 * 匹配所有方法的规范实例。
	 */
	MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;

}
```

### 五、主要实现

1. **AnnotationMethodMatcher** 

   + 这个类是用于匹配带有特定注解的方法的方法匹配器。它可以用来创建切点，以便对带有特定注解的方法进行拦截和增强。

2. **ControlFlowPointcut** 

   + 控制流切点用于定义在特定的方法调用链中触发通知的位置。它允许我们指定只有在控制流程满足某些条件时才触发通知。

3. **JdkRegexpMethodPointcut** 

   + 这个类使用基于正则表达式的方法匹配来创建切点。它允许我们根据方法的名称来定义匹配规则，从而决定哪些方法应该被拦截。

4. **NameMatchMethodPointcut** 

   + 这个类是基于方法名称的匹配器，它允许我们根据方法的名称模式来定义切点。只要方法名称匹配指定的模式，就可以触发通知。

5. **AspectJExpressionPointcut** 

   + 这个类使用 AspectJ 表达式语言来创建切点，它允许我们使用更加灵活和强大的语法来定义切点。AspectJ 表达式支持更多的特性，包括访问方法参数、异常类型等。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AbstractRegexpMethodPointcut
class AnnotationMethodMatcher
class AspectJExpressionPointcut
class ControlFlowPointcut
class IntroductionAwareMethodMatcher {
<<Interface>>

}
class JdkRegexpMethodPointcut
class MethodMatcher {
<<Interface>>

}
class NameMatchMethodPointcut
class StaticMethodMatcher
class StaticMethodMatcherPointcut

AbstractRegexpMethodPointcut  -->  StaticMethodMatcherPointcut 
AnnotationMethodMatcher  -->  StaticMethodMatcher 
AspectJExpressionPointcut  ..>  IntroductionAwareMethodMatcher 
ControlFlowPointcut  ..>  MethodMatcher 
IntroductionAwareMethodMatcher  -->  MethodMatcher 
JdkRegexpMethodPointcut  -->  AbstractRegexpMethodPointcut 
NameMatchMethodPointcut  -->  StaticMethodMatcherPointcut 
StaticMethodMatcher  ..>  MethodMatcher 
StaticMethodMatcherPointcut  -->  StaticMethodMatcher 
~~~

### 七、最佳实践

获取了名为 "setName" 的方法，并使用四种不同类型的方法匹配器对其进行匹配检查。其中，AnnotationMethodMatcher 检查该方法是否具有特定注解，AspectJExpressionPointcut 基于 AspectJ 表达式匹配方法，NameMatchMethodPointcut 基于方法名称匹配方法，JdkRegexpMethodPointcut 基于正则表达式匹配方法。最后，程序输出了每种匹配器的匹配结果。

```java
public class MethodMatcherDemo {

    public static void main(String[] args) throws Exception {
        Class<MyService> target = MyService.class;
        Method setNameMethod = target.getDeclaredMethod("setName");

        // 使用 AnnotationMethodMatcher 检查是否具有特定注解
        AnnotationMethodMatcher annotationMethodMatcher = new AnnotationMethodMatcher(MyMethodAnnotation.class);
        System.out.println("annotationMethodMatcher matches = " + annotationMethodMatcher.matches(setNameMethod, target));

        // 使用 AspectJExpressionPointcut 基于 AspectJ 表达式匹配方法
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("aspectJExpressionPointcut matches = " + aspectJExpressionPointcut.matches(setNameMethod, target));

        // 使用 NameMatchMethodPointcut 基于方法名称匹配方法
        NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
        nameMatchMethodPointcut.setMappedName("setName");
        System.out.println("nameMatchMethodPointcut matches = " + nameMatchMethodPointcut.matches(setNameMethod, target));

        // 使用 JdkRegexpMethodPointcut 基于正则表达式匹配方法
        JdkRegexpMethodPointcut jdkRegexpMethodPointcut = new JdkRegexpMethodPointcut();
        jdkRegexpMethodPointcut.setPattern(".*set.*");
        System.out.println("jdkRegexpMethodPointcut matches = " + jdkRegexpMethodPointcut.matches(setNameMethod, target));
    }
}
```

`MyService` 类中的 `setName` 方法被 `@MyMethodAnnotation` 注解修饰，表示该方法具有特定的自定义注解。

```java
public class MyService {

    @MyMethodAnnotation
    public void setName() {
        System.out.println("setName...");
    }
}
```

`MyMethodAnnotation` 是一个自定义注解，该注解可以应用于方法上。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyMethodAnnotation {
}
```

运行结果，对于目标类中的 "setName" 方法，无论是基于注解、AspectJ 表达式、方法名称还是正则表达式的匹配器，都返回了 true，即这些匹配器都成功匹配了该方法。

```java
annotationMethodMatcher matches = true
aspectJExpressionPointcut matches = true
nameMatchMethodPointcut matches = true
jdkRegexpMethodPointcut matches = true
```
