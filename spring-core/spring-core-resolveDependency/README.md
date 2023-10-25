### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN]() 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading)

### 二、方法描述

`resolveDependency` 是 Spring 框架中 `ConfigurableListableBeanFactory` 接口中定义的一个方法。这个方法的核心功能是解析并提供 bean 的依赖项。它主要在处理 `@Autowired` ， `@Inject` ，`@Value`这样的自动装配场景时起作用。

### 三、方法源码

Spring IOC 容器中的一个核心方法，用于解析 bean 之间的依赖关系。当容器需要知道应该为特定的 bean、字段、构造函数或方法注入哪个其他 bean 时，就会调用这个方法。

```java
/**
 * 在此工厂中定义的 beans 之间，解析指定的依赖项。
 * 
 * 该方法尝试匹配和返回一个适当的 bean 实例来满足给定的依赖描述符。
 * 依赖描述符可以描述字段、方法或构造函数中的依赖。
 * 
 * @param descriptor 描述依赖的对象，提供有关依赖类型、限定符等的详细信息。
 * @param requestingBeanName 声明或请求依赖的 bean 的名称。这通常用于解决 bean 之间的循环依赖。
 * @return 对应的 bean 实例以满足该依赖，如果没有合适的匹配，则返回 null。
 * 
 * @throws NoSuchBeanDefinitionException 当没有找到匹配的 bean 时抛出。
 * @throws NoUniqueBeanDefinitionException 当存在多个可能的匹配并且没有明确的选择时抛出。
 * @throws BeansException 如果由于其他原因解析失败则抛出。
 * 
 * @since 2.5
 * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter) 用于更复杂的依赖解析场景，允许传递排除的 beans 和类型转换器。
 */
@Nullable
Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;
```

### 四、主要功能

1. **依赖描述符**
   + 该方法接收一个 `DependencyDescriptor` 参数，它描述了所需的依赖关系。这个描述符可以表示一个字段、方法参数或构造函数参数的依赖。
2. **自动装配限定符**
   + 如果存在多个匹配的 bean，并且所需的依赖有 `@Autowired` 和 `@Qualifier` 注解，那么 `resolveDependency` 会考虑这些注解来确定正确的 bean。
3. **解决循环依赖**
   + 该方法也会考虑循环依赖的问题。例如，如果 Bean A 依赖于 Bean B，而 Bean B 又依赖于 Bean A，那么这种情况会被处理。
4. **返回匹配的 bean**
   + 此方法尝试返回一个与描述符匹配的 bean 实例。如果找不到合适的 bean，它可能返回 null 或抛出一个异常，具体取决于描述符的设置。
5. **异常处理**
   + 如果没有找到合适的 bean 或找到了多个合适的 bean 且没有明确的选择，该方法会抛出相应的异常。
6. **其他 Considerations**：
   - 可以考虑其他因素，如 `@Primary` 注解。
   - 对于基本类型或字符串类型的属性，可以解析 `@Value` 注解，从属性文件或环境变量中获取值。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。代码调用了两个方法（`methodResolveDependency` 和 `fieldResolveDependency`）来分别解析方法和字段的依赖。具体来说，它们会使用反射来获取目标方法或字段。创建一个描述这个方法或字段的 `DependencyDescriptor`。使用 `resolveDependency` 方法来从 Spring 容器中解析真正的依赖。使用反射来将解析得到的依赖注入到目标对象中。在解析方法和字段的依赖之后，代码通过格式化的字符串打印了相关信息。例如，它显示了方法或字段的完全限定名称和解析得到的依赖对象的值。

