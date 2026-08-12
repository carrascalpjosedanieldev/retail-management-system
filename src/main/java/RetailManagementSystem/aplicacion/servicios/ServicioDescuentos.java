package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.puertos.RepositorioDescuentos;

import java.math.BigDecimal;
import java.util.List;

public class ServicioDescuentos {

    //ATRIBUTOS:

    private final RepositorioDescuentos repositorioDescuentos;

    //CONSTRUCTOR:

    public ServicioDescuentos(RepositorioDescuentos repositorioDescuentos) {
        this.repositorioDescuentos = repositorioDescuentos;
    }

    //MÉTODOS:

    public Descuento registrarDescuento(String nombre, BigDecimal porcentaje, boolean activo){
        Descuento borrador = Descuento.crearNuevo(nombre, porcentaje, activo);
        return this.repositorioDescuentos.insertarDescuento(borrador);
    }

    public Descuento obtenerDescuento(int idDescuento){
        return this.repositorioDescuentos.obtenerDescuento(idDescuento);
    }

    public Descuento actualizarDescuento(int idDescuento, String nombre, BigDecimal porcentaje){
        Descuento descuento = this.obtenerDescuento(idDescuento);
        descuento.cambiarNombre(nombre);
        descuento.cambiarPorcentaje(porcentaje);
        this.repositorioDescuentos.actualizarDescuento(descuento);
        return descuento;
    }

    private void actualizarDescuento(Descuento descuento){
        this.repositorioDescuentos.actualizarDescuento(descuento);
    }

    public void cambiarEstadoDescuento(int idDescuento){
        Descuento descuento = this.obtenerDescuento(idDescuento);
        if (descuento.isActivo()){
            descuento.desactivar();
        } else {
            descuento.activar();
        }
        this.actualizarDescuento(descuento);
    }

    public List<Descuento> obtenerDescuentosActivos(){
        return this.repositorioDescuentos.obtenerDescuentosActivos();
    }

    public List<Descuento> obtenerTodosLosDescuentos(){
        return this.repositorioDescuentos.obtenerTodosLosDescuentos();
    }

}//===================================================================================================================//

