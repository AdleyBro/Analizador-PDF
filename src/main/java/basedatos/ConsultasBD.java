package basedatos;

import logger.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConsultasBD {

    protected Connection conexion;

    protected static String usuario;
    protected static String contra;
    protected static String bd;
    protected static String ip;
    protected static String puerto;

    protected static final String tablaUrlRaiz = "url_raiz";

    public ConsultasBD() throws SQLException {
        String peticionConexion = String.format("jdbc:postgresql://%s:%s/%s", ip, puerto, bd);
        try {
            conexion = DriverManager.getConnection(peticionConexion, usuario, contra);
        } catch (SQLException ex) {
            System.out.println("No se ha podido realizar la conexión a la base de datos. ¿Los datos de 'bd.properties' son correctos?");
            Log.error(ex);
            throw ex;
        }
    }

    /**
     * Esta función debe llamarse una única vez antes de comenzar a ejecutar los análisis de PDF.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void inicializarPropiedades() throws FileNotFoundException, IOException {

        InputStream ficheroProps;
        try {
            ficheroProps = new FileInputStream("./db.properties");
        } catch (FileNotFoundException ex) {
            System.out.println("No se ha podido encontrar el fichero 'db.properties', necesario para la conexion con la base de datos.");
            Log.error(ex);
            throw ex;
        }

        Properties propiedades = new Properties();
        try {
            propiedades.load(ficheroProps);
        } catch (IOException ex) {
            System.out.println("Ha ocurrido un error al intentar leer el fichero 'db.properties'.");
            Log.error(ex);
            throw ex;
        }

        usuario = propiedades.getProperty("usuario");
        contra = propiedades.getProperty("contra");
        bd = propiedades.getProperty("bd");
        ip = propiedades.getProperty("ip");
        puerto = propiedades.getProperty("puerto");
    }

    /**
     * Inserta la url raiz en la base de datos y devuelve un array con sus PK (timestamp actual y su url).
     * @param url
     * @return
     * @throws SQLException
     */
    public Timestamp insertUrlRaiz(String url) throws SQLException {
        Timestamp timestamp = Timestamp.from(Instant.now());
        String consulta = "INSERT INTO ? VALUES (?, ?)";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, tablaUrlRaiz);
        sentencia.setTimestamp(2, timestamp);
        sentencia.setString(3, url);
        ResultSet rs = sentencia.executeQuery();
        Log.LOGGER.info("BD con: " + sentencia);
        Log.LOGGER.info("BD res: " + rs);
        return timestamp;
    }

    public void insertPDF(String pdfurl, Timestamp fecha_hora, String url_raiz) throws SQLException {
        String consulta = "INSERT INTO pdf VALUES (?, ?, ?)";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, pdfurl);
        sentencia.setTimestamp(2, fecha_hora);
        sentencia.setString(3, url_raiz);
        ResultSet rs = sentencia.executeQuery();
        Log.LOGGER.info("BD con: " + sentencia);
        Log.LOGGER.info("BD res: " + rs);
    }

    public void finalizar() throws SQLException {
        conexion.close();
    }
}
