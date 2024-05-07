## AspectJAdvisorFactory

- [AspectJAdvisorFactory](#aspectjadvisorfactory)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [五、主要实现](#五主要实现)
    - [六、类关系图](#六类关系图)
    - [七、最佳实践](#七最佳实践)
    - [八、源码分析](#八源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`AspectJAdvisorFactory` 接口是 Spring AOP 中负责将 AspectJ 注解标记的切面类转换为 Advisor 对象的关键接口，实现类解析注解并生成 Advisor，使得 Spring AOP 能够与 AspectJ 注解风格结合，提供灵活的面向切面编程能力。

### 三、主要功能

1. **解析AspectJ注解**
   + AspectJAdvisorFactory 实现类负责解析 AspectJ 注解，如 @Aspect、@Before、@After 等，以及切点表达式等相关内容。
   
2. **创建Advisor对象**

   + 根据解析得到的 AspectJ 注解信息，AspectJAdvisorFactory 实现类生成对应的 Advisor 对象，其中包含切面的通知（Advice）和切入点（Pointcut）。
   
3. **注册Advisor对象**

   + 生成的 Advisor 对象可以被注册到 Spring AOP 框架中，以便在运行时实现面向切面编程的功能。
   
4. **支持与AspectJ注解风格的结合**

   + 通过 AspectJAdvisorFactory，Spring AOP 可以与 AspectJ 注解风格结合使用，为开发者提供了更为灵活和方便的 AOP 编程方式。


### 四、接口源码

 `AspectJAdvisorFactory`接口，用于创建 Spring AOP Advisors，其中 Advisors 是根据 AspectJ 注解语法标记的类来生成的。该接口包含了判断类是否为切面、验证切面类的有效性、构建切面实例的 Advisors 以及为给定的 AspectJ advice 方法构建 Spring AOP Advisor 和 Advice 的方法。

```java
/**
 * 用于从用 AspectJ 注解语法注释的类中创建 Spring AOP Advisor 的工厂接口。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjTypeSystem
 */
public interface AspectJAdvisorFactory {

    /**
     * 确定给定的类是否是一个切面，由 AspectJ 的 {@link org.aspectj.lang.reflect.AjTypeSystem} 报告。
     * <p>如果所谓的切面无效（例如扩展了具体切面类），则简单地返回 {@code false}。
     * 对于一些 Spring AOP 无法处理的切面，例如具有不受支持的实例化模型，将返回 true。
     * 如果需要处理这些情况，请使用 {@link #validate} 方法。
     * @param clazz 所谓的注解式 AspectJ 类
     * @return 此类是否被 AspectJ 识别为切面类
     */
    boolean isAspect(Class<?> clazz);

    /**
     * 给定的类是否是有效的 AspectJ 切面类？
     * @param aspectClass 要验证的所谓的 AspectJ 注解式类
     * @throws AopConfigException 如果类是无效的切面（永远不合法）
     * @throws NotAnAtAspectException 如果类根本不是一个切面（根据上下文的不同可能合法也可能不合法）
     */
    void validate(Class<?> aspectClass) throws AopConfigException;

    /**
     * 为指定的切面实例上的所有带有注解的 At-AspectJ 方法构建 Spring AOP Advisors。
     * @param aspectInstanceFactory 切面实例工厂
     * （而不是切面实例本身，以避免过早实例化）
     * @return 此类的一组 advisors
     */
    List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory);

    /**
     * 为给定的 AspectJ advice 方法构建 Spring AOP Advisor。
     * @param candidateAdviceMethod 候选的 advice 方法
     * @param aspectInstanceFactory 切面实例工厂
     * @param declarationOrder 在切面内的声明顺序
     * @param aspectName 切面的名称
     * @return 如果方法不是 AspectJ advice 方法，或者是将被其他 advice 使用但不会单独创建 Spring advice 的切入点，则返回 {@code null}
     */
    @Nullable
    Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
          int declarationOrder, String aspectName);

    /**
     * 为给定的 AspectJ advice 方法构建 Spring AOP Advice。
     * @param candidateAdviceMethod 候选的 advice 方法
     * @param expressionPointcut AspectJ 表达式切入点
     * @param aspectInstanceFactory 切面实例工厂
     * @param declarationOrder 在切面内的声明顺序
     * @param aspectName 切面的名称
     * @return 如果方法不是 AspectJ advice 方法，或者是将被其他 advice 使用但不会单独创建 Spring advice 的切入点，则返回 {@code null}
     * @see org.springframework.aop.aspectj.AspectJAroundAdvice
     * @see org.springframework.aop.aspectj.AspectJMethodBeforeAdvice
     * @see org.springframework.aop.aspectj.AspectJAfterAdvice
     * @see org.springframework.aop.aspectj.AspectJAfterReturningAdvice
     * @see org.springframework.aop.aspectj.AspectJAfterThrowingAdvice
     */
    @Nullable
    Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
          MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName);

}
```

### 五、主要实现

1. **ReflectiveAspectJAdvisorFactory**

   + `ReflectiveAspectJAdvisorFactory` 实现类是利用反射机制解析 AspectJ 注解，并创建相应的 Advisor 对象，支持注解风格的 AspectJ 切面，为我们提供了灵活而强大的面向切面编程能力。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AbstractAspectJAdvisorFactory
class AspectJAdvisorFactory {
<<Interface>>

}
class ReflectiveAspectJAdvisorFactory

AbstractAspectJAdvisorFactory  ..>  AspectJAdvisorFactory 
ReflectiveAspectJAdvisorFactory  -->  AbstractAspectJAdvisorFactory 

~~~

### 七、最佳实践

使用 `AspectJAdvisorFactory` 实现类 `ReflectiveAspectJAdvisorFactory`，以创建 Advisors 并打印它们。首先，通过 `DefaultListableBeanFactory` 创建了一个默认的 Bean 工厂，并在其中注册了一个名为 "myAspect" 的单例 Bean，类型为 `MyAspect`。然后，创建了一个 `MetadataAwareAspectInstanceFactory` 实例 `factory`，用于实例化切面。接着，创建了 `ReflectiveAspectJAdvisorFactory` 实例 `aspectJAdvisorFactory`，并使用它获取所有注解式 AspectJ 方法的 Advisors。最后，通过遍历 Advisors 并打印的方式展示了这些 Advisors。

```java
public class AspectJAdvisorFactoryDemo {

    public static void main(String[] args) {
        // 创建一个默认的 Bean 工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 在 Bean 工厂中注册一个名为 "myAspect" 的单例 Bean，类型为 MyAspect
        beanFactory.registerSingleton("myAspect", new MyAspect());

        // 创建一个 Aspect 实例工厂，用于实例化切面
        MetadataAwareAspectInstanceFactory factory = new BeanFactoryAspectInstanceFactory(beanFactory, "myAspect");
        // 创建 ReflectiveAspectJAdvisorFactory 实例，用于创建 Advisor
        ReflectiveAspectJAdvisorFactory aspectJAdvisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        // 获取所有注解式 AspectJ 方法的 Advisors
        List<Advisor> advisors = aspectJAdvisorFactory.getAdvisors(factory);
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

### 八、源码分析

在`org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisors`方法中，根据给定的切面实例工厂，获取切面类中的通知器列表。首先，验证切面类的有效性，然后使用元数据判断是否需要延迟实例化切面实例工厂。接着，遍历切面类中的方法，获取通知器，并将其添加到通知器列表中。如果切面是针对目标的并且是延迟实例化的，则添加一个虚拟实例化通知器。最后，查找切面类中的引入字段，获取相应的通知器，并将其添加到通知器列表中，最终返回该列表。

```java
/**
 * 获取通知器列表。
 * @param aspectInstanceFactory 切面实例工厂
 * @return 通知器列表
 */
@Override
public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
    // 获取切面类和切面名称
    Class<?> aspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
    String aspectName = aspectInstanceFactory.getAspectMetadata().getAspectName();
    // 验证切面类的有效性
    validate(aspectClass);

    // 将MetadataAwareAspectInstanceFactory包装成装饰器，以保证只实例化一次
    MetadataAwareAspectInstanceFactory lazySingletonAspectInstanceFactory =
          new LazySingletonAspectInstanceFactoryDecorator(aspectInstanceFactory);

    // 创建通知器列表
    List<Advisor> advisors = new ArrayList<>();
    // 遍历切面类中的方法，获取通知器
    for (Method method : getAdvisorMethods(aspectClass)) {
       // 由于JDK不再以源代码中的声明顺序返回方法，因此固定declarationOrderInAspect为0以支持跨JVM启动的可靠通知顺序
       Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, 0, aspectName);
       if (advisor != null) {
          advisors.add(advisor);
       }
    }

    // 如果是针对目标的切面，则添加一个虚拟实例化通知器
    if (!advisors.isEmpty() && lazySingletonAspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
       Advisor instantiationAdvisor = new SyntheticInstantiationAdvisor(lazySingletonAspectInstanceFactory);
       advisors.add(0, instantiationAdvisor);
    }

    // 查找切面类中的引入字段
    for (Field field : aspectClass.getDeclaredFields()) {
       Advisor advisor = getDeclareParentsAdvisor(field);
       if (advisor != null) {
          advisors.add(advisor);
       }
    }

    return advisors;
}
```

在`org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisorMethods`方法中，获取切面类中作为通知的方法列表。它通过反射遍历切面类的方法，并使用 adviceMethodFilter 过滤出通知方法，最后根据方法数量进行排序后返回。

```java
/**
 * 获取切面类中用作通知的方法列表。
 * 
 * @param aspectClass 切面类
 * @return 切面类中的通知方法列表
 */
private List<Method> getAdvisorMethods(Class<?> aspectClass) {
    // 创建一个空的方法列表
    List<Method> methods = new ArrayList<>();
    // 使用 ReflectionUtils 遍历切面类中的方法，将符合条件的方法添加到方法列表中
    ReflectionUtils.doWithMethods(aspectClass, methods::add, adviceMethodFilter);
    // 如果方法数量大于1，即存在多个通知方法，则按照指定的比较器对方法列表进行排序
    if (methods.size() > 1) {
        methods.sort(adviceMethodComparator);
    }
    // 返回方法列表
    return methods;
}
```

在`org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#adviceMethodFilter`字段中，用于筛选切面类中的通知方法。它通过 ReflectionUtils.USER_DECLARED_METHODS 筛选出用户自定义的方法，并排除带有 @Pointcut 注解的方法。

```java
private static final MethodFilter adviceMethodFilter = ReflectionUtils.USER_DECLARED_METHODS
    .and(method -> (AnnotationUtils.getAnnotation(method, Pointcut.class) == null));
```

在 `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#adviceMethodComparator` 字段中，按照通知的类型依次排列：`Around`、`Before`、`After`、`AfterReturning`、`AfterThrowing`。同时考虑了其中的特殊情况，即使 @After 方法在排序上位于 `@AfterReturning` 和 `@AfterThrowing` 之前，但实际上 @After 方法会在 `@AfterReturning` 和 `@AfterThrowing` 方法之后被调用。这是因为 `AspectJAfterAdvice.invoke(MethodInvocation)` 方法在 `try` 块中调用了 `proceed()` 方法，并且只有在相应的 `finally` 块中才调用了 `@After` 方法。

```java
private static final Comparator<Method> adviceMethodComparator;

static {
    // 注意：尽管 @After 排在 @AfterReturning 和 @AfterThrowing 之前，
    // 但实际上 @After 通知方法会在 @AfterReturning 和 @AfterThrowing 方法之后被调用，
    // 这是因为 AspectJAfterAdvice.invoke(MethodInvocation) 方法在 `try` 块中调用了 proceed() 方法，
    // 并且只有在相应的 `finally` 块中才调用了 @After 方法。

    // 定义一个方法比较器，按照通知的类型依次排列：Around、Before、After、AfterReturning、AfterThrowing。
    // 同时考虑了其中的特殊情况，即使 @After 方法在排序上位于 @AfterReturning 和 @AfterThrowing 之前，
    // 但实际上 @After 方法会在 @AfterReturning 和 @AfterThrowing 方法之后被调用。
    Comparator<Method> adviceKindComparator = new ConvertingComparator<>(
        new InstanceComparator<>(
            Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class),
        (Converter<Method, Annotation>) method -> {
            // 使用 AspectJAnnotation 查找方法上的 AspectJ 注解
            AspectJAnnotation<?> ann = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
            return (ann != null ? ann.getAnnotation() : null);
        });
    
    // 定义一个方法名比较器
    Comparator<Method> methodNameComparator = new ConvertingComparator<>(Method::getName);
    
    // 将两个比较器按顺序组合，首先按照通知类型排序，然后按照方法名称排序
    adviceMethodComparator = adviceKindComparator.thenComparing(methodNameComparator);
}
```

在`org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisor`方法中，根据候选的通知方法获取 Advisor 对象的功能，首先验证了 Aspect 类的有效性，然后获取切点表达式，如果切点表达式为空，则返回 null，否则创建一个 InstantiationModelAwarePointcutAdvisorImpl 对象，并使用该对象包装切点表达式、通知方法等信息。

```java
/**
 * 根据候选的通知方法获取 Advisor 对象。
 * 
 * @param candidateAdviceMethod 候选的通知方法
 * @param aspectInstanceFactory Aspect 实例工厂，用于创建 Aspect 实例
 * @param declarationOrderInAspect 在 Aspect 中的声明顺序
 * @param aspectName Aspect 的名称
 * @return Advisor 对象，如果候选方法不是有效的切点，则返回 null
 */
@Override
@Nullable
public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
       int declarationOrderInAspect, String aspectName) {

    // 验证 Aspect 类的有效性
    validate(aspectInstanceFactory.getAspectMetadata().getAspectClass());

    // 获取切点表达式
    AspectJExpressionPointcut expressionPointcut = getPointcut(
          candidateAdviceMethod, aspectInstanceFactory.getAspectMetadata().getAspectClass());
    // 如果切点表达式为空，则返回 null
    if (expressionPointcut == null) {
       return null;
    }

    // 创建 InstantiationModelAwarePointcutAdvisorImpl 对象，用于管理切点表达式、通知方法等信息
    return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod,
          this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
}
```

在`org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl#InstantiationModelAwarePointcutAdvisorImpl`方法中，初始化了切面相关的属性，包括声明的切点、切面方法的声明类、方法名称、参数类型、切面工厂、切面实例工厂、声明顺序和切面名称。如果切面是延迟实例化的，它会创建一个动态切点，并将静态切点与初始切点联合起来，以实现从预实例化到后实例化状态的动态变化。如果切面不是延迟实例化的，则使用初始切点，同时实例化通知方法。

```java
public InstantiationModelAwarePointcutAdvisorImpl(AspectJExpressionPointcut declaredPointcut,
       Method aspectJAdviceMethod, AspectJAdvisorFactory aspectJAdvisorFactory,
       MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {

    // 声明切点
    this.declaredPointcut = declaredPointcut;
    // 获取通知方法所在的类
    this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
    // 获取通知方法的名称
    this.methodName = aspectJAdviceMethod.getName();
    // 获取通知方法的参数类型
    this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
    // 设置 AspectJ 的通知方法
    this.aspectJAdviceMethod = aspectJAdviceMethod;
    // 设置 AspectJ 的 AdvisorFactory
    this.aspectJAdvisorFactory = aspectJAdvisorFactory;
    // 设置 Aspect 实例工厂
    this.aspectInstanceFactory = aspectInstanceFactory;
    // 设置声明顺序
    this.declarationOrder = declarationOrder;
    // 设置切面名称
    this.aspectName = aspectName;

    // 如果切面是延迟实例化的
    if (aspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
       // 切点的静态部分是一个延迟类型
       Pointcut preInstantiationPointcut = Pointcuts.union(
             aspectInstanceFactory.getAspectMetadata().getPerClausePointcut(), this.declaredPointcut);

       // 使之动态：必须从预实例化状态变为后实例化状态
       // 如果它不是动态切点，则可能会在第一次评估后被 Spring AOP 基础设施优化掉
       this.pointcut = new PerTargetInstantiationModelPointcut(
             this.declaredPointcut, preInstantiationPointcut, aspectInstanceFactory);
       // 设置为延迟
       this.lazy = true;
    }
    else {
       // 单例切面
       this.pointcut = this.declaredPointcut;
       this.lazy = false;
       // 实例化通知
       this.instantiatedAdvice = instantiateAdvice(this.declaredPointcut);
    }
}
```

在`org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl#instantiateAdvice`方法中，使用 AspectJAdvisorFactory 获取通知实例，然后检查是否为空，如果为空则返回一个空的通知。

```java
/**
 * 根据给定的切点实例化通知。
 *
 * @param pointcut 切点表达式
 * @return 实例化的通知
 */
private Advice instantiateAdvice(AspectJExpressionPointcut pointcut) {
    // 使用AspectJAdvisorFactory获取通知实例
    Advice advice = this.aspectJAdvisorFactory.getAdvice(this.aspectJAdviceMethod, pointcut,
            this.aspectInstanceFactory, this.declarationOrder, this.aspectName);
    // 如果获取的通知实例为空，则返回空通知
    return (advice != null ? advice : EMPTY_ADVICE);
}
```

在`org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvice`方法中，根据给定的候选通知方法、切点表达式、切面实例工厂等信息，获取对应的 Spring AOP 通知实例。它首先验证切面类的有效性，然后根据候选通知方法的 AspectJ 注解类型，实例化相应的 Spring AOP 通知，如环绕通知、前置通知、后置通知等，并配置相关的通知属性，最后返回所生成的 Spring AOP 通知实例。

```java
@Override
@Nullable
public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
       MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {

    // 获取候选通知方法所在的切面类
    Class<?> candidateAspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
    // 验证切面类的有效性
    validate(candidateAspectClass);

    // 获取候选通知方法的 AspectJ 注解
    AspectJAnnotation<?> aspectJAnnotation =
          AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
    // 如果候选通知方法没有 AspectJ 注解，则返回 null
    if (aspectJAnnotation == null) {
       return null;
    }

    // 判断切面类是否为 AspectJ 注解的类
    if (!isAspect(candidateAspectClass)) {
       // 如果不是，则抛出异常
       throw new AopConfigException("Advice must be declared inside an aspect type: " +
             "Offending method '" + candidateAdviceMethod + "' in class [" +
             candidateAspectClass.getName() + "]");
    }

    // 如果日志级别为 DEBUG，则打印找到的 AspectJ 方法信息
    if (logger.isDebugEnabled()) {
       logger.debug("Found AspectJ method: " + candidateAdviceMethod);
    }

    // 声明 Spring AOP 通知实例
    AbstractAspectJAdvice springAdvice;

    // 根据注解类型实例化相应的 Spring AOP 通知
    switch (aspectJAnnotation.getAnnotationType()) {
       // 处理切点注解
       case AtPointcut:
          if (logger.isDebugEnabled()) {
             logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
          }
          return null;
       // 处理环绕通知
       case AtAround:
          springAdvice = new AspectJAroundAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          break;
       // 处理前置通知
       case AtBefore:
          springAdvice = new AspectJMethodBeforeAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          break;
       // 处理后置通知
       case AtAfter:
          springAdvice = new AspectJAfterAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          break;
       // 处理返回后通知
       case AtAfterReturning:
          springAdvice = new AspectJAfterReturningAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          AfterReturning afterReturningAnnotation = (AfterReturning) aspectJAnnotation.getAnnotation();
          if (StringUtils.hasText(afterReturningAnnotation.returning())) {
             springAdvice.setReturningName(afterReturningAnnotation.returning());
          }
          break;
       // 处理抛出异常后通知
       case AtAfterThrowing:
          springAdvice = new AspectJAfterThrowingAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          AfterThrowing afterThrowingAnnotation = (AfterThrowing) aspectJAnnotation.getAnnotation();
          if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
             springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
          }
          break;
       // 处理其他类型的通知，抛出不支持的操作异常
       default:
          throw new UnsupportedOperationException(
                "Unsupported advice type on method: " + candidateAdviceMethod);
    }

    // 配置通知的相关属性
    springAdvice.setAspectName(aspectName);
    springAdvice.setDeclarationOrder(declarationOrder);
    // 获取通知方法的参数名数组
    String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
    // 如果参数名数组不为空，则设置到通知中
    if (argNames != null) {
       springAdvice.setArgumentNamesFromStringArray(argNames);
    }
    // 计算参数绑定
    springAdvice.calculateArgumentBindings();

    return springAdvice;
}
```
