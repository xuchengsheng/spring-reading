## PropertyResolver

- [PropertyResolver](#propertyresolver)
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

1. **PropertySource**

   - [PropertySource](https://github.com/xuchengsheng/spring-reading/blob/master/spring-env/spring-env-propertySources/spring-env/spring-env-propertySource/README.md) 类是 Spring 框架中的一个关键抽象类，专门用于封装不同来源的配置数据，如文件、环境变量、系统属性等。它为这些配置源提供了一个统一的接口，使得可以以一致的方式访问各种不同类型的配置数据。这个类的核心是其 `getProperty(String name)` 方法，它根据提供的属性名来检索属性值。在 Spring 的环境抽象中，`PropertySource` 的实例可以被添加到 `Environment` 对象中，从而允许我们在应用程序中方便地访问和管理这些属性。

### 三、基本描述

`PropertyResolver` 接口是 Spring 框架的一个核心组件，专注于提供一套灵活且强大的机制来处理应用程序配置属性。它定义了一系列方法，用于访问和操纵来自各种源（例如属性文件、环境变量、JVM 参数）的属性值。

### 四、主要功能

1. **获取属性值**

   + 通过 `getProperty(key)` 方法可以获取给定键名的属性值。这是处理配置数据时最常用的功能。

2. **带默认值的属性获取**

   + 如果指定的属性键不存在，`getProperty(key, defaultValue)` 方法允许返回一个默认值。

3. **属性值类型转换**
+ `getProperty(key, targetType)` 和 `getProperty(key,targetType,defaultValue)` 方法使得可以将属性值转换成指定的数据类型，如从字符串转换为整数或布尔值。
  
4. **检查属性存在性**

   + `containsProperty(key)` 方法用于判断是否存在特定的属性键。

5. **获取必需属性**

   + `getRequiredProperty(key)` 和 `getRequiredProperty(key, targetType)` 方法用于获取必须存在的属性值。如果属性不存在，这些方法会抛出异常。

6. **解析占位符**

   + `resolvePlaceholders(text)` 方法支持解析字符串中的占位符，并用相应的属性值替换它们。这对于处理包含动态内容的配置文件非常有用。

### 五、接口源码

`PropertyResolver` 接口提供了一系列方法来处理属性值的解析，包括检查属性是否存在，获取属性值，支持默认值，以及类型转换，另外它还包含用于解析字符串中的占位符的方法，允许动态替换配置值。

```java
/**
 * 用于针对任何底层源解析属性的接口。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see Environment
 * @see PropertySourcesPropertyResolver
 */
public interface PropertyResolver {

    /**
     * 返回给定属性键是否可用于解析，即给定键的值是否不为 {@code null}。
     */
    boolean containsProperty(String key);

    /**
     * 返回与给定键关联的属性值，或者如果无法解析该键，则返回 {@code null}。
     * @param key 要解析的属性名称
     */
    @Nullable
    String getProperty(String key);

    /**
     * 返回与给定键关联的属性值，或者如果无法解析该键，则返回 {@code defaultValue}。
     * @param key 要解析的属性名称
     * @param defaultValue 如果找不到值，则返回的默认值
     */
    String getProperty(String key, String defaultValue);

    /**
     * 返回与给定键关联的属性值，或者如果无法解析该键，则返回 {@code null}。
     * @param key 要解析的属性名称
     * @param targetType 期望的属性值类型
     */
    @Nullable
    <T> T getProperty(String key, Class<T> targetType);

    /**
     * 返回与给定键关联的属性值，或者如果无法解析该键，则返回 {@code defaultValue}。
     * @param key 要解析的属性名称
     * @param targetType 期望的属性值类型
     * @param defaultValue 如果找不到值，则返回的默认值
     */
    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    /**
     * 返回与给定键关联的属性值（永远不会是 {@code null}）。
     * @throws IllegalStateException 如果无法解析该键
     */
    String getRequiredProperty(String key) throws IllegalStateException;

    /**
     * 返回与给定键关联的属性值，转换为给定的 targetType（永远不会是 {@code null}）。
     * @throws IllegalStateException 如果无法解析给定键
     */
    <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

    /**
     * 解析给定文本中的 ${...} 占位符，将它们替换为 {@link #getProperty} 解析的相应属性值。
     * 无法解析且没有默认值的占位符将被忽略并保持不变。
     * @param text 要解析的字符串
     * @return 解析后的字符串（永远不会是 {@code null}）
     * @throws IllegalArgumentException 如果给定的文本是 {@code null}
     */
    String resolvePlaceholders(String text);

    /**
     * 解析给定文本中的 ${...} 占位符，将它们替换为 {@link #getProperty} 解析的相应属性值。
     * 无法解析且没有默认值的占位符将导致抛出 IllegalArgumentException。
     * @return 解析后的字符串（永远不会是 {@code null}）
     * @throws IllegalArgumentException 如果给定文本是 {@code null} 或任何占位符无法解析
     */
    String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

}
```

### 六、主要实现

1. **`PropertySourcesPropertyResolver`**

   + 这是最常用的 `PropertyResolver` 实现之一。它使用 `PropertySource` 对象（可能包含多个，如环境变量、JVM 属性、配置文件等）来解析属性。

2. **`AbstractEnvironment`**

   + 虽然 `AbstractEnvironment` 本身不直接实现 `PropertyResolver` 接口，但它提供了访问 `PropertyResolver` 功能的接口。`AbstractEnvironment` 通过内部持有的 `PropertySourcesPropertyResolver` 实例来提供属性解析服务。

3. **`StandardEnvironment` 和 `ConfigurableEnvironment`**

   + 这些类扩展了 `AbstractEnvironment`，提供了更具体的环境配置。它们通过继承 `AbstractEnvironment` 间接提供 `PropertyResolver` 的功能。

### 七、最佳实践

使用  `PropertyResolver` 来演示配置属性的管理和解析。我们创建了一个包含应用信息的属性源，通过 `PropertySourcesPropertyResolver` 获取和检查属性，处理默认值，以及解析带有占位符的字符串。

```java
public class SimplePropertyResolverDemo {

    public static void main(String[] args) {
        // 创建属性源
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");
        properties.put("app.description", "This is a ${app.name} with version ${app.version}");

        MapPropertySource propertySource = new MapPropertySource("myPropertySource", properties);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(propertySource);

        // 使用 PropertySourcesPropertyResolver
        PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        // 获取属性
        String appName = propertyResolver.getProperty("app.name");
        String appVersion = propertyResolver.getProperty("app.version");
        System.out.println("获取属性 app.name: " + appName);
        System.out.println("获取属性 app.version: " + appVersion);

        // 检查属性是否存在
        boolean containsDescription = propertyResolver.containsProperty("app.description");
        boolean containsReleaseDate = propertyResolver.containsProperty("app.releaseDate");
        System.out.println("是否包含 'app.description' 属性: " + containsDescription);
        System.out.println("是否包含 'app.releaseDate' 属性: " + containsReleaseDate);

        // 带默认值的属性获取
        String appReleaseDate = propertyResolver.getProperty("app.releaseDate", "2023-11-30");
        System.out.println("带默认值的属性获取 app.releaseDate : " + appReleaseDate);

        // 获取必需属性
        String requiredAppName = propertyResolver.getRequiredProperty("app.name");
        System.out.println("获取必需属性 app.name: " + requiredAppName);

        // 解析占位符
        String appDescription = propertyResolver.resolvePlaceholders(properties.get("app.description").toString());
        System.out.println("解析占位符 app.description: " + appDescription);

        // 解析必需的占位符
        String requiredAppDescription = propertyResolver.resolveRequiredPlaceholders("App Description: ${app.description}");
        System.out.println("解析必需的占位符 : " + requiredAppDescription);
    }
}
```

运行结果发现，`PropertyResolver` 成功地从配置中获取了属性值、验证了属性的存在性、提供了默认值、解析了包含占位符的字符串，

```java
获取属性 app.name: Spring-Reading
获取属性 app.version: 1.0.0
是否包含 'app.description' 属性: true
是否包含 'app.releaseDate' 属性: false
带默认值的属性获取 app.releaseDate : 2023-11-30
获取必需属性 app.name: Spring-Reading
解析占位符 app.description: This is a Spring-Reading with version 1.0.0
解析必需的占位符 : App Description: This is a Spring-Reading with version 1.0.0
```

### 八、与其他组件的关系

1. **`Environment` 抽象**

   - `PropertyResolver` 是 `Environment` 接口的一部分，而 `Environment` 提供了一个更为全面的配置和属性管理接口。`Environment` 抽象了属性源（Property Sources），如系统环境变量、JVM 属性、配置文件等。

   - 在实际使用中，当我们操作 `Environment` 对象时，我们也在使用 `PropertyResolver` 的功能，因为 `Environment` 继承了 `PropertyResolver` 接口。

2. **`PropertySource(s)` 和 `PropertySourcesPropertyResolver`**

   - `PropertySource` 代表了一个属性的源头，比如一个 `.properties` 文件或者环境变量。`MutablePropertySources` 是一个包含多个 `PropertySource` 的容器。

   - `PropertySourcesPropertyResolver` 是 `PropertyResolver` 的具体实现，它可以解析由一个或多个 `PropertySource` 提供的属性。

3. **占位符解析**

   - `PropertyResolver` 提供了解析占位符（如 `${property.name}`）的能力，这在处理配置文件时特别有用。

   - 它与 Spring 的 `PropertyPlaceholderConfigurer` 或者 `@Value` 注解协同工作，用于将配置文件中的属性值注入到 Spring 管理的 bean 中。

4. **配置类和注解**

   - 在使用基于注解的配置类（如使用 `@Configuration` 注解的类）时，`PropertyResolver` 可以用来动态解析和注入属性值，特别是结合 `@PropertySource` 注解使用时。
   
   - 例如，可以使用 `@Value("${property.name}")` 注解来将属性值注入到配置类的字段或方法中。

5. **Profile 管理**

   - `PropertyResolver` 与 Spring Profiles 的概念紧密相连。通过 `PropertyResolver`，可以方便地访问和检查当前激活的 Profiles，这对于根据不同环境（开发、测试、生产等）来加载特定的配置非常有用。

### 九、常见问题

1. **属性值未找到**

   + 如果尝试解析不存在的属性，可以通过提前使用 `containsProperty` 方法来检查属性是否存在，以避免问题。同时，确认属性名是否正确，并确保属性源已经包含了对应的属性。

2. **属性类型转换错误**

   + 在将属性值转换为不同的类型时，确保属性值的格式与目标类型兼容。如果格式不匹配，可以使用类型转换服务（如 Spring 的 `ConversionService`）进行显式转换。

3. **占位符未解析**

   + 如果属性值中的 `${...}` 占位符没有被正确解析，确保使用了合适的 `PropertyResolver` 实现（如 `PropertySourcesPropertyResolver`），并且相关的属性源已经包含了占位符引用的属性。

4. **环境相关属性处理**

   + 在处理不同环境（开发、测试、生产）下的属性时，可以使用 Spring Profiles 来定义环境特定的属性。启动应用时，确保激活了正确的 Profile。

5. **配置文件加载问题**

   + 如果属性文件没有被正确加载，检查属性文件的路径和格式是否正确，并确保在 Spring 配置类中使用了 `@PropertySource` 注解来指定属性文件。

6. **使用 `@Value` 注解注入属性时出错**

   + 在使用 `@Value` 注解注入属性值时，确保 `PropertyPlaceholderConfigurer` 或 `PropertySourcesPlaceholderConfigurer` 被正确配置并加载到 Spring 容器中。