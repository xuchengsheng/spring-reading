## AnnotationAwareAspectJAutoProxyCreator

- [AnnotationAwareAspectJAutoProxyCreator](#annotationawareaspectjautoproxycreator)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、类关系图](#四类关系图)
  - [五、最佳实践](#五最佳实践)
  - [六、时序图](#六时序图)
  - [七、源码分析](#七源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`AnnotationAwareAspectJAutoProxyCreator`是Spring AOP中的关键类，负责自动检测标记有`@Aspect`注解的切面类，并将其代理到Spring应用程序的bean中，实现切面逻辑的自动织入，从而支持注解驱动的面向切面编程。

### 三、主要功能

1. **自动代理创建** 

   + 检测应用程序上下文中被`@Aspect`注解标记的切面类，并自动创建代理对象，使切面逻辑能够在目标bean的方法调用中被织入。

2. **切面逻辑织入** 

   + 将切面逻辑织入到目标bean的方法调用中，实现例如在方法执行前后、异常抛出时等的切面操作，以实现各种横切关注点的功能。

3. **支持注解驱动的切面编程** 

   + 提供了基于注解的切面编程的支持，通过`@Aspect`等注解，开发者能够更便捷地定义切面类和切面逻辑。

4. **灵活的切面配置** 

   + 允许我们通过注解方式配置切面，而不需要在XML配置文件中显式声明，从而使切面的配置更加灵活和便捷。
   
5. **AOP功能整合** 

   + 将AspectJ切面功能整合到Spring框架中，使得我们能够在Spring应用程序中使用AspectJ风格的切面编程，从而更好地实现横切关注点的模块化和重用。

### 四、类关系图

~~~mermaid
classDiagram
direction BT
class AbstractAdvisorAutoProxyCreator
class AbstractAutoProxyCreator
class AnnotationAwareAspectJAutoProxyCreator
class AopInfrastructureBean {
<<Interface>>

}
class AspectJAwareAdvisorAutoProxyCreator
class Aware {
<<Interface>>

}
class BeanClassLoaderAware {
<<Interface>>

}
class BeanFactoryAware {
<<Interface>>

}
class BeanPostProcessor {
<<Interface>>

}
class InstantiationAwareBeanPostProcessor {
<<Interface>>

}
class ProxyConfig
class ProxyProcessorSupport
class SmartInstantiationAwareBeanPostProcessor {
<<Interface>>

}

AbstractAdvisorAutoProxyCreator  -->  AbstractAutoProxyCreator 
AbstractAutoProxyCreator  ..>  BeanFactoryAware 
AbstractAutoProxyCreator  -->  ProxyProcessorSupport 
AbstractAutoProxyCreator  ..>  SmartInstantiationAwareBeanPostProcessor 
AnnotationAwareAspectJAutoProxyCreator  -->  AspectJAwareAdvisorAutoProxyCreator 
AspectJAwareAdvisorAutoProxyCreator  -->  AbstractAdvisorAutoProxyCreator 
BeanClassLoaderAware  -->  Aware 
BeanFactoryAware  -->  Aware 
InstantiationAwareBeanPostProcessor  -->  BeanPostProcessor 
ProxyProcessorSupport  ..>  AopInfrastructureBean 
ProxyProcessorSupport  ..>  BeanClassLoaderAware 
ProxyProcessorSupport  -->  ProxyConfig 
SmartInstantiationAwareBeanPostProcessor  -->  InstantiationAwareBeanPostProcessor 
~~~

### 五、最佳实践

使用`EnableAspectJAutoProxy`
注解和Spring的基于注解的应用上下文来启用AspectJ自动代理功能。在程序中，首先创建了一个基于注解的应用上下文，然后通过该上下文获取了`MyService`
bean，并调用了其方法。

```java
public class EnableAspectJAutoProxyDemo {

    public static void main(String[] args) {
        // 创建基于注解的应用上下文
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // 从应用上下文中获取MyService bean
        MyService myService = context.getBean(MyService.class);
        // 调用MyService的方法
        myService.foo();
    }
}
```

`AppConfig` 类是一个使用 `@Configuration` 注解标记的配置类，通过 `@EnableAspectJAutoProxy` 开启了 AspectJ
自动代理功能，并通过 `@ComponentScan` 启用了组件扫描，用于自动发现和注册 Spring 组件。

```java

@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class AppConfig {

}
```

`MyService` 类是一个使用 `@Service` 注解标记的服务类，提供了一个名为 `foo()` 的方法，该方法在调用时会打印消息 "foo..."。

```java

@Service
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

`MyAspect`是一个使用了`@Aspect`注解的Java类，表示它是一个切面。在这个类中，定义了一个名为`advice`的方法，并使用了`@Before`
注解来指定在目标方法执行之前执行的通知。

```java

@Aspect
@Component
public class MyAspect {

    @Before("execution(* com.xcs.spring.MyService+.*(..))")
    public void before() {
        System.out.println("Before method execution");
    }
}
```

运行结果，调用 `MyService` 类的 `foo()` 方法之前，成功地执行了一个切面通知，输出了 "Before method execution"
的消息，然后执行了 `foo()` 方法，输出了 "foo..." 的消息。

```java
Before method
execution
foo...
```

### 六、时序图

~~~mermaid
sequenceDiagram
autonumber
BeanFactory->>AbstractAutoProxyCreator:postProcessAfterInitialization()
Note over BeanFactory,AbstractAutoProxyCreator: BeanFactory调用初始化后处理方法
AbstractAutoProxyCreator->>AbstractAutoProxyCreator:wrapIfNecessary()
Note over AbstractAutoProxyCreator,AbstractAutoProxyCreator: 条件判断与代理创建
AbstractAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:getAdvicesAndAdvisorsForBean()
Note over AbstractAutoProxyCreator,AbstractAdvisorAutoProxyCreator: 获取并返回适用的Advisors数组
AbstractAdvisorAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:findEligibleAdvisors()
Note over AbstractAdvisorAutoProxyCreator,AbstractAdvisorAutoProxyCreator: 查找并扩展可应用的Advisors列表
AbstractAdvisorAutoProxyCreator->>AnnotationAwareAspectJAutoProxyCreator:findCandidateAdvisors()
Note over AbstractAdvisorAutoProxyCreator,AnnotationAwareAspectJAutoProxyCreator: 添加Spring和AspectJ，合并为候选Advisors列表
AnnotationAwareAspectJAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:super.findCandidateAdvisors()
Note over AnnotationAwareAspectJAutoProxyCreator,AbstractAdvisorAutoProxyCreator: 获取自动代理的候选Advisors列表
AbstractAdvisorAutoProxyCreator->>BeanFactoryAdvisorRetrievalHelper:findAdvisorBeans()
Note over AbstractAdvisorAutoProxyCreator,BeanFactoryAdvisorRetrievalHelper: 获取当前bean工厂中所有合格的Advisor beans
AnnotationAwareAspectJAutoProxyCreator->>BeanFactoryAspectJAdvisorsBuilder:buildAspectJAdvisors()
Note over AnnotationAwareAspectJAutoProxyCreator,BeanFactoryAspectJAdvisorsBuilder: 获取AspectJ注解的切面并创建Advisor
AnnotationAwareAspectJAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:返回Advisors
AbstractAdvisorAutoProxyCreator->>AbstractAdvisorAutoProxyCreator:findAdvisorsThatCanApply()
Note over AbstractAdvisorAutoProxyCreator,AbstractAdvisorAutoProxyCreator: 查找适用的候选顾问并设置代理
AbstractAdvisorAutoProxyCreator->>AopUtils:findAdvisorsThatCanApply()
Note over AbstractAdvisorAutoProxyCreator,AopUtils: 筛选适用的顾问并添加到列表。
AopUtils->>AopUtils:canApply(advisor,targetClass,hasIntroductions)
Note over AopUtils,AopUtils: 判断顾问是否适用于目标类。
AopUtils->>AopUtils:canApply(pc,targetClass,hasIntroductions)
Note over AopUtils,AopUtils: 判断切点是否适用于目标类。
AbstractAdvisorAutoProxyCreator->>AspectJAwareAdvisorAutoProxyCreator:extendAdvisors()
Note over AbstractAdvisorAutoProxyCreator,AspectJAwareAdvisorAutoProxyCreator: 添加AspectJ支持到Advisor链。
AspectJAwareAdvisorAutoProxyCreator->>AspectJProxyUtils:makeAdvisorChainAspectJCapableIfNecessary()
Note over AspectJAwareAdvisorAutoProxyCreator,AspectJProxyUtils: 在Advisor链中添加AspectJ支持。
AbstractAdvisorAutoProxyCreator->>AbstractAutoProxyCreator:返回拦截器
AbstractAutoProxyCreator->>AbstractAutoProxyCreator:createProxy()
Note over AbstractAutoProxyCreator,AbstractAutoProxyCreator: 创建 AOP 代理对象的过程。
AbstractAutoProxyCreator->>ProxyFactory:new ProxyFactory()
ProxyFactory->>AbstractAutoProxyCreator:返回proxyFactory
AbstractAutoProxyCreator->>ProxyFactory:getProxy()
ProxyFactory->>AbstractAutoProxyCreator:返回代理对象
AbstractAutoProxyCreator->>BeanFactory:返回代理对象
~~~

### 七、源码分析

在`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization`方法中，用于在初始化后对bean进行后置处理。它的作用是检查是否需要对bean创建代理，并在需要时使用配置的拦截器创建代理对象，以实现AOP功能。

```java
/**
 * 如果子类确定要将该bean标识为需要代理的bean，则使用配置的拦截器创建代理。
 * @see #getAdvicesAndAdvisorsForBean
 */
@Override
public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
    if (bean != null) {
        // 获取缓存键
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        // 如果早期代理引用集合中存在该bean，并且不是同一引用，则进行包装
        if (this.earlyProxyReferences.remove(cacheKey) != bean) {
            return wrapIfNecessary(bean, beanName, cacheKey);
        }
    }
    // 返回bean
    return bean;
}
```

在`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary`方法中，首先检查bean的名称是否存在且是否属于目标源bean，如果是，则直接返回原始bean实例；然后检查缓存中是否存在已标记为不需要代理的bean，如果是，则同样直接返回原始bean实例；接着检查bean的类是否为基础结构类或者是否应该跳过该bean，如果是，则将其标记为不需要代理并返回原始bean实例；最后，如果存在通知（拦截器），则创建代理对象并返回，否则同样将其标记为不需要代理并返回原始bean实例。

```java
/**
 * 如果需要，对给定的bean进行包装，即如果它符合被代理的条件。
 * @param bean 原始的bean实例
 * @param beanName bean的名称
 * @param cacheKey 元数据访问的缓存键
 * @return 包装了bean的代理，或者原始的bean实例
 */
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
    // 如果beanName非空，并且该bean已经被目标源bean包含，则直接返回原始的bean实例
    if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
        return bean;
    }
    // 如果缓存中已经存在该bean的标记为不需要代理，则直接返回原始的bean实例
    if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
        return bean;
    }
    // 如果bean的类为基础结构类，或者应该跳过该bean的类，则直接返回原始的bean实例
    if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
        this.advisedBeans.put(cacheKey, Boolean.FALSE);
        return bean;
    }

    // 如果存在通知，则创建代理
    Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
    if (specificInterceptors != DO_NOT_PROXY) {
        this.advisedBeans.put(cacheKey, Boolean.TRUE);
        Object proxy = createProxy(
                bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
        this.proxyTypes.put(cacheKey, proxy.getClass());
        return proxy;
    }

    // 没有通知，将bean标记为不需要代理
    this.advisedBeans.put(cacheKey, Boolean.FALSE);
    return bean;
}
```

在`org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#getAdvicesAndAdvisorsForBean`方法中，首先调用`findEligibleAdvisors`方法查找适用于该bean的Advisors，然后将其转换为数组并返回。如果没有找到适用的Advisors，则返回一个特定的标记值`DO_NOT_PROXY`。

