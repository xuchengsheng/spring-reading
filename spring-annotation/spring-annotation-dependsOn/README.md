## @DependsOn

### 一、注解描述

`@DependsOn`注解，用于定义 Bean 初始化顺序。有时，你可能会碰到某些 Bean 需要在其他 Bean 之前被初始化的情况。在这种情况下，我们可以使用 `@DependsOn` 注解来明确指定 Bean 的初始化顺序。

### 二、注解源码

```java
/**
 * 当前bean所依赖的其他bean。任何指定的bean都保证在这个bean之前被容器创建。
 * 在少数情况下使用，当一个bean不通过属性或构造函数参数明确地依赖于另一个bean，
 * 而是依赖于另一个bean的初始化的副作用时。
 *
 * depends-on 声明既可以指定初始化时的依赖，又可以在单例bean的情况下，指定对应的销毁时的依赖。
 * 定义了 depends-on 关系的依赖bean会首先被销毁，然后再销毁给定的bean。
 * 因此，depends-on 声明也可以控制关闭顺序。
 *
 * 可以在直接或间接使用 org.springframework.stereotype.Component 注解的任何类上，
 * 或在使用 Bean 注解的方法上使用。
 *
 * 在类级别使用 DependsOn 在未使用组件扫描的情况下不会产生任何效果。
 * 如果通过XML声明了使用 DependsOn 注解的类，DependsOn 注解的元数据会被忽略，
 * 而 <bean depends-on="..."/> 会被考虑。
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DependsOn {

    // 定义当前bean所依赖的其他bean的名称。
	String[] value() default {};

}
```

### 三、主要功能

### 四、最佳实践

### 五、时序图

### 六、源码分析

### 七、注意事项

### 八、总结

#### 8.1、最佳实践总结

#### 8.2、源码分析总结