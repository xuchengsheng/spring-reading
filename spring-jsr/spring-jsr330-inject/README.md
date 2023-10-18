## @Inject

- [@Inject](#inject)
  - [一、基本信息](#一基本信息)
  - [二、注解描述](#二注解描述)
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

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133880436) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Inject源码](https://github.com/xuchengsheng/spring-reading/blob/master/spring-jsr/spring-jsr330-inject)

### 二、注解描述

`@Inject`注解起源于JSR-330，也称为`javax.inject.Inject`。这是Java依赖注入的一个标准化规范。Spring支持这个注解，意味着我们可以在Spring应用中使用`@Inject`来执行依赖注入，与使用Spring原生的`@Autowired`注解类似。与`@Autowired`不同的是`@Inject`没有一个内置的“`required`”属性。这意味着，如果我们想要一个可选的依赖注入。但是，我们可以使用 Java 8 的 `java.util.Optional` 类型来达到类似的效果。

### 三、接口源码

从源码上可以看到`@Inject`是为多种注入方式比如：字段注入、setter方法注入和构造函数注入。

```java
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {
}
```

### 四、主要功能

1. **自动注入依赖**
   - 使用 `@Inject`，我们可以请求框架自动为某个字段、构造函数或方法注入一个依赖。这意味着我们不需要手动创建和管理对象的实例。
2. **多种注入点**
   - **字段注入**
     - 可以直接在类的字段上使用 `@Inject`，从而请求框架为该字段提供相应的bean。
   - **构造函数注入**
     - 将 `@Inject` 放在类的构造函数上，表示我们想通过该构造函数注入依赖。
   - **方法注入**
     - 可以在setter方法或任何其他方法上使用 `@Inject`，表示我们希望框架通过调用该方法来注入依赖。
3. **与其他注解协同工作**
   - 特别是与 `@Named` 注解结合，用于消除依赖的歧义。例如，如果我们有多个实现同一接口的bean，我们可以使用 `@Named` 指定我们想要注入哪一个bean。
4. **跨框架兼容性**
   - 由于 `@Inject` 是 JSR-330 标准的一部分，使用它可以增加代码的可移植性。这意味着，理论上，使用 `@Inject` 注解的代码应该能在任何支持 JSR-330 的框架（如 Spring、Java EE、Google Guice 等）中运行。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showService`方法，

```java
public class InjectApplication {

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

 Spring 容器在初始化 `MyController` 时，我们使用了`@Inject`注解，会自动注入一个 `MyService` 类型的 bean 到 `myService` 字段。

```java
@Controller
public class MyController {

    @Inject
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

运行结果发现，我们使用 `@Inject` 注解的功能，在我们的 Spring 上下文中工作正常，并且它成功地自动注入了所需的依赖关系。

```java
myService = com.xcs.spring.service.MyService@6e535154
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: @Inject注解时序图
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>应用Bean定义的后置处理器
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>处理已合并的Bean定义
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>查找自动注入的元数据
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:buildAutowiringMetadata(clazz)<br>构建自动注入的元数据
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalFields(clazz,fc)<br>处理类的本地字段
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:解析有@Inject注解的字段
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>处理类的本地方法
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:解析有@Inject注解的方法
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:injectionMetadataCache.put(cacheKey, metadata)<br>将元数据存入缓存
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)<br>填充Bean的属性值
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>后处理Bean的属性
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>再次查找自动注入的元数据
Note right of AutowiredAnnotationBeanPostProcessor:<br>从缓存中获取注入的元数据
AutowiredAnnotationBeanPostProcessor->>InjectionMetadata:inject(bean, beanName, pvs)<br>执行实际的属性注入
InjectionMetadata->>AutowiredFieldElement:inject(target, beanName, pvs)<br>注入特定的字段元素
AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field,bean,beanName)<br>解析字段的值
AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(desc, beanName, autowiredBeanNames, typeConverter)<br>解析字段的依赖
DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)<br>解析指定的依赖关系
DefaultListableBeanFactory->>DefaultListableBeanFactory:findAutowireCandidates(beanName, type, descriptor)<br>查找符合自动装配条件的候选 Bean
DefaultListableBeanFactory->>DefaultListableBeanFactory:addCandidateEntry(result, candidate, descriptor, requiredType)<br>向结果集中添加候选 Bean
DefaultListableBeanFactory->>AbstractBeanFactory:getType(name)<br>获取指定 Bean 的类型
AbstractBeanFactory->>DefaultListableBeanFactory:返回被依赖Bean的类<br>返回依赖 Bean 的实际类
DefaultListableBeanFactory->>DependencyDescriptor:resolveCandidate(beanName, requiredType, beanFactory)<br>解析候选的依赖 Bean
DependencyDescriptor->>AbstractBeanFactory:getBean(name)<br>获取指定的 Bean 实例
AbstractBeanFactory->>DependencyDescriptor:<br>返回具体的依赖 Bean 实例
DependencyDescriptor->>DefaultListableBeanFactory:<br>返回依赖的 Bean 实例给工厂
DefaultListableBeanFactory->>AutowiredFieldElement:<br>返回依赖的 Bean 给字段注入器
AutowiredFieldElement->>Field:field.set(bean, value)<br>实际设置 Bean 的字段值
~~~

