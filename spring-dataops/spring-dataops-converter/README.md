## Converter

- [Converter](#converter)
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

1. **泛型（Generics）**

   + 由于 `Converter` 接口使用了 Java 泛型，因此对 Java 泛型有一定了解是必要的。理解如何使用泛型来创建类型安全的代码对实现自定义转换器非常重要。

2. **Spring MVC**
+ 如果你打算在 Web 应用中使用 `Converter` 接口，了解 Spring MVC 的工作原理是有帮助的。这包括理解控制器（Controllers）、请求映射（Request Mappings）、视图解析（View Resolvers）等。
  
3. **类型转换与格式化**

   + 理解 Java 中的类型转换基础，以及如何格式化数据。这对编写有效的转换器逻辑至关重要。

### 三、基本描述

`Converter` 接口是 Spring 框架中用于实现类型转换的一个关键组件。定义了一个简单的方法，用于将一种类型（源类型 S）转换为另一种类型（目标类型 T）。通过实现这个接口，我们可以创建自定义的转换逻辑，以便在 Spring 应用程序中无缝地进行复杂的数据转换。

### 四、主要功能

1. **类型转换**

   + 主要功能是提供一种机制来将一个对象从一种类型转换为另一种类型。这对于处理不兼容的数据类型非常有用，特别是在需要将外部系统的数据格式转换为内部使用的格式时。

2. **数据格式化**

   + 在 Web 应用程序中，`Converter` 可用于将来自用户界面的数据（通常是字符串格式）转换为后端系统可识别的格式，或反之。这对于日期、时间、数字等复杂数据类型尤其重要。

3. **数据绑定**

   + 在 Spring MVC 中，`Converter` 接口用于将请求参数（例如，来自 HTTP 请求的字符串）转换为控制器方法参数的适当类型。

4. **提升代码可维护性和重用性**

   + 通过定义清晰的转换规则，`Converter` 实现促进了代码的模块化，使得相同的转换逻辑可以在应用程序的不同部分重用。

5. **自定义转换逻辑**

   + 与内置的转换器相比，自定义 `Converter` 实现允许我们定义特定于应用程序的转换逻辑，从而处理更复杂或特定的转换需求。

6. **集成与扩展**

   + `Converter` 接口可以轻松集成到 Spring 框架的其他部分，如数据访问层、Spring Boot 自动配置等，同时也可以被扩展以支持更复杂的转换需求。

### 五、接口源码

`Converter<S, T>` 接口是 Spring 框架中用于实现从一种类型到另一种类型转换的标准机制。定义了一个单一的 `convert` 方法，用于执行实际的转换逻辑。这个接口是线程安全的，可在应用中共享和重复使用。除了基本的转换功能，该接口还提供了 `andThen` 方法，允许将多个转换器组合在一起，以实现更复杂的转换逻辑。

```java
/**
 * 一个转换器，负责将类型为 {@code S} 的源对象转换为类型为 {@code T} 的目标对象。
 *
 * <p>该接口的实现应是线程安全的，并且可以共享。
 *
 * <p>实现还可以选择实现 {@link ConditionalConverter}。
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 */
@FunctionalInterface
public interface Converter<S, T> {

	/**
	 * 将类型为 {@code S} 的源对象转换为类型为 {@code T} 的目标对象。
	 * @param source 要转换的源对象，必须是类型 {@code S} 的实例（不能是 {@code null}）
	 * @return 转换后的对象，必须是类型 {@code T} 的实例（可能是 {@code null}）
	 * @throws IllegalArgumentException 如果源对象无法转换为所需的目标类型
	 */
	@Nullable
	T convert(S source);

	/**
	 * 构建一个组合的 {@link Converter}，该转换器首先对其输入应用此 {@link Converter}，
	 * 然后将 {@code after} {@link Converter} 应用于结果。
	 * @param after 在此 {@link Converter} 应用后要应用的 {@link Converter}
	 * @param <U> both the {@code after} {@link Converter} 和组合的 {@link Converter} 的输出类型
	 * @return 一个组合的 {@link Converter}，首先应用此 {@link Converter}，然后应用 {@code after} {@link Converter}
	 * @since 5.3
	 */
	default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
		Assert.notNull(after, "After Converter 不能为空");
		return (S s) -> {
			T initialResult = convert(s);
			return (initialResult != null ? after.convert(initialResult) : null);
		};
	}

}
```

### 六、主要实现

1. **StringToArrayConverter**

   - 将字符串转换为数组，例如将逗号分隔的值转换为字符串数组。

2. **StringToBooleanConverter**

   - 将字符串转换为布尔值。

3. **StringToCharacterConverter**

   - 将字符串转换为字符。

4. **StringToCharsetConverter**

   - 将字符串转换为 `Charset` 对象。

5. **StringToCollectionConverter**

   - 将字符串转换为集合，例如将逗号分隔的字符串转换为列表。

6. **StringToCurrencyConverter**

   - 将字符串转换为 `Currency` 对象。

7. **StringToLocaleConverter**

   - 将字符串转换为 `Locale` 对象。

### 七、最佳实践

使用 `DefaultConversionService` 来管理和执行类型转换。首先，我们创建了一个转换服务的实例，并向其中注册了自定义的 `StringToLocalDateConverter` 和 `StringToBooleanConverter` 转换器。接着，代码通过 `canConvert` 方法检查是否可以执行特定的转换，并使用 `convert` 方法进行实际的转换。

```java
public class ConverterDemo {

    public static void main(String[] args) {
        // 创建一个默认的转换服务
        DefaultConversionService service = new DefaultConversionService();

        // 向服务中添加自定义的转换器
        service.addConverter(new StringToLocalDateConverter());
        service.addConverter(new StringToBooleanConverter());

        // 检查是否可以将字符串转换为 LocalDate
        if (service.canConvert(String.class, LocalDate.class)) {
            LocalDate localDate = service.convert("2023-12-07", LocalDate.class);
            System.out.println("LocalDate = " + localDate);
        }

        // 检查是否可以将字符串 "yes" 转换为 Boolean
        if (service.canConvert(String.class, Boolean.class)) {
            Boolean boolValue = service.convert("yes", Boolean.class);
            System.out.println("yes = " + boolValue);
        }

        // 检查是否可以将字符串 "no" 转换为 Boolean
        if (service.canConvert(String.class, Boolean.class)) {
            Boolean boolValue = service.convert("no", Boolean.class);
            System.out.println("no = " + boolValue);
        }
    }
}
```

定义了一个 `StringToLocalDateConverter` 类，用于实现将格式为 "`yyyy-MM-dd`" 的字符串转换为 `LocalDate` 对象。我们通过实现 Spring 的 `Converter` 接口，使用 `DateTimeFormatter` 对字符串进行解析，从而实现类型重用的日期转换功能。

```java
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(source, df);
    }
}
```

定义了 `StringToBooleanConverter` 类，实现了将字符串转换为布尔值的功能。我们通过预定义的字符串集合来识别表示真值（如 "true", "yes", "1"）和假值（如 "false", "no", "0"）的字符串，并相应地返回 `Boolean.TRUE` 或 `Boolean.FALSE`。

```java
public class StringToBooleanConverter implements Converter<String, Boolean> {

    private static final Set<String> trueValues = new HashSet<>(8);

    private static final Set<String> falseValues = new HashSet<>(8);

    static {
        trueValues.add("true");
        trueValues.add("on");
        trueValues.add("yes");
        trueValues.add("1");

        falseValues.add("false");
        falseValues.add("off");
        falseValues.add("no");
        falseValues.add("0");
    }

    @Override
    @Nullable
    public Boolean convert(String source) {
        if (trueValues.contains(source)) {
            return Boolean.TRUE;
        }
        if (falseValues.contains(source)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
    }
}
```

运行结果发现，字符串 "2023-12-07" 被转换为了对应的 `LocalDate` 对象，而字符串 "yes" 和 "no" 分别被准确地转换为了布尔值 `true` 和 `false`。这证明了自定义转换器在处理日期和布尔值转换时的有效性。

```java
LocalDate = 2023-12-07
yes = true
no = false
```

### 八、与其他组件的关系

1. **ConversionService**

   - `ConversionService` 是一个中心接口，用于执行类型转换。`Converter` 接口的实现通常被注册到 `ConversionService` 中，以便在整个应用程序中使用。

2. **Data Binding**

   - 在 Spring MVC 中，数据绑定过程利用 `Converter` 接口将 HTTP 请求参数转换为控制器方法参数的适当类型。这对于表单提交和查询参数处理尤其重要。

3. **Spring Boot and Spring Configuration**

   - 在 Spring Boot 和 Spring 应用程序配置中，`Converter` 接口用于将配置属性（如属性文件中的值）转换为更复杂的对象或自定义类型。

4. **Spring Data**

   - 在 Spring Data 的上下文中，`Converter` 接口可以用于在数据库实体和应用程序中使用的领域模型之间进行转换。

5. **Field Formatting**

   - `Converter` 通常与 `Formatter` 接口一起使用，后者专注于字段的格式化和解析，特别是在 Web 环境中。`Formatter` 可以视为一种特殊类型的 `Converter`。

6. **Bean Validation**

   - 在处理 Bean 验证时，`Converter` 接口可以用于在验证逻辑之前或之后转换数据。

7. **RestTemplate and WebClient**

   - 在使用 Spring 的 RestTemplate 或 WebClient 时，`Converter` 接口可以用于序列化和反序列化 HTTP 请求和响应体。

### 九、常见问题

1. **转换逻辑错误**

   - 错误地实现转换逻辑可能导致数据转换不正确或转换过程中发生异常。重要的是要确保转换逻辑正确，并且能够处理各种输入情况。

2. **注册转换器失败**

   - 有时我们可能忘记将自定义转换器注册到 Spring 的 `ConversionService` 中，导致预期的转换未能发生。

3. **处理空值**

   - 我们需要决定如何处理 `null` 输入。某些情况下可能需要将 `null` 转换为默认值，而在其他情况下则应保持为 `null` 或抛出异常。

4. **类型检查和错误处理**

   - 正确处理输入类型和转换过程中可能出现的错误是至关重要的。例如，转换方法应检查输入是否为预期类型，并在不合适的情况下抛出适当的异常。

5. **与其他转换器的冲突**

   - 在大型项目中，可能会有多个转换器能够处理相同的源和目标类型。这可能导致不明确的转换逻辑，需要小心管理以避免冲突。