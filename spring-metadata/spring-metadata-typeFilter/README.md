## TypeFilter

- [TypeFilter](#typefilter)
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

1. **Resource接口**
   + [Resource](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource) 是用于访问资源的抽象接口。资源可以是文件、类路径中的文件、URL 等等。我们需要了解如何使用 `Resource` 接口来获取资源的输入流、文件路径等信息。

2. **AnnotationMetadata接口**
   + [AnnotationMetadata](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-annotationMetadata) 是Spring 框架中用于处理类上的注解信息的接口，它提供了对类上注解信息的访问和操作方法。 

3. **MetadataReader接口**
   + [MetadataReader](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-metadataReader)是Spring 提供的一个接口，用于读取类的元数据信息。它可以用于扫描类文件，获取类的基本信息，如类名、类的注解等。在注解驱动的开发中，`MetadataReader` 通常用于扫描包中的类，并从这些类中提取注解信息，以便配置 Spring Bean。

4. **路径和模式解析**
   + Spring 中的路径解析，特别是使用 ant 风格的路径模式，例如 `classpath*:com/xcs/spring/**/*.xml`。

### 三、基本描述

`TypeFilter` 是 Spring 框架中的一个接口，用于在组件扫描期间对类进行筛选。通过实现 `match` 方法，我们可以定义自己的逻辑，决定哪些类应该被包含在组件扫描的结果中，而哪些类应该被排除。这一灵活的机制在spring中的 `@ComponentScan` 注解时被使用，可以通过自定义的 `TypeFilter` 对类进行精确的过滤，满足复杂应用程序结构或特定条件下的组件管理需求。Spring 提供了多个内置的 `TypeFilter` 实现，如 `AnnotationTypeFilter` 和 `AssignableTypeFilter`，用于基于注解或类型进行筛选。

### 四、主要功能

1. **自定义过滤逻辑**
   + 我们可以实现 `TypeFilter` 接口，通过覆盖 `match` 方法定义自己的过滤逻辑。这使得可以根据特定的条件，如类的注解、实现的接口或继承关系等，来决定类是否应该被包含在组件扫描的结果中。

2. **组件扫描定制**
   + 在使用 `@ComponentScan` 注解配置类时，可以通过设置 `includeFilters` 和 `excludeFilters` 属性，传入自定义的 `TypeFilter` 实例，从而定制组件扫描的规则。这样可以更精确地控制哪些类应该被纳入 Spring 容器的管理，哪些类应该被排除。

3. **适应复杂应用结构**
   + 对于复杂的应用结构，可能存在不同模块或层次的类，而我们可能只想要将特定模块或层次的类纳入 Spring 容器。通过自定义 `TypeFilter`，可以根据项目的实际结构，有选择地将类包含或排除。

4. **灵活性和可扩展性**
   + `TypeFilter` 提供了一种灵活的机制，使得我们可以根据特定需求扩展和定制组件扫描的行为。这种灵活性对于需要动态适应不同场景的应用程序是非常有用的。

### 五、接口源码

`TypeFilter`接口，作为组件扫描过程中自定义类过滤器的基础。该接口包含一个`match`方法，通过传入`MetadataReader`和`MetadataReaderFactory`，我们可以实现自定义的过滤逻辑，决定哪些类应该被包含在组件扫描结果中，从而增强了Spring框架在应用初始化时对类的筛选和管理的灵活性。

```java
/**
 * 使用 org.springframework.core.type.classreading.MetadataReader 的类型过滤器的基础接口。
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 */
@FunctionalInterface
public interface TypeFilter {

    /**
     * 确定该过滤器是否与给定元数据描述的类匹配。
     *
     * @param metadataReader       目标类的元数据读取器
     * @param metadataReaderFactory 用于获取其他类的元数据读取器的工厂（例如超类和接口）
     * @return 是否匹配该过滤器
     * @throws IOException 在读取元数据时发生 I/O 失败时抛出异常
     */
    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException;

}
```

### 六、主要实现

1. **AnnotationTypeFilter（基于注解的过滤器）**
   + 匹配带有指定注解的类。在组件扫描期间，通过比对类的注解信息，确定是否将该类包含在扫描结果中。

2. **AssignableTypeFilter（基于类型的过滤器）：**
   + 匹配指定类型的子类或实现类。通过与目标类的继承关系比对，确定是否将该类纳入组件扫描的结果中。

3. **AspectJTypeFilter（基于AspectJ表达式的过滤器）：**
   + 使用AspectJ表达式进行匹配。它允许通过编写AspectJ风格的表达式，灵活地选择需要被扫描的类。

4. **RegexPatternTypeFilter（基于正则表达式的过滤器）：**
   + 使用正则表达式来匹配类的名称。通过提供一个正则表达式，决定是否将符合条件的类包含在组件扫描的结果中。

~~~mermaid
classDiagram
    direction BT
    
    class TypeFilter{
    	<<interface>>
    }
    
    class AbstractTypeHierarchyTraversingFilter {
    	<<Abstract>>
    }
    
    class AbstractClassTestingTypeFilter {
    	<<Abstract>>
    }
  
    class AnnotationTypeFilter {
    }
    
    class AssignableTypeFilter {
    }

    class AspectJTypeFilter {
    }

    class RegexPatternTypeFilter {
    }
    
    AbstractTypeHierarchyTraversingFilter ..|> TypeFilter
    AnnotationTypeFilter --|> AbstractTypeHierarchyTraversingFilter
    AssignableTypeFilter --|> AbstractTypeHierarchyTraversingFilter
    AspectJTypeFilter ..|> TypeFilter
    AbstractClassTestingTypeFilter ..|> TypeFilter
    RegexPatternTypeFilter --|> AbstractClassTestingTypeFilter

~~~

### 七、最佳实践

使用 Spring 框架的 `TypeFilter` 进行类的筛选过程。通过创建路径匹配的资源模式解析器和元数据读取器工厂，以及定义一个注解类型过滤器（我们最佳实践中为 `MyAnnotation`），然后从`classpath*:com/xcs/spring/**/*.class`路径下获取所有类文件，并筛选出带有指定注解的类。遍历扫描到的类文件，输出文件名以及注解类型过滤的结果。

```java
public class TypeFilterDemo {

    public static void main(String[] args) throws IOException {
        // 创建路径匹配的资源模式解析器
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 创建一个简单的元数据读取器工厂
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

        // 创建一个注解类型过滤器，用于匹配带有 MyAnnotation 注解的类
        TypeFilter annotationTypeFilter = new AnnotationTypeFilter(MyAnnotation.class);

        // 使用资源模式解析器获取所有匹配指定路径的类文件
        Resource[] resources = resolver.getResources("classpath*:com/xcs/spring/**/*.class");

        // 遍历扫描到的类文件
        for (Resource resource : resources) {
            // 获取元数据读取器
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

            // 使用注解类型过滤器匹配当前类
            boolean match = annotationTypeFilter.match(metadataReader, metadataReaderFactory);

            // 输出扫描到的文件名和匹配结果
            System.out.printf("扫描到的文件: %-20s || 筛选器是否匹配: %s%n", resource.getFile().getName(), match);
        }
    }
}
```

`MyAnnotation`注解被用作 `TypeFilter` 中的匹配条件，用于过滤带有此注解的类。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation {
}
```

定义了一个名为 `MyComponent` 的类，并使用了 `MyAnnotation` 注解。

```java
package com.xcs.spring.component;

@MyAnnotation
public class MyComponent {
}
```

定义了一个名为 `MyController` 的类，但是该类没有使用任何自定义注解。

```java
package com.xcs.spring.controller;

public class MyController {
    
}
```

定义了一个名为 `MyRepository` 的类，但是该类没有使用任何自定义注解。

```java
package com.xcs.spring.repository;

public class MyRepository {
    
}
```

定义了一个名为 `MyService` 的类，但是该类没有使用任何自定义注解。

```java
package com.xcs.spring.service;

public class MyService {
    
}
```

运行结果发现，可以看出  `MyController`、`MyRepository`、`MyService`并没有匹配到注解类型过滤器，而带有 `MyAnnotation` 注解的类 `MyComponent` 成功匹配。这符合预期，证明了注解类型过滤器在筛选类时的有效性。

```java
扫描到的文件: TypeFilterDemo.class || 筛选器是否匹配: false
扫描到的文件: MyAnnotation.class   || 筛选器是否匹配: false
扫描到的文件: MyComponent.class    || 筛选器是否匹配: true
扫描到的文件: MyController.class   || 筛选器是否匹配: false
扫描到的文件: MyRepository.class   || 筛选器是否匹配: false
扫描到的文件: MyService.class      || 筛选器是否匹配: false
```

### 八、与其他组件的关系

1. **ClassPathScanningCandidateComponentProvider**
   + `registerDefaultFilters` 方法是 `ClassPathScanningCandidateComponentProvider` 类的一个方法，用于注册默认的类型过滤器。这个方法在组件扫描时被调用，它会添加一些默认的过滤器到 `includeFilters` 集合中，这些过滤器包括，用于扫描带有 `@Component` 注解的类，以及JSR 规范中的一些注解类型过滤器，如 `ManagedBean` 和 `Named` 注解。

2. **ComponentScanAnnotationParser**
   + `typeFiltersFor`方法是 `ComponentScanAnnotationParser` 类的一个私有方法，用于根据 `@ComponentScan` 注解中的属性信息创建类型过滤器列表。根据不同的过滤器类型（如注解类型、可分配类型、自定义类型等），它会创建对应的 `TypeFilter` 实例并添加到列表中。

3. **ComponentScanBeanDefinitionParser**
   + `createTypeFilter` 方法属于 `ComponentScanBeanDefinitionParser` 类，用于在解析 `<component-scan>` 元素时根据 XML 配置信息创建类型过滤器。它根据配置中的 `filter-type` 属性和 `filter-expression` 属性，动态选择创建相应的类型过滤器。支持的过滤器类型包括注解类型、可分配类型、AspectJ 表达式、正则表达式等。

### 九、常见问题

1. **无法正确匹配类**
   + 需要确保过滤器的匹配条件（如注解、类型、正则表达式等）与目标类的实际情况一致。检查过滤器的实例化和使用是否正确。
   
2. **自定义的 TypeFilter 不生效**
   + 确保自定义的 `TypeFilter` 实现正确并且被正确地配置。检查实现中的匹配逻辑是否符合预期。

3. **包扫描结果为空**
   + 检查包路径是否正确，确保过滤器条件与目标类匹配。也可以检查类加载器是否正确，以确保可以加载目标类。

4. **多个 TypeFilter 失效**
   + 确保多个 `TypeFilter` 的使用场景和条件不重叠，否则可能会出现只有一个过滤器生效的情况。

5. **AspectJ 表达式匹配失败：**
   + 确保 AspectJ 表达式正确，并且类加载器可访问相关的类。并检查 `AspectJTypeFilter` 的构造函数中的类加载器是否正确。

6. **性能问题：**
   + 在大型项目中，使用 `TypeFilter` 导致性能问题。考虑优化过滤器的实现，或者在适当的情况下缓存扫描结果。可以使用缓存或其他优化技术来减轻性能问题。