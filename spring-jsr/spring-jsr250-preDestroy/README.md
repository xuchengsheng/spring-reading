## @PreDestroy

- [@PreDestroy](#predestroy)
  - [一、基本信息](#一基本信息)
  - [二、接口描述](#二接口描述)
  - [三、接口源码](#三接口源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)
    - [前置条件](#前置条件)
    - [收集阶段](#收集阶段)
    - [销毁阶段](#销毁阶段)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133911656) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@PreDestroy源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-jsr/spring-jsr250-preDestroy)

### 二、接口描述

`@PreDestroy` 注解来源于 JSR-250（Java Specification Request 250），这是一个定义了一些常用的Java EE注解的规范。这些注解的目的是提供一个与平台无关的方式，允许我们定义一些生命周期事件，比如在bean销毁之后要执行的方法。

### 三、接口源码

`@PreDestroy` 是 Java EE 提供的一个标准注解，表示被标记的方法应该在对象销毁后立即执行。

```java
@Documented
@Retention (RUNTIME)
@Target(METHOD)
public @interface PreDestroy {
    
}
```

### 四、主要功能

1. **资源释放**
   + 例如，如果我们的 bean 打开了文件、数据库连接或网络连接，可以在 `@PreDestroy` 方法中关闭这些连接，确保资源被正确释放。
2. **清理工作**
   + 如果 bean 在其生命周期中创建了临时文件或临时数据结构，并且在 bean 销毁前需要删除或清除，可以在 `@PreDestroy` 方法中执行这些清理操作。
3. **日志和通知**
   + 在某些应用中，我们可能希望在 bean 的生命周期结束时记录日志或发送通知。可以使用 `@PreDestroy` 方法来实现这一点。
4. **状态存储**
   + 如果 bean 有状态，并且我们希望在其生命周期结束时保存这个状态，可以在 `@PreDestroy` 方法中做这个工作。
5. **与其他组件断开连接**
   + 如果 bean 在其生命周期中注册到了其他组件或服务，并且需要在销毁前从这些组件或服务中注销，可以在 `@PreDestroy` 方法中执行此操作。
6. **无需 XML 配置**
   + 与传统的 `destroy-method` XML 属性相比，使用 `@PreDestroy` 注解使代码更清晰，因为清理逻辑和 bean 代码位于同一位置，而无需查看 XML 配置文件。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，最后调用`context.close()`方法关闭容器。

```java
public class PreDestroyApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
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

`MyService` 的 Spring Service 类。这个类有一个无参构造函数和一个使用 `@PreDestroy` 注解的方法。

```java
@Service
public class MyService {

    public MyService(){
        System.out.println("执行MyService构造函数");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println("执行@PreDestroy方法");
    }
}
```

运行结果发现，关闭上下文时 `@PreDestroy` 方法会被调用。

```java
执行MyService构造函数
执行@PreDestroy方法
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: @PreDestroy注解时序图
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>开始应用 BeanDefinition 的后置处理器。
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>处理 Bean 的通用注解。
CommonAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName)<br>为生命周期注解（如 @PreDestroy）处理已合并的 Bean 定义。
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:findLifecycleMetadata(clazz)<br>查找类的生命周期元数据。
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:buildLifecycleMetadata(clazz)<br>构建类的生命周期元数据。
InitDestroyAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>处理类的所有本地方法。
ReflectionUtils->>InitDestroyAnnotationBeanPostProcessor:<br>解析那些有 @PreDestroy 注解的方法。
InitDestroyAnnotationBeanPostProcessor->>LifecycleElement:new LifecycleElement(member,ae,pd)<br>创建新的生命周期元素，代表 @PreDestroy 方法。
InitDestroyAnnotationBeanPostProcessor->>LifecycleMetadata:new LifecycleMetadata(clazz, initMethods, destroyMethods)<br>创建存储生命周期方法（初始化和销毁）的元数据。
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:this.lifecycleMetadataCache.put(clazz, metadata)<br>将构建的生命周期元数据缓存起来，方便后续访问。
AbstractAutowireCapableBeanFactory->>InitDestroyAnnotationBeanPostProcessor:doClose()<br>此处省略上下文关闭的步骤
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:postProcessBeforeDestruction(bean,beanName)
InitDestroyAnnotationBeanPostProcessor->>LifecycleMetadata:invokeDestroyMethods(target,beanName)
LifecycleMetadata->>LifecycleElement:invoke(target)
LifecycleElement->>Method:this.method.invoke(target, (Object[]) null)
~~~

### 七、源码分析

#### 前置条件

在Spring中，`InitDestroyAnnotationBeanPostProcessor`是处理`@PreDestroy`等注解的关键类，它实现了下述两个接口。因此，为了深入理解`@PreDestroy`的工作方式，研究这个类是非常有用的。简而言之，为了完全理解`@PreDestroy`的工作机制，了解下述接口确实是必要的。这两个接口提供了对bean生命周期中关键阶段的干预，从而允许进行方法执行和其他相关的操作。

1. `MergedBeanDefinitionPostProcessor`接口
   - 此接口提供的`postProcessMergedBeanDefinition`方法允许后处理器修改合并后的bean定义。合并后的bean定义是一个已经考虑了所有父bean定义属性的bean定义。对于`@PreDestroy`注解的处理，这一步通常涉及到收集需要被解析的`@PreDestroy`注解信息并准备对其进行后续处理。
   - 🔗 [MergedBeanDefinitionPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `DestructionAwareBeanPostProcessor`接口
   - 此接口提供了专门处理 bean 的销毁阶段。
   - 对于 `@PreDestroy` 注解，`InitDestroyAnnotationBeanPostProcessor` 在这个方法中确保标注了 `@PreDestroy` 的方法在 bean 被销毁之前被执行。
   - 🔗 [DestructionAwareBeanPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-destructionAwareBeanPostProcessor)

#### 收集阶段

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`方法中，首先调用了 `super.postProcessMergedBeanDefinition`，即调用了父类或接口默认的实现。

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`方法中，主要是处理与 `@PreDestroy` 注解相关的资源注入元数据，并在bean定义合并后对这些元数据进行进一步的处理或验证。这是Spring在处理JSR-250 `@PreDestroy` 注解时的处理入口。

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

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#buildLifecycleMetadata`方法中，这个方法首先查看类是否有`@PreDestroy`注解，然后遍历类的方法以找到标记为生命周期方法的那些带有 `@PreDestroy` 注解的方法。找到的方法被存储在列表中，并在结束时根据这些方法构建一个 `LifecycleMetadata` 对象。

```java
private LifecycleMetadata buildLifecycleMetadata(final Class<?> clazz) {
    // 判断给定的类是否有@PreDestroy注解。
    if (!AnnotationUtils.isCandidateClass(clazz, Arrays.asList(this.initAnnotationType, this.destroyAnnotationType))) {
        return this.emptyLifecycleMetadata;
    }

    // 存储识别出的初始化和销毁方法的列表。
	List<LifecycleElement> destroyMethods = new ArrayList<>();
    // ... [代码部分省略以简化]
    Class<?> targetClass = clazz;

    // 遍历目标类及其所有父类，直到达到Object类。
    do {
        final List<LifecycleElement> currInitMethods = new ArrayList<>();
        final List<LifecycleElement> currDestroyMethods = new ArrayList<>();

        // 使用反射处理类的所有本地方法。
        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
            // ... [代码部分省略以简化]
            
            // 查找标有@PreDestroy注解的方法。
            if (this.destroyAnnotationType != null && method.isAnnotationPresent(this.destroyAnnotationType)) {
                currDestroyMethods.add(new LifecycleElement(method));
                if (logger.isTraceEnabled()) {
                    logger.trace("Found destroy method on class [" + clazz.getName() + "]: " + method);
                }
            }
        });

        // 将当前类的生命周期方法添加到总列表中。
        destroyMethods.addAll(currDestroyMethods);

        // ... [代码部分省略以简化]
        targetClass = targetClass.getSuperclass();
    }
    while (targetClass != null && targetClass != Object.class);

    // 如果没有找到任何生命周期方法，则返回一个空的生命周期元数据对象；否则，返回新构建的元数据对象。
    return (initMethods.isEmpty() && destroyMethods.isEmpty() ? this.emptyLifecycleMetadata :
            new LifecycleMetadata(clazz, initMethods, destroyMethods));
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleElement#LifecycleElement`方法中，它用于封装与生命周期相关的方法（如 `@PreDestroy` 注解的方法）。

```java
public LifecycleElement(Method method) {
    // 检查提供的方法是否是无参数的。生命周期方法（如@PreDestroy）需要是无参数方法。
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

#### 销毁阶段

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessBeforeDestruction`方法中，实现了 `postProcessBeforeDestruction` 方法，它是 Spring 的 `DestructionAwareBeanPostProcessor` 接口的一部分，用于bean销毁之后要执行的方法。这个特定的实现与处理 `@PreDestroy`注解相关。

```java
@Override
public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
    LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
    try {
        metadata.invokeDestroyMethods(bean, beanName);
    }
    catch (InvocationTargetException ex) {
       // ... [代码部分省略以简化]
    }
    catch (Throwable ex) {
       // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#findLifecycleMetadata`方法中，首先`InitDestroyAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` 元数据收集阶段，`findLifecycleMetadata` 被调用以处理和缓存与 `@PreDestroy`和其他相关注解的 `LifecycleMetadata`。这意味着，在`postProcessBeforeDestruction`阶段再次调用 `findLifecycleMetadata` 时，会直接从缓存中获取已处理的 `LifecycleMetadata`，而不需要重新构建它。

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

在`org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeDestroyMethods`方法中，主要负责在一个 bean 实例上调用销毁方法的地方。它基于预先确定的生命周期元素集合（这里是销毁方法集合）进行迭代，并对每个元素（方法）进行调用。

```java
public void invokeDestroyMethods(Object target, String beanName) throws Throwable {
    // 获取已经检查过的销毁方法集合
    Collection<LifecycleElement> checkedDestroyMethods = this.checkedDestroyMethods;
    // 根据是否存在已检查的销毁方法，选择要使用的销毁方法集合
    Collection<LifecycleElement> destroyMethodsToUse =
        (checkedDestroyMethods != null ? checkedDestroyMethods : this.destroyMethods);

    // 判断是否有销毁方法需要执行
    if (!destroyMethodsToUse.isEmpty()) {
        // 遍历销毁方法集合
        for (LifecycleElement element : destroyMethodsToUse) {
            // 如果日志级别为 TRACE，记录正在调用的销毁方法信息
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking destroy method on bean '" + beanName + "': " + element.getMethod());
            }
            // 在目标 bean 上调用当前的销毁方法
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
   + 标记为 `@PreDestroy` 的方法不应该有参数，并且应该是无返回值的（即返回 `void`）。
2. **异常处理**
   + 标记为 `@PreDestroy` 的方法不应该抛出受检异常。如果在执行方法时出现任何错误或异常，建议在方法内部处理，以避免可能的未预期的行为。
3. **访问修饰符**
   + 虽然可以在 private、protected 或 public 方法上使用 `@PreDestroy`，但最好确保方法是可访问的，特别是在子类或不同的包中。
4. **原型 Bean 的限制**
   + 对于原型作用域的 bean，`@PreDestroy` 方法不会被 Spring 容器调用，因为 Spring 不管理原型 bean 的完整生命周期。因此，不要依赖 `@PreDestroy` 来清理原型 bean 的资源。
5. **多个销毁方法**
   + 如果一个 bean 既有 `@PreDestroy` 注解的方法又有通过 XML `destroy-method` 属性指定的方法，那么 `@PreDestroy` 注解的方法将首先被调用，然后是 `destroy-method` 指定的方法。
6. **依赖关系**
   + 如果我们的 bean 依赖于其他 bean，并且这些依赖关系在销毁过程中仍然重要，那么我们需要确保这些依赖关系在 `@PreDestroy` 方法执行时仍然满足。
7. **执行顺序**
   + Spring 不保证 `@PreDestroy` 方法的执行顺序，尤其是跨多个 bean 的情况。如果销毁方法之间的执行顺序很重要，我们可能需要考虑其他方法来协调这些销毁动作。
8. **JSR-250 依赖**
   + `@PreDestroy` 是 JSR-250 规范的一部分。要使用它，确保有适当的库依赖（尽管大多数现代 Spring 项目都会有）。
9. **与 Bean 的生命周期配合**
   + 只有当 bean 真正被 Spring 容器管理其生命周期时，`@PreDestroy` 才会被调用。这意味着，例如，如果我们手动创建一个 bean 的实例（而不是从 Spring 容器中获取），`@PreDestroy` 方法不会被自动调用。

### 九、总结

#### 最佳实践总结

1. **启动类入口**
   - `PreDestroyApplication` 类的 `main` 方法中，使用 `AnnotationConfigApplicationContext` 初始化Spring容器，该方法使用Java注解来配置Spring。我们将 `MyConfiguration` 作为参数传递，意味着我们希望从这个类开始加载Spring的配置。
2. **配置类**
   - `MyConfiguration` 被标记为一个配置类（通过 `@Configuration` 注解）。其中的 `@ComponentScan` 注解告诉Spring应该在哪些包里搜索组件。在这个例子中，Spring将会扫描 "`com.xcs.spring`" 包以及其子包，寻找例如 `@Component`、`@Service`、`@Repository` 和 `@Controller` 的注解，以此自动地注册bean。
3. **服务类**
   - 当Spring扫描 "`com.xcs.spring`" 包时，它找到了 `MyService` 类，这个类被标记为一个Service（通过 `@Service` 注解）。因此，Spring会为这个类创建一个bean实例。
4. **关闭上下文**
   - 当应用上下文被关闭（`context.close()`），所有的单例 beans 将被销毁。在这个过程中，任何带有 `@PreDestroy` 注解的方法都将被调用。所以，`MyService` 类中的 `preDestroy` 方法被执行，打印 "执行@PreDestroy方法"。

#### 源码分析总结

1. **前置条件**
   - `@PreDestroy`注解的执行依赖于两个核心接口：`MergedBeanDefinitionPostProcessor`和`DestructionAwareBeanPostProcessor`。
   - 这两个接口允许Spring在bean生命周期的关键阶段进行干预，如属性注入后，销毁前等。

2. **收集阶段**

   - 当Spring处理一个Bean的定义并且这个Bean可能有`@PreDestroy`注解时，`InitDestroyAnnotationBeanPostProcessor`的`postProcessMergedBeanDefinition`方法会被调用。

   - 在此方法中，与bean相关的`LifecycleMetadata`（包括`@PreDestroy`方法信息）被收集并缓存起来，以便后续使用。

3. **销毁阶段**

   - 当 Spring 上下文关闭时（如调用 `context.close()`），所有的单例 beans 将被销毁。
   - `postProcessBeforeDestruction` 方法是 `DestructionAwareBeanPostProcessor` 接口的一部分，它会被调用来处理每一个需要销毁的 bean。
   - 在这个方法中，再次调用 `findLifecycleMetadata` 方法从缓存中获取 `LifecycleMetadata`。
   - 之后，`invokeDestroyMethods` 方法会遍历所有销毁方法并在目标 bean 上调用它们。

   - 执行是通过查找bean的`LifecycleMetadata`（在之前的收集阶段中已经构建），然后迭代这些元数据中的方法，并使用反射来调用它们。

4. **实际方法调用**
   - 当需要调用具体的`@PreDestroy`方法时，会使用`LifecycleElement`类的`invoke`方法，该方法再次使用反射来确保方法是可访问的，并实际调用它。