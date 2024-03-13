package com.xcs.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TypeComparatorDemo {
    public static void main(String[] args) {
        // 创建一个EvaluationContext
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 创建SpEL表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();

        // 解析表达式
        Expression expression = parser.parseExpression("'2' < '-5.0'");

        // 使用TypeComparator进行比较
        boolean result = expression.getValue(context,Boolean.class);

        // 打印比较后的值
        System.out.println("result : " + result);
    }
}
