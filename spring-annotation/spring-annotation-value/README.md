## @Value

- [@Value](#value)
  - [一、基本信息](#一基本信息)
  - [二、注解描述](#二注解描述)
  - [三、注解源码](#三注解源码)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133815435) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Value源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-value)

### 二、注解描述

`@Value` 注解，是一个非常有用的功能，它允许我们从配置文件中注入属性值到Java类的字段或方法参数中。这样，我们可以将配置和代码分离，使应用更容易配置和维护。

### 三、注解源码

`@Value`注解是 Spring 框架自 3.1 版本开始引入的一个核心注解，主要目的是允许我们在Spring管理的bean中直接注入来自各种源（如属性文件、系统属性等）的值，而不需要显式地编写代码来解析这些值。

```java
/**
 * 用于字段或方法/构造函数参数级别的注解，
 * 表示被注解元素的默认值表达式。
 *
 * 通常用于基于表达式或属性的依赖注入。
 * 也支持动态解析处理器方法参数，例如在Spring MVC中。
 *
 * 常见的使用场景是使用 `#{systemProperties.myProp}` 这样的SpEL（Spring表达式语言）表达式来注入值。
 * 或者，使用 `${my.app.myProp}` 这样的属性占位符来注入值。
 *
 * 注意，@Value 注解的实际处理是由 BeanPostProcessor 执行的，
 * 这意味着我们**不能**在 BeanPostProcessor 或 BeanFactoryPostProcessor 类型中使用 @Value。
 * 请查阅 AutowiredAnnotationBeanPostProcessor 类的javadoc（默认检查此注解的存在）。
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Autowired
 * @see org.springframework.beans.factory.config.BeanExpressionResolver
 * @see org.springframework.beans.factory.support.AutowireCandidateResolver#getSuggestedValue
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * 实际的值表达式，如 `#{systemProperties.myProp}`，
     * 或属性占位符，如 `${my.app.myProp}`。
     */
    String value();
}
```

### 四、主要功能

1. **提供属性注入**
   + 允许从不同的配置源（如属性文件、系统属性等）直接向 Spring 管理的 beans 中注入值。
2. **支持表达式**：
   - SpEL (Spring Expression Language) 表达式：例如，`#{systemProperties.myProp}` 可以从系统属性中获取名为 `myProp` 的值。
   - 属性占位符：例如，`${my.app.myProp}` 可以从预定义的配置源，如 `application.properties` 或 `application.yml` 文件，获取名为 `my.app.myProp` 的属性值。
3. **动态值解析**
   + 与只能在启动时设置静态值相比，`@Value` 注解可以解析动态表达式，从而为字段或构造函数参数提供动态值。
4. **用于字段、方法参数、构造函数参数和注解**
   + 它可以被应用到这些元素上，以提供必要的值。
5. **与其他注解协同工作**
   + 尽管 `@Value` 本身是用于注入值的，但它经常与其他如 `@Component`、`@Service` 和 `@Controller` 之类的 Spring 注解一起使用，以创建完全由 Spring 管理和配置的 beans。
6. **与属性解析器配合**
   + 为了正确解析 `@Value` 中的表达式，Spring 应用上下文中需要有一个属性解析器，例如 `PropertySourcesPlaceholderConfigurer`。在 Spring Boot 项目中，这已经默认配置好了。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。

```java
public class ValueApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

这里使用`@Bean`注解，定义了一个Bean，是为了确保 `MyService` 被 Spring 容器执行，另外使用`@PropertySource`注解从类路径下的`application.properties`文件中加载属性。这意味着我们可以在这个文件中定义属性，然后在应用中使用`Environment`对象来访问它们。

```java
@Configuration
@PropertySource("classpath:application.properties")
public class MyConfiguration {

    @Bean
    public MyService myService(){
        return new MyService();
    }
}
```

`application.properties`文件在`src/main/resources`目录中，并添加以下内容。

```properties
app.name=My Spring Application
app.servers=server1,server2,server3
app.val1=10
app.val2=20
```

`MyService`类，展示了如何使用`@Value`注解的五种不同方式进行属性注入。

```java
public class MyService implements InitializingBean {

    /**
     * 直接注入值
     */
    @Value("Some String Value")
    private String someString;

    /**
     * 从属性文件中注入值方式
     */
    @Value("${app.name}")
    private String appName;

    /**
     * 使用默认值方式
     */
    @Value("${app.description:我是默认值}")
    private String appDescription;

    /**
     * 注入列表和属性
     */
    @Value("#{'${app.servers}'.split(',')}")
    private List<String> servers;

    /**
     * 使用Spring的SpEL
     */
    @Value("#{${app.val1} + ${app.val2}}")
    private int sumOfValues;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("直接注入值: " + someString);
        System.out.println("从属性文件中注入值: " + appName);
        System.out.println("使用默认值: " + appDescription);
        System.out.println("注入列表和属性: " + servers);
        System.out.println("使用Spring的SpEL: " + sumOfValues);
    }
}
```

运行结果发现， `@Value` 注解都被正确地解析并注入了预期的值。

```java
直接注入值: Some String Value
从属性文件中注入值: My Spring Application
使用默认值: 我是默认值
注入列表和属性: [server1, server2, server3]
使用Spring的SpEL: 30
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: @Value注解时序图
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>应用Bean定义的后置处理器
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>处理已合并的Bean定义
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>查找自动注入的元数据
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:buildAutowiringMetadata(clazz)<br>构建自动注入的元数据
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalFields(clazz,fc)<br>处理类的本地字段
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:解析有@Value注解的字段
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>处理类的本地方法
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:解析有@Value注解的方法
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:injectionMetadataCache.put(cacheKey, metadata)<br>将元数据存入缓存
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)<br>填充Bean的属性值
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>后处理Bean的属性
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>再次查找自动注入的元数据
Note right of AutowiredAnnotationBeanPostProcessor:<br>从缓存中获取注入的元数据
AutowiredAnnotationBeanPostProcessor->>InjectionMetadata:inject(bean, beanName, pvs)<br>执行实际的属性注入
InjectionMetadata->>AutowiredFieldElement:inject(target, beanName, pvs)<br>注入特定的字段元素
AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field,bean,beanName)<br>解析字段的值
AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(desc, beanName, autowiredBeanNames, typeConverter)<br>解析字段的依赖
DefaultListableBeanFactory->>AutowiredFieldElement:返回解析后的value值<br>返回解析后的属性值
AutowiredFieldElement->>Field:field.set(bean, value)<br>设置Bean字段的值
~~~

