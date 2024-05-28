## JdbcTemplate

- [JdbcTemplate](#jdbctemplate)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、最佳实践](#四最佳实践)
    - [四、源码分析](#四源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

JdbcTemplate是Spring Framework中的一个关键类，用于简化JDBC编程，提供了简洁的方法执行SQL查询、更新和批处理操作，同时处理了JDBC资源的获取、释放和异常处理，使得数据库操作更加简单、高效和安全。

### 三、主要功能

1. **简化的JDBC操作**

    + JdbcTemplate封装了JDBC的复杂性，提供了简洁的方法来执行SQL查询、更新等操作，无需手动管理连接、语句和结果集。

2. **异常处理**

    + JdbcTemplate自动捕获并转换JDBC异常为Spring的DataAccessException，使异常处理更加便捷，无需编写繁琐的异常处理代码。

3. **参数化查询**

    + 支持使用占位符进行参数化查询，防止SQL注入攻击，并且可以简化SQL语句的构建和维护。

4. **批处理操作**

    + 支持批处理操作，可以一次性执行多个SQL语句，提高数据库操作的效率。

5. **回调机制**

    + 提供了回调机制，可以在执行数据库操作前后插入自定义逻辑，如日志记录、性能监控等。

6. **支持ORM框架**

    + 可以与Spring的ORM框架（如Spring Data JPA、Spring Data JDBC）结合使用，提供更高层次的数据库访问抽象。

### 四、最佳实践

使用JdbcTemplate执行SQL查询操作。首先，通过配置数据库连接信息创建了一个`SimpleDriverDataSource`
对象来管理数据源，并创建了一个`DataSourceTransactionManager`对象用于事务管理。然后，通过这些对象创建了一个`JdbcTemplate`
实例。接着，使用`query`方法执行了一个查询操作，将查询结果映射为`Scores`对象的列表，最后打印输出了查询结果。

```java
public class JdbcTemplateDemo {

    public static void main(String[] args) throws Exception {
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

        List<Scores> scoresList = jdbcTemplate.query("select * from scores ", new RowMapper<Scores>() {
            @Override
            public Scores mapRow(ResultSet rs, int rowNum) throws SQLException {
                Scores scores = new Scores();
                scores.setId(rs.getLong("id"));
                scores.setScore(rs.getLong("score"));
                return scores;
            }
        });

        scoresList.forEach(System.out::println);
    }
}
```

`Scores`类是一个简单的Java Bean。

```java
public class Scores {

    private long id;

    private long score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Scores{" +
                "id=" + id +
                ", score=" + score +
                '}';
    }
}
```

运行结果，从数据库查询出的分数记录。

```java
Scores {
    id = 1715586394553, score = 40
}

Scores {
    id = 1715587289293, score = 90
}

Scores {
    id = 1715588070751, score = 20
}

Scores {
    id = 1715668592566, score = 25
}
```

### 四、源码分析

在`org.springframework.jdbc.core.JdbcTemplate#query(java.lang.String, org.springframework.jdbc.core.RowMapper<T>)`
方法中，用于执行SQL查询并将结果映射为对象列表。它接受SQL查询语句和一个RowMapper接口实现作为参数，并将查询结果通过RowMapperResultSetExtractor转换为对象列表后返回。

```java
/**
 * 执行给定的 SQL 查询，并将结果映射为对象列表。
 *
 * @param sql SQL查询语句
 * @param rowMapper 用于将每行结果映射为对象的 RowMapper 接口实现
 * @param <T> 返回结果的类型
 * @return 查询结果的对象列表
 * @throws DataAccessException 如果查询失败或转换结果时发生异常
 */
@Override
public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
    // 调用重载的 query 方法，将结果通过 RowMapperResultSetExtractor 转换为对象列表
    return result(query(sql, new RowMapperResultSetExtractor<>(rowMapper)));
}
```

在`org.springframework.jdbc.core.JdbcTemplate#query(java.lang.String, org.springframework.jdbc.core.ResultSetExtractor<T>)`
方法中，执行SQL查询并使用ResultSetExtractor提取结果。它接受SQL查询语句和一个ResultSetExtractor接口实现作为参数，通过Statement对象执行SQL查询并将结果集交给ResultSetExtractor进行处理，最终返回ResultSetExtractor提取的结果。

```java
/**
 * 执行给定的 SQL 查询，并使用指定的 ResultSetExtractor 提取结果。
 *
 * @param sql SQL查询语句
 * @param rse 用于提取结果的 ResultSetExtractor 接口实现
 * @param <T> 返回结果的类型
 * @return 查询结果，如果没有结果则返回 null
 * @throws DataAccessException 如果查询失败或提取结果时发生异常	
 */
@Nullable
@Override
public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
    // 确保 SQL 查询语句和 ResultSetExtractor 不为空
    Assert.notNull(sql, "SQL must not be null");
    Assert.notNull(rse, "ResultSetExtractor must not be null");

    // 如果开启了调试模式，则记录SQL查询语句
    if (logger.isDebugEnabled()) {
        logger.debug("Executing SQL query [" + sql + "]");
    }

    /**
     * 内部类，用于执行查询操作的回调。
     */
    class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
        /**
         * 在 Statement 中执行查询并提取结果。
         */
        @Override
        @Nullable
        public T doInStatement(Statement stmt) throws SQLException {
            ResultSet rs = null;
            try {
                // 执行查询语句，并获取结果集
                rs = stmt.executeQuery(sql);
                // 使用 ResultSetExtractor 提取结果
                return rse.extractData(rs);
            } finally {
                // 关闭结果集
                JdbcUtils.closeResultSet(rs);
            }
        }

        /**
         * 获取查询的 SQL 语句。
         */
        @Override
        public String getSql() {
            return sql;
        }
    }

    // 执行查询，并返回结果
    return execute(new QueryStatementCallback(), true);
}
```

在`org.springframework.jdbc.core.JdbcTemplate#execute(org.springframework.jdbc.core.StatementCallback<T>, boolean)`
方法中，执行数据库操作回调，并根据需要关闭资源。它接受一个数据库操作回调对象和一个布尔值参数，布尔值参数指示是否在执行完操作后关闭资源。在方法内部，首先获取数据库连接，然后创建Statement对象并应用设置。接着执行数据库操作回调，并处理操作过程中的警告信息。如果操作过程中发生了SQLException，会及时释放连接并抛出DataAccessException。最后，在finally块中根据需要关闭Statement和连接资源。

```java
/**
 * 执行给定的数据库操作回调，并根据需要关闭资源。
 *
 * @param action 数据库操作回调对象
 * @param closeResources 是否关闭资源的标志，如果为 true，则在执行完操作后关闭资源，否则不关闭
 * @param <T> 返回结果的类型
 * @return 执行操作的结果
 * @throws DataAccessException 如果执行操作失败
 */
@Nullable
private <T> T execute(StatementCallback<T> action, boolean closeResources) throws DataAccessException {
    // 确保回调对象不为空
    Assert.notNull(action, "Callback object must not be null");

    // 获取连接
    Connection con = DataSourceUtils.getConnection(obtainDataSource());
    Statement stmt = null;
    try {
        // 创建 Statement
        stmt = con.createStatement();
        // 应用 Statement 的设置
        applyStatementSettings(stmt);
        // 执行数据库操作
        T result = action.doInStatement(stmt);
        // 处理警告信息
        handleWarnings(stmt);
        return result;
    } catch (SQLException ex) {
        // 在可能发生连接池死锁的情况下，尽早释放连接以避免死锁
        String sql = getSql(action);
        JdbcUtils.closeStatement(stmt);
        stmt = null;
        DataSourceUtils.releaseConnection(con, getDataSource());
        con = null;
        // 转换异常并抛出
        throw translateException("StatementCallback", sql, ex);
    } finally {
        // 如果需要关闭资源，则在 finally 中关闭 Statement 和连接
        if (closeResources) {
            JdbcUtils.closeStatement(stmt);
            DataSourceUtils.releaseConnection(con, getDataSource());
        }
    }
}
```

