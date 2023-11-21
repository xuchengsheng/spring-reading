<div align="center">
    <img alt="logo" src="image/banner.png" style="height: 80px">
</div>
<div align="center">
    <h2>深入Spring，从源码开始！</h2>
    <h4>探索Java最受欢迎的框架，理解它的内部机制，带大家从入门到精通。</h4>
</div>
<p align="center">
    <a href="https://github.com/xuchengsheng/spring-reading/stargazers"><img src="https://img.shields.io/github/stars/xuchengsheng/spring-reading?logo=github&logoColor=%23EF2D5E&label=Stars&labelColor=%23000000&color=%23EF2D5E&cacheSeconds=3600" alt="Stars Badge"/></a>
    <a href="https://github.com/xuchengsheng"><img src="https://img.shields.io/github/followers/xuchengsheng?label=Followers&logo=github&logoColor=%23FC521F&labelColor=%231A2477&color=%23FC521F&cacheSeconds=3600" alt="Follow Badge"></a>
    <a href="https://github.com/xuchengsheng/spring-reading/fork"><img src="https://img.shields.io/github/forks/xuchengsheng/spring-reading?label=Forks&logo=github&logoColor=%23F2BB13&labelColor=%23BE2323&color=%23F2BB13" alt="Fork Badge"></a>
    <a href="https://github.com/xuchengsheng/spring-reading/watchers"><img src="https://img.shields.io/github/watchers/xuchengsheng/spring-reading?label=Watchers&logo=github&logoColor=%23FF4655&labelColor=%234169E1&color=%23FF4655&cacheSeconds=3600" alt="Watchers Badge"></a>
</p>
<p align="center">
    <img src="https://visitor-badge.lithub.cc/badge?page_id=github.com/xuchengsheng&left_text=Visitors" alt="Visitor Badge"/>
    <img src="https://img.shields.io/badge/WeChat-xcs19930428-%2307C160?logo=wechat" alt="Wechat Badge"/>
    <a href="https://blog.csdn.net/duzhuang2399"><img src="https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fblog.csdn.net%2Fduzhuang2399&query=%2F%2F*%5B%40id%3D%22userSkin%22%5D%2Fdiv%5B1%5D%2Fdiv%5B2%5D%2Fdiv%5B1%5D%2Fdiv%2Fdiv%5B2%5D%2Fdiv%5B1%5D%2Fdiv%5B1%5D%2Fdiv%5B2%5D%2Fspan&logo=C&logoColor=red&label=CSDN&color=red&cacheSeconds=3600" alt="CSDN Badge"></a>
</p>
<p align="center">
    ⚡ <a href="#技术">技术</a>
    |
    👋 <a href="#简介">简介</a>
    |
    🍵 <a href="#为何做Spring源码分析">Why</a>
    |
    🙏 <a href="#顺手点个星">点个星</a>
    |
    🌱 <a href="#spring-源码阅读系列">Spring源码</a>
    |
    💬 <a href="#与我联系">联系我</a>
    |
    ⛵ <a href="#欢迎贡献">贡献</a>
    |
    🔄 <a href="#持续更新中">更新</a>
    |
    💻 <a href="#我的-github-统计">统计</a>
</p>

---

## ⚡技术

<div align="left">
    <img src="https://img.shields.io/badge/Java-1.8%2B-%23437291?logo=openjdk&logoColor=%23437291"/>
    <img src="https://img.shields.io/badge/Spring-5.3.10-%23437291?logo=Spring&logoColor=%236DB33F&color=%236DB33F"/>
    <img src="https://img.shields.io/badge/SpringBoot-2.5.5-%23437291?logo=SpringBoot&logoColor=%236DB33F&color=%236DB33F"/>
    <img src="https://img.shields.io/badge/Maven-3.6.3-%23437291?logo=Apache%20Maven&logoColor=%23C71A36&color=%23C71A36"/>
    <img src="https://img.shields.io/badge/JSR-330-%2366CCFF?logo=OpenJDK&logoColor=%2366CCFF&color=%2366CCFF"/>
    <img src="https://img.shields.io/badge/JSR-250-%23FF9900?logo=OpenJDK&logoColor=%23FF9900&color=%23FF9900"/>
</div>

