package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;
import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOInventario {

    public EnsambladorDTOInventario() {
    }

    public InventarioDTO ensamblarDatosInventario(Inventario inventario){
        return new InventarioDTO(inventario.getIdInventario(), inventario.getNombre(), inventario.getCapacidadMaxima(), inventario.getCapacidadOcupada(), inventario.calcularCapacidadLibre());
    }

    public List<InventarioDTO> ensamblarDetalleInventarioGeneral(List<Inventario> inventarios){
        List<InventarioDTO> inventarioGeneral = new ArrayList<>();
        for (Inventario inventario:inventarios){
            inventarioGeneral.add(this.ensamblarDatosInventario(inventario));
        }
        return inventarioGeneral;
    }

}//===================================================================================================================//

