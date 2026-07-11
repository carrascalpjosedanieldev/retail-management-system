package ProyectoPropio1.servicios.aplicacion;

import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dominio.puertos.RepositorioImpuestos;

import java.math.BigDecimal;
import java.util.List;

public class ServicioImpuestos {

    private final RepositorioImpuestos repositorioImpuestos;

    public ServicioImpuestos(RepositorioImpuestos repositorioImpuestos) {
        this.repositorioImpuestos = repositorioImpuestos;
    }

    public int registrarImpuesto(String nombre, BigDecimal porcentaje){
        Impuesto borrador = Impuesto.crearNuevo(nombre, porcentaje);
        Impuesto impuesto = this.repositorioImpuestos.insertarImpuesto(borrador);
        return impuesto.getId();
    }

    public Impuesto obtenerImpuesto(int idImpuesto){
        return this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
    }

    private void actualizarImpuesto(Impuesto impuesto){
        this.repositorioImpuestos.actualizarImpuesto(impuesto);
    }

    public void activarImpuesto(int idImpuesto){
        Impuesto impuesto = this.obtenerImpuesto(idImpuesto);
        impuesto.activar();
        this.actualizarImpuesto(impuesto);
    }

    public void desactivarImpuesto(int idImpuesto){
        Impuesto impuesto = this.obtenerImpuesto(idImpuesto);
        impuesto.desactivar();
        this.actualizarImpuesto(impuesto);
    }

    public void cambiarNombreImpuesto(int idImpuesto, String nombreNuevo){
        Impuesto impuesto = this.obtenerImpuesto(idImpuesto);
        impuesto.cambiarNombre(nombreNuevo);
        this.actualizarImpuesto(impuesto);
    }

    public void cambiarPorcentajeImpuesto(int idImpuesto, BigDecimal porcentajeNuevo){
        Impuesto impuesto = this.obtenerImpuesto(idImpuesto);
        impuesto.cambiarPorcentaje(porcentajeNuevo);
        this.actualizarImpuesto(impuesto);
    }

    public List<Impuesto> obtenerImpuestosActivos(){
        return this.repositorioImpuestos.obtenerImpuestosActivos();
    }

    public List<Impuesto> obtenerImpuestosInactivos(){
        return this.repositorioImpuestos.obtenerImpuestosInactivos();
    }

}
