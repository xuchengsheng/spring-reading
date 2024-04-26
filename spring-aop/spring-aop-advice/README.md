## Advice

- [Advice](#advice)
  - [一、基本信息](#一基本信息)
  - [二、基本描述](#二基本描述)
  - [三、主要功能](#三主要功能)
  - [四、接口源码](#四接口源码)
  - [五、子接口](#五子接口)
  - [六、类关系图](#六类关系图)


### 一、基本信息

✒️ **作者** - Lex 📝 **博客** - [掘金](https://juejin.cn/user/4251135018533068/posts) 📚 **源码地址** - [github](https://github.com/xuchengsheng/spring-reading)

### 二、基本描述

`Advice`接口是Spring AOP中的核心接口之一，用于定义在切面逻辑中要执行的操作。它允许我们在目标方法执行前、执行后、抛出异常时等不同的连接点上添加自定义的行为。`Advice`接口的实现类可以通过方法拦截器（MethodInterceptor）、前置通知（BeforeAdvice）、后置通知（AfterReturningAdvice）、异常通知（ThrowsAdvice）等方式来实现不同类型的通知逻辑。

### 三、主要功能

1. **定义通知逻辑** 

   + 允许我们定义在目标方法执行前、执行后、抛出异常时等不同连接点上执行的操作。

2. **支持不同类型的通知**

   + `Advice`接口的实现类可以实现不同类型的通知逻辑，如前置通知、后置通知、环绕通知、异常通知等。

3. **与切点结合** 

   + `Advice`通常与切点（Pointcut）结合使用，以确定通知应该在哪些连接点上执行。

4. **应用于Advisor** 

   + `Advice`通常作为`Advisor`的一部分，与切点结合，以实现切面的逻辑。

### 四、接口源码

`Advice`接口是Spring AOP中的一个标签接口，用于定义各种类型的通知，例如拦截器。通过实现该接口，我们可以定义在方法执行前、执行后、抛出异常时等不同连接点上执行的操作，从而实现对应用程序行为的干预和控制。

```java
/**
 * Advice的标签接口。实现可以是任何类型的通知，例如拦截器。
 * 
 * 该接口用于定义通知。通知可以是在方法执行前、执行后、抛出异常时等不同连接点上执行的操作。
 * 实现该接口的类可以是拦截器（Interceptors）等任何类型的通知。
 *
 * @author Rod Johnson
 * @version $Id: Advice.java,v 1.1 2004/03/19 17:02:16 johnsonr Exp $
 */
public interface Advice {

}
```

### 五、子接口

1. **AfterAdvice（后置通知）** 

   + 是一个标记接口，用于表示后置通知的类型。

2. **AfterReturningAdvice（返回后通知）** 

   + 用于在目标方法成功执行并返回结果后执行自定义逻辑。

3. **BeforeAdvice（前置通知）** 

   + 用于在目标方法执行前执行自定义逻辑。

4. **ConstructorInterceptor（构造器拦截器）** 

   + 实现该接口的类可以在目标对象的构造器被调用时执行自定义逻辑。

5. **Interceptor（拦截器）** 

   + 是一个标记接口，表示通用的拦截器类型，通常用于包装方法调用。

6. **IntroductionInterceptor（引介拦截器）** 

   + 实现该接口的类可以在目标对象上添加新的方法和属性，用于实现AOP引介功能。

7. **MethodBeforeAdvice（方法前置通知）** 

   + 用于在目标方法执行前执行自定义逻辑。

8. **MethodInterceptor（方法拦截器）** 

   + 实现该接口的类可以在目标方法执行前、执行后以及抛出异常时进行拦截，并执行自定义的逻辑。

9. **ThrowsAdvice（异常通知）** 

   + 用于在目标方法抛出异常时执行自定义逻辑。

### 六、类关系图

~~~mermaid
classDiagram
direction BT
class Advice {
<<Interface>>

}
class AfterAdvice {
<<Interface>>

}
class AfterReturningAdvice {
<<Interface>>

}
class BeforeAdvice {
<<Interface>>

}
class ConstructorInterceptor {
<<Interface>>

}
class Interceptor {
<<Interface>>

}
class IntroductionInterceptor {
<<Interface>>

}
class MethodBeforeAdvice {
<<Interface>>

}
class MethodInterceptor {
<<Interface>>

}
class ThrowsAdvice {
<<Interface>>

}

AfterAdvice  -->  Advice 
AfterReturningAdvice  -->  AfterAdvice 
BeforeAdvice  -->  Advice 
ConstructorInterceptor  -->  Interceptor 
Interceptor  -->  Advice 
IntroductionInterceptor  -->  Advice 
IntroductionInterceptor  -->  MethodInterceptor 
MethodBeforeAdvice  -->  BeforeAdvice 
MethodInterceptor  -->  Interceptor 
ThrowsAdvice  -->  AfterAdvice 

~~~