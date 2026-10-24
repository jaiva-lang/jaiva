package com.jaiva;

import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.TConfig;

/**
 * Configuration class that both the tokenizer and interpreter extend off to
 * have {@link IConfig} and {@link TConfig}
 */
public abstract class Config {

    /**
     * Constructs a new {@code Config} instance with the specified JAIVA source
     * path.
     */
    public Config() {
    }
}
