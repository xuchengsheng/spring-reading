## @Resource



### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [我的CSDN](https://blog.csdn.net/duzhuang2399/article/details/133887864) 📚 **文章目录** - [所有文章](https://github.com/xuchengsheng/spring-reading) 🔗 **源码地址** - [@Resource源码](https://github.com/xuchengsheng/spring-reading/blob/master/spring-jsr/spring-jsr250-resource)

### 二、接口描述

`@Resource` 注解是由 JSR-250: Common Annotations for the Java Platform 规范定义的。这个规范定义了一组跨多个 Java 技术（如 Java EE 和 Java SE）的公共注解。

### 三、接口源码

`@Resource` 注解的目的是为了声明和注入应用程序所需的外部资源，从而允许容器在运行时为应用程序组件提供这些资源。

```java
/**
 * 标注需要注入的资源的注解。
 * 这个注解可以用于类、字段和方法上，指示容器为其注入资源。
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Resources.class)
public @interface Resource {

    /**
     * 资源的 JNDI 名称。
     *
     * @return 资源名称。
     */
    String name() default "";

    /**
     * JNDI 查询名称，用于运行时查找资源。
     *
     * @return JNDI 查询名称。
     */
    String lookup() default "";

    /**
     * 资源的期望类型。默认为 Object，意味着类型不特定。
     *
     * @return 资源类型。
     */
    Class<?> type() default Object.class;

    /**
     * 资源的身份验证类型。
     *
     * @return 身份验证类型。
     */
    Resource.AuthenticationType authenticationType() default Resource.AuthenticationType.CONTAINER;

    /**
     * 标示资源是否可以被多个客户端共享。
     *
     * @return 如果资源可以被共享则返回 true，否则返回 false。
     */
    boolean shareable() default true;

    /**
     * 与资源环境引用关联的产品特定的名称。
     *
     * @return 映射名称。
     */
    String mappedName() default "";

    /**
     * 对资源的简要描述。
     *
     * @return 资源描述。
     */
    String description() default "";

    /**
     * 身份验证类型的枚举。
     * CONTAINER: 容器管理身份验证。
     * APPLICATION: 应用程序管理身份验证。
     */
    public static enum AuthenticationType {
        CONTAINER,
        APPLICATION;
    }
}
```

### 四、主要功能

1. **资源定位**
   + 通过 `name` 和 `lookup` 属性，`@Resource` 可以定位到特定的资源，如 JNDI 中的一个数据库连接。
2. **类型指定**
   + 通过 `type` 属性，它允许指定所需资源的具体Java类型，确保注入的资源与预期类型匹配，从而提供类型安全。
3. **身份验证策略**
   + `authenticationType` 属性允许开发者选择资源的身份验证方式，决定是由容器还是应用程序来进行身份验证。
4. **共享策略**
   + 通过 `shareable` 属性，它指定资源是否可以在多个客户端或组件之间共享。
5. **供应商特定名称**
   + `mappedName` 属性可以提供与资源关联的供应商或平台特定的名称，增加部署的灵活性。
6. **描述信息**
   + 通过 `description` 属性，为资源提供了简要描述，有助于开发者和系统管理员理解其用途。

### 五、最佳实践

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类。然后从Spring上下文中获取一个`MyController`类型的bean并调用了`showService`方法，

```java
public class ResourceApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

在`MyConfiguration`类中，使用了`@ComponentScan("com.xcs.spring")`注解告诉 Spring 在指定的包（在这里是 "`com.xcs.spring`"）及其子包中搜索带有 `@Component`、`@Service`、`@Repository` 和 `@Controller` 等注解的类，并将它们自动注册为 beans。这样，spring就不必为每个组件明确写一个 bean 定义。Spring 会自动识别并注册它们。

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

}
```

Spring 容器在初始化 `MyController` 时，我们使用了`@Resource`注解，会自动注入一个 `MyService` 类型的 bean 到 `myService` 字段。

```java
@Controller
public class MyController {

    @Resource(name="myService")
    private MyService myService;

    public void showService(){
        System.out.println("myService = " + myService);
    }
}
```

`MyService` 是一个简单的服务类，但我们没有定义任何方法或功能。

```java
@Service
public class MyService {
    
}
```

运行结果发现，我们使用 `@Resource` 注解的功能，在我们的 Spring 上下文中工作正常，并且它成功地自动注入了所需的依赖关系。

```java
myService = com.xcs.spring.service.MyService@f0c8a99
```

### 六、时序图

~~~mermaid
sequenceDiagram
Title: @Resource注解时序图
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>应用Bean定义的后置处理器
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>处理已合并的Bean定义
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:findResourceMetadata(beanName,clazz,pvs)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:buildResourceMetadata(clazz)
CommonAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalFields(clazz,fc)<br>处理类的本地字段
ReflectionUtils->>CommonAnnotationBeanPostProcessor:解析有@Resource注解的字段
CommonAnnotationBeanPostProcessor->>ResourceElement:ResourceElement(member,ae,pd)
CommonAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>处理类的本地方法
ReflectionUtils->>CommonAnnotationBeanPostProcessor:解析有@Resource注解的方法
CommonAnnotationBeanPostProcessor->>ResourceElement:ResourceElement(member,ae,pd)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:injectionMetadataCache.put(cacheKey, metadata)<br>将元数据存入缓存
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)<br>填充Bean的属性值
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>后处理Bean的属性
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:findResourceMetadata(beanName,clazz,pvs)
Note right of CommonAnnotationBeanPostProcessor:<br>从缓存中获取注入的元数据
CommonAnnotationBeanPostProcessor->>InjectionMetadata:inject(target,beanName,pvs)
InjectionMetadata->>InjectedElement:inject(target,requestingBeanName,pvs)
InjectedElement->>ResourceElement:getResourceToInject(target,requestingBeanName)
ResourceElement->>CommonAnnotationBeanPostProcessor:getResource(element,requestingBeanName)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:autowireResource(factory,element,requestingBeanName)
CommonAnnotationBeanPostProcessor->>AbstractAutowireCapableBeanFactory:resolveBeanByName(name,descriptor)
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:返回被依赖的Bean
CommonAnnotationBeanPostProcessor->>ResourceElement:返回被依赖的Bean
ResourceElement->>InjectedElement:返回被依赖的Bean
InjectedElement->>Field:field.set(target, 返回被依赖的Bean)
~~~

### 七、源码分析

### 八、注意事项

### 九、总结

#### 最佳实践总结

#### 源码分析总结