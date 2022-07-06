
import logger.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class GeneradorSitemap {
    private static final ProcessBuilder processBuilder = new ProcessBuilder();

    public static File generarSitemap(String url, String nombre) throws IOException, InterruptedException {

        String rutaSitemap = "sitemaps/sitemap_" + nombre + ".xml";
        File sitemap = new File(rutaSitemap);
        if (sitemap.exists() && !sitemap.isDirectory()) {
            String infoMsg = "Se utilizará un sitemap existente para el sitio web '" + nombre + "' con URL: " + url;
            Log.LOGGER.info(infoMsg);
            System.out.println(infoMsg);
        } else {
            String[] comando = {"node", "tools/sitemapjs/sitemap_generator.js", url, rutaSitemap};

            processBuilder.command(comando);    // Indicamos, como comando, la ejecución del script generador del sitemap
            processBuilder.inheritIO();         // Mostramos el output que proporcione el script en nuestra terminal

            String infoMsg = "Generando sitemap para el sitio web '" + nombre + "' con URL: " + url;
            Log.LOGGER.info(infoMsg);
            System.out.println(infoMsg);

            processBuilder.start().waitFor();   // Ejecutamos y esperamos a que finalice el proceso

            sitemap = new File(rutaSitemap);

            String infoMsg2 = "¡Sitemap " + sitemap.getName() + " generado con éxito!";
            Log.LOGGER.info(infoMsg2);
            System.out.println(infoMsg2);
        }
        return sitemap;
    }

    public static boolean existeGenerador() {
        String rutaGenerador = "./tools/sitemapjs/sitemap_generator.js";
        File fichero = new File(rutaGenerador);
        return fichero.exists() && !fichero.isDirectory();
    }
}
