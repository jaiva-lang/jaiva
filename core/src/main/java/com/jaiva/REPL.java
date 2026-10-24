package com.jaiva;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.Interpreter;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.Vfs;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.TConfig;
import com.jaiva.tokenizer.Tokenizer;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;
import com.jaiva.tokenizer.tokens.specific.TExpression;
import com.jaiva.tokenizer.tokens.specific.TFuncCall;
import com.jaiva.tokenizer.tokens.specific.TVarRef;
import com.jaiva.utils.generic.BlockChain;
import com.jaiva.utils.generic.MultipleLinesOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * REPLMode is an enumeration that represents the mode of the REPL
 * (Read-Eval-Print Loop);
 */
enum REPLMode {
    /**
     * Standard mode of the REPL, where it evaluates and prints the result of the
     * input.
     */
    STANDARD(0),
    /**
     * Mode of the REPL that prints the token representation of the input without
     * evaluating it.
     */
    PRINT_TOKEN(1);

    /**
     * The integer value representing the mode.
     */
    private final int mode;

    /**
     * Constructor for REPLMode.
     * 
     * @param mode The integer value representing the mode.
     */
    REPLMode(int mode) {
        this.mode = mode;
    }

    /**
     * Returns the integer value representing the mode.
     * 
     * @return The integer value representing the mode.
     */
    public int getMode() {
        return mode;
    }

    /**
     * Converts an integer to the corresponding REPLMode enumeration.
     * 
     * @param mode The integer value representing the mode.
     * @return The corresponding REPLMode enumeration.
     */
    public REPLMode toEnum(int mode) {
        for (REPLMode m : REPLMode.values()) {
            if (m.getMode() == mode) {
                return m;
            }
        }
        return STANDARD;
    }
}

/**
 * State is an enumeration that represents the state of the REPL.
 */
enum State {
    /**
     * The REPL is in a state of active execution.
     */
    ACTIVE,
    /**
     * The REPL is in a state of inactive execution.
     */
    INACTIVE,
    /**
     * The REPL is in a state of error execution.
     */
    ERROR,
    /**
     * The REPL is or will soon exit.
     */
    EXIT,
}

/**
 * ReadOuput is a class that represents the output of the REPL.
 */
class ReadOuput {
    /**
     * The output of the REPL.
     * It can be null if the REPL is in a clean exit state.
     */
    private String output = null;

    /**
     * Parameteried Constructor for ReadOuput.
     * 
     * @param output The output of the REPL.
     */
    ReadOuput(String output) {
        this.output = output;
    }

    /**
     * Default Constructor for ReadOuput.
     * It sets the output to null.
     */
    ReadOuput() {
    }

    /**
     * Returns true if the REPL is in a clean exit state.
     * 
     * @return true if the REPL is in a clean exit state, false otherwise.
     */
    public boolean isCleanExit() {
        return output == null;
    }

    /**
     * Returns the output of the REPL.
     * 
     * @return The output of the REPL.
     */
    public String getOutput() {
        return output;
    }
}

/**
 * REPL (Read-Eval-Print Loop) is a class that represents the REPL for the Jaiva
 * programming language.
 * It allows users to interactively enter and evaluate Jaiva code
 * TODO: It's a bit buggy, come back to this.
 * 
 */
public class REPL {
    /**
     * The mode of the REPL.
     * It can be either STANDARD or PRINT_TOKEN.
     */
    private REPLMode mode = REPLMode.STANDARD;
    /**
     * The state of the REPL.
     * It can be either ACTIVE, INACTIVE, ERROR, or EXIT.
     */
    private State state = State.INACTIVE;
    /**
     * The BufferedReader used to read input from the user.
     */
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * The scope object.
     */
    public final Scope scope;
    /**
     * Interpreter configuration object.
     * It contains the resources and configuration for the interpreter.
     */
    private final IConfig<Object> iConfig = new IConfig<Object>();
    /**
     * Tokenizer configuration object.
     * It contains the configuration for the tokenizer.
     */
    private final TConfig tConfig = new TConfig("REPL");
    // private Token<?> tokenContainer = new Token<>(null);