### 七、源码分析

#### 前置条件

在Spring中，`AutowiredAnnotationBeanPostProcessor`是处理`@Value`等注解的关键类，它实现了下述两个接口。因此，为了深入理解`@Value`的工作方式，研究这个类是非常有用的。简而言之，为了完全理解`@Value`的工作机制，了解下述接口确实是必要的。这两个接口提供了对bean生命周期中关键阶段的干预，从而允许进行属性注入和其他相关的操作。

1. **`MergedBeanDefinitionPostProcessor`接口**
   + 此接口提供的`postProcessMergedBeanDefinition`方法允许后处理器修改合并后的bean定义。合并后的bean定义是一个已经考虑了所有父bean定义属性的bean定义。对于`@Value`注解的处理，这一步通常涉及到收集需要被解析的`@Value`注解信息并准备对其进行后续处理。
   + 🔗  [MergedBeanDefinitionPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. **`InstantiationAwareBeanPostProcessor`接口**
   + 此接口提供了几个回调方法，允许后处理器在bean实例化之前和实例化之后介入bean的创建过程。特别是，`postProcessProperties`方法允许后处理器对bean的属性进行操作。对于`@Value`注解，这通常涉及到实际地解析注解中的表达式或属性占位符，并将解析得到的值注入到bean中。
   + 🔗  [InstantiationAwareBeanPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)

#### 收集阶段

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`方法中，主要确保给定的bean定义与其预期的自动装配元数据一致。

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    // 对于给定的bean名称和类型，它首先尝试查找相关的InjectionMetadata，这可能包含了该bean的字段和方法的注入信息
    InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
    
    // 使用找到的InjectionMetadata来验证bean定义中的配置成员是否与预期的注入元数据匹配。
    metadata.checkConfigMembers(beanDefinition);
}
```

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#findAutowiringMetadata`方法中，确保了始终为给定的bean名称和类获取最新和相关的`InjectionMetadata`，并利用缓存机制优化性能。

```java
private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
    // 如果beanName为空，则使用类名作为缓存键。
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    // 首先尝试从并发缓存中获取InjectionMetadata。
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    // 检查获取到的元数据是否需要刷新。
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
        // 使用双重检查锁定确保线程安全。
        synchronized (this.injectionMetadataCache) {
            metadata = this.injectionMetadataCache.get(cacheKey);
            if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                // 如果有旧的元数据，清除它。
                if (metadata != null) {
                    metadata.clear(pvs);
                }
                // 为给定的类构建新的InjectionMetadata。
                metadata = buildAutowiringMetadata(clazz);
                // 将新构建的元数据更新到缓存中。
                this.injectionMetadataCache.put(cacheKey, metadata);
            }
        }
    }
    // 返回找到的或新构建的元数据。
    return metadata;
}
```

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#buildAutowiringMetadata`方法中，查找类及其所有父类中的字段和方法，以找出所有带有自动装配注解的字段和方法，并为它们创建一个统一的`InjectionMetadata`对象。

```java
private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
    // 检查类是否含有自动装配注解，若无则直接返回空的InjectionMetadata。
    if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
        return InjectionMetadata.EMPTY;
    }

    // 初始化存放注入元素的列表。
    List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
    Class<?> targetClass = clazz;

    do {
        // 当前类中要注入的元素列表。
        final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

        // 处理类中的所有字段。
        ReflectionUtils.doWithLocalFields(targetClass, field -> {
            // 查找字段上的自动装配注解。
            MergedAnnotation<?> ann = findAutowiredAnnotation(field);
            if (ann != null) {
                // 忽略静态字段。
                if (Modifier.isStatic(field.getModifiers())) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Autowired annotation is not supported on static fields: " + field);
                    }
                    return;
                }
                boolean required = determineRequiredStatus(ann);
                // 创建一个新的AutowiredFieldElement并加入到列表。
                currElements.add(new AutowiredFieldElement(field, required));
            }
        });

        // 处理类中的所有方法。
        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
            Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
            if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                return;
            }
            // 查找方法上的自动装配注解。
            MergedAnnotation<?> ann = findAutowiredAnnotation(bridgedMethod);
            if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                // 忽略静态方法。
                if (Modifier.isStatic(method.getModifiers())) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Autowired annotation is not supported on static methods: " + method);
                    }
                    return;
                }
                // 只处理带参数的方法。
                if (method.getParameterCount() == 0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Autowired annotation should only be used on methods with parameters: " +
                                    method);
                    }
                }
                boolean required = determineRequiredStatus(ann);
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                // 创建一个新的AutowiredMethodElement并加入到列表。
                currElements.add(new AutowiredMethodElement(method, required, pd));
            }
        });

        // 将当前类的注入元素加入到总的注入元素列表的开头。
        elements.addAll(0, currElements);
        // 处理父类。
        targetClass = targetClass.getSuperclass();
    }
    // 循环直至Object类。
    while (targetClass != null && targetClass != Object.class);

    // 返回为元素列表创建的新的InjectionMetadata。
    return InjectionMetadata.forElements(elements, clazz);
}
```

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#autowiredAnnotationTypes`字段中，主要的用途是告诉`AutowiredAnnotationBeanPostProcessor`哪些注解它应该处理。当Spring容器解析bean定义并创建bean实例时，如果这个bean的字段、方法或构造函数上的注解被包含在这个`autowiredAnnotationTypes`集合中，那么`AutowiredAnnotationBeanPostProcessor`就会对它进行处理。

```java
public AutowiredAnnotationBeanPostProcessor() {
   this.autowiredAnnotationTypes.add(Value.class);
   // ... [代码部分省略以简化]
}
```

#### 注入阶段

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties`方法中，用于处理bean属性的后处理，特别是通过`@Value`等注解进行的属性注入。

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

在`org.springframework.beans.factory.annotation.InjectionMetadata#inject`方法中，主要目的是将所有需要注入的元素（例如带有`@Value`等注解的字段或方法）注入到目标bean中。

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
    // 步骤1. 获取代表带有@Autowired注解的字段的Field对象。
    Field field = (Field) this.member;

    Object value;
    // 步骤2. 如果字段的值已经被缓存（即先前已解析过），则尝试从缓存中获取。
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
        // 步骤3. 如果字段值未被缓存，直接解析。
        value = resolveFieldValue(field, bean, beanName);
    }

    // 步骤4. 如果解析到的值不为null...
    if (value != null) {
        // 步骤4.1. 使字段可访问，这是必要的，特别是当字段是private时。
        ReflectionUtils.makeAccessible(field);
        // 步骤4.2. 实际将解析的值注入到目标bean的字段中。
        field.set(bean, value);
    }
}
```

首先来到`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject`方法中的步骤3。在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#resolveFieldValue`方法中，通过`beanFactory.resolveDependency`方法从Spring的bean工厂中解析字段的值。

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

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency`方法中，首先尝试获取一个延迟解析代理。如果无法获得，它会进一步尝试解析依赖。`doResolveDependency` 是实际进行解析工作的方法。

```java
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                                @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
	// ... [代码部分省略以简化]
    
    Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
        descriptor, requestingBeanName);
    if (result == null) {
        result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
    }
    return result;
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中，首先从`DependencyDescriptor`获取注解值，然后处理其中的字符串属性占位符和SpEL表达式。最后，确保值根据目标字段或参数类型进行正确的类型转换，并将其注入相应的位置。

