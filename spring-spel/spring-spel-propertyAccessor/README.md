## PropertyAccessor

- [PropertyAccessor](#propertyaccessor)
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

   + 了解 SpEL 的基本语法和功能，包括表达式的解析、运算符、变量、函数等。

2. **对象属性访问与反射机制** 

   + 了解如何使用 Java 的反射机制来访问和操作对象的属性，包括获取类的字段、调用对象的方法、设置字段的值等。

3. **设计模式** 

   + 理解常见的设计模式，如适配器模式、策略模式等，这些设计模式在实现自定义 `PropertyAccessor` 接口时可能会用到。

### 三、基本描述

`PropertyAccessor` 接口的作用在于定义了一套统一的属性访问规范，通过该接口可以实现对对象属性的读取和写入操作，使得 Spring 框架中的各个模块能够统一地访问对象的属性，从而实现更加灵活和可扩展的应用开发。通过实现这个接口，可以定制化对象属性的访问逻辑，以满足不同场景下的需求，例如在 Spring 表达式语言（SpEL）、数据绑定、AOP 编程等方面发挥作用。

### 四、主要功能

1. **属性读取** 

   + 提供了 `canRead()` 方法用于判断是否可以读取指定属性，以及 `read()` 方法用于读取属性的值。这使得可以通过统一的方式从对象中获取属性值。

2. **属性写入**

   + 提供了 `canWrite()` 方法用于判断是否可以写入指定属性，以及 `write()` 方法用于设置属性的值。这使得可以通过统一的方式向对象中设置属性值。

3. **自定义属性访问逻辑** 

   + 允许我们根据实际需求实现自定义的属性访问逻辑，例如通过自定义的 `PropertyAccessor` 实现类来访问对象的属性，从而实现特定的业务逻辑或行为。

4. **适配不同的对象结构** 

   + 可以针对不同的对象结构实现不同的 `PropertyAccessor` 实现类，以适应不同类型的对象，包括 JavaBean、Map、数组等。

### 五、接口源码

 `PropertyAccessor` 的接口，它用于读取和写入对象的属性。接口中包含了四个方法，分别用于判断是否能够读取属性、读取属性值、判断是否能够写入属性以及写入属性值。此外，接口中还包含了一个用于获取特定目标类数组的方法。该接口提供了一种统一的方式来访问对象的属性，使得可以实现自定义的属性访问逻辑，并能够适配不同的对象结构。

```java
/**
 * 属性访问器能够读取（可能写入）对象的属性。
 *
 * <p>这个接口没有限制，因此实现者可以自由地直接访问属性作为字段，或通过getter方法或其他方式访问属性。
 *
 * <p>解析器可以选择指定一个目标类的数组，用于调用它。然而，如果从{@link #getSpecificTargetClasses()}返回{@code null}，它将被调用
 * 以尝试解析所有属性引用，并有机会确定它是否可以读取或写入它们。
 *
 * <p>属性解析器被认为是有序的，并且每个都将依次被调用。影响调用顺序的唯一规则是，在{@link #getSpecificTargetClasses()}中直接命名目标类的任何解析器
 * 将在通用解析器之前首先被调用。
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface PropertyAccessor {

    /**
     * 返回此解析器应该被调用的类数组。
     * <p>返回{@code null}表示这是一个通用解析器，可以在任何类型上尝试解析属性。
     * @return 适用于此解析器的类数组（或{@code null}如果是通用解析器）
     */
    @Nullable
    Class<?>[] getSpecificTargetClasses();

    /**
     * 用于确定解析器实例是否能够访问指定目标对象上的指定属性。
     * @param context 尝试进行访问的评估上下文
     * @param target 要访问属性的目标对象
     * @param name 要访问的属性的名称
     * @return 如果此解析器能够读取属性，则为true
     * @throws AccessException 如果有任何问题确定是否可以读取属性
     */
    boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    /**
     * 用于从指定的目标对象中读取属性。
     * 只有当{@link #canRead}也返回{@code true}时才能成功。
     * @param context 尝试进行访问的评估上下文
     * @param target 要访问属性的目标对象
     * @param name 要访问的属性的名称
     * @return 包装读取的属性值和其类型描述符的TypedValue对象
     * @throws AccessException 如果有任何问题访问属性值
     */
    TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    /**
     * 用于确定解析器实例是否能够向指定目标对象上的指定属性写入。
     * @param context 尝试进行访问的评估上下文
     * @param target 要访问属性的目标对象
     * @param name 要访问的属性的名称
     * @return 如果此解析器能够写入属性，则为true
     * @throws AccessException 如果有任何问题确定是否可以写入属性
     */
    boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    /**
     * 用于向指定目标对象的属性写入。
     * 只有当{@link #canWrite}也返回{@code true}时才能成功。
     * @param context 尝试进行访问的评估上下文
     * @param target 要访问属性的目标对象
     * @param name 要访问的属性的名称
     * @param newValue 属性的新值
     * @throws AccessException 如果有任何问题写入属性值
     */
    void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue)
            throws AccessException;

}
```

### 六、主要实现

1. **BeanExpressionContextAccessor**

   +  允许在表达式中访问特定的对象和属性。通过 `BeanExpressionContextAccessor`，可以在 SpEL 表达式中直接访问上下文中的对象属性，这在某些场景下非常有用，例如在 Spring 的 `@Value` 注解中使用 SpEL 表达式读取配置属性。

2. **BeanFactoryAccessor** 

   + 允许在 SpEL 表达式中访问 Spring `BeanFactory` 中注册的 Bean 对象的属性。`BeanFactory` 是 Spring IoC 容器的核心接口，用于管理和创建 Bean。通过 `BeanFactoryAccessor`，可以在 SpEL 表达式中访问 IoC 容器中注册的 Bean，读取和设置其属性值，这为在 SpEL 中访问 Spring Bean 提供了便利。

3. **CompilablePropertyAccessor**

   + 允许对属性访问进行优化以提高性能。通过编译能力，可以在运行时对属性访问逻辑进行优化，从而减少不必要的计算和提高执行效率，这在某些性能要求较高的场景下非常有用。

4. **DataBindingPropertyAccessor** 

   + 允许在 Spring 应用程序中读取和写入属性。数据绑定是将用户输入绑定到后端数据模型的过程，在 Web 应用程序中广泛应用。`DataBindingPropertyAccessor` 提供了方便的方式来读取和设置对象的属性值，用于处理数据绑定过程中的属性访问需求。

5. **EnvironmentAccessor** 

   + `EnvironmentAccessor` 用于从 Spring 环境中访问属性，例如读取配置文件中的属性值。Spring 的环境抽象提供了一种统一的方式来访问应用程序的环境属性，包括系统属性、环境变量、配置文件等。通过 `EnvironmentAccessor`，可以在 SpEL 表达式中直接访问 Spring 环境中的属性值，从而实现动态的配置和属性注入。

6. **JspPropertyAccessor**

   + 允许在 JSP 页面中使用 SpEL 表达式，并在 JSP 标签中访问对象的属性。JSP 是一种常见的 Web 视图技术，与 SpEL 结合使用可以实现更加灵活和动态的页面渲染。通过在 JSP 中使用 SpEL 表达式，并在自定义标签（如 `eval` 标签）内访问对象的属性，可以实现更加灵活和可配置的页面逻辑。

7. **MapAccessor** 

   + `MapAccessor` 用于在 SpEL 表达式中直接访问 Map 对象中的键值对。Map 是一种常见的数据结构，用于存储键值对的集合。通过 `MapAccessor`，可以在 SpEL 表达式中直接访问 Map 对象中的键值对，读取和设置其属性值，这为在 SpEL 中操作 Map 提供了便利。

8. **OptimalPropertyAccessor** 

   + `OptimalPropertyAccessor` 提供了属性访问的优化实现，通常在 `ReflectivePropertyAccessor` 内部使用，用于提高属性访问的性能。通过对属性访问逻辑的优化，可以减少不必要的计算和提高执行效率，从而更加高效地访问对象的属性值。

### 七、最佳实践

创建了一个 SpEL 表达式解析器对象 `ExpressionParser`，然后创建了一个 `StandardEvaluationContext` 对象作为 SpEL 表达式的上下文。在上下文中设置了一个名为 `myBean` 的变量，其值为一个 `MyBean` 对象。接着，通过 `parser.parseExpression("#myBean.name")` 解析了一个 SpEL 表达式，该表达式表示访问 `myBean` 对象的 `name` 属性。最后，通过 `getValue(context, String.class)` 方法获取了属性值，并将其输出。

```java
public class PropertyAccessorDemo {
    public static void main(String[] args)  {
        // 创建一个SpEL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("myBean",new MyBean("spring-reading"));

        // 解析SpEL表达式，并使用构造函数实例化对象
        String name = parser.parseExpression("#myBean.name").getValue(context, String.class);

        System.out.println("name = " + name);
    }
}
```

 `MyBean` 类定义了一个简单的 Java Bean，具有一个名为 `name` 的私有字段和相应的 getter 和 setter 方法。

```java
public class MyBean {

    private String name;

    public MyBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
```

运行结果，通过 SpEL 表达式解析器访问了对象的属性，并得到了属性值 "spring-reading"。

```properties
name = spring-reading
```

### 八、与其他组件的关系

1. **Spring 表达式语言（SpEL）** 

   + 在 SpEL 中，`PropertyAccessor` 接口被用于访问对象的属性。SpEL 提供了一种在运行时动态求值表达式的机制，通过 `PropertyAccessor` 接口可以实现对对象属性的读写操作。

2. **Spring 数据绑定** 

   + 在 Spring 中，数据绑定是一种常见的操作，用于将用户提交的数据绑定到后端数据模型上。`PropertyAccessor` 接口提供了一种统一的方式来访问对象的属性，可以在数据绑定过程中使用。

3. **Spring AOP 编程** 

   + 在 Spring 的面向切面编程（AOP）中，`PropertyAccessor` 接口可以用于访问被增强对象的属性。通过 AOP 可以实现对被增强对象的属性进行拦截、修改等操作。

4. **Spring IOC 容器** 

   + 在 Spring 的控制反转（IoC）容器中，`PropertyAccessor` 接口可以用于访问注册在容器中的 Bean 对象的属性。通过 IoC 容器，可以方便地管理和访问对象的属性。

5. **其他自定义组件** 

   + 除了以上提到的场景之外，`PropertyAccessor` 接口还可以被自定义组件使用。例如，在自定义的框架或库中，可以使用 `PropertyAccessor` 接口来实现对对象属性的访问操作，以实现特定的功能或逻辑。

### 九、常见问题

1. **属性访问权限问题** 

   + 在使用 `PropertyAccessor` 接口时，可能会遇到对象的属性访问权限不足的问题，导致无法读取或写入属性。这可能涉及到对象的访问控制和权限设置。

2. **属性命名问题** 

   + 在访问对象的属性时，可能会出现属性命名不一致的问题，例如大小写不匹配、拼写错误等，导致无法正确访问属性。

3. **属性类型转换问题** 

   + 在进行属性读取或写入时，可能会遇到属性类型转换失败的问题，例如尝试将一个字符串值赋给一个整数类型的属性，导致转换异常。

4. **上下文环境配置问题** 

   + 在使用 `PropertyAccessor` 接口时，需要提供一个合适的上下文环境（如 `EvaluationContext`），可能会出现配置错误或环境设置不当的问题，导致属性访问失败。

5. **异常处理问题** 

   + 在属性访问过程中，可能会出现各种异常情况，例如空指针异常、访问权限异常等，需要进行适当的异常处理以保证程序的稳定性和可靠性。