### 七、源码分析

#### 前置条件

在Spring中，`AutowiredAnnotationBeanPostProcessor`是处理`@Inject`等注解的关键类，它实现了下述两个接口。因此，为了深入理解`@Inject`的工作方式，研究这个类是非常有用的。简而言之，为了完全理解`@Inject`的工作机制，了解下述接口确实是必要的。这两个接口提供了对bean生命周期中关键阶段的干预，从而允许进行属性注入和其他相关的操作。

1. `MergedBeanDefinitionPostProcessor`接口
   - 此接口提供的`postProcessMergedBeanDefinition`方法允许后处理器修改合并后的bean定义。合并后的bean定义是一个已经考虑了所有父bean定义属性的bean定义。对于`@Inject`注解的处理，这一步通常涉及到收集需要被解析的`@Inject`注解信息并准备对其进行后续处理。
   - 🔗 [MergedBeanDefinitionPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `InstantiationAwareBeanPostProcessor`接口
   - 此接口提供了几个回调方法，允许后处理器在bean实例化之前和实例化之后介入bean的创建过程。特别是，`postProcessProperties`方法允许后处理器对bean的属性进行操作。对于`@Inject`注解，这通常需要在属性设置或依赖注入阶段对 bean 进行处理，并将解析得到的值注入到bean中。
   - 🔗 [InstantiationAwareBeanPostProcessor接口传送门](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)

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
                
                // ... [代码部分省略以简化]
                
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
                
                // ... [代码部分省略以简化]
                
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
   // ... [代码部分省略以简化]
   try {
      this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
            ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
      logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
   }
   catch (ClassNotFoundException ex) {
      // JSR-330 API not available - simply skip.
   }
}
```

#### 注入阶段

在`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties`方法中，用于处理bean属性的后处理，特别是通过`@Inject`等注解进行的属性注入。

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

在`org.springframework.beans.factory.annotation.InjectionMetadata#inject`方法中，主要目的是将所有需要注入的元素（例如带有`@Inject`等注解的字段或方法）注入到目标bean中。

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
    // 步骤1. 获取代表带有@Inject注解的字段的Field对象。
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

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中，尝试解析一个特定的依赖，首先查找所有可能的匹配的 bean，然后选择一个最佳匹配的 bean。如果存在多个匹配的 bean 或没有找到匹配的 bean，它会进行相应的处理。

```java
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

    // ... [代码部分省略以简化]

    try {
        // 如果存在快捷解决依赖的方法，使用它
        Object shortcut = descriptor.resolveShortcut(this);
        if (shortcut != null) {
            return shortcut;
        }

        // 获取依赖的类型
        Class<?> type = descriptor.getDependencyType();
        
        // ... [代码部分省略以简化]

        // 步骤1. 根据依赖描述符查找匹配的bean
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
        
        // 如果没有找到匹配的bean
        if (matchingBeans.isEmpty()) {
            if (isRequired(descriptor)) {
                // 如果依赖是必需的，抛出异常
                raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
            }
            return null;
        }

        String autowiredBeanName;
        Object instanceCandidate;

        // 当找到多个匹配的bean
        if (matchingBeans.size() > 1) {
            // 确定最佳的自动装配候选者
            autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
            if (autowiredBeanName == null) {
                if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
                    // 如果不能确定唯一的bean，尝试解析不唯一的依赖
                    return descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
                }
                else {
                    return null;
                }
            }
            instanceCandidate = matchingBeans.get(autowiredBeanName);
        }
        else {
            // 只找到一个匹配的bean
            Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
            autowiredBeanName = entry.getKey();
            instanceCandidate = entry.getValue();
        }

        // 添加自动装配的bean名到集合
        if (autowiredBeanNames != null) {
            autowiredBeanNames.add(autowiredBeanName);
        }

        // 步骤2. 如果候选者是一个类，实例化它
        if (instanceCandidate instanceof Class) {
            instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
        }
        
        Object result = instanceCandidate;
        
        // ... [代码部分省略以简化]
        
        return result;
    }
    // ... [代码部分省略以简化]
}
```

