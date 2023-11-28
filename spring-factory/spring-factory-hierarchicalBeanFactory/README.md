## HierarchicalBeanFactory


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`HierarchicalBeanFactory` 接口是Spring框架中的一个接口，用于表示具有层次结构的`BeanFactory`，即支持父子容器的概念。通过继承 `BeanFactory` 接口，它定义了一些方法，使得容器能够组织成为层次结构，其中子容器可以访问父容器的 Bean 定义。

### 三、主要功能

1. **获取父容器**

   - 通过 `getParentBeanFactory()` 方法，可以获取当前 `BeanFactory` 的父级 `BeanFactory`，允许在子容器中访问和使用父容器中定义的 Bean。

2. **本地 Bean 的检查**

   - 通过 `containsLocalBean(String name)` 方法，可以检查当前 `BeanFactory` 是否包含具有给定名称的本地 Bean。本地 Bean 是指在当前容器中定义的 Bean，而不是从父容器继承的。

### 四、接口源码

`HierarchicalBeanFactory` 接口是由能够成为Spring容器层次结构一部分的Bean工厂实现的子接口。它定义了获取父级Bean工厂和检查本地Bean工厂是否包含指定名称的Bean的方法

```java
/**
 * 由能够成为层次结构一部分的Bean工厂实现的子接口。
 *
 * <p>对于那些允许以可配置的方式设置父级的Bean工厂，
 * 相应的 {@code setParentBeanFactory} 方法可以在 ConfigurableBeanFactory 接口中找到。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 07.07.2003
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
 */
public interface HierarchicalBeanFactory extends BeanFactory {

    /**
     * 返回父级Bean工厂，如果没有则返回 {@code null}。
     */
    @Nullable
    BeanFactory getParentBeanFactory();

    /**
     * 返回本地Bean工厂是否包含给定名称的Bean，
     * 忽略在祖先上下文中定义的Bean。
     * <p>这是 {@code containsBean} 的替代方法，忽略来自祖先Bean工厂的给定名称的Bean。
     * @param name 要查询的Bean的名称
     * @return 本地工厂中是否定义了具有给定名称的Bean
     * @see BeanFactory#containsBean
     */
    boolean containsLocalBean(String name);
}
```

### 五、主要实现

- `DefaultListableBeanFactory`
  - `DefaultListableBeanFactory`是Spring框架中实现`BeanFactory`接口的关键类之一，负责注册、管理和初始化应用程序中的所有Bean定义。它支持依赖注入、不同作用域的Bean管理、处理`FactoryBean`、层次性容器、以及各种生命周期回调等功能，是Spring IoC容器的核心实现，提供了灵活而强大的Bean管理和配置机制。

### 六、最佳实践

创建了一个包含父子层次结构的Spring容器，其中父容器包含一个名为`MyBean`的Bean，子容器继承了父容器并尝试获取这个Bean。代码通过`HierarchicalBeanFactory`接口的方法展示了如何在子容器中访问父容器的Bean，判断本地和整个BeanFactory中是否包含特定名称的Bean，并获取父级BeanFactory。

```java
public class HierarchicalBeanFactoryDemo {

    public static void main(String[] args) {
        // 创建父级容器
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(MyBean.class);
        // 创建子级容器
        AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setParent(parentContext);

        // 在子级 BeanFactory 中获取 bean
        HierarchicalBeanFactory childHierarchicalBeanFactory = childContext.getBeanFactory();
        System.out.println("在子级BeanFactory中获取Bean: " + childHierarchicalBeanFactory.getBean(MyBean.class));

        // 在父级 BeanFactory 中获取 bean
        HierarchicalBeanFactory parentHierarchicalBeanFactory = parentContext.getBeanFactory();
        System.out.println("在父级BeanFactory中获取Bean: " + parentHierarchicalBeanFactory.getBean(MyBean.class));

        // 获取父级 BeanFactory
        BeanFactory parentBeanFactory = childHierarchicalBeanFactory.getParentBeanFactory();
        System.out.println("获取父级BeanFactory: " + parentBeanFactory);

        // 判断本地 BeanFactory 是否包含指定名称的 bean
        boolean containsLocalBean = childHierarchicalBeanFactory.containsLocalBean("myBean");
        System.out.println("判断本地BeanFactory是否包含指定名称的Bean: " + containsLocalBean);

        // 判断整个 BeanFactory 是否包含指定名称的 bean
        boolean containsBean = childHierarchicalBeanFactory.containsBean("myBean");
        System.out.println("判断整个BeanFactory是否包含指定名称的Bean: " + containsBean);
    }
}
```

