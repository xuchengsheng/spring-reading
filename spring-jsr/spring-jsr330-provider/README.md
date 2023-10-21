## javax.inject.Provider

- [javax.inject.Provider](#javaxinjectprovider)
  - [一、基本信息](#一基本信息)
  - [二、接口描述](#二接口描述)
  - [三、接口源码](#三接口源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
    - [Provider注册](#provider注册)
    - [Provider实例化](#provider实例化)
    - [Provider泛型提取](#provider泛型提取)
  - [七、源码分析](#七源码分析)
    - [Provider注册](#provider注册-1)
    - [Provider实例化](#provider实例化-1)
    - [Provider泛型提取](#provider泛型提取-1)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN]() 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [Provider源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-jsr/spring-jsr330-provider)

### 二、接口描述

`javax.inject.Provider<T>` 是一个在 JSR-330 "Dependency Injection for Java" 规范中定义的接口，它用于延迟地获取注入的依赖项。Spring 框架支持 JSR-330，并因此也支持 `javax.inject.Provider` 接口。

### 三、接口源码

`Provider<T>` 接口的目的主要是为了允许实时、按需的创建和提供对象。

```java
/**
 * 代表一个特定类型T的实例提供者。
 * 它是 JSR-330 "Java的依赖注入" 规范的一部分。
 * 该接口允许实现每次调用 get 方法时产生新的实例（类似于工厂方法），
 * 或者为多次调用返回相同的实例。
 * 
 * 它在以下情况尤为有用：
 * - 懒加载或按需创建对象。
 * - 检索多个原型作用域 bean 的实例。
 * - 在某些情况下解决循环依赖。
 */
public interface Provider<T> {

    /**
     * 提供 T 的一个实例。根据实现情况，
     * 每次调用此方法时都可能创建一个新的实例，
     * 或者为多次调用返回相同的实例。
     *
     * @return T 的一个实例
     * @throws RuntimeException 如果在提供实例时出现错误。
     * 例如，在 T 的注入或构造过程中出现错误。
     * 建议调用者不要捕获这些异常，因为行为可能因不同的注入器实现或配置而异。
     */
    T get();
}
```

### 四、主要功能

1. **按需创建和提供实例**
   + 当我们请求一个对象的实例时，`Provider<T>` 可以为我们提供一个实例，而不需要直接实例化或查找对象。这为按需创建对象提供了一种机制。
2. **处理原型作用域的 beans**
   + 在Spring中，如果我们有一个原型作用域的bean，使用 `Provider<T>` 可以确保每次调用 `get()` 方法时都获得一个新的bean实例。
3. **解决循环依赖**
   + 在某些情况下，特别是当涉及到原型作用域的bean时，使用 `Provider<T>` 可以帮助解决因直接注入而产生的循环依赖问题。
4. **提供更多的灵活性**
   + 与直接注入bean相比，使用 `Provider<T>` 可以让我们决定何时以及如何获取bean实例。这可以为那些需要在运行时基于特定条件决定是否创建或获取bean的应用程序提供更大的灵活性。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showService`方法，

```java
public class ProviderApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

在`MyConfiguration`类中，使用了`@ComponentScan("com.xcs.spring")`注解告诉 Spring 在指定的包（在这里是 "`com.xcs.spring`"）及其子包中搜索带有 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类，并将它们自动注册为 beans。这样，spring就不必为每个组件明确写一个 bean 定义。Spring 会自动识别并注册它们。

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

}
```

`MyController`，它使用`@Autowired`注入了一个`Provider<MyService>`。然后，`MyController`类的`showService`方法中使用`myServiceProvider`来获取`MyService`的两个实例。

```java
@Controller
public class MyController {

    @Autowired
    private Provider<MyService> myServiceProvider;

    public void showService(){
        System.out.println("myServiceProvider1 = " + myServiceProvider.get());
        System.out.println("myServiceProvider2 = " + myServiceProvider.get());
    }
}
```

`MyService` 是一个简单的服务类，但我们没有定义任何方法或功能。

```java
@Service
public class MyService {
    
}
```

运行结果发现，`myServiceProvider1` 和 `myServiceProvider2` 两次获取到的 `MyService` 实例具有相同的对象引用（`@235ecd9f`）。这说明 `MyService` bean 是单例作用域的。

| :warning: 注意！                                             |
| :----------------------------------------------------------- |
| 在 Spring 中，默认的作用域是单例（singleton），这意味着在整个 Spring 容器中，一个特定的 bean 定义只有一个实例。因此，无论我们调用多少次 `myServiceProvider.get()`，它都会返回相同的 `MyService` 实例。如果我们想每次都获得一个新的 `MyService` 实例，我们需要将 `MyService` 定义为原型作用域（prototype）。 |

```java
myServiceProvider1 = com.xcs.spring.service.MyService@235ecd9f
myServiceProvider2 = com.xcs.spring.service.MyService@235ecd9f
```

### 六、时序图

#### Provider注册

~~~mermaid
sequenceDiagram
Title: Provider注册时序图

NamedApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)
Note right of AnnotationConfigApplicationContext: 创建一个基于注解的应用上下文

AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext()
Note right of AnnotationConfigApplicationContext: AnnotationConfigApplicationContext 构造器

AnnotationConfigApplicationContext->>GenericApplicationContext:GenericApplicationContext()
Note right of GenericApplicationContext: 基于GenericApplicationContext的构造

GenericApplicationContext->>DefaultListableBeanFactory: new DefaultListableBeanFactory()
Note right of DefaultListableBeanFactory: 创建一个默认的可列出的bean工厂

DefaultListableBeanFactory->>DefaultListableBeanFactory: ClassUtils.forName("javax.inject.Provider",classLoader)
Note right of DefaultListableBeanFactory: 执行DefaultListableBeanFactory内的静态代码块
~~~

#### Provider实例化

~~~mermaid
sequenceDiagram
Title: Provider实例化时序图

AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor: postProcessProperties(pvs,bean,beanName)
Note right of AutowiredAnnotationBeanPostProcessor: 处理bean属性的注入

AutowiredAnnotationBeanPostProcessor->>InjectionMetadata: inject(target,beanName,pvs)
Note right of InjectionMetadata: 对目标bean执行依赖注入

InjectionMetadata->>AutowiredFieldElement: inject(bean,beanName,pvs)
Note right of AutowiredFieldElement: 对具体的字段执行依赖注入

AutowiredFieldElement->>AutowiredFieldElement: resolveFieldValue(field,bean,beanName)
Note right of AutowiredFieldElement: 解析字段值以进行注入

AutowiredFieldElement->>DefaultListableBeanFactory: resolveDependency(desc, beanName, autowiredBeanNames, typeConverter)
Note right of DefaultListableBeanFactory: 解析指定的依赖关系

DefaultListableBeanFactory->>Jsr330Factory: new Jsr330Factory()
Note right of Jsr330Factory: 创建一个新的JSR-330工厂

Jsr330Factory->>DefaultListableBeanFactory: 返回Factory
Note right of DefaultListableBeanFactory: 获取JSR-330工厂实例

DefaultListableBeanFactory->>Jsr330Factory: createDependencyProvider(descriptor,beanName)
Note right of Jsr330Factory: 创建依赖提供者

Jsr330Factory->>Jsr330Provider: new Jsr330Provider(descriptor, beanName)
Note right of Jsr330Provider: 创建一个新的JSR-330提供者

Jsr330Provider->>Jsr330Factory: 返回Provider
Note right of Jsr330Factory: 获取JSR-330提供者实例

Jsr330Factory->>DefaultListableBeanFactory: 返回Provider
Note right of DefaultListableBeanFactory: 获取JSR-330提供者

DefaultListableBeanFactory->>AutowiredFieldElement: 返回Provider
Note right of AutowiredFieldElement: 

AutowiredFieldElement->>Field: field.set(bean, Provider)
Note right of Field: 使用Provider设置字段值

~~~

#### Provider泛型提取

~~~mermaid
sequenceDiagram
Title: Provider泛型提取时序图

MyController->>Jsr330Provider:get()
Note over Jsr330Provider: MyController尝试获取Bean。

Jsr330Provider->>DependencyObjectProvider:getValue()
Note over DependencyObjectProvider: Jsr330Provider委托给DependencyObjectProvider来实际获取值。

DependencyObjectProvider->>DefaultListableBeanFactory:doResolveDependency(descriptor,beanName,autowiredBeanNames,typeConverter)
Note over DefaultListableBeanFactory: DependencyObjectProvider将实际的依赖解析任务交给DefaultListableBeanFactory。

DefaultListableBeanFactory->>DependencyObjectProvider:返回Bean
Note over DependencyObjectProvider: DefaultListableBeanFactory完成解析并返回相关Bean。

DependencyObjectProvider->>Jsr330Provider:返回Bean
Note over Jsr330Provider: DependencyObjectProvider将Bean返回给Jsr330Provider。

Jsr330Provider->>MyController:返回Bean
Note over MyController: Jsr330Provider将Bean返回给MyController。
~~~

### 七、源码分析

#### Provider注册

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showService`方法，

```java
public class ProviderApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们本次重点关注`this()`。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()`方法中，初始化了的两个核心组件，一个用于读取注解定义的bean (`AnnotatedBeanDefinitionReader`)，另一个用于扫描类路径并自动检测bean组件 (`ClassPathBeanDefinitionScanner`)，也是本次重点分析的内容。

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

在`org.springframework.context.support.GenericApplicationContext#GenericApplicationContext()`方法中，创建了一个新的`DefaultListableBeanFactory`实例并赋值给`beanFactory`属性

```java
public GenericApplicationContext() {
    this.beanFactory = new DefaultListableBeanFactory();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory`静态代码块中，尝试加载JSR-330（即`javax.inject` API）中定义的`Provider`接口。这个接口是Java的依赖注入规范中的一部分。如果该接口可用，它将被赋给`javaxInjectProviderClass`变量；否则，如果接口不可用（例如，运行环境中没有提供相关的库），`javaxInjectProviderClass`将被设置为`null`。

```java
static {
    try {
        javaxInjectProviderClass =
            ClassUtils.forName("javax.inject.Provider", DefaultListableBeanFactory.class.getClassLoader());
    }
    catch (ClassNotFoundException ex) {
        // JSR-330 API not available - Provider interface simply not supported then.
        javaxInjectProviderClass = null;
    }
}
```

#### Provider实例化

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties`方法中，用于处理bean属性的后处理，特别是通过`@Autowired`等注解进行的属性注入。

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    // 获取与bean名称和类相关的InjectionMetadata。
    // 这包括该bean需要进行注入的所有字段和方法。
    InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
    
    try {
        // 使用获取到的InjectionMetadata，实际进行属性的注入。
        metadata.inject(bean, beanName, pvs);
    }
    // 如果在注入过程中出现BeanCreationException，直接抛出。
    catch (BeanCreationException ex) {
        throw ex;
    }
    // 捕获其他异常，并以BeanCreationException的形式抛出，提供详细的错误信息。
    catch (Throwable ex) {
        throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
    }
    // 返回原始的PropertyValues，因为这个方法主要关注依赖注入而不是修改属性。
    return pvs;
}
```

在`org.springframework.beans.factory.annotation.InjectionMetadata#inject`方法中，主要目的是将所有需要注入的元素（例如带有`@Autowired`等注解的字段或方法）注入到目标bean中。

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // 获取已经检查的元素。通常，在初始化阶段，所有的元素都会被检查一次。
    Collection<InjectedElement> checkedElements = this.checkedElements;

    // 如果已经有检查过的元素，则使用它们，否则使用所有注入的元素。
    Collection<InjectedElement> elementsToIterate =
        (checkedElements != null ? checkedElements : this.injectedElements);

    // 如果有需要注入的元素...
    if (!elementsToIterate.isEmpty()) {
        // 遍历每个元素并注入到目标bean中。
        for (InjectedElement element : elementsToIterate) {
            // 对每个元素（字段或方法）执行注入操作。
            element.inject(target, beanName, pvs);
        }
    }
}
```

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject`方法中，首先检查字段的值是否已经被缓存。如果已缓存，则从缓存中获取，否则重新解析。然后，它确保字段是可访问的（特别是对于私有字段），并将解析的值设置到目标bean的相应字段中。

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // 获取代表带有@Autowired注解的字段的Field对象。
    Field field = (Field) this.member;

    Object value;
    // 如果字段的值已经被缓存（即先前已解析过），则尝试从缓存中获取。
    if (this.cached) {
        try {
            // 从缓存中获取已解析的字段值。
            value = resolvedCachedArgument(beanName, this.cachedFieldValue);
        }
        catch (NoSuchBeanDefinitionException ex) {
            // 如果缓存中的bean已被意外删除 -> 重新解析。
            value = resolveFieldValue(field, bean, beanName);
        }
    }
    else {
        // 如果字段值未被缓存，直接解析。
        value = resolveFieldValue(field, bean, beanName);
    }

    // 如果解析到的值不为null...
    if (value != null) {
        // 使字段可访问，这是必要的，特别是当字段是private时。
        ReflectionUtils.makeAccessible(field);
        // 实际将解析的值注入到目标bean的字段中。
        field.set(bean, value);
    }
}
```

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#resolveFieldValue`方法中，通过`beanFactory.resolveDependency`方法从Spring的bean工厂中解析字段的值。

```java
@Nullable
private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
    // ... [代码部分省略以简化]
    Object value;
    try {
        // 通过`beanFactory.resolveDependency`方法从Spring的bean工厂中解析字段的值
        value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
    }
    catch (BeansException ex) {
        throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
    }
    // ... [代码部分省略以简化]
    return value;
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency`方法中，如果依赖类型确实是`javax.inject.Provider`，则创建一个`Jsr330Factory`实例并调用其`createDependencyProvider`方法。这将创建并返回一个满足JSR-330规范的`Provider`实例。

```java
@Override
@Nullable
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                                @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

    // ... [代码部分省略以简化]
    if (javaxInjectProviderClass == descriptor.getDependencyType()) {
        return new Jsr330Factory().createDependencyProvider(descriptor, requestingBeanName);
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory.Jsr330Factory#createDependencyProvider`方法中，创建并返回了一个 `Jsr330Provider` 的新实例。这个 `Jsr330Provider` 类是一个适配器或代理，用于实现 JSR-330 的 `Provider` 接口，从而允许 Spring 框架与其他遵循 JSR-330 规范的依赖注入框架互操作。

```java
public Object createDependencyProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
    return new Jsr330Provider(descriptor, beanName);
}
```

#### Provider泛型提取

`MyController`，它使用`@Autowired`注入了一个`Provider<MyService>`。然后，`MyController`类的`showService`方法中使用`myServiceProvider`来获取`MyService`的两个实例。

```java
@Controller
public class MyController {

    @Autowired
    private Provider<MyService> myServiceProvider;

    public void showService(){
        System.out.println("myServiceProvider1 = " + myServiceProvider.get());
        System.out.println("myServiceProvider2 = " + myServiceProvider.get());
    }
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory.Jsr330Factory.Jsr330Provider#get`方法中，简单地调用了 `getValue()` 方法来获取并返回一个对象。

```java
@Override
@Nullable
public Object get() throws BeansException {
    return getValue();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory.DependencyObjectProvider#getValue`方法中，直接解析和返回该依赖。

```java
@Nullable
protected Object getValue() throws BeansException {
    if (this.optional) {
        return createOptionalDependency(this.descriptor, this.beanName);
    }
    else {
        return doResolveDependency(this.descriptor, this.beanName, null, null);
    }
}
```

### 八、注意事项

1. **依赖检查**
   + 与直接注入Bean不同，使用 `Provider` 注入的Bean在启动时不会进行立即检查。如果我们希望容器启动时进行依赖检查，我们应该避免使用 `Provider`。
2. **性能**
   + 每次调用 `Provider.get()` 时，都可能会触发一个新的Bean的创建（如果该Bean的scope是prototype的话）。所以，频繁地调用 `Provider.get()` 可能会有性能问题。
3. **原型作用域**
   + 如果我们使用 `Provider` 来注入原型作用域的Bean，那么每次调用 `Provider.get()` 都会返回一个新的实例。需要确保这是我们所期望的行为。
4. **泛型类型**
   + 当使用 `Provider` 时，必须为其提供泛型类型以指示所期望注入的Bean的类型。
5. **与其他注解的组合**
   + 虽然 `Provider` 可以与其他Spring注解（如 `@Qualifier`）组合使用，但必须确保这些组合在语义上是有意义的。
6. **错误处理**
   + `Provider.get()` 方法可能会抛出 `BeansException`。在使用它时，我们应该准备处理这些异常。
7. **与JSR-330的兼容性**
   + 尽管 Spring 提供了对 `Provider` 的支持，但如果我们正在使用其他支持JSR-330的容器，确保 `Provider` 的行为在这些容器中是一致的。
8. **循环依赖**
   + 与直接Bean注入相比，使用 `Provider` 可能会更容易解决某些循环依赖的问题，但仍然要注意避免引入不必要的复杂性。

### 九、总结

#### 最佳实践总结

1. **启动类与上下文配置**
   + 利用 `AnnotationConfigApplicationContext` 为 Spring 提供了基于 Java 注解的配置方式。在这里，我们使用 `MyConfiguration` 类作为配置类来初始化 Spring 上下文。
2. **组件扫描**
   + 通过在 `MyConfiguration` 配置类上使用 `@ComponentScan` 注解，我们指示 Spring 自动扫描指定包及其子包中的组件（如 `@Component`、`@Service`、`@Repository` 和 `@Controller` 注解的类），并将它们注册为 beans，从而减少了明确为每个组件定义 bean 的需要。
3. **服务注入**
   + 在 `MyController` 类中，我们使用 `@Autowired` 注解来自动注入 `Provider<MyService>` 类型的 bean。通过这种方式，我们可以轻松地从 Provider 中获取 `MyService` 实例。
4. **服务使用**
   + 在 `MyController` 的 `showService` 方法中，我们两次调用 `myServiceProvider.get()` 以演示如何从 Provider 获取 `MyService` 的实例。
5. **单例作用域**
   + 通过运行结果，我们可以观察到两次获取的 `MyService` 实例是相同的，这说明 `MyService` bean 是单例作用域的。在 Spring 中，默认的 bean 作用域是单例，这意味着对于一个特定的 bean 定义，在整个 Spring 容器中只有一个实例。
6. **注意点**
   + 尽管 Spring 的默认作用域是单例，但在某些场景下，我们可能希望每次请求都返回一个新的 bean 实例。在这种情况下，我们可以考虑将 bean 定义为原型作用域。

#### 源码分析总结

1. **初始化与上下文配置**
   - 启动时，我们使用`AnnotationConfigApplicationContext`为Spring创建了基于Java注解的配置环境，并使用`MyConfiguration`组件类作为构造参数。
   - 在其构造函数中，通过`this()`初始化了两个核心组件：一个用于读取注解定义的bean (`AnnotatedBeanDefinitionReader`) 和一个用于扫描类路径并自动检测bean组件 (`ClassPathBeanDefinitionScanner`)。
2. **JSR-330 `Provider`的加载**
   - 在`DefaultListableBeanFactory`的静态代码块中，尝试加载了JSR-330规范的`Provider`接口。
   - 如果`Provider`接口可用，它就会被赋给一个特定的类变量。
3. **处理`@Autowired`注解**
   - 通过`AutowiredAnnotationBeanPostProcessor`处理bean属性，尤其是通过`@Autowired`进行的属性注入。
   - 在处理过程中，会解析字段的值并进行注入，其中涉及到`Provider`的使用和处理。
4. **解析`Provider`的依赖**
   - 当遇到一个字段或属性的类型为`Provider<T>`时，Spring会在`DefaultListableBeanFactory`中特殊处理它。
   - 对于JSR-330的`Provider`类型的依赖，Spring会创建一个特定的`Provider`实例来满足这个依赖，这是通过`Jsr330Factory`和其内部类`Jsr330Provider`实现的。
5. **获取`Provider`的值**
   - 在`Jsr330Provider`中的`get`方法被调用时，会进一步调用`getValue`方法。
   - `getValue`方法负责实际的依赖解析，返回所需的bean实例。