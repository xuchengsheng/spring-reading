## @ComponentScan

[TOC]

### 1、注解说明

`@ComponentScan`是Spring的核心注解，用于自动扫描和注册指定包下的组件。我们可以指定包路径、自定义组件过滤规则，并决定是否启用默认过滤器。该注解还支持懒加载、自定义命名策略、作用域解析，以及为特定生命周期的beans指定代理模式。默认情况下，它会扫描带有`@Component`、`@Service`、`@Repository`和`@Controller`的类，但可以通过`useDefaultFilters`属性调整。此外，从Spring 3.1开始，这个注解支持重复性，允许在单个配置类上多次使用，每次具有不同的属性设置。

### 2、注解源码

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

    ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

    String resourcePattern() default ClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;

    boolean useDefaultFilters() default true;

    Filter[] includeFilters() default {};

    Filter[] excludeFilters() default {};

    boolean lazyInit() default false;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface Filter {

        FilterType type() default FilterType.ANNOTATION;

        @AliasFor("classes")
        Class<?>[] value() default {};

        @AliasFor("value")
        Class<?>[] classes() default {};

        String[] pattern() default {};
    }
}
```

### 3、字段描述

#### 3.1 value

别名为 basePackages。这指定了一个或多个要扫描的基础包。扫描器将在这些包及其子包中搜索符合条件的组件。

#### 3.2 basePackages

与 value 属性功能相同，指定一个或多个要扫描的基础包。

#### 3.3 basePackageClasses

通过指定类来定义要扫描的包。提供的类本身不必标记为一个组件，它们仅仅用于确定要扫描的基础包。

#### 3.4 nameGenerator

提供一个Bean名称生成策略，用于为在组件扫描期间注册的bean定义生成名称。

#### 3.5 scopeResolver

定义一个范围元数据解析策略，它为在组件扫描期间注册的bean定义提供范围信息。

#### 3.6 scopedProxy

定义如何处理scoped beans的代理。这可以是创建一个接口代理还是使用CGLIB创建一个类的代理。

`ScopedProxyMode`有以下取值：

1. **DEFAULT**: 默认设置，这意味着必须通过其他方式进行配置，例如全局配置。如果未在其他地方明确配置，则不会创建代理。
2. **NO**: 表示不应为bean创建代理。在这种模式下，当你尝试注入短作用域的bean到更长作用域的bean时，会出现问题，因为你每次获得的都是同一个实例，而不是新的短作用域实例。
3. **TARGET_CLASS**: 这意味着应为目标bean创建CGLIB代理。这种模式是完全基于类的，不需要接口。当目标bean被代理时，它会成为子类的实例。这对于那些没有实现接口或需要代理具体的类非常有用。
4. **INTERFACES**: 代理必须通过接口实现。当使用此模式时，bean必须实现至少一个接口，代理将实现这些接口。如果bean没有实现任何接口，此模式将抛出异常。

#### 3.7 resourcePattern

定义要扫描的资源的模式。默认情况下，只有“.class”文件会被扫描。

#### 3.8 useDefaultFilters

是否应使用默认过滤器来检测例如 @Component、@Repository、@Service、@Controller 注解的组件。

#### 3.9 includeFilters

为扫描指定自定义的包括过滤器，即哪些组件应该被Spring上下文包括。

#### 3.10 excludeFilters

为扫描指定自定义的排除过滤器，即哪些组件应该被Spring上下文排除。

#### 3.11 lazyInit

如果为true，则所有通过此扫描注册的组件都将默认为延迟初始化。这意味着它们不会被实例化，除非显式地请求或在注入点被引用。

#### 3.12 Filter

Filter 定义了在组件扫描过程中使用的过滤器，可以基于注解、正则表达式等进行过滤。

###### 3.12.1 type

指定过滤器的类型。默认情况下，它基于注解进行过滤。

###### 3.12.2 value

指定过滤器要搜索的注解或类型。

###### 3.12.3 classes

同 value，指定过滤器要搜索的注解或类型。

###### 3.12.4 pattern

如果 type 被设置为 FilterType.REGEX 或 FilterType.ASPECTJ，则使用该属性指定模式。

### 4、如何使用

#### 4.1、使用basePackages或者value配置扫描包路径

使用basePackages或者value是我们最最最常见的一种配置扫描包的方式了，主要包含默认扫描包与指定扫描包2种方式。

**默认包扫描**

当我们在一个配置类上简单地使用 `@ComponentScan` 无任何参数时，Spring 会扫描该配置类所在的包及其子包下的所有组件和服务。

```java
@Configuration
@ComponentScan
public class MyConfiguration {
    
}
```

**指定包扫描**

使用 `basePackages` 属性来指定扫描的包路径。

```java
@Configuration
@ComponentScan(basePackages = "com.xcs.spring")
public class MyConfiguration {
    
}
```

或者使用 `value` 属性，它是 `basePackages` 的别名

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {
    
}
```

#### 4.2、使用basePackageClasses配置扫描包路径

`basePackageClasses` 允许开发者指定一个或多个类，Spring 会扫描这些类所在的包以及其子包中的所有类，以查找带有 `@Component`, `@Service`, `@Repository`, `@Controller` 以及其他相关注解的类，并将它们注册到 Spring 应用上下文中。

**示例**：

假设你有以下的包结构：

```java
com.xcs.spring
        ├── config
        │   └── MyConfiguration.java
        ├── controller
        │   ├── MyController1.java
        │   └── MyController2.java
        └── service
            ├── MyService1.java
            └── MyService2.java
        └── MyPackageMarker.java
```

如果你想从 `com.xcs.spring` 开始扫描（包括所有子包），你可以在 `com.xcs.spring` 下创建一个标记类，`MyPackageMarker`类并不是为了执行某种实际功能而定义的，而是仅仅作为一个参考或者标记。它通常位于你想要扫描的包的根目录

**例如：**

```java
package com.xcs.spring;

public class MyPackageMarker {

}
```

然后，在你的配置类中使用：

```java
@Configuration
@ComponentScan(basePackageClasses = MyPackageMarker.class)
public class MyConfiguration {
    
}
```

**使用basePackageClasses字段比basePackages或者value有何优势呢？**

使用 `basePackageClasses` 比使用 `basePackages` 优雅，因为它避免了硬编码字符串包名。如果你重构代码并更改包名，那么使用 `basePackageClasses` 的方法会更安全，因为IDE会自动更新类的引用。而硬编码的字符串包名在重构时可能会被遗漏。

#### 4.3、使用nameGenerator自定义Bean的名称

`nameGenerator` 允许我们为Spring的Bean指定一个 `BeanNameGenerator` 实现，该实现将为组件生成bean的名字。

**用法：**

