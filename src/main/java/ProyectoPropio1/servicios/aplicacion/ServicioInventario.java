package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Inventario;
import ProyectoPropio1.dominio.puertos.RepositorioInventario;

import java.util.List;

public class ServicioInventario {

    private final RepositorioInventario repositorioInventario;

    public ServicioInventario(RepositorioInventario repositorioInventario) {
        this.repositorioInventario = repositorioInventario;
    }

    public Inventario obtenerInventario(int idInventario){
        return this.repositorioInventario.obtenerInventario(idInventario);
    }

    public List<Inventario> obtenerInventarios(){
        return this.repositorioInventario.obtenerTodosInventariosConCapacidadOcupada();
    }

    public void verificarEspacioDisponible(int idInventario, int stockNuevo) {
        Inventario inventario = this.repositorioInventario.obtenerInventario(idInventario);
        inventario.validarEspacioDisponible(stockNuevo);
    }

    public int agregarInventario(String nombre, int capacidad){
        Inventario borrador = Inventario.crearNuevo(nombre, capacidad);
        Inventario inventario = this.repositorioInventario.insertarInventario(borrador);
        return inventario.getIdInventario();
    }

    public void cambiarNombreInventario(int id, String nombreNuevo){
        Inventario inventario = this.repositorioInventario.obtenerInventario(id);
        inventario.cambiarNombreInventario(nombreNuevo);
        this.repositorioInventario.actualizarInventario(inventario);
    }

    public void eliminarInventarioVacio(int idInventario){
        this.repositorioInventario.eliminarInventario(idInventario);
    }


}
