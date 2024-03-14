## ExpressionParser


- [ExpressionParser](#expressionparser)
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

✒ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. **XML 和/或注解配置**

   + 熟悉使用 XML 或注解来配置 Spring 应用程序，因为 SpEL 常用于这些配置文件中。

2. **理解 SpEL 语法**

   + SpEL 有自己的语法规则。了解这些规则对于编写有效的 SpEL 表达式至关重要。

3. **了解 AOP 和事件处理**

   + 虽然不是必需的，但了解 Spring 中的 AOP 和事件处理机制对于理解 SpEL 在复杂场景下的应用是有益的。

### 三、基本描述

`ExpressionParser` 接口是 Spring 框架中的一个关键组件，它专门用于解析和执行 Spring 表达式语言（SpEL）的表达式。SpEL 是一种功能丰富的表达式语言，允许在运行时动态地操作和查询对象图。通过 `ExpressionParser` 接口，我们可以将字符串形式的表达式转换为 `Expression` 对象，并对这些对象执行各种操作，例如获取值、设置值或执行复杂的表达式。

### 四、主要功能

1. **解析表达式**

   + 它可以解析基于文本的表达式字符串，将其转换为 `Expression` 对象。这是它的核心功能，允许将动态表达式嵌入到应用程序中。

2. **表达式求值**

   + 通过解析得到的 `Expression` 对象，可以对表达式进行求值，以获取其运行时值。这可能涉及访问对象属性、调用方法、使用逻辑运算符等。

3. **设置表达式值**

   + 除了获取表达式的值，`ExpressionParser` 还可以用来修改目标对象的属性。通过表达式，可以直接设置对象的某个属性值。

4. **类型转换**

   + 它还支持类型转换功能，能夠在表达式求值过程中自动将值从一种类型转换为另一种类型。

5. **函数和运算符处理**

   + `ExpressionParser` 支持使用各种函数和运算符，例如数学运算、字符串操作、逻辑运算等。

6. **集合操作**

   + 支持对集合和数组的操作，包括选择（selection）、投影（projection）和聚合（aggregation）等。

7. **条件表达式**

   + 能够处理条件表达式，例如 if-then-else 结构，提供了增强的决策能力。

8. **模板解析**

   + 可以处理带有模板文本的表达式，例如拼接字符串与动态表达式的结合。

### 五、接口源码

`ExpressionParser` 接口主要负责将表达式字符串转换为可评估的 `Expression` 对象。它支持解析标准表达式和模板，提供了两种方法来解析表达式：一种是基本解析，另一种允许通过上下文进行更灵活的解析。

```java
/**
 * 解析表达式字符串，编译成可评估的表达式。
 * 支持解析标准表达式字符串以及模板。
 *
 * @author Keith Donald
 * @author Andy Clement
 * @since 3.0
 */
public interface ExpressionParser {

	/**
	 * 解析表达式字符串并返回一个可用于重复评估的 Expression 对象。
	 * <p>一些例子:
	 * <pre class="code">
	 *     3 + 4
	 *     name.firstName
	 * </pre>
	 * @param expressionString 需要解析的原始表达式字符串
	 * @return 已解析表达式的评估器
	 * @throws ParseException 解析过程中发生的异常
	 */
	Expression parseExpression(String expressionString) throws ParseException;

	/**
	 * 解析表达式字符串并返回一个可用于重复评估的 Expression 对象。
	 * <p>一些例子:
	 * <pre class="code">
	 *     3 + 4
	 *     name.firstName
	 * </pre>
	 * @param expressionString 需要解析的原始表达式字符串
	 * @param context 影响此表达式解析过程的上下文（可选）
	 * @return 已解析表达式的评估器
	 * @throws ParseException 解析过程中发生的异常
	 */
	Expression parseExpression(String expressionString, ParserContext context) throws ParseException;

}
```

### 六、主要实现

1. **SpelExpressionParser**

   + 这是最常用的实现，提供了对 Spring 表达式语言（SpEL）的完整支持。它能够处理各种复杂的表达式，如方法调用、属性访问、关系运算符等。

### 七、最佳实践

首先创建了一个 SpEL 表达式解析器实例 `SpelExpressionParser`，然后使用该解析器解析了一个简单的数学表达式 `"100 * 2 + 10"`，最后将`Expression` 打印输出。

```java
public class ExpressionParserDemo {

    public static void main(String[] args) {
        // 创建解析器实例
        ExpressionParser parser = new SpelExpressionParser();
        // 解析基本表达式
        Expression expression = parser.parseExpression("100 * 2 + 10");

        System.out.println("expression = " + expression);
    }
}
```

运行结果，`SpelExpressionParser` 解析给定表达式后返回的 `SpelExpression` 对象。这个对象包含了对表达式的内部表示，可以用于后续的表达式计算。

```java
expression = org.springframework.expression.spel.standard.SpelExpression@754ba872
```

### 八、时序图

~~~mermaid
sequenceDiagram
    autonumber
    title: ExpressionParser时序图

    ExpressionParserDemo->>TemplateAwareExpressionParser: parseExpression(expressionString)
    TemplateAwareExpressionParser->>TemplateAwareExpressionParser: parseExpression(expressionString, context)
    TemplateAwareExpressionParser->>SpelExpressionParser: doParseExpression(expressionString, context)
    SpelExpressionParser->>InternalSpelExpressionParser: new InternalSpelExpressionParser(this.configuration)
    InternalSpelExpressionParser->>SpelExpressionParser: 返回parser
    SpelExpressionParser->>InternalSpelExpressionParser: doParseExpression(expressionString, context)
    InternalSpelExpressionParser->>Tokenizer: new Tokenizer(expressionString)
    Note over InternalSpelExpressionParser,Tokenizer: 字符串流分析为记号流
    Tokenizer->>InternalSpelExpressionParser: 返回tokenizer
    InternalSpelExpressionParser->>Tokenizer: process()
    Note over InternalSpelExpressionParser,Tokenizer: 词法分析
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatExpression()
    Note over InternalSpelExpressionParser: 生成抽象语法树
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatLogicalOrExpression
    Note over InternalSpelExpressionParser: 解析逻辑或表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatLogicalAndExpression
    Note over InternalSpelExpressionParser: 解析逻辑与表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatRelationalExpression
    Note over InternalSpelExpressionParser: 解析关系表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatSumExpression
    Note over InternalSpelExpressionParser: 解析加减表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatProductExpression
    Note over InternalSpelExpressionParser: 解析乘除表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatPowerIncDecExpression
    Note over InternalSpelExpressionParser: 解析幂运算和自增自减表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatUnaryExpression
    Note over InternalSpelExpressionParser: 解析一元表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatPrimaryExpression
    Note over InternalSpelExpressionParser: 解析主表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: eatStartNode
    Note over InternalSpelExpressionParser: 解析起始节点
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatLiteral
    Note over InternalSpelExpressionParser: 尝试解析字面量
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatParenExpression
    Note over InternalSpelExpressionParser: 尝试解析括号表达式
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatTypeReference
    Note over InternalSpelExpressionParser: 尝试解析类型引用
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatNullReference
    Note over InternalSpelExpressionParser: 尝试解析null引用
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatConstructorReference
    Note over InternalSpelExpressionParser: 尝试解析构造函数引用
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatBeanReference
    Note over InternalSpelExpressionParser: 尝试解析Bean引用
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatProjection
    Note over InternalSpelExpressionParser: 尝试解析投影
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatSelection
    Note over InternalSpelExpressionParser: 尝试解析选择
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatIndexer
    Note over InternalSpelExpressionParser: 尝试解析索引器
    InternalSpelExpressionParser->>InternalSpelExpressionParser: maybeEatInlineListOrMap
    Note over InternalSpelExpressionParser: 尝试解析内联列表或映射
    InternalSpelExpressionParser->>SpelExpressionParser: 返回表达式对象
    SpelExpressionParser->>TemplateAwareExpressionParser: 返回表达式对象
    TemplateAwareExpressionParser->>ExpressionParserDemo: 返回表达式对象

~~~



### 九、源码分析

在`org.springframework.expression.common.TemplateAwareExpressionParser#parseExpression(expressionString)`方法中，接受一个字符串类型的表达式作为输入，并调用了重载的 `parseExpression` 方法来执行实际的解析操作。

```java
@Override
public Expression parseExpression(String expressionString) throws ParseException {
    return parseExpression(expressionString, null);
}
```

在`org.springframework.expression.common.TemplateAwareExpressionParser#parseExpression(expressionString, context)`方法中，首先检查传入的 `ParserContext` 对象是否为模板模式，如果是，则调用 `parseTemplate` 方法来解析模板表达式；否则，调用 `doParseExpression` 方法来解析普通的表达式。

```java
@Override
public Expression parseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
    if (context != null && context.isTemplate()) {
       return parseTemplate(expressionString, context);
    }
    else {
       return doParseExpression(expressionString, context);
    }
}
```

在`org.springframework.expression.spel.standard.SpelExpressionParser#doParseExpression`方法中，重写了 `doParseExpression` 方法。在这个方法中，它创建了一个 `InternalSpelExpressionParser` 对象，并调用了其 `doParseExpression` 方法来执行实际的 SpEL 表达式解析操作，然后返回解析得到的 `SpelExpression` 对象。

```java
@Override
protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
    return new InternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#doParseExpression`方法中，首先对给定的表达式字符串进行分词处理，并通过构建抽象语法树（AST）来解析表达式。在解析过程中，它确保 AST 不为空，并且所有的令牌都已经处理。最后，基于解析得到的 AST 创建一个新的 `SpelExpression` 对象，并将其返回。

```java
@Override
protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context)
        throws ParseException {

    try {
        // 设置当前表达式字符串
        this.expressionString = expressionString;

        // 对表达式字符串进行分词处理
        Tokenizer tokenizer = new Tokenizer(expressionString);
        this.tokenStream = tokenizer.process();
        this.tokenStreamLength = this.tokenStream.size();
        this.tokenStreamPointer = 0;

        // 清空已构建节点的集合
        this.constructedNodes.clear();

        // 构建抽象语法树（AST）
        SpelNodeImpl ast = eatExpression();

        // 确保 AST 不为空
        Assert.state(ast != null, "No node");

        // 检查是否还有未处理的令牌，如果有则抛出异常
        Token t = peekToken();
        if (t != null) {
            throw new SpelParseException(t.startPos, SpelMessage.MORE_INPUT, toString(nextToken()));
        }

        // 确保已构建节点的集合为空
        Assert.isTrue(this.constructedNodes.isEmpty(), "At least one node expected");

        // 创建并返回 SpEL 表达式对象
        return new SpelExpression(expressionString, ast, this.configuration);
    } catch (InternalParseException ex) {
        // 抛出内部解析异常的原因
        throw ex.getCause();
    }
}
```

在`org.springframework.expression.spel.standard.Tokenizer#Tokenizer`方法中，接受一个字符串类型的输入数据，并将其初始化为 `Tokenizer` 类的实例。在构造函数中，输入数据被设置为类的成员变量 `expressionString`，并且将输入数据加上一个空字符以确保在处理过程中能够正确识别输入的末尾。然后，输入数据被转换为字符数组并赋值给 `charsToProcess`，同时确定了数组的长度，并将初始位置指针 `pos` 设置为0，表示在输入数据的开头位置。

```java
public Tokenizer(String inputData) {
    this.expressionString = inputData;
    this.charsToProcess = (inputData + "\0").toCharArray();
    this.max = this.charsToProcess.length;
    this.pos = 0;
}
```

在`org.springframework.expression.spel.standard.Tokenizer#process`方法中，处理输入的字符串并生成对应的令牌列表。在处理过程中，它遍历输入字符串的每个字符，根据字符的类型执行相应的操作：对字母进行标识符解析，处理运算符和分隔符生成对应的令牌，解析数字字面量，忽略空白字符，处理引号括起的字符串字面量，以及处理到达字符串末尾的情况。最终，生成的令牌列表作为处理结果返回。

```java
public List<Token> process() {
    // 循环处理输入字符，直到到达输入字符串的末尾
    while (this.pos < this.max) {
        // 获取当前位置的字符
        char ch = this.charsToProcess[this.pos];
        // 如果是字母，则解析标识符
        if (isAlphabetic(ch)) {
            lexIdentifier();
        } else {
            // 处理运算符和分隔符
            switch (ch) {
                case '+':
                    // 处理 "+" 号
                    if (isTwoCharToken(TokenKind.INC)) {
                        pushPairToken(TokenKind.INC);
                    } else {
                        pushCharToken(TokenKind.PLUS);
                    }
                    break;
                // 省略其他情况的处理...
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    // 解析数值字面量
                    lexNumericLiteral(ch == '0');
                    break;
                // 处理空格、制表符、回车和换行符
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    // 忽略这些字符
                    this.pos++;
                    break;
                case '\'':
                    // 解析单引号括起的字符串字面量
                    lexQuotedStringLiteral();
                    break;
                case '"':
                    // 解析双引号括起的字符串字面量
                    lexDoubleQuotedStringLiteral();
                    break;
                case 0:
                    // 到达输入字符串的末尾
                    this.pos++; // 移动指针到字符串末尾
                    break;
                case '\\':
                    // 抛出异常，不支持的转义字符
                    raiseParseException(this.pos, SpelMessage.UNEXPECTED_ESCAPE_CHAR);
                    break;
                default:
                    // 抛出异常，无法处理的字符
                    throw new IllegalStateException("Cannot handle (" + (int) ch + ") '" + ch + "'");
            }
        }
    }
    // 返回处理后的令牌列表
    return this.tokens;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatExpression`方法中，首先调用 `eatLogicalOrExpression` 方法解析逻辑或表达式，然后检查下一个令牌的类型。如果下一个令牌是赋值操作符 `=`，则构造一个赋值节点 `Assign`；如果是 Elvis 操作符 `?:`，则构造一个 Elvis 节点；如果是条件运算符 `?`，则构造一个三元表达式节点 `Ternary`。

```java
@Nullable
private SpelNodeImpl eatExpression() {
    // 解析逻辑或表达式
    SpelNodeImpl expr = eatLogicalOrExpression();
    // 获取下一个令牌
    Token t = peekToken();
    if (t != null) {
        // 如果下一个令牌是赋值操作符 "="
        if (t.kind == TokenKind.ASSIGN) {  // a=b
            // 如果逻辑或表达式为空，则构造一个空字面量节点
            if (expr == null) {
                expr = new NullLiteral(t.startPos - 1, t.endPos - 1);
            }
            // 消耗赋值操作符
            nextToken();
            // 解析赋值操作符右侧的逻辑或表达式
            SpelNodeImpl assignedValue = eatLogicalOrExpression();
            // 构造赋值节点
            return new Assign(t.startPos, t.endPos, expr, assignedValue);
        }
        // 如果下一个令牌是 Elvis 操作符 "?:"
        if (t.kind == TokenKind.ELVIS) {  // a?:b (a if it isn't null, otherwise b)
            // 如果逻辑或表达式为空，则构造一个空字面量节点
            if (expr == null) {
                expr = new NullLiteral(t.startPos - 1, t.endPos - 2);
            }
            // 消耗 Elvis 操作符
            nextToken();  // elvis has left the building
            // 解析 Elvis 操作符右侧的表达式
            SpelNodeImpl valueIfNull = eatExpression();
            // 如果右侧表达式为空，则构造一个空字面量节点
            if (valueIfNull == null) {
                valueIfNull = new NullLiteral(t.startPos + 1, t.endPos + 1);
            }
            // 构造 Elvis 节点
            return new Elvis(t.startPos, t.endPos, expr, valueIfNull);
        }
        // 如果下一个令牌是条件运算符 "?"
        if (t.kind == TokenKind.QMARK) {  // a?b:c
            // 如果逻辑或表达式为空，则构造一个空字面量节点
            if (expr == null) {
                expr = new NullLiteral(t.startPos - 1, t.endPos - 1);
            }
            // 消耗条件运算符
            nextToken();
            // 解析条件运算符右侧的表达式
            SpelNodeImpl ifTrueExprValue = eatExpression();
            // 消耗冒号分隔符
            eatToken(TokenKind.COLON);
            // 解析条件运算符的第三个操作数
            SpelNodeImpl ifFalseExprValue = eatExpression();
            // 构造三元表达式节点
            return new Ternary(t.startPos, t.endPos, expr, ifTrueExprValue, ifFalseExprValue);
        }
    }
    // 返回解析得到的表达式节点
    return expr;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatLogicalOrExpression`方法中，首先调用 `eatLogicalAndExpression` 方法解析逻辑与表达式，并在循环中检查下一个令牌是否为 "or" 关键字或者符号 "||"。如果是，则消耗该令牌，然后解析逻辑与表达式的右操作数，并构造一个逻辑或操作节点。

```java
@Nullable
private SpelNodeImpl eatLogicalOrExpression() {
    // 解析逻辑与表达式
    SpelNodeImpl expr = eatLogicalAndExpression();
    // 循环解析逻辑或操作
    while (peekIdentifierToken("or") || peekToken(TokenKind.SYMBOLIC_OR)) {
        // 获取当前操作符令牌并消耗
        Token t = takeToken();  //consume OR
        // 解析逻辑与表达式的右操作数
        SpelNodeImpl rhExpr = eatLogicalAndExpression();
        // 检查操作数有效性
        checkOperands(t, expr, rhExpr);
        // 构造逻辑或操作节点
        expr = new OpOr(t.startPos, t.endPos, expr, rhExpr);
    }
    // 返回解析得到的逻辑或表达式节点
    return expr;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatLogicalAndExpression`方法中，首先调用 `eatRelationalExpression` 方法解析关系表达式，并在循环中检查下一个令牌是否为 "and" 关键字或者符号 "&&"。如果是，则消耗该令牌，然后解析关系表达式的右操作数，并构造一个逻辑与操作节点。

```java
@Nullable
private SpelNodeImpl eatLogicalAndExpression() {
    // 解析关系表达式
    SpelNodeImpl expr = eatRelationalExpression();
    // 循环解析逻辑与操作
    while (peekIdentifierToken("and") || peekToken(TokenKind.SYMBOLIC_AND)) {
        // 获取当前操作符令牌并消耗
        Token t = takeToken();  // consume 'AND'
        // 解析关系表达式的右操作数
        SpelNodeImpl rhExpr = eatRelationalExpression();
        // 检查操作数有效性
        checkOperands(t, expr, rhExpr);
        // 构造逻辑与操作节点
        expr = new OpAnd(t.startPos, t.endPos, expr, rhExpr);
    }
    // 返回解析得到的逻辑与表达式节点
    return expr;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatRelationalExpression`方法中，首先调用 `eatSumExpression` 方法解析加法表达式，然后尝试消耗可能存在的关系运算符。如果存在关系运算符，则解析右操作数，并根据不同的运算符类型构造相应的节点，包括大于、小于、大于等于、小于等于、等于、不等于、instanceof、matches 和 between 运算符。

```java
@Nullable
private SpelNodeImpl eatRelationalExpression() {
    // 解析加法表达式
    SpelNodeImpl expr = eatSumExpression();
    // 尝试消耗可能存在的关系运算符
    Token relationalOperatorToken = maybeEatRelationalOperator();
    if (relationalOperatorToken != null) {
       // 消耗关系运算符
       Token t = takeToken();  // 消耗关系运算符令牌
       // 解析右操作数
       SpelNodeImpl rhExpr = eatSumExpression();
       // 检查操作数有效性
       checkOperands(t, expr, rhExpr);
       TokenKind tk = relationalOperatorToken.kind;

       // 根据不同的运算符类型构造相应的节点
       if (relationalOperatorToken.isNumericRelationalOperator()) {
          if (tk == TokenKind.GT) {
             return new OpGT(t.startPos, t.endPos, expr, rhExpr);
          }
          if (tk == TokenKind.LT) {
             return new OpLT(t.startPos, t.endPos, expr, rhExpr);
          }
          if (tk == TokenKind.LE) {
             return new OpLE(t.startPos, t.endPos, expr, rhExpr);
          }
          if (tk == TokenKind.GE) {
             return new OpGE(t.startPos, t.endPos, expr, rhExpr);
          }
          if (tk == TokenKind.EQ) {
             return new OpEQ(t.startPos, t.endPos, expr, rhExpr);
          }
          Assert.isTrue(tk == TokenKind.NE, "Not-equals token expected");
          return new OpNE(t.startPos, t.endPos, expr, rhExpr);
       }

       // 处理instanceof运算符
       if (tk == TokenKind.INSTANCEOF) {
          return new OperatorInstanceof(t.startPos, t.endPos, expr, rhExpr);
       }

       // 处理matches运算符
       if (tk == TokenKind.MATCHES) {
          return new OperatorMatches(t.startPos, t.endPos, expr, rhExpr);
       }

       // 处理between运算符
       Assert.isTrue(tk == TokenKind.BETWEEN, "Between token expected");
       return new OperatorBetween(t.startPos, t.endPos, expr, rhExpr);
    }
    // 返回解析得到的关系表达式节点
    return expr;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatSumExpression`方法中，首先调用 `eatProductExpression` 方法解析乘法表达式，并在循环中检查下一个令牌是否为加法、减法或自增运算符。如果是，则消耗该令牌，然后解析乘法表达式的右操作数，并根据不同的运算符类型构造相应的节点（加法或减法节点）。

```java
@Nullable
private SpelNodeImpl eatSumExpression() {
    // 解析乘法表达式
    SpelNodeImpl expr = eatProductExpression();
    // 循环解析加法表达式
    while (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.INC)) {
       // 获取当前操作符令牌并消耗
       Token t = takeToken();  // 消耗 PLUS、MINUS 或 INC 令牌
       // 解析乘法表达式的右操作数
       SpelNodeImpl rhExpr = eatProductExpression();
       // 检查右操作数的有效性
       checkRightOperand(t, rhExpr);
       // 根据不同的运算符类型构造相应的节点
       if (t.kind == TokenKind.PLUS) {
          // 构造加法节点
          expr = new OpPlus(t.startPos, t.endPos, expr, rhExpr);
       }
       else if (t.kind == TokenKind.MINUS) {
          // 构造减法节点
          expr = new OpMinus(t.startPos, t.endPos, expr, rhExpr);
       }
    }
    // 返回解析得到的加法表达式节点
    return expr;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatProductExpression`方法中，首先调用 `eatPowerIncDecExpression` 方法解析幂运算或自增自减表达式，并在循环中检查下一个令牌是否为乘法、除法或取模运算符。如果是，则消耗该令牌，然后解析幂运算或自增自减表达式的右操作数，并根据不同的运算符类型构造相应的节点（乘法、除法或取模节点）。

```java
@Nullable
private SpelNodeImpl eatProductExpression() {
    // 解析幂运算或自增自减表达式
    SpelNodeImpl expr = eatPowerIncDecExpression();
    // 循环解析乘法表达式
    while (peekToken(TokenKind.STAR, TokenKind.DIV, TokenKind.MOD)) {
       // 获取当前操作符令牌并消耗
       Token t = takeToken();  // 消耗 STAR、DIV 或 MOD 令牌
       // 解析幂运算或自增自减表达式的右操作数
       SpelNodeImpl rhExpr = eatPowerIncDecExpression();
       // 检查操作数有效性
       checkOperands(t, expr, rhExpr);
       // 根据不同的运算符类型构造相应的节点
       if (t.kind == TokenKind.STAR) {
          // 构造乘法节点
          expr = new OpMultiply(t.startPos, t.endPos, expr, rhExpr);
       }
       else if (t.kind == TokenKind.DIV) {
          // 构造除法节点
          expr = new OpDivide(t.startPos, t.endPos, expr, rhExpr);
       }
       else {
          Assert.isTrue(t.kind == TokenKind.MOD, "Mod token expected");
          // 构造取模节点
          expr = new OpModulus(t.startPos, t.endPos, expr, rhExpr);
       }
    }
    // 返回解析得到的乘法表达式节点
    return expr;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatPowerIncDecExpression`方法中，首先调用 `eatUnaryExpression` 方法解析一元表达式，并在条件语句中检查是否存在幂运算符或自增自减运算符。如果存在幂运算符，则解析幂运算符右侧的一元表达式，并构造幂运算节点；如果存在自增自减运算符，则根据运算符类型构造相应的自增自减节点。

```java
@Nullable
private SpelNodeImpl eatPowerIncDecExpression() {
    // 解析一元表达式
    SpelNodeImpl expr = eatUnaryExpression();
    // 检查是否存在幂运算符
    if (peekToken(TokenKind.POWER)) {
       // 获取并消耗幂运算符令牌
       Token t = takeToken();  // 消耗 POWER
       // 解析幂运算符右侧的一元表达式
       SpelNodeImpl rhExpr = eatUnaryExpression();
       // 检查右操作数的有效性
       checkRightOperand(t, rhExpr);
       // 构造幂运算节点
       return new OperatorPower(t.startPos, t.endPos, expr, rhExpr);
    }
    // 如果不存在幂运算符，但存在自增自减运算符
    if (expr != null && peekToken(TokenKind.INC, TokenKind.DEC)) {
       // 获取并消耗自增自减运算符令牌
       Token t = takeToken();  // 消耗 INC/DEC
       // 根据运算符类型构造相应的自增自减节点
       if (t.getKind() == TokenKind.INC) {
          return new OpInc(t.startPos, t.endPos, true, expr);
       }
       return new OpDec(t.startPos, t.endPos, true, expr);
    }
    // 如果既不存在幂运算符也不存在自增自减运算符，则直接返回解析得到的一元表达式节点
    return expr;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatUnaryExpression`方法中，首先检查下一个令牌是否为正号、负号或逻辑非运算符。如果是，则消耗该令牌，并递归调用 `eatUnaryExpression` 方法解析表达式的主体部分，并根据不同的运算符类型构造相应的节点（正号、负号或逻辑非节点）。如果下一个令牌是自增或自减运算符，则也消耗该令牌，并递归调用 `eatUnaryExpression` 方法解析表达式的主体部分，并根据运算符类型构造相应的自增或自减节点。如果以上条件都不满足，则调用 `eatPrimaryExpression` 方法解析主表达式。

```java
@Nullable
private SpelNodeImpl eatUnaryExpression() {
    // 检查下一个令牌是否为正号、负号或逻辑非运算符
    if (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.NOT)) {
        // 获取并消耗运算符令牌
        Token t = takeToken();
        // 解析表达式的主体部分
        SpelNodeImpl expr = eatUnaryExpression();
        // 断言表达式不为空
        Assert.state(expr != null, "No node");
        // 根据运算符类型构造相应的节点
        if (t.kind == TokenKind.NOT) {
            // 构造逻辑非节点
            return new OperatorNot(t.startPos, t.endPos, expr);
        }
        if (t.kind == TokenKind.PLUS) {
            // 构造正号节点
            return new OpPlus(t.startPos, t.endPos, expr);
        }
        // 如果不是正号，则是负号
        Assert.isTrue(t.kind == TokenKind.MINUS, "Minus token expected");
        // 构造负号节点
        return new OpMinus(t.startPos, t.endPos, expr);
    }
    // 如果下一个令牌是自增或自减运算符
    if (peekToken(TokenKind.INC, TokenKind.DEC)) {
        // 获取并消耗运算符令牌
        Token t = takeToken();
        // 解析表达式的主体部分
        SpelNodeImpl expr = eatUnaryExpression();
        // 根据运算符类型构造相应的自增或自减节点
        if (t.getKind() == TokenKind.INC) {
            // 构造自增节点
            return new OpInc(t.startPos, t.endPos, false, expr);
        }
        // 否则为自减节点
        return new OpDec(t.startPos, t.endPos, false, expr);
    }
    // 解析主表达式
    return eatPrimaryExpression();
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatPrimaryExpression`方法中，首先调用 `eatStartNode` 方法解析起始节点，并初始化一个列表来存储后续的节点。然后进入循环，持续调用 `eatNode` 方法来解析后续节点，直到无法解析出新的节点为止。在循环中，如果节点列表为空，则将起始节点添加到列表中；否则，将解析得到的节点添加到列表中。最后，如果起始节点或节点列表为空，则直接返回起始节点；否则，构造一个复合表达式节点，其范围从起始节点的起始位置到最后一个节点的结束位置，包含所有解析得到的节点，并返回该复合表达式节点。

```java
@Nullable
private SpelNodeImpl eatPrimaryExpression() {
    // 解析起始节点，通常为一个节点
    SpelNodeImpl start = eatStartNode();  
    // 节点列表，用于存储后续的节点
    List<SpelNodeImpl> nodes = null;
    // 解析节点
    SpelNodeImpl node = eatNode();
    // 循环解析后续节点，直到无法解析出新的节点为止
    while (node != null) {
        // 如果节点列表为空，则将起始节点添加到列表中
        if (nodes == null) {
            nodes = new ArrayList<>(4);
            nodes.add(start);
        }
        // 将解析得到的节点添加到列表中
        nodes.add(node);
        // 继续解析下一个节点
        node = eatNode();
    }
    // 如果起始节点或节点列表为空，则直接返回起始节点
    if (start == null || nodes == null) {
        return start;
    }
    // 构造一个复合表达式节点，包含所有解析得到的节点
    return new CompoundExpression(start.getStartPosition(), nodes.get(nodes.size() - 1).getEndPosition(),
          nodes.toArray(new SpelNodeImpl[0]));
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#eatStartNode`方法中，首先尝试解析字面量、括号表达式、类型引用、空引用、构造器引用、方法或属性、函数或变量、Bean引用、PROJECT、选择、索引器、内联列表或映射等不同类型的节点。

```java
@Nullable
private SpelNodeImpl eatStartNode() {
    // 尝试解析字面量
    if (maybeEatLiteral()) {
       // 返回解析的字面量节点
       return pop();
    }
    // 尝试解析括号表达式
    else if (maybeEatParenExpression()) {
       // 返回解析的括号表达式节点
       return pop();
    }
    // 尝试解析类型引用、空引用、构造器引用、方法或属性、函数或变量等节点
    else if (maybeEatTypeReference() || maybeEatNullReference() || maybeEatConstructorReference() ||
          maybeEatMethodOrProperty(false) || maybeEatFunctionOrVar()) {
       // 返回解析得到的节点
       return pop();
    }
    // 尝试解析Bean引用
    else if (maybeEatBeanReference()) {
       // 返回解析的Bean引用节点
       return pop();
    }
    // 尝试解析投影、选择、索引器节点
    else if (maybeEatProjection(false) || maybeEatSelection(false) || maybeEatIndexer()) {
       // 返回解析得到的节点
       return pop();
    }
    // 尝试解析内联列表或映射节点
    else if (maybeEatInlineListOrMap()) {
       // 返回解析得到的节点
       return pop();
    }
    // 如果无法解析出任何节点，则返回空
    else {
       return null;
    }
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatLiteral`方法中，首先检查下一个令牌是否存在，如果不存在，则返回 false；然后根据令牌的类型进行相应的处理：如果是整型字面量，则将其转换为整数字面量节点并入栈；如果是长整型字面量，则将其转换为长整数字面量节点并入栈；如果是十六进制整型字面量，则将其转换为对应的整数或长整数字面量节点并入栈；如果是实数或浮点数字面量，则将其转换为实数或浮点数字面量节点并入栈；如果是布尔值字面量 true，则入栈一个布尔值节点表示 true；如果是布尔值字面量 false，则入栈一个布尔值节点表示 false；如果是字符串字面量，则入栈一个字符串字面量节点；如果以上条件都不满足，则返回 false。最后，消耗当前令牌，并返回 true，表示成功解析字面量。

```java
private boolean maybeEatLiteral() {
    // 获取下一个令牌
    Token t = peekToken();
    // 如果令牌为空，则返回解析失败
    if (t == null) {
        return false;
    }
    // 根据令牌类型进行相应处理
    if (t.kind == TokenKind.LITERAL_INT) {
        // 将整型字面量转换为整数字面量节点并入栈
        push(Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 10));
    }
    else if (t.kind == TokenKind.LITERAL_LONG) {
        // 将长整型字面量转换为长整数字面量节点并入栈
        push(Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 10));
    }
    else if (t.kind == TokenKind.LITERAL_HEXINT) {
        // 将十六进制整型字面量转换为整数字面量节点并入栈
        push(Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 16));
    }
    else if (t.kind == TokenKind.LITERAL_HEXLONG) {
        // 将十六进制长整型字面量转换为长整数字面量节点并入栈
        push(Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 16));
    }
    else if (t.kind == TokenKind.LITERAL_REAL) {
        // 将实数字面量转换为实数数字面量节点并入栈
        push(Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, false));
    }
    else if (t.kind == TokenKind.LITERAL_REAL_FLOAT) {
        // 将浮点数字面量转换为浮点数数字面量节点并入栈
        push(Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, true));
    }
    else if (peekIdentifierToken("true")) {
        // 入栈一个布尔值节点表示 true
        push(new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, true));
    }
    else if (peekIdentifierToken("false")) {
        // 入栈一个布尔值节点表示 false
        push(new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, false));
    }
    else if (t.kind == TokenKind.LITERAL_STRING) {
        // 入栈一个字符串字面量节点
        push(new StringLiteral(t.stringValue(), t.startPos, t.endPos, t.stringValue()));
    }
    else {
        // 如果令牌类型不是字面量，则返回解析失败
        return false;
    }
    // 消耗当前令牌，并返回解析成功
    nextToken();
    return true;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatParenExpression`方法中，首先检查下一个令牌是否为左括号，如果是，则消耗该令牌，并解析括号内的表达式；然后检查是否成功解析出表达式节点，如果成功，则消耗右括号并将表达式节点入栈，最后返回解析成功；如果下一个令牌不是左括号，则直接返回解析失败。

```java
private boolean maybeEatParenExpression() {
    // 如果下一个令牌是左括号
    if (peekToken(TokenKind.LPAREN)) {
        // 消耗左括号令牌
        nextToken();
        // 解析括号内的表达式
        SpelNodeImpl expr = eatExpression();
        // 断言确保表达式节点非空
        Assert.state(expr != null, "No node");
        // 消耗右括号令牌
        eatToken(TokenKind.RPAREN);
        // 将表达式节点入栈
        push(expr);
        // 返回解析成功
        return true;
    }
    else {
        // 如果下一个令牌不是左括号，则返回解析失败
        return false;
    }
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatTypeReference`方法中，首先检查下一个令牌是否为标识符，如果是，则进一步检查该标识符是否为 "T"，如果不是，则返回解析失败；如果标识符为 "T"，则继续检查下一个令牌是否为右方括号，如果是，则将 "T" 视为映射的键，创建一个属性或字段引用节点，并入栈，最后返回解析成功；如果下一个令牌不是右方括号，则进一步解析括号内的内容，包括可能的限定标识符和数组维度，并构造一个类型引用节点，入栈，并返回解析成功；如果下一个令牌不是标识符，则直接返回解析失败。

```java
private boolean maybeEatTypeReference() {
    // 如果下一个令牌是标识符
    if (peekToken(TokenKind.IDENTIFIER)) {
        // 获取下一个令牌
        Token typeName = peekToken();
        // 断言确保令牌不为空
        Assert.state(typeName != null, "Expected token");
        // 如果标识符不是 "T"，则返回解析失败
        if (!"T".equals(typeName.stringValue())) {
            return false;
        }
        // 获取下一个令牌，用于进一步判断是否是映射的键
        Token t = takeToken();
        // 如果下一个令牌是右方括号，则将 "T" 视为映射的键，创建属性或字段引用节点，并入栈，最后返回解析成功
        if (peekToken(TokenKind.RSQUARE)) {
            // 创建属性或字段引用节点，表示 "T" 作为映射的键
            push(new PropertyOrFieldReference(false, t.stringValue(), t.startPos, t.endPos));
            return true;
        }
        // 否则，继续解析括号内的内容
        // 消耗左括号令牌
        eatToken(TokenKind.LPAREN);
        // 解析可能的限定标识符
        SpelNodeImpl node = eatPossiblyQualifiedId();
        // 检查是否存在数组维度
        int dims = 0;
        while (peekToken(TokenKind.LSQUARE, true)) {
            // 消耗左方括号和右方括号令牌，统计数组维度
            eatToken(TokenKind.RSQUARE);
            dims++;
        }
        // 消耗右括号令牌
        eatToken(TokenKind.RPAREN);
        // 创建类型引用节点，并入栈，最后返回解析成功
        this.constructedNodes.push(new TypeReference(typeName.startPos, typeName.endPos, node, dims));
        return true;
    }
    // 如果下一个令牌不是标识符，则返回解析失败
    return false;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatNullReference`方法中，首先检查下一个令牌是否为标识符，如果是，则进一步检查该标识符是否为 "null"，如果不是，则返回解析失败；如果标识符为 "null"，则将其解析为一个空字面量节点，并将其入栈，最后返回解析成功；如果下一个令牌不是标识符，则直接返回解析失败。

```java
private boolean maybeEatNullReference() {
    // 如果下一个令牌是标识符
    if (peekToken(TokenKind.IDENTIFIER)) {
        // 获取下一个令牌
        Token nullToken = peekToken();
        // 断言确保令牌不为空
        Assert.state(nullToken != null, "Expected token");
        // 如果标识符不是 "null"，则返回解析失败
        if (!"null".equalsIgnoreCase(nullToken.stringValue())) {
            return false;
        }
        // 消耗 "null" 标识符令牌
        nextToken();
        // 创建空字面量节点，并入栈，最后返回解析成功
        this.constructedNodes.push(new NullLiteral(nullToken.startPos, nullToken.endPos));
        return true;
    }
    // 如果下一个令牌不是标识符，则返回解析失败
    return false;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatConstructorReference`方法中，首先检查下一个令牌是否为 "new" 关键字，如果是，则继续解析构造函数引用的可能形式。如果 "new" 后面紧跟着右方括号，则将 "new" 视为映射的键，创建一个属性或字段引用节点，并入栈，最后返回解析成功；如果 "new" 后面是其他令牌，则继续解析构造函数引用的形式，包括可能的限定构造函数名和构造函数参数列表。如果构造函数参数列表中包含数组初始化器，则解析数组维度和内联列表或映射，最后创建构造函数引用节点，并入栈，最后返回解析成功；如果构造函数参数列表中不包含数组初始化器，则继续解析构造函数参数列表，最后创建构造函数引用节点，并入栈，最后返回解析成功；如果下一个令牌不是 "new" 关键字，则直接返回解析失败。

```java
private boolean maybeEatConstructorReference() {
    // 如果下一个令牌是 "new" 关键字
    if (peekIdentifierToken("new")) {
        // 获取 "new" 令牌
        Token newToken = takeToken();
        // 如果 "new" 后面是右方括号，则将 "new" 视为映射的键
        if (peekToken(TokenKind.RSQUARE)) {
            // 创建一个属性或字段引用节点，并入栈，最后返回解析成功
            push(new PropertyOrFieldReference(false, newToken.stringValue(), newToken.startPos, newToken.endPos));
            return true;
        }
        // 继续解析构造函数引用的可能形式
        SpelNodeImpl possiblyQualifiedConstructorName = eatPossiblyQualifiedId();
        List<SpelNodeImpl> nodes = new ArrayList<>();
        nodes.add(possiblyQualifiedConstructorName);
        // 如果构造函数参数列表中包含数组初始化器
        if (peekToken(TokenKind.LSQUARE)) {
            // 解析数组维度和内联列表或映射，创建构造函数引用节点，并入栈，最后返回解析成功
            eatConstructorArgs(nodes);
            push(new ConstructorReference(newToken.startPos, newToken.endPos, nodes.toArray(new SpelNodeImpl[0])));
        } else {
            // 继续解析构造函数参数列表，创建构造函数引用节点，并入栈，最后返回解析成功
            eatConstructorArgs(nodes);
            // 创建构造函数引用节点，并入栈，最后返回解析成功
            push(new ConstructorReference(newToken.startPos, newToken.endPos, nodes.toArray(new SpelNodeImpl[0])));
        }
        return true;
    }
    // 如果下一个令牌不是 "new" 关键字，则直接返回解析失败
    return false;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatBeanReference`方法中，用于尝试解析Bean引用。如果下一个令牌是 `TokenKind.BEAN_REF` 或 `TokenKind.FACTORY_BEAN_REF`，则将其视为Bean引用。然后尝试获取Bean名称，可以是标识符或字面字符串。解析完成后，根据Bean引用类型创建相应的 `BeanReference` 实例，并将其入栈。最后返回解析结果。

```java
private boolean maybeEatBeanReference() {
    // 如果下一个令牌是 BEAN_REF 或 FACTORY_BEAN_REF
    if (peekToken(TokenKind.BEAN_REF) || peekToken(TokenKind.FACTORY_BEAN_REF)) {
        // 获取 BEAN_REF 或 FACTORY_BEAN_REF 令牌
        Token beanRefToken = takeToken();
        Token beanNameToken = null;
        String beanName = null;
        // 如果下一个令牌是标识符，则表示 Bean 名称
        if (peekToken(TokenKind.IDENTIFIER)) {
            // 获取标识符令牌作为 Bean 名称
            beanNameToken = eatToken(TokenKind.IDENTIFIER);
            beanName = beanNameToken.stringValue();
        }
        // 如果下一个令牌是字符串字面量，则表示 Bean 名称
        else if (peekToken(TokenKind.LITERAL_STRING)) {
            // 获取字符串字面量令牌作为 Bean 名称
            beanNameToken = eatToken(TokenKind.LITERAL_STRING);
            beanName = beanNameToken.stringValue();
            // 去除字符串两侧的引号，获取真实的 Bean 名称
            beanName = beanName.substring(1, beanName.length() - 1);
        }
        // 如果 Bean 名称不是标识符或字符串字面量，则抛出异常
        else {
            throw internalException(beanRefToken.startPos, SpelMessage.INVALID_BEAN_REFERENCE);
        }
        BeanReference beanReference;
        // 根据 Bean 引用类型创建相应的 BeanReference 实例
        if (beanRefToken.getKind() == TokenKind.FACTORY_BEAN_REF) {
            // 如果是 FACTORY_BEAN_REF，则在 Bean 名称前添加前缀字符串
            String beanNameString = String.valueOf(TokenKind.FACTORY_BEAN_REF.tokenChars) + beanName;
            beanReference = new BeanReference(beanRefToken.startPos, beanNameToken.endPos, beanNameString);
        } else {
            // 如果是 BEAN_REF，则直接使用 Bean 名称
            beanReference = new BeanReference(beanNameToken.startPos, beanNameToken.endPos, beanName);
        }
        // 将 BeanReference 实例入栈，表示解析成功
        this.constructedNodes.push(beanReference);
        return true;
    }
    // 如果下一个令牌不是 BEAN_REF 或 FACTORY_BEAN_REF，则直接返回解析失败
    return false;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatProjection`方法中，用于尝试解析投影操作符（Projection），该操作符表示对集合进行投影操作，返回一个新的集合，其中每个元素都是对原集合元素的转换。若当前令牌为 PROJECT，则尝试解析表达式并创建 Projection 节点，表示成功解析。

```java
private boolean maybeEatProjection(boolean nullSafeNavigation) {
    Token t = peekToken(); // 获取当前令牌
    // 检查当前令牌是否为投影操作符，如果不是则返回 false
    if (!peekToken(TokenKind.PROJECT, true)) {
        return false;
    }
    // 断言当前令牌非空
    Assert.state(t != null, "No token"); 
    // 解析投影操作符后的表达式
    SpelNodeImpl expr = eatExpression(); 
    // 断言表达式节点非空
    Assert.state(expr != null, "No node"); 
    // 解析右方括号
    eatToken(TokenKind.RSQUARE); 
    // 创建 Projection 节点并将其压入节点栈中
    this.constructedNodes.push(new Projection(nullSafeNavigation, t.startPos, t.endPos, expr));
    return true; // 成功解析投影操作符，返回 true
}

```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatSelection`方法中，解析选择操作符（"![...]"）是否存在，如果存在则解析选择操作符的表达式。然后根据选择操作符的类型（FIRST、LAST 或 ALL）创建相应的 Selection 节点，并将其压入节点栈中，最后返回 true 表示成功解析选择操作符，否则返回 false。

```java
private boolean maybeEatSelection(boolean nullSafeNavigation) {
    Token t = peekToken();
    // 如果当前标记不是选择操作符，则返回false
    if (!peekSelectToken()) {
        return false;
    }
    Assert.state(t != null, "No token");
    // 消耗选择操作符标记
    nextToken();
    // 解析选择表达式
    SpelNodeImpl expr = eatExpression();
    // 如果表达式为空，则抛出异常
    if (expr == null) {
        throw internalException(t.startPos, SpelMessage.MISSING_SELECTION_EXPRESSION);
    }
    // 消耗右方括号标记
    eatToken(TokenKind.RSQUARE);
    // 根据不同的选择操作符创建相应的选择节点并压入解析节点栈中
    if (t.kind == TokenKind.SELECT_FIRST) {
        this.constructedNodes.push(new Selection(nullSafeNavigation, Selection.FIRST, t.startPos, t.endPos, expr));
    }
    else if (t.kind == TokenKind.SELECT_LAST) {
        this.constructedNodes.push(new Selection(nullSafeNavigation, Selection.LAST, t.startPos, t.endPos, expr));
    }
    else {
        this.constructedNodes.push(new Selection(nullSafeNavigation, Selection.ALL, t.startPos, t.endPos, expr));
    }
    return true;
}
```

在`org.springframework.expression.spel.standard.InternalSpelExpressionParser#maybeEatIndexer`方法中，尝试解析索引器表达式。如果当前标记是左方括号，则解析相应的表达式作为索引器的索引，并将其压入解析节点栈中。

```java
private boolean maybeEatIndexer() {
    // 获取当前标记
    Token t = peekToken(); 
    // 如果当前标记不是 '['，则返回 false
    if (!peekToken(TokenKind.LSQUARE, true)) { 
        return false;
    }
    // 断言当前标记不为空
    Assert.state(t != null, "No token"); 
    // 解析索引表达式
    SpelNodeImpl expr = eatExpression();
    // 断言索引表达式不为空
    Assert.state(expr != null, "No node"); 
    // 解析 ']'
    eatToken(TokenKind.RSQUARE);
    // 创建索引器节点并推入堆栈
    this.constructedNodes.push(new Indexer(t.startPos, t.endPos, expr));
    return true;
}
```

尝试解析内联列表或映射字面量。根据当前标记的不同情况，它可以解析空列表、空映射或包含一个或多个表达式的列表或映射。通过逐一解析表达式并检查后续标记，该方法构建相应的内联列表或映射节点，并将其推送到解析节点栈中。

```java
private boolean maybeEatInlineListOrMap() {
    // 获取当前标记
    Token t = peekToken(); 
    // 如果当前标记不是 '{'，则返回 false
    if (!peekToken(TokenKind.LCURLY, true)) { 
       return false;
    }
    // 断言当前标记不为空
    Assert.state(t != null, "No token"); 
    // 初始化节点
    SpelNodeImpl expr = null; 
    // 获取下一个标记
    Token closingCurly = peekToken(); 
    // 如果下一个标记是 '}'，表示是一个空列表 '{}'
    if (peekToken(TokenKind.RCURLY, true)) { 
       // 断言下一个标记不为空
       Assert.state(closingCurly != null, "No token"); 
       // 创建一个空的内联列表
       expr = new InlineList(t.startPos, closingCurly.endPos);
    }
    // 如果下一个标记是 ':'，表示是一个空映射 '{:}'
    else if (peekToken(TokenKind.COLON, true)) { 
       // 获取 '}'
       closingCurly = eatToken(TokenKind.RCURLY);
       // 创建一个空的内联映射
       expr = new InlineMap(t.startPos, closingCurly.endPos);
    }
    // 如果不是空列表或空映射，继续解析表达式
    else { 
       // 解析第一个表达式
       SpelNodeImpl firstExpression = eatExpression();
       // 下一个标记可能是 '}'
       if (peekToken(TokenKind.RCURLY)) {  
          // 创建只有一个元素的列表
          List<SpelNodeImpl> elements = new ArrayList<>();
          elements.add(firstExpression);
          closingCurly = eatToken(TokenKind.RCURLY);
          expr = new InlineList(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
       }
       // 下一个标记可能是逗号 ','，表示是多项列表
       else if (peekToken(TokenKind.COMMA, true)) {  
          // 创建多项列表
          List<SpelNodeImpl> elements = new ArrayList<>();
          elements.add(firstExpression);
          do {
             elements.add(eatExpression());
          }
          while (peekToken(TokenKind.COMMA, true));
          closingCurly = eatToken(TokenKind.RCURLY);
          expr = new InlineList(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
       }
       // 下一个标记可能是冒号 ':'，表示是映射
       else if (peekToken(TokenKind.COLON, true)) { 
          // 创建映射
          List<SpelNodeImpl> elements = new ArrayList<>();
          elements.add(firstExpression);
          elements.add(eatExpression());
          while (peekToken(TokenKind.COMMA, true)) {
             elements.add(eatExpression());
             eatToken(TokenKind.COLON);
             elements.add(eatExpression());
          }
          closingCurly = eatToken(TokenKind.RCURLY);
          expr = new InlineMap(t.startPos, closingCurly.endPos, elements.toArray(new SpelNodeImpl[0]));
       }
       // 若以上情况均不满足，则抛出异常
       else {
          throw internalException(t.startPos, SpelMessage.OOD);
       }
    }
    // 将解析的节点推入堆栈
    this.constructedNodes.push(expr);
    return true;
}
```

### 十、与其他组件的关系

1. **Expression**
   + 解析表达式字符串时，`SpelExpressionParser` 返回实现了 `Expression` 接口的对象。这些对象代表解析后的表达式，可以用于获取表达式的值、设置值或执行表达式。
   
2. **EvaluationContext**

   + 在评估表达式时，可以使用实现 `EvaluationContext` 接口的对象，如 `StandardEvaluationContext`，来提供表达式运行时的上下文信息（例如变量定义）。这有助于增强表达式的动态性和灵活性。

3. **ParseException&EvaluationException**

   + 这些是处理表达式解析和评估时可能出现的异常的类。如果在解析或评估表达式时出现错误，将抛出这些异常。

4. **PropertyAccessor&MethodResolver**

   + 这些类和接口用于在评估表达式时解析对象属性和方法调用。它们允许 `SpelExpressionParser` 与 Java 对象的属性和方法交互，实现复杂的逻辑。

5. **TypeConverter**

   + 用于在表达式计算过程中进行类型转换，使得可以在不同类型之间自由转换值。

### 十一、常见问题

1. **表达式语法错误**

   + 编写 SpEL 表达式时，常见的错误包括拼写错误、错误的符号或操作符使用。这些错误通常会在解析表达式时抛出 `ParseException`。

2. **性能问题**

   + 频繁解析和评估复杂的 SpEL 表达式可能会影响应用性能。合理缓存解析后的表达式对象可以帮助缓解这一问题。

3. **上下文变量未找到**

   + 如果在表达式中使用了上下文（Context）中未定义的变量，将会抛出异常。确保所有在表达式中使用的变量都已在上下文中定义。

4. **类型转换问题**

   + 在表达式求值过程中，可能会出现类型不匹配或不能正确转换的情况，导致 `EvaluationException`。

5. **属性或方法访问问题**

   + 尝试访问不存在的属性或调用不存在的方法时，会抛出异常。这可能是由于拼写错误或对象类型不正确。