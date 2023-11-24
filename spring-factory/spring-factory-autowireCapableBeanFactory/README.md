## AutowireCapableBeanFactory

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 三、基本描述

`AutowireCapableBeanFactory`接口是Spring框架中位于`org.springframework.beans.factory.config`包下的关键接口，扩展自`BeanFactory`，主要提供了在运行时进行Bean自动装配和创建的高级功能。其核心方法`createBean`允许动态创建Bean实例，并进行自动装配，解决了Bean之间的依赖关系，而其他方法如`autowireBean`和`applyBeanPostProcessorsBeforeInitialization`则提供了更细粒度的控制和定制点，使我们能够在Bean生命周期的不同阶段进行干预，实现更灵活的Bean管理和配置。这一接口的存在增强了Spring IoC容器的功能，使其能够更好地适应复杂系统的需求。

### 四、主要功能

1. **Bean的创建和初始化：** 
   + 通过`createBean`方法，可以创建一个新的Bean实例，并在创建过程中执行完整的初始化，包括所有适用的`BeanPostProcessor`的回调。
2. **自动装配：** 
   + 提供了不同的自动装配模式，包括按名称、按类型、按构造函数等，通过`autowire`和`autowireBeanProperties`方法实现对Bean属性的自动注入。
3. **Bean配置和后处理器应用：** 
   + 通过`configureBean`方法，可以配置已存在的Bean实例，应用属性值、工厂回调等，同时执行所有`BeanPostProcessor`的回调。
4. **定制化初始化和销毁过程：** 
   + 通过`initializeBean`方法，可以在Bean初始化过程中应用定制化的操作，例如执行初始化回调、应用后处理器等。还提供了`destroyBean`方法用于销毁Bean实例。
5. **解析依赖：** 
   + 通过`resolveDependency`方法，可以解析指定的依赖关系，支持字段、方法、构造函数等各种依赖注入方式。
6. **Bean实例的生命周期管理：** 
   + 提供了应用`BeanPostProcessor`的回调，允许在Bean的初始化前后应用定制的处理逻辑，以及执行销毁前的操作。
7. **解析Bean：** 
   + 提供了解析指定类型和名称的Bean实例的方法，包括通过`resolveNamedBean`解析唯一匹配的Bean实例。
8. **依赖检查：**
   + 提供`dependencyCheck`方法，用于检查Bean的依赖关系是否满足要求。
9. **Bean的销毁回调：**
   + 通过`destroyBean`方法，允许在销毁Bean实例时执行自定义的清理和回收操作。
10. **提供Bean的属性赋值：**
    + 通过`applyPropertyValues`方法，支持对Bean属性进行手动赋值，实现在运行时动态修改Bean的属性。
11. **提供Bean的类型转换：** 
    + 通过`getTypeConverter`方法，支持在运行时进行类型转换，确保属性值正确地转换为目标类型。
12. **Bean实例的后处理：**
    + 通过`postProcessBeanInstance`方法，允许在创建Bean实例后进行自定义的处理，如更改Bean的内部状态或执行其他定制逻辑。

### 五、接口源码

从`AutowireCapableBeanFactory`接口源码中看出，它承担了创建、配置和生命周期管理Bean实例的任务。通过定义常量和方法，它提供了细粒度的控制，包括特定的自动装配策略、初始化过程、属性注入、后处理器应用以及销毁阶段。

