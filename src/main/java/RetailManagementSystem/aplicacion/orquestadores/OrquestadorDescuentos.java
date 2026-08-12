package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.servicios.ServicioDescuentos;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        List<DescuentoDTO> activos = this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerDescuentosActivos()
        );
        List<DescuentoDTO> inactivos = this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerDescuentosInactivos()
        );
        List<DescuentoDTO> todosLosDescuentos = new ArrayList<>();
        todosLosDescuentos.addAll(activos);
        todosLosDescuentos.addAll(inactivos);
        return todosLosDescuentos;
    }

    public void registrarDescuento(String nombre, BigDecimal porcentaje, boolean activo) {
        this.servicioDescuentos.registrarDescuento(nombre, porcentaje, activo);
    }

    public DescuentoDTO actualizarDescuento(int idDescuento, String nombre, BigDecimal porcentaje) {
        Descuento descuento = this.servicioDescuentos.actualizarDescuento(idDescuento, nombre, porcentaje);
        return this.ensambladorDTODescuento.ensamblarDatosDescuento(descuento);
    }

    public void cambiarEstadoDescuento(int idDescuento) {
        this.servicioDescuentos.cambiarEstadoDescuento(idDescuento);
    }

}//===================================================================================================================//

