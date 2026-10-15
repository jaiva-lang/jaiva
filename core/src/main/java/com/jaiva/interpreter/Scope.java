package com.jaiva.interpreter;

import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.global.Globals;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.tokens.TokenDefault;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The Scope class is used to keep track of the scope in which a
 * variable
 * is defined or used.
 * <p>
 * It contains information about the current scope, the token associated with
 * it,
 * the line number where it is defined or used, and the parent scope if
 * any.
 */
public final class Scope {
    private ScopeConfig config = new ScopeConfig();
    private final Context current;
    private final TokenDefault<?> token;
    private final int lineNumber;
    private final Scope parent;

    private Vfs vfs;
    private final Globals globals;

    /**
     * Default constructor for Scope.
     * <p>
     * This constructor initializes the current scope to the global scope,
     * token to null, line number to 0, and parent scope to null.
     */
    public Scope(IConfig<Object> iconfig) {
        this.current = Context.GLOBAL;
        this.token = null;
        this.lineNumber = 0;
        this.parent = null;
        try {
            this.globals = new Globals(iconfig);
            this.vfs = this.getGlobals().getVfs(iconfig, this.getGlobals());
        } catch (JaivaException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Default constructor for Scope with imported Globals.
     * <p>
     * This constructor initializes the current scope to the global scope,
     * token to null, line number to 0, and parent scope to null.
     */
    public Scope(IConfig<Object> iconfig, List<Class<? extends BaseLibrary>> globals) {
        this.current = Context.GLOBAL;
        this.token = null;
        this.lineNumber = 0;
        this.parent = null;
        try {
            this.globals = new Globals(iconfig, Scope.class.getClassLoader(), globals);
            this.vfs = this.getGlobals().getVfs(iconfig, this.getGlobals());
        } catch (JaivaException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor for Scope with parent scope.
     * <p>
     * This constructor initializes the current scope, token, line number, and
     * sets the parent scope to the provided parent.
     *
     * @param newC       The current scope in which the variable is defined or
     *                   used.
     * @param token      The token associated with the current scope, if any.
     * @param parent     The parent scope, if any.
     */
    public Scope(Context newC, TokenDefault<?> token, @NotNull Scope parent) {
        this.current = newC;
        this.token = token;
        this.lineNumber = token == null ? 0 : token.lineNumber;
        this.parent = parent;
        this.vfs = parent.getVfs().clone();
        this.config = parent.getConfig();
        this.globals = parent.getGlobals();
    }

    /**
     * Constructor for Scope with parent scope and a given vfs object.
     * <p>
     *     This is used by {@link com.jaiva.interpreter.symbol.BaseFunction} which needs to createFunction it's own copy to inject parameters.
     * </p>
     * @param newC The current scope
     * @param token The token associated
     * @param parent The parent scope
     * @param vfs The vfs for this scope.
     */
    public Scope(Context newC, TokenDefault<?> token, @NotNull Scope parent, Vfs vfs) {
        this.current = newC;
        this.token = token;
        this.lineNumber = token == null ? 0 : token.lineNumber;
        this.parent = parent;
        this.vfs = vfs;
        this.globals = parent.getGlobals();
        this.config = parent.getConfig();
    }

    public Globals getGlobals() {
        return globals;
    }

    /**
     * Returns a string representation of the scope.
     * <p>
     * This method traverses the scope hierarchy and builds a string
     * representation of the current scope, token, and parent scopes.
     *
     * @return A string representation of the scope.
     */
    @Override
    public String toString() {
        Scope current = this;
        StringBuilder sb = new StringBuilder();
        while (current != null) {
            if (current.getCurrent() == Context.GLOBAL) {
                sb.append("global");
                current = null; // end the loop
            } else {
                sb.append(
                        current.getCurrent() == Context.EOF ? "end of file <- "
                                : (current.getToken().name.equals(current.getToken().getClass().getSimpleName())
                                        ? ("[" + current.getToken().getClass().getSimpleName() + " "
                                                + current.getToken().lineNumber
                                                + "]")
                                        : (("[" + current.getToken().getClass().getSimpleName() + " "
                                                + current.getToken().lineNumber + "]") + " : " + current.getToken().name + "()"))
                                        + " <- ");
                current = current.getParent();
            }
        }
        return sb.toString();
    }

    public ScopeConfig getConfig() {
        return config;
    }

    /**
     * The current scope in which the variable is defined or used.
     * This variable is used to determine the scope of a variable
     */
    public Context getCurrent() {
        return current;
    }

    public TokenDefault<?> getToken() {
        return token;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * The parent scope, if any.
     * This is used to keep track of the scope hierarchy.
     * <p>
     * If null, it means this is the root scope.
     * If not null, it means this scope is nested within another scope
     * trace.
     */
    public Scope getParent() {
        return parent;
    }

    public Vfs getVfs() {
        return vfs;
    }

    public void setVfs(Vfs vfs) {
        this.vfs = vfs;
    }
}
