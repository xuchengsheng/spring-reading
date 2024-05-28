## TransactionTemplate

- [TransactionTemplate](#transactiontemplate)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、最佳实践](#四最佳实践)
    - [五、源码分析](#五源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`TransactionTemplate` 是 Spring Framework 提供的工具类，用于在代码中以编程方式管理事务。它简化了事务的启动、提交/回滚以及异常处理，同时允许灵活配置事务属性，并提供了回调机制以执行特定操作。

### 三、主要功能

1. **事务的启动和提交/回滚**

    + 允许我们以编程方式启动事务，并在需要时提交或回滚事务。这种方式使得我们可以在代码的特定部分明确定义事务的边界，而不必依赖于容器管理。

2. **异常处理**

    + 提供了对异常的处理机制，我们可以通过配置指定在发生异常时应该执行的操作，比如回滚事务。

3. **事务属性的灵活配置**

    + 我们可以使用 `TransactionTemplate` 配置各种事务属性，如隔离级别、传播行为等。这使得我们可以针对不同的场景灵活地配置事务行为。

4. **回调机制**

    + 允许我们定义回调接口，通过这些回调接口，我们可以在事务的不同阶段执行特定的操作。这为更复杂的事务场景提供了更大的灵活性。

### 四、最佳实践

使用 `TransactionTemplate` 来管理事务。它首先创建了一个数据库连接，并通过 `DataSourceTransactionManager`
实例化了 `TransactionTemplate`。在 `TransactionTemplate` 的 `execute`
方法中，定义了一个事务回调接口，在该接口的 `doInTransaction` 方法中执行了数据库操作。通过这种方式，可以确保操作要么全部成功提交，要么全部回滚，从而保证数据的一致性和完整性。

```java
public class TransactionTemplateDemo {

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
        // 创建事务模板
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        Boolean insertSuccess = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                // 主键Id
                long id = System.currentTimeMillis();
                // 分数
                int score = new Random().nextInt(100);
                // 保存数据
                int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
                // 模拟异常，用于测试事务回滚
                // int i = 1 / 0;
                // 我们也可以使用setRollbackOnly来回滚
                // status.setRollbackOnly();
                // 返回是否新增成功
                return row >= 1;
            }
        });
        System.out.println("新增scores表数据：" + insertSuccess);
    }
}
```

运行结果，数据库操作成功完成并成功提交了事务。

```java
新增scores表数据：true
```

### 五、源码分析

在`org.springframework.transaction.support.TransactionTemplate#execute`方法中，首先确保了 `PlatformTransactionManager`
已经设置，然后根据事务管理器的类型选择合适的执行方式。如果事务管理器是 `CallbackPreferringPlatformTransactionManager`
的实例，就会调用其 `execute` 方法来执行事务。否则，它将获取事务状态，执行事务回调操作，并在操作过程中处理可能的异常。最后，无论成功还是失败，都会提交事务。

```java

@Override
@Nullable
public <T> T execute(TransactionCallback<T> action) throws TransactionException {
    // 断言确保已设置PlatformTransactionManager
    Assert.state(this.transactionManager != null, "No PlatformTransactionManager set");

    // 如果事务管理器是CallbackPreferringPlatformTransactionManager的实例
    if (this.transactionManager instanceof CallbackPreferringPlatformTransactionManager) {
        // 使用CallbackPreferringPlatformTransactionManager执行事务
        return ((CallbackPreferringPlatformTransactionManager) this.transactionManager).execute(this, action);
    } else {
        // 获取事务状态
        TransactionStatus status = this.transactionManager.getTransaction(this);
        T result;
        try {
            // 执行事务回调操作
            result = action.doInTransaction(status);
        } catch (RuntimeException | Error ex) {
            // 事务中的代码抛出应用程序异常 -> 回滚事务
            rollbackOnException(status, ex);
            throw ex;
        } catch (Throwable ex) {
            // 事务中的代码抛出意外异常 -> 回滚事务
            rollbackOnException(status, ex);
            throw new UndeclaredThrowableException(ex, "TransactionCallback threw undeclared checked exception");
        }
        // 提交事务
        this.transactionManager.commit(status);
        return result;
    }
}
```

在`org.springframework.transaction.support.TransactionTemplate#rollbackOnException`
方法中，首先确保已设置了 `PlatformTransactionManager`
，然后记录调试日志以表示在应用异常时启动事务回滚。接着尝试执行事务回滚操作，如果发生回滚异常，则记录错误日志，并将原始异常初始化为回滚异常的应用程序异常。最后，如果发生运行时异常或错误，则将其重新抛出。

```java
/**
 * 在应用异常时执行回滚，正确处理回滚异常。
 * @param status 表示事务的对象
 * @param ex 抛出的应用程序异常或错误
 * @throws TransactionException 如果发生回滚错误
 */
private void rollbackOnException(TransactionStatus status, Throwable ex) throws TransactionException {
    // 断言确保已设置PlatformTransactionManager
    Assert.state(this.transactionManager != null, "No PlatformTransactionManager set");

    // 打印调试日志，表示在应用异常时启动事务回滚
    logger.debug("Initiating transaction rollback on application exception", ex);
    try {
        // 执行事务回滚
        this.transactionManager.rollback(status);
    } catch (TransactionSystemException ex2) {
        // 打印错误日志，表示应用异常被回滚异常覆盖
        logger.error("Application exception overridden by rollback exception", ex);
        // 初始化应用程序异常
        ex2.initApplicationException(ex);
        throw ex2;
    } catch (RuntimeException | Error ex2) {
        // 打印错误日志，表示应用异常被回滚异常覆盖
        logger.error("Application exception overridden by rollback exception", ex);
        throw ex2;
    }
}
```