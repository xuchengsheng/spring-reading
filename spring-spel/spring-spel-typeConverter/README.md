## TypeConverter

- [TypeConverter](#TypeConverter)
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

1. **类型转换概念**

   + 了解什么是类型转换以及为什么在软件开发中会经常需要进行类型转换是非常重要的。了解基本数据类型、对象之间的转换、类型兼容性等概念将有助于理解`TypeConverter`的作用和用法。

2. **Spring表达式语言（SpEL）**

   + `TypeConverter`通常与Spring表达式语言（SpEL）一起使用，因此了解SpEL的基本语法和用法是很重要的。SpEL是一个强大的表达式语言，它可以用于在运行时评估和处理表达式。学习如何编写和解析SpEL表达式将有助于理解如何在SpEL中使用`TypeConverter`进行类型转换。

3. **Java反射机制**

   + `TypeConverter`通常需要使用Java的反射机制来动态地获取对象的类型信息并进行转换。了解Java反射机制如何工作以及如何使用`Class`、`Method`、`Field`等反射API将有助于理解`TypeConverter`的实现原理。

4. **Spring类型转换器的扩展**

   + 了解如何自定义和扩展Spring框架中的类型转换器也是很有用的。Spring框架提供了许多内置的类型转换器，同时也支持自定义类型转换器来满足特定需求。学习如何编写自定义类型转换器将有助于更好地理解`TypeConverter`的用法和实现原理。

### 三、基本描述

`TypeConverter`接口是Spring框架中用于在SpEL（Spring表达式语言）中进行类型转换的核心接口，它允许将不同类型的对象相互转换，例如将字符串转换为数字、将对象转换为字符串等，为Spring应用程序提供了统一且灵活的类型转换机制，以满足各种复杂场景下的类型转换需求。

### 四、主要功能

1. **类型转换**

   + 提供了将一个对象转换为另一个类型的能力，例如将字符串转换为数字、将对象转换为字符串等。

2. **类型兼容性检查**

   + 可以检查某个对象是否可以转换为指定类型，以避免类型转换错误导致的异常。

3. **支持自定义转换规则**

   + 可以根据实际需求自定义类型转换规则，扩展和定制类型转换器，以满足特定场景下的类型转换需求。

4. **与SpEL集成**

   + 作为SpEL的一部分，`TypeConverter`接口与Spring表达式语言（SpEL）集成，可以在SpEL表达式中直接使用，实现灵活的类型转换功能。

5. **提供默认实现**

   + Spring框架提供了默认的`TypeConverter`实现，同时也支持用户自定义的类型转换器，以适应不同的应用场景和需求。

### 五、接口源码

`TypeConverter`接口定义了一种类型转换器，用于在表达式评估过程中将不同类型的值相互转换。它提供了判断是否可以进行类型转换的方法以及执行类型转换的方法，并支持对类型化集合进行转换。

```java
/**
 * 类型转换器可以在表达式评估过程中将不同类型的值相互转换。这是表达式解析器的SPI；请参阅
 * {@link org.springframework.core.convert.ConversionService} 了解Spring转换功能的主要用户API。
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface TypeConverter {

	/**
	 * 如果类型转换器可以将指定类型转换为所需的目标类型，则返回{@code true}。
	 * @param sourceType 描述源类型的类型描述符
	 * @param targetType 描述所请求结果类型的类型描述符
	 * @return 如果可以执行该转换，则返回{@code true}
	 */
	boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * 将一个值从一种类型转换为另一种类型，例如从{@code boolean}到{@code String}。
	 * <p> {@link TypeDescriptor} 参数支持类型化集合：
	 * 例如，调用者可能更喜欢{@code List<Integer>}，而不是简单的{@code List}。
	 * @param value 要转换的值
	 * @param sourceType 提供有关源对象的额外信息的类型描述符
	 * @param targetType 提供有关所请求结果类型的额外信息的类型描述符
	 * @return 转换后的值
	 * @throws EvaluationException 如果转换失败或根本无法进行转换
	 */
	@Nullable
	Object convertValue(@Nullable Object value, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

}
```

`StandardTypeConverter`是`TypeConverter`接口的默认实现，它委托给核心的Spring `ConversionService`来执行类型转换。该实现提供了两个构造方法，一个使用默认的`ConversionService`，另一个接受用户指定的`ConversionService`。它实现了`canConvert`方法来判断是否可以进行类型转换，并实现了`convertValue`方法来执行实际的类型转换操作。

```java
/**
 * {@link TypeConverter}接口的默认实现，委托给核心的Spring {@link ConversionService}。
 * 
 * @author Juergen Hoeller
 * @author Andy Clement
 * @since 3.0
 * @see org.springframework.core.convert.ConversionService
 */
public class StandardTypeConverter implements TypeConverter {

	private final ConversionService conversionService;


	/**
	 * 创建一个使用默认ConversionService的StandardTypeConverter。
	 * @see DefaultConversionService#getSharedInstance()
	 */
	public StandardTypeConverter() {
		this.conversionService = DefaultConversionService.getSharedInstance();
	}

	/**
	 * 创建一个使用给定ConversionService的StandardTypeConverter。
	 * @param conversionService 要委托的ConversionService
	 */
	public StandardTypeConverter(ConversionService conversionService) {
		Assert.notNull(conversionService, "ConversionService must not be null");
		this.conversionService = conversionService;
	}


	@Override
	public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.conversionService.canConvert(sourceType, targetType);
	}

	@Override
	@Nullable
	public Object convertValue(@Nullable Object value, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
		try {
			return this.conversionService.convert(value, sourceType, targetType);
		}
		catch (ConversionException ex) {
			throw new SpelEvaluationException(ex, SpelMessage.TYPE_CONVERSION_ERROR,
					(sourceType != null ? sourceType.toString() : (value != null ? value.getClass().getName() : "null")),
					targetType.toString());
		}
	}

}
```

### 六、主要实现

1. **StandardTypeConverter**
   + `StandardTypeConverter`是`TypeConverter`接口的默认实现，它利用核心的Spring `ConversionService`来实现类型转换。

### 七、最佳实践

使用SpEL表达式以及`TypeConverter`进行类型转换。首先，通过SpEL表达式解析器创建解析器对象，并创建一个EvaluationContext作为表达式的运行环境。然后，定义一个需要转换的字符串值，通过解析表达式获取表达式对象，再通过`expression.getValue(context, Integer.class)`将字符串值转换为整数类型。最后，打印转换后的整数值。

```java
public class TypeConverterDemo {

    public static void main(String[] args) {
        // 创建SpEL表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();

        // 创建一个EvaluationContext
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 定义一个需要转换的值
        String stringValue = "'123'";

        // 解析表达式
        Expression expression = parser.parseExpression(stringValue);

        // 使用TypeConverter进行转换
        Integer intValue = expression.getValue(context, Integer.class);

        // 打印转换后的值
        System.out.println("Converted Integer value: " + intValue);
    }
}
```

运行结果，成功将字符串 `'123'` 转换为整数类型，并输出转换后的整数值 `123`。

```properties
Converted Integer value: 123
```

### 八、与其他组件的关系

1. **ConversionService接口**

   +  `TypeConverter`接口是`ConversionService`接口的一部分，它是ConversionService的SPI（Service Provider Interface）。`ConversionService`提供了一种通用的类型转换机制，它定义了一组用于在不同类型之间进行转换的方法。`TypeConverter`接口是`ConversionService`中用于执行具体类型转换的一部分，通过`ConversionService`接口，可以更方便地管理和使用`TypeConverter`。

2. **SpEL表达式语言（Spring Expression Language）**

   +  `TypeConverter`接口通常与SpEL表达式语言一起使用，以支持在表达式中进行类型转换的功能。在SpEL表达式中，可以直接调用`TypeConverter`接口的方法来进行类型转换，从而实现更复杂的表达式计算和处理。

3. **StandardEvaluationContext类**

   + `TypeConverter`接口通常与`StandardEvaluationContext`类一起使用，`StandardEvaluationContext`提供了表达式的运行环境，包括变量、函数、类型转换器等。在`StandardEvaluationContext`中可以配置`TypeConverter`，以便在SpEL表达式中使用。

### 九、常见问题

1. **类型转换失败**

   + 在进行类型转换时，可能会出现转换失败的情况，例如源对象的类型无法转换为目标类型，或者转换过程中发生异常。这可能会导致应用程序逻辑错误或异常。

2. **类型不兼容**

   + 在某些情况下，源对象的类型和目标类型之间可能存在不兼容的情况，无法进行转换。例如，将一个对象转换为不兼容的目标类型，或者将一个字符串转换为无法表示的目标类型。

3. **缺少类型转换器**

   + 如果没有合适的类型转换器可用，可能无法执行某些类型转换操作。在这种情况下，需要自定义类型转换器来满足特定的转换需求。
