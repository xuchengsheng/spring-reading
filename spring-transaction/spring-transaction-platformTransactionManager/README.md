## PlatformTransactionManager

- [PlatformTransactionManager](#platformtransactionmanager)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [五、主要实现](#五主要实现)
    - [六、类关系图](#六类关系图)
    - [七、最佳实践](#七最佳实践)
    - [八、源码分析](#八源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`PlatformTransactionManager` 接口是 Spring
框架中负责管理事务的核心接口，定义了统一的事务管理方法，包括事务的启动、提交、回滚和获取当前事务状态等，为不同的数据访问技术提供了统一的事务管理接口，便于在不同技术之间无缝切换。

### 三、主要功能

1. **开始事务**

    + `PlatformTransactionManager` 允许我们通过 `getTransaction(TransactionDefinition definition)`
      方法开始一个新的事务或获取一个已存在的事务。`TransactionDefinition` 对象定义了事务的属性，如传播行为、隔离级别、超时设置和只读标志等。

2. **提交事务**

    + 如果事务中的所有操作都成功完成，我们可以通过 `commit(TransactionStatus status)` 方法来提交事务。这将使事务中的所有更改永久化。

3. **回滚事务**

    + 如果在事务中发生任何异常或错误，我们可以通过 `rollback(TransactionStatus status)`
      方法来回滚事务。这将撤销事务中的所有更改，使数据库回到事务开始前的状态。

4. **获取事务状态**

    + `TransactionStatus` 对象提供了关于当前事务状态的信息，如是否是新事务、是否已完成、是否已回滚等。这些信息可以用于在事务处理过程中进行条件逻辑判断。

### 四、接口源码

`PlatformTransactionManager` 接口是 Spring 命令式事务基础架构的核心接口。它提供了三个主要功能`getTransaction()`
用于获取当前活动事务或创建新事务，`commit()` 用于提交给定的事务，而 `rollback()`
用于回滚给定的事务。这些功能使得应用程序能够有效地管理事务，确保数据的一致性和完整性。

```java
/**
 * 这是 Spring 命令式事务基础架构中的中心接口。
 * 应用程序可以直接使用它，但主要用途不是作为一个 API
 * 通常，应用程序将使用 TransactionTemplate 或通过 AOP 实现的声明式事务界定。
 *
 * <p>对于实现者来说，建议从提供的 AbstractPlatformTransactionManager 类派生，
 * 该类预先实现了定义的传播行为并处理了事务同步处理。子类必须实现底层事务的特定状态的模板方法，
 * 例如begin、suspend、resume、commit。
 *
 * <p>这个策略接口的默认实现是 JtaTransactionManager 和 DataSourceTransactionManager，
 * 它们可以作为其他事务策略的实现指南。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16.05.2003
 * @see org.springframework.transaction.support.TransactionTemplate
 * @see org.springframework.transaction.interceptor.TransactionInterceptor
 * @see org.springframework.transaction.ReactiveTransactionManager
 */
public interface PlatformTransactionManager extends TransactionManager {

    /**
     * 根据指定的传播行为返回当前活动事务或创建一个新事务。
     * <p>请注意，诸如隔离级别或超时之类的参数仅在创建新事务时应用，
     * 因此在参与活动事务时会被忽略。
     * <p>此外，并非所有的事务定义设置都会被所有的事务管理器支持
     * 当遇到不支持的设置时，适当的事务管理器实现应该抛出异常。
     * <p>以上规则的一个例外是只读标志，
     * 如果不支持显式的只读模式，则应该忽略它。从本质上讲，
     * 只读标志只是对潜在优化的一个提示。
     * @param definition TransactionDefinition 实例（对于默认值可以为 null），
     * 描述传播行为、隔离级别、超时等。
     * @return 表示新事务或当前事务的事务状态对象
     * @throws TransactionException 如果出现查找、创建或系统错误
     * @throws IllegalTransactionStateException 如果给定的事务定义无法执行
     * （例如，如果当前活动事务与指定的传播行为冲突）
     * @see TransactionDefinition#getPropagationBehavior
     * @see TransactionDefinition#getIsolationLevel
     * @see TransactionDefinition#getTimeout
     * @see TransactionDefinition#isReadOnly
     */
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
            throws TransactionException;

    /**
     * 根据其状态提交给定的事务。如果事务已被程序标记为仅回滚，请执行回滚。
     * <p>如果事务不是新的，则省略提交以正确参与周围的事务。
     * 如果先前的事务已被暂停以创建一个新事务，则在提交新事务后恢复先前的事务。
     * <p>请注意，当提交调用完成时，无论是正常还是抛出异常，事务都必须完全完成和清理。
     * 在这种情况下，不应该期望回滚调用。
     * <p>如果此方法引发除 TransactionException 之外的异常，
     * 则一些提交之前的错误导致提交尝试失败。例如，
     * 在提交之前可能会尝试将更改刷新到数据库中，
     * 由于 resulting DataAccessException 导致事务失败。在这种情况下，原始异常将传播到此 commit 方法的调用者。
     * @param status 由 getTransaction 方法返回的对象
     * @throws UnexpectedRollbackException 如果事务协调器启动了意外的回滚
     * @throws HeuristicCompletionException 如果事务协调器在事务协调器的一侧做出了启发式决策导致的事务失败
     * @throws TransactionSystemException 在提交或系统错误时（通常是由于基本资源故障引起）
     * @throws IllegalTransactionStateException 如果给定的事务已经完成（已提交或已回滚）
     * @see TransactionStatus#setRollbackOnly
     */
    void commit(TransactionStatus status) throws TransactionException;

    /**
     * 执行给定事务的回滚。
     * <p>如果事务不是新的，则仅将其标记为回滚，以便正确参与周围的事务。
     * 如果先前的事务已被暂停以创建一个新事务，则在回滚新事务后恢复先前的事务。
     * <p><b>如果提交引发异常，则不要调用事务回滚。</b>
     * 当提交返回时，事务已经完成和清理，即使在提交异常的情况下也是如此。
     * 因此，在提交失败后调用回滚将导致 IllegalTransactionStateException。
     * @param status 由 getTransaction 方法返回的对象
     * @throws TransactionSystemException 在回滚或系统错误时（通常是由于基本资源故障引起）
     * @throws IllegalTransactionStateException 如果给定的事务已经完成（已提交或已回滚）
     */
    void rollback(TransactionStatus status) throws TransactionException;

}
```

### 五、主要实现

1. **DataSourceTransactionManager**

    + 用于基于 JDBC 的事务管理。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AbstractPlatformTransactionManager
class DataSourceTransactionManager
class InitializingBean {
<<Interface>>

}
class PlatformTransactionManager {
<<Interface>>

}
class ResourceTransactionManager {
<<Interface>>

}
class TransactionManager {
<<Interface>>

}

AbstractPlatformTransactionManager  ..>  PlatformTransactionManager 
DataSourceTransactionManager  -->  AbstractPlatformTransactionManager 
DataSourceTransactionManager  ..>  InitializingBean 
DataSourceTransactionManager  ..>  ResourceTransactionManager 
PlatformTransactionManager  -->  TransactionManager 
ResourceTransactionManager  -->  PlatformTransactionManager 
~~~

### 七、最佳实践

使用Spring的事务管理器（`PlatformTransactionManager`）和JDBC模板（`JdbcTemplate`
）在Java应用中进行数据库操作。代码首先设置数据库连接信息，并创建数据源和事务管理器。然后，通过`JdbcTemplate`
执行SQL插入操作，将随机生成的分数插入数据库。在执行插入操作时，事务被启动，并在操作成功时提交。如果出现异常，则回滚事务，确保数据一致性。最后，打印操作影响的行数。

```java
public class PlatformTransactionManagerDemo {

    private static PlatformTransactionManager transactionManager;
    private static JdbcTemplate jdbcTemplate;

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
        transactionManager = new DataSourceTransactionManager(dataSource);
        // 创建 JdbcTemplate 对象，用于执行 SQL 语句
        jdbcTemplate = new JdbcTemplate(dataSource);

        insertScore();
    }

    private static void insertScore() {
        // 开启一个新的事务，返回事务状态对象
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            long id = System.currentTimeMillis();
            int score = new Random().nextInt(100);
            // 向数据库中插入随机生成的分数
            int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
            // 模拟异常，用于测试事务回滚
            // int i = 1 / 0;
            // 提交事务
            transactionManager.commit(transactionStatus);
            // 打印影响行数
            System.out.println("scores row = " + row);
        } catch (Exception e) {
            // 出现异常时回滚事务
            transactionManager.rollback(transactionStatus);
            e.printStackTrace();
        }
    }
}
```

### 八、源码分析

**开启事务**

在`org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction`
方法中，实现了 `PlatformTransactionManager` 接口的 `getTransaction()`
方法，处理了事务的传播行为。根据传播行为的不同，通过调用 `doGetTransaction()`、`isExistingTransaction()` 和 `doBegin()`
等方法来获取、检查现有事务，并开始新的事务。如果存在现有事务，则根据传播行为确定如何处理；如果不存在现有事务，则根据传播行为决定如何继续。在创建新事务时，可能会挂起已存在的事务，并在出现异常时恢复挂起的资源。如果传播行为指定了 "
mandatory"
，但未找到现有事务，则抛出异常。若指定的超时时间无效，则抛出超时异常。最后，根据传播行为返回相应的事务状态对象，可能是一个 "
empty" 事务，没有实际的事务，但可能存在同步。

```java
/**
 * 此实现处理传播行为。委托给 doGetTransaction、isExistingTransaction 和 doBegin。
 * @see #doGetTransaction
 * @see #isExistingTransaction
 * @see #doBegin
 */
@Override
public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
        throws TransactionException {

    // 如果没有给定事务定义，则使用默认值。
    TransactionDefinition def = (definition != null ? definition : TransactionDefinition.withDefaults());

    // 获取事务对象
    Object transaction = doGetTransaction();
    // 是否启用调试日志
    boolean debugEnabled = logger.isDebugEnabled();

    if (isExistingTransaction(transaction)) {
        // 找到现有事务 -> 检查传播行为以确定如何操作。
        return handleExistingTransaction(def, transaction, debugEnabled);
    }

    // 检查新事务的定义设置。
    if (def.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {
        throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());
    }

    // 未找到现有事务 -> 检查传播行为以确定如何继续。
    if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {
        throw new IllegalTransactionStateException(
                "No existing transaction found for transaction marked with propagation 'mandatory'");
    } else if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED ||
            def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW ||
            def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
        // 挂起已存在的事务（如果有），然后创建新事务。
        SuspendedResourcesHolder suspendedResources = suspend(null);
        if (debugEnabled) {
            logger.debug("Creating new transaction with name [" + def.getName() + "]: " + def);
        }
        try {
            // 开始新事务
            return startTransaction(def, transaction, debugEnabled, suspendedResources);
        } catch (RuntimeException | Error ex) {
            // 如果在开始新事务时出现异常，则恢复挂起的资源并抛出异常。
            resume(null, suspendedResources);
            throw ex;
        }
    } else {
        // 创建“空”事务：没有实际的事务，但可能存在同步。
        if (def.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT && logger.isWarnEnabled()) {
            logger.warn("Custom isolation level specified but no actual transaction initiated; " +
                    "isolation level will effectively be ignored: " + def);
        }
        // 返回准备好的事务状态，没有实际事务，但可能存在同步。
        boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
        return prepareTransactionStatus(def, null, true, newSynchronization, debugEnabled, null);
    }
}
```

在`org.springframework.jdbc.datasource.DataSourceTransactionManager#doGetTransaction`
方法中，首先创建了一个 `DataSourceTransactionObject`
对象，用于管理数据源事务。然后根据是否允许嵌套事务设置了保存点的允许状态。接着通过 `TransactionSynchronizationManager.getResource()`
方法获取当前线程绑定的数据源连接持有者对象，并将其设置到事务对象中。最后返回该事务对象。

```java

@Override
protected Object doGetTransaction() {
    // 创建 DataSourceTransactionObject 对象，用于管理数据源事务
    DataSourceTransactionObject txObject = new DataSourceTransactionObject();
    // 设置是否允许设置保存点
    txObject.setSavepointAllowed(isNestedTransactionAllowed());
    // 获取当前线程绑定的数据源的连接持有者
    ConnectionHolder conHolder =
            (ConnectionHolder) TransactionSynchronizationManager.getResource(obtainDataSource());
    // 将连接持有者设置到事务对象中
    txObject.setConnectionHolder(conHolder, false);
    return txObject;
}
```

在`org.springframework.jdbc.datasource.DataSourceTransactionManager#isExistingTransaction`
方法中，用于检查给定的事务对象是否表示一个已存在的活动事务。它首先将传入的事务对象强制转换为 `DataSourceTransactionObject`
类型，然后检查该事务对象是否具有连接持有者，并且该连接持有者中的事务是否处于活动状态。最后返回一个布尔值，表示是否存在活动事务。

```java

@Override
protected boolean isExistingTransaction(Object transaction) {
    // 将事务对象强制转换为 DataSourceTransactionObject 类型
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) transaction;
    // 检查事务对象是否具有连接持有者，并且连接持有者中的事务是否处于活动状态
    return (txObject.hasConnectionHolder() && txObject.getConnectionHolder().isTransactionActive());
}
```

在`org.springframework.transaction.support.AbstractPlatformTransactionManager#handleExistingTransaction`
方法中，根据事务定义中的传播行为不同，它可能会挂起当前事务并创建一个新的事务，也可能会创建一个嵌套事务，或者参与已存在的事务。最终，根据处理结果创建并返回一个新的
TransactionStatus 对象，用于表示已存在的事务。

```java
/**
 * 处理已存在的事务，为其创建一个 TransactionStatus 对象。
 *
 * @param definition     事务定义对象
 * @param transaction    事务对象
 * @param debugEnabled   是否启用调试日志
 * @return 一个包含有关现有事务的 TransactionStatus 对象
 * @throws TransactionException 如果在处理现有事务时发生错误
 */
private TransactionStatus handleExistingTransaction(
        TransactionDefinition definition, Object transaction, boolean debugEnabled)
        throws TransactionException {

    // 如果传播行为是 PROPAGATION_NEVER，则不允许存在现有事务
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NEVER) {
        throw new IllegalTransactionStateException(
                "Existing transaction found for transaction marked with propagation 'never'");
    }

    // 如果传播行为是 PROPAGATION_NOT_SUPPORTED，则挂起当前事务
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {
        if (debugEnabled) {
            logger.debug("Suspending current transaction");
        }
        // 挂起当前事务并获取挂起的资源
        Object suspendedResources = suspend(transaction);
        // 判断是否需要新的事务同步
        boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
        // 准备事务状态并返回
        return prepareTransactionStatus(
                definition, null, false, newSynchronization, debugEnabled, suspendedResources);
    }

    // 如果传播行为是 PROPAGATION_REQUIRES_NEW，则挂起当前事务并创建一个新事务
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
        if (debugEnabled) {
            logger.debug("Suspending current transaction, creating new transaction with name [" +
                    definition.getName() + "]");
        }
        // 挂起当前事务并获取挂起的资源
        SuspendedResourcesHolder suspendedResources = suspend(transaction);
        try {
            // 启动新的事务
            return startTransaction(definition, transaction, debugEnabled, suspendedResources);
        } catch (RuntimeException | Error beginEx) {
            // 开始事务时发生异常，恢复挂起的资源，并将异常抛出
            resumeAfterBeginException(transaction, suspendedResources, beginEx);
            throw beginEx;
        }
    }

    // 如果传播行为是 PROPAGATION_NESTED，则创建一个嵌套事务
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
        // 检查是否允许嵌套事务
        if (!isNestedTransactionAllowed()) {
            throw new NestedTransactionNotSupportedException(
                    "Transaction manager does not allow nested transactions by default - " +
                            "specify 'nestedTransactionAllowed' property with value 'true'");
        }
        if (debugEnabled) {
            logger.debug("Creating nested transaction with name [" + definition.getName() + "]");
        }
        // 如果使用保存点进行嵌套事务，则在现有 Spring 管理的事务中创建保存点
        if (useSavepointForNestedTransaction()) {
            // 通过 TransactionStatus 实现的 SavepointManager API 在现有的 Spring 管理的事务中创建保存点
            // 通常使用 JDBC 3.0 保存点，永远不会激活 Spring 同步。
            DefaultTransactionStatus status =
                    prepareTransactionStatus(definition, transaction, false, false, debugEnabled, null);
            status.createAndHoldSavepoint();
            return status;
        } else {
            // 通过嵌套的 begin 和 commit/rollback 调用创建嵌套事务
            // 通常仅用于 JTA：如果存在预先存在的 JTA 事务，则此处可能会激活 Spring 同步。
            return startTransaction(definition, transaction, debugEnabled, null);
        }
    }

    // Assumably PROPAGATION_SUPPORTS or PROPAGATION_REQUIRED.
    // 如果传播行为是 PROPAGATION_SUPPORTS 或 PROPAGATION_REQUIRED，则参与现有事务
    if (debugEnabled) {
        logger.debug("Participating in existing transaction");
    }
    // 如果启用了验证现有事务，则检查定义是否与现有事务兼容
    if (isValidateExistingTransaction()) {
        if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
            // 检查隔离级别是否与现有事务兼容
            Integer currentIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
            if (currentIsolationLevel == null || currentIsolationLevel != definition.getIsolationLevel()) {
                Constants isoConstants = DefaultTransactionDefinition.constants;
                throw new IllegalTransactionStateException("Participating transaction with definition [" +
                        definition + "] specifies isolation level which is incompatible with existing transaction: " +
                        (currentIsolationLevel != null ?
                                isoConstants.toCode(currentIsolationLevel, DefaultTransactionDefinition.PREFIX_ISOLATION) :
                                "(unknown)"));
            }
        }
        // 检查只读属性是否与现有事务兼容
        if (!definition.isReadOnly()) {
            if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                throw new IllegalTransactionStateException("Participating transaction with definition [" +
                        definition + "] is not marked as read-only but existing transaction is");
            }
        }
    }
    // 根据情况创建新的事务同步
    boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
    return prepareTransactionStatus(definition, transaction, false, newSynchronization, debugEnabled, null);
}
```

在`org.springframework.transaction.support.AbstractPlatformTransactionManager#startTransaction`
方法中，首先检查是否应该启用事务同步，然后创建一个新的事务状态对象。接着调用 `doBegin` 方法执行事务的开始操作，并准备同步操作。最后返回创建的事务状态对象。

```java
/**
 * 开始一个新的事务。
 */
private TransactionStatus startTransaction(TransactionDefinition definition, Object transaction,
                                           boolean debugEnabled, @Nullable SuspendedResourcesHolder suspendedResources) {

    // 判断是否要启用事务同步
    boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
    // 创建一个新的事务状态对象
    DefaultTransactionStatus status = newTransactionStatus(
            definition, transaction, true, newSynchronization, debugEnabled, suspendedResources);
    // 执行事务开始操作
    doBegin(transaction, definition);
    // 准备同步操作
    prepareSynchronization(status, definition);
    return status;
}
```

在`org.springframework.jdbc.datasource.DataSourceTransactionManager#doBegin`
方法中，首先尝试从数据源获取一个数据库连接，并在获取连接后将其存储在事务对象中。然后，它根据事务定义中的属性对连接进行一系列配置，例如设置隔离级别和只读属性。如果连接是自动提交的，则将其切换为手动提交以确保事务的一致性。接着，它准备事务连接，并将连接标记为活跃的事务。最后，如果是新的连接持有者，则将连接持有者绑定到当前线程，以便在事务期间管理连接的生命周期。如果在这个过程中发生任何错误，它将释放连接资源，并抛出一个表示无法创建事务的异常。

```java

@Override
protected void doBegin(Object transaction, TransactionDefinition definition) {
    // 将事务对象转换为 DataSourceTransactionObject
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) transaction;
    Connection con = null;

    try {
        // 如果当前事务对象没有连接持有者，或者连接持有者已与事务同步，则获取新的连接
        if (!txObject.hasConnectionHolder() ||
                txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
            // 获取数据源并从中获取连接
            Connection newCon = obtainDataSource().getConnection();
            // 如果日志记录级别为DEBUG，则打印获取到的连接信息
            if (logger.isDebugEnabled()) {
                logger.debug("Acquired Connection [" + newCon + "] for JDBC transaction");
            }
            // 设置连接持有者为新获取的连接，并指示需要与事务同步
            txObject.setConnectionHolder(new ConnectionHolder(newCon), true);
        }

        // 将连接的同步标志设置为true
        txObject.getConnectionHolder().setSynchronizedWithTransaction(true);
        // 获取当前连接
        con = txObject.getConnectionHolder().getConnection();

        // 准备连接的事务属性，并记录之前的隔离级别
        Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(con, definition);
        txObject.setPreviousIsolationLevel(previousIsolationLevel);
        // 设置只读属性
        txObject.setReadOnly(definition.isReadOnly());

        // 如果连接是自动提交的，则切换为手动提交
        if (con.getAutoCommit()) {
            // 标记需要恢复原来的自动提交状态
            txObject.setMustRestoreAutoCommit(true);
            // 如果日志记录级别为DEBUG，则打印切换自动提交到手动提交的信息
            if (logger.isDebugEnabled()) {
                logger.debug("Switching JDBC Connection [" + con + "] to manual commit");
            }
            // 设置为手动提交
            con.setAutoCommit(false);
        }

        // 准备事务连接，并将连接标记为活跃的事务
        prepareTransactionalConnection(con, definition);
        txObject.getConnectionHolder().setTransactionActive(true);

        // 确定事务的超时时间，并设置给连接持有者
        int timeout = determineTimeout(definition);
        if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
            txObject.getConnectionHolder().setTimeoutInSeconds(timeout);
        }

        // 如果是新的连接持有者，则将连接持有者绑定到线程
        if (txObject.isNewConnectionHolder()) {
            TransactionSynchronizationManager.bindResource(obtainDataSource(), txObject.getConnectionHolder());
        }
    } catch (Throwable ex) {
        // 如果是新的连接持有者，则释放连接，并将连接持有者设置为null
        if (txObject.isNewConnectionHolder()) {
            DataSourceUtils.releaseConnection(con, obtainDataSource());
            txObject.setConnectionHolder(null, false);
        }
        // 抛出无法创建事务异常
        throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction", ex);
    }
}
```

在`org.springframework.jdbc.datasource.DataSourceTransactionManager#prepareTransactionalConnection`
方法中，如果设置了“enforceReadOnly”标志为true，并且事务定义指示为只读事务，则会执行一个“SET TRANSACTION READ ONLY”语句。

```java
/**
 * 在事务开始后准备事务性 {@code Connection}。
 * <p>如果 {@link #setEnforceReadOnly "enforceReadOnly"} 标志设置为 {@code true}，
 * 并且事务定义指示为只读事务，那么默认实现将执行一个 "SET TRANSACTION READ ONLY" 语句。
 * <p>"SET TRANSACTION READ ONLY" 语句可被 Oracle、MySQL 和 Postgres 理解，并且可能适用于其他数据库。
 * 如果您希望调整此处理方式，请相应地重写此方法。
 * @param con 事务性 JDBC 连接
 * @param definition 当前事务定义
 * @throws SQLException 如果 JDBC API 抛出异常
 * @since 4.3.7
 * @see #setEnforceReadOnly
 */
protected void prepareTransactionalConnection(Connection con, TransactionDefinition definition)
        throws SQLException {

    if (isEnforceReadOnly() && definition.isReadOnly()) {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("SET TRANSACTION READ ONLY");
        }
    }
}
```

**提交事务**

在`org.springframework.transaction.support.AbstractPlatformTransactionManager#commit`
方法中，检查事务状态是否已完成，如果已完成则抛出非法事务状态异常。然后，检查事务状态是否为本地回滚，如果是，则执行回滚操作。接着，如果全局事务标记为回滚，但事务代码请求提交，则执行回滚操作。最后，如果以上情况都不满足，则执行事务的提交操作。

```java
/**
 * 此提交的实现处理参与现有事务和编程回滚请求。委托给{@code isRollbackOnly}、{@code doCommit}和{@code rollback}。
 * 如果事务已经完成，则抛出异常。
 * 如果事务状态为本地回滚，则根据情况执行回滚操作。
 * 如果不应仅在全局回滚的情况下提交事务，并且事务状态为全局回滚，则根据情况执行回滚操作。
 * 否则，执行提交操作。
 * @param status 事务状态对象
 * @throws TransactionException 如果提交过程中发生事务异常
 * @see org.springframework.transaction.TransactionStatus#isRollbackOnly()
 * @see #doCommit
 * @see #rollback
 */
@Override
public final void commit(TransactionStatus status) throws TransactionException {
    // 如果事务已经完成，则抛出异常
    if (status.isCompleted()) {
        throw new IllegalTransactionStateException(
                "Transaction is already completed - do not call commit or rollback more than once per transaction");
    }

    DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
    // 如果事务状态为本地回滚，则根据情况执行回滚操作
    if (defStatus.isLocalRollbackOnly()) {
        if (defStatus.isDebug()) {
            logger.debug("Transactional code has requested rollback");
        }
        processRollback(defStatus, false);
        return;
    }

    // 如果不应仅在全局回滚的情况下提交事务，并且事务状态为全局回滚，则根据情况执行回滚操作
    if (!shouldCommitOnGlobalRollbackOnly() && defStatus.isGlobalRollbackOnly()) {
        if (defStatus.isDebug()) {
            logger.debug("Global transaction is marked as rollback-only but transactional code requested commit");
        }
        processRollback(defStatus, true);
        return;
    }

    // 否则，执行提交操作
    processCommit(defStatus);
}
```

在`org.springframework.transaction.support.AbstractPlatformTransactionManager#processCommit`
方法中，检查并应用了回滚标志，然后执行相应的提交逻辑。在提交的过程中，会触发各种回调方法，如提交前、提交后等，以便在事务提交的不同阶段执行特定的逻辑。如果发生了意外回滚或提交失败，它会相应地处理异常情况，并执行相应的回滚操作。最终，无论提交是否成功，都会执行清理操作以确保事务状态正确。

```java
/**
 * 处理实际的提交操作。
 * 已经检查并应用了回滚标志。
 *
 * @param status 表示事务的对象
 * @throws TransactionException 如果提交失败
 */
private void processCommit(DefaultTransactionStatus status) throws TransactionException {
    try {
        boolean beforeCompletionInvoked = false;

        try {
            boolean unexpectedRollback = false;
            // 为提交做准备
            prepareForCommit(status);
            // 触发提交前的回调
            triggerBeforeCommit(status);
            // 触发事务完成前的回调
            triggerBeforeCompletion(status);
            beforeCompletionInvoked = true;

            // 如果存在保存点
            if (status.hasSavepoint()) {
                if (status.isDebug()) {
                    // 释放事务保存点
                    logger.debug("Releasing transaction savepoint");
                }
                // 判断是否全局回滚
                unexpectedRollback = status.isGlobalRollbackOnly();
                // 释放持有的保存点
                status.releaseHeldSavepoint();
            } else if (status.isNewTransaction()) {
                // 如果是新事务
                if (status.isDebug()) {
                    // 开始事务提交
                    logger.debug("Initiating transaction commit");
                }
                // 判断是否全局回滚
                unexpectedRollback = status.isGlobalRollbackOnly();
                // 执行提交
                doCommit(status);
            } else if (isFailEarlyOnGlobalRollbackOnly()) {
                // 如果全局回滚
                // 判断是否全局回滚
                unexpectedRollback = status.isGlobalRollbackOnly();
            }

            // 如果存在意外回滚，但仍未从提交中获得相应的异常，则抛出 UnexpectedRollbackException 异常
            if (unexpectedRollback) {
                throw new UnexpectedRollbackException(
                        "Transaction silently rolled back because it has been marked as rollback-only");
            }
        } catch (UnexpectedRollbackException ex) {
            // 只能由 doCommit 导致
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);
            throw ex;
        } catch (TransactionException ex) {
            // 只能由 doCommit 导致
            if (isRollbackOnCommitFailure()) {
                doRollbackOnCommitException(status, ex);
            } else {
                triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
            }
            throw ex;
        } catch (RuntimeException | Error ex) {
            if (!beforeCompletionInvoked) {
                triggerBeforeCompletion(status);
            }
            doRollbackOnCommitException(status, ex);
            throw ex;
        }

        // 触发 afterCommit 回调，在那里抛出的异常被传播给调用者，但事务仍被视为已提交。
        try {
            triggerAfterCommit(status);
        } finally {
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_COMMITTED);
        }

    } finally {
        // 完成后的清理
        cleanupAfterCompletion(status);
    }
}
```

在`org.springframework.jdbc.datasource.DataSourceTransactionManager#doCommit`
方法中，通过事务状态对象获取数据源事务对象，然后从事务对象中获取数据库连接，接着尝试提交数据库事务，如果提交过程中发生 SQL
异常，则将其转换为 Spring 事务异常并抛出。

```java

@Override
protected void doCommit(DefaultTransactionStatus status) {
    // 强制转换为 DataSourceTransactionObject 对象，获取事务相关信息
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
    // 从事务对象中获取数据库连接
    Connection con = txObject.getConnectionHolder().getConnection();
    // 如果是调试模式，则记录调试信息
    if (status.isDebug()) {
        logger.debug("Committing JDBC transaction on Connection [" + con + "]");
    }
    try {
        // 提交数据库事务
        con.commit();
    } catch (SQLException ex) {
        // 发生 SQL 异常，将其转换为 Spring 事务异常并抛出
        throw translateException("JDBC commit", ex);
    }
}
```

**回滚事务**

在`org.springframework.transaction.support.AbstractPlatformTransactionManager#rollback`
方法中，，用于处理现有事务的回滚操作。它委托给 `processRollback` 方法来执行回滚，并通过检查事务状态来确保不会多次调用提交或回滚操作。

```java
/**
 * 该回滚操作的实现处理参与现有事务。委托给 {@code doRollback} 和 {@code doSetRollbackOnly}。
 *
 * @see #doRollback
 * @see #doSetRollbackOnly
 */
@Override
public final void rollback(TransactionStatus status) throws TransactionException {
    // 如果事务已经完成，则抛出异常
    if (status.isCompleted()) {
        throw new IllegalTransactionStateException(
                "Transaction is already completed - do not call commit or rollback more than once per transaction");
    }

    // 将事务状态转换为默认事务状态
    DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
    // 执行回滚操作
    processRollback(defStatus, false);
}
```

在`org.springframework.transaction.support.AbstractPlatformTransactionManager#processRollback`
方法中，检查事务的完成标志，然后根据事务的状态执行相应的回滚操作。如果存在保存点，则回滚到该保存点；如果是新事务，则执行初始化的事务回滚操作；如果参与了较大的事务，则根据条件进行相应的处理。在执行过程中，会根据情况触发相应的事务同步操作，并根据全局回滚标记判断是否引发意外回滚异常。最后，无论是否发生异常，都会执行完成后的清理操作。

```java
/**
 * 处理实际的回滚操作。
 * 已经检查过事务完成标志。
 * @param status 表示事务的对象
 * @param unexpected 是否意外回滚
 * @throws TransactionException 如果回滚失败
 */
private void processRollback(DefaultTransactionStatus status, boolean unexpected) {
    try {
        boolean unexpectedRollback = unexpected;

        try {
            // 触发完成前操作
            triggerBeforeCompletion(status);

            // 回滚到保存点
            if (status.hasSavepoint()) {
                if (status.isDebug()) {
                    logger.debug("Rolling back transaction to savepoint");
                }
                status.rollbackToHeldSavepoint();
            }
            // 初始化事务回滚
            else if (status.isNewTransaction()) {
                if (status.isDebug()) {
                    logger.debug("Initiating transaction rollback");
                }
                doRollback(status);
            } else {
                // 参与较大的事务
                if (status.hasTransaction()) {
                    // 如果是本地回滚，或者全局回滚失败
                    if (status.isLocalRollbackOnly() || isGlobalRollbackOnParticipationFailure()) {
                        if (status.isDebug()) {
                            logger.debug("Participating transaction failed - marking existing transaction as rollback-only");
                        }
                        // 设置事务为仅回滚
                        doSetRollbackOnly(status);
                    } else {
                        if (status.isDebug()) {
                            logger.debug("Participating transaction failed - letting transaction originator decide on rollback");
                        }
                    }
                } else {
                    logger.debug("Should roll back transaction but cannot - no transaction available");
                }
                // 如果不是全局仅回滚，则不会在此处考虑意外回滚
                if (!isFailEarlyOnGlobalRollbackOnly()) {
                    unexpectedRollback = false;
                }
            }
        } catch (RuntimeException | Error ex) {
            // 触发完成后操作，状态为未知
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
            throw ex;
        }

        // 触发完成后操作，状态为已回滚
        triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);

        // 如果存在全局回滚标记，则引发UnexpectedRollbackException
        if (unexpectedRollback) {
            throw new UnexpectedRollbackException(
                    "Transaction rolled back because it has been marked as rollback-only");
        }
    } finally {
        // 完成后清理
        cleanupAfterCompletion(status);
    }
}
```

在`org.springframework.jdbc.datasource.DataSourceTransactionManager#doRollback`
方法中，从事务状态中获取数据源事务对象，然后从该对象中获取连接对象。接着，它尝试执行连接对象的回滚操作。

```java
/**
 * 执行回滚操作。
 * @param status 表示事务的对象
 */
@Override
protected void doRollback(DefaultTransactionStatus status) {
    // 获取事务数据源对象
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
    // 获取连接对象
    Connection con = txObject.getConnectionHolder().getConnection();
    // 如果启用了调试模式，则记录回滚日志
    if (status.isDebug()) {
        logger.debug("Rolling back JDBC transaction on Connection [" + con + "]");
    }
    try {
        // 执行回滚操作
        con.rollback();
    } catch (SQLException ex) {
        // 抛出数据库异常
        throw translateException("JDBC rollback", ex);
    }
}
```