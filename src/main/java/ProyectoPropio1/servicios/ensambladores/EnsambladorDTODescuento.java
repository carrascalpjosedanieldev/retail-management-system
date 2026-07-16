package ProyectoPropio1.servicios.ensambladores;

import ProyectoPropio1.dominio.Descuento;
import ProyectoPropio1.dto.DescuentoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTODescuento {

    public EnsambladorDTODescuento() {
    }

    public List<DescuentoDTO> ensamblarDetalleDescuentos(List<Descuento> descuentos){
        List<DescuentoDTO> detalleDescuentosActivos = new ArrayList<>();
        String estado;
        for (Descuento descuento :descuentos){
            if (descuento.isActivo()){
                estado = "Activo";
            } else {
                estado = "Inactivo";
            }
            DescuentoDTO descuentoDTO = new DescuentoDTO(descuento.getId(), descuento.getNombre(), descuento.getPorcentaje(), estado);
            detalleDescuentosActivos.add(descuentoDTO);
        }
        return detalleDescuentosActivos;
    }

}

