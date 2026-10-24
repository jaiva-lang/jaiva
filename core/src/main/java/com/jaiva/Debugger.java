package com.jaiva;

import com.jaiva.interpreter.Interpreter;
import com.jaiva.interpreter.MapValue;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.DebugController;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseVariable.DefinedVariable;
import com.jaiva.interpreter.symbol.DefinedFunction;
import com.jaiva.interpreter.symbol.Symbol;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.tuple.SamePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * The Debugger class is responsible for managing the debugging environment
 * within the Jaiva interpreter.
 * 
 * What it does is ask for CLI input from the user to execute debug related
 * commands like adding or removing breakpoints.
 * This class is called when the user runs the interpreter with the debug flag
 * enabled.
 */
public final class Debugger {
    public static final String PROMPT = "jaiva-debug> ";
    /**
     * Flag indicating whether the debugger is running in CLI mode. This means it
     * will print the prompt and other thing slike that. Otherwise it will do so
     * cleanly without cluttering the output.
     */
    private static boolean CLI;
    private static final String DEBUG_COMMANDS = """
            Available commands:

            - help/h : Show this help message.
            - breakpoint/bp <subcommand> [arg] : Manage breakpoints:
                - breakpoint add <line> : Add a breakpoint at the specified line number.
                - breakpoint remove <line> : Remove a breakpoint at the specified line number.
                - breakpoint list : List all current breakpoints.
                - breakpoint clear : Clear all breakpoints.
            - step/s : Step over the next line of code.
            - pause : Pause the debugger.
            - start : Start the debugging session.
            - continue/cont : Continue execution until the next breakpoint or end of file.
            - vfs <subcommand> [arg] : Manage the variable functions hashmap (vfs):
                - vfs get <variable_name> : Get the value of a variable from the vfs.
                - vfs dump [all] : Dump the current variable functions hashmap (vfs).
                    - If 'all' is specified, it will show all symbols, otherwise only show user defined symbols
            - exit/quit : Exit the debugger.
            - stack trace/st/scope : Prints a human-readable linked list representing the current scope and parent scopes.

            """;
    /**
     * The current state of the debugger.
     */
    private State state = State.STARTUP;
    /**
     * The reader used to read input from the user in the debugging environment.
     */
    private final BufferedReader reader;
    /**
     * The list of tokens representing the current program being debugged.
     * This is used to interpret the program and manage breakpoints.
     */
    private final ArrayList<Token<?>> tokens;
    /**
     * The configuration for the interpreter runtime, including the debug
     * controller.
     */
    public IConfig<Object> config;

    /**
     * Constructs a new Debugger instance with the specified configuration,
     * tokens, and CLI mode.
     * 
     * @param c   The configuration for the interpreter runtime.
     * @param t   The list of tokens representing the current program.
     * @param cli Whether to run the debugger in CLI mode.
     */
    public Debugger(IConfig<Object> c, ArrayList<Token<?>> t, boolean cli) {
        // Constructor can be used to initialize any resources needed for debugging
        reader = new BufferedReader(new InputStreamReader(System.in));
        config = c;
        tokens = t;
        CLI = cli;
        prompt();
    }

