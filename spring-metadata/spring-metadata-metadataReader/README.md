## MetadataReader

- [MetadataReader](#metadatareader)
  - [一、知识储备](#一知识储备)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、接口源码](#四接口源码)
  - [五、主要实现](#五主要实现)
  - [六、最佳实践](#六最佳实践)
  - [七、与其他组件的关系](#七与其他组件的关系)
  - [八、常见问题](#八常见问题)


### 一、知识储备

1. **Resource接口**
   + 了解 Spring 的 `Resource` 接口，它是用于访问资源的抽象接口。资源可以是文件、类路径中的文件、URL 等等。我们需要了解如何使用 `Resource` 接口来获取资源的输入流、文件路径等信息。[点击查看Resource接口](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource)
2. **ResourceLoader接口**
   + 了解 Spring 的 `ResourceLoader` 接口，它是用于获取 `Resource` 对象的工厂。我们需要了解如何使用 `ResourceLoader` 接口来获取 `Resource` 对象，以便加载资源。[点击查看ResourceLoader接口](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource-resourceLoader)
3. **类路径扫描**
   + 了解如何使用 `Resource` 接口和 `ResourceLoader` 接口来扫描类路径上的资源，以获取类的元数据信息。
4. **Spring配置**
   + 熟悉如何配置 Spring 应用程序上下文，以便能够使用 `Resource` 和 `ResourceLoader`。这可能涉及到配置文件（如 XML 或 Java 配置类）的编写和加载。
5. **ASM（可选）**
   + 虽然不是必需的，但了解基本的ASM（字节码操作框架）知识可以帮助我们更好地理解 `MetadataReader` 的内部工作原理。Spring的`SimpleMetadataReader` 使用ASM来解析类文件。

### 二、基本描述

`org.springframework.core.type.classreading.MetadataReader` 接口是 Spring 框架中用于读取和解析类文件元数据的核心接口之一。它的主要作用是允许应用程序获取有关类的元数据信息，包括类的名称、访问修饰符、接口、超类、注解等等。这些元数据信息可以在运行时用于实现各种高级功能，例如组件扫描、条件化注解处理、AOP（面向切面编程）等。

### 三、主要功能

1. **获取类的基本信息**
   + `MetadataReader` 允许我们获取关于类的基本信息，包括类名、是否为接口、是否为抽象类、是否为注解类等。
2. **获取类上的注解信息**
   + 我们可以使用 `MetadataReader` 获取类上的注解信息，包括注解的类型、注解的属性值等。
3. **获取方法上的注解信息**
   + 通过 `MetadataReader`，我们可以获取类中方法的元数据信息，包括方法的名称、返回类型、是否为抽象方法，以及方法上的注解信息。
4. **获取类的成员类信息**
   + `MetadataReader` 提供了方法来获取类中声明的成员类（嵌套类或内部类）的名称。
5. **获取类的资源信息**
   + 我们可以使用 `MetadataReader` 获取与类关联的资源信息，例如类文件的路径。
6. **获取类的超类信息**
   + `MetadataReader` 允许我们获取类的超类信息，包括超类的名称和是否有超类。

### 四、接口源码

`MetadataReader` 接口是 Spring 框架中的一个简单接口，用于访问类元数据。它提供了获取类文件资源、类的基本元数据和类上注解元数据的方法。这些元数据是由 ASM 中的 ClassReader 读取的。这个接口的主要用途是在 Spring 框架中处理类级别的元数据和注解，使我们能够访问和操作类的信息和注解信息。

```java
/**
 * MetadataReader 接口：
 * 提供了一种访问类元数据的简单接口，以便读取类信息，包括类的基本信息和类上的注解信息。
 * 这些信息是由 ASM 中的 ClassReader 读取的。
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public interface MetadataReader {

    /**
     * 返回类文件的资源引用。
     */
    Resource getResource();

    /**
     * 读取底层类的基本元数据。
     */
    ClassMetadata getClassMetadata();

    /**
     * 读取底层类的完整注解元数据，包括类上的注解信息以及带注解的方法的元数据。
     */
    AnnotationMetadata getAnnotationMetadata();
}
```

### 五、主要实现

1. `SimpleMetadataReader`
   + 用于从类文件中读取元数据。它使用 ASM 库的 `ClassReader` 来解析类文件并提取元数据。

~~~mermaid
classDiagram
    direction BT
    class MetadataReader {
    	<<interface>>
        + getResource() : Resource
        + getClassMetadata() : ClassMetadata
        + getAnnotationMetadata() : AnnotationMetadata
    }

    class SimpleMetadataReader {
    }

    SimpleMetadataReader ..|> MetadataReader

~~~

### 六、最佳实践

首先创建了一个`SimpleMetadataReaderFactory`的实例，它是用于创建`MetadataReader`对象的工厂。然后，通过`SimpleMetadataReaderFactory`创建了一个`MetadataReader`。接着，使用`MetadataReader`获取了目标类的基本信息，包括类名、类的类型（接口、注解、抽象类、普通类等）以及其他相关属性，以便深入了解类的结构。接下来，获取了类上的注解信息，包括注解的类型，以帮助了解类是否使用了特定的注解。最后，遍历类中带有指定注解的方法，获取方法的详细信息，例如方法名、声明方法的类名、返回类型等，以便执行自定义注解处理或特定方法的逻辑。

```java
public class MetadataReaderDemo {
    public static void main(String[] args) throws IOException {

        // 创建 MetadataReaderFactory
        SimpleMetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory();
        // 获取 MetadataReader
        MetadataReader metadataReader = readerFactory.getMetadataReader("com.xcs.spring.bean.MyBean");

        // 获取类的基本信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        System.out.println("Class Name = " + classMetadata.getClassName());
        System.out.println("Class IsInterface = " + classMetadata.isInterface());
        System.out.println("Class IsAnnotation = " + classMetadata.isAnnotation());
        System.out.println("Class IsAbstract = " + classMetadata.isAbstract());
        System.out.println("Class IsConcrete = " + classMetadata.isConcrete());
        System.out.println("Class IsFinal = " + classMetadata.isFinal());
        System.out.println("Class IsIndependent = " + classMetadata.isIndependent());
        System.out.println("Class HasEnclosingClass = " + classMetadata.hasEnclosingClass());
        System.out.println("Class EnclosingClassName = " + classMetadata.getEnclosingClassName());
        System.out.println("Class HasSuperClass = " + classMetadata.hasSuperClass());
        System.out.println("Class SuperClassName = " + classMetadata.getSuperClassName());
        System.out.println("Class InterfaceNames = " + Arrays.toString(classMetadata.getInterfaceNames()));
        System.out.println("Class MemberClassNames = " + Arrays.toString(classMetadata.getMemberClassNames()));
        System.out.println("Class Annotations: " +  metadataReader.getAnnotationMetadata().getAnnotationTypes());

        System.out.println();

        // 获取方法上的注解信息
        for (MethodMetadata methodMetadata : metadataReader.getAnnotationMetadata().getAnnotatedMethods("com.xcs.spring.annotation.MyAnnotation")) {
            System.out.println("Method Name: " + methodMetadata.getMethodName());
            System.out.println("Method DeclaringClassName: " + methodMetadata.getDeclaringClassName());
            System.out.println("Method ReturnTypeName: " + methodMetadata.getReturnTypeName());
            System.out.println("Method IsAbstract: " + methodMetadata.isAbstract());
            System.out.println("Method IsStatic: " + methodMetadata.isStatic());
            System.out.println("Method IsFinal: " + methodMetadata.isFinal());
            System.out.println("Method IsOverridable: " + methodMetadata.isOverridable());
            System.out.println();
        }
    }
}
```

定义了一个Java类`MyBean`，它继承了一个抽象类`MyAbstract`，并被标记为`@MyClassAnnotation`的类级别注解。`MyBean`类包含字段`key`和`value`，以及三个方法：`myMethod1`（静态方法，标记有`@MyAnnotation`注解）、`myMethod2`（实例方法，返回字符串，同样标记有`@MyAnnotation`注解）和`myMethod3`（普通实例方法）。此外，`MyBean`类定义了一个静态内部类`MyInnerClass`。这个代码我给大家展示了Java类的不同方面，包括继承、实现接口、字段、方法、注解以及内部类的使用。

```java
public abstract class MyAbstract {
    
}

@MyClassAnnotation
public final class MyBean extends MyAbstract implements Serializable {

    public String key;

    public String value;

    @MyAnnotation
    public static void myMethod1() {
        
    }

    @MyAnnotation
    public String myMethod2() {
        return "hello world";
    }

    public void myMethod3() {
        
    }

    public static class MyInnerClass {
        // 内部类的定义
    }
}
```

定义了两个自定义注解：`MyAnnotation` 和 `MyClassAnnotation`。

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
public @interface MyClassAnnotation {
    String value() default "";
}
```

运行结果发现，使用 `MetadataReader` 可以获取类和方法的基本信息、注解元数据，实现自定义注解处理逻辑，识别不带特定注解的方法，深入了解类的结构，以及在运行时动态检查类的特性，为开发自定义框架、组件扫描和反射操作提供了强大的工具。但是，`MetadataReader`不会收集没有特定注解的方法(比如：`myMethod3`)。这意味着未被注解标记的方法将不会包含在元数据中。

```java
Class Name = com.xcs.spring.bean.MyBean
Class IsInterface = false
Class IsAnnotation = false
Class IsAbstract = false
Class IsConcrete = true
Class IsFinal = true
Class IsIndependent = true
Class HasEnclosingClass = false
Class EnclosingClassName = null
Class HasSuperClass = true
Class SuperClassName = com.xcs.spring.bean.MyAbstract
Class InterfaceNames = [java.io.Serializable]
Class MemberClassNames = [com.xcs.spring.bean.MyBean$MyInnerClass]
Class Annotations: [com.xcs.spring.annotation.MyClassAnnotation]

Method Name: myMethod1
Method DeclaringClassName: com.xcs.spring.bean.MyBean
Method ReturnTypeName: void
Method IsAbstract: false
Method IsStatic: true
Method IsFinal: false
Method IsOverridable: false

Method Name: myMethod2
Method DeclaringClassName: com.xcs.spring.bean.MyBean
Method ReturnTypeName: java.lang.String
Method IsAbstract: false
Method IsStatic: false
Method IsFinal: false
Method IsOverridable: true
```

### 七、与其他组件的关系

1. **`ClassPathBeanDefinitionScanner`**
   + `ClassPathBeanDefinitionScanner`使用 `MetadataReader` 类来实现组件扫描。组件扫描是一种机制，它自动发现并注册应用程序中的类，以便它们可以被Spring容器管理。`MetadataReader` 用于扫描类的元数据，以确定哪些类应该被注册为Spring组件，例如标记为 `@Component`、`@Service`、`@Repository` 等注解的类。
2. **`MetadataReaderFactory`**
   + `MetadataReaderFactory`是一个工厂类，用于创建 `MetadataReader` 实例。而`MetadataReader` 是一个接口，用于读取和解析类的元数据信息，包括类级别和方法级别的注解信息。`MetadataReaderFactory`负责处理类资源（如类文件或字节码），并将其包装成 `MetadataReader` 对象，以便进一步处理类的元数据。

### 八、常见问题

1. **`MetadataReader` 与 `ClassPathBeanDefinitionScanner` 之间的关系是什么？**
   - `ClassPathBeanDefinitionScanner` 是 Spring 框架用于执行组件扫描的类，它使用 `MetadataReader` 来扫描指定包中的类，识别哪些类应该被注册为 Spring Bean。
2. **在 Spring 中的哪些场景中使用 `MetadataReader`？**
   - `MetadataReader` 在 Spring 中主要用于组件扫描、AOP、注解处理、Bean后处理等场景中，以读取和处理类的元数据信息。
3. **为什么使用 `MetadataReader` 而不是反射？**
   - 使用 `MetadataReader` 通常比反射更高效，因为它直接读取字节码，而不需要加载整个类。此外，它允许进行更高级的元数据分析和自定义操作。
4. **有哪些常见的 `MetadataReader` 的实现类？**
   - Spring 框架提供了多个 `MetadataReader` 的实现类，最常见的是 `SimpleMetadataReader`。它们用于从类文件和注解信息中读取元数据。
5. **如何自定义 `MetadataReader` 的使用？**
   - 我们可以编写自定义的元数据读取器，实现 `MetadataReader` 接口，以满足特定需求，例如自定义注解处理、特殊的类加载逻辑等。