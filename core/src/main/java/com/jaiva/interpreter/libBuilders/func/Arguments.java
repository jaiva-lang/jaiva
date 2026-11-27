package com.jaiva.interpreter.libBuilders.func;

import com.jaiva.interpreter.libBuilders.func.arg.AVarArgument;
import com.jaiva.interpreter.libBuilders.func.arg.NeighbouringArg;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Arguments {
    ArrayList<Argument> arguments = new ArrayList<>();
    Arguments() {}

    @NotNull
    @Contract(" -> new")
    public static Arguments getInstance() {
        return new Arguments();
    }

    public Arguments add(NeighbouringArg argument) {
        arguments.add((Argument) argument);
        return this;
    }

    public Arguments addVarArg(AVarArgument argument) {
        // only one arg there.
        if (!arguments.isEmpty()) {
            throw new RuntimeException("Only one argument (vararg) is allowed when added.");
        }
        arguments.add(argument);
        return this;
    }

    public String[] build() {
        if (arguments.isEmpty()) {
            return new String[] {};
        }
        String[] result = new String[arguments.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = arguments.get(i).toTokenString();
        }
        return result;
    }

    public ArrayList<Argument> getArguments() {
        return  arguments;
    }

    public int getIndexFromName(String name) {
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i).toTokenString().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
