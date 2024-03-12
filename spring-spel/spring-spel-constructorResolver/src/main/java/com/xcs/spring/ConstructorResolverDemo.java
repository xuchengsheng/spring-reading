package com.xcs.spring;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author xcs
 * @date 2024年3月12日11:28:47
 **/
public class ConstructorResolverDemo {
    public static void main(String[] args) {
        // 创建一个SpEL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        // 解析SpEL表达式，并使用构造函数实例化对象
        // 这里的SpEL表达式是一个构造函数调用，创建了一个MyBean对象，参数为'spring-reading'
        MyBean myBean = parser.parseExpression("new com.xcs.spring.MyBean('spring-reading')").getValue(MyBean.class);

        // 打印输出实例化的MyBean对象
        System.out.println(myBean);
    }
}
