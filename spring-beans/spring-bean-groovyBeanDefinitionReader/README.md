## GroovyBeanDefinitionReader

- [GroovyBeanDefinitionReader](#groovybeandefinitionreader)
  - [一、知识储备](#一知识储备)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、源码分析](#五源码分析)
  - [六、与其他组件的关系](#六与其他组件的关系)
  - [七、常见问题](#七常见问题)

### 一、知识储备

1. **Groovy 语言**
   + 了解 Groovy 语言的基础语法和特性非常重要，因为 `GroovyBeanDefinitionReader` 用于解析 Groovy 脚本文件。我们应该理解 Groovy 的闭包、动态类型、元编程等概念。
2. **Spring 配置**
   + 了解如何配置 Spring 应用程序上下文，包括 XML 配置、注解配置以及使用 Groovy 脚本文件配置的方式。我们应该知道如何定义和配置 Spring Bean，并了解 Bean 的作用域、生命周期等。
3. **Groovy 脚本编写**
   + 学习如何编写 Groovy 脚本以定义和配置 Spring Bean。这可能包括创建 Bean 定义、引入其他类和资源、执行自定义逻辑等。
4. **Spring 脚本支持**
   + 了解 Spring 如何支持不同类型的配置方式，包括 XML、注解和 Groovy 脚本，以及它们之间的差异和优缺点。

### 二、基本描述

`GroovyBeanDefinitionReader` 是一个用于读取 Groovy 脚本文件并将其解析为 Spring 的 Bean 定义的组件。它是 Spring Framework 的一部分，主要用于支持将 Groovy 脚本用于配置应用程序上下文中的 Bean。

### 三、主要功能

1. **加载 Groovy 脚本文件**
   + `GroovyBeanDefinitionReader` 允许我们加载 Groovy 脚本文件，这些脚本文件可以包含 Spring Bean 的定义以及其他配置信息。
2. **解析 Bean 定义**
   + 它解析 Groovy 脚本中定义的 Bean，包括 Bean 的类型、属性、依赖关系等信息，并将其转化为 Spring Bean 定义。
3. **注册 Bean 定义**
   + 解析后的 Bean 定义将被注册到 Spring 容器中，使这些 Bean 可以在应用程序中被管理和使用。
4. **支持依赖注入**
   + `GroovyBeanDefinitionReader` 支持在 Groovy 脚本中使用依赖注入，允许我们引用其他 Bean 或资源，并将它们注入到正在创建的 Bean 中。
5. **支持自定义逻辑**
   + 我们可以在 Groovy 脚本中编写自定义逻辑来动态计算 Bean 的属性值，从而实现更复杂的配置。
6. **支持Bean的作用域和生命周期**
   + 我们可以在 Groovy 脚本中指定 Bean 的作用域（例如单例或原型）以及 Bean 的生命周期回调方法（例如初始化方法和销毁方法）。

### 四、最佳实践

首先通过`GroovyBeanDefinitionReader`加载和注册了Spring Bean定义，从`my-beans.groovy` Groovy文件中创建了`MyService` Bean实例，并通过该Bean打印一条消息，。

```java
public class GroovyBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // 创建一个 Spring IOC 容器
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // 创建一个 GroovyBeanDefinitionReader
        GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader(factory);

        // 加载 Groovy 文件并注册 Bean 定义
        reader.loadBeanDefinitions(new ClassPathResource("my-beans.groovy"));

        // 获取MyService
        MyService myService = factory.getBean(MyService.class);
        // 打印消息
        myService.showMessage();
    }
}
```

定义一个`MyService`接口

```java
public interface MyService {
    void showMessage();
}
```

我们在`classpath:my-beans.groovy`文件中，定义了一个名为`MyServiceImpl`的Groovy类，实现了`MyService`接口和`InitializingBean`接口，其中包含了一个`message`字段和对应的setter和getter方法，以及在初始化时会调用的`afterPropertiesSet`方法。通过`Groovy DSL`的`beans`闭包，我们定义了一个名为`myServiceImpl`的Spring Bean，指定其类型为`MyServiceImpl`，并设置了`message`属性为"`hello world`"。

```groovy
import com.xcs.spring.service.MyService
import org.springframework.beans.factory.InitializingBean

class MyServiceImpl implements MyService, InitializingBean {
    private String message

    void setMessage(String message) {
        this.message = message
    }

    String getMessage() {
        return message
    }

    @Override
    void afterPropertiesSet() throws Exception {
        System.out.println("MyServiceImpl.afterPropertiesSet")
    }

    @Override
    void showMessage() {
        System.out.println(message)
    }
}

beans {
    myServiceImpl(MyServiceImpl) {
        message = "hello world"
    }
}
```

运行结果发现，我们成功创建了`myServiceImpl` Bean，并在初始化时调用了`afterPropertiesSet`方法。最后，`myServiceImpl` Bean 打印了"`hello world`"消息，这是我们在配置文件中设置的消息内容。这表明我们的`Groovy DSL`配置和Spring容器设置正常工作。

```java
MyServiceImpl.afterPropertiesSet
hello world
```

### 五、源码分析

在`org.springframework.beans.factory.support.AbstractBeanDefinitionReader#loadBeanDefinitions(location)`方法中，又调用了 `loadBeanDefinitions(encodedResource)` 方法，同时将 `resource` 包装成一个 `EncodedResource` 对象。

```java
@Override
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
    return loadBeanDefinitions(new EncodedResource(resource));
}
```

在`org.springframework.beans.factory.support.AbstractBeanDefinitionReader#loadBeanDefinitions(location,actualResources)`方法中，主要用于加载 Groovy 配置文件中的 Bean 定义。如果文件扩展名是 "`.xml`"，则会委托给标准的 `XmlBeanDefinitionReader` 进行加载。否则，它使用 Groovy 脚本加载 Bean 定义，创建 `GroovyShell` 和 `Binding` 对象，通过 Groovy 脚本执行加载操作。

```java
public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
    // 检查是否为 XML 文件，如果是，将其重定向到 "standard" XmlBeanDefinitionReader 加载。
    String filename = encodedResource.getResource().getFilename();
    if (StringUtils.endsWithIgnoreCase(filename, ".xml")) {
        return this.standardXmlBeanDefinitionReader.loadBeanDefinitions(encodedResource);
    }

    // 如果不是 XML 文件，执行 Groovy Bean 定义加载。
    if (logger.isTraceEnabled()) {
        logger.trace("Loading Groovy bean definitions from " + encodedResource);
    }

    // 创建 Closure 对象 "beans" 用于处理 Bean 定义。
    @SuppressWarnings("serial")
    Closure<Object> beans = new Closure<Object>(this) {
        @Override
        public Object call(Object... args) {
            // 调用 invokeBeanDefiningClosure 方法处理 Bean 定义。
            invokeBeanDefiningClosure((Closure<?>) args[0]);
            return null;
        }
    };

    // 创建 Binding 对象，用于将变量绑定到 Groovy 脚本。
    Binding binding = new Binding() {
        @Override
        public void setVariable(String name, Object value) {
            if (currentBeanDefinition != null) {
                // 如果存在当前 Bean 定义，将属性应用到 Bean 定义中。
                applyPropertyToBeanDefinition(name, value);
            }
            else {
                super.setVariable(name, value);
            }
        }
    };
    binding.setVariable("beans", beans);

    // 记录加载 Bean 定义之前的数量。
    int countBefore = getRegistry().getBeanDefinitionCount();

    try {
        // 创建 GroovyShell，并使用 Binding 绑定变量。
        GroovyShell shell = new GroovyShell(getBeanClassLoader(), binding);
        // 评估 Groovy 脚本以加载 Bean 定义。
        shell.evaluate(encodedResource.getReader(), "beans");
    }
    catch (Throwable ex) {
        throw new BeanDefinitionParsingException(new Problem("Error evaluating Groovy script: " + ex.getMessage(),
                new Location(encodedResource.getResource()), null, ex));
    }

    // 计算加载后的 Bean 定义数量。
    int count = getRegistry().getBeanDefinitionCount() - countBefore;
    if (logger.isDebugEnabled()) {
        logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
    }
    return count;
}
```

在`org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader#invokeMethod`方法中，方法用于处理 `Groovy DSL` 中的方法调用，根据方法名和参数执行不同的操作，包括创建 Bean 定义、处理引用和其他操作。

```java
@Override
public Object invokeMethod(String name, Object arg) {
    // 将参数转换为 Object 数组。
    Object[] args = (Object[]) arg;

    if ("beans".equals(name) && args.length == 1 && args[0] instanceof Closure) {
        // 如果方法名是 "beans"，并且只有一个参数是闭包，调用 beans 方法。
        return beans((Closure<?>) args[0]);
    }
    else if ("ref".equals(name)) {
        // 如果方法名是 "ref"，处理 Bean 引用。
        String refName;
        if (args[0] == null) {
            throw new IllegalArgumentException("Argument to ref() is not a valid bean or was not found");
        }
        if (args[0] instanceof RuntimeBeanReference) {
            refName = ((RuntimeBeanReference) args[0]).getBeanName();
        }
        else {
            refName = args[0].toString();
        }
        boolean parentRef = false;
        if (args.length > 1 && args[1] instanceof Boolean) {
            parentRef = (Boolean) args[1];
        }
        return new RuntimeBeanReference(refName, parentRef);
    }
    else if (this.namespaces.containsKey(name) && args.length > 0 && args[0] instanceof Closure) {
        // 如果方法名匹配已知的命名空间，且参数包含闭包，处理动态元素。
        GroovyDynamicElementReader reader = createDynamicElementReader(name);
        reader.invokeMethod("doCall", args);
    }
    else if (args.length > 0 && args[0] instanceof Closure) {
        // 如果参数包含闭包，处理抽象 Bean 定义。
        return invokeBeanDefiningMethod(name, args);
    }
    else if (args.length > 0 &&
             (args[0] instanceof Class || args[0] instanceof RuntimeBeanReference || args[0] instanceof Map)) {
        // 如果参数包含类、RuntimeBeanReference 对象或映射，处理 Bean 定义。
        return invokeBeanDefiningMethod(name, args);
    }
    else if (args.length > 1 && args[args.length - 1] instanceof Closure) {
        // 如果参数包含闭包且是最后一个参数，处理 Bean 定义。
        return invokeBeanDefiningMethod(name, args);
    }
    MetaClass mc = DefaultGroovyMethods.getMetaClass(getRegistry());
    if (!mc.respondsTo(getRegistry(), name, args).isEmpty()) {
        // 如果以上条件都不匹配，尝试调用 Groovy MetaClass 中的相应方法。
        return mc.invokeMethod(getRegistry(), name, args);
    }
    return this;
}
```

在`org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader#invokeBeanDefiningMethod`方法中，根据传入的参数创建或更新 Bean 定义，并将其注册到 `BeanFactory` 中。

```java
private GroovyBeanDefinitionWrapper invokeBeanDefiningMethod(String beanName, Object[] args) {
    boolean hasClosureArgument = (args[args.length - 1] instanceof Closure);

    if (args[0] instanceof Class) {
        Class<?> beanClass = (Class<?>) args[0];
        if (hasClosureArgument) {
            // 如果参数包含闭包，创建 GroovyBeanDefinitionWrapper，解析构造参数。
            if (args.length - 1 != 1) {
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(
                    beanName, beanClass, resolveConstructorArguments(args, 1, args.length - 1));
            }
            else {
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass);
            }
        }
        else  {
            // 如果没有闭包参数，创建 GroovyBeanDefinitionWrapper，解析构造参数。
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(
                beanName, beanClass, resolveConstructorArguments(args, 1, args.length));
        }
    }
    else if (args[0] instanceof RuntimeBeanReference) {
        // 如果参数是 RuntimeBeanReference，创建 GroovyBeanDefinitionWrapper 表示引用其他 Bean。
        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
        this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(((RuntimeBeanReference) args[0]).getBeanName());
    }
    else if (args[0] instanceof Map) {
        // 如果参数是映射，可能表示具名构造参数或工厂方法。
        if (args.length > 1 && args[1] instanceof Class) {
            // 具名构造参数情况，解析构造参数并设置属性。
            List<Object> constructorArgs =
                resolveConstructorArguments(args, 2, hasClosureArgument ? args.length - 1 : args.length);
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, (Class<?>) args[1], constructorArgs);
            Map<?, ?> namedArgs = (Map<?, ?>) args[0];
            for (Map.Entry<?, ?> entity : namedArgs.entrySet()) {
                String propName = (String) entity.getKey();
                setProperty(propName, entity.getValue());
            }
        }
        else {
            // 工厂方法情况，解析参数并设置工厂相关属性。
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
            Map.Entry<?, ?> factoryBeanEntry = ((Map<?, ?>) args[0]).entrySet().iterator().next();
            int constructorArgsTest = (hasClosureArgument ? 2 : 1);
            if (args.length > constructorArgsTest){
                // 存在构造参数，解析构造参数。
                int endOfConstructArgs = (hasClosureArgument ? args.length - 1 : args.length);
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null,
                                                                             resolveConstructorArguments(args, 1, endOfConstructArgs));
            }
            else {
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
            }
            this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(factoryBeanEntry.getKey().toString());
            this.currentBeanDefinition.getBeanDefinition().setFactoryMethodName(factoryBeanEntry.getValue().toString());
        }
    }
    else if (args[0] instanceof Closure) {
        // 如果参数是闭包，创建 GroovyBeanDefinitionWrapper 表示抽象 Bean 定义。
        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
        this.currentBeanDefinition.getBeanDefinition().setAbstract(true);
    }
    else {
        // 其他情况，解析构造参数。
        List<Object> constructorArgs =
            resolveConstructorArguments(args, 0, hasClosureArgument ? args.length - 1 : args.length);
        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, constructorArgs);
    }

    if (hasClosureArgument) {
        // 如果存在闭包参数，设置闭包的代理和解析策略，并调用闭包处理 Bean 定义。
        Closure<?> callable = (Closure<?>) args[args.length - 1];
        callable.setDelegate(this);
        callable.setResolveStrategy(Closure.DELEGATE_FIRST);
        callable.call(this.currentBeanDefinition);
    }

    // 获取 Bean 定义并将其注册到 BeanFactory。
    GroovyBeanDefinitionWrapper beanDefinition = this.currentBeanDefinition;
    this.currentBeanDefinition = null;
    beanDefinition.getBeanDefinition().setAttribute(GroovyBeanDefinitionWrapper.class.getName(), beanDefinition);
    getRegistry().registerBeanDefinition(beanName, beanDefinition.getBeanDefinition());
    return beanDefinition;
}
```

### 六、与其他组件的关系

1. **`GenericGroovyApplicationContext`**
   + 这是一个实现了 `org.springframework.context.ApplicationContext` 接口的 Spring 应用上下文类。它允许你加载 Groovy 配置文件，并使用 `GroovyBeanDefinitionReader` 来解析 `Groovy DSL`。
2. **`GroovyScriptFactory`**
   + 这个类允许你将 Groovy 脚本作为 Spring Bean 定义加载，使用 `GroovyBeanDefinitionReader` 解析 Groovy 脚本。

### 七、常见问题

1. **找不到类或 Bean 定义错误**
   - 在 `Groovy DSL` 文件中引用了不存在的类或 Bean 定义，导致找不到类的错误。我们需要确保类和 Bean 定义的名称和路径是正确的，确保 Groovy 文件中的引用与实际类名一致。
2. **Groovy 语法错误**
   - `Groovy DSL` 文件中存在语法错误，导致加载失败。我们需要仔细检查 `Groovy DSL` 文件中的语法，确保没有语法错误。可以使用 `Groovy IDE` 或编辑器来辅助检查语法。
3. **类路径问题**
   - `Groovy DSL` 文件中引用的类不在类路径上，导致加载失败。我们需要确保引用的类位于类路径上，可以通过修改类路径或将类文件放在正确的位置来解决。
4. **Groovy 版本兼容性**
   - 使用的 Groovy 版本不兼容 Spring 版本，导致加载失败。我们需要确保 Groovy 版本与所使用的 Spring 版本兼容。查看 Spring 文档以获取支持的 Groovy 版本信息。