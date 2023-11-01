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

1. **IOC 容器**
   - 资源加载与访问
     - Resource
     - ResourceLoader
   - 元数据
     - MetadataReader
     - AnnotationMetadata
   - Bean的定义与注册
     - BeanDefinition
     - BeanDefinitionHolder
     - BeanDefinitionRegistry
     
     - XmlBeanDefinitionReader
     - PropertiesBeanDefinitionReader
     - AnnotatedBeanDefinitionReader
     - ClassPathBeanDefinitionScanner
     - ImportBeanDefinitionRegistrar
     - BeanDefinitionImportSelector
   - Bean的过滤
     - TypeFilter
     - ConditionEvaluator
     - Condition
     - ConditionContext
     - ConfigurationCondition
   - 属性编辑与类型转换
     - PropertyEditor
     - ConversionService
     - Converter
   - 表达式语言(SpEL)
     - Expression
     - ExpressionParser
   	 - EvaluationContext
     - PropertyAccessor
     - MethodResolver
     - TypeLocator
   - Bean工厂
     - BeanFactory
     - ListableBeanFactory
     - HierarchicalBeanFactory
     - ConfigurableBeanFactory
   - Bean生命周期
     - Bean的定义解析
     - Bean的初始化过程
     - Bean的依赖解析过程
     - Bean的销毁过程
   - Bean初始化与扩展点
     - InitializingBean
     - DisposableBean
     - BeanDefinitionRegistryPostProcessor
     - BeanFactoryPostProcessor
     - BeanPostProcessor
     - InstantiationAwareBeanPostProcessor
     - DestructionAwareBeanPostProcessor
     - MergedBeanDefinitionPostProcessor
     - SmartInstantiationAwareBeanPostProcessor
     - SmartInitializingSingleton
   - 基于Java的配置
     - ConfigurationClassPostProcessor
     - ConfigurationClassParser
   - 核心注解
     - @Configuration
     - @ComponentScan
     - @Bean
     - @Import
     - @PropertySource
     - @DependsOn
     - @Conditional
     - @Lazy
     - @Primary
     - @Description
     - @Role
     - @Value
     - @Autowired
     - @Indexed
     - @Order
   - JSR规范
     - @Inject
     - @Named
     - @Resource
     - @Qualifier
     - @Scope
     - @Singleton
     - @PostConstruct
     - @PreDestroy
     - Provider
   - Aware接口系列
     - BeanNameAware
     - BeanClassLoaderAware
     - BeanFactoryAware
     - EnvironmentAware
     - EmbeddedValueResolverAware
     - ResourceLoaderAware
     - ApplicationEventPublisherAware
     - MessageSourceAware
     - ApplicationStartupAware
     - ApplicationContextAware
     - ImportAware
     - BeanDefinitionRegistryAware
   - 容器上下文
     - ClassPathXmlApplicationContext
     - FileSystemXmlApplicationContext
     - AnnotationConfigApplicationContext
     - GenericApplicationContext
2. **AOP (面向切面编程)**
   - AOP 术语：Aspect、Join point、Advice、Pointcut 等
   - Spring AOP 实现原理
   - 动态代理：JDK 与 CGLIB
   - @AspectJ 支持与使用
   - 切点表达式解析
3. **事务管理**
   - Spring 事务管理介绍
   - 编程式与声明式事务
   - @Transactional 注解解析
   - 事务传播行为
   - 事务隔离级别
   - 事务管理器实现原理
4. **Spring MVC**
   - Spring MVC 流程
   - DispatcherServlet 的角色与工作原理
   - 控制器（Controller）的工作机制
   - 视图解析与渲染
   - 异常处理
   - RESTful 支持
5. **Spring 数据访问**
   - JdbcTemplate 的使用与实现原理
   - Spring Data JPA 简介
   - ORM 框架集成：Hibernate、MyBatis 等
6. **Spring 安全（Spring Security）**
   - 认证与授权的基本概念
   - Spring Security 的核心组件
   - 过滤器链
   - 用户详情服务
   - 密码加密
   - 记住我功能
7. **Spring Boot**
   - Spring Boot 与 Spring 的区别
   - 自动配置原理
   - Spring Boot starter 介绍
   - Spring Boot Actuator
8. **Spring 事件机制**
   - 事件的发布与监听
   - 自定义事件
9. **高级主题**
   - Spring 缓存抽象
   - Spring WebFlux (响应式编程)
   - Spring Session
   - Spring Websocket
10. **Spring 源码的编程风格与设计模式**
    - 设计模式在 Spring 源码中的应用
    - Spring 源码阅读技巧
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