```java
public class ResolveDependencyApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        // 获得Bean工厂
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // 被注入对象
        MyServiceB injectTarget = new MyServiceB();

        System.out.println("Before MyServiceB = " + injectTarget + "\n");

        methodResolveDependency(beanFactory, injectTarget, "setMethodMyServiceA");
        fieldResolveDependency(beanFactory, injectTarget, "fieldMyServiceA");
        fieldResolveDependency(beanFactory, injectTarget, "myPropertyValue");

        System.out.println("After MyServiceB = " + injectTarget + "\n");
    }

    /**
     * 解析方法依赖
     *
     * @param beanFactory
     * @param injectTarget
     */
    public static void methodResolveDependency(ConfigurableListableBeanFactory beanFactory, Object injectTarget, String name) {
        try {
            // 1. 获取MyServiceB类中名为setMyServiceA的方法的引用
            Method method = injectTarget.getClass().getMethod(name, MyServiceA.class);

            // 2. 创建一个描述此方法参数的DependencyDescriptor
            DependencyDescriptor desc = new DependencyDescriptor(new MethodParameter(method, 0), true);

            // 3. 使用BeanFactory来解析这个方法参数的依赖
            Object value = beanFactory.resolveDependency(desc, null);

            System.out.println("解析方法依赖结果:");
            System.out.println("---------------------------------------------");
            System.out.println(String.format("Name:   %s.%s",method.getDeclaringClass().getName(),method.getName()));
            System.out.println(String.format("Value:  %s", value));
            System.out.println("---------------------------------------------\n");

            // 4. 使方法可访问（特别是如果它是private的）
            ReflectionUtils.makeAccessible(method);

            // 5. 使用反射调用这个方法，将解析得到的依赖注入到目标对象中
            method.invoke(injectTarget, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析字段依赖
     *
     * @param beanFactory
     * @param injectTarget
     */
    public static void fieldResolveDependency(ConfigurableListableBeanFactory beanFactory, Object injectTarget, String name) {
        try {
            // 1. 获取MyServiceB类中名为fieldMyServiceA的字段的引用
            Field field = injectTarget.getClass().getDeclaredField(name);

            // 2. 创建一个描述此字段的DependencyDescriptor
            DependencyDescriptor desc = new DependencyDescriptor(field, true);

            // 3. 使用BeanFactory来解析这个字段的依赖
            Object value = beanFactory.resolveDependency(desc, null);

            System.out.println("解析字段依赖结果:");
            System.out.println("---------------------------------------------");
            System.out.println(String.format("Name:   %s.%s", field.getDeclaringClass().getName(), field.getName()));
            System.out.println(String.format("Value:  %s", value));
            System.out.println("---------------------------------------------\n");

            // 4. 使字段可访问（特别是如果它是private的）
            ReflectionUtils.makeAccessible(field);

            // 5. 使用反射设置这个字段的值，将解析得到的依赖注入到目标对象中
            field.set(injectTarget, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

在`MyConfiguration`类中，使用了`@ComponentScan("com.xcs.spring")`注解告诉 Spring 在指定的包（在这里是 "`com.xcs.spring`"）及其子包中搜索带有 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类，并将它们自动注册为 beans。这样，spring就不必为每个组件明确写一个 bean 定义。Spring 会自动识别并注册它们。另外使用`@PropertySource`注解从类路径下的`application.properties`文件中加载属性。

```java
@Configuration
@ComponentScan("com.xcs.spring")
@PropertySource("classpath:application.properties")
public class MyConfiguration {
    
}
```

`application.properties`文件在`src/main/resources`目录中，并添加以下内容。

```properties
my.property.value = Hello from Environment!
```

`MyService` 是一个简单的服务类，但我们没有定义任何方法或功能。

```java
@Service
public class MyServiceA {
    
}
```

首先 `MyServiceB` 没有被 Spring 托管，那么它在代码中的表现就与一个普通的 Java 类没有什么不同。虽然 `MyServiceB` 本身不是 Spring 托管的，但 `ResolveDependencyApplication` 类中，我给大家展示了如何使用 Spring 的底层 API 手动解析和注入依赖。

- **字段 `myPropertyValue`**
  + 虽然它有一个 `@Value` 注解，但由于 `MyServiceB` 不是一个 Spring 管理的 bean，所以这个注解不会自动被解析。
- **字段 `myServiceA` 和 `fieldMyServiceA`**
  + 由于没有自动的 Spring 依赖注入，这两个字段默认为 `null`。
- **方法 `setMyServiceA`**
  + 这是一个普通的 setter 方法。但在 `MyServiceB` 不被 Spring 托管的情况下，它只是一个普通的 setter。
- **方法 `toString`**
  + 该方法为该类提供了一个自定义的字符串表示。这与 `MyServiceB` 是否被 Spring 托管无关。

```java
public class MyServiceB {

