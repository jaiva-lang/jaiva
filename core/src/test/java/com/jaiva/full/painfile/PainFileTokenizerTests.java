package com.jaiva.full.painfile;

import com.jaiva.test.Jaiva;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.jdoc.tags.DParameter;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.specific.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PainFileTokenizerTests{

    static PainFileTester pft;
    static ArrayList<Token<?>> tokens;

    @BeforeAll
    static void setUp() throws Exception{
        pft = new PainFileTester();
        tokens = pft.tokenize();
    }

    @Test
    void structureTest() {
        Assertions.assertNotNull(tokens);

        // The file should contain 11 top level tokens
        Assertions.assertEquals(11, tokens.size(),
                "Tokenizer emitted more than the expected 11 tokens ("+tokens.size()+")."
        );
    }

    @Test
    void importsTest() {
        TImport arraysImport = Jaiva.assertAndCastToken(tokens.getFirst(), TImport.class);
        TImport debugImport = Jaiva.assertAndCastToken(tokens.get(1), TImport.class);

        Jaiva.assertLineNumber(arraysImport, 26);
        Jaiva.assertLineNumber(debugImport, 27);

        Assertions.assertNotNull(arraysImport);
        Assertions.assertNotNull(debugImport);

        // Imports are normalised to the user's directory even if library so check for the name
        // contained in the import

        Assertions.assertTrue(arraysImport.fileName.contains("arrays"));
        Assertions.assertTrue(debugImport.fileName.contains("debug"));

        Assertions.assertTrue(arraysImport.isLib);
        Assertions.assertTrue(debugImport.isLib);

        Assertions.assertEquals(new ArrayList<>(), arraysImport.symbols);
        Assertions.assertEquals(new ArrayList<>(List.of("d_emit")), debugImport.symbols);
    }

    @Test
    void replaceOddChar_functionDefintion() {
        TFunction replaceOddChar =  Jaiva.assertAndCastToken(tokens.get(2), TFunction.class);
        Jaiva.assertLineNumber(replaceOddChar, 34);
        // name with F~ prepended to it
        Assertions.assertEquals("F~replaceOddChar", replaceOddChar.name);

        // Function is exportedc with * syntax
        Assertions.assertTrue(replaceOddChar.exportSymbol);

        Jaiva.assertFunctionDefinition(
                replaceOddChar, new String[]{"original", "c"},
                new ArrayList<>(List.of(false, false)),
                false,
                (actual) -> {
                    return actual.equals("F~replaceOddChar(original, c)");
                }
        );

        // Function has only a single body
        TCodeblock body = Jaiva.assertConstruct(replaceOddChar, 1).getFirst();

        // the body goes to line number 45 and has 5 tokens
        Jaiva.assertCodeblockLineNumber(
                body, 34, 45
        );
        Assertions.assertEquals(5, body.lines.size());
    }

    @Test
    void replaceOddChar_jdoc() {
        TFunction replaceOddChar =  Jaiva.assertAndCastToken(tokens.get(2), TFunction.class);
        Assertions.assertNotNull(replaceOddChar.tooltip);
        Assertions.assertInstanceOf(JDoc.class, replaceOddChar.tooltip);
        JDoc doc = (JDoc) replaceOddChar.tooltip;
        Assertions.assertNotNull(doc.getDescription());
        Assertions.assertEquals(
                "My lovely function which replaces the odd character",
                doc.getDescription().trim()
        );
        ArrayList<DParameter> parameterDocs = doc.getParameters();
        Assertions.assertEquals(2,  parameterDocs.size());
        // first param's doc
        DParameter originalDoc = parameterDocs.getFirst();
        Assertions.assertEquals("original", originalDoc.varName);
        Assertions.assertEquals("string", originalDoc.type);
        Assertions.assertFalse(originalDoc.optional);
        Assertions.assertEquals("The original text", originalDoc.desc);
        // second param's doc
        DParameter cDoc = parameterDocs.getLast();
        Assertions.assertEquals("c", cDoc.varName);
        Assertions.assertEquals("string", cDoc.type);
        Assertions.assertFalse(cDoc.optional);
        Assertions.assertEquals(
                "The char to replace with. If this isn't a string" +
                        " then just return the input",
                cDoc.desc);
        // returns doc
        String returnsDoc = doc.getReturns();
        Assertions.assertEquals(
                "A new string with the odd characters replaced with c (0-indexed)",
                returnsDoc
        );
    }

    @Test
    void replaceOddChar_Body() {
        TFunction replaceOddChar =  Jaiva.assertAndCastToken(tokens.get(2), TFunction.class);
        TCodeblock body = replaceOddChar.getCodeBlocks().getFirst();
        ArrayList<Token<?>> funcBody =  body.lines;

        // 5 tokens
        Assertions.assertEquals(5, funcBody.size());

        // maak originalAsCharArray <- arrLit(original:::)!
        {
            TUnknownScalar<?, ?> originalAsCharArray = Jaiva.assertAndCastToken(funcBody.getFirst(), TUnknownScalar.class);
            Jaiva.assertLineNumber(originalAsCharArray, 35);
            Assertions.assertEquals("originalAsCharArray", originalAsCharArray.name);
            Assertions.assertInstanceOf(Token.class, originalAsCharArray.value);
            TFuncCall arrLitFuncCall = Jaiva.assertAndCastToken((Token<?>) originalAsCharArray.value, TFuncCall.class);
            Jaiva.assertLineNumber(arrLitFuncCall, 35);
            Assertions.assertEquals("arrLit", arrLitFuncCall.functionName);
            Assertions.assertEquals(1, arrLitFuncCall.args.size());
            Jaiva.assertReferenceProperties(
                    arrLitFuncCall,
                    false,   // No length
                    false           // No spread
            );
            TVarRef original = Jaiva.assertAndCastToken(arrLitFuncCall.args.getFirst(), TVarRef.class);
            Jaiva.assertLineNumber(original, 35);
            Assertions.assertEquals("original", original.varName);
            Jaiva.assertReferenceProperties(
                    original,
                    false,      // No length
                    true               // Spread.
            );

        }

        /*
        if (typeOf(c) != "string") ->
            khutla original!
        <~
         */
        {
            TIfStatement ifStatement = Jaiva.assertAndCastToken(funcBody.get(1), TIfStatement.class);
            Jaiva.assertLineNumber(ifStatement, 36);

            // the expression
            TExpression expr = Jaiva.assertAndCastToken(
                    ifStatement.getConditionToken(),
                    TExpression.class
            );
            Jaiva.assertLineNumber(expr, 36);
            Assertions.assertEquals("!=", expr.op);
            // left hand side 'typeOf(c)'
            TFuncCall typeOfFuncCall = Jaiva.assertAndCastToken(expr.lHandSide, TFuncCall.class);
            Jaiva.assertLineNumber(typeOfFuncCall, 36);
            Assertions.assertEquals("typeOf",  typeOfFuncCall.functionName);
            Assertions.assertEquals(1, typeOfFuncCall.args.size());
            Jaiva.assertReferenceProperties(
                    typeOfFuncCall,
                    false,          // No length
                    false                   // No Spread
            );
            TVarRef cRef = Jaiva.assertAndCastToken(typeOfFuncCall.args.getFirst(), TVarRef.class);
            Jaiva.assertLineNumber(cRef, 36);
            Assertions.assertEquals("c", cRef.varName);
            Jaiva.assertReferenceProperties(
                    cRef,
                    false,      // No length
                    false               // No spread
            );
            // right hand side '"string"'
            Assertions.assertEquals("string", expr.rHandSide);

            // body
            ArrayList<TCodeblock> ifBodies =
                    Jaiva.assertConstruct(ifStatement, 1);
            TCodeblock ifBody = ifBodies.getFirst();
            Jaiva.assertCodeblockLineNumber(
                    ifBody, 36, 38
            );
            Assertions.assertEquals(1, ifBody.lines.size());
            TFuncReturn funcReturn = Jaiva.assertAndCastToken(ifBody.lines.getFirst(), TFuncReturn.class);
            Jaiva.assertLineNumber(funcReturn, 37);
            TVarRef originalVarRef = Jaiva.assertAndCastToken(funcReturn.value, TVarRef.class);
            Jaiva.assertLineNumber(originalVarRef, 37);
            Assertions.assertEquals("original", originalVarRef.varName);
            Jaiva.assertReferenceProperties(
                    originalVarRef,
                    false,      // No length
                    false       // No Spread
            );
        }

        // maak out <- ""
        {
            TStringVar outVar = Jaiva.assertAndCastToken(
                    funcBody.get(2), TStringVar.class
            );
            Jaiva.assertLineNumber(outVar, 39);
            Assertions.assertEquals("out", outVar.name);
            Assertions.assertEquals("",  outVar.value);
        }

        /*
        colonize (i <- 0 <| i < originalAsCharArray~ <| +) ->
            maak let <- originalAsCharArray[i]!
            out <- out + (i % 2 != 0 => c however let)!
        <~
         */
        // Oh God.
        {
            TForLoop colonizeLoop = Jaiva.assertAndCastToken(funcBody.get(3), TForLoop.class);
            Jaiva.assertLineNumber(colonizeLoop, 40);
            Jaiva.assertLoopType(
                    colonizeLoop, Jaiva.LoopType.CSTYLE
            );
            // i <- 0
            TNumberVar iVar = Jaiva.assertAndCastToken(colonizeLoop.variable,  TNumberVar.class);
            Jaiva.assertLineNumber(iVar, 40);
            Assertions.assertEquals("i", iVar.name);
            Assertions.assertEquals(0,  iVar.value);
            // i < originalAsCharArray~
            TExpression expr = Jaiva.assertAndCastToken(colonizeLoop.condition, TExpression.class);
            Jaiva.assertLineNumber(expr, 40);
            Assertions.assertEquals("<", expr.op);

            // left hand side 'i'
            TVarRef iRef = Jaiva.assertAndCastToken(expr.lHandSide, TVarRef.class);
            Jaiva.assertLineNumber(iRef, 40);
            Assertions.assertEquals("i", iRef.varName);
            Jaiva.assertReferenceProperties(
                    iRef, false, false
            );
            // right hand side 'originalAsCharArray~'
            TVarRef originalAsCharArrayRef1 =
                    Jaiva.assertAndCastToken(expr.rHandSide, TVarRef.class);
            Jaiva.assertLineNumber(originalAsCharArrayRef1, 40);
            Assertions.assertEquals("originalAsCharArray", originalAsCharArrayRef1.varName);
            Jaiva.assertReferenceProperties(
                    originalAsCharArrayRef1,
                    true,
                    false
            );

            // increment, in this case just "+"
            Assertions.assertEquals("+", colonizeLoop.increment);
            ArrayList<TCodeblock> list =
                    Jaiva.assertConstruct(colonizeLoop, 1);
            TCodeblock colLoopBody =
                    list.getFirst();
            Jaiva.assertCodeblockLineNumber(
                    colLoopBody, 40, 43
            );
            // 2 tokens
            Assertions.assertEquals(2,  colLoopBody.lines.size());
            ArrayList<Token<?>> colonizeLoopBody =
                    colLoopBody.lines;

            // maak let <- originalAsCharArray[i]
            TUnknownScalar<?, ?> letVar
                    = Jaiva.assertAndCastToken(colonizeLoopBody.getFirst(), TUnknownScalar.class);
            Jaiva.assertLineNumber(letVar, 41);
            Assertions.assertEquals("let", letVar.name);
            // originalAsCharArray[i]
            TVarRef originalAsCharArrayRef2 =
                    Jaiva.assertAndCastToken(letVar.value, TVarRef.class);
            Jaiva.assertLineNumber(originalAsCharArrayRef2, 41);
            Assertions.assertEquals("originalAsCharArray", originalAsCharArrayRef2.varName);
            Jaiva.assertReferenceProperties(
                    originalAsCharArrayRef2, false, false
            );
            Assertions.assertNotNull(originalAsCharArrayRef2.index);
            // i ref in index
            TVarRef iRefAsIndex = Jaiva.assertAndCastToken(
                    originalAsCharArrayRef2.index,
                    TVarRef.class
            );
            Jaiva.assertLineNumber(iRefAsIndex, 41);
            Assertions.assertEquals("i", iRefAsIndex.varName);
            Jaiva.assertReferenceProperties(
                    iRefAsIndex, false, false
            );

            // out <- out + (i % 2 != 0 => c however let)
            TVarReassign outReassignment
                    = Jaiva.assertAndCastToken(colonizeLoopBody.getLast(), TVarReassign.class);
            Jaiva.assertLineNumber(outReassignment, 42);
            Assertions.assertEquals("out", outReassignment.name);

            TExpression outExpr
                    = Jaiva.assertAndCastToken(outReassignment.newValue, TExpression.class);
            Assertions.assertEquals("+", outExpr.op);
            Jaiva.assertLineNumber(outExpr, 42);
            // LHS = 'out'
            TVarRef outLhs =
                    Jaiva.assertAndCastToken(outExpr.lHandSide, TVarRef.class);
            Jaiva.assertLineNumber(outLhs, 42);
            Assertions.assertEquals("out", outLhs.varName);
            Jaiva.assertReferenceProperties(outLhs, false, false);
            // RHS = '(i % 2 != 0 => c however let)'
            TTernary ternaryRhs
                    = Jaiva.assertAndCastToken(outExpr.rHandSide, TTernary.class);
            Jaiva.assertLineNumber(ternaryRhs, 42);
            // (i % 2) != (0)
            TExpression ternaryExpr
                    =  Jaiva.assertAndCastToken(ternaryRhs.condition, TExpression.class);
            Jaiva.assertLineNumber(ternaryExpr, 42);
            Assertions.assertEquals("!=", ternaryExpr.op);
            Assertions.assertEquals(0, ternaryExpr.rHandSide);
            // i % 2
            TExpression ternaryInnerExpr
                    = Jaiva.assertAndCastToken(ternaryExpr.lHandSide, TExpression.class);
            Jaiva.assertLineNumber(ternaryInnerExpr, 42);
            Assertions.assertEquals("%", ternaryInnerExpr.op);
            Assertions.assertEquals(2, ternaryInnerExpr.rHandSide);
            TVarRef iInner
                    = Jaiva.assertAndCastToken(ternaryInnerExpr.lHandSide, TVarRef.class);
            Jaiva.assertLineNumber(iInner, 42);
            Assertions.assertEquals("i", iInner.varName);
            Jaiva.assertReferenceProperties(iInner, false, false);

            // ternary true and false returns
            TVarRef ternTrueExpr
                    = Jaiva.assertAndCastToken(ternaryRhs.trueExpr, TVarRef.class);
            Jaiva.assertLineNumber(ternTrueExpr, 42);
            Assertions.assertEquals("c", ternTrueExpr.varName);
            Jaiva.assertReferenceProperties(ternTrueExpr, false, false);
            TVarRef ternFalseExpr
                    =  Jaiva.assertAndCastToken(ternaryRhs.falseExpr, TVarRef.class);
            Jaiva.assertLineNumber(ternFalseExpr, 42);
            Assertions.assertEquals("let", ternFalseExpr.varName);
            Jaiva.assertReferenceProperties(ternFalseExpr, false, false);
        }

        // khutla out
        {
            TFuncReturn funcReturn
                    = Jaiva.assertAndCastToken(funcBody.get(4), TFuncReturn.class);
            Jaiva.assertLineNumber(funcReturn, 44);
            TVarRef funcReturnValue
                    = Jaiva.assertAndCastToken(funcReturn.value,  TVarRef.class);
            Jaiva.assertLineNumber(funcReturnValue, 44);
            Assertions.assertEquals("out", funcReturnValue.varName);
            Jaiva.assertReferenceProperties(funcReturnValue, false, false);
        }

    }

    // TODO: the rest of the tests.
}
