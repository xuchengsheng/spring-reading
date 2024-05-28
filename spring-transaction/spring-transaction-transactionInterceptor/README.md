## TransactionInterceptor

- [TransactionInterceptor](#transactioninterceptor)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、最佳实践](#四最佳实践)
    - [五、源码分析](#五源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`TransactionInterceptor` 类是 Spring
框架中的一个核心组件，用于实现声明式事务管理。它通过拦截方法调用，根据事务属性（如传播行为、隔离级别等）来控制事务的开始、提交和回滚，确保在方法执行过程中事务的一致性和完整性。

### 三、主要功能

1. **获取事务属性**

    + 从方法或类的元数据中获取事务属性（如传播行为、隔离级别等）。

2. **事务管理器决策**

    + 根据事务属性和当前的事务上下文（如是否存在活动事务）来决定是创建一个新事务、加入现有事务还是不需要事务。

3. **事务控制**

    + 通过 `TransactionManager` 来控制事务的开始、提交和回滚。

4. **异常处理**

    + 在方法执行过程中，如果捕获到异常，根据事务属性配置来决定是否回滚事务。

### 四、最佳实践

通过 `SimpleDriverDataSource` 创建了数据库连接池，然后使用 `DataSourceTransactionManager`
进行事务管理。通过 `JdbcTemplate` 执行 SQL 语句，并使用 `AnnotationTransactionAttributeSource` 和 `TransactionInterceptor`
来定义事务的属性和拦截方法调用，以确保方法在事务中执行。最后，通过 `ProxyFactory` 创建代理对象，并调用代理对象的方法，使方法的执行受到声明式事务的控制。

```java
public class TransactionInterceptorDemo {

    public static void main(String[] args) throws SQLException {
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // 数据库用户名
        String username = "root";
        // 数据库密码
        String password = "123456";

        // 创建 SimpleDriverDataSource 对象，用于管理数据源
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(new Driver(), url, username, password);
        // 创建 DataSourceTransactionManager 对象，用于管理事务
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        // 创建 JdbcTemplate 对象，用于执行 SQL 语句
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // 创建 AnnotationTransactionAttributeSource 对象，用于获取事务属性
        AnnotationTransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();

        // 创建 TransactionInterceptor 对象，用于拦截方法调用并管理事务
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionAttributeSource(transactionAttributeSource);
        transactionInterceptor.setTransactionManager(transactionManager);

        // 创建 BeanFactoryTransactionAttributeSourceAdvisor 对象，用于配置事务拦截器
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(transactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);

        // 创建 ProxyFactory 对象，用于创建代理对象
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addAdvisor(advisor);
        proxyFactory.setTarget(new ScoresServiceImpl(jdbcTemplate));

        // 获取代理对象，并调用其方法
        ScoresService scoresService = (ScoresService) proxyFactory.getProxy();
        scoresService.insertScore();
    }
}
```

### 五、源码分析

在`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`方法中， `TransactionInterceptor`
类中实现了`MethodInterceptor`的 `invoke` 方法，它是 Spring AOP
在事务管理方面的核心实现。通过拦截方法调用并根据事务配置信息，确保在方法执行前开启事务、方法执行后根据结果提交或回滚事务，从而保证数据操作的一致性和完整性。

```java

@Override
@Nullable
public Object invoke(MethodInvocation invocation) throws Throwable {
    // 确定目标类：可能为 {@code null}。
    // TransactionAttributeSource 应该传递目标类和方法，方法可能来自接口。
    Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

    // 适配到 TransactionAspectSupport 的 invokeWithinTransaction 方法...
    return invokeWithinTransaction(invocation.getMethod(), targetClass, new CoroutinesInvocationCallback() {

        @Override
        @Nullable
        public Object proceedWithInvocation() throws Throwable {
            return invocation.proceed();
        }

        @Override
        public Object getTarget() {
            return invocation.getThis();
        }

        @Override
        public Object[] getArguments() {
            return invocation.getArguments();
        }
    });
}
```

在`org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`
方法中，根据方法的事务属性确定是否需要事务管理，然后选择合适的事务管理器执行事务操作，包括事务的开始、提交和回滚。如果方法执行过程中抛出异常，则根据事务状态进行事务回滚，并重新抛出异常；在方法正常返回时根据配置的回滚规则设置回滚标志，并提交事务。

```java
/**
 * 在基于环绕通知的子类中的通用委托，委托给该类上的几个其他模板方法。
 * 能够处理 {@link CallbackPreferringPlatformTransactionManager}、常规 {@link PlatformTransactionManager} 实现，
 * 以及用于响应式返回类型的 {@link ReactiveTransactionManager} 实现。
 *
 * @param method 被调用的方法
 * @param targetClass 我们正在调用方法的目标类
 * @param invocation 用于进行目标调用的回调
 * @return 方法的返回值（如果有）
 * @throws Throwable 从目标调用中传播的异常
 */
@Nullable
protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
                                         final InvocationCallback invocation) throws Throwable {

    // 如果事务属性为 null，则方法是非事务性的。
    TransactionAttributeSource tas = getTransactionAttributeSource();
    final TransactionAttribute txAttr = (tas != null ? tas.getTransactionAttribute(method, targetClass) : null);
    final TransactionManager tm = determineTransactionManager(txAttr);

    // 如果存在响应式适配器并且 tm 是 ReactiveTransactionManager 类型，则...
    if (this.reactiveAdapterRegistry != null && tm instanceof ReactiveTransactionManager) {
        // ... [代码部分省略以简化]
    }

    // 将 tm 转换为 PlatformTransactionManager
    PlatformTransactionManager ptm = asPlatformTransactionManager(tm);
    final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);

    // 如果 txAttr 为 null 或者 ptm 不是 CallbackPreferringPlatformTransactionManager 类型
    if (txAttr == null || !(ptm instanceof CallbackPreferringPlatformTransactionManager)) {
        // 使用 getTransaction 和 commit/rollback 调用进行标准事务划分。
        TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

        Object retVal;
        try {
            // 这是一个环绕通知：调用链中的下一个拦截器。
            // 这通常会导致目标对象被调用。
            retVal = invocation.proceedWithInvocation();
        } catch (Throwable ex) {
            // 目标调用异常
            completeTransactionAfterThrowing(txInfo, ex);
            throw ex;
        } finally {
            cleanupTransactionInfo(txInfo);
        }

        // 如果 retVal 不为 null 且存在 vavrPresent 并且 retVal 是 Vavr Try 类型
        if (retVal != null && vavrPresent && VavrDelegate.isVavrTry(retVal)) {
            // 根据回滚规则在 Vavr 失败时设置仅回滚...
            TransactionStatus status = txInfo.getTransactionStatus();
            if (status != null && txAttr != null) {
                retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status);
            }
        }

        // 在方法正常返回后提交事务
        commitTransactionAfterReturning(txInfo);
        return retVal;
    } else {
        // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.transaction.interceptor.TransactionAspectSupport#createTransactionIfNecessary`
方法中，如果事务属性存在且未指定事务名称，则使用方法标识作为事务名称。然后，如果事务属性和事务管理器都存在，则通过事务管理器获取事务状态；如果没有配置事务管理器，则记录调试日志表示跳过该事务连接点。最后，通过调用 `prepareTransactionInfo`
方法，准备并返回包含事务信息的 `TransactionInfo` 对象，无论是否创建了事务都返回该对象。

```java
/**
 * 根据给定的 TransactionAttribute 创建一个事务（如果有必要）。
 * <p>允许调用者通过 TransactionAttributeSource 执行自定义的 TransactionAttribute 查找。
 *
 * @param tm 事务管理器（可能为 {@code null}）
 * @param txAttr 事务属性（可能为 {@code null}）
 * @param joinpointIdentification 完全限定的方法名（用于监控和日志记录）
 * @return 一个 TransactionInfo 对象，不论是否创建了事务。
 * 可以使用 TransactionInfo 的 {@code hasTransaction()} 方法来判断是否创建了事务。
 * @see #getTransactionAttributeSource()
 */
@SuppressWarnings("serial")
protected TransactionInfo createTransactionIfNecessary(@Nullable PlatformTransactionManager tm,
                                                       @Nullable TransactionAttribute txAttr, final String joinpointIdentification) {

    // 如果事务属性不为 null 且事务名称为 null，则使用方法标识作为事务名称。
    if (txAttr != null && txAttr.getName() == null) {
        txAttr = new DelegatingTransactionAttribute(txAttr) {
            @Override
            public String getName() {
                return joinpointIdentification;
            }
        };
    }

    TransactionStatus status = null;
    if (txAttr != null) {
        if (tm != null) {
            // 如果事务属性不为 null 且事务管理器不为 null，则通过事务管理器获取事务状态。
            status = tm.getTransaction(txAttr);
        } else {
            // 如果事务管理器为 null，记录调试日志表示跳过事务连接点。
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping transactional joinpoint [" + joinpointIdentification +
                        "] because no transaction manager has been configured");
            }
        }
    }
    // 准备并返回 TransactionInfo 对象。
    return prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
}
```

在`org.springframework.transaction.interceptor.TransactionAspectSupport#prepareTransactionInfo`
方法中，通过给定的事务属性和状态对象创建并准备一个 `TransactionInfo` 对象。如果事务属性不为
null，表示该方法需要事务，并在调试日志中记录获取事务的信息，并通过 `newTransactionStatus` 方法设置事务状态；如果事务属性为
null，表示该方法不需要事务，仅创建 `TransactionInfo`
对象以维护线程局部变量的完整性。无论是否创建新事务，总是将 `TransactionInfo` 绑定到线程，以确保 `TransactionInfo` 堆栈被正确管理。

```java
/**
 * 为给定的事务属性和状态对象准备一个 TransactionInfo。
 * @param tm 事务管理器（可能为 {@code null}）
 * @param txAttr 事务属性（可能为 {@code null}）
 * @param joinpointIdentification 完全限定的方法名（用于监控和日志记录）
 * @param status 当前事务的 TransactionStatus
 * @return 准备好的 TransactionInfo 对象
 */
protected TransactionInfo prepareTransactionInfo(@Nullable PlatformTransactionManager tm,
                                                 @Nullable TransactionAttribute txAttr, String joinpointIdentification,
                                                 @Nullable TransactionStatus status) {

    // 创建一个 TransactionInfo 对象
    TransactionInfo txInfo = new TransactionInfo(tm, txAttr, joinpointIdentification);
    if (txAttr != null) {
        // 如果事务属性不为 null，表示该方法需要事务
        if (logger.isTraceEnabled()) {
            logger.trace("Getting transaction for [" + txInfo.getJoinpointIdentification() + "]");
        }
        // 事务管理器会在已经存在不兼容事务时标记错误
        txInfo.newTransactionStatus(status);
    } else {
        // 如果事务属性为 null，表示该方法不需要事务，仅创建 TransactionInfo 以维护线程局部变量的完整性
        if (logger.isTraceEnabled()) {
            logger.trace("No need to create transaction for [" + joinpointIdentification +
                    "]: This method is not transactional.");
        }
    }

    // 我们总是将 TransactionInfo 绑定到线程，即使我们没有在这里创建新事务。
    // 这保证了即使没有通过这个切面创建事务，TransactionInfo 堆栈也会被正确管理。
    txInfo.bindToThread();
    return txInfo;
}
```

在`org.springframework.transaction.interceptor.TransactionAspectSupport.TransactionInfo#bindToThread`
方法中，将当前的 `TransactionInfo` 对象绑定到线程上下文中。在绑定之前，会保存当前线程上的旧的 `TransactionInfo`
对象，以便在事务完成后恢复之前保存的旧的 `TransactionInfo` 对象。

```java
/**
 * 将当前的 TransactionInfo 对象绑定到线程上下文中。
 * 在绑定之前会保存当前线程上的旧的 TransactionInfo 对象，
 * 在事务完成后会恢复之前保存的旧的 TransactionInfo 对象。
 */
private void bindToThread() {
    // 暴露当前的 TransactionStatus，并在事务完成后恢复任何现有的 TransactionStatus。
    this.oldTransactionInfo = transactionInfoHolder.get();
    transactionInfoHolder.set(this);
}
```

在`org.springframework.transaction.interceptor.TransactionAspectSupport#commitTransactionAfterReturning`
方法中，通过 `txInfo` 参数获取当前事务的信息，包括事务状态和事务管理器，并调用事务管理器的 `commit` 方法来提交事务。

```java
/**
 * 在方法成功完成调用后执行，但不会在处理异常后执行。
 * 如果没有创建事务，则什么也不做。
 *
 * @param txInfo 当前事务的信息
 */
protected void commitTransactionAfterReturning(@Nullable TransactionInfo txInfo) {
    // 如果 txInfo 不为 null 且事务状态不为 null，则执行事务提交
    if (txInfo != null && txInfo.getTransactionStatus() != null) {
        // 如果启用了跟踪日志，则记录完成事务的信息
        if (logger.isTraceEnabled()) {
            logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() + "]");
        }
        // 调用事务管理器的 commit 方法来提交事务
        txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
    }
}
```

在`org.springframework.transaction.interceptor.TransactionAspectSupport#completeTransactionAfterThrowing`
方法中，根据事务属性的配置完成事务的提交或回滚操作。如果事务属性要求在异常时回滚事务，则会调用事务管理器的回滚方法；否则会调用事务管理器的提交方法。

```java
/**
 * 处理可抛出的异常，完成事务。
 * 根据配置可能会提交或回滚事务。
 *
 * @param txInfo 当前事务的信息
 * @param ex 遇到的可抛出异常
 */
protected void completeTransactionAfterThrowing(@Nullable TransactionInfo txInfo, Throwable ex) {
    // 如果 txInfo 不为 null 且事务状态不为 null，则执行事务完成操作
    if (txInfo != null && txInfo.getTransactionStatus() != null) {
        // 如果启用了跟踪日志，则记录在异常后完成事务的信息和异常信息
        if (logger.isTraceEnabled()) {
            logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() +
                    "] after exception: " + ex);
        }
        // 如果事务属性不为 null，并且根据事务属性需要在异常时回滚事务
        if (txInfo.transactionAttribute != null && txInfo.transactionAttribute.rollbackOn(ex)) {
            try {
                // 使用事务管理器回滚事务
                txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
            } catch (TransactionSystemException ex2) {
                // 如果回滚过程中发生异常，则记录错误信息并抛出异常
                logger.error("Application exception overridden by rollback exception", ex);
                ex2.initApplicationException(ex);
                throw ex2;
            } catch (RuntimeException | Error ex2) {
                // 如果回滚过程中发生运行时异常或错误，则记录错误信息并抛出异常
                logger.error("Application exception overridden by rollback exception", ex);
                throw ex2;
            }
        } else {
            // 如果不需要在异常时回滚事务，则继续提交事务
            // 如果事务状态为 RollbackOnly，则最终仍然会回滚事务
            try {
                txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
            } catch (TransactionSystemException ex2) {
                // 如果提交过程中发生异常，则记录错误信息并抛出异常
                logger.error("Application exception overridden by commit exception", ex);
                ex2.initApplicationException(ex);
                throw ex2;
            } catch (RuntimeException | Error ex2) {
                // 如果提交过程中发生运行时异常或错误，则记录错误信息并抛出异常
                logger.error("Application exception overridden by commit exception", ex);
                throw ex2;
            }
        }
    }
}
```