    /**
     * 方法注入
     */
    private MyServiceA methodMyServiceA;

    /**
     * 字段注入
     */
    private MyServiceA fieldMyServiceA;

    /**
     * 字段注入 (环境变量)
     */
    @Value("${my.property.value}")
    private String myPropertyValue;

    public void setMethodMyServiceA(MyServiceA methodMyServiceA){
        this.methodMyServiceA = methodMyServiceA;
    }

    @Override
    public String toString() {
        return "MyServiceB{" +
                "myPropertyValue='" + myPropertyValue + '\'' +
                ", methodMyServiceA=" + methodMyServiceA +
                ", fieldMyServiceA=" + fieldMyServiceA +
                '}';
    }
}
```

运行结果发现，使用了 Spring 的底层 `resolveDependency` 方法来为 `MyServiceB` 类的字段和方法手动注入依赖。虽然在常规的 Spring 开发中我们通常不这样做（因为 Spring 提供了更高级的自动化工具进行依赖注入），但这主要目的是给大家展示 Spring 如何在底层工作原理，并提供了一种手动控制依赖注入的方法。

```java
Before MyServiceB = MyServiceB{myPropertyValue='null', myServiceA=null, fieldMyServiceA=null}

解析方法依赖结果:
---------------------------------------------
Name:   com.xcs.spring.service.MyServiceB.setMyServiceA
Value:  com.xcs.spring.service.MyServiceA@202b0582
---------------------------------------------

解析字段依赖结果:
---------------------------------------------
Name:   com.xcs.spring.service.MyServiceB.fieldMyServiceA
Value:  com.xcs.spring.service.MyServiceA@202b0582
---------------------------------------------

解析字段依赖结果:
---------------------------------------------
Name:   com.xcs.spring.service.MyServiceB.myPropertyValue
Value:  Hello from Environment!
---------------------------------------------

After MyServiceB = MyServiceB{myPropertyValue='Hello from Environment!', myServiceA=com.xcs.spring.service.MyServiceA@202b0582, fieldMyServiceA=com.xcs.spring.service.MyServiceA@202b0582}

