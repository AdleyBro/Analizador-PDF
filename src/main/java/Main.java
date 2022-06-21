import parametros.ParamsEjecucion;
import logger.Log;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        try {
            Log.inicializar();
        } catch (IOException e) {
            System.out.println("Error al inicializar el Logger.");
            Log.error(e);
            return;
        }

        try {
            ParamsEjecucion.getParametrosEjecucion(args);
        } catch (MalformedURLException | ParseException e) {
            return;
        }

        File sitemap;
        try
        {
            sitemap = GeneradorSitemap.generarSitemap(ParamsEjecucion.getUrlWeb(), ParamsEjecucion.getNombreWeb());

        } catch (IOException e) {
            Log.error(new IOException("Ha ocurrido un error al intentar leer o generar el sitemap."));
            return;
        } catch (InterruptedException e) {
            Log.error(new InterruptedException("Se ha interrumpido el proceso de generación del sitemap."));
            return;
        }

        try {
            RecolectorPDF.analizarPDFs(sitemap, ParamsEjecucion.getTiposAnalizadores());

        } catch (InterruptedException | IOException e) {
            return;
        }

        try {
            Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe /T");
        } catch (IOException e) {
            System.out.println("Advertencia: no se ha podido ejecutar la terminación de procesos geckodriver.");
            Log.warn(e);
        }
    }
}
