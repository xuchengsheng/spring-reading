## @Configuration

[TOC]

### 1、注解说明

`@Configuration`是Spring框架中的一个核心注解，主要用于类级别，标识该类是一个配置类。配置类是Spring IoC容器的重要组成部分，它包含了Spring容器如何初始化和配置应用中的bean的信息。在配置类中，你可以定义一个或多个公共的`@Bean`注解方法，这些方法会实例化、配置并初始化新的对象，然后这些对象被Spring IoC容器管理

### 2、注解源码

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

	@AliasFor(annotation = Component.class)
	String value() default "";

	boolean proxyBeanMethods() default true;
}	
```

### 3、字段描述

#### 3.1、value

用于指定Bean的名称的。这个属性是`@Component`注解的一部分，因为`@Configuration`注解是元注解`@Component`的特化

#### 3.2、proxyBeanMethods

这是Spring 5.2新增的属性，用于控制`@Configuration`类的`@Bean`方法是否应该被CGLIB代理。如果`proxyBeanMethods`设置为true （full模式），那么@Bean方法会被代理，每次调用都会检查Spring上下文，确保返回的是同一个bean实例。如果设置为false（lite模式），那么`@Bean`方法会像普通方法一样执行，每次调用都会返回一个新的实例。这种方式可能会使应用启动得更快，因为不需要生成CGLIB代理类，但是你必须自己处理@Bean方法之间的引用。

### 4、如何使用

首先来看看启动类入口，上下文环境使用`AnnotationConfigApplicationContext`（此类是使用Java注解来配置Spring容器的方式），构造参数我们给定了一个`MyConfiguration`组件类，在最后我们调用两次获取MyBean对象并打印查看内存地址。

```java
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);
        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());
    }
}
```

创建MyBean类，作为IOC容器的Bean对象

```java
public class MyBean {

    private String beanId;

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }
}
```

#### 4.1、测试proxyBeanMethods为true情况

创建`MyConfiguration`类，作为spring的启动配置引导类，由于`@Configuration`中的proxyBeanMethods字段默认为true，此处使用缺省值

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

通过运行main方法，我们发现打印出来的2个对象是一致的<img src="https://img-blog.csdnimg.cn/466e60aca5c8471e8da94dd2586013dc.png#pic_center" alt="image-20230808100238267" style="zoom:150%;" />

#### 4.2、测试proxyBeanMethods为false情况

创建`MyConfiguration`类，作为spring的启动配置引导类，此处proxyBeanMethods设置为false

```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

通过再次运行main方法，我们发现打印出来的2个对象是不一致的

<img src="https://img-blog.csdnimg.cn/094abfa1fa75427095aa43bc3e6a41ee.png#pic_center" alt="image-20230808102446140" style="zoom:150%;" />

#### 4.3、@Configuration注解几种组合用法

`@Configuration`注解在Spring框架中通常和其他注解一起使用，以满足各种各样的配置需求。下面是一些常见的组合用法：

##### 4.3.1、与`@Bean`组合

 这是最常见的用法，`@Bean`注解用在`@Configuration`类的方法上，这个方法的返回值会作为一个bean注册到Spring容器中。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

##### 4.3.2、与`@ComponentScan`组合

 `@ComponentScan`注解用来配置Spring哪些包进行扫描。Spring会扫描指定包及其子包下的所有类，如果这些类上有`@Component`、`@Controller`、`@Service`、`@Repository`或者`@Configuration`等注解，Spring就会把这些类作为Bean定义注册到容器中。

```java
@Configuration
@ComponentScan(basePackages = "com.xcs.spring.bean")
public class MyConfiguration {

}
```

##### 4.3.3、与`@Import`组合

`@Import`注解用来导入其他的`@Configuration`类。这样可以把多个小的、专门用途的配置类组合成一个大的配置类

```java
@Configuration
@Import({DatabaseConfig.class, WebConfig.class})
public class MyConfiguration {
	
}
```

##### 4.3.4、与`@PropertySource`组合

`@PropertySource`注解用来指定加载哪些属性文件。加载的属性会添加到Spring的`Environment`中，可以通过`@Value`注解或者`Environment`对象来获取属性值。

```java
@Configuration
@PropertySource("classpath:application.properties")
public class MyConfiguration {
	
}
```

##### 4.3.5、与`@Enable*`组合

 `@Enable*`是一类注解，用来开启Spring的某些功能，比如`@EnableTransactionManagement`开启事务管理，`@EnableScheduling`开启计划任务，`@EnableAsync`开启异步执行等。这些注解必须用在`@Configuration`类上才能生效。

```java
@Configuration
@EnableTransactionManagement
public class MyConfiguration {

}
```

##### 4.3.6、与`@Profile`组合

`@Profile`注解用来定义配置类或bean定义适用的环境。只有当前环境和`@Profile`指定的环境匹配时，配置类或bean定义才会被注册到容器中。

```java
@Configuration
@Profile("development")
public class MyConfiguration {

}
```

##### 4.3.7、与@Configuration内嵌组合

你可以在一个`@Configuration`类中嵌套其他的`@Configuration`类，这是一种组织配置的方式，可以让你的配置更加模块化和层次化。这种方式通常用在一个大的配置类中，你可以将某些特定的配置组合在一起，放在一个内嵌的`@Configuration`类中。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyBean myBean() {
        return new MyBean();
    }

    @Configuration
    public static class MyDatabaseConfig {

        @Bean
        public DataSource dataSource() {
            return new DataSource();
        }
    }

    @Configuration
    public static class MyWebConfig {

        @Bean
        public Controller controller() {
            return new Controller();
        }
    }
}
```

##### 4.3.8、与`@Conditional`组合

可以使得某个bean定义或者配置类只有在特定的条件满足时才会被注册。例如，我们可以定义一个条件类检查某个系统属性是否存在，然后用`@Conditional`注解将这个条件类应用到bean定义或配置类上。

```java
@Configuration
@Conditional(MyCondition.class)
public class MyConfiguration {
    // ...
}

public class MyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return System.getProperty("myProperty") != null;
    }
}
```

##### 4.3.9、与`Environment`API组合

可以用来获取系统属性、环境变量、配置文件中的属性等，然后根据这些属性来决定要创建哪些bean。例如，我们可以在`@Bean`方法中检查一个特定的环境变量，然后根据这个环境变量的值来决定创建哪个版本的bean。

```java
@Configuration
public class MyConfiguration {

    @Autowired
    Environment env;

    @Bean
    public MyBean myBean() {
        if ("version1".equals(env.getProperty("myBean.version"))) {
            return new MyBeanVersion1();
        } else {
            return new MyBeanVersion2();
        }
    }
}
```

##### 4.3.9、组合总结

以上这些Spring注解是可以多个组合使用的，而且这种组合是很常见的。使用这些注解组合可以实现高级的配置策略，增加配置的灵活性。例如，你可以在一个`@Configuration`类中同时使用`@Profile`、`@Bean`、`@Import`等注解，每个注解都有其特定的用途。通过组合多个注解，我们可以为Spring应用创建出极具灵活性和动态性的配置。需要注意的是，虽然使用多个注解可以带来很大的灵活性，但同时也会增加配置的复杂性，因此，我们需要在保持配置简洁和提供足够的灵活性之间找到一个平衡点。

### 5、原理分析

当Spring容器加载`@Configuration`注解的类时，它实际上会创建一个CGLIB代理的子类来替代这个类。在代理类中，每个`@Bean`方法都会被重写，以确保每个方法的调用都会通过Spring的Bean工厂，这样就可以保证单例Bean的语义。

在Spring 5.2版本之前，对于`@Configuration`注解的类，无论其`@Bean`方法是否被设计为单例，Spring容器都会确保它们的行为如同单例Bean一样。即使在同一个配置类中，一个`@Bean`方法调用另一个`@Bean`方法，也会得到同一个实例。这个特性就是full模式的核心。

```java
// 使用@Configuration，5.2以前没有proxyBeanMethods字段，默认就是full模式
@Configuration
public class MyConfiguration {
    
