package basedatos;

import logger.Log;
import parametros.ParamsEjecucion;

import java.io.IOException;
import java.sql.*;

public class ConsultasBDTingtun extends ConsultasBD {

    private static final String tablaPropiedades = "props_tingtun";
    private static final String tablaResultados = "res_tingtun";
    public final int idPDF;


    public ConsultasBDTingtun(String pdfurl, Timestamp fecha_hora, String url_raiz) throws SQLException {
        super();
        idPDF = insertPDF(pdfurl, fecha_hora, url_raiz);
    }

    public void insertResultado(String tituloPropiedad, String resultado, int idpdf) throws SQLException {
        int idPropiedad;
        try {
            idPropiedad = getIdPropiedad(tituloPropiedad);
            String consulta = String.format("INSERT INTO %s VALUES (?, ?, ?)", tablaResultados);
            PreparedStatement sentencia = conexion.prepareStatement(consulta);
            sentencia.setString(1, resultado);
            sentencia.setInt(2, idpdf);
            sentencia.setInt(3, idPropiedad);
            boolean res = sentencia.execute();
            Log.LOGGER.info("BD consulta: " + sentencia);
            Log.LOGGER.info("BD introducido?: " + res);
        } catch (SQLException ex) {
            System.out.println("Ha ocurrido un error al intentar realizar consultas con la base de datos");
            Log.error(ex);
            throw ex;
        }
    }

    private int getIdPropiedad(String titulo) throws SQLException {
        String consulta = "SELECT id_propiedad FROM ? WHERE titulo = ?";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, tablaPropiedades);
        sentencia.setString(2, titulo);
        ResultSet rs = sentencia.executeQuery();
        Log.LOGGER.info("BD consulta: " + sentencia);
        Log.LOGGER.info("BD respuesta: " + rs);
        return rs.getInt(0);
    }
}
