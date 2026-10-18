package com.jaiva.interpreter.libs.annotation;

import com.jaiva.interpreter.libs.Globals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Special annotation only applied to the {@link Globals} class to mark it as the Jaiva scoping entry poiny.
 * <p>
 *     This annotation should not be used by external tooling.
 * </p>
 * @see Library
 * @see PublicLibrary
 * @see JaivaLibrary
 * @see Exports
 * @author Lehlogonolo Poole
 */
@Retention(RetentionPolicy.RUNTIME)
@JaivaLibrary(path="global", description = "The globals, These functions and variables are available in any scope.")
@Target(ElementType.TYPE)
public @interface GlobalsLib {
    String path() default "globals";
    String description() default "The globals, These functions and variables are available in any scope without an explicit import.";
}
