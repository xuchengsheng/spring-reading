## @PostConstruct

- [@PostConstruct](#postconstruct)
  - [一、基本信息](#一基本信息)
  - [二、接口描述](#二接口描述)
  - [三、接口源码](#三接口源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)
    - [前置条件](#前置条件)
    - [收集阶段](#收集阶段)
    - [执行阶段](#执行阶段)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133904802) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@PostConstruct源码](https://github.com/xuchengsheng/spring-reading/blob/master/spring-jsr/spring-jsr250-postConstruct)

### 二、接口描述

`@PostConstruct` 注解来源于 JSR-250（Java Specification Request 250），这是一个定义了一些常用的Java EE注解的规范。这些注解的目的是提供一个与平台无关的方式，允许我们定义一些生命周期事件，比如在bean初始化之后要执行的方法。

### 三、接口源码

`@PostConstruct` 是 Java EE 提供的一个标准注解，表示被标记的方法应该在对象实例化后立即执行。Spring 容器支持此注解，它确保在构造函数完成初始化工作之后、所有属性被设置之后、并且初始化回调（如 `InitializingBean.afterPropertiesSet()` 或自定义的 `init` 方法）被触发之前，这个特定的方法被调用。

```java
@Documented
@Retention (RUNTIME)
@Target(METHOD)
public @interface PostConstruct {

}
```

### 四、主要功能

1. **初始化逻辑**
   + 允许在对象创建并完成依赖注入后执行特定的初始化逻辑。
2. **资源配置**
   + 对于需要访问数据库、文件或其他外部资源的组件，可以使用 `@PostConstruct` 来确保在使用资源之前它们已经正确配置。
3. **数据预加载**
   + 可以在应用启动时加载一些必要的数据或缓存。
4. **验证**
   + 确保组件的某些属性或配置在对象使用之前具有有效的状态或值。
5. **与平台无关**
   + `@PostConstruct` 是一个标准的 Java EE 注解，这意味着它在不同的容器和框架中都有一致的行为。
6. **执行顺序**
   + 在 Spring 中，`@PostConstruct` 被调用的时间是在构造函数之后、所有属性设置之后，并在任何初始化回调（如 `InitializingBean.afterPropertiesSet()` 或指定的 init 方法）之前。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class PostConstructApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
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

`MyService` 的 Spring Service 类。这个类有一个无参构造函数和一个使用 `@PostConstruct` 注解的方法。

```java
@Service
public class MyService {

    public MyService(){
        System.out.println("执行MyService构造函数");
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("执行@PostConstruct方法");
    }
}
```

运行结果发现，当 Spring 容器初始化 `MyService` Bean 时，我们会首先看到构造函数的输出，紧接着看到 `@PostConstruct` 方法的输出。

```
执行MyService构造函数
执行@PostConstruct方法
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: @PostConstruct注解时序图
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>开始应用 BeanDefinition 的后置处理器。
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>处理 Bean 的通用注解。
CommonAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName)<br>为生命周期注解（如 @PostConstruct）处理已合并的 Bean 定义。
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:findLifecycleMetadata(clazz)<br>查找类的生命周期元数据。
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:buildLifecycleMetadata(clazz)<br>构建类的生命周期元数据。
InitDestroyAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>处理类的所有本地方法。
ReflectionUtils->>InitDestroyAnnotationBeanPostProcessor:解析有@PostConstruct注解的方法<br>解析那些有 @PostConstruct 注解的方法。
InitDestroyAnnotationBeanPostProcessor->>LifecycleElement:new LifecycleElement(member,ae,pd)<br>创建新的生命周期元素，代表 @PostConstruct 方法。
InitDestroyAnnotationBeanPostProcessor->>LifecycleMetadata:new LifecycleMetadata(clazz, initMethods, destroyMethods)<br>创建存储生命周期方法（初始化和销毁）的元数据。
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:this.lifecycleMetadataCache.put(clazz, metadata)<br>将构建的生命周期元数据缓存起来，方便后续访问。
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName, exposedObject, mbd)<br>开始初始化 Bean。
AbstractAutowireCapableBeanFactory->>InitDestroyAnnotationBeanPostProcessor:postProcessBeforeInitialization(result, beanName)<br>在 Bean 初始化前执行后置处理。
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:findResourceMetadata(beanName,clazz,pvs)<br>查找需要注入的资源的元数据。
Note right of InitDestroyAnnotationBeanPostProcessor:<br>从缓存中快速获取先前解析的生命周期元数据。
InitDestroyAnnotationBeanPostProcessor->>LifecycleMetadata:invokeInitMethods(bean, beanName)<br>调用所有标记为 @PostConstruct 的初始化方法。
LifecycleMetadata->>LifecycleElement:invoke(target)<br>执行具体的 @PostConstruct 方法。
LifecycleElement->>Method:this.method.invoke(target, (Object[]) null)<br>使用反射调用目标 Bean 的 @PostConstruct 方法。
~~~

### 七、源码分析

#### 前置条件

在Spring中，`InitDestroyAnnotationBeanPostProcessor`是处理`@PostConstruct`等注解的关键类，它实现了下述两个接口。因此，为了深入理解`@PostConstruct`的工作方式，研究这个类是非常有用的。简而言之，为了完全理解`@PostConstruct`的工作机制，了解下述接口确实是必要的。这两个接口提供了对bean生命周期中关键阶段的干预，从而允许进行方法执行和其他相关的操作。

1. `MergedBeanDefinitionPostProcessor`接口
   - 此接口提供的`postProcessMergedBeanDefinition`方法允许后处理器修改合并后的bean定义。合并后的bean定义是一个已经考虑了所有父bean定义属性的bean定义。对于`@PostConstruct`注解的处理，这一步通常涉及到收集需要被解析的`@PostConstruct`注解信息并准备对其进行后续处理。
   - 🔗 [MergedBeanDefinitionPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `BeanPostProcessor`接口
   - 此接口提供了修改新实例化的 Bean 的机会，它允许在 Spring 容器初始化 Bean 的任何属性之前和之后执行自定义的修改。
   - 对于 `@PostConstruct`，当容器调用 `postProcessBeforeInitialization` 方法时，`CommonAnnotationBeanPostProcessor`会检查 Bean 是否有标注 `@PostConstruct` 的方法，如果有，这些方法会在这个阶段被调用。
   - 🔗 [BeanPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-beanPostProcessor)

#### 收集阶段

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`方法中，首先调用了 `super.postProcessMergedBeanDefinition`，即调用了父类或接口默认的实现。

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`方法中，主要是处理与 `@PostConstruct` 注解相关的资源注入元数据，并在bean定义合并后对这些元数据进行进一步的处理或验证。这是Spring在处理JSR-250 `@PostConstruct` 注解时的处理入口。

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    // 根据给定的bean类型查找与其相关的生命周期元数据。
    LifecycleMetadata metadata = findLifecycleMetadata(beanType);
    
    // 使用找到的生命周期元数据来检查并可能修改给定的bean定义。
    metadata.checkConfigMembers(beanDefinition);
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#findLifecycleMetadata`方法中，首先尝试从缓存中获取 `LifecycleMetadata`，如果它不存在或需要刷新，则会创建新的 `LifecycleMetadata` 并将其存入缓存。这种缓存策略可以提高效率，避免对同一类型的类反复构建注入元数据。

```java
private LifecycleMetadata findLifecycleMetadata(Class<?> clazz) {
    
    // 检查生命周期元数据缓存是否为空，这可能发生在反序列化或销毁阶段。
    if (this.lifecycleMetadataCache == null) {
        return buildLifecycleMetadata(clazz);
    }
    
    // 首先，在并发映射中进行快速检查，以最小的锁定。
    LifecycleMetadata metadata = this.lifecycleMetadataCache.get(clazz);
    
    // 如果缓存中没有找到元数据，则构建元数据并放入缓存。
    if (metadata == null) {
        synchronized (this.lifecycleMetadataCache) {
            metadata = this.lifecycleMetadataCache.get(clazz);
            
            // 双重检查锁定模式，确保只有一个线程构建和缓存元数据。
            if (metadata == null) {
                // 根据给定的类构建生命周期元数据。
                metadata = buildLifecycleMetadata(clazz);
                // 将新构建的元数据缓存，以便后续请求可以快速从缓存中检索。
                this.lifecycleMetadataCache.put(clazz, metadata);
            }
            return metadata;
        }
    }
    
    return metadata;
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#buildLifecycleMetadata`方法中，这个方法首先查看类是否有`@PostConstruct`注解，然后遍历类的方法以找到标记为生命周期方法的那些带有 `@PostConstruct` 注解的方法。找到的方法被存储在列表中，并在结束时根据这些方法构建一个 `LifecycleMetadata` 对象。

```java
private LifecycleMetadata buildLifecycleMetadata(final Class<?> clazz) {
    // 判断给定的类是否有@PostConstruct`注解。
    if (!AnnotationUtils.isCandidateClass(clazz, Arrays.asList(this.initAnnotationType, this.destroyAnnotationType))) {
        return this.emptyLifecycleMetadata;
    }

    // 存储识别出的初始化和销毁方法的列表。
    List<LifecycleElement> initMethods = new ArrayList<>();
    // ... [代码部分省略以简化]
    Class<?> targetClass = clazz;

    // 遍历目标类及其所有父类，直到达到Object类。
    do {
        final List<LifecycleElement> currInitMethods = new ArrayList<>();
        final List<LifecycleElement> currDestroyMethods = new ArrayList<>();

        // 使用反射处理类的所有本地方法。
        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
            // 查找标有初始化注解的方法。
            if (this.initAnnotationType != null && method.isAnnotationPresent(this.initAnnotationType)) {
                LifecycleElement element = new LifecycleElement(method);
                currInitMethods.add(element);
                if (logger.isTraceEnabled()) {
                    logger.trace("Found init method on class [" + clazz.getName() + "]: " + method);
                }
            }
            // ... [代码部分省略以简化]
        });

        // 将当前类的生命周期方法添加到总列表中。
        initMethods.addAll(0, currInitMethods);
        // ... [代码部分省略以简化]
        targetClass = targetClass.getSuperclass();
    }
    while (targetClass != null && targetClass != Object.class);

    // 如果没有找到任何生命周期方法，则返回一个空的生命周期元数据对象；否则，返回新构建的元数据对象。
    return (initMethods.isEmpty() && destroyMethods.isEmpty() ? this.emptyLifecycleMetadata :
            new LifecycleMetadata(clazz, initMethods, destroyMethods));
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleElement#LifecycleElement`方法中，它用于封装与生命周期相关的方法（如 `@PostConstruct` 注解的方法）。

```java
public LifecycleElement(Method method) {
    // 检查提供的方法是否是无参数的。生命周期方法（如@PostConstruct）需要是无参数方法。
    if (method.getParameterCount() != 0) {
        throw new IllegalStateException("Lifecycle method annotation requires a no-arg method: " + method);
    }
    
    // 存储提供的方法。
    this.method = method;

    // 根据方法的修饰符（如private）确定唯一标识符。如果方法是私有的，我们使用完全限定名，否则只使用方法名。
    this.identifier = (Modifier.isPrivate(method.getModifiers()) ?
                       ClassUtils.getQualifiedMethodName(method) : method.getName());
}
```

#### 执行阶段

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization`方法中，实现了 `postProcessBeforeInitialization` 方法，它是 Spring 的 `BeanPostProcessor` 接口的一部分，用于初始化 Bean 的任何属性之前和之后执行自定义的修改。这个特定的实现与处理 `@PostConstruct`注解相关。

```java
@Override
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    // 查找bean类的生命周期元数据。
    LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
    
    try {
        // 调用bean的初始化方法（如@PostConstruct注解的方法）。
        metadata.invokeInitMethods(bean, beanName);
    }
    catch (InvocationTargetException ex) {
        // 如果初始化方法调用失败（如因为抛出的异常），则抛出Bean创建异常。
        throw new BeanCreationException(beanName, "Invocation of init method failed", ex.getTargetException());
    }
    catch (Throwable ex) {
        // 对于其他错误，也抛出Bean创建异常。
        throw new BeanCreationException(beanName, "Failed to invoke init method", ex);
    }
    
    // 返回原始bean实例。
    return bean;
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#findLifecycleMetadata`方法中，首先`InitDestroyAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` 元数据收集阶段，`findLifecycleMetadata` 被调用以处理和缓存与  `@PostConstruct`和其他相关注解的 `LifecycleMetadata`。这意味着，在`postProcessBeforeInitialization`阶段之后的其他生命周期方法中，当再次调用 `findLifecycleMetadata` 时，会直接从缓存中获取已处理的 `LifecycleMetadata`，而不需要重新构建它。

```java
private LifecycleMetadata findLifecycleMetadata(Class<?> clazz) {
    
    // 检查生命周期元数据缓存是否为空，这可能发生在反序列化或销毁阶段。
    if (this.lifecycleMetadataCache == null) {
        return buildLifecycleMetadata(clazz);
    }
    
    // 首先，在并发映射中进行快速检查，以最小的锁定。
    LifecycleMetadata metadata = this.lifecycleMetadataCache.get(clazz);
    
    // 如果缓存中没有找到元数据，则构建元数据并放入缓存。
    if (metadata == null) {
        synchronized (this.lifecycleMetadataCache) {
            metadata = this.lifecycleMetadataCache.get(clazz);
            
            // 双重检查锁定模式，确保只有一个线程构建和缓存元数据。
            if (metadata == null) {
                // 根据给定的类构建生命周期元数据。
                metadata = buildLifecycleMetadata(clazz);
                // 将新构建的元数据缓存，以便后续请求可以快速从缓存中检索。
                this.lifecycleMetadataCache.put(clazz, metadata);
            }
            return metadata;
        }
    }
    
    return metadata;
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeInitMethods`方法中，主要是调用Spring Bean的初始化方法。初始化方法是由 `@PostConstruct` 注解标记的。

```java
public void invokeInitMethods(Object target, String beanName) throws Throwable {
    // 获取已检查的初始化方法集合。
    Collection<LifecycleElement> checkedInitMethods = this.checkedInitMethods;
    
    // 如果已有检查的初始化方法，则使用它们；否则，使用所有初始化方法。
    Collection<LifecycleElement> initMethodsToIterate =
        (checkedInitMethods != null ? checkedInitMethods : this.initMethods);
    
    // 如果存在初始化方法，则进行迭代调用。
    if (!initMethodsToIterate.isEmpty()) {
        for (LifecycleElement element : initMethodsToIterate) {
            // 如果启用了跟踪日志，则记录每个初始化方法的调用信息。
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking init method on bean '" + beanName + "': " + element.getMethod());
            }
            
            // 实际调用目标对象上的初始化方法。
            element.invoke(target);
        }
    }
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleElement#invoke`方法中，使用反射调用目标对象上的特定方法。

```java
public void invoke(Object target) throws Throwable {
    // 确保封装的方法是可访问的，特别是如果它是私有的。
    ReflectionUtils.makeAccessible(this.method);

    // 使用反射实际调用方法。由于该方法没有参数，所以传递一个null作为参数列表。
    this.method.invoke(target, (Object[]) null);
}
```

### 八、注意事项

1. **无参数方法**
   + 使用 `@PostConstruct` 注解的方法必须不带任何参数。
2. **返回类型**
   + 这些方法应该没有返回值（即 `void`）。因为其他返回类型会被忽略。
3. **访问修饰符**
   + 虽然通常建议使用 `public` 或 `protected` 修饰符，但这并不是强制的。私有方法也可以使用此注解，Spring 会确保这些方法仍然被调用。
4. **异常处理**
   + 如果 `@PostConstruct` 注解的方法抛出任何未检查的异常，则组件的初始化将失败，Spring 容器可能拒绝加载该组件。
5. **多个 @PostConstruct 方法**
   + 虽然可能有多个方法都有 `@PostConstruct` 注解，但这并不是一个好的实践。正确的做法是只在一个方法上使用此注解，以避免混淆和不确定的初始化顺序。
6. **与其他生命周期方法的关系**
   + 如果我们同时使用 `@PostConstruct` 和 `InitializingBean` 接口（其有 `afterPropertiesSet` 方法），那么 `@PostConstruct` 注解的方法会在 `afterPropertiesSet` 之后执行。
7. **避免多次 `@PostConstruct`**
   + 避免在同一个bean中使用多次 `@PostConstruct`。如果确实有多个方法需要在bean初始化时执行，考虑将它们放在一个单独的 `@PostConstruct` 方法中，并按期望的顺序调用它们。
8. **跨框架支持**
   + 虽然 `@PostConstruct` 有其起源于 Java EE，但它在多个Java框架中都得到了支持，包括 Spring。然而，在不同的运行环境中，始终确保我们的运行时环境确实支持此注解。
9. **避免长时间运行的操作**
   + `@PostConstruct` 方法应该避免执行耗时很长的操作，因为它会阻塞bean的初始化过程。如果确实需要进行长时间运行的初始化，考虑使用其他机制，例如异步执行。

### 九、总结

#### 最佳实践总结

1. **启动类入口**
   + `PostConstructApplication` 类的 `main` 方法中，使用 `AnnotationConfigApplicationContext` 初始化Spring容器，该方法使用Java注解来配置Spring。我们将 `MyConfiguration` 作为参数传递，意味着我们希望从这个类开始加载Spring的配置。
2. **配置类**
   + `MyConfiguration` 被标记为一个配置类（通过 `@Configuration` 注解）。其中的 `@ComponentScan` 注解告诉Spring应该在哪些包里搜索组件。在这个例子中，Spring将会扫描 "`com.xcs.spring`" 包以及其子包，寻找例如 `@Component`、`@Service`、`@Repository` 和 `@Controller` 的注解，以此自动地注册bean。
3. **服务类**
   + 当Spring扫描 "`com.xcs.spring`" 包时，它找到了 `MyService` 类，这个类被标记为一个Service（通过 `@Service` 注解）。因此，Spring会为这个类创建一个bean实例。
4. **生命周期**：
   - 当Spring创建 `MyService` 的实例时，它首先调用类的构造函数。这就是为什么我们首先看到 "执行MyService构造函数" 的输出。
   - 在Bean的所有属性都已经被设置后，并且所有的Bean初始化回调（例如 `BeanPostProcessor` 的 `postProcessBeforeInitialization` 方法）都已经被调用后，`@PostConstruct` 注解的方法会被执行。在这个例子中，这个方法是 `postConstruct`。因此，接下来我们看到了 "执行@PostConstruct方法" 的输出。

#### 源码分析总结

1. **前置条件**
   - `@PostConstruct`注解的执行依赖于两个核心接口：`MergedBeanDefinitionPostProcessor`和`BeanPostProcessor`。
   - 这两个接口允许Spring在bean生命周期的关键阶段进行干预，如属性注入后、初始化方法前、初始化方法后等。
2. **收集阶段**
   - 当Spring处理一个Bean的定义并且这个Bean可能有`@PostConstruct`注解时，`InitDestroyAnnotationBeanPostProcessor`的`postProcessMergedBeanDefinition`方法会被调用。
   - 在此方法中，与bean相关的`LifecycleMetadata`（包括`@PostConstruct`方法信息）被收集并缓存起来，以便后续使用。
3. **执行阶段**
   - 在Spring bean的生命周期中，初始化之前的一个关键点是`postProcessBeforeInitialization`方法的执行。在这个阶段，如果Bean有一个或多个`@PostConstruct`注解的方法，那么这些方法将被执行。
   - 执行是通过查找bean的`LifecycleMetadata`（在之前的收集阶段中已经构建），然后迭代这些元数据中的方法，并使用反射来调用它们。
4. **实际方法调用**
   - 当需要调用具体的`@PostConstruct`方法时，会使用`LifecycleElement`类的`invoke`方法，该方法再次使用反射来确保方法是可访问的，并实际调用它。