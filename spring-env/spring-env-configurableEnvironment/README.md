## ConfigurableEnvironment

- [ConfigurableEnvironment](#configurableenvironment)
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

3. **Environment**

   + [Environment](/spring-env/spring-env-environment/README.md) 接口是 Spring 框架中的一个核心部分，它提供了一个统一的方式来访问各种外部化的配置数据，例如环境变量、JVM 系统属性、命令行参数、以及应用程序配置文件（如 properties 或 YAML 文件）。

### 三、基本描述

`ConfigurableEnvironment` 是 Spring 框架中的一个核心接口，用于灵活地管理和访问应用程序的配置环境。它提供了统一的接口来处理来自不同来源（如属性文件、环境变量、命令行参数）的配置数据，并允许在运行时动态地添加、移除或修改这些属性源。

### 四、主要功能

1. **属性源管理**

   + 管理和操作属性源（PropertySources），如添加、移除和重新排序属性源。这允许从不同来源（如属性文件、环境变量、命令行参数）灵活地配置和访问属性。

2. **配置文件激活**

   + 支持基于激活的配置文件。例如，可以根据不同的环境（开发、测试、生产）激活不同的配置文件，通过 `@Profile` 注解或设置 `spring.profiles.active` 来实现。

3. **属性解析**

   + 提供了解析属性值的功能，支持将配置值转换为各种数据类型（如字符串、数字、布尔值）。

4. **环境抽象**

   + 作为 Spring 环境的一部分，它提供了一个抽象层，用于统一处理不同来源的配置数据。

5. **属性覆盖和优先级**

   + 支持属性覆盖机制，允许某些配置（如环境变量）优先于其他配置（如应用属性文件）。

6. **动态属性修改**

   + 允许在应用运行时动态添加、移除或修改属性源，为应用提供了更大的灵活性和动态配置能力。

### 五、接口源码

`ConfigurableEnvironment` 是 Spring 框架中用于环境配置的核心接口。它不仅提供了设置活动和默认配置文件的能力，还允许操作底层属性源，使得配置更灵活、更符合不同环境的需求。该接口支持添加、移除、重新排序或替换属性源，以及添加新的属性源。

```java
/**
 * 需要由大多数（如果不是所有）{@link Environment} 类型实现的配置接口。
 * 提供了设置活动和默认配置文件的设施，并操纵底层属性源。
 * 通过 {@link ConfigurablePropertyResolver} 超接口，允许客户端设置和验证所需的属性，自定义转换服务等。
 *
 * <h2>操作属性源</h2>
 * <p>可以移除、重新排序或替换属性源；并且可以使用从 {@link #getPropertySources()} 返回的 {@link MutablePropertySources} 实例添加额外的属性源。
 * 以下示例针对 {@link StandardEnvironment} 的实现，但通常适用于任何实现，尽管特定的默认属性源可能有所不同。
 *
 * <h4>示例：添加具有最高搜索优先级的新属性源</h4>
 * <pre class="code">
 * ConfigurableEnvironment environment = new StandardEnvironment();
 * MutablePropertySources propertySources = environment.getPropertySources();
 * Map&lt;String, String&gt; myMap = new HashMap&lt;&gt;();
 * myMap.put("xyz", "myValue");
 * propertySources.addFirst(new MapPropertySource("MY_MAP", myMap));
 * </pre>
 *
 * <h4>示例：移除默认的系统属性属性源</h4>
 * <pre class="code">
 * MutablePropertySources propertySources = environment.getPropertySources();
 * propertySources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)
 * </pre>
 *
 * <h4>示例：出于测试目的模拟系统环境</h4>
 * <pre class="code">
 * MutablePropertySources propertySources = environment.getPropertySources();
 * MockPropertySource mockEnvVars = new MockPropertySource().withProperty("xyz", "myValue");
 * propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
 * </pre>
 *
 * 当 {@link Environment} 被 {@code ApplicationContext} 使用时，重要的是在上下文的 {@link
 * org.springframework.context.support.AbstractApplicationContext#refresh() refresh()} 方法调用之前执行任何此类 {@code PropertySource} 操作。
 * 这确保了所有属性源在容器引导过程中都可用，包括由 {@linkplain
 * org.springframework.context.support.PropertySourcesPlaceholderConfigurer property
 * placeholder configurers} 使用。
 *
 * @author Chris Beams
 * @since 3.1
 * @see StandardEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 */
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {

    /**
     * 设置此 {@code Environment} 的活动配置文件集。配置文件在容器引导期间评估，以确定是否应向容器注册 bean 定义。
     * 使用给定参数替换任何现有的活动配置文件；调用时不带参数以清除当前的活动配置文件集。
     * 使用 {@link #addActiveProfile} 添加配置文件，同时保留现有集合。
     * @throws IllegalArgumentException 如果任何配置文件为 null、为空或仅为空格
     */
    void setActiveProfiles(String... profiles);

    /**
     * 向当前活动配置文件集添加一个配置文件。
     * @throws IllegalArgumentException 如果配置文件为 null、为空或仅为空格
     */
    void addActiveProfile(String profile);

    /**
     * 指定默认情况下激活的配置文件集，如果没有通过 {@link #setActiveProfiles} 明确激活其他配置文件。
     * @throws IllegalArgumentException 如果任何配置文件为 null、为空或仅为空格
     */
    void setDefaultProfiles(String... profiles);

    /**
     * 返回此 {@code Environment} 的 {@link PropertySources}，以可变形式，允许操纵应在解析此 {@code Environment} 对象时搜索的 {@link PropertySource} 对象集。
     * {@link MutablePropertySources} 的各种方法（如 {@link MutablePropertySources#addFirst addFirst}、
     * {@link MutablePropertySources#addLast addLast}、{@link MutablePropertySources#addBefore addBefore} 和
     * {@link MutablePropertySources#addAfter addAfter}）允许对属性源排序进行精细控制。
     */
    MutablePropertySources getPropertySources();

    /**
     * 返回当前 {@link SecurityManager} 允许的情况下 {@link System#getProperties()} 的值，否则返回一个尝试使用 {@link System#getProperty(String)} 调用访问各个键的映射实现。
     */
    Map<String, Object> getSystemProperties();

    /**
     * 返回当前 {@link SecurityManager} 允许的情况下 {@link System#getenv()} 的值，否则返回一个尝试使用 {@link System#getenv(String)} 调用访问各个键的映射实现。
     */
    Map<String, Object> getSystemEnvironment();

    /**
     * 将给定父环境的活动配置文件、默认配置文件和属性源追加到此（子）环境的各自集合中。
     * 对于父子中均存在的同名 {@code PropertySource} 实例，保留子实例并丢弃父实例。
     * 这有助于允许子环境重写属性源，同时避免通过公共属性源类型进行冗余搜索，例如系统环境和系统属性。
     * 活动和默认配置文件名也进行了过滤，以避免混淆和冗余存储。
     * 父环境在任何情况下均保持不变。请注意，调用 {@code merge} 后对父环境进行的任何更改将不会反映在子环境中。
     * 因此，在调用 {@code merge} 之前应注意配置父属性源和配置文件信息。
     */
    void merge(ConfigurableEnvironment parent);
}
```

### 六、主要实现

1. **AbstractEnvironment**

   - `Environment` 接口的抽象基类，提供了共通的实现机制，供其他具体实现类继承。   

2. **StandardEnvironment**

   - 通用实现，处理系统属性和环境变量，适用于大多数标准应用程序。

3. **StandardServletEnvironment**

   - 针对 Servlet-based Web 应用程序，增加对 Servlet 上下文和配置参数的支持。

### 七、最佳实践

使用 `ConfigurableEnvironment` 来管理和操作应用程序的配置环境，包括激活特定的配置文件，获取和合并系统属性和环境变量，以及获取和操作属性源。

```java
public class ConfigurableEnvironmentDemo {

    public static void main(String[] args) {
        // 创建 StandardEnvironment 实例，用于访问属性和配置文件信息
        ConfigurableEnvironment environment = new StandardEnvironment();

        // 设置配置文件
        environment.setActiveProfiles("dev");
        System.out.println("Active Profiles: " + String.join(", ", environment.getActiveProfiles()));

        // 添加配置文件
        environment.addActiveProfile("test");
        System.out.println("Updated Active Profiles: " + String.join(", ", environment.getActiveProfiles()));

        // 设置默认配置文件
        environment.setDefaultProfiles("default");
        System.out.println("Default Profiles: " + String.join(", ", environment.getDefaultProfiles()));

        // 获取系统属性
        Map<String, Object> systemProperties = environment.getSystemProperties();
        System.out.println("System Properties: " + systemProperties);

        // 获取系统环境变量
        Map<String, Object> systemEnvironment = environment.getSystemEnvironment();
        System.out.println("System Environment: " + systemEnvironment);

        // 合并环境变量
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");
        StandardEnvironment standardEnvironment = new StandardEnvironment();
        standardEnvironment.getPropertySources().addFirst(new MapPropertySource("myEnvironment", properties));
        environment.merge(standardEnvironment);

        // 获取可变属性源
        MutablePropertySources propertySources = environment.getPropertySources();
        System.out.println("MutablePropertySources: " + propertySources);
    }
}
```

运行结果发现，`ConfigurableEnvironment` 提供了强大的功能来管理和访问应用程序的配置数据。通过设置活动和默认配置文件，获取系统属性和环境变量，以及操作可变属性源，我们可以根据不同的需求和环境灵活配置应用程序。

```java
Active Profiles: dev
Updated Active Profiles: dev, test
Default Profiles: default
System Properties: {sun.desktop=windows, awt.toolkit=sun.awt.windows.WToolkit, java.specification.version=11, sun.cpu.isalist=amd64, sun.jnu.encoding=GBK, java.class.path=D:\idea-work-space-xcs\spring-reading\spring-env\spring-env-configurableEnvironment\target\classes;D:\tools\repository\org\springframework\spring-context\5.3.10\spring-context-5.3.10.jar;D:\tools\repository\org\springframework\spring-beans\5.3.10\spring-beans-5.3.10.jar;D:\tools\repository\org\springframework\spring-core\5.3.10\spring-core-5.3.10.jar;D:\tools\repository\org\springframework\spring-jcl\5.3.10\spring-jcl-5.3.10.jar;D:\tools\repository\org\springframework\spring-expression\5.3.10\spring-expression-5.3.10.jar;D:\tools\repository\org\springframework\spring-aspects\5.3.10\spring-aspects-5.3.10.jar;D:\tools\repository\org\aspectj\aspectjweaver\1.9.7\aspectjweaver-1.9.7.jar;D:\tools\repository\org\springframework\spring-aop\5.3.10\spring-aop-5.3.10.jar;D:\tools\repository\org\springframework\spring-tx\5.3.10\spring-tx-5.3.10.jar;D:\tools\repository\org\springframework\spring-webmvc\5.3.10\spring-webmvc-5.3.10.jar;D:\tools\repository\org\springframework\spring-web\5.3.10\spring-web-5.3.10.jar, java.vm.vendor=Oracle Corporation, sun.arch.data.model=64, user.variant=, java.vendor.url=http://java.oracle.com/, user.timezone=, os.name=Windows 10, java.vm.specification.version=11, sun.java.launcher=SUN_STANDARD, user.country=CN, sun.boot.library.path=D:\install\jdk-11\bin, sun.java.command=com.xcs.spring.ConfigurableEnvironmentDemo, jdk.debug=release, sun.cpu.endian=little, user.home=C:\Users\Lenovo, user.language=zh, java.specification.vendor=Oracle Corporation, java.version.date=2018-09-25, java.home=D:\install\jdk-11, file.separator=\, java.vm.compressedOopsMode=Zero based, line.separator=
, java.specification.name=Java Platform API Specification, java.vm.specification.vendor=Oracle Corporation, java.awt.graphicsenv=sun.awt.Win32GraphicsEnvironment, user.script=, sun.management.compiler=HotSpot 64-Bit Tiered Compilers, java.runtime.version=11+28, user.name=Lenovo, path.separator=;, os.version=10.0, java.runtime.name=Java(TM) SE Runtime Environment, file.encoding=UTF-8, java.vm.name=Java HotSpot(TM) 64-Bit Server VM, java.vendor.version=18.9, java.vendor.url.bug=http://bugreport.java.com/bugreport/, java.io.tmpdir=C:\Users\Lenovo\AppData\Local\Temp\, java.version=11, user.dir=D:\idea-work-space-xcs\spring-reading, os.arch=amd64, java.vm.specification.name=Java Virtual Machine Specification, java.awt.printerjob=sun.awt.windows.WPrinterJob, sun.os.patch.level=, java.library.path=D:\install\jdk-11\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;D:\app\Lenovo\product\11.2.0\dbhome_1\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;D:\install\Git\cmd;D:\install\jdk\bin;D:\install\jdk\jre\bin;C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\install\go\bin;D:\install\Xshell\;D:\install\Xftp\;C:\Program Files\dotnet\;C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\150\Tools\Binn\;D:\tools\x86_64-8.1.0-release-win32-seh-rt_v6-rev0\mingw64\bin;;C:\Program Files\Docker\Docker\resources\bin;C:\Program Files\nodejs\;C:\Users\Lenovo\AppData\Local\Microsoft\WindowsApps;;C:\Users\Lenovo\AppData\Local\Programs\Fiddler;C:\WINDOWS\system32\config\systemprofile\go\bin;C:\WINDOWS\system32\config\systemprofile\AppData\Local\Microsoft\WindowsApps;D:\install\Microsoft VS Code\bin;C:\WINDOWS\system32\config\systemprofile\.dotnet\tools;D:\install\lens\resources\cli\bin;C:\Users\Lenovo\AppData\Roaming\npm;., java.vendor=Oracle Corporation, java.vm.info=mixed mode, java.vm.version=11+28, sun.io.unicode.encoding=UnicodeLittle, java.class.version=55.0}
System Environment: {USERDOMAIN_ROAMINGPROFILE=DESKTOP-HRS3987, PROCESSOR_LEVEL=6, SESSIONNAME=Console, ALLUSERSPROFILE=C:\ProgramData, PROCESSOR_ARCHITECTURE=AMD64, PSModulePath=C:\Program Files\WindowsPowerShell\Modules;C:\WINDOWS\system32\WindowsPowerShell\v1.0\Modules, SystemDrive=C:, MAVEN_HOME=D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4, USERNAME=Lenovo, ProgramFiles(x86)=C:\Program Files (x86), FPS_BROWSER_USER_PROFILE_STRING=Default, PATHEXT=.COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC, DriverData=C:\Windows\System32\Drivers\DriverData, OneDriveConsumer=C:\Users\Lenovo\OneDrive, GOPATH=C:\Users\Lenovo\go, ProgramData=C:\ProgramData, ProgramW6432=C:\Program Files, HOMEPATH=\Users\Lenovo, MYSQL_HOME=C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0, PROCESSOR_IDENTIFIER=Intel64 Family 6 Model 165 Stepping 3, GenuineIntel, M2_HOME=D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4, ProgramFiles=C:\Program Files, PUBLIC=C:\Users\Public, windir=C:\WINDOWS, =::=::\, ZES_ENABLE_SYSMAN=1, LOCALAPPDATA=C:\Users\Lenovo\AppData\Local, ChocolateyLastPathUpdate=133456990830913519, USERDOMAIN=DESKTOP-HRS3987, FPS_BROWSER_APP_PROFILE_STRING=Internet Explorer, LOGONSERVER=\\DESKTOP-HRS3987, JAVA_HOME=D:\install\jdk, EFC_9756=1, OneDrive=C:\Users\Lenovo\OneDrive, APPDATA=C:\Users\Lenovo\AppData\Roaming, ChocolateyInstall=C:\ProgramData\chocolatey, CommonProgramFiles=C:\Program Files\Common Files, Path=D:\app\Lenovo\product\11.2.0\dbhome_1\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;D:\install\Git\cmd;D:\install\jdk\bin;D:\install\jdk\jre\bin;C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\install\go\bin;D:\install\Xshell\;D:\install\Xftp\;C:\Program Files\dotnet\;C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\150\Tools\Binn\;D:\tools\x86_64-8.1.0-release-win32-seh-rt_v6-rev0\mingw64\bin;;C:\Program Files\Docker\Docker\resources\bin;C:\Program Files\nodejs\;C:\Users\Lenovo\AppData\Local\Microsoft\WindowsApps;;C:\Users\Lenovo\AppData\Local\Programs\Fiddler;C:\WINDOWS\system32\config\systemprofile\go\bin;C:\WINDOWS\system32\config\systemprofile\AppData\Local\Microsoft\WindowsApps;D:\install\Microsoft VS Code\bin;C:\WINDOWS\system32\config\systemprofile\.dotnet\tools;D:\install\lens\resources\cli\bin;C:\Users\Lenovo\AppData\Roaming\npm, OS=Windows_NT, COMPUTERNAME=DESKTOP-HRS3987, PROCESSOR_REVISION=a503, CLASSPATH=.;D:\install\jdk\lib\dt.jar;D:\install\jdk\lib\tools.jar;, CommonProgramW6432=C:\Program Files\Common Files, ComSpec=C:\WINDOWS\system32\cmd.exe, SystemRoot=C:\WINDOWS, TEMP=C:\Users\Lenovo\AppData\Local\Temp, HOMEDRIVE=C:, USERPROFILE=C:\Users\Lenovo, TMP=C:\Users\Lenovo\AppData\Local\Temp, CommonProgramFiles(x86)=C:\Program Files (x86)\Common Files, NUMBER_OF_PROCESSORS=12, IDEA_INITIAL_DIRECTORY=D:\tools\ideaIU-2021.2.2.win\bin}
MutablePropertySources: [PropertiesPropertySource {name='systemProperties'}, SystemEnvironmentPropertySource {name='systemEnvironment'}, MapPropertySource {name='myEnvironment'}]
```

### 八、与其他组件的关系

1. **ApplicationContext**

   + 在 Spring 的 `ApplicationContext` （应用上下文）中，`ConfigurableEnvironment` 被用来加载和管理应用程序的配置数据。`ApplicationContext` 使用 `Environment` 来解析配置文件和属性，支持基于不同环境（开发、测试、生产）的配置。

2. **PropertySource**

   + `ConfigurableEnvironment` 与 `PropertySource` 对象紧密相关。`PropertySources` 是属性源的集合，它们定义了配置数据的来源，如系统环境变量、JVM 属性、配置文件等。`ConfigurableEnvironment` 允许开发者添加、移除或自定义这些属性源。

3. **Profile**

   + `ConfigurableEnvironment` 管理 Spring 配置文件（Profiles），使得可以根据当前环境（例如开发、测试、生产）激活或停用特定的 bean 定义。这在构建具有不同配置需求的多环境应用时非常有用。

4. **PropertyResolver**

   + `ConfigurableEnvironment` 继承自 `ConfigurablePropertyResolver`，这使它能够解析属性值，包括从属性源中查找属性和将属性值转换为各种数据类型。

5. **BeanFactory**

   + 在 Spring 的 Bean 生命周期中，`Environment` 用于影响 bean 的创建和配置。例如，通过 `@Value` 注解可以将环境属性注入到 bean 中，或者通过 `@Conditional` 注解根据环境条件来决定是否创建某个 bean。

6. **PropertySourcesPlaceholderConfigurer**

   + 这是一个特殊的 bean，用于解析配置文件中的占位符。它与 `Environment` 接口协同工作，允许在定义 bean 时使用环境属性。

7. **Spring Boot**

   + 在 Spring Boot 应用中，`ConfigurableEnvironment` 的作用更为突出，因为它支持各种外部化配置和复杂的配置顺序，包括对配置文件的灵活处理和条件化配置。

### 九、常见问题

1. **属性值不被正确读取或解析**

   + 如果在使用 `environment.getProperty()` 获取属性值时返回 `null` 或不正确的值，首先确保属性名称正确无误，并检查属性源是否已被正确添加到 `ConfigurableEnvironment` 中。此外，检查属性源的添加顺序，因为这可能导致预期外的属性覆盖。

2. **激活的配置文件不生效**

   + 如果设置了活动配置文件（Profiles）但应用没有使用这些配置，应该确保在 Spring 容器完全启动之前设置活动配置文件。在某些情况下，可能需要在应用启动参数中显式指定活动的配置文件。

3. **环境合并时出现问题**

   + 当使用 `environment.merge()` 方法合并两个环境时，如果出现属性值不一致或丢失，仔细检查合并逻辑，并理解父子环境中相同属性源的覆盖规则，以确保合并后的环境符合预期。

4. **配置文件（Profiles）之间的冲突**

   + 当多个配置文件被激活时，可能因为不同配置文件中定义了相同的 bean 而出现一些意料之外的行为。解决这个问题的方法是，明确配置文件间的优先级和作用域，尽量减少不同配置文件间的冲突，并合理组织配置文件以避免重复定义。

5. **属性源顺序导致的问题**

   + 属性源的添加顺序可能导致期望的属性值被其他源中的同名属性覆盖。解决这个问题的方法是调整属性源的添加顺序，确保高优先级的属性源被优先考虑。