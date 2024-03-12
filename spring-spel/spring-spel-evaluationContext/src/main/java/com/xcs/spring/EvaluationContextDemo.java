package com.xcs.spring;

import org.springframework.expression.*;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

/**
 * @author xcs
 * @date 2024年3月12日11:28:47
 **/
public class EvaluationContextDemo {
    public static void main(String[] args) {
        // 创建评估上下文
        EvaluationContext context = new StandardEvaluationContext(new MyRootObject("Root Data"));

        // 获取根对象
        TypedValue root = context.getRootObject();
        System.out.println("根对象: " + root.getValue());

        // 设置变量
        context.setVariable("name", "John");
        System.out.println("变量'name'的值: " + context.lookupVariable("name"));

        // 获取属性访问器
        List<PropertyAccessor> propertyAccessors = context.getPropertyAccessors();
        System.out.println("属性访问器: " + propertyAccessors);

        // 获取构造函数解析器
        List<ConstructorResolver> constructorResolvers = context.getConstructorResolvers();
        System.out.println("构造函数解析器: " + constructorResolvers);

        // 获取方法解析器
        List<MethodResolver> methodResolvers = context.getMethodResolvers();
        System.out.println("方法解析器: " + methodResolvers);

        // 获取 bean 解析器
        BeanResolver beanResolver = context.getBeanResolver();
        System.out.println("bean 解析器: " + beanResolver);

        // 获取类型定位器
        TypeLocator typeLocator = context.getTypeLocator();
        System.out.println("类型定位器: " + typeLocator);

        // 获取类型转换器
        TypeConverter typeConverter = context.getTypeConverter();
        System.out.println("类型转换器: " + typeConverter);

        // 获取类型比较器
        TypeComparator typeComparator = context.getTypeComparator();
        System.out.println("类型比较器: " + typeComparator);

        // 获取运算符重载器
        OperatorOverloader operatorOverloader = context.getOperatorOverloader();
        System.out.println("运算符重载器: " + operatorOverloader);
    }

    // 定义根对象的类
    static class MyRootObject {
        private String data;

        public MyRootObject(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
