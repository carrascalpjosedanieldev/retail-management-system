package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTODescuento {

    public EnsambladorDTODescuento() {
    }

    public DescuentoDTO ensamblarDatosDescuento(Descuento descuento){
        String estado;
        if (descuento.isActivo()){
            estado = "Activo";
        } else {
            estado = "Inactivo";
        }
        return new DescuentoDTO(
                descuento.getId(), descuento.getNombre(), descuento.getPorcentaje(), estado
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