```java
/**
 * 获取适用于给定bean的Advisors。
 * @param beanClass bean的类
 * @param beanName bean的名称
 * @param targetSource 目标源
 * @return 包含Advisors的数组，如果没有找到适用的Advisors，则返回DO_NOT_PROXY
 */
@Override
@Nullable
protected Object[] getAdvicesAndAdvisorsForBean(
        Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {

    // 查找适用于bean的Advisors
    List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
    // 如果没有找到适用的Advisors，则返回DO_NOT_PROXY
    if (advisors.isEmpty()) {
        return DO_NOT_PROXY;
    }
    // 返回Advisors的数组
    return advisors.toArray();
}
```

在`org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findEligibleAdvisors`方法中，主要用于查找适用于自动代理给定类的所有合适的 Advisors。它首先调用 findCandidateAdvisors 方法来查找候选的 Advisors，然后通过 findAdvisorsThatCanApply 方法筛选出可以应用于当前类的 Advisors。接着，它调用 extendAdvisors 方法来扩展 Advisors 列表，以确保适当的拦截器和通知已被应用。最后，如果有适用的 Advisors，则对 Advisors 列表进行排序并返回；如果没有适用的 Advisors，则返回一个空列表。

```java
/**
 * 查找适用于自动代理该类的所有合格Advisors。
 * @param beanClass 要查找Advisors的类
 * @param beanName 当前代理的bean的名称
 * @return 空列表，非null，如果没有切点或拦截器
 * @see #findCandidateAdvisors
 * @see #sortAdvisors
 * @see #extendAdvisors
 */
protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
    // 查找候选Advisors
    List<Advisor> candidateAdvisors = findCandidateAdvisors();
    // 查找可应用的Advisors
    List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
    // 扩展Advisors
    extendAdvisors(eligibleAdvisors);
    // 如果有可应用的Advisors，则对Advisors进行排序
    if (!eligibleAdvisors.isEmpty()) {
        eligibleAdvisors = sortAdvisors(eligibleAdvisors);
    }
    return eligibleAdvisors;
}
```

