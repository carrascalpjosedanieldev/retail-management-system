package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.Inventario;

import java.util.List;

public interface RepositorioInventario {

    //CREATE:

    void insertarInventario(Inventario inventario);

    //READ:

    Inventario obtenerInventario(int idInventario);

    List<Inventario> obtenerTodosInventariosConCapacidadOcupada();

    //UPDATE:

    void actualizarInventario(Inventario inventario);

    //DELETE:

    void eliminarInventario(int idInventario);

}//===================================================================================================================//