```java
/**
 * org.springframework.beans.factory.BeanFactory的扩展接口，由能够进行自动装配的Bean工厂实现，
 * 前提是它们希望为现有的Bean实例暴露此功能。
 *
 * 此子接口不应在正常应用代码中使用：请使用org.springframework.beans.factory.BeanFactory
 * 或org.springframework.beans.factory.ListableBeanFactory以供典型用例使用。
 *
 * 其他框架的集成代码可以利用此接口来连接和填充Spring不控制生命周期的现有Bean实例。
 * 这对于WebWork Actions和Tapestry Page对象等情况特别有用。
 *
 * 请注意，此接口不由org.springframework.context.ApplicationContext门面实现，
 * 因为它在应用代码中几乎不被使用。尽管如此，它仍可从应用程序上下文中访问，通过ApplicationContext的
 * org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()方法获得。
 *
 * 您还可以实现org.springframework.beans.factory.BeanFactoryAware接口，
 * 它在ApplicationContext中运行时公开内部BeanFactory，以便访问AutowireCapableBeanFactory：
 * 只需将传入的BeanFactory强制转换为AutowireCapableBeanFactory。
 *
 * @author Juergen Hoeller
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * 常量，表示没有外部定义的自动装配。请注意，仍将应用BeanFactoryAware等，并且将应用基于注释的注入。
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_NO = 0;

    /**
     * 常量，表示按名称自动装配Bean属性（适用于所有Bean属性设置器）。
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_BY_NAME = 1;

    /**
     * 常量，表示按类型自动装配Bean属性（适用于所有Bean属性设置器）。
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_BY_TYPE = 2;

    /**
     * 常量，表示自动装配可以满足的最贪婪的构造函数（涉及解析适当的构造函数）。
     * @see #createBean
     * @see #autowire
     */
    int AUTOWIRE_CONSTRUCTOR = 3;

    /**
     * 常量，表示通过对Bean类进行内省来确定适当的自动装配策略。
     * @see #createBean
     * @see #autowire
     * @deprecated 自Spring 3.0起：如果使用了混合自动装配策略，请优先使用基于注释的自动装配，以清晰标记自动装配需求。
     */
    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;

    /**
     * 初始化现有Bean实例时使用的后缀，以实现“原始实例”约定：附加到完全限定的Bean类名称，
     * 例如“com.mypackage.MyClass.ORIGINAL”，以强制返回给定实例，即没有代理等。
     * @since 5.1
     * @see #initializeBean(Object, String)
     * @see #applyBeanPostProcessorsBeforeInitialization(Object, String)
     * @see #applyBeanPostProcessorsAfterInitialization(Object, String)
     */
    String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

    /**
     * 完全创建给定类的新Bean实例。
     * 执行Bean的完全初始化，包括所有适用的BeanPostProcessor BeanPostProcessors。
     * 注意：这用于创建一个新实例，填充带注释的字段和方法以及应用所有标准的Bean初始化回调。
     * 它不意味着传统的按名称或按类型自动装配属性；对于这些目的，请使用#createBean(Class, int, boolean)。
     * @param beanClass 要创建的Bean的类
     * @return 新的Bean实例
     * @throws BeansException 如果实例化或装配失败
     */
    <T> T createBean(Class<T> beanClass) throws BeansException;

    /**
     * 通过应用实例化后回调和Bean属性后处理（例如用于注释驱动的注入）来填充给定的Bean实例。
     * 注意：这主要用于（重新）填充带注释的字段和方法，无论是对于新实例还是对于反序列化的实例。
     * 它不意味着传统的按名称或按类型自动装配属性；对于这些目的，请使用#autowireBeanProperties。
     * @param existingBean 现有的Bean实例
     * @throws BeansException 如果装配失败
     */
    void autowireBean(Object existingBean) throws BeansException;

    /**
     * 配置给定的原始Bean：自动装配Bean属性，应用Bean属性值，
     * 应用工厂回调，如{@code setBeanName和{@code setBeanFactory，
     * 以及应用所有Bean后处理器（包括可能包装给定原始Bean的后处理器）。
     * 这实际上是#initializeBean提供的超集，完全应用相应Bean定义指定的配置。
     * 注意：此方法需要给定名称的Bean定义！
     * @param existingBean 现有的Bean实例
     * @param beanName Bean的名称，如果需要，将传递给它
     *                 （必须存在该名称的Bean定义）
     * @return 用于使用的Bean实例，原始或包装的其中之一
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     * 如果没有给定名称的Bean定义
     * @throws BeansException 如果初始化失败
     * @see #initializeBean
     */
    Object configureBean(Object existingBean, String beanName) throws BeansException;

    /**
     * 以指定的自动装配策略完全创建给定类的新Bean实例。
     * 此接口支持此处定义的所有常量。
     * 执行Bean的完全初始化，包括所有适用的BeanPostProcessor BeanPostProcessors。
     * 这实际上是#autowire提供的超集，添加了#initializeBean的行为。
     * @param beanClass 要创建的Bean的类
     * @param autowireMode 按名称或类型，使用此接口中的常量
     * @param dependencyCheck 是否对对象执行依赖关系检查
     *                        （不适用于构造函数的自动装配，因此在这里被忽略）
     * @return 新的Bean实例
     * @throws BeansException 如果实例化或装配失败
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     */
    Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * 使用指定的自动装配策略实例化给定类的新Bean实例。
     * 此处支持此接口中定义的所有常量。
     * 也可以使用{@code AUTOWIRE_NO调用，以便仅应用实例化前回调（例如用于注释驱动的注入）。
     * 不会应用标准的BeanPostProcessor BeanPostProcessors回调或对Bean的进一步初始化。
     * 此接口为这些目的提供了不同的、细粒度的操作，例如#initializeBean。
     * 然而，如果适用于实例的构建，将应用InstantiationAwareBeanPostProcessor回调。
     * @param beanClass 要实例化的Bean的类
     * @param autowireMode 按名称或类型，使用此接口中的常量
     * @param dependencyCheck 是否对Bean实例中的对象引用执行依赖关系检查
     *                        （不适用于构造函数的自动装配，因此在这里被忽略）
     * @return 新的Bean实例
     * @throws BeansException 如果实例化或装配失败
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     * @see #AUTOWIRE_AUTODETECT
     * @see #initializeBean
     * @see #applyBeanPostProcessorsBeforeInitialization
     * @see #applyBeanPostProcessorsAfterInitialization
     */
    Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * 按名称或类型自动装配给定Bean实例的Bean属性。
     * 也可以使用{@code AUTOWIRE_NO调用，以便仅应用实例化后回调（例如用于注释驱动的注入）。
     * 不会应用标准的BeanPostProcessor BeanPostProcessors回调或对Bean的进一步初始化。
     * 此接口为这些目的提供了不同的、细粒度的操作，例如#initializeBean。
     * 然而，如果适用于实例的配置，将应用InstantiationAwareBeanPostProcessor回调。
     * @param existingBean 现有的Bean实例
     * @param autowireMode 按名称或类型，使用此接口中的常量
     * @param dependencyCheck 是否对Bean实例中的对象引用执行依赖关系检查
     * @throws BeansException 如果装配失败
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_NO
     */
    void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
            throws BeansException;


    /**
     * 将给定bean定义名称的bean定义的属性值应用于给定的bean实例。
     * bean定义可以定义一个完全独立的bean，重用其属性值，或仅用于现有bean实例的属性值。
     * 此方法不会自动装配bean属性；它只应用显式定义的属性值。
     * 使用#autowireBeanProperties方法来自动装配现有的bean实例。
     * 注意：此方法需要给定名称的bean定义！
     * 不会应用标准的BeanPostProcessor BeanPostProcessors回调或对bean的进一步初始化。
     * 此接口为这些目的提供了不同的、细粒度的操作，例如#initializeBean。
     * 但是，如果适用于实例的配置，将应用InstantiationAwareBeanPostProcessor回调。
     * @param existingBean 现有的bean实例
     * @param beanName bean工厂中bean定义的名称
     *                 （必须存在该名称的bean定义）
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     * 如果没有给定名称的bean定义
     * @throws BeansException 如果应用属性值失败
     * @see #autowireBeanProperties
     */
    void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

    /**
     * 初始化给定的原始bean，应用工厂回调，例如{@code setBeanName和{@code setBeanFactory，
     * 也应用所有bean后处理器（包括可能包装给定原始bean的后处理器）。
     * 请注意，给定名称的bean工厂不必存在bean定义。
     * 传入的bean名称将仅用于回调，但不会与已注册的bean定义进行检查。
     * @param existingBean 现有的bean实例
     * @param beanName bean的名称，如果需要，将传递给它
     *                 （仅传递给BeanPostProcessor BeanPostProcessors；
     *                  可以遵循#ORIGINAL_INSTANCE_SUFFIX约定，以强制返回给定的实例，
     *                  即没有代理等）
     * @return 要使用的bean实例，原始的或包装的其中之一
     * @throws BeansException 如果初始化失败
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object initializeBean(Object existingBean, String beanName) throws BeansException;

    /**
     * 将BeanPostProcessor BeanPostProcessors应用于给定的现有bean实例，
     * 调用其{@code postProcessBeforeInitialization方法。返回的bean实例可能是原始bean的包装。
     * @param existingBean 现有的bean实例
     * @param beanName bean的名称，如果需要，将传递给它
     *                 （仅传递给BeanPostProcessor BeanPostProcessors；
     *                  可以遵循#ORIGINAL_INSTANCE_SUFFIX约定，以强制返回给定的实例，
     *                  即没有代理等）
     * @return 要使用的bean实例，原始的或包装的其中之一
     * @throws BeansException 如果任何后处理失败
     * @see BeanPostProcessor#postProcessBeforeInitialization
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * 将BeanPostProcessor BeanPostProcessors应用于给定的现有bean实例，
     * 调用其{@code postProcessAfterInitialization方法。返回的bean实例可能是原始bean的包装。
     * @param existingBean 现有的bean实例
     * @param beanName bean的名称，如果需要，将传递给它
     *                 （仅传递给BeanPostProcessor BeanPostProcessors；
     *                  可以遵循#ORIGINAL_INSTANCE_SUFFIX约定，以强制返回给定的实例，
     *                  即没有代理等）
     * @return 要使用的bean实例，原始的或包装的其中之一
     * @throws BeansException 如果任何后处理失败
     * @see BeanPostProcessor#postProcessAfterInitialization
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * 销毁给定的bean实例（通常来自#createBean），应用
     * org.springframework.beans.factory.DisposableBean合同以及注册的
     * DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessors。
     * 在销毁过程中出现的任何异常都应该被捕获并记录，而不是传播到此方法的调用方。
     * @param existingBean 要销毁的bean实例
     */
    void destroyBean(Object existingBean);

    /**
     * 解析唯一匹配给定对象类型的bean实例，如果有的话，包括其bean名称。
     * 这实际上是#getBean(Class)的一个变体，它保留匹配实例的bean名称。
     * @param requiredType bean必须匹配的类型；可以是接口或超类
     * @return bean名称加上bean实例
     * @throws NoSuchBeanDefinitionException 如果没有找到匹配的bean
     * @throws NoUniqueBeanDefinitionException 如果找到多个匹配的bean
     * @throws BeansException 如果无法创建bean
     * @since 4.3.3
     * @see #getBean(Class)
     */
    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

    /**
     * 为给定的bean名称解析bean实例，提供一个依赖项描述符，以供目标工厂方法使用。
     * 这实际上是#getBean(String, Class)的一个变体，支持具有
     * org.springframework.beans.factory.InjectionPoint参数的工厂方法。
     * @param name 要查找的bean的名称
     * @param descriptor 用于请求注入点的依赖项描述符
     * @return 相应的bean实例
     * @throws NoSuchBeanDefinitionException 如果没有指定名称的bean
     * @throws BeansException 如果无法创建bean
     * @since 5.1.5
     * @see #getBean(String, Class)
     */
    Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;

    /**
     * 解析针对此工厂中定义的bean的指定依赖项。
     * @param descriptor 依赖项的描述符（字段/方法/构造函数）
     * @param requestingBeanName 声明给定依赖项的bean的名称
     * @return 已解析的对象，如果找不到则返回{@code null
     * @throws NoSuchBeanDefinitionException 如果未找到匹配的bean
     * @throws NoUniqueBeanDefinitionException 如果找到多个匹配的bean
     * @throws BeansException 如果由于其他原因导致依赖项解析失败
     * @since 2.5
     * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
     */
    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

    /**
     * 解析针对此工厂中定义的bean的指定依赖项。
     * @param descriptor 依赖项的描述符（字段/方法/构造函数）
     * @param requestingBeanName 声明给定依赖项的bean的名称
     * @param autowiredBeanNames 应将所有自动装配的bean的名称（用于解析给定依赖项）添加到的Set
     * @param typeConverter 用于填充数组和集合的TypeConverter
     * @return 已解析的对象，如果找不到则返回{@code null
     * @throws NoSuchBeanDefinitionException 如果未找到匹配的bean
     * @throws NoUniqueBeanDefinitionException 如果找到多个匹配的bean
     * @throws BeansException 如果由于其他原因导致依赖项解析失败
     * @since 2.5
     * @see DependencyDescriptor
     */
    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
            @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;
			
}
```

### 六、主要实现

### 七、最佳实践

### 八、与其他组件的关系

### 九、常见问题