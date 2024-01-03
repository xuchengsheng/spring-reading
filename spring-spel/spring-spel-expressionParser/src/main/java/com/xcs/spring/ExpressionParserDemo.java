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
        try {
            Expression expression = parser.parseExpression("100 * 2 + 10");
            Integer result = expression.getValue(Integer.class);
            System.out.println("表达式 '100 * 2 + 10' 的结果为: " + result);
        } catch (ParseException e) {
            System.err.println("解析表达式出错: " + e.getMessage());
        }

        // 使用上下文的解析
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("myVariable", 50);
            Expression expressionWithContext = parser.parseExpression("100 * #myVariable + 10");
            Integer resultWithContext = expressionWithContext.getValue(context, Integer.class);
            System.out.println("带上下文的表达式 '100 * #myVariable + 10' 的结果为: " + resultWithContext);
        } catch (ParseException e) {
            System.err.println("解析带上下文的表达式出错: " + e.getMessage());
        }
    }
}
