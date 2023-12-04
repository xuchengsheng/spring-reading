## Environment

- [Environment](#environment)
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

2. **ConfigurablePropertyResolver**
   + [ConfigurablePropertyResolver](/spring-env/spring-env-configurablePropertyResolver/README.md) 接口在Spring中关键作用是提供灵活的配置属性解析。它能从多种源读取并转换属性值，支持占位符解析以增强配置的动态性。接口提供类型转换，确保属性值符合期望格式。它还允许检查属性存在性，并处理默认值，增加健壮性。

### 三、基本描述

`Environment` 接口是 Spring 框架中的一个核心部分，它提供了一个统一的方式来访问各种外部化的配置数据，例如环境变量、JVM 系统属性、命令行参数、以及应用程序配置文件（如 properties 或 YAML 文件）。这个接口允许我们在运行时方便地获取和操作这些配置数据，同时也支持配置文件（Profiles）的概念，这使得在不同环境（如开发、测试、生产）下进行条件性的配置变得简单。通过 `Environment`，我们可以查询和操作属性值，检查属性的存在，以及处理多个属性源。

### 四、主要功能

1. **属性访问**

   + 它允许从不同的属性源（如环境变量、系统属性、配置文件等）访问属性值。提供了方法来检索字符串类型或类型安全的属性值。

2. **属性源管理**

   + `Environment` 抽象支持多个属性源，并按优先级顺序检索属性。这意味着可以从多个地方（如不同的配置文件和环境变量）加载属性，并根据需要覆盖它们。

3. **配置文件（Profiles）**

   + 支持配置文件，这是一种用于根据不同环境（如开发、测试、生产）来分隔配置的机制。它允许应用程序根据当前激活的配置文件来调整其行为。

4. **属性存在性检查**

   + 提供了方法来检查是否存在特定的属性，这有助于在属性可能不存在的情况下编写更健壮的代码。

5. **类型安全的属性访问**

   + 除了普通的属性访问方法外，`Environment` 还提供了类型安全的方法，允许直接将属性值转换为期望的类型。

6. **必需属性的访问**

   + 可以获取标记为必需的属性，如果这些属性不存在，将抛出异常，这有助于在启动时捕捉配置错误。

### 五、接口源码

`Environment` 接口是 Spring 应用程序环境的主要表现形式，重点关注两个方面：配置文件（profiles）和属性（properties）。配置文件是对一组逻辑分组的 Bean 定义的命名，只有在相应的配置文件被激活时才会注册这些 Bean。属性则是来自多种来源的配置数据，如文件、环境变量等。`Environment` 提供了一系列方法来激活和管理这些配置文件，以及方便地访问和解析这些属性。

```java
/**
 * 代表当前应用程序运行环境的接口。
 * 模型化应用环境的两个关键方面：<em>配置文件（profiles）</em> 和 <em>属性（properties）</em>。
 * 与属性访问相关的方法通过 {@link PropertyResolver} 超接口暴露。
 *
 * <p>一个 <em>配置文件</em> 是一组命名的、逻辑分组的 bean 定义，只有在给定的配置文件处于 <em>活跃</em> 状态时才会被容器注册。
 * 无论是在 XML 中定义还是通过注解定义，Beans 都可以被分配到一个配置文件中；
 * 有关语法细节，请参阅 spring-beans 3.1 schema 或 {@link org.springframework.context.annotation.Profile @Profile} 注解。
 * {@code Environment} 对象与配置文件相关的角色在于确定哪些配置文件（如果有的话）当前是 {@linkplain #getActiveProfiles 活跃的}，
 * 以及哪些配置文件（如果有的话）应该默认是活跃的。
 *
 * <p><em>属性</em> 在几乎所有应用程序中都扮演重要角色，可能来自多种来源：属性文件、JVM 系统属性、系统环境变量、JNDI、
 * servlet 上下文参数、临时 Properties 对象、Maps 等。环境对象与属性相关的角色在于为用户提供一个方便的服务接口，
 * 用于配置属性源并从中解析属性。
 *
 * <p>在 {@code ApplicationContext} 中管理的 Beans 可以注册为 {@link org.springframework.context.EnvironmentAware EnvironmentAware} 
 * 或 {@code @Inject} {@code Environment}，以查询配置文件状态或直接解析属性。
 *
 * <p>然而，在大多数情况下，应用程序级别的 Beans 不应该直接与 {@code Environment} 交互，
 * 而应该通过像 {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer 
 * PropertySourcesPlaceholderConfigurer} 这样的属性占位符配置器来替换 {@code ${...}} 属性值，
 * 这个配置器本身是 {@code EnvironmentAware}，并且从 Spring 3.1 开始，使用 {@code <context:property-placeholder/>} 时默认会注册。
 *
 * <p>必须通过 {@code ConfigurableEnvironment} 接口对环境对象进行配置，所有
 * {@code AbstractApplicationContext} 子类的 {@code getEnvironment()} 方法都返回此接口。
 * 请参阅 {@link ConfigurableEnvironment} Javadoc，了解在应用程序上下文 {@code refresh()} 之前操作属性源的示例。
 *
 * @author Chris Beams
 * @since 3.1
 * @see PropertyResolver
 * @see EnvironmentCapable
 * @see ConfigurableEnvironment
 * @see AbstractEnvironment
 * @see StandardEnvironment
 * @see org.springframework.context.EnvironmentAware
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#setEnvironment
 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
 */
public interface Environment extends PropertyResolver {

	/**
	 * 返回为此环境显式激活的配置文件集。配置文件用于有条件地注册 bean 定义，例如基于部署环境创建逻辑分组。
	 * 通过设置系统属性 {@linkplain AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 * "spring.profiles.active"} 或调用 {@link ConfigurableEnvironment#setActiveProfiles(String...)} 来激活配置文件。
	 * <p>如果没有明确指定活跃的配置文件，则任何 {@linkplain #getDefaultProfiles() 默认配置文件} 将自动激活。
	 * @see #getDefaultProfiles
	 * @see ConfigurableEnvironment#setActiveProfiles
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	String[] getActiveProfiles();

	/**
	 * 当没有明确设置活跃配置文件时，默认激活的配置文件集。
	 * @see #getActiveProfiles
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 */
	String[] getDefaultProfiles();

	/**
	 * 返回一个或多个给定配置文件是否活跃，或者在没有明确活跃配置文件的情况下，给定配置文件是否包含在默认配置文件集中。
	 * 如果配置文件以 '!' 开头，则逻辑被反转，即如果给定的配置文件 <em>不</em> 活跃，则方法将返回 {@code true}。
	 * 例如，{@code env.acceptsProfiles("p1", "!p2")} 将在 'p1' 配置文件活跃或 'p2' 配置文件不活跃时返回 {@code true}。
	 * @throws IllegalArgumentException 如果调用时没有参数，或者任何配置文件是 {@code null}、空的或只有空格
	 * @see #getActiveProfiles
	 * @see #getDefaultProfiles
	 * @see #acceptsProfiles(Profiles)
	 * @deprecated 从 5.1 版本开始，建议使用 {@link #acceptsProfiles(Profiles)}
	 */
	@Deprecated
	boolean acceptsProfiles(String... profiles);

	/**
	 * 返回 {@linkplain #getActiveProfiles() 活跃配置文件} 是否匹配给定的 {@link Profiles} 谓词。
	 */
	boolean acceptsProfiles(Profiles profiles);

}
```

### 六、主要实现

1. **AbstractEnvironment**

   + `Environment` 接口的抽象基类，提供了共通的实现机制，供其他具体实现类继承。

2. **StandardEnvironment**

   + 通用实现，处理系统属性和环境变量，适用于大多数标准应用程序。

3. **StandardServletEnvironment**

   + 针对 Servlet-based Web 应用程序，增加对 Servlet 上下文和配置参数的支持。

### 七、最佳实践

使用 Spring 的 `StandardEnvironment` 在 Java 程序中模拟配置文件的激活和属性访问。它设置并展示了激活的配置文件（"test"），默认配置文件（"dev"），并检查了特定配置文件（"test"）的激活状态，以及获取并打印了系统的 Java 版本。

```java
public class EnvironmentDemo {

    public static void main(String[] args) {
        // 设置系统属性以模拟 Spring 的配置文件功能
        System.setProperty("spring.profiles.default", "dev");
        System.setProperty("spring.profiles.active", "test");

        // 创建 StandardEnvironment 实例，用于访问属性和配置文件信息
        Environment environment = new StandardEnvironment();

        // 使用 getProperty 方法获取系统属性。这里获取了 Java 版本
        String javaVersion = environment.getProperty("java.version");
        System.out.println("java.version: " + javaVersion);

        // 获取当前激活的配置文件（profiles）
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("activeProfiles = " + String.join(",", activeProfiles));

        // 获取默认配置文件（当没有激活的配置文件时使用）
        String[] defaultProfiles = environment.getDefaultProfiles();
        System.out.println("defaultProfiles = " + String.join(",", defaultProfiles));

        // 检查是否激活了指定的配置文件。这里检查的是 'test' 配置文件
        boolean isDevProfileActive = environment.acceptsProfiles("test");
        System.out.println("acceptsProfiles('test'): " + isDevProfileActive);
    }
}
```

运行结果发现， `StandardEnvironment` 在模拟和管理 Spring 配置文件以及访问系统属性方面的有效性，特别是在不依赖于完整 Spring 应用程序上下文的场景中。

```java
java.version: 11
activeProfiles = test
defaultProfiles = dev
acceptsProfiles('test'): true
```

### 八、与其他组件的关系

1. **ApplicationContext**

   - `ApplicationContext`（Spring 容器）在启动时会创建和使用 `Environment` 实例来加载和处理配置文件（profiles）和属性（properties）。它提供了对 `Environment` 的访问和定制的接口，例如，可以通过 `ApplicationContext` 获取或替换默认的 `Environment` 实例。

2. **PropertySourcesPlaceholderConfigurer**

   - 这是一个 `BeanFactoryPostProcessor`，用于解析属性占位符（例如 `${property.name}`）。它与 `Environment` 一起工作，从 `Environment` 管理的属性源中解析属性值。

3. **@PropertySource** 和 **PropertySources**

   - `@PropertySource` 注解和 `PropertySources` 类用于声明属性源（如 .properties 或 .yml 文件）。这些属性源被添加到 `Environment` 中，以便从中读取配置属性。

4. **@ConfigurationProperties**

   - 这个注解通常用于将配置属性绑定到具有字段的类。`@ConfigurationProperties` 类可以利用 `Environment` 来获取所需的属性值，并将它们映射到类的字段上。

### 九、常见问题

1. **属性值未找到**

   + 如果尝试访问一个不存在的属性值，可能得到 `null` 或抛出异常。为解决这个问题，确保属性名称正确，并检查该属性是否已在正确的属性源中定义。

2. **配置文件未正确激活**

   + 当应用程序没有按预期激活特定的配置文件时，检查配置文件的激活方式（例如通过环境变量、JVM 参数等）是否正确，以及 `@Profile` 注解是否正确应用于相应的 Beans。

3. **属性类型转换错误**

   + 在将属性值转换为目标类型时出错时，确保属性值可以被正确地转换为所需的类型，并考虑定义自定义转换器。

4. **占位符未解析**

   + 当属性占位符（如 `${property.name}`）在应用程序中未被替换为实际的属性值时，检查 `PropertySourcesPlaceholderConfigurer` 或类似机制是否已正确配置在 Spring 上下文中。

5. **属性源优先级问题**

   + 遇到多个属性源中存在同名属性，导致获取到的值不是预期的那个问题时，了解和管理不同属性源的优先级，确保正确的属性源被优先读取。