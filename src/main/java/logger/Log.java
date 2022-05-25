package logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Log {

    public static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void inicializar() throws IOException {
        LOGGER.setLevel(Level.INFO);

        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler)
            rootLogger.removeHandler(handlers[0]);

        FileHandler ficheroLog = new FileHandler("ultimo_log.log");
        ConsoleHandler consoleHandler = new ConsoleHandler();

        CustomFormatter formatter = new CustomFormatter();
        ficheroLog.setFormatter(formatter);
        LOGGER.addHandler(ficheroLog);
    }

    public static void error(Throwable ex) {
        LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
    }

    public static void warn(Throwable ex) {
        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
    }

    private static String calcHora(long ms) {
        SimpleDateFormat formato_hora = new SimpleDateFormat("HH:mm:ss");
        return formato_hora.format(new Date(ms));
    }
}
