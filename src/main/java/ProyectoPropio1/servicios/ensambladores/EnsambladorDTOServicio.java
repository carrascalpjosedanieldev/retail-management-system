package ProyectoPropio1.servicios.ensambladores;

import ProyectoPropio1.dominio.Servicio;
import ProyectoPropio1.dto.DatosCatalogoServiciosDTO;
import ProyectoPropio1.dto.ServicioDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOServicio {

    public EnsambladorDTOServicio() {
    }

    public ServicioDTO ensamblarServicio(Servicio servicio, LocalDate fecha){
        return new ServicioDTO(servicio.getCodigo(), servicio.getNombre(), servicio.getValorVenta(fecha), servicio.getIdImpuesto());
    }

    public DatosCatalogoServiciosDTO ensamblarDatosCatalogoServicios(List<Servicio> servicios, LocalDate fecha){
        List<ServicioDTO> listaServicios = new ArrayList<>();
        for (Servicio servicio: servicios){
            listaServicios.add(this.ensamblarServicio(servicio, fecha));
        }
        return new DatosCatalogoServiciosDTO(listaServicios);
    }

}

