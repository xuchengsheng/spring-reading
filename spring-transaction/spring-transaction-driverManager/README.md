## DriverManager

- [DriverManager](#drivermanager)
    - [一、基本信息](#一基本信息)
    - [二、基本描述](#二基本描述)
    - [三、主要功能](#三主要功能)
    - [四、最佳实践](#四最佳实践)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址
** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`DriverManager` 是 Java 标准库中的一个类，用于管理 JDBC 驱动程序，提供了加载驱动程序和建立数据库连接的静态方法，使得 Java
应用程序能够方便地与各种数据库进行交互。

### 三、主要功能

1. **加载数据库驱动程序**

    + 通过 `registerDriver()` 方法注册数据库驱动程序，使得 `DriverManager` 能够识别和加载特定数据库的驱动程序。

2. **建立数据库连接**

    + 通过 `getConnection()` 方法，根据指定的数据库 URL、用户名和密码获取数据库连接对象，以便后续对数据库进行操作。

3. **管理数据库连接**

    + `DriverManager` 负责管理数据库连接对象，确保连接的安全性和可用性，并提供了方法来获取、关闭数据库连接。

4. **卸载驱动程序**

    + 通过 `deregisterDriver()` 方法卸载已注册的数据库驱动程序，释放相关资源。

### 四、最佳实践

使用 `java.sql.DriverManager` 类来连接到 MySQL 数据库，并执行一个简单的查询操作。通过指定数据库连接的
URL、用户名和密码，它建立了与名为 "spring-reading" 的数据库的连接，然后执行了一个查询语句来获取名为 "scores"
的表中的数据。最后，它遍历结果集并将每行数据的 "id" 和 "score" 列打印出来，然后关闭了结果集、预处理语句对象和数据库连接。

```java
public class DriverManagerDemo {

    public static void main(String[] args) throws Exception {
        // 数据库连接 URL，格式为 jdbc:数据库驱动名称://主机地址:端口号/数据库名称
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // 数据库用户名
        String username = "root";
        // 数据库密码
        String password = "123456";

        // 建立数据库连接
        Connection connection = DriverManager.getConnection(url, username, password);
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
id:1,score:3.50
id:2,score:3.65
id:3,score:4.00
id:4,score:3.85
id:5,score:4.00
id:6,score:3.65
```

