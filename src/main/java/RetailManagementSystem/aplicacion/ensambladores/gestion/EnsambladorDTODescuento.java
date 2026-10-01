package RetailManagementSystem.aplicacion.ensambladores.gestion;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTODescuento {

    //CONSTRUCTOR:

    public EnsambladorDTODescuento() { }

    //MÉTODOS:

    public DescuentoDTO ensamblarDatosDescuento(Descuento descuento){
        return new DescuentoDTO(
                descuento.getId(), descuento.getNombre(),
                descuento.getPorcentaje().setScale(2, RoundingMode.HALF_UP), descuento.isActivo()
        );
    }

    public List<DescuentoDTO> ensamblarDetalleDescuentos(List<Descuento> descuentos){
        List<DescuentoDTO> detalleDescuentosActivos = new ArrayList<>();
        for (Descuento descuento:descuentos){
            DescuentoDTO datosDescuento = ensamblarDatosDescuento(descuento);
            detalleDescuentosActivos.add(datosDescuento);
        }
        return detalleDescuentosActivos;
    }

}//===================================================================================================================//

