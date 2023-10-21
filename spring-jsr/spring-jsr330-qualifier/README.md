## @Qualifier

- [@Qualifier](#qualifier)
  - [一、基本信息](#一基本信息)
  - [二、注解描述](#二注解描述)
  - [三、注解源码](#三注解源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
    - [@Qualifier注册](#qualifier注册)
    - [@Qualifier解析](#qualifier解析)
  - [七、源码分析](#七源码分析)
    - [@Qualifier注册](#qualifier注册-1)
    - [@Qualifier解析](#qualifier解析-1)
      - [前置条件](#前置条件)
      - [解析入口](#解析入口)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN]() 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Qualifier源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-jsr/spring-jsr330-qualifier)

### 二、注解描述

`@Qualifier` 注解是 Java 的标准注解，来源于 JSR 330: Dependency Injection for Java。主要用于解决依赖注入中的歧义性，当有多个同类型的 bean 或实例可供选择时，指导容器明确选择哪一个进行注入。

### 三、注解源码

`@Qualifier` 表示它是一个基础注解，主要用于创建其他的自定义注解，这些新的注解通常用于限定和解决依赖注入时的歧义性。这与 Spring 和 JSR 330 中 `@Qualifier` 的定义和目的是一致的。

```java
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {

}
```

### 四、主要功能

1. **解决歧义性**
   + 当容器中存在多个同一类型的 bean 时，`@Qualifier` 用于指定哪一个具体的 bean 应该被注入。
2. **与 `@Autowired` 和 `@Inject` 配合使用**
   - 在 Spring 中，`org.springframework.beans.factory.annotation.Qualifier` 通常与 `org.springframework.beans.factory.annotation.Autowired` 配合使用。这是为了从Spring容器中解析bean时解决歧义。
   - 而在 JSR 330 规范（例如在 CDI 中）中，`javax.inject.Qualifier` 是一个元注解，用于定义新的注解作为限定符。这些自定义的限定符注解然后可以与 `javax.inject.Inject` 配合使用，以解决歧义。
3. **自定义限定符注解**
   + 尤其在 JSR 330 规范中，`@Qualifier` 用于创建其他自定义注解，这些新定义的注解随后可以用作限定符。
4. **提高代码的语义清晰度**
   + 通过使用 `@Qualifier` 或基于它的自定义注解，代码更具有描述性，更容易理解应注入哪个特定的 bean。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showMessage`方法。

```java
public class QualifierApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MessageController messageController = context.getBean(MessageController.class);
        messageController.showMessage();
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

我们定义了两个自定义注解，`@Email` 和 `@SMS`，它们都被标记为 `@Qualifier`。这意味着它们都可以被用作限定符注解。

```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {

}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SMS {

}
```

`EmailServiceImpl` & `SMSServiceImpl`这两个类都实现了 `MessageService` 接口，但分别带有 `@Email` 和 `@SMS` 限定符注解，意味着它们在被注入时可以被明确区分。

```java
public interface MessageService {
    String getMessage();
}

@Email
@Named
public class EmailServiceImpl implements MessageService {
    @Override
    public String getMessage() {
        return "Email message";
    }
}

@SMS
@Named
public class SMSServiceImpl implements MessageService {
    @Override
    public String getMessage() {
        return "SMS message";
    }
}
```

`MessageController` 注入了两种 `MessageService` 的实现：一种用于Email，另一种用于SMS。通过使用自定义的限定符注解 `@Email` 和 `@SMS`，确保了正确的服务实现被注入到对应的字段中。`showMessage` 方法用于显示这两个服务实现返回的消息。

```java
@Controller
public class MessageController {

    @Inject
    @Email
    private MessageService emailService;

    @Inject
    @SMS
    private MessageService smsService;

    public void showMessage() {
        System.out.println("EmailService: " + emailService.getMessage());
        System.out.println("SMSService: " + smsService.getMessage());
    }
}
```

运行结果发现，`@Email` 限定符确保了 `EmailServiceImpl` 被注入到 `emailService` 字段。`@SMS` 限定符确保了 `SMSServiceImpl` 被注入到 `smsService` 字段。`@Qualifier` 注解在这里起到了关键的作用，确保了正确的服务实现被注入，从而使得运行结果符合预期。

```java
EmailService: Email message
SMSService: SMS message
```

### 六、时序图

#### @Qualifier注册

~~~mermaid
sequenceDiagram
Title: @Qualifier注册时序图

QualifierApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
Note over AnnotationConfigApplicationContext: 上下文初始化开始，传入了一些组件类
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext()
Note over AnnotationConfigApplicationContext: 上下文构造器调用
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader:AnnotatedBeanDefinitionReader(registry)
Note over AnnotatedBeanDefinitionReader: 为给定的注册中心创建一个Bean定义读取器
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:AnnotatedBeanDefinitionReader(registry,environment)
Note over AnnotatedBeanDefinitionReader: 读取器构造器使用特定的环境设置
AnnotatedBeanDefinitionReader->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry)
Note over AnnotationConfigUtils: 注册注解配置处理器
AnnotationConfigUtils->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry,source)
Note right of AnnotationConfigUtils: 注册注解配置处理器
AnnotationConfigUtils->>QualifierAnnotationAutowireCandidateResolver:QualifierAnnotationAutowireCandidateResolver()
Note over QualifierAnnotationAutowireCandidateResolver: 创建一个新的限定符自动装配候选解析器
QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:this.qualifierTypes.add("javax.inject.Qualifier");
Note right of QualifierAnnotationAutowireCandidateResolver: 添加javax.inject.Qualifier到限定符类型列表
QualifierAnnotationAutowireCandidateResolver->>AnnotationConfigUtils:返回Resolver
Note right of AnnotationConfigUtils: 解析器创建完毕，现在返回给调用者
AnnotationConfigUtils->>DefaultListableBeanFactory:setAutowireCandidateResolver(autowireCandidateResolver)
Note over DefaultListableBeanFactory: 在Bean工厂中设置自动装配候选解析器
~~~

#### @Qualifier解析

~~~mermaid
sequenceDiagram
Title: @Qualifier解析时序图

InjectionMetadata->>AutowiredFieldElement:inject(bean,beanName,pvs)
Note over AutowiredFieldElement: 开始注入字段

AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field, bean, beanName)
Note over AutowiredFieldElement: 尝试解析字段的值

AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(desc,beanName,autowiredBeanNames,typeConverter)
Note over DefaultListableBeanFactory: 解决字段的依赖关系

DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor,requestingBeanName,autowiredBeanNames,typeConverter)
Note over DefaultListableBeanFactory: 执行依赖解析

DefaultListableBeanFactory->>DefaultListableBeanFactory:findAutowireCandidates(beanName,type,descriptor)
Note over DefaultListableBeanFactory: 查找可能的自动装配候选者

DefaultListableBeanFactory->>DefaultListableBeanFactory:isAutowireCandidate(candidate,descriptor)
Note over DefaultListableBeanFactory: 检查候选bean是否是合适的自动装配候选者

DefaultListableBeanFactory->>DefaultListableBeanFactory:isAutowireCandidate(beanName,descriptor,resolver)
Note right of DefaultListableBeanFactory: 进一步检查，使用解析器

DefaultListableBeanFactory->>DefaultListableBeanFactory:isAutowireCandidate(beanName,mbd,descriptor,resolver)
Note right of DefaultListableBeanFactory: 使用给定的bean定义进一步检查

DefaultListableBeanFactory->>QualifierAnnotationAutowireCandidateResolver:isAutowireCandidate(holder, descriptor)
Note over QualifierAnnotationAutowireCandidateResolver: 判断是否根据@Qualifier注解是自动装配的候选者

QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:checkQualifiers(bdHolder,annotationsToSearch)
Note over QualifierAnnotationAutowireCandidateResolver: 检查所有相关的限定符注解

QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:isQualifier(annotationType)
Note over QualifierAnnotationAutowireCandidateResolver: 判断给定的注解类型是否是一个有效的限定符
~~~

### 七、源码分析

#### @Qualifier注册

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showMessage`方法。

```java
public class QualifierApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MessageController messageController = context.getBean(MessageController.class);
        messageController.showMessage();
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们本次重点关注`this()`。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()`方法中，初始化了的两个核心组件，一个用于读取注解定义的bean (`AnnotatedBeanDefinitionReader`)也是本次重点分析的内容，另一个用于扫描类路径并自动检测bean组件 (`ClassPathBeanDefinitionScanner`)。

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry)`方法中，又调用了另一个构造函数。

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
    this(registry, getOrCreateEnvironment(registry));
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry,environment)`方法中，这是一个重要的调用`registerAnnotationConfigProcessors`，会向容器注册一系列的后置处理器，这些后置处理器对于处理各种注解（如 `@Inject`, `@Qualifier`等）至关重要。

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    Assert.notNull(environment, "Environment must not be null");
    this.registry = registry;
    this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
    AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry)`方法中，又调用了另外一个重载的方法。

```java
public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
    registerAnnotationConfigProcessors(registry, null);
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry,source)`方法中，在 Spring 的自动装配机制中，当有多个候选bean可以被注入到某个位置时，需要有一个方式来解决哪个bean是最佳的候选者。`AutowireCandidateResolver` 就是用来做这个决策的。

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
			BeanDefinitionRegistry registry, @Nullable Object source) {

    DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
    if (beanFactory != null) {
        // ... [代码部分省略以简化]
        if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
            beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        }
    }

    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#QualifierAnnotationAutowireCandidateResolver()`方法中，目的是为解析器设置一些基本配置。其中最重要的部分是尝试加载 JSR-330 的 `@Qualifier` 注解，并将其添加到 `qualifierTypes` 集合中，以便在后续的依赖注入过程中能够正确处理这个注解。如果 JSR-330 不可用，解析器会简单地跳过这个步骤。

```java
public QualifierAnnotationAutowireCandidateResolver() {
    // ... [代码部分省略以简化]
    try {
        this.qualifierTypes.add((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Qualifier",
                 		QualifierAnnotationAutowireCandidateResolver.class.getClassLoader()));
    }
    catch (ClassNotFoundException ex) {
        // JSR-330 API not available - simply skip.
    }
}
```

#### @Qualifier解析

##### 前置条件

在Spring中，`AutowiredAnnotationBeanPostProcessor` 负责处理多种依赖注入注解，包括 JSR-330 的 `@Inject` 注解。为了深入了解 `@Inject` 注解及其与 `@Qualifier` 注解的结合方式，研究这个后处理器是至关重要的。简而言之，为了完全掌握 `@Inject` 和 `@Qualifier` 的协同工作原理，深入阅读 `@Inject` 博客是非常必要的。这些博客为我们提供了对如何在 bean 生命周期中的关键阶段处理这些注解的深入理解。

1. **@Inject注解源码分析**:
   - 在这篇博客中，我们会深入了解 `@Inject` 注解的背后原理。从 JSR-330 规范的引入，到如何在 Spring 中正确使用，以及与其他注解如 `@Autowired` 的差异，这篇博客为我们提供了全面的视角。
   - 🔗 [@Inject注解传送门](https://github.com/xuchengsheng/spring-reading/blob/master/spring-jsr/spring-jsr330-inject)

##### 解析入口

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

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`方法中，主要目的是解析给定的依赖描述符 (`DependencyDescriptor`)，并尝试找到一个合适的 bean 来满足这个依赖。

```java
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

    // ... [代码部分省略以简化]

    try {
        // ... [代码部分省略以简化]

        // 步骤1. 根据依赖描述符查找匹配的bean
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
        
        // ... [代码部分省略以简化]
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#findAutowireCandidates`方法中，首先基于给定的类型获取所有可能的bean名。接着，对于每一个可能的候选bean，它检查该bean是否是一个合适的自动注入候选，如果是，它将这个bean添加到结果集中。最后，方法返回找到的所有合适的候选bean。

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

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#isAutowireCandidate(beanName, descriptor)`方法中，首先是调用 `getAutowireCandidateResolver()` 方法时，我们会得到这里设置的那个解析器，会根据其实现来决定哪个 bean 是自动装配的候选者，尤其当存在多个可能的候选者时，会考虑到 `@Qualifier` 和其他相关的注解，然后会进一步调用 `isAutowireCandidate` 的另一个重载版本，并且会传入解析器。

```java
@Override
public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
    throws NoSuchBeanDefinitionException {

    return isAutowireCandidate(beanName, descriptor, getAutowireCandidateResolver());
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#isAutowireCandidate(beanName,descriptor,resolver)`方法中，首先是调用`getMergedLocalBeanDefinition(bdName)`来获取被依赖Bean的定义， 然后会进一步调用 `isAutowireCandidate` 的另一个重载版本，并且会传入合并的 bean 定义。

```java
protected boolean isAutowireCandidate(
			String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver)
			throws NoSuchBeanDefinitionException {

    String bdName = BeanFactoryUtils.transformedBeanName(beanName);
    if (containsBeanDefinition(bdName)) {
        return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(bdName), descriptor, resolver);
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#isAutowireCandidate(beanName, mbd,descriptor, resolver)`方法中，首先是初始化了一个`BeanDefinitionHolder`，这是一个方便的数据结构，用于同时持有 `BeanDefinition`, bean 的名称和别名。这对于解析自动装配候选项很有用，因为有时我们需要知道 bean 的名称和别名，然后会进一步调用解析器的 `isAutowireCandidate` 方法，并且会传入`BeanDefinitionHolder`与依赖描述符 。

```java
protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd,
			DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {

    // ... [代码部分省略以简化]
    BeanDefinitionHolder holder = (beanName.equals(bdName) ?
                                   this.mergedBeanDefinitionHolders.computeIfAbsent(beanName,
                                                                                    key -> new BeanDefinitionHolder(mbd, beanName, getAliases(bdName))) :
                                   new BeanDefinitionHolder(mbd, beanName, getAliases(bdName)));
    return resolver.isAutowireCandidate(holder, descriptor);
}
```

在`org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`方法中，主要目的是增强了对 `@Qualifier` 注解的支持。如果一个bean或依赖被 `@Qualifier` 注解，并指定了一个bean的名称，那么只有名称匹配的bean会被认为是自动装配的候选者。如果没有指定名称，但指定了其他属性或元注解，那么只有匹配这些属性或元注解的bean会被认为是自动装配的候选者。

```java
@Override
public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
    // 首先通过父类的方法进行基本的匹配检查。
    boolean match = super.isAutowireCandidate(bdHolder, descriptor);
    
    // 如果基本检查通过，则进一步检查 bdHolder 和 descriptor 中的注解是否匹配。
    if (match) {
        // 检查候选bean的资格符与依赖描述符的注解是否匹配。
        match = checkQualifiers(bdHolder, descriptor.getAnnotations());
        
        if (match) {
            // 获取与依赖相关的方法参数（如果有的话，例如当依赖是一个setter方法的参数时）。
            MethodParameter methodParam = descriptor.getMethodParameter();
            
            // 如果有关联的方法参数，则进行进一步的检查。
            if (methodParam != null) {
                Method method = methodParam.getMethod();
                
                // 如果方法是void返回类型（如setter方法），则检查它的注解与候选bean的资格符是否匹配。
                if (method == null || void.class == method.getReturnType()) {
                    match = checkQualifiers(bdHolder, methodParam.getMethodAnnotations());
                }
            }
        }
    }
    
    // 返回最终的匹配结果。
    return match;
}
```

在`org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#checkQualifiers`方法中，主要目的是确保待注入的bean与所有相关的 `@Qualifier` 注解和元注解匹配。如果其中一个注解或元注解与bean定义不匹配，那么这个bean就不是当前注入点的合适候选。

```java
protected boolean checkQualifiers(BeanDefinitionHolder bdHolder, Annotation[] annotationsToSearch) {
    // 如果注解数组为空，直接返回 true
    if (ObjectUtils.isEmpty(annotationsToSearch)) {
        return true;
    }
    
    SimpleTypeConverter typeConverter = new SimpleTypeConverter();
    for (Annotation annotation : annotationsToSearch) {
        Class<? extends Annotation> type = annotation.annotationType();
        boolean checkMeta = true;
        boolean fallbackToMeta = false;
        
        // 检查当前注解是否是 @Qualifier 或自定义的资格符注解
        if (isQualifier(type)) {
            // 如果是，并且与bean定义匹配
            if (!checkQualifier(bdHolder, annotation, typeConverter)) {
                // 不匹配则可能需要检查元注解
                fallbackToMeta = true; 
            } else {
                // 匹配则不需要进一步检查元注解
                checkMeta = false; 
            }
        }
        
        // 如果不是资格符注解或与bean定义不匹配，但有元注解是资格符注解
        if (checkMeta) {
            boolean foundMeta = false;
            for (Annotation metaAnn : type.getAnnotations()) {
                Class<? extends Annotation> metaType = metaAnn.annotationType();
                // 检查元注解是否是资格符注解
                if (isQualifier(metaType)) {
                    foundMeta = true;
                    // 只有当 @Qualifier 注解有值或其他条件满足时，才会接受元注解的匹配
                    if ((fallbackToMeta && ObjectUtils.isEmpty(AnnotationUtils.getValue(metaAnn))) ||
                        !checkQualifier(bdHolder, metaAnn, typeConverter)) {
                        // 元注解不匹配
                        return false; 
                    }
                }
            }
            if (fallbackToMeta && !foundMeta) {
                return false; // 需要元注解，但没有找到
            }
        }
    }
    
    // 所有注解都匹配
    return true;
}
```

在`org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#isQualifier`方法中，主要目的是确定给定的注解是否为一个"资格符"注解，也就是我们通常所说的限定符注解，如`@Qualifier`。这是通过检查注解是否存在于已知的`qualifierTypes`集合中，或者它是否带有这些资格符注解来完成的。

```java
protected boolean isQualifier(Class<? extends Annotation> annotationType) {
    // 遍历已知的资格符注解类型
    for (Class<? extends Annotation> qualifierType : this.qualifierTypes) {
        // 如果给定的注解是已知的资格符注解，或者它带有这样的元注解
        if (annotationType.equals(qualifierType) || annotationType.isAnnotationPresent(qualifierType)) {
            return true; // 是资格符注解
        }
    }
    return false; // 不是资格符注解
}
```

### 八、注意事项

1. **确保正确的依赖注入**
   + 使用 `@Qualifier` 主要是为了解决Spring容器中有多个同类型Bean的问题。没有 `@Qualifier`，Spring将无法决定注入哪一个Bean。但使用 `@Qualifier` 时，我们需要确保给定的资格符名称确实存在，否则Spring会抛出异常。
2. **与其他注解组合**
   +  `@Qualifier` 通常与 `@Autowired` 或 `@Inject` 一起使用。确保在正确的地方使用它（字段、setter方法或构造函数）。
3. **自定义资格符注解**
   + 我们可以创建自己的注解，并使用 `@Qualifier` 作为元注解。这样，我们可以创建具有特定命名或其他语义的资格符注解，使代码更具可读性。
4. **使用字符串名称**
   +  `@Qualifier` 默认使用字符串值来指定Bean的名称。这意味着，如果我们重命名了Bean或更改了其名称，我们也需要更改所有使用这个Bean名称的 `@Qualifier` 注解。
5. **与Java配置一起使用**
   + 当使用Java配置创建Beans时，可以使用 `@Bean` 的方法名称作为资格符名称。这使得使用Java配置和 `@Qualifier` 更为一致。
6. **与 `@Primary` 的关系**
   + 如果我们同时使用了 `@Primary` 和 `@Qualifier`，则 `@Qualifier` 的优先级更高。也就是说，如果一个Bean被标记为 `@Primary`，但在注入点使用了 `@Qualifier`，则会使用 `@Qualifier` 指定的Bean。
7. **名称和类型的匹配**
   + 尽管 `@Qualifier` 主要用于通过名称进行匹配，但Spring仍然会验证匹配的Bean类型。所以，如果Bean名称匹配但类型不匹配，仍然会出现异常。
8. **与JSR-330的兼容性**
   + Spring的 `@Qualifier` 注解与JSR-330（Java的依赖注入规范）中的 `javax.inject.Qualifier` 注解兼容。但是，当使用JSR-330标准时，确保依赖注入提供程序（如Spring）正确支持它。

### 九、总结

#### 最佳实践总结

1. **明确的配置**
   + 使用 `AnnotationConfigApplicationContext` 作为Spring容器，并通过构造参数指定配置类，使得Spring容器初始化过程清晰明了。
2. **利用组件扫描**
   + 通过 `@ComponentScan` 注解，自动扫描指定包及其子包中的组件，减少了手动注册bean的工作，使代码更简洁、高效。
3. **自定义资格符注解**
   + 为了解决多个同类型Bean的问题，定义了 `@Email` 和 `@SMS` 两个自定义限定符注解。这使得代码更有可读性，并提供了更明确的注入意图。
4. **准确的服务注入**
   + 在 `MessageController` 中，使用了自定义的 `@Email` 和 `@SMS` 注解与 `@Inject` 组合，确保了正确的 `MessageService` 实现被注入到相应的字段。
5. **清晰的输出**
   + 通过 `showMessage` 方法的输出，我们可以清晰地看到 `@Qualifier` 的作用，确保了不同的服务实现被正确注入。
6. **充分利用Java配置**
   + 通过 `@Configuration` 和 `@ComponentScan`，结合Java配置，Spring容器的初始化和bean的注册过程都变得更加直观和简洁。
7. **注解的扩展性**
   + 通过使用 `@Qualifier` 作为元注解，自定义注解的方式体现了Spring的灵活性和注解的扩展性。
8. **保持代码整洁和模块化**
   + 每个类和接口都有明确的职责，并且代码被组织得清晰、模块化，这不仅使代码更易于维护，还提高了代码的可读性和可复用性。

#### 源码分析总结

1. **应用启动及上下文初始化**
   + 当创建`AnnotationConfigApplicationContext`时，它接收一个配置类（如`MyConfiguration`）作为参数，用于初始化Spring上下文。启动时，会从Spring上下文中获取并使用相应的Bean。
2. **AnnotationConfigApplicationContext的构造过程**
   + 在其构造函数中，`AnnotationConfigApplicationContext`执行了几个关键步骤，其中最核心的是`this()`，该方法初始化了两个组件：`AnnotatedBeanDefinitionReader`（负责读取注解定义的bean）和`ClassPathBeanDefinitionScanner`（负责扫描类路径并自动检测bean组件）。
3. **AnnotatedBeanDefinitionReader的初始化**
   + `AnnotatedBeanDefinitionReader`在其构造函数中调用`registerAnnotationConfigProcessors`，此方法向容器注册了一系列的后置处理器，这些后置处理器对于处理各种注解（如`@Inject`, `@Qualifier`等）是关键。
4. **Qualifier注解的注册**
   + 当创建`QualifierAnnotationAutowireCandidateResolver`实例时，它会尝试加载JSR-330的`@Qualifier`注解，并将其添加到`qualifierTypes`集合中。这使得在后续的依赖注入过程中，Spring可以正确处理这个注解。
5. **依赖解析过程**
   + 当Spring尝试解析某个依赖时，它会进入`DefaultListableBeanFactory#resolveDependency`。此方法首先尝试获取一个延迟解析代理，如果不能获得，则会尝试解析依赖，此过程涉及到`doResolveDependency`方法。
6. **自动装配候选检查**
   + 在`findAutowireCandidates`中，Spring首先找出所有可能的bean名称，然后检查每一个可能的候选bean，看看它是否是一个合适的自动装配候选。此检查涉及到`isAutowireCandidate`方法。
7. **利用AutowireCandidateResolver判断是否为自动装配候选者**
   + `isAutowireCandidate`方法的工作是判断某个bean是否是自动装配的候选者。在有多个可能的候选bean时，`AutowireCandidateResolver`（这里的实例是`QualifierAnnotationAutowireCandidateResolver`）会被用来做决策，考虑到`@Qualifier`和其他相关的注解。
8. **处理@Qualifier注解**
   + `QualifierAnnotationAutowireCandidateResolver`的主要任务是增强对`@Qualifier`注解的支持。它确保待注入的bean与所有相关的`@Qualifier`注解和元注解匹配。如果其中一个注解或元注解与bean定义不匹配，那么这个bean就不是当前注入点的合适候选。
9. **资格符检查**
   + `isQualifier`方法则用来确定给定的注解是否为一个资格符注解（限定符注解），例如`@Qualifier`。这是通过检查注解是否存在于已知的`qualifierTypes`集合中或它是否带有这些资格符注解来完成的。