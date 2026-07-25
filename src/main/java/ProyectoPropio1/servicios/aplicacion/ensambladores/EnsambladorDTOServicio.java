package ProyectoPropio1.servicios.aplicacion.ensambladores;

import ProyectoPropio1.dominio.Servicio;
import ProyectoPropio1.dto.ServicioDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOServicio {

    public EnsambladorDTOServicio() {
    }

    public ServicioDTO ensamblarServicio(Servicio servicio, LocalDate fecha){
        String estado;
        if (servicio.isActivo()){
            estado = "Activo";
        } else {
            estado = "Inactivo";
        }
        return new ServicioDTO(servicio.getCodigo(), servicio.getNombre(), servicio.getPrecioBase(),
                servicio.getValorVenta(fecha), estado,
                servicio.getIdImpuesto(), servicio.getImpuesto().getNombre(),
                servicio.getIdDescuento(), servicio.getDescuento().getNombre());
    }

    public List<ServicioDTO> ensamblarDatosCatalogoServicios(List<Servicio> servicios, LocalDate fecha){
        List<ServicioDTO> listaServicios = new ArrayList<>();
        for (Servicio servicio: servicios){
            listaServicios.add(this.ensamblarServicio(servicio, fecha));
        }
        return listaServicios;
    }

}

