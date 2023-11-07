## AnnotatedBeanDefinitionReader

- [AnnotatedBeanDefinitionReader](#annotatedbeandefinitionreader)
  - [一、知识储备](#一知识储备)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、源码分析](#五源码分析)
  - [六、与其他组件的关系](#六与其他组件的关系)
  - [七、常见问题](#七常见问题)

### 一、知识储备

1. **Bean定义**
   - 了解Bean的概念以及如何定义和配置Bean是非常重要的。这包括Bean的ID、类名、属性注入、依赖关系等。
2. **注解处理器**
   + 注解处理器是用于处理注解的工具或框架，它们可以在编译时或运行时读取和处理注解。Spring的`AnnotatedBeanDefinitionReader`就是一个注解处理器，用于解析带有Spring注解的类并将其注册为Bean定义。

### 二、基本描述

`AnnotatedBeanDefinitionReader`是一个用于读取和解析带有注解的Bean定义的类，它主要用于基于注解的配置方式，允许开发者将Java类标记为Spring组件，从而让Spring容器自动扫描和注册这些组件，而不需要显式配置这些组件的Bean定义。

### 三、主要功能

1. **注册Bean定义**
   + 适用于那些没有使用特定注解的类，但需要交给 Spring 容器管理的情况。
2. **集成 Spring 容器**
   + `AnnotatedBeanDefinitionReader` 通常与 Spring 容器的注册机（如 `GenericApplicationContext`）一起使用，从而将解析的 Bean 定义注册到容器。

### 四、最佳实践

创建一个 `DefaultListableBeanFactory` 容器和关联的 `AnnotatedBeanDefinitionReader`，手动注册一个 `MyBean` 类为 Spring Bean，然后获取和打印该 Bean 的实例。

```java
public class AnnotatedBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // 创建一个 AnnotationConfigApplicationContext
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // 创建 AnnotatedBeanDefinitionReader 并将其关联到容器
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(factory);

        // 使用 AnnotatedBeanDefinitionReader 注册Bean对象
        reader.registerBean(MyBean.class);

        // 获取并打印 MyBean
        System.out.println("MyBean = " + factory.getBean(MyBean.class));
    }
}
```

`MyBean` 的Java类，代表了一个简单的Java Bean。

```java
public class MyBean {
}
```

运行结果发现，手动注册和获取 Bean 的过程成功，并且 `MyBean` 现在已经被 Spring 容器管理。

```java
MyBean = com.xcs.spring.bean.MyBean@6166e06f
```

### 五、源码分析

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#registerBean(beanClass)`方法中，实际上又调用了`doRegisterBean`方法。

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

在`org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean`方法中，创建一个 Bean 的定义，设置其属性（如作用域、名称、限定符、自定义配置等），最后将这个定义注册到 Spring 容器中，使其成为容器管理的 Bean。

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
        @Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
        @Nullable BeanDefinitionCustomizer[] customizers) {
    // 创建一个 AnnotatedGenericBeanDefinition，用于表示要注册的 Bean 的定义
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
    // 如果存在条件判断，根据条件结果决定是否注册 Bean
    if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
        return;
    }
    // 设置 Bean 的实例提供者（supplier），用于创建 Bean 实例
    abd.setInstanceSupplier(supplier);
    // 解析 Bean 的作用域并设置，确定 Bean 的范围（Scope）
    ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
    abd.setScope(scopeMetadata.getScopeName());
    // 生成或使用指定的 Bean 名称，如果没有指定名称，生成默认的 Bean 名称
    String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
    // 处理常见的 Bean 定义注解，如 @Lazy、@Primary 等
    AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    // 处理 Bean 的限定符（qualifiers），如 @Qualifier 注解
    if (qualifiers != null) {
        for (Class<? extends Annotation> qualifier : qualifiers) {
            if (Primary.class == qualifier) {
                abd.setPrimary(true);
            } else if (Lazy.class == qualifier) {
                abd.setLazyInit(true);
            } else {
                abd.addQualifier(new AutowireCandidateQualifier(qualifier));
            }
        }
    }
    // 自定义 Bean 定义，如果有自定义操作的话
    if (customizers != null) {
        for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(abd);
        }
    }
    // 将 Bean 注册到 Spring 容器中，使其成为容器管理的 Bean
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
}
```

在`org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition`方法中，将Bean定义注册到Spring容器的Bean定义注册表中，并处理别名的注册。

```java
public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

    // Register bean definition under primary name.
    String beanName = definitionHolder.getBeanName();
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

    // Register aliases for bean name, if any.
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

### 六、与其他组件的关系

1. **`AnnotationConfigApplicationContext`**
   + `AnnotationConfigApplicationContext` 是一个常见的 Spring 应用程序上下文类，用于基于注解的配置。它内部使用 `AnnotatedBeanDefinitionReader` 来解析和注册带有注解的类。
2. **`AnnotationConfigWebApplicationContext`**
   + `AnnotationConfigWebApplicationContext` 用于基于注解的配置，但专门用于基于 Spring MVC 的 Web 应用程序。它也使用 `AnnotatedBeanDefinitionReader` 来解析和注册带有注解的类。
3. **Spring Boot 自动配置**
   + Spring Boot 使用 `AnnotatedBeanDefinitionReader` 以及其他类来自动注册和配置应用程序的组件。Spring Boot 的自动配置通常是基于注解的，因此它们与 `AnnotatedBeanDefinitionReader` 集成。
4. **自定义组件扫描器**
   + 我们可以自定义组件扫描器，以扩展或修改 `AnnotatedBeanDefinitionReader` 的行为。这些自定义扫描器通常也会使用 `AnnotatedBeanDefinitionReader` 的功能。

### 七、常见问题

1. **基于注解的 Spring 配置**
   + `AnnotatedBeanDefinitionReader` 可以用于创建基于注解的 Spring 配置，从而避免使用传统的 XML 配置。这在现代的 Spring 应用程序开发中非常常见，可以提高配置的可读性和维护性。
2. **Spring Boot 自动配置**
   + 在 Spring Boot 中，`AnnotatedBeanDefinitionReader` 用于自动扫描并注册 Spring Boot 自动配置类。这使得 Spring Boot 应用程序可以自动配置各种功能，从而降低了我们的工作负担。
3. **自定义组件扫描**
   + 我们可以自定义组件扫描器，使用 `AnnotatedBeanDefinitionReader` 的功能来实现特定的组件注册逻辑。这在某些复杂场景下非常有用，例如需要根据特定条件动态注册组件。