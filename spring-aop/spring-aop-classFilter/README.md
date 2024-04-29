## ClassFilter

- [ClassFilter](#classfilter)
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

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AnnotationClassFilter
class AspectJExpressionPointcut
class ClassFilter {
<<Interface>>

}
class RootClassFilter
class TypePatternClassFilter

AnnotationClassFilter  ..>  ClassFilter 
AspectJExpressionPointcut  ..>  ClassFilter 
RootClassFilter  ..>  ClassFilter 
TypePatternClassFilter  ..>  ClassFilter 

~~~



### 七、最佳实践

使用不同类型的类过滤器（AnnotationClassFilter、TypePatternClassFilter、RootClassFilter）以及基于 AspectJ 表达式的切点（AspectJExpressionPointcut）来匹配目标类，并输出匹配结果。

```java
public class ClassFilterDemo {
    public static void main(String[] args) {
        // 创建 AnnotationClassFilter 实例，匹配带有 MyAnnotation 注解的类
        ClassFilter annotationClassFilter = new AnnotationClassFilter(MyClassAnnotation.class);
        System.out.println("annotationClassFilter matches =" + annotationClassFilter.matches(MyService.class));

        // 创建 TypePatternClassFilter 实例，匹配指定类名的类
        ClassFilter typePatternClassFilter = new TypePatternClassFilter("com.xcs.spring.MyService");
        System.out.println("typePatternClassFilter matches =" + typePatternClassFilter.matches(MyService.class));

        // 创建 RootClassFilter 实例，匹配指定类的根类
        ClassFilter rootClassFilter = new RootClassFilter(MyService.class);
        System.out.println("rootClassFilter matches = " + rootClassFilter.matches(MySubService.class));

        // 创建 AspectJExpressionPointcut 实例，根据 AspectJ 表达式匹配类和方法
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("aspectJExpressionPointcut matches = " + aspectJExpressionPointcut.matches(MyService.class));
    }
}
```

`MyService` 类被 `@MyClassAnnotation` 注解修饰。

```java
@MyClassAnnotation
public class MyService {
}
```

`MyClassAnnotation` 注解，应用于类级别的元素。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyClassAnnotation {
}
```

运行结果，四种不同类型的类过滤器都成功地匹配了相应的目标类。

```java
annotationClassFilter matches =true
typePatternClassFilter matches =true
rootClassFilter matches = true
aspectJExpressionPointcut matches = true
```
