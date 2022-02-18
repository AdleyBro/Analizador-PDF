
import java.io.File;
import java.io.IOException;

public class GeneradorSitemap {
    private static final ProcessBuilder processBuilder = new ProcessBuilder();

    public static File generarSitemap(String url) throws IOException, InterruptedException {
        File sitemap = new File("sitemap.xml");
        if (sitemap.exists())
            return sitemap;

        String[] comando = {"node", "sitemapjs/sitemap_generator.js", url};

        processBuilder.command(comando);    // Indicamos, como comando, la ejecución del script generador del sitemap
        processBuilder.inheritIO();         // Mostramos el output que proporcione el script en nuestra terminal
        processBuilder.start().waitFor();   // Ejecutamos y esperamos a que finalice el proceso

        return new File("sitemap.xml");
    }
}
