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

------

<table align="left">
    <tr align="center">
        <th>标题</th>
        <th>地址</th>
        <th>难度级别</th>
        <th>视频讲解</th>
    </tr>
    <tr align="center">
        <td><strong>【资源加载与访问】</strong></td>
        <td colspan="3"></td>
    </tr>
    <tr align="center">
        <td>资源加载</td>
        <td><a href="spring-resources/spring-resource/README.md">Resource</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>资源加载器</td>
        <td><a href="spring-resources/spring-resource-resourceLoader/README.md">ResourceLoader</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>XML资源加载器</td>
        <td><a href="spring-resources/spring-resource-documentLoader/README.md">DocumentLoader</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td><strong>【元数据与过滤】</strong></td>
        <td colspan="3"></td>
    </tr>
    <tr align="center">
        <td>类元数据读取</td>
        <td><a href="spring-metadata/spring-metadata-metadataReader/README.md">MetadataReader</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>注解元数据</td>
        <td><a href="spring-metadata/spring-metadata-annotationMetadata/README.md">AnnotationMetadata</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>类过滤器</td>
        <td><a href="spring-metadata/spring-metadata-typeFilter/README.md">TypeFilter</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>条件过滤器</td>
        <td><a href="spring-metadata/spring-metadata-condition/README.md">Condition</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td><strong>【Bean定义与注册】</strong></td>
        <td colspan="3"></td>
    </tr>
    <tr align="center">
        <td>Bean定义</td>
        <td><a href="spring-beans/spring-bean-beanDefinition/README.md">BeanDefinition</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean定义持有者</td>
        <td><a href="spring-beans/spring-bean-beanDefinitionHolder/README.md">BeanDefinitionHolder</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean定义注册器</td>
        <td><a href="spring-beans/spring-bean-beanDefinitionRegistry/README.md">BeanDefinitionRegistry</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td><strong>【Bean定义读取与扫描】</strong></td>
        <td colspan="3"></td>
    </tr>
    <tr align="center">
        <td>XML Bean定义读取器</td>
        <td><a href="spring-beans/spring-bean-xmlBeanDefinitionReader/README.md">XmlBeanDefinitionReader</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>属性文件Bean定义读取器</td>
        <td><a href="spring-beans/spring-bean-propertiesBeanDefinitionReader/README.md">PropertiesBeanDefinitionReader</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Groovy脚本Bean定义读取器</td>
        <td><a href="spring-beans/spring-bean-groovyBeanDefinitionReader/README.md">GroovyBeanDefinitionReader</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E7%AE%80%E5%8D%95-Green"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>注解Bean定义读取器</td>
        <td><a href="spring-beans/spring-bean-annotatedBeanDefinitionReader/README.md">AnnotatedBeanDefinitionReader</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>类路径Bean定义扫描器</td>
        <td><a href="spring-beans/spring-bean-classPathBeanDefinitionScanner/README.md">ClassPathBeanDefinitionScanner</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td><strong>【Bean生命周期过程】</strong></td>
        <td colspan="3"></td>
    </tr>
    <tr align="center">
        <td>Bean的定义解析</td>
        <td><a href="#">Bean的定义解析</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean的初始化过程</td>
        <td><a href="spring-core/spring-core-getBean/README.md">Bean的初始化过程</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean的依赖解析过程</td>
        <td><a href="spring-core/spring-core-resolveDependency/README.md">Bean的依赖解析过程</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean的销毁过程</td>
        <td><a href="#">Bean的销毁过程</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td><strong>【后置处理器与初始化】</strong></td>
        <td colspan="3"></td>
    </tr>
    <tr align="center">
        <td>属性设置后的初始化操作</td>
        <td><a href="spring-interface/spring-interface-initializingBean/README.md">InitializingBean</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>资源清理与销毁</td>
        <td><a href="spring-interface/spring-interface-disposableBean/README.md">DisposableBean</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>动态修改Bean定义</td>
        <td><a href="spring-interface/spring-interface-beanDefinitionRegistryPostProcessor/README.md">BeanDefinitionRegistryPostProcessor</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>动态调整Bean配置</td>
        <td><a href="spring-interface/spring-interface-beanFactoryPostProcessor/README.md">BeanFactoryPostProcessor</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>调整Bean属性</td>
        <td><a href="spring-interface/spring-interface-beanPostProcessor/README.md">BeanPostProcessor</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean实例拦截</td>
        <td><a href="spring-interface/spring-interface-instantiationAwareBeanPostProcessor/README.md">InstantiationAwareBeanPostProcessor</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean销毁生命周期</td>
        <td><a href="spring-interface/spring-interface-destructionAwareBeanPostProcessor/README.md">DestructionAwareBeanPostProcessor</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean定义的动态处理</td>
        <td><a href="spring-interface/spring-interface-mergedBeanDefinitionPostProcessor/README.md">MergedBeanDefinitionPostProcessor</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>调整Bean实例化策略</td>
        <td><a href="spring-interface/spring-interface-smartInstantiationAwareBeanPostProcessor/README.md">SmartInstantiationAwareBeanPostProcessor</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>All Beans完全初始化后</td>
        <td><a href="spring-interface/spring-interface-smartInitializingSingleton/README.md">SmartInitializingSingleton</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td><strong>【Aware接口】</strong></td>
        <td colspan="3"></td>
    </tr>
    <tr align="center">
        <td>获取Bean名称</td>
        <td><a href="spring-aware/spring-aware-beanNameAware/README.md">BeanNameAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>获取类加载器</td>
        <td><a href="spring-aware/spring-aware-beanClassLoaderAware/README.md">BeanClassLoaderAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>与Bean工厂互动</td>
        <td><a href="spring-aware/spring-aware-beanFactoryAware/README.md">BeanFactoryAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>感知运行环境</td>
        <td><a href="spring-aware/spring-aware-environmentAware/README.md">EnvironmentAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>嵌入值解析</td>
        <td><a href="spring-aware/spring-aware-embeddedValueResolverAware/README.md">EmbeddedValueResolverAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>资源加载策略</td>
        <td><a href="spring-aware/spring-aware-resourceLoaderAware/README.md">ResourceLoaderAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>发布应用事件</td>
        <td><a href="spring-aware/spring-aware-applicationEventPublisherAware/README.md">ApplicationEventPublisherAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>访问消息源</td>
        <td><a href="spring-aware/spring-aware-messageSourceAware/README.md">MessageSourceAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>感知应用启动过程</td>
        <td><a href="spring-aware/spring-aware-applicationStartupAware/README.md">ApplicationStartupAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>访问应用上下文</td>
        <td><a href="spring-aware/spring-aware-applicationContextAware/README.md">ApplicationContextAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>了解关联导入信息</td>
        <td><a href="spring-aware/spring-aware-importAware/README.md">ImportAware</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E4%B8%80%E8%88%AC-blue"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td><strong>【核心注解】</strong></td>
        <td colspan="3"></td>
    </tr>
    <tr align="center">
        <td>Java配置</td>
        <td><a href="spring-annotation/spring-annotation-configuration/README.md">@Configuration</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>组件扫描</td>
        <td><a href="spring-annotation/spring-annotation-componentScan/README.md">@ComponentScan</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>Bean定义</td>
        <td><a href="spring-annotation/spring-annotation-bean/README.md">@Bean</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>导入配置</td>
        <td><a href="spring-annotation/spring-annotation-import/README.md">@Import</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>属性绑定</td>
        <td><a href="spring-annotation/spring-annotation-propertySource/README.md">@PropertySource</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>初始化顺序</td>
        <td><a href="spring-annotation/spring-annotation-dependsOn/README.md">@DependsOn</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>条件注册</td>
        <td><a href="spring-annotation/spring-annotation-conditional/README.md">@Conditional</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>延迟加载</td>
        <td><a href="spring-annotation/spring-annotation-lazy/README.md">@Lazy</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>属性注入</td>
        <td><a href="spring-annotation/spring-annotation-value/README.md">@Value</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>依赖注入</td>
        <td><a href="spring-annotation/spring-annotation-autowired/README.md">@Autowired</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>注入依赖</td>
        <td><a href="spring-jsr/spring-jsr330-inject/README.md">@Inject</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>具名组件</td>
        <td><a href="spring-jsr/spring-jsr330-named/README.md">@Named</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>初始化后操作</td>
        <td><a href="spring-jsr/spring-jsr250-postConstruct/README.md">@PostConstruct</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>销毁前操作</td>
        <td><a href="spring-jsr/spring-jsr250-preDestroy/README.md">@PreDestroy</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>资源绑定</td>
        <td><a href="spring-jsr/spring-jsr250-resource/README.md">@Resource</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>提供者机制</td>
        <td><a href="spring-jsr/spring-jsr330-provider/README.md">Provider</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>限定符</td>
        <td><a href="spring-jsr/spring-jsr330-qualifier/README.md">@Qualifier</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>作用域定义</td>
        <td><a href="spring-jsr/spring-jsr330-scope/README.md">@Scope</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>单例模式</td>
        <td><a href="spring-jsr/spring-jsr330-singleton/README.md">@Singleton</a></td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>定义主要候选项</td>
        <td>@Primary</td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>添加描述信息</td>
        <td>@Description</td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>指定注解角色</td>
        <td>@Role</td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>标记为可索引</td>
        <td>@Indexed</td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
    <tr align="center">
        <td>指定顺序</td>
        <td>@Order</td>
        <td><img src="https://img.shields.io/badge/Level-%E5%9B%B0%E9%9A%BE-orange"/></td>
        <td>❌</td>
    </tr>
</table>

------



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
