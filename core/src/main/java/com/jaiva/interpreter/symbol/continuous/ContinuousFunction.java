package com.jaiva.interpreter.symbol.continuous;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Primitives;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libs.cont.ContinuousConst;
import com.jaiva.interpreter.libs.file.bytes.FileBytes;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.interpreter.symbol.inf.BFMethodToConsumer;
import com.jaiva.interpreter.symbol.inf.BFMethodToSupplier;
import com.jaiva.tokenizer.tokens.Token;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A ContinuousFunction is a specialisation of your ordinary {@link BaseFunction} with the sole purpose
 * of being a closure over resources. Either an {@link InputStream} or an {@link OutputStream} implementation.
 * <p>
 *     What it does is close over said {@link Closeable}, but doesn't itself do any IO. it instead acts as a dispatcher
 *     for smaller {@link CFClosures} (Continuous Function Closures) which have specific tasks. The user themselves uses the
 *     {@link ContinuousFunction} via passing a {@link CFuncInput} type to return the {@link CFClosures} they want to use.
 * </p>
 * <p>
 *     A continuous function either holds a subtype of a {@link InputStream} or a subtype of a {@link OutputStream} at once and
 *     hence only has the capabilities for that particular stream. (e.g, only output stream hel continuous functions are able to
 *     make {@link CFClosures.Close} have a semantic meaning.)
 * </p>
 * <p>
 *     The following {@link CFuncInput} maps to the corresponding closure
 *     <table>
 *         <tr>
 *             <td>Continuous Function Input</td> <td>{@link CFClosures} instance</td> <td> Description </td>
 *         </tr>
 *         <tr>
 *             <td>{@link CFuncInput#READ}</td> <td>{@link CFClosures.Input}</td>
 *             <td> Specifically wraps an input stream. Mutually exclusive with {@link CFuncInput#WRITE} </td>
 *         </tr>
 *         <tr>
 *             <td>{@link CFuncInput#WRITE}</td> <td>{@link CFClosures.Output}</td>
 *             <td> Specifically wraps an output stream. Mutually excluse with {@link CFuncInput#READ} </td>
 *         </tr>
 *         <tr>
 *             <td>{@link CFuncInput#FLUSH}</td> <td>{@link CFClosures.Flush}</td>
 *             <td> Provies any {@link ContinuousFunction} of an {@link OutputStream} to call {@link OutputStream#flush()} </td>
 *         </tr>
 *         <tr>
 *             <td>{@link CFuncInput#EOF}</td> <td>{@link CFClosures.EOF}</td>
 *             <td> Provies any {@link ContinuousFunction} of an {@link InputStream} to  signal the end of a file. Although
 *             the implementation si responsible for what exactly that means as not all input streams have some EOF marker.
 *             </td>
 *         </tr>
 *         <tr>
 *             <td>{@link CFuncInput#CLOSE}</td> <td>{@link CFClosures.Close}</td>
 *             <td> Provies any {@link ContinuousFunction} of any {@link Closeable} to be able to release it's resources by
 *             calling {@link Closeable#close()}
 *             </td>
 *         </tr>
 *     </table>
 * </p>
 * <p>
 *     Example Jaiva Usage with the {@link FileBytes} library.
 *     <pre>{@code
 *      tsea "jaiva/file/bytes" <- f_creader, R_BYTE!
 *      tsea "jaiva/continuous" <- C_READ, C_EOF, C_CLOSE!
 *
 *      maak conf <- f_creader("(RANDOM FILE)")! @ Returns a continious function
 *      maak read <- conf(C_READ)! @ Get the reader closure
 *      maak eof <- conf(C_EOF)! @ Get the EOF closure
 *
 *      maak collect <-|!
 *      maak lastVal!
 *      nikhil (eof()') -> @ If not EOF
 *          lastVal <- read(R_BYTE)! @ Read a single byte
 *          if (lastVal != idk) -> @ defensive.
 *              collect <- arrLit(collect:::, lastVal)!
 *          <~
 *      <~
 *
 *      conf(C_CLOSE)()! @ Close and release resources.
 *
 *      khuluma("The following bytes were found:")!
 *      khuluma(collect)!
 *     }</pre>
 * </p>
 * @see CFClosures
 * @see CFuncInput
 * @see BaseFunction
 * @see com.jaiva.interpreter.runtime.Resources
 * @see com.jaiva.interpreter.runtime.GlobalResources
 * @see InputStream
 * @see OutputStream
 * @see Closeable
 * @see ContinuousConst
 * @author Lehlogonolo Poole
 * @param <T> The type of the closeable.
 */
public class ContinuousFunction<T extends Closeable> extends BaseFunction {

    public enum ContinuousFunctionType {INPUT, OUTPUT}
    T closeable;
    ContinuousFunctionType continuousFunctionType;

    CFClosures.Input inputStreamClosure;
    CFClosures.Output outputStreamClosure;
    CFClosures.Close closeClosure;
    CFClosures.Flush flushClosure;
    CFClosures.EOF eofClosure;


    private ContinuousFunction(
            IConfig<Object> config,
            T closeable,
            ContinuousFunctionType continuousFunctionType,
            BFMethodToSupplier<?> readStreamSupplier,
            BFMethodToConsumer<Object> writeStreamConsumer,
            CFClosures.EOF eofClosure
    ) {
        super(
                // docs wont matter here, all the stuff here is purely to create
                // the TFunction token.
                FunctionBuilder.start()
                        .name("continuousFunction")
                        .arguments(Arguments.getInstance().add(
                                new AArgument(
                                        "string",
                                        "The input string ",
                                        false,
                                        Argument.Type.STRING
                                )
                        ))
        );
        this.closeable = closeable;
        this.continuousFunctionType = continuousFunctionType;

        inputStreamClosure = new CFClosures.Input(readStreamSupplier);
        outputStreamClosure = new CFClosures.Output(writeStreamConsumer);
        closeClosure = new CFClosures.Close(
                config.getGlobalResources().ofCurrentThread().addResource(closeable)
        );
        flushClosure = new CFClosures.Flush(closeable);
        this.eofClosure = eofClosure;
    }

    public static <T extends InputStream> ContinuousFunction<T> from(
            IConfig<Object> config, T inputStream,
            BFMethodToSupplier<?> inputStreamConsumer,
            CFClosures.EOF eofClosure
    ) {
        return new ContinuousFunction<T>(config, inputStream, ContinuousFunctionType.INPUT, inputStreamConsumer, null, eofClosure);
    }

    public static <T extends OutputStream> ContinuousFunction<T> from(
            IConfig<Object> config, T outputStream,
            BFMethodToConsumer<Object> outputStreamConsumer
    ) {
        return new ContinuousFunction<T>(config, outputStream, ContinuousFunctionType.OUTPUT, null, outputStreamConsumer, new CFClosures.EOF());
    }

    @Override
    public Object call(CallProperties callProperties) throws Exception {
        // do all the handling logic dispatch and new function creation shenangians
        checkParams(callProperties.tFuncCall(), callProperties.scope());
        if (!callProperties.params().isEmpty()) {
            Object prot = Primitives.toPrimitive(callProperties.params().getFirst(), false, callProperties.config(), callProperties.scope());
            if (!(prot instanceof String protocol))
                throw new InterpreterException.FunctionParametersException(
                        callProperties.scope(), this, "1", prot, String.class, callProperties.tFuncCall().lineNumber
                );

            protocol = protocol.toLowerCase();
            if (!CFuncInput.isValid(protocol))
                throw new InterpreterException.WtfAreYouDoingException(
                        callProperties.scope(), "A continuous function can only take string inputs of: " +
                        " \"read\", \"write\", \"flush\" or \"close\"!", callProperties.tFuncCall().lineNumber
                );

            // isValid allows this to pass.
            CFuncInput input = CFuncInput.fromChar(protocol.charAt(0));

            assert input != null; // it won't be null but java.
            return switch (input) {
                case READ -> inputStreamClosure;
                case WRITE -> outputStreamClosure;
                case CLOSE -> closeClosure;
                case FLUSH -> flushClosure;
                case EOF -> eofClosure;
            };
        }
        return Token.voidValue(callProperties.tFuncCall().lineNumber);
    }

}
