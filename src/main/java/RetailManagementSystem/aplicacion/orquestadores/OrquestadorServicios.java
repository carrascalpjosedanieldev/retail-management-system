package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOServicio;
import RetailManagementSystem.aplicacion.servicios.ServicioServicios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrquestadorServicios {

    //ATRIBUTOS:

    private final ServicioServicios servicioServicios;

    private final EnsambladorDTOServicio ensambladorDTOServicio;

    //CONSTRUCTOR:

    public OrquestadorServicios(
            ServicioServicios servicioServicios, EnsambladorDTOServicio ensambladorDTOServicio
    ) {
        this.servicioServicios = servicioServicios;
        this.ensambladorDTOServicio = ensambladorDTOServicio;
    }

    //MÉTODOS:

    public List<ServicioDTO> obtenerTodosLosServicios(LocalDate fecha){
        return this.ensambladorDTOServicio.ensamblarDatosCatalogoServicios(
                this.servicioServicios.obtenerTodosLosServicios(), fecha
        );
    }

    public ServicioDTO registrarServicio(
            String nombre, BigDecimal precioBase, int idImpuesto, int idDescuento, LocalDate fecha
    ) {
        return this.ensambladorDTOServicio.ensamblarServicio(
                this.servicioServicios.registrarServicioNuevo(
                        nombre, precioBase, idImpuesto, idDescuento
                ), fecha
        );
    }

    public ServicioDTO actualizarServicio(
            String codigoServicio, String nuevoNombre, BigDecimal nuevoPrecioBase,
            int idImpuesto, int idDescuento, LocalDate fecha
    ) {
        return this.ensambladorDTOServicio.ensamblarServicio(
                this.servicioServicios.actualizarServicio(
                        codigoServicio, nuevoNombre, nuevoPrecioBase, idImpuesto, idDescuento
                ), fecha
        );
    }

    public void cambiarEstadoServicio(String codigoServicio){
        this.servicioServicios.cambiarEstadoServicio(codigoServicio);
    }

}//===================================================================================================================//

