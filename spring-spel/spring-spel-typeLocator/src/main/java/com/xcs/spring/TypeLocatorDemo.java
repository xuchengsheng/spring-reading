package com.xcs.spring;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class TypeLocatorDemo {
    public static void main(String[] args) {
        // 创建一个SpEL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        // 解析表达式获取 Date 类的 Class 对象
        Class dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
        System.out.println("dateClass = " + dateClass);

        // 解析表达式获取 String 类的 Class 对象
        Class stringClass = parser.parseExpression("T(String)").getValue(Class.class);
        System.out.println("stringClass = " + stringClass);

        // 解析表达式比较两个 RoundingMode 枚举值的大小
        boolean trueValue = parser.parseExpression("T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR").getValue(Boolean.class);
        System.out.println("trueValue = " + trueValue);
    }
}
