## BeanFactoryAdvisorRetrievalHelper

- [BeanFactoryAdvisorRetrievalHelper](#beanfactoryadvisorretrievalhelper)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、时序图](#五时序图)
  - [六、源码分析](#六源码分析)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`BeanFactoryAdvisorRetrievalHelper` 类是 Spring AOP 框架中的辅助工具，用于在 Bean 工厂中检索 Advisor，这些 Advisor 定义了切面逻辑，可以在目标 Bean 的方法调用中织入相应的通知。

### 三、主要功能

1. **协助Advisor的检索**
   + 帮助 Spring AOP 框架在应用程序的 Bean 工厂中查找与目标 Bean 相关的 Advisor。
   
2. **解析Advisor的Bean名称**
   + 解析 Advisor 在 Spring 容器中的 Bean 名称，并根据名称从 Bean 工厂中获取相应的 Advisor 实例。
   
3. **适配不同类型的Advisor**
   + 支持不同类型的 Advisor，包括前置通知（BeforeAdvice）、后置通知（AfterAdvice）、环绕通知（AroundAdvice）等，能够正确地应用到目标 Bean 上。
   
4. **辅助创建代理**

   + 辅助 Spring 容器创建代理对象，并将 Advisor 中定义的通知逻辑织入到目标 Bean 的方法调用中。

### 四、最佳实践

使用基于注解的应用上下文来获取并调用 `MyService` Bean 的 `foo()`
方法。首先，创建了一个 `AnnotationConfigApplicationContext` 实例，通过传入 `AppConfig.class`
来初始化基于注解的应用上下文。然后，通过 `context.getBean(MyService.class)` 获取了 `MyService` Bean
的实例，并调用了其 `foo()` 方法。

```java
public class BeanFactoryAdvisorRetrievalHelperDemo {

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

使用 `@Component` 注解标记的自定义 Advisor，继承自 `AbstractPointcutAdvisor`。它定义了一个总是返回真值的 Pointcut，并将一个自定义的
Advice `MyAdvice` 应用于目标方法上。

```java

@Component
public class MyAdvisor extends AbstractPointcutAdvisor {

    @Override
    public Pointcut getPointcut() {
        return Pointcut.TRUE;
    }

    @Override
    public Advice getAdvice() {
        return new MyAdvice();
    }
}
```

`MyAdvice` 类是一个实现了 `MethodBeforeAdvice` 接口的自定义通知类，用于在目标方法执行前执行特定逻辑。在 `before()`
方法中，它打印了一条消息："Before method execution"。

```java
public class MyAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before method execution");
    }
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

运行结果，调用 `MyService` 类的 `foo()` 方法之前，成功地执行了一个切面通知，输出了 "Before method execution"
的消息，然后执行了 `foo()` 方法，输出了 "foo..." 的消息。

```java
Before method
execution
foo...
```

### 五、时序图

~~~mermaid
sequenceDiagram
    AbstractAutowireCapableBeanFactory->>AbstractAutoProxyCreator: postProcessAfterInitialization()
    Note over AbstractAutowireCapableBeanFactory,AbstractAutoProxyCreator: 调用后处理方法
    AbstractAutoProxyCreator->>AbstractAutoProxyCreator: wrapIfNecessary()
    Note over AbstractAutoProxyCreator: 调用包装方法
    AbstractAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: getAdvicesAndAdvisorsForBean()
    Note over AbstractAutoProxyCreator,AbstractAdvisorAutoProxyCreator: 获取通知和 Advisors
    AbstractAdvisorAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: findEligibleAdvisors()
    Note over AbstractAdvisorAutoProxyCreator: 查找合适的 Advisors
    AbstractAdvisorAutoProxyCreator->>AnnotationAwareAspectJAutoProxyCreator: findCandidateAdvisors()
    Note over AbstractAdvisorAutoProxyCreator,AnnotationAwareAspectJAutoProxyCreator: 查找候选的 Advisors
    AnnotationAwareAspectJAutoProxyCreator->>AbstractAdvisorAutoProxyCreator: super.findCandidateAdvisors()
    Note over AnnotationAwareAspectJAutoProxyCreator,AbstractAdvisorAutoProxyCreator: 调用父类的查找候选的 Advisors
    AbstractAdvisorAutoProxyCreator->>BeanFactoryAdvisorRetrievalHelper: findAdvisorBeans()
    Note over AbstractAdvisorAutoProxyCreator,BeanFactoryAdvisorRetrievalHelper: 查找当前Bean工厂中所有符合条件的Advisor
    BeanFactoryAdvisorRetrievalHelper->>AbstractAutoProxyCreator: 返回 advisors
~~~

### 六、源码分析

在`org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper#findAdvisorBeans`方法中，主要功能是在当前的 Bean 工厂中查找所有符合条件的 Advisor Beans。它忽略了 FactoryBeans，并排除了当前正在创建中的 Beans。该方法首先确定 Advisor Bean 的名称列表，如果尚未缓存，则通过 `BeanFactoryUtils.beanNamesForTypeIncludingAncestors()` 方法获取。然后，它遍历这些 Advisor Bean 的名称，检查它们是否符合条件，并将符合条件的 Advisor Bean 添加到结果列表中。在添加之前，它会检查该 Bean 是否当前正在创建中，如果是，则跳过。最后，返回包含所有符合条件的 Advisor Beans 的列表。

```java
/**
 * 在当前 Bean 工厂中查找所有符合条件的 Advisor Bean，
 * 忽略 FactoryBeans，并排除当前正在创建的 Bean。
 * @return {@link org.springframework.aop.Advisor} Bean 的列表
 * @see #isEligibleBean
 */
public List<Advisor> findAdvisorBeans() {
    // 如果未缓存 Advisor Bean 的名称列表，则确定该列表。
    String[] advisorNames = this.cachedAdvisorBeanNames;
    if (advisorNames == null) {
        // 不要在这里初始化 FactoryBeans我们需要保持所有常规 Bean 未初始化，以便自动代理创建器应用到它们上！
        advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                this.beanFactory, Advisor.class, true, false);
        this.cachedAdvisorBeanNames = advisorNames;
    }
    if (advisorNames.length == 0) {
        return new ArrayList<>();
    }

    List<Advisor> advisors = new ArrayList<>();
    // 遍历 Advisor Bean 名称列表
    for (String name : advisorNames) {
        // 检查 Bean 是否符合条件
        if (isEligibleBean(name)) {
            // 如果 Bean 当前正在创建中，则跳过
            if (this.beanFactory.isCurrentlyInCreation(name)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Skipping currently created advisor '" + name + "'");
                }
            }
            else {
                try {
                    // 尝试获取 Advisor Bean，并添加到列表中
                    advisors.add(this.beanFactory.getBean(name, Advisor.class));
                }
                catch (BeanCreationException ex) {
                    Throwable rootCause = ex.getMostSpecificCause();
                    if (rootCause instanceof BeanCurrentlyInCreationException) {
                        BeanCreationException bce = (BeanCreationException) rootCause;
                        String bceBeanName = bce.getBeanName();
                        // 如果当前 Bean 依赖于正在创建的 Bean，则跳过
                        if (bceBeanName != null && this.beanFactory.isCurrentlyInCreation(bceBeanName)) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("Skipping advisor '" + name +
                                        "' with dependency on currently created bean: " + ex.getMessage());
                            }
                            // 忽略表示对当前正在尝试进行通知的 Bean 的引用。
                            // 我们希望找到除当前正在创建的 Bean 本身之外的其他 Advisor。
                            continue;
                        }
                    }
                    // 如果获取 Advisor Bean 失败，则抛出异常
                    throw ex;
                }
            }
        }
    }
    return advisors;
}
```
