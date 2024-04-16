## BeanFactoryAspectJAdvisorsBuilder

- [BeanFactoryAspectJAdvisorsBuilder](#beanfactoryaspectjadvisorsbuilder)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、源码分析](#五源码分析)
  - [六、常见问题](#六常见问题)

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

使用 `BeanFactoryAspectJAdvisorsBuilder` 类来构建基于 AspectJ 注解的切面，首先创建了一个默认的 Bean 工厂，并注册了一个名为 "myAspect" 的单例 Bean，然后通过 `BeanFactoryAspectJAdvisorsBuilder` 实例构建了 AspectJ Advisors，并将其打印出来。

```java
public class BeanFactoryAspectJAdvisorsBuilderDemo {

    public static void main(String[] args) {
        // 创建一个默认的 Bean 工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 在 Bean 工厂中注册一个名为 "myAspect" 的单例 Bean，类型为 MyAspect
        beanFactory.registerSingleton("myAspect", new MyAspect());

        // 创建 BeanFactoryAspectJAdvisorsBuilder 实例，并传入 Bean 工厂和 ReflectiveAspectJAdvisorFactory 实例
        BeanFactoryAspectJAdvisorsBuilder builder = new BeanFactoryAspectJAdvisorsBuilder(beanFactory, new ReflectiveAspectJAdvisorFactory(beanFactory));
        // 构建 AspectJ Advisors
        List<Advisor> advisors = builder.buildAspectJAdvisors();
        // 打印 Advisors
        advisors.forEach(System.out::println);
    }
}
```

使用了 AspectJ 的注解 `@Aspect` 进行标记。在该切面类中，包含了两个通知方法`before()` 和 `after()`，分别使用 `@Before` 和 `@After` 注解标记。这两个通知方法分别在目标方法 `com.xcs.spring.MyService.doSomething()` 执行之前和之后执行，并输出相应的日志信息。

```java
@Aspect
class MyAspect {

    @Before("execution(* com.xcs.spring.MyService.doSomething(..))")
    public void before() {
        System.out.println("Before executing the method..." );
    }

    @After("execution(* com.xcs.spring.MyService.doSomething(..))")
    public void after() {
        System.out.println("After executing the method..." );
    }
}
```

定义了一个名为 `MyService` 的简单 Java 类，其中包含一个名为 `doSomething()` 的方法。该方法简单地打印一条日志信息 "Doing something..."。这个类作为示例类使用，用来演示在 AOP 中如何应用切面逻辑。

```java
public class MyService {
    public void doSomething() {
        System.out.println("Doing something...");
    }
}
```

运行结果，显示了两个 Advisor 对象的信息，它们分别对应着切面类 `MyAspect` 中的 `before()` 和 `after()` 方法，并针对相同的切点表达式 `execution(* com.xcs.spring.MyService.doSomething(..))`。

```java
InstantiationModelAwarePointcutAdvisor: expression [execution(* com.xcs.spring.MyService.doSomething(..))]; advice method [public void com.xcs.spring.MyAspect.before()]; perClauseKind=SINGLETON
InstantiationModelAwarePointcutAdvisor: expression [execution(* com.xcs.spring.MyService.doSomething(..))]; advice method [public void com.xcs.spring.MyAspect.after()]; perClauseKind=SINGLETON
```

### 五、源码分析

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

### 六、常见问题

1. **实例化模型匹配** 

   + 在判断切面的实例化模型时，需要确保该模型与实际的 Bean 实例化策略相匹配。如果切面 Bean 被声明为单例模式，但实际上是多例的，或者反之，则可能会导致不一致或异常情况。