在`org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors`方法中，重写了父类方法 `findCandidateAdvisors()`，首先调用了父类方法以获取所有Spring Advisors，然后利用 `aspectJAdvisorsBuilder` 构建了所有AspectJ切面对应的Advisors，并将其添加到`advisors`列表中返回。

[BeanFactoryAspectJAdvisorsBuilder源码分析](../spring-aop-beanFactoryAspectJAdvisorsBuilder/README.md)

```java
@Override
protected List<Advisor> findCandidateAdvisors() {
    // 添加根据超类规则找到的所有Spring顾问。
    List<Advisor> advisors = super.findCandidateAdvisors();
    // 为bean工厂中的所有AspectJ切面构建顾问。
    if (this.aspectJAdvisorsBuilder != null) {
        advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
    }
    return advisors;
}
```

在`org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findCandidateAdvisors`方法中，调用`BeanFactoryAdvisorRetrievalHelper.findAdvisorBeans()` 方法来获取候选的Advisors列表。

[BeanFactoryAdvisorRetrievalHelper源码分析](../spring-aop-beanFactoryAdvisorRetrievalHelper/README.md)

```java
/**
 * 查找用于自动代理的所有候选Advisors。
 * @return 候选Advisors的列表
 */
protected List<Advisor> findCandidateAdvisors() {
    Assert.state(this.advisorRetrievalHelper != null, "No BeanFactoryAdvisorRetrievalHelper available");
    return this.advisorRetrievalHelper.findAdvisorBeans();
}
```