## 👋简介
大家好呀，我是Lex👨‍💻。我是一名拥有8年经验的Java 后端开发人员👨‍💼，也是一个对 Spring 框架充满热情❤️的程序员。为了帮助那些希望深入了解 Spring 框架的程序员们🧑‍💻，我创建了这个 “Spring 源码阅读系列”📖。通过这个系列，我希望能够与你们共同探索 Spring 的内部工作机制⚙️。如果您有同样的兴趣或问题🤔，请联系我📩！

## 🍵**为何做Spring源码分析**
在我作为框架研发的开发者👨‍🔬的工作中，我经常遇到需要深入理解和调整框架行为的情况🔧。这些工作不只是简单地使用框架的API，更多地是需要对框架的内部工作方式有详细的了解🔍。虽然Github上有关于Spring的简化版本📦，这些对于入门学习确实有很大的帮助✅，但当涉及到真实的项目应用时，与真正的Spring框架还是有很大的差异❌。因此，我开始深入研究Spring的源码，希望能够更透彻地理解其内部的工作机制，以便更好地应用到我的实际工作中🧰。分享我的源码分析📝，也是为了给那些希望真正理解Spring，而不仅仅是使用它的开发者提供一些参考和帮助🙌。

## 🙏顺手点个星
亲爱的朋友们👥，我真的花了很多心思💭去研究和整理这个“Spring 源码阅读系列”📘。如果你觉得这东西还不错👍，或者给你带来了一点点帮助🤗，麻烦点一下星星吧🌟。这真的对我意义重大🎖，每一颗星✨都能让我觉得所有的努力都是值得的💪。我知道这是小事一桩，但你的那一下点击🖱，对我来说就是最好的鼓励🎉。无论如何，都要感谢你抽时间🕰阅读我的内容，真的很感激🙏！

## 🌱Spring 源码阅读系列

#### Spring IOC

- 资源加载与访问
  - [`Resource`](spring-resources/spring-resource/README.md)：抽象接口，表示文件、类路径等，用于访问不同来源的资源。
  - [`ResourceLoader`](spring-resources/spring-resource-resourceLoader/README.md)：资源获取核心接口，实现统一加载不同位置资源的策略。
  - [`DocumentLoader`](spring-resources/spring-resource-documentLoader/README.md)：XML文档加载解析核心接口，支持后台自动配置Spring应用。
- 元数据与过滤
  - [`MetadataReader`](spring-metadata/spring-metadata-metadataReader/README.md)：类元数据获取核心，支持组件扫描、条件化注解、AOP等高级功能。
  - [`AnnotationMetadata`](spring-metadata/spring-metadata-annotationMetadata/README.md)：动态获取和操作运行时类注解信息
  - [`TypeFilter`](spring-metadata/spring-metadata-typeFilter/README.md)：组件扫描时自定义类筛选，支持复杂条件和精确过滤。
  - [`Condition`](spring-metadata/spring-metadata-condition/README.md)：条件判断，决定Bean创建和配置的灵活机制。
- Bean定义与注册
  - [`BeanDefinition`](spring-beans/spring-bean-beanDefinition/README.md)：详细描述Bean，支持依赖注入、AOP、作用域控制等核心功能。
  - [`BeanDefinitionHolder`](spring-beans/spring-bean-beanDefinitionHolder/README.md)：管理和操作BeanDefinition的关键类。
  - [`BeanDefinitionRegistry`](spring-beans/spring-bean-beanDefinitionRegistry/README.md)：Bean定义注册管理关键接口，处理Bean元数据。
- Bean定义读取与扫描
  - [`XmlBeanDefinitionReader`](spring-beans/spring-bean-xmlBeanDefinitionReader/README.md)：加载解析XML配置，构建IOC容器，注册Bean定义。
  - [`PropertiesBeanDefinitionReader`](spring-beans/spring-bean-propertiesBeanDefinitionReader/README.md)：属性文件加载，解析为Bean定义，提升配置灵活性和可扩展性。
  - [`GroovyBeanDefinitionReader`](spring-beans/spring-bean-groovyBeanDefinitionReader/README.md)：Groovy脚本解析为Bean定义，支持应用程序上下文配置。
  - [`AnnotatedBeanDefinitionReader`](spring-beans/spring-bean-annotatedBeanDefinitionReader/README.md)：注解配置，自动扫描注册Spring组件，简化Bean定义配置。
  - [`ClassPathBeanDefinitionScanner`](spring-beans/spring-bean-classPathBeanDefinitionScanner/README.md)：类路径扫描注册Spring Bean，支持自动装配，提高可维护性和扩展性。