```java
@Nullable
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
                                  @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

    // ... [其他代码部分省略以简化]

    try {
        // 尝试快速解析依赖
        Object shortcut = descriptor.resolveShortcut(this);
        if (shortcut != null) {
            return shortcut;
        }

        // 获取依赖的类型
        Class<?> type = descriptor.getDependencyType();
        
        // 步骤1. 获取依赖的建议值，例如@Value注解的值
        Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);

        // 如果建议的值是字符串类型
        if (value instanceof String) {
            // 步骤2. 解析嵌入的值，如处理属性占位符
            String strVal = resolveEmbeddedValue((String) value);
            
            // 获取与bean名称相关的BeanDefinition
            BeanDefinition bd = (beanName != null && containsBean(beanName) ?
                                     getMergedBeanDefinition(beanName) : null);
                                     
            // 步骤3. 对Bean定义字符串进行评估，如处理SpEL表达式
            value = evaluateBeanDefinitionString(strVal, bd);
        }
        
        TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
        try {
            // 步骤4. 获取类型转换器并进行必要的类型转换
            return converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
        }
        catch (UnsupportedOperationException ex) {
            // ... [其他代码部分省略以简化]
        }

        // ... [其他代码部分省略以简化]

    }
    // ... [其他代码部分省略以简化]
}
```

