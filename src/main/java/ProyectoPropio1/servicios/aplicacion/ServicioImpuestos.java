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

    public int registrarImpuesto(String nombre, BigDecimal porcentaje, boolean activo){
        Impuesto borrador = Impuesto.crearNuevo(nombre, porcentaje, activo);
        Impuesto impuesto = this.repositorioImpuestos.insertarImpuesto(borrador);
        return impuesto.getId();
    }

    public Impuesto obtenerImpuesto(int idImpuesto){
        return this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
    }

    public void actualizarImpuesto(int idImpuesto, String nombre, BigDecimal porcentaje){
        Impuesto impuesto = this.obtenerImpuesto(idImpuesto);
        impuesto.cambiarNombre(nombre);
        impuesto.cambiarPorcentaje(porcentaje);
        this.repositorioImpuestos.actualizarImpuesto(impuesto);
    }

    private void actualizarImpuesto(Impuesto impuesto){
        this.repositorioImpuestos.actualizarImpuesto(impuesto);
    }

    public void cambiarEstadoImpuesto(int idImpuesto){
        Impuesto impuesto = this.obtenerImpuesto(idImpuesto);
        if (impuesto.isActivo()){
            impuesto.desactivar();
        } else {
            impuesto.activar();
        }
        this.actualizarImpuesto(impuesto);
    }

    public List<Impuesto> obtenerImpuestosActivos(){
        return this.repositorioImpuestos.obtenerImpuestosActivos();
    }

    public List<Impuesto> obtenerImpuestosInactivos(){
        return this.repositorioImpuestos.obtenerImpuestosInactivos();
    }

}