- Bean定义导入与组合
  - `ImportBeanDefinitionRegistrar`：运行时动态注册 Bean，实现灵活配置，扩展配置类功能。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `ImportSelector`：运行时动态导入配置类，实现条件选择和灵活配置。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `DeferredImportSelector`：运行时动态导入配置，支持条件选择和按组别延迟加载。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
- Bean工厂
  - `BeanFactory`：Spring的核心接口，提供对Bean的配置、创建、管理的基本功能。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `ListableBeanFactory`：支持按类型获取Bean的集合。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `HierarchicalBeanFactory`：支持父子容器关系，实现Bean定义的层次结构。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `ConfigurableBeanFactory`：提供对BeanFactory配置的扩展，如属性编辑器、作用域等。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
- 基于Java的配置
  - `ConfigurationClassPostProcessor`：处理@Configuration注解，关键容器启动后置处理器。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `ConfigurationClassParser`：解析@Configuration，提取Config信息，支持@Bean和条件化配置。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
- 容器上下文
  - `ClassPathXmlApplicationContext`：用于从类路径（classpath）加载 XML 配置文件的上下文。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `FileSystemXmlApplicationContext`：用于从文件系统加载 XML 配置文件的上下文。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `AnnotationConfigApplicationContext`：用于从注解配置类中加载配置信息的上下文。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `GenericApplicationContext`：用于支持多种配置方式，包括XML、注解、手动注册的上下文。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
- Bean生命周期
  - `Bean的定义解析`：加载配置，解析配置文件，注册解析得到的Bean定义，包括类名、作用域、属性等。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - [`Bean的初始化过程`](spring-core/spring-core-getBean/README.md)：实例化、属性注入、Aware回调、后置处理器、初始化方法调用、Bean就绪。
  - [`Bean的依赖解析过程`](spring-core/spring-core-resolveDependency/README.md)：声明依赖，查找依赖，注入依赖，处理循环依赖，延迟依赖解析。
  - `Bean的销毁过程`：销毁方法调用，接口回调，后处理清理，通知触发，GC回收资源。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
- Bean初始化与扩展点
  - [`InitializingBean`](spring-interface/spring-interface-initializingBean/README.md)：提供Bean初始化时执行自定义逻辑的接口。
  - [`DisposableBean`](spring-interface/spring-interface-disposableBean/README.md)：定义Bean销毁前执行清理操作的接口。
  - [`BeanDefinitionRegistryPostProcessor`](spring-interface/spring-interface-beanDefinitionRegistryPostProcessor/README.md)：在容器启动时，对BeanDefinition进行动态修改或添加。
  - [`BeanFactoryPostProcessor`](spring-interface/spring-interface-beanFactoryPostProcessor/README.md)：在Bean实例化前，对BeanFactory进行全局修改或配置。
  - [`BeanPostProcessor`](spring-interface/spring-interface-beanPostProcessor/README.md)：在Bean初始化前后，进行自定义处理，可影响所有Bean。
  - [`InstantiationAwareBeanPostProcessor`](spring-interface/spring-interface-instantiationAwareBeanPostProcessor/README.md)：扩展BeanPostProcessor，提供更深层次的实例化和属性注入控制。
  - [`DestructionAwareBeanPostProcessor`](spring-interface/spring-interface-destructionAwareBeanPostProcessor/README.md)： 扩展BeanPostProcessor，允许在Bean销毁前进行额外的清理操作。
  - [`MergedBeanDefinitionPostProcessor`](spring-interface/spring-interface-mergedBeanDefinitionPostProcessor/README.md)：在合并Bean定义时，对BeanDefinition进行进一步处理。
  - [`SmartInstantiationAwareBeanPostProcessor`](spring-interface/spring-interface-smartInstantiationAwareBeanPostProcessor/README.md)：扩展InstantiationAwareBeanPostProcessor，提供更智能的实例化控制。
  - [`SmartInitializingSingleton`](spring-interface/spring-interface-smartInitializingSingleton/README.md)：在所有单例Bean初始化完成后，执行自定义逻辑。
