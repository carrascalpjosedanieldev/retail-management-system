package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioImpuestos;

import java.math.BigDecimal;
import java.util.List;

public class ServicioImpuestos {

    //ATRIBUTOS:

    private final RepositorioImpuestos repositorioImpuestos;

    //CONSTRUCTOR:

    public ServicioImpuestos(RepositorioImpuestos repositorioImpuestos) {
        this.repositorioImpuestos = repositorioImpuestos;
    }

    //MÉTODOS:

    public Impuesto registrarImpuesto(String nombre, BigDecimal porcentaje, boolean activo){
        Impuesto impuesto = Impuesto.crearNuevo(nombre, porcentaje, activo);
        return this.repositorioImpuestos.insertarImpuesto(impuesto);
    }

    public Impuesto obtenerImpuesto(int idImpuesto){
        return this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
    }

    public Impuesto actualizarImpuesto(int idImpuesto, String nombre, BigDecimal porcentaje){
        Impuesto impuesto = obtenerImpuesto(idImpuesto);
        impuesto.cambiarNombre(nombre);
        impuesto.cambiarPorcentaje(porcentaje);
        actualizarImpuesto(impuesto);
        return impuesto;
    }

    private void actualizarImpuesto(Impuesto impuesto){
        this.repositorioImpuestos.actualizarImpuesto(impuesto);
    }

    public void cambiarEstadoImpuesto(int idImpuesto){
        Impuesto impuesto = obtenerImpuesto(idImpuesto);
        impuesto.cambiarEstado();
        actualizarImpuesto(impuesto);
    }

    public List<Impuesto> obtenerImpuestosActivos(){
        return this.repositorioImpuestos.obtenerImpuestosActivos();
    }

    public List<Impuesto> obtenerTodosLosImpuestos(){
        return this.repositorioImpuestos.obtenerTodosLosImpuestos();
    }

}//===================================================================================================================//

