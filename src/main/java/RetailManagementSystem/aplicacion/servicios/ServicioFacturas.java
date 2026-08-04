package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.ventas.Factura;
import RetailManagementSystem.dominio.entidades.ventas.ItemVendido;
import RetailManagementSystem.dominio.entidades.ventas.ReporteRecaudo;
import RetailManagementSystem.dominio.entidades.ventas.ResumenVentaDia;
import RetailManagementSystem.dominio.puertos.RepositorioFacturas;

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

    public ReporteRecaudo obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin){
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas para el reporte no pueden estar vacías.");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("Error de lógica: La fecha de inicio (" + fechaInicio + ")" +
                    " NO puede ser posterior a la fecha de fin (" + fechaFin + ").");
        }
        return this.repositorioFacturas.obtenerReporteRecaudo(fechaInicio, fechaFin);
    }

    public ResumenVentaDia obtenerResumenHoy() {
        LocalDate hoy = LocalDate.now();
        ReporteRecaudo reporteHoy = this.obtenerReporteRecaudo(hoy, hoy);
        int cantidadFacturas = reporteHoy.getCantidadFacturasEmitidas();
        BigDecimal totalVentas = reporteHoy.getTotalRecaudo();
        BigDecimal ultimaVenta = this.repositorioFacturas.obtenerTotalUltimaVenta(hoy);
        return ResumenVentaDia.reconstruirDesdeBD(totalVentas, cantidadFacturas, ultimaVenta);
    }

}//===================================================================================================================//

