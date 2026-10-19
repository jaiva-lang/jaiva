package com.jaiva.interpreter.libs.types;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class TypeConverter {
    public static byte toByte(int i)  {
        if (i < 0 || i > 255)
            throw new IllegalArgumentException(
                    "Expected a byte value between 0 and 255, got: " + i
            );


        return (byte)i;
    }

    public static byte toByte(Object i)  {
        if (!(i instanceof Integer i2)) throw new IllegalArgumentException(
                "Expected an int value. got: " + i
        );
        return toByte(i2.intValue());
    }

    public static byte toByte(long i)  {
        if (i < 0 || i > 255) {
            throw new IllegalArgumentException(
                    "Expected a byte value between 0 and 255, got: " + i
            );
        }

        return (byte)i;
    }

    @NotNull
    @Contract(pure = true)
    public static int[] byteArrToIntArr(@NotNull byte[] arr) {
        int[] arr2 = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            arr2[i] = fromByte(arr[i]);
        }
        return arr2;
    }

    @NotNull
    @Contract(pure = true)
    public static byte[] intArrToByteArr(@NotNull int[] b2) {
        byte[] b = new byte[b2.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = toByte(b2[i]);
        }
        return b;
    }

    @NotNull
    @Contract(pure = true)
    public static byte[] intArrToByteArr(@NotNull ArrayList<?> b2) {
        byte[] b = new byte[b2.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = toByte(b2.get(i));
        }
        return b;
    }

    public static int fromByte(byte b) {
        return b & 0xFF;
    }

    @NotNull
    public static <T> ArrayList<Object> asArr(Object arrayObject) {
        ArrayList<Object> list = new ArrayList<>();

        if (arrayObject != null && arrayObject.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(arrayObject);

            for (int i = 0; i < length; i++) {
                list.add(java.lang.reflect.Array.get(arrayObject, i));
            }
        }

        return list;
    }
}
