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

    public int registrarDescuento(String nombre, BigDecimal porcentaje){
        Descuento borrador = Descuento.crearNuevo(nombre, porcentaje);
        Descuento descuento = this.repositorioDescuentos.insertarDescuento(borrador);
        return descuento.getId();
    }

    public void desactivarDescuento(int idDescuento){
        this.repositorioDescuentos.desactivarDescuento(idDescuento);
    }

    public void activarDescuento(int idDescuento){
        this.repositorioDescuentos.activarDescuento(idDescuento);
    }

    public Descuento obtenerDescuento(int idDescuento){
        return this.repositorioDescuentos.obtenerDescuento(idDescuento);
    }

    public void actualizarDescuento(Descuento descuento){
        this.repositorioDescuentos.actualizarDescuento(descuento);
    }

    public List<Descuento> obtenerDescuentosActivos(){
        return this.repositorioDescuentos.obtenerDescuentosActivos();
    }

    public List<Descuento> obtenerDescuentosInactivos(){
        return this.repositorioDescuentos.obtenerDescuentosInactivos();
    }

}