如果你要使用自定义的 `BeanNameGenerator`，只需实现 `BeanNameGenerator` 接口，并在 `@ComponentScan` 中指定该类即可。

```java
@Configuration
@ComponentScan(nameGenerator = MyCustomBeanNameGenerator.class)
public class MyConfiguration {
    
}
```

我们使用自定义的 `MyCustomBeanNameGenerator`，只需实现 `BeanNameGenerator` 接口中的generateBeanName方法，bean的名称根据自己的实际情况生成

```java
public class MyCustomBeanNameGenerator implements BeanNameGenerator {

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        // 此处只是一个示例，我们可以根据自己的实际情况生成Bean名称
        String originalName = definition.getBeanClassName();
        if (originalName != null) {
            return "_这是我自定义Bean名称的前缀_" + originalName;
        } else {
            // 你可以选择其他的逻辑处理或者抛出异常
            throw new IllegalArgumentException("Bean class name is null");
        }
    }
}
```

#### 4.4、使用scopeResolver自定义Bean的作用域

`scopeResolver`属性允许您指定一个自定义的`ScopeMetadataResolver`，它解析bean的作用域元数据。通过这种方式，你可以控制和自定义bean的作用域策略。

**用法：**

如果要使用自定义的`ScopeMetadataResolver`，需要实现`ScopeMetadataResolver`接口并在`@ComponentScan`中指定这个类。

```java
@Configuration
@ComponentScan(scopeResolver = MyCustomScopeMetadataResolver.class)
public class MyConfiguration {
    
}
```

我们使用自定义的 `MyCustomScopeMetadataResolver`，只需实现 `ScopeMetadataResolver` 接口中的resolveScopeMetadata方法，bean的作用于根据自己的实际情况生成。我们此处给`MyService1`与`MyService2`设置成了多例模式，否则我们重新交由`AnnotationScopeMetadataResolver`类解析，如果不由`AnnotationScopeMetadataResolver`类处理的话会导致@Scope注解失效

```java
public class MyCustomScopeMetadataResolver implements ScopeMetadataResolver {

    private AnnotationScopeMetadataResolver resolver = new AnnotationScopeMetadataResolver();

    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        ScopeMetadata scopeMetadata = new ScopeMetadata();

        // 检查Bean的名称是否为'MyService1'或'MyService2'，并据此设置相应的作用域。
        if (MyService1.class.getName().equals(definition.getBeanClassName()) ||
                MyService2.class.getName().equals(definition.getBeanClassName())) {
            scopeMetadata.setScopeName(BeanDefinition.SCOPE_PROTOTYPE);
            return scopeMetadata;
        }
        // 再次交由AnnotationScopeMetadataResolver解析否则会导致@Scope失效
        return resolver.resolveScopeMetadata(definition);
    }
}
```

**注意：**

 `scopedProxy` 和 `scopeResolver` 当两者都被设置时，`scopedProxy` 的优先级更高，并且 `scopeResolver` 会被忽略

#### 4.5、使用`includeFilters `定义哪些组件应该被<font color=red>包含</font>

**基于ANNOTATION过滤**

只有使用`@Service`注解的类会被包含

```java
@Service
public class MyService1 {
    
}

@Service
public class MyService2 {
    
}

// 还有其他被@Controller，@Component标注类再此不展示了。。。

@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class), useDefaultFilters = false)
public class MyComponentScanConfig {
    
}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中有被`@Service`注解标注的类，被`@Controller`，`@Component`等都不在容器中说明基于`includeFilters`字段中的`ANNOTATION`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myService1
beanName = myService2
```

**基于ASSIGNABLE_TYPE过滤**

只有实现`MyInterface`接口的组件会被包含

```java
public interface MyInterface {
    
}

@Component
public class MyComponent implements MyInterface {
    
}

// 还有其他被@Controller，@Component标注类再此不展示了。。。

@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MyInterface.class), useDefaultFilters = false)
public class MyComponentScanConfig {
    
}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中有`MyComponent`类，因为`MyComponent`实现了`MyInterface`接口，而其他被`@Controller`，`@Component`等都不在容器中说明基于`includeFilters`字段中的`ASSIGNABLE_TYPE`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponent
```

**基于CUSTOM过滤**

类名以“Custom”结尾的组件会被包含

```java
public class MyCustomTypeFilter implements TypeFilter {
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return metadataReader.getClassMetadata().getClassName().endsWith("Custom");
    }
}

@Component
public class MyComponentCustom {

}

// 还有其他被@Controller，@Component标注类再此不展示了。。。

@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = MyCustomTypeFilter.class), useDefaultFilters = false)
public class MyComponentScanConfig {
    
}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中有`MyComponentCustom`类，因为`MyComponentCustom`类符合以`“Custom”`结尾的组件，而其他被`@Controller`，`@Component`等都不在容器中说明基于`includeFilters`字段中的`CUSTOM`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponentCustom
```

**基于REGEX过滤**

类名匹配正则表达式`.*ComponentRegex`的组件会被包含。

```java
@Component
public class MyComponentRegex {
    
}

@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*ComponentRegex"), useDefaultFilters = false)
public class MyConfiguration {

}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中有`myComponentRegex`类，因为`MyComponentRegex`类符合以表达式`.*ComponentRegex`的组件，而其他被`@Controller`，`@Component`等都不在容器中说明基于`includeFilters`字段中的`REGEX`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponentRegex
```

**基于AspectJ过滤**

类名匹配AspectJ类型模式`*..*AspectJ`的组件会被包含。

```java
@Component
public class MyComponentAspectJ {
    
}

@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "*..*AspectJ"), useDefaultFilters = false)
public class MyConfiguration {

}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中有`myComponentAspectJ`类，因为`MyComponentRegex`类符合以AspectJ类型模式`*..*AspectJ`的组件，而其他被`@Controller`，`@Component`等都不在容器中说明基于`includeFilters`字段中的`AspectJ`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponentAspectJ
```

**为什么网上很多博主说设置了includeFilters包含规则后，必须要把useDefaultFilters字段设置成false呢？为什么要这样做？**

想象一下，既然你都使用了includeFilters字段，说明你只想在组件扫描中包括那些具有自定义注解的bean，而忽略所有其他的bean。假如说你设置了includeFilters字段，然后`useDefaultFilters`没有设置为`false`，这意味着具有`@Component`, `@Repository`, `@Service`, 和`@Controller`等注解的类会被自动检测并注册为beans，可能会出现重复扫描，可能会出现混淆，违背了我们使用includeFilters做过滤的初衷。因此，通常建议明确地设置`useDefaultFilters`的值，以避免任何混淆和不期望的行为。如果你仅想使用`includeFilters`，最好将`useDefaultFilters`设置为`false`。如果你想使用默认的过滤器和自定义的过滤器，请明确地设置`useDefaultFilters`为`true`。

