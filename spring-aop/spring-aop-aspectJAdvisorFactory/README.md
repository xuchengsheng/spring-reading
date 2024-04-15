## AspectJAdvisorFactory

- [AspectJAdvisorFactory](#AspectJAdvisorFactory)
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

### 六、最佳实践

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

### 七、源码分析

 `ReflectiveAspectJAdvisorFactory` 类是 Spring AOP 中用于基于 AspectJ 注解语法的切面通知器工厂。它通过反射查找切面类中的通知方法，并根据这些方法创建相应的 Advisor 对象。在获取 Advisor 的过程中，它会考虑通知方法的顺序，并根据切点表达式构建相应的 AspectJExpressionPointcut 对象。同时，它还支持处理 Introduction（引入）字段，生成相应的 DeclareParentsAdvisor。

```java
/**
 * 通过反射调用相应的通知方法，从遵循AspectJ注解语法的类中创建Spring AOP Advisors的工厂。
 * 
 * 该工厂可以创建Spring AOP Advisors，这些Advisors是通过反射调用对应的通知方法从遵循AspectJ注解语法的类中生成的。
 * 
 * @author Rod Johnson
 * @author Adrian Colyer
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 2.0
 */
@SuppressWarnings("serial")
public class ReflectiveAspectJAdvisorFactory extends AbstractAspectJAdvisorFactory implements Serializable {

    // 排除 @Pointcut 方法
    private static final MethodFilter adviceMethodFilter = ReflectionUtils.USER_DECLARED_METHODS
            .and(method -> (AnnotationUtils.getAnnotation(method, Pointcut.class) == null));

    // 通知方法的比较器
    private static final Comparator<Method> adviceMethodComparator;

    static {
        // 注意尽管 @After 在 @AfterReturning 和 @AfterThrowing 之前排序，
        // 但实际上 @After 通知方法会在 @AfterReturning 和 @AfterThrowing 方法之后执行，
        // 这是因为 AspectJAfterAdvice.invoke(MethodInvocation)
        // 在 try 块中调用了 proceed()，只有在相应的 finally 块中才会调用 @After 通知方法。
        Comparator<Method> adviceKindComparator = new ConvertingComparator<>(
                new InstanceComparator<>(
                        Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class),
                (Converter<Method, Annotation>) method -> {
                    AspectJAnnotation<?> ann = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
                    return (ann != null ? ann.getAnnotation() : null);
                });
        Comparator<Method> methodNameComparator = new ConvertingComparator<>(Method::getName);
        adviceMethodComparator = adviceKindComparator.thenComparing(methodNameComparator);
    }

    // Bean 工厂
    @Nullable
    private final BeanFactory beanFactory;

    /**
     * 创建一个新的 {@code ReflectiveAspectJAdvisorFactory}。
     */
    public ReflectiveAspectJAdvisorFactory() {
        this(null);
    }

    /**
     * 创建一个新的 {@code ReflectiveAspectJAdvisorFactory}，将给定的 {@link BeanFactory}
     * 传播到创建的 {@link AspectJExpressionPointcut} 实例中，
     * 用于 bean 切点处理以及一致的 {@link ClassLoader} 解析。
     * 
     * @param beanFactory 要传播的 BeanFactory（可能为 null）
     * @since 4.3.6
     * @see AspectJExpressionPointcut#setBeanFactory
     * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getBeanClassLoader()
     */
    public ReflectiveAspectJAdvisorFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
        // 获取切面类和切面名称
        Class<?> aspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
        String aspectName = aspectInstanceFactory.getAspectMetadata().getAspectName();
        // 验证切面类的有效性
        validate(aspectClass);

        // 需要使用装饰器将 MetadataAwareAspectInstanceFactory 包装起来，
        // 这样它只会实例化一次。
        MetadataAwareAspectInstanceFactory lazySingletonAspectInstanceFactory =
                new LazySingletonAspectInstanceFactoryDecorator(aspectInstanceFactory);

        // 创建存储 Advisor 的列表
        List<Advisor> advisors = new ArrayList<>();
        // 遍历切面类中的所有方法，获取 Advisor
        for (Method method : getAdvisorMethods(aspectClass)) {
            // 在较早的 Spring Framework 版本中，advisors.size() 被提供作为 declarationOrderInAspect，
            // 以表示在已声明方法列表中的“当前位置”。
            // 但是，自 Java 7 起，“当前位置”不再有效，因为 JDK 不再按源代码中声明的顺序返回声明的方法。
            // 因此，我们现在为通过反射发现的所有通知方法硬编码 declarationOrderInAspect 为 0，
            // 以支持跨 JVM 启动的可靠通知排序。
            // 具体来说，值为 0 与 AspectJPrecedenceComparator.getAspectDeclarationOrder(Advisor) 中使用的默认值一致。
            Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, 0, aspectName);
            if (advisor != null) {
                advisors.add(advisor);
            }
        }

        // 如果是针对目标切面的，则发出虚拟实例化切面。
        if (!advisors.isEmpty() && lazySingletonAspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
            Advisor instantiationAdvisor = new SyntheticInstantiationAdvisor(lazySingletonAspectInstanceFactory);
            advisors.add(0, instantiationAdvisor);
        }

        // 查找引入字段。
        for (Field field : aspectClass.getDeclaredFields()) {
            Advisor advisor = getDeclareParentsAdvisor(field);
            if (advisor != null) {
                advisors.add(advisor);
            }
        }

        return advisors;
    }

    /**
     * 获取切面类中的所有通知方法。
     *
     * @param aspectClass 切面类
     * @return 切面类中的通知方法列表
     */
    private List<Method> getAdvisorMethods(Class<?> aspectClass) {
        List<Method> methods = new ArrayList<>();
        // 使用 ReflectionUtils.doWithMethods 方法获取切面类中的所有方法，并添加到方法列表中
        ReflectionUtils.doWithMethods(aspectClass, methods::add, adviceMethodFilter);
        // 如果方法数量大于 1，则按照 adviceMethodComparator 比较器对方法列表进行排序
        if (methods.size() > 1) {
            methods.sort(adviceMethodComparator);
        }
        return methods;
    }

    /**
     * 为给定的引入字段构建一个 DeclareParentsAdvisor。
     * <p>生成的 Advisors 需要根据目标进行评估。
     *
     * @param introductionField 要检查的字段
     * @return Advisor 实例；如果不是 Advisor，则返回 {@code null}
     */
    @Nullable
    private Advisor getDeclareParentsAdvisor(Field introductionField) {
        // 获取字段上的 DeclareParents 注解
        DeclareParents declareParents = introductionField.getAnnotation(DeclareParents.class);
        // 如果没有 DeclareParents 注解，则返回空
        if (declareParents == null) {
            // 不是一个引入字段
            return null;
        }

        // 检查 DeclareParents 注解的 defaultImpl 属性是否设置
        if (DeclareParents.class == declareParents.defaultImpl()) {
            throw new IllegalStateException("'defaultImpl' attribute must be set on DeclareParents");
        }

        // 创建一个 DeclareParentsAdvisor 实例，并返回
        return new DeclareParentsAdvisor(
                introductionField.getType(), declareParents.value(), declareParents.defaultImpl());
    }

    @Override
    @Nullable
    public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
                               int declarationOrderInAspect, String aspectName) {
        // 验证切面类的有效性
        validate(aspectInstanceFactory.getAspectMetadata().getAspectClass());

        // 获取切点表达式
        AspectJExpressionPointcut expressionPointcut = getPointcut(
                candidateAdviceMethod, aspectInstanceFactory.getAspectMetadata().getAspectClass());
        // 如果切点表达式为空，则返回空
        if (expressionPointcut == null) {
            return null;
        }

        // 创建 InstantiationModelAwarePointcutAdvisorImpl 实例作为 Advisor
        return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod,
                this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
    }

    /**
     * 获取给定通知方法的切点表达式。
     *
     * @param candidateAdviceMethod 通知方法
     * @param candidateAspectClass 候选切面类
     * @return 切点表达式的 AspectJExpressionPointcut 实例；如果没有找到 AspectJ 注解，则返回 {@code null}
     */
    @Nullable
    private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
        // 在给定的通知方法上查找 AspectJ 注解
        AspectJAnnotation<?> aspectJAnnotation =
                AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        // 如果没有找到 AspectJ 注解，则返回空
        if (aspectJAnnotation == null) {
            return null;
        }

        // 创建一个 AspectJExpressionPointcut 实例，并设置候选切面类及空的切点表达式和参数类型
        AspectJExpressionPointcut ajexp =
                new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class<?>[0]);
        // 设置切点表达式
        ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
        // 如果 beanFactory 不为空，则设置 beanFactory
        if (this.beanFactory != null) {
            ajexp.setBeanFactory(this.beanFactory);
        }
        return ajexp;
    }

    @Override
    @Nullable
    public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
                            MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {
        // 获取切面类
        Class<?> candidateAspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
        // 验证切面类的有效性
        validate(candidateAspectClass);

        // 获取 AspectJ 注解信息
        AspectJAnnotation<?> aspectJAnnotation =
                AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        // 如果没有找到 AspectJ 注解，则返回空
        if (aspectJAnnotation == null) {
            return null;
        }

        // 确定当前方法为 AspectJ 方法后，检查该方法所在的类是否是有效的切面类
        if (!isAspect(candidateAspectClass)) {
            throw new AopConfigException("Advice must be declared inside an aspect type: " +
                    "Offending method '" + candidateAdviceMethod + "' in class [" +
                    candidateAspectClass.getName() + "]");
        }

        // 如果日志级别为调试，则输出 AspectJ 方法信息
        if (logger.isDebugEnabled()) {
            logger.debug("Found AspectJ method: " + candidateAdviceMethod);
        }

        AbstractAspectJAdvice springAdvice;

        // 根据注解类型创建对应的 Spring AOP Advice 实例
        switch (aspectJAnnotation.getAnnotationType()) {
            case AtPointcut:
                // 如果是 @Pointcut 注解，则直接返回空，因为 Pointcut 不是一个 Advice
                if (logger.isDebugEnabled()) {
                    logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
                }
                return null;
            case AtAround:
                springAdvice = new AspectJAroundAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtBefore:
                springAdvice = new AspectJMethodBeforeAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtAfter:
                springAdvice = new AspectJAfterAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtAfterReturning:
                springAdvice = new AspectJAfterReturningAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                AfterReturning afterReturningAnnotation = (AfterReturning) aspectJAnnotation.getAnnotation();
                if (StringUtils.hasText(afterReturningAnnotation.returning())) {
                    springAdvice.setReturningName(afterReturningAnnotation.returning());
                }
                break;
            case AtAfterThrowing:
                springAdvice = new AspectJAfterThrowingAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                AfterThrowing afterThrowingAnnotation = (AfterThrowing) aspectJAnnotation.getAnnotation();
                if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
                    springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        "Unsupported advice type on method: " + candidateAdviceMethod);
        }

        // 配置 Advice 的属性
        springAdvice.setAspectName(aspectName);
        springAdvice.setDeclarationOrder(declarationOrder);
        String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
        if (argNames != null) {
            springAdvice.setArgumentNamesFromStringArray(argNames);
        }
        springAdvice.calculateArgumentBindings();

        return springAdvice;
    }

    /**
     * 合成 Advisor，实例化切面。
     * 在非单例切面上触发 per-clause 切点。
     * 该通知没有任何效果。
     */
    @SuppressWarnings("serial")
    protected static class SyntheticInstantiationAdvisor extends DefaultPointcutAdvisor {

        public SyntheticInstantiationAdvisor(final MetadataAwareAspectInstanceFactory aif) {
            super(aif.getAspectMetadata().getPerClausePointcut(), (MethodBeforeAdvice)
                    (method, args, target) -> aif.getAspectInstance());
        }
    }

}
```

### 八、常见问题

1. **切点表达式问题**

   + 关于如何编写有效的切点表达式，以及如何在表达式中使用 AspectJ 的语法和通配符。

2. **通知类型问题**

   + 关于不同类型的通知（Before、After、Around 等）的使用场景和区别，以及如何在 AspectJ 注解中正确声明和使用它们。

3. **参数绑定问题**

   + 关于如何在通知方法中获取和使用方法参数，以及如何在切点表达式中指定参数名称。

4. **Introduction（引入）问题**

   + 关于如何在切面中引入新的接口或功能，以及如何正确配置和使用 Introduction 相关的注解和通知。

5. **Spring AOP 与 AspectJ 的区别问题**

   + 关于 Spring AOP 和 AspectJ 在实现方式、功能特性和应用场景上的区别，以及何时选择使用哪种方式。