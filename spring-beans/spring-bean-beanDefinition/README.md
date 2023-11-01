## BeanDefinition

- [BeanDefinition](#beandefinition)
  - [一、知识储备](#一知识储备)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、接口源码](#四接口源码)
  - [五、主要实现](#五主要实现)
  - [六、最佳实践](#六最佳实践)
  - [七、与其他组件的关系](#七与其他组件的关系)
  - [八、常见问题](#八常见问题)


### 一、知识储备

1. **`MetadataReader`**
   + `MetadataReader` 是 Spring 提供的一个接口，用于读取类的元数据信息。它可以用于扫描类文件，获取类的基本信息，如类名、类的注解等。在注解驱动的开发中，`MetadataReader` 通常用于扫描包中的类，并从这些类中提取注解信息，以便配置 Spring Bean。[点击查看](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-metadataReader)
2. **`AnnotationMetadata.introspect`**
   + `AnnotationMetadata` 接口中的 `introspect` 方法用于深入分析类的注解信息。它可以帮助我们获取类上的注解、类的方法上的注解、类的字段上的注解等。在 Spring 中，`introspect` 方法通常用于解析被 `@Component`, `@Configuration` 和其他注解标记的类，以确定它们如何被实例化并配置为 Spring Bean。[点击查看](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-annotationMetadata)

### 二、基本描述

`BeanDefinition` 是 Spring 框架中的关键构建块，它是一种配置元数据，用于详细描述和定义应用程序中的 Bean 对象，包括 Bean 的类名、作用域、依赖关系、构造函数参数、属性值、初始化方法、销毁方法等信息，从而允许 Spring 容器准确地实例化、配置和管理这些 Bean。通过`BeanDefinition`，我们可以灵活地配置应用程序中的组件，使其能够实现依赖注入、AOP 切面、作用域控制等核心功能，促进松耦合、可维护和可扩展的应用程序开发。

### 三、主要功能

1. **定义 Bean 的类**
   + 用于指定要实例化的 Bean 的类名。它告诉 Spring 容器要创建哪个 Java 类的对象。
2. **定义 Bean 的作用域**
   + 允许我们指定 Bean 的作用域，例如 singleton（单例）或 prototype（多例）。这影响了 Bean 在容器中的生命周期。
3. **构造函数参数和属性值**
   + 允许我们指定 Bean 的构造函数参数和属性值，以便在实例化 Bean 时传递参数或设置属性。
4. **定义初始化和销毁方法**
   + 定义 Bean 的初始化方法和销毁方法，以确保在 Bean 创建和销毁时执行特定的逻辑。
5. **Bean 的延迟初始化**
   + 允许我们设置 Bean 是否延迟初始化，即在第一次请求时创建 Bean 实例。
6. **依赖关系**
   + 允许我们指定 Bean 之间的依赖关系，以确保在创建 Bean 时正确注入依赖的其他 Bean。
7. **描述 Bean 的角色**
   + 允许我们为 Bean 指定一个角色（role），通常包括应用程序 Bean、基础设施 Bean、测试 Bean 等。
8. **Bean 的属性覆盖**
   + 允许我们使用属性覆盖机制，通过不同的配置源（如属性文件或环境变量）覆盖已定义的属性值。
9. **Bean 的注解和元数据**
   + 可以包含关于 Bean 的注解信息和元数据，这对于处理注解驱动的开发非常有用。
10. **动态创建和注册 Bean**
    + 允许我们在运行时动态创建和注册 Bean，而不仅仅是静态配置。

### 四、接口源码

从`BeanDefinition` 接口源码来看，它描述和配置 Spring Bean 的各个方面。它包括了配置 Bean 的类名、作用域、初始化和销毁方法、构造函数参数、属性值等。

```java
/**
 * BeanDefinition 描述一个 Bean 实例，包括属性值、构造函数参数值以及具体实现提供的更多信息。
 *
 * 这只是一个最小的接口：主要意图是允许 BeanFactoryPostProcessor 检查和修改属性值以及其他 Bean 元数据。
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2004-03-19
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

    /**
     * 标准单例范围的范围标识符：value。
     * 请注意，扩展的 Bean 工厂可能支持更多范围。
     * @see #setScope
     * @see ConfigurableBeanFactory#SCOPE_SINGLETON
     */
    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    /**
     * 标准原型范围的范围标识符：value。
     * 请注意，扩展的 Bean 工厂可能支持更多范围。
     * @see #setScope
     * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
     */
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    /**
     * 表示 BeanDefinition 是应用程序的主要部分的角色提示。
     * 通常对应于用户定义的 Bean。
     */
    int ROLE_APPLICATION = 0;

    /**
     * 表示 BeanDefinition 是某个更大配置的支持部分的角色提示，通常是外部
     * org.springframework.beans.factory.parsing.ComponentDefinition。
     * 当在特定的 org.springframework.beans.factory.parsing.ComponentDefinition 上查看时，这些"SUPPORT" Bean 被认为足够重要，
     * 但在查看应用程序的整体配置时，它们无关紧要。
     */
    int ROLE_SUPPORT = 1;

    /**
     * 表示 BeanDefinition 提供了完全背景角色，与最终用户无关。
     * 当注册完全属于 org.springframework.beans.factory.parsing.ComponentDefinition 内部工作的 Bean 时使用此提示。
     */
    int ROLE_INFRASTRUCTURE = 2;

    /**
     * 设置此 Bean 定义的父定义的名称，如果有的话。
     */
    void setParentName(@Nullable String parentName);

    /**
     * 返回此 Bean 定义的父定义的名称，如果有的话。
     */
    @Nullable
    String getParentName();

    /**
     * 指定此 Bean 定义的 Bean 类名称。
     * 在 Bean 工厂后处理期间，可以修改类名，通常用解析后的类名替换原始类名。
     */
    void setBeanClassName(@Nullable String beanClassName);

    /**
     * 返回此 Bean 定义的当前 Bean 类名。
     * 请注意，这不一定是运行时实际使用的类名，在子定义从父定义继承类名的情况下。
     * 此外，在工厂方法上调用的类可能为空。
     * 因此，不要将其视为运行时的确定 Bean 类型，而只在个别 Bean 定义级别用于解析目的。
     */
    @Nullable
    String getBeanClassName();

    /**
     * 覆盖此 Bean 的目标范围，指定新的范围名称。
     * @see #SCOPE_SINGLETON
     * @see #SCOPE_PROTOTYPE
     */
    void setScope(@Nullable String scope);

    /**
     * 返回此 Bean 的当前目标范围名称，如果尚未知，则返回 null。
     */
    @Nullable
    String getScope();

    /**
     * 设置此 Bean 是否应懒初始化。
     * 如果为 false，则 Bean 将由执行单例的 Bean 工厂在启动时实例化。
     */
    void setLazyInit(boolean lazyInit);

    /**
     * 返回此 Bean 是否应懒初始化，即不在启动时急切实例化。仅适用于单例 Bean。
     */
    boolean isLazyInit();

    /**
     * 设置此 Bean 依赖于初始化的 Bean 的名称。
     * Bean 工厂将保证这些 Bean 首先得到初始化。
     */
    void setDependsOn(@Nullable String... dependsOn);

    /**
     * 返回此 Bean 依赖的 Bean 名称。
     */
    @Nullable
    String[] getDependsOn();

    /**
     * 设置此 Bean 是否是自动装配候选 Bean。
     * 请注意，此标志仅用于影响基于类型的自动装配。
     * 它不影响名称上的显式引用，如果指定的 Bean 未标记为自动装配候选 Bean，则名称匹配仍然会注入 Bean。
     */
    void setAutowireCandidate(boolean autowireCandidate);

    /**
     * 返回此 Bean 是否是自动装配候选 Bean。
     */
    boolean isAutowireCandidate();

    /**
     * 设置此 Bean 是否是主要自动装配候选 Bean。
     * 如果多个匹配的候选 Bean 中有一个 Bean 的此值为 true，则它将作为补充选项。
     */
    void setPrimary(boolean primary);

    /**
     * 返回此 Bean 是否是主要自动装配候选 Bean。
     */
    boolean isPrimary();

    /**
     * 指定要使用的工厂 Bean，如果有的话。
     * 这是要调用指定工厂方法的 Bean 的名称。
     * @see #setFactoryMethodName
     */
    void setFactoryBeanName(@Nullable String factoryBeanName);

    /**
     * 返回工厂 Bean 名称，如果有的话。
     */
    @Nullable
    String getFactoryBeanName();

    /**
     * 指定工厂方法，如果有的话。此方法将使用构造函数参数调用，
     * 或者如果未指定参数，则使用没有参数调用。
     * 该方法将在指定工厂 Bean 上调用，如果没有指定工厂 Bean，则在本地 Bean 类上调用。
     * @see #setFactoryBeanName
     * @see #setBeanClassName
     */
    void setFactoryMethodName(@Nullable String factoryMethodName);

    /**
     * 返回工厂方法，如果有的话。
     */
    @Nullable
    String getFactoryMethodName();

    /**
     * 返回此 Bean 的构造函数参数值。
     * 返回的实例可以在 Bean 工厂后处理期间修改。
     * @return 构造函数参数值对象（永不为 null）
     */
    ConstructorArgumentValues getConstructorArgumentValues();

    /**
     * 返回是否为此 Bean 定义定义了构造函数参数值。
     * @since 5.0.2
     */
    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }

    /**
     * 返回要应用于 Bean 新实例的属性值。
     * 返回的实例可以在 Bean 工厂后处理期间修改。
     * @return 可变属性值对象（永不为 null）
     */
    MutablePropertyValues getPropertyValues();

    /**
     * 返回是否为此 Bean 定义定义了属性值。
     * @since 5.0.2
     */
    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }

    /**
     * 设置初始化方法的名称。
     * @since 5.1
     */
    void setInitMethodName(@Nullable String initMethodName);

    /**
     * 返回初始化方法的名称。
     * @since 5.1
     */
    @Nullable
    String getInitMethodName();

    /**
     * 设置销毁方法的名称。
     * @since 5.1
     */
    void setDestroyMethodName(@Nullable String destroyMethodName);

    /**
     * 返回销毁方法的名称。
     * @since 5.1
     */
    @Nullable
    String getDestroyMethodName();

    /**
     * 设置 BeanDefinition 的角色提示。角色提示提供框架和工具一个有关特定 BeanDefinition 的角色和重要性的指示。
     * @since 5.1
     * @see #ROLE_APPLICATION
     * @see #ROLE_SUPPORT
     * @see #ROLE_INFRASTRUCTURE
     */
    void setRole(int role);

    /**
     * 获取 BeanDefinition 的角色提示。角色提示提供框架和工具一个有关特定 BeanDefinition 的角色和重要性的指示。
     * @see #ROLE_APPLICATION
     * @see #ROLE_SUPPORT
     * @see #ROLE_INFRASTRUCTURE
     */
    int getRole();

    /**
     * 设置 BeanDefinition 的人类可读描述。
     * @since 5.1
     */
    void setDescription(@Nullable String description);

    /**
     * 返回 BeanDefinition 的人类可读描述。
     */
    @Nullable
    String getDescription();

    /**
     * 基于 Bean 类或其他特定元数据返回可解析的类型的类型。
     * 这通常在运行时合并的 Bean 定义上完全解析，但不一定在配置时定义实例上解析。
     * @return 可解析类型（可能为 ResolvableType#NONE）
     * @since 5.2
     * @see ConfigurableBeanFactory#getMergedBeanDefinition
     */
    ResolvableType getResolvableType();

    /**
     * 返回是否为Singleton，在所有调用上返回单个共享实例。
     * @see #SCOPE_SINGLETON
     */
    boolean isSingleton();

    /**
     * 返回是否为Prototype，每次调用都返回独立的实例。
     * @since 3.0
     * @see #SCOPE_PROTOTYPE
     */
    boolean isPrototype();

    /**
     * 返回此 Bean 是否是"抽象"的，即不应该被实例化。
     */
    boolean isAbstract();

    /**
 	 * 返回此 Bean 定义所来自的资源的描述（以便在出现错误时显示上下文）。
	 */
    @Nullable
    String getResourceDescription();

    /**
     * 返回原始的 Bean 定义，如果没有则返回 {@code null}。
     * 允许检索已装饰的 Bean 定义（如果有）。
     * 请注意，此方法返回最直接的起源。遍历起源链以找到用户定义的原始 Bean 定义。
     */
    @Nullable
    BeanDefinition getOriginatingBeanDefinition();
    
}
```

### 五、主要实现

1. **`GenericBeanDefinition`**
   - 描述通用的 Bean 定义，可以用于大多数类型的 Bean。
   - 具有灵活的属性配置，可以设置类名、作用域、初始化和销毁方法、构造函数参数、属性值等。
   - 通常用于手动配置 Bean 或需要自定义 Bean 定义的情况。
2. **`RootBeanDefinition`**
   - 用于表示独立的根级 Bean 定义，通常直接定义 Bean。
   - 可以继承 `GenericBeanDefinition`，并支持配置 Bean 类的其他信息。
   - 通常用于定义应用程序中的独立 Bean。
3. **`ChildBeanDefinition`**
   - 用于表示派生的子级 Bean 定义，继承父级 Bean 定义的配置。
   - 通常用于创建一个 Bean 的变体，继承父 Bean 的配置并进行部分覆盖或修改。
   - 具有指向父 Bean 的引用。
4. **`AnnotatedGenericBeanDefinition`**
   - 用于基于注解的 Bean 定义，通常用于扫描组件和配置类。
   - 可以表示使用注解定义的 Bean，支持类级别的注解配置。
   - 通常用于自动扫描和注册组件。
5. **`ConfigurationClassBeanDefinition`**
   - 用于表示配置类（`@Configuration` 注解）的 Bean 定义，通常用于 Spring 配置。
   - 表示配置类作为 Bean 定义，支持包含其他 Bean 定义的配置类。
   - 通常由 Spring 容器自动创建，以支持 `@Configuration` 注解的配置。
6. **`ScannedGenericBeanDefinition`**
   - 用于表示扫描到的 Bean 的 Bean 定义，通常用于自动扫描组件。
   - 通常在组件扫描过程中创建，表示被发现的组件类。
   - 通常用于自动注册组件，如 `@Component` 注解的类。

~~~mermaid
classDiagram
    direction BT
    
    class BeanDefinition {
    	<<interface>>
    }
    
    class AnnotatedBeanDefinition {
    	<<interface>>
    }
    
    class AbstractBeanDefinition {
    	<<Abstract>>
    }

    class GenericBeanDefinition {
    }
    
    class RootBeanDefinition {
    }
    
    class ChildBeanDefinition {
    }
    
    class AnnotatedGenericBeanDefinition {
    }
    
    class ScannedGenericBeanDefinition {
    }
    
    class ConfigurationClassBeanDefinition {
    }
    
    
    
    AnnotatedBeanDefinition ..|> BeanDefinition
    AbstractBeanDefinition --|> BeanDefinition
    GenericBeanDefinition ..|> AbstractBeanDefinition
    
    RootBeanDefinition ..|> AbstractBeanDefinition
    ChildBeanDefinition ..|> AbstractBeanDefinition
	
	AnnotatedGenericBeanDefinition ..|> GenericBeanDefinition
	ScannedGenericBeanDefinition ..|> GenericBeanDefinition
	ConfigurationClassBeanDefinition ..|> RootBeanDefinition
~~~



### 六、最佳实践

创建了一个Spring容器（`DefaultListableBeanFactory`），然后通过`createBeanDefinition()`方法创建并配置了一个自定义的Bean定义（`ScannedGenericBeanDefinition`），包括作用域、初始化方法、销毁方法、属性值等。接着，它注册这个Bean定义到容器中，并使用容器获取Bean实例，打印出Bean的内容。最后，它通过容器销毁这个Bean，调用其销毁方法。

```java
public class BeanDefinitionDemo {
    public static void main(String[] args) throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("myBean", createBeanDefinition());

        // 获取MyBean
        MyBean myChildBean = beanFactory.getBean("myBean", MyBean.class);
        // 打印Bean对象
        System.out.println("MyBean = " + myChildBean);
        // 销毁myBean
        beanFactory.destroySingleton("myBean");
    }

    private static BeanDefinition createBeanDefinition() throws IOException {
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(MyBean.class.getName());

        ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
        beanDefinition.setScope("singleton");
        beanDefinition.setLazyInit(true);
        beanDefinition.setPrimary(true);
        beanDefinition.setAbstract(false);
        beanDefinition.setInitMethodName("init");
        beanDefinition.setDestroyMethodName("destroy");
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
        beanDefinition.setDescription("This is a custom bean definition");
        beanDefinition.setResourceDescription("com.xcs.spring.BeanDefinitionDemo");
        beanDefinition.getPropertyValues().add("name", "lex");
        beanDefinition.getPropertyValues().add("age", "18");
        return beanDefinition;
    }
}
```

`MyBean` 的Java类，代表了一个简单的Java Bean。

```java
public class MyBean {

    private String name;

    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void init(){
        System.out.println("execute com.xcs.spring.bean.MyBean.init");
    }

    public void destroy(){
        System.out.println("execute com.xcs.spring.bean.MyBean.destroy");
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
```

运行结果发现，与`BeanDefinition`配置相关，初始化方法和销毁方法的调用以及属性值的设置受`BeanDefinition`中相应的配置项影响，`BeanDefinition`用于定义和配置Bean的元信息，使Spring容器可以正确管理Bean的生命周期和属性。

```java
execute com.xcs.spring.bean.MyBean.init
MyBean = MyBean{name='lex', age='18'}
execute com.xcs.spring.bean.MyBean.destroy
```

### 七、与其他组件的关系

1. **`DefaultListableBeanFactory`**
   + 负责管理Bean的创建、初始化和销毁，而`BeanDefinition`提供了描述Bean的元信息的方式，`DefaultListableBeanFactory`使用`BeanDefinition`来创建和管理Bean实例。
2. **`BeanPostProcessor`**
   + 拦截Bean初始化过程的接口，它可以在Bean创建后、初始化前后对Bean进行处理。`BeanDefinition`的信息可以在`BeanPostProcessor`中使用，例如在初始化前修改Bean的属性值。
3. **`BeanDefinitionRegistry`**
   + 注册和管理`BeanDefinition`的接口，定义了`BeanDefinition`的注册和访问方法。`BeanFactory`和`ApplicationContext`实现了`BeanDefinitionRegistry`接口，通过它们可以注册和获取`BeanDefinition`。
4. **`BeanDefinitionReader`**
   + 从外部配置文件（如`XML、YAML、Properties`文件）中读取`BeanDefinition`的工具。它将外部配置信息解析成`BeanDefinition`并注册到`BeanFactory`中。

### 八、常见问题

1. **`BeanDefinition`的作用是什么？**
   + 主要作用是定义和配置Bean的属性和行为，以便Spring容器可以根据这些信息动态地创建、初始化和管理Bean实例。
2. **`BeanDefinition`的生命周期是怎样的？**
   + 生命周期与Spring容器相同，它在容器启动时进行注册和解析，然后被用于创建和管理Bean实例的生命周期。
3. **`BeanDefinition`如何注册？**
   + 通过`BeanDefinitionRegistry`接口的实现类（如`DefaultListableBeanFactory`）进行注册，或者通过`BeanDefinitionReader`从外部配置文件中加载并注册。
4. **`BeanDefinition`的属性有哪些？**
   + 属性包括Bean的类名、作用域、初始化方法、销毁方法、属性值等。具体属性取决于Bean的配置需求。
5. **`BeanDefinition`如何修改？**
   + 可以在注册后通过编程方式进行修改，例如更改属性值、作用域、初始化方法、销毁方法等。
6. **`BeanDefinition`的作用域有哪些？**
   + 包括Singleton（单例）、Prototype（原型）、Request（请求）、Session（会话）、等等，可以根据需求选择合适的作用域。
7. **`BeanDefinition`的注册和加载有什么区别？**
   + 注册是将已创建的`BeanDefinition`添加到容器中，而加载是从外部配置文件中读取Bean的元信息并注册到容器中。
8. **如何使用`BeanDefinition`来实现依赖注入？**
   + 通过设置`BeanDefinition`中的属性，如构造函数参数、属性值、引用其他Bean的方式，可以实现依赖注入。
9. **`BeanDefinition`是否可以动态生成？**
   + 可以在运行时动态生成并注册到容器中，这在某些复杂的情况下非常有用，例如基于条件的Bean注册。