import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;

public class CustomOperatorOverloader implements OperatorOverloader {
    @Override
    public boolean overridesOperation(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
        return leftOperand instanceof MyBean && rightOperand instanceof MyBean;
    }

    @Override
    public Object operate(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
        return new MyBean(((MyBean) leftOperand).getAge() + ((MyBean) rightOperand).getAge());
    }
}
