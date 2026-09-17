package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Inventario;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioInventario;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.util.List;

public class ServicioInventario {

    //ATRIBUTOS:

    private final RepositorioInventario repositorioInventario;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioInventario(RepositorioInventario repositorioInventario, GestorTransaccional gestorTransaccional) {
        this.repositorioInventario = repositorioInventario;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    private Inventario obtenerInventario(int idInventario){
        return this.repositorioInventario.obtenerInventario(idInventario);
    }

    private void actualizarInventario(Inventario inventario){
        this.repositorioInventario.actualizarInventario(inventario);
    }

    public Inventario registrarInventario(String nombre, int capacidad){
        Inventario inventario = Inventario.crearNuevo(nombre, capacidad);
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioInventario.insertarInventario(inventario)
        );
    }

    public Inventario actualizarInventario(int idInventario, String nombreNuevo){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Inventario inventario = obtenerInventario(idInventario);
            inventario.cambiarNombreInventario(nombreNuevo);
            actualizarInventario(inventario);
            return inventario;
        });
    }

    public Inventario aumentarCapacidadMaximaInventario(int idInventario, Integer cantidadesExtra){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Inventario inventario = obtenerInventario(idInventario);
            inventario.aumentarCapacidadMaxima(cantidadesExtra);
            actualizarInventario(inventario);
            return inventario;
        });
    }

    public List<Inventario> obtenerTodosLosInventarios(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioInventario::obtenerTodosInventariosConCapacidadOcupada
        );
    }

}//===================================================================================================================//