#### 4.6、使用`excludeFilters`定义哪些组件应该被<font color=red>排除</font>

**基于ANNOTATION过滤**

所有使用`@Service`注解的类将不会被注册。

```java
@Service
public class MyService1 {
    
}

@Service
public class MyService2 {
    
}

// 还有其他被@Controller，@Component标注类再此不展示了。。。

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class))
public class MyComponentScanConfig {
    
}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中没有被`@Service`标注的类，而其他被`@Controller`，`@Component`等都在容器中说明基于`excludeFilters`字段中的`ANNOTATION`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponent
beanName = myComponentAspectJ
beanName = myComponentCustom
beanName = myComponentRegex
beanName = myController1
beanName = myController2
```

**基于ASSIGNABLE_TYPE过滤**

实现`ExcludedInterface`接口的组件都不会被注册。

```java
public interface MyInterface {
    
}

@Component
public class MyComponent implements MyInterface {
    
}

// 还有其他被@Controller，@Component标注类再此不展示了。。。

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MyInterface.class))
public class MyComponentScanConfig {
    
}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中没有`MyComponent`类对象，因为`MyComponent`类实现了`MyInterface`所以被排除了，而其他被`@Controller`，`@Component`等都在容器中说明基于`excludeFilters`字段中的`ASSIGNABLE_TYPE`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponentAspectJ
beanName = myComponentCustom
beanName = myComponentRegex
beanName = myController1
beanName = myController2
beanName = myService1
beanName = myService2
```

**基于CUSTOM过滤**

类名以`"Custom"`结尾的组件都不会被注册。

```java
public class MyCustomTypeFilter implements TypeFilter {
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return metadataReader.getClassMetadata().getClassName().endsWith("Custom");
    }
}

@Component
public class MyComponentCustom {

}

// 还有其他被@Controller，@Component标注类再此不展示了。。。

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = MyCustomTypeFilter.class))
public class MyComponentScanConfig {
    
}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中没有`MyComponentCustom`类对象，因为`MyComponentCustom`类名以`"Custom"`结尾的组件，所以被排除了，而其他被`@Controller`，`@Component`等都在容器中说明基于`excludeFilters`字段中的`CUSTOM`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponent
beanName = myComponentAspectJ
beanName = myComponentRegex
beanName = myController1
beanName = myController2
beanName = myService1
beanName = myService2
```

**基于REGEX过滤**

类名匹配正则表达式`.*ComponentRegex`的组件不会被注册。

```java
@Component
public class MyComponentRegex {
    
}

// 还有其他被@Controller，@Component标注类再此不展示了。。。

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*ComponentRegex"))
public class MyComponentScanConfig {
    
}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中没有`MyComponentRegex`类对象，因为`MyComponentRegex`类名符合表达式`.*ComponentRegex`的组件，所以被排除了，而其他被`@Controller`，`@Component`等都在容器中说明基于`excludeFilters`字段中的`REGEX`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponent
beanName = myComponentAspectJ
beanName = myComponentCustom
beanName = myController1
beanName = myController2
beanName = myService1
beanName = myService2
```

**基于AspectJ过滤**

类名匹配AspectJ类型模式`*..*AspectJ`的组件不会被注册。

```java
@Component
public class MyComponentAspectJ {

}

// 还有其他被@Controller，@Component标注类再此不展示了。。。

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "*..*AspectJ"))
public class MyComponentScanConfig {

}
```

通过main方法运行查看结果

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

通过运行结果发现，在Bean容器中没有`MyComponentAspectJ`类对象，因为`MyComponentAspectJ`类名符合AspectJ类型模式`*..*AspectJ`的组件，所以被排除了，而其他被`@Controller`，`@Component`等都在容器中说明基于`excludeFilters`字段中的`AspectJ`类型过滤已经生效

```java
beanName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanName = org.springframework.context.event.internalEventListenerProcessor
beanName = org.springframework.context.event.internalEventListenerFactory
beanName = myComponentScanConfig
beanName = myComponent
beanName = myComponentCustom
beanName = myComponentRegex
beanName = myController1
beanName = myController2
beanName = myService1
beanName = myService2
```

### 5、源码分析

我们的`ComponentScanApplication`类是main方法入口，那我们就从这里开始跟踪源码，我们使用的是`AnnotationConfigApplicationContext`做为上下文环境，并传入了一个组件类的类名，那么我们继续进入`AnnotationConfigApplicationContext`的构造函数查看源码

```java
public class ComponentScanApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyComponentScanConfig.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanName = " + beanDefinitionName);
        }
    }
}
```

首先我们来看看源码中的，构造函数中，执行了三个步骤，我们重点关注refresh()方法

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

`refresh()`方法中我们重点关注一下`invokeBeanFactoryPostProcessors(beanFactory)`这方法，其他方法不是本次源码阅读的重点暂时忽略，在`invokeBeanFactoryPostProcessors(beanFactory)`方法会对实现了`BeanDefinitionRegistryPostProcessor`，`BeanFactoryPostProcessor`这两个接口进行接口回调。

```java
public void refresh() throws BeansException, IllegalStateException {
    // ----------------------忽略其他代码---------------------------
    // Invoke factory processors registered as beans in the context.
    invokeBeanFactoryPostProcessors(beanFactory);
    // ----------------------忽略其他代码---------------------------
}
```

