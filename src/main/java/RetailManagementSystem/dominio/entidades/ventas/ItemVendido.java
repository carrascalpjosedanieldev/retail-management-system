package RetailManagementSystem.dominio.entidades.ventas;

import RetailManagementSystem.dominio.enums.TipoItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    private ItemVendido(TipoItem tipoItem, String codigo, String nombre, int cantidad, BigDecimal precioUnitario,
                        BigDecimal porcentajeImpuesto) {
        if (nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("El Nombre del Item de Código -" + codigo + "- NO puede estar Vacío.");
        }
        if (codigo == null || codigo.isBlank()){
            throw new IllegalArgumentException("El Código del Item -" + nombre + "- NO puede estar Vacío.");
        }
        if (cantidad <= 0){
            throw new IllegalArgumentException("La Cantidad del Item -" + nombre + "- es Invalida.");
        }
        if (precioUnitario.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("El Precio Unitario del Item -" + nombre + "- es Invalido.");
        }
        if (porcentajeImpuesto.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("El Porcentaje de Impuesto del Item -" + nombre + "- es Invalido.");
        }
        this.tipoItem = tipoItem;
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.totalLinea = this.precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        this.porcentajeImpuesto = porcentajeImpuesto;
        BigDecimal factorDivisor = BigDecimal.ONE.add(this.porcentajeImpuesto.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        this.subtotalNeto = this.totalLinea.divide(factorDivisor, 6, RoundingMode.HALF_UP);
        this.montoImpuesto = this.totalLinea.subtract(this.subtotalNeto);
    }

    public static ItemVendido crearNuevo(TipoItem tipoItem, String codigo, String nombre, int cantidad, BigDecimal precioUnitario, BigDecimal porcentajeImpuesto){
        return new ItemVendido(tipoItem, codigo, nombre, cantidad, precioUnitario, porcentajeImpuesto);
    }

}//===================================================================================================================//

