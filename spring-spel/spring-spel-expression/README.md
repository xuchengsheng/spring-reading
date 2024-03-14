## Expression

- [Expression](#expression)
  - [一、基本信息](#一基本信息)
  - [二、知识储备](#二知识储备)
  - [三、基本描述](#三基本描述)
  - [四、主要功能](#四主要功能)
  - [五、接口源码](#五接口源码)
  - [六、主要实现](#六主要实现)
  - [七、最佳实践](#七最佳实践)
  - [八、时序图](#八时序图)
  - [九、源码分析](#九源码分析)
  - [十、与其他组件的关系](#十与其他组件的关系)
  - [十一、常见问题](#十一常见问题)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. **表达式语言（SpEL）**

   + 理解表达式语言的基本语法和功能，包括表达式的结构、操作符、函数、变量引用等。熟悉 SpEL 的使用场景和用法，以及在 Spring 中如何应用 SpEL。

2. **评估上下文（Evaluation Context）**

   + 了解评估表达式时的上下文环境，包括根对象、变量、函数注册等。理解如何在评估表达式时提供自定义的上下文。

3. **类型转换和类型推断**

   + 理解表达式的类型转换机制，以及如何进行类型推断和类型安全的表达式评估。

4. **使用 SpEL 解析器**

   + 熟悉如何使用 `SpelExpressionParser` 类来解析表达式字符串并创建 `Expression` 对象，以及如何对表达式进行评估。

### 三、基本描述

`Expression` 接口是 Spring Framework 中表达式语言（SpEL）模块的关键组件之一，它定义了评估表达式和获取表达式字符串表示形式的方法。通过该接口，我们可以使用 SpEL 强大的语法对对象进行动态查询和操作，实现了在 Spring 应用程序中灵活地处理配置、条件和数据转换等需求。该接口的实现类通常由 `SpelExpressionParser` 解析表达式字符串而创建，为 Spring 框架提供了一种便捷的方式来实现动态性和可扩展性。

### 四、主要功能

1. **表达式求值（Evaluation）**

   + 该接口允许我们对表达式进行求值，从而获取表达式的结果。通过调用 `getValue()` 方法，可以在指定的评估上下文（Evaluation Context）中对表达式进行求值，并返回结果。这使得我们可以动态地在运行时获取表达式的结果，从而实现各种灵活的逻辑和业务需求。

2. **类型转换（Type Conversion）**

   + `Expression` 接口允许我们指定期望的结果类型，并在求值过程中进行类型转换。通过调用带有 `expectedType` 参数的 `getValue()` 方法，可以将表达式的结果转换为指定的目标类型。这对于确保表达式求值结果的类型安全性非常重要，并且能够避免类型转换错误。

3. **获取表达式字符串（Get Expression String）**

   + `Expression` 接口提供了 `getExpressionString()` 方法，用于获取表达式的字符串表示形式。这对于调试和日志记录非常有用，可以方便地查看表达式的实际内容，以便进行问题排查和分析。

### 五、接口源码

`Expression` 接口是 Spring Framework 中表达式语言（SpEL）模块的核心组件之一，它封装了先前解析的表达式字符串，并提供了获取原始表达式字符串和在默认标准上下文中对表达式进行评估的方法。

```java
/**
 * 一个能够针对上下文对象自我评估的表达式。
 * 封装了先前解析的表达式字符串的细节。
 * 提供了对表达式评估的通用抽象。
 *
 * @author Keith Donald
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface Expression {

	/**
	 * 返回用于创建此表达式的原始字符串（未修改）。
	 * @return 原始表达式字符串
	 */
	String getExpressionString();

	/**
	 * 在默认标准上下文中评估此表达式。
	 * @return 评估结果
	 * @throws EvaluationException 如果在评估过程中出现问题
	 */
	@Nullable
	Object getValue() throws EvaluationException;

	// ... [代码部分省略以简化]
}
```

### 六、主要实现

1. **CompositeStringExpression**
   + 用于表示复合字符串表达式，它由多个子表达式组成，每个子表达式都是一个字符串。
   
2. **LiteralExpression**
   + 用于表示字面值表达式，即直接返回给定的字面值，不进行任何计算或解析。
   
3. **SpelExpression**
+ SpEL 的默认实现之一，用于对 SpEL 表达式进行求值。

### 七、最佳实践

使用 Spring Expression Language（SpEL）解析器来解析并求值一个基本的数学表达式。首先，创建了一个 SpEL 解析器实例，然后使用该解析器将字符串表达式 `"100 + 10"` 解析成一个 `Expression` 对象。接着，通过调用 `getValue(Integer.class)` 方法计算表达式的值，并将结果打印输出。

```java
public class ExpressionDemo {
    public static void main(String[] args) {
        // 创建解析器实例
        ExpressionParser parser = new SpelExpressionParser();
        // 解析基本表达式
        Expression expression = parser.parseExpression("100 + 10");
        // 为表达式计算结果
        Integer result = expression.getValue(Integer.class);
        System.out.println("表达式 '100 + 10' 的结果为: " + result);
    }
}
```

运行结果，`Expression` 如何能够处理包括数学运算和变量替换在内的复杂表达式，并准确地计算出结果。

```properties
表达式 '100 + 10' 的结果为: 110
```

### 八、时序图

~~~mermaid
sequenceDiagram
    autonumber
    title: Expression时序图
    
ExpressionDemo->>SpelExpression: getValue(expectedResultType)
Note over ExpressionDemo,SpelExpression: 发起表达式求值请求
SpelExpression->>SpelNodeImpl: getTypedValue(expressionState)
Note over SpelExpression,SpelNodeImpl: 调用内部节点获取值
SpelNodeImpl->>OpPlus: getValueInternal(state)
Note over SpelNodeImpl,OpPlus: 处理加法操作
OpPlus->>SpelNodeImpl: 返回 TypedValue
Note over OpPlus,SpelNodeImpl: 返回加法结果
SpelNodeImpl->>SpelExpression: 返回 TypedValue
Note over SpelNodeImpl,SpelExpression: 将结果返回给 SpEL 表达式
SpelExpression->>ExpressionUtils: convertTypedValue(context,typedValue,targetType)
Note over SpelExpression,ExpressionUtils: 转换结果类型
ExpressionUtils->>SpelExpression: 返回 SpEL 表达式结果
Note over ExpressionUtils,SpelExpression: 返回转换后的结果
SpelExpression->>ExpressionDemo: 返回 SpEL 表达式结果
Note over SpelExpression,ExpressionDemo: 返回表达式求值结果
~~~



### 九、源码分析

在`org.springframework.expression.spel.standard.SpelExpression#getValue(expectedResultType)`方法中，首先检查是否已经编译了表达式（通过检查 `compiledAst` 是否为 null），如果已编译，则尝试在编译后的抽象语法树上进行表达式求值。如果编译后的求值过程中出现异常，并且编译模式为 `SpelCompilerMode.MIXED`，则会将表达式恢复为解释模式。如果编译模式为 `SpelCompilerMode.IMMEDIATE`，则将异常抛出给调用者。如果尚未编译表达式，则在解释模式下对表达式进行求值。

```java
@Override
@Nullable
public <T> T getValue(@Nullable Class<T> expectedResultType) throws EvaluationException {
    CompiledExpression compiledAst = this.compiledAst;
    if (compiledAst != null) {
        try {
            // 获取评估上下文
            EvaluationContext context = getEvaluationContext();
            // 在编译后的抽象语法树上评估表达式
            Object result = compiledAst.getValue(context.getRootObject().getValue(), context);
            // 如果期望的结果类型为 null，则直接返回结果
            if (expectedResultType == null) {
                return (T) result;
            } else {
                // 否则将结果转换为期望的结果类型
                return ExpressionUtils.convertTypedValue(getEvaluationContext(), new TypedValue(result), expectedResultType);
            }
        } catch (Throwable ex) {
            // 如果运行在混合模式下，则回退到解释模式
            if (this.configuration.getCompilerMode() == SpelCompilerMode.MIXED) {
                // 恢复为解释模式
                this.compiledAst = null;
                // 重置解释计数器
                this.interpretedCount.set(0);
            } else {
                // 如果运行在即时编译模式下，将异常传播给调用者
                throw new SpelEvaluationException(ex, SpelMessage.EXCEPTION_RUNNING_COMPILED_EXPRESSION);
            }
        }
    }

    // 如果尚未编译表达式，则在解释模式下评估表达式
    ExpressionState expressionState = new ExpressionState(getEvaluationContext(), this.configuration);
    TypedValue typedResultValue = this.ast.getTypedValue(expressionState);
    // 检查编译状态
    checkCompile(expressionState);
    // 将结果转换为期望的结果类型并返回
    return ExpressionUtils.convertTypedValue(expressionState.getEvaluationContext(), typedResultValue, expectedResultType);
}
```

在`org.springframework.expression.spel.ast.SpelNodeImpl#getTypedValue`方法中，调用了 `getValueInternal()` 方法来获取表达式的值，并将其封装为 `TypedValue` 对象返回。这个方法用于在给定的表达式状态下获取表达式的值。

```java
@Override
public final TypedValue getTypedValue(ExpressionState expressionState) throws EvaluationException {
    return getValueInternal(expressionState);
}
```

在`org.springframework.expression.spel.ast.OpPlus#getValueInternal`方法中，首先，它检查表达式的左操作数，如果只有一个操作数，则执行一元加法操作；如果有两个操作数，则执行加法操作。在执行加法操作时，它会根据操作数的类型进行相应的类型转换和计算，并返回计算结果。

```java
/**
 * 执行表达式的内部求值逻辑，主要用于处理加法操作。
 * 
 * @param state 表达式的状态
 * @return 表达式的求值结果
 * @throws EvaluationException 如果在求值过程中出现问题
 */
@Override
public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
    // 获取左操作数
    SpelNodeImpl leftOp = getLeftOperand();

    // 如果只有一个操作数，则执行一元加法操作
    if (this.children.length < 2) {
        // 获取左操作数的值
        Object operandOne = leftOp.getValueInternal(state).getValue();
        // 如果左操作数是数字类型，则直接返回该数字
        if (operandOne instanceof Number) {
            // 根据数字类型设置返回值的类型描述符
            if (operandOne instanceof Double) {
                this.exitTypeDescriptor = "D";
            } else if (operandOne instanceof Float) {
                this.exitTypeDescriptor = "F";
            } else if (operandOne instanceof Long) {
                this.exitTypeDescriptor = "J";
            } else if (operandOne instanceof Integer) {
                this.exitTypeDescriptor = "I";
            }
            return new TypedValue(operandOne);
        }
        // 如果左操作数不是数字类型，则进行加法操作
        return state.operate(Operation.ADD, operandOne, null);
    }

    // 获取左操作数的值
    TypedValue operandOneValue = leftOp.getValueInternal(state);
    Object leftOperand = operandOneValue.getValue();
    // 获取右操作数的值
    TypedValue operandTwoValue = getRightOperand().getValueInternal(state);
    Object rightOperand = operandTwoValue.getValue();

    // 如果左右操作数都是数字类型，则执行加法操作
    if (leftOperand instanceof Number && rightOperand instanceof Number) {
        Number leftNumber = (Number) leftOperand;
        Number rightNumber = (Number) rightOperand;
        // 处理不同类型的数字相加的情况
        if (...) {
            // 处理 BigDecimal 相加
        } else if (...) {
            // 处理 Double 相加
        } else if (...) {
            // 处理 Float 相加
        } else if (...) {
            // 处理 BigInteger 相加
        } else if (...) {
            // 处理 Long 相加
        } else if (...) {
            // 处理 Integer 相加
        } else {
            // 处理其他未知类型的相加，最佳猜测为 double 相加
        }
    }

    // 如果左右操作数都是字符串类型，则执行字符串连接操作
    if (leftOperand instanceof String && rightOperand instanceof String) {
        this.exitTypeDescriptor = "Ljava/lang/String";
        return new TypedValue((String) leftOperand + rightOperand);
    }

    // 如果左操作数是字符串，则将右操作数转换为字符串并执行字符串连接操作
    if (leftOperand instanceof String) {
        return new TypedValue(
            leftOperand + (rightOperand == null ? "null" : convertTypedValueToString(operandTwoValue, state)));
    }

    // 如果右操作数是字符串，则将左操作数转换为字符串并执行字符串连接操作
    if (rightOperand instanceof String) {
        return new TypedValue(
            (leftOperand == null ? "null" : convertTypedValueToString(operandOneValue, state)) + rightOperand);
    }

    // 如果左右操作数不是数字类型或字符串类型，则执行加法操作
    return state.operate(Operation.ADD, leftOperand, rightOperand);
}
```

在`org.springframework.expression.common.ExpressionUtils#convertTypedValue`方法中，首先，它获取 `TypedValue` 中的值，并检查目标类型是否为 null，如果是则直接返回值。然后，如果给定了评估上下文 `context`，则使用上下文的类型转换器将值转换为目标类型。如果没有给定上下文，则尝试使用 `ClassUtils.isAssignableValue()` 方法检查值是否可以直接赋值给目标类型。

```java
@Nullable
public static <T> T convertTypedValue(
        @Nullable EvaluationContext context, TypedValue typedValue, @Nullable Class<T> targetType) {

    // 获取 TypedValue 中的值
    Object value = typedValue.getValue();
    // 如果目标类型为 null，则直接返回值
    if (targetType == null) {
        return (T) value;
    }
    // 如果给定了评估上下文，则使用上下文的类型转换器进行转换
    if (context != null) {
        return (T) context.getTypeConverter().convertValue(
                value, typedValue.getTypeDescriptor(), TypeDescriptor.valueOf(targetType));
    }
    // 如果没有给定评估上下文，则尝试直接将值转换为目标类型
    if (ClassUtils.isAssignableValue(targetType, value)) {
        return (T) value;
    }
    // 如果无法进行转换，则抛出异常
    throw new EvaluationException("Cannot convert value '" + value + "' to type '" + targetType.getName() + "'");
}
```

### 十、与其他组件的关系

1. **SpelExpressionParser**

   + `SpelExpressionParser` 类是用于解析表达式字符串并创建 `Expression` 对象的工厂类。它与 `Expression` 接口的关系是，通过调用 `parseExpression()` 方法创建 `Expression` 实例，从而实现对表达式的解析和处理。

2. **EvaluationContext**

   + `EvaluationContext` 接口定义了表达式的评估上下文，包括根对象、变量和函数注册等。`Expression` 接口通常需要一个 `EvaluationContext` 对象来执行表达式的求值操作。

3. **TypedValue**

   + `TypedValue` 类用于表示表达式的求值结果，包括值本身和值的类型描述符。在 `Expression` 接口的实现中，常常需要创建 `TypedValue` 对象来表示表达式的结果。

4. **ExpressionParser**

   + `ExpressionParser` 接口是用于解析表达式字符串并创建 `Expression` 对象的顶级接口，它定义了解析器的基本功能。`SpelExpressionParser` 类实现了 `ExpressionParser` 接口，因此与 `Expression` 接口的关系是，它是用于创建 `Expression` 实例的具体实现之一。

5. **EvaluationException**

   + `EvaluationException` 类是用于表示表达式求值过程中的异常情况，如语法错误、类型转换错误等。`Expression` 接口的实现通常会抛出 `EvaluationException` 异常来指示求值过程中出现的问题。

### 十一、常见问题

1. **表达式语法错误**

   + 在编写表达式时可能会出现语法错误，例如括号未匹配、操作符使用错误等。

2. **类型转换错误**

   + 当表达式中的操作数类型与预期的结果类型不匹配时，可能会导致类型转换错误，例如将字符串转换为数字或将对象转换为不兼容的类型。

3. **空指针异常**

   + 如果在评估表达式时上下文对象为 null，或者表达式中涉及到的对象属性为 null，可能会导致空指针异常。

4. **表达式求值效率低下**

   + 某些复杂的表达式可能会导致求值效率较低，特别是在大数据量或高并发情况下。

5. **错误的结果**

   + 由于表达式求值逻辑错误或表达式解析错误，可能会导致错误的结果返回。

6. **不支持的操作**

   + 某些表达式可能包含不受支持的操作或函数调用，这可能会导致求值过程中抛出异常。