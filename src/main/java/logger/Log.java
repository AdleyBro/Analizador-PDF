package logger;

import java.util.logging.Logger;

public class Log {
    private static final Logger LOGGER = Logger.getLogger(Log.class.getName());

    public static void error(String msg, String excepcion) {
        LOGGER.severe(msg + "\n" + excepcion + "\n=======================");
    }

    public static void error(String msg) {
        LOGGER.severe(msg);
    }

    public static void advertencia(String msg) {
        LOGGER.warning(msg);
    }

    public static void info(String msg) {
        LOGGER.info(msg);
    }
}
