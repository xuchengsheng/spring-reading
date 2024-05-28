## DataSource

- [DataSource](#datasource)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、接口源码](#四接口源码)
    - [五、最佳实践](#五最佳实践)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`DataSource` 接口是 Java
中用于管理数据库连接的标准接口，它提供了一种抽象的方法来获取数据库连接，使得应用程序能够与不同的数据库进行交互而无需修改代码，通过配置数据源的方式可以更灵活地处理连接池、事务管理以及连接的分配等问题，从而提高了应用程序的性能和可维护性。

### 三、主要功能

1. **提供数据库连接**

    + 定义了获取数据库连接的方法，通过调用这些方法可以获取与数据库的连接，从而进行数据库操作。

2. **连接池管理**

    + 实现连接池，避免了频繁地创建和销毁数据库连接，提高了应用程序的性能。

3. **连接配置**

    + 允许配置连接的相关属性，如数据库地址、用户名、密码等信息，这些配置可以在不同的环境中灵活地进行调整。

4. **资源管理**

    + 负责管理数据库连接资源，包括连接的分配、回收、超时控制等，确保连接的有效性和可用性。

5. **实现数据源的透明性**

    + 应用程序通过 `DataSource` 获取数据库连接，无需关心具体的数据库类型和配置细节，使得代码更具可移植性和扩展性。

### 四、接口源码

`DataSource`用于获取与物理数据源的连接。`DataSource` 提供了两个获取连接的方法：`getConnection()`
和 `getConnection(String username, String password)`
，分别用于获取默认用户和密码的连接以及指定用户和密码的连接。此外，接口还定义了一些管理连接的方法，如获取和设置日志写入器、设置登录超时时间等。接口说明了 `DataSource`
的作用及其实现的方式，以及与 `DriverManager` 的比较。最后，该接口还提供了一个默认方法 `createConnectionBuilder()`
，用于创建连接构建器的实例。

```java
/**
 * <p>此 {@code DataSource} 对象表示对物理数据源的连接工厂。作为 {@code DriverManager} 设施的替代，{@code DataSource} 对象是获取连接的首选方式。实现 {@code DataSource} 接口的对象通常会在基于 Java&trade; Naming and Directory (JNDI) API 的命名服务中注册。</p>
 * <p>{@code DataSource} 接口由驱动程序供应商实现。有三种类型的实现：</p>
 * <ol>
 *   <li>基本实现 -- 生成标准的 {@code Connection} 对象</li>
 *   <li>连接池实现 -- 生成自动参与连接池的 {@code Connection} 对象。该实现与中间层连接池管理器配合使用。</li>
 *   <li>分布式事务实现 -- 生成可用于分布式事务的 {@code Connection} 对象，几乎总是参与连接池。该实现与中间层事务管理器以及几乎总是与连接池管理器一起使用。</li>
 * </ol>
 * <p>{@code DataSource} 对象具有在必要时可以修改的属性。例如，如果数据源移动到不同的服务器，则可以更改服务器的属性。好处在于，因为数据源的属性可以更改，因此访问该数据源的任何代码都不需要更改。</p>
 * <p>通过 {@code DataSource} 对象访问的驱动程序不会向 {@code DriverManager} 注册自己。相反，通过查找操作检索 {@code DataSource} 对象，然后用于创建 {@code Connection} 对象。对于基本实现，通过 {@code DataSource} 对象获取的连接与通过 {@code DriverManager} 设施获取的连接相同。</p>
 * <p>{@code DataSource} 的实现必须包括一个公共的无参数构造函数。</p>
 *
 * @since 1.4
 */
public interface DataSource extends CommonDataSource, Wrapper {

  /**
   * <p>尝试与此 {@code DataSource} 对象表示的数据源建立连接。</p>
   *
   * @return 与数据源的连接
   * @exception SQLException 如果发生数据库访问错误
   * @throws java.sql.SQLTimeoutException  当驱动程序确定 {@code setLoginTimeout} 方法指定的超时值已超过并且至少尝试取消当前数据库连接尝试时
   */
  Connection getConnection() throws SQLException;

  /**
   * <p>尝试使用指定用户和密码与此 {@code DataSource} 对象表示的数据源建立连接。</p>
   *
   * @param username 连接所代表用户的数据库用户
   * @param password 用户的密码
   * @return 与数据源的连接
   * @exception SQLException 如果发生数据库访问错误
   * @throws java.sql.SQLTimeoutException  当驱动程序确定 {@code setLoginTimeout} 方法指定的超时值已超过并且至少尝试取消当前数据库连接尝试时
   * @since 1.4
   */
  Connection getConnection(String username, String password) throws SQLException;

  /**
   * {@inheritDoc}
   * @since 1.4
   */
  @Override
  java.io.PrintWriter getLogWriter() throws SQLException;

  /**
   * {@inheritDoc}
   * @since 1.4
   */
  @Override
  void setLogWriter(java.io.PrintWriter out) throws SQLException;

  /**
   * {@inheritDoc}
   * @since 1.4
   */
  @Override
  void setLoginTimeout(int seconds) throws SQLException;

  /**
   * {@inheritDoc}
   * @since 1.4
   */
  @Override
  int getLoginTimeout() throws SQLException;

  // JDBC 4.3

  /**
   * 创建一个新的 {@code ConnectionBuilder} 实例
   * @implSpec 默认实现将抛出 {@code SQLFeatureNotSupportedException}
   * @return 创建的 ConnectionBuilder 实例
   * @throws SQLException 如果创建构建器时发生错误
   * @throws SQLFeatureNotSupportedException 如果驱动程序不支持分片
   * @since 9
   * @see ConnectionBuilder
   */
  default ConnectionBuilder createConnectionBuilder() throws SQLException {
        throw new SQLFeatureNotSupportedException("createConnectionBuilder not implemented");
  };

}
```

### 五、最佳实践

使用 `DataSource` 来建立与数据库的连接，体现了高内聚的设计原则，有利于后续更改数据库。通过 `MysqlDataSource` 类设置数据库连接的
URL、用户名和密码，然后获取数据库连接。接着，执行 SQL 查询语句，将查询结果输出到控制台。最后，关闭结果集、PreparedStatement
和数据库连接，释放资源。

```java
public class DataSourceDemo {

    public static void main(String[] args) throws Exception {
        // 使用 DataSource 体现了高内聚，有利于后面更改数据库
        MysqlDataSource dataSource = new MysqlDataSource();
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        dataSource.setUrl("jdbc:mysql://localhost:3306/spring-reading");
        // 数据库用户名
        dataSource.setUser("root");
        // 数据库密码
        dataSource.setPassword("123456");

        // 建立数据库连接
        Connection connection = dataSource.getConnection();
        // SQL 查询语句
        String sql = "SELECT * FROM scores";
        // 创建 PreparedStatement 对象，用于执行 SQL 查询
        PreparedStatement statement = connection.prepareStatement(sql);
        // 执行查询，获取结果集
        ResultSet resultSet = statement.executeQuery();

        // 遍历结果集
        while (resultSet.next()) {
            // 获取 id 列的值
            int id = resultSet.getInt("id");
            // 获取 score 列的值
            String score = resultSet.getString("score");
            // 输出结果
            System.out.println("id: " + id + ", score: " + score);
        }
        // 关闭结果集、PreparedStatement 和数据库连接
        resultSet.close();
        statement.close();
        connection.close();
    }
}
```

运行结果，从 "spring-reading" 的数据库的 "scores" 表中检索到的数据。每行都包括一个 "id" 和一个 "score" 列。

```java
id: 1, score: 3.50
id: 2, score: 3.65
id: 3, score: 4.00
id: 4, score: 3.85
id: 5, score: 4.00
id: 6, score: 3.65
```

