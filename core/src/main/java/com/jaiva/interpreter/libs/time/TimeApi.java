package com.jaiva.interpreter.libs.time;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.fn.CallProperties;
import com.jaiva.interpreter.symbol.fn.ImplementedFunction;
import com.jaiva.tokenizer.jdoc.JDoc;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JaivaLibrary(path = "time/api", description = "The time api where the relevant things such as parsing dates are.")
public class TimeApi extends BaseLibrary {
    public TimeApi() {
        add(new FNow(), new FMsToSec(), new FParseDate(), new VMaxTime());
    }

    static class FNow extends ImplementedFunction {
        public FNow() {
            super(
                    FunctionBuilder.start()
                            .name("t_now")
                            .setLib("time/api")
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the current time in milliseconds since the Unix epoch.")
                                            .addReturns("A number representing the current time in milliseconds.")
                                            .sinceVersion("5.0.0")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            return System.currentTimeMillis();
        }
    }

    static class FMsToSec extends ImplementedFunction {
        public FMsToSec() {
            super(
                    FunctionBuilder.start()
                            .name("t_msToSec")
                            .setLib("time/api")
                            .arguments(Arguments.getInstance().add(
                                    new AArgument("milliseconds", "The number of milliseconds to convert.", false, Argument.Type.NUMBER)
                            ))
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Converts milliseconds to seconds.")
                                            .addReturns("A number representing the converted seconds.")
                                            .sinceVersion("5.0.0")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            Number num = fetch("milliseconds", Number.class, callProperties);
            return num.doubleValue() / 1000.0;
        }

    }

    public static class FParseDate extends ImplementedFunction {
        public FParseDate() {
            super(
                    FunctionBuilder.start()
                            .name("t_parseDate")
                            .setLib("time/api")
                            .arguments(Arguments.getInstance()
                                    .add(
                                            new AArgument(
                                            "dateString", "The date string to parse.",
                                                    false, Argument.Type.STRING
                                            )
                                    ).add(
                                            new AArgument(
                                                    "format",
                                                    "The format of the date string (e.g. \"yyyy-MM-dd HH:mm:ss\")."
                                                    + "Defaults to ISO_LOCAL_DATE_TIME,", true, Argument.Type.STRING
                                            )
                                    ).add(
                                            new AArgument(
                                                    "timezone",
                                                    "The timezone to parse this date into. "
                                                            + "You can either put a magic string yourself or use the constants within "
                                                            + "\"jaiva/time/zone\"",
                                                    true, Argument.Type.STRING
                                            )
                                    )
                            ).docs(
                                    JDoc.builder()
                                            .addDesc("Parses a date string into milliseconds since the Unix epoch.")
                                            .addReturns("A number representing the parsed date in milliseconds.")
                                            .addNote("If no timezone is provided, a default timezone of UTC is used.")
                                            .addExample("""
                                            @ Import jaiva/time/zone
                                            tsea "jaiva/time/zone" <- TZ_AfricaJohannesburg!
                                            maak ms <- t_parseDate("2023-10-05 14:30:00", "yyyy-MM-dd HH:mm:ss", TZ_AfricaJohannesburg)!
                                            khuluma(ms)! @ Outputs the milliseconds since epoch for the given date in the specified timezone.
                                            """)
                                            .sinceVersion("5.0.0")
                            )
            );
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            checkParams(callProperties.toCheckParams());
            String dateString = fetch("dateString", String.class, callProperties);
            String format = fetchNullable("format", String.class, callProperties, null);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            if (format != null) {
                try {
                    formatter = DateTimeFormatter.ofPattern(format);
                } catch (IllegalArgumentException e) {
                    throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "Pls put valid format for date gng. "+
                            "(One day I'll make a format package, and that same day ill make the inverse to this function)", callProperties.getLineNumber());
                }
            }

            LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);

            String timezone = fetch("timezone", String.class, callProperties,"UTC");

            ZoneId timeZoneId;
            try {
                timeZoneId = ZoneId.of(timezone);
            } catch (DateTimeException f) {
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "If you parse a zoneID it has to be valid zawlf", callProperties.getLineNumber());
            }

            ZonedDateTime zonedDateTime = dateTime.atZone(timeZoneId);
            return zonedDateTime.toInstant().toEpochMilli();
        }
    }

    public static class VMaxTime extends BaseVariable {
        public VMaxTime() {
            super(
                    VariableBuilder.start()
                            .name("t_maxTime")
                            .value(Long.MAX_VALUE)
                            .docs(
                                    JDoc.builder()
                                            .addDesc("Returns the maximum possible value for a time in milliseconds (Long.MAX_VALUE).")
                                            .sinceVersion("5.0.0")
                            )
            );
            freeze();
        }
    }
}