- Aware接口系列
  - [`BeanNameAware`](spring-aware/spring-aware-beanNameAware/README.md)：让Bean获取自身在容器中的名字，实现`setBeanName`方法。
  - [`BeanClassLoaderAware`](spring-aware/spring-aware-beanClassLoaderAware/README.md)：允许Bean获取其类加载器，实现`setBeanClassLoader`方法。
  - [`BeanFactoryAware`](spring-aware/spring-aware-beanFactoryAware/README.md)：提供Bean获取所属的BeanFactory，实现`setBeanFactory`方法。
  - [`EnvironmentAware`](spring-aware/spring-aware-environmentAware/README.md)：允许Bean获取应用程序环境配置，实现`setEnvironment`方法。
  - [`EmbeddedValueResolverAware`](spring-aware/spring-aware-embeddedValueResolverAware/README.md)：允许Bean解析嵌入式值占位符，实现`setEmbeddedValueResolver`方法。
  - [`ResourceLoaderAware`](spring-aware/spring-aware-beanClassLoaderAware/README.md)：允许Bean获取资源加载器，实现`setResourceLoader`方法。
  - [`ApplicationEventPublisherAware`](spring-aware/spring-aware-applicationEventPublisherAware/README.md)：允许Bean发布应用程序事件，实现`setApplicationEventPublisher`方法。
  - [`MessageSourceAware`](spring-aware/spring-aware-messageSourceAware/README.md)：允许Bean获取消息源，实现`setMessageSource`方法。
  - [`ApplicationStartupAware`](spring-aware/spring-aware-applicationStartupAware/README.md)：允许Bean获取应用程序启动信息，实现`setApplicationStartup`方法。
  - [`ApplicationContextAware`](spring-aware/spring-aware-applicationContextAware/README.md)：允许Bean获取应用程序上下文，实现`setApplicationContext`方法。
  - [`ImportAware`](spring-aware/spring-aware-importAware/README.md)：允许被导入的配置类获取导入它的类的信息，实现`setImportMetadata`方法。
- 核心注解
  - [`@Configuration`](spring-annotation/spring-annotation-configuration/README.md)：声明类为配置类，定义Bean和Bean之间的依赖关系。
  - [`@ComponentScan`](spring-annotation/spring-annotation-componentScan/README.md)：启用组件扫描，自动发现并注册标记为组件的类。
  - [`@Bean`](spring-annotation/spring-annotation-bean/README.md)：在配置类中声明方法，返回Bean实例。
  - [`@Import`](spring-annotation/spring-annotation-import/README.md)：引入其他配置类，将其Bean定义合并到当前容器。
  - [`@PropertySource`](spring-annotation/spring-annotation-propertySource/README.md)：指定属性文件，加载外部配置到环境中。
  - [`@DependsOn`](spring-annotation/spring-annotation-dependsOn/README.md)：指定Bean的依赖顺序，确保特定Bean在其他Bean之前初始化。
  - [`@Conditional`](spring-annotation/spring-annotation-conditional/README.md)：根据条件决定是否创建Bean。
  - [`@Lazy`](spring-annotation/spring-annotation-lazy/README.md)：指定Bean的延迟初始化，只有在首次使用时才创建。
  - [`@Value`](spring-annotation/spring-annotation-value/README.md)：注入简单值或表达式到Bean的字段或方法参数。
  - [`@Autowired`](spring-annotation/spring-annotation-autowired/README.md)：自动装配Bean依赖。
  - `@Primary`：指定在多个候选Bean中优先选择的首选Bean。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `@Description`：为Bean提供描述性信息。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `@Role`：为Bean提供角色提示，用于区分相似类型的Bean。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `@Indexed`： 标记Bean用于索引。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
  - `@Order`：指定Bean的加载顺序。<img src="https://img.shields.io/badge/%E5%8D%B3%E5%B0%86%E6%9B%B4%E6%96%B0-339933"></img>
- JSR规范
  - [`@Inject`](spring-jsr/spring-jsr330-inject/README.md)：JSR-330标准的依赖注入注解。
  - [`@Named`](spring-jsr/spring-jsr330-named/README.md)：JSR-330标准的命名注解。
  - [`@Resource`](spring-jsr/spring-jsr250-resource/README.md)：Java EE标准的资源注入注解。
  - [`@Qualifier`](spring-jsr/spring-jsr330-qualifier/README.md)：用于限定注入的Bean。
  - [`@Scope`](spring-jsr/spring-jsr330-scope/README.md)：指定Bean的作用域。
  - [`@Singleton`](spring-jsr/spring-jsr330-singleton/README.md)：指定Bean为单例。
  - [`@PostConstruct`](spring-jsr/spring-jsr250-postConstruct/README.md)：指定初始化方法。
  - [`@PreDestroy`](spring-jsr/spring-jsr250-preDestroy/README.md)：指定销毁方法。
  - [`Provider`](spring-jsr/spring-jsr330-provider/README.md)：ava标准库提供的通用Bean工厂接口。

