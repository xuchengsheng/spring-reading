## @Conditional

- [@Conditional](#conditional)
  - [一、基本信息](#一基本信息)
  - [二、注解描述](#二注解描述)
  - [三、注解源码](#三注解源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
    - [在@Bean上使用](#在bean上使用)
    - [在@Configuration上使用](#在configuration上使用)
    - [自定义组合注解](#自定义组合注解)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)
  - [八、注意事项](#八注意事项)
  - [九、总结](#九总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133800722) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Conditional源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-conditional)

### 二、注解描述

`@Conditional`注解，是用来基于满足某些特定条件来决定一个Bean是否应该被注册到Spring容器的。这提供了一种灵活的方式来根据环境、配置或其他因素来决定是否激活或者创建某个Bean。

### 三、注解源码

`@Conditional`注解是 Spring 框架自 3.0 版本开始引入的一个核心注解，用于指示一个组件只在所有指定条件匹配时才能被注册。

```java
/**
 * 表明只有当所有指定的条件都满足时，组件才有资格被注册。
 *
 * 条件是可以在bean定义被注册前以编程方式确定的任何状态（参考 Condition 获取详情）。
 *
 * @Conditional 注解可以以下列方式使用：
 * 
 * 作为直接或间接使用 @Component 注解的任何类的类型级别注解，包括 Configuration @Configuration 类
 * 作为元注解，用于组合自定义的范型注解
 * 作为任何 Bean @Bean 方法的方法级别注解
 * 
 *
 * 如果一个 @Configuration 类标记为 @Conditional，与该类关联的所有 @Bean 方法、Import @Import 注解，
 * 和 ComponentScan @ComponentScan 注解都将受到这些条件的限制。
 *
 * 注意：不支持 @Conditional 注解的继承；任何从超类或从被覆盖的方法继承的条件都不会被考虑。
 * 为了强制这些语义，@Conditional 本身未声明为 java.lang.annotation.Inherited @Inherited；
 * 此外，任何用  @Conditional 作为元注解的自定义组成注解也不应声明为 @Inherited。
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 4.0
 * @see Condition
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {

	/**
	 * 所有必须满足的 {@link Condition} 类，以便组件可以被注册。
	 */
	Class<? extends Condition>[] value();
}
```

### 四、主要功能

1. **条件化 Bean 注册**
   + 可以根据特定的条件来决定是否创建并注册一个 Bean。这允许我们根据环境、配置或其他因素动态地选择哪些 Bean 需要被实例化。
2. **条件化配置类**
   + 不仅可以对单个 Bean 使用，还可以对整个配置类使用。如果配置类上的条件不满足，那么该配置类中定义的所有 Beans 都不会被注册。
3. **灵活性**
   + 与一个实现了 `Condition` 接口的类一起使用。这个接口允许我们定义自己的条件逻辑，使得其可以非常灵活地根据各种场景来决定是否注册 Bean。
4. **与其他注解组合**
   + 除了与 `@Bean` 和 `@Configuration` 注解一起使用外，`@Conditional` 还可以作为元注解，用于创建自定义的组合注解，这些组合注解内部使用 `@Conditional` 来应用条件逻辑。
5. **对整个配置类的影响**
   + 当 `@Conditional` 用于配置类时，不仅仅是该类，还有与该类关联的所有 `@Bean` 方法、`@Import` 注解和 `@ComponentScan` 注解都将受到条件的影响。
6. **不支持继承**
   + `@Conditional` 注解本身不是继承的，因此，从父类或接口继承的条件不会被子类考虑。

### 五、最佳实践

#### 在@Bean上使用

首先来看看启动类入口，首先设置一个系统属性`enable.bean`为`true`，然后上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，最后打印了Spring上下文中所有的bean定义名称。

```java
public class ConditionBeanApplication {

    public static void main(String[] args) {
        System.setProperty("enable.bean","true");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyBeanConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

`MyBeanConfiguration`，其中定义了两个bean：`user1`和`user2`。`user1` bean的创建是基于条件的，具体取决于`BeanPropertyCondition`条件的结果。而`user2` bean则无条件创建。

```java
@Configuration
public class MyBeanConfiguration {

    @Bean
    @Conditional(BeanPropertyCondition.class)
    public User1 user1() {
        return new User1();
    }

    @Bean
    public User2 user2() {
        return new User2();
    }
}
```

`BeanPropertyCondition`。这个实现会根据`enable.bean`属性的值决定是否满足条件。具体来说：如果环境属性`enable.bean`的值是`true`，则`user1` bean会被创建并添加到Spring容器。如果`enable.bean`不是`true`（或者没有设置这个属性），`user1` bean不会被创建。

```java
public class BeanPropertyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "true".equals(context.getEnvironment().getProperty("enable.bean"));
    }
}
```

定义两个简单的Java类：`User1`和`User2`。

```java
public class User1 {

}

public class User2 {

}
```

当`enable.bean`为`true`运行结果发现，根据`enable.bean`属性的值来注册`user1` bean，而`user2` bean则不受此属性的影响。

```java
beanDefinitionName = myBeanConfiguration
beanDefinitionName = user1
beanDefinitionName = user2
```

当`enable.bean`为`false`运行结果发现，`enable.bean`值为`false`，所以条件不满足。因此`user1`bean不会被注册，`user2` bean不受任何条件的影响。

```java
beanDefinitionName = myBeanConfiguration
beanDefinitionName = user2
```

#### 在@Configuration上使用

首先来看看启动类入口，首先设置一个系统属性`enable.config`为`true`，然后上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，最后打印了Spring上下文中所有的bean定义名称。

```java
public class ConditionConfigurationApplication {

    public static void main(String[] args) {
        System.setProperty("enable.config","true");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfigConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

`MyConfigConfiguration`，其中定义了两个bean：`user3`和`user4`。当`ConfigPropertyCondition`条件不满足时`MyConfigConfiguration`配置类不被激活，该配置类中定义的`user3`和`user4`不会被注册。

```java
@Configuration
@Conditional(ConfigPropertyCondition.class)
public class MyConfigConfiguration {

    @Bean
    public User3 user3() {
        return new User3();
    }

    @Bean
    public User4 user4() {
        return new User4();
    }
}
```

`ConfigPropertyCondition`。这个实现会根据`enable.config`属性的值决定是否满足条件。具体来说：当`enable.config`设置为`true`，`ConfigPropertyCondition`满足，`MyConfigConfiguration`配置类被激活，`user3`和`user4` beans都将被注册到Spring上下文。当`enable.config`设置为`false`或未设置，`ConfigPropertyCondition`不满足，`MyConfigConfiguration`配置类不被激活，`user3`和`user4` beans都不会被注册。

```java
public class ConfigPropertyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "true".equals(System.getProperty("enable.config"));
    }
}
```

定义两个简单的Java类：`User3`和`User4`。

```java
public class User3 {
    
}

public class User4 {
    
}
```

当`enable.config`为`true`运行结果发现，`MyConfigConfiguration`中的`user3`和`user4`都被注册了。

```java
beanDefinitionName = myConfigConfiguration
beanDefinitionName = user3
beanDefinitionName = user4
```

当`enable.config`为`false`运行结果发现，我们不会看到任何与 `MyConfigConfiguration` （包括它自己）相关的 beans 被注册了。

```java
无任何bean
```

#### 自定义组合注解

首先来看看启动类入口，首先设置一个系统属性`enable.custom`为`true`，然后上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，最后打印了Spring上下文中所有的bean定义名称。

```java
public class ConditionCustomApplication {

    public static void main(String[] args) {
        System.setProperty("enable.custom","true");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyCustomConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

`MyCustomConfiguration`，其中定义了两个bean：`user5`和`user6`。如果`@ConditionalOnCustomActive`的条件满足，`MyCustomConfiguration`配置类将被激活，在此配置类中定义`user5`和`user6`将被注册到Spring容器中。如果`@ConditionalOnCustomActive`的条件不满足，`MyCustomConfiguration`配置类将不被激活。`user5`和`user6` beans都不会被注册。

```java
@Configuration
@ConditionalOnCustomActive
public class MyCustomConfiguration {

    @Bean
    public User5 user5() {
        return new User5();
    }

    @Bean
    public User6 user6() {
        return new User6();
    }
}
```

`@ConditionalOnCustomActive`定义了一个组合注解，并通过`@Conditional`元注解将其关联到`CustomActiveCondition`。

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(CustomActiveCondition.class)
public @interface ConditionalOnCustomActive {

}
```

`CustomActiveCondition`。这个实现会根据`enable.custom`属性的值决定是否满足条件。具体来说：当`enable.custom`设置为`true`，`CustomActiveCondition`满足条件，因为`matches`方法会返回`true`，`MyCustomConfiguration`配置类由于带有`@ConditionalOnCustomActive`注解（该注解内部引用了`CustomActiveCondition`）将被激活，在该配置类中定义的`user5`和`user6`将被注册到Spring容器中。当`enable.custom`设置为`false`或未设置`CustomActiveCondition`不满足条件，因为`matches`方法会返回`false`，由于`MyCustomConfiguration`带有`@ConditionalOnCustomActive`注解，该配置类不被激活。`user5`和`user6` beans都不会被注册。

```java
public class CustomActiveCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "true".equals(System.getProperty("enable.custom"));
    }
}
```

定义两个简单的Java类：`User5`和`User6`。

```java
public class User5 {
    
}

public class User6 {
    
}
```

当`enable.custom`为`true`运行结果发现，我们的组合注解 `@ConditionalOnCustomActive` 和相应的条件 `CustomActiveCondition` 正常工作，正确地根据 `enable.custom` 系统属性的值来激活 `MyCustomConfiguration` 配置类。

```java
beanDefinitionName = myCustomConfiguration
beanDefinitionName = user5
beanDefinitionName = user6
```

当`enable.custom`为`false`运行结果发现，与 `MyCustomConfiguration` 相关的任何 beans（包括它自己）都不会被注册到 Spring 容器中。

```java
无任何bean
```

### 六、时序图

~~~mermaid
sequenceDiagram 
ConditionCustomApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>启动上下文
AnnotationConfigApplicationContext-->>ConfigurationApplication: 返回context<br>返回上下文实例
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: register(componentClasses)<br>注册组件类
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: register(componentClasses)<br>读取器注册类
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: registerBean(beanClass)<br>注册Bean类
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: doRegisterBean(beanClass,name,qualifiers, supplier,customizers)<br>执行Bean注册
AnnotatedBeanDefinitionReader->>ConditionEvaluator:shouldSkip(metadata)
ConditionEvaluator->>ConditionEvaluator:shouldSkip(metadata,phase)
ConditionEvaluator->>ConditionEvaluator:getConditionClasses(metadata)
Note right of ConditionEvaluator: 返回 List<String[]>
ConditionEvaluator->>ConditionEvaluator:getCondition(conditionClassName,classloader)
ConditionEvaluator->>AnnotationAwareOrderComparator:sort(conditions)
ConditionEvaluator->>CustomActiveCondition:matches(context,metadata)
CustomActiveCondition->>ConditionEvaluator:返回true or false
ConditionEvaluator->>AnnotatedBeanDefinitionReader:返回true or false
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:如果shouldSkip返回是true,跳过Bean的注册
~~~

### 七、源码分析

首先来看看启动类入口，首先设置一个系统属性`enable.custom`为`true`，然后上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，最后打印了Spring上下文中所有的bean定义名称。

```java
public class ConditionCustomApplication {

    public static void main(String[] args) {
        System.setProperty("enable.custom","true");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyCustomConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们重点关注`refresh()`方法。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#register`方法中，主要是允许我们注册一个或多个组件类（例如，那些使用 `@Component`, `@Service`, `@Repository`, `@Controller`, `@Configuration` 等注解的类）到Spring容器。

```java
@Override
public void register(Class<?>... componentClasses) {
    Assert.notEmpty(componentClasses, "At least one component class must be specified");
    StartupStep registerComponentClass = this.getApplicationStartup().start("spring.context.component-classes.register")
        .tag("classes", () -> Arrays.toString(componentClasses));
    this.reader.register(componentClasses);
    registerComponentClass.end();
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#register`方法中，遍历每一个传入的组件类，并逐一调用另一个方法来完成实际的注册工作。

```java
public void register(Class<?>... componentClasses) {
    for (Class<?> componentClass : componentClasses) {
        registerBean(componentClass);
    }
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#registerBean(beanClass)`方法中，主要目的是快速注册一个 bean 类型，而不需要指定其他详细的配置或参数。

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`方法中，主要用于条件性注册 bean 的逻辑，只有当特定的条件满足时，bean 才会被注册。

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {

    // 基于给定的 bean 类创建一个带注解的 bean 定义。
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);

    // 利用条件评估器检查是否应该跳过当前 bean 的注册。
    // 如果 bean 不满足指定的条件，那么将直接返回，不继续执行后续的注册逻辑。
    if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
        return;
    }

	// ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.ConditionEvaluator#shouldSkip(metadata)`方法中，又委托给另一个版本的 `shouldSkip` 方法，并为第二个参数传入 `null`。

```java
public boolean shouldSkip(AnnotatedTypeMetadata metadata) {
    return shouldSkip(metadata, null);
}
```

在`org.springframework.context.annotation.ConditionEvaluator#shouldSkip(metadata,phase)`方法中，主要目的是决定是否应根据给定的条件（通常由 `@Conditional` 注解定义）跳过某个配置类或 bean 的注册。

```java
/**
 * 根据提供的元数据和配置阶段判断是否应跳过某个操作或逻辑。
 *
 * @param metadata 元数据，与注解相关。
 * @param phase    当前的配置阶段，可能为 null。
 * @return 如果应跳过，则返回 true；否则返回 false。
 */
public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationPhase phase) {
    // 如果元数据为空或未标注 @Conditional 注解，则不跳过。
    if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
        return false;
    }

    // 如果没有指定配置阶段，确定正确的配置阶段。
    if (phase == null) {
        // 如果元数据是注解元数据，并且是配置候选项，则选择 PARSE_CONFIGURATION 阶段。
        if (metadata instanceof AnnotationMetadata &&
            ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
            return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
        }
        // 否则选择 REGISTER_BEAN 阶段。
        return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
    }

    // 获取所有的条件，并从相关的条件类实例化它们。
    List<Condition> conditions = new ArrayList<>();
    for (String[] conditionClasses : getConditionClasses(metadata)) {
        for (String conditionClass : conditionClasses) {
            Condition condition = getCondition(conditionClass, this.context.getClassLoader());
            conditions.add(condition);
        }
    }

    // 对条件进行排序。
    AnnotationAwareOrderComparator.sort(conditions);

    // 遍历所有条件，检查它们是否与当前配置阶段匹配。
    for (Condition condition : conditions) {
        ConfigurationPhase requiredPhase = null;
        if (condition instanceof ConfigurationCondition) {
            requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
        }
        // 如果条件不匹配上下文和元数据，则跳过。
        if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
            return true;
        }
    }

    return false;
}
```

在`org.springframework.context.annotation.ConditionEvaluator#getConditionClasses`方法中，从提供的注解元数据中获取与 `@Conditional` 注解关联的条件类的名称。

```java
/**
 * 从提供的注解元数据中获取与 @Conditional 注解关联的条件类的名称。
 *
 * @param metadata 元数据，通常与某个 bean 或配置类的注解相关。
 * @return 一个列表，其中包含与 @Conditional 注解关联的条件类的名称。如果没有相关的条件类，则返回一个空列表。
 */
private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
    // 获取 @Conditional 注解的所有属性值。
    MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
    
    // 试图从属性值中获取 "value"，它应该是一个指向条件类名称的引用。
    Object values = (attributes != null ? attributes.get("value") : null);

    // 返回条件类名称的列表，或者如果没有条件类，则返回一个空列表。
    return (List<String[]>) (values != null ? values : Collections.emptyList());
}
```

在`org.springframework.context.annotation.ConditionEvaluator#getCondition`方法中，根据给定的条件类名称和类加载器实例化一个 `Condition` 对象。

```java
/**
 * 根据提供的条件类名称和类加载器实例化一个 Condition 对象。
 *
 * @param conditionClassName 条件类的完全限定名。
 * @param classloader 用于加载条件类的类加载器，可以为 null。
 * @return 实例化的 Condition 对象。
 */
private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
    // 使用类加载器解析并加载指定的条件类。
    Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
    
    // 实例化解析的条件类并返回。
    return (Condition) BeanUtils.instantiateClass(conditionClass);
}
```

### 八、注意事项

1. **实现 `Condition` 接口**
   + 为了使用 `@Conditional`, 我们需要实现 `Condition` 接口。该接口只有一个方法，`matches(ConditionContext context, AnnotatedTypeMetadata metadata)`，我们需要在这里放置我们的条件逻辑。
2. **类级别和方法级别**：
   - `@Conditional` 可以应用于 `@Bean` 方法，用于控制特定 bean 的创建。
   - 也可以应用于 `@Configuration` 类，控制整个配置类的加载。
3. **组合多个条件**
   + 可以使用 `@Conditional` 注解的数组形式来组合多个条件，所有条件都必须满足才能创建 bean。
4. **与其他注解的组合**
   + `@Conditional` 可以与其他注解一起使用，如 `@Profile`。但是注意他们之间的交互效果。例如，如果一个 bean 标记为特定的 `@Profile` 并且使用 `@Conditional`，那么两者都必须为 true 才能创建该 bean。
5. **避免复杂的逻辑**
   + 虽然 `Condition` 允许我们编写任意的条件逻辑，但最好避免过于复杂。简单明了的逻辑更易于理解和维护。
6. **性能**
   + `matches` 方法可能会在应用的生命周期中被多次调用，因此应确保其执行效率，避免在此方法中进行高开销的操作。
7. **配合 `ConditionContext`**:
   + `ConditionContext` 提供了关于当前应用上下文、环境属性、系统属性等的信息，可以使我们的条件判断更加具体和强大。
8. **自定义条件注解**
   + 为了重用或组合多个条件，我们可以创建自己的条件注解。例如，我们可以创建一个 `@ConditionalOnCustomActive` 注解，它封装了检查`enable.custom`的条件。
9. **注意与 `@Profile` 的区别**
   + 虽然 `@Conditional` 和 `@Profile` 在某些情况下可以达到相同的效果，但它们的目的不同。`@Profile` 基于环境，而 `@Conditional` 更加通用，允许我们基于任意条件创建 bean。

### 九、总结

#### 最佳实践总结

1. **基于`@Bean`的条件配置**

   - **场景描述**
  - 在单个bean的创建上应用条件。
   - **实现方法**
  - 通过在`@Bean`注解方法上直接使用`@Conditional`。
   - **结果**：
     - 当条件满足（如`enable.bean`为`true`），特定的bean（如`user1`）会被注册。
     - 当条件不满足，该bean不会被注册。

2. **基于`@Configuration`的条件配置**

   - **场景描述**
  - 控制整个配置类的激活状态，从而影响该配置中定义的所有beans。
   - **实现方法**
  - 在`@Configuration`注解的类上直接使用`@Conditional`。
   - **结果**：
     - 当条件满足（如`enable.config`为`true`），配置类被激活，其内部的所有beans（如`user3`和`user4`）都会被注册。
     - 当条件不满足，配置类及其内部定义的所有beans都不会被注册。

3. **使用自定义组合注解**

   - **场景描述**
     - 创建自己的条件注解，以提供更清晰、更简洁的语法，或为特定的业务逻辑封装条件逻辑。
   - **实现方法**
     - 定义一个新的注解（如`@ConditionalOnCustomActive`），并使用`@Conditional`元注解将其关联到特定的条件类。
   - **结果**：
     - 当条件满足（如`enable.custom`为`true`），带有`@ConditionalOnCustomActive`注解的配置类或bean会被注册。
     - 当条件不满足，它们不会被注册。

#### 源码分析总结

1. **初始化与启动**
   - 通过 `AnnotationConfigApplicationContext` 构造函数初始化 Spring 上下文，并通过 `register` 和 `refresh` 方法完成 bean 的注册和容器的刷新。

2. **注册组件类**
   + 使用 `AnnotatedBeanDefinitionReader` 来注册组件类。在 `register` 方法中，每一个组件类都会通过 `registerBean` 方法进行注册。

3. **条件检查**
   + 在 `doRegisterBean` 方法中，执行了核心的条件检查逻辑。使用 `ConditionEvaluator` 来评估与给定 bean 或配置类关联的条件是否满足。

4. **元数据检查**
   + `ConditionEvaluator` 会首先检查提供的元数据（通常与特定的 bean 或配置类的注解关联）是否包含 `@Conditional` 注解。如果没有，直接进行下一步。如果存在，继续检查条件是否满足。

5. **条件匹配**
   + `ConditionEvaluator` 获取与 `@Conditional` 注解关联的所有条件类，并为每一个条件类创建一个实例。随后，它会遍历所有的条件，并检查它们是否满足。这是通过调用每一个条件的 `matches` 方法来完成的。如果任何一个条件不满足，整个条件检查逻辑返回 `false`，表示不应注册与 `@Conditional` 注解关联的 bean 或配置类。

6. **条件实例化**
   + 通过 `getCondition` 方法，`ConditionEvaluator` 能够根据提供的条件类名称和类加载器实例化一个 `Condition` 对象。