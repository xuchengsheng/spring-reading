## PropertySources

- [PropertySources](#propertysources)
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

✒️**作者**- Lex 📝**博客**- [掘金](https://juejin.cn/user/4251135018533068/posts) 📚**源码地址**- [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. **PropertySource**

   + [PropertySource](spring-env/spring-env-propertySource/README.md) 类是 Spring 框架中的一个关键抽象类，专门用于封装不同来源的配置数据，如文件、环境变量、系统属性等。它为这些配置源提供了一个统一的接口，使得可以以一致的方式访问各种不同类型的配置数据。这个类的核心是其 `getProperty(String name)` 方法，它根据提供的属性名来检索属性值。在 Spring 的环境抽象中，`PropertySource` 的实例可以被添加到 `Environment` 对象中，从而允许我们在应用程序中方便地访问和管理这些属性。

### 三、基本描述

`PropertySources` 是一个Spring框架中的接口，用于表示和管理一组属性源（PropertySource对象），这些属性源包含了应用程序环境中的配置数据。该接口提供了一系列方法来检索、添加、替换和删除这些属性源，允许开发者以统一的方式访问不同来源的配置信息，如环境变量、系统属性、配置文件等。

### 四、主要功能

1. **属性源管理**
   + 接口允许添加、移除和替换属性源。这使得可以在运行时动态地调整应用程序的配置。

2. **属性检索**
   + 提供了方法来检查特定的属性是否存在于任何已注册的属性源中。

3. **属性访问**
   + 允许从一系列的属性源中检索属性值。由于属性源是有序的，这种检索会按照特定的顺序进行，通常是按照属性源添加的顺序。

4. **属性源枚举**
   + 可以枚举所有注册的属性源，这对于调试和分析配置环境非常有用。

5. **层次化属性源**
   + 支持属性源的层次化，使得可以有优先级和覆盖机制，例如，一个环境变量可以覆盖同名的系统属性。

6. **属性源自定义**
   + 由于它是一个接口，用户可以实现自定义的 `PropertySources`，提供更特定的属性源管理策略。

### 五、接口源码

`PropertySources` 是一个在Spring框架中定义的接口，旨在作为一个容器，管理和封装一组 `PropertySource` 对象。这些对象代表了应用程序中的各种属性源，如环境变量、系统属性或配置文件。接口提供了流式访问这些属性源的功能，允许开发者以顺序流的形式对属性源进行操作。它还包括用于检查特定属性源是否存在的方法，以及根据名称检索属性源的能力

```java
/**
 * 包含一个或多个 {@link PropertySource} 对象的容器。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertySource
 */
public interface PropertySources extends Iterable<PropertySource<?>> {

    /**
     * 返回一个包含属性源的顺序 {@link Stream}。
     * @since 5.1
     */
    default Stream<PropertySource<?>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * 返回是否包含给定名称的属性源。
     * @param name 要找的属性源的名称
     */
    boolean contains(String name);

    /**
     * 返回给定名称的属性源，如果未找到则返回 null。
     * @param name 要找的属性源的名称
     */
    @Nullable
    PropertySource<?> get(String name);

}
```

### 六、主要实现

1. **MutablePropertySources**

   + 这是最常用的实现，提供了一个可修改的属性源集合。它允许添加、替换和移除属性源，是处理动态环境属性时的首选。

### 七、最佳实践

使用 Spring 的 `MutablePropertySources` 来管理应用配置属性的过程。它创建了一个属性源集合，向其中添加、替换和移除了不同的属性源，并展示了如何检查属性源的存在性。

```java
public class PropertySourcesDemo {

    public static void main(String[] args) {
        // 创建 MutablePropertySources 对象
        MutablePropertySources propertySources = new MutablePropertySources();

        // 创建两个 MapPropertySource 对象
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "value1");
        PropertySource<?> mapPropertySource1 = new MapPropertySource("config1", config1);

        Map<String, Object> config2 = new HashMap<>();
        config2.put("key2", "value2");
        PropertySource<?> mapPropertySource2 = new MapPropertySource("config2", config2);

        // 添加属性源到开头
        propertySources.addFirst(mapPropertySource1);
        // 添加属性源到末尾
        propertySources.addLast(mapPropertySource2);

        // 打印
        System.out.println("打印属性源");
        for (PropertySource<?> ps : propertySources) {
            System.out.printf("Name: %-10s || Source: %s%n", ps.getName(), ps.getSource());
        }
        System.out.println();

        // 替换属性源
        Map<String, Object> newConfig = new HashMap<>();
        newConfig.put("app.name", "Spring-Reading");
        newConfig.put("app.version", "1.0.0");
        PropertySource<?> newMapPropertySource = new MapPropertySource("config1", newConfig);
        propertySources.replace("config1", newMapPropertySource);

        // 打印替换后
        System.out.println("打印替换后的属性源");
        for (PropertySource<?> ps : propertySources) {
            System.out.printf("Name: %-10s || Source: %s%n", ps.getName(), ps.getSource());
        }
        System.out.println();

        // 检查是否包含属性源
        System.out.println("检查是否包含属性源 config2: " + propertySources.contains("config2"));

        // 移除属性源
        System.out.println("移除属性源 config2: " + propertySources.remove("config2"));

        // 再次检查是否包含属性源
        System.out.println("删除后是否包含属性源 config2: " + propertySources.contains("config2"));
    }
}
```

运行结果发现，`MutablePropertySources` 如何灵活地管理属性源，包括添加、替换和移除操作，以及如何查询属性源的存在性。

```java
打印属性源
Name: config1    || Source: {key1=value1}
Name: config2    || Source: {key2=value2}

打印替换后的属性源
Name: config1    || Source: {app.name=Spring-Reading, app.version=1.0.0}
Name: config2    || Source: {key2=value2}

检查是否包含属性源 config2: true
移除属性源 config2: MapPropertySource {name='config2'}
删除后是否包含属性源 config2: false
```

### 八、与其他组件的关系

1. **`Environment`**

   + `PropertySources` 是 `Environment` 接口的核心组成部分，负责提供应用程序的配置数据。在 `Environment` 中，`PropertySources` 被用来搜索和访问不同来源的属性值，如系统属性、环境变量和配置文件，从而实现统一的配置属性访问机制。

2. **`PropertySource`**

   + `PropertySources` 管理一组 `PropertySource` 对象，其中每个 `PropertySource` 代表了一个单独的属性源，如配置文件或环境变量集。`PropertySources` 为这些不同的属性源提供集中式的访问和管理能力。

3. **`PropertyResolver`**

   + `PropertySources` 与 `PropertyResolver` 接口紧密相关，后者提供属性值的解析功能，例如处理占位符。在处理和转换属性值时，`PropertySources` 通常与 `PropertyResolver` 结合使用，以提高属性管理的灵活性和效率。

4. **`ApplicationContext`**

   + 在 Spring 的 `ApplicationContext` 中，`PropertySources` 被用于配置和管理应用程序的环境属性。它与应用上下文交互，允许灵活定义和访问应用程序的配置设置，从而成为 Spring 应用配置的重要组成部分。

5. **`Profile`**

   + `PropertySources` 与 `Profile` 相关联，后者用于定义特定环境的配置。通过 `PropertySources`，可以存储和访问与特定 `Profile` 相关的属性，同时在应用启动时激活或禁用特定的 `Profile`，控制配置的加载。

### 九、常见问题

1. **属性值重复或覆盖**

   + 当多个属性源中定义了相同的属性时，可能会发生意外的覆盖。解决这个问题需要检查 `PropertySources` 中各个属性源的优先级和顺序，确保期望的属性源具有正确的优先级。

2. **属性值未找到**

   + 尝试获取不存在的属性值时可能会引发异常。解决这一问题的方法是确保所需属性已在相应属性源中定义，或使用默认值和条件检查来处理可能缺失的属性。

3. **环境依赖性问题**

   + 不同环境（如开发、测试、生产）可能需要不同的属性设置，如果混淆这些设置，可能会导致配置错误。使用 Spring Profiles 或条件注解区分不同环境的配置可以有效解决这一问题。

4. **属性类型不匹配**

   + 尝试将属性值转换为不匹配的数据类型时可能会发生类型转换异常。确保属性值与目标类型兼容，或使用自定义类型转换器，可以避免这种问题。

5. **属性源更新问题**

   + 在运行时动态更新属性值可能导致同步问题或更新不生效。使用 `MutablePropertySources` 并确保适当的同步和配置刷新，或考虑使用外部配置管理系统，可以解决这一问题。

6. **属性解析问题**

   + 处理占位符或表达式时可能出现解析错误。检查属性占位符的语法和解析性，确保所有相关的属性源已被正确加载和配置，可以帮助避免这类问题。