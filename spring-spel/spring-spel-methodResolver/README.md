## MethodResolver

- [MethodResolver](#MethodResolver)
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

1. **Spring 表达式语言（SpEL）**

   + `MethodResolver` 接口通常用于 SpEL 中，因此需要了解 SpEL 的语法和基本用法。这包括在 XML 或注解配置中如何使用 SpEL 表达式，以及如何在代码中动态解析 SpEL 表达式。

2. **反射（Reflection）**

   + `MethodResolver` 通常需要使用反射来动态地查找和调用对象的方法。因此，需要了解 Java 中的反射机制，包括 `Class`、`Method`、`Field` 等类的使用方法，以及如何通过反射调用对象的方法。

### 三、基本描述

`MethodResolver` 接口是 Spring Framework 表达式语言（SpEL）模块的关键组成部分之一，用于在运行时解析并执行对象的方法。它定义了一个方法 `resolve`，接受方法名和参数类型列表作为参数，并返回一个 `MethodExecutor` 对象，代表解析到的方法。`MethodResolver` 的实现负责根据给定的方法名和参数类型，确定要调用的方法，并创建一个对应的 `MethodExecutor` 实例，以便在表达式求值期间执行该方法。

### 四、主要功能

1. **方法解析**

   + `MethodResolver` 接口负责解析表达式中的方法调用。它根据方法名和参数类型列表，确定要调用的方法。

2. **动态方法调用**

   + `MethodResolver` 允许在运行时动态地调用对象的方法。这使得 SpEL 可以根据表达式的需求，灵活地调用对象的不同方法。

3. **方法执行器创建**

   + `MethodResolver` 返回一个 `MethodExecutor` 对象，该对象代表解析到的方法。`MethodExecutor` 负责实际执行方法，并返回执行结果。

4. **扩展性**

   + `MethodResolver` 接口是可扩展的，用户可以根据自己的需求实现自定义的 `MethodResolver`，以实现特定的方法解析逻辑。这使得 SpEL 在处理不同类型的对象和方法调用时更加灵活和可定制。

### 五、接口源码

`MethodResolver` 接口定义了一个方法解析器的规范，其主要功能是在给定的上下文中，根据对象和方法名，确定可以处理指定参数的合适方法，并返回一个执行器以供调用。这个接口允许在运行时动态地解析和调用对象的方法，为表达式语言提供了灵活性和扩展性。

```java
/**
 * 方法解析器尝试定位一个方法，并返回一个命令执行器，该执行器可用于调用该方法。命令执行器将被缓存，
 * 但如果它过期了，则会再次调用解析器。
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface MethodResolver {

    /**
     * 在提供的上下文中，确定目标对象上可以处理指定参数的合适方法。返回一个 {@link MethodExecutor}，
     * 该执行器可用于调用该方法，如果找不到方法，则返回 {@code null}。
     * @param context 当前的评估上下文
     * @param targetObject 调用方法的对象
     * @param name 方法名
     * @param argumentTypes 方法的参数类型列表
     * @return 可以调用方法的 MethodExecutor，如果找不到方法，则返回 null
     * @throws AccessException 如果访问方法时发生错误
     */
    @Nullable
    MethodExecutor resolve(EvaluationContext context, Object targetObject, String name,
                           List<TypeDescriptor> argumentTypes) throws AccessException;
}
```

`ReflectiveMethodResolver` 是一个基于反射的方法解析器，用于在给定的上下文中尝试定位目标对象上的适当方法，并返回一个方法执行器以供调用。它通过反射机制检查目标对象的方法，根据方法名和参数类型进行匹配。在匹配过程中，它考虑了方法的参数类型以及是否需要类型转换。此外，它还支持注册方法过滤器来过滤特定类型上的方法。在解析方法时，它会对方法进行排序，处理桥接方法，并且可以选择是否使用距离计算来查找更准确的匹配。如果找到匹配的方法，它将返回一个方法执行器；否则，返回 null。

```java
/**
 * 基于反射的 {@link MethodResolver}，在 {@link StandardEvaluationContext} 中默认使用，
 * 除非已经指定了显式的方法解析器。
 * 
 * 作者：Andy Clement, Juergen Hoeller, Chris Beams
 * 自从：3.0版本
 * @see StandardEvaluationContext#addMethodResolver(MethodResolver)
 */
public class ReflectiveMethodResolver implements MethodResolver {

	// 使用距离将确保发现更准确的匹配，
	// 更接近遵循Java规则。
	private final boolean useDistance;

	@Nullable
	private Map<Class<?>, MethodFilter> filters;

	public ReflectiveMethodResolver() {
		this.useDistance = true;
	}

	/**
	 * 此构造函数允许配置 ReflectiveMethodResolver，
	 * 使其使用距离计算来检查两个接近匹配中的更好的匹配
	 * （当存在多个匹配时）。使用距离计算旨在确保匹配更接近
	 * Java 编译器在考虑装箱/拆箱和方法候选人是否声明为
	 * 处理传入参数类型的类型（参数的类型）时会发生什么。
	 * @param useDistance 如果计算匹配时应使用距离计算，则为 {@code true}；否则为 {@code false}
	 */
	public ReflectiveMethodResolver(boolean useDistance) {
		this.useDistance = useDistance;
	}

	/**
	 * 注册给定类型的方法过滤器。
	 * @param type 要过滤的类型
	 * @param filter 相应的方法过滤器，
	 *               如果要清除给定类型的任何过滤器，则为 {@code null}
	 */
	public void registerMethodFilter(Class<?> type, @Nullable MethodFilter filter) {
		if (this.filters == null) {
			this.filters = new HashMap<>();
		}
		if (filter != null) {
			this.filters.put(type, filter);
		}
		else {
			this.filters.remove(type);
		}
	}

	/**
     * 在给定的上下文中，尝试定位目标对象上的适当方法，并返回一个方法执行器以供调用。
     * 方法执行器将被缓存，但如果其状态过期，解析器将被再次调用。
     * <p>
     * 在给定的上下文中，该方法确定可以处理指定参数的适当方法，并返回一个 {@link MethodExecutor} 对象，
     * 该对象代表解析到的方法。如果找不到方法，则返回 {@code null}。
     * @param context 当前的评估上下文
     * @param targetObject 调用方法的对象
     * @param name 方法名
     * @param argumentTypes 方法的参数类型列表
     * @return 可以调用方法的 MethodExecutor，如果找不到方法，则返回 null
     * @throws AccessException 如果访问方法时发生错误
     */
    @Override
    @Nullable
    public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name,
                                  List<TypeDescriptor> argumentTypes) throws AccessException {
        try {
            // 获取类型转换器
            TypeConverter typeConverter = context.getTypeConverter();
            // 获取目标对象的类
            Class<?> type = (targetObject instanceof Class ? (Class<?>) targetObject : targetObject.getClass());
            // 获取目标对象上的所有方法
            ArrayList<Method> methods = new ArrayList<>(getMethods(type, targetObject));

            // 如果为该类型注册了过滤器，请调用它
            MethodFilter filter = (this.filters != null ? this.filters.get(type) : null);
            if (filter != null) {
                List<Method> filtered = filter.filter(methods);
                methods = (filtered instanceof ArrayList ? (ArrayList<Method>) filtered : new ArrayList<>(filtered));
            }

            // 将方法排序为合理的顺序
            if (methods.size() > 1) {
                methods.sort((m1, m2) -> {
                    int m1pl = m1.getParameterCount();
                    int m2pl = m2.getParameterCount();
                    // 变长参数方法放在最后
                    if (m1pl == m2pl) {
                        if (!m1.isVarArgs() && m2.isVarArgs()) {
                            return -1;
                        } else if (m1.isVarArgs() && !m2.isVarArgs()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                    return Integer.compare(m1pl, m2pl);
                });
            }

            // 解析任何桥接方法
            for (int i = 0; i < methods.size(); i++) {
                methods.set(i, BridgeMethodResolver.findBridgedMethod(methods.get(i)));
            }

            // 删除重复的方法（由于解析的桥接方法可能导致）
            Set<Method> methodsToIterate = new LinkedHashSet<>(methods);

            Method closeMatch = null;
            int closeMatchDistance = Integer.MAX_VALUE;
            Method matchRequiringConversion = null;
            boolean multipleOptions = false;

            // 遍历方法，查找适合的方法
            for (Method method : methodsToIterate) {
                if (method.getName().equals(name)) {
                    int paramCount = method.getParameterCount();
                    List<TypeDescriptor> paramDescriptors = new ArrayList<>(paramCount);
                    // 构造方法参数类型描述符列表
                    for (int i = 0; i < paramCount; i++) {
                        paramDescriptors.add(new TypeDescriptor(new MethodParameter(method, i)));
                    }
                    ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
                    if (method.isVarArgs() && argumentTypes.size() >= (paramCount - 1)) {
                        // 处理变长参数
                        matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
                    } else if (paramCount == argumentTypes.size()) {
                        // 检查参数是否匹配
                        matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
                    }
                    if (matchInfo != null) {
                        if (matchInfo.isExactMatch()) {
                            return new ReflectiveMethodExecutor(method);
                        } else if (matchInfo.isCloseMatch()) {
                            if (this.useDistance) {
                                // 计算匹配的距离
                                int matchDistance = ReflectionHelper.getTypeDifferenceWeight(paramDescriptors, argumentTypes);
                                if (closeMatch == null || matchDistance < closeMatchDistance) {
                                    // 保存更好的匹配
                                    closeMatch = method;
                                    closeMatchDistance = matchDistance;
                                }
                            } else {
                                // 如果没有更好的匹配，将其视为接近匹配
                                if (closeMatch == null) {
                                    closeMatch = method;
                                }
                            }
                        } else if (matchInfo.isMatchRequiringConversion()) {
                            if (matchRequiringConversion != null) {
                                multipleOptions = true;
                            }
                            matchRequiringConversion = method;
                        }
                    }
                }
            }
            // 返回找到的方法执行器
            if (closeMatch != null) {
                return new ReflectiveMethodExecutor(closeMatch);
            } else if (matchRequiringConversion != null) {
                if (multipleOptions) {
                    // 如果有多个匹配，则抛出异常
                    throw new SpelEvaluationException(SpelMessage.MULTIPLE_POSSIBLE_METHODS, name);
                }
                return new ReflectiveMethodExecutor(matchRequiringConversion);
            } else {
                return null;
            }
        } catch (EvaluationException ex) {
            // 解析方法时出现异常
            throw new AccessException("Failed to resolve method", ex);
        }
    }


	private Set<Method> getMethods(Class<?> type, Object targetObject) {
		if (targetObject instanceof Class) {
			Set<Method> result = new LinkedHashSet<>();
			// 添加这些方法，以便在类型上可调用静态方法：例如 Float.valueOf（..）
			Method[] methods = getMethods(type);
			for (Method method : methods) {
				if (Modifier.isStatic(method.getModifiers())) {
					result.add(method);
				}
			}
			// 还从 java.lang.Class 本身公开方法
			Collections.addAll(result, getMethods(Class.class));
			return result;
		}
		else if (Proxy.isProxyClass(type)) {
			Set<Method> result = new LinkedHashSet<>();
			// 公开接口方法（不是代理声明的重写）以便适当的变长参数内省
			for (Class<?> ifc : type.getInterfaces()) {
				Method[] methods = getMethods(ifc);
				for (Method method : methods) {
					if (isCandidateForInvocation(method, type)) {
						result.add(method);
					}
				}
			}
			return result;
		}
		else {
			Set<Method> result = new LinkedHashSet<>();
			Method[] methods = getMethods(type);
			for (Method method : methods) {
				if (isCandidateForInvocation(method, type)) {
					result.add(method);
				}
			}
			return result;
		}
	}

	/**
	 * 返回此类型的方法集。默认实现返回给定类型的 {@link Class#getMethods()} 的结果，
	 * 但子类可以重写以更改结果，例如指定在其他地方声明的静态方法。
	 * @param type 要返回方法的类
	 * @since 3.1.1
	 */
	protected Method[] getMethods(Class<?> type) {
		return type.getMethods();
	}

	/**
	 * 确定给定的 {@code Method} 是否是在给定目标类的实例上进行方法解析的候选方法。
	 * <p>默认实现将任何方法都视为候选方法，即使对于 {@link Object} 基类的静态方法
	 * 和非用户声明的方法也是如此。
	 * @param method 要评估的方法
	 * @param targetClass 正在内省的具体目标类
	 * @since 4.3.15
	 */
	protected boolean isCandidateForInvocation(Method method, Class<?> targetClass) {
		return true;
	}

}
```

### 六、主要实现

1. **DataBindingMethodResolver** 

   + 是用于数据绑定环境的方法解析器，支持根据数据绑定的规则解析方法，例如 Spring 数据绑定框架中的规则，并可能使用更高级的技术如 Spring DataBinding，根据数据绑定的配置和元数据来查找适当的方法。

2. **ReflectiveMethodResolver** 

   + 是基于反射机制的通用方法解析器，通过反射检查目标对象的方法，根据方法名和参数类型进行匹配，适用于大多数的 Java 对象和方法的解析，不需要特殊的配置或规则，只需要基于 Java 反射来解析方法即可。

### 七、最佳实践

使用Spring Expression Language（SpEL）来调用自定义对象（`MyBean`）中的方法。首先，通过SpEL表达式解析器创建一个解析器和一个上下文对象，然后实例化一个`MyBean`对象并将其设置为上下文中的变量。接着，创建一个SpEL表达式，调用`MyBean`对象的`add`方法，并传入两个参数。最后，将表达式与上下文关联，并计算表达式的值，即调用`MyBean`对象的方法并获取结果。

```java
public class MethodResolverDemo {

    public static void main(String[] args) {
        // 创建一个SpEL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = new StandardEvaluationContext();

        // 在 MyBean 中定义的方法将被 SpEL 表达式调用
        MyBean myBean = new MyBean();
        context.setVariable("myBean", myBean);

        // 创建一个 SpEL 表达式，调用 MyBean 中的方法
        SpelExpression expression = (SpelExpression) parser.parseExpression("#myBean.add(10, 5)");

        // 为表达式设置上下文，并计算结果
        int result = (int) expression.getValue(context);

        // 打印输出实例化的MyBean对象
        System.out.println("result = " + result);
    }
}
```

 `MyBean` 类定义了一个简单的方法 `add`，它接受两个整数参数，并返回它们的和。

```java
public class MyBean {
    public int add(int a, int b) {
        return a + b;
    }
}
```

运行结果，在 `MethodResolverDemo` 中调用了 `MyBean` 对象的 `add` 方法，并传入了两个参数（10 和 5），所以计算结果为 10 + 5 = 15。

```java
result = 15
```

### 八、与其他组件的关系

1. **StandardEvaluationContext**

   + `MethodResolver` 接口通常与 `StandardEvaluationContext` 类一起使用。`StandardEvaluationContext` 提供了 SpEL 表达式的运行时上下文，其中可以存储变量、函数等信息，并且可以设置自定义的方法解析器，其中就包括了 `MethodResolver` 接口的实现类。

2. **ExpressionParser**

   + `ExpressionParser` 接口用于解析 SpEL 表达式。在 SpEL 中，可以使用 `ExpressionParser` 实现类来解析表达式，例如 `SpelExpressionParser`。在解析 SpEL 表达式时，通常需要提供一个 `EvaluationContext`，这个上下文中可能会包含一个或多个 `MethodResolver`，用于解析表达式中的方法调用。

3. **MethodExecutor**

   + `MethodResolver` 接口的 `resolve` 方法返回一个 `MethodExecutor` 对象，它用于执行解析到的方法。`MethodExecutor` 是一个接口，它定义了执行方法的方法 `execute`，具体的执行逻辑由其实现类提供。

4. **ReflectiveMethodResolver** & **DataBindingMethodResolver**

   + 这两个类是 `MethodResolver` 接口的实现类，分别用于基于反射和数据绑定环境下的方法解析。它们与 `MethodResolver` 接口的关系在于，它们实现了 `MethodResolver` 接口，并提供了具体的方法解析逻辑。在使用 SpEL 表达式时，可以选择性地使用这两个类中的一个或者自定义其他的方法解析器来解析方法调用。

### 九、常见问题

1. **如何自定义方法解析器？**

   - 可以实现 `MethodResolver` 接口，并覆写其中的 `resolve` 方法来定义自定义的方法解析逻辑。然后将这个自定义的方法解析器注册到 `StandardEvaluationContext` 中。

2. **方法解析器的优先级如何确定？**

   - 在 `StandardEvaluationContext` 中，可以注册多个方法解析器，它们的解析顺序由注册的顺序决定。解析时会按照注册的顺序逐个调用方法解析器，直到找到匹配的方法。

3. **如何处理方法重载？**

   - 当方法重载时，`MethodResolver` 需要根据传入的参数类型列表来确定最适合的方法。在解析方法调用时，会尝试匹配所有可能的方法，并根据参数类型的匹配程度来选择最佳的匹配。

4. **如何处理变长参数方法？**

   - 变长参数方法（例如使用 `...` 定义的参数）在方法解析中需要特殊处理。方法解析器需要检查传入的参数数量和类型是否与方法定义匹配，并正确处理变长参数的情况。

5. **如何处理方法参数的类型转换？**

   - 当方法参数的类型与传入参数的类型不完全匹配时，可能需要进行类型转换。方法解析器可以使用注册的类型转换器来进行必要的参数类型转换。

6. **如何处理桥接方法？**

   - 桥接方法是 Java 泛型中的一个特殊情况，可能会导致方法重载或者匹配错误。方法解析器需要正确处理桥接方法，以确保找到正确的方法进行调用。

7. **如何处理方法的静态调用？**

   - 当调用静态方法时，需要在方法解析器中考虑到静态方法的情况。方法解析器需要正确处理静态方法的调用，以确保可以找到并调用正确的静态方法。