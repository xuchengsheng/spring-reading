## ConfigurablePropertyResolver

- [ConfigurablePropertyResolver](#configurablepropertyresolver)
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

1. **PropertyResolver**
   + [PropertyResolver](/spring-env/spring-env-propertyResolver/README.md) 接口是 Spring 框架的一个核心组件，专注于提供一套灵活且强大的机制来处理应用程序配置属性。它定义了一系列方法，用于访问和操纵来自各种源（例如属性文件、环境变量、JVM 参数）的属性值。

### 三、基本描述

`ConfigurablePropertyResolver` 接口在Spring中关键作用是提供灵活的配置属性解析。它能从多种源读取并转换属性值，支持占位符解析以增强配置的动态性。接口提供类型转换，确保属性值符合期望格式。它还允许检查属性存在性，并处理默认值，增加健壮性。

### 四、主要功能

1. **属性值解析**

   + 从各种属性源（如配置文件、环境变量等）中读取并解析属性值。

2. **占位符解析**

   + 支持在属性值中使用占位符（如`${property.name}`），并解析这些占位符为实际的配置值，提升配置的动态性和灵活性。

3. **类型转换**

   + 提供了将属性值从一种类型转换为另一种类型的功能，例如，从字符串转换为整数、布尔值等。

4. **默认值处理**

   + 允许为属性指定默认值，这在配置项可能缺失的情况下特别有用。

5. **属性存在性检查**

   + 提供方法来检查特定属性是否存在，这有助于在配置项可能缺失时采取相应的措施。

6. **属性源管理**

   + 管理不同的属性源，使得可以根据当前应用程序的运行环境灵活调整配置。

### 五、接口源码

`ConfigurablePropertyResolver` 接口提供了一系列用于管理和处理配置属性的方法。允许自定义属性值的类型转换服务，管理占位符的前缀和后缀，处理默认值分隔符，以及设置是否忽略无法解析的嵌套占位符。

```java
/**
 * 配置接口，大多数 {@link PropertyResolver} 类型都应实现此接口。
 * 提供访问和自定义在属性值类型转换时使用的 {@link org.springframework.core.convert.ConversionService ConversionService} 的功能。
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

    /**
     * 返回用于执行属性类型转换时的 {@link ConfigurableConversionService}。
     * 可配置的转换服务允许方便地添加和移除单个 {@code Converter} 实例。
     * 
     * @return 当前使用的转换服务
     */
    ConfigurableConversionService getConversionService();

    /**
     * 设置在执行属性类型转换时使用的 {@link ConfigurableConversionService}。
     * 注意：作为完全替换 ConversionService 的替代方案，考虑通过 getConversionService() 添加或移除单个 {@code Converter} 实例。
     */
    void setConversionService(ConfigurableConversionService conversionService);

    /**
     * 设置该解析器替换的占位符的前缀。
     */
    void setPlaceholderPrefix(String placeholderPrefix);

    /**
     * 设置该解析器替换的占位符的后缀。
     */
    void setPlaceholderSuffix(String placeholderSuffix);

    /**
     * 指定分隔该解析器替换的占位符及其关联默认值的字符。
     * 如果不处理特殊字符作为值分隔符，则为 {@code null}。
     */
    void setValueSeparator(@Nullable String valueSeparator);

    /**
     * 设置当遇到无法解析的嵌套占位符时是否抛出异常。
     * {@code false} 表示严格解析，即会抛出异常。
     * {@code true} 表示未解析的嵌套占位符应以其未解析的 ${...} 形式通过。
     */
    void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

    /**
     * 指定必须存在的属性，通过 {@link #validateRequiredProperties()} 方法进行验证。
     */
    void setRequiredProperties(String... requiredProperties);

    /**
     * 验证通过 {@link #setRequiredProperties} 设置的每个属性是否存在并解析为非 {@code null} 值。
     * 如果任何必需的属性无法解析，则抛出 MissingRequiredPropertiesException 异常。
     */
    void validateRequiredProperties() throws MissingRequiredPropertiesException;

}
```

### 六、主要实现

1. **`AbstractPropertyResolver`**

   + 这是一个抽象基类，为 `ConfigurablePropertyResolver` 接口的大部分通用实现提供了基础。它实现了大多数方法，但作为一个抽象类，不能直接实例化。

2. **`PropertySourcesPropertyResolver`**

   + 这个类是 `ConfigurablePropertyResolver` 的一个具体实现，它主要用于处理基于 `PropertySources` 的属性解析。`PropertySources` 是 Spring 环境抽象中的一部分，用于表示配置数据的源，如环境变量、JVM属性、配置文件等。

### 七、最佳实践

使用 Spring 的 `ConfigurablePropertyResolver` 接口来管理和解析配置属性。我们首先创建并配置了属性源，接着实例化了 `PropertySourcesPropertyResolver` 作为属性解析器。在此基础上，代码设置了属性值的转换服务、定义了占位符的前后缀、配置了默认值分隔符，并处理了未解析占位符的情况。此外，还指定并验证了必需的属性，最后读取并输出了配置属性值。

```java
public class ConfigurablePropertyResolverDemo {

    public static void main(String[] args) {
        // 创建属性源
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");

        MapPropertySource propertySource = new MapPropertySource("myPropertySource", properties);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(propertySource);

        // 创建 ConfigurablePropertyResolver
        PropertySourcesPropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        // 设置和获取转换服务
        ConfigurableConversionService conversionService = new DefaultConversionService();
        propertyResolver.setConversionService(conversionService);

        // 设置占位符前后缀
        propertyResolver.setPlaceholderPrefix("${");
        propertyResolver.setPlaceholderSuffix("}");

        // 设置默认值分隔符
        propertyResolver.setValueSeparator(":");

        // 设置未解析占位符的处理方式
        propertyResolver.setIgnoreUnresolvableNestedPlaceholders(true);

        // 设置并验证必需的属性
        propertyResolver.setRequiredProperties("app.name", "app.version");
        propertyResolver.validateRequiredProperties();

        // 读取属性
        String appName = propertyResolver.getProperty("app.name");
        String appVersion = propertyResolver.getProperty("app.version", String.class, "Unknown Version");
        System.out.println("获取属性 app.name: " + appName);
        System.out.println("获取属性 app.version: " + appVersion);
    }
}
```

运行结果发现，`PropertySourcesPropertyResolver` 能够正确地从给定的属性源中解析出属性值，并且代码中的属性源配置和属性解析器的使用是正确的。

```java
获取属性 app.name: Spring-Reading
获取属性 app.version: 1.0.0
```

### 八、与其他组件的关系

1. **`Environment` **

   - `Environment` 接口继承自 `PropertyResolver`，而 `ConfigurableEnvironment` 是 `Environment` 的子接口，提供了对属性解析器的配置能力。在 Spring 中，`Environment` 用于封装所有的配置属性，包括系统属性、环境变量和应用配置文件。

   - `ConfigurablePropertyResolver` 提供了更灵活的属性解析和转换功能，它允许自定义属性解析的行为，如设置占位符的前后缀、类型转换等。

2. **`PropertySources` **

   - `PropertySources` 是一个包含多个 `PropertySource` 对象的容器，代表不同的属性源，如配置文件、环境变量、命令行参数等。

   - `ConfigurablePropertyResolver` 通常与 `PropertySources` 一起使用，用于从这些属性源中解析属性值。

3. **`PropertySource` **

   - `PropertySource` 是单个属性源的抽象，如一个配置文件或环境变量集。

   - `ConfigurablePropertyResolver` 使用 `PropertySource`（通过 `PropertySources`）来获取具体的属性值。

4. **`ConversionService` **

   - `ConversionService` 在 Spring 中用于类型转换，将一种类型的值转换为另一种类型。

   - `ConfigurablePropertyResolver` 可以配置自定义的 `ConversionService`，以控制属性值的转换方式，这在处理非字符串类型的配置属性时特别有用。

5. **`Profile` 和 `@Conditional` 注解**

   - Spring 中的配置文件和组件可以根据不同的环境（开发、测试、生产等）激活不同的配置。

   - `ConfigurablePropertyResolver` 可以用来检查哪些配置文件或 `Profile` 被激活，以及确定哪些条件注解（如 `@Conditional`）应该满足。

### 九、常见问题

1. **占位符无法解析**

   + 如果在属性值中使用的占位符（如 `${property.name}`）无法正确解析，应确保占位符的前缀和后缀设置正确，并且相关属性已在属性源中定义。

2. **类型转换错误**

   + 当遇到类型转换异常时，检查是否为 `ConfigurablePropertyResolver` 设置了正确的 `ConversionService` 并注册了必要的类型转换器。

3. **属性值覆盖问题**

   + 如果多个属性源包含相同名称的属性可能导致属性值覆盖，需要了解并管理属性源的优先级，确保以正确的顺序将它们添加到环境中。

4. **处理默认值时的混淆**

   + 使用带有默认值的方法（如 `getProperty(String, Class, T defaultValue)`）时，要清楚地区分属性源中的空值和未定义的属性，避免混淆。

5. **必需属性缺失**

   + 使用 `validateRequiredProperties` 方法时，如果因为缺少必需属性而抛出异常，应在应用启动或配置阶段检查并确保所有必需的属性都已在属性源中定义。

6. **环境和配置不一致**

   + 在不同环境（开发、测试、生产）下遇到配置不一致的问题，可以通过使用 Spring Profiles 来管理不同环境的配置，并确保每个环境都有适当的配置文件和属性设置。

7. **并发问题**

   + 在多线程环境下使用 `ConfigurablePropertyResolver` 时，为了避免线程安全问题，应保证对属性源的访问是线程安全的，或在应用启动时预先解析所有必需的属性。