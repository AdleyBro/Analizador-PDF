import analizador.*;
import logger.Log;

import java.io.*;
import java.util.stream.Stream;

public class RecolectorPDF {
    //private static final HashMap<Character, Analizador> tablaAnalizadores = new HashMap<>();
    //private static Analizador analizador = null;

    private static EjecutorAnalisis ejecutorAnalisis;

    public static void analizarPDFs(File sitemap, String[] tiposAnalizadores) throws FileNotFoundException, InterruptedException {
        ejecutorAnalisis = new EjecutorAnalisis(tiposAnalizadores);
        recorrerYAnalizar(sitemap);
    }

    private static void recorrerYAnalizar(File sitemap) throws FileNotFoundException, InterruptedException {
        BufferedReader lector = new BufferedReader(new FileReader(sitemap));

        Log.LOGGER.info("Comenzado los análisis de los pdf.");
        Stream<String> lineasSitemap = lector.lines();
        for (String linea : lineasSitemap.toList()) {
            String[] lineaConURL = linea.split("<loc>|</loc>");
            if (lineaConURL.length > 1 && lineaConURL[1].endsWith(".pdf"))
            {
                String pdfurl = lineaConURL[1];
                Log.LOGGER.info("Analizando " + pdfurl);
                ejecutorAnalisis.analizar(pdfurl);
            }
        }
        ejecutorAnalisis.finalizar();
        Log.LOGGER.info("Fin de los análisis.");
    }
}
