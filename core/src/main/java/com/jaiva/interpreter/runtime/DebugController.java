package com.jaiva.interpreter.runtime;

import com.jaiva.Debugger;
import com.jaiva.interpreter.Context;
import com.jaiva.interpreter.Interpreter;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.tuple.SamePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * The DebugController class is responsible for actually controllig the
 * debugging stuff.
 * <p>
 * It comes as part of {@link IConfig} and is expected to be used by
 * {@link Debugger} to manage the debugging environment.
 */
public final class DebugController {
    /**
     * The current line number being interpreted.
     * This variable is used to track the current line number during interpretation
     * and used for stepping through code.
     * It is updated by the interpreter as it processes each line of code.
     */
    public int currentLineNumber = 0;

    /**
     * Semaphore used to pause the debugger when a breakpoint is hit.
     * It allows the debugger to wait until the user decides to continue.
     */
    private final Semaphore pauseSem = new Semaphore(0);
    /**
     * Flag indicating whether the debugging environment is active. This is updated
     * by {@link Debugger} immediately after the user uses the debug flag.
     */
    public boolean active = false;
    /**
     * Set of breakpoints. Each breakpoint is represented by a line number.
     * This set is used to check if a breakpoint exists at a specific line number
     * and to manage breakpoints.
     */
    private final Set<Integer> breakpoints = new HashSet<>();
    /**
     * The current state of the debugger.
     * This variable is used to track the state of the debugger, such as
     * whether it is paused, running, or stopped.
     * It is updated by the debugger based on user input and the execution context.
     */
    public State state = State.PAUSED;
    /**
     * Flag indicating whether to step over the next line of code.
     * This is used to control the behavior of the debugger when stepping through
     * code.
     * If true, the debugger will skip the current line and move to the next one.
     */
    public SamePair<Boolean> stepOver = new SamePair<>(false, false);

    /**
     * The context trace for the current execution.
     * This is used to keep track of the execution context, including the current
     * function call stack and variable values.
     * It is updated by the interpreter as it processes each line of code.
     */
    public Scope scope;

    /**
     * Activates the debugger.
     */
    public void activate() {
        active = true;
    }

    /**
     * Prints the current state of the debugger, including the current line
     * number, symbol, and token.
     * <p>
     * This method is called when a breakpoint is hit, and it provides
     * information about the current execution context.
     * This method is also called when stepping through code which is why it sets
     * stepOver to false.
     * 
     * @param lineNumber The line number where the breakpoint was hit.
     * @param s          The symbol associated with the current execution context,
     *                   if any.
     * @param t          The token associated with the current execution context, if
     *                   any.
     * @param scope Context Trace.
     */
    public void print(int lineNumber, Symbol s, Token<?> t, Scope scope) {
        if (active) {
            currentLineNumber = lineNumber;
            if (!stepOver.getSecond() && stepOver.getFirst()) {
                stepOver = new SamePair<>(false, true);
                this.scope = scope;
            } else {
                if (!stepOver.getFirst() && stepOver.getSecond()) {
                    stepOver = new SamePair<>(false, false);
                    System.out.print("On line: " + lineNumber);
                } else {
                    System.out.print("breakpoint hit on line: " + lineNumber);
                    if (s != null) {
                        System.out.print(" | Symbol: " + s.toString());
                    }
                    if (t != null) {
                        System.out.print(" | Token: " + t.value());
                    }
                }
                System.out.println();
                System.out.print(Debugger.PROMPT);
                state = State.PAUSED;
                this.scope = scope;
                pause();
            }
            // removeBreakpoint(lineNumber); <- Removed to support recursive functions.
        }
    }

    /**
     * Ends the debugging session and releases the semaphore to allow the debugger
     * to exit.
     * <p>
     * This method is called by
     * {@link Interpreter#interpret(ArrayList, Scope, IConfig)}
     * when the end of the file is reached, indicating that
     * there are no more lines to interpret.
     * 
     * @param scope The current scope holding the virtual file system containing the variables and their values
     *           at the end of the file.
     */
    public void endOfFile(Scope scope) {
        if (active) {
            System.out.println("END OF FILE");
            System.out.println(Debugger.PROMPT);
            active = false;
            state = State.STOPPED;
            this.scope = new Scope(Context.EOF, null, scope); // Resetting cTrace to EOL context
            pauseSem.release(); // Release the semaphore to allow the debugger to exit
        }
    }

    /**
     * Pauses the debugger, waiting for the user to continue.
     * <p>
     * This method is called when a breakpoint is hit or when the user
     * explicitly pauses the debugger.
     * It blocks until the user decides to resume the debugging session.
     */
    public void pause() {
        if (active) {
            // System.out.println("Debugger paused. Type 'continue' to resume.");
            try {
                pauseSem.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Resumes the debugging session, allowing the interpreter to continue
     * executing code.
     * <p>
     * This method is called when the user decides to continue after hitting a
     * breakpoint or pausing the debugger.
     */
    public void resume() {
        if (active) {
            pauseSem.release();
        }
    }

    /**
     * Adds a breakpoint at the specified line number.
     * 
     * @param lineNumber The line number where the breakpoint should be added.
     */
    public void addBreakpoint(int lineNumber) {
        breakpoints.add(lineNumber);
    }

    /**
     * Removes a breakpoint at the specified line number.
     * 
     * @param lineNumber The line number where the breakpoint should be removed.
     */
    public void removeBreakpoint(int lineNumber) {
        breakpoints.remove(lineNumber);
    }

    /**
     * Checks if a breakpoint exists at the specified line number.
     * 
     * @param lineNumber The line number to check for a breakpoint.
     * @return true if a breakpoint exists at the specified line number, false
     *         otherwise.
     */
    public boolean hasBreakpoint(int lineNumber) {
        return breakpoints.contains(lineNumber);
    }

    /**
     * Gets the set of all breakpoints.
     * 
     * @return A set containing all line numbers where breakpoints are set.
     */
    public Set<Integer> getBreakpoints() {
        return new HashSet<>(breakpoints);
    }

    /**
     * Clears all breakpoints.
     */
    public void clearBreakpoints() {
        breakpoints.clear();
    }

    /**
     * The state of the debugger.
     * <p>
     * This enum represents the different states the debugger can be in, such as
     * running or waiting for user input.
     */
    public enum State {
        /**
         * The debugger is paused, waiting for user input.
         * This state is typically reached when a breakpoint is hit or when the user
         * explicitly pauses the debugger.
         */
        PAUSED,
        /**
         * The debugger is running, executing code without interruption.
         * This state is typically reached when the user resumes the debugging session
         * after pausing or hitting a breakpoint.
         */
        RUNNING,
        /**
         * The debugger is stopped, indicating that the debugging session has ended.
         * This state is typically reached when the end of the file is reached or when
         * the user explicitly stops the debugging session.
         */
        STOPPED
    }

}
