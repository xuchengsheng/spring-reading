## ClassFilter

- [ClassFilter](#classfilter)
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

`ClassFilter` 接口是 Spring AOP 框架中的一个关键组件，用于定义切面（Aspect）应该拦截哪些类的规则。允许我们根据具体的条件来判断传入的类是否应该被拦截。通过实现该接口，可以灵活地定义过滤器，以匹配特定的类或者类的集合，从而精确地控制切面的作用范围。

### 三、主要功能

1. **指定切面拦截的类**

   + 允许我们定义规则，确定哪些类应该被应用切面。通过实现 `matches(Class<?> clazz)` 方法，可以根据特定的条件来判断传入的类是否应该被拦截。

2. **过滤器功能**

   + 作为过滤器模式的一种应用，`ClassFilter` 接口允许我们定义过滤器，以匹配特定的类或者类的集合。这样可以灵活地控制切面的作用范围，只针对符合条件的类应用切面逻辑。

3. **精确定义切面作用范围**

   + 通过 `ClassFilter` 接口，可以实现非常灵活的切面选择逻辑，例如只拦截某个特定包下的类、只拦截实现了某个接口的类等，从而精确地定义切面的作用范围。

### 四、接口源码

`ClassFilter` 接口是一个过滤器，用于限制某个切点或引入的匹配范围到一组指定的目标类。通过实现 `matches(Class<?> clazz)` 方法，可以确定切面是否应该应用到给定的目标类上。

```java
/**
 * 过滤器，用于限制一个切点或引入的匹配到一组给定的目标类。
 *
 * <p>可以作为 {@link Pointcut} 的一部分或者用于整个 {@link IntroductionAdvisor} 的定位。
 *
 * <p>这个接口的具体实现通常应该提供 {@link Object#equals(Object)} 和 {@link Object#hashCode()} 的适当实现，
 * 以便允许在缓存场景中使用过滤器，例如，在 CGLIB 生成的代理中。
 *
 * @author Rod Johnson
 * @see Pointcut
 * @see MethodMatcher
 */
@FunctionalInterface
public interface ClassFilter {

	/**
	 * 是否应该应用到给定的接口或目标类？
	 * @param clazz 候选目标类
	 * @return 是否应该将通知应用到给定的目标类
	 */	
	boolean matches(Class<?> clazz);


	/**
	 * 匹配所有类的 ClassFilter 的规范实例。
	 */
	ClassFilter TRUE = TrueClassFilter.INSTANCE;

}

```

### 五、主要实现

1. **AnnotationClassFilter**

   - 根据注解匹配类的过滤器，用于选取带有指定注解的类。

2. **TypePatternClassFilter** 

   + 根据类型模式匹配类的过滤器，用于匹配满足指定类型模式的类。

3. **RootClassFilter** 

   + 匹配指定类的根类的过滤器。

4. **AspectJExpressionPointcut**

   + 主要用于基于 AspectJ 表达式匹配目标类。

### 六、最佳实践

使用不同类型的类过滤器在 Spring AOP 中的使用方式。我们创建了四种不同的类过滤器实例，并测试它们是否匹配了特定的类。通过打印输出结果，展示了每个类过滤器的匹配情况，从而说明了它们在过滤目标类方面的作用。

```java
public class ClassFilterDemo {
    public static void main(String[] args) {
        // 创建 AnnotationClassFilter 实例，匹配带有 MyAnnotation 注解的类
        ClassFilter filter1 = new AnnotationClassFilter(MyAnnotation.class);
        System.out.println("AnnotationClassFilter 是否匹配 MyService 类：" + filter1.matches(MyService.class));

        // 创建 TypePatternClassFilter 实例，匹配指定类名的类
        ClassFilter filter2 = new TypePatternClassFilter("com.xcs.spring.MyService");
        System.out.println("TypePatternClassFilter 是否匹配 MyService 类：" + filter2.matches(MyService.class));

        // 创建 RootClassFilter 实例，匹配指定类的根类
        ClassFilter filter3 = new RootClassFilter(MyService.class);
        System.out.println("RootClassFilter 是否匹配 MySubService 的根类：" + filter3.matches(MySubService.class));

        // 创建 AspectJExpressionPointcut 实例，根据 AspectJ 表达式匹配类和方法
        AspectJExpressionPointcut filter4 = new AspectJExpressionPointcut();
        filter4.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("AspectJExpressionPointcut 是否匹配 MyService 类：" + filter4.matches(MyService.class));
    }
}
```

运行结果，四种不同类型的类过滤器都成功地匹配了相应的目标类。

```java
AnnotationClassFilter 是否匹配 MyService 类：true
TypePatternClassFilter 是否匹配 MyService 类：true
RootClassFilter 是否匹配 MySubService 的根类：true
AspectJExpressionPointcut 是否匹配 MyService 类：true
```

### 七、源码分析

暂无

### 八、常见问题

1. **匹配准确性**

   + 可能会出现由于匹配规则不准确导致无法正确匹配目标类的情况。在使用 `ClassFilter` 时，需要确保定义的匹配规则能够准确地选择出目标类，否则可能会导致切面不正确地应用或不应用于预期的类。

2. **匹配范围不一致**
+ 在定义 `ClassFilter` 时，可能会出现匹配范围不一致的情况。例如，一个切面匹配了目标类的所有方法，而另一个切面只匹配了部分方法。在这种情况下，可能会出现不一致的行为，需要确保所有切面的匹配范围是一致的。