我们来到在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中的步骤1。在`org.springframework.beans.factory.support.DefaultListableBeanFactory#findAutowireCandidates`方法中，首先基于给定的类型获取所有可能的bean名。接着，对于每一个可能的候选bean，它检查该bean是否是一个合适的自动注入候选，如果是，它将这个bean添加到结果集中。最后，方法返回找到的所有合适的候选bean。

```java
protected Map<String, Object> findAutowireCandidates(
			@Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {

    // 根据所需的类型，包括所有父工厂中的bean，获取所有可能的bean名
    String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
        this, requiredType, true, descriptor.isEager());

    // ... [代码部分省略以简化]

    // 遍历所有候选bean名
    for (String candidate : candidateNames) {
        // 如果候选bean不是正在查找的bean本身并且它是一个合适的自动注入候选
        if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, descriptor)) {
            // 添加这个候选bean到结果中
            addCandidateEntry(result, candidate, descriptor, requiredType);
        }
    }

    // ... [代码部分省略以简化]

    // 返回找到的所有候选bean
    return result; 
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#addCandidateEntry`方法中，主要获取候选bean的类型，并将其添加到候选bean的集合中。

```java
private void addCandidateEntry(Map<String, Object> candidates, String candidateName,
			DependencyDescriptor descriptor, Class<?> requiredType) {
	// ... [代码部分省略以简化]
    candidates.put(candidateName, getType(candidateName));
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#getType(name)`方法中，通过bean的名字来获取对应bean的类型。

```java
public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
    return getType(name, true);
}
```

我们来到在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中的步骤2。在`org.springframework.beans.factory.config.DependencyDescriptor#resolveCandidate`方法中，最后发现`@Inject` 的整个流程最终还是从Spring容器中获取一个bean实例并注入到相应的字段或构造函数参数中。

```java
public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory)
			throws BeansException {

    return beanFactory.getBean(beanName);
}
```

最后我们来到`org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject`方法中的步骤4.2。在 `AutowiredFieldElement#inject` 方法内部，通过`resolveFieldValue(field, bean, beanName)`方法，来确定了正确的bean值并满足某个字段的 `@Inject` 注解，将使用反射来实际设置这个值。具体地说，它会使用 `Field` 类的 `set` 方法来为目标对象的这个字段设置相应的值。这就是 `@Inject` 在字段上使用时如何使得Spring能够自动为这个字段注入值的背后原理。

```java
// 步骤4. 如果解析到的值不为null...
if (value != null) {
    // 步骤4.1. 使字段可访问，这是必要的，特别是当字段是private时。
    ReflectionUtils.makeAccessible(field);
    // 步骤4.2. 实际将解析的值注入到目标bean的字段中。
    field.set(bean, value);
}
```

### 八、注意事项

1. **需要依赖**：
   - 由于 `@Inject` 是 JSR-330 规范的一部分，我们需要在项目中添加 `javax.inject` 依赖。如果不这样做，我们的代码将无法编译。
2. **无 `required` 属性**：
   - 与 Spring 的 `@Autowired` 不同，`@Inject` 没有 `required` 属性。这意味着如果没有找到匹配的bean，它会默认抛出异常。
3. **与其他注解的组合**：
   - 为了指定具体的bean或解决多个可选bean之间的歧义，我们可以与 `@Named` 注解结合使用。例如：`@Inject @Named("specificBeanName")`。
4. **不仅限于Spring**：
   - 尽管 `@Inject` 在 Spring 中得到了很好的支持，但它并不是 Spring 特有的。其他实现了 JSR-330 规范的框架（如 Google Guice）也支持 `@Inject`。
5. **推荐使用构造器注入**：
   - 尽管我们可以在字段、方法和构造器上使用 `@Inject`，但现代的最佳实践建议使用构造器注入。这确保了bean的不变性和更好的测试性。
6. **循环依赖问题**：
   - 如果我们在使用字段或方法注入时不小心引入了循环依赖，Spring容器可能会抛出异常。使用构造器注入时，循环依赖会更明显地暴露出来。
7. **不要混合使用**：
   - 在一个项目中，尽量不要同时使用 `@Inject` 和 `@Autowired`，以保持一致性。选择其中之一并坚持使用。
8. **避免过度使用**：
   - 依赖注入是一个强大的特性，但也应该谨慎使用。过度使用自动注入，特别是在大型项目中，可能会使代码难以跟踪和维护。
9. **单一职责原则**：
   - 如果我们发现一个类需要太多的依赖，这可能是违反了单一职责原则的信号。考虑对类进行重构或分解。
10. **与Java EE的兼容性**：
    - 如果我们的应用程序在 Java EE 容器中运行，那么容器可能已经有了对 `@Inject` 的原生支持，而无需 Spring。

### 九、总结

