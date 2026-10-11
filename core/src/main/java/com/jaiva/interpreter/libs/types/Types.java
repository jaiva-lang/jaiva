package com.jaiva.interpreter.libs.types;

import com.jaiva.errors.InterpreterException.FunctionParametersException;
import com.jaiva.errors.InterpreterException.WtfAreYouDoingException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.lang.Keywords;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.specific.TVoidValue;
import com.yetnt.utils.functional.function.ThrowableTriFunction;

import java.util.ArrayList;

@Exports(Numbers.class)
@JaivaLibrary(path = "types", description = "Converting between types and stuff")
public class Types extends BaseLibrary {
    public Types() {
        // the import will be "jaiva/types.jiv"
        add(new FNum());
        add(new FStr());
    }

    /**
     * 
     * t_num(string, radix?) -> number
     *
     */
    static class FNum extends BaseFunction {
        FNum() {
            super(
                    FunctionBuilder.start()
                            .name("t_num")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(
                                                    new AArgument(
                                                            "string", "The input to convert to a number",
                                                            false, Argument.Type.STRING
                                                    )
                                            )
                                            .add(
                                                    new AArgument(
                                                            "radix", "An optional radix to convert to",
                                                            true, Argument.Type.NUMBER
                                                    )
                                            )
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Converts a given **string** to a number with an optional **radix**.")
                                            .addNote(
                                                    "Jaiva integer prefixes [such as _0x_ or _0b_], are checked for first before the radix."
                                                    + " If the resulting input is too big, a long may be returned instead of an integer"
                                            )
                                            .addReturns("A number. (Either a double, integer or long)")
                                            .addExample("""
                                            khuluma(t_num("0b1010"))! @ prints 10
                                            khuluma(t_num("FF", 16))! @ prints 255
                                            khuluma(t_num("3.14"))! @ prints 3.14
                                            khuluma(t_num("0x1A"))! @ prints 26
                                            khuluma(t_num("30129382409L"))! @ prints 30129382409 (as a long value)
                                            """)
                                            .sinceVersion("2.0.0-beta.3")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            this.checkParams(callProperties.tFuncCall(), callProperties.scope());
            Object val = callProperties.params().getFirst();

            if (!(val instanceof String value))
                throw new WtfAreYouDoingException(callProperties.scope(), callProperties.params().getFirst() + " cannot become a number kau",
                        callProperties.tFuncCall().lineNumber);

            int type = value.startsWith("0b") ? 2 : value.startsWith("0x") ? 16 : value.startsWith("0c") ? 8 : -1;
            value = type != -1 ? value.substring(2) : value;

            // only this cuz it can actually throw, and im not making a ThrowableBiFunction since
            // it'd make sense since bi is effectively final.
            ThrowableTriFunction<
                    Integer, ArrayList<Object>, String,
                    Long, Exception
                    > attemptLong = (t, p, v) -> {
                if (p.size() == 1)
                    return Long.parseLong(v, t != -1 ? t : 10);

                Object r = p.get(1);
                if (!(r instanceof TVoidValue) && !(r instanceof Integer))
                    throw new FunctionParametersException(callProperties.scope(), this, "2", r, Integer.class, callProperties.tFuncCall().lineNumber);
                int radix = r instanceof TVoidValue ? -1 : (int) r;

                return Long.parseLong(v, t != -1 ? t : radix != -1 ? radix : 10);
            };
            boolean attemptedLong = false;

            try {
                if (value.contains("."))
                    return Double.parseDouble(value);
                else if (value.endsWith("l") ||  value.endsWith("L")) {
                    attemptedLong = true;
                    value =  value.substring(0, value.length() - 1);
                    return attemptLong.apply(type, callProperties.params(), value);
                }
                else {
                    if (callProperties.params().size() == 1)
                        return Integer.parseInt(value, type != -1 ? type : 10);

                    Object r = callProperties.params().get(1);
                    if (!(r instanceof TVoidValue) && !(r instanceof Integer))
                        throw new FunctionParametersException(callProperties.scope(), this, "2", r, Integer.class, callProperties.tFuncCall().lineNumber);
                    int radix = r instanceof TVoidValue ? -1 : (int) r;

                    return Integer.parseInt(value, type != -1 ? type : radix != -1 ? radix : 10);
                }
            } catch (NumberFormatException e) {
                // i lowkey dont care if this can be simplified. double try catch
                try {
                    if (!attemptedLong) {
                        return attemptLong.apply(type, callProperties.params(), value);
                    } else {
                        throw new WtfAreYouDoingException(callProperties.scope(), callProperties.params().getFirst() + " cannot become a number kau",
                                callProperties.tFuncCall().lineNumber);
                    }
                } catch (NumberFormatException e2){
                    throw new WtfAreYouDoingException(callProperties.scope(), callProperties.params().getFirst() + " cannot become a number kau",
                            callProperties.tFuncCall().lineNumber);
                }
            }
        }
    }

