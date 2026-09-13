package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.math.BigDecimal;
import java.util.List;

public class ServicioDescuentos {

    //ATRIBUTOS:

    private final RepositorioDescuentos repositorioDescuentos;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioDescuentos(RepositorioDescuentos repositorioDescuentos, GestorTransaccional gestorTransaccional) {
        this.repositorioDescuentos = repositorioDescuentos;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public Descuento registrarDescuento(String nombre, BigDecimal porcentaje, boolean activo){
        Descuento borrador = Descuento.crearNuevo(nombre, porcentaje, activo);
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioDescuentos.insertarDescuento(borrador)
        );
    }

    public Descuento obtenerDescuento(int idDescuento){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(()->
                this.repositorioDescuentos.obtenerDescuento(idDescuento)
        );
    }

    public Descuento actualizarDescuento(int idDescuento, String nombre, BigDecimal porcentaje){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Descuento descuento = obtenerDescuento(idDescuento);
            descuento.cambiarNombre(nombre);
            descuento.cambiarPorcentaje(porcentaje);
            actualizarDescuento(descuento);
            return descuento;
        });
    }

    private void actualizarDescuento(Descuento descuento){
        this.repositorioDescuentos.actualizarDescuento(descuento);
    }

    public void cambiarEstadoDescuento(int idDescuento){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            Descuento descuento = obtenerDescuento(idDescuento);
            descuento.cambiarEstado();
            actualizarDescuento(descuento);
        });
    }

    public List<Descuento> obtenerDescuentosActivos(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioDescuentos::obtenerDescuentosActivos
        );
    }

    public List<Descuento> obtenerTodosLosDescuentos(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioDescuentos::obtenerTodosLosDescuentos
        );
    }

}//===================================================================================================================//

