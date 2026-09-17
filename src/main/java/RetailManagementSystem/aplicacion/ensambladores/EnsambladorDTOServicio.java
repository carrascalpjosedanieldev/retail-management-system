package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
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
        if (servicio == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO de un Servicio Vacío.");
        }
        ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(servicio.getImpuesto());
        DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento(servicio.getDescuento());
        return new ServicioDTO(
                servicio.getCodigo(), servicio.getNombre(), servicio.getPrecioBase(),
                servicio.getValorVenta(fecha), servicio.isActivo(), datosImpuesto, datosDescuento
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

