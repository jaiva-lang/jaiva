package com.jaiva.full;

import com.jaiva.Main;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.jaiva.full.Files.LAMBDA_JVA;
// test for the lambda.jva file in test/resources
public class LambdaTest {
    public static ArrayList<Token<?>> tokens;
    static {
        try {
            tokens = Main.parseTokens(LAMBDA_JVA.toString(), false);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
    @Test
    void lambdaParsed() {
        Assertions.assertEquals(4, tokens.size());

        // (Other test before test the imports, so those tokens dont need to be tested rigorously.

        Assertions.assertInstanceOf(TImport.class, tokens.getFirst().value(), "First token is not an import");
        Assertions.assertInstanceOf(TImport.class, tokens.get(1).value(), "Second token is not an import");

        // Variable containing the immediately invoked lambda

        Assertions.assertInstanceOf(TUnknownScalar.class, tokens.get(2).value(), "Third token is not variable");

        TUnknownScalar<?, ?> var = (TUnknownScalar<?, ?>) tokens.get(2).value();
        Assertions.assertEquals(4, var.lineNumber, "Variable is not on line number 4");
        Assertions.assertEquals("g", var.name, "Variable isn't named 'g'");
        // Variable's value
        Assertions.assertInstanceOf(TFuncCall.class, ((Token<?>)var.value).value(), "Variable's value is not a function call");
        TFuncCall lambdaCall = (TFuncCall) ((Token<?>) var.value).value();
        Assertions.assertEquals(1, lambdaCall.args.size(), "Function call does not contain a single argument.");
        Assertions.assertEquals("yolo", lambdaCall.args.getFirst(), "Function call does not contain 'yolo' as the single argument.");
        Assertions.assertInstanceOf(TLambda.class, lambdaCall.functionName, "Function name is not a lambda");
        TLambda lambda = (TLambda) lambdaCall.functionName;
        // Lambda
        Assertions.assertEquals(1, lambda.args.size(), "Lambda does not take in a single parameter");
        Assertions.assertEquals("msg", lambda.args.getFirst().getName(), "Lambda does not take in a parameter named 'msg'");
        Assertions.assertEquals(1, lambda.body.lines.size(), "Lambda has more than one line of code");
        Assertions.assertInstanceOf(TFuncReturn.class, lambda.body.lines.getFirst().value(), "First line of lambda is not a function return");
        Assertions.assertInstanceOf(TExpression.class, ((Token<?>)((TFuncReturn)lambda.body.lines.getFirst().value()).value).value(), "Lambda's return value is not an expression");
        TExpression expression = (TExpression) ((Token<?>) ((TFuncReturn)lambda.body.lines.getFirst().value()).value).value();
        // The expression the lambda tries to apply, not going to check rigorously
        Assertions.assertInstanceOf(Token.class, expression.lHandSide, "Expression's left hand side is not a token");
        Assertions.assertEquals("const", expression.rHandSide, "Expression's right hand side is not 'const'");
        Assertions.assertEquals("+", expression.op, "Expression's operator is not '+'");

        // d_emit Function call
        Assertions.assertInstanceOf(TFuncCall.class, tokens.getLast().value(), "Last token is not a function call");
    }
}
