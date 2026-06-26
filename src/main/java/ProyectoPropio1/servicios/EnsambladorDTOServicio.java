package ProyectoPropio1.servicios;

import ProyectoPropio1.dominio.Servicio;
import ProyectoPropio1.dto.DatosServicioDTO;

public class EnsambladorDTOServicio {

    public EnsambladorDTOServicio() {
    }

    public DatosServicioDTO ensamblarServicio(Servicio servicio){
        return new DatosServicioDTO(servicio.getCodigoServicio(), servicio.getNombre(), servicio.getValorCobrado());
    }

}

