## TypeComparator

- [TypeComparator](#TypeComparator)
    - [一、基本信息](#一基本信息)
    - [二、知识储备](#二知识储备)
    - [三、基本描述](#三基本描述)
    - [四、主要功能](#四主要功能)
    - [五、接口源码](#五接口源码)
    - [六、主要实现](#六主要实现)
    - [七、最佳实践](#七最佳实践)
    - [八、与其他组件的关系](#八与其他组件的关系)
    - [九、常见问题](#九常见问题)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. **类型系统**

   + 了解编程语言中的类型系统是很重要的。这包括静态类型和动态类型语言的区别，以及强类型和弱类型语言的区别。理解类型的继承、实现和转换规则对于理解类型比较是很有帮助的。

2. **泛型**

   + 泛型是 Java 编程语言中的一个重要特性，它允许类、接口和方法在定义时使用参数类型。理解泛型如何在编译时和运行时被处理，以及泛型擦除的概念对于理解 `TypeComparator` 接口的实现可能是有帮助的。

3. **反射**

   + 反射是 Java 编程语言中的一个强大特性，它允许程序在运行时检查和操作类、对象、方法和属性。了解如何使用反射来获取类的信息、调用方法以及操作字段对于实现 `TypeComparator` 接口可能是有帮助的。

4. **设计模式**

   + 了解一些常见的设计模式，如策略模式和适配器模式，可能有助于理解 `TypeComparator` 接口的设计和用法。

### 三、基本描述

`TypeComparator` 接口是 Spring Framework 中的一个重要组件，用于定义对象类型的比较器。通过实现该接口，您可以自定义对象的类型比较逻辑，以满足特定应用场景下的需求，例如在 Spring 表达式语言（SpEL）中确定两个对象的类型是否相同或是否可以进行比较。

### 四、主要功能

1. **类型比较**

   + `TypeComparator` 接口定义了 `compare(T o1, T o2)` 方法，用于比较两个对象的类型。我们可以根据自己的需求实现该方法，以确定两个对象的类型是否相同或是否在继承层次结构中相互关联。

2. **可比较性判断**

   + `TypeComparator` 接口定义了 `canCompare(Object o1, Object o2)` 方法，用于确定两个对象是否可以进行比较。这在某些情况下是必要的，例如在进行对象类型比较之前，需要先检查对象是否为特定类型或是否具有特定的属性。

3. **自定义比较逻辑**

   + 通过实现 `TypeComparator` 接口，我们可以定义自己的比较逻辑，以满足特定应用场景下的需求。例如，可以根据对象的某些属性或特征来确定对象的类型，而不仅仅是基于 Java 类型系统的默认行为。

4. **扩展性**

   + `TypeComparator` 接口为 Spring Framework 提供了一种灵活和可扩展的机制，用于处理对象类型比较相关的任务。通过实现自定义的 `TypeComparator`，可以轻松地扩展 Spring 应用程序的功能，以满足不同的业务需求。

### 五、接口源码

`TypeComparator` 接口定义了对象类型比较器的标准，要求实现类能够比较两个对象是否相等，并且提供了确定对象是否可比较的方法。该接口的主要目的是为了支持 Spring 表达式语言（SpEL）中的对象类型比较操作，提供了灵活的机制来定制对象类型的比较逻辑。

```java
/**
 * 实现类型比较器的实例应该能够比较一对对象是否相等。
 * 返回值的规范与 {@link java.lang.Comparable} 相同。
 *
 * @author Andy Clement
 * @since 3.0
 * @see java.lang.Comparable
 */
public interface TypeComparator {

    /**
     * 如果比较器能够比较这两个对象，则返回 {@code true}。
     * @param firstObject 第一个对象
     * @param secondObject 第二个对象
     * @return 如果比较器能够比较这两个对象，则返回 {@code true}
     */
    boolean canCompare(@Nullable Object firstObject, @Nullable Object secondObject);

    /**
     * 比较给定的两个对象。
     * @param firstObject 第一个对象
     * @param secondObject 第二个对象
     * @return 如果它们相等，则返回 0；如果第一个对象小于第二个对象，则返回 <0；如果第一个对象大于第二个对象，则返回 >0
     * @throws EvaluationException 如果在比较过程中出现问题（或者它们本来就无法比较）
     */
    int compare(@Nullable Object firstObject, @Nullable Object secondObject) throws EvaluationException;

}
```

`StandardTypeComparator` 是 `TypeComparator` 接口的标准实现，支持对 `Number` 类型以及实现了 `Comparable` 接口的类型进行比较。它提供了针对不同类型的对象进行比较的逻辑，包括基本数值类型、BigDecimal、BigInteger 等，并提供了一个灵活而可靠的机制来执行对象类型的比较操作。

```java
/**
 * {@link TypeComparator} 接口的基本实现：支持对 {@link Number} 类型以及实现了 {@link Comparable} 接口的类型进行比较。
 * 
 * 作者：Andy Clement
 * 作者：Juergen Hoeller
 * 作者：Giovanni Dall'Oglio Risso
 * 自版本 3.0 起
 */
public class StandardTypeComparator implements TypeComparator {

	@Override
	public boolean canCompare(@Nullable Object left, @Nullable Object right) {
		// 如果其中一个对象为 null，则认为可以进行比较
		if (left == null || right == null) {
			return true;
		}
		// 如果两个对象都是 Number 类型，则可以进行比较
		if (left instanceof Number && right instanceof Number) {
			return true;
		}
		// 如果左侧对象实现了 Comparable 接口，则可以进行比较
		if (left instanceof Comparable) {
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compare(@Nullable Object left, @Nullable Object right) throws SpelEvaluationException {
		// 如果其中一个对象为 null，则根据情况返回相应值
		if (left == null) {
			return (right == null ? 0 : -1);
		}
		else if (right == null) {
			return 1;  // 此时左侧对象不可能为 null
		}

		// 基本的数值比较
		if (left instanceof Number && right instanceof Number) {
			Number leftNumber = (Number) left;
			Number rightNumber = (Number) right;

			if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
				// 处理 BigDecimal 类型的比较
				BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
				BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
				return leftBigDecimal.compareTo(rightBigDecimal);
			}
            // 其他数值类型的比较，包括 Double、Float、BigInteger、Long、Integer、Short、Byte
            // 如果不属于以上类型，采用 double 类型的乘法来比较
			else if (leftNumber instanceof Double || rightNumber instanceof Double) {
				return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
			}
            // 其他数值类型的比较
			else if (leftNumber instanceof Float || rightNumber instanceof Float) {
				return Float.compare(leftNumber.floatValue(), rightNumber.floatValue());
			}
			else if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
				BigInteger leftBigInteger = NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
				BigInteger rightBigInteger = NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
				return leftBigInteger.compareTo(rightBigInteger);
			}
			else if (leftNumber instanceof Long || rightNumber instanceof Long) {
				return Long.compare(leftNumber.longValue(), rightNumber.longValue());
			}
			else if (leftNumber instanceof Integer || rightNumber instanceof Integer) {
				return Integer.compare(leftNumber.intValue(), rightNumber.intValue());
			}
			else if (leftNumber instanceof Short || rightNumber instanceof Short) {
				return Short.compare(leftNumber.shortValue(), rightNumber.shortValue());
			}
			else if (leftNumber instanceof Byte || rightNumber instanceof Byte) {
				return Byte.compare(leftNumber.byteValue(), rightNumber.byteValue());
			}
			else {
				// 未知的 Number 子类型 -> 最佳猜测是 double 类型的乘法
				return Double.compare(leftNumber.doubleValue(), rightNumber.doubleValue());
			}
		}

		try {
			// 如果左侧对象实现了 Comparable 接口，则使用 compareTo 方法进行比较
			if (left instanceof Comparable) {
				return ((Comparable<Object>) left).compareTo(right);
			}
		}
		catch (ClassCastException ex) {
			// 抛出异常，表示两个对象不可比较
			throw new SpelEvaluationException(ex, SpelMessage.NOT_COMPARABLE, left.getClass(), right.getClass());
		}

		// 抛出异常，表示两个对象不可比较
		throw new SpelEvaluationException(SpelMessage.NOT_COMPARABLE, left.getClass(), right.getClass());
	}

}
```

### 六、主要实现

1. **StandardTypeComparator**
   + `StandardTypeComparator` 实现类是 `TypeComparator` 接口的标准实现，提供了对 `Number` 类型和实现了 `Comparable` 接口的类型进行比较的功能。

### 七、最佳实践

使用 Spring 表达式语言（SpEL）中的 `TypeComparator` 接口进行对象类型的比较操作。首先，创建了一个 `StandardEvaluationContext` 对象作为评估上下文，并创建了一个 `SpelExpressionParser` 对象用于解析 SpEL 表达式。然后，使用解析器解析了一个包含比较运算符的表达式，将其表示为 `Expression` 对象。接下来，通过调用 `getValue` 方法并传入评估上下文，利用 SpEL 引擎执行了表达式，并使用 `TypeComparator` 进行比较，最后打印了比较结果。

```java
public class TypeComparatorDemo {
    public static void main(String[] args) {
        // 创建一个EvaluationContext
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // 创建SpEL表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();
        
        // 解析表达式
        Expression expression = parser.parseExpression("'2' < '-5.0'");
        
        // 使用TypeComparator进行比较
        boolean result = expression.getValue(context,Boolean.class);
        
        // 打印比较后的值
        System.out.println("result : " + result);
    }
}
```

运行结果，表达式 `'2' < '-5.0'` 返回了 `false`。这意味着在 SpEL 中，字符串 `'2'` 被视为不小于字符串 `'-5.0'`，因此比较结果为 `false`。

```java
result : false
```

### 八、与其他组件的关系

1. **java.lang.Comparable**

   + `TypeComparator` 接口与 `Comparable` 接口有一定的关系。`Comparable` 接口定义了对象自身的自然顺序，而 `TypeComparator` 接口提供了比较任意两个对象的类型的机制。在 `StandardTypeComparator` 实现类中，会利用 `Comparable` 接口的实现来进行对象的比较。

2. **StandardTypeComparator**

   + `StandardTypeComparator` 是 `TypeComparator` 接口的默认实现类，它提供了一组用于比较对象类型的标准逻辑。这个类在 Spring Framework 中被广泛使用，特别是在 SpEL 中用于处理类型比较的场景。

3. **StandardEvaluationContext**

   + 在 SpEL 中，用于存储表达式求值所需的上下文信息。在 `TypeComparatorDemo` 类中，就使用了 `StandardEvaluationContext` 类来创建一个上下文对象，以便进行 SpEL 表达式的求值。

4. **`SpelExpressionParser` 类**

   + 用于解析 SpEL 表达式的类。在 `TypeComparatorDemo` 类中，就使用了 `SpelExpressionParser` 类来创建一个表达式解析器对象，以便解析 SpEL 表达式。

### 九、常见问题

1. **自定义比较逻辑**

   + 我们可能会遇到需要定义自己的比较逻辑以满足特定需求的情况。这可能涉及到对特定类型的对象进行特殊处理或考虑继承关系。

2. **类型转换问题**

   + 在比较对象时，可能会遇到类型转换问题，特别是当涉及到不同类型的对象时。我们需要确保在比较之前将对象转换为正确的类型。