首先来到在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中的步骤1。在`org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#getSuggestedValue`方法中，主要是用于解析与`DependencyDescriptor`相关的注解值，特别是`@Value`注解。如果字段或方法参数上有`@Value`注解，它会从注解中提取相应的值或表达式。

```java
@Override
@Nullable
public Object getSuggestedValue(DependencyDescriptor descriptor) {
    // 从描述符的注解中查找@Value注解提供的值
    Object value = findValue(descriptor.getAnnotations());
    
    // 如果在描述符的注解中没有找到，检查是否存在与此描述符关联的方法参数
    if (value == null) {
        MethodParameter methodParam = descriptor.getMethodParameter();
        
        if (methodParam != null) {
            // 如果存在方法参数，再从方法参数的注解中查找@Value提供的值
            value = findValue(methodParam.getMethodAnnotations());
        }
    }
    // 返回找到的值，如果没有找到则返回null
    return value;
}
```

在`org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#findValue`方法中，目标是在提供的注解集合中找到并返回`@Value`注解的值。如果没有找到，它会返回null。

```java
protected Object findValue(Annotation[] annotationsToSearch) {
    if (annotationsToSearch.length > 0) {   // qualifier annotations have to be local
        AnnotationAttributes attr = AnnotatedElementUtils.getMergedAnnotationAttributes(
            AnnotatedElementUtils.forAnnotations(annotationsToSearch), this.valueAnnotationType);
        if (attr != null) {
            return extractValue(attr);
        }
    }
    return null;
}
```

