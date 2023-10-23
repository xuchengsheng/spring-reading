<p align="center">
	<img alt="logo" src="banner.png" style="height: 80px">
</p>
<h2 align="center" style="margin: 10px 0 10px; font-weight: bold;">深入Spring，从源码开始！</h2>
<h4 align="center">探索Java最受欢迎的框架，理解它的内部机制，带大家从入门到精通。</h4>
<p align="center">
    <a href="https://github.com/xuchengsheng/spring-reading/stargazers"><img src="https://img.shields.io/github/stars/xuchengsheng/spring-reading?style=social" alt="Stars Badge"/></a>
    <a href="https://github.com/xuchengsheng"><img src="https://img.shields.io/github/followers/xuchengsheng?label=follow&style=social" alt="Follow Badge"></a>
    <a href="https://github.com/xuchengsheng/spring-reading/fork"><img src="https://img.shields.io/github/forks/xuchengsheng/spring-reading?label=fork&style=social" alt="Fork Badge"></a>
    <a href="https://github.com/xuchengsheng/spring-reading/watchers"><img src="https://img.shields.io/github/watchers/xuchengsheng/spring-reading?style=social&logo=github" alt="Watchers Badge"></a>
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
    🙏 <a href="#顺手点个星">点个星?</a>
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
    <img src="https://img.shields.io/badge/SpringBoot-5.3.10-%23437291?logo=SpringBoot&logoColor=%236DB33F&color=%236DB33F"/>
    <img src="https://img.shields.io/badge/Maven-3.6.3-%23437291?logo=Apache%20Maven&logoColor=%23C71A36&color=%23C71A36"/>
    <img src="https://img.shields.io/badge/JSR-330-%2366CCFF?logo=OpenJDK&logoColor=%2366CCFF&color=%2366CCFF"/>
    <img src="https://img.shields.io/badge/JSR-250-%23FF9900?logo=OpenJDK&logoColor=%23FF9900&color=%23FF9900"/>
</div>

## 👋简介

大家好呀，我是Lex。我是一名拥有8年经验的Java 后端开发人员，也是一个对 Spring 框架充满热情的程序员。为了帮助那些希望深入了解 Spring 框架的程序员们，我创建了这个 “Spring 源码阅读系列”。通过这个系列，我希望能够与你们共同探索 Spring 的内部工作机制。如果您有同样的兴趣或问题，请联系我！

## 🙏顺手点个星？

亲爱的朋友们，我真的花了很多心思去研究和整理这个“Spring 源码阅读系列”。如果你觉得这东西还不错，或者给你带来了一点点帮助，麻烦点一下星星吧🌟。这真的对我意义重大，每一颗星都能让我觉得所有的努力都是值得的。我知道这是小事一桩，但你的那一下点击，对我来说就是最好的鼓励。无论如何，都要感谢你抽时间阅读我的内容，真的很感激！

## 🌱Spring 源码阅读系列

> ### 🔅 核心方法

*Spring 框架背后的动力，这些核心方法确保了依赖注入、Bean 生命周期管理、配置解析和许多其他关键功能。*

- [**获取Bean实例getBean()**](spring-core/spring-core-getBean/README.md) - 从Bean的定义到其实例化，全面揭示Spring如何管理Bean的生命周期。
- [**解析依赖关系resolveDependency()**](spring-core/spring-core-resolveDependency/README.md) - 探索Spring如何优雅地解决Bean之间的依赖关系，确保应用稳定运行。
- [**销毁Bean实例destroyBean()**](spring-core/spring-core-destroyBean/README.md) - 深入Spring的销毁策略，理解如何优雅地释放资源，确保应用的健壮性。

> ### 💡 后置处理器与初始化

*深入 Spring 的后置处理技巧，揭示它如何优雅地初始化和管理 beans。*

