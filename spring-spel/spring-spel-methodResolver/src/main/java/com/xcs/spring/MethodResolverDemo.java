package com.xcs.spring;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author xcs
 * @date 2024年3月12日11:28:47
 **/
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