在`invokeBeanFactoryPostProcessors()`中又委托了`PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`进行调用

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
	PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
	// ----------------------忽略其他代码---------------------------
}
```

在这个`invokeBeanFactoryPostProcessors(beanFactory, beanFactoryPostProcessors)`方法中，主要是对`BeanDefinitionRegistryPostProcessor`，`BeanFactoryPostProcessor`这两个接口的实现类进行回调，至于为什么这个方法里面代码很长呢？其实这个方法就做了一个事就是对处理器的执行顺序在做出来。比如说要先对实现了`PriorityOrdered.class`类回调，在对实现了`Ordered.class`类回调，最后才是对没有实现任何优先级的处理器进行回调。

```java
public static void invokeBeanFactoryPostProcessors(
        ConfigurableListableBeanFactory beanFactory, 
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

    // ----------------------忽略其他代码---------------------------
    
    // 存储已经处理过的bean
    Set<String> processedBeans = new HashSet<>();
    
    // 创建一个新的BeanDefinitionRegistryPostProcessor列表来存放当前正在处理的后处理器。
    List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

    // 获取所有已注册且为BeanDefinitionRegistryPostProcessor类型的bean的名称。
    String[] postProcessorNames =
        beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);

    // 遍历获取到的BeanDefinitionRegistryPostProcessor的名字
    for (String ppName : postProcessorNames) {
        // 筛选出那些还实现了PriorityOrdered接口的BeanDefinitionRegistryPostProcessor
        if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
            // 将筛选出的后处理器实例添加到currentRegistryProcessors列表中
            currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
            processedBeans.add(ppName);
        }
    }

    // 使用sortPostProcessors方法，根据它们的优先级对BeanDefinitionRegistryPostProcessor进行排序。
    sortPostProcessors(currentRegistryProcessors, beanFactory);
    
    // 将当前处理的currentRegistryProcessors添加到全局的registryProcessors列表中。
    registryProcessors.addAll(currentRegistryProcessors);

    // 调用invokeBeanDefinitionRegistryPostProcessors方法执行所有选定的后处理器。
    invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
	
    // ----------------------忽略其他代码---------------------------
}
```

下面是我画的一个时序图大家可以参考一下。

~~~mermaid
sequenceDiagram
    participant Init as invokeBeanFactoryPostProcessors
    participant BDRPP_PO as BDRPP(PriorityOrdered)
    participant BDRPP_O as BDRPP(Ordered)
    participant BDRPP as 其余的BDRPP
    participant BFPP_PO as BFPP(PriorityOrdered)
    participant BFPP_O as BFPP(Ordered)
    participant BFPP as 其余的BFPP

    Init->>BDRPP_PO: 回调
    BDRPP_PO-->>Init: 完成
    Init->>BDRPP_O: 回调
    BDRPP_O-->>Init: 完成
    Init->>BDRPP: 回调
    BDRPP-->>Init: 完成
    Init->>BFPP_PO: 回调
    BFPP_PO-->>Init: 完成
    Init->>BFPP_O: 回调
    BFPP_O-->>Init: 完成
    Init->>BFPP: 回调
    BFPP-->>Init: 完成
    
    Note right of BFPP: 提示: 
    Note right of BFPP: BDRPP = BeanDefinitionRegistryPostProcessor
    Note right of BFPP: BFPP = BeanFactoryPostProcessor


~~~

从截图就可以看出`ConfigurationClassPostProcessor`也实现了`BeanDefinitionRegistryPostProcessor`接口中的`postProcessBeanDefinitionRegistry`方法，其中一个重要的功能就是解析`@ComponentScan`注解。

![image-20230816101148882](https://img-blog.csdnimg.cn/851ff307c3644840b5cee1062c513b4d.png#pic_center)

`invokeBeanDefinitionRegistryPostProcessors(postProcessors)`方法中，循环调用了实现`BeanDefinitionRegistryPostProcessor`接口中的`postProcessBeanDefinitionRegistry(registry)`方法，

```java
private static void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry, ApplicationStartup applicationStartup) {
    for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
        StartupStep postProcessBeanDefRegistry = applicationStartup.start("spring.context.beandef-registry.post-process")
            .tag("postProcessor", postProcessor::toString);
        postProcessor.postProcessBeanDefinitionRegistry(registry);
        postProcessBeanDefRegistry.end();
    }
}
```

在`ConfigurationClassPostProcessor`类中的`postProcessBeanDefinitionRegistry`回调方法中又调用了`processConfigBeanDefinitions(registry)`方法

```java
public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    int registryId = System.identityHashCode(registry);
    if (this.registriesPostProcessed.contains(registryId)) {
        throw new IllegalStateException(
            "postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
    }
    if (this.factoriesPostProcessed.contains(registryId)) {
        throw new IllegalStateException(
            "postProcessBeanFactory already called on this post-processor against " + registry);
    }
    this.registriesPostProcessed.add(registryId);

    processConfigBeanDefinitions(registry);
}
```

在`processConfigBeanDefinitions(registry)`方法中，创建一个用于解析`@Configuration`类的解析器实例(在我们的Demo中只有一个`MyComponentScanConfig`的配置类)，最后调用`parser.parse(candidates)`方法去解析。

```java
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    // ...[省略其他代码]...
    
    // 创建一个用于解析@Configuration类的解析器实例。
    // 传入所需的依赖，如元数据读取器工厂、问题报告器、环境、资源加载器等。
    ConfigurationClassParser parser = new ConfigurationClassParser(
        this.metadataReaderFactory, this.problemReporter, this.environment,
        this.resourceLoader, this.componentScanBeanNameGenerator, registry);
    
    // 初始化待解析的配置类集合以及已解析的配置类集合。
    Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
    Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
    
    // 循环解析候选的@Configuration类，直到所有配置类都被解析。
    do {
        StartupStep processConfig = this.applicationStartup.start("spring.context.config-classes.parse");
        
        // 使用解析器解析当前的候选配置类。如果发现更多的配置类，它们将被添加到candidates集合中。
        parser.parse(candidates);
        
        // 对解析的结果进行校验，确保它们不违反任何约束。
        parser.validate();
    } while (!candidates.isEmpty()); // 如果还有未解析的候选配置类，则继续循环。
    
    // ...[省略其他代码]...
}
```

，

```java
/**
 * 解析给定的配置类候选集。
 * 
 * @param configCandidates 待解析的配置类的BeanDefinitionHolder集合
 */
