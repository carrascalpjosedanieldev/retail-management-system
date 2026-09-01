package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;

import java.util.List;

public interface RepositorioInventario {

    //CREATE:

    Inventario insertarInventario(Inventario inventario);

    //READ:

    Inventario obtenerInventario(int idInventario);

    List<Inventario> obtenerTodosInventariosConCapacidadOcupada();

    //UPDATE:

    void actualizarInventario(Inventario inventario);

}//===================================================================================================================//

