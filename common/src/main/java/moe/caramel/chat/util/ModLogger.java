package moe.caramel.chat.util;

import moe.caramel.chat.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging Utility
 */
public final class ModLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger("caramelChat");

    /**
     * Prints a log message.
     *
     * @param msg log format
     * @param data other data
     */
    public static void log(final String msg, final Object... data) {
        LOGGER.info(msg, data);
    }

    /**
     * Prints a error message.
     *
     * @param msg log format
     * @param data other data
     */
    public static void error(final String msg, final Object... data) {
        LOGGER.error(msg, data);
    }

    /**
     * Prints a debug message.
     *
     * @param msg log format
     * @param args other data
     */
    public static void debug(final String msg, final Object... args) {
        if (Main.DEBUG) {
            LOGGER.warn(msg, args);
        }
    }
}
