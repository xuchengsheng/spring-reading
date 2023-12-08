## ConditionalConverter

- [ConditionalConverter](#conditionalconverter)
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

1. **Converter**

   + [Converter](/spring-dataops/spring-dataops-converter/README.md) 接口是 Spring 框架中用于实现类型转换的一个关键组件。定义了一个简单的方法，用于将一种类型（源类型 S）转换为另一种类型（目标类型 T）。通过实现这个接口，我们可以创建自定义的转换逻辑，以便在 Spring 应用程序中无缝地进行复杂的数据转换。

2. **ConverterFactory**

   + [ConverterFactory](/spring-dataops/spring-dataops-converterFactory/README.md) 是 Spring 框架中的一个接口，用于实现类型转换的工厂。在 Spring 的类型转换体系中，`ConverterFactory` 接口扮演着创建特定类型转换器(`Converter`)的角色。这个接口主要用于那些有共同特性的一组类型转换场景。

3. **GenericConverter**

   + [GenericConverter](/spring-dataops/spring-dataops-genericConverter/README.md) 是 Spring 框架中的一个关键接口，专门用于类型转换。这个接口与 Spring 的类型转换系统紧密相关，其主要功能是将一个类型的对象转换为另一个类型。与 `Converter` 接口相比，`GenericConverter` 提供了更灵活的转换机制，允许转换操作在多个源目标类型和目标类型之间进行。

### 三、基本描述

`ConditionalConverter` 是 Spring 框架中用于类型转换的一个接口，它是 Spring 类型转换系统的一部分。这个接口的主要目的是为转换器（Converter）提供一个条件检查的功能，使得在执行实际的类型转换之前，转换器能够根据特定的条件判断是否应该进行转换。

### 四、主要功能

1. **条件性检查**

   - `ConditionalConverter` 提供了一个 `matches(TypeDescriptor sourceType, TypeDescriptor targetType)` 方法，这是它的主要功能。这个方法允许转换器在执行实际的转换逻辑之前，根据源类型和目标类型判断是否应该进行转换。

2. **灵活性**

   - 通过实现这个接口，我们可以编写更灵活的转换逻辑。转换器可以在运行时根据当前的上下文或特定的条件决定是否执行转换。这种方法特别适用于那些转换逻辑依赖于动态条件或环境变量的情况。

3. **与其他转换器接口的协作**

   - 虽然 `ConditionalConverter` 可以单独实现，但通常它会与其他类型转换器接口（如 `Converter` 或 `GenericConverter`）结合使用。在这种情况下，`ConditionalConverter` 为转换提供了一个额外的决策层，允许转换器根据条件进行智能选择。

4. **减少资源消耗**

   - 对于资源密集型的转换操作，使用 `ConditionalConverter` 可以减少不必要的资源消耗，因为它允许转换器在确定转换是必要的之前，先进行条件判断。

### 五、接口源码

`ConditionalConverter` 接口在 Spring 框架中用于有条件的类型转换。它允许转换器（Converter）、泛型转换器（GenericConverter）或转换器工厂（ConverterFactory）基于源对象和目标对象的类型特征（如注解或特定方法的存在）来决定是否执行特定的转换操作。

```java
/**
 * 允许一个 {@link Converter}、{@link GenericConverter} 或 {@link ConverterFactory} 
 * 根据 {@code source} 源类型和 {@code target} 目标类型的 {@link TypeDescriptor} 属性来有条件地执行转换。
 *
 * <p>这通常用于根据字段或类级特征（如注解或方法）的存在，有选择性地匹配自定义转换逻辑。
 * 例如，在将字符串字段转换为日期字段时，如果目标字段也用 {@code @DateTimeFormat} 注解标记，
 * 实现可能会返回 {@code true}。
 *
 * <p>另一个例子是，在将字符串字段转换为 {@code Account} 字段时，
 * 如果目标 Account 类定义了一个 {@code public static findAccount(String)} 方法，
 * 实现可能会返回 {@code true}。
 *
 * @author Phillip Webb
 * @author Keith Donald
 * @since 3.2
 * @see Converter
 * @see GenericConverter
 * @see ConverterFactory
 * @see ConditionalGenericConverter
 */
public interface ConditionalConverter {

	/**
	 * 是否应该选择当前考虑的从 {@code sourceType} 源类型到 {@code targetType} 目标类型的转换？
	 * @param sourceType 正在转换的字段的类型描述符
	 * @param targetType 正在转换到的字段的类型描述符
	 * @return 如果应该执行转换则为 true，否则为 false
	 */
	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);

}
```

### 六、主要实现

1. **AnnotationParserConverter**

   - 解析注解并转换为相应的对象。

2. **AnnotationPrinterConverter**

   - 将对象转换为表示其注解的字符串形式。

3. **ArrayToArrayConverter**

   - 将一个数组转换为另一种类型的数组。

4. **ArrayToCollectionConverter**

   - 将数组转换为集合。

5. **ArrayToDelimitedStringConverter**

   - 将数组转换为由特定分隔符分隔的字符串。

6. **ArrayToObjectConverter**

   - 将数组转换为单个对象。

7. **ArrayToStringConverter**

   - 将数组转换为字符串表示形式。

8. **ByteBufferConverter**

   - 转换字节缓冲区为其他类型。

9. **CharSequenceToObjectConverter**

   - 将字符序列转换为对象。

10. **CollectionToArrayConverter**

    - 将集合转换为数组。

11. **CollectionToCollectionConverter**

    - 将一种类型的集合转换为另一种类型的集合。

12. **CollectionToDelimitedStringConverter**

    - 将集合转换为由特定分隔符分隔的字符串。

13. **CollectionToObjectConverter**

    - 将集合转换为单个对象。

14. **CollectionToStringConverter**

    - 将集合转换为字符串表示形式。

15. **DelimitedStringToArrayConverter**

    - 将由特定分隔符分隔的字符串转换为数组。

16. **DelimitedStringToCollectionConverter**

    - 将由特定分隔符分隔的字符串转换为集合。

17. **FallbackObjectToStringConverter**

    - 作为后备方案，将对象转换为字符串。

18. **IdToEntityConverter**

    - 将标识符转换为实体对象。

19. **MapToMapConverter**

    - 将一种类型的映射（Map）转换为另一种类型的映射。

20. **ObjectToArrayConverter**

    - 将对象转换为数组。

21. **ObjectToCollectionConverter**

    - 将对象转换为集合。

22. **ObjectToObjectConverter**

    - 将一种类型的对象转换为另一种类型的对象。

23. **ObjectToOptionalConverter**

    - 将对象转换为 `Optional` 类型。

24. **StreamConverter**

    - 处理流（Stream）相关的转换。

25. **StringToArrayConverter**

    - 将字符串转换为数组。

26. **StringToCollectionConverter**

    - 将字符串转换为集合。

27. **TypeConverterConverter**

    - 处理类型转换器之间的转换。

### 七、最佳实践

使用 `StringToIntegerConditionalConverter` 转换器。首先，它创建了转换器的实例，并为源类型（`String`）和目标类型（`Integer`）定义了类型描述符。然后，它使用 `matches` 方法检查是否满足从 `String` 到 `Integer` 的转换条件。如果条件匹配，则使用 `convert` 方法执行转换并打印结果；如果条件不匹配，则输出一条消息表明转换不适用。

```java
public class ConditionalConverterDemo {

    public static void main(String[] args) {
        // 创建自定义的转换器实例
        StringToIntegerConditionalConverter converter = new StringToIntegerConditionalConverter();

        // 定义源类型（String）和目标类型（Integer）的描述符
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);

        // 测试转换器是否适用于从 String 到 Integer 的转换
        if (converter.matches(sourceType, targetType)) {
            // 如果转换条件匹配，则执行转换
            Integer result = converter.convert("8");
            System.out.println("Converted result: " + result);
        } else {
            // 如果条件不匹配，打印不适用的消息
            System.out.println("Conversion not applicable.");
        }
    }
}
```

`StringToIntegerConditionalConverter` 是一个根据源类型和目标类型的类型信息来决定是否执行转换的条件转换器。它只在源类型为 `String` 且目标类型为 `Integer` 时执行转换操作，并且提供了字符串到整数的具体转换逻辑。

```java
public class StringToIntegerConditionalConverter implements Converter<String, Integer>, ConditionalConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        // 判断条件：当源类型是 String 且目标类型是 Integer 时返回 true
        return String.class.equals(sourceType.getType()) && Integer.class.equals(targetType.getType());
    }

    @Override
    public Integer convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert String to Integer: " + source, e);
        }
    }
}
```

运行结果发现，字符串 `"8"` 被正确转换为了整数 `8`。这个输出证明了您的自定义转换器按预期工作，并能够在满足特定条件时将字符串转换为整数。

```java
Converted result: 8
```

### 八、与其他组件的关系

1. **Converter**

   - `Converter<S, T>` 是一个简单的接口，用于将一种类型 `S` 转换为另一种类型 `T`。`ConditionalConverter` 可以与 `Converter` 接口结合使用，允许基于特定条件执行转换。

2. **GenericConverter**

   - `GenericConverter` 是一个更复杂的接口，允许更灵活的类型转换，包括集合和泛型类型。`ConditionalConverter` 可以与 `GenericConverter` 结合使用，为复杂的转换提供条件控制。

3. **ConverterFactory**

   - `ConverterFactory` 用于创建特定类型之间的转换器。当与 `ConditionalConverter` 结合时，可以在创建转换器时加入条件判断的逻辑。

4. **ConditionalGenericConverter**

   - `ConditionalGenericConverter` 是 `GenericConverter` 和 `ConditionalConverter` 的结合体，用于复杂的转换场景，同时需要基于条件判断是否执行转换。

5. **ConversionService**

   - `ConversionService` 是 Spring 提供的一个中心接口，用于在整个框架中执行类型转换操作。实现 `ConditionalConverter` 的转换器可以被注册到 `ConversionService` 中，使得转换操作可以在满足特定条件时自动触发。

### 九、常见问题

1. **判断条件不清晰或过于复杂**

   - 在实现 `matches` 方法时，需要明确和简洁地定义转换应用的条件。如果条件过于复杂，可能会导致理解困难和维护问题。确保条件逻辑既清晰又准确。

2. **性能问题**

   - 条件判断逻辑如果不够高效，可能会对应用程序的性能产生负面影响。特别是在大量数据需要转换时，应该优化条件判断逻辑，避免不必要的复杂计算。

3. **不正确的类型匹配**

   - 确保 `matches` 方法中的类型匹配逻辑正确无误。错误的类型匹配可能会导致转换器在不适当的情况下被调用，进而引发数据错误或异常。

