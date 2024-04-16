## BeanFactoryAdvisorRetrievalHelper

- [BeanFactoryAdvisorRetrievalHelper](#beanfactoryadvisorretrievalhelper)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、源码分析](#五源码分析)
  - [六、常见问题](#六常见问题)

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

使用 `BeanFactoryAdvisorRetrievalHelper` 类来从一个默认的 Bean 工厂中检索 Advisor，并打印出这些 Advisor 的列表。首先，我们创建一个默认的 Bean 工厂，并向其注册一个名为 "myAdvisor" 的 Advisor。然后，我们创建了 `BeanFactoryAdvisorRetrievalHelper` 实例，并将 Bean 工厂传入其中。接着，通过调用 `findAdvisorBeans()` 方法，我们获取了 Bean 工厂中的 Advisor 列表，并通过循环遍历的方式打印出每个 Advisor 的信息。

```java
public class BeanFactoryAdvisorRetrievalHelperDemo {

    public static void main(String[] args) {
        // 创建一个默认的 Bean 工厂
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 向 Bean 工厂注册一个名为 "myAdvisor" 的 Advisor
        beanFactory.registerSingleton("myAdvisor", new MyAdvisor());

        // 创建 BeanFactoryAdvisorRetrievalHelper 实例，并传入 Bean 工厂
        BeanFactoryAdvisorRetrievalHelper helper = new BeanFactoryAdvisorRetrievalHelper(beanFactory);
        // 获取 Bean 工厂中的 Advisor 列表
        List<Advisor> advisors = helper.findAdvisorBeans();
        // 打印 Advisors
        advisors.forEach(System.out::println);
    }
}
```

`MyAdvisor` 类是一个自定义的 Advisor，继承自 `AbstractPointcutAdvisor`，用于定义切面的逻辑。在该类中，`getPointcut()` 方法返回一个始终为真的 Pointcut，表示适用于所有的连接点；`getAdvice()` 方法返回一个空的 Advice，表示不对目标方法添加任何额外的通知逻辑。因此，该 Advisor 没有实际的业务逻辑，仅作为演示目的。

```java
public class MyAdvisor extends AbstractPointcutAdvisor {

    @Override
    public Pointcut getPointcut() {
        return Pointcut.TRUE;
    }

    @Override
    public Advice getAdvice() {
        return Advisor.EMPTY_ADVICE;
    }
}
```

运行结果，成功地从 Bean 工厂中获取了Advisor。

```java
com.xcs.spring.MyAdvisor@1f7030a6
```

### 五、源码分析

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

### 六、常见问题

1. **忽略FactoryBeans**

   + 在 `findAdvisorBeans()` 方法中，该类会忽略 FactoryBeans，只处理常规的 Advisor Beans。这是因为 FactoryBeans 可能会在初始化时产生副作用，而 `BeanFactoryAdvisorRetrievalHelper` 需要保持所有常规 Beans 未初始化，以便自动代理创建器能够正确地应用于它们。

2. **排除当前正在创建的Beans**

   + 在遍历 Advisor Beans 名称列表时，`findAdvisorBeans()` 方法会排除当前正在创建中的 Beans。这是为了避免在 Bean 的创建过程中引入不稳定的代理逻辑。

3. **错误处理**

   + 当尝试获取 Advisor Bean 时，可能会抛出 `BeanCreationException` 异常。`BeanFactoryAdvisorRetrievalHelper` 需要正确处理这些异常情况，例如，当 Advisor Bean 的依赖 Bean 正在创建中时，可以选择跳过该 Advisor。

4. **缓存机制**

   + 为了提高性能，`BeanFactoryAdvisorRetrievalHelper` 类使用了缓存机制来存储 Advisor Bean 的名称列表。需要注意，在 Bean 工厂中添加或删除 Advisor Bean 时，需要更新缓存以确保数据的一致性。