在`org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findAdvisorsThatCanApply`方法中，搜索给定的候选 Advisors，以找到所有能够应用于指定 bean 的 Advisors。它设置当前被代理的 bean 名称，并尝试查找所有适用于指定 bean 的 Advisors。在查找完成后，它会清除当前被代理的 bean 名称。

```java
/**
 * 搜索给定的候选顾问，找到所有适用于指定bean的顾问。
 * @param candidateAdvisors 候选顾问列表
 * @param beanClass 目标bean的类
 * @param beanName 目标bean的名称
 * @return 适用的顾问列表
 * @see ProxyCreationContext#getCurrentProxiedBeanName()
 */
protected List<Advisor> findAdvisorsThatCanApply(
        List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
    
    // 设置当前代理的bean名称
    ProxyCreationContext.setCurrentProxiedBeanName(beanName);
    try {
        // 查找适用于指定bean的顾问
        return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
    }
    finally {
        // 清除当前代理的bean名称
        ProxyCreationContext.setCurrentProxiedBeanName(null);
    }
}
```

在`org.springframework.aop.support.AopUtils#findAdvisorsThatCanApply`方法中，用于确定适用于给定类的候选Advisors子列表。它遍历给定的Advisors列表，首先将能够应用于指定类的引介Advisors添加到结果列表中。然后，它检查是否已经存在引介Advisors，如果存在，则跳过；如果不存在，则继续遍历Advisors列表，并将能够应用于指定类的其他Advisors添加到结果列表中。最终返回这个子列表，其中包含可以应用于给定类的所有Advisors。

