package io.quarkus.devtools;

import java.util.function.Supplier;

public interface MessageWriter {

    MessageWriter DEFAULT = new DefaultMessageWriter();

    default void info(String format, Object... args) {
        info(String.format(format, args));
    }

    void info(String msg);

    default void error(String format, Object... args) {
        error(String.format(format, args));
    }

    void error(String msg);

    boolean isDebugEnabled();

    default void debug(String format, Object... args) {
        if (!isDebugEnabled()) {
            return;
        }
        debug(String.format(format, args));
    }

    void debug(String msg);

    default void debug(Supplier<String> messageSupplier) {
        if (!isDebugEnabled()) {
            debug(messageSupplier.get());
        }
    }

    default void warn(String format, Object... args) {
        warn(String.format(format, args));
    }

    void warn(String msg);
}