    /**
     * Constructor for REPL.
     * It initializes the REPL with the specified mode and starts the REPL loop.
     * 
     * @param mode The mode in which the REPL operates.
     *             It determines the behavior of the REPL and can be one of the
     *             values defined
     *             in the REPLMode enumeration.
     *             For example, STANDARD mode processes and evaluates input, while
     *             PRINT_TOKEN
     *             mode outputs tokens.
     * @throws InterpreterException when it encounters one.
     */
    public REPL(int mode) throws InterpreterException {
        this.iConfig.setREPL(true);
        this.scope = new Scope(iConfig);
        this.state = State.ACTIVE;
        this.mode = REPLMode.STANDARD.toEnum(mode);

        MultipleLinesOutput m = null;
        BlockChain b = null;
        String previousLine = "";

        System.out.println("type 'exit' to exit.");
        System.out.println();

        while (state == State.ACTIVE || state == State.ERROR) {
            try {
                state = State.ACTIVE;
                System.out.print(m != null || b != null ? ">... " : "Jaiva! > ");
                String line = b != null ? b.currentLine() : reader.readLine().trim();
                if (line == null || line.equals("exit")) {
                    close();
                    break;
                }
                Object something = read(line, (b != null ? "" : previousLine), m, b);
                switch (something) {
                    case ReadOuput ro -> {
                        m = null;
                        b = null;
                        if (this.state == State.ERROR) {
                            System.out.println("Chaai an error: " + ro.getOutput());
                        } else if (!ro.isCleanExit()) {
                            System.out.println(ro.getOutput());
                        }
                    }
                    case MultipleLinesOutput multipleLinesOutput -> {
                        m = multipleLinesOutput;
                        b = null;
                    }
                    case BlockChain blockChain -> {
                        m = null;
                        b = blockChain;
                    }
                    case null, default -> {
                        b = null;
                        m = null;
                    }
                }
                previousLine = line;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        close();
        System.out.println("hai bye mfana.");
    }

    /**
     * Closes the REPL resources, including the reader and any associated resources.
     * Ensures proper cleanup by releasing resources and handling potential I/O
     * exceptions
     */
    public void close() {
        try {
            reader.close();
            iConfig.releaseAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a line of input and processes it based on the REPL mode.
     * It evaluates the input, handles variables, and returns the output.
     * 
     * @param line         The line of input to read.
     * @param previousLine The previous line of input.
     * @param m            The MultipleLinesOutput object for handling multiple
     *                     lines.
     * @param b            The BlockChain object for handling block chains.
     * @return The output of the REPL after processing the input.
     */
    public Object read(String line, String previousLine, MultipleLinesOutput m, BlockChain b) {
        Object something;
        try {
            something = Tokenizer.readLine(line, previousLine, m, b, 0, tConfig);
            if (something instanceof MultipleLinesOutput) {
                return something;
            } else if (something instanceof BlockChain) {
                return something;
            } else if (something instanceof Token<?>) {
                if (mode == REPLMode.STANDARD) {
                    TokenDefault token = ((Token<?>) something).value();
                    if (Interpreter.isVariableToken(token)) {
                        Object value = Interpreter.handleVariables(token,
                                iConfig, scope);
                        // TODO: This assumes the global context, which might not be correct.
                        assert value != null;
                        return new ReadOuput(value.toString());
                    } else {
                        Object h = Interpreter.interpret(new ArrayList<>(List.of((Token<?>) something)),
                                scope, iConfig);
                        if (h instanceof HashMap)
                            scope.setVfs((Vfs) h);

                        return new ReadOuput();
                    }
                } else if (mode == REPLMode.PRINT_TOKEN) {
                    return new ReadOuput(((Token<?>) something).toString());
                }
            } else if (something instanceof ArrayList<?>) {
                ArrayList<Token<?>> tokens = (ArrayList<Token<?>>) something;
                if (mode == REPLMode.STANDARD) {
                    if (tokens.size() == 1) {
                        TokenDefault token = ((Token<?>) tokens.get(0)).value();
                        if (Interpreter.isVariableToken(token)) {
                            boolean isReturnTokenClass = token instanceof TExpression || token instanceof TVarRef
                                    || token instanceof TFuncCall;
                            Object input = isReturnTokenClass
                                    ? ((Token<?>) tokens.getFirst())
                                    : token;
                            // TODO: This assumes the global context, which might not be correct.
                            Object value = Interpreter.handleVariables(input, iConfig, scope);
                            if (isReturnTokenClass) {
                                assert value != null;
                                return new ReadOuput(value.toString());
                            } else {
                                return new ReadOuput();
                            }
                        } else {
                            Object h = Interpreter.interpret(new ArrayList<>(Collections.singletonList((Token<?>) tokens.getFirst(
                                    ))),
                                    scope, iConfig);
                            if (h instanceof HashMap)
                                scope.setVfs((Vfs) h);

                            return new ReadOuput();
                        }
                    } else {
                        Object h = Interpreter.interpret(tokens, scope, iConfig);
                        if (h instanceof HashMap)
                            scope.setVfs((Vfs) h);
                        return new ReadOuput();
                    }
                } else if (mode == REPLMode.PRINT_TOKEN) {
                    StringBuilder sb = new StringBuilder();
                    for (Token<?> t : tokens) {
                        sb.append(t.toString()).append(tokens.size() != 1 ? "\n" : "");
                    }
                    return new ReadOuput(sb.toString());
                }
            }
        } catch (Exception e) {
            this.state = State.ERROR;
            // add stack trace as a string separated by new lines.

            StringBuilder stackTrace = new StringBuilder();
            stackTrace.append(e.getMessage()).append("\n");
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace.append("\t").append(element.toString()).append("\n");
            }
            return new ReadOuput(stackTrace.toString());
        }
        return null;
    }
}
