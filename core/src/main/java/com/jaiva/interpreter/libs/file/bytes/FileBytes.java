package com.jaiva.interpreter.libs.file.bytes;

import com.jaiva.errors.InterpreterException;
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
import com.jaiva.interpreter.symbol.continuous.CFClosures;
import com.jaiva.interpreter.symbol.continuous.ContinuousFunction;
import com.jaiva.interpreter.libs.types.TypeConverter;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.builders.MarkDownLiteral;
import com.yetnt.utils.functional.consumer.ThrowableConsumer;
import com.yetnt.utils.functional.generic.ThrowableSupplier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Exports(ByteConstants.class)
@JaivaLibrary(
        path = "file/bytes",
        description = "Allows byte level reading and writing of files onto disk. This makes use of " +
                "continuous functions to do so."
)
public class FileBytes extends BaseLibrary {
    public FileBytes() {
        add(new FByteReader(), new FByteWriter());
    }

    static class FByteWriter extends BaseFunction {
        public FByteWriter() {
            super(
                    FunctionBuilder.start()
                            .name("f_cwriter")
                            .arguments(Arguments.getInstance()
                                    .add(
                                            new AArgument(
                                                    "filePath",
                                                    "The (Absolute) file path to write to.",
                                                    false,
                                                    Argument.Type.STRING
                                            )
                                    ).add(
                                            new AArgument(
                                                    "append",
                                                    "Append to the file if it already exists, otherwise overwrite. Defaults to true, which appends.",
                                                    true,
                                                    Argument.Type.BOOLEAN
                                            )
                                    ))
                            .docs(
                                    JDoc.builder().sinceVersion("6.1.0")
                                            .addDesc("Provides byte level writes into a file.")
                                            .addReturns("Returns a " +
                                                    new MarkDownLiteral("Continuous Function")
                                                            .linkTo("../Continuous-Functions.md")
                                             + " over the file writing resource.")
                                            .addExample("""
                                                    tsea "jaiva/file/bytes"!
                                                    tsea "jaiva/continuous!"
                                                    
                                                    maak cont <- f_cwriter("C:\\Users\\Wow\\myFile.jib")!
                                                    maak write <- cont(C_OUTPUT)!
                                                    
                                                    write(W_UTF8, "What's Up Dawg??")!
                                                    write(W_BYTE, 255)! @ Write 255 marker so we know where the UTF8 bytes end.
                                                    
                                                    cont(C_FLUSH)()!
                                                    cont(C_CLOSE)()!
                                                    """)
                            )
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.tFuncCall(), callProperties.scope());

            Object p = callProperties.params().getFirst();
            if (!(p instanceof String path))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "1", p, String.class, callProperties.tFuncCall().lineNumber
                );

            boolean append = true;
            if (callProperties.params().size() > 1
                    && callProperties.params().get(1) instanceof Boolean b)
                append = b;