运行结果发现，容器层次结构的特性，包括父子容器之间的Bean继承和在不同层次的容器中判断Bean的存在。

```java
在子级BeanFactory中获取Bean: com.xcs.spring.bean.MyBean@6379eb
在父级BeanFactory中获取Bean: com.xcs.spring.bean.MyBean@6379eb
获取父级BeanFactory: org.springframework.beans.factory.support.DefaultListableBeanFactory@2f112965: defining beans [org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.event.internalEventListenerProcessor,org.springframework.context.event.internalEventListenerFactory,myBean]; root of factory hierarchy
判断本地BeanFactory是否包含指定名称的Bean: false
判断整个BeanFactory是否包含指定名称的Bean: true
```

### 七、与其他组件的关系

1. **ApplicationContext接口**

   - `HierarchicalBeanFactory` 接口是 `ApplicationContext` 接口的子接口。因此，任何实现了 `HierarchicalBeanFactory` 的类也是 `ApplicationContext` 的子类。`ApplicationContext` 是 Spring 中应用程序上下文的核心接口，提供了更多的功能，包括事件发布、国际化支持等。

2. **ConfigurableApplicationContext接口**

   - `ConfigurableApplicationContext` 是 `ApplicationContext` 的子接口，同时也扩展了 `HierarchicalBeanFactory`。它定义了一些用于配置应用程序上下文的额外方法，例如设置父级上下文、激活和取消激活配置文件等。

3. **BeanFactory接口**

   - `HierarchicalBeanFactory` 扩展了 `BeanFactory` 接口，因此它继承了 `BeanFactory` 中定义的许多方法，用于获取和管理 Bean 实例。

4. **DefaultListableBeanFactory类**

   - `DefaultListableBeanFactory` 是 Spring 框架中 `HierarchicalBeanFactory` 接口的默认实现类。它实现了 `HierarchicalBeanFactory` 接口，并提供了标准的 `BeanFactory` 功能。`DefaultListableBeanFactory` 也是 `ConfigurableListableBeanFactory` 接口的实现类，进一步增强了配置的能力。

5. **BeanDefinition接口**

   - `HierarchicalBeanFactory` 与 `BeanDefinition` 接口密切相关。`BeanDefinition` 定义了 Bean 的元数据，包括类名、属性值、构造函数参数等。在容器层次结构中，`HierarchicalBeanFactory` 负责管理和维护这些 `BeanDefinition`。

6. **ApplicationContext层次结构**

   - `HierarchicalBeanFactory` 接口支持 Spring 容器的层次结构。通过父子关系，容器可以继承和覆盖 Bean 定义。这有助于实现模块化和组织化的应用程序架构，不同层次的容器之间可以共享或隔离 Bean。

### 八、常见问题

1. **Bean名称冲突**

   - 当子容器和父容器中都定义了相同名称的Bean时，可能会导致Bean名称冲突。在设计时，避免在父子容器中定义相同名称的Bean。如果冲突是不可避免的，可以通过在子容器中重新定义Bean来覆盖父容器中的定义。

2. **父子容器的初始化顺序**

   - 容器的初始化顺序可能会导致在子容器初始化之前尝试访问父容器中的Bean。我们需要确保在访问子容器中的Bean之前，子容器已经成功初始化。可以使用事件监听器等机制来确保容器的正确初始化顺序。

3. **父容器中Bean的更新**

   - 子容器在初始化时会获取父容器中的Bean定义，并且在后续运行时不会再次更新。如果需要动态更新父容器中的Bean定义，可以考虑在特定的时机重新刷新子容器。

4. **对父容器中Bean的引用**

   - 子容器中的Bean可以直接引用父容器中的Bean。Spring会自动处理这些引用，确保在子容器中正确注入父容器中的Bean。

5. **层次结构的深度**

   -  尽管Spring支持容器层次结构，但在设计时要考虑层次结构的深度。深层次结构可能会导致容器初始化和访问的复杂性，可能影响性能。

6. **配置错误**

   -  定期审查配置，确保正确设置了父子容器关系。使用合适的配置和注解来避免潜在的错误。