    @Bean
    public ServiceA serviceA() {
        return new ServiceA();
    }

    @Bean
    public ServiceB serviceB() {
        // 这里调用的 serviceA() 返回的是同一个 ServiceA 实例
        return new ServiceB(serviceA());  
    }
}
```

在Spring 5.2版本之前，如果你想使用lite模式

```java
// 使用类标记为@Component或@Service等
@Component
public class MyConfiguration {
    
    @Bean
    public ServiceA serviceA() {
        return new ServiceA();
    }

    @Bean
    public ServiceB serviceB() {
        // 这里调用的 serviceA() 每次都会返回一个新的 ServiceA 实例
        return new ServiceB(serviceA());
    }
}	
```

而在Spring 5.2及之后的版本中，`@Configuration`注解有一个`proxyBeanMethods`属性，它用来决定是否需要使用CGLIB代理。如果`proxyBeanMethods`设置为`true`，那么Spring会为这个配置类创建一个CGLIB代理类，这被称为full模式。

```java
// 使用@Configuration，5.2以后新增proxyBeanMethods字段，默认只为true，表示走full模式
@Configuration(proxyBeanMethods = true)
public class MyConfiguration {
    
    @Bean
    public ServiceA serviceA() {
        return new ServiceA();
    }

    @Bean
    public ServiceB serviceB() {
        // 这里调用的 serviceA() 返回的是同一个 ServiceA 实例
        return new ServiceB(serviceA());  
    }
}
```

如果`proxyBeanMethods`设置为`false`，那么Spring不会创建代理类，这被称为lite模式。在lite模式下，`@Bean`方法的调用就像普通的Java方法调用一样，不会通过Spring的Bean工厂，也不会确保单例语义。因此，即使你将一个Bean定义为单例，如果你在一个配置类中多次调用这个Bean的方法，也会得到不同的实例。这种方式在某些场景下可能会更快，但是需要你自己来保证配置的正确性。

```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {
    
    @Bean
    public ServiceA serviceA() {
        return new ServiceA();
    }

    @Bean
    public ServiceB serviceB() {
        // 这里调用的 serviceA() 每次都会返回一个新的 ServiceA 实例
        return new ServiceB(serviceA());
    }
}	
```

前面说到`@Configuration`是走的CGLIB代理来实现的，那么我们可以借助一个`arthas`的工具，查看一下是否生成了代理类，被代理后的类长什么样的呢？

#### 5.1、分析proxyBeanMethods为true

首先`MyConfiguration`类中的`proxyBeanMethods`字段默认为true，此处就不设置了，使用缺省值。

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

下面是我的启动类，通过`context.getBean(MyConfiguration.class)`获得`MyConfiguration`对象，最后通过configuration.getClass().getName()打印类名，查看是原始类名还是被CGLIB代理后的类名，最后使用了System.in.read()是防止spring程序结束退出程序。

```java
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);
        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());

        System.out.println("MyConfiguration = " + configuration.getClass().getName());

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

运行结果发现，`MyConfiguration`已经成功被CGLIB代理，代理类为`com.xcs.spring.MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac`，接下来我们使用arthas工具反编译一下此类，查看具体被代理后的代码是什么样子的呢？