在`org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#extractValue`方法中，目的是从`AnnotationAttributes`对象中直接提取`@Value`注解的值。如果没有提供值，它会抛出异常。

```java
protected Object extractValue(AnnotationAttributes attr) {
    Object value = attr.get(AnnotationUtils.VALUE);
    if (value == null) {
        throw new IllegalStateException("Value annotation must have a value attribute");
    }
    return value;
}
```

当我们使用 `@Value("${app.description:我是默认值}")` 在你的字段上时，Spring 会在运行时尝试解析这个属性占位符。当 Spring 容器处理这个字段的注入时，它会使用 `QualifierAnnotationAutowireCandidateResolver`（或其他相关的后处理器）来获取并解析这个属性值。在我们的最佳实践下，`extractValue` 方法就是从注解属性中提取该属性占位符的逻辑，即返回值为 `"${app.description:我是默认值}"`。这个值随后会被 Spring 的属性解析器进一步处理，解析真实的值或使用默认值，并最终注入到 `appDescription` 字段中。

```java
${app.description:我是默认值}
```

然后我们来到在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中的步骤2。在`org.springframework.beans.factory.support.AbstractBeanFactory#resolveEmbeddedValue`方法中，用于解析给定字符串中的内嵌值。它遍历所有注册的`StringValueResolver`解析器，对给定的字符串值进行连续解析，以处理可能存在的多重内嵌值或引用。例如，如果字符串中有一个`${property}`形式的属性，它可以通过注册的解析器进行处理和解析为实际的属性值。

```java
@Override
@Nullable
public String resolveEmbeddedValue(@Nullable String value) {
    // 初始检查：如果提供的值为null，则直接返回null
    if (value == null) {
        return null;
    }
    
    String result = value;
    
    // 遍历所有的内嵌值解析器
    for (StringValueResolver resolver : this.embeddedValueResolvers) {
        // 使用当前解析器解析result中的值
        result = resolver.resolveStringValue(result);
        
        // 如果解析后的值为null，则直接返回null
        if (result == null) {
            return null;
        }
    }
    
    // 返回所有解析器处理后的最终值
    return result;
}
```

然后我们来到在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中的步骤3。在`org.springframework.beans.factory.support.AbstractBeanFactory#evaluateBeanDefinitionString`方法中，用于评估给定的字符串值，特别是处理可能包含Spring表达式语言 (SpEL) 表达式的字符串。首先，它检查是否有一个`beanExpressionResolver`可用来解析SpEL。如果有，它可能会获取bean定义的作用域（如果提供了bean定义），然后使用`beanExpressionResolver`对字符串值进行评估，并考虑到相关的作用域上下文。

```java
@Nullable
protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
    // 如果没有设置bean表达式解析器，直接返回原始值
    if (this.beanExpressionResolver == null) {
        return value;
    }

    Scope scope = null;

    // 如果提供了bean定义
    if (beanDefinition != null) {
        // 获取bean的作用域
        String scopeName = beanDefinition.getScope();

        // 如果作用域名称不为空，则尝试从已注册的作用域中获取对应的作用域
        if (scopeName != null) {
            scope = getRegisteredScope(scopeName);
        }
    }

    // 使用bean表达式解析器解析提供的值，并返回结果
    // 这可以处理例如使用Spring EL的情况
    return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
}
```

