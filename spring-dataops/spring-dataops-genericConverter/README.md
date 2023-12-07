

## GenericConverter

- [GenericConverter](#genericconverter)
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

`GenericConverter` 是 Spring 框架中的一个关键接口，专门用于类型转换。这个接口与 Spring 的类型转换系统紧密相关，其主要功能是将一个类型的对象转换为另一个类型。与 `Converter` 接口相比，`GenericConverter` 提供了更灵活的转换机制，允许转换操作在多个源目标类型和目标类型之间进行。

### 四、主要功能

1. **多元类型转换**

   + `GenericConverter` 能够处理更复杂的转换场景，支持从一个类型到多个目标类型的转换，也能从多个源类型转换到一个目标类型。这比简单的一对一转换（如 `Converter` 接口所提供的）更为复杂和灵活。

2. **支持泛型和复杂类型转换**

   + 使用 `TypeDescriptor`类，`GenericConverter` 能够处理包括泛型在内的更复杂的类型转换。这对于集合类型和泛型类的转换尤为重要。

3. **自定义转换逻辑**

   + 我们可以通过实现 `GenericConverter` 接口来提供自定义的转换逻辑，以满足特定的业务需求。

4. **与Spring类型转换体系的集成**

   + `GenericConverter` 可以无缝集成到 Spring 的类型转换体系中，与 Spring 的其他组件（如数据绑定）协同工作。

5. **灵活的转换类型声明**

   + 通过 `getConvertibleTypes()` 方法声明它可以处理的源类型和目标类型对，这使得 Spring 能夠更灵活地选择合适的转换器。

### 五、接口源码

`GenericConverter`接口允许在多个源和目标类型对之间进行转换，并在转换过程中利用 `TypeDescriptor` 来访问和处理字段的上下文信息，如注解和泛型。这种能力使得 `GenericConverter` 能够实现更为复杂和特定的转换逻辑。此外，该接口还包含一个内部类 `ConvertiblePair`，用于表示可转换的源到目标类型对。

```java
/**
 * 用于在两种或多种类型之间进行转换的通用转换器接口。
 *
 * <p>这是 Converter SPI 接口中最灵活但也最复杂的一个。它之所以灵活，是因为 GenericConverter 可以支持在多个源/目标
 * 类型对之间进行转换（参见 {@link #getConvertibleTypes()}）。此外，GenericConverter 实现在类型转换过程中可以访问源/目标
 * {@link TypeDescriptor 字段上下文}。这允许解析源和目标字段的元数据，如注解和泛型信息，这些信息可以用来影响转换逻辑。
 *
 * <p>当简单的 {@link Converter} 或 {@link ConverterFactory} 接口足够时，通常不应使用此接口。
 *
 * <p>实现还可以选择实现 {@link ConditionalConverter}。
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see TypeDescriptor
 * @see Converter
 * @see ConverterFactory
 * @see ConditionalConverter
 */
public interface GenericConverter {

	/**
	 * 返回此转换器可以在其间转换的源和目标类型。
	 * <p>每个条目都是一个可转换的源到目标类型对。
	 * <p>对于{@link ConditionalConverter 条件转换器}，此方法可能返回{@code null}，表示应该考虑所有源到目标的对。
	 */
	@Nullable
	Set<ConvertiblePair> getConvertibleTypes();

	/**
	 * 将源对象转换为 {@code TypeDescriptor} 描述的目标类型。
	 * @param source 要转换的源对象（可能是 {@code null}）
	 * @param sourceType 我们要从中转换的字段的类型描述符
	 * @param targetType 我们要转换到的字段的类型描述符
	 * @return 转换后的对象
	 */
	@Nullable
	Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType);


	/**
	 * 用于源到目标类对的持有者。
	 */
	final class ConvertiblePair {

		private final Class<?> sourceType;

		private final Class<?> targetType;

		/**
		 * 创建一个新的源到目标对。
		 * @param sourceType 源类型
		 * @param targetType 目标类型
		 */
		public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
			Assert.notNull(sourceType, "源类型不得为空");
			Assert.notNull(targetType, "目标类型不得为空");
			this.sourceType = sourceType;
			this.targetType = targetType;
		}

		public Class<?> getSourceType() {
			return this.sourceType;
		}

		public Class<?> getTargetType() {
			return this.targetType;
		}

		// 省略了 equals, hashCode 和 toString 方法的中文注释
	}
}
```

### 六、主要实现

1. **ArrayToArrayConverter**

   - 用于将一个数组转换为另一种类型的数组。例如，从 `Integer[]` 转换为 `String[]`。

2. **ArrayToCollectionConverter**

   - 将数组转换为集合。例如，将 `String[]` 转换为 `List<String>`。

3. **ArrayToObjectConverter**

   - 将数组转换为单个对象。通常用于从单元素数组中提取元素。

4. **ArrayToStringConverter**

   - 将数组转换为其字符串表示形式，通常用于打印或日志记录。

5. **ByteBufferConverter**

   - 用于将 `ByteBuffer` 转换为其他类型，如字符串或二进制数组。

6. **CollectionToArrayConverter**

   - 将集合转换为数组。例如，将 `List<Integer>` 转换为 `Integer[]`。

7. **CollectionToCollectionConverter**

   - 将一种集合转换为另一种类型的集合。例如，从 `List<Integer>` 转换为 `Set<Integer>`。

8. **CollectionToObjectConverter**

   - 将集合转换为单个对象，通常用于从单元素集合中提取元素。

9. **CollectionToStringConverter**

   - 将集合转换为其字符串表示，通常用于打印或日志记录。

10. **EnumToIntegerConverter**

    - 将枚举值转换为整数，通常是枚举的顺序值。

11. **EnumToStringConverter**

    - 将枚举值转换为字符串。

12. **FallbackObjectToStringConverter**

    - 当没有其他更具体的转换器可用时，将对象转换为字符串的后备转换器。

13. **IdToEntityConverter**

    - 将ID转换为实体对象，通常用于数据库实体的转换。

14. **MapToMapConverter**

    - 将一种类型的映射转换为另一种类型的映射。

15. **NumberToCharacterConverter**

    - 将数字转换为字符。

16. **ObjectToArrayConverter**

    - 将对象转换为数组，例如将单个对象包装成单元素数组。

17. **ObjectToCollectionConverter**

    - 将对象转换为集合，例如将单个对象包装成单元素集合。

18. **ObjectToObjectConverter**

    - 将一个对象转换为另一个类型的对象，通常用于复杂对象之间的转换。

19. **ObjectToOptionalConverter**

    - 将对象转换为 `Optional` 类型。

20. **ObjectToStringConverter**

    - 将对象转换为字符串。

21. **PropertiesToStringConverter**

    - 将 `Properties` 对象转换为字符串。

22. **StreamConverter**

    - 用于处理 Java 8 流（Stream）类型的转换。

### 七、最佳实践

使用 Spring 的 `DefaultConversionService` 来进行自定义类型转换。我们自定义的 `AnnotatedStringToDateConverter` 类然后添加到转换服务中，用于将字符串转换为 `Date` 对象。在我们的示例中，我给大家演示了两种转换：将普通日期字符串和包含时间的日期字符串分别转换为 `Date` 对象。转换后的日期被设置到 `MyBean` 实例的属性中，然后打印出这个实例以展示转换结果。

```java
public class GenericConverterDemo {

    public static void main(String[] args) {
        // 创建一个默认的转换服务
        DefaultConversionService service = new DefaultConversionService();
        // 向转换服务中添加自定义的转换器
        service.addConverter(new AnnotatedStringToDateConverter());

        // 定义源类型和目标类型，准备将 String 转换为 Date
        TypeDescriptor sourceType1 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType1 = new TypeDescriptor(ReflectionUtils.findField(MyBean.class, "date"));
        // 执行转换操作
        Date date = (Date) service.convert("2023-01-01", sourceType1, targetType1);

        // 定义另一组源类型和目标类型，准备将另一个 String 格式转换为 Date
        TypeDescriptor sourceType2 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType2 = new TypeDescriptor(ReflectionUtils.findField(MyBean.class, "dateTime"));
        // 执行转换操作
        Date dateTime = (Date) service.convert("2023-01-01 23:59:59", sourceType2, targetType2);

        // 使用转换得到的日期对象设置 MyBean 实例的属性
        MyBean myBean = new MyBean();
        myBean.setDate(date);
        myBean.setDateTime(dateTime);

        // 输出转换结果
        System.out.println("myBean = " + myBean);
    }
}
```

`AnnotatedStringToDateConverter` 类是一个自定义的类型转换器，用于将字符串转换为 `Date` 对象。转换逻辑不是固定的，而是基于目标 `Date` 类型字段上的 `DateFormat` 注解动态确定。

```java
public class AnnotatedStringToDateConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        // 定义可转换的类型对：从 String 到 Date
        return Collections.singleton(new ConvertiblePair(String.class, Date.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        // 如果源对象为空，直接返回 null
        if (source == null) {
            return null;
        }

        // 将源对象转换为字符串
        String dateString = (String) source;
        // 获取目标类型（Date类型字段）上的 DateFormat 注解
        DateFormat dateFormatAnnotation = targetType.getAnnotation(DateFormat.class);
        // 如果目标字段上没有 DateFormat 注解，则抛出异常
        if (dateFormatAnnotation == null) {
            throw new IllegalArgumentException("目标字段上缺少DateFormat注解");
        }

        try {
            // 根据注解中提供的日期格式创建 SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatAnnotation.value());
            // 使用 SimpleDateFormat 将字符串解析为日期对象
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            // 如果解析失败，抛出异常
            throw new IllegalArgumentException("无法解析日期", e);
        }
    }
}
```

定义了一个 Java 注解 `DateFormat`，用于指定日期格式。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateFormat {
    String value();
}
```

`MyBean`类中的 `date` 字段被注解为 `"yyyy-MM-dd"` 格式，而 `dateTime` 字段则被注解为 `"yyyy-MM-dd hh:mm:ss"` 格式。这些注解可以被用于自定义转换逻辑中，例如在使用之前定义的 `AnnotatedStringToDateConverter` 转换器时，这些注解将指导转换器如何将字符串转换为 `Date` 对象。

```java
public class MyBean {

    @DateFormat("yyyy-MM-dd")
    private Date date;

    @DateFormat("yyyy-MM-dd hh:mm:ss")
    private Date dateTime;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "date=" + date +
                ", dateTime=" + dateTime +
                '}';
    }
}
```

运行结果发现，`MyBean` 类的实例成功地将字符串转换为了相应的 `Date` 对象，并且这些对象的值反映了源字符串中的日期和时间信息。这个结果是自定义类型转换在实际应用中的一个典型例子，展示了通过注解和转换器相结合来实现灵活的类型转换逻辑。

```java
myBean = MyBean{date=Sun Jan 01 00:00:00 CST 2023, dateTime=Sun Jan 01 23:59:59 CST 2023}
```

### 八、与其他组件的关系

1. **Converter<S, T>**

   + 用于简单的一对一类型转换。`Converter` 接口定义了一个方法，将一种类型 `S` 转换为另一种类型 `T`，适用于直接的类型转换场景。

2. **ConverterFactory<S, R>**

   + 为一个源类型 `S` 到其子类型 `R` 的多个目标类型提供转换器。`ConverterFactory` 可以创建特定于目标类型的 `Converter` 实例，适合需要统一源类型到多个相关目标类型转换的场景。

3. **ConversionService**

   + 定义了一个类型转换服务，它在运行时提供类型转换功能。`ConversionService` 可以注册和管理多个 `Converter` 和 `GenericConverter` 实例，为应用程序提供集中的类型转换机制。

4. **ConditionalConverter**

   + 是一种特殊的转换器，它的执行依赖于特定条件。这种接口允许转换逻辑根据特定情况（如特定注解或字段属性）来决定是否执行。

### 九、常见问题

1. **类型匹配问题**

   + 在转换过程中可能会出现源类型或目标类型不匹配的问题。要解决这个问题，需要确保 `getConvertibleTypes()` 方法正确返回所有支持的源和目标类型对。

2. **转换逻辑错误**

   + 错误的转换逻辑可能导致转换失败或产生不正确的结果。为了避免这种情况，应仔细检查 `convert` 方法的实现，并确保转换逻辑正确处理了所有边界情况和异常情况。

3. **性能问题**

   + 复杂的转换逻辑可能影响应用程序的性能。为了优化性能，应避免在 `convert` 方法中进行昂贵的操作，并考虑对常用的转换结果进行缓存。

4. **上下文信息不足**

   + 有时转换过程需要更多的上下文信息，而 `TypeDescriptor` 提供的信息可能不足。解决这个问题可以考虑使用 `TypeDescriptor` 的高级功能，如访问字段的注解或泛型信息，或者改用更适合的转换器接口。

5. **异常处理不当**

   + 在转换过程中可能遇到各种异常，如果没有正确处理这些异常，可能会导致程序崩溃或不稳定。为了解决这个问题，在 `convert` 方法中应妥善处理所有潜在的异常，并在必要时抛出适当的自定义异常。