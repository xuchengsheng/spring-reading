## TransactionDefinition

- [TransactionDefinition](#transactiondefinition)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [五、主要实现](#五主要实现)
    - [六、类关系图](#六类关系图)
    - [七、最佳实践](#七最佳实践)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https//juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https//github.com/xuchengsheng/spring-reading)

### 二、基本描述

`TransactionDefinition` 接口是 Spring 框架中用于定义事务属性的接口，它包含了事务的传播行为、隔离级别、超时时间和只读状态等属性，可以通过配置这些属性来灵活控制应用程序中的数据库事务行为。

### 三、主要功能

1. **定义事务传播行为**

    + 定义了事务的传播方式，即当一个方法被调用时，它应该如何处理现有的事务。例如，是加入已有的事务还是创建一个新的事务。

2. **指定事务隔离级别**

    + 定义了事务的隔离级别，即事务操作之间的隔离程度。不同的隔离级别可以解决不同的并发问题，如脏读、不可重复读和幻读等。

3. **设置事务超时时间**

    + 指定了事务的超时时间，即事务在多长时间内必须完成。如果事务在指定的时间内没有完成，则会被自动回滚。

4. **配置事务只读属性**

    + 指定了事务是否只读。只读事务可以优化数据库性能，因为数据库可以跳过一些读取锁的操作。

### 四、接口源码

`TransactionDefinition`接口，用于定义符合 Spring 规范的事务属性。它基于与 EJB CMT
属性类似的传播行为定义。该接口包括事务传播行为、隔离级别、超时设置和只读标志等属性。它提供了默认值和静态构建器方法，以方便创建事务定义对象。

```java
/**
 * 接口定义了符合 Spring 规范的事务属性。
 * 基于与 EJB CMT 属性类似的传播行为定义。
 *
 * <p>注意，隔离级别和超时设置仅在实际启动新事务时才会应用。
 * 由于只有 {@link #PROPAGATION_REQUIRED}、{@link #PROPAGATION_REQUIRES_NEW} 和 {@link #PROPAGATION_NESTED} 可能会引起这种情况，
 * 因此在其他情况下指定这些设置通常是没有意义的。
 * 此外，注意并非所有的事务管理器都支持这些高级功能，因此在给定非默认值时可能会抛出相应的异常。
 *
 * <p>{@link #isReadOnly() 只读标志} 适用于任何事务上下文，无论是由实际资源事务支持还是在资源级别非事务性地操作。
 * 在后一种情况下，该标志仅适用于应用程序中的受管资源，例如 Hibernate 的 {@code Session}。
 *
 * @author Juergen Hoeller
 * @since 08.05.2003
 * @see PlatformTransactionManager#getTransaction(TransactionDefinition)
 * @see org.springframework.transaction.support.DefaultTransactionDefinition
 * @see org.springframework.transaction.interceptor.TransactionAttribute
 */
public interface TransactionDefinition {

    /**
     * 支持当前事务；如果不存在则创建一个新事务。
     * 类似于具有相同名称的 EJB 事务属性。
     * <p>这通常是事务定义的默认设置，
     * 通常定义了事务同步范围。
     */
    int PROPAGATION_REQUIRED = 0;

    /**
     * 支持当前事务；如果不存在则以非事务方式执行。
     * 类似于具有相同名称的 EJB 事务属性。
     * <p><b>注意：</b>对于具有事务同步的事务管理器，
     * {@code PROPAGATION_SUPPORTS} 与没有事务稍有不同，
     * 因为它定义了可能应用于的事务范围同步。
     * 因此，同一资源（JDBC {@code Connection}、Hibernate {@code Session} 等）将用于整个指定的范围。
     * 注意，确切的行为取决于事务管理器的实际同步配置！
     * <p>通常情况下，谨慎使用 {@code PROPAGATION_SUPPORTS}！
     * 特别是，在 {@code PROPAGATION_SUPPORTS} 范围内不要依赖 {@code PROPAGATION_REQUIRED} 或 {@code PROPAGATION_REQUIRES_NEW}
     * （这可能会导致运行时同步冲突）。
     * 如果无法避免此类嵌套，请确保适当配置事务管理器（通常切换到“在实际事务上同步”）。
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#SYNCHRONIZATION_ON_ACTUAL_TRANSACTION
     */
    int PROPAGATION_SUPPORTS = 1;

    /**
     * 支持当前事务；如果不存在则抛出异常。
     * 类似于具有相同名称的 EJB 事务属性。
     * <p>在 {@code PROPAGATION_MANDATORY} 范围内的事务同步始终由周围的事务驱动。
     */
    int PROPAGATION_MANDATORY = 2;

    /**
     * 创建一个新事务，如果存在则挂起当前事务。
     * 类似于具有相同名称的 EJB 事务属性。
     * <p><b>注意：</b>实际的事务挂起不会在所有事务管理器上自动工作。
     * 这尤其适用于 {@link org.springframework.transaction.jta.JtaTransactionManager}，
     * 它要求将 {@code javax.transaction.TransactionManager} 提供给它（在标准 Java EE 中是特定于服务器的）。
     * <p>{@code PROPAGATION_REQUIRES_NEW} 范围总是定义自己的事务同步。
     * 现有的同步将被暂停和恢复。
     * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
     */
    int PROPAGATION_REQUIRES_NEW = 3;

    /**
     * 不支持当前事务；而总是以非事务方式执行。
     * 类似于具有相同名称的 EJB 事务属性。
     * <p><b>注意：</b>实际的事务挂起不会在所有事务管理器上自动工作。
     * 这尤其适用于 {@link org.springframework.transaction.jta.JtaTransactionManager}，
     * 它要求将 {@code javax.transaction.TransactionManager} 提供给它（在标准 Java EE 中是特定于服务器的）。
     * <p>请注意，在 {@code PROPAGATION_NOT_SUPPORTED} 范围内不可用事务同步。
     * 现有同步将被暂停和恢复。
     * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
     */
    int PROPAGATION_NOT_SUPPORTED = 4;

    /**
     * 不支持当前事务；如果存在则抛出异常。
     * 类似于具有相同名称的 EJB 事务属性。
     * <p>请注意，在 {@code PROPAGATION_NEVER} 范围内不可用事务同步。
     */
    int PROPAGATION_NEVER = 5;

    /**
     * 在存在当前事务时执行嵌套事务，否则与 {@link #PROPAGATION_REQUIRED} 行为相同。
     * 在 EJB 中没有类似的功能。
     * <p><b>注意：</b>实际创建嵌套事务只能在特定的事务管理器上工作。
     * 默认情况下，这仅适用于 JDBC {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}
     * 在使用 JDBC 3.0 驱动程序时。
     * 一些 JTA 提供程序可能也支持嵌套事务。
     * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
     */
    int PROPAGATION_NESTED = 6;

    /**
     * 使用底层数据存储的默认隔离级别。
     * 所有其他级别都对应于 JDBC 隔离级别。
     * @see java.sql.Connection
     */
    int ISOLATION_DEFAULT = -1;

    /**
     * 表示允许发生脏读、不可重复读和幻读。
     * <p>该级别允许一个事务修改的行在另一个事务提交之前被另一个事务读取（“脏读”）。
     * 如果其中任何更改被回滚，第二个事务将检索到无效的行。
     * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
     */
    int ISOLATION_READ_UNCOMMITTED = 1;

    /**
     * 表示防止脏读；可以发生不可重复读和幻读。
     * <p>该级别仅禁止事务读取一个包含未提交更改的行。
     * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
     */
    int ISOLATION_READ_COMMITTED = 2;

    /**
     * 表示防止脏读和不可重复读；可以发生幻读。
     * <p>该级别禁止事务读取一个包含未提交更改的行，同时禁止以下情况的发生：
     * 一个事务读取一行，第二个事务修改该行，第一个事务重新读取该行，第二次得到的值与第一次不同（“不可重复读”）。
     * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
     */
    int ISOLATION_REPEATABLE_READ = 4;

    /**
     * 表示防止脏读、不可重复读和幻读。
     * <p>该级别包括 {@link #ISOLATION_REPEATABLE_READ} 中的禁止，同时进一步禁止以下情况的发生：
     * 一个事务读取满足 {@code WHERE} 条件的所有行，第二个事务插入满足该 {@code WHERE} 条件的行，
     * 第一个事务为相同的条件重新读取，第二次读取中检索到额外的“幻行”。
     * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
     */
    int ISOLATION_SERIALIZABLE = 8;

    /**
     * 使用底层事务系统的默认超时时间，如果不支持超时，则为无。
     */
    int TIMEOUT_DEFAULT = -1;

    /**
     * 返回传播行为。
     * <p>必须返回 {@link TransactionDefinition} 接口上定义的 {@code PROPAGATION_XXX} 常量之一。
     * <p>默认值为 {@link #PROPAGATION_REQUIRED}。
     * @return 传播行为
     * @see #PROPAGATION_REQUIRED
     * @see org.springframework.transaction.support.TransactionSynchronizationManager#isActualTransactionActive()
     */
    default int getPropagationBehavior() {
        return PROPAGATION_REQUIRED;
    }

    /**
     * 返回隔离级别。
     * <p>必须返回 {@link TransactionDefinition} 接口上定义的 {@code ISOLATION_XXX} 常量之一。
     * 这些常量设计用于与 {@link java.sql.Connection} 上的相同常量的值匹配。
     * <p>专门用于与 {@link #PROPAGATION_REQUIRED} 或 {@link #PROPAGATION_REQUIRES_NEW} 一起使用，
     * 因为它仅适用于新启动的事务。
     * 如果我们希望在参与具有不同隔离级别的现有事务时拒绝隔离级别声明，请考虑在事务管理器上切换 "validateExistingTransactions" 标志为 "true"。
     * <p>默认值为 {@link #ISOLATION_DEFAULT}。
     * 请注意，不支持自定义隔离级别的事务管理器将在给定除 {@link #ISOLATION_DEFAULT} 之外的任何级别时抛出异常。
     * @return 隔离级别
     * @see #ISOLATION_DEFAULT
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
     */
    default int getIsolationLevel() {
        return ISOLATION_DEFAULT;
    }

    /**
     * 返回事务超时时间。
     * <p>必须返回以秒为单位的数字，或者 {@link #TIMEOUT_DEFAULT}。
     * <p>专门用于与 {@link #PROPAGATION_REQUIRED} 或 {@link #PROPAGATION_REQUIRES_NEW} 一起使用，
     * 因为它仅适用于新启动的事务。
     * <p>请注意，不支持超时的事务管理器将在给定除 {@link #TIMEOUT_DEFAULT} 之外的任何超时时抛出异常。
     * <p>默认值为 {@link #TIMEOUT_DEFAULT}。
     * @return 事务超时时间
     */
    default int getTimeout() {
        return TIMEOUT_DEFAULT;
    }

    /**
     * 返回是否优化为只读事务。
     * <p>只读标志适用于任何事务上下文，无论是由实际资源事务（{@link #PROPAGATION_REQUIRED}/
     * {@link #PROPAGATION_REQUIRES_NEW}）支持，还是在资源级别非事务性地操作（{@link #PROPAGATION_SUPPORTS}）。
     * 在后一种情况下，该标志仅适用于应用程序中的受管资源，例如 Hibernate 的 {@code Session}。
     * <p>这只是对实际事务子系统的提示；
     * 它不一定会导致写入访问尝试失败。
     * 不能解释只读提示的事务管理器在要求只读事务时不会抛出异常。
     * @return 如果事务被优化为只读，则为 {@code true}（默认为 {@code false}）
     * @see org.springframework.transaction.support.TransactionSynchronization#beforeCommit(boolean)
     * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
     */
    default boolean isReadOnly() {
        return false;
    }

    /**
     * 返回此事务的名称。可以为 {@code null}。
     * <p>如果适用（例如，WebLogic），则将用作要显示在事务监视器中的事务名称。
     * <p>在 Spring 的声明式事务中，暴露的名称将是 {@code fully-qualified class name + "." + method name}（默认）。
     * @return 此事务的名称（默认为 {@code null}）
     * @see org.springframework.transaction.interceptor.TransactionAspectSupport
     * @see org.springframework.transaction.support.TransactionSynchronizationManager#getCurrentTransactionName()
     */
    @Nullable
    default String getName() {
        return null;
    }

    /**
     * 返回具有默认值的不可修改的 {@code TransactionDefinition}。
     * <p>为了定制目的，可以使用可修改的 {@link org.springframework.transaction.support.DefaultTransactionDefinition}。
     * @since 5.2
     */
    static TransactionDefinition withDefaults() {
        return StaticTransactionDefinition.INSTANCE;
    }
}
```

### 五、主要实现

1. **DefaultTransactionDefinition**

    - Spring
      框架中定义事务基本属性的默认实现类。它允许我们指定事务的传播行为、隔离级别、超时设置和只读标志等。通过提供默认值，它简化了事务属性的设置，并提供了静态方法 `withDefaults()`
      ，可用于创建具有默认属性的事务定义对象。

2. **DefaultTransactionAttribute**

    - Spring
      框架中定义事务属性的默认实现类。它封装了事务的传播行为、隔离级别、超时设置和只读标志等属性，并提供了操作方法和属性获取方法。作为 `TransactionAttribute`
      接口的默认实现，它可以轻松设置和获取方法或类级别的事务属性。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class DefaultTransactionAttribute
class DefaultTransactionDefinition
class TransactionAttribute {
<<Interface>>

}
class TransactionDefinition {
<<Interface>>

}

DefaultTransactionAttribute  -->  DefaultTransactionDefinition 
DefaultTransactionAttribute  ..>  TransactionAttribute 
DefaultTransactionDefinition  ..>  TransactionDefinition 
TransactionAttribute  -->  TransactionDefinition 
~~~

### 七、最佳实践

使用`DefaultTransactionDefinition`类来定义事务属性，并设置传播行为、隔离级别、事务超时时间、是否只读和事务名称等属性，然后打印出它们的值。

```java
public class TransactionDefinitionDemo {

    public static void main(String[] args) {
        // 创建一个 DefaultTransactionDefinition 实例
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();

        // 设置传播行为为PROPAGATION_REQUIRES_NEW
        transactionDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        System.out.println("Propagation Behavior: " + transactionDefinition.getPropagationBehavior());

        // 设置隔离级别为ISOLATION_REPEATABLE_READ
        transactionDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_REPEATABLE_READ);
        System.out.println("Isolation Level: " + transactionDefinition.getIsolationLevel());

        // 设置事务超时时间为30秒
        transactionDefinition.setTimeout(30);
        System.out.println("Timeout: " + transactionDefinition.getTimeout());

        // 设置事务为只读
        transactionDefinition.setReadOnly(true);
        System.out.println("Is Read Only: " + transactionDefinition.isReadOnly());

        // 设置事务名称为"DemoTransaction"
        transactionDefinition.setName("DemoTransaction");
        System.out.println("Transaction Name: " + transactionDefinition.getName());
    }
}
```

运行结果，使用 `DefaultTransactionDefinition` 类设置的事务属性：传播行为为 `PROPAGATION_REQUIRES_NEW`
，隔离级别为 `ISOLATION_REPEATABLE_READ`，超时时间为30秒，事务被设置为只读，并且事务名称为 "DemoTransaction"。

```java
Propagation Behavior:3
Isolation Level:4
Timeout:30
Is Read
Only:true
Transaction Name:DemoTransaction
```