## ConfigurableListableBeanFactory

- [ConfigurableListableBeanFactory](#configurablelistablebeanfactory)
	- [一、基本信息](#一基本信息)
	- [二、基本描述](#二基本描述)
	- [三、主要功能](#三主要功能)
	- [四、接口源码](#四接口源码)
	- [五、最佳实践](#五最佳实践)
	- [六、与其他组件的关系](#六与其他组件的关系)
	- [七、常见问题](#七常见问题)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`ConfigurableListableBeanFactory`接口是Spring框架中的一个关键接口，扩展了`ListableBeanFactory`和`ConfigurableBeanFactory`，提供了更多用于配置和管理bean的方法。通过该接口，可以注册单例对象、自定义作用域、冻结配置、获取BeanDefinition信息、设置父BeanFactory等。

### 三、主要功能

1. **注册单例对象** 

   + 允许通过指定的bean名称注册一个单例对象，使用`registerSingleton(String beanName, Object singletonObject)`方法。

2. **注册作用域** 

   + 提供方法`registerScope(String scopeName, Scope scope)`，用于注册自定义的作用域，扩展了Spring默认的单例、原型等作用域。

3. **冻结配置** 

   + 通过`freezeConfiguration()`方法，可以冻结bean工厂的配置，防止在后续阶段对其进行修改。这在某些场景下可以增强应用程序的安全性。

4. **提供已注册的BeanDefinition** 

   + 通过`getBeanDefinition(String beanName)`方法，可以获取指定bean名称的`BeanDefinition`对象，包含有关bean的配置信息，例如作用域、依赖等。

5. **提供已注册的所有BeanDefinition名称** 

   + 使用`getBeanDefinitionNames()`方法，可以获取所有已注册bean定义的名称数组，方便进行遍历和查看。

6. **提供已注册的所有单例bean名称** 

   + 通过`getSingletonNames()`方法，可以获取当前已注册的所有单例bean的名称数组，方便查看和管理已实例化的bean。

7. **设置父BeanFactory** 

   + 允许通过`setParentBeanFactory(BeanFactory parentBeanFactory)`方法设置一个父BeanFactory，使得在父工厂中查找bean定义。

8. **获取类加载器** 

   + 通过`getBeanClassLoader()`方法获取用于加载bean类的类加载器，这在动态加载类的场景中很有用。

### 四、接口源码

```java
/**
 * ConfigurableListableBeanFactory接口是大多数可列举的bean工厂应该实现的配置接口。
 * 除了继承自{@link ConfigurableBeanFactory}的方法外，它提供了分析和修改bean定义以及预实例化单例bean的功能。
 *
 * <p>{@link org.springframework.beans.factory.BeanFactory}的这个子接口通常不应该在正常应用程序代码中使用：
 * 对于典型的用例，请使用{@link org.springframework.beans.factory.BeanFactory}或
 * {@link org.springframework.beans.factory.ListableBeanFactory}。
 * 此接口只是为了允许在需要访问bean工厂配置方法时进行框架内部的插拔。
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.context.support.AbstractApplicationContext#getBeanFactory()
 */
public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

	/**
	 * 忽略指定的自动装配依赖类型，例如String。默认为空。
	 * @param type 要忽略的依赖类型
	 */
	void ignoreDependencyType(Class<?> type);

	/**
	 * 忽略指定的自动装配依赖接口。
	 * <p>通常由应用程序上下文使用，以注册通过其他方式解析的依赖关系，
	 * 例如通过BeanFactoryAware或ApplicationContextAware。
	 * <p>默认情况下，仅忽略BeanFactoryAware接口。
	 * 若要忽略更多类型，请为每个类型调用此方法。
	 * @param ifc 要忽略的依赖接口
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.context.ApplicationContextAware
	 */
	void ignoreDependencyInterface(Class<?> ifc);

	/**
	 * 注册特殊的依赖类型及其对应的自动装配值。
	 * <p>这用于应该是可自动装配但在工厂中未定义为bean的工厂/上下文引用，
	 * 例如类型为ApplicationContext的依赖，解析为bean所在的ApplicationContext实例。
	 * <p>注意：在纯BeanFactory中没有默认类型注册，甚至没有为BeanFactory接口本身注册。
	 * @param dependencyType 要注册的依赖类型。这通常是一个基本接口，如BeanFactory，
	 * 如果声明为自动装配依赖（例如，ListableBeanFactory），则扩展的接口也会被解析，
	 * 只要给定的值实际上实现了扩展接口。
	 * @param autowiredValue 对应的自动装配值。这也可以是org.springframework.beans.factory.ObjectFactory
	 * 接口的实现，允许延迟解析实际目标值。
	 */
	void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

	/**
	 * 确定指定的bean是否符合自动装配的条件，
	 * 即是否可注入到声明具有匹配类型的依赖关系的其他bean中。
	 * <p>此方法还检查祖先工厂。
	 * @param beanName 要检查的bean的名称
	 * @param descriptor 要解析的依赖项的描述符
	 * @return bean是否应被视为自动装配候选
	 * @throws NoSuchBeanDefinitionException 如果没有给定名称的bean
	 */
	boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException;

	/**
	 * 返回指定bean的注册BeanDefinition，允许访问其属性值和构造函数参数值
	 * （可以在bean工厂后处理期间进行修改）。
	 * <p>返回的BeanDefinition对象不应该是副本，而应该是在工厂中注册的原始定义对象。
	 * 这意味着，如果有必要，它应该可以转换为更具体的实现类型。
	 * <p><b>注意：</b>此方法不考虑祖先工厂。它仅用于访问此工厂的本地bean定义。
	 * @param beanName 要获取的bean的名称
	 * @return 注册的BeanDefinition
	 * @throws NoSuchBeanDefinitionException 如果在此工厂中没有给定名称的bean定义
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 返回此工厂管理的所有bean名称的统一视图。
	 * <p>包括bean定义名称以及手动注册的单例实例的名称，
	 * bean定义名称始终首先出现，类似于通过类型/注解特定检索bean名称的方式。
	 * @return 用于bean名称视图的复合迭代器
	 * @since 4.1.2
	 * @see #containsBeanDefinition
	 * @see #registerSingleton
	 * @see #getBeanNamesForType
	 * @see #getBeanNamesForAnnotation
	 */
	Iterator<String> getBeanNamesIterator();

	/**
	 * 清除合并的bean定义缓存，删除不符合完全元数据缓存资格的bean的条目。
	 * <p>通常在对原始bean定义进行更改后触发，例如在应用BeanFactoryPostProcessor之后。
	 * 请注意，此时已经创建的bean的元数据将被保留。
	 * @since 4.2
	 * @see #getBeanDefinition
	 * @see #getMergedBeanDefinition
	 */
	void clearMetadataCache();

	/**
	 * 冻结所有bean定义，表示注册的bean定义将不再被修改或进一步后处理。
	 * <p>这允许工厂积极地缓存bean定义元数据。
	 */
	void freezeConfiguration();

	/**
	 * 返回此工厂的bean定义是否被冻结，
	 * 即不应再被修改或进一步后处理。
	 * @return 如果工厂的配置被认为已冻结，则为{@code true}
	 */
	boolean isConfigurationFrozen();

	/**
	 * 确保实例化所有非懒加载单例，还考虑到org.springframework.beans.factory.FactoryBean。
	 * 通常在工厂设置结束时调用，如果需要的话。
	 * @throws BeansException 如果无法创建其中一个单例bean。
	 * 注意：这可能已经使工厂具有一些已初始化的bean！在这种情况下，调用destroySingletons()进行完全清理。
	 * @see #destroySingletons()
	 */
	void preInstantiateSingletons() throws BeansException;
}

```

### 五、最佳实践

演示了`ConfigurableListableBeanFactory`接口的一些常用方法。包括依赖类型的忽略、注册可解析的依赖、判断是否为自动注入的候选者、获取BeanDefinition等功能的使用。

```java
public class ConfigurableListableBeanFactoryDemo {

    public static void main(String[] args) throws NoSuchFieldException {
        // 创建 ApplicationContext
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

        // 获取 ConfigurableListableBeanFactory
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        // 忽略指定类型的依赖
        beanFactory.ignoreDependencyType(String.class);

        // 忽略指定接口的依赖
        beanFactory.ignoreDependencyInterface(BeanFactory.class);

        // 注册可解析的依赖
        beanFactory.registerResolvableDependency(ApplicationContext.class, applicationContext);

        // 判断指定的 Bean 是否可以作为自动注入的候选者
        String beanName = "myService";

        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(MyController.class.getDeclaredField("myService"), false);
        boolean isAutowireCandidate = beanFactory.isAutowireCandidate(beanName, dependencyDescriptor);
        System.out.println(beanName + " 是否为自动注入的候选者: " + isAutowireCandidate);

        // 获取指定 Bean 的 BeanDefinition
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        System.out.println(beanName + " 的 BeanDefinition: " + beanDefinition);

        // 获取所有 Bean 的名称的迭代器
        Iterator<String> beanNamesIterator = beanFactory.getBeanNamesIterator();
        System.out.print("所有 Bean 的名称: ");
        beanNamesIterator.forEachRemaining(System.out::print);

        // 清除元数据缓存
        beanFactory.clearMetadataCache();

        // 冻结配置
        beanFactory.freezeConfiguration();

        // 判断配置是否已冻结
        boolean isConfigurationFrozen = beanFactory.isConfigurationFrozen();
        System.out.println("配置是否已冻结: " + isConfigurationFrozen);

        // 预实例化所有非懒加载的单例 Bean
        beanFactory.preInstantiateSingletons();
    }
}
```

### 六、与其他组件的关系

1. **BeanFactory**

   - `ConfigurableListableBeanFactory` 继承自 `BeanFactory` 接口，因此它继承了 `BeanFactory` 的基本功能，如 `getBean` 等方法。

   - `ConfigurableListableBeanFactory` 是 `ListableBeanFactory` 和 `ConfigurableBeanFactory` 的组合，提供了对bean列表和配置的管理能力。

2. **ListableBeanFactory**

   - `ConfigurableListableBeanFactory` 扩展了 `ListableBeanFactory` 接口，使得它除了具备 `ListableBeanFactory` 的能力外，还能够进行更灵活的配置和管理。

3. **AutowireCapableBeanFactory**

   - `ConfigurableListableBeanFactory` 扩展了 `AutowireCapableBeanFactory` 接口，使得它能够进行更高级的自动装配，包括忽略依赖类型、判断是否为自动注入的候选者等。

4. **ConfigurableBeanFactory**

   - `ConfigurableListableBeanFactory` 实现了 `ConfigurableBeanFactory` 接口，提供了一系列配置 bean 工厂的方法，如设置 BeanClassLoader、注册单例、设置作用域等。

5. **ApplicationContext**

   - `ConfigurableListableBeanFactory` 是 Spring 应用上下文（`ApplicationContext`）的一部分。通过 `ApplicationContext` 实例，可以获取到 `ConfigurableListableBeanFactory` 的引用，进而对 bean 进行配置和管理。

   - 具体实现类，例如 `GenericApplicationContext` 或 `AnnotationConfigApplicationContext`，内部持有 `ConfigurableListableBeanFactory` 实例，通过该实例管理 bean 定义和实例。

6. **BeanPostProcessor**

   - `ConfigurableListableBeanFactory` 与 `BeanPostProcessor` 接口协同工作。`BeanPostProcessor` 允许在 bean 实例化和初始化的过程中进行自定义的处理。`ConfigurableListableBeanFactory` 通过添加 `BeanPostProcessor` 来实现对 bean 的定制化处理。

7. **BeanDefinition**

   - `ConfigurableListableBeanFactory` 提供了获取 `BeanDefinition` 的方法，通过 `getBeanDefinition(String beanName)` 可以获取指定 bean 的定义信息。这对于查看和修改 bean 的配置信息非常有用。

### 七、常见问题

1. **Bean的循环依赖问题** 

   - 当两个或多个 bean 彼此依赖形成循环依赖时，可能导致应用程序启动失败或不稳定。我们应该尽量设计避免循环依赖，考虑通过构造函数注入、setter 方法注入、`@Lazy` 注解等方式来解决。

2. **Bean的配置错误** 

   - 配置文件中的 bean 定义存在错误，导致无法正确创建 bean。 我们应该仔细检查配置文件，确保 XML 或 Java 配置正确无误，属性值和引用关系正确。

3. **依赖注入失败** 

   - 依赖注入失败，bean 的某些属性为 null。我们应该检查 bean 的依赖关系，确保属性注入的名称和类型正确，使用 `@Autowired` 或者 `@Resource` 注解时检查自动装配是否生效。

4. **Bean的生命周期问题** 

   - 想要在 bean 的初始化或销毁阶段执行一些特定操作，但操作未生效。我们应该使用 `InitializingBean` 和 `DisposableBean` 接口，或者通过 `@PostConstruct` 和 `@PreDestroy` 注解在方法上执行相应的初始化和销毁逻辑。

5. **Bean的作用域问题** 

   - 需要使用不同的作用域（例如单例、原型等），但实际应用中未生效。我们应该在配置文件或 Java 配置中明确指定 bean 的作用域，确保作用域的使用符合预期。

6. **Bean的懒加载问题** 

   - 想要将某个 bean 设置为懒加载，但该设置未生效。我们应该使用 `@Lazy` 注解或者在 XML 配置中设置 `lazy-init="true"`，确保懒加载的配置正确。

7. **Bean的条件化注册问题** 

   - 想要根据条件动态注册某个 bean，但条件未生效。我们应该使用条件化注解（例如 `@Conditional`）或者通过编程方式在 `ConfigurableListableBeanFactory` 中注册 bean 前进行条件判断。

8. **Bean的后置处理器问题** 

   - 想要对所有 bean 进行额外的处理，但自定义的 `BeanPostProcessor` 未生效。我们应该确保自定义的 `BeanPostProcessor` 被正确注册，可以通过 `ConfigurableListableBeanFactory.addBeanPostProcessor` 方法添加。