## @DependsOn

- [@DependsOn](#dependson)
  - [一、基本信息](#一基本信息)
  - [二、注解描述](#二注解描述)
  - [三、注解源码](#三注解源码)
  - [四、主要功能](#四主要功能)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
    - [Bean注册时序图](#bean注册时序图)
    - [Bean创建时序图](#bean创建时序图)
    - [Bean销毁时序图](#bean销毁时序图)
  - [六、源码分析](#六源码分析)
    - [Bean注册源码分析](#bean注册源码分析)
    - [Bean创建源码分析](#bean创建源码分析)
    - [Bean销毁源码分析](#bean销毁源码分析)
  - [七、注意事项](#七注意事项)
  - [八、总结](#八总结)
    - [最佳实践总结](#最佳实践总结)
    - [源码分析总结](#源码分析总结)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133800615) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@DependsOn源码](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-dependsOn)

### 二、注解描述

`@DependsOn`注解，用于定义 Bean 初始化顺序。有时，我们可能会碰到某些 Bean 需要在其他 Bean 之前被初始化的情况。在这种情况下，我们可以使用 `@DependsOn` 注解来明确指定 Bean 的初始化顺序。

### 三、注解源码

`@DependsOn`注解是 Spring 框架自 3.0 版本开始引入的一个核心注解，其中`value`属性是 `@DependsOn` 注解的主要属性，它允许我们定义当前bean依赖的其他bean的名称。

```java
/**
 * 当前bean所依赖的其他bean。任何指定的bean都保证在这个bean之前被容器创建。
 * 在少数情况下使用，当一个bean不通过属性或构造函数参数明确地依赖于另一个bean，
 * 而是依赖于另一个bean的初始化的副作用时。
 *
 * depends-on 声明既可以指定初始化时的依赖，又可以在单例bean的情况下，指定对应的销毁时的依赖。
 * 定义了 depends-on 关系的依赖bean会首先被销毁，然后再销毁给定的bean。
 * 因此，depends-on 声明也可以控制关闭顺序。
 *
 * 可以在直接或间接使用 org.springframework.stereotype.Component 注解的任何类上，
 * 或在使用 Bean 注解的方法上使用。
 *
 * 在类级别使用 DependsOn 在未使用组件扫描的情况下不会产生任何效果。
 * 如果通过XML声明了使用 DependsOn 注解的类，DependsOn 注解的元数据会被忽略，
 * 而 <bean depends-on="..."/> 会被考虑。
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DependsOn {

    // 定义当前bean所依赖的其他bean的名称。
	String[] value() default {};

}
```

### 四、主要功能

1. **初始化顺序**
   + 使用 `@DependsOn` 可以确保某个或某些 bean 在当前 bean 之前被初始化。这在某个 bean 的初始化逻辑依赖于另一个 bean 的副作用时特别有用。
2. **销毁顺序（仅限单例 bean）**
   + 除了影响初始化顺序，`@DependsOn` 也会影响单例 bean 的销毁顺序。依赖关系中的 bean 会在它们所依赖的 bean 之前被销毁。
3. **指定多个依赖**
   + `@DependsOn` 允许我们指定多个依赖，这意味着我们可以确保多个 bean 都在当前 bean 之前被初始化。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，最后调用`context.close()`方法关闭容器。

```java
public class DependsOnApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
```

这里使用`@Bean`注解，定义了三个Bean，是为了确保`BeanA`，`BeanB`，`BeanC`被 Spring 容器执行，其中`BeanA`依赖于 `BeanB`，`BeanB`依赖于 `BeanC`，`BeanC`没有明确的依赖关系。

```java
@Configuration
public class MyConfiguration {

    @Bean
    @DependsOn("beanB")
    public BeanA beanA() {
        return new BeanA();
    }

    @Bean
    @DependsOn("beanC")
    public BeanB beanB() {
        return new BeanB();
    }

    @Bean
    public BeanC beanC() {
        return new BeanC();
    }
}
```

`BeanA`, `BeanB`, 和 `BeanC`，每一个都有各种的构造函数与实现 `DisposableBean` 接口，

```java
public class BeanA implements DisposableBean {

    public BeanA() {
        System.out.println("BeanA Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanA Destroyed");
    }
}

public class BeanB implements DisposableBean {

    public BeanB() {
        System.out.println("BeanB Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanB Destroyed");
    }
}

public class BeanC implements DisposableBean {

    public BeanC() {
        System.out.println("BeanC Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanC Destroyed");
    }
}
```

运行结果发现，通过 `@DependsOn` 注解和 `DisposableBean` 接口的 `destroy()` 方法，我们不仅可以控制 bean 的初始化顺序，还可以控制它们的销毁顺序。

```assembly
BeanC Initialized
BeanB Initialized
BeanA Initialized
BeanA Destroyed
BeanB Destroyed
BeanC Destroyed

PS
初始化的顺序为：
1. `BeanC` (`BeanC Initialized` 将会被打印)
2. `BeanB` (`BeanB Initialized` 将会被打印)
3. `BeanA` (`BeanA Initialized` 将会被打印)

当关闭 Spring 容器时，销毁的顺序是与初始化的顺序相反：
1. `BeanA` (`BeanA Destroyed` 将会被打印)
2. `BeanB` (`BeanB Destroyed` 将会被打印)
3. `BeanC` (`BeanC Destroyed` 将会被打印)
```

### 六、时序图

#### Bean注册时序图

~~~mermaid
sequenceDiagram 
DependsOnApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>启动上下文
AnnotationConfigApplicationContext-->>ConfigurationApplication: 返回context<br>返回上下文实例
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: register(componentClasses)<br>注册组件类
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: register(componentClasses)<br>读取器注册类
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: registerBean(beanClass)<br>注册Bean类
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: doRegisterBean(beanClass,name,qualifiers, supplier,customizers)<br>执行Bean注册
AnnotatedBeanDefinitionReader->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd)<br>处理通用定义注解
AnnotationConfigUtils-->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd,metadata)<br>解析DependsOn注解并存储在BeanDefinition中
AnnotatedBeanDefinitionReader->>BeanDefinitionReaderUtils: registerBeanDefinition(definitionHolder,registry)<br>注册Bean定义
BeanDefinitionReaderUtils->>DefaultListableBeanFactory: registerBeanDefinition(beanName,beanDefinition)<br>工厂存Bean定义
~~~

#### Bean创建时序图

~~~mermaid
sequenceDiagram 
DependsOnApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>创建应用上下文
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>刷新应用上下文
AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>完成bean工厂初始化
AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>预实例化单例beans
DefaultListableBeanFactory->>+AbstractBeanFactory:getBean(name)<br>获取bean实例
AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>具体获取bean逻辑
AbstractBeanFactory->>AbstractBeanDefinition:获取bean所依赖的bean名称
AbstractBeanDefinition->>AbstractBeanFactory:返回被依赖的bean名称
AbstractBeanFactory->>DefaultSingletonBeanRegistry:isDependent(beanName,dependentBeanName)<br>检查依赖关系
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:isDependent(beanName,dependentBeanName,alreadySeen)<br>检查依赖关系
DefaultSingletonBeanRegistry->>AbstractBeanFactory:返回是否存在依赖 true or false
AbstractBeanFactory->>-AbstractBeanFactory:getBean(name)<br>获取被依赖bean实例(递归)
~~~

#### Bean销毁时序图

~~~mermaid
sequenceDiagram
DisposableBeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses	)<br>应用开始初始化上下文
AnnotationConfigApplicationContext-->>DisposableBeanApplication:初始化完成
DisposableBeanApplication->>AbstractApplicationContext:close()<br>请求关闭上下文
AbstractApplicationContext->>AbstractApplicationContext:doClose()<br>执行关闭逻辑
AbstractApplicationContext->>AbstractApplicationContext:destroyBeans()<br>开始销毁beans
AbstractApplicationContext->>DefaultListableBeanFactory:destroySingletons()<br>销毁单例beans
DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingletons()<br>调父类销毁方法
DefaultSingletonBeanRegistry-->>DefaultListableBeanFactory:destroySingleton(beanName)<br>销毁单个bean
DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingleton(beanName)<br>调父类销毁bean方法
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:destroyBean(beanName,bean)<br>执行销毁bean操作
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:删除被依赖映射关系(dependentBeanMap)
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:删除依赖映射关系(dependenciesForBeanMap)
~~~

### 六、源码分析

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，最后调用`context.close()`方法关闭容器。

```java
public class DependsOnApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中，执行了三个步骤。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    // 步骤1. 这个构造方法初始化了基本的配置读取器和类路径扫描器
    this();
    // 步骤2. 这个方法将这些类注册到 Spring 上下文中，这样它们可以被识别并进一步处理。
    register(componentClasses);
    // 步骤3. 这个方法触发整个Spring容器的启动过程
    refresh();
}
```

#### Bean注册源码分析

首先我们来到`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中步骤2。在`org.springframework.context.annotation.AnnotationConfigApplicationContext#register`方法中，主要是允许我们注册一个或多个组件类（例如，那些使用 `@Component`, `@Service`, `@Repository`, `@Controller`, `@Configuration` 等注解的类）到Spring容器。

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

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`方法中，主要目的是为给定的 `beanClass` 创建一个 bean 定义，并根据提供的参数和注解对其进行配置。

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {
    
    // ... [代码部分省略以简化]
    
    // 获取合并后的本地bean定义（可能包括父bean定义中的属性，如果是子bean定义）
    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
    // 检查合并后的bean定义，确保它是有效的并且满足当前的创建需求
    checkMergedBeanDefinition(mbd, beanName, args);
    
    // 保证初始化当前bean所依赖的其他beans
    String[] dependsOn = mbd.getDependsOn();
    if (dependsOn != null) {
        for (String dep : dependsOn) {
            // 检查是否存在循环依赖，即当前bean也依赖于它自己
            if (isDependent(beanName, dep)) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                                "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
            }
            // 在bean之间注册依赖关系
            registerDependentBean(dep, beanName);
            try {
                // 尝试获取并初始化依赖的bean
                getBean(dep);
            }
            // 如果尝试获取依赖的bean失败，则抛出异常
            catch (NoSuchBeanDefinitionException ex) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                                "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
            }
        }
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#processCommonDefinitionAnnotations`方法中，主要目的是将具体的注解处理逻辑委托给另一个方法，以此来进行真正的配置工作。

```java
public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
    processCommonDefinitionAnnotations(abd, abd.getMetadata());
}
```

在`org.springframework.context.annotation.AnnotationConfigUtils#processCommonDefinitionAnnotations(abd,metadata)`方法中，使用 `attributesFor(metadata, DependsOn.class)` 方法从 `metadata` 中获取 `@DependsOn` 注解的属性。这可能会返回 `AnnotationAttributes` 对象，这个对象提供了方便的方法来访问注解的属性值。如果找到了 `@DependsOn` 注解（即 `dependsOn` 不为 `null`），则从该注解中获取 `value` 属性。这个属性是一个字符串数组，代表了其他 Bean 的名称，当前 Bean 依赖于这些名称。使用 `abd.setDependsOn()` 方法设置这个 Bean 依赖的其他 Bean 名称。

```java
static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
    // ... [代码部分省略以简化]
    AnnotationAttributes dependsOn = attributesFor(metadata, DependsOn.class);
    if (dependsOn != null) {
        abd.setDependsOn(dependsOn.getStringArray("value"));
    }
	// ... [代码部分省略以简化]
}
```

#### Bean创建源码分析

然后我们来到`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext`构造函数中步骤3。在`org.springframework.context.support.AbstractApplicationContext#refresh`方法中，我们重点关注一下`finishBeanFactoryInitialization(beanFactory)`这方法会对实例化所有剩余非懒加载的单列Bean对象，其他方法不是本次源码阅读的重点暂时忽略。

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [代码部分省略以简化]
    // 实例化所有剩余非懒加载的单列Bean对象
    finishBeanFactoryInitialization(beanFactory);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization`方法中，会继续调用`DefaultListableBeanFactory`类中的`preInstantiateSingletons`方法来完成所有剩余非懒加载的单列Bean对象。

```java
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

在`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`方法中，从 `BeanDefinition` 中，首先提取由 `@DependsOn` 注解定义的依赖关系，并将这些依赖存储在 `dependsOn` 字符串数组中。接着，系统会遍历当前 bean 的每一个依赖。通过使用 `isDependent(beanName, dep)` 方法，Spring 检查是否存在循环依赖。如果发现当前 bean 同时也是 `dep` bean 的依赖，那么 Spring 将抛出 `BeanCreationException`，因为这明确地表示了一个循环依赖。接着，系统使用 `registerDependentBean(dep, beanName)` 方法来通知 Spring 容器，表示 `beanName` 依赖于其他的 bean。最后，通过 `getBean(dep)` 方法，系统会尝试初始化并获取该依赖 bean 的实例。

```java
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {
    // ... [代码部分省略以简化]

    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
	checkMergedBeanDefinition(mbd, beanName, args);
    
    // Guarantee initialization of beans that the current bean depends on.
    String[] dependsOn = mbd.getDependsOn();
    if (dependsOn != null) {
        for (String dep : dependsOn) {
            if (isDependent(beanName, dep)) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                                "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
            }
            registerDependentBean(dep, beanName);
            try {
                getBean(dep);
            }
            catch (NoSuchBeanDefinitionException ex) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                                "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
            }
        }
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#isDependent`方法中，用于检查一个 bean 是否直接或间接地依赖于另一个 bean。

```java
protected boolean isDependent(String beanName, String dependentBeanName) {
    synchronized (this.dependentBeanMap) {
        return isDependent(beanName, dependentBeanName, null);
    }
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#isDependent(beanName, dependentBeanName, alreadySeen)`方法中，用于递归地检查一个 bean 是否依赖于另一个 bean。它通过跟踪直接和间接的依赖关系来实现这一点。

```java
private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
    // 检查bean是否已在已处理的bean集合中，如果是，则返回false，以避免无限递归
    if (alreadySeen != null && alreadySeen.contains(beanName)) {
        return false;
    }

    // 获取bean的规范名称（可能涉及转换或别名解析）
    String canonicalName = canonicalName(beanName);
    // 从依赖映射中获取bean的直接依赖
    Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
    // 如果bean没有任何直接依赖，则返回false
    if (dependentBeans == null) {
        return false;
    }

    // 如果bean的直接依赖包含目标依赖bean，则返回true
    if (dependentBeans.contains(dependentBeanName)) {
        return true;
    }

    // 递归检查bean的每一个直接依赖，看它们是否间接依赖于目标依赖bean
    for (String transitiveDependency : dependentBeans) {
        // 如果还没有创建已处理的bean集合，那么创建它
        if (alreadySeen == null) {
            alreadySeen = new HashSet<>();
        }
        // 将当前bean添加到已处理的bean集合中
        alreadySeen.add(beanName);
        // 递归检查
        if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
            return true;
        }
    }

    // 如果上述所有检查都未确认存在依赖关系，则返回false
    return false;
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerDependentBean`方法中，记录了两个 beans 之间的依赖关系，并确保了这种关系是双向的，即 A 依赖于 B，同时 B 被 A 依赖。这在解析和管理 bean 之间的复杂依赖关系时非常有用。

```java
public void registerDependentBean(String beanName, String dependentBeanName) {
    // 获取bean的规范名称（可能涉及转换或别名解析）
    String canonicalName = canonicalName(beanName);

    // 同步代码块，确保线程安全地更新bean的依赖映射
    synchronized (this.dependentBeanMap) {
        // 获取或创建bean的依赖集合
        Set<String> dependentBeans =
            this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
        // 如果依赖bean名尚未添加，则添加；否则直接返回
        if (!dependentBeans.add(dependentBeanName)) {
            return;
        }
    }

    // 同步代码块，确保线程安全地更新bean的被依赖映射（反向依赖）
    synchronized (this.dependenciesForBeanMap) {
        // 获取或创建被依赖bean的反向依赖集合
        Set<String> dependenciesForBean =
            this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
        // 添加反向依赖信息
        dependenciesForBean.add(canonicalName);
    }
}
```

#### Bean销毁源码分析

在`org.springframework.context.support.AbstractApplicationContext#close`方法中，首先是启动了一个同步块，它同步在 `startupShutdownMonitor` 对象上。这确保了在给定时刻只有一个线程可以执行这个块内的代码，防止多线程导致的资源竞争或数据不一致，然后是调用了 `doClose` 方法，最后是为 JVM 注册了一个关闭钩子。

```java
@Override
public void close() {
    synchronized (this.startupShutdownMonitor) {
        doClose();
        // ... [代码部分省略以简化]
    }
}
```

在`org.springframework.context.support.AbstractApplicationContext#doClose`方法中，又调用了 `destroyBeans` 方法。

```java
protected void doClose() {
    // ... [代码部分省略以简化]
    // Destroy all cached singletons in the context's BeanFactory.
    destroyBeans();
    // ... [代码部分省略以简化]
}
```

在`org.springframework.context.support.AbstractApplicationContext#destroyBeans`方法中，首先是调用了`getBeanFactory()`返回 Spring 的 `BeanFactory` ，然后在获得的 `BeanFactory` 上，调用了 `destroySingletons` 方法，这个方法的目的是销毁所有在 `BeanFactory` 中缓存的单例 beans。

```java
protected void destroyBeans() {
    getBeanFactory().destroySingletons();
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingletons`方法中，首先是调用了父类的 `destroySingletons` 方法，为了确保继承自父类的销毁逻辑得到了执行。

```java
@Override
public void destroySingletons() {
    super.destroySingletons();
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingletons`方法中，首先是在`disposableBeans` 字段上，从其键集合中获取所有的 bean 名称，并将它们转换为一个字符串数组。`disposableBeans` 可能包含了实现了 `DisposableBean` 接口的 beans，这些 beans 需要在容器销毁时特殊处理，最后倒序循环，从最后一个开始，销毁所有在 `disposableBeans` 列表中的 beans。这样做是为了确保依赖关系正确地处理，beans先被创建的应该后被销毁。

```java
public void destroySingletons() {
    // ... [代码部分省略以简化]
    String[] disposableBeanNames;
    synchronized (this.disposableBeans) {
        disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
    }
    for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
        destroySingleton(disposableBeanNames[i]);
    }
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingleton`方法中，首先是调用了父类的 `destroySingleton` 方法，为了确保继承自父类的销毁逻辑得到了执行。

```java
@Override
public void destroySingleton(String beanName) {
    super.destroySingleton(beanName);
    // ... [代码部分省略以简化]
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton`方法中，首先是使用 `synchronized` 关键字在 `disposableBeans` 对象上进行同步，以确保在多线程环境中安全地访问和修改它，从 `disposableBeans` 集合中移除指定名称的 bean，并将其转换为 `DisposableBean` 类型，最后调用`destroyBean`方法执行实际的销毁操作。

```java
public void destroySingleton(String beanName) {
    // ... [代码部分省略以简化]
    destroyBean(beanName, disposableBean);
}
```

在`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean`方法中， bean 从所有依赖它的其他 beans 的依赖列表中被移除，并且这个 bean 的所有已准备的依赖信息也被删除。

```java
protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
    // ... [代码部分省略以简化]
    
    // 从其他beans的依赖中移除已销毁的bean
    synchronized (this.dependentBeanMap) {
        // 遍历存储依赖关系的map
        for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Set<String>> entry = it.next();
            // 获取当前bean的依赖列表
            Set<String> dependenciesToClean = entry.getValue();
            // 从依赖列表中移除已销毁的bean
            dependenciesToClean.remove(beanName);
            // 如果当前bean的依赖列表为空，则从map中移除该条目
            if (dependenciesToClean.isEmpty()) {
                it.remove();
            }
        }
    }

    // 移除已销毁bean的已准备的依赖信息
    this.dependenciesForBeanMap.remove(beanName);
}
```

### 七、注意事项

1. **避免循环依赖**
   + 确保不创建一个循环依赖的场景，即 Bean A 依赖 Bean B，同时 Bean B 又依赖 Bean A。这会导致 Spring 容器无法成功初始化这两个 beans。
2. **不要过度使用**
   + 只在真正需要控制初始化顺序时使用这个注解。过度使用可能使代码更难理解和维护。
3. **与构造器/属性注入结合使用**
   + 即使我们使用了 `@DependsOn`，如果一个 bean 需要另一个 bean 作为构造函数参数或属性，我们还是应该使用 `@Autowired` 或 XML 配置进行注入。
4. **销毁顺序**
   + `@DependsOn` 也会影响 beans 的销毁顺序。如果 Bean A 依赖于 Bean B，那么在销毁时，Bean A 会在 Bean B 之后被销毁。
5. **不是为运行时依赖**
   + 请注意，`@DependsOn` 只确保初始化顺序。如果我们的 bean 在运行时需要另一个 bean，那么我们应该考虑其他方法，如注入。
6. **与 `@Lazy` 结合使用**
   + 如果我们的 bean 使用了 `@Lazy` 注解（表示它会延迟初始化），同时又用 `@DependsOn` 指定了依赖，那么这可能会导致意外的初始化顺序，因为延迟初始化的 bean 可能不会按预期的顺序被初始化。
7. **组件扫描与显式声明**
   + 如果我们使用组件扫描（通过 `@ComponentScan`）并且在类级别使用了 `@DependsOn`，那么这个注解会生效。但如果通过 XML 定义了该 bean，并且还在类上使用了 `@DependsOn`，那么注解会被忽略，我们应该使用 XML 的 `depends-on` 属性来声明依赖。
8. **不适用于 `@Bean` 方法的参数**
   + 如果我们在 Java 配置类中使用 `@Bean` 方法定义 beans，并尝试通过方法参数注入依赖，那么 `@DependsOn` 不会对这些依赖产生影响，因为方法参数自然地声明了初始化顺序。

### 八、总结

#### 最佳实践总结

1. **启动类设置**
   + 我们使用 `AnnotationConfigApplicationContext` 来启动 Spring 容器，并指定了 `MyConfiguration` 作为配置类。当程序运行完毕，我们关闭了该容器。
2. **配置类与依赖声明**
   + 在 `MyConfiguration` 配置类中，我们使用 `@Bean` 注解定义了三个 bean：`BeanA`, `BeanB`, 和 `BeanC`。通过 `@DependsOn` 注解，我们明确地指定了它们之间的依赖关系，确保 `BeanA` 依赖于 `BeanB` 的初始化，而 `BeanB` 依赖于 `BeanC` 的初始化。
3. **Bean的声明与销毁逻辑**
   + 每个 bean 都实现了 `DisposableBean` 接口。在各自的构造函数中，它们打印一个消息表示它们已经被初始化，而在 `destroy` 方法中，它们打印一个消息表示它们已经被销毁。
4. **结果与结论**
   + 我们运行程序时，初始化的顺序遵循了我们通过 `@DependsOn` 注解定义的依赖关系。同样地，销毁的顺序与初始化顺序相反，这确保了所有的依赖都在被依赖的 bean 之前被销毁。

#### 源码分析总结

1. **启动和注册**
   + 使用 `AnnotationConfigApplicationContext` 启动 Spring 容器，并将配置类注册到 Spring 上下文中。
2. **Bean 注册分析**
   - 在 `AnnotationConfigApplicationContext` 构造函数中，执行了注册和启动容器的两个关键步骤。
   - `register` 方法允许我们将组件类（如使用 `@Component` 或 `@Configuration` 注解的类）注册到 Spring 容器。
   - `AnnotatedBeanDefinitionReader` 负责注册这些类，然后在 `doRegisterBean` 方法中为给定的 `beanClass` 创建一个 bean 定义并配置它。
3. **处理 @DependsOn 注解**
   - 在 bean 的定义过程中，Spring 将解析 `@DependsOn` 注解并存储其依赖关系。
   - 这些关系将在后面的 bean 生命周期中使用，以确保按正确的顺序创建和销毁 beans。
4. **Bean 创建分析**
   - 在容器启动过程的 `refresh` 方法中，会实例化所有的单例 beans。
   - `preInstantiateSingletons` 方法会触发所有非懒加载单例 beans 的创建过程。
   - 如果一个 bean 通过 `@DependsOn` 指定了依赖，这些依赖会首先被初始化。
5. **Bean 销毁分析**
   - 在容器关闭时，会调用 `destroySingletons` 方法来销毁所有缓存的单例 beans。
   - Beans 的销毁顺序与其创建顺序相反，以确保所有依赖项在销毁过程中得到正确的处理。
6. **处理循环依赖**
   - Spring 会检查 `@DependsOn` 指定的依赖是否导致了循环依赖，如果是这种情况，Spring 会抛出异常。