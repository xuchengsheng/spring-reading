## AopProxy

- [AopProxy](#AopProxy)
    - [一、基本信息](#一基本信息)
    - [二、知识储备](#二知识储备)
    - [三、基本描述](#三基本描述)
    - [四、主要功能](#四主要功能)
    - [五、接口源码](#五接口源码)
    - [六、主要实现](#六主要实现)
    - [七、最佳实践](#七最佳实践)
    - [八、与其他组件的关系](#八与其他组件的关系)
    - [九、常见问题](#九常见问题)

### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、知识储备

1. **代理模式**

   + 了解代理模式的概念，包括静态代理和动态代理。Spring AOP就是通过代理模式来实现切面功能的。

2. **JDK动态代理和CGLIB**
   + 了解JDK动态代理和CGLIB代理的区别，以及Spring AOP是如何选择代理方式的。`AopProxy` 接口的实现通常涉及这两种代理方式。
   
3. **Spring AOP的实现原理**

   + 深入了解Spring AOP的实现原理，包括如何创建代理、如何应用通知等。`AopProxy` 接口在Spring AOP中扮演着重要的角色，它负责创建代理对象。

### 三、基本描述

`AopProxy` 接口是Spring框架中用于支持面向切面编程（AOP）的关键组件之一，它定义了生成代理对象的标准接口，允许在运行时动态地创建代理对象，以实现对目标对象的方法调用进行拦截和增强。

### 四、主要功能

1. **代理对象的创建与管理**

   + `AopProxy` 接口定义了创建和管理代理对象的标准方法，可以通过这些方法在运行时动态地生成代理对象。

2. **对目标对象方法的拦截与增强**

   + AOP代理对象通过 `AopProxy` 接口实现对目标对象方法的拦截，允许在方法执行前、后或异常时执行额外的逻辑，从而实现对方法行为的增强。

3. **支持不同的代理方式**

   + `AopProxy` 接口支持多种代理方式，包括JDK动态代理和CGLIB代理。这样可以根据目标对象是否实现接口来选择合适的代理方式。

4. **实现AOP的横切关注点**

   + 通过 `AopProxy` 接口，可以将AOP的横切关注点与业务逻辑进行分离，提高了代码的模块化和可维护性，同时也使得横切关注点可以被重用在多个不同的业务逻辑中。

### 五、接口源码

`AopProxy` 接口是一个委托接口，用于配置AOP代理，并允许创建实际的代理对象。它提供了两个方法用于创建代理对象，第一个方法使用默认的类加载器创建代理对象，通常是线程上下文类加载器；第二个方法允许指定类加载器创建代理对象。可以使用JDK动态代理或者CGLIB代理技术来生成代理对象。

```java
/**
 * 配置AOP代理的委托接口，允许创建实际的代理对象。
 *
 * <p>默认情况下，可用于创建代理对象的实现包括JDK动态代理和CGLIB代理，
 * 这些代理实现由 {@link DefaultAopProxyFactory} 应用。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DefaultAopProxyFactory
 */
public interface AopProxy {

	/**
	 * 创建一个新的代理对象。
	 * <p>使用AopProxy的默认类加载器（必要时用于代理创建）：
	 * 通常为线程上下文类加载器。
	 * @return 新的代理对象（永远不会是 {@code null}）
	 * @see Thread#getContextClassLoader()
	 */
	Object getProxy();

	/**
	 * 创建一个新的代理对象。
	 * <p>使用给定的类加载器（必要时用于代理创建）。
	 * 如果给定的类加载器为 {@code null}，则简单地传递并因此导致低级代理工具的默认值，
	 * 这通常不同于AopProxy实现的 {@link #getProxy()} 方法选择的默认值。
	 * @param classLoader 用于创建代理的类加载器
	 * （或 {@code null} 表示使用低级代理工具的默认值）
	 * @return 新的代理对象（永远不会是 {@code null}）
	 */
	Object getProxy(@Nullable ClassLoader classLoader);

}
```

### 六、主要实现

1. **JdkDynamicAopProxy**

   + 使用 JDK 动态代理实现的 `AopProxy` 实现类。当目标对象实现了至少一个接口时，Spring 将使用该类创建代理对象。该类通过 Java 标准库中的 `java.lang.reflect.Proxy` 类来创建代理对象。

2. **CglibAopProxy**

   + 使用 CGLIB（Code Generation Library）动态代理实现的 `AopProxy` 实现类。当目标对象没有实现任何接口时，Spring 将使用该类创建代理对象。该类通过生成目标类的子类来创建代理对象，实现了对目标对象方法的拦截和增强。

### 七、最佳实践

#### JDK动态代理

使用 JDK 动态代理来创建 AOP 代理对象。在 `jdkProxy` 方法中，通过配置 `AdvisedSupport` 对象，设置目标对象和接口，然后利用反射创建 `JdkDynamicAopProxy` 实例，并调用 `AopProxy` 接口的 `getProxy` 方法生成代理对象。最后，输出代理对象的信息和调用代理对象方法的结果。

```java
public class AopProxyDemo {

    public static void main(String[] args) throws Exception {
        jdkProxy();
    }

    /**
     * Jdk代理
     *
     * @throws Exception
     */
    private static void jdkProxy() throws Exception {
        // 创建AdvisedSupport对象，用于配置AOP代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());
        // 设置目标对象实现的接口
        advisedSupport.setInterfaces(MyService.class);

        // 获取JdkDynamicAopProxy的Class对象
        Class jdkClass = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy");

        // 获取JdkDynamicAopProxy的构造方法
        Constructor constructor = jdkClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // 使用构造方法创建JdkDynamicAopProxy实例
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // 调用getProxy方法创建代理对象
        MyService myService = (MyService) aopProxy.getProxy();

        // 输出代理对象的信息
        System.out.println("JDK Class = " + myService.getClass());
        // 调用代理对象的方法
        System.out.println("doSomething method result = " + myService.doSomething());
    } 
}
```

运行结果，代理对象的类为 `com.sun.proxy.$Proxy0`，调用代理对象的 `doSomething` 方法结果为 `"hello world"`。

```java
JDK Class = class com.sun.proxy.$Proxy0
doSomething method result = hello world
```

#### CGLIB代理

使用 CGLIB 动态代理来创建 AOP 代理对象。在 `cglibProxy` 方法中，通过配置 `AdvisedSupport` 对象，设置目标对象，然后利用反射创建 `CglibAopProxy` 实例，并调用 `AopProxy` 接口的 `getProxy` 方法生成代理对象。最后，输出代理对象的信息和调用代理对象方法的结果。

```java
public class AopProxyDemo {

    public static void main(String[] args) throws Exception {
        cglibProxy();
    }

    /**
     * cglib代理
     *
     * @throws Exception
     */
    private static void cglibProxy() throws Exception {
        // 创建AdvisedSupport对象，用于配置AOP代理
        AdvisedSupport advisedSupport = new AdvisedSupport();
        // 设置目标对象
        advisedSupport.setTarget(new MyServiceImpl());

        // 获取CglibAopProxy的Class对象
        Class cglibClass = Class.forName("org.springframework.aop.framework.CglibAopProxy");

        // 获取CglibAopProxy的构造方法
        Constructor constructor = cglibClass.getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);

        // 使用构造方法创建CglibAopProxy实例
        AopProxy aopProxy = (AopProxy) constructor.newInstance(advisedSupport);

        // 调用getProxy方法创建代理对象
        MyService myService = (MyService) aopProxy.getProxy();

        // 输出代理对象的信息
        System.out.println("Cglib Class = " + myService.getClass());
        // 调用代理对象的方法
        System.out.println("doSomething method result = " + myService.doSomething());
    }   
}
```

运行结果，代理对象的类为 `com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$3c231008`，调用代理对象的 `doSomething` 方法结果为 `"hello world"`。

```java
Cglib Class = class com.xcs.spring.MyServiceImpl$$EnhancerBySpringCGLIB$$3c231008
doSomething method result = hello world
```

### 八、与其他组件的关系

1. **AdvisedSupport**

   + `AopProxy` 接口的实现类需要依赖 `AdvisedSupport` 对象来获取配置信息，包括目标对象、通知、切点等。通过 `AdvisedSupport` 对象，`AopProxy` 实现类可以知道如何创建代理对象。

2. **java.lang.reflect.Proxy**
   +  `java.lang.reflect.Proxy` 类是 Java 标准库中的一个类，用于创建基于接口的动态代理对象。它提供了静态方法 `newProxyInstance`，可以根据指定的类加载器、接口列表和调用处理器来创建代理对象。
   
3. **org.springframework.cglib.proxy.Enhancer**
   + `org.springframework.cglib.proxy.Enhancer` 类是 CGLIB 库中的一个类，用于创建基于类的动态代理对象。它能够生成目标类的子类，并重写其中的方法以实现方法拦截和增强。

### 九、常见问题

1. **选择合适的代理方式**

   + 在使用 Spring AOP 时，需要根据目标对象是否实现接口来选择合适的代理方式（JDK 动态代理还是 CGLIB 动态代理）。如果目标对象实现了接口，则会使用 JDK 动态代理，否则会使用 CGLIB 动态代理。选择合适的代理方式可以影响到代理对象的性能和行为。

2. **代理对象的类型**

   + 生成的代理对象的类型可能与原始目标对象的类型不同。这可能会导致一些类型转换或 instanceof 判断出现问题。因此，在使用代理对象时，需要注意其类型和行为与原始对象可能存在的差异。