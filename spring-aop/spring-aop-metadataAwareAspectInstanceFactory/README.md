## MetadataAwareAspectInstanceFactory

- [MetadataAwareAspectInstanceFactory](#metadataawareaspectinstancefactory)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、接口源码](#四接口源码)
  - [五、主要实现](#五主要实现)
  - [六、类关系图](#六类关系图)
  - [七、最佳实践](#七最佳实践)
  - [八、源码分析](#八源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https//juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https//github.com/xuchengsheng/spring-reading)

### 二、基本描述

`MetadataAwareAspectInstanceFactory` 接口是 Spring AOP 中的关键接口，用于实例化切面并处理其元数据信息，为 Spring 框架提供了对 AspectJ 注解风格的 AOP 切面的支持。

### 三、主要功能

1. **实例化切面**

   + 通过 `getAspectInstance()` 方法，提供切面实例，以便在运行时应用切面的通知。
   
2. **处理元数据信息**

   + 通过 `getAspectMetadata()` 方法，获取切面类的元数据信息，如类名、所属的类、切点表达式等，以便在运行时能够正确地应用切面。
   
3. **支持 AspectJ 注解风格的 AOP**

   + 通过这个接口，Spring AOP 能够实现对 AspectJ 注解风格的 AOP 切面的实例化和元数据处理，从而支持在 Spring 应用中使用 AspectJ 注解定义切面。

### 四、接口源码

`MetadataAwareAspectInstanceFactory` 接口是 `AspectInstanceFactory` 的子接口，用于返回与 AspectJ 注解类关联的 `AspectMetadata`。`AspectMetadata` 包含与切面相关的元数据信息。此接口还定义了一个方法 `getAspectCreationMutex()`，用于返回此工厂的最佳创建互斥锁。由于 `AspectMetadata` 使用了 Java 5 专用的 `org.aspectj.lang.reflect.AjType`，因此需要将此方法拆分到这个子接口中。

```java
/**
 * {@link org.springframework.aop.aspectj.AspectInstanceFactory} 的子接口，用于返回与 AspectJ 注解类关联的 {@link AspectMetadata}。
 *
 * <p>理想情况下，AspectInstanceFactory 本身应该包括此方法，但由于 AspectMetadata 使用了 Java 5 专用的 {@link org.aspectj.lang.reflect.AjType}，
 * 我们需要拆分出这个子接口。
 *
 * @author Rod Johnson
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjType
 */
public interface MetadataAwareAspectInstanceFactory extends AspectInstanceFactory {

	/**
	 * 返回此工厂的切面的 AspectJ AspectMetadata。
	 * @return 切面元数据
	 */
	AspectMetadata getAspectMetadata();

	/**
	 * 返回此工厂的最佳创建互斥锁。
	 * @return 互斥锁对象（如果不需要使用锁，则可能为 {@code null}）
	 * @since 4.3
	 */
	@Nullable
	Object getAspectCreationMutex();

}
```

### 五、主要实现

1. **SimpleMetadataAwareAspectInstanceFactory**

   - 这个实现类是最简单的一种，它用于创建单例的切面实例。它简单地实例化切面类，并提供其实例作为切面的实例。

2. **SingletonMetadataAwareAspectInstanceFactory**

   - 与 `SimpleMetadataAwareAspectInstanceFactory` 类似，这个实现类也用于创建单例的切面实例，但是它可以与 Spring 的容器集成，以便将切面实例作为容器中的单例 bean 进行管理。

3. **BeanFactoryAspectInstanceFactory**

   - 这个实现类与 Spring 的 BeanFactory 集成，它用于创建切面实例，并且能够处理切面类的依赖注入。它可以在切面类中注入其他 Spring 管理的 bean，实现更复杂的业务逻辑。

4. **PrototypeAspectInstanceFactory**

   - 这个实现类用于创建原型（prototype）的切面实例。与单例不同，原型实例每次请求时都会创建一个新的实例，适用于需要在每次使用时都重新创建实例的场景。

5. **LazySingletonAspectInstanceFactoryDecorator**

   - 这个实现类是一个装饰器，用于延迟初始化单例的切面实例。它在首次请求切面实例时才进行实例化，以提高性能并延迟资源消耗。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AspectInstanceFactory {
<<Interface>>

}
class BeanFactoryAspectInstanceFactory
class LazySingletonAspectInstanceFactoryDecorator
class MetadataAwareAspectInstanceFactory {
<<Interface>>

}
class PrototypeAspectInstanceFactory
class SimpleAspectInstanceFactory
class SimpleMetadataAwareAspectInstanceFactory
class SingletonAspectInstanceFactory
class SingletonMetadataAwareAspectInstanceFactory

BeanFactoryAspectInstanceFactory  ..>  MetadataAwareAspectInstanceFactory 
LazySingletonAspectInstanceFactoryDecorator  ..>  MetadataAwareAspectInstanceFactory 
MetadataAwareAspectInstanceFactory  -->  AspectInstanceFactory 
PrototypeAspectInstanceFactory  -->  BeanFactoryAspectInstanceFactory 
SimpleAspectInstanceFactory  ..>  AspectInstanceFactory 
SimpleMetadataAwareAspectInstanceFactory  ..>  MetadataAwareAspectInstanceFactory 
SimpleMetadataAwareAspectInstanceFactory  -->  SimpleAspectInstanceFactory 
SingletonAspectInstanceFactory  ..>  AspectInstanceFactory 
SingletonMetadataAwareAspectInstanceFactory  ..>  MetadataAwareAspectInstanceFactory 
SingletonMetadataAwareAspectInstanceFactory  -->  SingletonAspectInstanceFactory 
~~~

### 七、最佳实践

使用不同的 `MetadataAwareAspectInstanceFactory` 实现类来实例化切面，并展示了它们的不同行为。首先，使用 `SimpleMetadataAwareAspectInstanceFactory` 和 `SingletonMetadataAwareAspectInstanceFactory` 分别创建单例的切面实例，然后使用 `BeanFactoryAspectInstanceFactory` 在 Spring Bean 工厂中注册并实例化切面，最后使用 `LazySingletonAspectInstanceFactoryDecorator` 延迟初始化单例切面实例。在每个步骤中，都输出了切面实例及其元数据信息。

```java
public class MetadataAwareAspectInstanceFactoryDemo {

    public static void main(String[] args) {
        // 使用 SimpleMetadataAwareAspectInstanceFactory 实例化切面
        SimpleMetadataAwareAspectInstanceFactory simpleMetadataAwareAif = new SimpleMetadataAwareAspectInstanceFactory(MyAspect.class, "myAspect");
        System.out.println("SimpleMetadataAwareAspectInstanceFactory (1) = " + simpleMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory (2) = " + simpleMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(simpleMetadataAwareAif.getAspectMetadata()));
        System.out.println();

        // 使用 SingletonMetadataAwareAspectInstanceFactory 实例化切面
        SingletonMetadataAwareAspectInstanceFactory singletonMetadataAwareAif = new SingletonMetadataAwareAspectInstanceFactory(new MyAspect(), "myAspect");
        System.out.println("SingletonMetadataAwareAspectInstanceFactory (1) = " + singletonMetadataAwareAif.getAspectInstance());
        System.out.println("SingletonMetadataAwareAspectInstanceFactory (2) = " + singletonMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(singletonMetadataAwareAif.getAspectMetadata()));
        System.out.println();

        // 使用 BeanFactoryAspectInstanceFactory 实例化切面
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("myAspect", new MyAspect());
        BeanFactoryAspectInstanceFactory banFactoryAif = new BeanFactoryAspectInstanceFactory(beanFactory, "myAspect");
        System.out.println("BeanFactoryAspectInstanceFactory (1) = " + banFactoryAif.getAspectInstance());
        System.out.println("BeanFactoryAspectInstanceFactory (2) = " + banFactoryAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(banFactoryAif.getAspectMetadata()));
        System.out.println();

        // 使用 LazySingletonAspectInstanceFactoryDecorator 实例化切面
        LazySingletonAspectInstanceFactoryDecorator lazySingletonAifD = new LazySingletonAspectInstanceFactoryDecorator(banFactoryAif);
        System.out.println("LazySingletonAspectInstanceFactoryDecorator (1) = " + lazySingletonAifD.getAspectInstance());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator (2) = " + lazySingletonAifD.getAspectInstance());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator AspectCreationMutex = " + lazySingletonAifD.getAspectCreationMutex());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator AspectMetadata = " + JSONUtil.toJsonStr(lazySingletonAifD.getAspectMetadata()));
        System.out.println();
    }
}
```

运行结果，展示了不同类型的 `MetadataAwareAspectInstanceFactory` 实现类的行为。`SimpleMetadataAwareAspectInstanceFactory` 每次返回不同的切面实例，而 `SingletonMetadataAwareAspectInstanceFactory` 每次返回相同的实例，说明了它们的单例和非单例的行为。`BeanFactoryAspectInstanceFactory` 在 Spring Bean 工厂中注册并实例化切面，表现出与前两者类似的行为。`LazySingletonAspectInstanceFactoryDecorator` 是对 `BeanFactoryAspectInstanceFactory` 的装饰器，延迟初始化单例切面实例，但最终结果与 `BeanFactoryAspectInstanceFactory` 相同。

```java
SimpleMetadataAwareAspectInstanceFactory (1) = com.xcs.spring.MyAspect@5f341870
SimpleMetadataAwareAspectInstanceFactory (2) = com.xcs.spring.MyAspect@553f17c
SimpleMetadataAwareAspectInstanceFactory AspectMetadata = {"aspectName":"myAspect","aspectClass":"com.xcs.spring.MyAspect","perClausePointcut":{}}

SingletonMetadataAwareAspectInstanceFactory (1) = com.xcs.spring.MyAspect@1da51a35
SingletonMetadataAwareAspectInstanceFactory (2) = com.xcs.spring.MyAspect@1da51a35
SimpleMetadataAwareAspectInstanceFactory AspectMetadata = {"aspectName":"myAspect","aspectClass":"com.xcs.spring.MyAspect","perClausePointcut":{}}

BeanFactoryAspectInstanceFactory (1) = com.xcs.spring.MyAspect@6646153
BeanFactoryAspectInstanceFactory (2) = com.xcs.spring.MyAspect@6646153
SimpleMetadataAwareAspectInstanceFactory AspectMetadata = {"aspectName":"myAspect","aspectClass":"com.xcs.spring.MyAspect","perClausePointcut":{}}

LazySingletonAspectInstanceFactoryDecorator (1) = com.xcs.spring.MyAspect@6646153
LazySingletonAspectInstanceFactoryDecorator (2) = com.xcs.spring.MyAspect@6646153
LazySingletonAspectInstanceFactoryDecorator AspectCreationMutex = null
LazySingletonAspectInstanceFactoryDecorator AspectMetadata = {"aspectName":"myAspect","aspectClass":"com.xcs.spring.MyAspect","perClausePointcut":{}}
```

### 八、源码分析

**SimpleMetadataAwareAspectInstanceFactory**

`SimpleMetadataAwareAspectInstanceFactory` 是一个实现了 `MetadataAwareAspectInstanceFactory` 接口的类，它在每次调用 `getAspectInstance()` 方法时都会为指定的切面类创建一个新的实例。这个类通过 `AspectMetadata` 对象来管理切面的元数据信息，并且实现了 `getAspectMetadata()` 方法来提供这些元数据。它还实现了 `getAspectCreationMutex()` 方法来返回切面实例的创建锁，以及 `getOrderForAspectClass()` 方法来确定切面类的顺序。

```java
/**
 * 实现了 {@link MetadataAwareAspectInstanceFactory} 接口的类，每次调用 {@link #getAspectInstance()} 方法都会为指定的切面类创建一个新的实例。
 *
 * @author Juergen Hoeller
 * @since 2.0.4
 */
public class SimpleMetadataAwareAspectInstanceFactory extends SimpleAspectInstanceFactory
        implements MetadataAwareAspectInstanceFactory {

    private final AspectMetadata metadata; // 切面的元数据信息

    /**
     * 创建一个新的 SimpleMetadataAwareAspectInstanceFactory 实例，用于给定的切面类。
     *
     * @param aspectClass 切面类
     * @param aspectName  切面名称
     */
    public SimpleMetadataAwareAspectInstanceFactory(Class<?> aspectClass, String aspectName) {
        super(aspectClass);
        this.metadata = new AspectMetadata(aspectClass, aspectName); // 创建切面的元数据信息
    }

    /**
     * 获取切面的元数据信息。
     *
     * @return 切面的元数据信息
     */
    @Override
    public final AspectMetadata getAspectMetadata() {
        return this.metadata;
    }

    /**
     * 获取切面实例的创建锁。
     *
     * @return 切面实例的创建锁
     */
    @Override
    public Object getAspectCreationMutex() {
        return this;
    }

    /**
     * 获取切面类的顺序。
     *
     * @param aspectClass 切面类
     * @return 切面类的顺序
     */
    @Override
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return OrderUtils.getOrder(aspectClass, Ordered.LOWEST_PRECEDENCE); // 获取切面类的顺序
    }

}
```

**SingletonMetadataAwareAspectInstanceFactory**

`SingletonMetadataAwareAspectInstanceFactory` 是一个实现了 `MetadataAwareAspectInstanceFactory` 接口的类，它通过指定的单例对象支持切面实例的创建，每次调用 `getAspectInstance()` 方法都返回相同的实例。该类使用一个单例的切面实例，并通过 `AspectMetadata` 对象管理切面的元数据信息。它也实现了 `Serializable` 接口以支持序列化，并且继承自 `SingletonAspectInstanceFactory`，提供了获取切面实例的相关方法和逻辑。

```java
/**
 * 实现了 {@link MetadataAwareAspectInstanceFactory} 接口的类，通过指定的单例对象支持切面实例的创建，每次调用 {@link #getAspectInstance()} 方法都返回同一个实例。
 * 
 * 该类通过 {@link AspectMetadata} 对象管理切面的元数据信息，并且实现了 {@link Serializable} 接口以支持序列化。
 * 
 * 作者：Rod Johnson, Juergen Hoeller
 * @since 2.0
 * @see SimpleMetadataAwareAspectInstanceFactory
 */
@SuppressWarnings("serial")
public class SingletonMetadataAwareAspectInstanceFactory extends SingletonAspectInstanceFactory
        implements MetadataAwareAspectInstanceFactory, Serializable {

    private final AspectMetadata metadata; // 切面的元数据信息

    /**
     * 为给定的切面创建一个新的 SingletonMetadataAwareAspectInstanceFactory。
     * 
     * @param aspectInstance 切面的单例实例
     * @param aspectName 切面的名称
     */
    public SingletonMetadataAwareAspectInstanceFactory(Object aspectInstance, String aspectName) {
        super(aspectInstance); // 调用父类的构造方法，传入切面的单例实例
        this.metadata = new AspectMetadata(aspectInstance.getClass(), aspectName); // 创建切面的元数据信息
    }

    /**
     * 获取切面的元数据信息。
     * 
     * @return 切面的元数据信息
     */
    @Override
    public final AspectMetadata getAspectMetadata() {
        return this.metadata;
    }

    /**
     * 获取切面实例的创建锁。
     * 
     * @return 切面实例的创建锁
     */
    @Override
    public Object getAspectCreationMutex() {
        return this;
    }

    /**
     * 获取切面类的顺序。
     * 
     * @param aspectClass 切面类
     * @return 切面类的顺序
     */
    @Override
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return OrderUtils.getOrder(aspectClass, Ordered.LOWEST_PRECEDENCE); // 获取切面类的顺序
    }

}
```

**BeanFactoryAspectInstanceFactory**

`BeanFactoryAspectInstanceFactory` 是一个实现了 `MetadataAwareAspectInstanceFactory` 接口的类，它通过 Spring 的 `BeanFactory` 支持切面实例的创建。这个工厂可以通过指定的 bean 名称从 `BeanFactory` 中获取切面实例，并且可以通过提供的类型来自省以创建 AspectJ 的元数据信息。它可以处理单例和非单例的情况，并且能够确定切面的顺序，支持使用 `Ordered` 接口或 `@Order` 注解来定义顺序。

```java
/**
 * {@link org.springframework.aop.aspectj.AspectInstanceFactory} 接口的实现，
 * 由 Spring {@link org.springframework.beans.factory.BeanFactory} 支持。
 *
 * <p>注意，如果使用原型模式可能会多次实例化，这可能不会得到您期望的语义。
 * 使用 {@link LazySingletonAspectInstanceFactoryDecorator} 来包装这个工厂，
 * 以确保只返回一个新的切面。
 *
 * 作者：Rod Johnson, Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.factory.BeanFactory
 * @see LazySingletonAspectInstanceFactoryDecorator
 */
@SuppressWarnings("serial")
public class BeanFactoryAspectInstanceFactory implements MetadataAwareAspectInstanceFactory, Serializable {

    private final BeanFactory beanFactory; // Bean 工厂
    private final String name; // Bean 名称
    private final AspectMetadata aspectMetadata; // 切面的元数据信息

    /**
     * 创建一个 BeanFactoryAspectInstanceFactory。AspectJ 将被调用来自省，
     * 使用从 BeanFactory 中为给定的 bean 名称返回的类型创建 AJType 元数据。
     *
     * @param beanFactory BeanFactory，用于获取实例
     * @param name bean 的名称
     */
    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name) {
        this(beanFactory, name, null);
    }

    /**
     * 创建一个 BeanFactoryAspectInstanceFactory，提供一个类型，AspectJ 应该自省以创建 AJType 元数据。
     * 如果 BeanFactory 可能将类型视为子类（例如使用 CGLIB），并且信息应该与超类相关，则使用此选项。
     *
     * @param beanFactory BeanFactory，用于获取实例
     * @param name bean 的名称
     * @param type AspectJ 应该自省的类型（{@code null} 表示通过 bean 名称解析通过 {@link BeanFactory#getType} 的类型）
     */
    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name, @Nullable Class<?> type) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.notNull(name, "Bean name must not be null");
        this.beanFactory = beanFactory;
        this.name = name;
        Class<?> resolvedType = type;
        if (type == null) {
            resolvedType = beanFactory.getType(name);
            Assert.notNull(resolvedType, "Unresolvable bean type - explicitly specify the aspect class");
        }
        this.aspectMetadata = new AspectMetadata(resolvedType, name); // 创建切面的元数据信息
    }

    /**
     * 获取切面实例。
     *
     * @return 切面实例
     */
    @Override
    public Object getAspectInstance() {
        return this.beanFactory.getBean(this.name);
    }

    /**
     * 获取切面的类加载器。
     *
     * @return 切面的类加载器
     */
    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return (this.beanFactory instanceof ConfigurableBeanFactory ?
                ((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader() :
                ClassUtils.getDefaultClassLoader());
    }

    /**
     * 获取切面的元数据信息。
     *
     * @return 切面的元数据信息
     */
    @Override
    public AspectMetadata getAspectMetadata() {
        return this.aspectMetadata;
    }

    /**
     * 获取切面实例的创建锁。
     *
     * @return 切面实例的创建锁
     */
    @Override
    @Nullable
    public Object getAspectCreationMutex() {
        if (this.beanFactory.isSingleton(this.name)) {
            // 依赖于工厂提供的单例语义 -> 没有本地锁。
            return null;
        } else if (this.beanFactory instanceof ConfigurableBeanFactory) {
            // 从工厂中没有单例保证 -> 让我们本地锁定，但重用工厂的单例锁，以防万一我们的通知 bean 的惰性依赖项
            // 不小心触发了单例锁隐式...
            return ((ConfigurableBeanFactory) this.beanFactory).getSingletonMutex();
        } else {
            return this;
        }
    }

    /**
     * 确定此工厂目标切面的顺序，可以通过实现 {@link org.springframework.core.Ordered} 接口来表达实例特定的顺序
     * （仅对单例 bean 进行检查），也可以通过 {@link org.springframework.core.annotation.Order} 注解在类级别表达顺序。
     *
     * @see org.springframework.core.Ordered
     * @see org.springframework.core.annotation.Order
     */
    @Override
    public int getOrder() {
        Class<?> type = this.beanFactory.getType(this.name);
        if (type != null) {
            if (Ordered.class.isAssignableFrom(type) && this.beanFactory.isSingleton(this.name)) {
                return ((Ordered) this.beanFactory.getBean(this.name)).getOrder();
            }
            return OrderUtils.getOrder(type, Ordered.LOWEST_PRECEDENCE);
        }
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": bean name '" + this.name + "'";
    }
}
```

**LazySingletonAspectInstanceFactoryDecorator**

`LazySingletonAspectInstanceFactoryDecorator`类是一个装饰器，用于确保一个 `MetadataAwareAspectInstanceFactory` 只实例化一次。它包装了另一个 `MetadataAwareAspectInstanceFactory` 实例，并在首次调用 `getAspectInstance()` 方法时进行实例化。在后续的调用中，它将返回已经实例化的对象，而不会再次实例化。

```java
/**
 * 修饰器，使 {@link MetadataAwareAspectInstanceFactory} 仅实例化一次。
 * 
 * 作者：Rod Johnson, Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class LazySingletonAspectInstanceFactoryDecorator implements MetadataAwareAspectInstanceFactory, Serializable {

	private final MetadataAwareAspectInstanceFactory maaif; // 要装饰的 MetadataAwareAspectInstanceFactory 实例

	@Nullable
	private volatile Object materialized; // 实例化的对象

	/**
	 * 创建一个对给定 AspectInstanceFactory 进行懒初始化的修饰器。
	 * @param maaif 要装饰的 MetadataAwareAspectInstanceFactory
	 */
	public LazySingletonAspectInstanceFactoryDecorator(MetadataAwareAspectInstanceFactory maaif) {
		Assert.notNull(maaif, "AspectInstanceFactory must not be null");
		this.maaif = maaif;
	}

	/**
     * 获取切面实例。
     * 如果实例化过程中已经存在一个实例，则直接返回该实例；
     * 否则，根据实例化互斥锁（如果存在）保证多线程环境下只实例化一次。
     * 如果没有互斥锁，则直接实例化切面对象并将其赋值给 materialized 变量，然后返回该实例。
     * 如果存在互斥锁，则使用该锁来保护实例化过程，确保在多线程环境下只有一个线程可以执行实例化操作。
     * 
     * @return 切面实例
     */
    @Override
    public Object getAspectInstance() {
        // 尝试获取已实例化的对象
        Object aspectInstance = this.materialized; 
        // 如果不存在已实例化的对象
        if (aspectInstance == null) { 
            // 获取实例化互斥锁
            Object mutex = this.maaif.getAspectCreationMutex(); 
            // 如果不存在互斥锁
            if (mutex == null) { 
                // 直接实例化切面对象
                aspectInstance = this.maaif.getAspectInstance(); 
                // 将实例化后的对象赋值给 materialized 变量
                this.materialized = aspectInstance; 
            } else { 
                // 使用互斥锁保护实例化过程
                synchronized (mutex) {
                    // 再次尝试获取已实例化的对象
                    aspectInstance = this.materialized; 
                    // 双重检查，确保在锁内部只实例化一次
                    if (aspectInstance == null) { 
                        // 实例化切面对象
                        aspectInstance = this.maaif.getAspectInstance(); 
                        // 将实例化后的对象赋值给 materialized 变量
                        this.materialized = aspectInstance; 
                    }
                }
            }
        }
        return aspectInstance; // 返回切面实例
    }

	/**
	 * 返回是否已经实例化。
	 */
	public boolean isMaterialized() {
		return (this.materialized != null);
	}

	@Override
	@Nullable
	public ClassLoader getAspectClassLoader() {
		return this.maaif.getAspectClassLoader();
	}

	@Override
	public AspectMetadata getAspectMetadata() {
		return this.maaif.getAspectMetadata();
	}

	@Override
	@Nullable
	public Object getAspectCreationMutex() {
		return this.maaif.getAspectCreationMutex();
	}

	@Override
	public int getOrder() {
		return this.maaif.getOrder();
	}

	@Override
	public String toString() {
		return "LazySingletonAspectInstanceFactoryDecorator: decorating " + this.maaif;
	}

}
```
