package com.jaiva.tokenizer.tokens.specific;

import com.jaiva.tokenizer.tokens.Token;

/**
 * This represents a token which it's purpose is only for documentating specific
 * user defined symbols. This token should not be able to be interpreted or be
 * seen in an array or anywhere in a tokens arraylist, as whatever method
 * handling the reading of a list of tokens, Should handle this specific token
 * case before the generic {@link Token}, by setting an outside variable or
 * another
 * way to do such. Then, when we do not receive the TDocsComment and that outside
 * variable is set, we set the "tooltip" property of the new token to that
 * outside variable, therefore adding the documentation.
 * <p>
 * (This ensures if we collect multiple TDocsComment)
 */
public final class TDocsComment extends TokenDefault<TDocsComment> {
    /**
     * The documentation comment.
     */
    public String comment;

    /**
     * Constructor for TDocsComment
     *
     * @param c The comment.
     */
    public TDocsComment(String c) {
        super("TDocsComment", -1);
        comment = c + "\n";
    }

    /**
     * Converts this token to the default {@link Token}
     *
     * @return {@link Token} with a T value of {@link TDocsComment}
     */
    public Token<TDocsComment> toToken() {
        return new Token<>(this);
    }
}
