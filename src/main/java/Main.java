import analizador.AnalizadorTingtun;
import basedatos.ConsultasBD;
import parametros.ParamsEjecucion;
import logger.Log;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        if (args.length < 2) {
            ParamsEjecucion.imprimeAyuda();
            return;
        }

        if (!GeneradorSitemap.existeGenerador()) {
            System.out.println("ERROR: El programa generador de sitemaps no se encuentra en la ruta './tools/sitemapjs/sitemap_generator.js'");
            return;
        }

        if (!AnalizadorTingtun.existeDriverWeb()) {
            System.out.println("ERROR: El driver web, necesario para acceder al analizador web, no se encuentra en la ruta './tools/drivers/geckodriver.exe'");
            return;
        }

        if (!ConsultasBD.existeFicheroPropiedades()) {
            System.out.println("ERROR: El fichero de propiedades para la conexión con la base de datos no se encuentra en la ruta './bd.properties'");
            return;
        }

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

        } catch (InterruptedException | IOException  | SQLException e) {
            return;
        }

        /*
        try {
            Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe /T");
        } catch (IOException e) {
            System.out.println("Advertencia: no se ha podido ejecutar la terminación de procesos geckodriver.");
            Log.warn(e);
        }*/
    }
}
