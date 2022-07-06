package logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        Date date = new Date(System.currentTimeMillis());
        String tituloLog = "analisis_" + dateFormatter.format(date) + ".log";

        // Se crea la carpeta logs, en caso de que no exista.
        Path dir = Paths.get("logs/");
        if (!Files.exists(dir))
            Files.createDirectory(dir);

        FileHandler ficheroLog = new FileHandler(dir + "/" + tituloLog);
        //ConsoleHandler consoleHandler = new ConsoleHandler();

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
