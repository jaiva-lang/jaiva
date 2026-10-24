package com.jaiva.interpreter.runtime;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

/**
 * The {@code Resources} class provides a centralised container for
 * a single thread to manage its own resources used throughout its life cycle.
 */
public final class Resources {

    public enum Common {
        CONSOLE_IN(UUID.randomUUID());

        private final UUID uuid;
        Common(UUID uuid) {
            this.uuid = uuid;
        }
        public UUID getUuid() {
            return uuid;
        }
    }

    private final HashMap<UUID, Closeable> closeables = new HashMap<>();

    /**
     * The configuration object for the interpreter.
     */
    public Resources() {
        closeables.put(Common.CONSOLE_IN.getUuid(), new Scanner(System.in));
    }

    @NotNull
    public UUID addResource(Closeable closeable) {
        UUID uuid = UUID.randomUUID();
        closeables.put(uuid, closeable);
        return uuid;
    }

    @NotNull
    public <T extends Closeable> T getResource(UUID uuid, Class<T> tClass) throws IllegalArgumentException {
        Closeable closeable = closeables.get(uuid);
        if (closeable == null || closeable.getClass() != tClass) {
            throw new IllegalArgumentException(tClass.getSimpleName() + " is not a valid resource");
        }
        return (T) closeable;
    }

    public void releaseResource(UUID uuid) throws IOException {
        Closeable closeable = closeables.remove(uuid);
        closeable.close();
    }

    /**
     * Releases the global resources by closing the input stream associated with the
     * console.
     * This method should be called to free up resources when they are no longer
     * needed.
     */
    public int release() throws IOException {
        int i = 0;
        for (Closeable c : closeables.values()) {
            c.close();
            i++;
        }
        return i;
    }
}
