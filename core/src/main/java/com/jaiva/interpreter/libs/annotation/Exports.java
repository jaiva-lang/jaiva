package com.jaiva.interpreter.libs.annotation;

import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.LibraryLike;
import com.jaiva.interpreter.libs.Globals;
import com.jaiva.interpreter.runtime.IConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which specifies that this {@link BaseLibrary} class, is simply an exporter of
 * multiple other {@link BaseLibrary} classes.
 * <p>
 *     This is different form a container library since one with Exports is generally expected
 *     to be of {@link PublicLibrary} or {@link JaivaLibrary}. It just simply allows the aggregation
 *     of multiple libraries (including built in libraries) into a singular path string that the above 2 interfaces
 *     enforce. This naturally allows hierarchical paths to be possible
 *     <pre>{@code
 *      @PublicLibrary(path = "time/zone")
 *      public class TimeZoneLibrary {...}
 *
 *      @PublicLibrary(path = "time/api")
 *      public class TimeApiLibrary {...}
 *
 *      @Exports(value = {TimeZoneLibrary.class, TimeApiLibrary.class})
 *      @PublicLibrary(path = "time")
 *      public class TimeZoneLibrary {...}
 *
 *      // in Jaiva
 *
 *      @ You can import the singular libraries
 *      tsea "jaiva/time/zone"!
 *      tsea "jaiva/time/api"!
 *
 *
 *      @ But you can now import the aggregation, which is
 *      @ functionally the same as importing both
 *      tsea "jaiva/time"! @ ALl stuff from both libraries
 *
 *      @ Keep in mind the aggregation might have its own
 *      @ libraries which are unique to it
 *     }</pre>
 * </p>
 * @implSpec The library with this interface is simply a normal library with the added external symbols
 * from other libraries. Meaning tooling such as exporting as JSON or Markdown documentation, will naturally
 * have an overlap with the exported library as the symbols are identical. I cannot be bothered to deduplicate
 * but be warned if you expected some grand scheme.
 * @implNote A {@link PublicLibrary} can be an aggregation of either other {@link PublicLibrary} or even {@link JaivaLibrary}
 * @see PublicLibrary
 * @see JaivaLibrary
 * @see GlobalsLib
 * @see Library
 * @see LibraryLike#loadClassLibrary(IConfig, Globals)
 * @see Globals
 * @see BaseLibrary
 * @author Lehlogonolo Poole
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Exports {
    Class<? extends BaseLibrary>[] value();
}
