package basedatos;

import logger.Log;
import parametros.ParamsEjecucion;

import java.io.IOException;
import java.sql.*;

public class ConsultasBDTingtun extends ConsultasBD {

    private static final String tablaPropiedades = "props_tingtun";
    private static final String tablaResultados = "res_tingtun";
    public int idPDF;


    public ConsultasBDTingtun(boolean autoCommit) throws SQLException {
        super();
        setAutocommit(autoCommit);
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

            Log.LOGGER.info("PDF " + idpdf + " " + resultado + " - Propiedad " + idPropiedad);
        } catch (SQLException ex) {
            System.out.println("Ha ocurrido un error al intentar insertar un resultado de análisis en la base de datos. ID de PDF: " + idpdf);
            Log.error(ex);
            throw ex;
        }
    }

    private int getIdPropiedad(String titulo) throws SQLException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int idPropiedad;
        try {
            String consulta = String.format("SELECT id_propiedad FROM %s WHERE titulo = ?", tablaPropiedades);
            ps = conexion.prepareStatement(consulta);
            ps.setString(1, titulo);
            rs = ps.executeQuery();

            if (rs.next())
                idPropiedad = rs.getInt(1);
            else
                throw new SQLException();

        } catch (SQLException ex) {
            System.out.println("Ha ocurrido un error en la comunicación con la base de datos.");
            Log.error(ex);
            throw ex;
        } finally {
            try { rs.close(); } catch (Exception ex) {}
            try { ps.close(); } catch (Exception ex) {}
        }

        return idPropiedad;
    }
}
