## PropertiesBeanDefinitionReader

- [PropertiesBeanDefinitionReader](#propertiesbeandefinitionreader)
  - [一、知识储备](#一知识储备)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)
  - [七、与其他组件的关系](#七与其他组件的关系)
  - [八、常见问题](#八常见问题)


### 一、知识储备

1. `Resource`
   - `Resource` 代表一个资源，可以是文件、类路径上的文件、URL 等。它提供了对资源的抽象和访问方法。
   - [点击查看Resource接口](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource)
2. `BeanDefinition`
   - `BeanDefinition` 是 Spring 中描述和管理 Bean 配置的核心概念，它包括了有关 Bean 的信息，如类名、作用域、依赖关系、初始化方法等，而 `AnnotatedBeanDefinitionReader` 的主要任务之一是将使用注解配置的类转化为 `BeanDefinition` 并注册到 Spring 容器中。
   - [点击查看BeanDefinition接口](https://github.com/xuchengsheng/spring-reading/tree/master/spring-beans/spring-bean-beanDefinition)

### 二、基本描述

`PropertiesBeanDefinitionReader` 是 Spring 框架的一个组件，它的主要功能是从属性文件中加载 Bean 的配置信息，然后将这些配置信息解析并转化为 Spring 容器中的 Bean 定义。属性文件的格式通常是键值对，键表示 Bean 的名称，值表示 Bean 的类名或其他配置属性。这个过程允许我们将 Bean 配置与应用程序代码和 XML 配置文件分离，以实现动态加载和管理 Bean 定义，增强了应用程序的可扩展性和配置的灵活性。

### 三、主要功能

1. **加载属性文件**
   + `PropertiesBeanDefinitionReader` 通过 `loadBeanDefinitions(Resource resource)` 方法加载属性文件，其中的 `Resource` 可以是文件路径、类路径或其他资源标识符，指定了包含 Bean 定义信息的属性文件的位置。
2. **解析属性文件**
   + 一旦属性文件被加载，`PropertiesBeanDefinitionReader` 会解析文件中的内容，识别 Bean 的名称、类名以及其他配置属性。
3. **创建 Bean 定义**
   + 根据属性文件中的配置信息，`PropertiesBeanDefinitionReader` 创建相应的 `BeanDefinition` 对象，其中包含了 Bean 的定义信息，包括名称、类、属性、依赖关系等。
4. **注册 Bean 定义**
   + `PropertiesBeanDefinitionReader` 将解析得到的 Bean 定义注册到 Spring IOC 容器中，使这些 Bean 可以在应用程序中被实例化和使用。

### 四、最佳实践

我们创建了一个 Spring Bean 工厂（`DefaultListableBeanFactory`）和一个属性文件读取器（`PropertiesBeanDefinitionReader`），然后从属性文件中加载 Bean 的定义，然后通过 Bean 工厂获取相应的 Bean 对象，以实现动态加载和管理 Bean 配置

```java
public class PropertiesBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(beanFactory);

        // 从properties文件加载bean定义
        reader.loadBeanDefinitions(new ClassPathResource("bean-definitions.properties"));

        // 获取bean
        System.out.println("myBean = " + beanFactory.getBean("myBean"));
        System.out.println("myBean = " + beanFactory.getBean("myBean"));
    }
}
```

属性文件 "`bean-definitions.properties`" 中的内容如下：

```properties
myBean.(class)=com.xcs.spring.bean.MyBean
myBean.message=hello world
myBean.(lazy-init)=true
myBean.(scope)=prototype
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

    @Override
    public String toString() {
        return "MyBean{" +
                "message='" + message + '\'' +
                ", hashCode='0x" + Integer.toHexString(System.identityHashCode(this)).toUpperCase() + '\'' +
                '}';
    }
}
```

运行结果发现，我们从属性文件中动态加载 Bean 配置，同时控制懒加载和作用域，以实现更灵活的 Bean 管理。在这个场景中，每次请求 "`myBeanA`" 都会得到一个新的实例，这是因为作用域设置为 "`prototype`"。

```java
myBean = MyBean{message='hello world', hashCode='0x6646153'}
myBean = MyBean{message='hello world', hashCode='0x45DD4EDA'}
```

### 五、时序图

~~~mermaid
sequenceDiagram
autonumber
Title: PropertiesBeanDefinitionReader时序图

PropertiesBeanDefinitionReaderDemo->>PropertiesBeanDefinitionReader:loadBeanDefinitions(resource)
Note right of PropertiesBeanDefinitionReaderDemo: 从指定的属性文件加载bean定义
PropertiesBeanDefinitionReader->>PropertiesBeanDefinitionReader:loadBeanDefinitions(encodedResource,prefix)
Note over PropertiesBeanDefinitionReader: 从指定的属性文件加载bean定义
PropertiesBeanDefinitionReader->>PropertiesBeanDefinitionReader:registerBeanDefinitions(map,prefix,resourceDescription)
Note over PropertiesBeanDefinitionReader: 注册Map中包含的bean定义
PropertiesBeanDefinitionReader->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)
Note over PropertiesBeanDefinitionReader,DefaultListableBeanFactory: 注册Bean定义到容器

~~~

### 六、源码分析

在`org.springframework.beans.factory.support.PropertiesBeanDefinitionReader#loadBeanDefinitions(resource)`方法中，又调用 `loadBeanDefinitions(encodedResource,prefix)` 方法来实际加载 Bean 定义。

```java
@Override
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
    return loadBeanDefinitions(new EncodedResource(resource), null);
}
```

在`org.springframework.beans.factory.support.PropertiesBeanDefinitionReader#loadBeanDefinitions(encodedResource,prefix)`方法中，主要是从属性文件中加载和管理 Bean 配置，实现了 Spring 中属性文件的解析和注册。

```java
public int loadBeanDefinitions(EncodedResource encodedResource, @Nullable String prefix)
			throws BeanDefinitionStoreException {

    // 如果启用了跟踪级别的日志，则输出加载属性文件的日志信息
    if (logger.isTraceEnabled()) {
        logger.trace("Loading properties bean definitions from " + encodedResource);
    }

    // 创建 Properties 对象，用于存储属性文件中的键值对配置
    Properties props = new Properties();
    try {
        try (InputStream is = encodedResource.getResource().getInputStream()) {
            // 如果指定了编码，使用指定编码读取属性文件
            if (encodedResource.getEncoding() != null) {
                getPropertiesPersister().load(props, new InputStreamReader(is, encodedResource.getEncoding()));
            }
            // 否则，使用默认编码读取属性文件
            else {
                getPropertiesPersister().load(props, is);
            }
        }

        // 注册 Bean 定义，将属性文件中的配置转化为 Bean 定义并注册到容器中
        int count = registerBeanDefinitions(props, prefix, encodedResource.getResource().getDescription());

        // 如果启用了调试级别的日志，则输出已加载的 Bean 定义数量的日志信息
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
        }

        return count;
    }
    catch (IOException ex) {
        // 处理异常，如果在读取或注册过程中发生异常，抛出 BeanDefinitionStoreException 异常
        throw new BeanDefinitionStoreException("Could not parse properties from " + encodedResource.getResource(), ex);
    }
}
```

在`org.springframework.beans.factory.support.PropertiesBeanDefinitionReader#registerBeanDefinitions(map, prefix,resourceDescription)`方法中，首先遍历属性文件中的键值对配置，根据配置的前缀以及键的格式，将合法的 Bean 定义注册到容器中。如果键不符合要求或已经注册，则会忽略。最终，该方法返回已注册的 Bean 定义数量，用于跟踪注册的 Bean 数量。

```java
public int registerBeanDefinitions(Map<?, ?> map, @Nullable String prefix, String resourceDescription)
			throws BeansException {

    if (prefix == null) {
        prefix = "";
    }
    int beanCount = 0;

    // 遍历属性文件中的键值对配置
    for (Object key : map.keySet()) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Illegal key [" + key + "]: only Strings allowed");
        }
        String keyString = (String) key;
        if (keyString.startsWith(prefix)) {
            // 键的格式为：prefix<名称>.属性
            String nameAndProperty = keyString.substring(prefix.length());
            // 查找属性名之前的点号，忽略属性键中的点号。
            int sepIdx ;
            int propKeyIdx = nameAndProperty.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX);
            if (propKeyIdx != -1) {
                sepIdx = nameAndProperty.lastIndexOf(SEPARATOR, propKeyIdx);
            }
            else {
                sepIdx = nameAndProperty.lastIndexOf(SEPARATOR);
            }
            if (sepIdx != -1) {
                String beanName = nameAndProperty.substring(0, sepIdx);
                if (logger.isTraceEnabled()) {
                    logger.trace("Found bean name '" + beanName + "'");
                }
                if (!getRegistry().containsBeanDefinition(beanName)) {
                    // 如果还未注册该 Bean...
                    // 注册 Bean 定义
                    registerBeanDefinition(beanName, map, prefix + beanName, resourceDescription);
                    ++beanCount;
                }
            }
            else {
                // 忽略该键：它不是有效的 Bean 名称和属性，
                // 尽管它以所需的前缀开始。
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid bean name and property [" + nameAndProperty + "]");
                }
            }
        }
    }

    // 返回已注册的 Bean 定义数量
    return beanCount;
}
```

在`org.springframework.beans.factory.support.PropertiesBeanDefinitionReader#registerBeanDefinition(beanName,map,prefix, resourceDescription)`方法中，主要是处理属性文件中的配置并将其转化为 Spring Bean 定义，然后注册到容器中。

```java
protected void registerBeanDefinition(String beanName, Map<?, ?> map, String prefix, String resourceDescription)
			throws BeansException {

    // ... [代码部分省略以简化]

    try {
        AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(
            parent, className, getBeanClassLoader());
        bd.setScope(scope);
        bd.setAbstract(isAbstract);
        bd.setLazyInit(lazyInit);
        bd.setConstructorArgumentValues(cas);
        bd.setPropertyValues(pvs);
        getRegistry().registerBeanDefinition(beanName, bd);
    }
    catch (ClassNotFoundException ex) {
        throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
    }
    catch (LinkageError err) {
        throw new CannotLoadBeanClassException(resourceDescription, beanName, className, err);
    }
}
```

### 七、与其他组件的关系

1. **独立应用程序**
   + 在一些独立的 Spring 应用程序中，我们可以使用 `PropertiesBeanDefinitionReader` 来动态加载 Bean 定义，从而使应用程序的配置更加灵活。这样，应用程序可以根据需要动态配置和管理 Bean。
2. **基于属性文件的配置**
   + 一些应用程序采用基于属性文件的配置方式，将各种 Bean 的配置信息存储在属性文件中。`PropertiesBeanDefinitionReader` 可以用来加载这些配置文件，并将配置信息转化为 Spring 的 Bean 定义。

### 八、常见问题

1. **如何使用 `PropertiesBeanDefinitionReader`？**
   + 使用 `PropertiesBeanDefinitionReader` 需要创建一个实例并与 Spring 的 Bean 工厂（如 `DefaultListableBeanFactory`）结合使用。然后，使用 `loadBeanDefinitions` 方法加载属性文件中的配置，并将 Bean 定义注册到容器中。
2. **属性文件的格式是什么？**
   + 属性文件的格式通常采用键值对（key=value）的形式，其中键表示 Bean 的名称，而值表示 Bean 的配置信息。属性文件中可以包含 Bean 的类、作用域、属性值等信息。
3. **如何配置懒加载和作用域？**
   + 在属性文件中，我们可以使用 `lazy-init` 和 `scope` 来配置懒加载和作用域。例如，`myBean.(lazy-init)=true` 可以配置一个 Bean 为懒加载，而 `myBean.(scope)=prototype` 可以配置 Bean 的作用域为原型。
4. **什么是属性文件中的 Bean 名称？**
   + 属性文件中的 Bean 名称通常与配置的键（key）一致。例如，`myBeanA=(class)=com.example.MyBean` 中的 Bean 名称是 "myBeanA"。
5. **为什么使用 `PropertiesBeanDefinitionReader`？**
   + `PropertiesBeanDefinitionReader` 可以使应用程序更加灵活，允许我们将配置信息从代码中分离出来，从而实现应用程序的可配置性。它还支持动态加载和管理 Bean，适用于需要动态配置的场景。