#### Spring AOP

+ AOP 术语：Aspect、Join point、Advice、Pointcut 等
+ Spring AOP 实现原理
+ 动态代理：JDK 与 CGLIB
+ @AspectJ 支持与使用
+ 切点表达式解析

#### Spring 事件机制

+ 事件的发布与监听
+ 自定义事件

#### Spring 事务管理

+ Spring 事务管理介绍
+ 编程式与声明式事务
+ @Transactional 注解解析
+ 事务传播行为
+ 事务隔离级别
+ 事务管理器实现原理

#### Spring MVC

+ Spring MVC 流程
+ DispatcherServlet 的角色与工作原理
+ 控制器（Controller）的工作机制
+ 视图解析与渲染
+ 异常处理
+ RESTful 支持

#### Spring Boot

+ Spring Boot 与 Spring 的区别
+ 自动配置原理
+ Spring Boot starter 介绍
+ Spring Boot Actuator

#### Spring Cloud

- `@EnableDiscoveryClient`：启用服务发现客户端，用于将服务注册到服务注册中心（例如 Eureka）。

- `@EnableEurekaServer`：启用 Eureka 服务端，用于搭建服务注册中心。

- `@LoadBalanced`：启用负载均衡，通常用于 RestTemplate 和 WebClient，使其具备负载均衡的能力。

- `@FeignClient`：声明一个声明式的 HTTP 客户端，简化了服务调用的过程。

- `@EnableCircuitBreaker`：启用断路器，用于防止分布式系统中的雪崩效应。

- `@HystrixCommand`：定义一个熔断器命令。

- `@EnableZuulProxy`：启用 Zuul API 网关代理。

- `@ZuulRoute`：用于配置 Zuul 路由。

- `@EnableConfigServer`：启用配置中心服务端。

- `@RefreshScope`：用于刷新配置，通常与 Spring Cloud Config 配合使用。

- `@EnableZipkinServer`：启用 Zipkin 服务器，用于分布式链路追踪。

- `@EnableBinding`：绑定消息通道，与 Spring Cloud Stream 配合使用。

- `@GlobalTransactional`：全局事务注解，与 Seata 等分布式事务框架配合使用。

- `@SentinelResource`：Sentinel 限流和熔断注解。

- `@DubboTransported`：用于 Dubbo 服务的注解。

- `@NacosInjected`：用于注入 Nacos 相关的实例。


#### Spring 编程风格与设计模式

+ 设计模式在 Spring 源码中的应用
+ Spring 源码阅读技巧

## 💬与我联系

✉️ [Email](xuchengshengsuper@163.com) | 💬 [Issue](https://github.com/xuchengsheng/spring-reading/issues) | 🌐 [CSDN](https://blog.csdn.net/duzhuang2399?type=blog)  Me about everything!

## ⛵欢迎贡献！

如果你发现任何错误🔍或者有改进建议🛠️，欢迎提交 issue 或者 pull request。你的反馈📢对于我非常宝贵💎！

## 🔄持续更新中

为了给大家提供最新🌱、最有价值的内容💼，我会坚持每天更新这个仓库⏳。每一天，你都可以期待看到一些新的内容或者对已有内容的改进✨。如果你有任何建议或反馈📣，欢迎随时联系我📞。我非常珍视每一个反馈💌，因为这是我持续改进的动力🚀。

## 💻我的 GitHub 统计

[![Star History Chart](https://api.star-history.com/svg?repos=xuchengsheng/spring-reading&type=Date)](https://star-history.com/#xuchengsheng/spring-reading&Date)

## 🍱请我吃盒饭？

作者晚上还要写博客✍️,平时还需要工作💼,如果帮到了你可以请作者吃个盒饭🥡
<div>
<img alt="logo" src="image/WeChatPay.png" style="width: 370px;height: 400px">
<img alt="logo" src="image/Alipay.png" style="width: 370px;height: 400px">
</div>

## 👥加入我们

欢迎加入我们的群聊！一起探讨、分享和学习吧！ 🌐

<div>
<img alt="logo" src="image/wechat-group.jpg" style="width: 344px;height: 483px">
</div>

