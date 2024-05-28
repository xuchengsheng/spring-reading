## TransactionAnnotationParser

- [TransactionAnnotationParser](#transactionannotationparser)
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

`TransactionAnnotationParser` 接口是 Spring Framework 中的一个接口，用于定义解析事务相关注解的标准方式，我们可以通过实现该接口来自定义解析特定事务注解的逻辑，并将其转换为
Spring 内部的事务配置对象，以实现更灵活的事务管理。

### 三、主要功能

1. **解析事务注解**

    + 该接口定义了解析事务相关注解的方法，我们可以通过实现该方法来提取注解中的属性信息。

2. **支持多种注解**

    + 允许实现类支持多种事务相关的注解，不仅限于 `@Transactional`，这使得接口更加灵活和通用。

3. **自定义扩展**

    + 根据自己的需求实现该接口，并自定义解析逻辑，以满足特定场景下的事务管理需求，从而扩展 Spring Framework 的事务管理功能。

### 四、接口源码

用于解析已知的事务注解类型。它包括一个默认方法用于确定给定类是否是事务注解的候选类，以及一个方法用于解析给定方法或类上的事务注解并将其转换为
Spring 框架的事务属性对象。该接口的实现类可用于支持特定的事务注解类型，如 Spring 的 `@Transactional`、JTA 1.2
的 `javax.transaction.Transactional` 或 EJB3 的 `javax.ejb.TransactionAttribute`。

```java
/**
 * 已知事务注解类型解析的策略接口。
 * {@link AnnotationTransactionAttributeSource} 委托给此类解析器，以支持特定的注解类型，例如 Spring 的 {@link Transactional}、JTA 1.2 的 {@link javax.transaction.Transactional} 或 EJB3 的 {@link javax.ejb.TransactionAttribute}。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see AnnotationTransactionAttributeSource
 * @see SpringTransactionAnnotationParser
 * @see Ejb3TransactionAnnotationParser
 * @see JtaTransactionAnnotationParser
 */
public interface TransactionAnnotationParser {

    /**
     * 确定给定类是否是此 {@code TransactionAnnotationParser} 的注解格式中的事务属性候选类。
     * <p>如果此方法返回 {@code false}，则给定类上的方法将不会被遍历用于 {@code #parseTransactionAnnotation} 内省。
     * 返回 {@code false} 是针对未受影响的类的优化，而 {@code true} 意味着类需要对给定类上的每个方法进行完整的内省。
     * @param targetClass 要内省的类
     * @return 如果已知该类在类或方法级别上没有事务注解，则返回 {@code false}；否则返回 {@code true}。默认实现返回 {@code true}，导致常规内省。
     * @since 5.2
     */
    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    /**
     * 根据此解析器理解的注解类型，解析给定方法或类的事务属性。
     * <p>本质上，这将已知的事务注解解析为 Spring 的元数据属性类。如果方法/类不是事务性的，则返回 {@code null}。
     * @param element 被注解的方法或类
     * @return 配置的事务属性，如果没有找到则返回 {@code null}
     * @see AnnotationTransactionAttributeSource#determineTransactionAttribute
     */
    @Nullable
    TransactionAttribute parseTransactionAnnotation(AnnotatedElement element);

}
```

### 五、主要实现

1. **SpringTransactionAnnotationParser**

    + 用于解析 Spring Framework 中的 `@Transactional` 注解，它能够将 `@Transactional` 注解中的属性信息提取出来，并转换为
      Spring 内部的事务配置对象。在 Spring 应用中，通常使用 `@Transactional` 注解声明事务，因此这个解析器是非常常用的，它使得事务管理更加便捷和灵活。

2. **Ejb3TransactionAnnotationParser**

    + 用于解析 EJB3 规范中的 `javax.ejb.TransactionAttribute` 注解。EJB3 是 Java EE
      规范中的一部分，用于开发企业级应用程序。`Ejb3TransactionAnnotationParser` 负责解析 `javax.ejb.TransactionAttribute`
      注解，并将其转换为 Spring 内部的事务配置对象，这使得 Spring 能够与 EJB3 技术集成，实现统一的事务管理。

3. **JtaTransactionAnnotationParser**

    + 用于解析 JTA（Java Transaction API）规范中定义的 `javax.transaction.Transactional` 注解。JTA 是 Java 平台的一部分，提供了一套
      API 用于管理分布式事务。`JtaTransactionAnnotationParser` 负责解析 `javax.transaction.Transactional` 注解，并将其转换为
      Spring 内部的事务配置对象，这使得 Spring 能够与分布式事务相关的技术集成，实现全面的事务管理支持。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class Ejb3TransactionAnnotationParser
class JtaTransactionAnnotationParser
class SpringTransactionAnnotationParser
class TransactionAnnotationParser {
<<Interface>>

}

Ejb3TransactionAnnotationParser  ..>  TransactionAnnotationParser 
JtaTransactionAnnotationParser  ..>  TransactionAnnotationParser 
SpringTransactionAnnotationParser  ..>  TransactionAnnotationParser 
~~~

### 七、最佳实践

使用 `SpringTransactionAnnotationParser`
类来解析方法上的事务注解，并将其转换为事务属性对象。在这个示例中，通过反射获取了 `ScoresServiceImpl` 类中的 `insertScore`
方法，然后通过 `SpringTransactionAnnotationParser` 解析该方法上的事务注解，最后将解析结果输出到控制台。

```java
public class SpringTransactionAnnotationParserDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // 获取 ScoresServiceImpl 类中的 insertScore 方法
        Method insertScoreMethod = ScoresServiceImpl.class.getMethod("insertScore");
        // 创建 SpringTransactionAnnotationParser 实例
        SpringTransactionAnnotationParser parser = new SpringTransactionAnnotationParser();
        // 解析 insertScore 方法上的事务注解，并转换为事务属性对象
        TransactionAttribute transactionAttribute = parser.parseTransactionAnnotation(insertScoreMethod);
        // 输出事务属性对象
        System.out.println(transactionAttribute);
    }
}
```

`ScoresServiceImpl` 类实现了 `ScoresService` 接口，其中的 `insertScore` 方法被 `@Transactional`
注解修饰，声明了一个事务。该事务的特性包括：只读（readOnly = true），遇到任何异常都会回滚（rollbackFor =
Exception.class），事务隔离级别为可重复读（isolation = Isolation.REPEATABLE_READ），超时时间为 30 秒（timeout =
30），以及标签为 "tx1" 和 "tx2"。

```java
public class ScoresServiceImpl implements ScoresService {

    @Override
    @Transactional(
            readOnly = true,
            rollbackFor = Exception.class,
            isolation = Isolation.REPEATABLE_READ,
            timeout = 30,
            label = {"tx1", "tx2"}
    )
    public void insertScore() {
        // TODO
    }
}
```

运行结果，事务的传播行为为 `PROPAGATION_REQUIRED`，隔离级别为 `ISOLATION_REPEATABLE_READ`，超时时间为 30
秒，只读模式开启，标签为 "tx1" 和 "tx2"，同时，事务会回滚遇到 `java.lang.Exception` 及其子类的异常。

```java
PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout_30,readOnly; [tx1,tx2],-java.lang.Exception
```

### 八、源码分析

在`org.springframework.transaction.annotation.SpringTransactionAnnotationParser#parseTransactionAnnotation(java.lang.reflect.AnnotatedElement)`
方法中，首先通过 `AnnotatedElementUtils` 查找并获取元素上合并的 `@Transactional`
注解的属性信息，如果找到了注解，则调用另一个方法解析事务注解并返回事务属性对象，如果未找到注解，则返回 null。

```java

@Override
@Nullable
public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
    // 查找并获取元素上合并的 @Transactional 注解的属性信息
    AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
            element, Transactional.class, false, false);
    // 如果属性信息不为空，则解析事务注解并返回事务属性对象
    if (attributes != null) {
        return parseTransactionAnnotation(attributes);
    }
    // 如果属性信息为空，则返回 null
    else {
        return null;
    }
}
```

在`org.springframework.transaction.annotation.SpringTransactionAnnotationParser#parseTransactionAnnotation(org.springframework.core.annotation.AnnotationAttributes)`
方法中，用于解析给定的注解属性并将其转换为事务属性对象。它根据注解属性中的信息设置事务的传播行为、隔离级别、超时时间、只读属性、限定符、标签和回滚规则等。最后，它返回一个基于规则的事务属性对象。

```java
protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
    // 创建一个基于规则的事务属性对象
    RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();

    // 设置事务传播行为
    Propagation propagation = attributes.getEnum("propagation");
    rbta.setPropagationBehavior(propagation.value());
    // 设置事务隔离级别
    Isolation isolation = attributes.getEnum("isolation");
    rbta.setIsolationLevel(isolation.value());

    // 设置事务超时时间
    rbta.setTimeout(attributes.getNumber("timeout").intValue());
    // 设置事务超时时间字符串
    String timeoutString = attributes.getString("timeoutString");
    // 校验是否同时设置了超时时间和超时时间字符串
    Assert.isTrue(!StringUtils.hasText(timeoutString) || rbta.getTimeout() < 0,
            "Specify 'timeout' or 'timeoutString', not both");
    rbta.setTimeoutString(timeoutString);

    // 设置事务是否为只读模式
    rbta.setReadOnly(attributes.getBoolean("readOnly"));
    // 设置事务限定符
    rbta.setQualifier(attributes.getString("value"));
    // 设置事务标签
    rbta.setLabels(Arrays.asList(attributes.getStringArray("label")));

    // 解析回滚规则
    List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
    // 解析需要回滚的异常类
    for (Class<?> rbRule : attributes.getClassArray("rollbackFor")) {
        rollbackRules.add(new RollbackRuleAttribute(rbRule));
    }
    // 解析需要回滚的异常类名
    for (String rbRule : attributes.getStringArray("rollbackForClassName")) {
        rollbackRules.add(new RollbackRuleAttribute(rbRule));
    }
    // 解析不需要回滚的异常类
    for (Class<?> rbRule : attributes.getClassArray("noRollbackFor")) {
        rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
    }
    // 解析不需要回滚的异常类名
    for (String rbRule : attributes.getStringArray("noRollbackForClassName")) {
        rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
    }
    // 设置事务的回滚规则
    rbta.setRollbackRules(rollbackRules);

    // 返回解析后的事务属性对象
    return rbta;
}
```