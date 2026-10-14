package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.errors.JaivaException;
import com.jaiva.errors.TokenizerException;
import com.jaiva.lang.Keywords;
import com.jaiva.tokenizer.tokens.TStatement;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.tokens.TokenDefault;

/**
 * Represents a loop control statement such as {@code voetsek!} or
 * {@code nevermind!}
 */
public class TLoopControl extends TokenDefault<TLoopControl> implements TStatement {
    /**
     * The type of the loop control statement.
     */
    public Keywords.LoopControl type;

    /**
     * Constructor for TLoopControl
     *
     * @param type The type of the loop control statement.
     * @param ln   The line number.
     */
    public TLoopControl(String type, int ln) throws TokenizerException {
        super("TLoopControl", ln);
        if (type.equals(Keywords.LC_CONTINUE)) {
            this.type = Keywords.LoopControl.CONTINUE;
        } else if (type.equals(Keywords.LC_BREAK)) {
            this.type = Keywords.LoopControl.BREAK;
        } else {
            throw new TokenizerException.CatchAllException("So we're in LoopControl but not correctly?", ln);
        }
        json();
    }

    public void json() {
        json.set("loopType", type.toString());
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TLoopControl}
     */
    public Token<TLoopControl> toToken() {
        return new Token<>(this);
    }
}