    /**
     * t_str(input?, radix?) -> string
     * <p>
     * Input is marked as optional to allow passing of {@link TVoidValue} into it.
     */
    static class FStr extends BaseFunction {
        FStr() {
            super(
                    FunctionBuilder.start()
                            .name("t_str")
                            .arguments(
                                    Arguments.getInstance()
                                            .add(
                                                    new AArgument(
                                                            "input", "The input to convert",
                                                            true, Argument.Type.ANY
                                                    )
                                            )
                                            .add(
                                                    new AArgument(
                                                            "radix", "A given radix for integers or longs",
                                                            true, Argument.Type.NUMBER
                                                    )
                                            )
                            )
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Converts any input of any given type to a string.")
                                            .addReturns("A string representation of the given input")
                                            .addNote("If given an input of `idk`, it will return `idk`. Not a string.")
                                            .addExample("""
                                            khuluma(t_str(255))! @ prints "255"
                                            khuluma(t_str(true))! @ prints "yebo"
                                            khuluma(t_str(yebo))! @ prints "yebo"
                                            khuluma(t_str(10, 2))! @ prints "0b1010"
                                            """)
                                            .sinceVersion("2.0.0-beta.3")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties)
                throws Exception {
            this.checkParams(callProperties.tFuncCall(), callProperties.scope());
            Object val = callProperties.params().getFirst();

            return switch (val) {
                case Double aDouble -> val.toString();
                case BaseFunction baseFunction ->
                    val.toString();
                case Boolean b ->
                    // makes it such that, true and false even though they are valid keywords, we
                    // return Jaiva's true and false all the time.
                    b ? Keywords.TRUE : Keywords.FALSE;
                case Long l -> {
                    if (callProperties.params().size() == 1)
                        yield val.toString();
                    Object r = callProperties.params().get(1);
                    if (!(r instanceof TVoidValue) && !(r instanceof Integer))
                        throw new FunctionParametersException(callProperties.scope(), this, "2", r, Integer.class, callProperties.tFuncCall().lineNumber);
                    int radix = r instanceof TVoidValue ? -1 : (int) r;
                    yield switch (radix) {
                        case 2 -> "0b" + Long.toBinaryString(l);
                        case 16 -> "0x" + Long.toHexString(l).toUpperCase();
                        case 8 -> "0c" + Long.toOctalString(l);
                        default -> Long.toString(l, radix);
                    };
                }
                case Integer integer -> {
                    if (callProperties.params().size() == 1)
                        yield val.toString();
                    Object r = callProperties.params().get(1);
                    if (!(r instanceof TVoidValue) && !(r instanceof Integer))
                        throw new FunctionParametersException(callProperties.scope(), this, "2", r, Integer.class, callProperties.tFuncCall().lineNumber);
                    int radix = r instanceof TVoidValue ? -1 : (int) r;
                    yield switch (radix) {
                        case 2 -> "0b" + Integer.toBinaryString(integer);
                        case 16 -> "0x" + Integer.toHexString(integer).toUpperCase();
                        case 8 -> "0c" + Integer.toOctalString(integer);
                        default -> Integer.toString(integer, radix);
                    };
                }
                case TVoidValue v -> v.toString();
                case String s -> s;
                case null, default -> throw new WtfAreYouDoingException(callProperties.scope(), val + " cannot become a string kau",
                        callProperties.tFuncCall().lineNumber);
            };
        }
    }


}
