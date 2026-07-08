package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dominio.Servicio;
import ProyectoPropio1.dominio.puertos.RepositorioServicio;
import ProyectoPropio1.excepciones.ImpuestoNoEncontradoException;
import ProyectoPropio1.excepciones.ServicioNoEncontradoException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioServicioMySQL implements RepositorioServicio {

    public void insertarServicio(Servicio servicio){
        String sql = "INSERT INTO servicios (codigo_servicio, nombre, precio_base, id_impuesto) VALUES (?, ?, ?, ?)";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, servicio.getCodigo());
            pstmt.setString(2, servicio.getNombre());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());
            pstmt.setInt(4, servicio.getIdImpuesto());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("La inserción falló: Ninguna fila fue afectada en la base de datos.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) {
                throw new ImpuestoNoEncontradoException("No se puede guardar el servicio: El impuesto especificado no existe.");
            }
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Violación de integridad: Ya existe un servicio con este código o nombre.");
            }
            throw new RuntimeException("Error crítico de persistencia al guardar el servicio: " + e.getMessage(), e);
        }
    }


    @Override
    public void eliminarServicio(String codigoServicio) {
        String sql = "DELETE FROM servicios WHERE codigo_servicio = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoServicio);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ServicioNoEncontradoException("El Servicio con Codigo -" + codigoServicio + "- NO existe o ya fue eliminado antes.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al intentar eliminar el Servicio", e);
        }
    }


    @Override
    public Servicio obtenerServicio(String codigoServicio) {
        String sql = "SELECT s.codigo_servicio, s.nombre, s.precio_base, s.id_impuesto, " +
                "i.nombre AS nombre_impuesto, i.porcentaje, i.activo " +
                "FROM servicios s " +
                "INNER JOIN impuestos i ON i.id_impuesto = s.id_impuesto " +
                "WHERE s.codigo_servicio = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoServicio);

            try (ResultSet rs = pstmt.executeQuery()){

                if (rs.next()){

                    String codigo = rs.getString("codigo_servicio");
                    String  nombre = rs.getString("nombre");
                    BigDecimal precioBase = rs.getBigDecimal("precio_base");

                    int idImpuesto = rs.getInt("id_impuesto");
                    String  nombreImpuesto = rs.getString("nombre_impuesto");
                    BigDecimal porcentaje = rs.getBigDecimal("porcentaje");
                    boolean activo = rs.getBoolean("activo");

                    Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, nombreImpuesto, porcentaje, activo);

                    return new Servicio(codigo, nombre, precioBase, impuesto);

                }

                throw new ServicioNoEncontradoException("Error de negocio: El Servicio con código -" + codigoServicio + "- no existe");

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al intentar obtener el Servicio", e);
        }

    }


    @Override
    public void actualizarServicio(Servicio servicio) {
        String sql = "UPDATE servicios SET id_impuesto = ?, nombre = ?, precio_base = ? WHERE codigo_servicio = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, servicio.getIdImpuesto());
            pstmt.setString(2, servicio.getNombre());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas==0){
                throw new ServicioNoEncontradoException("NO se pudo actualizar. El Servicio con el codigo -" + servicio.getCodigo() + "- NO existe");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al intentar modificar el Servicio", e);
        }

    }


    @Override
    public List<Servicio> obtenerServicios() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT s.codigo_servicio, s.nombre, s.precio_base, s.id_impuesto, " +
                "i.nombre AS nombre_impuesto, i.porcentaje, i.activo " +
                "FROM servicios s " +
                "INNER JOIN impuestos i ON i.id_impuesto = s.id_impuesto ";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()){

                String codigo = rs.getString("codigo_servicio");
                String  nombre = rs.getString("nombre");
                BigDecimal precioBase = rs.getBigDecimal("precio_base");

                int idImpuesto = rs.getInt("id_impuesto");
                String  nombreImpuesto = rs.getString("nombre_impuesto");
                BigDecimal porcentaje = rs.getBigDecimal("porcentaje");
                boolean activo = rs.getBoolean("activo");

                Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, nombreImpuesto, porcentaje, activo);

                Servicio servicio = new Servicio(codigo, nombre, precioBase, impuesto);
                servicios.add(servicio);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al intentar obtener el Servicio", e);
        }

        return servicios;
    }

}