[如果你还对arthas不了解请查看arthas官网文档](https://arthas.aliyun.com/doc/)

<img src="https://img-blog.csdnimg.cn/d1ea9d6651674d47863312591b8ef31b.png#pic_center" alt="image-20230808140424359" style="zoom:150%;" />

通过arthas的反编译，首先我们发现 `MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac`是`MyConfiguration`的一个子类，并实现了`ConfigurationClassEnhancer.EnhancedConfiguration`接口中的setBeanFactory方法

```java
package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.bean.MyBean;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.ConfigurationClassEnhancer;

public class MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac extends MyConfiguration implements ConfigurationClassEnhancer.EnhancedConfiguration {
    // 标记是否已经将CGLIB的回调对象绑定到了当前对象上。
    private boolean CGLIB$BOUND;
    // 存储CGLIB用于创建代理对象的工厂数据。
    public static Object CGLIB$FACTORY_DATA;
    // 线程局部变量，用于存储当前线程的CGLIB回调对象。
    private static final ThreadLocal CGLIB$THREAD_CALLBACKS;
    // 存储静态的CGLIB回调对象
    private static final Callback[] CGLIB$STATIC_CALLBACKS;
    // CGLIB的回调对象，用于处理方法调用。Spring通过这些回调对象，拦截对@Bean方法的调用，并确保返回的是Spring容器管理的bean实例。
    private MethodInterceptor CGLIB$CALLBACK_0;
    private MethodInterceptor CGLIB$CALLBACK_1;
    private NoOp CGLIB$CALLBACK_2;
    // CGLIB的回调过滤器，用于决定某个方法调用应该使用哪个回调对象来处理。
    private static Object CGLIB$CALLBACK_FILTER;
    // 被代理的方法，例如myBean()方法和setBeanFactory()方法。
    private static final Method CGLIB$myBean$0$Method;
    // CGLIB的MethodProxy对象，用于代理方法调用。
    private static final MethodProxy CGLIB$myBean$0$Proxy;
    // 这个字段是一个空的参数数组，用于在调用没有参数的方法时使用。
    private static final Object[] CGLIB$emptyArgs;
    private static final Method CGLIB$setBeanFactory$5$Method;
    private static final MethodProxy CGLIB$setBeanFactory$5$Proxy;
    // MyConfiguration类实现了BeanFactoryAware接口，因此Spring在创建bean实例后，会自动调用setBeanFactory方法
    public BeanFactory $$beanFactory;

    public MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac() {
        MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac myConfiguration$$EnhancerBySpringCGLIB$$fce000ac = this;
        MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac.CGLIB$BIND_CALLBACKS(myConfiguration$$EnhancerBySpringCGLIB$$fce000ac);
    }

    static {
        MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac.CGLIB$STATICHOOK2();
        MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac.CGLIB$STATICHOOK1();
    }

    public final MyBean myBean() {
        // 首先，它尝试获取一个名为`CGLIB$CALLBACK_0，这个拦截器是CGLIB回调机制的核心，它负责处理`@Bean`方法的调用。
        MethodInterceptor methodInterceptor = this.CGLIB$CALLBACK_0;
        // 如果拦截器不存在,那么它会尝试调用`CGLIB$BIND_CALLBACKS(this)`方法，该方法负责将拦截器绑定到当前对象上。
        if (methodInterceptor == null) {
            MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac.CGLIB$BIND_CALLBACKS(this);
            // 绑定拦截器后，它再次获取拦截器。
            methodInterceptor = this.CGLIB$CALLBACK_0;
        }
        // 如果拦截器存在，那么它就调用拦截器的`intercept()`方法，处理对`myBean()`方法的调用。`intercept()`方法的参数包括：当前对象、代表`myBean()`方法的`Method`对象、一个空的参数数组（因为`myBean()`方法没有参数），以及一个`MethodProxy`对象（用于通过CGLIB调用超类的原始方法）。
        if (methodInterceptor != null) {
            return (MyBean) methodInterceptor.intercept(this, CGLIB$myBean$0$Method, CGLIB$emptyArgs, CGLIB$myBean$0$Proxy);
        }
        // 如果拦截器不存在，那么它就直接调用超类的`myBean()`方法，即原始的`MyConfiguration`类的`myBean()`方法。
        return super.myBean();
    }

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // 获取一个名为CGLIB$CALLBACK_1的MethodInterceptor（方法拦截器）
        MethodInterceptor methodInterceptor = this.CGLIB$CALLBACK_1;
        // 如果拦截器不存在,那么它会尝试调用CGLIB$BIND_CALLBACKS(this)方法，该方法负责将拦截器绑定到当前对象上
        if (methodInterceptor == null) {
            MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac.CGLIB$BIND_CALLBACKS(this);
            // 绑定拦截器后，它再次获取拦截器。
            methodInterceptor = this.CGLIB$CALLBACK_1;
        }
        // 如果拦截器存在，那么它就调用拦截器的intercept()方法，处理对setBeanFactory方法的调用。
        // 这个intercept()方法的参数包括：当前对象、代表setBeanFactory方法的Method对象、一个包含一个元素（即传入的BeanFactory）的参数数组，
        // 以及一个MethodProxy对象（用于通过CGLIB调用超类的原始方法）。
        if (methodInterceptor != null) {
            Object object = methodInterceptor.intercept(this, CGLIB$setBeanFactory$5$Method, new Object[]{beanFactory}, CGLIB$setBeanFactory$5$Proxy);
            return;
        }
        // 如果拦截器不存在，那么它就直接调用超类的setBeanFactory方法，即原始的MyConfiguration类的setBeanFactory方法。
        super.setBeanFactory(beanFactory);
    }

    public static void CGLIB$SET_STATIC_CALLBACKS(Callback[] callbackArray) {
        CGLIB$STATIC_CALLBACKS = callbackArray;
    }

    public static void CGLIB$SET_THREAD_CALLBACKS(Callback[] callbackArray) {
        CGLIB$THREAD_CALLBACKS.set(callbackArray);
    }

    // CGLIB库的内部实现细节，一般情况下，你不需要直接使用或理解这些代码
    public static MethodProxy CGLIB$findMethodProxy(Signature signature) {
        String string = ((Object) signature).toString();
        switch (string.hashCode()) {
            case -1352508034: {
                if (!string.equals("myBean()Lcom/xcs/spring/bean/MyBean;")) break;
                return CGLIB$myBean$0$Proxy;
            }
            case 2095635076: {
                if (!string.equals("setBeanFactory(Lorg/springframework/beans/factory/BeanFactory;)V")) break;
                return CGLIB$setBeanFactory$5$Proxy;
            }
        }
        return null;
    }

    final void CGLIB$setBeanFactory$5(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
    }

    // 通过这个方法，CGLIB可以在需要的时候将回调对象绑定到代理对象上，
    // 然后通过这些回调对象来处理方法调用。这是CGLIB实现方法拦截的一部分，也是Spring实现@Configuration注解的重要机制。
    private static final void CGLIB$BIND_CALLBACKS(Object object) {
        block2:
        {
            Object object2;
            MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac myConfiguration$$EnhancerBySpringCGLIB$$fce000ac;
            block3:
            {
                // 首先，它将传入的对象转换为MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac类型。
                myConfiguration$$EnhancerBySpringCGLIB$$fce000ac = (MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac) object;
                // 如果这个对象还没有绑定回调对象（即，CGLIB$BOUND字段为false）,跳出整个代码块block2
                if (myConfiguration$$EnhancerBySpringCGLIB$$fce000ac.CGLIB$BOUND) break block2;
                // 那么它将CGLIB$BOUND字段设为true，表示已经绑定了回调对象。
                myConfiguration$$EnhancerBySpringCGLIB$$fce000ac.CGLIB$BOUND = true;
                // 然后，它尝试从CGLIB$THREAD_CALLBACKS线程局部变量中获取回调对象。
                object2 = CGLIB$THREAD_CALLBACKS.get();
                if (object2 != null) break block3;
                // 如果没有找到，就尝试获取静态的CGLIB$STATIC_CALLBACKS回调对象。
                object2 = CGLIB$STATIC_CALLBACKS;
                // 跳出整个代码块block2
                if (CGLIB$STATIC_CALLBACKS == null) break block2;
            }
            // 如果找到了回调对象，就将它们绑定到当前对象上。例如，
            // CGLIB$CALLBACK_2字段是一个NoOp对象，它是Callback接口的一个实现，
            // 表示一个没有操作的回调。CGLIB$CALLBACK_0和CGLIB$CALLBACK_1字段是MethodInterceptor对象，它们用于处理方法调用。
            Callback[] callbackArray = (Callback[]) object2;
            MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac myConfiguration$$EnhancerBySpringCGLIB$$fce000ac2 = myConfiguration$$EnhancerBySpringCGLIB$$fce000ac;
            myConfiguration$$EnhancerBySpringCGLIB$$fce000ac2.CGLIB$CALLBACK_2 = (NoOp) callbackArray[2];
            myConfiguration$$EnhancerBySpringCGLIB$$fce000ac2.CGLIB$CALLBACK_1 = (MethodInterceptor) callbackArray[1];
            myConfiguration$$EnhancerBySpringCGLIB$$fce000ac2.CGLIB$CALLBACK_0 = (MethodInterceptor) callbackArray[0];
        }
    }

    // 这个方法在代理类的静态初始化块中被调用，它初始化了代理类需要的一些字段和数据结构。
    // 静态初始化块是Java语言的一部分，用于初始化静态字段。这个块在类被加载时执行一次。
    static void CGLIB$STATICHOOK1() {
        CGLIB$THREAD_CALLBACKS = new ThreadLocal();
        CGLIB$emptyArgs = new Object[0];
        Class<?> clazz = Class.forName("com.xcs.spring.config.MyConfiguration$$EnhancerBySpringCGLIB$$fce000ac");
        Class<?> clazz2 = Class.forName("org.springframework.beans.factory.BeanFactoryAware");
        CGLIB$setBeanFactory$5$Method = ReflectUtils.findMethods(new String[]{"setBeanFactory", "(Lorg/springframework/beans/factory/BeanFactory;)V"}, clazz2.getDeclaredMethods())[0];
        CGLIB$setBeanFactory$5$Proxy = MethodProxy.create(clazz2, clazz, "(Lorg/springframework/beans/factory/BeanFactory;)V", "setBeanFactory", "CGLIB$setBeanFactory$5");
        clazz2 = Class.forName("com.xcs.spring.config.MyConfiguration");
        CGLIB$myBean$0$Method = ReflectUtils.findMethods(new String[]{"myBean", "()Lcom/xcs/spring/bean/MyBean;"}, clazz2.getDeclaredMethods())[0];
        CGLIB$myBean$0$Proxy = MethodProxy.create(clazz2, clazz, "()Lcom/xcs/spring/bean/MyBean;", "myBean", "CGLIB$myBean$0");
    }

    final MyBean CGLIB$myBean$0() {
        return super.myBean();
    }

    static void CGLIB$STATICHOOK2() {
    }
}
```

#### 5.2、分析proxyBeanMethods为false

我们再次调整`MyConfiguration`类中的`proxyBeanMethods`字段设置为false

```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