public void parse(Set<BeanDefinitionHolder> configCandidates) {
    // 遍历每一个配置类候选
    for (BeanDefinitionHolder holder : configCandidates) {
        // 获取BeanDefinition
        BeanDefinition bd = holder.getBeanDefinition();

        try {
            // 如果BeanDefinition是一个AnnotatedBeanDefinition（通过注解定义的）
            if (bd instanceof AnnotatedBeanDefinition) {
                // 根据其元数据和bean名称解析它
                parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
            } 
            // 如果BeanDefinition是一个AbstractBeanDefinition，并且它有一个bean类
            else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
                // 根据其bean类和bean名称进行解析
                parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
            } 
            // 其他情况，直接使用bean的类名进行解析
            else {
                parse(bd.getBeanClassName(), holder.getBeanName());
            }
        } 
        // 捕获BeanDefinitionStoreException，并直接重新抛出
        catch (BeanDefinitionStoreException ex) {
            throw ex;
        } 
        // 捕获其他异常，封装后抛出
        catch (Throwable ex) {
            throw new BeanDefinitionStoreException(
                "Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
        }
    }
    
    // 处理所有延迟的导入选择器
    this.deferredImportSelectorHandler.process();
}
```

在parse使用给定的注解元数据和bean名称创建一个新的`ConfigurationClass`实例。

```java
protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
    processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
}
```

这里的核心逻辑是通过do-while循环递归地调用`doProcessConfigurationClass(configClass, sourceClass, filter)`方法，目的是处理配置类及其超类层次结构。这确保了不仅仅是直接注解了`@Configuration`的类被处理，而且它的所有超类也都被解析。

```java
protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
    // 1. 使用条件评估器判断当前的配置类是否应该被跳过。
    if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationPhase.PARSE_CONFIGURATION)) {
        return;
    }

    // 2. 检查当前的配置类是否已经被处理过。
    ConfigurationClass existingClass = this.configurationClasses.get(configClass);
    
    // 3. 如果该配置类已存在，检查其来源（是否由导入或其他方式得到）。
    if (existingClass != null) {
        if (configClass.isImported()) {
            if (existingClass.isImported()) {
                // 合并两个导入的配置类的来源信息。
                existingClass.mergeImportedBy(configClass);
            }
            // 如果当前类是导入的，但已存在的类不是，则忽略当前类。
            return;
        } else {
            // 如果存在的类是导入的，但当前类不是，则删除旧的类并使用新类。
            this.configurationClasses.remove(configClass);
            this.knownSuperclasses.values().removeIf(configClass::equals);
        }
    }

    // 4. 递归处理配置类及其所有的超类。
    SourceClass sourceClass = asSourceClass(configClass, filter);
    do {
        sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
    } while (sourceClass != null);

    // 5. 在处理完成后，将配置类添加到已处理列表中。
    this.configurationClasses.put(configClass, configClass);
}
```

我们来到`doProcessConfigurationClass(configClass, sourceClass, filter)`方法中，是不是看到了我们非常熟悉的`ComponentScan.class`注解类，我们之前配置的`@ComponentScan`注解就是在此处被解析了，并把解析后的内容封装在一个`AnnotationAttributes`中，然后调用 `this.componentScanParser.parse`进行解析，最后解析到的`BeanDefinitionHolder`进行递归处理，为什么要递归处理？因为扫描到的类还有可能是`@Configuration`标记的类，所以还要再次对扫描到的类继续解析。

```java
protected final SourceClass doProcessConfigurationClass(
        ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
        throws IOException {
	// ----------------------忽略其他代码---------------------------
    
    // 获取sourceClass上的@ComponentScan和@ComponentScans注解信息
    Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
        sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);

    // 判断sourceClass上是否有@ComponentScan或@ComponentScans注解，并且它没有被某种条件（如@Conditional注解）排除
    if (!componentScans.isEmpty() && 
        !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {

        // 遍历每一个@ComponentScan注解的属性
        for (AnnotationAttributes componentScan : componentScans) {

            // 执行具体的组件扫描，并返回扫描到的Bean定义
            Set<BeanDefinitionHolder> scannedBeanDefinitions =
                this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());

            // 遍历每个扫描到的Bean定义，检查其是否为配置类，并递归解析
            for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
                BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
                if (bdCand == null) {
                    bdCand = holder.getBeanDefinition();
                }

                // 如果当前Bean定义是一个配置类候选，则进行递归解析
                if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
                    parse(bdCand.getBeanClassName(), holder.getBeanName());
                }
            }
        }
    }
    // ----------------------忽略其他代码---------------------------
    // No superclass -> processing is complete
    return null;
}
```

我们来到`ComponentScanAnnotationParser`类中的`parse(componentScan,declaringClass)`方法 ，我们在`@ComponentScan`注解中定义的配置都被封装了了`AnnotationAttributes`类中，最后通过一个`ClassPathBeanDefinitionScanner`类扫描器去处理，并把我们之前在`MyComponentScanConfig`类中设置的`@ComponentScan`参数全部传入给这个扫描器。

```java
public Set<BeanDefinitionHolder> parse(AnnotationAttributes componentScan, final String declaringClass) {

    // 1. 创建扫描器，设置是否使用默认的过滤器，如@Component、@Service等
    ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(
        this.registry, componentScan.getBoolean("useDefaultFilters"), this.environment, this.resourceLoader);

    // 2. 设置Bean命名生成策略
    Class<? extends BeanNameGenerator> generatorClass = componentScan.getClass("nameGenerator");
    boolean useInheritedGenerator = (BeanNameGenerator.class == generatorClass);
    scanner.setBeanNameGenerator(useInheritedGenerator ? this.beanNameGenerator : 
                                 BeanUtils.instantiateClass(generatorClass));

    // 3. 设置作用域代理模式
    ScopedProxyMode scopedProxyMode = componentScan.getEnum("scopedProxy");
    if (scopedProxyMode != ScopedProxyMode.DEFAULT) {
        scanner.setScopedProxyMode(scopedProxyMode);
    }
    // 如果没有设置scopedProxy，就使用scopeResolver定义的解析器
    else {
        Class<? extends ScopeMetadataResolver> resolverClass = componentScan.getClass("scopeResolver");
        scanner.setScopeMetadataResolver(BeanUtils.instantiateClass(resolverClass));
    }

    // 4. 设置资源的搜索模式，例如 "*.class"
    scanner.setResourcePattern(componentScan.getString("resourcePattern"));

    // 5. 根据includeFilters添加包括类型的过滤器
    for (AnnotationAttributes filter : componentScan.getAnnotationArray("includeFilters")) {
        for (TypeFilter typeFilter : typeFiltersFor(filter)) {
            scanner.addIncludeFilter(typeFilter);
        }
    }

    // 6. 根据excludeFilters添加排除类型的过滤器
    for (AnnotationAttributes filter : componentScan.getAnnotationArray("excludeFilters")) {
        for (TypeFilter typeFilter : typeFiltersFor(filter)) {
            scanner.addExcludeFilter(typeFilter);
        }
    }

    // 7. 设置懒加载
    boolean lazyInit = componentScan.getBoolean("lazyInit");
    if (lazyInit) {
        scanner.getBeanDefinitionDefaults().setLazyInit(true);
    }

    // 8. 确定要扫描的基本包
    Set<String> basePackages = new LinkedHashSet<>();
    String[] basePackagesArray = componentScan.getStringArray("basePackages");
    for (String pkg : basePackagesArray) {
        String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg),
                                                               ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        Collections.addAll(basePackages, tokenized);
    }
    for (Class<?> clazz : componentScan.getClassArray("basePackageClasses")) {
        basePackages.add(ClassUtils.getPackageName(clazz));
    }
    if (basePackages.isEmpty()) {
        basePackages.add(ClassUtils.getPackageName(declaringClass));
    }

    // 9. 排除@ComponentScan的声明类自身，防止自己被扫描
    scanner.addExcludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
        @Override
        protected boolean matchClassName(String className) {
            return declaringClass.equals(className);
        }
    });

    // 10. 执行扫描并返回扫描到的Bean定义
    return scanner.doScan(StringUtils.toStringArray(basePackages));
}
```

我们来看看这个`ClassPathBeanDefinitionScanner`类的构造方法，在这个类中有一个重要的初始化过程就是`registerDefaultFilters()`方法主要作用就是注册一个默认过滤器，有一个条件判断`useDefaultFilters`，而我们在启动的时候`@ComponentScan`注解中配置了`useDefaultFilters`为true，默认值也是true。

```java
public ClassPathBeanDefinitionScanner(
        BeanDefinitionRegistry registry,
        boolean useDefaultFilters,
        Environment environment,
        @Nullable ResourceLoader resourceLoader) {

    // 确保registry不为null，否则抛出异常。
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");

    // 将registry参数赋值给类成员变量。
    this.registry = registry;

    // 如果设置为使用默认过滤器，则注册这些默认过滤器。
    if (useDefaultFilters) {
        registerDefaultFilters();
    }

    // 设置扫描器的环境。
    setEnvironment(environment);

    // 设置资源加载器。
    setResourceLoader(resourceLoader);
}
```

个方法确保了类路径扫描器可以识别带有`@Component`, 像我们的`@Service`，`@Controller`，`@Repository`都是`@Component`派生出来的注解，所以这些派生注解都能被扫描到，另外还注册了两个注解`@ManagedBean`和`@Named`注解的类。如果某些注解的库（如JSR-250或JSR-330）不在类路径上，扫描器会安全地跳过它们，不会导致任何错误。

```java
protected void registerDefaultFilters() {
    // 添加一个默认的类型过滤器，用于检测带有@Component注解的类
    this.includeFilters.add(new AnnotationTypeFilter(Component.class));
    
    // 获取当前类的类加载器
    ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
    
    // 尝试为JSR-250的ManagedBean注解添加一个类型过滤器
    try {
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false));
        logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // 如果JSR-250的ManagedBean类不在类路径上，只需简单跳过。
    }
    
    // 尝试为JSR-330的Named注解添加一个类型过滤器
    try {
        this.includeFilters.add(new AnnotationTypeFilter(
            ((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
        logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
    }
    catch (ClassNotFoundException ex) {
        // 如果JSR-330的Named类不在类路径上，只需简单跳过。
    }
}
```

我们回到`ComponentScanAnnotationParser`类中的`parse(componentScan,declaringClass)`方法来，在这个方法的最后会调用`ClassPathBeanDefinitionScanner`类中`doScan(basePackages)`方法中对于每个包名，它都会查找候选组件。这些候选组件基本上是那些可能需要被Spring管理的类，例如带有@Component、@Service、@Repository或@Controller注解的类。

```java
/**
 * 扫描指定的基础包，并为找到的类注册bean定义。
 * 
 * @param basePackages 需要扫描的基础包名称。
 * @return 找到并注册的bean定义集合。
 */
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    // 确保提供的包名不为空
    Assert.notEmpty(basePackages, "At least one base package must be specified");

    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();

    for (String basePackage : basePackages) {
        // 对给定的包进行扫描，查找候选组件
        Set<BeanDefinition> candidates = findCandidateComponents(basePackage);

        for (BeanDefinition candidate : candidates) {
            // 确定bean的作用域（如：singleton, prototype等）
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            candidate.setScope(scopeMetadata.getScopeName());

            // 为bean定义生成一个唯一的名称
            String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);

            // 对AbstractBeanDefinition类型的bean定义进行后处理
            if (candidate instanceof AbstractBeanDefinition) {
                postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
            }

            // 对带注解的bean定义进行处理，例如处理@Autowired、@Value等注解
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }

            // 检查当前bean的名称是否已在注册表中使用
            if (checkCandidate(beanName, candidate)) {
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                // 如果bean具有特定的作用域（例如会话作用域），可能需要应用一个代理
                definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                
                beanDefinitions.add(definitionHolder);

                // 在bean定义注册表中注册bean定义
                registerBeanDefinition(definitionHolder, this.registry);
            }
        }
    }
    return beanDefinitions;
}
```

在看看`doScan`方法中调用的`findCandidateComponents`方法，这个方法首先对`spring.components`文件提供的配置进行扫描，目的是可以提高扫描速度，有兴趣的朋友可以去研究一下`@Component`注解中的`@Indexed`注解。我们本次测试是没有使用到`spring.components`文件加速，所以我们继续查看`scanCandidateComponents(basePackage)`方法。

```java
public Set<BeanDefinition> findCandidateComponents(String basePackage) {
    // 如果存在一个预建的组件索引（例如，通过spring.components文件），并且该索引支持当前配置的过滤条件
    if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
        // 使用该索引来查找符合条件的组件，这通常更快
        return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
    }
    // 如果没有预建索引或索引不支持当前的过滤条件，则进行传统的运行时扫描
    else {
        // 扫描basePackage下的所有类，查找标记为Spring组件的类
        return scanCandidateComponents(basePackage);
    }
}
```

从指定的包路径`basePackage`中扫描并识别Spring管理的组件，并将它们返回为一组`BeanDefinition`。方法首先构建了一个搜索路径（`classpath*:com/xcs/spring/**/*.class`），用于在classpath中查找所有相关的资源。然后遍历每个找到的资源，尝试读取其元数据，并根据元数据判断该资源是否是一个有效的Spring组件。符合条件的组件被转化为`BeanDefinition`对象并添加到结果集中。

```java
private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
    // 初始化一个用于保存候选Bean定义的集合
    Set<BeanDefinition> candidates = new LinkedHashSet<>();

    try {
        // 构建包路径的搜索字符串，它将在classpath中查找所有匹配的资源
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
            resolveBasePackage(basePackage) + '/' + this.resourcePattern;

        // 获取与搜索路径匹配的所有资源
        Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
        
        // 日志标志，用于后续的日志记录判断
        boolean traceEnabled = logger.isTraceEnabled();
        boolean debugEnabled = logger.isDebugEnabled();

        // 遍历所有找到的资源
        for (Resource resource : resources) {
            if (traceEnabled) {
                logger.trace("Scanning " + resource);
            }

            // 如果资源是可读的，尝试读取它的元数据
            if (resource.isReadable()) {
                try {
                    // 获取资源的元数据读取器
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);

                    // 判断资源是否是一个候选组件
                    if (isCandidateComponent(metadataReader)) {
                        // 创建一个ScannedGenericBeanDefinition对象，并设置其资源
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setSource(resource);

                        // 进一步确认资源是否是一个候选组件
                        if (isCandidateComponent(sbd)) {
                            if (debugEnabled) {
                                logger.debug("Identified candidate component class: " + resource);
                            }
                            candidates.add(sbd);
                        }
                        else {
                            if (debugEnabled) {
                                logger.debug("Ignored because not a concrete top-level class: " + resource);
                            }
                        }
                    }
                    else {
                        if (traceEnabled) {
                            logger.trace("Ignored because not matching any filter: " + resource);
                        }
                    }
                }
                // 处理可能的异常
                catch (Throwable ex) {
                    throw new BeanDefinitionStoreException(
                        "Failed to read candidate component class: " + resource, ex);
                }
            }
            else {
                if (traceEnabled) {
                    logger.trace("Ignored because not readable: " + resource);
                }
            }
        }
    }
    // 处理I/O异常
    catch (IOException ex) {
        throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
    }
    
    // 返回识别出的Bean定义
    return candidates;
}
```

`isCandidateComponent`方法根据指定的排除和包含过滤器，判断一个类是否符合作为Spring组件的候选条件

```java
protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
    // 遍历所有的排除过滤器
    for (TypeFilter tf : this.excludeFilters) {
        // 如果当前类匹配任何一个排除过滤器，则返回false，表示它不是候选组件
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            return false;
        }
    }

    // 遍历所有的包含过滤器
    for (TypeFilter tf : this.includeFilters) {
        // 如果当前类匹配任何一个包含过滤器
        if (tf.match(metadataReader, getMetadataReaderFactory())) {
            // 则进一步检查它是否满足其他条件，例如@Condition注解
            return isConditionMatch(metadataReader);
        }
    }
    
    // 如果既没有匹配到排除过滤器也没有匹配到包含过滤器，则返回false
    return false;
}
```

我们回到`ClassPathBeanDefinitionScanner`类中`doScan(basePackages)`方法方法来，在`doScan`中调用了`checkCandidate`方法，该`checkCandidate`方法验证新的`beanDefinition`在`beanDefinitionMap`内现有的是否冲突，并在冲突时抛出异常，确保bean名称的唯一性和兼容性。

```java
protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
    // 检查注册中心是否已经包含该bean名称的定义
    if (!this.registry.containsBeanDefinition(beanName)) {
        // 若不包含，则为有效候选
        return true;
    }
    
    // 获取当前注册中心已存在的bean定义
    BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
    
    // 检查是否存在原始的Bean定义，若存在，则使用原始的定义
    BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
    if (originatingDef != null) {
        existingDef = originatingDef;
    }
    
    // 检查新的bean定义与现有定义是否兼容
    if (isCompatible(beanDefinition, existingDef)) {
        return false;
    }
    
    // 如果新的bean定义与现有定义不兼容，抛出异常
    throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName +
                                                 "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                                                 "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
}
```

`registerBeanDefinition`方法又委托了`BeanDefinitionReaderUtils.registerBeanDefinition`去执行

```java
protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
}
```

最后我们扫描出来的`BeanDefinition`都通过`BeanDefinitionRegistry`注册的Spring上下文中(`AnnotationConfigApplicationContext`)

```java
public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

    // 获取BeanDefinitionHolder中的bean名称
    String beanName = definitionHolder.getBeanName();
    // 使用bean名称将BeanDefinition注册到BeanDefinitionRegistry中
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

    // 获取BeanDefinitionHolder中的所有别名
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        // 循环每个别名并将其注册到BeanDefinitionRegistry中，与主bean名称关联
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

