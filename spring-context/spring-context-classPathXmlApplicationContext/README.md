## ClassPathXmlApplicationContext

- [ClassPathXmlApplicationContext](#classpathxmlapplicationcontext)
  - [一、基本信息](#一基本信息)
  - [二、知识储备](#二知识储备)
  - [三、基本描述](#三基本描述)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. **XmlBeanDefinitionReader**

   + [XmlBeanDefinitionReader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-xmlBeanDefinitionReader/README.md)是Spring Framework中的一个类，用于加载和解析XML格式的Bean定义配置文件，将配置文件中定义的Bean元数据信息提取为Spring容器内部的Bean定义对象，进而实现IOC容器的构建和管理。这类负责读取XML配置文件，解析Bean的定义信息（包括ID、类名、属性、依赖等），并将这些定义注册到Spring应用程序上下文，使我们能够方便地配置和管理应用程序中的各种Bean组件。

### 三、基本描述

`ClassPathXmlApplicationContext` 是 Spring 框架中用于从类路径（classpath）加载 XML 配置文件并初始化 Spring 容器的一种方式。它是 `ApplicationContext` 接口的实现类之一，负责读取配置文件，解析配置信息，然后创建和管理 Spring 容器中的 bean 实例。

### 四、主要功能

1. **加载配置文件** 

   + 主要功能是加载指定的 XML 配置文件，该配置文件包含了应用程序中各个组件（bean）的定义、依赖关系、配置信息等。

2. **容器初始化**

   + `ClassPathXmlApplicationContext` 在被实例化时，会读取并解析配置文件，然后初始化 Spring 容器。这个过程包括创建和管理 bean 实例、解决 bean 之间的依赖关系等。

3. **获取 bean 实例**

   + 通过容器的 `getBean` 方法，可以从容器中获取在配置文件中定义的 bean 实例。

4. **IoC（控制反转）** 

   + `ClassPathXmlApplicationContext` 是 IoC 容器的一种实现，它负责管理和控制组件的生命周期。在容器初始化时，会根据配置文件中的信息实例化和装配 bean，而不是由应用程序代码直接创建对象。

5. **依赖注入** 

   + 容器通过读取配置文件中的信息，自动解决 bean 之间的依赖关系。这意味着在配置文件中声明的 bean 可以通过属性注入或构造函数注入的方式获取其依赖的其他 bean。

6. **AOP（面向切面编程）**

   +  `ClassPathXmlApplicationContext` 支持通过配置文件定义切面和通知，实现横切关注点的分离，使得应用程序的关注点更加清晰和模块化。

7. **事件传播**

   + Spring 容器支持事件机制，`ClassPathXmlApplicationContext` 可以发布应用程序中发生的事件，以便其他组件能够监听并作出相应的响应。


### 五、最佳实践

通过 `ClassPathXmlApplicationContext` 构造方法创建 Spring 容器的实例，加载类路径下的 "beans.xml" 配置文件。使用容器的 `getBean` 方法，通过指定 bean 的类型（`MyBean.class`）获取在配置文件中定义的 bean 实例，并将其打印出来。

```java
public class ClassPathXmlApplicationContextDemo {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans.xml");
        System.out.println("MyBean = " + context.getBean(MyBean.class));
    }
}
```

加载类路径下名为 "`classpath:beans.xml`" 的资源文件的内容。在我们的示例配置文件中，这个资源文件定义了一个名为 "`myBean`" 的 Spring Bean，该 Bean 具有一个属性 "message"，其值设置为 "Hello World"。

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myBean" class="com.xcs.spring.bean.MyBean">
        <property name="message" value="Hello World"/>
    </bean>

</beans>
```

`MyBean` 的Java类，代表了一个简单的Java Bean。

```java
public class MyBean {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: ClassPathXmlApplicationContext时序图
ClassPathXmlApplicationContextDemo->>ClassPathXmlApplicationContext:ClassPathXmlApplicationContext(configLocation)
Note over ClassPathXmlApplicationContextDemo,ClassPathXmlApplicationContext: 创建ClassPathXmlApplicationContext实例

ClassPathXmlApplicationContext->>ClassPathXmlApplicationContext:ClassPathXmlApplicationContext(configLocations,refresh,parent)
Note over ClassPathXmlApplicationContext: 通过多个配置文件路径、刷新标志和父上下文构造实例

ClassPathXmlApplicationContext->>AbstractApplicationContext:refresh()
Note over ClassPathXmlApplicationContext,AbstractApplicationContext: 刷新应用程序上下文

AbstractApplicationContext->>AbstractApplicationContext:obtainFreshBeanFactory()
Note over AbstractApplicationContext: 获取刷新过的bean工厂

AbstractApplicationContext->>AbstractRefreshableApplicationContext:refreshBeanFactory()
Note over AbstractApplicationContext,AbstractRefreshableApplicationContext: 调用子类的refreshBeanFactory()

AbstractRefreshableApplicationContext->>AbstractXmlApplicationContext:loadBeanDefinitions(beanFactory)
Note over AbstractRefreshableApplicationContext,AbstractXmlApplicationContext: 调用子类的loadBeanDefinitions方法

AbstractXmlApplicationContext->>XmlBeanDefinitionReader:new XmlBeanDefinitionReader(beanFactory)
Note over AbstractXmlApplicationContext,XmlBeanDefinitionReader: 创建XmlBeanDefinitionReader实例

XmlBeanDefinitionReader->>AbstractXmlApplicationContext:返回Bean定义读取器
Note over XmlBeanDefinitionReader,AbstractXmlApplicationContext: 返回Bean定义读取器

AbstractXmlApplicationContext->>AbstractXmlApplicationContext:loadBeanDefinitions(reader)
Note over AbstractXmlApplicationContext: 调用XmlBeanDefinitionReader的loadBeanDefinitions方法

AbstractXmlApplicationContext->>XmlBeanDefinitionReader:reader.loadBeanDefinitions(configLocations)
Note over AbstractXmlApplicationContext,XmlBeanDefinitionReader: 加载配置文件中的Bean定义

AbstractApplicationContext->>AbstractRefreshableApplicationContext:getBeanFactory()
Note over AbstractApplicationContext,AbstractRefreshableApplicationContext: 获取Bean工厂

AbstractRefreshableApplicationContext->>AbstractApplicationContext:返回Bean工厂
Note over AbstractApplicationContext: 返回Bean工厂

~~~

### 七、源码分析

在`org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(configLocation)`方法中，又调用了另外一个构造方法。

```java
public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
    this(new String[] {configLocation}, true, null);
}
```

在`org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(configLocations, refresh,parent)`方法中，首先设置了父应用程序上下文和配置文件位置，然后根据是否需要刷新，决定是否立即执行应用程序上下文的刷新操作。

```java
public ClassPathXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {

    super(parent);
    setConfigLocations(configLocations);
    if (refresh) {
        refresh();
    }
}
```

在`org.springframework.context.support.AbstractApplicationContext#refresh`方法中，调用 `obtainFreshBeanFactory` 方法来刷新内部的 bean 工厂，从而触发容器的初始化过程。

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [代码部分省略以简化]
    
    // 该方法由子类实现，用于刷新内部的 bean 工厂。
    ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
    
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#obtainFreshBeanFactory`方法中，首先调用 `refreshBeanFactory` 方法来刷新内部的 bean 工厂，并最终返回这个刷新过的 bean 工厂。

```java
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
    refreshBeanFactory();
    return getBeanFactory();
}
```

在`org.springframework.context.support.AbstractRefreshableApplicationContext#refreshBeanFactory`方法中，首先检查是否已存在 bean 工厂，如果存在则先销毁已有的 bean 并关闭 bean 工厂。接着，创建一个新的 `DefaultListableBeanFactory` 实例，设置其序列化 ID 为容器的 ID，然后调用 `customizeBeanFactory` 定制 bean 工厂。随后，通过 `loadBeanDefinitions` 加载 bean 的定义，包括读取配置文件、解析 bean 的定义等。

```java
@Override
protected final void refreshBeanFactory() throws BeansException {
   // 如果已经存在 bean 工厂，则销毁已有的 bean 并关闭 bean 工厂
   if (hasBeanFactory()) {
      destroyBeans();
      closeBeanFactory();
   }

   try {
      // 创建一个新的 DefaultListableBeanFactory 实例
      DefaultListableBeanFactory beanFactory = createBeanFactory();
      // 设置 bean 工厂的序列化 ID 为容器的 ID
      beanFactory.setSerializationId(getId());
      // 定制 bean 工厂，由子类实现
      customizeBeanFactory(beanFactory);
      // 加载 bean 的定义，包括读取配置文件、解析 bean 的定义等
      loadBeanDefinitions(beanFactory);
      // 将创建好的 bean 工厂赋值给容器
      this.beanFactory = beanFactory;
   } catch (IOException ex) {
      // 如果在解析 bean 定义时发生 I/O 错误，则抛出 ApplicationContextException 异常
      throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
   }
}
```

在`org.springframework.context.support.AbstractXmlApplicationContext#loadBeanDefinitions(beanFactory)`方法中，首先创建了一个 `XmlBeanDefinitionReader` 对象，用于读取 XML 格式的 bean 定义。然后，配置了 bean 定义阅读器的环境、资源加载器和实体解析器等属性。接着，允许子类通过 `initBeanDefinitionReader` 提供自定义的初始化操作。最后，调用阅读器的加载方法，实际上读取配置文件，解析 bean 定义，将它们注册到 bean 工厂中。

```java
protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
    // 为给定的 BeanFactory 创建一个新的 XmlBeanDefinitionReader
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

    // 使用该上下文的资源加载环境配置 bean 定义阅读器
    beanDefinitionReader.setEnvironment(this.getEnvironment());
    // 将资源加载器设置为当前应用程序上下文
    beanDefinitionReader.setResourceLoader(this);
    // 设置实体解析器为 ResourceEntityResolver，用于解析 bean 定义中的实体引用
    beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

    // 允许子类提供对阅读器的自定义初始化，然后继续实际加载 bean 定义
    initBeanDefinitionReader(beanDefinitionReader);
    // 调用阅读器的加载方法，实际上会读取配置文件，解析 bean 定义，将它们注册到 bean 工厂中
    loadBeanDefinitions(beanDefinitionReader);
}

```

在`org.springframework.context.support.AbstractXmlApplicationContext#loadBeanDefinitions(reader)`方法中，首先获取配置资源（`Resource`）数组，这些资源通过类路径方式加载的配置文件。如果配置资源不为空，则通过 `XmlBeanDefinitionReader` 对象加载这些资源中的 bean 定义。接着，获取配置文件路径数组，这些路径通过字符串形式指定的配置文件路径。如果配置文件路径不为空，则同样通过`XmlBeanDefinitionReader`来加载这些路径中的 bean 定义。

> **关于`reader.loadBeanDefinitions`方法的源码分析已经在另外一篇关于[XmlBeanDefinitionReader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-xmlBeanDefinitionReader/README.md)类的博客中详细分析了。**

```java
protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
    // 获取配置资源（Resource）数组，通常表示通过类路径或其他方式加载的配置文件
    Resource[] configResources = getConfigResources();
    // 如果配置资源不为空，则通过阅读器加载这些资源中的 bean 定义
    if (configResources != null) {
        reader.loadBeanDefinitions(configResources);
    }

    // 获取配置文件路径数组，通常表示通过字符串形式指定的配置文件路径
    String[] configLocations = getConfigLocations();
    // 如果配置文件路径不为空，则通过阅读器加载这些路径中的 bean 定义
    if (configLocations != null) {
        reader.loadBeanDefinitions(configLocations);
    }
}
```