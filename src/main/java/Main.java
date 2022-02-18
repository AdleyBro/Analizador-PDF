import logger.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try
        {
            File sitemap = GeneradorSitemap.generarSitemap("https://cepis.org/");

            RecolectorPDF.analizarSitemap(sitemap, args);

        } catch (IOException e) {
            Log.error("Ha ocurrido un error al intentar leer o generar el sitemap.", e.toString());
        } catch (InterruptedException e) {
            Log.error("Se ha interrumpido el proceso de generación del sitemap.", e.toString());
        }
    }
}
