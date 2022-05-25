package logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String level = record.getLevel().getName();
        if (level.equals("SEVERE"))
            level = "ERROR";

        StringBuilder buf = new StringBuilder(1000);
        buf.append("\n[").append(calcHora(record.getMillis())).append("] ");
        buf.append("[").append(level).append("] ");

        if (record.getThrown() != null)
            buf.append("\n").append(stackTraceToString(record.getThrown()));
        else {
            buf.append("[").append(record.getSourceClassName()).append("/").append(record.getSourceMethodName()).append("] ");
            buf.append(formatMessage(record));
        }

        return buf.toString();
    }

    private String calcHora(long ms) {
        SimpleDateFormat formato_hora = new SimpleDateFormat("HH:mm:ss");
        return formato_hora.format(new Date(ms));
    }

    private String stackTraceToString(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
