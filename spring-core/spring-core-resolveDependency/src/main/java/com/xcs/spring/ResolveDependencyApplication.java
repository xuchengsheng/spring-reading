package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.MyServiceA;
import com.xcs.spring.service.MyServiceB;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author xcs
 * @date 2023年10月25日 10时13分
 **/
public class ResolveDependencyApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        // 获得Bean工厂
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // 被注入对象
        MyServiceB injectTarget = new MyServiceB();

        System.out.println("Before MyServiceB = " + injectTarget + "\n");

        methodResolveDependency(beanFactory, injectTarget, "setMethodMyServiceA");
        fieldResolveDependency(beanFactory, injectTarget, "fieldMyServiceA");
        fieldResolveDependency(beanFactory, injectTarget, "myPropertyValue");

        System.out.println("After MyServiceB = " + injectTarget + "\n");
    }

    /**
     * 解析方法依赖
     *
     * @param beanFactory
     * @param injectTarget
     */
    public static void methodResolveDependency(ConfigurableListableBeanFactory beanFactory, Object injectTarget, String name) {
        try {
            // 1. 获取MyServiceB类中名为setMyServiceA的方法的引用
            Method method = injectTarget.getClass().getMethod(name, MyServiceA.class);

            // 2. 创建一个描述此方法参数的DependencyDescriptor
            DependencyDescriptor desc = new DependencyDescriptor(new MethodParameter(method, 0), true);

            // 3. 使用BeanFactory来解析这个方法参数的依赖
            Object value = beanFactory.resolveDependency(desc, null);

            System.out.println("解析方法依赖结果:");
            System.out.println("---------------------------------------------");
            System.out.println(String.format("Name:   %s.%s",method.getDeclaringClass().getName(),method.getName()));
            System.out.println(String.format("Value:  %s", value));
            System.out.println("---------------------------------------------\n");

            // 4. 使方法可访问（特别是如果它是private的）
            ReflectionUtils.makeAccessible(method);

            // 5. 使用反射调用这个方法，将解析得到的依赖注入到目标对象中
            method.invoke(injectTarget, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析字段依赖
     *
     * @param beanFactory
     * @param injectTarget
     */
    public static void fieldResolveDependency(ConfigurableListableBeanFactory beanFactory, Object injectTarget, String name) {
        try {
            // 1. 获取MyServiceB类中名为fieldMyServiceA的字段的引用
            Field field = injectTarget.getClass().getDeclaredField(name);

            // 2. 创建一个描述此字段的DependencyDescriptor
            DependencyDescriptor desc = new DependencyDescriptor(field, true);

            // 3. 使用BeanFactory来解析这个字段的依赖
            Object value = beanFactory.resolveDependency(desc, null);

            System.out.println("解析字段依赖结果:");
            System.out.println("---------------------------------------------");
            System.out.println(String.format("Name:   %s.%s", field.getDeclaringClass().getName(), field.getName()));
            System.out.println(String.format("Value:  %s", value));
            System.out.println("---------------------------------------------\n");

            // 4. 使字段可访问（特别是如果它是private的）
            ReflectionUtils.makeAccessible(field);

            // 5. 使用反射设置这个字段的值，将解析得到的依赖注入到目标对象中
            field.set(injectTarget, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
