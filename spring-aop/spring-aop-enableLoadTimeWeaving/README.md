## @EnableLoadTimeWeaving

- [@EnableLoadTimeWeaving](#enableloadtimeweaving)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、注解源码](#四注解源码)
    - [五、最佳实践](#五最佳实践)
    - [六、源码分析](#六源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`@EnableLoadTimeWeaving` 是 Spring 框架提供的注解，用于启用加载时编织（Load Time Weaving，LTW），允许在类加载过程中动态织入切面逻辑，以实现诸如日志记录、性能监控等横切关注点的功能。

### 三、主要功能

1. **启用加载时编织（LTW）** 

   + 允许在类加载的过程中动态地织入切面逻辑，而无需修改源代码或者使用特定的编译器。

2. **支持横切关注点的实现** 

   + 通过加载时编织，可以将横切关注点与应用的核心业务逻辑分离，提高代码的模块化和可维护性。

3. **灵活性和可配置性** 

   + 可以通过 AspectJ 提供的丰富语法和功能，灵活地定义切点和切面逻辑，以满足各种复杂的应用场景。

4. **不依赖源代码** 

   + 加载时编织不依赖于源代码的修改或特殊的编译器，因此可以在已有的应用中轻松地引入切面逻辑，而无需对现有代码进行重构。

### 四、注解源码

注解 `@EnableLoadTimeWeaving`，用于激活 Spring 应用上下文中的加载时编织（Load Time Weaving）。通过该注解，可以方便地配置加载时编织，类似于 Spring XML 配置中的 `<context:load-time-weaver>` 元素。同时，还可以通过 `aspectjWeaving()` 属性控制是否启用基于 AspectJ 的编织，提供了灵活的配置选项。

```java
/**
 * 激活一个 Spring {@link LoadTimeWeaver} 用于该应用程序上下文，可作为一个名为 "loadTimeWeaver" 的 bean 使用，
 * 类似于 Spring XML 中的 {@code <context:load-time-weaver>} 元素。
 *
 * <p>要在 @{@link org.springframework.context.annotation.Configuration Configuration} 类上使用；
 * 最简单的示例如下
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving
 * public class AppConfig {
 *
 *     // 应用特定的 &#064;Bean 定义...
 * }</pre>
 *
 * 上面的示例等价于以下的 Spring XML 配置
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver/&gt;
 *
 *     &lt;!-- 应用特定的 &lt;bean&gt; 定义 --&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * <h2>{@code LoadTimeWeaverAware} 接口</h2>
 * 任何实现 {@link org.springframework.context.weaving.LoadTimeWeaverAware LoadTimeWeaverAware} 接口的 bean
 * 都将自动接收到 {@code LoadTimeWeaver} 引用；例如，Spring 的 JPA 启动支持。
 *
 * <h2>定制 {@code LoadTimeWeaver}</h2>
 * 默认的 weaver 将自动确定参见 {@link DefaultContextLoadTimeWeaver}。
 *
 * <p>要定制使用的 weaver，{@code @Configuration} 类可以实现 {@link LoadTimeWeavingConfigurer} 接口，并通过
 * {@code #getLoadTimeWeaver} 方法返回一个自定义的 {@code LoadTimeWeaver} 实例
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving
 * public class AppConfig implements LoadTimeWeavingConfigurer {
 *
 *     &#064;Override
 *     public LoadTimeWeaver getLoadTimeWeaver() {
 *         MyLoadTimeWeaver ltw = new MyLoadTimeWeaver();
 *         ltw.addClassTransformer(myClassFileTransformer);
 *         // ...
 *         return ltw;
 *     }
 * }</pre>
 *
 * 上面的示例可与以下 Spring XML 配置进行比较
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver weaverClass="com.acme.MyLoadTimeWeaver"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * 代码示例与 XML 示例的区别在于它实际上实例化了 {@code MyLoadTimeWeaver} 类型，这意味着它还可以配置实例，
 * 例如调用 {@code #addClassTransformer} 方法。这展示了基于代码的配置方法通过直接编程访问更加灵活。
 *
 * <h2>启用基于 AspectJ 的编织</h2>
 * 可通过 {@link #aspectjWeaving()} 属性启用 AspectJ 加载时编织，这将导致通过 {@link LoadTimeWeaver#addTransformer}
 * 注册 {@linkplain org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter AspectJ 类转换器}。如果类路径中存在
 * "META-INF/aop.xml" 资源，则默认情况下将激活 AspectJ 编织。示例
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving(aspectjWeaving=ENABLED)
 * public class AppConfig {
 * }</pre>
 *
 * 上面的示例可与以下 Spring XML 配置进行比较
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver aspectj-weaving="on"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * 这两个示例是等价的，但有一个重要的例外在 XML 的情况下，当 {@code aspectj-weaving} 是 "on" 时，
 * {@code <context:spring-configured>} 的功能将自动启用。在使用 {@code @EnableLoadTimeWeaving(aspectjWeaving=ENABLED)}
 * 时，这种情况不会发生。相反，您必须显式添加 {@code @EnableSpringConfigured}（包含在 {@code spring-aspects} 模块中）。
 *
 * @author Chris Beams
 * @since 3.1
 * @see LoadTimeWeaver
 * @see DefaultContextLoadTimeWeaver
 * @see org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoadTimeWeavingConfiguration.class)
public @interface EnableLoadTimeWeaving {

    /**
     * 是否启用 AspectJ 编织。
     */
    AspectJWeaving aspectjWeaving() default AspectJWeaving.AUTODETECT;

    /**
     * AspectJ 编织启用选项。
     */
    enum AspectJWeaving {

        /**
         * 启用基于 Spring 的 AspectJ 加载时编织。
         */
        ENABLED,

        /**
         * 关闭基于 Spring 的 AspectJ 加载时编织（即使类路径上存在 "META-INF/aop.xml" 资源）。
         */
        DISABLED,

        /**
         * 如果类路径上存在 "META-INF/aop.xml" 资源，则启用 AspectJ 加载时编织。
         * 如果没有此类资源，则关闭 AspectJ 加载时编织。
         */
        AUTODETECT;
    }

}
```

### 五、最佳实践

使用加载时编织（Load Time Weaving）功能。首先，它创建了一个基于注解的 Spring 应用程序上下文，并通过 `AppConfig`
类配置了应用程序的相关组件。创建了一个 `MyService` 的普通实例，并调用了其 `foo` 方法。

```java
public class EnableLoadTimeWeavingDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MyService myService = new MyService();
        myService.foo();
        context.close();
    }
}
```

通过 `@Configuration` 注解表明这是一个配置类，而 `@EnableLoadTimeWeaving` 注解则启用了加载时编织功能。

```java
@Configuration
@EnableLoadTimeWeaving
public class AppConfig {

}
```

定义了一个切面类 `MyLTWAspect`，用于实现加载时编织（Load Time Weaving）功能。通过 `@Aspect`
注解标记该类为一个切面，并在其中定义了一个环绕通知方法 `around`
，用于在目标方法调用前后执行特定逻辑。在该方法中，首先输出了目标方法的名称，然后调用了原始方法，并输出了方法返回值。同时，通过 `@Pointcut`
注解定义了一个切点 `ltwPointcut()`，指定了需要被切入的目标方法，这里是 `com.xcs.spring.MyService` 类中的所有公共方法。

```java
@Aspect
public class MyLTWAspect {

    @Around("ltwPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 在方法调用之前执行的逻辑
        System.out.println("Before Method " + pjp.getSignature().getName());
        // 调用原始方法
        Object result = pjp.proceed();
        // 在方法调用之后执行的逻辑
        System.out.println("After Method " + pjp.getSignature().getName());
        return result;
    }

    @Pointcut("execution(public * com.xcs.spring.MyService.*(..))")
    public void ltwPointcut() {
    }
}
```

定义了加载时编织（Load Time Weaving）的规则和切面配置。在 `<weaver>` 元素中指定了仅对应用程序特定包中的类进行编织，这里是 `com.xcs.spring` 包及其子包下的所有类。然后，在 `<aspects>` 元素中指定了要编织的切面，即 `com.xcs.spring.MyLTWAspect`。

```java
<!DOCTYPE aspectj PUBLIC "-//AspectJ//DTD//EN" "https://www.eclipse.org/aspectj/dtd/aspectj.dtd">
<aspectj>

    <weaver>
        <!-- only weave classes in our application-specific packages -->
        <include within="com.xcs.spring..*"/>
    </weaver>

    <aspects>
        <!-- weave in just this aspect -->
        <aspect name="com.xcs.spring.MyLTWAspect"/>
    </aspects>

</aspectj>
```

`MyService` 类定义了一个简单的方法 `foo()`。

```java
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

这是启动参数，使用AspectJ Weaver和Spring Instrumentation实现加载时编织。它确保了在应用程序启动时启用了加载时编织，使AspectJ切面能够拦截和处理方法调用。

> 使用自定义的jar包存放位置（如aspectjweaver-1.9.7.jar，spring-instrument-5.3.10.jar）时，注意确保在引用这些jar包时路径替换的正确性。在启动参数或配置文件中指定的路径应该与实际jar包存放位置一致，以避免加载时编织或其他功能无法正常工作。

```shell
java -javaagent:D:\tools\repository\org\aspectj\aspectjweaver\1.9.7\aspectjweaver-1.9.7.jar -javaagent:D:\tools\repository\org\springframework\spring-instrument\5.3.10\spring-instrument-5.3.10.jar -Dfile.encoding=UTF-8 com.xcs.spring.EnableLoadTimeWeavingDemo
```

运行结果，直接使用 `new` 操作符创建的 `MyService` 对象。在调用 `foo()`
方法时，都会先打印方法被调用的消息，然后执行原始的方法逻辑（打印 "foo..."），这证明切面 `MyLTWAspect`
中定义的逻辑在目标方法调用前后得到了执行，不论是从 Spring 容器中获取的 bean 还是直接创建的对象，都受到了拦截。

```java
Before Method
foo
foo...
After Method
foo
```

### 六、源码分析

`LoadTimeWeavingConfiguration` 类，负责注册一个 `LoadTimeWeaver` bean，用于启用加载时编织（Load-Time Weaving）功能。在应用中使用 `@EnableLoadTimeWeaving` 注解时，这个配置类会被自动导入。它通过检查 `EnableLoadTimeWeaving` 注解的属性来决定是否启用 AspectJ 编织功能，并根据配置创建相应的 `LoadTimeWeaver` 实例。如果用户提供了自定义的 `LoadTimeWeavingConfigurer` 实例，则会使用用户提供的实例；否则，会创建一个默认的 `DefaultContextLoadTimeWeaver` 实例作为 `LoadTimeWeaver`。根据 `EnableLoadTimeWeaving` 注解中的配置，决定是否启用 AspectJ 编织功能，并根据情况调用 `AspectJWeavingEnabler` 中的方法来实现编织。

```java
/**
 * {@code @Configuration} 类，注册一个 {@link LoadTimeWeaver} bean。
 *
 * <p>当使用 {@link EnableLoadTimeWeaving} 注解时，这个配置类会自动导入。
 * 完整的使用详情请参阅 {@code @EnableLoadTimeWeaving} 的 javadoc。
 * 
 * <p>作者Chris Beams
 * 
 * @since 3.1
 * @see LoadTimeWeavingConfigurer
 * @see ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class LoadTimeWeavingConfiguration implements ImportAware, BeanClassLoaderAware {

    @Nullable
    private AnnotationAttributes enableLTW;

    @Nullable
    private LoadTimeWeavingConfigurer ltwConfigurer;

    @Nullable
    private ClassLoader beanClassLoader;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        // 获取 @EnableLoadTimeWeaving 注解的属性
        this.enableLTW = AnnotationConfigUtils.attributesFor(importMetadata, EnableLoadTimeWeaving.class);
        if (this.enableLTW == null) {
            throw new IllegalArgumentException(
                    "@EnableLoadTimeWeaving is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required = false)
    public void setLoadTimeWeavingConfigurer(LoadTimeWeavingConfigurer ltwConfigurer) {
        // 设置用户自定义的 LoadTimeWeavingConfigurer 实例
        this.ltwConfigurer = ltwConfigurer;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        // 设置类加载器
        this.beanClassLoader = beanClassLoader;
    }

    /**
     * 注册 LoadTimeWeaver bean。
     */
    @Bean(name = ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LoadTimeWeaver loadTimeWeaver() {
        Assert.state(this.beanClassLoader != null, "No ClassLoader set");
        LoadTimeWeaver loadTimeWeaver = null;

        if (this.ltwConfigurer != null) {
            // 用户提供了自定义的 LoadTimeWeaver 实例
            loadTimeWeaver = this.ltwConfigurer.getLoadTimeWeaver();
        }

        if (loadTimeWeaver == null) {
            // 没有提供自定义的 LoadTimeWeaver -> 使用默认的
            loadTimeWeaver = new DefaultContextLoadTimeWeaver(this.beanClassLoader);
        }

        if (this.enableLTW != null) {
            // 获取启用 AspectJ 编织的配置
            AspectJWeaving aspectJWeaving = this.enableLTW.getEnum("aspectjWeaving");
            switch (aspectJWeaving) {
                case DISABLED:
                    // AspectJ 编织被禁用 -> 什么也不做
                    break;
                case AUTODETECT:
                    if (this.beanClassLoader.getResource(AspectJWeavingEnabler.ASPECTJ_AOP_XML_RESOURCE) == null) {
                        // 类路径上没有 aop.xml -> 视为 'disabled'
                        break;
                    }
                    // 类路径上有 aop.xml -> 启用编织
                    AspectJWeavingEnabler.enableAspectJWeaving(loadTimeWeaver, this.beanClassLoader);
                    break;
                case ENABLED:
                    // 启用 AspectJ 编织
                    AspectJWeavingEnabler.enableAspectJWeaving(loadTimeWeaver, this.beanClassLoader);
                    break;
            }
        }

        return loadTimeWeaver;
    }
}
```

`AspectJWeavingEnabler` 的后处理器，它实现了多个接口，包括 `BeanFactoryPostProcessor`、`BeanClassLoaderAware`、`LoadTimeWeaverAware` 和 `Ordered`。它的主要作用是在 Spring 应用程序上下文中注册 AspectJ 的 `ClassPreProcessorAgentAdapter`，并与默认的 `LoadTimeWeaver` 进行关联。其中，`enableAspectJWeaving` 方法用于启用 AspectJ 编织功能，而 `AspectJClassBypassingClassFileTransformer` 类则实现了一个用于绕过 AspectJ 类处理的装饰器，以避免潜在的链接错误。

```java
/**
 * 后处理器，将 AspectJ 的 {@link org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter}
 * 注册到 Spring 应用程序上下文的默认 {@link org.springframework.instrument.classloading.LoadTimeWeaver} 中。
 * 用于启用 AspectJ 的编织功能。
 * 
 * <p>作者Juergen Hoeller，Ramnivas Laddad
 * 
 * @since 2.5
 */
public class AspectJWeavingEnabler
        implements BeanFactoryPostProcessor, BeanClassLoaderAware, LoadTimeWeaverAware, Ordered {

    /**
     * {@code aop.xml} 资源位置。
     */
    public static final String ASPECTJ_AOP_XML_RESOURCE = "META-INF/aop.xml";

    @Nullable
    private ClassLoader beanClassLoader;

    @Nullable
    private LoadTimeWeaver loadTimeWeaver;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 启用 AspectJ 编织
        enableAspectJWeaving(this.loadTimeWeaver, this.beanClassLoader);
    }

    /**
     * 使用给定的 {@link LoadTimeWeaver} 启用 AspectJ 编织。
     * 
     * @param weaverToUse 要应用的 LoadTimeWeaver（或 {@code null} 表示使用默认的 weaver）
     * @param beanClassLoader 如果需要，为其创建默认 weaver 的类加载器
     */
    public static void enableAspectJWeaving(
            @Nullable LoadTimeWeaver weaverToUse, @Nullable ClassLoader beanClassLoader) {
        if (weaverToUse == null) {
            if (InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
                // 如果可以使用 Instrumentation，创建 InstrumentationLoadTimeWeaver
                weaverToUse = new InstrumentationLoadTimeWeaver(beanClassLoader);
            }
            else {
                throw new IllegalStateException("No LoadTimeWeaver available");
            }
        }
        // 添加一个 ClassFileTransformer，用于绕过 AspectJ 类的处理，以避免潜在的 LinkageErrors
        weaverToUse.addTransformer(
                new AspectJClassBypassingClassFileTransformer(new ClassPreProcessorAgentAdapter()));
    }

    /**
     * ClassFileTransformer 的装饰器，用于禁止处理 AspectJ 类，以避免潜在的 LinkageErrors。
     * 
     * @see org.springframework.context.annotation.LoadTimeWeavingConfiguration
     */
    private static class AspectJClassBypassingClassFileTransformer implements ClassFileTransformer {

        private final ClassFileTransformer delegate;

        public AspectJClassBypassingClassFileTransformer(ClassFileTransformer delegate) {
            this.delegate = delegate;
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (className.startsWith("org.aspectj") || className.startsWith("org/aspectj")) {
                // 如果是 AspectJ 类，则直接返回原始字节码
                return classfileBuffer;
            }
            // 否则，调用委托的 ClassFileTransformer 处理字节码
            return this.delegate.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }
    }
}
```
