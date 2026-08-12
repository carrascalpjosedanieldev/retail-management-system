package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOImpuesto;
import RetailManagementSystem.aplicacion.servicios.ServicioImpuestos;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import java.math.BigDecimal;
import java.util.List;

public class OrquestadorImpuestos {

    //ATRIBUTOS:

    private final ServicioImpuestos servicioImpuestos;

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    //CONSTRUCTOR:

    public OrquestadorImpuestos(ServicioImpuestos servicioImpuestos, EnsambladorDTOImpuesto ensambladorDTOImpuesto) {
        this.servicioImpuestos = servicioImpuestos;
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
    }

    //MÉTODOS:

    public List<ImpuestoDTO> obtenerTodosLosImpuestos() {
        return this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                this.servicioImpuestos.obtenerTodosLosImpuestos()
        );
    }

    public ImpuestoDTO registrarImpuesto(String nombre, BigDecimal porcentaje, boolean activo) {
        Impuesto impuesto = this.servicioImpuestos.registrarImpuesto(nombre, porcentaje, activo);
        return this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(impuesto);
    }

    public ImpuestoDTO actualizarImpuesto(int idImpuesto, String nombre, BigDecimal porcentaje) {
        Impuesto impuesto = this.servicioImpuestos.actualizarImpuesto(idImpuesto, nombre, porcentaje);
        return this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(impuesto);
    }

    public void cambiarEstadoDescuento(int idImpuesto) {
        this.servicioImpuestos.cambiarEstadoImpuesto(idImpuesto);
    }

}//===================================================================================================================//

