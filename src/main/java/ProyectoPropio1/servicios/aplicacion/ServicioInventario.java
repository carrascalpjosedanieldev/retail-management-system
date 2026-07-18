package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Inventario;
import ProyectoPropio1.dominio.puertos.RepositorioInventario;

import java.util.ArrayList;
import java.util.List;

public class ServicioInventario {

    private final RepositorioInventario repositorioInventario;

    public ServicioInventario(RepositorioInventario repositorioInventario) {
        this.repositorioInventario = repositorioInventario;
    }

    public Inventario obtenerInventario(int idInventario){
        return this.repositorioInventario.obtenerInventario(idInventario);
    }

    public void verificarEspacioDisponible(int idInventario, int stockNuevo) {
        Inventario inventario = this.repositorioInventario.obtenerInventario(idInventario);
        inventario.validarEspacioDisponible(stockNuevo);
    }

    public void agregarInventario(String nombre, int capacidad){
        Inventario inventario = Inventario.crearNuevo(nombre, capacidad);
        this.repositorioInventario.insertarInventario(inventario);
    }

    public void actualizarInventario(int idInventario, String nombreNuevo){
        Inventario inventario = this.repositorioInventario.obtenerInventario(idInventario);
        inventario.cambiarNombreInventario(nombreNuevo);
        this.repositorioInventario.actualizarInventario(inventario);
    }

    public List<Inventario> obtenerTodosLosInventarios(){
        return new ArrayList<>(this.repositorioInventario.obtenerTodosInventariosConCapacidadOcupada());
    }

}
