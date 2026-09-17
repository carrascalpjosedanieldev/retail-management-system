package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTODescuento {

    //CONSTRUCTOR:

    public EnsambladorDTODescuento() {
    }

    //MÉTODOS:

    public DescuentoDTO ensamblarDatosDescuento(Descuento descuento){
        if (descuento == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO de un Descuento Vacío.");
        }
        return new DescuentoDTO(
                descuento.getId(), descuento.getNombre(), descuento.getPorcentaje(), descuento.isActivo()
        );
    }

    public List<DescuentoDTO> ensamblarDetalleDescuentos(List<Descuento> descuentos){
        List<DescuentoDTO> detalleDescuentosActivos = new ArrayList<>();
        for (Descuento descuento :descuentos){
            DescuentoDTO datosDescuento = this.ensamblarDatosDescuento(descuento);
            detalleDescuentosActivos.add(datosDescuento);
        }
        return detalleDescuentosActivos;
    }

}//===================================================================================================================//