下面是调用时序图



~~~mermaid
sequenceDiagram
title:@ComponentScan注解源码时序图
ComponentScanApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()
AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)
AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup) 
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor:postProcessBeanDefinitionRegistry(registry)
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor:processConfigBeanDefinitions(registry)
ConfigurationClassPostProcessor->>ConfigurationClassParser:ConfigurationClassParser(...)
ConfigurationClassParser-->>ConfigurationClassPostProcessor:返回解析解析器
ConfigurationClassPostProcessor->>ConfigurationClassParser:parser.parse(candidates)
ConfigurationClassParser->>ConfigurationClassParser:parse(metadata, String beanName)
ConfigurationClassParser->>ConfigurationClassParser:processConfigurationClass(configClass,filter)
ConfigurationClassParser->>ConfigurationClassParser:doProcessConfigurationClass(configClass,sourceClass,filter)
ConfigurationClassParser->>ComponentScanAnnotationParser:parse(componentScan,declaringClass)
ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner:ClassPathBeanDefinitionScanner(registry,useDefaultFilters,environment,resourceLoader) 
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerDefaultFilters()
ClassPathBeanDefinitionScanner-->>ComponentScanAnnotationParser:返回扫描器
ComponentScanAnnotationParser->>ClassPathBeanDefinitionScanner:doScan(basePackages)
ClassPathBeanDefinitionScanner->>ClassPathScanningCandidateComponentProvider:findCandidateComponents(basePackage)
ClassPathScanningCandidateComponentProvider->>ClassPathScanningCandidateComponentProvider:scanCandidateComponents(basePackage)
ClassPathScanningCandidateComponentProvider-->>ClassPathBeanDefinitionScanner:返回BeanDefinition
ClassPathBeanDefinitionScanner->>ClassPathBeanDefinitionScanner:registerBeanDefinition(definitionHolder,registry)
ClassPathBeanDefinitionScanner->>BeanDefinitionReaderUtils:registerBeanDefinition(definitionHolder, registry)
BeanDefinitionReaderUtils->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)
ClassPathBeanDefinitionScanner-->>ComponentScanAnnotationParser:返回BeanDefinition
~~~



