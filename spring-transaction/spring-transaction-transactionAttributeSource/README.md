## TransactionAttributeSource

- [TransactionAttributeSource](#TransactionAttributeSource)
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

`TransactionAttributeSource` 接口是 Spring Framework
中的关键接口，用于提供事务管理的配置信息，通过分析给定的方法和目标类，确定事务的属性，例如传播行为、隔离级别等，为声明式事务提供了灵活性和可定制性。

### 三、主要功能

1. **提供事务属性**

    + 根据给定的方法和目标类，确定事务的属性，包括传播行为、隔离级别、超时时间、只读状态等。

2. **可扩展性**

    + Spring 框架提供了多种实现 `TransactionAttributeSource`
      接口的类，如 `NameMatchTransactionAttributeSource`、`AnnotationTransactionAttributeSource`
      等，以支持不同的解析策略，例如基于方法名的匹配、基于注解的配置等。

### 四、接口源码

`TransactionAttributeSource` 接口，主要是由 `TransactionInterceptor`
用于元数据检索的策略接口。该接口的实现知道如何获取事务属性，可以从配置、源级别的元数据属性或其他任何地方获取。它包含了两个方法，一个用于确定给定类是否是事务属性的候选类，另一个用于返回给定方法的事务属性。

```java
/**
 * {@code TransactionAttributeSource} 接口是由 {@link TransactionInterceptor} 用于元数据检索的策略接口。
 * 实现类知道如何获取事务属性，无论是从配置、源级别的元数据属性（例如 Java 5 注解）还是其他任何地方。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 15.04.2003
 * @see TransactionInterceptor#setTransactionAttributeSource
 * @see TransactionProxyFactoryBean#setTransactionAttributeSource
 * @see org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
 */
public interface TransactionAttributeSource {

    /**
     * 确定给定的类是否是此 {@code TransactionAttributeSource} 的元数据格式中事务属性的候选类。
     * <p>如果此方法返回 {@code false}，则不会遍历给定类的方法以进行 {@link #getTransactionAttribute} 内省。
     * 返回 {@code false} 因此是对非受影响类的优化，而 {@code true} 则意味着必须针对给定类的每个方法进行完全内省。
     * @param targetClass 要内省的类
     * @return 如果类已知在类级别或方法级别没有事务属性，则返回 {@code false}；否则返回 {@code true}。
     * 默认实现返回 {@code true}，导致常规内省。
     * @since 5.2
     */
    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    /**
     * 返回给定方法的事务属性，如果方法不是事务性的，则返回 {@code null}。
     * @param method 要内省的方法
     * @param targetClass 目标类（可能为 {@code null}，在这种情况下，必须使用方法的声明类）
     * @return 匹配的事务属性，如果未找到则返回 {@code null}
     */
    @Nullable
    TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass);

}
```

### 五、主要实现

1. **AnnotationTransactionAttributeSource**

    + 用于解析基于注解的事务配置信息的实现类。它能够解析类级别和方法级别的 `@Transactional`
      注解，将注解中定义的事务属性转换为 `TransactionAttribute` 对象。

2. **CompositeTransactionAttributeSource**

    + 将多个 `TransactionAttributeSource` 组合在一起。通过组合多个 `TransactionAttributeSource`
      对象，可以实现多种事务属性解析策略的混合使用，提高了灵活性和定制性。

3. **MatchAlwaysTransactionAttributeSource**

    + 简单的 `TransactionAttributeSource`
      实现，它始终返回相同的事务属性。通常用于简单的场景或者作为其他复杂 `TransactionAttributeSource` 实现的默认备选项。

4. **MethodMapTransactionAttributeSource**

    + 基于方法名匹配的 `TransactionAttributeSource` 实现。它通过配置一个方法名到事务属性的映射表，根据方法名来确定相应的事务属性。

5. **NameMatchTransactionAttributeSource**

    + 根据方法名进行匹配的 `TransactionAttributeSource` 实现。它能够根据配置的方法名模式，匹配目标方法并返回相应的事务属性。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AbstractFallbackTransactionAttributeSource
class AnnotationTransactionAttributeSource
class CompositeTransactionAttributeSource
class MatchAlwaysTransactionAttributeSource
class MethodMapTransactionAttributeSource
class NameMatchTransactionAttributeSource
class TransactionAttributeSource {
<<Interface>>

}

AbstractFallbackTransactionAttributeSource  ..>  TransactionAttributeSource 
AnnotationTransactionAttributeSource  -->  AbstractFallbackTransactionAttributeSource 
CompositeTransactionAttributeSource  ..>  TransactionAttributeSource 
MatchAlwaysTransactionAttributeSource  ..>  TransactionAttributeSource 
MethodMapTransactionAttributeSource  ..>  TransactionAttributeSource 
NameMatchTransactionAttributeSource  ..>  TransactionAttributeSource 
~~~

### 六、最佳实践

使用 `AnnotationTransactionAttributeSource` 类来解析基于注解的事务配置信息。通过获取 `ScoresServiceImpl`
类中的 `insertScore` 方法，然后利用 `AnnotationTransactionAttributeSource`
对象来解析该方法的事务属性，最后将解析结果输出到控制台。这样可以帮助我们了解特定方法的事务配置情况，以便进行调试和优化。

```java
public class TransactionAttributeSourceDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // 获取 ScoresServiceImpl 类中的 insertScore 方法
        Method insertScoreMethod = ScoresServiceImpl.class.getMethod("insertScore");
        // 创建一个基于注解的事务属性源对象
        TransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();
        // 解析 insertScore 方法的事务属性
        TransactionAttribute transactionAttribute = transactionAttributeSource.getTransactionAttribute(insertScoreMethod, ScoresServiceImpl.class);
        // 输出事务属性
        System.out.println(transactionAttribute);
    }
}
```

`ScoresServiceImpl` 类实现了 `ScoresService` 接口，其中的 `insertScore` 方法被 `@Transactional`
注解修饰，声明了一个事务。该事务的特性包括只读（readOnly = true），遇到任何异常都会回滚（rollbackFor =
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

### 七、源码分析

在`org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource#getTransactionAttribute`
方法中，用于确定方法调用的事务属性。如果在方法级别找不到事务属性，则默认使用类级别的事务属性。首先，它检查是否有缓存的事务属性值，如果有则直接返回缓存值，否则计算方法的事务属性并将其放入缓存。在计算事务属性时，会根据方法的限定名来标识方法，如果事务属性是 `DefaultTransactionAttribute`
类型，则设置描述符和解析属性字符串。最后，根据日志级别输出添加事务方法及其属性的日志，并将计算得到的事务属性放入缓存。

```java
/**
 * 确定此方法调用的事务属性。
 * <p>如果未找到方法属性，则默认为类的事务属性。
 * @param method 当前调用的方法（永远不会为 {@code null}）
 * @param targetClass 此调用的目标类（可能为 {@code null}）
 * @return 此方法的 TransactionAttribute，如果方法不是事务性的，则返回 {@code null}
 */
@Override
@Nullable
public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
    // 如果方法是 Object 类的方法，直接返回 null，因为这些方法不应该是事务性的。
    if (method.getDeclaringClass() == Object.class) {
        return null;
    }

    // 首先，查看是否有缓存值。
    Object cacheKey = getCacheKey(method, targetClass);
    TransactionAttribute cached = this.attributeCache.get(cacheKey);
    if (cached != null) {
        // 值将是一个规范值，表示没有事务属性，或者是一个实际的事务属性。
        if (cached == NULL_TRANSACTION_ATTRIBUTE) {
            return null;
        }
        else {
            return cached;
        }
    }
    else {
        // 我们需要计算它。
        TransactionAttribute txAttr = computeTransactionAttribute(method, targetClass);
        // 将其放入缓存。
        if (txAttr == null) {
            this.attributeCache.put(cacheKey, NULL_TRANSACTION_ATTRIBUTE);
        }
        else {
            // 获取方法的限定名，用于在日志中标识方法。
            String methodIdentification = ClassUtils.getQualifiedMethodName(method, targetClass);
            // 如果事务属性是 DefaultTransactionAttribute 类型，设置描述符和解析属性字符串。
            if (txAttr instanceof DefaultTransactionAttribute) {
                DefaultTransactionAttribute dta = (DefaultTransactionAttribute) txAttr;
                dta.setDescriptor(methodIdentification);
                dta.resolveAttributeStrings(this.embeddedValueResolver);
            }
            // 如果日志级别为 TRACE，输出添加事务方法及其属性的日志。
            if (logger.isTraceEnabled()) {
                logger.trace("Adding transactional method '" + methodIdentification + "' with attribute: " + txAttr);
            }
            this.attributeCache.put(cacheKey, txAttr);
        }
        return txAttr;
    }
}
```

在`org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource#computeTransactionAttribute`
方法中，首先检查方法是否是公共方法，并根据情况从目标类或原始方法中查找事务属性。如果找到了事务属性，则返回该属性；否则返回
null。

```java
/**
 * 与 {@link #getTransactionAttribute} 具有相同的签名，但不缓存结果。
 * {@link #getTransactionAttribute} 实际上是此方法的缓存装饰器。
 * <p>从 4.1.8 版本开始，此方法可以被重写。
 * @since 4.1.8
 * @see #getTransactionAttribute
 */
@Nullable
protected TransactionAttribute computeTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
	// 如果只允许公共方法，并且方法不是公共的，则不允许。
	if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
		return null;
	}

	// 方法可能在接口上，但我们需要从目标类获取属性。
	// 如果目标类为 null，则方法不会改变。
	Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

	// 首先尝试目标类中的方法。
	TransactionAttribute txAttr = findTransactionAttribute(specificMethod);
	if (txAttr != null) {
		return txAttr;
	}

	// 其次尝试目标类上的事务属性。
	txAttr = findTransactionAttribute(specificMethod.getDeclaringClass());
	if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
		return txAttr;
	}

	if (specificMethod != method) {
		// 回退到原始方法。
		txAttr = findTransactionAttribute(method);
		if (txAttr != null) {
			return txAttr;
		}
		// 最后回退到原始方法的类。
		txAttr = findTransactionAttribute(method.getDeclaringClass());
		if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
			return txAttr;
		}
	}

	return null;
}
```

在`org.springframework.transaction.annotation.AnnotationTransactionAttributeSource#findTransactionAttribute(java.lang.reflect.Method)`
方法中，调用了 `determineTransactionAttribute` 方法来确定给定方法的事务属性，并将其返回。如果无法确定事务属性，则返回 null。

```java
@Override
@Nullable
protected TransactionAttribute findTransactionAttribute(Method method) {
    return determineTransactionAttribute(method);
}
```

在`org.springframework.transaction.annotation.AnnotationTransactionAttributeSource#determineTransactionAttribute`
方法中，用于确定给定方法或类的事务属性。它通过配置的 `TransactionAnnotationParsers` 来解析已知注解，并将其转换为 Spring
的元数据属性类。如果找不到事务属性，则返回 null。该方法可以被重写以支持携带事务元数据的自定义注解。

```java
/**
 * 确定给定方法或类的事务属性。
 * <p>此实现委托给配置的 {@link TransactionAnnotationParser TransactionAnnotationParsers}，
 * 用于将已知的注解解析为 Spring 的元数据属性类。
 * 如果不是事务性的，则返回 {@code null}。
 * <p>可以重写此方法以支持携带事务元数据的自定义注解。
 * @param element 带有注解的方法或类
 * @return 配置的事务属性，如果找不到则返回 {@code null}
 */
@Nullable
protected TransactionAttribute determineTransactionAttribute(AnnotatedElement element) {
    // 遍历所有的 TransactionAnnotationParser 实例
    for (TransactionAnnotationParser parser : this.annotationParsers) {
        // 解析注解，获取事务属性
        TransactionAttribute attr = parser.parseTransactionAnnotation(element);
        // 如果找到事务属性，则返回
        if (attr != null) {
            return attr;
        }
    }
    // 如果未找到事务属性，则返回 null
    return null;
}
```

