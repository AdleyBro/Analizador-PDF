package basedatos;

import logger.Log;
import parametros.ParamsEjecucion;

import java.io.IOException;
import java.sql.*;

public class ConsultasBDTingtun extends ConsultasBD {

    private static final String tablaPropiedades = "props_tingtun";


    public ConsultasBDTingtun() throws SQLException {
        super();
        /*
        Statement sentencia = conexion.createStatement();
        String nombre="pepe";
        String consulta = "select * from Persona where nombre='"+nombre+"'";
        ResultSet rs=sentencia.executeQuery(consulta);
        */
    }

    public void insertResultado(String resultado, String tituloPropiedad, String urlpdf) throws SQLException {
        int idPropiedad;
        try {
            idPropiedad = getIdPropiedad(tituloPropiedad);
            insertUrlRaiz(ParamsEjecucion.getUrlWeb());
        } catch (SQLException ex) {
            System.out.println("Ha ocurrido un error al intentar realizar consultas con la base de datos");
            Log.error(ex);
            throw ex;
        }
    }

    private int getIdPropiedad(String titulo) throws SQLException {
        String consulta = "SELECT id_propiedad FROM ? WHERE titulo = '?'";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, tablaPropiedades);
        sentencia.setString(2, titulo);
        ResultSet rs = sentencia.executeQuery();
        Log.LOGGER.info("BD con: " + sentencia);
        Log.LOGGER.info("BD res: " + rs);
        return rs.getInt(0);
    }


}