下面是我的启动类，通过`context.getBean(MyConfiguration.class)`获得`MyConfiguration`对象，最后通过`configuration.getClass().getName()`打印类名，查看是原始类名还是被CGLIB代理后的类名，最后使用了`System.in.read()`是防止spring程序结束退出程序。

```java
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);
        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());

        System.out.println("MyConfiguration = " + configuration.getClass().getName());

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

我们运行后，发现CGLIB代理并未生效，而是使用原始的`MyConfiguration`作为Bean对象，所以此处我们就没有必要进行类反编译操作啦，到此已经发现full模式与lite模式的一些区别了吧。

<img src="https://img-blog.csdnimg.cn/f4a9c2cb3b83460cbefaacd98e1aa73f.png#pic_center" alt="image-20230808142855277" style="zoom:150%;" />

### 6、源码分析

首先在最前面透露一下，处理`@Configuration`的核心类是在`ConfigurationClassPostProcessor`类

首先通过IDEA查看类图，发现`ConfigurationClassPostProcessor`类实现了一个重要的接口`BeanDefinitionRegistryPostProcessor`，并实现了该接口中有`postProcessBeanDefinitionRegistry`方法，并间接实现了`BeanFactoryPostProcessor`接口中的`postProcessBeanFactory`方法。那么`ConfigurationClassPostProcessor`类是什么时候还是起作用并生效的呢？我们此时需要跟踪一下源码就知道啦

![image-20230808173929978](https://img-blog.csdnimg.cn/885e52a890b44f02a83fbb2a595720ef.png#pic_center)

我们的`ConfigurationApplication`类的main方法开始跟踪源码，我们使用的是`AnnotationConfigApplicationContext`做为上下文环境，并传入了一个组件类的类名，那么我们继续进入`AnnotationConfigApplicationContext`的构造函数查看源码

```java
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);
        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());

        System.out.println("MyConfiguration = " + configuration.getClass().getName());

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

在`AnnotationConfigApplicationContext`构造函数中，执行了三个步骤

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    // (this构造函数) 6.1
    this();
    // 注册MyConfiguration 6.2
    register(componentClasses);
    // 刷新容器 6.3
    refresh();
}
```

#### 6.1、this构造函数

无参构造函数中使用了`AnnotatedBeanDefinitionReader`（该类主要用于从注解类中解析出 `BeanDefinition`，然后将解析出的 `BeanDefinition` 注册到 `DefaultListableBeanFactory`中）与`ClassPathBeanDefinitionScanner`（该类用于扫描类路径下指定包（包括子包）中的类，解析这些类中的注解信息，然后生成对应的 `BeanDefinition`，最后同样对的也是将解析出的 `BeanDefinition` 注册到 `DefaultListableBeanFactory`中），接下来我们重点关注一下`AnnotatedBeanDefinitionReader`类的构造函数

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

首先会通过调用 `getOrCreateEnvironment(registry)` 来获取或创建一个 `Environment`。`Environment` 是 Spring 中用于处理应用环境的接口，它能够访问到应用的环境变量、系统属性等信息。然后，构造函数会用传入的 `registry` 和获取到的 `Environment` 作为参数，调用另一个构造函数 `AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment)`，完成`AnnotatedBeanDefinitionReader`的初始化。

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
    this(registry, getOrCreateEnvironment(registry));
}
```

在继续跟进构造函数，在函数中做了一些参数校验工作，这里 `this.conditionEvaluator` 是通过创建一个新的 `ConditionEvaluator` 实例来初始化的，`ConditionEvaluator` 是用来处理 `@Conditional` 注解的，这里只做了解就好，不是本次关注的重点。接下来的重点在`AnnotationConfigUtils.registerAnnotationConfigProcessors()`是一个用来注册各种注解处理器的静态方法。其中我们本次关注的核心类`ConfigurationClassPostProcessor`就是在这里被注册上的。

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    Assert.notNull(environment, "Environment must not be null");
    this.registry = registry;
    this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
    AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
}
```

如果容器中还没有这个后置处理器的 `ConfigurationClassPostProcessor`，那么就创建一个 `RootBeanDefinition` 对象，并设置其 Bean 类型为 `ConfigurationClassPostProcessor.class`，即后置处理器的类型。然后，为这个 `BeanDefinition` 设置来源 `source`，这里传入的 `source` 参数是一个可选的对象，用于标识注册这些 BeanDefinition 的来源。接着，通过调用 `registerPostProcessor()` 方法将这个 `BeanDefinition` 注册到容器中。

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(BeanDefinitionRegistry registry, @Nullable Object source) {
    Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);
    // -------------------忽略其他代码-------------------------
    if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
    }
	// -------------------忽略其他代码-------------------------
    return beanDefs;
}
```

这个过程实际上是将 `ConfigurationClassPostProcessor` 类注册为一个特殊的后置处理器，用于处理 `@Configuration` 注解的配置类，使得这些配置类能够正常生效并且能够注册其中的 `BeanDefinition` 到容器中。

```java
private static BeanDefinitionHolder registerPostProcessor(BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {
    definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    registry.registerBeanDefinition(beanName, definition);
    return new BeanDefinitionHolder(definition, beanName);
}
```

下面是`ConfigurationClassPostProcessor`被注册过程的时序图

~~~mermaid
sequenceDiagram 
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)  
AnnotationConfigApplicationContext-->>ConfigurationApplication: 返回context
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext()
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: AnnotatedBeanDefinitionReader(registry)
AnnotatedBeanDefinitionReader-->>AnnotationConfigApplicationContext: 返回reader
AnnotatedBeanDefinitionReader-->>AnnotationConfigUtils: registerAnnotationConfigProcessors(registry)
AnnotationConfigUtils-->>AnnotationConfigUtils: registerAnnotationConfigProcessors(registry,source) 
AnnotationConfigUtils-->>AnnotationConfigUtils: registerPostProcessor(registry,definition,beanName) 
AnnotationConfigUtils-->>DefaultListableBeanFactory: registerBeanDefinition(beanName, beanDefinition)
~~~

#### 6.2、register(componentClasses)

在上一个步骤中`this()`已经执行完毕，接下来我们回到`AnnotationConfigApplicationContext`的构造函数中的`register(componentClasses)`方法来，**该方法我们重点关注MyConfiguration是如何被注册的过程**。

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

通过调用 `this.reader.register(componentClasses);` 进行实际的组件类注册。这里的 `reader` (`AnnotatedBeanDefinitionReader`),在构造函数中初始化完成的)是一个用于解析和注册注解配置的对象，这个方法会处理给定的组件类，解析其注解，并将相应的 `BeanDefinition` 注册到Spring容器中。`componentClasses`实际的类就是我们的`MyConfiguration`。

