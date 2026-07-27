package ProyectoPropio1.dominio.entidades;

import java.math.BigDecimal;

public class ResumenVentaDia {

    //ATRIBUTOS:

    private final BigDecimal totalVentas;

    private final int cantidadFacturas;

    private final BigDecimal ultimaVenta;

    //GETTERS Y SETTERS:

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public int getCantidadFacturas() {
        return cantidadFacturas;
    }

    public BigDecimal getUltimaVenta() {
        return ultimaVenta;
    }

    //CONSTRUCTORES:

    private ResumenVentaDia(BigDecimal totalVentas, int cantidadFacturas, BigDecimal ultimaVenta) {
        this.totalVentas = totalVentas;
        this.cantidadFacturas = cantidadFacturas;
        this.ultimaVenta = ultimaVenta;
    }

    public static ResumenVentaDia reconstruirDesdeBD(BigDecimal totalVentas, int cantidadFacturas,
                                                     BigDecimal ultimaVenta) {
        return new ResumenVentaDia(totalVentas, cantidadFacturas, ultimaVenta);
    }

}//===================================================================================================================//

