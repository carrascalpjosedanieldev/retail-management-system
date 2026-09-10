package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.dto.consultas.ReporteRecaudoDTO;
import RetailManagementSystem.aplicacion.dto.consultas.ResumenVentaDiaDTO;
import RetailManagementSystem.dominio.entidades.ventas.Factura;
import RetailManagementSystem.dominio.entidades.ventas.ItemVendido;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioFacturas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ServicioFacturas {

    //ATRIBUTOS:

    private final RepositorioFacturas repositorioFacturas;

    //CONSTRUCTOR:

    public ServicioFacturas(RepositorioFacturas repositorioFacturas) {
        this.repositorioFacturas = repositorioFacturas;
    }

    //MÉTODOS:

    public Factura registrarVentaYObtenerFactura(List<ItemVendido> itemsDelCarrito) {
        if (itemsDelCarrito == null || itemsDelCarrito.isEmpty()) {
            throw new IllegalArgumentException("NO se puede Registrar una Venta Vacía.");
        }
        return this.repositorioFacturas.insertarFactura(itemsDelCarrito);
    }

    public ReporteRecaudoDTO obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin){
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas para el reporte no pueden estar vacías.");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("Error de lógica: La fecha de inicio (" + fechaInicio + ")" +
                    " NO puede ser posterior a la fecha de fin (" + fechaFin + ").");
        }
        return this.repositorioFacturas.obtenerReporteRecaudo(fechaInicio, fechaFin);
    }

    public ResumenVentaDiaDTO obtenerResumenHoy() {
        LocalDate hoy = LocalDate.now();
        ReporteRecaudoDTO reporteHoy = this.obtenerReporteRecaudo(hoy, hoy);
        int cantidadFacturas = reporteHoy.cantidadFacturasEmitidas();
        BigDecimal totalVentas = reporteHoy.totalRecaudo();
        BigDecimal ultimaVenta = this.repositorioFacturas.obtenerTotalUltimaVenta(hoy);
        return new ResumenVentaDiaDTO(totalVentas, cantidadFacturas, ultimaVenta);
    }

}//===================================================================================================================//