            File f = new File(path);

//            if (f.exists())
//                throw new InterpreterException.WtfAreYouDoingException(
//                        scope,  "File no existy", tFuncCall.lineNumber
//                );

            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f,  append)));

            return ContinuousFunction.from(
                    callProperties.config(), dos,
                    (callProps) -> {
                        ArrayList<Object> pars = callProps.params();
                        // the writer from continous function takes the first argument as what to write.
                        ThrowableConsumer<Object, IOException> doNothing
                                = (o) -> {
                            return;
                        };

                        if (pars.size() < 2) return doNothing;

                        if (!(pars.removeFirst() instanceof String type)) return doNothing;

                        if (type.startsWith("w_"))
                            type = type.substring(2);

                        return switch (type) {
                            case "int", "integer" -> (i) -> {
                                if (!(i instanceof Integer i2)) return;
                                dos.writeInt(i2);
                            };
                            case "boolean", "bool" -> (i) -> {
                                if  (!(i instanceof Boolean b)) return;
                                dos.writeBoolean(b);
                            };
                            case "long" -> (i) -> {
                                if (!(i instanceof Long l)) return ;
                                dos.writeLong(l);
                            };
                            case "double" -> (i) -> {
                                if (!(i instanceof Double d)) return ;
                                dos.writeDouble(d);
                            };
                            case "string", "utf8", "utf_8" -> (i) -> {
                                if (!(i instanceof String str)) return ;
                                dos.write(str.getBytes(StandardCharsets.UTF_8));
                            };
                            case "mod_utf8", "java_utf8" -> (i) -> {
                                if (!(i instanceof String str)) return ;
                                dos.writeUTF(str);
                            };
                            case "byte" -> (i) -> {
                                if (!(i instanceof Integer i2)) return;
                                byte val = TypeConverter.toByte(i2);
                                dos.writeByte(val);
                            };
                            default -> doNothing;
                        };
                    }
            );
        }
    }


    static class FByteReader extends BaseFunction {
        public FByteReader() {
            super(
                    FunctionBuilder.start()
                            .name("f_creader")
                            .arguments(Arguments.getInstance()
                                    .add(
                                            new AArgument(
                                                    "filePath",
                                                    "Absolute path",
                                                    false,
                                                    Argument.Type.STRING
                                            )
                                    ))
                            .docs(
                                    JDoc.builder().sinceVersion("6.1.0")
                                            .addDesc("Provides byte level reads into a file.")
                                            .addReturns("Returns a " +
                                                    new MarkDownLiteral("Continuous Function")
                                                            .linkTo("../Continuous-Functions.md")
                                                    + " over the file reading resource.")
                                            .addExample("""
                                                    tsea "jaiva/file/bytes"!
                                                    tsea "jaiva/continuous!"
                                                    tsea "jaiva/types/numbers"!
                                                    
                                                    @ If the file contains a UTF8 string then a 255 byte value
                                                    
                                                    maak conf <- f_creader("C:\\Users\\Wow\\myFile.jib")!
                                                    maak read <- conf(C_READ)!
                                                    maak collected <- aowa!
                                                
                                                    maak collect <-|!
                                                
                                                    nikhil (conf(C_EOF)()') ->
                                                        maak byte <- read(R_BYTE)!
                                                
                                                        if (byte?) ->
                                                            nevermind!
                                                        <~
                                                
                                                        if (byte != 255) ->
                                                            collect <- arrLit(collect:::, byte)!
                                                        <~
                                                    <~
                                                
                                                    conf(C_CLOSE)()!
                                                
                                                    khuluma(t_strFromByteArr(collect:::))!
                                                    """)
                            )
            );
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.tFuncCall(), callProperties.scope());

            Object p = callProperties.params().getFirst();
            if (!(p instanceof String path))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "1", p, String.class, callProperties.tFuncCall().lineNumber
                );

            File f = new File(path);

            if (!f.exists())
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(),  "File no existy", callProperties.tFuncCall().lineNumber
                );

            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

            CFClosures.EOF eof = new CFClosures.EOF();

            ThrowableSupplier<Object, IOException> unknown
                    = () -> Token.voidValue(callProperties.tFuncCall().lineNumber);

            // The code is only as such due to the fact that we need to handle the EOFException
            // in this call site and nowhere else. Other implementers might genuinely be better
            // expressed as method references, only the byte reader looses out on calling the method
            // references directly.

            return ContinuousFunction.from(
                    callProperties.config(),
                    dis,
                    (callProps) -> {
                        ArrayList<Object> pr = callProps.params();
                        try {

                            if (pr.isEmpty()) return unknown;
                            if (pr.size() == 1 && pr.getFirst() == null) return unknown;
                            if (!(pr.getFirst() instanceof String type)) return unknown;

                            if (type.startsWith("r_"))
                                type = type.substring(2);

                            return switch (type) {
                                case "int", "integer" -> {
                                    int i = dis.readInt();
                                    yield () -> i;
                                }
                                case "boolean" , "bool" -> {
                                    boolean b = dis.readBoolean();
                                    yield () -> b;
                                }
                                case "long" -> {
                                    long l = dis.readLong();
                                    yield () -> l;
                                }
                                case "double" -> {
                                    double d = dis.readDouble();
                                    yield () -> d;
                                }
                                case "mod_utf8", "java_utf8" -> {
                                    String s = dis.readUTF();
                                    yield () -> s;
                                }
                                case "byte" -> {
                                    byte b = dis.readByte();
                                    yield () -> b;
                                }
                                case "bytes" -> {
                                    if (pr.size() > 1 && (pr.get(1) instanceof Integer i1)) {
                                        byte[] bs = dis.readNBytes(i1);
                                        if (i1 > bs.length) eof.set();
                                        yield () -> bs;
                                    } else {
                                        yield unknown;
                                    }
                                }
                                default -> unknown;
                            };

                        } catch (EOFException e) {
                            eof.set();
                            return unknown;
                        }
                    },
                    eof
            );
        }
    }
}
