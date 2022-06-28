package basedatos;

import logger.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
    protected static final String tablaPDF = "pdf";

    public ConsultasBD() throws SQLException {
        conectar();
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
     * Inserta la url raiz en la base de datos y devuelve el timestamp de su insercion.
     * @param url
     * @return timestamp de insercion
     * @throws SQLException
     */
    public Timestamp insertUrlRaiz(String url) throws SQLException {
        Timestamp timestamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        String consulta = String.format("INSERT INTO %s VALUES (?, ?)", tablaUrlRaiz);
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setTimestamp(1, timestamp);
        sentencia.setString(2, url);
        boolean res = sentencia.execute();
        Log.LOGGER.info("BD consulta: " + sentencia);
        Log.LOGGER.info("BD introducido?: " + res);
        return timestamp;
    }

    /**
     *
     * @param pdfurl
     * @param fecha_hora
     * @param url_raiz
     * @return ID del PDF introducido en la base de datos
     * @throws SQLException
     */
    public int insertPDF(String pdfurl, Timestamp fecha_hora, String url_raiz) throws SQLException {
        String consulta = String.format("INSERT INTO %s(url, fecha_hora_url_raiz, url_url_raiz) VALUES (?, ?, ?) RETURNING id", tablaPDF);
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, pdfurl);
        sentencia.setTimestamp(2, fecha_hora);
        sentencia.setString(3, url_raiz);
        ResultSet rs = sentencia.executeQuery();
        Log.LOGGER.info("BD consulta: " + sentencia);
        Log.LOGGER.info("BD respuesta: " + rs);

        if (rs.next())
            return rs.getInt(1);
        else
            return -1;
    }

    public void finalizar() throws SQLException {
        conexion.close();
    }

    public void setAutocommit(boolean modo) throws SQLException {
        conexion.setAutoCommit(modo);
    }

    public void commit() throws SQLException {
        conexion.commit();
    }

    public void rollback() throws SQLException {
        conexion.rollback();
    }

    private void conectar() throws SQLException {
        String peticionConexion = String.format("jdbc:postgresql://%s:%s/%s", ip, puerto, bd);
        try {
            conexion = DriverManager.getConnection(peticionConexion, usuario, contra);
        } catch (SQLException ex) {
            System.out.println("No se ha podido realizar la conexión a la base de datos. ¿Los datos de 'bd.properties' son correctos?");
            Log.error(ex);
            throw ex;
        }
    }
}