在`org.springframework.context.expression.StandardBeanExpressionResolver#evaluate`方法中，主要目的是解析并评估给定的值（可能是一个Spring EL表达式）。为了提高性能，它使用缓存来存储先前解析的表达式和评估上下文。此方法首先从缓存中检索或解析表达式，然后准备一个评估上下文，并使用它评估表达式。这个评估上下文被配置为能够访问与Spring容器相关的各种内容，如beans、环境属性等。

```java
@Override
@Nullable
public Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws BeansException {
    // 如果提供的值为空或没有内容，直接返回该值
    if (!StringUtils.hasLength(value)) {
        return value;
    }
    try {
        // 从缓存中尝试获取表达式
        Expression expr = this.expressionCache.get(value);
        // 如果缓存中没有表达式，则使用表达式解析器解析该值，并将其放入缓存中
        if (expr == null) {
            expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
            this.expressionCache.put(value, expr);
        }
        
        // 尝试从缓存中获取评估上下文
        StandardEvaluationContext sec = this.evaluationCache.get(evalContext);
        // 如果缓存中没有评估上下文，则创建一个新的，并进行一些初始化配置
        if (sec == null) {
            sec = new StandardEvaluationContext(evalContext);
            // 添加各种属性访问器以支持对特定类型的属性的访问
            sec.addPropertyAccessor(new BeanExpressionContextAccessor());
            sec.addPropertyAccessor(new BeanFactoryAccessor());
            sec.addPropertyAccessor(new MapAccessor());
            sec.addPropertyAccessor(new EnvironmentAccessor());
            // 设置bean解析器和类型定位器
            sec.setBeanResolver(new BeanFactoryResolver(evalContext.getBeanFactory()));
            sec.setTypeLocator(new StandardTypeLocator(evalContext.getBeanFactory().getBeanClassLoader()));
            // 如果有可用的转换服务，则设置类型转换器
            ConversionService conversionService = evalContext.getBeanFactory().getConversionService();
            if (conversionService != null) {
                sec.setTypeConverter(new StandardTypeConverter(conversionService));
            }
            // 自定义评估上下文，允许子类提供额外的配置
            customizeEvaluationContext(sec);
            // 将创建的评估上下文放入缓存
            this.evaluationCache.put(evalContext, sec);
        }

        // 使用已准备好的评估上下文评估表达式并返回结果
        return expr.getValue(sec);
    }
    catch (Throwable ex) {
        // 如果在解析或评估过程中出现任何异常，抛出BeanExpressionException
        throw new BeanExpressionException("Expression parsing failed", ex);
    }
}
```

然后我们来到在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中的步骤4。在`org.springframework.beans.TypeConverterSupport#convertIfNecessary(value,requiredType,typeDescriptor)`方法中，又重新委托给`typeConverterDelegate`进行实际的转换工作

```java
@Nullable
@Override
public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType,
                                @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {
    Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");

    try {
        // 委托给typeConverterDelegate进行实际的转换工作
        return this.typeConverterDelegate.convertIfNecessary(null, null, value, requiredType, typeDescriptor);
    }
    catch (ConverterNotFoundException | IllegalStateException ex) {
        throw new ConversionNotSupportedException(value, requiredType, ex);
    }
    catch (ConversionException | IllegalArgumentException ex) {
        throw new TypeMismatchException(value, requiredType, ex);
    }
}
```

在`org.springframework.beans.TypeConverterDelegate#convertIfNecessary(propertyName,oldValue,newValue,requiredType,typeDescriptor)`方法中，负责将一个值转换为必需的类型。首先，它会尝试查找对应的自定义编辑器。如果没有找到编辑器但设置了自定义的转换服务，它会尝试使用此服务进行转换。如果上述两步都失败，该方法还会尝试执行一些标准的转换规则，例如从字符串到枚举或从数字到其他数字类型的转换。如果所有尝试都失败，该方法会抛出相应的异常，指出不能执行所需的转换。

