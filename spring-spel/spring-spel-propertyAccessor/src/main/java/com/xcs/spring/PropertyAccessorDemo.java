package com.xcs.spring;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;

public class PropertyAccessorDemo {
    public static void main(String[] args) throws AccessException {
        // 创建 ReflectivePropertyAccessor 对象
        ReflectivePropertyAccessor propertyAccessor = new ReflectivePropertyAccessor();

        // 创建一个对象，我们将在表达式中访问它的属性
        MyBean myBean = new MyBean();
        myBean.setName("spring-reading");

        // 创建一个 EvaluationContext 对象
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 演示 read 方法
        if (propertyAccessor.canRead(context, myBean, "name")) {
            System.out.println("Name: " + propertyAccessor.read(context, myBean, "name"));
        }

        // 演示 write 方法
        if (propertyAccessor.canWrite(context, myBean, "name")) {
            propertyAccessor.write(context, myBean, "name", "spring-reading-xcs");
            System.out.println("Updated Name: " + myBean.getName());
        }
    }
}
