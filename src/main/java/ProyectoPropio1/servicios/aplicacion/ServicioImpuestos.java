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

    public void actualizarImpuesto(Impuesto impuesto){
        this.repositorioImpuestos.actualizarImpuesto(impuesto);
    }

    public List<Impuesto> obtenerImpuestosActivos(){
        return this.repositorioImpuestos.obtenerImpuestosActivos();
    }

    public List<Impuesto> obtenerImpuestosInactivos(){
        return this.repositorioImpuestos.obtenerImpuestosInactivos();
    }

}
