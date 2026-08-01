package RetailManagementSystem.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Factura {

    //ATRIBUTOS:

    private final List<ItemVendido> itemsFinales;

    private final Integer idFactura;

    private final String numeroFactura;

    private final LocalDateTime fechaHoraEmision;

    private final BigDecimal totalGeneral;

    private final BigDecimal totalImpuestos;

    private final BigDecimal subTotal;

    //GETTERS Y SETTERS:

    public Integer getIdFactura() {
        return idFactura;
    }

    public List<ItemVendido> getItemsFinales(){
        return Collections.unmodifiableList(this.itemsFinales);
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public LocalDateTime getFechaHoraEmision() {
        return fechaHoraEmision;
    }

    public BigDecimal getTotalGeneral() {
        return totalGeneral;
    }

    public BigDecimal getTotalImpuestos() {
        return totalImpuestos;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    //CONSTRUCTOR:

    private Factura(List<ItemVendido> itemsFinales, Integer idFactura, String numeroFactura, LocalDateTime fechaHoraEmision,
                   BigDecimal totalGeneral, BigDecimal totalImpuestos, BigDecimal subTotal) {
        this.itemsFinales = List.copyOf(itemsFinales);
        this.idFactura = idFactura;
        this.numeroFactura = numeroFactura;
        this.fechaHoraEmision = fechaHoraEmision;
        this.totalGeneral = totalGeneral;
        this.totalImpuestos = totalImpuestos;
        this.subTotal = subTotal;
    }

    public static Factura reconstruirDesdeBD(List<ItemVendido> itemsFinales, Integer idFactura, String numeroFactura,
                                             LocalDateTime fechaHoraEmision, BigDecimal totalGeneral,
                                             BigDecimal totalImpuestos, BigDecimal subTotal){
        return new Factura(itemsFinales, idFactura, numeroFactura, fechaHoraEmision, totalGeneral, totalImpuestos, subTotal);
    }

    private Factura(List<ItemVendido> itemsFinales, String numeroFactura, LocalDateTime fechaHoraEmision) {
        this.itemsFinales = List.copyOf(itemsFinales);
        this.idFactura = null;
        this.numeroFactura = numeroFactura;
        this.fechaHoraEmision = fechaHoraEmision;
        BigDecimal tempSubtotal = BigDecimal.ZERO;
        BigDecimal tempImpuestos = BigDecimal.ZERO;
        for (ItemVendido item : this.itemsFinales) {
            tempSubtotal = tempSubtotal.add(item.getSubtotalNeto());
            tempImpuestos = tempImpuestos.add(item.getMontoImpuesto());
        }
        this.subTotal = tempSubtotal;
        this.totalImpuestos = tempImpuestos;
        this.totalGeneral = this.subTotal.add(this.totalImpuestos);

    }

    public static Factura crearNueva(List<ItemVendido> itemsFinales, String numeroFactura, LocalDateTime fechaHoraEmision){
        return new Factura(itemsFinales, numeroFactura, fechaHoraEmision);
    }

}//===================================================================================================================//

