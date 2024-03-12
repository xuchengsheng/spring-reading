## ConstructorResolver

- [ConstructorResolver](#ConstructorResolver)
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

1. **理解构造函数（Constructor）**

   + 构造函数是用于创建类的实例的特殊方法。了解构造函数的概念、使用方式以及与普通方法的区别是很重要的。

2. **反射（Reflection）**

   + 反射是Java语言的一个特性，允许程序在运行时检查和操作类、对象、方法和属性。学习如何使用`java.lang.reflect`包中的类来获取和操作构造函数是必要的。

3. **Spring表达式语言（SpEL）**

   + `ConstructorResolver`通常与Spring表达式语言（SpEL）一起使用。因此，了解SpEL的基本语法和用法是很有帮助的。

4. **设计模式**

   + 了解一些设计模式，特别是创建型模式（如工厂模式、建造者模式等），可以帮助理解对象创建的不同方法和策略。

### 三、基本描述

`ConstructorResolver`接口是Spring框架中用于解析和执行构造函数的核心接口之一，它允许在运行时动态地确定和调用对象的构造函数。该接口定义了一个`resolve(ConstructorExecutor, TypedValue[])`方法，该方法用于根据给定的构造函数执行器和参数来解析构造函数。通过实现`ConstructorResolver`接口，Spring能够以灵活和动态的方式实现对象的实例化，从而使得依赖注入和对象创建更加灵活和可定制化。在Spring框架中的实现类中，通常会利用反射机制来动态地实例化对象，并根据对象的类型和参数列表来选择合适的构造函数进行调用。

### 四、主要功能

1. **解析构造函数**

   + 接口定义了一个方法`resolve(ConstructorExecutor, TypedValue[])`，用于解析给定的构造函数。这意味着可以根据传入的参数和构造函数的签名来确定要调用的构造函数。

2. **执行构造函数**

   + 一旦构造函数被解析，接口负责调用相应的构造函数以创建对象实例。这涉及到创建实例、传递参数以及处理构造函数的执行。

3. **支持动态对象实例化**

   + `ConstructorResolver`接口使得在运行时能够动态地选择合适的构造函数实例化对象，从而提供了灵活性和可定制性。

### 五、接口源码

`ConstructorResolver`接口定义了一个方法`resolve`，用于在给定的上下文中确定特定类型的合适构造函数，该构造函数能够处理指定的参数类型列表。接口返回一个`ConstructorExecutor`，可以用于调用找到的构造函数。

```java
/**
 * 构造函数解析器尝试定位一个构造函数，并返回一个ConstructorExecutor，
 * 该Executor可以用于调用该构造函数。ConstructorExecutor将被缓存，
 * 但如果它“过时”，则会重新调用解析器。
 *
 * @author Andy Clement
 * @since 3.0
 */
@FunctionalInterface
public interface ConstructorResolver {

    /**
     * 在提供的上下文中确定指定类型上可以处理指定参数的合适构造函数。
     * 返回一个ConstructorExecutor，可以用于调用该构造函数（如果找不到构造函数，则返回null）。
     *
     * @param context       当前的评估上下文
     * @param typeName      要查找构造函数的类型
     * @param argumentTypes 构造函数必须能够处理的参数
     * @return 一个ConstructorExecutor，可以调用该构造函数，如果找不到则返回null
     * @throws AccessException 访问异常
     */
    @Nullable
    ConstructorExecutor resolve(EvaluationContext context, String typeName, List<TypeDescriptor> argumentTypes)
            throws AccessException;

}
```

`ReflectiveConstructorResolver`是Spring框架中的一个实现类，用于通过反射机制来定位应该被调用的构造函数。它能够根据提供的参数类型列表，对目标类的构造函数进行匹配，并选择合适的构造函数进行实例化。在匹配过程中，可能会发生三种类型的匹配：完全匹配、不完全匹配和需要类型转换的匹配。根据匹配结果，`ReflectiveConstructorResolver`将返回一个`ConstructorExecutor`对象，用于执行选定的构造函数。

```java
/**
 * 使用反射来定位应该被调用的构造函数的构造函数解析器。
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ReflectiveConstructorResolver implements ConstructorResolver {

    /**
     * 在类型上定位一个构造函数。可能会出现三种类型的匹配:
     * <ol>
     * <li>完全匹配，其中参数的类型与构造函数的类型相匹配
     * <li>不完全匹配，其中我们要查找的类型是构造函数上定义的类型的子类型
     * <li>匹配，其中我们能够将参数转换成构造函数预期的类型，根据已注册的类型转换器。
     * </ol>
     */
    @Override
    @Nullable
    public ConstructorExecutor resolve(EvaluationContext context, String typeName, List<TypeDescriptor> argumentTypes)
            throws AccessException {

        try {
            // 获取类型转换器和类类型
            TypeConverter typeConverter = context.getTypeConverter();
            Class<?> type = context.getTypeLocator().findType(typeName);
            Constructor<?>[] ctors = type.getConstructors();

            // 按参数数量排序构造函数
            Arrays.sort(ctors, Comparator.comparingInt(Constructor::getParameterCount));

            // 初始化匹配变量
            Constructor<?> closeMatch = null;
            Constructor<?> matchRequiringConversion = null;

            // 遍历构造函数
            for (Constructor<?> ctor : ctors) {
                int paramCount = ctor.getParameterCount();
                List<TypeDescriptor> paramDescriptors = new ArrayList<>(paramCount);
                for (int i = 0; i < paramCount; i++) {
                    paramDescriptors.add(new TypeDescriptor(new MethodParameter(ctor, i)));
                }
                ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
                if (ctor.isVarArgs() && argumentTypes.size() >= paramCount - 1) {
                    // 处理可变参数的匹配
                    matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
                } else if (paramCount == argumentTypes.size()) {
                    // 处理普通参数的匹配
                    matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
                }
                if (matchInfo != null) {
                    if (matchInfo.isExactMatch()) {
                        return new ReflectiveConstructorExecutor(ctor);
                    } else if (matchInfo.isCloseMatch()) {
                        closeMatch = ctor;
                    } else if (matchInfo.isMatchRequiringConversion()) {
                        matchRequiringConversion = ctor;
                    }
                }
            }

            // 返回匹配的构造函数执行器
            if (closeMatch != null) {
                return new ReflectiveConstructorExecutor(closeMatch);
            } else if (matchRequiringConversion != null) {
                return new ReflectiveConstructorExecutor(matchRequiringConversion);
            } else {
                return null;
            }
        } catch (EvaluationException ex) {
            throw new AccessException("Failed to resolve constructor", ex);
        }
    }
}
```

### 六、主要实现

+ **ReflectiveConstructorResolver**

  + 用于解析和执行构造函数的主要实现类。通过利用Java的反射机制，它能够动态地确定并调用类的构造函数，从而实现对象的实例化。

### 七、最佳实践

创建了一个`SpelExpressionParser`对象，它是Spring表达式语言的解析器。然后，我们使用SpEL表达式`"new com.xcs.spring.MyBean('spring-reading')"`来创建一个`MyBean`对象，参数为`"spring-reading"`。接着，我们通过调用`getValue(MyBean.class)`方法来获取实例化后的`MyBean`对象。最后，我们打印输出了这个实例化的`MyBean`对象。

```java
public class ConstructorResolverDemo {
    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();
        MyBean myBean = parser.parseExpression("new com.xcs.spring.MyBean('spring-reading')").getValue(MyBean.class);
        System.out.println(myBean);
    }
}
```

定义了一个简单的Java Bean，名为`MyBean`，具有一个私有属性`name`和一个带参构造函数、以及相应的getter和setter方法。此外，还重写了`toString()`方法，以便在打印对象时输出其属性值。

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

运行结果，成功地创建了一个`MyBean`对象，并将其属性`name`设置为`"spring-reading"`。

```properties
MyBean{name='spring-reading'}
```

### 八、与其他组件的关系

1. **EvaluationContext**

   + `ConstructorResolver`接口通常用于解析构造函数的过程中需要一个评估上下文，用于提供解析过程中所需的信息和环境，例如变量、函数、类型等。在Spring框架中，通常使用`EvaluationContext`接口或其实现类来表示评估上下文。

2. **TypedValue**

   + `ConstructorResolver`接口中的方法可能需要用到`TypedValue`对象，该对象用于表示表达式中的值，并提供了对值的类型信息的访问。在SpEL中，`TypedValue`通常用于表示表达式中的字面值、变量值等。

3. **ConstructorExecutor**

   + `ConstructorResolver`接口的实现类通常会返回一个`ConstructorExecutor`对象，该对象用于执行已解析的构造函数。`ConstructorExecutor`接口定义了一个`execute`方法，用于执行构造函数并返回结果对象。

4. **SpelExpressionParser**

   + `SpelExpressionParser`是Spring框架中用于解析SpEL表达式的核心类之一。在上下文中使用`SpelExpressionParser`来解析表达式时，可能会涉及到`ConstructorResolver`接口的实现类，用于处理表达式中的构造函数引用。

5. **ReflectiveConstructorResolver**

   + `ReflectiveConstructorResolver`是Spring框架中`ConstructorResolver`接口的主要实现类之一。它使用Java的反射机制来动态地解析和执行构造函数。

### 九、常见问题

1. **如何处理构造函数参数的类型匹配问题？**

   - `ConstructorResolver`接口的实现类通常会根据提供的参数类型列表来选择合适的构造函数。如果参数类型与构造函数参数类型不匹配，可能会导致解析失败或选择错误的构造函数。因此，需要确保传递正确类型的参数。

2. **如何处理构造函数重载的情况？**

   - 当类中存在多个构造函数时，`ConstructorResolver`接口的实现类可能需要选择最适合的构造函数。通常情况下，会根据提供的参数类型列表和构造函数参数列表的匹配程度来进行选择。如果存在多个匹配的构造函数，可能会导致解析失败或选择不确定的构造函数。

3. **ConstructorResolver接口与Java反射机制的关系是什么？**

   - `ConstructorResolver`接口的实现类通常会利用Java的反射机制来动态地解析和执行构造函数。通过反射，可以在运行时检查类的结构，并调用特定的构造函数来实例化对象。因此，`ConstructorResolver`接口与Java反射机制密切相关。

4. **如何处理构造函数中可能抛出的异常？**

   - 在调用构造函数时，可能会出现各种异常，如参数类型不匹配、访问权限问题等。`ConstructorResolver`接口的实现类可能会处理这些异常，具体取决于实现。通常情况下，需要在调用构造函数之前进行适当的异常处理。
