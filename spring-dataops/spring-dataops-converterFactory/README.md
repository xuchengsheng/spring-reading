## ConverterFactory

- [ConverterFactory](#converterfactory)
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

### 三、基本描述

`ConverterFactory` 是 Spring 框架中的一个接口，用于实现类型转换的工厂。在 Spring 的类型转换体系中，`ConverterFactory` 接口扮演着创建特定类型转换器(`Converter`)的角色。这个接口主要用于那些有共同特性的一组类型转换场景。

### 四、主要功能

1. **动态创建转换器**

   + `ConverterFactory` 能够根据不同的目标类型动态地创建相应的 `Converter` 实例。这意味着我们可以为一系列相关的转换任务只定义一个 `ConverterFactory`，而不是为每种转换任务单独定义一个 `Converter`。

2. **支持广泛的类型转换**

   + 可以用于处理各种复杂的类型转换需求，特别是当源类型和目标类型之间的转换规则类似，但目标类型有多种可能时。例如，将字符串转换为不同类型的枚举值或数字类型。

3. **提高代码的重用性和维护性**

   + 通过集中处理一组相似的转换逻辑，`ConverterFactory` 帮助减少代码的重复，并使类型转换的维护更加方便。

4. **灵活性和扩展性**

   + 提供了一种灵活的方式来扩展 Spring 的类型转换功能。开发者可以根据实际需求自定义 `ConverterFactory`，以适应特定应用程序的类型转换需求。

5. **与 Spring ConversionService 的集成**

   + 在 Spring 的类型转换体系中，`ConverterFactory` 可以轻松地与 `ConversionService` 集成，从而在整个应用程序中提供一致的类型转换服务。

6. **简化配置**

   + 在使用 Spring 配置时，`ConverterFactory` 使得针对一组相关类型的转换配置更加简洁，因为我们只需要注册一个工厂，而不是注册多个独立的转换器。

### 五、接口源码

`ConverterFactory` 是 Spring 框架中的一个接口，用于创建能够将对象从一种源类型（S）转换到其目标类型（R）的子类型的转换器。这个接口主要适用于需要处理多种相关但具体不同的目标子类型的转换场景，提供了一种灵活和高效的方式来生成和管理这些类型转换器。通过其 `getConverter` 方法，可以根据特定的目标类型动态获取相应的转换器，从而简化了类型转换的实现和配置过程。

```java
/**
 * 用于创建能够将对象从 S 类型转换为 R 的子类型的“范围”转换器的工厂。
 *
 * <p>实现类可以额外实现 {@link ConditionalConverter}。
 *
 * @author Keith Donald
 * @since 3.0
 * @param <S> 此工厂创建的转换器可以从中转换的源类型
 * @param <R> 此工厂创建的转换器可以转换到的目标范围（或基础）类型；
 *            例如对于一组数字子类型来说就是 {@link Number}。
 * @see ConditionalConverter
 */
public interface ConverterFactory<S, R> {

	/**
	 * 获取转换器以从 S 转换到目标类型 T，其中 T 也是 R 的实例。
	 * @param <T> 目标类型
	 * @param targetType 要转换到的目标类型
	 * @return 从 S 到 T 的转换器
	 */
	<T extends R> Converter<S, T> getConverter(Class<T> targetType);

}
```

### 六、主要实现

1. **CharacterToNumberFactory**

   + 用于创建将字符（`Character`）转换为数字（`Number`）类型的转换器。它可以用于场景，比如将字符 '1' 转换为数字 1。

2. **IntegerToEnumConverterFactory**

   + 用于创建将整数（`Integer`）转换为枚举（`Enum`）值的转换器。这在处理基于整数索引的枚举类型时非常有用，例如，将整数 0、1、2 分别转换为枚举中的第一个、第二个、第三个值。

3. **LenientObjectToEnumConverterFactory**

   + 用于提供一种从任意对象到枚举类型的宽松转换。它不仅处理直接的类型映射，而且通常包含更复杂的逻辑，以允许从各种不同的源类型安全地转换为枚举值。

4. **NumberToNumberConverterFactory**

   + 用于创建从一种数字类型转换为另一种数字类型的转换器。例如，它可以创建一个转换器，将 `Double` 转换为 `Integer`，或者将 `Long` 转换为 `Float` 等。

5. **StringToEnumConverterFactory**

   + 用于创建从字符串（`String`）转换到枚举（`Enum`）类型的转换器。它在处理来自文本源（如配置文件或用户输入）的枚举值时非常实用，能够将字符串映射到相应的枚举常量。

6. **StringToNumberConverterFactory**

   + 用于创建将字符串转换为数字的转换器。这在很多情况下都是必需的，比如在解析数字类型的配置参数或者用户输入时。它可以处理从字符串到各种数字类型（如 `Integer`、`Double`、`BigDecimal` 等）的转换。

### 七、最佳实践

我们使用 `DefaultConversionService` 实例，这是 Spring 框架提供的一个用于默认类型转换的服务。接着，我们向这个服务中添加了一个我们自定义的转换工厂类`StringToNumberConverterFactory`，用于创建从字符串到数字的转换器的工厂类，最后我们通过调用 `conversionService.convert("8", Integer.class)`方法，将字符串 "8" 转换为 `Integer` 类型的数字。

```java
public class CharacterToNumberFactoryDemo {

    public static void main(String[] args) {
        // 创建一个默认的转换服务
        // 这里使用 GenericConversionService，它是一个通用的类型转换服务
        DefaultConversionService conversionService = new DefaultConversionService();

        // 向转换服务中添加一个字符串到数字的转换器工厂
        // StringToNumberConverterFactory 是一个工厂类，用于创建字符串到数字的转换器
        conversionService.addConverterFactory(new StringToNumberConverterFactory());

        // 使用转换服务将字符串 "8" 转换为 Integer 类型
        // 这里演示了如何将字符串转换为对应的整数
        Integer num = conversionService.convert("8", Integer.class);

        // 输出转换结果
        System.out.println("String to Integer: " + num);
    }
}
```

 `StringToNumberConverterFactory` 类是一个实现了 `ConverterFactory` 接口的具体类，专门用于将字符串转换为数字类型。这个类内部定义了一个静态内部类 `StringToNumber`，它实现了 `Converter` 接口，用于执行实际的转换逻辑。

```java
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumberConverterFactory.StringToNumber<>(targetType);
    }

    private static final class StringToNumber<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        public StringToNumber(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        @Nullable
        public T convert(String source) {
            return NumberUtils.parseNumber(source, this.targetType);
        }
    }
}
```

运行结果发现，`StringToNumberConverterFactory` 类的实现成功地将字符串 "8" 转换成了 `Integer` 类型的数字 8。

```java
String to Integer: 8
```

### 八、与其他组件的关系

1. **Converter 接口**

   - `ConverterFactory` 用于创建 `Converter` 实例。`Converter<S, T>` 接口是一个更简单的类型转换接口，它将一个类型 `S` 直接转换为另一个类型 `T`。`ConverterFactory` 用于更复杂的场景，其中需要基于目标类型动态创建转换器。

2. **ConditionalConverter**

   - `ConverterFactory` 实现可以选择性地实现 `ConditionalConverter` 接口。这允许转换器在执行转换前进行一些条件判断，比如检查源类型和目标类型是否满足特定条件。

3. **ConversionService**

   - `ConversionService` 是 Spring 的类型转换服务的核心接口。它定义了一个用于类型转换的统一服务接口，可以注册 `Converter` 和 `ConverterFactory` 实现。`ConversionService` 在进行类型转换时会查询注册的转换器和转换器工厂以执行转换任务。

4. **GenericConversionService**

   - `GenericConversionService` 是 `ConversionService` 接口的一个具体实现，提供了类型转换的通用实现。它管理着一组 `Converter` 和 `ConverterFactory` 实例，并根据需要进行类型转换。

5. **DefaultConversionService**

   - `DefaultConversionService` 是 `GenericConversionService` 的扩展，预先注册了一系列常用的 `Converter` 和 `ConverterFactory` 实例。它为常见的转换场景提供了即用型的支持。

### 九、常见问题

1. **转换器未被正确注册**

   + 确保在 Spring 配置中正确注册了 `ConverterFactory`，并且 `ConversionService` 在应用程序上下文中可用。

2. **转换逻辑错误或不足**

   + 对 `Converter` 实现进行彻底测试，确保它能够正确处理各种输入情况，包括异常值和边界情况。

3. **类型转换异常**

   + 增加适当的错误处理和类型检查逻辑，确保在执行转换之前源类型和目标类型是兼容的。

4. **转换性能问题**

   + 优化转换器的实现，避免不必要的计算和资源消耗，可以考虑缓存某些重复的转换结果。

5. **泛型类型擦除问题**

   + 使用具体的目标类型而非泛型类型来注册和查找转换器。

6. **自定义转换规则的维护和更新**

   + 保持转换器逻辑的清晰和模块化，使其易于维护和扩展。