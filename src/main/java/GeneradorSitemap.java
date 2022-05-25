
import logger.Log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class GeneradorSitemap {
    private static final ProcessBuilder processBuilder = new ProcessBuilder();

    public static File generarSitemap(String url, String nombre) throws IOException, InterruptedException {
        File sitemap = new File("sitemap_" + nombre + ".xml");
        if (sitemap.exists()) {
            String infoMsg = "Se utilizará un sitemap existente para el sitio web " + nombre;
            Log.LOGGER.info(infoMsg);
            System.out.println(infoMsg);
        } else {
            String[] comando = {"node", "sitemapjs/sitemap_generator.js", url, nombre};

            processBuilder.command(comando);    // Indicamos, como comando, la ejecución del script generador del sitemap
            processBuilder.inheritIO();         // Mostramos el output que proporcione el script en nuestra terminal

            String infoMsg = "Generando sitemap para el sitio web " + nombre + "...";
            Log.LOGGER.info(infoMsg);
            System.out.println(infoMsg);
            processBuilder.start().waitFor();   // Ejecutamos y esperamos a que finalice el proceso

            sitemap = new File("sitemap_" + nombre + ".xml");
            String infoMsg2 = "¡Sitemap generado con éxito!";
            Log.LOGGER.info(infoMsg2);
            System.out.println(infoMsg2);
        }
        return sitemap;
    }
}
