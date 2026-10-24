package com.jaiva;

import com.jaiva.errors.InterpreterException;
import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.Interpreter;
import com.jaiva.interpreter.Scope;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.tokenizer.tokens.Token;
import com.yetnt.utils.builders.AnsiColour;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public final class Streamer {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private boolean run = true;

//    public static String removeAnsiColour(String rawMessage) {
//        StringBuilder result = new StringBuilder();
//
//        for (int i = 0; i < rawMessage.length(); i++) {
//            if (rawMessage.charAt(i) == '\u001B'
//                    && i + 1 < rawMessage.length()
//                    && rawMessage.charAt(i + 1) == '[') {
//
//                i += 2;
//
//                // Skip until the terminating 'm'
//                while (i < rawMessage.length() && rawMessage.charAt(i) != 'm') {
//                    i++;
//                }
//
//                continue;
//            }
//
//            result.append(rawMessage.charAt(i));
//        }
//
//        return result.toString();
//    }

    private void printTokens(ArrayList<Token<?>> tokens) throws JaivaException {
        System.out.print("[");
        for (int i = 0; i < tokens.size(); i++) {
            Token<?> token = tokens.get(i);
            System.out.print(token.value().toJson());
            if (i != tokens.size() - 1) {
                System.out.print(",");
            }
        }
        System.out.print("]");
        System.out.println();
    }

    Streamer(String[] args) {
        try {

            if (args.length > 2) {
                String arg3 = args[1]; // some flag
                String arg4 = args[2]; // the file to parse

                if (arg3.equals("-e") || arg3.equals("--exit")) {
                    ArrayList<Token<?>> tokens = Main.parseTokens(arg4, false);
                    printTokens(tokens);
                    return;
                }
            }

            while (run) {
                String line = reader.readLine();
                if (line == null || line.equals("EXIT") || line.isEmpty()) {
                    run = false;
                    System.out.println();
                    reader.close();
                } else {
                    // pray its just a file input
                    try {
                        String l = line;
                        String arg = null;
                        if (line.contains("#")) {
                            String[] parts = line.split("#");
                            l = parts[0].trim();
                            arg = parts[1].trim();
                        }
                        ArrayList<Token<?>> tokens = Main.parseTokens(l, false);

                        if (arg != null && arg.equals("INTERP")) {
                            IConfig<Object> iconfig = new IConfig<>(line.split("#"), l);
                            iconfig.streamer();
                            try {
                                Scope scope = new Scope(iconfig);
                                Interpreter.interpret(tokens, scope, iconfig);
                                // if we make it here, cool
                                System.out.println(toJsonError(iconfig, new InterpreterException.StreamerSuccess("Success", -1, scope), Type.INTERP_SUCCESS));
                            } catch (InterpreterException e) {
                                System.out.println(toJsonError(iconfig, e, Type.ERR_INTERP));
                            } catch (Exception e) {
                                System.out.println(toJsonError(iconfig, e, Type.ERR_INTERP_DIED));
                            }
                            System.out.println();
                            continue;
                        }

                        printTokens(tokens);
                    } catch (JaivaException e) {
                        System.out.println(toJsonError(null, e, Type.ERR_TOKENS));
                        System.out.println();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(
                    toJsonError(null, e, Type.ERR_STREAMER)
            );
            System.out.println();
	    if (args.length < 2)
            new Streamer(args); // try again.
        }
    }

    enum Type {
        ERR_TOKENS, ERR_STREAMER, ERR_INTERP, ERR_INTERP_DIED, INTERP_SUCCESS
    }

    private static String jsonEscape(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public String toJsonError(IConfig<Object> config, Exception e, Type type) {
        String str =  "{\"streamer\":true,\"message\":\"" + jsonEscape(AnsiColour.remove(e.getMessage())) +
                "\",\"lineNumber\":"
                + ((e instanceof JaivaException j) ? j.getLineNumber() : -1)
                +",\"type\":\"" + type.toString() + "\"";
        if (config != null && e instanceof InterpreterException interpreterException) {
            String warnings = config.getWarnings().stream().reduce(
                    " ",
                    (s, w) ->
                            s + "{\"message\":\"" + jsonEscape(AnsiColour.remove(w.getMessage())) + "\",\"lineNumber\":" + w.getLineNumber() + "},"
                    ,
                    (s, s2) -> s + s2
            );
            String scopeStr = interpreterException.getScopeTrace().toString();
            str = str.replace("\\n" + scopeStr, "");
            return str + ",\"scope\":\""+scopeStr+"\", \"warnings\":[" + warnings.substring(0, warnings.length()-1) + "]}";
        } else {
            return str + "}";
        }
    }
}