```java
public void register(Class<?>... componentClasses) {
    Assert.notEmpty(componentClasses, "At least one component class must be specified");
    StartupStep registerComponentClass = this.getApplicationStartup().start("spring.context.component-classes.register").tag("classes", () -> Arrays.toString(componentClasses));
    this.reader.register(componentClasses);
    registerComponentClass.end();
}
```

接收一个可变参数数组 `componentClasses`在循环内部，对每一个 `componentClass`，都调用了 `registerBean` 方法。这个方法

```java
public void register(Class<?>... componentClasses) {
    for (Class<?> componentClass : componentClasses) {
        registerBean(componentClass);
    }
}
```

在这个方法中，主要的逻辑被委托给了 `doRegisterBean` 方法

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

在这个方法中为`MyConfiguration`类创建`BeanDefinition`定义，最后，bean定义被封装在`BeanDefinitionHolder`中，并使用`BeanDefinitionReaderUtils.registerBeanDefinition`方法在bean定义注册表中注册。

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {

    // 1. 给MyConfiguration类创建一个新的AnnotatedGenericBeanDefinition
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
     // 2. 使用conditionEvaluator检查当前bean是否应被跳过
    if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
        return;
    }	
	 // 3. 如果提供了Supplier，则设置到bean定义中
    abd.setInstanceSupplier(supplier);
     // 4. 解析bean的作用域（singleton, prototype等）
    ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
    abd.setScope(scopeMetadata.getScopeName());
    
    // 5. 生成或使用给定的bean名称
    String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

    // 6. 处理常见的注解定义（例如：@Lazy, @Primary等）
    AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    
     // 7. 根据给定的限定符处理bean定义
    if (qualifiers != null) {
        for (Class<? extends Annotation> qualifier : qualifiers) {
            if (Primary.class == qualifier) {
                abd.setPrimary(true);
            }
            else if (Lazy.class == qualifier) {
                abd.setLazyInit(true);
            }
            else {
                abd.addQualifier(new AutowireCandidateQualifier(qualifier));
            }
        }
    }
    
    // 8. 使用任何提供的自定义器来修改bean定义
    if (customizers != null) {
        for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(abd);
        }
    }
	// 9. 创建一个BeanDefinitionHolder来持有bean定义及其名称
    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
     // 10. 如果需要，应用作用域代理模式
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
    // 11. 将bean定义注册到bean定义注册表
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
}
```

方法首先获取bean的主名称，并使用此名称将bean定义注册到注册表中。如果bean有别名，该方法还会将这些别名也注册到注册表中。别名在Spring中允许我们使用替代名称引用相同的bean。

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

下面是`MyConfiguration`被注册过程的时序图

~~~mermaid
sequenceDiagram
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)
AnnotationConfigApplicationContext-->>ConfigurationApplication: 返回context
AnnotationConfigApplicationContext-->>AnnotationConfigApplicationContext: register(componentClasses)
AnnotationConfigApplicationContext-->>AnnotatedBeanDefinitionReader: register(componentClasses)
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: registerBean(beanClass)
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: doRegisterBean(beanClass,name,qualifiers, supplier,customizers)
AnnotatedBeanDefinitionReader-->>BeanDefinitionReaderUtils: registerBeanDefinition(definitionHolder,registry)
BeanDefinitionReaderUtils-->>DefaultListableBeanFactory: registerBeanDefinition(beanName,beanDefinition)
~~~

#### 6.3、refresh()

在上一个步骤中`register(componentClasses)`已经执行完毕，接下来我们关注`refresh()`方法，在`refresh()`方法中调用了`invokeBeanFactoryPostProcessors(beanFactory);`是一个关键步骤，它确保所有的`BeanFactoryPostProcessor`被按预期的顺序执行，从而允许对bean定义进行必要的修改和处理，而`ConfigurationClassPostProcessor`类间接实现了`BeanFactoryPostProcessor`接口。**该方法我们重点关注ConfigurationClassPostProcessor是如何被调用过程**。

```java
public void refresh() throws BeansException, IllegalStateException {
	// ----------------------忽略其他代码---------------------------
	invokeBeanFactoryPostProcessors(beanFactory);
	// ----------------------忽略其他代码---------------------------
}
```

在`invokeBeanFactoryPostProcessors`方法中又委托了`PostProcessorRegistrationDelegate`类中的`invokeBeanFactoryPostProcessors`去执行。在`getBeanFactoryPostProcessors()`这个方法从当前的`ApplicationContext`实例中检索所有已注册`BeanFactoryPostProcessor`

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
	// ----------------------忽略其他代码---------------------------
}
```

```java
public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
    // ----------------------忽略其他代码---------------------------
	// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
	invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);	
    // ----------------------忽略其他代码---------------------------
}
```

最后在第147行被调用，执行了`invokeBeanFactoryPostProcessors(registryProcessors, beanFactory)`方法

