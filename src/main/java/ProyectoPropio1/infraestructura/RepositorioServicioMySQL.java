package ProyectoPropio1.infraestructura;

import ProyectoPropio1.dominio.Descuento;
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
        String sql = "INSERT INTO servicios (codigo_servicio, nombre, precio_base, id_impuesto, id_descuento) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            pstmt.setString(1, servicio.getCodigo());
            pstmt.setString(2, servicio.getNombre());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());
            pstmt.setInt(4, servicio.getIdImpuesto());
            pstmt.setInt(5, servicio.getIdDescuento());

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
    public Servicio obtenerServicio(String codigoServicio) {
        String sql = "SELECT s.codigo_servicio, s.nombre, s.precio_base, s.id_impuesto, s.activo, " +
                "i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, i.activo AS activo_impuesto, " +
                "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
                "des.activo AS descuento_activo " +
                "FROM servicios s " +
                "INNER JOIN impuestos i ON i.id_impuesto = s.id_impuesto " +
                "INNER JOIN descuentos des ON s.id_descuento = des.id_descuento " +
                "WHERE s.codigo_servicio = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoServicio);

            try (ResultSet rs = pstmt.executeQuery()){

                if (rs.next()){

                    String codigo = rs.getString("codigo_servicio");
                    String  nombre = rs.getString("nombre");
                    BigDecimal precioBase = rs.getBigDecimal("precio_base");
                    boolean activo = rs.getBoolean("activo");

                    int idImpuesto = rs.getInt("id_impuesto");
                    String  nombreImp = rs.getString("nombre_impuesto");
                    BigDecimal porcentajeImp = rs.getBigDecimal("porcentaje_impuesto");
                    boolean activoImp = rs.getBoolean("activo_impuesto");
                    Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, nombreImp, porcentajeImp, activoImp);

                    int idDescuento = rs.getInt("id_descuento");
                    String nombreDesc = rs.getString("nombre_descuento");
                    BigDecimal porcentajeDesc = rs.getBigDecimal("porcentaje_descuento");
                    boolean activoDesc = rs.getBoolean("descuento_activo");
                    Descuento descuento = Descuento.reconstruirDesdeBD(idDescuento, nombreDesc, porcentajeDesc, activoDesc);

                    return Servicio.reconstruirDesdeBD(codigo, nombre, precioBase, impuesto, descuento, activo);

                }

                throw new ServicioNoEncontradoException("Error de negocio: El Servicio con código -" + codigoServicio + "- no existe");

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al intentar obtener el Servicio", e);
        }

    }


    @Override
    public void actualizarServicio(Servicio servicio) {
        String sql = "UPDATE servicios SET id_impuesto = ?, nombre = ?, precio_base = ?, id_descuento = ?, activo = ? " +
                "WHERE codigo_servicio = ?";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, servicio.getIdImpuesto());
            pstmt.setString(2, servicio.getNombre());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());
            pstmt.setInt(4, servicio.getIdDescuento());
            pstmt.setBoolean(5, servicio.isActivo());
            pstmt.setString(6, servicio.getCodigo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas==0){
                throw new ServicioNoEncontradoException("NO se pudo actualizar. El Servicio con el Codigo -" + servicio.getCodigo() + "- NO existe");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al intentar modificar el Servicio", e);
        }

    }


    @Override
    public List<Servicio> obtenerServiciosActivos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT s.codigo_servicio, s.nombre, s.precio_base, s.id_impuesto, s.id_descuento, s.activo, " +
                "i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, i.activo AS activo_impuesto, " +
                "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento," +
                " des.activo AS activo_descuento " +
                "FROM servicios s " +
                "INNER JOIN impuestos i ON i.id_impuesto = s.id_impuesto " +
                "INNER JOIN descuentos des ON s.id_descuento = des.id_descuento " +
                "WHERE s.activo = true ";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()){

                String codigo = rs.getString("codigo_servicio");
                String  nombre = rs.getString("nombre");
                BigDecimal precioBase = rs.getBigDecimal("precio_base");
                boolean activo = rs.getBoolean("activo");

                int idImpuesto = rs.getInt("id_impuesto");
                String  nombreImp = rs.getString("nombre_impuesto");
                BigDecimal porcentajeImp = rs.getBigDecimal("porcentaje_impuesto");
                boolean activoImp = rs.getBoolean("activo_impuesto");
                Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, nombreImp, porcentajeImp, activoImp);

                int idDescuento = rs.getInt("id_descuento");
                String nombreDesc = rs.getString("nombre_descuento");
                BigDecimal porcentajeDesc = rs.getBigDecimal("porcentaje_descuento");
                boolean activoDesc = rs.getBoolean("activo_descuento");
                Descuento descuento = Descuento.reconstruirDesdeBD(idDescuento, nombreDesc, porcentajeDesc, activoDesc);

                Servicio servicio = Servicio.reconstruirDesdeBD(codigo, nombre, precioBase, impuesto, descuento, activo);
                servicios.add(servicio);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al intentar obtener el Servicio", e);
        }

        return servicios;
    }


    @Override
    public List<Servicio> obtenerServiciosInactivos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT s.codigo_servicio, s.nombre, s.precio_base, s.id_impuesto, s.activo, " +
                "i.nombre AS nombre_impuesto, i.porcentaje AS porcentaje_impuesto, i.activo AS activo_impuesto, " +
                "des.id_descuento, des.nombre AS nombre_descuento, des.porcentaje AS porcentaje_descuento, " +
                "des.activo AS activo_descuento " +
                "FROM servicios s " +
                "INNER JOIN impuestos i ON i.id_impuesto = s.id_impuesto " +
                "INNER JOIN descuentos des ON s.id_descuento = des.id_descuento " +
                "WHERE s.activo = false ";

        try (Connection conn = AdministradorConexion.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()){

                String codigo = rs.getString("codigo_servicio");
                String  nombre = rs.getString("nombre");
                BigDecimal precioBase = rs.getBigDecimal("precio_base");
                boolean activo = rs.getBoolean("activo");

                int idImpuesto = rs.getInt("id_impuesto");
                String  nombreImp = rs.getString("nombre_impuesto");
                BigDecimal porcentajeImp = rs.getBigDecimal("porcentaje_impuesto");
                boolean activoImp = rs.getBoolean("activo_impuesto");
                Impuesto impuesto = Impuesto.reconstruirDesdeBD(idImpuesto, nombreImp, porcentajeImp, activoImp);

                int idDescuento = rs.getInt("id_descuento");
                String nombreDesc = rs.getString("nombre_descuento");
                BigDecimal porcentajeDesc = rs.getBigDecimal("porcentaje_descuento");
                boolean activoDesc = rs.getBoolean("activo_descuento");
                Descuento descuento = Descuento.reconstruirDesdeBD(idDescuento, nombreDesc, porcentajeDesc, activoDesc);

                Servicio servicio = Servicio.reconstruirDesdeBD(codigo, nombre, precioBase, impuesto, descuento, activo);
                servicios.add(servicio);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Error crítico de infraestructura al intentar obtener el Servicio", e);
        }

        return servicios;
    }


}

