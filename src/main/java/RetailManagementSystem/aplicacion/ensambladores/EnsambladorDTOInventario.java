package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;
import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOInventario {

    //CONSTRUCTOR:

    public EnsambladorDTOInventario() {
    }

    //MÉTODOS:

    public InventarioDTO ensamblarDatosInventario(Inventario inventario){
        if (inventario == null){
            throw new IllegalArgumentException("NO puedes ensamblar un DTO de un Inventario Vacío.");
        }
        return new InventarioDTO(
                inventario.getIdInventario(),
                inventario.getNombre(),
                inventario.getCapacidadMaxima(),
                inventario.getCapacidadOcupada(),
                inventario.calcularCapacidadLibre()
        );
    }

    public List<InventarioDTO> ensamblarDetalleInventarioGeneral(List<Inventario> inventarios){
        List<InventarioDTO> inventarioGeneral = new ArrayList<>();
        for (Inventario inventario:inventarios){
            inventarioGeneral.add(this.ensamblarDatosInventario(inventario));
        }
        return inventarioGeneral;
    }

}//===================================================================================================================//

