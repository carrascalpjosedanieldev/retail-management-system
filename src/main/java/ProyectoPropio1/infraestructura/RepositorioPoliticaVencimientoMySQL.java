package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.PoliticaVencimiento;
import ProyectoPropio1.dominio.puertos.RepositorioPoliticaVencimiento;
import ProyectoPropio1.excepciones.PoliticaVencimientoNoEncontradaException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPoliticaVencimientoMySQL implements RepositorioPoliticaVencimiento {

    @Override
    public PoliticaVencimiento insertarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento) {
        String sql = "INSERT INTO politicas_vencimiento (nombre_politica, dias_umbral, porcentaje_descuento) VALUES (?, ?, ?)";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, politicaVencimiento.getNombre());
            pstmt.setInt(2, politicaVencimiento.getDiasUmbral());
            pstmt.setBigDecimal(3, politicaVencimiento.getPorcentajeDescuento());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("La Inserción falló: Ninguna fila fue afectada en la Base de Datos.");
            }

            try (ResultSet gk = pstmt.getGeneratedKeys()) {
                if (gk.next()) {
                    int idReal = gk.getInt(1);
                    return PoliticaVencimiento.reconstruirDesdeBD(idReal, politicaVencimiento.getNombre(),
                            politicaVencimiento.getDiasUmbral(), politicaVencimiento.getPorcentajeDescuento(),
                            politicaVencimiento.isActiva());
                } else {
                    throw new RuntimeException("La Inserción fue Exitosa, pero no se pudo obtener el ID autogenerado.");
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Ya existe una Politica de Vencimiento registrado con el nombre: " +
                        politicaVencimiento.getNombre());
            }
            throw new RuntimeException("Error crítico de persistencia al guardar la Politica de Vencimiento: " +
                    e.getMessage(), e);
        }
    }

    @Override
    public PoliticaVencimiento obtenerPoliticaVencimiento(int idPoliticaVencimiento) {
        if (idPoliticaVencimiento <= 0){
            throw new IllegalArgumentException("El ID a buscar debe ser positivo");
        }
        String sql = "SELECT id_politica, nombre_politica, dias_umbral, porcentaje_descuento, activa " +
                "FROM politicas_vencimiento " +
                "WHERE id_politica = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

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

                throw new PoliticaVencimientoNoEncontradaException("No existe una Politica de Vencimiento con el " +
                        "ID: " + idPoliticaVencimiento);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al obtener la Politica de Vencimiento", e);
        }

    }

    @Override
    public void actualizarPoliticaVencimiento(PoliticaVencimiento politicaVencimiento) {
        String sql = "UPDATE politicas_vencimiento " +
                "SET nombre_politica = ?, dias_umbral = ?, porcentaje_descuento = ?, activa = ? " +
                "WHERE id_politica = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, politicaVencimiento.getNombre());
            pstmt.setInt(2, politicaVencimiento.getDiasUmbral());
            pstmt.setBigDecimal(3, politicaVencimiento.getPorcentajeDescuento());
            pstmt.setBoolean(4, politicaVencimiento.isActiva());
            pstmt.setInt(5, politicaVencimiento.getIdPolitica());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new PoliticaVencimientoNoEncontradaException("No se pudo actualizar: La Politica de " +
                        " Vencimiento con ID -" + politicaVencimiento.getIdPolitica() + "- no existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al actualizar la Politica de Vencimiento", e);
        }

    }

    @Override
    public List<PoliticaVencimiento> obtenerPoliticasVencimientoActivas() {
        List<PoliticaVencimiento> politicasVencimiento = new ArrayList<>();

        String sql = "SELECT id_politica, nombre_politica, dias_umbral, porcentaje_descuento, activa " +
                "FROM politicas_vencimiento WHERE activa = true";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
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
            throw new RuntimeException("Error al listar las Politicas de Vencimiento Activas", e);
        }
        return politicasVencimiento;

    }

    @Override
    public List<PoliticaVencimiento> obtenerPoliticasVencimientoInactivas() {
        List<PoliticaVencimiento> politicasVencimiento = new ArrayList<>();

        String sql = "SELECT id_politica, nombre_politica, dias_umbral, porcentaje_descuento, activa " +
                "FROM politicas_vencimiento WHERE activa = false";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
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
            throw new RuntimeException("Error al listar las Politicas de Vencimiento Inactivas", e);
        }
        return politicasVencimiento;
    }
}
