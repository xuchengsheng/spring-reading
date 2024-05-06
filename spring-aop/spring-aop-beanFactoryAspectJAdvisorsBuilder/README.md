## BeanFactoryAspectJAdvisorsBuilder

- [BeanFactoryAspectJAdvisorsBuilder](#beanfactoryaspectjadvisorsbuilder)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`BeanFactoryAspectJAdvisorsBuilder` 是 Spring AOP 中的一个重要类，负责将应用中使用 AspectJ 注解标记的切面解析并转换为 Spring AOP 中的通知器，从而实现基于注解的切面编程。

### 三、主要功能

1. **扫描 AspectJ 注解** 

   + 这个类会扫描应用中的类，查找带有 AspectJ 注解的类，比如 `@Aspect`。它会识别这些类，并将它们转换成 Spring AOP 中的通知器。

2. **解析切面和通知** 

   + 一旦发现带有 AspectJ 注解的类，`BeanFactoryAspectJAdvisorsBuilder` 将解析这些类，找到其中定义的切面以及切面中的通知。

3. **创建通知器（advisors）** 

   + 基于解析得到的切面和通知信息，这个类会创建对应的通知器。通知器包含了切面逻辑以及连接点（切入点）信息，它们将被应用到目标对象的方法调用中。

4. **注册通知器** 

   + 最后，`BeanFactoryAspectJAdvisorsBuilder` 将创建的通知器注册到 Spring 的 AOP 框架中，以便在应用程序运行时生效。

### 四、最佳实践

使用基于注解的应用上下文来获取并调用 `MyService` Bean 的 `foo()`
方法。首先，创建了一个 `AnnotationConfigApplicationContext` 实例，通过传入 `AppConfig.class`
来初始化基于注解的应用上下文。然后，通过 `context.getBean(MyService.class)` 获取了 `MyService` Bean
的实例，并调用了其 `foo()` 方法。

```java
public class BeanFactoryAspectJAdvisorsBuilderDemo {

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

通过 @Aspect 和 @Component 注解将其标记为 Spring 组件，并定义了一个在 com.xcs.spring.MyService 类的 foo
方法执行前执行的前置通知（Before advice）。

```java
@Aspect
@Component
class MyAspect {

    @Before("execution(* com.xcs.spring.MyService.foo(..))")
    public void before() {
        System.out.println("Before method execution");
    }
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

运行结果，调用 `MyService` 类的 `foo()` 方法之前，成功地执行了一个切面通知，输出了 "Before method execution"
的消息，然后执行了 `foo()` 方法，输出了 "foo..." 的消息。

```java
Before method
execution
foo...
```

### 五、时序图

~~~mermaid
sequenceDiagram
    AbstractAutowireCapableBeanFactory->>AbstractAutoProxyCreator: postProcessAfterInitialization()
    Note over AbstractAutowireCapableBeanFactory,AbstractAutoProxyCreator: 调用后处理方法
    AbstractAutoProxyCreator->>AbstractAutoProxyCreator: wrapIfNecessary()
    Note over AbstractAutoProxyCreator: 调用包装方法
    AbstractAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: getAdvicesAndAdvisorsForBean()
    Note over AbstractAutoProxyCreator,AbstractAdvisorAutoProxyCreator: 获取通知和 Advisors
    AbstractAdvisorAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: findEligibleAdvisors()
    Note over AbstractAdvisorAutoProxyCreator: 查找合适的 Advisors
    AbstractAdvisorAutoProxyCreator->>AnnotationAwareAspectJAutoProxyCreator: findCandidateAdvisors()
    Note over AbstractAdvisorAutoProxyCreator,AnnotationAwareAspectJAutoProxyCreator: 查找候选的 Advisors
    AnnotationAwareAspectJAutoProxyCreator->>BeanFactoryAspectJAdvisorsBuilder: buildAspectJAdvisors()
    Note over AnnotationAwareAspectJAutoProxyCreator,BeanFactoryAspectJAdvisorsBuilder: 构建 AspectJ Advisors
    BeanFactoryAspectJAdvisorsBuilder->>AbstractAutoProxyCreator: 返回 advisors
~~~

### 六、源码分析

在`org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors`方法中，主要负责在当前的 Bean 工厂中查找使用 AspectJ 注解标记的切面 Bean，并将其转换为 Spring AOP Advisors 的列表。它遍历所有的 Bean 名称，识别切面 Bean，并根据其实例化模型（单例或多例）创建对应的 AspectJ Advisors。在处理过程中，还会缓存单例切面的 Advisors，以提高性能。

```java
/**
 * 在当前 Bean 工厂中查找使用 AspectJ 注解标记的切面 Bean，并返回表示它们的 Spring AOP Advisors 列表。
 * <p>为每个 AspectJ 的通知方法创建一个 Spring Advisor。
 * @return 包含 {@link org.springframework.aop.Advisor} beans 的列表
 * @see #isEligibleBean
 */
public List<Advisor> buildAspectJAdvisors() {
    // 如果切面 Bean 名称列表为空，则进行查找
    List<String> aspectNames = this.aspectBeanNames;

    if (aspectNames == null) {
        synchronized (this) {
            aspectNames = this.aspectBeanNames;
            if (aspectNames == null) {
                // 初始化切面 Advisors 列表和切面 Bean 名称列表
                List<Advisor> advisors = new ArrayList<>();
                aspectNames = new ArrayList<>();
                // 获取当前 Bean 工厂中的所有 Bean 名称
                String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                        this.beanFactory, Object.class, true, false);
                // 遍历所有 Bean 名称
                for (String beanName : beanNames) {
                    // 检查 Bean 是否符合条件
                    if (!isEligibleBean(beanName)) {
                        continue;
                    }
                    // 获取 Bean 的类型
                    Class<?> beanType = this.beanFactory.getType(beanName, false);
                    // 如果无法获取类型，则跳过
                    if (beanType == null) {
                        continue;
                    }
                    // 判断 Bean 是否是切面
                    if (this.advisorFactory.isAspect(beanType)) {
                        // 将切面 Bean 名称加入列表
                        aspectNames.add(beanName);
                        // 获取切面元数据
                        AspectMetadata amd = new AspectMetadata(beanType, beanName);
                        // 判断切面的实例化模型
                        if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
                            // 单例模式
                            // 创建单例模式的切面实例工厂
                            MetadataAwareAspectInstanceFactory factory =
                                    new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
                            // 获取切面的 Advisors
                            List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
                            // 缓存单例切面的 Advisors
                            if (this.beanFactory.isSingleton(beanName)) {
                                this.advisorsCache.put(beanName, classAdvisors);
                            } else {
                                this.aspectFactoryCache.put(beanName, factory);
                            }
                            advisors.addAll(classAdvisors);
                        } else {
                            // 多例模式
                            if (this.beanFactory.isSingleton(beanName)) {
                                // 如果切面实例化模型为多例，但 Bean 是单例，则抛出异常
                                throw new IllegalArgumentException("Bean with name '" + beanName +
                                        "' is a singleton, but aspect instantiation model is not singleton");
                            }
                            // 创建多例模式的切面实例工厂
                            MetadataAwareAspectInstanceFactory factory =
                                    new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
                            // 缓存切面实例工厂
                            this.aspectFactoryCache.put(beanName, factory);
                            // 获取切面的 Advisors
                            advisors.addAll(this.advisorFactory.getAdvisors(factory));
                        }
                    }
                }
                // 将切面 Bean 名称列表缓存起来
                this.aspectBeanNames = aspectNames;
                return advisors;
            }
        }
    }

    // 如果切面 Bean 名称列表为空，则返回空列表
    if (aspectNames.isEmpty()) {
        return Collections.emptyList();
    }
    // 创建用于存储所有 Advisors 的列表
    List<Advisor> advisors = new ArrayList<>();
    // 遍历切面 Bean 名称列表
    for (String aspectName : aspectNames) {
        // 从缓存中获取 Advisors
        List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
        if (cachedAdvisors != null) {
            // 如果缓存中有 Advisors，则加入到结果列表中
            advisors.addAll(cachedAdvisors);
        } else {
            // 如果缓存中没有 Advisors，则从切面实例工厂中获取 Advisors
            MetadataAwareAspectInstanceFactory factory = this.aspectFactoryCache.get(aspectName);
            advisors.addAll(this.advisorFactory.getAdvisors(factory));
        }
    }
    return advisors;
}
```

在org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory#isAspect方法中，判断给定的类是否是一个切面。它首先检查类是否带有 AspectJ 注解，然后再确认该类不是由 AspectJ 编译器编译的。如果符合这两个条件，则返回 true，表示该类是一个切面；否则返回 false。

```java
/**
 * 判断给定的类是否是切面。
 * @param clazz 要检查的类
 * @return 如果类是切面，则返回 true；否则返回 false
 */
@Override
public boolean isAspect(Class<?> clazz) {
    // 判断类是否带有 AspectJ 注解，并且不是由 AspectJ 编译器编译的
    return (hasAspectAnnotation(clazz) && !compiledByAjc(clazz));
}
```

在`org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory#hasAspectAnnotation`方法中，检查给定的类是否带有 AspectJ 注解。

```java
/**
 * 判断给定的类是否带有 AspectJ 注解。
 * @param clazz 要检查的类
 * @return 如果类带有 AspectJ 注解，则返回 true；否则返回 false
 */
private boolean hasAspectAnnotation(Class<?> clazz) {
    // 使用 AnnotationUtils.findAnnotation 方法查找类上的 Aspect 注解
    return (AnnotationUtils.findAnnotation(clazz, Aspect.class) != null);
}
```



