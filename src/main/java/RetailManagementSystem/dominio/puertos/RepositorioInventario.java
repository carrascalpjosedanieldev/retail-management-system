package RetailManagementSystem.dominio.puertos;

import RetailManagementSystem.dominio.entidades.Inventario;

import java.util.List;

public interface RepositorioInventario {

    Inventario insertarInventario(Inventario inventario);

    void eliminarInventario(int idInventario);

    Inventario obtenerInventario(int idInventario);

    void actualizarInventario(Inventario inventario);

    List<Inventario> obtenerTodosInventariosConCapacidadOcupada();
}

