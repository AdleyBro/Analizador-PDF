import analizador.*;
import basedatos.ConsultasBD;
import logger.Log;
import parametros.ParamsEjecucion;

import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.stream.Stream;

public class RecolectorPDF {

    private static EjecutorAnalisis ejecutorAnalisis;
    private static ConsultasBD bd;

    private static Timestamp timestamp;

    public static void analizarPDFs(File sitemap, String[] tiposAnalizadores) throws IOException, InterruptedException, SQLException {
        ejecutorAnalisis = new EjecutorAnalisis(tiposAnalizadores);
        BufferedReader sitemapAbierto = abreSitemap(sitemap);

        try {
            ConsultasBD.inicializarPropiedades(); // Importante que esta función se llame antes de ejecutar los hilos de análisis.
            bd = new ConsultasBD(true);
            timestamp = bd.insertUrlRaiz(ParamsEjecucion.getUrlWeb());
        } catch (SQLException ex) {
            System.out.println("Ha ocurrido un error al intentar insertar el url raiz en la base de datos.");
            Log.error(ex);
            throw ex;
        }

        recorrerYAnalizar(sitemapAbierto);

        try {
            bd.finalizar();
        } catch (SQLException ex) {
            System.out.println("Ha ocurrido un error al intentar finalizar la conexión con la base de datos.");
            Log.error(ex);
            throw ex;
        }
    }

    private static void recorrerYAnalizar(BufferedReader sitemap) throws InterruptedException, IOException, SQLException {

        System.out.println("Comenzado los análisis de los pdf.");
        Log.LOGGER.info("Comenzado los análisis de los pdf.");

        int contadorPDFAnalizados = 0;
        Stream<String> lineasSitemap = sitemap.lines();
        for (String linea : lineasSitemap.toList()) {

            String[] lineaConURL = linea.split("<loc>|</loc>");

            if (lineaConURL.length > 1 && lineaConURL[1].endsWith(".pdf"))
            {
                String pdfurl = lineaConURL[1];

                try {
                    int pdfId = bd.insertPDF(pdfurl, timestamp, ParamsEjecucion.getUrlWeb());
                    ejecutorAnalisis.analizar(pdfurl, pdfId);
                    contadorPDFAnalizados++;

                } catch (IOException ex) {
                    System.out.println("Ha ocurrido un problema al intentar analizar los PDF.");
                    Log.error(ex);
                    throw ex;
                } catch (SQLException ex) {
                    System.out.println("Error al intentar insertar un nuevo PDF a la base de datos.");
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

        String msg = "\n>>> Fin de los análisis. PDF analizados con éxito: %d <<<".formatted(contadorPDFAnalizados);
        System.out.println(msg);
        Log.LOGGER.info(msg);
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
