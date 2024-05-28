## EnableTransactionManagement

- [EnableTransactionManagement](#EnableTransactionManagement)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [五、主要实现](#五主要实现)
    - [六、最佳实践](#六最佳实践)
    - [七、源码分析](#七源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`@EnableTransactionManagement` 是 Spring 框架中的注解，用于启用注解驱动的事务管理。通过在配置类上使用该注解，Spring
会自动扫描和处理应用中的 `@Transactional` 注解，从而实现声明性事务管理。它通常与一个 `PlatformTransactionManager`
实例配合使用，以管理和协调数据库事务。

### 三、主要功能

1. **启用事务管理**

    + 通过在配置类上添加 `@EnableTransactionManagement`，Spring
      会自动扫描应用上下文中的所有事务注解（例如 `@Transactional`），并为这些注解提供事务管理的支持。

2. **事务管理器**

    + `@EnableTransactionManagement` 需要一个 `PlatformTransactionManager` 实例来管理事务。通常情况下，Spring
      会自动检测配置中的事务管理器。如果没有配置，Spring 会尝试创建一个默认的事务管理器。

### 四、注解源码

`@EnableTransactionManagement`
注解用于在Spring框架中启用注解驱动的事务管理功能，通过在配置类上添加该注解，Spring会自动扫描和处理应用中的 `@Transactional`
注解，实现声明式事务管理，支持传统的命令式事务管理和响应式事务管理；它允许灵活配置事务管理器，通过 `proxyTargetClass`
属性指定代理类型，通过 `mode` 属性选择代理模式（默认是 `PROXY`，可选 `ASPECTJ`
），并可通过实现 `TransactionManagementConfigurer` 接口明确指定使用的事务管理器，从而为复杂的事务管理需求提供高效的解决方案，同时其默认行为和XML配置方式具有很好的兼容性。

```java
/**
 * 启用Spring的注解驱动事务管理功能，类似于Spring的{@code <tx:*>} XML命名空间中的支持。
 * 该注解用于{@link org.springframework.context.annotation.Configuration @Configuration}类上，
 * 以配置传统的命令式事务管理或响应式事务管理。
 *
 * <p>下面的示例演示了使用{@link org.springframework.transaction.PlatformTransactionManager
 * PlatformTransactionManager}的命令式事务管理。对于响应式事务管理，配置
 * {@link org.springframework.transaction.ReactiveTransactionManager
 * ReactiveTransactionManager}即可。
 *
 * <pre class="code">
 * @Configuration
 * @EnableTransactionManagement
 * public class AppConfig {
 *
 *     @Bean
 *     public FooRepository fooRepository() {
 *         // 配置并返回一个包含@Transactional方法的类
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     @Bean
 *     public DataSource dataSource() {
 *         // 配置并返回所需的JDBC DataSource
 *     }
 *
 *     @Bean
 *     public PlatformTransactionManager txManager() {
 *         return new DataSourceTransactionManager(dataSource());
 *     }
 * }</pre>
 *
 * <p>参考上面的示例，可以与以下Spring XML配置进行比较：
 *
 * <pre class="code">
 * <beans>
 *
 *     <tx:annotation-driven/>
 *
 *     <bean id="fooRepository" class="com.foo.JdbcFooRepository">
 *         <constructor-arg ref="dataSource"/>
 *     </bean>
 *
 *     <bean id="dataSource" class="com.vendor.VendorDataSource"/>
 *
 *     <bean id="transactionManager" class="org.sfwk...DataSourceTransactionManager">
 *         <constructor-arg ref="dataSource"/>
 *     </bean>
 *
 * </beans>
 * </pre>
 *
 * 在上述两种情况下，{@code @EnableTransactionManagement}和{@code
 * <tx:annotation-driven/>}负责注册驱动注解事务管理所需的Spring组件，
 * 如TransactionInterceptor和在调用{@code JdbcFooRepository}的{@code @Transactional}方法时
 * 将拦截器织入调用堆栈的代理或基于AspectJ的建议。
 *
 * <p>两个示例之间的一个小差异在于{@code TransactionManager} bean的命名：
 * 在{@code @Bean}示例中，名称是<em>"txManager"</em>（根据方法名）；在XML示例中，名称是
 * <em>"transactionManager"</em>。{@code <tx:annotation-driven/>}默认硬编码查找名为
 * "transactionManager"的bean，而{@code @EnableTransactionManagement}更加灵活；
 * 它会回退为按类型查找容器中的任何{@code TransactionManager} bean。因此名称可以是
 * "txManager"、"transactionManager"或"tm"：名称无关紧要。
 *
 * <p>对于希望在{@code @EnableTransactionManagement}和要使用的确切事务管理器bean之间建立更直接关系的人，
 * 可以实现{@link TransactionManagementConfigurer}回调接口 - 请注意下面的{@code implements}子句和{@code @Override}注解的方法：
 *
 * <pre class="code">
 * @Configuration
 * @EnableTransactionManagement
 * public class AppConfig implements TransactionManagementConfigurer {
 *
 *     @Bean
 *     public FooRepository fooRepository() {
 *         // 配置并返回一个包含@Transactional方法的类
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     @Bean
 *     public DataSource dataSource() {
 *         // 配置并返回所需的JDBC DataSource
 *     }
 *
 *     @Bean
 *     public PlatformTransactionManager txManager() {
 *         return new DataSourceTransactionManager(dataSource());
 *     }
 *
 *     @Override
 *     public PlatformTransactionManager annotationDrivenTransactionManager() {
 *         return txManager();
 *     }
 * }</pre>
 *
 * <p>这种方法可能只是因为更显式而是可取的，或者可能是为了区分同一容器中存在的两个{@code TransactionManager} bean。
 * 顾名思义，{@code annotationDrivenTransactionManager()}将用于处理{@code @Transactional}方法。
 * 有关详细信息，请参阅{@link TransactionManagementConfigurer} Javadoc。
 *
 * <p>{@link #mode}属性控制如何应用建议：如果模式为{@link AdviceMode#PROXY}（默认），则其他属性控制代理行为。
 * 请注意，代理模式仅允许通过代理拦截调用；同一类中的本地调用无法以这种方式拦截。
 *
 * <p>请注意，如果{@linkplain #mode}设置为{@link AdviceMode#ASPECTJ}，则{@link #proxyTargetClass}属性的值将被忽略。
 * 此外，在这种情况下，{@code spring-aspects}模块JAR必须存在于类路径上，并且编译时织入或加载时织入将该方面应用于受影响的类。
 * 这种情况下没有代理；本地调用也会被拦截。
 *
 * @作者 Chris Beams
 * @作者 Juergen Hoeller
 * @自从 3.1
 * @参见 TransactionManagementConfigurer
 * @参见 TransactionManagementConfigurationSelector
 * @参见 ProxyTransactionManagementConfiguration
 * @参见 org.springframework.transaction.aspectj.AspectJTransactionManagementConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {

    /**
     * 指示是否创建基于子类的（CGLIB）代理（{@code true}），而不是标准的基于Java接口的代理（{@code false}）。
     * 默认值为{@code false}。<strong>仅当{@link #mode()}设置为{@link AdviceMode#PROXY}时适用</strong>。
     * <p>请注意，将此属性设置为{@code true}将影响<em>所有</em>需要代理的Spring管理的bean，不仅仅是那些标有
     * {@code @Transactional}的bean。例如，其他标有Spring的{@code @Async}注解的bean也会同时升级为子类代理。
     * 这种方法在实践中没有负面影响，除非一个明确期望一种类型的代理与另一种类型，例如在测试中。
     */
    boolean proxyTargetClass() default false;

    /**
     * 指示应如何应用事务性建议。
     * <p><b>默认值为{@link AdviceMode#PROXY}。</b>
     * 请注意，代理模式仅允许通过代理拦截调用。同一类中的本地调用无法以这种方式拦截；
     * 在这种运行时情况下，具有{@link Transactional}注解的方法上的拦截器甚至不会触发。
     * 对于更高级的拦截模式，请考虑将其切换为{@link AdviceMode#ASPECTJ}。
     */
    AdviceMode mode() default AdviceMode.PROXY;

    /**
     * 指示在特定连接点应用多个建议时事务顾问的执行顺序。
     * <p>默认值为{@link Ordered#LOWEST_PRECEDENCE}。
     */
    int order() default Ordered.LOWEST_PRECEDENCE;

}
```

### 六、最佳实践

基于注解的应用上下文 `AnnotationConfigApplicationContext`，并加载 `AppConfig`
配置类，然后从该上下文中获取名为 `ScoresService` 的bean，并调用 `ScoresService` 的 `insertScore`
方法，展示了如何在Spring应用中通过注解配置和使用服务类。

```java
public class EnableTransactionManagementDemo {

    public static void main(String[] args) {
        // 创建基于注解的应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从应用上下文中获取ScoresService bean
        ScoresService scoresService = context.getBean(ScoresService.class);
        // 调用ScoresService的方法
        scoresService.insertScore();
    }
}
```

Spring配置类 `AppConfig`，通过注解 `@Configuration`、`@ComponentScan` 和 `@EnableTransactionManagement`
来启用组件扫描和注解驱动的事务管理，并配置了几个 `@Bean`，包括一个 `DataSourceTransactionManager`
来管理事务，一个 `JdbcTemplate` 用于简化JDBC操作，以及一个 `SimpleDriverDataSource`
用于配置数据库连接，连接的数据库是MySQL，指定了连接URL、用户名和密码。

```java

@Configuration
@ComponentScan
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SimpleDriverDataSource dataSource() throws SQLException {
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // 数据库用户名
        String username = "root";
        // 数据库密码
        String password = "123456";
        // 初始化数据源
        return new SimpleDriverDataSource(new Driver(), url, username, password);
    }
}
```

实现了 `ScoresService` 接口，使用 `@Service` 注解将其标记为Spring管理的服务组件，并通过 `@Autowired` 注入 `JdbcTemplate`
；在 `insertScore` 方法中，通过 `@Transactional` 注解启用事务管理，方法生成一个随机分数并插入到数据库中，并打印受影响的行数，同时包含一个被注释掉的异常模拟代码，用于测试事务回滚机制。

```java

@Service
public class ScoresServiceImpl implements ScoresService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void insertScore() {
        long id = System.currentTimeMillis();
        int score = new Random().nextInt(100);
        // 向数据库中插入随机生成的分数
        int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
        // 模拟异常，用于测试事务回滚
        // int i = 1 / 0;
        // 打印影响行数
        System.out.println("scores row = " + row);
    }
}
```

### 七、源码分析

在`org.springframework.transaction.annotation.TransactionManagementConfigurationSelector`
类中，继承自 `AdviceModeImportSelector<EnableTransactionManagement>`，根据 `@EnableTransactionManagement` 注解的 `mode`
属性值选择合适的事务管理配置类。如果 `mode` 是 `PROXY`，则返回 `AutoProxyRegistrar`
和 `ProxyTransactionManagementConfiguration` 类名；如果是 `ASPECTJ`
，则根据类路径中是否存在 `javax.transaction.Transactional` 类来决定返回 JTA 事务切面配置类名或非 JTA
事务切面配置类名。这样可以灵活地根据不同的事务管理模式选择合适的实现来配置 Spring 应用的事务管理。

```java
/**
 * 根据导入的 {@code @Configuration} 类上 {@link EnableTransactionManagement#mode} 的值
 * 选择应使用的 {@link AbstractTransactionManagementConfiguration} 实现。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see EnableTransactionManagement
 * @see ProxyTransactionManagementConfiguration
 * @see TransactionManagementConfigUtils#TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME
 * @see TransactionManagementConfigUtils#JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME
 */
public class TransactionManagementConfigurationSelector extends AdviceModeImportSelector<EnableTransactionManagement> {

    /**
     * 根据事务管理模式返回相应的配置类。
     *
     * @param adviceMode 事务管理模式，可以是 PROXY 或 ASPECTJ
     * @return 包含事务管理配置类名的数组
     */
    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                // 如果是 PROXY 模式，返回 AutoProxyRegistrar 和 ProxyTransactionManagementConfiguration 类名
                return new String[]{AutoProxyRegistrar.class.getName(),
                        ProxyTransactionManagementConfiguration.class.getName()};
            case ASPECTJ:
                // 如果是 ASPECTJ 模式，返回确定的事务切面配置类名
                return new String[]{determineTransactionAspectClass()};
            default:
                // 如果模式不是 PROXY 或 ASPECTJ，返回 null
                return null;
        }
    }

    /**
     * 确定要使用的事务切面配置类。
     *
     * @return JTA 或非 JTA 事务切面配置类名
     */
    private String determineTransactionAspectClass() {
        // 检查类路径中是否存在 javax.transaction.Transactional
        return (ClassUtils.isPresent("javax.transaction.Transactional", getClass().getClassLoader()) ?
                // 如果存在，返回 JTA 事务切面配置类名
                TransactionManagementConfigUtils.JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME :
                // 如果不存在，返回非 JTA 事务切面配置类名
                TransactionManagementConfigUtils.TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME);
    }
}
```

在`org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration`类中，用于注册启用基于代理的注解驱动事务管理所需的
Spring 基础设施 bean。它包括注册事务通知的 `BeanFactoryTransactionAttributeSourceAdvisor`
、事务属性源的 `AnnotationTransactionAttributeSource` 和事务拦截器的 `TransactionInterceptor`。这些 bean 的注册使得 Spring
能够拦截带有 `@Transactional` 注解的方法调用，并应用事务管理功能。

```java
/**
 * {@code @Configuration} 类，注册了启用基于代理的注解驱动事务管理所需的Spring基础设施bean。
 *
 * @author Chris Beams
 * @author Sebastien Deleuze
 * @since 3.1
 * @see EnableTransactionManagement
 * @see TransactionManagementConfigurationSelector
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyTransactionManagementConfiguration extends AbstractTransactionManagementConfiguration {

    /**
     * 注册事务通知的Bean。
     *
     * @param transactionAttributeSource 事务属性源
     * @param transactionInterceptor    事务拦截器
     * @return BeanFactoryTransactionAttributeSourceAdvisor 实例
     */
    @Bean(name = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor(
            TransactionAttributeSource transactionAttributeSource, TransactionInterceptor transactionInterceptor) {

        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(transactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);
        if (this.enableTx != null) {
            advisor.setOrder(this.enableTx.<Integer>getNumber("order"));
        }
        return advisor;
    }

    /**
     * 注册事务属性源的Bean。
     *
     * @return AnnotationTransactionAttributeSource 实例
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionAttributeSource transactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }

    /**
     * 注册事务拦截器的Bean。
     *
     * @param transactionAttributeSource 事务属性源
     * @return TransactionInterceptor 实例
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionInterceptor transactionInterceptor(TransactionAttributeSource transactionAttributeSource) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionAttributeSource(transactionAttributeSource);
        if (this.txManager != null) {
            interceptor.setTransactionManager(this.txManager);
        }
        return interceptor;
    }

}
```

在`org.springframework.context.annotation.AutoProxyRegistrar`类中，实现了 `ImportBeanDefinitionRegistrar`
接口，用于根据 `@Enable*` 注解的 `mode` 和 `proxyTargetClass`
属性设置适当的自动代理创建器，并将其注册到当前的 `BeanDefinitionRegistry` 中。该类在导入的 `@Configuration`
类上查找具有正确 `mode` 和 `proxyTargetClass` 属性的最近的注解，并根据其设置注册和配置自动代理创建器。

```java
/**
 * 根据 {@code @Enable*} 注解的 {@code mode} 和 {@code proxyTargetClass} 属性设置适当的
 * 自动代理创建器 (Auto Proxy Creator, APC)，并将其注册到当前的 {@link BeanDefinitionRegistry} 中。 
 *
 * 该类通过在导入的 {@code @Configuration} 类上查找具有 {@code mode} 和 {@code proxyTargetClass}
 * 属性的最近的注解来工作。如果 {@code mode} 设置为 {@code PROXY}，则注册 APC；如果 
 * {@code proxyTargetClass} 设置为 {@code true}，则强制 APC 使用子类（CGLIB）代理。
 *
 * 多个 {@code @Enable*} 注解公开了 {@code mode} 和 {@code proxyTargetClass} 属性。重要的是
 * 注意，大多数这些功能最终会共享一个 {@linkplain AopConfigUtils#AUTO_PROXY_CREATOR_BEAN_NAME 单个 APC}。
 * 因此，此实现不关心确切地找到哪个注解，只要它公开正确的 {@code mode} 和 {@code proxyTargetClass}
 * 属性，就可以注册和配置 APC。
 *
 * @作者 Chris Beams
 * @自 3.1
 * @见 org.springframework.cache.annotation.EnableCaching
 * @见 org.springframework.transaction.annotation.EnableTransactionManagement
 */
public class AutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * 根据导入的类的元数据和Bean定义注册表，注册、升级和配置标准的自动代理创建器（APC）。
     * 通过在导入的 {@code @Configuration} 类上找到最近的带有 {@code mode} 和 {@code proxyTargetClass}
     * 属性的注解来工作。如果 {@code mode} 设置为 {@code PROXY}，则注册 APC；如果 
     * {@code proxyTargetClass} 设置为 {@code true}，则强制 APC 使用子类（CGLIB）代理。
     * <p>
     * 多个 {@code @Enable*} 注解公开了 {@code mode} 和 {@code proxyTargetClass} 属性。重要的是
     * 注意，大多数这些功能最终会共享一个 {@linkplain AopConfigUtils#AUTO_PROXY_CREATOR_BEAN_NAME 单个 APC}。
     * 因此，此实现不关心确切地找到哪个注解，只要它公开正确的 {@code mode} 和 {@code proxyTargetClass}
     * 属性，就可以注册和配置 APC。
     *
     * @param importingClassMetadata 导入类的元数据
     * @param registry Bean定义注册表
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 是否找到候选注解
        boolean candidateFound = false;
        // 获取导入类的所有注解类型
        Set<String> annTypes = importingClassMetadata.getAnnotationTypes();
        // 遍历所有注解类型
        for (String annType : annTypes) {
            // 获取注解的属性
            AnnotationAttributes candidate = AnnotationConfigUtils.attributesFor(importingClassMetadata, annType);
            // 如果属性为空，则继续下一个循环
            if (candidate == null) {
                continue;
            }
            // 获取 mode 和 proxyTargetClass 属性
            Object mode = candidate.get("mode");
            Object proxyTargetClass = candidate.get("proxyTargetClass");
            // 如果 mode 和 proxyTargetClass 属性不为空，并且它们的类型分别是 AdviceMode 和 Boolean
            if (mode != null && proxyTargetClass != null && AdviceMode.class == mode.getClass() &&
                    Boolean.class == proxyTargetClass.getClass()) {
                candidateFound = true;
                // 如果 mode 是 AdviceMode.PROXY
                if (mode == AdviceMode.PROXY) {
                    // 注册 APC
                    AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
                    // 如果 proxyTargetClass 是 true
                    if ((Boolean) proxyTargetClass) {
                        // 强制 APC 使用类代理
                        AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
                        return;
                    }
                }
            }
        }
        // 如果未找到候选注解，并且 logger 是启用的
        if (!candidateFound && logger.isInfoEnabled()) {
            String name = getClass().getSimpleName();
            logger.info(String.format("%s was imported but no annotations were found " +
                    "having both 'mode' and 'proxyTargetClass' attributes of type " +
                    "AdviceMode and boolean respectively. This means that auto proxy " +
                    "creator registration and configuration may not have occurred as " +
                    "intended, and components may not be proxied as expected. Check to " +
                    "ensure that %s has been @Import'ed on the same class where these " +
                    "annotations are declared; otherwise remove the import of %s " +
                    "altogether.", name, name, name));
        }
    }

}
```