### 6、常见问题

以下示例只是为了说明可能的问题，并不代表实际的错误代码。我们大家在实际开发中，应确保清楚了解每个注解的含义和效果。

#### 6.1、默认的扫描路径

如果不指定`basePackages`或`basePackageClasses`，`@ComponentScan`默认会从声明这个注解的类的包开始扫描。如果配置类不在根包路径下，可能会错过某些组件。

```java
package com.xcs.spring.config;

@Configuration
// 默认会从此类所在的包开始扫描 (默认扫描路径为：com.xcs.spring.config)
// 如果你有一个Service类需要扫描,路径为:com.xcs.spring.service.MyService1此时会错过这个组件扫描
@ComponentScan 
public class AppConfig {

}
```

#### 6.2、过度扫描

如果`basePackages`设置得过于宽泛，可能会无意中扫描到许多不必要的bean，导致启动时间增加和不必要的bean实例化。

```java
@Configuration
// 这将扫描com包下的所有子包，可能会扫描到很多不必要的组件
// com下可能有成千上万的类，导致启动缓慢
// 如果项目结构复杂，类很多，`@ComponentScan`可能导致启动速度减慢，因为它需要扫描大量的类。
@ComponentScan(basePackages = "com")  
public class AppConfig {

}
```

#### 6.3、过滤问题

 `includeFilters`和`excludeFilters`的不恰当设置可能导致意外地包含或排除某些组件。

