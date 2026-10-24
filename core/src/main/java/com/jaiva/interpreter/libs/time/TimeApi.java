package com.jaiva.interpreter.libs.time;

import com.jaiva.errors.InterpreterException;
import com.jaiva.interpreter.libBuilders.func.Argument;
import com.jaiva.interpreter.libBuilders.func.Arguments;
import com.jaiva.interpreter.libBuilders.func.FunctionBuilder;
import com.jaiva.interpreter.libBuilders.func.arg.AArgument;
import com.jaiva.interpreter.libBuilders.var.VariableBuilder;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.specific.TVoidValue;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JaivaLibrary(path = "time/api", description = "The time api where the relevant things such as parsing dates are.")
public class TimeApi extends BaseLibrary {
    public TimeApi(IConfig<Object> config) {
        add(new FNow(), new FMsToSec(), new FParseDate(), new VMaxTime());
    }

    public static class FNow extends BaseFunction {
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

    public static class FMsToSec extends BaseFunction {
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
            Object val = callProperties.params().getFirst();
            if (!(val instanceof Number num))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "t_msToSec() only accepts a number.", callProperties.getLineNumber());

            return num.doubleValue() / 1000.0;
        }

    }

    public static class FParseDate extends BaseFunction {
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
            Object dateStringObj = callProperties.params().getFirst();
            if (!(dateStringObj instanceof String dateString))
                throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "t_parseDate() expects a date string as the first argument.", callProperties.getLineNumber());

            String format = null;
            if (callProperties.params().size() > 1) {
                Object formatObj = callProperties.params().get(1);
                if (!(formatObj instanceof String) && !(formatObj instanceof TVoidValue))
                    throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "t_parseDate() expects a format string as the second argument.", callProperties.getLineNumber());
                format = formatObj instanceof TVoidValue ? null : (String) formatObj;
            }

            DateTimeFormatter formatter;
            if (format != null) {
                try {
                    formatter = DateTimeFormatter.ofPattern(format);
                } catch (IllegalArgumentException e) {
                    throw new InterpreterException.WtfAreYouDoingException(callProperties.scope(), "Pls put valid format for date gng. "+
                            "(One day i'll make a format package, and that same day ill make the inverse to this function)", callProperties.getLineNumber());
                }
            } else
                formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);

            String timezone = "UTC";
            if (callProperties.params().size() > 2) {
                Object formatObj = callProperties.params().get(2);
                if (!(formatObj instanceof String))
                    throw new InterpreterException.FunctionParametersException(callProperties.scope(), this, "3", formatObj, String.class, callProperties.getLineNumber());
//                    throw new InterpreterException.WtfAreYouDoingException(scope, "t_parseDate() needs the 3rd param to be a string zawg.", tFuncCall.lineNumber);
                timezone = (String) formatObj;
            }

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