- [**动态修改Bean定义BeanDefinitionRegistryPostProcessor**](spring-interface/spring-interface-beanDefinitionRegistryPostProcessor/README.md) - 揭示如何修改应用的 bean 定义。
- [**动态调整Bean配置BeanFactoryPostProcessor**](spring-interface/spring-interface-beanFactoryPostProcessor/README.md) - 理解 Spring 如何在 bean 初始化之前进行微调。
- [**调整Bean属性BeanPostProcessor**](spring-interface/spring-interface-beanPostProcessor/README.md) - 探索如何在 bean 实例化后进行拦截。
- [**Bean实例拦截InstantiationAwareBeanPostProcessor**](spring-interface/spring-interface-instantiationAwareBeanPostProcessor/README.md) - 理解它如何在实例化 bean 之前进行操作。
- [**管理Bean销毁周期DestructionAwareBeanPostProcessor**](spring-interface/spring-interface-destructionAwareBeanPostProcessor/README.md) - 揭露它如何管理 bean 的销毁生命周期。
- [**Bean定义的动态处理MergedBeanDefinitionPostProcessor**](spring-interface/spring-interface-mergedBeanDefinitionPostProcessor/README.md) - 理解如何合并 bean 的定义。
- [**调整Bean实例化策略SmartInstantiationAwareBeanPostProcessor**](spring-interface/spring-interface-smartInstantiationAwareBeanPostProcessor/README.md) - 深入了解其智能实例化的策略。
- [**属性设置后的初始化操作InitializingBean**](spring-interface/spring-interface-initializingBean/README.md) - 揭露如何在 bean 初始化后进行操作。
- [**资源清理与销毁DisposableBean**](spring-interface/spring-interface-disposableBean/README.md) - 探查它如何确保 bean 的正确销毁。
- [**All Beans完全初始化后SmartInitializingSingleton**](spring-interface/spring-interface-smartInitializingSingleton/README.md) - 理解它如何在所有单例 bean 初始化后进行操作。

> ### 🛠 Aware接口

*探索 Spring 的自我感知能力，如何赋予 beans 更多的上下文感知特性。*

- [**获取Bean名称BeanNameAware**](spring-aware/spring-aware-beanNameAware/README.md) - 当一个 bean 需要知道其在容器中的名字时。
- [**获取类加载器BeanClassLoaderAware**](spring-aware/spring-aware-beanClassLoaderAware/README.md) - 揭示如何为 bean 提供类加载器的引用。
- [**与Bean工厂互动BeanFactoryAware**](spring-aware/spring-aware-beanFactoryAware/README.md) - 探索 bean 如何与其工厂互动。
- [**感知运行环境EnvironmentAware**](spring-aware/spring-aware-environmentAware/README.md) - 了解 bean 如何感知并与其运行的环境互动。
- [**嵌入值解析EmbeddedValueResolverAware**](spring-aware/spring-aware-embeddedValueResolverAware/README.md) - 探查如何提供字符串值解析策略给 bean。
- [**资源加载策略ResourceLoaderAware**](spring-aware/spring-aware-resourceLoaderAware/README.md) - 理解如何为 bean 提供一个资源加载器。
- [**发布应用事件ApplicationEventPublisherAware**](spring-aware/spring-aware-applicationEventPublisherAware/README.md) - 揭露 bean 如何发布事件到应用上下文。
- [**访问消息源MessageSourceAware**](spring-aware/spring-aware-messageSourceAware/README.md) - 深入了解 bean 如何访问消息源。
- [**感知应用启动过程ApplicationStartupAware**](spring-aware/spring-aware-applicationStartupAware/README.md) - 理解 bean 如何感知应用的启动过程。
- [**访问应用上下文ApplicationContextAware**](spring-aware/spring-aware-applicationContextAware/README.md) - 探索 bean 如何访问其运行的应用上下文。
- [**了解关联导入信息ImportAware**](spring-aware/spring-aware-importAware/README.md) - 揭露 bean 如何知道与其关联的导入元数据。

> ### 🎖 核心注解

*了解 Spring 如何通过注解驱动开发，简化和加强代码。*

