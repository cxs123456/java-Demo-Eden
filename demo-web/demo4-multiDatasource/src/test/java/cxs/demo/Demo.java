package cxs.demo;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/2 12:53
 **/
public class Demo {

    public static void main(String[] args) {
        // 测试 spel表达式

        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("A", 123);

        Object value = parser.parseExpression("#A != null", null).getValue(context);

        System.out.println(value);
    }
}
