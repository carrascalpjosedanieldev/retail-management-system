package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.comercial.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.comercial.ImpuestoDTO;
import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOServicio {

    //ATRIBUTOS:

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;
    private final EnsambladorDTODescuento ensambladorDTODescuento;

    //CONSTRUCTOR:

    public EnsambladorDTOServicio(
            EnsambladorDTOImpuesto ensambladorDTOImpuesto, EnsambladorDTODescuento ensambladorDTODescuento
    ) {
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
    }

    //MÉTODOS:

    public ServicioDTO ensamblarServicio(Servicio servicio, LocalDate fecha){
        String estado;
        if (servicio.isActivo()){
            estado = "Activo";
        } else {
            estado = "Inactivo";
        }
        ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(servicio.getImpuesto());
        DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento(servicio.getDescuento());
        return new ServicioDTO(
                servicio.getCodigo(), servicio.getNombre(), servicio.getPrecioBase(),
                servicio.getValorVenta(fecha), estado, datosImpuesto, datosDescuento
        );
    }

    public List<ServicioDTO> ensamblarDatosCatalogoServicios(List<Servicio> servicios, LocalDate fecha){
        List<ServicioDTO> listaServicios = new ArrayList<>();
        for (Servicio servicio: servicios){
            listaServicios.add(this.ensamblarServicio(servicio, fecha));
        }
        return listaServicios;
    }

}//===================================================================================================================//

