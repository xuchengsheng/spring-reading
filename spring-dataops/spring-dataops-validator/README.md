## Validator

- [Validator](#validator)
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

1. **Spring MVC**

   + 了解 Spring MVC 是很重要的。这包括控制器（Controllers）、请求映射（RequestMapping）、视图解析（View Resolution）等。理解如何处理 HTTP 请求和响应，以及 Spring MVC 中的数据绑定和表单处理。

2. **Bean Validation（JSR 303/JSR 380）**

   + 虽然 `Validator` 接口是 Spring 的一部分，了解 JSR 303/JSR 380（Bean Validation API）也是有益的，因为 Spring 支持这些标准。了解常用的 Bean Validation 注解，如 `@NotNull`, `@Size`, `@Min`, `@Max` 等。

3. **错误处理**

   + 理解 Spring 中的错误处理机制，特别是与数据验证相关的，如 `Errors` 和 `BindingResult` 接口。

### 三、基本描述

`Validator` 接口是 Spring 框架中用于实现对象数据验证的关键部分，它提供了一种标准化的方法来验证 Java 对象，特别是在处理用户输入和表单数据时。

### 四、主要功能

1. **自定义数据验证**

   + 允许我们根据业务需求实现自定义的验证逻辑，用于检查对象属性是否符合特定的约束和条件。

2. **支持多种对象类型**

   + 通过 `supports(Class<?> clazz)` 方法，验证器可以指定它能够验证哪些类型的对象，使得同一个验证器可以用于不同类型的数据模型。

3. **错误记录**

   + 在 `validate(Object target, Errors errors)` 方法中，验证器检查目标对象的属性，如果发现不符合要求的情况，可以将错误信息添加到 `Errors` 对象中，这些信息可用于错误展示和后续处理。

4. **与 Spring MVC 集成**

   + 在 Spring MVC 中，`Validator` 可以集成到控制器流程中，自动验证模型对象，常与 `@Valid` 注解一同使用，用于处理表单提交和请求参数的验证。

### 五、接口源码

`Validator` 接口，是一个用于自定义对象验证的关键组件。这个接口包含两个主要方法：`supports(Class<?> clazz)` 用于判断验证器是否支持特定类型的对象，通常通过检查对象是否为特定类或其子类来实现；`validate(Object target, Errors errors)` 用于执行实际的验证逻辑，其中 `target` 是待验证的对象，而 `errors` 用于记录验证中发现的错误。

```java
/**
 * 用于应用程序特定对象的验证器。
 *
 * <p>此接口与任何基础设施或上下文完全脱离关系；也就是说，它不仅限于验证Web层、数据访问层或任何其他层的对象。因此，它适用于应用程序的任何层次，并支持将验证逻辑作为其本身的一等公民进行封装。
 *
 * <p>下面是一个简单但完整的 {@code Validator} 实现示例，它验证了 {@code UserLogin} 实例的各种 {@link String} 属性不为空（即它们不是 {@code null} 并且不完全由空白字符组成），以及任何存在的密码至少是 {@code 'MINIMUM_PASSWORD_LENGTH'} 个字符长。
 *
 * <pre class="code"> public class UserLoginValidator implements Validator {
 *
 *    private static final int MINIMUM_PASSWORD_LENGTH = 6;
 *
 *    public boolean supports(Class clazz) {
 *       return UserLogin.class.isAssignableFrom(clazz);
 *    }
 *
 *    public void validate(Object target, Errors errors) {
 *       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "field.required");
 *       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required");
 *       UserLogin login = (UserLogin) target;
 *       if (login.getPassword() != null
 *             && login.getPassword().trim().length() < MINIMUM_PASSWORD_LENGTH) {
 *          errors.rejectValue("password", "field.min.length",
 *                new Object[]{Integer.valueOf(MINIMUM_PASSWORD_LENGTH)},
 *                "The password must be at least [" + MINIMUM_PASSWORD_LENGTH + "] characters in length.");
 *       }
 *    }
 * }</pre>
 *
 * <p>有关 {@code Validator} 接口及其在企业应用中的角色的更全面讨论，请参阅 Spring 参考手册。
 *
 * @author Rod Johnson
 * @see SmartValidator
 * @see Errors
 * @see ValidationUtils
 */
public interface Validator {

    /**
     * 判断这个 Validator 是否能够验证提供的 clazz 类型的实例。
     * <p>这个方法通常这样实现：
     * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
     * （其中 Foo 是实际要被验证的对象实例的类或超类。）
     * @param clazz 这个 Validator 被询问是否能验证的 Class 类型
     * @return 如果这个 Validator 能验证提供的 clazz 类型的实例，返回 {@code true}
     */
    boolean supports(Class<?> clazz);

    /**
     * 对提供的目标对象进行验证，该目标对象必须是 supports(Class) 方法通常会（或可能会）返回 true 的 Class 类型。
     * <p>提供的 Errors 实例可以用来报告任何验证错误。
     * @param target 需要被验证的对象
     * @param errors 验证过程中的上下文状态
     * @see ValidationUtils
     */
    void validate(Object target, Errors errors);

}
```

### 六、主要实现

1. **LocalValidatorFactoryBean**

   - 这是一个桥接类，用于在 Spring 应用中集成 JSR-303/JSR-380 Bean Validation API。它实现了 `Validator` 接口，允许使用标准的 Bean Validation 注解对对象进行验证。

2. **SpringValidatorAdapter**

   - 这个类适配 JSR-303/JSR-380 标准验证器到 Spring 的 `Validator` 接口。它使得可以在 Spring 中使用标准的 Bean Validation API。

### 七、最佳实践

使用 Spring 的 `Validator` 接口来验证 `Person` 对象的属性。程序创建了一个 `Person` 实例并设置了潜在的无效值，接着实例化了一个自定义的 `PersonValidator` 来进行验证。通过 `BeanPropertyBindingResult` 对象来存储和追踪验证过程中的错误，最后检查并打印出所有验证错误。

```java
public class ValidatorDemo {

    public static void main(String[] args) {
        // 创建一个 Person 对象实例
        Person person = new Person();
        person.setName(null);
        person.setAge(130);

        // 创建一个 BeanPropertyBindingResult 对象，用于存储验证过程中的错误
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(person, "person");

        // 创建一个 PersonValidator 实例，这是自定义的验证器
        PersonValidator validator = new PersonValidator();

        // 检查 PersonValidator 是否支持 Person 类的验证
        if (validator.supports(person.getClass())) {
            // 执行验证逻辑
            validator.validate(person, result);
        }

        // 检查是否存在验证错误，并打印出所有的字段错误
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println(error.getField() + ":" + error.getDefaultMessage());
            }
        }
    }
}
```

 `PersonValidator` 类用于检查 `Person` 对象的 `name` 是否为空以及 `age` 是否在有效范围内。如果发现不符合规则的字段，将相应的错误信息记录到 `Errors` 对象中。

```java
public class PersonValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        // 检查名称是否为空
        ValidationUtils.rejectIfEmpty(e, "name", "name.empty", "姓名不能为空");

        // 将对象转换为 Person 类型
        Person p = (Person) obj;

        // 检查年龄是否为负数
        if (p.getAge() < 0) {
            e.rejectValue("age", "negative.value", "年龄不能为负数");
        }
        // 检查年龄是否超过 120 岁
        else if (p.getAge() > 120) {
            e.rejectValue("age", "too.darn.old", "目前年龄最大的是120岁");
        }
    }
}
```

定义了一个简单的 Java 类 `Person`。

```java
public class Person {

    private String name;

    private int age;

    // get and set
}
```

运行结果发现， `PersonValidator` 正确地根据定义的验证规则识别出了 `Person` 对象的不合规数据，并通过 `Errors` 对象生成了相应的错误消息。

```
name:姓名不能为空
age:目前年龄最大的是120岁
```

### 八、与其他组件的关系

+ **Spring MVC框架**

  + `Validator` 接口的功能主要集中在处理和验证Web层的用户输入和模型数据。它与控制器紧密结合，用于在处理客户端请求（如表单提交）前验证模型数据。在数据绑定过程中，`Validator` 可用于检查绑定到模型对象的请求参数是否满足特定的验证规则。此外，Spring MVC 通过 `LocalValidatorFactoryBean` 支持与 JSR 303/JSR 380 Bean Validation 的集成，这允许我们在保持现有代码不变的情况下利用标准的 Bean Validation 注解。

### 九、常见问题

1. **正确实现 `supports` 方法**

   - 确保 `supports(Class<?> clazz)` 方法正确实现是常见的问题之一。这个方法需要正确判断验证器是否支持特定类型的对象，否则可能导致验证逻辑不被执行。

2. **逻辑的分离与国际化**

   - 我们在保持验证逻辑与业务逻辑分离的同时，需要确保验证逻辑完整且与业务规则相符。另外管理和显示验证错误信息，特别是在需要支持多语言环境下进行错误信息国际化的情况。

3. **集成与 JSR 303/JSR 380 Bean Validation**

   + 在使用 `Validator` 与 JSR 303/JSR 380 Bean Validation 标准共同工作时，了解二者之间的区别和如何有效集成是一个常见问题。

5. **嵌套属性和复杂对象的验证**

   + 当验证对象具有嵌套属性或结构复杂时，实现有效且高效的验证逻辑可能会更加困难。

