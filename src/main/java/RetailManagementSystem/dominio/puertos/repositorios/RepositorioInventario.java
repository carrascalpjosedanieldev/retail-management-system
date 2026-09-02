package RetailManagementSystem.dominio.puertos.repositorios;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;

import java.util.List;

public interface RepositorioInventario {

    //CREATE:

    Inventario insertarInventario(Inventario inventario);

    //READ:

    Inventario obtenerInventario(int idInventario);

    List<Inventario> obtenerTodosInventariosConCapacidadOcupada();

    //UPDATE:

    void validarCapacidadInventario(int idInventario, int stockASumar);

    void actualizarInventario(Inventario inventario);

}//===================================================================================================================//