- [**Java配置@Configuration**](spring-annotation/spring-annotation-configuration/README.md) - 揭露如何使用 Java 配置定义 beans。
- [**组件扫描@ComponentScan**](spring-annotation/spring-annotation-componentScan/README.md) - 探索如何自动检测和注册 beans。
- [**Bean定义@Bean**](spring-annotation/spring-annotation-bean/README.md) - 理解如何通过 Java 方法定义 beans。
- [**导入配置@Import**](spring-annotation/spring-annotation-import/README.md) - 揭示如何导入其他配置类或组件。
- [**属性绑定@PropertySource**](spring-annotation/spring-annotation-propertySource/README.md) - 深入了解如何为应用上下文添加属性源。
- [**初始化顺序@DependsOn**](spring-annotation/spring-annotation-dependsOn/README.md) - 精确控制 Spring Beans 的加载顺序。
- [**条件注册@Conditional**](spring-annotation/spring-annotation-conditional/README.md) - 从基础使用到源码分析，全方位理解Spring的条件注册策略。
- [**延迟加载@Lazy**](spring-annotation/spring-annotation-lazy/README.md) - 如何优雅地实现 Spring Beans 的延迟加载。
- [**属性注入@Value**](spring-annotation/spring-annotation-value/README.md) - 如何在Spring中优雅地注入配置属性。
- [**依赖注入@Autowired**](spring-annotation/spring-annotation-autowired/README.md) - 了解如何通过@Autowired实现依赖管理和连接组件。

> ### 📜 JSR 规范

*理解 Spring 是如何实现和优化 JSR 规范中的注解，深入揭露其与 Java 标准化的紧密结合。*

- [**注入依赖@Inject**](spring-jsr/spring-jsr330-inject/README.md) - Spring中如何通过`@Inject`实现依赖注入。
- [**具名组件@Named**](spring-jsr/spring-jsr330-named/README.md) - 使用`@Named`为Spring Beans提供具体的标识。
- [**初始化后操作@PostConstruct**](spring-jsr/spring-jsr250-postConstruct/README.md) - 如何利用`@PostConstruct`在Bean初始化后执行特定操作。
- [**销毁前操作@PreDestroy**](spring-jsr/spring-jsr250-preDestroy/README.md) - 揭示`@PreDestroy`如何在Bean销毁前执行特定任务。
- [**资源绑定@Resource**](spring-jsr/spring-jsr250-resource/README.md) - 如何优雅地使用`@Resource`在Spring中注入资源。
- [**提供者机制Provider**](spring-jsr/spring-jsr330-provider/README.md) - 探索Spring中Provider的作用和如何使用它来提供Bean实例。
- [**限定符@Qualifier**](spring-jsr/spring-jsr330-qualifier/README.md) - 了解`@Qualifier`的重要性及其在解决注入冲突中的作用。
- [**作用域定义@Scope**](spring-jsr/spring-jsr330-scope/README.md) - 揭露如何使用`@Scope`定义Bean的生命周期和作用域。
- [**单例模式@Singleton**](spring-jsr/spring-jsr330-singleton/README.md) - 深入理解`@Singleton`注解，确保Spring Bean的单一实例化。

## 💬与我联系

✉️ [Email](xuchengshengsuper@163.com) | 💬 [Issue](https://github.com/xuchengsheng/spring-reading/issues) | 🌐 [CSDN](https://blog.csdn.net/duzhuang2399?type=blog)  Me about everything!

## ⛵欢迎贡献！

如果你发现任何错误或者有改进建议，欢迎提交 issue 或者 pull request。你的反馈对于我非常宝贵！

## 🔄持续更新中

为了给大家提供最新、最有价值的内容，我会坚持每天更新这个仓库。每一天，你都可以期待看到一些新的内容或者对已有内容的改进。如果你有任何建议或反馈，欢迎随时联系我。我非常珍视每一个反馈，因为这是我持续改进的动力。

## 💻我的 GitHub 统计

[![Star History Chart](https://api.star-history.com/svg?repos=xuchengsheng/spring-reading&type=Date)](https://star-history.com/#xuchengsheng/spring-reading&Date)