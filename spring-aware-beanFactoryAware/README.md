## BeanFactoryAware

- [BeanFactoryAware](#beanfactoryaware)
  - [一、接口描述](#一接口描述)
  - [二、接口源码](#二接口源码)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [8.1、最佳实践总结](#81最佳实践总结)
    - [8.2、源码分析总结](#82源码分析总结)

### 一、接口描述

`BeanFactoryAware` 接口，允许 Spring bean 获得其所在的 `BeanFactory` 的引用。当一个 bean 实现了这个接口，Spring 容器在初始化该 bean 时，会自动调用 `setBeanFactory()` 方法，并传递一个 `BeanFactory` 实例。

### 二、接口源码

`BeanFactoryAware` 是 Spring 框架自 11.03.2003 开始引入的一个核心接口。允许 Spring beans 获知并与其所在的 `BeanFactory` 进行交互。这为 beans 提供了直接访问 `BeanFactory` 的能力，进而可以查询和交互其他的 beans。

```java
/**
 * 由希望知道其所属的 BeanFactory 的 beans 实现的接口。
 *
 * 例如，beans 可以通过工厂查找合作的 beans（依赖查找）。
 * 请注意，大多数 beans 会选择通过相应的 bean 属性或构造函数参数
 * 接收对合作 beans 的引用（依赖注入）。
 *
 * 有关所有 bean 生命周期方法的列表，请参阅
 * BeanFactory BeanFactory javadocs。
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @since 11.03.2003
 * @see BeanNameAware
 * @see BeanClassLoaderAware
 * @see InitializingBean
 * @see org.springframework.context.ApplicationContextAware
 */
public interface BeanFactoryAware extends Aware {

	/**
	 * 向 bean 实例提供其拥有的工厂的回调。
	 * 在正常 bean 属性填充之后但在初始化回调之前（如
	 * InitializingBean#afterPropertiesSet() 或自定义的初始化方法）调用。
	 * @param beanFactory 拥有的 BeanFactory（永远不会是 null）。
	 * bean 可以立即调用工厂上的方法。
	 * @throws BeansException 初始化错误的情况下
	 * @see BeanInitializationException
	 */
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}
```

### 三、主要功能

**获取 `BeanFactory` 引用**： 通过实现 `BeanFactoryAware` 接口并重写 `setBeanFactory` 方法，bean 在初始化过程中会收到其所属的 `BeanFactory` 的引用。Spring 容器会自动为实现了该接口的 bean 调用 `setBeanFactory` 方法。

**依赖查找**： 一旦 bean 有了 `BeanFactory` 的引用，它就可以使用这个工厂来动态地查找其他 beans。这种方式被称为“依赖查找”（Dependency Lookup），与常见的“依赖注入”（Dependency Injection）方式相对。

**与 `BeanFactory` 进行交互**： 获取 `BeanFactory` 的引用不仅仅是为了查找其他 beans，bean 还可以与其所在的 `BeanFactory` 进行更广泛的互动，例如检查 bean 的作用域、检查 bean 是否为单例、或获取 bean 的别名等。

### 四、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`UserService`类型的bean，最后调用`validateUser`方法。

```java
public class BeanNameAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        UserService userService = context.getBean(UserService.class);
        userService.validateUser("root", "123456");
    }
}
```

使用`@ComponentScan`注解，告诉 Spring 容器去 "`com.xcs.spring.validate`" "`com.xcs.spring.service`"扫描包及其子包

```java
@Configuration
@ComponentScan({"com.xcs.spring.validate", "com.xcs.spring.service"})
public class MyConfiguration {

}
```

**`UserValidator`**是一个简单的验证器接口，具有一个方法 `validate`，用于验证用户名和密码是否有效。SimpleUserValidator是一个实现。它进行简单的验证，仅检查用户名和密码是否为非空。**`ComplexUserValidator` 是 `UserValidator` 接口的另一个实现。这个验证器有点复杂，除了检查用户名和密码是否为空外，还检查用户名的长度是否大于 5 以及密码的长度是否大于 8。

```java
public interface UserValidator {
    boolean validate(String username, String password);
}

@Component("simpleUserValidator")
public class SimpleUserValidator implements UserValidator {
    @Override
    public boolean validate(String username, String password) {
        System.out.println("使用SimpleUserValidator");
        return username != null && password != null;
    }
}

@Component("complexUserValidator")
public class ComplexUserValidator implements UserValidator {
    @Override
    public boolean validate(String username, String password) {
        System.out.println("使用ComplexUserValidator");
        return username != null && username.length() > 5 && password != null && password.length() > 8;
    }
}
```

`UserService` 类利用了 Spring 的 `BeanFactoryAware` 和 `InitializingBean` 接口，动态地选择了一个验证器。这种设计提供了极大的灵活性，允许 `UserService` 根据不同的配置或条件使用不同的验证方法。这也意味着在未来，如果需要添加更多的验证方法，只需简单地添加新的验证器实现，然后在 `someConfigurationMethod()` 中进行相应的调整。

```java
@Service
public class UserService implements BeanFactoryAware, InitializingBean {

    private BeanFactory beanFactory;
    private UserValidator userValidator;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (someConfigurationMethod()) {
            userValidator = beanFactory.getBean("simpleUserValidator", UserValidator.class);
        } else {
            userValidator = beanFactory.getBean("complexUserValidator", UserValidator.class);
        }
    }

    public void validateUser(String username, String password) {
        boolean success = userValidator.validate(username, password);
        if (success){
            System.out.println("验证账号密码成功");
        }else{
            System.out.println("验证账号密码失败");
        }
    }

    private boolean someConfigurationMethod() {
        return true;
    }
}
```

运行结果发现，使用了`SimpleUserValidator`来验证账号密码，并且验证成功。

```java
使用SimpleUserValidator
验证账号密码成功
```

### 五、时序图

~~~mermaid
sequenceDiagram
    Title: BeanNameAware时序图
    participant InitializingBeanApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant UserService
    
    InitializingBeanApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建上下文
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新上下文
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>初始化Bean工厂
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>实例化单例
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>获取Bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>执行获取Bean
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>获取单例Bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>获取Bean实例
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>创建Bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>执行Bean创建
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName,bean,mbd)<br>负责bean的初始化
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeAwareMethods(beanName, bean)<br>调用Aware方法
    AbstractAutowireCapableBeanFactory->>UserService:setBeanFactory(beanFactory)<br>设置beanFactory
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:返回Bean对象
    AbstractBeanFactory-->>DefaultListableBeanFactory:返回Bean对象
    AnnotationConfigApplicationContext-->>InitializingBeanApplication:初始化完成

~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`UserService`类型的bean，最后调用`validateUser`方法。

```java
public class BeanNameAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        UserService userService = context.getBean(UserService.class);
        userService.validateUser("root", "123456");
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤，我们重点关注`refresh()`方法

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

在`org.springframework.context.support.AbstractApplicationContext#refresh`方法中我们重点关注一下`finishBeanFactoryInitialization(beanFactory)`这方法会对实例化所有剩余非懒加载的单列Bean对象，其他方法不是本次源码阅读的重点暂时忽略。

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [代码部分省略以简化]
    // Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization`方法中，会继续调用`DefaultListableBeanFactory`类中的`preInstantiateSingletons`方法来完成所有剩余非懒加载的单列Bean对象。

```java
/**
 * 完成此工厂的bean初始化，实例化所有剩余的非延迟初始化单例bean。
 * 
 * @param beanFactory 要初始化的bean工厂
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [代码部分省略以简化]
    // 完成所有剩余非懒加载的单列Bean对象。
    beanFactory.preInstantiateSingletons();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons`方法中，主要的核心目的是预先实例化所有非懒加载的单例bean。在Spring的上下文初始化完成后，该方法会被触发，以确保所有单例bean都被正确地创建并初始化。其中`getBean(beanName)`是此方法的核心操作。对于容器中定义的每一个单例bean，它都会调用`getBean`方法，这将触发bean的实例化、初始化及其依赖的注入。如果bean之前没有被创建过，那么这个调用会导致其被实例化和初始化。

```java
public void preInstantiateSingletons() throws BeansException {
    // ... [代码部分省略以简化]
    // 循环遍历所有bean的名称
    for (String beanName : beanNames) {
        getBean(beanName);
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#getBean()`方法中，又调用了`doGetBean`方法来实际执行创建Bean的过程，传递给它bean的名称和一些其他默认的参数值。此处，`doGetBean`负责大部分工作，如查找bean定义、创建bean（如果尚未创建）、处理依赖关系等。

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中，首先检查所请求的bean是否是一个单例并且已经创建。如果尚未创建，它将创建一个新的实例。在这个过程中，它处理可能的异常情况，如循环引用，并确保返回的bean是正确的类型。这是Spring容器bean生命周期管理的核心部分。

```java
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {
    // ... [代码部分省略以简化]

    // 开始创建bean实例
    if (mbd.isSingleton()) {
        // 如果bean是单例的，我们会尝试从单例缓存中获取它
        // 如果不存在，则使用lambda创建一个新的实例
        sharedInstance = getSingleton(beanName, () -> {
            try {
                // 尝试创建bean实例
                return createBean(beanName, mbd, args);
            }
            catch (BeansException ex) {
                // ... [代码部分省略以简化]
            }
        });
        // 对于某些bean（例如FactoryBeans），可能需要进一步处理以获取真正的bean实例
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [代码部分省略以简化]

    // 确保返回的bean实例与请求的类型匹配
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()`方法中，主要负责从单例缓存中获取一个已存在的bean实例，或者使用提供的`ObjectFactory`创建一个新的实例。这是确保bean在Spring容器中作为单例存在的关键部分。

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // 断言bean名称不能为空
    Assert.notNull(beanName, "Bean name must not be null");

    // 同步访问单例对象缓存，确保线程安全
    synchronized (this.singletonObjects) {
        // 从缓存中获取单例对象
        Object singletonObject = this.singletonObjects.get(beanName);

        // 如果缓存中没有找到
        if (singletonObject == null) {
            // ... [代码部分省略以简化]

            try {
                // 使用工厂创建新的单例实例
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            catch (IllegalStateException ex) {
                // ... [代码部分省略以简化]
            }
            catch (BeanCreationException ex) {
                // ... [代码部分省略以简化]
            }
            finally {
                // ... [代码部分省略以简化]
            }

            // ... [代码部分省略以简化]
        }

        // 返回单例对象
        return singletonObject;
    }
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()`方法中，主要的逻辑是调用 `doCreateBean`，这是真正进行 bean 实例化、属性填充和初始化的地方。这个方法会返回新创建的 bean 实例。

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [代码部分省略以简化]
    
    try {
        // 正常的bean实例化、属性注入和初始化。
        // 这里是真正进行bean创建的部分。
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        // 记录bean成功创建的日志
        if (logger.isTraceEnabled()) {
            logger.trace("Finished creating instance of bean '" + beanName + "'");
        }
        return beanInstance;
    }
    catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
        // ... [代码部分省略以简化]
    }
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`方法中，`initializeBean`方法是bean初始化，确保bean是完全配置和准备好的。

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // 声明一个对象，后续可能用于存放初始化后的bean或它的代理对象
    Object exposedObject = bean;

    // ... [代码部分省略以简化]
    
    try {
        // ... [代码部分省略以简化]
        
        // bean初始化
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    } 
    catch (Throwable ex) {
        // ... [代码部分省略以简化]
    }

    // 返回创建和初始化后的bean
    return exposedObject;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`方法中，`invokeAwareMethods(beanName, bean)`是一个非常重要的步骤。这个方法是为了处理实现了Spring的`Aware`接口族的Beans（例如`BeanNameAware`, `BeanFactoryAware`等）。如果提供的bean实现了任何这些接口，该方法会回调相应的`Aware`方法。

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [代码部分省略以简化]
    
    invokeAwareMethods(beanName, bean);
    
    // ... [代码部分省略以简化]

    return wrappedBean;
}
```

在`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeAwareMethods`方法中，用于处理实现了 Spring `Aware` 接口族的 beans。当一个 bean 实现了如 `BeanNameAware`、`BeanClassLoaderAware` 或 `BeanFactoryAware` 等接口时，此方法确保正确的回调方法被调用，从而为 bean 提供关于其运行环境或其他相关信息。

```java
private void invokeAwareMethods(String beanName, Object bean) {
    if (bean instanceof Aware) {
        if (bean instanceof BeanNameAware) {
            // ... [代码部分省略以简化]
        }
        if (bean instanceof BeanClassLoaderAware) {
            // ... [代码部分省略以简化]
        }
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
        }
    }
}
```

最后执行到我们自定义的逻辑中，容器将调用 `setBeanFactory()` 方法，并将当前的 bean factory 实例作为参数传递。

```java
@Service
public class UserService implements BeanFactoryAware, InitializingBean {

    private BeanFactory beanFactory;
    private UserValidator userValidator;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // ... [代码部分省略以简化]
    }

    public void validateUser(String username, String password) {
        // ... [代码部分省略以简化]
    }

    private boolean someConfigurationMethod() {
        return true;
    }
}
```

### 七、注意事项

**生命周期时机**：`setBeanFactory` 方法是在 bean 属性设置之后但在其他初始化方法（如 `@PostConstruct`、`InitializingBean#afterPropertiesSet` 或指定的初始化方法）之前调用的。

**避免循环依赖**：当 beans 通过 `BeanFactory` 查找其他 beans 时，可能会出现循环依赖的情况。例如，bean A 在其 `setBeanFactory` 方法中查找 bean B，而 bean B 在其 `setBeanFactory` 方法中查找 bean A。这种情况会导致容器初始化失败。

**知道 bean 的作用域**：当从 `BeanFactory` 获取 beans 时，请记住 bean 的作用域。如果 bean 是原型作用域的，每次 `getBean` 调用都会返回一个新的实例。

**不要过度自定义**：除非有很好的理由，否则应避免在 `setBeanFactory` 方法中执行大量的自定义逻辑。这会使 bean 的初始化过程变得复杂，并可能导致不可预见的副作用。

### 八、总结

#### 8.1、最佳实践总结

**构建与配置**：在 `BeanNameAwareApplication` 启动类中，使用了 `AnnotationConfigApplicationContext` 来基于 Java 配置类 (`MyConfiguration`) 初始化 Spring 上下文。这是一个 Java-based 的配置方法，与传统的 XML-based 配置相比，更加直观和灵活。

**组件扫描**：`MyConfiguration` 配置类使用 `@ComponentScan` 注解指定了需要被扫描的包路径。Spring 容器会自动扫描这些包以及其子包下的组件，并将它们注册为 Spring beans。

**验证器设计**：我们设计了一个 `UserValidator` 接口，以及两个实现该接口的类：`SimpleUserValidator` 和 `ComplexUserValidator`。这两个验证器具有不同的验证逻辑，以满足不同的验证需求。

**动态选择验证器**：`UserService` 类是此应用的核心，它根据某些配置动态地从 `BeanFactory` 中选择一个验证器。这是通过实现 `BeanFactoryAware` 和 `InitializingBean` 接口来完成的：`BeanFactoryAware` 允许 `UserService` 访问 Spring 容器的 `BeanFactory`。InitializingBean` 确保在所有属性（例如依赖注入）设置完毕后，选择合适的验证器。

**运行与输出**：当调用 `validateUser` 方法验证用户名和密码时，根据所选择的验证器（在此示例中是 `SimpleUserValidator`），将输出相应的验证信息。此外，验证器本身也输出了它正在使用的验证方法。

#### 8.2、源码分析总结

**应用启动与上下文初始化**: 当启动类 `BeanNameAwareApplication` 被执行，一个新的 `AnnotationConfigApplicationContext` 被创建并初始化，其中传入了配置类 `MyConfiguration`。

**配置类与组件扫描**: `MyConfiguration` 是一个 Java 配置类，它告诉 Spring 容器去扫描特定的包以查找组件。

**单例bean的预实例化**: 在上下文的 `refresh()` 方法中，Spring 会预先实例化所有非懒加载的单例bean。这意味着在容器启动时，这些bean会被初始化。

**Bean的实例化和初始化**: 在上下文刷新的过程中，Spring 容器会逐个创建并初始化所有的单例bean。`doCreateBean` 方法负责实例化bean、注入依赖、并调用任何初始化方法。

**处理 Aware 接口**: 对于实现了 `Aware` 接口的bean，如 `BeanFactoryAware`，在初始化过程中，Spring 容器会调用相应的 `Aware` 方法（例如，`setBeanFactory`）。这使得bean可以获得关于其运行环境的信息或其他 Spring 功能。

**自定义逻辑执行**: 一旦bean被初始化，并且所有的 `Aware` 方法都被调用，就可以执行自定义逻辑。在这个例子中，这是通过 `UserService` 的 `validateUser` 方法来完成的。

**BeanFactoryAware 的特性**: 通过实现 `BeanFactoryAware`，`UserService` 能够获得对 `BeanFactory` 的访问权限。这使得它可以在运行时动态地从 `BeanFactory` 中获取bean，如在示例中的 `UserValidator`。