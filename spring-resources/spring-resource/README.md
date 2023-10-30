## org.springframework.core.io.Resource

- [org.springframework.core.io.Resource](#orgspringframeworkcoreioresource)
  - [一、简介](#一简介)
  - [二、主要功能](#二主要功能)
  - [三、主要方法](#三主要方法)
  - [四、主要实现](#四主要实现)
  - [五、最佳实践](#五最佳实践)
    - [`ClassPathResource`](#classpathresource)
    - [`FileSystemResource`](#filesystemresource)
    - [`UrlResource`](#urlresource)
    - [`ByteArrayResource`](#bytearrayresource)
    - [`InputStreamResource`](#inputstreamresource)
  - [六、与其他组件的关系](#六与其他组件的关系)
  - [七、常见问题和解决方法](#七常见问题和解决方法)
  - [八、参考资料](#八参考资料)


### 一、简介

- `Resource` 是 Spring 框架中用于简化和统一对底层资源（如文件、classpath 资源、URL 等）的访问的一个核心接口。它为不同来源的资源提供了一个共同的抽象，并隐藏了具体资源访问的细节。

- 在 Java 开发中，资源的访问是常见的需求，如读取配置文件、图片、音频等。但 Java 的标准库为不同类型的资源提供了不同的访问机制：例如，对于文件系统中的资源，我们可能使用 `java.io.File`；对于 classpath 中的资源，我们可能使用 `ClassLoader` 的 `getResource` 或 `getResourceAsStream` 方法；对于网络资源，我们可能使用 `java.net.URL`。

  这些不同的机制意味着开发者需要了解和使用多种方式来访问资源，这导致的问题是代码复杂性增加、重复代码以及可能的错误。为了提供一个统一、简化和更高级的资源访问机制，Spring 框架引入了 `Resource` 接口，这个接口为所有的资源提供了一个统一的抽象。

### 二、主要功能

- **统一的资源抽象**
  - 无论资源来自于文件系统、classpath、URL 还是其他来源，`Resource` 接口都为其提供了一个统一的抽象。
- **资源描述**
  - 通过 `getDescription()` 方法，每个 `Resource` 实现都可以为其所代表的底层资源提供描述性信息，这对于错误处理和日志记录特别有用。
- **读取能力**
  - `Resource` 提供了 `getInputStream()` 方法，允许直接读取资源内容，而无需关心资源的实际来源。
- **存在性与可读性**
  - `Resource` 提供了 `exists()` 和 `isReadable()` 方法来确定资源是否存在及其是否可读。
- **开放性检查**
  - `isOpen()` 方法用于检查资源是否表示一个已经打开的流，这有助于避免重复读取流资源。
- **URI 和 URL 访问**
  - `Resource` 允许通过 `getURI()` 和 `getURL()` 方法获取其底层资源的 URI 和 URL，这为进一步的资源处理提供了可能。
- **文件访问**
  - 当资源代表一个文件系统中的文件时，可以通过 `getFile()` 直接访问该文件。
- **多种实现**
  - Spring 提供了多种 `Resource` 的实现，以支持不同来源的资源，如 `ClassPathResource`、`FileSystemResource` 和 `UrlResource` 等。

### 三、接口源码

`InputStreamSource` 是一个简单的接口，用于提供一个输入流。它被设计为可以多次返回一个新的、未读取的输入流，这对于那些需要多次读取输入流的API。

```java
/**
 * 表示可以提供输入流的资源或对象的接口。
 */
public interface InputStreamSource {

	/**
	 * 返回基础资源内容的 InputStream。
	 * 期望每次调用都会创建一个新的流。
	 * 当你考虑到像 JavaMail 这样的API时，这个要求尤为重要，因为在创建邮件附件时，JavaMail需要能够多次读取流。对于这样的用例，要求每个 getInputStream() 调用都返回一个新的流。
	 * @return 基础资源的输入流（不能为 null）
	 * @throws java.io.FileNotFoundException 如果基础资源不存在
	 * @throws IOException 如果无法打开内容流
	 */
	InputStream getInputStream() throws IOException;

}
```

`Resource` 是Spring框架中的核心接口，代表了外部或内部的资源，如文件、类路径资源、URL资源等。它为访问底层资源提供了一个统一的抽象，从而使得代码可以独立于实际资源的类型。

```java
/**
 * 用于描述资源的接口，该接口抽象了底层资源的实际类型，如文件或类路径资源。
 *
 * <p>对于每个资源，如果它在物理形式上存在，都可以打开一个输入流，但只有某些资源才能返回 URL 或文件句柄。具体行为取决于其实现。
 */
public interface Resource extends InputStreamSource {

    /**
     * 判断此资源是否在物理形式上真正存在。
     */
    boolean exists();

    /**
     * 指示是否可以通过 {@link #getInputStream()} 读取此资源的非空内容。
     * 实际的内容读取可能仍然失败。
     */
    default boolean isReadable() {
        return exists();
    }

    /**
     * 指示此资源是否代表一个打开的流的句柄。
     * 如果为 true，则输入流不能被多次读取，并且在读取后必须被关闭，以避免资源泄露。
     */
    default boolean isOpen() {
        return false;
    }

    /**
     * 判断此资源是否代表文件系统中的文件。
     */
    default boolean isFile() {
        return false;
    }

    /**
     * 返回此资源的 URL 句柄。
     */
    URL getURL() throws IOException;

    /**
     * 返回此资源的 URI 句柄。
     */
    URI getURI() throws IOException;

    /**
     * 返回此资源的文件句柄。
     */
    File getFile() throws IOException;

    /**
     * 返回一个 {@link ReadableByteChannel}。
     */
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /**
     * 确定此资源的内容长度。
     */
    long contentLength() throws IOException;

    /**
     * 确定此资源的最后修改时间戳。
     */
    long lastModified() throws IOException;

    /**
     * 创建相对于此资源的资源。
     */
    Resource createRelative(String relativePath) throws IOException;

    /**
     * 返回此资源的文件名。
     */
    @Nullable
    String getFilename();

    /**
     * 返回此资源的描述，用于在处理资源时的错误输出。
     */
    String getDescription();
}
```

### 四、主要实现

- `ClassPathResource`: 用于加载 classpath 下的资源。
- `FileSystemResource`: 用于访问文件系统中的资源。
- `UrlResource`: 用于基于 URL 的资源。
- `ServletContextResource`: 用于 Web 应用中的资源。
- `ByteArrayResource` & `InputStreamResource`: 基于内存和流的资源表示。

~~~mermaid
classDiagram
    direction BT
    
    class InputStreamSource{
    	<<interface>>
    	+ getInputStream(): InputStream
    }
    
    class Resource {
    	<<interface>>
        + exists(): boolean
        + getDescription(): String
        + 其他方法()
    }
    Resource ..|> InputStreamSource

    class AbstractResource {
    	<<Abstract>>
    }
    AbstractResource --|> Resource

    class ClassPathResource {
    }
    ClassPathResource ..|> AbstractResource
    
    class FileSystemResource {
    }
    FileSystemResource ..|> AbstractResource
    
    class UrlResourceDemo {
    }
    UrlResourceDemo ..|> AbstractResource
    
    class ByteArrayResource {
    }
    ByteArrayResource ..|> AbstractResource
    
    class InputStreamResourceDemo {
    }
    InputStreamResourceDemo ..|> AbstractResource
~~~



### 五、最佳实践

#### `ClassPathResource`

使用`ClassPathResource` 是 Spring 框架中的一个组件，用于访问类路径上的资源。在此示例中，`path` 变量存储了类路径资源的路径。这里，文件位于 `src/main/resources/application.properties` 路径目录。

```java
public class ClassPathResourceDemo {
    public static void main(String[] args) throws Exception {
        String path = "application.properties";
        Resource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            // 读取和处理资源内容
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

#### `FileSystemResource`

使用 `FileSystemResource` 是 Spring 框架中的一个组件，用于访问文件系统上的文件。在此示例中，`path` 变量存储了文件的完整路径。这个路径需要被替换为你自己的有效文件路径。

```java
public class FileSystemResourceDemo {
    public static void main(String[] args) throws Exception {
        // 请替换你自己的目录
        String path = "D:\\idea-work-space-xcs\\spring-reading\\spring-resources\\spring-resource\\myfile.txt";
        Resource resource = new FileSystemResource(path);
        try (InputStream is = resource.getInputStream()) {
            // 读取和处理资源内容
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

#### `UrlResource`

使用`UrlResource` 类是 Spring 框架中的一个组件，用于访问网络上的资源。在这个示例中，它被用来读取一个在线的文本文件。

```java
public class UrlResourceDemo {
    public static void main(String[] args) throws Exception {
        Resource resource = new UrlResource("https://dist.apache.org/repos/dist/test/test.txt");
        try (InputStream is = resource.getInputStream()) {
            // 读取和处理资源内容
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

#### `ByteArrayResource`

使用`ByteArrayResource` 是 Spring 框架中的一个组件，用于访问字节数组作为资源。在这个示例中，它被用来读取一个简单的字符串 `"hello world"`。

```java
public class ByteArrayResourceDemo {
    public static void main(String[] args) throws Exception {
        byte[] data = "hello world".getBytes();
        Resource resource = new ByteArrayResource(data);
        try (InputStream is = resource.getInputStream()) {
            // 读取和处理资源内容
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

#### `InputStreamResource`

使用`InputStreamResource` 是 Spring 框架中的一个组件，用于将任何 `InputStream` 包装为 Spring 的 `Resource`。在这个示例中，使用字符串 "hello world" 的字节来创建一个 `ByteArrayInputStream`。这样我们就有了一个可以读取 "hello world" 的输入流。

```java
public class InputStreamResourceDemo {
    public static void main(String[] args) throws Exception {
        InputStream isSource = new ByteArrayInputStream("hello world".getBytes());
        Resource resource = new InputStreamResource(isSource);
        try (InputStream is = resource.getInputStream()) {
            // 读取和处理资源内容
            System.out.println(new String(is.readAllBytes()));
        }
    }
}
```

### 六、与其他组件的关系

- 例如：`ResourceLoader` 和 `ResourcePatternResolver`。

### 七、常见问题和解决方法

- 列出与此接口相关的常见问题及其解决方法。

### 八、参考资料

- 提供进一步的阅读或学习资料。