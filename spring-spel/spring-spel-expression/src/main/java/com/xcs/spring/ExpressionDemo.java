package com.xcs.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ExpressionDemo {
    public static void main(String[] args) {
        // 创建解析器实例
        ExpressionParser parser = new SpelExpressionParser();
        // 解析基本表达式
        Expression expression = parser.parseExpression("100 + 10");
        // 为表达式计算结果
        Integer result = expression.getValue(Integer.class);
        System.out.println("表达式 '100 + 10' 的结果为: " + result);
    }
}