```java
public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue,
                                @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws IllegalArgumentException {

    // 查找此类型的自定义编辑器
    PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);

    // 用于捕获ConversionService尝试失败的异常
    ConversionFailedException conversionAttemptEx = null;

    // 没有自定义编辑器，但是否指定了自定义ConversionService？
    ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
    if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
        TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
        if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
            try {
                return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
            }
            catch (ConversionFailedException ex) {
                conversionAttemptEx = ex;  // 记录转换尝试失败
            }
        }
    }

    Object convertedValue = newValue;

    // 如果值不是所需类型，进行转换
    if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
        if (typeDescriptor != null && requiredType != null && Collection.class.isAssignableFrom(requiredType) &&
            convertedValue instanceof String) {
            TypeDescriptor elementTypeDesc = typeDescriptor.getElementTypeDescriptor();
            if (elementTypeDesc != null) {
                Class<?> elementType = elementTypeDesc.getType();
                if (Class.class == elementType || Enum.class.isAssignableFrom(elementType)) {
                    convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
                }
            }
        }
        if (editor == null) {
            editor = findDefaultEditor(requiredType);  // 如果没有自定义编辑器，找默认的
        }
        convertedValue = doConvertValue(oldValue, convertedValue, requiredType, editor);  // 进行转换
    }

    // 对于特定情况尝试标准类型转换，如字符串到枚举、数字转换等
    boolean standardConversion = false;
    if (requiredType != null) {
        if (convertedValue != null) {
            // 若目标类型为Object，则直接返回转换值
            if (Object.class == requiredType) {
                return (T) convertedValue;
            }
            // 若目标类型为数组，进行数组转换
            else if (requiredType.isArray()) {
                if (convertedValue instanceof String && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
                    convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
                }
                return (T) convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
            }
            // 如果是Collection或Map,则尝试转换集合或映射的内容
            else if (convertedValue instanceof Collection) {
                convertedValue = convertToTypedCollection((Collection<?>) convertedValue, propertyName, requiredType, typeDescriptor);
                standardConversion = true;
            }
            else if (convertedValue instanceof Map) {
                convertedValue = convertToTypedMap((Map<?, ?>) convertedValue, propertyName, requiredType, typeDescriptor);
                standardConversion = true;
            }
            if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                convertedValue = Array.get(convertedValue, 0);
                standardConversion = true;
            }
            if (String.class == requiredType && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                return (T) convertedValue.toString();
            }
            else if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
                String trimmedValue = ((String) convertedValue).trim();
                if (requiredType.isEnum() && trimmedValue.isEmpty()) {
                    return null;
                }
                convertedValue = attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
                standardConversion = true;
            }
            else if (convertedValue instanceof Number && Number.class.isAssignableFrom(requiredType)) {
                convertedValue = NumberUtils.convertNumberToTargetClass((Number) convertedValue, (Class<Number>) requiredType);
                standardConversion = true;
            }
        }
        else if (requiredType == Optional.class) {
            convertedValue = Optional.empty();
        }

        // 如果经过上述所有转换后，值仍不匹配目标类型，则抛出异常
        if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
            if (conversionAttemptEx != null) {
                throw conversionAttemptEx;
            }
            else if (conversionService != null && typeDescriptor != null) {
                TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
                if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                    return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                }
            }
            throw new IllegalStateException("转换失败");  // 实际异常消息会更详细，这里简化了
        }
    }

    if (conversionAttemptEx != null) {
        throw conversionAttemptEx;
    }

    return (T) convertedValue;
}
```

### 八、注意事项

1. **SpEL表达式**
   + `@Value`可以用来解析Spring Expression Language (SpEL) 表达式。确保我们的表达式是正确的，以防止运行时错误。
2. **默认值**
   + 我们可以为`@Value`注解提供默认值，以防止某个属性在属性文件中未被定义。例如：`@Value("${some.property:default}")`。
3. **类型转换**
   + 确保`@Value`提供的值可以被转换为字段或方法参数的类型。Spring会尝试自动进行这种转换，但不一定总是成功。
4. **不适用于复杂类型**
   + 尽管`@Value`可以用于简单的类型（如字符串、整数、枚举等），但不应用于复杂的bean注入，这时应该使用`@Autowired`或`@Inject`。
