import analizador.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RecolectorPDF {
    //private static final HashMap<Character, Analizador> tablaAnalizadores = new HashMap<>();
    //private static Analizador analizador = null;

    private static EjecutorAnalisis ejecutorAnalisis;

    public static void analizarSitemap(File sitemap, String[] args) throws FileNotFoundException {
        ejecutorAnalisis = new EjecutorAnalisis(args);
        recorrerYAnalizar(sitemap);
    }

    private static void recorrerYAnalizar(File sitemap) throws FileNotFoundException {
        BufferedReader lector = new BufferedReader(new FileReader(sitemap));

        lector.lines().forEach((String linea) -> {
            String[] elems = linea.split("<loc>|</loc>");
            if (elems.length > 1)
            {
                String pdfurl = elems[1];
                ejecutorAnalisis.analizar(pdfurl);
            }
        });
    }
}
