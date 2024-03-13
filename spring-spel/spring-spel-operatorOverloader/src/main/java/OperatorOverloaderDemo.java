import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class OperatorOverloaderDemo {

    public static void main(String[] args) {
        // 创建表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        // 创建表达式上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 创建自定义的OperatorOverloader实例并注册到表达式上下文中
        context.setOperatorOverloader(new CustomOperatorOverloader());
        context.setVariable("myBean1", new MyBean(18));
        context.setVariable("myBean2", new MyBean(20));

        // 定义一个SpEL表达式，使用自定义的运算符
        Expression expression = parser.parseExpression("#myBean1 + #myBean2");

        // 解析并评估表达式
        MyBean myBean = expression.getValue(context, MyBean.class);

        System.out.println("myBean1+myBean2 = " + myBean);
    }
}
