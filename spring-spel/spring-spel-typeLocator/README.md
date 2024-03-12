## TypeLocator

- [TypeLocator](#TypeLocator)
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

1. **Spring 表达式语言（SpEL）**

   + 了解 SpEL 的基础语法和用法是必要的，因为 `TypeLocator` 接口通常用于 SpEL 中，用于动态获取类型信息。

2. **反射（Reflection）**

   + 了解 Java 中的反射机制，包括 `Class` 类、`Method` 类、`Field` 类等，因为 `TypeLocator` 接口通常需要使用反射来查找和操作类型信息。

3. **设计模式**
+ 熟悉常见的设计模式，如工厂模式、策略模式等，这些设计模式在实现 `TypeLocator` 接口时可能会有所应用。

### 三、基本描述

`TypeLocator` 接口是 Spring Framework 中的关键接口之一，用于动态定位类型信息，在 Spring 表达式语言（SpEL）等场景中扮演重要角色，通过提供方法如`findType(String typeName)`和`hasType(String typeName)`，允许 SpEL 在运行时动态获取和检查类型信息，增强了 Spring 应用程序的灵活性和功能性。

### 四、主要功能

1. **查找类型信息**

   + 通过 `findType(String typeName)` 方法，根据给定的类型名称查找对应的类型信息，使得 SpEL 在运行时能够动态获取所需类型的信息。

2. **检查类型是否存在**

   + 通过 `hasType(String typeName)` 方法，可以检查是否存在给定名称的类型。这对于确定能否解析给定的类型很有用。

### 五、接口源码

`TypeLocator` 接口定义了一种用于定位类型信息的机制，其中包含一个抽象方法 `findType(String typeName)`，用于根据给定的类型名称查找对应的类型，并返回表示该类型的 `Class` 对象。

```java
/**
 * 实现此接口的类应能够定位类型。它们可以使用自定义的 {@link ClassLoader}，
 * 和/或以任何方式处理常见的包前缀（例如 {@code java.lang}）。
 *
 * <p>参见 {@link org.springframework.expression.spel.support.StandardTypeLocator}
 * 以获取示例实现。
 *
 * @author Andy Clement
 * @since 3.0
 */
@FunctionalInterface
public interface TypeLocator {

	/**
	 * 根据名称查找类型。名称可以是完全限定的，也可以不是（例如 {@code String} 或 {@code java.lang.String}）。
	 * @param typeName 要定位的类型
	 * @return 表示该类型的 {@code Class} 对象
	 * @throws EvaluationException 如果查找类型时出现问题
	 */
	Class<?> findType(String typeName) throws EvaluationException;

}

```

`StandardTypeLocator` 是一个简单的实现类，实现了 `TypeLocator` 接口，用于根据给定的类型名称查找对应的类型信息。它支持使用上下文 ClassLoader 和注册的导入前缀来定位类型，当找不到类型时会尝试使用注册的导入前缀来定位。

```java
/**
 * 一个简单的 {@link TypeLocator} 实现，它使用上下文 ClassLoader（或设置在其上的任何 ClassLoader）。
 * 它支持'well-known'包：如果找不到类型，则会尝试注册的导入来定位它。
 * 
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class StandardTypeLocator implements TypeLocator {

    @Nullable
    private final ClassLoader classLoader;

    private final List<String> knownPackagePrefixes = new ArrayList<>(1);

    /**
     * 为默认的 ClassLoader（通常是线程上下文 ClassLoader）创建一个 StandardTypeLocator。
     */
    public StandardTypeLocator() {
        this(ClassUtils.getDefaultClassLoader());
    }

    /**
     * 为给定的 ClassLoader 创建一个 StandardTypeLocator。
     * @param classLoader 要委托的 ClassLoader
     */
    public StandardTypeLocator(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
        // 类似于编写常规的 Java 代码，默认只知道 java.lang
        registerImport("java.lang");
    }

    /**
     * 注册一个新的导入前缀，用于搜索未限定类型时使用。
     * 期望的格式类似于 "java.lang"。
     * @param prefix 要注册的前缀
     */
    public void registerImport(String prefix) {
        this.knownPackagePrefixes.add(prefix);
    }

    /**
     * 从此定位器的导入列表中删除指定的前缀。
     * @param prefix 要移除的前缀
     */
    public void removeImport(String prefix) {
        this.knownPackagePrefixes.remove(prefix);
    }

    /**
     * 返回此 StandardTypeLocator 注册的所有导入前缀的列表。
     * @return 注册的导入前缀列表
     */
    public List<String> getImportPrefixes() {
        return Collections.unmodifiableList(this.knownPackagePrefixes);
    }

    /**
     * 查找（可能是未限定的）类型引用 - 首先使用原始类型名称，然后如果找不到类型名称，则尝试任何注册的前缀。
     * @param typeName 要定位的类型
     * @return 类型的 Class 对象
     * @throws EvaluationException 如果找不到类型
     */
    @Override
    public Class<?> findType(String typeName) throws EvaluationException {
        String nameToLookup = typeName;
        try {
            return ClassUtils.forName(nameToLookup, this.classLoader);
        }
        catch (ClassNotFoundException ey) {
            // 在放弃之前尝试任何已注册的前缀
        }
        for (String prefix : this.knownPackagePrefixes) {
            try {
                nameToLookup = prefix + '.' + typeName;
                return ClassUtils.forName(nameToLookup, this.classLoader);
            }
            catch (ClassNotFoundException ex) {
                // 可能是另一个前缀
            }
        }
        throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
    }
}
```

### 六、主要实现

1. **StandardTypeLocator**

   + `StandardTypeLocator` 类是实现了 `TypeLocator` 接口的简单实现，用于在给定类型名称时定位类型信息。

### 七、最佳实践

使用 Spring 表达式语言（SpEL）来获取类型信息。通过解析不同的表达式，包括获取特定类型的 Class 对象和比较不同类型的枚举值，展示了 SpEL 在类型定位和类型比较方面的功能。

```java
public class TypeLocatorDemo {
    public static void main(String[] args) {
        // 创建一个SpEL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        // 解析表达式获取 Date 类的 Class 对象
        Class dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
        System.out.println("dateClass = " + dateClass);

        // 解析表达式获取 String 类的 Class 对象
        Class stringClass = parser.parseExpression("T(String)").getValue(Class.class);
        System.out.println("stringClass = " + stringClass);

        // 解析表达式比较两个 RoundingMode 枚举值的大小
        boolean trueValue = parser.parseExpression("T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR").getValue(Boolean.class);
        System.out.println("trueValue = " + trueValue);
    }
}
```

运行结果，成功获取了 `java.util.Date` 和 `java.lang.String` 的 Class 对象，并且对 `java.math.RoundingMode` 枚举类型进行了比较，结果为真。

```properties
dateClass = class java.util.Date
stringClass = class java.lang.String
trueValue = true
```

### 八、与其他组件的关系

1. **Class**

   + `Class` 类是 Java 反射中的重要类，用于表示类的运行时信息。它提供了获取类的名称、方法、字段等信息的方法。在 `TypeLocator` 接口的实现中，可能会使用 `Class` 类来表示获取到的类信息。

2. **ClassLoader**

   + `ClassLoader` 类是 Java 中的一个关键类，用于动态加载 Java 类文件到 Java 虚拟机中。它负责加载类文件并生成对应的 `Class` 对象。在与 `TypeLocator` 接口相关的实现中，可能会使用 `ClassLoader` 来加载和获取类信息。

3. **StandardTypeLocator**

   + `TypeLocator`接口的实现类，是一个简单的类型定位器，通常用于在 SpEL 中查找类型信息。

### 九、常见问题

1. **如何实现自定义的 TypeLocator？**

   - 我们可能会想要根据特定需求实现自定义的 `TypeLocator` 接口，以满足特定的类型定位需求。在这种情况下，需要实现 `findType(String typeName)` 方法，根据给定的类型名称查找对应的类型信息，并根据需求处理类型的查找逻辑。

2. **如何处理不同的类型查找策略？**

   - 在某些情况下，可能需要根据不同的情况使用不同的类型查找策略。例如，可能需要根据不同的包前缀使用不同的类型查找逻辑，或者需要根据不同的条件动态切换类型查找策略。在这种情况下，我们需要考虑如何设计和实现灵活的类型查找策略。

3. **如何处理类型查找失败的情况？**

   - 当无法找到指定类型时，需要考虑如何处理类型查找失败的情况。可能的处理方式包括抛出异常、返回默认值或者尝试其他类型查找策略。我们需要根据具体情况选择合适的处理方式，并确保用户能够得到明确的反馈。