#### 最佳实践总结

1. **上下文初始化**
   - 当我们创建 `AnnotationConfigApplicationContext` 并提供 `MyConfiguration` 类作为参数时，Spring 开始初始化上下文。这意味着它会加载所有的bean定义并准备创建实例。
2. **组件扫描**
   - 在 `MyConfiguration` 类中，我们使用了 `@ComponentScan` 注解指定了扫描的包路径。这使得Spring扫描指定包和其子包中的所有类，并查找标记为 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类。找到后，Spring 会自动将这些类注册为bean。
3. **依赖解析**
   - 在 `MyController` 类中，我们在 `myService` 字段上使用了 `Inject` 注解。这告诉Spring，当创建 `MyController` bean时，需要找到一个 `MyService` 类型的bean，并自动注入到该字段中。
4. **实例化并注入**
   - 当我们从上下文中请求 `MyController` 类型的bean时，Spring会先创建 `MyController` 的一个实例。但在此之前，它会查看所有带有 `@Inject` 注解的字段，然后为这些字段找到匹配的bean并注入。
   - 在我们的例子中，Spring找到了 `MyService` 类型的bean并将其注入到了 `myService` 字段中。
5. **执行业务逻辑**
   - 在 `showService` 方法被调用时，它简单地打印了 `myService` 字段。由于这个字段已经被成功地自动注入，所以我们看到了预期的输出，证明 `@Inject` 功能正常。
6. **结果**
   - 最终输出显示了 `myService` 已经被成功地注入到 `MyController` 中，并显示了其实例的内存地址。

#### 源码分析总结

1. **核心后处理器**

   - `AutowiredAnnotationBeanPostProcessor`是处理`@Inject`等注解的主要后处理器。它实现了两个关键的接口，`MergedBeanDefinitionPostProcessor`和`InstantiationAwareBeanPostProcessor`，这两个接口允许在bean的生命周期中的关键阶段进行干预，为属性注入提供了机制。

2. **收集阶段**
   + 检索Inject的元数据

     - Spring首先使用`postProcessMergedBeanDefinition`方法确保给定的bean定义与其预期的自动装配元数据一致。

     - 在该方法中, Spring会尝试查找与给定bean名称和类型相关的`InjectionMetadata`。这可能包括了该bean的字段和方法的注入信息。

   + 寻找匹配的Autowiring元数据
     - 在`findAutowiringMetadata`中，Spring确保始终为给定的bean名称和类获取最新和相关的`InjectionMetadata`。Spring也利用了缓存机制，以提高性能。

   + 构建Autowiring元数据

     - 在`buildAutowiringMetadata`方法中，Spring会查找类及其所有父类中的字段和方法，以找出所有带有自动装配注解的字段和方法。

     - 然后，为这些字段和方法创建一个统一的`InjectionMetadata`对象。

   + 检查注解类型
     - 在`AutowiredAnnotationBeanPostProcessor`的构造方法中，主要的目的是告诉这个后处理器它应该处理哪些注解。例如, `@Inject`就是这些注解之一。

3. **注入阶段**

   + 处理bean属性的后处理

     - 在`postProcessProperties`中，Spring用于处理bean属性的后处理，特别是通过`@Inject`进行的属性注入。

     - 这涉及到实际将解析得到的值注入到bean中。

   + 注入元数据的实际注入操作

     - 在`InjectionMetadata#inject`方法中，这里会对bean进行属性的实际注入。

     - Spring会遍历每一个需要注入的元素，并执行实际的注入操作。

   + 字段的实际注入

     - 在`AutowiredFieldElement#inject`中，Spring首先会检查字段的值是否已经被缓存。如果已缓存，则从缓存中获取，否则重新解析。

     - 然后，它确保字段是可访问的，并将解析的值设置到目标bean的相应字段中。

   + 解析依赖

     - 在`doResolveDependency`方法中，Spring开始尝试解析一个特定的依赖。

     - 首先，基于给定的类型，Spring会查找所有匹配的bean。

     - 如果找到多个匹配的bean，它会尝试确定哪一个是最佳的自动装配候选。

   + 获取bean的类型

     - 在`addCandidateEntry`方法中，Spring主要获取候选bean的类型，并将其添加到候选bean的集合中。

     - 使用`getType`方法，Spring可以通过bean的名字来获取对应bean的类型。

   + 从Spring容器中获取bean实例
     - 在`resolveCandidate`中，即从Spring容器中获取一个bean实例并注入到相应的字段或构造函数参数中。
   + 反射注入
     + 通过`field.set(bean, value)`来完成实际字段注入的步骤，将解析出的bean实例（value）注入到目标bean的对应字段上。这是整个`@Inject`流程的最终步骤