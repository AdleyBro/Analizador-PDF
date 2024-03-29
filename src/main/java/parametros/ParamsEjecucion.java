package parametros;

import analizador.ConstructorAnalizador;
import logger.Log;
import org.apache.commons.cli.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

public class ParamsEjecucion {

    private static String urlWeb;
    private static String nombreWeb;
    private static String[] tiposAnalizadores;
    private static int numHilos;
    private static int retardo;
    private static boolean debugMode;

    private static final Options options = inicializarOptions();
    private static final CommandLineParser parser = new DefaultParser();

    public static void getParametrosEjecucion(String[] args) throws MalformedURLException, ParseException, IllegalArgumentException {
        urlWeb = parseUrlWeb(args[0]);
        nombreWeb = parseNombreWeb(args[1]);

        if (args.length == 2)
            analizarParametros(new String[] {});
        else
            analizarParametros(Arrays.copyOfRange(args, 2, args.length));
    }

    private static String parseUrlWeb(String url) throws MalformedURLException {
        try {
            new URL(url);
        } catch (MalformedURLException ex) {
            String msg = "La url de la web introducida no es válida.";
            System.out.println(msg);
            throw new MalformedURLException(msg);
        }
        return url;
    }

    private static String parseNombreWeb(String parametro) throws IllegalArgumentException {
        if (parametro.matches(".*[<>:\"/\\|?*].*")) {
            String msg = "El nombre de la web contiene caracteres especiales inválidos: <>:\"/\\|?*";
            System.out.println(msg);
            throw new IllegalArgumentException(msg);
        }

        return parametro.toLowerCase(Locale.ROOT);
    }

    private static Options inicializarOptions() {
        Options options = new Options();
        Option opcionAnalizadores = new Option("a", "analizadores", true,
                "Tipos de analizadores que se querran utilizar.\nValor predeterminado = "
                        + ConstructorAnalizador.getAnalizadorPredet() + "\nValores posibles = " + ConstructorAnalizador.getTiposValidos());
        opcionAnalizadores.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(opcionAnalizadores);
        options.addOption("n", "numhilos", true, "Numero de hilos.\nValor predeterminado = numero de nucleos de la maquina");
        options.addOption("r", "retardo", true, "Retardo en milisegundos entre cada analisis.\nValor predeterminado = 0");
        options.addOption("d", false, "Modo depuracion.\nValor predeterminado = false");
        return options;
    }

    /**
     * Analiza el resto de los argumentos de entrada (saltándose los dos primeros, correspondientes a las url y nombre web),
     * para obtener los parámetros introducidos con la forma "-x val1 val2 ...", donde "x" indica el tipo de parámetro
     * y los "val" los valores correspondientes al parámetro (ej: -a tingtun pave).
     * @param args argumentos de entrada del programa.
     * @throws ParseException Se lanza cuando algún parámetro es inexistente o tiene valores inválidos.
     */
    private static void analizarParametros(String[] args) throws ParseException {

        CommandLine comando = parser.parse(options, args);
        if (comando.hasOption("a"))
            parseAnalizadores(comando);
        else
            tiposAnalizadores = new String[] { ConstructorAnalizador.getAnalizadorPredet() };

        if (comando.hasOption("n"))
            parseNumhilos(comando);
        else
            numHilos = Runtime.getRuntime().availableProcessors() * 2;

        if (comando.hasOption("r"))
            parseRetardo(comando);
        else
            retardo = 0;

        debugMode = comando.hasOption("d");
    }

    private static void parseAnalizadores(CommandLine comando) throws ParseException {
        tiposAnalizadores = comando.getOptionValues("a");

        if (!sonAnalizadoresValidos(tiposAnalizadores)) {
            System.out.println("Alguno/s de los analizadores indicados " + Arrays.stream(tiposAnalizadores).toList() + " no existe/n. Los analizadores disponiblesson: " + ConstructorAnalizador.getTiposValidos());
            throw new ParseException("Alguno/s de los analizadores introducidos no existe.");
        }
    }

    private static void parseNumhilos(CommandLine comando) throws NumberFormatException, ParseException {
        try {
            numHilos = Integer.parseInt(comando.getOptionValue("n"));
        } catch (NumberFormatException ex) {
            System.out.println("El número de hilos introducido no es un valor válido.");
            Log.error(ex);
            throw ex;
        }

        if (numHilos < 1) {
            System.out.println("La cantidad de hilos no puede ser nula o negativa, comprueba los parámetros de entrada.");
            throw new ParseException("Se ha indicado un número inválido de hilos en los parámetros de entrada.");
        }
    }

    private static void parseRetardo(CommandLine comando) throws NumberFormatException, ParseException {
        try {
            retardo = Integer.parseInt(comando.getOptionValue("r"));
        } catch (NumberFormatException ex) {
            System.out.println("La cantidad de retardo introducido no es un valor válido.");
            Log.error(ex);
            throw ex;
        }

        if (retardo < 1) {
            System.out.println("La cantidad de retardo no puede ser nula o negativa, comprueba los parámetros de entrada.");
            throw new ParseException("Se ha indicado una cantidad de retardo inválida en los parámetros de entrada.");
        }
    }

    private static boolean sonAnalizadoresValidos(String[] analizadores) {
        Set<String> tiposValidos = ConstructorAnalizador.getTiposValidos();

        for (String tipo : analizadores) {
            if (!tiposValidos.contains(tipo))
                return false;
        }
        return true;
    }

    public static void imprimeAyuda() {
        System.out.println("Siempre se debe ejecutar con, al menos, estos dos argumentos de entrada:");
        System.out.println("  1. URL de página web. Ej: https://www.uah.es");
        System.out.println("  2. Nombre del fichero sitemap. Ej: uah");
        System.out.println("\nEjemplo de ejecución, usando tres hilos: 'java -jar AnalizadorPDFs.jar https://www.uah.es uah -n 3'");
        System.out.println("\nAdemás, existen los siguientes argumentos opcionales:");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Analizador PDF", options);
    }

    public static String getUrlWeb() { return urlWeb; }
    public static String getNombreWeb() { return nombreWeb; }
    public static String[] getTiposAnalizadores() { return tiposAnalizadores; }
    public static int getNumHilos() { return numHilos; }
    public static int getRetardo() { return retardo; }
}