```java
@Configuration
// 这里可能误包含了某些@Service组件或误排除了某些@Repository组件
@ComponentScan(basePackages = "com.xcs",
               includeFilters = @Filter(type = FilterType.ANNOTATION, classes = Service.class),
               excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class AppConfig {

}
```

#### 6.4、重复的bean定义

 不同包中的类可能有相同的简单名称。默认情况下，bean名称是基于简单类名的，这可能导致bean名称冲突。

```java
// 在com.xcs.a包中
@Component
public class MyService {
}

// 在com.xcs.b包中
@Component
public class MyService {
	// com.xcs.a包中的MyService同名
}
```

#### 6.5、与其他配置混淆

当使用`@ComponentScan`与XML配置或其他Java配置结合时，可能会有意外的bean定义覆盖。

在下述示例中，由于`MyServiceImpl`使用了`@Service`注解，并指定了相同的bean名称`myService`，因此`@ComponentScan`会扫描并注册这个bean。但在XML配置中，也为相同的bean名称提供了一个定义。

当Spring IoC容器初始化并处理bean定义时，由于两个定义具有相同的bean名称，因此后处理的定义（取决于加载顺序）可能会覆盖先处理的定义。这可能会导致意外的行为，例如使用了错误的bean属性或bean类。

为了避免此类问题，需要确保在整个应用程序中为每个bean提供一个唯一的名称，并明确知道哪个配置源负责定义哪个特定的bean。如果确实需要在多个配置源中定义同一个bean，最好明确地指定哪个定义应该优先。

```java
@Configuration
@ComponentScan(basePackages = "com.xcs.spring")
public class AppConfig {
}

@Service("myService")
public class MyServiceImpl implements MyService {
    private String message = "Hello from @Service!";

    // getter and setter
}

// 在XML中
<!-- 可能会覆盖上面的某些bean定义 -->
<beans>
    <bean id="myService" class="com.example.MyServiceImpl">
        <property name="message" value="Hello from XML!"/>
    </bean>
</beans>
```

#### 6.6、条件扫描的误区

当使用`@Conditional`注解与`@ComponentScan`结合时，要确保条件正确配置，否则可能导致组件不被扫描。

例如，你可能想在开发环境中注册一个bean，但在生产环境中不这么做。或者，如果数据库类型是H2，你可能想注册一个bean，但如果是MySQL，则不这么做。

```java
@Component
// DevComponent bean只有在OnDevelopmentCondition条件满足时才会被Spring容器管理。
@Conditional(OnDevelopmentCondition.class)
public class DevComponent {
   // ...
}
```

```java
public class OnDevelopmentCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "dev".equals(context.getEnvironment().getProperty("spring.profiles.active"));
    }
}
```

在这个条件实现中，我们检查当前激活的Spring配置文件是否为"dev"。

如果你在使用`@ComponentScan`的时候忘记正确配置这样的条件或者误配置，可能会遇到以下情况：

1. **预期的bean没有被注册**：如果你期望在某些条件下`DevComponent`被注册，但条件没有满足（例如，配置文件不正确或环境属性设置错误），则该bean将不会被扫描和注册。
2. **不希望的bean被注册**：如果条件误配置并错误地满足了，可能会导致本不应该在当前环境中创建的bean被注册。
3. **应用程序启动失败**：如果其他bean依赖于基于条件创建的bean，并且条件没有满足，那么应用程序在启动时可能会因为找不到依赖的bean而失败。

所以我们大家在使用`@Conditional`与`@ComponentScan`结合时，非常重要的是要确保你的条件是正确配置的，以确保spring程序中的bean正确的被加载。

#### 6.7、第三方库的扫描

不小心扫描了第三方库，可能会导致不必要的bean被注册或者出现版本冲突。

```java
@Configuration
// 可能导致第三方库不必要的bean注册
@ComponentScan(basePackages = {"com.xcs.spring", "com.alibaba.dubbo"})  
public class AppConfig {
	
}
```

#### 6.8、忽视了`useDefaultFilters`

 有时我们使用了`includeFilters`包含过滤器，但是可能忘记将`useDefaultFilters`设置为`false`，导致除了自定义过滤器外，还应用了默认的过滤器。

```java
@Configuration
@ComponentScan(basePackages = "com.xcs.spring", 
               useDefaultFilters = true,  // 使用了默认过滤器
               includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyAnnotation.class))
// 既使用了默认过滤器又使用了自定义过滤器
public class AppConfig {
    
}
```

因为`useDefaultFilters`默认值为`true`，Spring将继续按默认的注解(`@Component`, `@Service`, `@Repository`, `@Controller`等)进行组件扫描，即使你可能只想按自定义的`includeFilters`进行扫描

### 7、总结

在你阅读完本次`@ComponentScan`注解的源码分析后，我们来大概做一下总结，在第一部分中首先是我们介绍了`@ComponentScan`注解说明。在第二部分中我们从spring源码总摘取了`@ComponentScan`注解的源码，从中了解了这个注解中有许多的字段。在第三部分中我们我们介绍了`@ComponentScan`注解的字段，比如说你要配置扫描包路径有`value`，`basePackages`，`basePackageClasses`三个字段都可以配置。又比如说你想设置Bean名称生成策略，那么你可以使用到`nameGenerator`字段，然后注解中有一个非常重要字段就是`useDefaultFilters`，当它的值被设置成true是会扫描到 @Component、@Repository、@Service、@Controller 注解的组件，最后是`includeFilters`，`excludeFilters`两个做过滤的字段。在第四部分中我们介绍了`@ComponentScan`注解如何使用，如果你忘记了可以回头在仔细看看上面的介绍。来到第五部分是我们的源码分析，在源码分析过程中我们发现`@ComponentScan`本身定义了多个属性，如 `basePackages`、`basePackageClasses` 和多种过滤器 `(includeFilters/excludeFilters`)，当 Spring 容器启动并读取到配置类标记有 `@ComponentScan`时，会被我们的核心类`ConfigurationClassParser` 被用来解析配置类。在此过程中，会处理该注解，并确定要扫描的包路径。实际的扫描由 `ClassPathBeanDefinitionScanner` 完成。这个扫描器读取 `@ComponentScan` 的属性，并在指定的包路径下搜索候选的组件，扫描器在搜索组件时会使用 `includeFilters` 和 `excludeFilters`，这两个过滤器列表确定哪些组件应被包括和哪些应被排除，扫描完毕后，找到的组件会被解析并注册到 IOC容器中，从而允许后续的 bean 生命周期处理和自动装配功能。第六部分主要是介绍了一些常见问题。好啦，本次分析到此结束