package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioImpuestos;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;

import java.math.BigDecimal;
import java.util.List;

public class ServicioImpuestos {

    //ATRIBUTOS:

    private final RepositorioImpuestos repositorioImpuestos;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioImpuestos(RepositorioImpuestos repositorioImpuestos, GestorTransaccional gestorTransaccional) {
        this.repositorioImpuestos = repositorioImpuestos;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public Impuesto registrarImpuesto(String nombre, BigDecimal porcentaje, boolean activo){
        Impuesto impuesto = Impuesto.crearNuevo(nombre, porcentaje, activo);
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->
                this.repositorioImpuestos.insertarImpuesto(impuesto)
        );
    }

    public Impuesto obtenerImpuesto(int idImpuesto){
        return this.repositorioImpuestos.obtenerImpuesto(idImpuesto);
    }

    public Impuesto actualizarImpuesto(int idImpuesto, String nombre, BigDecimal porcentaje){
        return this.gestorTransaccional.ejecutarEnTransaccionConRetorno(()->{
            Impuesto impuesto = obtenerImpuesto(idImpuesto);
            impuesto.cambiarNombre(nombre);
            impuesto.cambiarPorcentaje(porcentaje);
            actualizarImpuesto(impuesto);
            return impuesto;
        });
    }

    private void actualizarImpuesto(Impuesto impuesto){
        this.repositorioImpuestos.actualizarImpuesto(impuesto);
    }

    public void cambiarEstadoImpuesto(int idImpuesto){
        this.gestorTransaccional.ejecutarEnTransaccion(()->{
            Impuesto impuesto = obtenerImpuesto(idImpuesto);
            impuesto.cambiarEstado();
            actualizarImpuesto(impuesto);
        });
    }

    public List<Impuesto> obtenerImpuestosActivos(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioImpuestos::obtenerImpuestosActivos
        );
    }

    public List<Impuesto> obtenerTodosLosImpuestos(){
        return this.gestorTransaccional.ejecutarEnTransaccionDeLectura(
                this.repositorioImpuestos::obtenerTodosLosImpuestos
        );
    }

}//===================================================================================================================//

