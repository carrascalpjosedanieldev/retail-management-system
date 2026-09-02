package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioInventario;

import java.util.List;

public class ServicioInventario {

    //ATRIBUTOS:

    private final RepositorioInventario repositorioInventario;

    //CONSTRUCTOR:

    public ServicioInventario(RepositorioInventario repositorioInventario) {
        this.repositorioInventario = repositorioInventario;
    }

    //MÉTODOS:

    public Inventario obtenerInventario(int idInventario){
        return this.repositorioInventario.obtenerInventario(idInventario);
    }

    public void verificarEspacioDisponible(int idInventario, int stockNuevo) {
        Inventario inventario = obtenerInventario(idInventario);
        inventario.validarEspacioDisponible(stockNuevo);
    }

    public Inventario registrarInventario(String nombre, int capacidad){
        Inventario inventario = Inventario.crearNuevo(nombre, capacidad);
        return this.repositorioInventario.insertarInventario(inventario);
    }

    public Inventario actualizarInventario(int idInventario, String nombreNuevo){
        Inventario inventario = obtenerInventario(idInventario);
        inventario.cambiarNombreInventario(nombreNuevo);
        this.repositorioInventario.actualizarInventario(inventario);
        return inventario;
    }

    public List<Inventario> obtenerTodosLosInventarios(){
        return this.repositorioInventario.obtenerTodosInventariosConCapacidadOcupada();
    }

}//===================================================================================================================//

