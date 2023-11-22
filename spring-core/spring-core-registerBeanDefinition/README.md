## Bean的定义注册

- [Bean的定义注册](#bean的定义注册)
  - [一、基本信息](#一基本信息)
  - [二、知识储备](#二知识储备)
  - [三、最佳实践](#三最佳实践)
  - [四、源码分析](#四源码分析)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

+ **ClassPathBeanDefinitionScanner**
  + [ClassPathBeanDefinitionScanner](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-classPathBeanDefinitionScanner/README.md) 类，用于在类路径上扫描指定包及其子包中的类，识别符合条件的类，并将其注册为 Spring Bean 的定义，从而实现组件扫描和自动装配，使我们能够方便地管理和配置应用程序中的 Bean。它允许我们定义过滤条件，以确定哪些类应被注册为 Bean，以及配合自动装配实现依赖注入，提高了应用程序的可维护性和扩展性。
+ **AnnotatedBeanDefinitionReader**
  + [AnnotatedBeanDefinitionReader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-annotatedBeanDefinitionReader/README.md)是一个用于读取和解析带有注解的Bean定义的类，它主要用于基于注解的配置方式，允许开发者将Java类标记为Spring组件，从而让Spring容器自动扫描和注册这些组件，而不需要显式配置这些组件的Bean定义。

### 三、最佳实践

通过`AnnotationConfigApplicationContext`容器，以手动注册和包扫描两种方式注册Bean定义，其中手动注册了单个Bean（`MyBean`类），并通过包扫描注册了指定包路径下的所有标有`@Component`及其派生注解的类。最后，通过打印输出了所有已注册的Bean定义的名称。

```java
public class RegisterBeanDefinitionApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册Bean
        context.register(MyBean.class);
        // 扫描包
        context.scan("com.xcs.spring");
        // 打印Bean定义
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

一个简单的Java类 `MyBean`，这个类没有任何注解或其他特殊标记。

```java
public class MyBean {
    
}
```

分别使用`@Controller`、`@Service`和`@Repository`。这些注解是Spring框架中用于标识不同层次组件的特殊注解。

```java
@Controller
public class MyController {
    
}

@Service
public class MyService {

}

@Repository
public class MyRepository {

}
```

运行结果发现，Spring应用上下文中成功加载了配置，并注册了各种Bean定义，包括手动注册和通过注解自动注册的。

PS：前面4个是Spring容器内部的注册的Bean定义。

```java
beanDefinitionName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanDefinitionName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanDefinitionName = org.springframework.context.event.internalEventListenerProcessor
beanDefinitionName = org.springframework.context.event.internalEventListenerFactory
beanDefinitionName = myBean
beanDefinitionName = myController
beanDefinitionName = myRepository
beanDefinitionName = myService
```

### 四、源码分析

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()`方法中，初始化`AnnotationConfigApplicationContext`实例中的两个关键组件：`AnnotatedBeanDefinitionReader`和`ClassPathBeanDefinitionScanner`，这两者分别用于处理注解式Bean定义和类路径扫描注册Bean定义。

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#register`方法中，主要作用是通过`AnnotatedBeanDefinitionReader`注册一组组件类，将它们解析为Spring容器中的Bean定义。

>  **具体源码分析已经在另外一篇关于[AnnotatedBeanDefinitionReader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-annotatedBeanDefinitionReader/README.md)类的博客中详细分析了。**

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

在`org.springframework.context.annotation.AnnotationConfigApplicationContext#scan`方法中，主要作用是通过`ClassPathBeanDefinitionScanner`扫描指定的基础包路径下的类，将它们解析为Spring容器中的Bean定义。

>  **具体源码分析已经在另外一篇关于[ClassPathBeanDefinitionScanner](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-classPathBeanDefinitionScanner/README.md)类的博客中详细分析了。**

```java
@Override
public void scan(String... basePackages) {
    Assert.notEmpty(basePackages, "At least one base package must be specified");
    StartupStep scanPackages = this.getApplicationStartup().start("spring.context.base-packages.scan")
        .tag("packages", () -> Arrays.toString(basePackages));
    this.scanner.scan(basePackages);
    scanPackages.end();
}
```