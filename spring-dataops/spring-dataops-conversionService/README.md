## ConversionService

- [ConversionService](#conversionservice)
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

4. **ConditionalConverter**

   + [ConditionalConverter](/spring-dataops/spring-dataops-conditionalConverter/README.md) 是 Spring 框架中用于类型转换的一个接口，它是 Spring 类型转换系统的一部分。这个接口的主要目的是为转换器（Converter）提供一个条件检查的功能，使得在执行实际的类型转换之前，转换器能够根据特定的条件判断是否应该进行转换。

### 三、基本描述

`ConversionService` 接口是 Spring 框架中的一个关键部分，专门用于在不同数据类型之间进行转换。这个接口提供了一个中心化的方式来将一个类型的对象转换为另一个类型，极大地简化了数据处理和操作的复杂性。它也支持自定义转换规则，并与 Spring 的数据绑定和验证机制紧密集成，特别适用于处理 Web 请求参数、配置数据以及数据库记录。

### 四、主要功能

1. **类型转换**

   + 最基本的功能是将一种数据类型转换为另一种。这涵盖了从简单类型（如字符串到整数）到更复杂对象的转换。

2. **支持自定义转换器**

   + 允许我们注册自定义的转换器（ `Converter`, `ConverterFactory`, 或 `GenericConverter`），以处理特定类型的转换逻辑。

3. **通用性**

   + 这个接口不局限于任何特定的数据类型，因此可以广泛应用于不同的场景，如用户界面数据绑定、数据持久化等。

4. **集成Spring MVC**

   + 在 Spring MVC中，`ConversionService` 被用来处理数据绑定和类型转换，特别是在处理表单提交或路径参数时。
   
6. **查询转换能力**
   + 可以查询 `ConversionService` 以确定是否可以在两种特定类型之间进行转换。

### 五、接口源码

`ConversionService` 接口定义了一套灵活的方法来支持在不同类型之间的转换。它提供了两种核心功能：一是检查是否可以在特定的源类型和目标类型之间进行转换；二是执行实际的转换操作。这个接口特别强调了对于集合、数组和映射类型转换的特殊处理，并明确指出在这些情况下可能会遇到的异常。

```java
/**
 * 用于类型转换的服务接口。这是转换系统的入口点。
 * 调用 {@link #convert(Object, Class)} 使用这个系统执行线程安全的类型转换。
 *
 * @author Keith Donald
 * @author Phillip Webb
 * @since 3.0
 */
public interface ConversionService {

	/**
	 * 如果 {@code sourceType} 类型的对象可以转换为 {@code targetType}，则返回 {@code true}。
	 * <p>如果此方法返回 {@code true}，意味着 {@link #convert(Object, Class)} 能够将 {@code sourceType} 的实例转换为 {@code targetType}。
	 * <p>关于集合、数组和映射类型的特别说明：
	 * 对于集合、数组和映射类型之间的转换，即使转换调用可能仍会产生 {@link ConversionException}（如果底层元素不可转换），此方法也会返回 {@code true}。
	 * 调用者在处理集合和映射时，应该处理这种异常情况。
	 * @param sourceType 要从中转换的源类型（如果源是 {@code null} 则可能为 {@code null}）
	 * @param targetType 要转换到的目标类型（必需）
	 * @return 如果可以执行转换则返回 {@code true}，否则返回 {@code false}
	 * @throws IllegalArgumentException 如果 {@code targetType} 为 {@code null}
	 */
	boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);

	/**
	 * 如果 {@code sourceType} 类型的对象可以转换为 {@code targetType}，则返回 {@code true}。
	 * TypeDescriptors 提供有关转换将发生的源和目标位置的附加上下文，通常是对象字段或属性位置。
	 * <p>如果此方法返回 {@code true}，意味着 {@link #convert(Object, TypeDescriptor, TypeDescriptor)}
	 * 能够将 {@code sourceType} 的实例转换为 {@code targetType}。
	 * <p>关于集合、数组和映射类型的特别说明：同上。
	 * @param sourceType 关于要从中转换的源类型的上下文（如果源是 {@code null} 则可能为 {@code null}）
	 * @param targetType 关于要转换到的目标类型的上下文（必需）
	 * @return 如果可以在源和目标类型之间执行转换，则返回 {@code true}，否则返回 {@code false}
	 * @throws IllegalArgumentException 如果 {@code targetType} 为 {@code null}
	 */
	boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * 将给定的 {@code source} 转换为指定的 {@code targetType}。
	 * @param source 要转换的源对象（可能为 {@code null}）
	 * @param targetType 要转换到的目标类型（必需）
	 * @return 转换后的对象，是 targetType 的实例
	 * @throws ConversionException 如果转换过程中发生异常
	 * @throws IllegalArgumentException 如果 targetType 为 {@code null}
	 */
	@Nullable
	<T> T convert(@Nullable Object source, Class<T> targetType);

	/**
	 * 将给定的 {@code source} 转换为指定的 {@code targetType}。
	 * TypeDescriptors 提供有关转换将发生的源和目标位置的附加上下文。
	 * @param source 要转换的源对象（可能为 {@code null}）
	 * @param sourceType 关于要从中转换的源类型的上下文（如果源是 {@code null} 则可能为 {@code null}）
	 * @param targetType 关于要转换到的目标类型的上下文（必需）
	 * @return 转换后的对象，是 {@link TypeDescriptor#getObjectType() targetType} 的实例
	 * @throws ConversionException 如果转换过程中发生异常
	 * @throws IllegalArgumentException 如果 targetType 为 {@code null}，
	 * 或者 {@code sourceType} 为 {@code null} 但源不是 {@code null}
	 */
	@Nullable
	Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);

}
```

### 六、主要实现

1. **GenericConversionService**

   + 作为 `ConversionService` 的基本实现，`GenericConversionService` 提供了一个通用的转换框架，支持包括 `Converter`、`ConverterFactory` 和 `GenericConverter` 在内的各种转换器类型。它是构建更复杂转换服务的基础，因其通用性，适用于多种转换场景。

2. **DefaultConversionService**

   + 扩展自 `GenericConversionService`，`DefaultConversionService` 增加了许多常见数据类型的默认转换器，为大多数标准应用程序提供了即插即用的转换功能。这使它成为处理常规数据类型转换的理想选择。

3. **FormattingConversionService**

   + `FormattingConversionService` 不仅支持类型转换，还提供了对象到字符串的格式化以及从字符串到对象的解析功能，特别适合于处理用户界面相关的数据转换。它常用于 Web 应用程序，尤其在 Spring MVC 中处理表单数据和视图层数据展示。

4. **DefaultFormattingConversionService**

   +  这个服务是 `FormattingConversionService` 的具体实现，它集成了 `DefaultConversionService` 的所有功能，并添加了常用的格式化器和解析器。它提供了一套完整的解决方案，用于 Web 应用程序中的数据格式化和解析。

5. **ApplicationConversionService**

   + 为 Spring 应用程序设计的全面 `ConversionService` 实现，`ApplicationConversionService` 继承自 `DefaultFormattingConversionService`，加入了额外的 Spring 特定转换器。它是 Spring Boot 自动配置中的默认选择，适用于大多数 Spring 应用程序。

6. **TypeConverterConversionService**

   + 这是一个适配器类，将 `TypeConverter` 接口和 `ConversionService` 接口桥接在一起。它允许旧的 `PropertyEditor` 类型转换器在新的 `ConversionService` 结构中使用，适用于将旧代码迁移到使用新的转换服务框架的场景。

7. **WebConversionService**

   + 专门为 Web 环境设计的 `ConversionService` 实现，`WebConversionService` 扩展了 `FormattingConversionService`，提供了特定于 Web 的转换器和格式化器。这个服务特别适用于处理 HTTP 请求和响应中的数据转换，是 Web 应用程序的理想选择。

### 七、最佳实践

使用 `DefaultConversionService` 来执行类型转换。首先，它通过 `canConvert` 方法检查是否可以从 `Integer` 类型转换为 `String` 类型。如果可以，它会执行转换并输出结果。接着，示例通过使用 `TypeDescriptor` 进行更细粒度的转换能力检查，这是一种更复杂的转换检查方式。使用 `TypeDescriptor` 可以处理更复杂的转换场景，例如，当源和目标类型需要更具体的上下文信息时。

```java
public class ConversionServiceDemo {

    public static void main(String[] args) {
        // 创建 DefaultConversionService 实例
        ConversionService conversionService = new DefaultConversionService();

        // 使用 canConvert 检查转换是否可能
        if (conversionService.canConvert(Integer.class, String.class)) {
            System.out.println("Can convert from Integer to String");

            // 执行转换操作，将 Integer 转换为 String
            String converted = conversionService.convert(666, String.class);
            System.out.println("Converted: " + converted);
        }

        // 使用 TypeDescriptor 检查转换是否可能
        if (conversionService.canConvert(
                TypeDescriptor.valueOf(Integer.class),
                TypeDescriptor.valueOf(String.class))) {
            System.out.println("Can convert from Integer to String using TypeDescriptors");

            // 使用 TypeDescriptor 执行转换
            Object convertedWithTypeDescriptor = conversionService.convert(
                    888,
                    TypeDescriptor.valueOf(Integer.class),
                    TypeDescriptor.valueOf(String.class));
            System.out.println("Converted with TypeDescriptors: " + convertedWithTypeDescriptor);
        }
    }
}
```

运行结果发现，`ConversionService` 的高效性和实用性：它不仅能够确认是否可以执行特定的类型转换（如从 `Integer` 到 `String`），而且能够实际执行这些转换，将整数值成功转换为相应的字符串表示。

```java
Can convert from Integer to String
Converted: 666
Can convert from Integer to String using TypeDescriptors
Converted with TypeDescriptors: 888
```

### 八、与其他组件的关系

1. **Converter&ConverterFactory**

   + `ConversionService` 依赖于 `Converter` 和 `ConverterFactory` 接口的实现来执行具体的类型转换，允许我们通过实现这些接口并将自定义转换器注册到 `ConversionService` 中，来提供特定的转换逻辑，从而增强了类型转换的灵活性和扩展性。

2. **GenericConverter** 

   + `GenericConverter` 强化了 `ConversionService` 在 Spring 中的转换功能，提供灵活的多类型转换能力。通过将 `GenericConverter` 实现注册到 `ConversionService`，它使 `ConversionService` 能够处理更广泛和复杂的转换场景，包括那些需要额外上下文的转换任务。

3. **PropertyEditor**

   + `PropertyEditor` 是早期的类型转换工具，主要用于简单的属性编辑场景，但由于其非线程安全性和有限的灵活性，被 `ConversionService` 所取代。`ConversionService` 提供了一个现代、线程安全且灵活的类型转换机制，是现代 Spring 应用中进行类型转换的首选方法。虽然 `PropertyEditor` 仍得到支持，但在复杂和并发的应用环境中，`ConversionService` 更为合适。

4. **DataBinder**

   + 在数据绑定过程中，尤其是将表单数据绑定到对象或处理 MVC 控制器方法参数时，`DataBinder` 依赖 `ConversionService` 来转换属性值，这使得从一种类型到另一种类型的数据转换变得简洁高效。

5. **配置和属性管理**

   + `ConversionService` 在处理 Spring 配置属性时发挥重要作用，特别是在处理使用 `@Value` 注解注入的属性时，它能够将属性文件中的字符串值转换成更复杂的对象类型，简化了配置管理流程。
   
6. **BeanFactory&ApplicationContext**

   + 作为 Spring 容器中的一个 bean，`ConversionService` 可以在 `BeanFactory` 或 `ApplicationContext` 中注册，一旦注册，它便可被容器中的其他组件用于执行各种类型的转换操作，增强了整个 Spring 应用程序的类型转换能力和灵活性。

### 九、常见问题

1. **自定义转换器注册**

   + 在使用 `ConversionService` 时，正确注册自定义转换器是一个常见挑战。我们需要确保他们的 `Converter`、`ConverterFactory` 或 `GenericConverter` 实现被适当地添加到 `ConversionService` 中，否则预期的转换可能不会发生，这可能是由于配置错误或遗漏。

2. **转换器冲突**

   + 当存在多个适用于同一转换任务的转换器时，可能会引发冲突，我们需要理解和管理这些转换器的优先级，以确保正确的转换器被应用于特定情况。

3. **复杂类型转换**

   + 在处理复杂或泛型类型的转换时，我们需要实现能够处理这些复杂情况的逻辑，尤其是当转换逻辑需要额外的上下文信息时，例如在集合类型之间进行转换并同时处理元素类型的转换。

4. **与其他 Spring 组件的集成**

   + `ConversionService` 与 Spring MVC、数据绑定和验证等组件的正确集成可能在复杂场景中变得复杂，需要深入理解 Spring 配置和组件交互。

5. **转换精度和数据丢失**

   + 在进行数值类型转换等操作时，应注意转换精度和潜在的数据丢失问题，确保数据的准确性和完整性。