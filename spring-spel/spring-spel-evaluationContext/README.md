## EvaluationContext

- [EvaluationContext](#evaluationcontext)
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

1. **Spring Framework 基础**

   + 理解 Spring Framework 的核心概念和基本用法对于理解 `SpEL` 是至关重要的。这包括理解依赖注入、控制反转、面向切面编程等概念。

2. **Spring Expression Language（`SpEL`）**
   + 熟悉 `SpEL` 的语法、功能和用法是必要的。这包括了解如何在 Spring 应用程序中使用 `SpEL` 表达式，如何在 XML 配置、注解、Spring Data Query 方法等各种场景下使用 `SpEL`。
   
3. **Spring 表达式解析器**

   + 了解 Spring 如何解析和执行 `SpEL` 表达式是至关重要的。这包括熟悉 `SpEL` 的解析器、编译器、评估上下文等组件的工作原理和用法。

### 三、基本描述

`EvaluationContext` 接口是 Spring Framework 中的一个关键接口，用于在运行时对 Spring 表达式语言（`SpEL`）表达式进行评估和处理。它提供了一个环境，用于管理 `SpEL` 表达式中的变量、函数、类型信息和其他上下文，以便在表达式求值时可以访问和操作这些信息。通过 `EvaluationContext`，我们可以定义和控制 `SpEL` 表达式的求值上下文，从而实现对应用程序对象图的动态访问和操作，以及实现诸如动态过滤、排序、计算等功能。

### 四、主要功能

1. **管理变量**
   + `EvaluationContext` 允许在 `SpEL` 表达式中定义和使用变量。它提供了方法来设置和获取变量的值，使得在表达式求值时可以访问这些变量。
   
2. **管理函数**

   + 除了支持基本的运算符和操作之外，`SpEL` 还可以调用自定义函数。`EvaluationContext` 提供了注册和管理函数的方法，以便在表达式中调用这些函数。

3. **类型转换和类型检查**

   + 在 `SpEL` 表达式求值过程中，可能涉及到不同类型的数据操作，如类型转换和类型检查。`EvaluationContext` 提供了方法来执行这些操作，确保表达式中的操作可以正确执行。

4. **对象导航和方法调用**

   + `EvaluationContext` 提供了方法来执行对象导航和方法调用。这使得在 `SpEL` 表达式中可以直接访问对象的属性和方法，从而实现对应用程序对象图的动态访问和操作。

5. **安全性设置**

   + `EvaluationContext` 允许设置安全性相关的属性，如是否允许访问私有字段和方法、是否启用安全访问等，以确保表达式的安全性。

### 五、接口源码

 `EvaluationContext` 接口提供了 `SpEL` 表达式的评估上下文，用于支持在运行时对 `SpEL` 表达式进行求值和处理。其中，`EvaluationContext` 接口包括获取和设置根对象、获取属性访问器、构造函数解析器、方法解析器、bean 解析器等组件的方法，这些组件在表达式求值过程中起着重要的作用。此外，接口还提供了类型定位器、类型转换器、类型比较器和运算符重载器等功能，用于处理表达式中的类型转换、类型比较和运算符重载。最后，接口允许设置和查找命名变量，使得在表达式求值过程中可以动态地引用和修改变量值。综合来看，`EvaluationContext` 接口为 `SpEL` 表达式提供了必要的运行时环境和支持，使得我们能够灵活地处理和求值表达式。

```java
/**
 * 表达式在评估上下文中执行。在表达式评估过程中遇到引用时，将在此上下文中解析这些引用。
 *
 * <p>有一个此 EvaluationContext 接口的默认实现：
 * {@link org.springframework.expression.spel.support.StandardEvaluationContext}，可以进行扩展，而不必手动实现所有内容。
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface EvaluationContext {

	/**
	 * 返回默认的根上下文对象，对未限定的属性/方法等应进行解析的对象。
	 * 在评估表达式时可以覆盖此对象。
	 */
	TypedValue getRootObject();

	/**
	 * 返回一个访问器列表，依次询问以读取/写入属性。
	 */
	List<PropertyAccessor> getPropertyAccessors();

	/**
	 * 返回一个解析器列表，依次询问以查找构造函数。
	 */
	List<ConstructorResolver> getConstructorResolvers();

	/**
	 * 返回一个解析器列表，依次询问以查找方法。
	 */
	List<MethodResolver> getMethodResolvers();

	/**
	 * 返回一个 bean 解析器，可以按名称查找 bean。
	 */
	@Nullable
	BeanResolver getBeanResolver();

	/**
	 * 返回一个类型定位器，可用于按短名称或完全限定名称查找类型。
	 */
	TypeLocator getTypeLocator();

	/**
	 * 返回一个类型转换器，可以将一个值从一种类型转换（或强制转换）为另一种类型。
	 */
	TypeConverter getTypeConverter();

	/**
	 * 返回一个类型比较器，用于比较对象对是否相等。
	 */
	TypeComparator getTypeComparator();

	/**
	 * 返回一个运算符重载器，可能支持超出标准类型集的数学运算。
	 */
	OperatorOverloader getOperatorOverloader();

	/**
	 * 将此评估上下文中的命名变量设置为指定的值。
	 * @param name 要设置的变量名称
	 * @param value 要放置在变量中的值
	 */
	void setVariable(String name, @Nullable Object value);

	/**
	 * 在此评估上下文中查找命名变量。
	 * @param name 要查找的变量名称
	 * @return 变量的值，如果未找到则返回 {@code null}
	 */
	@Nullable
	Object lookupVariable(String name);

}
```

### 六、主要实现

1. **StandardEvaluationContext**

   + `StandardEvaluationContext` 是 SpEL 默认的评估上下文实现，提供了对变量、函数、类型转换、类型检查等基本功能的支持。它是最常用的评估上下文实现，适用于大多数的 SpEL 使用场景。`StandardEvaluationContext` 也是其他上下文实现的基础。

2. **SimpleEvaluationContext**

   +  `SimpleEvaluationContext` 是 `StandardEvaluationContext` 的另一个简化版本，它减少了一些不太常用的功能，从而提供了更轻量级的评估上下文实现。虽然功能相对较少，但是对于某些简单的应用场景或者对性能要求较高的场景，使用 `SimpleEvaluationContext` 可能是更合适的选择。

3. **CacheEvaluationContext**

   + `CacheEvaluationContext` 是 `StandardEvaluationContext` 的子类，它在标准的评估上下文的基础上增加了对表达式求值结果的缓存支持。这意味着对于相同的表达式，如果输入不变，将不会重复计算结果，而是直接返回缓存的值，从而提高了性能。这对于那些计算开销较大的表达式可以提供显著的性能提升。

4. **MethodBasedEvaluationContext**

   + `MethodBasedEvaluationContext` 是 `StandardEvaluationContext` 的另一个子类，它专门用于在表达式中调用方法。与标准评估上下文不同的是，`MethodBasedEvaluationContext` 允许将目标对象的方法暴露给 `SpEL` 表达式，并在表达式中进行调用。这使得在 `SpEL` 表达式中可以直接访问对象的方法，从而更加灵活地处理数据。

### 七、最佳实践

使用 Spring Expression Language（`SpEL`）的 `EvaluationContext` 接口及其实现类 `StandardEvaluationContext` 来设置根对象、设置变量以及获取属性访问器、构造函数解析器、方法解析器、bean 解析器等上下文相关的信息。其中，通过创建一个自定义的根对象类 `MyRootObject` 并将其作为参数传递给 `StandardEvaluationContext` 构造函数来设置根对象，然后演示了如何设置变量、获取属性访问器、构造函数解析器等。

```java
public class EvaluationContextDemo {
    public static void main(String[] args) {
        // 创建评估上下文
        EvaluationContext context = new StandardEvaluationContext(new MyRootObject("Root Data"));

        // 获取根对象
        TypedValue root = context.getRootObject();
        System.out.println("根对象: " + root.getValue());

        // 设置变量
        context.setVariable("name", "spring-reading");
        System.out.println("变量'name'的值: " + context.lookupVariable("name"));

        // 获取属性访问器
        List<PropertyAccessor> propertyAccessors = context.getPropertyAccessors();
        System.out.println("属性访问器: " + propertyAccessors);

        // 获取构造函数解析器
        List<ConstructorResolver> constructorResolvers = context.getConstructorResolvers();
        System.out.println("构造函数解析器: " + constructorResolvers);

        // 获取方法解析器
        List<MethodResolver> methodResolvers = context.getMethodResolvers();
        System.out.println("方法解析器: " + methodResolvers);

        // 获取 bean 解析器
        BeanResolver beanResolver = context.getBeanResolver();
        System.out.println("bean 解析器: " + beanResolver);

        // 获取类型定位器
        TypeLocator typeLocator = context.getTypeLocator();
        System.out.println("类型定位器: " + typeLocator);

        // 获取类型转换器
        TypeConverter typeConverter = context.getTypeConverter();
        System.out.println("类型转换器: " + typeConverter);

        // 获取类型比较器
        TypeComparator typeComparator = context.getTypeComparator();
        System.out.println("类型比较器: " + typeComparator);

        // 获取运算符重载器
        OperatorOverloader operatorOverloader = context.getOperatorOverloader();
        System.out.println("运算符重载器: " + operatorOverloader);
    }

    // 定义根对象的类
    static class MyRootObject {
        private String data;

        public MyRootObject(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
```

运行结果发现，通过 `StandardEvaluationContext` 设置的评估上下文中的各种信息：根对象是 `EvaluationContextDemo` 类中定义的 `MyRootObject` 类的实例，变量 `'name'` 的值为 `'spring-reading'`，同时显示了使用的属性访问器、构造函数解析器、方法解析器、类型定位器、类型转换器、类型比较器以及运算符重载器等组件的具体实现类。

```properties
根对象: com.xcs.spring.EvaluationContextDemo$MyRootObject@735b478
变量'name'的值: spring-reading
属性访问器: [org.springframework.expression.spel.support.ReflectivePropertyAccessor@13c78c0b]
构造函数解析器: [org.springframework.expression.spel.support.ReflectiveConstructorResolver@7ca48474]
方法解析器: [org.springframework.expression.spel.support.ReflectiveMetho
        dResolver@b97c004]
bean 解析器: null
类型定位器: org.springframework.expression.spel.support.StandardTypeLocator@51565ec2
类型转换器: org.springframework.expression.spel.support.StandardTypeConverter@1c3a4799
类型比较器: org.springframework.expression.spel.support.StandardTypeComparator@131276c2
运算符重载器: org.springframework.expression.spel.support.StandardOperatorOverloader@26aa12dd
```

### 八、与其他组件的关系

1. **StandardEvaluationContext**

   + `StandardEvaluationContext` 是 `EvaluationContext` 接口的一个实现类，它提供了一个标准的评估上下文实现，用于在 SpEL 表达式求值过程中管理上下文信息。

2. **TypedValue**

   + `TypedValue` 类用于包装表达式求值的结果，它与 `EvaluationContext` 接口一起工作，用于在表达式求值过程中传递和处理值。

3. **PropertyAccessor**、**ConstructorResolver**、**MethodResolver**、**BeanResolver**

   + 这些接口和类都与 `EvaluationContext` 接口相关联，用于支持 `SpEL` 表达式中属性、构造函数、方法和 bean 的解析和访问。

4. **TypeLocator**、**TypeConverter**、**TypeComparator**、**OperatorOverloader**

   + 这些接口和类也与 `EvaluationContext` 接口相关联，用于支持 `SpEL` 表达式中类型相关的操作和比较。

5. **ExpressionParser**、**Expression**

   + `ExpressionParser` 接口用于解析表达式字符串并返回 `Expression` 对象，而 `Expression` 接口用于表示已解析的表达式，它们与 `EvaluationContext` 接口一起工作，用于实际的表达式求值过程。

### 九、常见问题

1. **如何创建和使用 `EvaluationContext` 实例？**

   - 通过 `StandardEvaluationContext` 或其子类来创建 `EvaluationContext` 实例，并使用其方法设置根对象、变量等上下文信息，然后将其传递给表达式求值器进行表达式求值。

2. **如何设置根对象和变量？**

   - 使用 `setRootObject` 方法设置根对象，使用 `setVariable` 方法设置变量。根对象是表达式求值的起点，而变量则提供了在表达式求值过程中使用的上下文信息。

3. **`EvaluationContext` 接口的实现类有哪些？它们有何不同？**

   -  `EvaluationContext` 实现类包括 `StandardEvaluationContext`、`SimpleEvaluationContext` 等。它们在功能复杂度、性能等方面可能有所不同，我们可以根据具体需求选择合适的实现类。

4. **`EvaluationContext` 与 `ExpressionParser`、`Expression` 之间的关系是什么？**

   - `ExpressionParser` 用于解析表达式字符串并返回 `Expression` 对象，而 `EvaluationContext` 则用于在表达式求值过程中提供上下文信息。`Expression` 对象与 `EvaluationContext` 实例一起用于实际的表达式求值过程。

5. **`EvaluationContext` 如何与 SpEL 中的属性、方法、构造函数解析器等组件协作？**

   - `EvaluationContext` 可以通过其方法获取属性访问器、方法解析器、构造函数解析器等组件，并在表达式求值过程中使用它们来解析和访问属性、方法、构造函数等。

6. **如何处理表达式求值过程中可能出现的异常？**

   - 在表达式求值过程中可能出现各种异常，如语法错误、类型转换错误等。可以通过捕获异常并进行适当的处理来处理这些异常，以确保程序的稳定性和健壮性。

