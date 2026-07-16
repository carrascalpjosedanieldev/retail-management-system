package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Descuento;
import ProyectoPropio1.dominio.puertos.RepositorioDescuentos;

import java.math.BigDecimal;
import java.util.List;

public class ServicioDescuentos {

    private final RepositorioDescuentos repositorioDescuentos;

    public ServicioDescuentos(RepositorioDescuentos repositorioDescuentos) {
        this.repositorioDescuentos = repositorioDescuentos;
    }

    public int registrarDescuento(String nombre, BigDecimal porcentaje, boolean activo){
        Descuento borrador = Descuento.crearNuevo(nombre, porcentaje, activo);
        Descuento descuento = this.repositorioDescuentos.insertarDescuento(borrador);
        return descuento.getId();
    }

    public Descuento obtenerDescuento(int idDescuento){
        return this.repositorioDescuentos.obtenerDescuento(idDescuento);
    }

    public void actualizarDescuento(int idDescuento, String nombre, BigDecimal porcentaje){
        Descuento descuento = this.obtenerDescuento(idDescuento);
        descuento.cambiarNombre(nombre);
        descuento.cambiarPorcentaje(porcentaje);
        this.repositorioDescuentos.actualizarDescuento(descuento);
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

    public void cambiarNombreDescuento(int idDescuento, String  nombreNuevo){
        Descuento descuento = this.obtenerDescuento(idDescuento);
        descuento.cambiarNombre(nombreNuevo);
        this.actualizarDescuento(descuento);
    }

    public void cambiarPorcentajeDescuento(int idDescuento, BigDecimal porcentajeNuevo){
        Descuento descuento = this.obtenerDescuento(idDescuento);
        descuento.cambiarPorcentaje(porcentajeNuevo);
        this.actualizarDescuento(descuento);
    }

    public List<Descuento> obtenerDescuentosActivos(){
        return this.repositorioDescuentos.obtenerDescuentosActivos();
    }

    public List<Descuento> obtenerDescuentosInactivos(){
        return this.repositorioDescuentos.obtenerDescuentosInactivos();
    }

}
