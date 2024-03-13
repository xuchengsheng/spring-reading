package com.xcs.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TypeConverterDemo {

    public static void main(String[] args) {
        // 创建SpEL表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();

        // 创建一个EvaluationContext
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 定义一个需要转换的值
        String stringValue = "'123'";

        // 解析表达式
        Expression expression = parser.parseExpression(stringValue);

        // 使用TypeConverter进行转换
        Integer intValue = expression.getValue(context, Integer.class);

        // 打印转换后的值
        System.out.println("Converted Integer value: " + intValue);
    }
}
