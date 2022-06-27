import analizador.*;
import basedatos.ConsultasBD;
import logger.Log;
import parametros.ParamsEjecucion;

import java.io.*;
import java.sql.SQLException;
import java.util.stream.Stream;

public class RecolectorPDF {

    private static EjecutorAnalisis ejecutorAnalisis;

    public static void analizarPDFs(File sitemap, String[] tiposAnalizadores) throws IOException, InterruptedException, SQLException {
        ejecutorAnalisis = new EjecutorAnalisis(tiposAnalizadores);
        BufferedReader sitemapAbierto = abreSitemap(sitemap);

        try {
            ConsultasBD.inicializarPropiedades(); // Importante que esta función se llame antes de ejecutar los hilos de análisis.
            ConsultasBD bd = new ConsultasBD();
            ParamsEjecucion.fechaHoraInicio = bd.insertUrlRaiz(ParamsEjecucion.getUrlWeb());
            bd.finalizar();
        } catch (SQLException ex) {
            System.out.println("Ha ocurrido un error al intentar insertar el url raiz en la base de datos.");
            Log.error(ex);
            throw ex;
        }

        recorrerYAnalizar(sitemapAbierto);
    }

    private static void recorrerYAnalizar(BufferedReader sitemap) throws InterruptedException, IOException {

        Log.LOGGER.info("Comenzado los análisis de los pdf.");

        Stream<String> lineasSitemap = sitemap.lines();
        for (String linea : lineasSitemap.toList()) {

            String[] lineaConURL = linea.split("<loc>|</loc>");

            if (lineaConURL.length > 1 && lineaConURL[1].endsWith(".pdf"))
            {
                String pdfurl = lineaConURL[1];
                Log.LOGGER.info("Analizando " + pdfurl);
                try {
                    ejecutorAnalisis.analizar(pdfurl);

                } catch (IOException ex) {
                    System.out.println("Ha ocurrido un problema al intentar analizar los PDF.");
                    Log.error(ex);
                    throw ex;
                }
            }
        }

        try {
            ejecutorAnalisis.finalizar();
        } catch (InterruptedException ex) {
            System.out.println("Ha ocurrido un error al intentar detener los hilos.");
            Log.error(ex);
            throw ex;
        }
        Log.LOGGER.info("Fin de los análisis.");
    }

    private static BufferedReader abreSitemap(File sitemap) throws FileNotFoundException {
        BufferedReader lector;
        try {
            lector = new BufferedReader(new FileReader(sitemap));
        } catch (FileNotFoundException ex) {
            System.out.println("No se ha encontrado el fichero sitemap.");
            Log.error(ex);
            throw ex;
        }
        return lector;
    }
}
