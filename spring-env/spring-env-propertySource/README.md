## PropertySource

- [PropertySource](#propertysource)
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

1. **Java I/O 和文件处理**

   + 需要熟悉 Java 文件 I/O 操作，特别是如何读取和写入文件，理解处理 `.properties` 文件的 Java 类如 `Properties`，以及如何利用类路径和文件系统路径来定位和加载资源文件，这些知识对于从文件系统或类路径加载属性文件至关重要。

2. **Spring 资源抽象**

   + 需要了解 Spring 的 [Resource](/spring-resources/spring-resource/README.md) 接口及其实现，比如 `ClassPathResource`、`FileSystemResource` 和 `UrlResource`，这包括理解如何利用 Spring 的强大资源加载机制来读取外部配置文件，这对于从各种位置加载配置文件非常有用。

3. **Java 集合框架**

   + 需要了解 Java 集合框架有深入了解，特别是 `Map` 接口及其实现如 `HashMap` 和 `LinkedHashMap`，这包括知道如何在 Map 中存储、检索和更新键值对，这对于从键值对集合中加载属性至关重要。

4. **系统环境和 Java 系统属性**

   + 需要了解 Java 中如何访问和操作系统环境变量和系统属性，包括使用 `System.getenv()` 和 `System.getProperties()`，并理解这些变量在不同操作系统中如何设置和修改，这对于从操作系统环境中加载配置非常重要。

5. **命令行参数解析**

   + 需要了解如何在 Java 程序中解析命令行参数，包括不同的参数格式（如标志、键值对），以及如何使用第三方库简化命令行参数的解析，这对于从命令行参数中加载配置非常关键。

### 三、基本描述

`PropertySource` 类是 Spring 框架中的一个关键抽象类，专门用于封装不同来源的配置数据，如文件、环境变量、系统属性等。它为这些配置源提供了一个统一的接口，使得可以以一致的方式访问各种不同类型的配置数据。这个类的核心是其 `getProperty(String name)` 方法，它根据提供的属性名来检索属性值。在 Spring 的环境抽象中，`PropertySource` 的实例可以被添加到 `Environment` 对象中，从而允许我们在应用程序中方便地访问和管理这些属性。

### 四、主要功能

1. **统一属性源接口**

   + `PropertySource` 提供了一个统一的接口来访问不同来源的属性，如文件、环境变量、系统属性等。这种统一性使得在不同环境下处理配置数据变得更加简单。

2. **属性查找和访问**

   + 它允许通过键（属性名）来检索属性值。这是 `PropertySource` 最基本和直接的功能，支持对各种配置数据的读取。

3. **属性源优先级和覆盖**

   + 在 Spring 的环境中可以注册多个 `PropertySource` 实例，它们按照一定的优先级顺序搜索。这意味着可以通过调整不同 `PropertySource` 的优先级来控制哪些属性将覆盖其他的属性。

4. **环境集成**

   + `PropertySource` 实例通常被整合到 Spring 的 `Environment` 抽象中，这使得在整个应用程序中访问属性变得更加方便。

5. **可扩展性**

   + 由于 `PropertySource` 是抽象的，开发者可以创建自定义 `PropertySource` 实现，从而支持从几乎任何数据源加载属性，例如数据库、远程服务、非标准格式的文件等。

6. **支持多种数据格式**

   + Spring 提供了多种 `PropertySource` 实现，支持从标凑属性文件、JSON 文件、YAML 文件等多种格式中加载属性。

7. **动态属性更新**

   + 某些 `PropertySource` 实现支持动态更新属性，这对于需要在运行时改变配置的应用程序特别有用。

8. **配置隔离和模块化**

   + `PropertySource` 有助于将配置隔离到不同的模块中，使得配置管理更加模块化和清晰。

### 五、接口源码

`PropertySource` 类是 Spring 框架中用于封装属性源的抽象基类。它允许从多种来源（如属性文件、环境变量等）以统一的方式访问配置数据。类中定义了一些核心方法，如获取属性值的 `getProperty` 方法、检查属性是否存在的 `containsProperty` 方法，以及获取属性源名称和源对象的方法。

```java
/**
 * 抽象基类，表示属性源。
 * 属性源是键值对的集合，它们可以从各种源（如属性文件、环境变量等）加载。
 *
 * @param <T> 属性源的类型
 */
public abstract class PropertySource<T> {

    protected final Log logger = LogFactory.getLog(getClass());

    // 属性源的名称
    protected final String name; 

    // 属性源对象
    protected final T source; 

    /**
     * 构造一个新的 PropertySource，带有给定的名称和源对象。
     *
     * @param name 属性源的名称
     * @param source 属性源对象
     */
    public PropertySource(String name, T source) {
        Assert.hasText(name, "Property source name must contain at least one character");
        Assert.notNull(source, "Property source must not be null");
        this.name = name;
        this.source = source;
    }

    /**
     * 构造函数重载，创建一个具有指定名称的 PropertySource，源对象为新的 Object 实例。
     * 通常用于测试场景，创建匿名实现时不需要查询实际源。
     *
     * @param name 属性源的名称
     */
    @SuppressWarnings("unchecked")
    public PropertySource(String name) {
        this(name, (T) new Object());
    }

    /**
     * 获取此 PropertySource 的名称。
     *
     * @return 属性源的名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 返回此 PropertySource 的底层源对象。
     *
     * @return 属性源对象
     */
    public T getSource() {
        return this.source;
    }

    /**
     * 检查此 PropertySource 是否包含给定名称的属性。
     * 默认实现仅检查 getProperty(String) 返回的值是否为 null。
     * 子类可以实现更高效的算法。
     *
     * @param name 要查找的属性名称
     * @return 如果包含该属性，则返回 true
     */
    public boolean containsProperty(String name) {
        return (getProperty(name) != null);
    }

    /**
     * 返回与给定名称关联的值，如果未找到，则返回 null。
     * 
     * @param name 要查找的属性名称
     * @return 属性值或 null
     */
    @Nullable
    public abstract Object getProperty(String name);

    // 其他方法（equals、hashCode、toString）省略
}
```

### 六、主要实现

1. **`RopertiesPropertySource`**

   - 用于加载 `.properties` 文件中的属性。通常通过 `Properties` 对象来构造。

2. **`ResourcePropertySource`**

   - 扩展自 `PropertiesPropertySource`。可以直接从 `Resource` 对象（如文件系统资源、类路径资源等）中加载属性。

3. **`MapPropertySource`**

   - 从 `Map<String, Object>` 中加载属性。非常灵活，可以用于从任何键值对映射中提取属性。

4. **`SystemEnvironmentPropertySource`**

   - 用于访问操作系统环境变量。这个实现类特别处理了环境变量的命名约定（例如，将所有字符转换为小写，将非字母数字字符替换为下划线）。

5. **`CommandLinePropertySource`**

   - 用于处理命令行参数。支持简单的命名参数以及更复杂的键值对参数。

6. **`CompositePropertySource`**

   - 一个组合类，可以包含多个 `PropertySource` 实例。用于将多个 `PropertySource` 对象合并为一个逻辑单元，以便统一处理。

### 七、最佳实践

演示了如何在 Java 应用中使用 Spring Framework 的 `PropertySource` 类来从不同来源（如属性文件、资源文件、Map 对象、系统环境变量和命令行参数）加载配置数据。最后，它使用 `CompositePropertySource` 将所有这些不同的属性源组合在一起，以便能够统一访问和管理这些来自不同来源的配置信息。

```java
public class PropertySourceDemo {

    public static void main(String[] args) throws Exception {
        // 从 .properties 文件加载属性
        Properties source = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
        PropertySource propertySource = new PropertiesPropertySource("properties", source);

        // 直接从Resource加载属性
        ClassPathResource classPathResource = new ClassPathResource("application.properties");
        PropertySource resourcePropertySource = new ResourcePropertySource("resource", classPathResource);

        // 从Map加载属性
        Map<String, Object> map = new HashMap<>();
        map.put("app.name", "Spring-Reading");
        map.put("app.version", "1.0.0");
        PropertySource mapPropertySource = new MapPropertySource("mapSource", map);

        // 访问系统环境变量
        Map mapEnv = System.getenv();
        PropertySource envPropertySource = new SystemEnvironmentPropertySource("systemEnv", mapEnv);

        // 解析命令行参数
        String[] myArgs = {"--appName=MyApplication", "--port=8080"};
        PropertySource commandLinePropertySource = new SimpleCommandLinePropertySource(myArgs);

        // 组合多个 PropertySource 实例
        CompositePropertySource composite = new CompositePropertySource("composite");
        composite.addPropertySource(propertySource);
        composite.addPropertySource(resourcePropertySource);
        composite.addPropertySource(mapPropertySource);
        composite.addPropertySource(envPropertySource);
        composite.addPropertySource(commandLinePropertySource);

        // 打印结果
        for (PropertySource<?> ps : composite.getPropertySources()) {
            System.out.printf("Name: %-15s || Source: %s%n", ps.getName(), ps.getSource());
        }
    }
}
```

运行结果发现，每个 `PropertySource` 实例显示了其特定的数据源和内容，如从文件中读取的配置项、环境变量的详细列表，以及命令行参数的封装。

```java
Name: properties      || Source: {app.name=Spring-Reading}
Name: resource        || Source: {app.name=Spring-Reading}
Name: mapSource       || Source: {app.name=Spring-Reading, app.version=1.0.0}
Name: systemEnv       || Source: {USERDOMAIN_ROAMINGPROFILE=DESKTOP-HRS3987, PROCESSOR_LEVEL=6, SESSIONNAME=Console, ALLUSERSPROFILE=C:\ProgramData, PROCESSOR_ARCHITECTURE=AMD64, PSModulePath=C:\Program Files\WindowsPowerShell\Modules;C:\WINDOWS\system32\WindowsPowerShell\v1.0\Modules, SystemDrive=C:, MAVEN_HOME=D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4, USERNAME=Lenovo, ProgramFiles(x86)=C:\Program Files (x86), FPS_BROWSER_USER_PROFILE_STRING=Default, PATHEXT=.COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC, DriverData=C:\Windows\System32\Drivers\DriverData, OneDriveConsumer=C:\Users\Lenovo\OneDrive, GOPATH=C:\Users\Lenovo\go, ProgramData=C:\ProgramData, ProgramW6432=C:\Program Files, HOMEPATH=\Users\Lenovo, MYSQL_HOME=C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0, PROCESSOR_IDENTIFIER=Intel64 Family 6 Model 165 Stepping 3, GenuineIntel, M2_HOME=D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4, ProgramFiles=C:\Program Files, PUBLIC=C:\Users\Public, windir=C:\WINDOWS, =::=::\, ZES_ENABLE_SYSMAN=1, LOCALAPPDATA=C:\Users\Lenovo\AppData\Local, ChocolateyLastPathUpdate=133456990830913519, USERDOMAIN=DESKTOP-HRS3987, FPS_BROWSER_APP_PROFILE_STRING=Internet Explorer, LOGONSERVER=\\DESKTOP-HRS3987, JAVA_HOME=D:\install\jdk, EFC_9756=1, OneDrive=C:\Users\Lenovo\OneDrive, APPDATA=C:\Users\Lenovo\AppData\Roaming, ChocolateyInstall=C:\ProgramData\chocolatey, CommonProgramFiles=C:\Program Files\Common Files, Path=D:\app\Lenovo\product\11.2.0\dbhome_1\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;D:\install\Git\cmd;D:\install\jdk\bin;D:\install\jdk\jre\bin;C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\install\go\bin;D:\install\Xshell\;D:\install\Xftp\;C:\Program Files\dotnet\;C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\150\Tools\Binn\;D:\tools\x86_64-8.1.0-release-win32-seh-rt_v6-rev0\mingw64\bin;;C:\Program Files\Docker\Docker\resources\bin;C:\Program Files\nodejs\;C:\Users\Lenovo\AppData\Local\Microsoft\WindowsApps;;C:\Users\Lenovo\AppData\Local\Programs\Fiddler;C:\WINDOWS\system32\config\systemprofile\go\bin;C:\WINDOWS\system32\config\systemprofile\AppData\Local\Microsoft\WindowsApps;D:\install\Microsoft VS Code\bin;C:\WINDOWS\system32\config\systemprofile\.dotnet\tools;D:\install\lens\resources\cli\bin;C:\Users\Lenovo\AppData\Roaming\npm, OS=Windows_NT, COMPUTERNAME=DESKTOP-HRS3987, PROCESSOR_REVISION=a503, CLASSPATH=.;D:\install\jdk\lib\dt.jar;D:\install\jdk\lib\tools.jar;, CommonProgramW6432=C:\Program Files\Common Files, ComSpec=C:\WINDOWS\system32\cmd.exe, SystemRoot=C:\WINDOWS, TEMP=C:\Users\Lenovo\AppData\Local\Temp, HOMEDRIVE=C:, USERPROFILE=C:\Users\Lenovo, TMP=C:\Users\Lenovo\AppData\Local\Temp, CommonProgramFiles(x86)=C:\Program Files (x86)\Common Files, NUMBER_OF_PROCESSORS=12, IDEA_INITIAL_DIRECTORY=D:\tools\ideaIU-2021.2.2.win\bin}
Name: commandLineArgs || Source: org.springframework.core.env.CommandLineArgs@6321e813
```

### 八、与其他组件的关系

1. **Environment**

   + `PropertySource` 提供配置数据给 `Environment`，后者是一个表示应用运行环境的接口，用于整合不同的属性源。

2. **ApplicationContext**

   + `PropertySource` 中的属性被 `ApplicationContext` 加载，用于影响和配置应用上下文中的 bean 和其他配置。

3. **@PropertySource 注解**

   + 通过 `@PropertySource` 注解可以将外部配置文件声明为 `PropertySource`，进而集成到 Spring 的 `Environment` 中。

4. **PropertyResolver**

   + `PropertyResolver` 接口提供从 `PropertySource` 解析属性的功能，是连接属性源和其查询机制的桥梁。

5. **@Value 注解**

   + `@Value` 注解允许直接从 `PropertySource` 注入属性值到 Spring 管理的 bean 字段中。

6. **Profile**

   + `PropertySource` 支持基于 Spring Profiles 的不同配置，使得可以根据应用运行的环境加载不同的属性集。

7. **PlaceholderConfigurer类**

   + 类似 `PropertySourcesPlaceholderConfigurer` 的配置器用于解析 `PropertySource` 中的属性占位符，并应用于 Spring bean 配置。

### 九、常见问题

1. **属性值未找到**

   + 如果尝试访问不存在的属性值时返回 `null` 或抛出异常，确保属性文件或其他属性源已正确加载，并检查属性名是否拼写正确。

2. **属性覆盖问题**

   + 当多个 `PropertySource` 包含相同名称的属性时，为了避免不清楚哪个值会被使用的情况，需要明确 `PropertySource` 的优先级顺序，并了解 `Environment` 是如何处理多个属性源中相同属性的。

3. **属性类型转换问题**

   + 从 `PropertySource` 获取的属性值默认为字符串类型，可能需要转换为其他类型。使用合适的类型转换逻辑（如 `ConversionService`）或手动转换属性值。

4. **动态属性更新问题**

   + 在应用运行时动态更新 `PropertySource` 中的属性，并反映这些更改时，可以采用支持动态更新的 `PropertySource` 实现，如使用 Spring Cloud Config。

5. **环境变量和系统属性冲突**

   + 系统环境变量和 Java 系统属性可能具有相同的键，导致预期之外的覆盖。理解并明确 `SystemEnvironmentPropertySource` 和 `SystemPropertiesPropertySource` 的优先级，适当调整以避免冲突。

6. **`@Value` 注解不解析**

   + 使用 `@Value` 注解注入属性时，如果无法正确解析属性值，确保有一个活跃的 `PropertySourcesPlaceholderConfigurer` 在上下文中，用于解析占位符。

7. **配置文件路径问题**

   + 加载外部配置文件时，如果由于路径问题导致文件未被正确加载，检查文件路径是否正确，确保配置文件位于可访问的位置。