    /**
     * Starts the debugging prompt, allowing the user to enter commands to control
     * the debugging session.
     * <p>
     * This method runs in a loop, waiting for user input and executing commands
     * based on the input.
     */
    public void prompt() {
        while (true) {
            if (config.dc.state == DebugController.State.PAUSED || config.dc.state == DebugController.State.STOPPED)
                System.out.print(PROMPT);
            try {
                String input = reader.readLine();
                if (input == null || input.trim().isEmpty()) {
                    continue; // Skip empty input
                }
                if (input.equals("exit") || input.equalsIgnoreCase("quit")) {
                    if (CLI)
                        System.out.println("Exiting debugger.");
                    reader.close();
                    config.dc.active = false; // Deactivate the debug controller
                    break; // Exit the debugger
                }
                if (input.equals("scope") || input.equals("st") || input.equals("stack trace")) {
                    System.out.println(config.dc.scope.toString());
                }
                String[] parts = input.split(" ");
                String command = parts[0].toLowerCase();
                switch (command) {
                    case "help", "h" -> {
                        if (CLI) {
                            System.out.println(DEBUG_COMMANDS);
                        }
                    }
                    case "breakpoint", "bp" -> {
                        String sub = parts.length > 1 ? parts[1] : null;
                        String arg = parts.length > 2 ? parts[2] : null;
                        if (sub != null && sub.equals("add") && arg != null) {
                            try {
                                int lineNumber = Integer.parseInt(arg);
                                config.dc.addBreakpoint(lineNumber);
                                if (CLI)
                                    System.out.println("Breakpoint added at line: " + lineNumber);
                            } catch (NumberFormatException e) {
                                if (CLI)
                                    System.out.println("Invalid line number: " + arg);
                            }
                        } else if (sub != null && sub.equals("remove") && arg != null) {
                            try {
                                int lineNumber = Integer.parseInt(arg);
                                config.dc.removeBreakpoint(lineNumber);
                                if (CLI)
                                    System.out.println("Breakpoint removed at line: " + lineNumber);
                            } catch (NumberFormatException e) {
                                if (CLI)
                                    System.out.println("Invalid line number: " + arg);
                            }
                        } else if (sub == null || sub.isEmpty() && CLI) {
                            System.out.println(
                                    "Usage: breakpoint add <line> | breakpoint remove <line> | breakpoint list | breakpoint clear");
                        } else {
                            switch (sub) {
                                case "list" -> {
                                    if (config.dc.getBreakpoints().isEmpty()) {
                                        System.out.println(CLI ? "No breakpoints set." : "[]");
                                    } else {
                                        System.out.println(
                                                (CLI ? "Current breakpoints: " : "") + config.dc.getBreakpoints());
                                    }
                                    break;
                                }
                                case "clear" -> {
                                    config.dc.clearBreakpoints();
                                    if (CLI)
                                        System.out.println("All breakpoints cleared.");
                                    break;
                                }
                                default -> {
                                    if (CLI)
                                        System.out.println(
                                                "Usage: breakpoint add <line> | breakpoint remove <line> | breakpoint list | breakpoint clear");

                                }
                            }
                        }
                    }
                    case "step", "s" -> {
                        if (config.dc.active) {
                            config.dc.stepOver = new SamePair<>(Boolean.TRUE, Boolean.FALSE); // Set step over flag
                            config.dc.currentLineNumber++; // Increment the line number
                            if (CLI)
                                System.out.println("Stepping over the next line.");
                            config.dc.resume(); // Resume the debugger to step over
                        } else {
                            if (CLI)
                                System.out.println("Debugger is not active.");
                        }
                    }
                    case "pause" -> {
                        if (state != State.STARTUP)
                            config.dc.pause();
                    }
                    case "start" -> {
                        // config.dc.resume();
                        if (state == State.STARTUP) {
                            state = State.RUNNING;
                            config.dc.state = DebugController.State.RUNNING;
                            new Thread(() -> {
                                try {
                                    Interpreter.interpret(tokens, new Scope(config), config);
                                } catch (Exception e) {
                                    System.err.println("Error during interpretation: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }).start();
                        } else {
                            if (CLI)
                                System.out.println("Debugger is already running.");
                        }
                    }
                    case "continue", "cont" -> {
                        if (config.dc.state == DebugController.State.PAUSED) {
                            config.dc.resume();
                            config.dc.state = DebugController.State.RUNNING;
                            if (CLI)
                                System.out.println("Debugger resumed.");
                        } else {
                            if (CLI)
                                System.out.println("Debugger is not paused.");
                        }
                    }
                    case "vfs" -> {
                        String sub = parts.length > 1 ? parts[1] : "";
                        String arg = parts.length > 2 ? parts[2] : "";

                        switch (sub) {
                            case "dump" -> {
                                boolean showAll = "all".equals(arg);
                                config.dc.scope.getVfs().forEach((name, value) -> {
                                    Symbol s = (Symbol) value.getValue();
                                    if (showAll || value.getValue() instanceof DefinedFunction
                                            || value.getValue() instanceof DefinedVariable) {
                                        System.out.println("\t" + name + " <- " + s.toDebugString());
                                    }
                                });
                            }
                            case "get" -> {
                                if (config.dc.scope.getVfs().isEmpty()) {
                                    if (CLI)
                                        System.out.println("No symbols in the current context.");
                                } else if (arg != null) {
                                    MapValue mv = config.dc.scope.getVfs().get(arg);
                                    if (mv == null) {
                                        System.out.println("Symbol '" + arg + "' not found in the current context.");
                                    } else {
                                        Symbol sym = (Symbol) mv.getValue();
                                        if (sym != null) {
                                            System.out.println(sym.toDebugString());
                                        } else {
                                            System.out
                                                    .println("Symbol '" + arg + "' not found in the current context.");
                                        }
                                    }
                                } else {
                                    System.out.println("Usage: vfs get <variable_name>");
                                }
                            }
                            default -> {
                                config.dc.scope.getVfs().forEach((name, value) -> {
                                    Symbol s = value.getValue();
                                    if (value.getValue() instanceof DefinedFunction
                                            || value.getValue() instanceof DefinedVariable) {
                                        System.out.println("\t" + name + " <- " + s.toDebugString());
                                    }
                                });
                            }
                        }
                    }

                }
            } catch (java.io.IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
            }
        }
    }

    /**
     * Sets the state of the debugger.
     * 
     * @param state The new state to set.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Gets the current state of the debugger.
     * 
     * @return The current state of the debugger.
     */
    public State getState() {
        return state;
    }

    /**
     * The state of the debugger.
     * <p>
     * This enum represents the different states the debugger can be in, such as
     * running or waiting for user input.
     */
    enum State {
        /**
         * The debugger is running.
         */
        RUNNING,
        /**
         * The debugger has just started and is waiting for user input.
         */
        STARTUP
    }
}
