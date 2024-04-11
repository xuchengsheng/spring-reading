## Cglib动态代理

- [Cglib动态代理](#cglib动态代理)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、源码分析](#五源码分析)
  - [六、常见问题](#六常见问题)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

Cglib 是一个基于 Java 的开源代码生成库，它通过动态生成字节码的方式实现了对类的动态代理，无需目标类实现接口即可进行代理，常用于 AOP 编程、方法拦截与增强等场景，提供了灵活而高效的代理解决方案。

### 三、主要功能

1. **动态代理生成**
   + Cglib 能够在运行时动态生成类的子类，从而实现对目标类的动态代理，无需目标类实现接口。
   
2. **AOP 支持**

   + Cglib 是 AOP 编程中常用的工具之一，它可以通过代理技术实现方法拦截和增强，方便实现横切关注点的功能。

3. **字节码操作**

   + Cglib 允许我们直接操作字节码，从而能够在运行时修改类的结构和行为。

4. **高性能**

   + 相对于 JDK 标准库中的动态代理，Cglib 生成的代理类性能更高，因为它直接操作字节码，而不是通过反射调用。

5. **无接口代理**

   + 与 JDK 动态代理不同，Cglib 可以代理那些没有实现接口的类，提供了更广泛的应用场景。

### 四、最佳实践

使用 Cglib 实现动态代理。它首先创建了一个 Enhancer 对象，然后设置了目标对象的父类和回调拦截器，最后通过 Enhancer 创建了代理对象。这个代理对象可以调用目标对象的方法，并且在方法执行前后执行拦截器中定义的逻辑。

```java
public class CglibProxyDemo {

    public static void main(String[] args) {
        // 创建 Enhancer 对象，用于生成代理类
        Enhancer enhancer = new Enhancer();
        // 设置目标对象的父类
        enhancer.setSuperclass(MyServiceImpl.class);
        // 设置回调拦截器
        enhancer.setCallback(new MyMethodInterceptor());
        // 创建代理对象
        MyService proxyObject = (MyService) enhancer.create();
        // 输出代理对象的类名
        System.out.println("ProxyObject = " + proxyObject.getClass());
        // 调用代理对象的方法
        proxyObject.doSomething();
    }
}
```

实现了 `MethodInterceptor` 接口的类，用于定义拦截器的行为。在 `intercept` 方法中，它接收被代理对象、目标方法、方法参数以及方法代理对象作为参数，并在目标方法执行前后执行一些逻辑。

```java
public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("Before invoking method: " + method.getName());
        Object result = methodProxy.invokeSuper(obj, args);
        System.out.println("After invoking method: " + method.getName());
        return result;
    }
}
```

定义了一个接口 `MyService`，其中包含一个抽象方法 `doSomething()`。然后，定义了一个实现了 `MyService` 接口的类 `MyServiceImpl`，并实现了 `doSomething()` 方法。

```java
public interface MyService {
    void doSomething();
}

public class MyServiceImpl implements MyService {

    @Override
    public void doSomething() {
        System.out.println("hello world");
    }
}
```

运行结果，成功创建了代理对象，并且在调用 `doSomething()` 方法前后执行了拦截器中定义的逻辑。

```java
ProxyObject = class com.xcs.spring.MyServiceImpl$$EnhancerByCGLIB$$bff4cd04
Before invoking method: doSomething
hello world
After invoking method: doSomething
```

### 五、源码分析

这段代码是通过反编译工具（arthas）得到的 Cglib 生成的代理类的源代码。这个代理类继承了目标类 `MyServiceImpl`，并实现了 `Factory` 接口。它重写了目标类的方法，并添加了拦截器逻辑。在每个方法的实现中，先尝试获取拦截器对象，然后通过拦截器的 `intercept` 方法执行拦截逻辑，最终调用目标方法。除此之外，它还包含了一些静态方法和静态字段，用于初始化和支持代理类的其他操作。

```java
package com.xcs.spring;

import com.xcs.spring.MyServiceImpl;
import java.lang.reflect.Method;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class MyServiceImpl$$EnhancerByCGLIB$$bff4cd04 extends MyServiceImpl implements Factory {
    private boolean CGLIB$BOUND;
    public static Object CGLIB$FACTORY_DATA;
    private static final ThreadLocal CGLIB$THREAD_CALLBACKS;
    private static final Callback[] CGLIB$STATIC_CALLBACKS;
    private MethodInterceptor CGLIB$CALLBACK_0;
    private static Object CGLIB$CALLBACK_FILTER;
    private static final Method CGLIB$doSomething$0$Method;
    private static final MethodProxy CGLIB$doSomething$0$Proxy;
    private static final Object[] CGLIB$emptyArgs;
    private static final Method CGLIB$equals$1$Method;
    private static final MethodProxy CGLIB$equals$1$Proxy;
    private static final Method CGLIB$toString$2$Method;
    private static final MethodProxy CGLIB$toString$2$Proxy;
    private static final Method CGLIB$hashCode$3$Method;
    private static final MethodProxy CGLIB$hashCode$3$Proxy;
    private static final Method CGLIB$clone$4$Method;
    private static final MethodProxy CGLIB$clone$4$Proxy;

    static void CGLIB$STATICHOOK1() {
        CGLIB$THREAD_CALLBACKS = new ThreadLocal();
        CGLIB$emptyArgs = new Object[0];
        Class<?> clazz = Class.forName("com.xcs.spring.MyServiceImpl$$EnhancerByCGLIB$$bff4cd04");
        Class<?> clazz2 = Class.forName("java.lang.Object");
        Method[] methodArray = ReflectUtils.findMethods(new String[]{"equals", "(Ljava/lang/Object;)Z", "toString", "()Ljava/lang/String;", "hashCode", "()I", "clone", "()Ljava/lang/Object;"}, clazz2.getDeclaredMethods());
        CGLIB$equals$1$Method = methodArray[0];
        CGLIB$equals$1$Proxy = MethodProxy.create(clazz2, clazz, "(Ljava/lang/Object;)Z", "equals", "CGLIB$equals$1");
        CGLIB$toString$2$Method = methodArray[1];
        CGLIB$toString$2$Proxy = MethodProxy.create(clazz2, clazz, "()Ljava/lang/String;", "toString", "CGLIB$toString$2");
        CGLIB$hashCode$3$Method = methodArray[2];
        CGLIB$hashCode$3$Proxy = MethodProxy.create(clazz2, clazz, "()I", "hashCode", "CGLIB$hashCode$3");
        CGLIB$clone$4$Method = methodArray[3];
        CGLIB$clone$4$Proxy = MethodProxy.create(clazz2, clazz, "()Ljava/lang/Object;", "clone", "CGLIB$clone$4");
        clazz2 = Class.forName("com.xcs.spring.MyServiceImpl");
        CGLIB$doSomething$0$Method = ReflectUtils.findMethods(new String[]{"doSomething", "()V"}, clazz2.getDeclaredMethods())[0];
        CGLIB$doSomething$0$Proxy = MethodProxy.create(clazz2, clazz, "()V", "doSomething", "CGLIB$doSomething$0");
    }

    final void CGLIB$doSomething$0() {
        super.doSomething();
    }

    public final void doSomething() {
        MethodInterceptor methodInterceptor = this.CGLIB$CALLBACK_0;
        if (methodInterceptor == null) {
            MyServiceImpl$$EnhancerByCGLIB$$bff4cd04.CGLIB$BIND_CALLBACKS(this);
            methodInterceptor = this.CGLIB$CALLBACK_0;
        }
        if (methodInterceptor != null) {
            Object object = methodInterceptor.intercept(this, CGLIB$doSomething$0$Method, CGLIB$emptyArgs, CGLIB$doSomething$0$Proxy);
            return;
        }
        super.doSomething();
    }
    
    // ... [代码部分省略以简化]

    // ... [toString代码部分省略以简化]
    // ... [hashCode代码部分省略以简化]
    // ... [equals代码部分省略以简化]
    // ... [clone代码部分省略以简化]

    // ... [代码部分省略以简化]
    static {
        MyServiceImpl$$EnhancerByCGLIB$$bff4cd04.CGLIB$STATICHOOK1();
    }
}
```

### 六、常见问题

1. **Final 类和方法无法代理**

   + 由于 Cglib 是通过生成目标类的子类来实现代理，所以无法代理被 final 修饰的类和方法。如果目标类或方法被标记为 final，则无法使用 Cglib 进行动态代理。

2. **构造函数无法被代理**

   + Cglib 无法代理目标类的构造函数。因为构造函数的调用是在对象创建阶段完成的，而代理对象在目标对象创建后才生成，因此无法代理构造函数。

3. **内部类无法被代理**

   + Cglib 无法代理目标类中的内部类。这是因为 Cglib 是通过生成目标类的子类来实现代理，而内部类无法被继承。