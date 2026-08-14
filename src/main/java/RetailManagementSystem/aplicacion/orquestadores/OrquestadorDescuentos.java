package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.servicios.ServicioDescuentos;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;

import java.math.BigDecimal;
import java.util.List;

public class OrquestadorDescuentos {

    //ATRIBUTOS:

    private final ServicioDescuentos servicioDescuentos;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    //CONSTRUCTOR:

    public OrquestadorDescuentos(ServicioDescuentos servicioDescuentos, EnsambladorDTODescuento ensambladorDTODescuento) {
        this.servicioDescuentos = servicioDescuentos;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
    }

    //MÉTODOS:

    public List<DescuentoDTO> obtenerTodosLosDescuentos() {
        return this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerTodosLosDescuentos()
        );
    }

    public List<DescuentoDTO> obtenerDescuentosActivos(){
        return this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerDescuentosActivos()
        );
    }

    public DescuentoDTO registrarDescuento(String nombre, BigDecimal porcentaje, boolean activo) {
        Descuento descuento = this.servicioDescuentos.registrarDescuento(nombre, porcentaje, activo);
        return this.ensambladorDTODescuento.ensamblarDatosDescuento(descuento);
    }

    public DescuentoDTO actualizarDescuento(int idDescuento, String nombre, BigDecimal porcentaje) {
        Descuento descuento = this.servicioDescuentos.actualizarDescuento(idDescuento, nombre, porcentaje);
        return this.ensambladorDTODescuento.ensamblarDatosDescuento(descuento);
    }

    public void cambiarEstadoDescuento(int idDescuento) {
        this.servicioDescuentos.cambiarEstadoDescuento(idDescuento);
    }

}//===================================================================================================================//

