package io.quarkus.deployment.logging;

import java.util.logging.Handler;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * Declare that a custom log handler should be used.
 *
 * @author Andy Damevin
 */
public final class CustomLogHandlerBuildItem extends MultiBuildItem {

    private final Handler handler;

    public CustomLogHandlerBuildItem(final Handler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler cannot be null");
        }

        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }
}
