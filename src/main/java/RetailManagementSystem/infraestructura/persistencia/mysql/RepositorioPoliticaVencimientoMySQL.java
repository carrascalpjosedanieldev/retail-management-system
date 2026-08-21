package RetailManagementSystem.infraestructura.persistencia.mysql;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.puertos.RepositorioPoliticaVencimiento;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.PoliticaVencimientoNoEncontradaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IdAutogeneradoNoRecibidoException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.IncersionFallidaException;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPoliticaVencimientoMySQL implements RepositorioPoliticaVencimiento {

    //CREATE:

    private static final String SQL_INSERTAR_POLITICA_V =
            "INSERT INTO politicas_vencimiento (nombre_politica, dias_umbral, porcentaje_descuento) VALUES (?, ?, ?)";

    @Override
    public PoliticaVencimiento insertarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERTAR_POLITICA_V, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, politicaVencimiento.getNombre());
            pstmt.setInt(2, politicaVencimiento.getDiasUmbral());
            pstmt.setBigDecimal(3, politicaVencimiento.getPorcentajeDescuento());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IncersionFallidaException("La Inserción falló: Ninguna fila fue afectada en la Base de Datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt(1);
                    return PoliticaVencimiento.reconstruirDesdeBD(idReal, politicaVencimiento.getNombre(),
                            politicaVencimiento.getDiasUmbral(), politicaVencimiento.getPorcentajeDescuento(),
                            politicaVencimiento.isActiva());
                } else {
                    throw new IdAutogeneradoNoRecibidoException("La Inserción fue Exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Ya existe una Política de Vencimiento registrado con el nombre: " +
                        politicaVencimiento.getNombre());
            }
            throw new PersistenciaException("Error crítico de persistencia al guardar la Política de Vencimiento: " +
                    e.getMessage(), e);
        }
    }


    //READ:

    private static final String SQL_OBTENER_POLITICA_V =
            "SELECT id_politica, nombre_politica, dias_umbral, porcentaje_descuento, activa " +
            "FROM politicas_vencimiento " +
            "WHERE id_politica = ?";

    @Override
    public PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento) {
        if (idPoliticaVencimiento <= 0){
            throw new IllegalArgumentException("El ID a buscar debe ser positivo");
        }
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_POLITICA_V)){

            pstmt.setInt(1, idPoliticaVencimiento);

            try (ResultSet rs = pstmt.executeQuery()){

                if (rs.next()) {
                    int idReal = rs.getInt("id_politica");
                    String nombre = rs.getString("nombre_politica");
                    int diasUmbral = rs.getInt("dias_umbral");
                    BigDecimal porcentaje = rs.getBigDecimal("porcentaje_descuento");
                    boolean activo = rs.getBoolean("activa");

                    return PoliticaVencimiento.reconstruirDesdeBD(idReal, nombre, diasUmbral, porcentaje, activo);
                }

                throw new PoliticaVencimientoNoEncontradaException("No existe una Política de Vencimiento con el " +
                        "ID: " + idPoliticaVencimiento);

            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al obtener la Política de Vencimiento", e);
        }
    }


    private static final String SQL_OBTENER_POLITICAS_V_ACTIVAS =
            "SELECT id_politica, nombre_politica, dias_umbral, porcentaje_descuento, activa " +
            "FROM politicas_vencimiento WHERE activa = true";

    @Override
    public List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas() {
        List<PoliticaVencimiento> politicasVencimiento = new ArrayList<>();
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_POLITICAS_V_ACTIVAS);
             ResultSet rs = pstmt.executeQuery();){

            while (rs.next()){
                PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(
                        rs.getInt("id_politica"),
                        rs.getString("nombre_politica"),
                        rs.getInt("dias_umbral"),
                        rs.getBigDecimal("porcentaje_descuento"),
                        rs.getBoolean("activa")
                );
                politicasVencimiento.add(politicaVencimiento);
            }

        } catch (SQLException e){
            throw new PersistenciaException("Error al listar las Políticas de Vencimiento Activas", e);
        }
        return politicasVencimiento;
    }


    private static final String SQL_OBTENER_TODAS_LAS_POLITICAS_V =
            "SELECT id_politica, nombre_politica, dias_umbral, porcentaje_descuento, activa " +
            "FROM politicas_vencimiento ORDER BY activa DESC, id_politica ASC";

    @Override
    public List<PoliticaVencimiento> obtenerTodasLasPoliticasDeVencimiento() {
        List<PoliticaVencimiento> politicasVencimiento = new ArrayList<>();

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_OBTENER_TODAS_LAS_POLITICAS_V);
             ResultSet rs = pstmt.executeQuery();){

            while (rs.next()){
                PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(
                        rs.getInt("id_politica"),
                        rs.getString("nombre_politica"),
                        rs.getInt("dias_umbral"),
                        rs.getBigDecimal("porcentaje_descuento"),
                        rs.getBoolean("activa")
                );
                politicasVencimiento.add(politicaVencimiento);
            }

        } catch (SQLException e){
            throw new PersistenciaException("Error al listar las Políticas de Vencimiento Inactivas", e);
        }
        return politicasVencimiento;
    }


    //UPDATE:

    private static final String SQL_ACTUALIZAR_POLITICA_V =
            "UPDATE politicas_vencimiento " +
            "SET nombre_politica = ?, dias_umbral = ?, porcentaje_descuento = ?, activa = ? " +
            "WHERE id_politica = ?";

    @Override
    public void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento) {
        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(SQL_ACTUALIZAR_POLITICA_V)){

            pstmt.setString(1, politicaVencimiento.getNombre());
            pstmt.setInt(2, politicaVencimiento.getDiasUmbral());
            pstmt.setBigDecimal(3, politicaVencimiento.getPorcentajeDescuento());
            pstmt.setBoolean(4, politicaVencimiento.isActiva());
            pstmt.setInt(5, politicaVencimiento.getIdPolitica());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new PoliticaVencimientoNoEncontradaException("No se pudo actualizar: La Política de " +
                        " Vencimiento con ID -" + politicaVencimiento.getIdPolitica() + "- no existe.");
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error de base de datos al actualizar la Política de Vencimiento", e);
        }

    }


}//===================================================================================================================//