![image-20230809155509098](https://img-blog.csdnimg.cn/21c9eda01a9048a3a8b047076c92a8dc.png#pic_center)

在此代码段中，`ConfigurationClassPostProcessor`的`postProcessBeanFactory`方法被调用，

```java
private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
    for (BeanFactoryPostProcessor postProcessor : postProcessors) {
        StartupStep postProcessBeanFactory = beanFactory.getApplicationStartup().start("spring.context.bean-factory.post-process").tag("postProcessor", postProcessor::toString);
        postProcessor.postProcessBeanFactory(beanFactory);
        postProcessBeanFactory.end();
    }
}
```

![image-20230809160938216](https://img-blog.csdnimg.cn/361a4012d236408fa55397129e5b4226.png#pic_center)

接下来，我们看看`ConfigurationClassPostProcessor`类中的`postProcessBeanFactory`方法是如何对`@Configuration`注解进行CGLIB增强的。`postProcessBeanFactory`方法中的`enhanceConfigurationClasses`调用，对标注为`@Configuration`的类进行了增强，确保了它们的`@Bean`方法行为符合预期。继续跟进`enhanceConfigurationClasses`方法

```java
@Override
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // ----------------------忽略其他代码---------------------------
    enhanceConfigurationClasses(beanFactory);
    beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
}
```

`enhanceConfigurationClasses`方法中主要的功能就是从`beanFactory.getBeanDefinitionNames()`中遍历`BeanDefinition`，筛选出full模式(proxyBeanMethods = true)下的`@Configuration`注解，然后通过`ConfigurationClassEnhancer`这个类来生成代理类(`com.xcs.spring.MyConfiguration$$EnhancerBySpringCGLIB$$60658f22`),然后进行替换`BeanDefinition`对象中的`beanClass`字段

```java
public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
	StartupStep enhanceConfigClasses = this.applicationStartup.start("spring.context.config-classes.enhance");
	// 用于存储需要增强的配置类的bean定义的map
    Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<>();
	// 循环遍历bean工厂中的所有bean定义
    for (String beanName : beanFactory.getBeanDefinitionNames()) {
		BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
        // 获取元数据属性以识别配置类
		Object configClassAttr = beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE);
		AnnotationMetadata annotationMetadata = null;
		MethodMetadata methodMetadata = null;
		if (beanDef instanceof AnnotatedBeanDefinition) {
			AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDef;
			annotationMetadata = annotatedBeanDefinition.getMetadata();
			methodMetadata = annotatedBeanDefinition.getFactoryMethodMetadata();
		}
        // 检查bean是否为配置类，且尚未增强
		if ((configClassAttr != null || methodMetadata != null) && beanDef instanceof AbstractBeanDefinition) {
			// Configuration class (full or lite) or a configuration-derived @Bean method
			// -> eagerly resolve bean class at this point, unless it's a 'lite' configuration
			// or component class without @Bean methods.
			AbstractBeanDefinition abd = (AbstractBeanDefinition) beanDef;
			if (!abd.hasBeanClass()) {
				boolean liteConfigurationCandidateWithoutBeanMethods =
						(ConfigurationClassUtils.CONFIGURATION_CLASS_LITE.equals(configClassAttr) &&
							annotationMetadata != null && !ConfigurationClassUtils.hasBeanMethods(annotationMetadata));
				if (!liteConfigurationCandidateWithoutBeanMethods) {
					try {
						abd.resolveBeanClass(this.beanClassLoader);
					}
					catch (Throwable ex) {
						throw new IllegalStateException(
								"Cannot load configuration class: " + beanDef.getBeanClassName(), ex);
					}
				}
			}
		}
         // 检查bean定义是否为full模式配置类(proxyBeanMethods = true)，如果是，则将其添加到configBeanDefs中。
		if (ConfigurationClassUtils.CONFIGURATION_CLASS_FULL.equals(configClassAttr)) {
			if (!(beanDef instanceof AbstractBeanDefinition)) {
				throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" +
						beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
			}
			else if (logger.isInfoEnabled() && beanFactory.containsSingleton(beanName)) {
				logger.info("Cannot enhance @Configuration bean definition '" + beanName +
						"' since its singleton instance has been created too early. The typical cause " +
						"is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor " +
						"return type: Consider declaring such methods as 'static'.");
			}
            // 添加到需要增强的配置类的bean定义的configBeanDefs
			configBeanDefs.put(beanName, (AbstractBeanDefinition) beanDef);
		}
	}
    // // 如果没有配置类需要增强，则只需结束该步骤
	if (configBeanDefs.isEmpty() || NativeDetector.inNativeImage()) {
		// nothing to enhance -> return immediately
		enhanceConfigClasses.end();
		return;
	}

    // 初始化增强器，用于增强配置类
	ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
	 // 循环遍历需要增强的配置类
	for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
		AbstractBeanDefinition beanDef = entry.getValue();
		// 对于@Configuration类，始终代理目标类
		beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
		// 在定义中设置增强后的类作为bean类
		Class<?> configClass = beanDef.getBeanClass();
		Class<?> enhancedClass = enhancer.enhance(configClass, this.beanClassLoader);
		if (configClass != enhancedClass) {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("Replacing bean definition '%s' existing class '%s' with " +
						"enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
			}
            // 吧BeanClass修改为代理类
			beanDef.setBeanClass(enhancedClass);
		}
	}
	enhanceConfigClasses.tag("classCount", () -> String.valueOf(configBeanDefs.keySet().size())).end();
}
```

![image-20230809175058746](https://img-blog.csdnimg.cn/3241f2e0658944a0af51786b98012938.png#pic_center)

接下来我们看看`ConfigurationClassEnhancer`类中的`enhance`方法是如何产生代理类的呢，在这个方法中调用了2个比较重要的方法，`newEnhancer`方法，`createClass`方法，我们继续跟进源码。。。

```java
public Class<?> enhance(Class<?> configClass, @Nullable ClassLoader classLoader) {
    // 如果configClass已经是增强的（或者说它已经是EnhancedConfiguration的子类或实例），
    // 则不再进行增强，并返回原始的configClass。
    if (EnhancedConfiguration.class.isAssignableFrom(configClass)) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Ignoring request to enhance %s as it has " +
                                       "already been enhanced. This usually indicates that more than one " +
                                       "ConfigurationClassPostProcessor has been registered (e.g. via " +
                                       "<context:annotation-config>). This is harmless, but you may " +
                                       "want check your configuration and remove one CCPP if possible",
                                       configClass.getName()));
        }
        return configClass;
    }
    // 如果configClass不是已知的增强类型，那么它将会被增强。
    // 使用CGLIB（或其他技术）为configClass创建一个新的增强版本。
    Class<?> enhancedClass = createClass(newEnhancer(configClass, classLoader));
    if (logger.isTraceEnabled()) {
        logger.trace(String.format("Successfully enhanced %s; enhanced class name is: %s",
                                   configClass.getName(), enhancedClass.getName()));
    }
    // 返回增强的类。
    return enhancedClass;
}
```

这个方法`newEnhancer`的目的是为一个指定的配置类（`MyConfiguration`）创建一个CGLIB的`Enhancer`对象，用于后续生成该配置类的代理或子类。在Spring中，CGLIB是用来在运行时生成Java类的代码库

```java
private Enhancer newEnhancer(Class<?> configSuperClass, @Nullable ClassLoader classLoader) {
    // 创建一个新的CGLIB Enhancer实例。
    Enhancer enhancer = new Enhancer();
    // 设置要增强的类的超类，即原始的配置类。
    enhancer.setSuperclass(configSuperClass);
    // 设置增强类实现的接口。这里，增强的类会实现EnhancedConfiguration接口，
    // 这通常用于后续的检查或识别。
    // 使用场景，在上个方法中(enhance)作为判断条件 if (EnhancedConfiguration.class.isAssignableFrom(configClass))
    enhancer.setInterfaces(new Class<?>[] {EnhancedConfiguration.class});
    // 设置使用工厂模式。这里设置为false意味着生成的增强类不会实现Factory接口。
    enhancer.setUseFactory(false);
    // 设置命名策略，这决定了生成的增强类的名称。
    enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
    // 设置策略，该策略决定如何生成增强类的字节码。
    // 这里，策略还负责使增强类变为“BeanFactory-aware”，
    // 这意味着它可以与Spring的BeanFactory交互。
    enhancer.setStrategy(new BeanFactoryAwareGeneratorStrategy(classLoader));
    // 设置回调过滤器，该过滤器决定哪些方法需要被代理以及如何被代理。
    enhancer.setCallbackFilter(CALLBACK_FILTER);
    // 设置增强类需要使用的回调类型，基于前面设置的回调过滤器。
    enhancer.setCallbackTypes(CALLBACK_FILTER.getCallbackTypes());
    // 返回为指定配置类准备好的Enhancer。
    return enhancer;
}
```

传入的CGLIB的`Enhancer`对象来创建一个新的增强类，并注册其相关的回调，我们看看`CALLBACKS`到底设置了那些回调参数

```java
private Class<?> createClass(Enhancer enhancer) {
    // 使用Enhancer创建一个新的增强类。这实际上会生成一个新的类的字节码，
    // 该类是原始配置类的子类，并增加了一些额外的功能或行为。
    Class<?> subclass = enhancer.createClass();
    // 注册回调函数。这些回调函数决定增强类中的哪些方法如何被增强或代理。
    // 使用静态注册（而不是基于线程局部的）在OSGi环境中是关键的，因为它确保
    // 回调在所有线程和类加载器之间都是可见的和一致的。
    // （注：OSGi是一个Java模块化系统，在这种环境中，类加载器和线程的行为可能与
    // 标准Java应用有所不同，所以特殊处理是必要的。）
    Enhancer.registerStaticCallbacks(subclass, CALLBACKS);
    
    // 返回新创建的增强类。
    return subclass;
}
```

让我们看看这三个回调：

`BeanMethodInterceptor`: 用于增强或代理那些对应于bean的方法

`BeanFactoryAwareMethodInterceptor`: 为代理类中的`$$beanFactory`字段赋值，具体请查看arthas反编译后的字节码类

`NoOp.INSTANCE`: 这是CGLIB提供的一个特殊回调，代表不执行任何操作。当使用这个回调增强方法时，方法的原始行为将不会被改变。

```java
private static final Callback[] CALLBACKS = new Callback[] {
    new BeanMethodInterceptor(),
    new BeanFactoryAwareMethodInterceptor(),
    NoOp.INSTANCE
};
```

首先看看`BeanMethodInterceptor`类的实现，此类主要用于拦截对 `@Configuration` 类中定义的 `@Bean` 方法的调用，以确保当这些方法被调用时，返回的 bean 实例是正确管理和处理的。

下面是拦截方法的参数介绍

- `enhancedConfigInstance`: 这是经过 CGLIB 增强的 `@Configuration` 类的实例。
- `beanMethod`: 被调用的 `@Bean` 方法。
- `beanMethodArgs`: 调用该方法时传递的参数。
- `cglibMethodProxy`: CGLIB 提供的方法代理，用于调用原始或超类的方法。

```java
/**
 * 拦截并处理对 @Bean 方法的调用。
 */
public Object intercept(Object enhancedConfigInstance, Method beanMethod, Object[] beanMethodArgs,
				MethodProxy cglibMethodProxy) throws Throwable {

	// 获取关联的 BeanFactory 通过反射读取了代理类中的$$beanFactory字段
	ConfigurableBeanFactory beanFactory = getBeanFactory(enhancedConfigInstance);
	// 确定当前 @Bean 方法对应的 bean 名称
	String beanName = BeanAnnotationHelper.determineBeanNameFor(beanMethod);

	// 检查当前的 @Bean 方法是否定义了一个作用域代理
	if (BeanAnnotationHelper.isScopedProxy(beanMethod)) {
		String scopedBeanName = ScopedProxyCreator.getTargetBeanName(beanName);
		if (beanFactory.isCurrentlyInCreation(scopedBeanName)) {
			beanName = scopedBeanName;
		}
	}
    // FactoryBeans 在 Spring 中是特殊的 beans，它们不产生 bean 实例本身，而是产生其他 beans。
    // 此代码块处理了当 FactoryBean 被请求时的情况，
    // 确保返回的是 FactoryBean 创建的实际 bean，而不是 FactoryBean 本身。
	if (factoryContainsBean(beanFactory, BeanFactory.FACTORY_BEAN_PREFIX + beanName) &&
		factoryContainsBean(beanFactory, beanName)) {
		// 此部分代码省略，但它处理 FactoryBean 创建的 bean 的返回和增强
	}

	// 检查当前的方法是否是正在被工厂调用的工厂方法
	if (isCurrentlyInvokedFactoryMethod(beanMethod)) {
		// 如果是，直接调用方法的原始实现
		return cglibMethodProxy.invokeSuper(enhancedConfigInstance, beanMethodArgs);
	}

	// 尝试从 bean 工厂中解析并返回 bean 的引用
	return resolveBeanReference(beanMethod, beanMethodArgs, beanFactory, beanName);
}
```

在看看`BeanFactoryAwareMethodInterceptor`类的实现，这个类的目的主要是为代理类设置的`$$beanFactory`的字段赋值

下面是拦截方法的参数介绍

- `obj`: 被代理的对象。
- `method`: 正在被调用的原方法。
- `args`: 调用方法时传入的参数。
- `proxy`: 代表CGLIB用于调用原始方法的`MethodProxy`对象。

```java
public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    // 使用反射查找obj类中名为$$beanFactory的字段
    Field field = ReflectionUtils.findField(obj.getClass(), BEAN_FACTORY_FIELD);
    
    // 确保找到了相关字段，如果没有找到，则抛出异常。
    Assert.state(field != null, "Unable to find generated BeanFactory field");

    // 将obj对象的BEAN_FACTORY_FIELD字段设置为args[0]，这里args[0]是BeanFactory的实例。
    field.set(obj, args[0]);

    // 检查obj的实际超类（不包括CGLIB生成的部分）是否实现了BeanFactoryAware接口。
    if (BeanFactoryAware.class.isAssignableFrom(ClassUtils.getUserClass(obj.getClass().getSuperclass()))) {
        // 如果实际超类实现了BeanFactoryAware接口，那么它会有一个setBeanFactory()方法，
        // 所以我们继续调用该方法。
        return proxy.invokeSuper(obj, args);
    }
    
    // 如果实际超类没有实现BeanFactoryAware接口，那么直接返回null。
    return null;
}
```

最后看看`NoOp.INSTANCE`类的实现，你会发现这个类什么都没有干，那么为什么会设置一个这样的回调呢？其目的是为什么呢？

举个例子，假设你只想拦截和处理代理对象的`setXXX`方法，而其他所有方法（如`getXXX`）都应该按原样执行，没有额外的逻辑。在这种情况下，你可以为`setXXX`方法设置特定的拦截器，而为`getXXX`方法设置`NoOp.INSTANCE`。因为CGLIB默认是代理所有的方法的，如果不提供NoOp.INSTANCE类，那么你可能会出现一个这样的异常信息`Exception in thread "main" java.lang.IllegalArgumentException: No callback found for index 1`

```java
public interface NoOp extends Callback {
    NoOp INSTANCE = new NoOp() {
    };
}
```

下面是`ConfigurationClassPostProcessor`被执行的时序图

~~~mermaid
sequenceDiagram
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)
AnnotationConfigApplicationContext-->>ConfigurationApplication: 返回context
AnnotationConfigApplicationContext-->>AnnotationConfigApplicationContext: refresh
AnnotationConfigApplicationContext-->>AnnotationConfigApplicationContext: invokeBeanFactoryPostProcessors
AnnotationConfigApplicationContext-->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)
PostProcessorRegistrationDelegate-->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(postProcessors,beanFactory)
PostProcessorRegistrationDelegate-->>ConfigurationClassPostProcessor: postProcessBeanFactory(beanFactory)
ConfigurationClassPostProcessor-->>ConfigurationClassPostProcessor: enhanceConfigurationClasses(beanFactory)
ConfigurationClassPostProcessor-->>ConfigurationClassEnhancer: enhance(configClass,classLoader)
ConfigurationClassEnhancer-->>ConfigurationClassEnhancer: createClass(enhancer)
ConfigurationClassEnhancer-->>ConfigurationClassPostProcessor: 增强后的Class类


~~~



### 7、常见问题

#### 7.1、在@Bean主键在方法上时，访问修饰符为什么不能是private或者final修饰呢？

那么我们对这两种场景做个测试。。。

##### 7.1.1、private修饰符

```java
@Configuration
public class MyConfiguration {

    @Bean
    private MyBean myBean(){
        return new MyBean();
    }
}
```

运行代码发现直接报错，启动失败。

```java
8月 10, 2023 2:30:41 下午 org.springframework.context.support.AbstractApplicationContext refresh
警告: Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.parsing.BeanDefinitionParsingException: Configuration problem: @Bean method 'myBean' must not be private or final; change the method's modifiers to continue
Offending resource: com.xcs.spring.MyConfiguration
Exception in thread "main" org.springframework.beans.factory.parsing.BeanDefinitionParsingException: Configuration problem: @Bean method 'myBean' must not be private or final; change the method's modifiers to continue
Offending resource: com.xcs.spring.MyConfiguration
	at org.springframework.beans.factory.parsing.FailFastProblemReporter.error(FailFastProblemReporter.java:72)
	at org.springframework.context.annotation.BeanMethod.validate(BeanMethod.java:52)
	at org.springframework.context.annotation.ConfigurationClass.validate(ConfigurationClass.java:220)
	at org.springframework.context.annotation.ConfigurationClassParser.validate(ConfigurationClassParser.java:216)
	at org.springframework.context.annotation.ConfigurationClassPostProcessor.processConfigBeanDefinitions(ConfigurationClassPostProcessor.java:332)
	at org.springframework.context.annotation.ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(ConfigurationClassPostProcessor.java:247)
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(PostProcessorRegistrationDelegate.java:311)
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:112)
	at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:746)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:564)
	at org.springframework.context.annotation.AnnotationConfigApplicationContext.<init>(AnnotationConfigApplicationContext.java:93)
	at com.xcs.spring.ConfigurationApplication.main(ConfigurationApplication.java:15)

```

##### 7.1.2、final修饰符

```java
@Configuration
public class MyConfiguration {

    @Bean
    public final MyBean myBean(){
        return new MyBean();
    }
}
```

同样的依旧是此错误

```java
8月 10, 2023 2:33:24 下午 org.springframework.context.support.AbstractApplicationContext refresh
警告: Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.parsing.BeanDefinitionParsingException: Configuration problem: @Bean method 'myBean' must not be private or final; change the method's modifiers to continue
Offending resource: com.xcs.spring.MyConfiguration
Exception in thread "main" org.springframework.beans.factory.parsing.BeanDefinitionParsingException: Configuration problem: @Bean method 'myBean' must not be private or final; change the method's modifiers to continue
Offending resource: com.xcs.spring.MyConfiguration
	at org.springframework.beans.factory.parsing.FailFastProblemReporter.error(FailFastProblemReporter.java:72)
	at org.springframework.context.annotation.BeanMethod.validate(BeanMethod.java:52)
	at org.springframework.context.annotation.ConfigurationClass.validate(ConfigurationClass.java:220)
	at org.springframework.context.annotation.ConfigurationClassParser.validate(ConfigurationClassParser.java:216)
	at org.springframework.context.annotation.ConfigurationClassPostProcessor.processConfigBeanDefinitions(ConfigurationClassPostProcessor.java:332)
	at org.springframework.context.annotation.ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(ConfigurationClassPostProcessor.java:247)
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(PostProcessorRegistrationDelegate.java:311)
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:112)
	at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:746)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:564)
	at org.springframework.context.annotation.AnnotationConfigApplicationContext.<init>(AnnotationConfigApplicationContext.java:93)
	at com.xcs.spring.ConfigurationApplication.main(ConfigurationApplication.java:15)
```

##### 7.1.3、原因分析

1. **CGLIB代理**: 当Spring使用CGLIB创建`@Configuration`类的代理时，它实际上是为这个类创建了一个子类。为了使代理可以工作，CGLIB需要能够重写`@Configuration`类中的`@Bean`方法。
   - **private方法**: 在java规范中被声明为`private`的方法不能被子类重写。由于CGLIB子类无法访问或重写这些方法，所以如果`@Bean`方法被声明为`private`，CGLIB代理将无法正确地管理它们。
   - **final方法**: 同样地在java规范中，`final`方法也不能被子类重写。因此，如果`@Bean`方法被声明为`final`，CGLIB也将无法管理这些方法。
2. **单例语义**: 在标准的`@Configuration`类中，当一个`@Bean`方法被调用多次时，它实际上只创建一个实例，因为这些方法调用是通过代理拦截的。这保证了单例bean的单例语义。如果`@Bean`方法是`private`或`final`的，Spring将无法拦截这些方法调用，从而可能导致每次调用都创建一个新的bean实例，违反了单例语义。

#### 7.2 @Configuration中full模式与lite模式如何选择？

`@Configuration` 注解有两种模式：`full` 和 `lite`。它们在功能和性能上有所不同。了解它们的优缺点有助于为特定的场景做出合适的选择。

##### 7.2.1 Full 模式

- 启用方式：在 `@Configuration` 注解中不设置 `proxyBeanMethods` 或将其设置为 `true`。
- 功能：当在配置类中的 `@Bean` 方法内部调用另一个 `@Bean` 方法时，Spring 会确保返回的是容器中的单例bean，而不是一个新的实例。这是通过CGLIB代理实现的。
- 优势：保持单例语义，确保容器中的单例Bean在配置类中的调用中始终是单例的。
- 劣势：需要通过CGLIB创建配置类的子类，可能带来一些性能开销，增加了启动时间，可能与某些库不兼容，这些库期望操作实际类而不是其CGLIB代理。

##### 7.2.2 Lite 模式

- 启用方式：在 `@Configuration` 注解中设置 `proxyBeanMethods` 为 `false`。
- 功能：禁用CGLIB代理。`@Bean` 方法之间的调用就像普通的Java方法调用，每次都会创建一个新的实例。
- 优势：更快的启动时间，因为不需要通过CGLIB增强配置类，对于简单的注入，这种模式可能更为简洁和直接。
- 劣势：不保持单例语义。如果在一个 `@Bean` 方法内部调用另一个 `@Bean` 方法，会创建一个新的bean实例。

##### 7.2.3 如何选择

- 如果你的配置中需要确保在配置类中调用的bean始终是Spring容器中的单例bean，选择full模式。
- 如果你的配置类只是简单地定义beans并注入依赖，且不需要在配置类方法之间共享单例实例，选择lite模式。
- 如果你关心应用的启动性能，特别是在云环境或微服务中，使用lite模式可能更合适，因为它避免了额外的CGLIB处理。

最终，根据项目的具体需求和场景选择合适的模式。如果没有特殊的单例需求，推荐使用lite模式，因为它更简单且启动性能更好。

### 8、总结

在这一节源码分析中，了解到了`@Configuration`的full模式`(proxyBeanMethods=true)`与lite模式`(proxyBeanMethods=false)`，并在4.1与4.2中做了测试并验证，另外还了解到了`@Configuration`注解几种组合用法，甚至我们可以多个组合使用的在spring中是非常常见的一种使用方式。然后我们利用arthas进行了反编译字节码进行原理分析，发现是利用CGLIB对`MyConfiguration`类继承方式然后重写了@Bean注解修饰的的方法来完成代理并保证了`@Bean`的语义。最后我们对源码进行了分析，其中最核心的类就是`ConfigurationClassPostProcessor`这个类间接实现了`BeanFactoryPostProcessor`接口中的`postProcessBeanFactory`方法，在这个方法中筛选出full模式下的的`BeanDefinition`，然后进行CGLIB增强处理。
