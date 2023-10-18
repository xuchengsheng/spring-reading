## @Resource

- [@Resource](#resource)
  - [一、基本信息](#一基本信息)
  - [二、接口描述](#二接口描述)
  - [三、接口源码](#三接口源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)
    - [前置条件](#前置条件)
    - [收集阶段](#收集阶段)
    - [注入阶段](#注入阶段)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133887864) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Resource源码](https://github.com/xuchengsheng/spring-reading/blob/master/spring-jsr/spring-jsr250-resource)

### 二、接口描述

`@Resource` 注解是由 JSR-250: Common Annotations for the Java Platform 规范定义的。这个规范定义了一组跨多个 Java 技术（如 Java EE 和 Java SE）的公共注解。

### 三、接口源码

`@Resource` 注解的目的是为了声明和注入应用程序所需的外部资源，从而允许容器在运行时为应用程序组件提供这些资源。

```java
/**
 * 标注需要注入的资源的注解。
 * 这个注解可以用于类、字段和方法上，指示容器为其注入资源。
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Resources.class)
public @interface Resource {

    /**
     * 资源的 JNDI 名称。
     *
     * @return 资源名称。
     */
    String name() default "";

    /**
     * JNDI 查询名称，用于运行时查找资源。
     *
     * @return JNDI 查询名称。
     */
    String lookup() default "";

    /**
     * 资源的期望类型。默认为 Object，意味着类型不特定。
     *
     * @return 资源类型。
     */
    Class<?> type() default Object.class;

    /**
     * 资源的身份验证类型。
     *
     * @return 身份验证类型。
     */
    Resource.AuthenticationType authenticationType() default Resource.AuthenticationType.CONTAINER;

    /**
     * 标示资源是否可以被多个客户端共享。
     *
     * @return 如果资源可以被共享则返回 true，否则返回 false。
     */
    boolean shareable() default true;

    /**
     * 与资源环境引用关联的产品特定的名称。
     *
     * @return 映射名称。
     */
    String mappedName() default "";

    /**
     * 对资源的简要描述。
     *
     * @return 资源描述。
     */
    String description() default "";

    /**
     * 身份验证类型的枚举。
     * CONTAINER: 容器管理身份验证。
     * APPLICATION: 应用程序管理身份验证。
     */
    public static enum AuthenticationType {
        CONTAINER,
        APPLICATION;
    }
}
```

### 四、主要功能

1. **资源定位**
   + 通过 `name` 和 `lookup` 属性，`@Resource` 可以定位到特定的资源，如 JNDI 中的一个数据库连接。
2. **类型指定**
   + 通过 `type` 属性，它允许指定所需资源的具体Java类型，确保注入的资源与预期类型匹配，从而提供类型安全。
3. **身份验证策略**
   + `authenticationType` 属性允许我们选择资源的身份验证方式，决定是由容器还是应用程序来进行身份验证。
4. **共享策略**
   + 通过 `shareable` 属性，它指定资源是否可以在多个客户端或组件之间共享。
5. **供应商特定名称**
   + `mappedName` 属性可以提供与资源关联的供应商或平台特定的名称，增加部署的灵活性。
6. **描述信息**
   + 通过 `description` 属性，为资源提供了简要描述，有助于我们和系统管理员理解其用途。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showService`方法，

```java
public class ResourceApplication {

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

Spring 容器在初始化 `MyController` 时，我们使用了`@Resource`注解，会自动注入一个 `MyService` 类型的 bean 到 `myService` 字段。

```java
@Controller
public class MyController {

    @Resource(name="myService")
    private MyService myService;

    public void showService(){
        System.out.println("myService = " + myService);
    }
}
```

`MyService` 是一个简单的服务类，但我们没有定义任何方法或功能。

```java
@Service
public class MyService {
    
}
```

运行结果发现，我们使用 `@Resource` 注解的功能，在我们的 Spring 上下文中工作正常，并且它成功地自动注入了所需的依赖关系。

```java
myService = com.xcs.spring.service.MyService@f0c8a99
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: @Resource注解时序图
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>应用Bean定义的后置处理器
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>处理已合并的Bean定义
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:findResourceMetadata(beanName,clazz,pvs)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:buildResourceMetadata(clazz)
CommonAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalFields(clazz,fc)<br>处理类的本地字段
ReflectionUtils->>CommonAnnotationBeanPostProcessor:解析有@Resource注解的字段
CommonAnnotationBeanPostProcessor->>ResourceElement:ResourceElement(member,ae,pd)
CommonAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>处理类的本地方法
ReflectionUtils->>CommonAnnotationBeanPostProcessor:解析有@Resource注解的方法
CommonAnnotationBeanPostProcessor->>ResourceElement:ResourceElement(member,ae,pd)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:injectionMetadataCache.put(cacheKey, metadata)<br>将元数据存入缓存
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)<br>填充Bean的属性值
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>后处理Bean的属性
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:findResourceMetadata(beanName,clazz,pvs)
Note right of CommonAnnotationBeanPostProcessor:<br>从缓存中获取注入的元数据
CommonAnnotationBeanPostProcessor->>InjectionMetadata:inject(target,beanName,pvs)
InjectionMetadata->>InjectedElement:inject(target,requestingBeanName,pvs)
InjectedElement->>ResourceElement:getResourceToInject(target,requestingBeanName)
ResourceElement->>CommonAnnotationBeanPostProcessor:getResource(element,requestingBeanName)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:autowireResource(factory,element,requestingBeanName)
CommonAnnotationBeanPostProcessor->>AbstractAutowireCapableBeanFactory:resolveBeanByName(name,descriptor)
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:返回被依赖的Bean
CommonAnnotationBeanPostProcessor->>ResourceElement:返回被依赖的Bean
ResourceElement->>InjectedElement:返回被依赖的Bean
InjectedElement->>Field:field.set(target, 返回被依赖的Bean)
~~~

### 七、源码分析

#### 前置条件

在Spring中，`CommonAnnotationBeanPostProcessor`是处理`@Inject`等注解的关键类，它实现了下述两个接口。因此，为了深入理解`@Inject`的工作方式，研究这个类是非常有用的。简而言之，为了完全理解`@Inject`的工作机制，了解下述接口确实是必要的。这两个接口提供了对bean生命周期中关键阶段的干预，从而允许进行属性注入和其他相关的操作。

1. `MergedBeanDefinitionPostProcessor`接口
   - 此接口提供的`postProcessMergedBeanDefinition`方法允许后处理器修改合并后的bean定义。合并后的bean定义是一个已经考虑了所有父bean定义属性的bean定义。对于`@Inject`注解的处理，这一步通常涉及到收集需要被解析的`@Inject`注解信息并准备对其进行后续处理。
   - 🔗 [MergedBeanDefinitionPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `InstantiationAwareBeanPostProcessor`接口
   - 此接口提供了几个回调方法，允许后处理器在bean实例化之前和实例化之后介入bean的创建过程。特别是，`postProcessProperties`方法允许后处理器对bean的属性进行操作。对于`@Inject`注解，这通常需要在属性设置或依赖注入阶段对 bean 进行处理，并将解析得到的值注入到bean中。
   - 🔗 [InstantiationAwareBeanPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)

#### 收集阶段

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`方法中，主要是处理与 `@Resource` 注解相关的资源注入元数据，并在bean定义合并后对这些元数据进行进一步的处理或验证。这是Spring在处理JSR-250 `@Resource` 注解时的处理入口。

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    // 调用父类的 postProcessMergedBeanDefinition 方法，确保继承的处理逻辑得到执行
    super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
    
    // 根据给定的 bean 名称和类型查找相关的资源注入元数据
    InjectionMetadata metadata = findResourceMetadata(beanName, beanType, null);
    
    // 使用找到的资源注入元数据对bean定义进行进一步的处理或验证
    metadata.checkConfigMembers(beanDefinition);
}
```

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#findResourceMetadata`方法中，首先尝试从缓存中获取 `InjectionMetadata`，如果它不存在或需要刷新，则会创建新的 `InjectionMetadata` 并将其存入缓存。这种缓存策略可以提高效率，避免对同一类型的类反复构建注入元数据。

```java
private InjectionMetadata findResourceMetadata(String beanName, final Class<?> clazz, @Nullable PropertyValues pvs) {
    
    // 若 beanName 有值，则使用 beanName 作为缓存键；否则，使用类名作为缓存键。
    // 这也为那些自定义调用提供了向后兼容性。
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    
    // 首先进行一个快速检查在并发Map中的缓存，以最小化锁定。
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    
    // 检查当前的 metadata 是否需要刷新，例如它可能是过时的或不再适用。
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
         // 对缓存进行同步处理以确保线程安全
        synchronized (this.injectionMetadataCache) { 
            metadata = this.injectionMetadataCache.get(cacheKey);
            if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                if (metadata != null) {
                    // 清除旧的 metadata
                    metadata.clear(pvs);  
                }
                // 构建新的资源注入元数据
                metadata = buildResourceMetadata(clazz);
                // 将新的 metadata 放入缓存
                this.injectionMetadataCache.put(cacheKey, metadata);
            }
        }
    }
    // 返回找到或创建的资源注入元数据
    return metadata;  
}
```

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#buildResourceMetadata`方法中，目的是检查给定类及其所有超类中的所有字段和方法，查找标有 `@Resource` 和其他相关注解的元素，并据此创建一个 `InjectionMetadata` 对象。这个对象会包含了`ResourceElement`类，此类会处理如何注入这些资源的所有必要信息。

```java
private InjectionMetadata buildResourceMetadata(final Class<?> clazz) {
    
    // 判断给定的类是否可能包含任何资源注解。
    if (!AnnotationUtils.isCandidateClass(clazz, resourceAnnotationTypes)) {
        return InjectionMetadata.EMPTY;  // 如果不是，返回一个空的元数据。
    }

    List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
    Class<?> targetClass = clazz;

    // 开始遍历给定类及其所有超类
    do {
        final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

        // 检查类的所有局部字段上的注解
        ReflectionUtils.doWithLocalFields(targetClass, field -> {
            // ... (此处的代码检查各种注解，例如@WebServiceRef, @EJB 和 @Resource，并据此创建对应的元素)
            
            // ... [代码部分省略以简化]
            
            if (field.isAnnotationPresent(Resource.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("@Resource annotation is not supported on static fields");
                }
                if (!this.ignoredResourceTypes.contains(field.getType().getName())) {
                    currElements.add(new ResourceElement(field, field, null));
                }
            }
        });

        // 检查类的所有局部方法上的注解
        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
            // ... (与字段相似，此处的代码检查各种注解，并据此创建对应的元素)
            
            // ... [代码部分省略以简化]
            if (bridgedMethod.isAnnotationPresent(Resource.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new IllegalStateException("@Resource annotation is not supported on static methods");
                }
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length != 1) {
                    throw new IllegalStateException("@Resource annotation requires a single-arg method: " + method);
                }
                if (!this.ignoredResourceTypes.contains(paramTypes[0].getName())) {
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                    currElements.add(new ResourceElement(method, bridgedMethod, pd));
                }
            }
        });

        // 将当前类找到的元素添加到总列表的开头
        elements.addAll(0, currElements);
        // 移动到下一个超类进行处理
        targetClass = targetClass.getSuperclass();
    }
    // 持续处理，直到没有超类或达到 Object 类为止
    while (targetClass != null && targetClass != Object.class);

    // 根据收集到的注入元素创建并返回一个 InjectionMetadata 实例
    return InjectionMetadata.forElements(elements, clazz);
}
```

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.ResourceElement#ResourceElement`方法中，主要基于 `@Resource` 注解和其他相关信息（如字段或方法名、`@Lazy` 注解等）初始化了一个 `ResourceElement` 实例，该实例将包含有关如何查找和注入特定资源的所有必要信息。

PS：在`ResourceElement`实现中，Spring只关心了`@Resource` 注解的 `name`, `type`, `lookup` 和 `mappedName` 这四个属性，这是因为这些属性与Spring的DI（依赖注入）机制最直接相关。其他的属性，如 `authenticationType`, `shareable`, 和 `description`，在Spring的上下文中并没有实际的用途或者没有被实现。

```java
public ResourceElement(Member member, AnnotatedElement ae, @Nullable PropertyDescriptor pd) {
    
    // 调用父类的构造函数，传入成员和属性描述符
    super(member, pd);

    // 从给定的注解元素（字段或方法）获取 @Resource 注解
    Resource resource = ae.getAnnotation(Resource.class);

    // 获取资源的名称和类型
    String resourceName = resource.name();
    Class<?> resourceType = resource.type();

    // 判断资源名称是否为默认名称（即没有明确指定）
    this.isDefaultName = !StringUtils.hasLength(resourceName);
    
    if (this.isDefaultName) {
        // 如果资源名称是默认的，使用成员的名称作为资源名称
        resourceName = this.member.getName();
        // 如果这是一个setter方法，可能会提取属性名称作为资源名称
        if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
            resourceName = Introspector.decapitalize(resourceName.substring(3));
        }
    }
    // 解析可能的占位符或表达式
    else if (embeddedValueResolver != null) {
        resourceName = embeddedValueResolver.resolveStringValue(resourceName);
    }

    // 如果资源类型明确指定，则验证该类型
    if (Object.class != resourceType) {
        checkResourceType(resourceType);
    }
    else {
        // 如果没有明确指定资源类型，根据成员类型推断资源类型
        resourceType = getResourceType();
    }
    
    this.name = (resourceName != null ? resourceName : "");
    this.lookupType = resourceType;
    
    // 获取查找值或映射名称
    String lookupValue = resource.lookup();
    this.mappedName = (StringUtils.hasLength(lookupValue) ? lookupValue : resource.mappedName());

    // 检查是否存在 @Lazy 注解，并据此设置lazyLookup属性
    Lazy lazy = ae.getAnnotation(Lazy.class);
    this.lazyLookup = (lazy != null && lazy.value());
}
```

#### 注入阶段

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessProperties`方法中，实现了 `postProcessProperties` 方法，它是 Spring 的 `InstantiationAwareBeanPostProcessor` 接口的一部分，用于在实例化 bean 之后但在属性注入之前进行操作。这个特定的实现与处理 `@Resource` 注解相关。

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    
    // 根据bean的名称和类找到相应的资源注入元数据
    InjectionMetadata metadata = findResourceMetadata(beanName, bean.getClass(), pvs);

    try {
        // 尝试使用找到的元数据对给定的bean进行注入
        metadata.inject(bean, beanName, pvs);
    }
    // 如果在注入过程中出现任何问题，抛出一个Bean创建异常
    catch (Throwable ex) {
        throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
    }

    // 返回处理后的属性值
    return pvs;
}
```

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#findResourceMetadata`方法中，首先`CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` 元数据收集阶段，`findResourceMetadata` 被调用以处理和缓存与 `@Resource` 和其他相关注解相关的 `InjectionMetadata`。这意味着，在`postProcessProperties`阶段之后的其他生命周期方法中，当再次调用 `findResourceMetadata` 时，会直接从缓存中获取已处理的 `InjectionMetadata`，而不需要重新构建它。

```java
private InjectionMetadata findResourceMetadata(String beanName, final Class<?> clazz, @Nullable PropertyValues pvs) {
    
    // 如果 beanName 存在则使用它作为缓存键，否则使用类名。这也确保了与自定义调用者的向后兼容性。
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    
    // 首先在并发 Map 中进行快速检查，尽量减少锁的使用。
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    
    // 检查当前的 metadata 是否需要刷新。例如，它可能是过时的或不再适用。
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
        synchronized (this.injectionMetadataCache) {  // 对缓存进行同步处理以确保线程安全
            metadata = this.injectionMetadataCache.get(cacheKey);
            if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                if (metadata != null) {
                    metadata.clear(pvs);  // 清除旧的 metadata
                }
                // 构建新的资源注入元数据
                metadata = buildResourceMetadata(clazz);
                // 将新的 metadata 放入缓存
                this.injectionMetadataCache.put(cacheKey, metadata);
            }
        }
    }
    return metadata;  // 返回找到或创建的资源注入元数据
}
```

在`org.springframework.beans.factory.annotation.InjectionMetadata#inject`方法中，首先是遍历 `InjectedElement` 集合，并为给定的目标对象执行实际的注入操作。

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    Collection<InjectedElement> checkedElements = this.checkedElements;
    Collection<InjectedElement> elementsToIterate =
        (checkedElements != null ? checkedElements : this.injectedElements);
    if (!elementsToIterate.isEmpty()) {
        for (InjectedElement element : elementsToIterate) {
            element.inject(target, beanName, pvs);
        }
    }
}
```

在`org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement#inject`方法中，主要根据注入点的类型（字段或方法）执行实际的资源注入操作。对于字段，它直接设置字段的值。对于方法，它调用该方法并将资源作为参数传递，从而实现注入。

```java
protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs)
				throws Throwable {

    // 如果注入点是一个字段
    if (this.isField) {
        Field field = (Field) this.member; // 获取字段信息
        ReflectionUtils.makeAccessible(field); // 确保字段是可访问的，即使它是私有的
        // 实际将资源设置/注入到目标对象的字段中
        field.set(target, getResourceToInject(target, requestingBeanName));
    }
    else { // 如果注入点是一个方法（例如setter方法）
        // 检查是否应跳过属性注入，可能基于提供的属性值（pvs）
        if (checkPropertySkipping(pvs)) {
            return;
        }
        try {
            Method method = (Method) this.member; // 获取方法信息
            ReflectionUtils.makeAccessible(method); // 确保方法是可访问的，即使它是私有的
            // 通过方法调用实际将资源注入到目标对象中
            method.invoke(target, getResourceToInject(target, requestingBeanName));
        }
        catch (InvocationTargetException ex) {
            // 如果调用方法时发生异常，抛出实际的目标异常
            throw ex.getTargetException();
        }
    }
}
```

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.ResourceElement#getResourceToInject`方法中，首先根据 `lazyLookup` 属性来决定是否为资源构建一个懒加载代理。如果 `lazyLookup` 为 `true`，则返回一个代表懒加载资源的代理对象；否则，它直接返回资源实例。

```java
@Override
protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
    // 检查资源是否应该懒加载
    return (this.lazyLookup ? 
            // 如果是懒加载，则构建一个资源的懒加载代理
            buildLazyResourceProxy(this, requestingBeanName) :
            // 如果不是懒加载，则直接获取资源
            getResource(this, requestingBeanName));
}
```

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#getResource`方法中，首先尝试从 JNDI 中获取资源，如果 JNDI 不可用或不适用，它会尝试从 Spring 上下文中自动装配资源。如果两者都不可用，它会抛出异常。

```java
protected Object getResource(LookupElement element, @Nullable String requestingBeanName)
			throws NoSuchBeanDefinitionException {

    // 如果 LookupElement 的 'mappedName' 属性有值
    if (StringUtils.hasLength(element.mappedName)) {
        // 从 JNDI 中获取与 'mappedName' 匹配的资源
        return this.jndiFactory.getBean(element.mappedName, element.lookupType);
    }
    // 如果配置为总是使用 JNDI 查找
    if (this.alwaysUseJndiLookup) {
        // 使用 LookupElement 的 'name' 属性从 JNDI 中获取资源
        return this.jndiFactory.getBean(element.name, element.lookupType);
    }
    // 如果没有配置 resourceFactory（例如，一个 Spring ApplicationContext）
    if (this.resourceFactory == null) {
        // 抛出异常，因为无法从 Spring 上下文中获取资源
        throw new NoSuchBeanDefinitionException(element.lookupType,
                                                "No resource factory configured - specify the 'resourceFactory' property");
    }
    // 从 Spring ApplicationContext 中自动装配资源
    return autowireResource(this.resourceFactory, element, requestingBeanName);
}
```

在`org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#autowireResource`方法中，首先根据 `LookupElement` 提供的描述自动装配资源。它可以根据类型匹配来解析依赖，或者直接根据名称来解析。对于被自动装配的资源，如果`BeanFactory`是`ConfigurableBeanFactory`，会为每个自动装配的bean名称注册依赖关系。

```java
protected Object autowireResource(BeanFactory factory, LookupElement element, @Nullable String requestingBeanName)
			throws NoSuchBeanDefinitionException {

    Object resource;
    Set<String> autowiredBeanNames;
    String name = element.name;

    // 如果工厂支持自动装配能力
    if (factory instanceof AutowireCapableBeanFactory) {
        AutowireCapableBeanFactory beanFactory = (AutowireCapableBeanFactory) factory;
        DependencyDescriptor descriptor = element.getDependencyDescriptor();
        
        // 当资源的名称为默认值，且在BeanFactory中没有与这个名字匹配的bean时
        if (this.fallbackToDefaultTypeMatch && element.isDefaultName && !factory.containsBean(name)) {
            autowiredBeanNames = new LinkedHashSet<>();
            resource = beanFactory.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames, null);
            if (resource == null) {
                throw new NoSuchBeanDefinitionException(element.getLookupType(), "No resolvable resource object");
            }
        }
        // 否则，通过名称解析资源
        else {
            resource = beanFactory.resolveBeanByName(name, descriptor);
            autowiredBeanNames = Collections.singleton(name);
        }
    }
    // 如果不支持自动装配，则直接使用名称和类型从BeanFactory获取资源
    else {
        resource = factory.getBean(name, element.lookupType);
        autowiredBeanNames = Collections.singleton(name);
    }

    // 如果BeanFactory是可配置的，为每一个自动装配的bean名注册一个依赖bean
    if (factory instanceof ConfigurableBeanFactory) {
        ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory) factory;
        for (String autowiredBeanName : autowiredBeanNames) {
            if (requestingBeanName != null && beanFactory.containsBean(autowiredBeanName)) {
                beanFactory.registerDependentBean(autowiredBeanName, requestingBeanName);
            }
        }
    }

    return resource;
}
```

### 八、注意事项

1. **来源**
   + `@Resource` 注解来自 Java 的 JSR-250 规范，而不是 Spring 核心。尽管如此，Spring 提供了对此注解的全面支持。
2. **默认名称**
   + 如果不指定名称，`@Resource` 默认会按照属性名或方法名来寻找匹配的 bean。比如，`@Resource private MyService myService;` 会查找名为 "myService" 的 bean。
3. **类型 vs 名称**
   + `@Resource` 默认是基于名称进行自动装配的。如果没有找到名称匹配的bean，它会回退到类型匹配。这与 `@Autowired` 不同，后者默认基于类型进行自动装配。
4. **指定名称**
   + 我们可以通过 `name` 属性明确指定要注入的 bean 的名称：`@Resource(name = "myService")`。
5. **处理冲突**
   + 在一个上下文中，如果有多个相同类型的 bean，为避免冲突，最好使用 `name` 属性明确指定想要注入的 bean。
6. **与其他注解的结合**
   + 不建议在同一个字段或setter上同时使用 `@Resource` 和 `@Autowired` 或 `@Inject`。
7. **静态字段**
   + `@Resource` 不支持静态字段。静态字段不属于任何实例，因此无法注入依赖关系。
8. **必需性**
   + 默认情况下，`@Resource` 注解的依赖是必需的，即如果没有找到相应的 bean，会抛出异常。如果某些情况下允许依赖项为 null 或不存在，必须结合其他配置来实现，例如使用 `@Autowired(required = false)`。
9. **懒加载**
   + 在Spring中，如果我们希望延迟资源的初始化并在首次请求时加载它，可以结合 `@Lazy` 注解使用。

### 九、总结

#### 最佳实践总结

1. **启动类入口**
   + `ResourceApplication` 类作为应用程序的入口。它创建了一个基于注解的 Spring 应用上下文，`AnnotationConfigApplicationContext`，并为其提供了一个配置类 `MyConfiguration`。
2. **上下文初始化**
   + 通过 `AnnotationConfigApplicationContext`，Spring 上下文初始化并加载配置类，同时扫描指定的包及其子包中的组件。
3. **组件扫描**
   + `MyConfiguration` 类使用 `@ComponentScan` 注解指定 Spring 搜索 "`com.xcs.spring`" 包及其子包。Spring 在这些包中搜索带有 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类，并自动注册它们作为 beans。
4. **依赖注入**
   + 在 `MyController` 类中，一个名为 `myService` 的字段使用 `@Resource` 注解。这告诉 Spring，当创建 `MyController` 的实例时，它需要为 `myService` 字段注入一个类型为 `MyService` 的 bean。并且，由于指定了 `name="myService"`，Spring 将按名称而不是类型进行注入。
5. **服务创建**
   + `MyService` 类被标注为 `@Service`，这意味着 Spring 会自动创建其实例并将其注册到上下文中。在后续的依赖注入过程中，它被注入到了 `MyController` 的 `myService` 字段中。
6. **方法调用**
   + 在 `ResourceApplication` 的 `main` 方法中，从上下文中获取了 `MyController` 的 bean 并调用了其 `showService` 方法，从而输出了 `myService` 的实例信息，证明了注入过程是成功的。
7. **输出结果**
   + 应用程序输出了 `myService` 实例的信息，证明了 `@Resource` 注解成功地完成了依赖注入，并且整个过程工作得很好。

#### 源码分析总结

1. **前置条件**
   + `CommonAnnotationBeanPostProcessor`是处理`@Resource`和其他相关注解的核心类。为了完全理解`@Resource`的工作机制，我们关注了`MergedBeanDefinitionPostProcessor`和`InstantiationAwareBeanPostProcessor`两个接口。这两个接口提供了对bean生命周期中的关键阶段的干预，从而允许进行属性注入和其他相关操作。
2. **收集阶段**
   - `postProcessMergedBeanDefinition`方法：在bean定义合并后，对`@Resource`相关的资源注入元数据进行进一步处理。
   - `findResourceMetadata`方法：尝试从缓存中获取与`@Resource`相关的`InjectionMetadata`。如果它不存在或需要刷新，创建一个新的`InjectionMetadata`并将其加入缓存。
   - `buildResourceMetadata`方法：检查类及其所有超类，查找带有`@Resource`等相关注解的字段和方法，为这些元素创建一个新的`InjectionMetadata`对象。
3. **注入阶段**
   - `postProcessProperties`方法：在实例化bean之后但在属性注入之前，用于处理与`@Resource`注解相关的注入。
   - `inject`方法：遍历`InjectedElement`集合，并为给定的目标对象执行实际的注入操作。
   - `getResourceToInject`方法：根据`lazyLookup`属性决定是为资源构建一个懒加载代理还是直接返回资源实例。
   - `getResource`方法：首先尝试从JNDI中获取资源。如果JNDI不可用或不适用，尝试从Spring上下文中自动装配资源。
   - `autowireResource`方法：根据`LookupElement`描述自动装配资源。它可以通过类型匹配来解析依赖，或者直接通过名称来解析。