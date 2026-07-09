package ProyectoPropio1.dominio;

import ProyectoPropio1.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ItemVendido {

    //ATRIBUTOS:

    private final TipoItem tipoItem;

    private final String codigo;

    private final String nombre;

    private final int cantidad;

    private final BigDecimal precioUnitario;

    private final BigDecimal subtotalNeto;

    private final BigDecimal porcentajeImpuesto;

    private final BigDecimal montoImpuesto;

    private final BigDecimal totalLinea;

    //GETTERS Y SETTERS:

    public TipoItem getTipoItem() {
        return tipoItem;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotalNeto() {
        return subtotalNeto;
    }

    public BigDecimal getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public BigDecimal getMontoImpuesto() {
        return montoImpuesto;
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }

    //CONSTRUCTOR:

    private ItemVendido(ItemFacturable item, int cantidad, LocalDate fecha) {
        this.tipoItem = item.getTipoItem();
        this.codigo = item.getCodigo();
        this.nombre = item.getNombre();
        this.cantidad = cantidad;
        this.precioUnitario = item.getValorVenta(fecha);
        this.subtotalNeto = this.precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        this.porcentajeImpuesto = item.getPorcentajeImpuesto();
        BigDecimal factorImpuesto = this.porcentajeImpuesto.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
        this.montoImpuesto = this.subtotalNeto.multiply(factorImpuesto);
        this.totalLinea = this.subtotalNeto.add(this.montoImpuesto);
    }

    public static ItemVendido crearNuevo(ItemFacturable item, int cantidad, LocalDate fecha){
        return new ItemVendido(item, cantidad, fecha);
    }

}