5. **不可用于`BeanPostProcessor`或`BeanFactoryPostProcessor`**
   + `@Value`注解在`BeanPostProcessor`或`BeanFactoryPostProcessor`实现中是不起作用的，因为它们在Spring容器生命周期中的处理时机早于`@Value`的处理。
6. **占位符解析器的配置**
   + 要使用属性占位符（如`${property.name}`），需要确保已配置了`PropertySourcesPlaceholderConfigurer`或`PropertyPlaceholderConfigurer`。
7. **环境变量与系统属性**
   + 我们可以使用`@Value`来引用环境变量或系统属性，例如：`@Value("${JAVA_HOME}")`。
8. **防止注入敏感信息**
   + 不要使用`@Value`来注入敏感信息，如密码，除非它们是适当加密的。考虑使用专门的解决方案，如Spring Cloud Config的Vault集成。
9. **循环依赖**
   + 尽管与`@Autowired`不同，但需要注意的是，使用`@Value`可能间接导致循环依赖，尤其是当注入的值是其他bean的属性时。
10. **性能考虑**
    + 大量使用SpEL表达式可能对性能产生轻微的影响，因为这些表达式需要在运行时进行解析。

### 九、总结

#### 最佳实践总结

1. **启动类入口**：
   - 使用`AnnotationConfigApplicationContext`来启动Spring上下文，该上下文支持基于Java注解的配置。
   - 在创建上下文时，为其提供了`MyConfiguration`作为配置类。
2. **配置类**：
   - `MyConfiguration`类标记为`@Configuration`，表示它提供了bean定义的配置信息。
   - 使用`@PropertySource`指定一个属性文件`application.properties`来为上下文加载属性。
   - 定义了一个bean：`MyService`，确保其在Spring容器中被创建和初始化。
3. **属性文件**：
   - 在`application.properties`文件中定义了几个属性，这些属性可以在应用程序中使用。
4. **属性注入**：
   - 在`MyService`类中，展示了如何使用`@Value`注解进行五种不同方式的属性注入，从直接注入字符串值到使用SpEL表达式。
5. **注入结果的验证**：
   - 实现`InitializingBean`接口并重写`afterPropertiesSet`方法来验证注入的属性值。
   - 运行应用后，该方法会打印出所有注入属性的值，从而验证`@Value`注解正确地解析并注入了预期的值。

#### 源码分析总结

1. **核心后处理器**：
   + `AutowiredAnnotationBeanPostProcessor`是处理`@Value`等注解的主要后处理器。它实现了两个关键的接口，`MergedBeanDefinitionPostProcessor`和`InstantiationAwareBeanPostProcessor`，这两个接口允许在bean的生命周期中的关键阶段进行干预，为属性注入提供了机制。
2. **收集阶段**：
   - 在`postProcessMergedBeanDefinition`方法中，`AutowiredAnnotationBeanPostProcessor`确保bean的定义与预期的自动装配元数据匹配。
   - `findAutowiringMetadata`方法确保为给定的bean名称和类获取相关的`InjectionMetadata`，并利用缓存机制优化性能。
   - `buildAutowiringMetadata`方法检查类及其所有父类，确定带有`@Autowired`、`@Value`等注解的字段和方法，并为这些元素创建一个统一的`InjectionMetadata`对象。
3. **注入阶段**：
   + `postProcessProperties`方法用于处理bean的属性的后处理，特别是注入由`@Value`等注解标记的属性。
   + `InjectionMetadata#inject`方法用于将所有需要注入的元素（例如带有`@Value`的字段）注入到目标bean中。
   + `AutowiredFieldElement#inject`方法处理具体的字段注入，包括解析`@Value`注解中的值。
   + `DefaultListableBeanFactory#resolveDependency`方法从Spring的bean工厂中解析字段的值。
   + `DefaultListableBeanFactory#doResolveDependency`方法是实际解析工作的主要场所，涉及到处理`@Value`注解中的字符串属性占位符和SpEL表达式，并确保值经过正确的类型转换。