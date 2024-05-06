## @EnableAspectJAutoProxy

- [@EnableAspectJAutoProxy](#enableaspectjautoproxy)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、注解源码](#四注解源码)
  - [五、最佳实践](#五最佳实践)
  - [六、源码分析](#六源码分析)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`EnableAspectJAutoProxy`注解是Spring框架中的一个注解，用于启用AspectJ自动代理功能，它能够自动将AspectJ切面与Spring的IoC容器集成，无需显式配置大量AOP相关的内容，从而简化AOP的使用和配置。

### 三、主要功能

1. **启用AspectJ自动代理功能**

   + 通过在配置类上添加该注解，Spring会自动启用AspectJ自动代理功能，无需显式配置大量AOP相关内容。

2. **自动创建代理对象**

   + Spring会自动创建代理对象来应用切面，将切面逻辑与目标对象进行结合。

3. **集成AspectJ切面与Spring容器**

   + 能够方便地将AspectJ切面与Spring的IoC容器集成，实现横切关注点的模块化管理。

4. **简化AOP配置**

   + 减少了手动配置AOP所需的繁琐步骤，提高了开发效率。

5. **自动扫描切面**

+ Spring会自动扫描应用中的AspectJ切面，并将其应用到相应的目标对象中，无需手动配置切面。

### 四、注解源码

`EnableAspectJAutoProxy`注解，用于启用支持处理使用AspectJ的`@Aspect`注解标记的组件，在Spring中类似于XML配置中的`<aop:aspectj-autoproxy>`功能。它允许通过配置类轻松集成AspectJ切面，并控制代理类型和代理的可见性。

```java
/**
 * 启用支持处理使用AspectJ的{@code @Aspect}注解标记的组件，
 * 类似于Spring的{@code <aop:aspectj-autoproxy>} XML元素中的功能。
 * 应用在如下的@{@link Configuration}类上：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public FooService fooService() {
 *         return new FooService();
 *     }
 *
 *     &#064;Bean
 *     public MyAspect myAspect() {
 *         return new MyAspect();
 *     }
 * }</pre>
 *
 * 这里的{@code FooService}是一个典型的POJO组件，{@code MyAspect}是一个
 * {@code @Aspect}-风格的切面：
 *
 * <pre class="code">
 * public class FooService {
 *
 *     // 各种方法
 * }</pre>
 *
 * <pre class="code">
 * &#064;Aspect
 * public class MyAspect {
 *
 *     &#064;Before("execution(* FooService+.*(..))")
 *     public void advice() {
 *         // 适当地提供FooService方法的建议
 *     }
 * }</pre>
 *
 * 在上述场景中，{@code @EnableAspectJAutoProxy}确保{@code MyAspect}
 * 将被正确处理，并且{@code FooService}将被代理，混合其中的建议。
 *
 * <p>用户可以通过{@link #proxyTargetClass()}属性控制为{@code FooService}创建的代理类型。
 * 以下示例启用了CGLIB风格的“子类”代理，而不是默认的基于接口的JDK代理方式。
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy(proxyTargetClass=true)
 * public class AppConfig {
 *     // ...
 * }</pre>
 *
 * <p>注意，{@code @Aspect} bean可以像任何其他组件一样进行组件扫描。
 * 只需将切面标记为{@code @Aspect}和{@code @Component}：
 *
 * <pre class="code">
 * package com.foo;
 *
 * &#064;Component
 * public class FooService { ... }
 *
 * &#064;Aspect
 * &#064;Component
 * public class MyAspect { ... }</pre>
 *
 * 然后使用@{@link ComponentScan}注解来同时选择它们：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan("com.foo")
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     // 不需要显式的{@code @Bean}定义
 * }</pre>
 *
 * <b>注意：{@code @EnableAspectJAutoProxy}仅适用于其本地应用上下文，
 * 允许在不同级别选择性地对bean进行代理。</b>
 * 如果需要在多个级别应用其行为，例如常见的根Web应用程序上下文和任何单独的{@code DispatcherServlet}应用程序上下文中，
 * 请在每个单独的上下文中重新声明{@code @EnableAspectJAutoProxy}。
 *
 * <p>该功能要求类路径上存在{@code aspectjweaver}。
 * 虽然{@code spring-aop}一般情况下对该依赖是可选的，但是对于{@code @EnableAspectJAutoProxy}及其基础设施，它是必需的。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see org.aspectj.lang.annotation.Aspect
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

    /**
     * 指示是否创建基于子类（CGLIB）的代理，而不是标准的基于Java接口的代理。默认值为{@code false}。
     */
    boolean proxyTargetClass() default false;

    /**
     * 指示代理是否应该被AOP框架公开为一个{@code ThreadLocal}，以便通过{@link org.springframework.aop.framework.AopContext}类进行检索。
     * 默认情况下关闭，即不保证{@code AopContext}访问将起作用。
     * @since 4.3.1
     */
    boolean exposeProxy() default false;

}
```

### 五、最佳实践

使用`EnableAspectJAutoProxy`
注解和Spring的基于注解的应用上下文来启用AspectJ自动代理功能。在程序中，首先创建了一个基于注解的应用上下文，然后通过该上下文获取了`MyService`
bean，并调用了其方法。

```java
public class EnableAspectJAutoProxyDemo {

    public static void main(String[] args) {
        // 创建基于注解的应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从应用上下文中获取MyService bean
        MyService myService = context.getBean(MyService.class);
        // 调用MyService的方法
        myService.foo();
    }
}
```

`AppConfig` 类是一个使用 `@Configuration` 注解标记的配置类，通过 `@EnableAspectJAutoProxy` 开启了 AspectJ
自动代理功能，并通过 `@ComponentScan` 启用了组件扫描，用于自动发现和注册 Spring 组件。

```java
@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class AppConfig {

}
```

`MyService` 类是一个使用 `@Service` 注解标记的服务类，提供了一个名为 `foo()` 的方法，该方法在调用时会打印消息 "foo..."。

```java

@Service
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

`MyAspect`是一个使用了`@Aspect`注解的Java类，表示它是一个切面。在这个类中，定义了一个名为`advice`的方法，并使用了`@Before`注解来指定在目标方法执行之前执行的通知。

```java
@Aspect
@Component
public class MyAspect {

    @Before("execution(* com.xcs.spring.MyService+.*(..))")
    public void before() {
        System.out.println("Before method execution");
    }
}
```

运行结果，调用 `MyService` 类的 `foo()` 方法之前，成功地执行了一个切面通知，输出了 "Before method execution"
的消息，然后执行了 `foo()` 方法，输出了 "foo..." 的消息。

```java
Before method
execution
foo...
```

### 六、源码分析

在`org.springframework.context.annotation.AspectJAutoProxyRegistrar#registerBeanDefinitions`方法中，首先注册了AspectJ注解自动代理创建器，然后获取了`@EnableAspectJAutoProxy`注解的属性。如果`@EnableAspectJAutoProxy`注解中指定了`proxyTargetClass`属性为true，则强制使用CGLIB代理；如果指定了`exposeProxy`属性为true，则强制代理对象暴露为ThreadLocal。

```java
@Override
public void registerBeanDefinitions(
        AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    // 1、注册AspectJ注解自动代理创建器
    AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);

    // 获取@EnableAspectJAutoProxy注解的属性
    AnnotationAttributes enableAspectJAutoProxy =
            AnnotationConfigUtils.attributesFor(importingClassMetadata, EnableAspectJAutoProxy.class);
    if (enableAspectJAutoProxy != null) {
        ///2.如果@EnableAspectJAutoProxy注解指定了proxyTargetClass属性为true，则强制使用CGLIB代理
        if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
            AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
        }
        // 3.如果@EnableAspectJAutoProxy注解指定了exposeProxy属性为true，则强制代理对象暴露为ThreadLocal
        if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
            AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
        }
    }
}

```

在`org.springframework.aop.config.AopConfigUtils#registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry)`方法中，注册AspectJ注解自动代理创建器。它提供了一个重载方法，允许传入一个额外的参数，但这里调用的是没有额外参数的版本。在方法中，它会根据给定的`BeanDefinitionRegistry`对象来注册AspectJ注解自动代理创建器，并返回相应的BeanDefinition。

```java
@Nullable
public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry) {
    return registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry, null);
}
```

在`org.springframework.aop.config.AopConfigUtils#registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry,source)`方法中，注册AspectJ注解自动代理创建器，并且可以指定源对象。在方法中，它调用了一个辅助方法`registerOrEscalateApcAsRequired`，该方法会根据需要注册或升级AspectJ注解自动代理创建器，并返回相应的BeanDefinition。

[AnnotationAwareAspectJAutoProxyCreator源码分析](../spring-aop-annotationAwareAspectJAutoProxyCreator/README.md)

```java
@Nullable
public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(
       BeanDefinitionRegistry registry, @Nullable Object source) {

    return registerOrEscalateApcAsRequired(AnnotationAwareAspectJAutoProxyCreator.class, registry, source);
}
```

在`org.springframework.aop.config.AopConfigUtils#registerOrEscalateApcAsRequired`方法中，首先检查给定的`BeanDefinitionRegistry`中是否已经存在了自动代理创建器的定义。如果存在，则比较现有自动代理创建器与指定类的优先级，如果指定类的优先级更高，则进行升级操作；如果不存在，则创建一个新的自动代理创建器的定义，并将其注册到给定的`BeanDefinitionRegistry`中。

```java
@Nullable
private static BeanDefinition registerOrEscalateApcAsRequired(
       Class<?> cls, BeanDefinitionRegistry registry, @Nullable Object source) {

    // 检查BeanDefinitionRegistry对象是否为null
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");

    // 如果已经存在相同名称的自动代理创建器，则进行升级操作
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
       // 获取已存在的自动代理创建器的BeanDefinition
       BeanDefinition apcDefinition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
       // 比较已存在的自动代理创建器与指定类的优先级
       if (!cls.getName().equals(apcDefinition.getBeanClassName())) {
          int currentPriority = findPriorityForClass(apcDefinition.getBeanClassName());
          int requiredPriority = findPriorityForClass(cls);
          // 如果指定类的优先级高于已存在的自动代理创建器，则进行升级
          if (currentPriority < requiredPriority) {
             apcDefinition.setBeanClassName(cls.getName());
          }
       }
       return null;
    }

    // 如果不存在相同名称的自动代理创建器，则进行注册操作
    // 创建新的自动代理创建器的BeanDefinition
    RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
    beanDefinition.setSource(source);
    beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
    beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    // 注册新的自动代理创建器的BeanDefinition
    registry.registerBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME, beanDefinition);
    return beanDefinition;
}
```

在`org.springframework.aop.config.AopConfigUtils#forceAutoProxyCreatorToUseClassProxying`方法中，强制自动代理创建器使用基于类的代理。它首先检查给定的`BeanDefinitionRegistry`中是否已经存在了自动代理创建器的定义。如果存在，则获取该定义并设置其`proxyTargetClass`属性为`true`，表示使用基于类的代理。

```java
public static void forceAutoProxyCreatorToUseClassProxying(BeanDefinitionRegistry registry) {
    // 检查是否存在自动代理创建器的BeanDefinition
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
        // 获取自动代理创建器的BeanDefinition
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        // 设置proxyTargetClass属性为true，表示使用基于类的代理
        definition.getPropertyValues().add("proxyTargetClass", Boolean.TRUE);
    }
}
```

在`org.springframework.aop.config.AopConfigUtils#forceAutoProxyCreatorToExposeProxy`方法中，强制自动代理创建器暴露代理对象。它首先检查给定的`BeanDefinitionRegistry`中是否已经存在了自动代理创建器的定义。如果存在，则获取该定义并设置其`exposeProxy`属性为`true`，表示暴露代理对象。

```java
public static void forceAutoProxyCreatorToExposeProxy(BeanDefinitionRegistry registry) {
    // 检查是否存在自动代理创建器的BeanDefinition
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
        // 获取自动代理创建器的BeanDefinition
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        // 设置exposeProxy属性为true，表示暴露代理对象
        definition.getPropertyValues().add("exposeProxy", Boolean.TRUE);
    }
}

```