```java
/**
 * 确定 {@code candidateAdvisors} 列表中适用于给定类的子列表。
 * @param candidateAdvisors 要评估的顾问列表
 * @param clazz 目标类
 * @return 可应用于给定类的顾问子列表
 * （可能是原始列表）
 */
public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
    // 如果候选顾问列表为空，则直接返回空列表
    if (candidateAdvisors.isEmpty()) {
       return candidateAdvisors;
    }
    // 创建一个用于存储适用于给定类的顾问的列表
    List<Advisor> eligibleAdvisors = new ArrayList<>();
    // 遍历候选顾问列表
    for (Advisor candidate : candidateAdvisors) {
       // 如果候选顾问是引介顾问，并且可以应用于给定类，则将其添加到结果列表中
       if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
          eligibleAdvisors.add(candidate);
       }
    }
    // 检查是否存在引介顾问
    boolean hasIntroductions = !eligibleAdvisors.isEmpty();
    // 继续遍历候选顾问列表
    for (Advisor candidate : candidateAdvisors) {
       // 如果候选顾问是引介顾问，则跳过
       if (candidate instanceof IntroductionAdvisor) {
          // 已经处理过
          continue;
       }
       // 如果候选顾问可以应用于给定类，则将其添加到结果列表中
       if (canApply(candidate, clazz, hasIntroductions)) {
          eligibleAdvisors.add(candidate);
       }
    }
    return eligibleAdvisors;
}
```

在`org.springframework.aop.support.AopUtils#canApply(org.springframework.aop.Advisor, java.lang.Class<?>, boolean)`方法中，判断给定的顾问是否能够在指定的类上应用。它首先检查顾问是否是引介顾问，如果是，则通过类过滤器来判断是否可以应用于目标类。如果顾问不是引介顾问，而是切点顾问，则通过切点来判断是否可以应用于目标类。如果顾问既不是引介顾问也不是切点顾问，则假设它适用于目标类。

```java
/**
 * 判断给定的顾问是否能够在指定的类上应用。
 * <p>这是一个重要的测试，因为它可以用于优化掉一个类的顾问。
 * 这个版本还考虑了引介（对于IntroductionAwareMethodMatchers）。
 * @param advisor 要检查的顾问
 * @param targetClass 我们正在测试的类
 * @param hasIntroductions 顾问链中是否包含任何引介
 * @return 切点是否能够应用于任何方法
 */
public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
    // 如果顾问是引介顾问，则通过类过滤器来判断是否可以应用于目标类
    if (advisor instanceof IntroductionAdvisor) {
       return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
    }
    // 如果顾问是切点顾问，则通过切点来判断是否可以应用于目标类
    else if (advisor instanceof PointcutAdvisor) {
       PointcutAdvisor pca = (PointcutAdvisor) advisor;
       return canApply(pca.getPointcut(), targetClass, hasIntroductions);
    }
    // 否则，假设它适用于目标类
    else {
       // 它没有切点，因此我们假设它适用。
       return true;
    }
}
```

在`org.springframework.aop.support.AopUtils#canApply(org.springframework.aop.Pointcut, java.lang.Class<?>, boolean)`方法中，确定给定的切点是否能够在指定的类上应用。首先，它检查切点的类过滤器是否与目标类匹配。如果不匹配，则返回 false。如果类过滤器匹配目标类，它会检查方法匹配器是否为 MethodMatcher.TRUE，如果是，则表示切点适用于目标类的任何方法，直接返回 true。如果方法匹配器不是 MethodMatcher.TRUE，则遍历目标类及其所有接口，并检查每个类中的方法是否与切点匹配。如果找到匹配的方法，则返回 true；如果没有找到匹配的方法，则返回 false。

```java
/**
 * 判断给定的切点是否能够在指定的类上应用。
 * <p>这是一个重要的测试，因为它可以用于优化掉一个类的切点。
 * @param pc 要检查的静态或动态切点
 * @param targetClass 要测试的类
 * @param hasIntroductions 顾问链中是否包含任何引介
 * @return 切点是否能够应用于任何方法
 */
public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
	Assert.notNull(pc, "Pointcut must not be null");
    // 首先检查类过滤器是否匹配目标类
    if (!pc.getClassFilter().matches(targetClass)) {
        return false;
    }

    MethodMatcher methodMatcher = pc.getMethodMatcher();
    // 如果方法匹配器是 MethodMatcher.TRUE，则不需要遍历方法，直接返回true
    if (methodMatcher == MethodMatcher.TRUE) {
        // 如果我们匹配任何方法，则不需要遍历方法...
        return true;
    }

    IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
    if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
        introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
    }

    // 获取目标类及其所有接口的集合
    Set<Class<?>> classes = new LinkedHashSet<>();
    // 如果目标类不是代理类，则将其添加到类集合中
    if (!Proxy.isProxyClass(targetClass)) {
        classes.add(ClassUtils.getUserClass(targetClass));
    }
    // 将目标类的所有接口添加到类集合中
    classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));

    // 遍历类集合
    for (Class<?> clazz : classes) {
        // 获取类中声明的所有方法
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
        // 遍历方法
        for (Method method : methods) {
            // 如果存在引介感知的方法匹配器，并且方法匹配，则返回true；
            // 否则，如果方法匹配器匹配方法，则返回true
            if (introductionAwareMethodMatcher != null ?
                    introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions) :
                    methodMatcher.matches(method, targetClass)) {
                return true;
            }
        }
    }

    return false;
}
```

