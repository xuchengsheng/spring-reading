package com.xcs.spring;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author xcs
 * @date 2024年1月3日22:35:51
 */
public class ExpressionParserDemo {

    public static void main(String[] args) {
        // 创建解析器实例
        ExpressionParser parser = new SpelExpressionParser();
        // 解析基本表达式
        Expression expression = parser.parseExpression("100 * 2 + 10");

        System.out.println("expression = " + expression);
    }
}
