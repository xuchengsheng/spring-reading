## AspectInstanceFactory

- [AspectInstanceFactory](#aspectinstancefactory)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、接口源码](#四接口源码)
  - [五、主要实现](#五主要实现)
  - [六、类关系图](#六类关系图)
  - [七、最佳实践](#七最佳实践)
  - [八、源码分析](#八源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`AspectInstanceFactory` 接口是 Spring AOP 中的关键接口，负责在运行时动态创建切面实例，以适应不同的场景和需求，其实现类通过 `getAspectInstance()` 方法提供切面实例，并可指定切面的创建模式。

### 三、主要功能

1. **动态创建切面实例**

   + 允许在运行时动态地创建切面实例，以便在应用程序中应用切面功能。

2. **提供切面实例**

   + 通过 `getAspectInstance()` 方法获取切面实例，以供 Spring AOP 使用。

3. **管理切面生命周期**

   + 可控制切面实例的生命周期，例如可以指定为单例模式（Singleton）或原型模式（Prototype）。

4. **灵活适应不同需求**

   + 允许根据应用程序的需求定制切面实例的创建和管理方式，提供了灵活性和可扩展性。

### 四、接口源码

 `AspectInstanceFactory`接口，用于提供 AspectJ 切面的实例。它与 Spring 的 bean 工厂解耦，通过 `getAspectInstance()` 方法创建切面实例，并通过 `getAspectClassLoader()` 方法公开切面类加载器。此接口还继承了 `Ordered` 接口，以表达切面在链中的顺序值。

```java
/**
 * 用于提供一个 AspectJ 切面实例的接口，与 Spring 的 bean 工厂解耦。
 *
 * <p>扩展了 {@link org.springframework.core.Ordered} 接口，用于表达链中底层切面的顺序值。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.factory.BeanFactory#getBean
 */
public interface AspectInstanceFactory extends Ordered {

	/**
	 * 创建此工厂的切面实例。
	 * @return 切面实例（永远不会为 {@code null}）
	 */
	Object getAspectInstance();

	/**
	 * 公开此工厂使用的切面类加载器。
	 * @return 切面类加载器（对于引导加载器，为 {@code null}）
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	@Nullable
	ClassLoader getAspectClassLoader();

}
```

### 五、主要实现

1. **SimpleAspectInstanceFactory**

   + 一个简单的切面实例工厂，用于创建基于注解的切面实例。

2. **SingletonAspectInstanceFactory**
   + 一个单例的切面实例工厂，用于创建单例的切面实例。

3. **SimpleBeanFactoryAwareAspectInstanceFactory**

+ 一个简单的 Bean 工厂感知切面实例工厂，用于在创建切面实例时考虑 Bean 工厂的上下文信息。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class AspectInstanceFactory {
<<Interface>>

}
class SimpleAspectInstanceFactory
class SimpleBeanFactoryAwareAspectInstanceFactory
class SingletonAspectInstanceFactory

SimpleAspectInstanceFactory  ..>  AspectInstanceFactory 
SimpleBeanFactoryAwareAspectInstanceFactory  ..>  AspectInstanceFactory 
SingletonAspectInstanceFactory  ..>  AspectInstanceFactory 

~~~

### 七、最佳实践

使用不同类型的 `AspectInstanceFactory` 实现类来创建和管理切面实例。首先，通过 `SimpleAspectInstanceFactory` 和 `SingletonAspectInstanceFactory` 分别创建简单实例和单例实例的切面。然后，通过注册一个名为 "myAspect" 的单例 bean，并将其用于配置 `SimpleBeanFactoryAwareAspectInstanceFactory`，从而创建一个依赖于 Bean 工厂的切面实例。最后，展示了获取 `SimpleBeanFactoryAwareAspectInstanceFactory` 实例的切面对象，并输出其结果。

```java
public class AspectInstanceFactoryDemo {

    public static void main(String[] args) {
        // 使用 SimpleAspectInstanceFactory 创建切面实例
        SimpleAspectInstanceFactory sAif = new SimpleAspectInstanceFactory(MyAspect.class);
        System.out.println("SimpleAspectInstanceFactory (1): " + sAif.getAspectInstance());
        System.out.println("SimpleAspectInstanceFactory (2): " + sAif.getAspectInstance());

        // 使用 SingletonAspectInstanceFactory 创建单例切面实例
        SingletonAspectInstanceFactory singletonAif = new SingletonAspectInstanceFactory(new MyAspect());
        System.out.println("SingletonAspectInstanceFactory (1): " + singletonAif.getAspectInstance());
        System.out.println("SingletonAspectInstanceFactory (2): " + singletonAif.getAspectInstance());

        // 创建一个 DefaultListableBeanFactory 实例，用于注册和管理 bean
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 注册一个名为 "myAspect" 的单例 bean，类型为 MyAspect
        beanFactory.registerSingleton("myAspect", new MyAspect());
        // 创建一个切面工厂的 BeanDefinition
        RootBeanDefinition aspectFactoryDef = new RootBeanDefinition(SimpleBeanFactoryAwareAspectInstanceFactory.class);
        // 设置切面工厂的属性 aspectBeanName 为 "myAspect"
        aspectFactoryDef.getPropertyValues().add("aspectBeanName", "myAspect");
        // 设置切面工厂为合成的，即不对外暴露
        aspectFactoryDef.setSynthetic(true);
        // 注册名为 "simpleBeanFactoryAwareAspectInstanceFactory" 的 bean，并使用切面工厂的 BeanDefinition
        beanFactory.registerBeanDefinition("simpleBeanFactoryAwareAspectInstanceFactory", aspectFactoryDef);
        // 从 BeanFactory 中获取 SimpleBeanFactoryAwareAspectInstanceFactory 的实例
        SimpleBeanFactoryAwareAspectInstanceFactory simpleBeanFactoryAwareAif = beanFactory.getBean(SimpleBeanFactoryAwareAspectInstanceFactory.class);
        System.out.println("SimpleBeanFactoryAwareAspectInstanceFactory (1): " + simpleBeanFactoryAwareAif.getAspectInstance());
        System.out.println("SimpleBeanFactoryAwareAspectInstanceFactory (2): " + simpleBeanFactoryAwareAif.getAspectInstance());
    }
}
```

运行结果，通过不同的切面实例工厂创建切面对象的情况：`SimpleAspectInstanceFactory` 每次调用 `getAspectInstance()` 都会创建一个新的切面对象，因此得到的实例不同；而 `SingletonAspectInstanceFactory` 返回的是单例对象，所以多次调用 `getAspectInstance()` 得到的是同一个实例；`SimpleBeanFactoryAwareAspectInstanceFactory` 从 `BeanFactory` 中获取指定名称的 bean，该 bean 默认是单例的，因此也得到相同的实例。

```java
SimpleAspectInstanceFactory (1): com.xcs.spring.MyAspect@6d8a00e3
SimpleAspectInstanceFactory (2): com.xcs.spring.MyAspect@548b7f67
SingletonAspectInstanceFactory (1): com.xcs.spring.MyAspect@5f375618
SingletonAspectInstanceFactory (2): com.xcs.spring.MyAspect@5f375618
SimpleBeanFactoryAwareAspectInstanceFactory (1): com.xcs.spring.MyAspect@41ee392b
SimpleBeanFactoryAwareAspectInstanceFactory (2): com.xcs.spring.MyAspect@41ee392b
```

### 八、源码分析

**SimpleAspectInstanceFactory**

 `SimpleAspectInstanceFactory`类是 `AspectInstanceFactory` 接口的实现。每次调用 `getAspectInstance()` 方法创建指定切面类的新实例。它通过反射机制在运行时实例化切面类，并提供了方法来获取切面类、获取切面类的类加载器以及确定切面实例的顺序。

```java
/**
 * {@link AspectInstanceFactory} 接口的实现类，用于在每次调用 {@link #getAspectInstance()} 方法时为指定的切面类创建一个新实例。
 * 创建新实例的切面工厂。
 *
 * @author Juergen Hoeller
 * @since 2.0.4
 */
public class SimpleAspectInstanceFactory implements AspectInstanceFactory {

    // 切面类
    private final Class<?> aspectClass;

    /**
     * 为给定的切面类创建一个新的 SimpleAspectInstanceFactory。
     * @param aspectClass 切面类
     */
    public SimpleAspectInstanceFactory(Class<?> aspectClass) {
        Assert.notNull(aspectClass, "Aspect class must not be null");
        this.aspectClass = aspectClass;
    }

    /**
     * 返回指定的切面类（永远不为 {@code null}）。
     */
    public final Class<?> getAspectClass() {
        return this.aspectClass;
    }

    @Override
    public final Object getAspectInstance() {
        try {
            // 使用反射获取切面类的可访问构造函数，并创建新实例
            return ReflectionUtils.accessibleConstructor(this.aspectClass).newInstance();
        } catch (NoSuchMethodException ex) {
            throw new AopConfigException("No default constructor on aspect class: " + this.aspectClass.getName(), ex);
        } catch (InstantiationException ex) {
            throw new AopConfigException("Unable to instantiate aspect class: " + this.aspectClass.getName(), ex);
        } catch (IllegalAccessException ex) {
            throw new AopConfigException("Could not access aspect constructor: " + this.aspectClass.getName(), ex);
        } catch (InvocationTargetException ex) {
            throw new AopConfigException("Failed to invoke aspect constructor: " + this.aspectClass.getName(), ex.getTargetException());
        }
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        // 返回切面类的类加载器
        return this.aspectClass.getClassLoader();
    }

    /**
     * 确定此工厂的切面实例的顺序，
     * 可通过实现 {@link org.springframework.core.Ordered} 接口表达实例特定的顺序，
     * 或者使用一个默认顺序。
     * @see org.springframework.core.Ordered
     * @see #getOrderForAspectClass
     */
    @Override
    public int getOrder() {
        return getOrderForAspectClass(this.aspectClass);
    }

    /**
     * 确定在切面实例没有通过实现 {@link org.springframework.core.Ordered} 接口表达实例特定顺序时的后备顺序。
     * <p>默认实现简单地返回 {@code Ordered.LOWEST_PRECEDENCE}。
     * @param aspectClass 切面类
     */
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
```

**SingletonAspectInstanceFactory**

 `SingletonAspectInstanceFactory` 类是 `AspectInstanceFactory` 接口的实现。该类通过指定的单例对象作为后端支持，每次调用 `getAspectInstance()` 方法时都返回相同的实例。此外，它还提供了方法来获取切面实例的类加载器以及确定切面实例的顺序，支持实现了 `Ordered` 接口的切面实例。

```java
/**
 * {@link AspectInstanceFactory} 接口的实现类，由指定的单例对象支持，
 * 每次调用 {@link #getAspectInstance()} 方法时返回相同的实例。
 * 单例切面实例工厂。
 *
 * 由指定的单例对象支持，每次调用 getAspectInstance() 方法时返回相同的实例。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see SimpleAspectInstanceFactory
 */
@SuppressWarnings("serial")
public class SingletonAspectInstanceFactory implements AspectInstanceFactory, Serializable {

    // 单例切面实例
    private final Object aspectInstance;

    /**
     * 为给定的切面实例创建一个新的 SingletonAspectInstanceFactory。
     * @param aspectInstance 单例切面实例
     */
    public SingletonAspectInstanceFactory(Object aspectInstance) {
        Assert.notNull(aspectInstance, "Aspect instance must not be null");
        this.aspectInstance = aspectInstance;
    }

    @Override
    public final Object getAspectInstance() {
        // 返回单例切面实例
        return this.aspectInstance;
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        // 返回切面实例的类加载器
        return this.aspectInstance.getClass().getClassLoader();
    }

    /**
     * 确定此工厂的切面实例的顺序，
     * 可通过实现 {@link org.springframework.core.Ordered} 接口表达实例特定的顺序，
     * 或者使用一个默认顺序。
     * @see org.springframework.core.Ordered
     * @see #getOrderForAspectClass
     */
    @Override
    public int getOrder() {
        if (this.aspectInstance instanceof Ordered) {
            // 如果切面实例实现了 Ordered 接口，则返回其顺序
            return ((Ordered) this.aspectInstance).getOrder();
        }
        // 否则返回切面实例类的默认顺序
        return getOrderForAspectClass(this.aspectInstance.getClass());
    }

    /**
     * 确定在切面实例没有通过实现 {@link org.springframework.core.Ordered} 接口表达实例特定顺序时的后备顺序。
     * <p>默认实现简单地返回 {@code Ordered.LOWEST_PRECEDENCE}。
     * @param aspectClass 切面类
     */
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
```

**SimpleBeanFactoryAwareAspectInstanceFactory**

`SimpleBeanFactoryAwareAspectInstanceFactory` 类是 `AspectInstanceFactory` 接口的实现。该类通过配置的 bean 名称从 `BeanFactory` 中定位切面实例。每次调用 `getAspectInstance()` 方法时，都会查找并返回指定名称的 bean。此外，它还提供了方法来获取切面实例的类加载器以及确定切面实例的顺序，支持实现了 `Ordered` 接口的切面实例。

```java
/**
 * {@link AspectInstanceFactory} 接口的实现类，通过配置的 bean 名称从 {@link org.springframework.beans.factory.BeanFactory} 中定位切面。
 * SimpleBeanFactoryAwareAspectInstanceFactory 类。
 * 
 * 通过配置的 bean 名称从 BeanFactory 中定位切面。
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class SimpleBeanFactoryAwareAspectInstanceFactory implements AspectInstanceFactory, BeanFactoryAware {

    // 切面 bean 名称
    @Nullable
    private String aspectBeanName;

    // BeanFactory
    @Nullable
    private BeanFactory beanFactory;

    /**
     * 设置切面 bean 的名称。调用 {@link #getAspectInstance()} 时返回该 bean。
     * @param aspectBeanName 切面 bean 名称
     */
    public void setAspectBeanName(String aspectBeanName) {
        this.aspectBeanName = aspectBeanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Assert.notNull(this.aspectBeanName, "'aspectBeanName' is required");
    }

    /**
     * 从 BeanFactory 中查找切面 bean 并返回。
     * @see #setAspectBeanName
     */
    @Override
    public Object getAspectInstance() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        Assert.state(this.aspectBeanName != null, "No 'aspectBeanName' set");
        return this.beanFactory.getBean(this.aspectBeanName);
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader();
        } else {
            return ClassUtils.getDefaultClassLoader();
        }
    }

    @Override
    public int getOrder() {
        if (this.beanFactory != null && this.aspectBeanName != null &&
                this.beanFactory.isSingleton(this.aspectBeanName) &&
                this.beanFactory.isTypeMatch(this.aspectBeanName, Ordered.class)) {
            return ((Ordered) this.beanFactory.getBean(this.aspectBeanName)).getOrder();
        }
        return Ordered.LOWEST_PRECEDENCE;
    }
}
```