在`org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator#extendAdvisors`方法中，将 `ExposeInvocationInterceptor` 添加到通知链的开头。这是必要的额外处理，特别是在使用 AspectJ 切点表达式和 AspectJ 风格的建议时。

```java
/**
 * 将 {@link ExposeInvocationInterceptor} 添加到通知链的开头。
 * <p>在使用AspectJ切点表达式和AspectJ风格的建议时，需要添加此额外的建议。
 * @Override
 * @param candidateAdvisors 候选的Advisors列表
 */
@Override
protected void extendAdvisors(List<Advisor> candidateAdvisors) {
    // 如果需要，使Advisor链支持AspectJ
    AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(candidateAdvisors);
}
```

在`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#createProxy`方法中，首先检查`BeanFactory`是否是`ConfigurableListableBeanFactory`类型的，如果是，则调用`AutoProxyUtils.exposeTargetClass`方法来暴露目标类。然后创建一个`ProxyFactory`实例，并根据当前的代理配置进行设置。根据是否启用了代理目标类的标志，决定是否将代理目标类设置为true或false。接着构建适用于该bean的所有顾问，将它们添加到`ProxyFactory`中，并设置目标源为预先配置好的`TargetSource`。最后，根据当前的代理配置和代理类加载器，使用`ProxyFactory`获取代理实例并返回。

```java
/**
 * 为给定的 bean 创建一个 AOP 代理。
 * @param beanClass bean 的类
 * @param beanName bean 的名称
 * @param specificInterceptors 适用于此 bean 的拦截器集合（可能为空，但不为 null）
 * @param targetSource 代理的 TargetSource，已预先配置以访问该 bean
 * @return bean 的 AOP 代理
 * @see #buildAdvisors
 */
protected Object createProxy(Class<?> beanClass, @Nullable String beanName,
                              @Nullable Object[] specificInterceptors, TargetSource targetSource) {

    // 如果 beanFactory 是 ConfigurableListableBeanFactory 类型的，则暴露目标类
    if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
        AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory) this.beanFactory, beanName, beanClass);
    }

    // 创建 ProxyFactory 实例
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.copyFrom(this);

    // 如果需要使用代理目标类，则设置为 true
    if (proxyFactory.isProxyTargetClass()) {
        // 对 JDK 代理目标进行显式处理（用于介绍建议场景）
        if (Proxy.isProxyClass(beanClass)) {
            // 必须允许引入；不能只设置接口为代理的接口。
            for (Class<?> ifc : beanClass.getInterfaces()) {
                proxyFactory.addInterface(ifc);
            }
        }
    } else {
        // 未强制代理目标类标志，让我们应用默认检查...
        if (shouldProxyTargetClass(beanClass, beanName)) {
            proxyFactory.setProxyTargetClass(true);
        } else {
            evaluateProxyInterfaces(beanClass, proxyFactory);
        }
    }

    // 构建顾问数组
    Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
    // 将顾问添加到 ProxyFactory
    proxyFactory.addAdvisors(advisors);
    proxyFactory.setTargetSource(targetSource);
    // 自定义 ProxyFactory
    customizeProxyFactory(proxyFactory);

    proxyFactory.setFrozen(this.freezeProxy);
    if (advisorsPreFiltered()) {
        proxyFactory.setPreFiltered(true);
    }

    // 如果 bean 类没有在重写类加载器中本地加载，则使用原始 ClassLoader
    ClassLoader classLoader = getProxyClassLoader();
    if (classLoader instanceof SmartClassLoader && classLoader != beanClass.getClassLoader()) {
        classLoader = ((SmartClassLoader) classLoader).getOriginalClassLoader();
    }
    // 获取代理实例
    return proxyFactory.getProxy(classLoader);
}
```