```

### 六、时序图

#### 环境属性和变量

通过使用`@Value`注解，我们可以请求一个特定的环境属性或变量。例如，我们可能会这样请求一个名为`my.property.value`的属性。

~~~mermaid
sequenceDiagram
Title: resolveDependency方法解析环境属性和变量时序图

ResolveDependencyApplication-->>AbstractAutowireCapableBeanFactory:resolveDependency(descriptor,requestingBeanName)
note over AbstractAutowireCapableBeanFactory: 请求解析依赖

AbstractAutowireCapableBeanFactory->>DefaultListableBeanFactory:resolveDependency(descriptor, requestingBeanName, null, null)
note over DefaultListableBeanFactory: 转到具体的Bean工厂进行解析

DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)
note over DefaultListableBeanFactory: 进行实际的依赖解析

DefaultListableBeanFactory->>DefaultListableBeanFactory:getAutowireCandidateResolver()
note over DefaultListableBeanFactory: 获取当前的依赖解析策略

DefaultListableBeanFactory->>QualifierAnnotationAutowireCandidateResolver:getSuggestedValue(descriptor)
note over QualifierAnnotationAutowireCandidateResolver: 基于给定的描述符查找建议的值

QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:findValue(annotationsToSearch)
note over QualifierAnnotationAutowireCandidateResolver: 在指定的注解中寻找值

QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:extractValue(attr)
note over QualifierAnnotationAutowireCandidateResolver: 从注解属性中提取值

QualifierAnnotationAutowireCandidateResolver->>DefaultListableBeanFactory:返回@Value注解的表达式
note over DefaultListableBeanFactory: 解析注解中的表达式或值

DefaultListableBeanFactory->>AbstractBeanFactory:resolveEmbeddedValue(value)
note over AbstractBeanFactory: 解析嵌入的值（如占位符或SpEL表达式）

DefaultListableBeanFactory->>AbstractBeanFactory:evaluateBeanDefinitionString(strVal, bd)
note over AbstractBeanFactory: 对bean定义字符串进行评估（可能是一个表达式）

DefaultListableBeanFactory->>AbstractBeanFactory:getTypeConverter()
note over AbstractBeanFactory: 获取用于类型转换的转换器

DefaultListableBeanFactory->>TypeConverterSupport:convertIfNecessary(value, type, typeDescriptor())
note over TypeConverterSupport: 根据需要将值转换为指定的类型

TypeConverterSupport->>DefaultListableBeanFactory:返回类型转换的结果
note over DefaultListableBeanFactory: 用转换后的值继续解析流程

DefaultListableBeanFactory->>AbstractAutowireCapableBeanFactory:返回环境属性
AbstractAutowireCapableBeanFactory->>ResolveDependencyApplication:返回环境属性
~~~

#### Bean依赖

这是其主要的功能。当我们有一个bean，它需要另一个bean的实例时，`resolveDependency` 会为我们找到并提供所需的bean。例如，如果一个`MyServiceB`类需要一个`MyServiceA`类的实例，那么`resolveDependency` 可以为`MyServiceB`类提供一个`MyServiceA`的实例。

~~~mermaid
sequenceDiagram
Title: resolveDependency方法解析Bean依赖时序图

ResolveDependencyApplication-->>AbstractAutowireCapableBeanFactory:resolveDependency(descriptor,requestingBeanName)
note right of AbstractAutowireCapableBeanFactory: 开始解析依赖请求

AbstractAutowireCapableBeanFactory->>DefaultListableBeanFactory:resolveDependency(descriptor, requestingBeanName, null, null)
note right of DefaultListableBeanFactory: 委托给具体的Bean工厂进行依赖解析

DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)
note right of DefaultListableBeanFactory: 执行具体的依赖解析逻辑

DefaultListableBeanFactory->>DefaultListableBeanFactory:resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter)
note right of DefaultListableBeanFactory: 解析满足条件的多个Beans

DefaultListableBeanFactory->>DefaultListableBeanFactory:findAutowireCandidates(beanName, type, descriptor)
note right of DefaultListableBeanFactory: 查找适合自动装配的候选Beans

DefaultListableBeanFactory->>DefaultListableBeanFactory:isSelfReference(beanName, candidate)
note right of DefaultListableBeanFactory: 检查bean是否引用了自身

DefaultListableBeanFactory->>DefaultListableBeanFactory:isAutowireCandidate(candidate, descriptor)
note right of DefaultListableBeanFactory: 检查bean是否是一个合适的自动装配候选

DefaultListableBeanFactory->>DefaultListableBeanFactory:addCandidateEntry(result, candidate, descriptor, requiredType)
note right of DefaultListableBeanFactory: 将找到的候选添加到结果集中

DefaultListableBeanFactory->>DependencyDescriptor:resolveCandidate(candidateName, requiredType, beanFactory)
note right of DependencyDescriptor: 尝试解析并返回候选Bean

DependencyDescriptor->>AbstractBeanFactory:getBean(String name)
note right of AbstractBeanFactory: 请求获取指定名称的bean

AbstractBeanFactory->>DependencyDescriptor:返回候选的Bean对象
note right of DependencyDescriptor: 返回找到的Bean实例

DependencyDescriptor->>DefaultListableBeanFactory:返回候选的Bean对象
note right of DefaultListableBeanFactory: 将Bean实例返回给Bean工厂

DefaultListableBeanFactory->>DefaultListableBeanFactory:determineAutowireCandidate(matchingBeans, descriptor)
note right of DefaultListableBeanFactory: 从匹配的Beans中确定自动装配的候选

DefaultListableBeanFactory->>AbstractAutowireCapableBeanFactory:返回Bean
note right of AbstractAutowireCapableBeanFactory: 将确定的Bean返回给上一级的Bean工厂

AbstractAutowireCapableBeanFactory->>ResolveDependencyApplication:返回bean
note right of ResolveDependencyApplication: 返回bean给依赖解析的请求者

~~~



### 七、源码分析

### 八、注意事项

### 九、总结

#### 最佳实践总结

#### 源码分析总结