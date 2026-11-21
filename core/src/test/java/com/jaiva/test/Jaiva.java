package com.jaiva.test;

import com.jaiva.tokenizer.tokens.*;
import com.jaiva.tokenizer.tokens.metadata.CodeRegion;
import com.jaiva.tokenizer.tokens.metadata.FunctionArg;
import com.jaiva.tokenizer.tokens.specific.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Jaiva {
    public static <T extends TokenDefault<T>> T assertAndCastToken(Object token, Class<T> clz) {
        if (token instanceof TokenDefault<?> l) {
            // Normally other tokens wrap others by calling .toToken()
            // but in the case where it can really only ever be one token
            // e.g. expressions in ternaries or if statements, it gets the token directly
            // hence handle it ehre
            Assertions.assertInstanceOf(clz, token);
            return (T) l;
        }
        Assertions.assertInstanceOf(Token.class, token);
        Token<?> e = (Token<?>) token;
        return assertAndCastToken(e, clz);
    }

    public static <T extends TokenDefault<T>> T assertAndCastToken(Token<?> token, Class<T> clz) {
        Assertions.assertNotNull(token);
        Assertions.assertInstanceOf(
                clz, token.value(),
                "Token was expected to be " + clz.getSimpleName() + ". Was instead " + token.value().getClass().getSimpleName()
                );
        return (T) token.value();
    }

    public static void assertLineNumber(TokenDefault<?> token, int expectedLineNumber) {
        Assertions.assertNotNull(token);
        Assertions.assertEquals(
                expectedLineNumber,
                token.lineNumber,
                "The " + token.name + " token (" + token.getClass().getSimpleName() + ") "
                + "was expected to be on line number " + expectedLineNumber
        );
    }

    public static void assertCodeblockLineNumber(CodeRegion token, int expectedLineNumber, int expectdEndNumber) {
        Assertions.assertNotNull(token);
        Assertions.assertEquals(
                expectedLineNumber,
                token.lineNumber,
                "The codeblock token (" + token.getClass().getSimpleName() + ") "
                        + "was expected to be on line number " + expectedLineNumber
        );
        Assertions.assertEquals(
                expectdEndNumber,
                token.lineNumberEnd,
                "The codeblock token (" + token.getClass().getSimpleName() + ") "
                + "was expected to end on line number " + expectdEndNumber
        );
    }

    public static ArrayList<CodeRegion> assertConstruct(TConstruct construct, int codeblockAmount) {
        Assertions.assertNotNull(construct);

        Assertions.assertEquals(
                codeblockAmount,
                construct.getCodeBlocks().size()
        );

        return construct.getCodeBlocks();
    }

    public static void assertFunctionDefinition(
            @NotNull TFunction function, ArrayList<FunctionArg> expectedArgs,
            boolean expectedVarArgs,
            @NotNull Predicate<String> defintionStringPredicate
    ) {
        // Arguments
        Assertions.assertIterableEquals(expectedArgs, function.args);
        // Not Var Args
        Assertions.assertEquals(expectedVarArgs, function.varArgs);
        // Definition string matches
        Assertions.assertTrue(defintionStringPredicate.test(function.toDefinitionString()));
    }

    public static void assertReferenceProperties(
            TReference reference,
            boolean length,
            boolean spread
    ) {
        Assertions.assertEquals(length, reference.getLength());
        Assertions.assertEquals(spread, reference.getSpreadArr());
    }

    public static void assertLoopType(
            TForLoop forLoop,
            LoopType type
    ) {
        if (type == LoopType.ARR) {
            Assertions.assertNotNull(forLoop.array);
            Assertions.assertNull(forLoop.increment);
            Assertions.assertInstanceOf(
                    TVarRef.class,
                    forLoop.variable
            );
        } else {
            Assertions.assertNull(forLoop.array);
            Assertions.assertNotNull(forLoop.increment);
            Assertions.assertInstanceOf(
                    TVariable.class,
                    forLoop.variable
            );
        }
    }

    public static enum LoopType {
        CSTYLE, ARR
    }
}
