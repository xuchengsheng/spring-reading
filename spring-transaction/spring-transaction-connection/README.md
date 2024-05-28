## Connection

- [Connection](#connection)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [六、最佳实践](#六最佳实践)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`Connection` 接口是 Java JDBC API 中的一部分，代表着与数据库的连接。它提供了方法来管理数据库连接、执行 SQL
语句以及处理事务，是与数据库交互的重要接口之一。

### 三、主要功能

1. **执行 SQL 语句**

    + 通过 `createStatement()` 方法创建 `Statement` 对象，或者通过 `prepareStatement(String sql)`
      方法创建 `PreparedStatement` 对象，用于执行 SQL 查询、更新或删除等操作。

2. **事务管理**

    + 支持事务操作，可以通过 `commit()` 提交事务或者 `rollback()` 回滚事务。

3. **设置事务属性**

    + 可以设置事务的隔离级别、自动提交等属性。

### 四、接口源码

`Connection` 接口，用于表示与特定数据库的连接。它提供了方法来执行 SQL 语句、管理事务、提交或回滚更改以及关闭连接。

```java
/**
 * <P>表示与特定数据库的连接（会话）。SQL 语句在连接的上下文中执行并返回结果。
 * <P>
 * <code>Connection</code> 对象的数据库能够提供描述其表、支持的 SQL 语法、存储的过程、连接的功能等信息。这些信息是通过 <code>getMetaData</code> 方法获取的。
 *
 * <P><B>注意：</B>在配置 <code>Connection</code> 时，JDBC 应用程序应使用适当的 <code>Connection</code> 方法，如 <code>setAutoCommit</code> 或 <code>setTransactionIsolation</code>。应用程序不应直接调用 SQL 命令来更改连接的配置，而应使用 JDBC 方法。默认情况下，<code>Connection</code> 对象处于自动提交模式，这意味着它在执行每个语句后会自动提交更改。如果已禁用自动提交模式，则必须显式调用 <code>commit</code> 方法才能提交更改；否则，数据库更改将不会保存。
 * <P>
 * 使用 JDBC 2.1 核心 API 创建的新的 <code>Connection</code> 对象具有一个最初为空的类型映射。用户可以为此类型映射中的用户定义类型（UDT）输入自定义映射。
 * 当从数据源检索到 UDT 并使用 <code>ResultSet.getObject</code> 方法时，<code>getObject</code> 方法将检查连接的类型映射以查看是否存在该 UDT 的条目。如果存在，则 <code>getObject</code> 方法将把 UDT 映射到指定的类。如果不存在条目，则使用标准映射映射 UDT。
 * <p>
 * 用户可以创建一个新的类型映射，它是一个 <code>java.util.Map</code> 对象，向其中添加一个条目，并将其传递给可以执行自定义映射的 <code>java.sql</code> 方法。在这种情况下，方法将使用给定的类型映射而不是与连接关联的类型映射。
 * <p>
 * 例如，以下代码片段指定 SQL 类型 <code>ATHLETES</code> 将映射到 Java 编程语言中的类 <code>Athletes</code>。该代码片段检索 <code>Connection</code> 对象 <code>con</code> 的类型映射，将条目插入其中，然后将带有新条目的类型映射设置为连接的类型映射。
 * <pre>
 *      java.util.Map map = con.getTypeMap();
 *      map.put("mySchemaName.ATHLETES", Class.forName("Athletes"));
 *      con.setTypeMap(map);
 * </pre>
 *
 * @see DriverManager#getConnection
 * @see Statement
 * @see ResultSet
 * @see DatabaseMetaData
 * @since 1.1
 */
public interface Connection extends Wrapper, AutoCloseable {

    /**
     * 创建一个用于向数据库发送 SQL 语句的 <code>Statement</code> 对象。
     * 通常使用 <code>Statement</code> 对象执行无参数的 SQL 语句。如果相同的 SQL 语句需要执行多次，使用 <code>PreparedStatement</code> 对象可能更有效率。
     * <P>
     * 使用返回的 <code>Statement</code> 对象创建的结果集默认为 <code>TYPE_FORWARD_ONLY</code> 类型，并且并发级别为 <code>CONCUR_READ_ONLY</code>。可以通过调用 {@link #getHoldability} 来确定创建的结果集的可保持性。
     *
     * @return 一个新的默认 <code>Statement</code> 对象
     * @exception SQLException 如果发生数据库访问错误，或者在关闭的连接上调用此方法
     */
    Statement createStatement() throws SQLException;

    /**
     * 创建一个用于向数据库发送参数化 SQL 语句的 <code>PreparedStatement</code> 对象。
     * <P>
     * 可以预编译带有或不带有 IN 参数的 SQL 语句，并将其存储在 <code>PreparedStatement</code> 对象中。然后可以使用该对象多次有效地执行此语句。
     * <P><B>注意：</B>此方法针对处理受益于预编译的参数化 SQL 语句进行了优化。如果驱动程序支持预编译，则方法 <code>prepareStatement</code> 将向数据库发送语句进行预编译。某些驱动程序可能不支持预编译。在这种情况下，直到执行 <code>PreparedStatement</code> 对象时，语句才会被发送到数据库。这对用户没有直接影响；但是，它影响了哪些方法会抛出某些 <code>SQLException</code> 对象。
     * <P>
     * 使用返回的 <code>PreparedStatement</code> 对象创建的结果集默认为 <code>TYPE_FORWARD_ONLY</code> 类型，并且并发级别为 <code>CONCUR_READ_ONLY</code>。可以通过调用 {@link #getHoldability} 来确定创建的结果集的可保持性。
     *
     * @param sql 可能包含一个或多个 '?' IN 参数占位符的 SQL 语句
     * @return 包含预编译的 SQL 语句的新的默认 <code>PreparedStatement</code> 对象
     * @exception SQLException 如果发生数据库访问错误，或者在关闭的连接上调用此方法
     */
    PreparedStatement prepareStatement(String sql) throws SQLException;

    /**
     * 使自上次提交/回滚以来所做的所有更改永久，并释放此 <code>Connection</code> 对象当前持有的任何数据库锁定。
     * 只有在禁用自动提交模式时才应该使用此方法。
     *
     * @exception SQLException 如果发生数据库访问错误，如果在参与分布式事务时调用此方法，如果在关闭的连接上调用此方法或者此 <code>Connection</code> 对象处于自动提交模式下
     * @see #setAutoCommit
     */
    void commit() throws SQLException;

    /**
     * 撤消当前事务中所做的所有更改，并释放此 <code>Connection</code> 对象当前持有的任何数据库锁定。
     * 只有在禁用自动提交模式时才应该使用此方法。
     *
     * @exception SQLException 如果发生数据库访问错误，如果在参与分布式事务时调用此方法，如果在关闭的连接上调用此方法或者此 <code>Connection</code> 对象处于自动提交模式下
     * @see #setAutoCommit
     */
    void rollback() throws SQLException;

    /**
     * 立即释放此 <code>Connection</code> 对象的数据库和 JDBC 资源，而不是等待它们自动释放。
     * <P>
     * 对已关闭的 <code>Connection</code> 对象调用 <code>close</code> 方法是一个无操作。
     * <P>
     * 强烈建议在调用 <code>close</code> 方法之前显式地提交或回滚活动事务。如果调用了 <code>close</code> 方法且存在活动事务，则结果是由实现定义的。
     *
     * @exception SQLException 如果发生数据库访问错误
     */
    void close() throws SQLException;
    
    // .... 
}
```

### 六、最佳实践

使用 Java JDBC API 连接到数据库，并执行插入操作。它首先配置了一个 `DataSource` 对象，用于连接到 MySQL
数据库。然后，它使用这个数据源获取了一个数据库连接，并设置了不自动提交，开启了一个事务。接着，它准备了一个 SQL
插入语句，并创建了一个 `PreparedStatement`
对象来执行插入操作。在执行插入操作后，它提交了事务，并打印了影响的行数。如果在执行过程中发生了异常，它会回滚事务并打印异常信息。最后，它处理了关闭连接时可能发生的异常。

```java
public class ConnectionDemo {

    public static void main(String[] args) {
        // 使用 DataSource 体现了高内聚，有利于后面更改数据库
        MysqlDataSource dataSource = new MysqlDataSource();
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        dataSource.setUrl("jdbc:mysql://localhost:3306/spring-reading");
        // 数据库用户名
        dataSource.setUser("root");
        // 数据库密码
        dataSource.setPassword("123456");

        try (Connection connection = dataSource.getConnection()) {
            // 设置不自动提交，开启事务
            connection.setAutoCommit(false);
            // SQL 插入语句
            String sql = "insert into scores(score) values(?)";
            // 创建 PreparedStatement 对象，用于执行 SQL 插入操作
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // 设置参数
                statement.setInt(1, new Random().nextInt(100));
                // 执行插入操作
                int row = statement.executeUpdate();
                // 模拟异常，用于测试事务回滚
                // int i = 1 / 0;
                // 提交事务
                connection.commit();
                // 打印影响行数
                System.out.println("row = " + row);
            } catch (Exception e) {
                // 发生异常时回滚事务
                connection.rollback();
                // 打印异常信息
                e.printStackTrace();
            }
        } catch (SQLException e) {
            // 处理关闭连接异常
            e.printStackTrace();
        }
    }
}
```