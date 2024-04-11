## JDK动态代理

- [JDK动态代理](#jdk动态代理)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、最佳实践](#四最佳实践)
  - [五、源码分析](#五源码分析)
  - [六、常见问题](#六常见问题)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

JDK动态代理是一种在运行时生成代理类的机制，它基于接口实现，通过在代理类中重定向方法调用到实际对象，并提供了InvocationHandler接口来实现代理对象方法调用的处理逻辑，适用于AOP、远程方法调用等场景，能够在不修改原始类的情况下实现横切关注点的统一管理，提供更灵活和可维护的代码结构。

### 三、主要功能

1. **代理对象生成**

   + 在运行时生成代理对象，无需提前编写代理类的代码。

2. **接口实现**

   + 动态代理是基于接口的，可以为接口生成代理对象，而不是具体的类。

3. **方法重定向**

   + 代理对象可以重定向方法调用到实际对象，允许在方法调用前后执行一些额外逻辑。

4. **横切关注点的统一管理**

   + 通过代理对象，在方法调用前后执行统一的逻辑，如日志记录、权限验证、事务管理等，实现了横切关注点的统一管理。

5. **AOP的实现**

   + 动态代理是AOP（面向切面编程）的基础之一，可以通过动态代理实现切面的横切关注点，将应用程序核心业务逻辑与横切关注点分离开来，提高了代码的可维护性和灵活性。

### 四、最佳实践

使用 JDK 动态代理的基本流程。首先，创建目标对象 `MyService` 的实例，然后获取目标对象的类信息。接着，通过调用 `Proxy.newProxyInstance` 方法创建代理对象，传入目标对象的类加载器、实现的接口以及调用处理器。最后，通过代理对象调用方法，实际上会调用 `MyInvocationHandler` 中的 `invoke` 方法来处理方法调用，并在方法执行前后添加额外的逻辑。

```java
public class JdkProxyDemo {

    public static void main(String[] args) {
        // 创建目标对象
        MyService target = new MyServiceImpl();
        // 获取目标对象的类对象
        Class clz = target.getClass();
        // 创建代理对象，并指定目标对象的类加载器、实现的接口以及调用处理器
        MyService proxyObject = (MyService) Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new MyInvocationHandler(target));
        // 打印代理对象的类信息
        System.out.println("ProxyObject = " + proxyObject.getClass());
        // 通过代理对象调用方法，实际上会调用 MyInvocationHandler 中的 invoke 方法
        proxyObject.doSomething();
    }
}
```

实现了 `InvocationHandler` 接口，定义了一个名为 `MyInvocationHandler` 的类。该类包含一个私有属性 `target`，用于存储目标对象。构造函数接收一个目标对象作为参数，并将其存储在 `target` 属性中。在 `invoke` 方法中，会在目标方法执行前打印 "Before method execution"，然后通过反射调用目标对象的方法，并获取方法的返回结果。最后，在目标方法执行后打印 "After method execution"，并返回方法的结果。

```java
class MyInvocationHandler implements InvocationHandler {

    private Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before method execution");
        Object result = method.invoke(target, args);
        System.out.println("After method execution");
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

运行结果，成功使用了 JDK 动态代理。首先打印了代理对象的类信息，确认了代理对象确实是由 `$Proxy0` 类生成的。接着，打印了 "Before method execution"，表示方法执行前的逻辑已经被执行。然后调用了目标对象的 `doSomething()` 方法，输出了 "hello world"。最后打印了 "After method execution"，表示方法执行后的逻辑也被执行了。这表明 JDK 动态代理成功地代理了目标对象的方法，并在方法执行前后执行了额外的逻辑。

```java
ProxyObject = class com.sun.proxy.$Proxy0
Before method execution
hello world
After method execution
```

### 五、源码分析

这段代码是通过 Arthas 工具反编译得到的结果。它是一个代理类，位于 `com.sun.proxy` 包下，命名为 `$Proxy0`。该类继承自 `Proxy` 类，并实现了 `MyService` 接口。在 `doSomething` 方法中，通过 `InvocationHandler` 对象的 `invoke` 方法调用了目标对象的 `doSomething` 方法。在静态代码块中，获取了 `java.lang.Object` 类中的 `equals`、`hashCode` 和 `toString` 方法，以及 `MyService` 接口中的 `doSomething` 方法的 `Method` 对象。

```java
package com.sun.proxy;

import com.xcs.spring.MyService;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public final class $Proxy0 extends Proxy implements MyService {
    
    private static Method m1;
    private static Method m3;
    private static Method m2;
    private static Method m0;

    public $Proxy0(InvocationHandler invocationHandler) {
        super(invocationHandler);
    }

    public final void doSomething() {
        try {
            this.h.invoke(this, m3, null);
            return;
        }
        catch (Error | RuntimeException throwable) {
            throw throwable;
        }
        catch (Throwable throwable) {
            throw new UndeclaredThrowableException(throwable);
        }
    }
    
    // ... [toString代码部分省略以简化]
    // ... [hashCode代码部分省略以简化]
    // ... [equals代码部分省略以简化]

    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m3 = Class.forName("com.xcs.spring.MyService").getMethod("doSomething", new Class[0]);
            m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
            m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
            return;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new NoSuchMethodError(noSuchMethodException.getMessage());
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}
```

### 六、常见问题

1. **代理对象的类型限制**

   + 由于 JDK 动态代理是基于接口实现的，因此只能代理实现了接口的类。如果目标对象没有实现接口，就无法使用 JDK 动态代理。

2. **无法代理 final 类和方法**

   + JDK 动态代理无法代理 final 类和 final 方法。因为 final 类无法被继承，final 方法无法被重写，而动态代理需要生成代理类并继承目标类或实现目标接口。

3. **无法代理静态方法**

   + JDK 动态代理无法代理静态方法，因为动态代理是基于实例的，而静态方法是属于类的。

4. **性能开销**

   + 与静态代理相比，JDK 动态代理的性能开销较大。动态代理在运行时生成代理类的字节码，并通过反射来调用方法，相比静态代理的直接方法调用，会增加额外的开销。

5. **泛型的处理**

   + 如果目标方法涉及泛型参数，代理对象可能无法正确处理。因为在泛型擦除后，代理对象无法获取到准确的泛型信息。

6. **调用堆栈的可读性**

   + 由于动态代理的调用会经过 `InvocationHandler`，可能会增加调用堆栈的深度，降低代码的可读性和调试的便利性。