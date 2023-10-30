## org.springframework.core.io.ResourceLoader

- [org.springframework.core.io.ResourceLoader](#orgspringframeworkcoreioresourceloader)
  - [一、知识储备](#一知识储备)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、接口源码](#四接口源码)
  - [五、主要实现](#五主要实现)
  - [六、最佳实践](#六最佳实践)
  - [七、与其他组件的关系](#七与其他组件的关系)
  - [八、常见问题](#八常见问题)


### 一、知识储备

1. **Spring 资源抽象**
   - `org.springframework.core.io.Resource` 接口及其各种实现 [点击查看Resource接口](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource)。
2. **路径和模式解析**
   - Spring 中的路径解析，特别是使用 ant 风格的路径模式，例如 `classpath*:com/example/**/*.xml`。
3. **理解不同的资源类型**
   - 文件资源、类路径资源、URL 资源、JAR 中的资源等。

### 二、基本描述

`org.springframework.core.io.ResourceLoader` 是 Spring 框架中的一个关键接口，它定义了如何获取资源（例如类路径资源、文件系统资源或网页资源）的策略。这个接口是 Spring 资源加载抽象的核心，使得应用程序可以从不同的资源位置以统一的方式加载资源。

### 三、主要功能

1. **统一资源加载**
   - `ResourceLoader` 提供了一个标准化的方法来加载资源，不论资源是存放在类路径、文件系统、网络URL还是其他位置。
2. **资源位置解析**
   - 根据提供的资源字符串位置（如 "classpath:", "file:", "http:"），`ResourceLoader` 可以确定资源的类型，并为其创建相应的 `Resource` 实例。
3. **返回 `Resource` 实例**
   - 通过 `Resource getResource(String location)` 方法，`ResourceLoader` 返回一个 `Resource` 对象，代表了指定位置的资源。这使得读取和操作资源变得简单且统一。
4. **与 `ClassLoader` 的交互**
   - `ResourceLoader` 通过 `ClassLoader getClassLoader()` 方法返回与其关联的 `ClassLoader`。这使得资源加载策略可以与特定的类加载器关联。
5. **扩展性**
   - `ResourceLoader` 是一个接口，这意味着我们可以实现自己的资源加载策略，或者扩展默认的策略以满足特定需求。
6. **内置实现与整合**
   - Spring 提供了默认的 `ResourceLoader` 实现，如 `DefaultResourceLoader`。但更重要的是，`org.springframework.context.ApplicationContext` 也实现了 `ResourceLoader`，这意味着 Spring 上下文本身就是一个资源加载器。

### 四、接口源码

`ResourceLoader` 接口为 Spring 框架定义了资源加载策略。它提供了获取资源的方法，并公开了其使用的 `ClassLoader`。通过这种策略，资源可以从各种来源（如类路径、文件系统等）以统一方式加载。这提供了资源加载的灵活性和一致性，并支持各种资源描述符，如 URL、类路径等。此外，它还允许对资源句柄进行多次重新使用和读取。

```java
/**
 * 用于加载资源（例如类路径或文件系统资源）的策略接口。
 * 一个 ApplicationContext 需要提供此功能以及扩展的 ResourcePatternResolver 支持。
 * 
 * DefaultResourceLoader 是一个独立的实现，可在 ApplicationContext 外部使用，并被 ResourceEditor 使用。
 * 
 * 当在 ApplicationContext 中运行时，类型为 Resource 和 Resource[] 的 Bean 属性可以从字符串中填充，使用特定上下文的资源加载策略。
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 */
public interface ResourceLoader {

	/** 用于从类路径加载的伪 URL 前缀："classpath:"。 */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

	/**
	 * 返回指定资源位置的 Resource 句柄。
	 * 该句柄应始终是一个可重用的资源描述符，允许进行多次 Resource#getInputStream() 调用。
	 * 必须支持完全限定的 URLs，例如 "file:C:/test.dat"。
	 * 必须支持类路径伪-URLs，例如 "classpath:test.dat"。
	 * 应支持相对文件路径，例如 "WEB-INF/test.dat"。
	 * （这将是实现特定的，通常由 ApplicationContext 实现提供。）
	 * 请注意，Resource 句柄并不意味着资源存在；我们需要调用 Resource#exists 来检查其存在性。
	 *
	 * @param location 资源位置
	 */
	Resource getResource(String location);

	/**
	 * 公开此 ResourceLoader 使用的 ClassLoader。
	 * 需要直接访问 ClassLoader 的客户端可以与 ResourceLoader 以统一的方式这样做，而不是依赖线程上下文 ClassLoader。
	 *
	 * @return ClassLoader（仅当连系统 ClassLoader 都不可访问时为 null）
	 */
	@Nullable
	ClassLoader getClassLoader();
}
```

`ResourcePatternResolver` 接口提供了一种机制，允许使用特定的模式（如Ant风格的路径模式）从不同的来源解析多个 `Resource` 对象。这不仅限于解析单一资源，而是可以批量解析符合某个模式的所有资源，这在加载分散在多个位置或JAR文件中的配置文件时特别有用。与普通的 `ResourceLoader` 相比，它增加了位置模式解析的能力，使得资源加载更加灵活和强大。

```java
/**
 * 用于将位置模式（例如，Ant风格的路径模式）解析为 Resource 对象的策略接口。
 * 
 * 这是 ResourceLoader 接口的扩展。可以检查传入的 ResourceLoader（例如，通过
 * ResourceLoaderAware 在上下文中运行时传入的 ApplicationContext）是否也实现了这个扩展接口。
 * 
 * PathMatchingResourcePatternResolver 是一个独立的实现，可以在 ApplicationContext 外部使用，
 * 也被 ResourceArrayPropertyEditor 用于填充 Resource 数组bean属性。
 * 
 * 可以与任何形式的位置模式一起使用（例如 "/WEB-INF/*-context.xml"）：输入模式必须匹配策略实现。
 * 此接口仅指定转换方法而不是特定的模式格式。
 * 
 * 此接口还为类路径中的所有匹配资源建议了一个新的资源前缀 "classpath*:"。
 * 请注意，这种情况下的资源位置预计是一个没有占位符的路径（例如 "/beans.xml"）；
 * JAR 文件或类路径中的不同目录可能包含多个同名文件。
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 */
public interface ResourcePatternResolver extends ResourceLoader {

	/**
	 * 从类路径获取所有匹配资源的伪 URL 前缀："classpath*:"。
	 * 这与 ResourceLoader 的类路径 URL 前缀不同，因为它检索给定名称的所有匹配资源（例如 "/beans.xml"），
	 * 例如所有部署的 JAR 文件的根目录。
	 */
	String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

	/**
	 * 将给定的位置模式解析为 Resource 对象。
	 * 尽可能避免重叠的资源条目指向同一物理资源。结果应该具有集合语义。
	 * @param locationPattern 要解析的位置模式
	 * @return 对应的 Resource 对象
	 * @throws IOException 如果出现 I/O 错误
	 */
	Resource[] getResources(String locationPattern) throws IOException;

}
```

### 五、主要实现

1. `DefaultResourceLoader`
   + 这是基本的资源加载器实现。它可以处理 "classpath:" 前缀的资源，如果没有提供这样的前缀，它会尝试使用类加载器或文件系统来加载资源。
2. `PathMatchingResourcePatternResolver`
   + 这个类不仅实现了 `ResourceLoader` 接口，还实现了 `ResourcePatternResolver` 接口。它扩展了 `DefaultResourceLoader` 的功能，支持 "classpath*:" 这样的模式来加载匹配的所有资源。

~~~mermaid
classDiagram
    direction BT
    class ResourceLoader {
    	<<interface>>
        + getResource(location) : Resource
        + getClassLoader() : ClassLoader
    }

    class ResourcePatternResolver {
    	<<interface>>
        +getResources(locationPattern) : Resource[]
    }

    class DefaultResourceLoader {
        -ClassLoader classLoader
    }

    class PathMatchingResourcePatternResolver {
        -ResourceLoader resourceLoader
    }

    ResourcePatternResolver --|> ResourceLoader
    DefaultResourceLoader ..|> ResourceLoader
    PathMatchingResourcePatternResolver ..|> ResourcePatternResolver

~~~

### 六、最佳实践

**`DefaultResourceLoader`**

使用 `DefaultResourceLoader` 从不同的资源（类路径和文件系统）加载内容。

```java
public class DefaultResourceLoaderDemo {
    public static void main(String[] args) {
        DefaultResourceLoader loader = new DefaultResourceLoader();

        // 从类路径加载资源
        Resource classpathResource = loader.getResource("classpath:application.properties");
        try (InputStream is = classpathResource.getInputStream()) {
            // 读取和处理资源内容
            System.out.println("Classpath = "+ new String(is.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 加载文件系统中的资源
        Resource fileResource = loader.getResource("file:/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/myfile1.txt");
        try (InputStream is = fileResource.getInputStream()) {
            // 读取和处理资源内容
            System.out.println("File = "+ new String(is.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

运行结果发现

- 从类路径上，我们加载了一个文件：`application.properties` 。这意味着在我们的项目的类路径中，有这个文件。
- 从文件系统上，我们加载了一个文件：`myfile1.txt`。这些文件位于我们之前在代码中硬编码的文件路径 `/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/` 下。

```java
Classpath Exists= true
File Exists = true
```

**`PathMatchingResourcePatternResolver`**

使用 `PathMatchingResourcePatternResolver` 来加载匹配指定模式的资源。

```java
public class PathMatchingResourcePatternResolverDemo {
    public static void main(String[] args) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 加载所有匹配的类路径资源
        Resource[] resources = resolver.getResources("classpath*:*.properties");
        for (Resource resource : resources) {
            System.out.println("Classpath = " + resource.getFilename());
        }

        // 加载文件系统中的所有匹配资源
        Resource[] fileResources = resolver.getResources("file:/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/*.txt");
        for (Resource resource : fileResources) {
            System.out.println("File = " + resource.getFilename());
        }
    }
}
```

运行结果发现

- 从类路径上，我们加载了两个文件：`application.properties` 和 `bootstrap.properties`。这意味着在我们的项目的类路径中，有这两个文件。
- 从文件系统上，我们加载了三个文件：`myfile1.txt`, `myfile2.txt`, 和 `myfile3.txt`。这些文件位于我们之前在代码中硬编码的文件路径 `/idea-work-space-xcs/spring-reading/spring-resources/spring-resource-resourceLoader/` 下。

```java
Classpath = application.properties
Classpath = bootstrap.properties
File = myfile1.txt
File = myfile2.txt
File = myfile3.txt
```

### 七、与其他组件的关系

1. **`ApplicationContext`**
   + 所有的 Spring `ApplicationContext` 都实现了 `ResourceLoader`。这意味着我们可以使用 Spring 上下文本身来加载资源。

2. **`Resource`**
   + `ResourceLoader` 返回 `Resource` 对象，它代表实际的资源，可以是文件系统中的文件、类路径资源、URLs 等。`Resource` 提供了访问和读取资源内容的方法。

3. **`ResourcePatternResolver`**
   + 这是 `ResourceLoader` 的扩展，可以解析给定的位置模式以加载多个资源。`PathMatchingResourcePatternResolver` 是它的主要实现。

4. **`ResourceEditor`**
   + 这是一个属性编辑器，用于将字符串转换为 `Resource` 对象。它内部使用 `ResourceLoader` 来执行转换。

5. **`ResourceLoaderAware`**
   + 这是一个特殊的接口，任何 bean 如果实现了它，那么它就可以在被创建时获得对 `ResourceLoader` 的引用，这样它就可以自己加载资源。

6. **`EmbeddedValueResolverAware`**
   + 一些组件，如属性占位符处理器，可能需要解析值中的动态部分。它们可以使用 `ResourceLoader` 作为解析这些值的一部分，特别是当值代表资源位置时。

7. **`PathMatchingResourcePatternResolver`**
   + 它是 `ResourcePatternResolver` 的一个实现，它扩展了 `ResourceLoader` 来处理以 "classpath*:" 开头的资源模式，这允许加载所有匹配的资源，而不仅仅是第一个找到的资源。

### 八、常见问题

1. **如何加载类路径资源？**
   - 使用前缀 "classpath:"，例如：`loader.getResource("classpath:myconfig.xml")`。
2. **如何加载文件系统资源？**
   - 使用前缀 "file:"，例如：`loader.getResource("file:/path/to/myconfig.xml")`。
3. **如何加载URL资源？**
   - 直接使用 URL，例如：`loader.getResource("http://www.example.com/config.xml")`。
4. **为什么我加载的资源不存在？**
   - 使用 `Resource.exists()` 方法检查资源是否存在。确保路径或位置正确，并且资源真的存在于预期的位置。
5. **如何读取资源内容？**
   - 从 `Resource` 对象中获取 `InputStream`，例如：`resource.getInputStream()`。
6. **我可以从 `Resource` 获取到文件路径吗？**
   - 使用 `Resource.getFile()`。但请注意，这并不总是有效的，例如当资源实际上是一个类路径资源或URL资源时。
7. **如何加载匹配特定模式的多个资源？**
   - 使用 `ResourcePatternResolver` 或其实现 `PathMatchingResourcePatternResolver`。
8. **如何自动注入 `ResourceLoader`？**
   - 实现 `ResourceLoaderAware` 接口，Spring 将自动为我们的 bean 提供 `ResourceLoader` 的引用。
9. **我如何扩展或自定义资源加载机制？**
   - 我们可以实现自己的 `ResourceLoader` 或继承现有的实现，如 `DefaultResourceLoader`。
10. **加载资源时出现了异常，如何处理？**
    - 捕获 `IOException` 或具体的异常，例如 `FileNotFoundException`。
11. **是否可以在加载资源时考虑环境或属性占位符？**
    - 使用 `PropertyPlaceholderConfigurer` 或 `PropertySourcesPlaceholderConfigurer` 与 `@Value` 注解可以解析属性值中的资源路径。